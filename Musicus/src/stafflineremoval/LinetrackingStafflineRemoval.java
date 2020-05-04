package stafflineremoval;

import java.util.ArrayList;
import java.util.Arrays;

import general.Staffline;
import interfaces.StafflineRemoval;
import utils.UtilMath;

//This class implements the Linetracking Chord algorithm of "A Comparative Study of Staff Removal Algorithms" Page 3
public class LinetrackingStafflineRemoval implements StafflineRemoval{

	@Override
	public boolean[][] removeStafflines(boolean[][] staffImage, ArrayList<Staffline> stafflines) {
		
		boolean[][] copy = staffImage.clone();
		
		
		//For every Staffline
		for(Staffline line : stafflines) {
			double gradient = (double)(line.getEndPoint().getY() - line.getStartPoint().getY()) / (double)(line.getEndPoint().getX() - line.getStartPoint().getX());
			int angle = (int)Math.tan(gradient);

			int y = line.getStartPoint().getY();
			int w = (int) (line.getWidth() - 1);
			
			
			//Over the whole line length
			for(int x = line.getStartPoint().getX(); x <= line.getEndPoint().getX(); x++) {
				
				int[] lengths = new int[180]; //track the lengths of the chords
				
				for(int theta = angle; theta < angle+180; theta++) {
					
					int length = chordlength(x, (int) ((y+0.5+w)/2),staffImage, theta);
					lengths[theta-angle] = length;
					
					if(theta > 30 && theta < 150 && length > 1.75 * line.getWidth() * Math.abs(Math.sin(theta))) {
						
						
						System.out.println(theta + " " + 1.75 * line.getWidth() * Math.abs(Math.sin(theta)) + " " + length);
						for (int i = y; i <= y+w; i++) {
							copy[x][i] = false;
						}
						break;
					}
				}
				
				y = (int) (line.getStartPoint().getY() + x*gradient);
			}
		}
		return copy;
	}
		
	public int chordlength(int x, int y, boolean[][] image, int angle) {
		double gradient = Math.atan(angle);
		
		int countPos = 0;
		
		while(true) {
			int nextX = (int) ((y+(countPos+1)*gradient)+0.5);
			int nextY = x+(countPos+1);
			
			if(nextX >= image.length || nextY < 0 || nextY >= image[nextX].length || image[nextX][nextY] == false) {
				break;
			}

			countPos++;
		}
		
		int countNeg = 0;
		while(true) {
			//Check bounds
			int nextX = (int) ((y-(countNeg+1)*gradient)+0.5);
			int nextY = x-(countNeg+1);
			
			if(nextX < 0 || nextY < 0 || nextY >= image[nextX].length || image[nextX][nextY] == false) {
				break;
			}
			countNeg++;
		}
		
		return countPos+countNeg;
	}

}
