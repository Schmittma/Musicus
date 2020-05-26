package stafflineremoval;

import java.util.ArrayList;
import java.util.Arrays;

import general.Staffline;
import interfaces.StafflineRemoval;
import utils.Util;
import utils.UtilMath;

//This class implements the Linetracking Chord algorithm of "A Comparative Study of Staff Removal Algorithms" Page 3
public class LinetrackingStafflineRemoval implements StafflineRemoval{

	int minimumAngle;
	double lengthMultiplier;
	int resolution;
	
	
	/**
	 * 
	 * @param minimumAngle The minimum angle for a chord to be considered a distinct peak 
	 * @param lengthMultiplier this value is multiplied with the line width and determines the threshold for the chordlength
	 * @param resolution this value determines the resolution of the angles at which the chords are calculated
	 */
	public LinetrackingStafflineRemoval(int minimumAngle, double lengthMultiplier, int resolution) {
		super();
		this.minimumAngle = minimumAngle;
		this.lengthMultiplier = lengthMultiplier;
		this.resolution = Math.min(resolution,90);
	}

	@Override
	public boolean[][] removeStafflines(boolean[][] staffImage, ArrayList<Staffline> stafflines) {
		/*
		boolean[][] copy = Util.copyArray(staffImage);
		
		
		//For every Staffline
		for(Staffline line : stafflines) {
			double gradient = (double)(line.getEndPoint().getY() - line.getStartPoint().getY()) / (double)(line.getEndPoint().getX() - line.getStartPoint().getX());
			int angle = (int)Math.atan(gradient);

			int y = line.getStartPoint().getY();
			int w = (int) (line.getWidth() - 1);
			
			
			//Over the whole line length
			for(int x = line.getStartPoint().getX(); x <= line.getEndPoint().getX(); x++) {
				
				boolean remove = true;
				for(int theta = angle-90; theta <= angle+90; theta+=resolution) {
					
					int length = chordlength(x, (int) (y + 0.5 + w/2),staffImage, theta);
					
					if(Math.abs(theta) > minimumAngle && length > lengthMultiplier * line.getWidth()) {
						remove = false;
						break;
					}
				}
				
				if(remove) {
					for (int i = y; i <= y+w; i++) {
						copy[x][i] = false;
					}
				}
				
				y = (int) (line.getStartPoint().getY() + x*gradient);
			}

		}
		return copy;
		*/
		System.err.println("CURRENTLY NOT WORKING STAFFLINE REMOVAL ALGORITHM WAS USED");
		return null;
	}
		
	public int chordlength(int x, int y, boolean[][] image, int angle) {
		
		double rad = angle * Math.PI/180;
		
		double gradHyp = Math.tan(rad);
		double gradX = Math.cos(rad) * gradHyp;
		double gradY = Math.sin(rad) * gradHyp;
		double max = Math.max(Math.abs(gradX), Math.abs(gradY));
		
		//Normalize to 0-1
		gradX /= max;
		gradY /= max;
		
		int countPos = 0;
		while(true) {
			int nextX = (int) ((x+(countPos+1)*gradX)+0.5);
			int nextY = (int) ((y+(countPos+1)*gradY)+0.5);
			
			if(nextX < 0 || nextX >= image.length || nextY < 0 || nextY >= image[nextX].length || image[nextX][nextY] == false) {
				break;
			}

			countPos++;
		}
		
		int countNeg = 0;
		while(true) {
			//Check bounds
			int nextX = (int) ((x-(countNeg+1)*gradX)+0.5);
			int nextY = (int) ((y-(countNeg+1)*gradY)+0.5);
			
			if(nextX < 0 || nextX >= image.length || nextY < 0 || nextY >= image[nextX].length || image[nextX][nextY] == false) {
				break;
			}
			countNeg++;
		}
		
		return countPos+countNeg;
	}

}
