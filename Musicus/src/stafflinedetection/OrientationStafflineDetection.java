package stafflinedetection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;

import org.jgrapht.alg.drawing.model.Points;

import general.Point;
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
		//Find the staff line orientation
		int kRange = 1;
		int tNumSegment = 2; //Minimum number of staff segments to be considered a valid coloumn
		int tNumOrientation = 2; // Minimum number of orientations that have to be valid
		ArrayList<Double> stafflineOrientation = new ArrayList<>(copy.length);
		
		for(int col = 0; col < copy.length-kRange; col++) {

			//ArrayList<Integer> colRun = runs.get(col);
			if(runs.get(col).size() < tNumSegment*2) {
				stafflineOrientation.add(null);
				continue;
			}
			
			int y = 0; //TODO
			ArrayList<Double> orientationsAtThisColumn = new ArrayList<>();
			
			for(int runNum = 0; runNum < runs.get(col).size( ); runNum++) {
				
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
//						System.out.print(connectedMiddle+"  ");
						double orientation = 0;
						for(int i = 1; i < connectedMiddle.size(); i++) {
							orientation += (connectedMiddle.get(i) - connectedMiddle.get(0));
									
						}
//						System.out.print(orientation+"  ");
						orientation /= (connectedMiddle.size()-1);
//						System.out.print(orientation+"   | ");
						orientationsAtThisColumn.add(orientation);
					}
					
				}
				
				y+= runs.get(col).get(runNum);
			}
			
			//Calculate the average distance at this column
			
			double avg = 0;
			if(orientationsAtThisColumn.size() < tNumOrientation) {
				stafflineOrientation.add(null);
			}
			else {
//				System.out.print("|"+orientationsAtThisColumn+"\t");
				for(int i = 0; i < orientationsAtThisColumn.size(); i++) {
					avg += orientationsAtThisColumn.get(i);
				}
				
//				System.out.print(avg+"\t");
				avg /= orientationsAtThisColumn.size();
//				System.out.print(avg+"\n");
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
		
		//plotStaffLineOrientation(stafflineOrientation, system.length, system[0].length);
		
		//Find the stafflines with the given orientation information (with the assumption, that the line will be uniformly thick (should not matter tho)).
		
		ArrayList<Point> counter = new ArrayList<>();//count for every row, how much pixels match the staffline orientation line.

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
				/*
				if((int) (row + Math.round(y)) >= 0 && (int) (row + Math.round(y)) < copy[x].length &&
						copy[x][(int)(row+Math.round(y))]) {
					tempCounter++;
				}
				*/

				y += stafflineOrientation.get(x);
			}
			counter.add(new Point(row,tempCounter));
		}
		
		
		
		BufferedImage im = ImageConverter.horizontalProjectionToImage(listToIntArray(counter), stafflineOrientation.size());
		
		try {
			ImageIO.write(im, "png", new File(debugPath + Globals.STAFFLINE_DETECTION_DATA + "counter.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//Remove all entries adjacent to peaks
		
		counter.sort(Collections.reverseOrder(Comparator.comparing(Point::getY)));	
		double threshold = 0.5;

		for(int x1 = 0; x1 < counter.size(); x1++) {
			//If the peak is smaller than a certain value
			if(counter.get(x1).getY() < counter.get(0).getY()*threshold) {
				break;
			}
			
			for(int x2 = 0; x2 < counter.size(); x2++) {
				//Between the peak index and whitespaceheight on both sides
				if(x1 != x2 && counter.get(x2).getX() >= counter.get(x1).getX() - (whitespaceHeight-stafflineHeight) && 
						counter.get(x2).getX() <= counter.get(x1).getX() + whitespaceHeight-stafflineHeight) {
					
					counter.remove(x2);
					x2--;
				}
			}
		}
		
		counter.removeIf(i -> i.getY() <= counter.get(0).getY() * threshold);
		
		ArrayList<Point> counterCleaned = toOriginalSize(counter, copy[0].length);
		counterCleaned.sort(Comparator.comparing(Point::getX));
		im = ImageConverter.horizontalProjectionToImage(listToIntArray(counterCleaned), stafflineOrientation.size());
		
		try {
			ImageIO.write(im, "png", new File(debugPath + Globals.STAFFLINE_DETECTION_DATA + "counter_cleaned.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		//Follow the peak with the orientation list and figure out the complete Staffline on the original image
		//If the is no white pixel found on the followed line, we set the position to the nearest black pixel and continue from there
		

		counter.sort(Comparator.comparing(Point::getX));
		System.out.println(counter);
		
		ArrayList<ArrayList<Point>> stafflinePixels = new ArrayList<>();
		for(Point entry : counter) {
			ArrayList<Point> pixels = new ArrayList<>();
			double y = entry.getX();
			
			
			for(int x = 0; x < system.length; x++) {
				boolean found = false;
				int foundIndex = 0;
				int corrY = (int) Math.round(y); //If we change the way y has to be corrected (example cutting instead of rounding, do it here)
				
				//Find the next black pixel in a local window which fits certain criteria (Runlength size smaller than a certain threshold
				for(int y2 = 0; y2 < whitespaceHeight; y2++) {
					
					if(corrY + y2 >= 0 && corrY + y2 < system[x].length && system[x][corrY+y2] && getRunlength(system, x, corrY+y2) < Tlength) {
						
						pixels.add(new Point(x,corrY + y2));
						found = true;
						foundIndex = y2;
						break;
					}
					
					else if(corrY-y2 >= 0 && corrY-y2 < system[x].length &&system[x][corrY-y2] && getRunlength(system, x, corrY-y2) < Tlength) {
						
						pixels.add(new Point(x,corrY-y2));
						found = true;
						foundIndex = -y2;
						break;
					}
				}
				//Check around the found staffline (in both directions) to include pixels based on the staffline width, if they are foreground pixels and in the margin of stafflineheight (in both directions)
				if(found) {
					int numOfIncludedStafflines = stafflineHeight;
					for(int y2 = 1; y2 < stafflineHeight; y2++) {
						if(numOfIncludedStafflines > 0 && corrY+foundIndex+y2 >= 0 && corrY+foundIndex+y2 < system[x].length && system[x][corrY+foundIndex+y2]) {
							pixels.add(new Point(x,corrY+foundIndex+y2));
							numOfIncludedStafflines--;
						}
						
						if(numOfIncludedStafflines > 0 && corrY+foundIndex-y2 >= 0 && corrY+foundIndex-y2 < system[x].length && system[x][corrY+foundIndex-y2]) {
							pixels.add(new Point(x,corrY+foundIndex-y2));
							numOfIncludedStafflines--;
						}
					}
				}
				
				if(x < stafflineOrientation.size()) {
					y+=stafflineOrientation.get(x);
				}

			}
			stafflinePixels.add(pixels);
		} 
		
		plotStafflines(stafflinePixels, system);
		
		ArrayList<Staffline> stafflines = new ArrayList<>();
		for(ArrayList<Point> pixels : stafflinePixels) {
			stafflines.add(new Staffline(pixels,this.stafflineHeight));
		}
		
		
		return stafflines;
		
	}

	private void plotStaffLineOrientation(ArrayList<Double> stafflineOrientation, int width, int height) {
		
		double y = 0;
		BufferedImage im = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		
		for(int x = 0; x < stafflineOrientation.size(); x++) {
			im.setRGB(x, height/2 +(int)(y), 0xFFFFFFFF);
			y+=stafflineOrientation.get(x);
		}
		
		try {
			ImageIO.write(im, "png", new File(debugPath + Globals.STAFFLINE_DETECTION_DATA + "staffline_image.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	private int[] listToIntArray(ArrayList<Point> list) {
		int[] ret = new int[list.size()];
		for(int x = 0; x < ret.length; x++) {
			ret[x] = list.get(x).getY();
		}
		
		return ret;
	}
	
	private ArrayList<Point> toOriginalSize(ArrayList<Point> values, int size){
		ArrayList<Point> ret = new ArrayList<>();
		boolean[] check = new boolean[size];
		
		for(int x = 0; x < values.size(); x++) {
			ret.add(new Point(values.get(x).getX(), values.get(x).getY()));
			check[values.get(x).getX()] = true;
		}
		
		for(int x = 0; x < check.length; x++) {
			if(check[x] == false) {
				ret.add(new Point(x, 0));
			}
		}
		
		return ret;
	}
	
	private void plotStafflines(ArrayList<ArrayList<Point>> stafflinePixels, boolean[][] system) {
		
		
		BufferedImage im = ImageConverter.BinaryImageToBuffered(system);
		
		for(ArrayList<Point> stafflines : stafflinePixels) {
			for(Point entry : stafflines) {
				im.setRGB(entry.getX(), entry.getY(), 0xFFFF0000);
			}
		}
		
		try {
			ImageIO.write(im, "png", new File(debugPath + Globals.STAFFLINE_DETECTION_DATA + "staffline_detected.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	private int getRunlength(boolean[][] image, int x, int y) {
		
		int y2;
		for(y2 = y; y2 >= 0; y2--) {
			if(image[x][y] != image[x][y2]) {
				y2++;
				break;
			}
		}
		
		int y3;
		for(y3 = y; y3 < image[x].length; y3++) {
			if(image[x][y] != image[x][y3]) {
				y3--;
				break;
			}
		}
		
		return y3-y2+1;
	}
}
