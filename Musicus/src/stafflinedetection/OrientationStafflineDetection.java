package stafflinedetection;

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

public class OrientationStafflineDetection implements StafflineDetection {

	private int stafflineHeight;
	private int whitespaceHeight;
	
	
	
	public OrientationStafflineDetection(int stafflineHeight, int whitespaceHeight) {
		super();
		this.stafflineHeight = stafflineHeight;
		this.whitespaceHeight = whitespaceHeight;
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
			ImageIO.write(ImageConverter.BinaryImageToBuffered(copy), "png", new File("C:\\Users\\Marius\\Desktop\\musicus_data\\" + Globals.STAFFLINE_DETECTION_DATA + "system.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//We now have an image with much of the musical symbols removed
		int kRange = 5;
		int tNumSegment = 10;
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
					
					if(connectedMiddle.size() >= 6) {
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
			if(orientationsAtThisColumn.size() < 3) {
				stafflineOrientation.add(null);
			}
			else {
				for(int i = 0; i < orientationsAtThisColumn.size(); i++) {
					avg += orientationsAtThisColumn.get(i);
					System.out.print(orientationsAtThisColumn.get(i) + " ");
				}
				System.out.println();
				
				avg /= orientationsAtThisColumn.size();
				
				stafflineOrientation.add(avg);
				
			}
		}
		
		System.out.println(stafflineOrientation+"\n\n");
		try {
			Files.write(Paths.get("C:\\Users\\Marius\\Desktop\\musicus_data\\" + Globals.STAFFLINE_DETECTION_DATA + "stafflines.txt"), stafflineOrientation.toString().replace(", ", "\n").getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Interpolate missing Stafflines
		while(stafflineOrientation.contains(null)) {
			
		}
		
		
		
		return null;
		
	}


	private int distance(boolean[][] copy, ArrayList<ArrayList<Integer>> runs, int col, int y, int kRange) {
		
		
		
		
		return 0;
	}

}
