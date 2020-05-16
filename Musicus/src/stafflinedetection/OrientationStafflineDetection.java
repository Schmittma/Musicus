package stafflinedetection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import general.Staffline;
import interfaces.StafflineDetection;
import start.Globals;
import utils.ImageConverter;
import utils.Util;
import utils.UtilMath;

public class OrientationStafflineDetection implements StafflineDetection {

	private int stafflineHeight;
	private int whitespaceHeight;
	private String debugPath;
	
	
	
	public OrientationStafflineDetection(int stafflineHeight, int whitespaceHeight, String debugPath) {
		super();
		this.stafflineHeight = stafflineHeight;
		this.whitespaceHeight = whitespaceHeight;
		this.debugPath = debugPath;
	}
 

	@Override
	public ArrayList<Staffline> detectStafflines(boolean[][] system) {
		
		boolean[][] copy = new boolean[system.length][system[0].length];
		
		int Tlength = Math.min(2*stafflineHeight, stafflineHeight+whitespaceHeight);
		ArrayList<ArrayList<Integer>> runs = Util.GetVerticalRunLengths(system);
		
		for(int x = 0; x < runs.size(); x++) {
			for(int run = 1; run < runs.get(x).size(); run+=2) {
				if(runs.get(x).get(run) >= Tlength) {
					int newRun = runs.get(x).get(run) + runs.get(x).get(run-1);
					
					if(run < runs.get(x).size()-1) {
						newRun += runs.get(x).get(run+1); 
					}
					
					runs.get(x).set(run-1, newRun);
					runs.get(x).remove(run);
					
					if(run < runs.get(x).size()) {
						runs.get(x).remove(run);
					}
					run-=2;
					
				}
			}
		}
		
		for(int x = 0; x < runs.size(); x++) {
			int count = 0;
			for(int run = 0; run < runs.get(x).size(); run++) {
				
				for(int y = count; y < count+runs.get(x).get(run); y++) {
					copy[x][y] = run % 2 != 0;
				}
				count += runs.get(x).get(run);
			}
		}
		
		try {
			ImageIO.write(ImageConverter.BinaryImageToBuffered(copy), "png", new File(debugPath + Globals.STAFFLINE_DETECTION_DATA + "system.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//We now have an image with much of the musical symbols removed
		int kRange = 5;
		int tNumSegment = 5; //Minimum number of staff segments to be considered a valid coloumn
		ArrayList<Double> stafflineOrientation = new ArrayList<>(copy.length);
		
		for(int col = 0; col < copy.length-kRange; col++) {

			//ArrayList<Integer> colRun = runs.get(col);
			if(runs.get(col).size() < tNumSegment*2) {
				stafflineOrientation.add(null);
				continue;
			}
			
			int y = 0;
			ArrayList<Double> orientationsAtThisColumn = new ArrayList<>();
			
			for(int runNum = 0; runNum < runs.get(col).size(); runNum++) {
				
				if(runNum % 2 != 0) { //Black run
					
					ArrayList<Integer> connectedMiddle = new ArrayList<>();
					ArrayList<Integer> connectedRunIndex = new ArrayList<>();
					ArrayList<Integer> connectedRunStart = new ArrayList<>();
					
					//First entries
					connectedMiddle.add(runs.get(col).get(runNum) > 1 ? y + runs.get(col).get(runNum)/2 : y );
					connectedRunIndex.add(runNum);
					connectedRunStart.add(y);
					
					for(int j = 1; j <= kRange; j++) {//Check all next columns
						//Check if it has connection in the next row
	
						boolean connected = false;
						ArrayList<Integer> connectedY = new ArrayList<>(); ;
						
						boolean isForking = false;
						for(int i = connectedRunStart.get(j-1); i < connectedRunStart.get(j-1) + runs.get(col+j-1).get(connectedRunIndex.get(j-1)); i++) {
							if(copy[col+j][i]) {
								connectedY.add(i);
								if(connectedY.size() > 1 && Math.abs(connectedY.get(connectedY.size()-1)-connectedY.get(connectedY.size()-2)) > 1){
									isForking = true;
								}
								connected = true;
							}
						}
						if(isForking || !connected) {
							break;
						}
						
						//Check the run index of the connected segment
						int temp = connectedY.get(0);
						int count = 0; //
						
						for(int i = 0; i < runs.get(col+j).size(); i++) {
							if(i % 2 != 0) {
								if(temp >= count && temp < count + runs.get(col+j).get(i)) {
									connectedRunStart.add(count);
									connectedRunIndex.add(i);
									int middle = runs.get(col+j).get(i) > 1 ? count + runs.get(col+j).get(i)/2 : count;
									connectedMiddle.add(middle);
									break;
								}
							}
							count += runs.get(col+j).get(i);
						}
					}
					
					//Calculate the average distance at this Segment in this column
					
					if(connectedMiddle.size() >= kRange+1) {
						double orientation = 0;
						for(int i = 1; i < connectedMiddle.size(); i++) {
							orientation += (connectedMiddle.get(0) - connectedMiddle.get(i));
									
						}
						
						orientation /= (connectedMiddle.size()-1);
						orientationsAtThisColumn.add(orientation);
					}
					
				}
				
				y+= runs.get(col).get(runNum);
			}
			
			//Calculate the average distance at this column
			
			double avg = 0;
			if(orientationsAtThisColumn.size() < 2) {
				stafflineOrientation.add(null);
			}
			else {
				for(int i = 0; i < orientationsAtThisColumn.size(); i++) {
					avg += orientationsAtThisColumn.get(i);
				}
				
				avg /= orientationsAtThisColumn.size();
				
				stafflineOrientation.add(avg);
				
			}
		}
		
		try {
			Files.write(Paths.get(debugPath + Globals.STAFFLINE_DETECTION_DATA + "stafflines.txt"), stafflineOrientation.toString().replace(", ", "\n").getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		//If the first values are null, default them to zero and interpolate from there
		if(stafflineOrientation.get(0) == null) {
			stafflineOrientation.set(0, 0.0);
		}
		if(stafflineOrientation.get(stafflineOrientation.size()-1) == null) {
			stafflineOrientation.set(stafflineOrientation.size()-1, 0.0);
		}
		
		//Interpolate missing Stafflines
		for(int x = 0; x < stafflineOrientation.size(); x++) {
			if(stafflineOrientation.get(x) == null) {
				//Search for the next non null value on the left and on the right
				int xLeft;
				for(xLeft = x-1; xLeft >= 0; xLeft--) {
					if(stafflineOrientation.get(xLeft) != null) {
						break;
					}
				}
				
				int xRight;
				for(xRight = x+1; xRight < stafflineOrientation.size(); xRight++) {
					if(stafflineOrientation.get(xRight) != null) {
						break;
					}
				}
				
				//Found some points, interpolate from those
				if(xLeft >= 0 && xRight < stafflineOrientation.size()) {
					double mu = (double)(x-xLeft) / (double)(xRight-xLeft);
					double interpolated = UtilMath.LinearInterpolate(stafflineOrientation.get(xLeft), stafflineOrientation.get(xRight), mu);
					stafflineOrientation.set(x, interpolated);
				}
			}
		}
		
		try {
			Files.write(Paths.get(debugPath + Globals.STAFFLINE_DETECTION_DATA + "stafflines_Interpolated.txt"), stafflineOrientation.toString().replace(", ", "\n").getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		plotStaffLineOrientation(stafflineOrientation, system.length, system[0].length);
		
		//Find the stafflines with the given orientation information (with the assumption, that the line will be uniformly thick (should not matter tho)).
		
		ArrayList<Integer> counter = new ArrayList<>();//count for every row, how much pixels match the staffline orientation line.

		for(int row = 0; row < copy[0].length; row++) {
			double y = 0;
			int tempCounter = 0;
			for(int x = 0; x < stafflineOrientation.size(); x++) {
				for(int yWidth = 0; yWidth < stafflineHeight; yWidth++) {
					if((int) (row + yWidth + Math.round(y)) >= 0 && (int) (row + yWidth + Math.round(y)) < copy[x].length && 
							copy[x][(int) (row + yWidth + Math.round(y))]) {
					tempCounter++;
					}
				}
				

				y += stafflineOrientation.get(x);
			}
			counter.add(tempCounter);
		}


		
		BufferedImage im = ImageConverter.horizontalProjectionToImage(counter.stream().mapToInt(i->i).toArray(), stafflineHeight*stafflineOrientation.size());
		
		try {
			ImageIO.write(im, "png", new File(debugPath + Globals.STAFFLINE_DETECTION_DATA + "counter.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}


	private void plotStaffLineOrientation(ArrayList<Double> stafflineOrientation, int width, int height) {
		
		double y = 0;
		BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		
		for(int x = 0; x < stafflineOrientation.size(); x++) {
			im.setRGB(x, height/2 +(int)(y), 0xFFFFFFFF);
			y+=stafflineOrientation.get(x);
			
			if(stafflineOrientation.get(x) == 0) {
				y = Math.round(y);
			}
		}
		
		try {
			ImageIO.write(im, "png", new File(debugPath + Globals.STAFFLINE_DETECTION_DATA + "staffline_image.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
