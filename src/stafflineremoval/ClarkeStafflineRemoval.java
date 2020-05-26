package stafflineremoval;

import java.util.ArrayList;

import general.Point;
import general.Staffline;
import interfaces.StafflineRemoval;
import utils.Util;

public class ClarkeStafflineRemoval implements StafflineRemoval{

	@Override
	public boolean[][] removeStafflines(boolean[][] staffImage, ArrayList<Staffline> stafflines) {
		
		//Copy staffline array
		boolean[][] copy = Util.copyArray(staffImage);
		
		//For every Staffline
		for(Staffline line : stafflines) {
			
			int w = (int) (line.getWidth() - 1);
			int count = 0;
			boolean matchedUpperClarkTemplate = false;
			boolean matchedLowerClarkTemplate = false;
			//Over the whole line length
			for(int x = 0; x < staffImage.length; x++) {
				ArrayList<Point> points = line.getPointsOnXCoordinate(x);
				if(points.size() <= 0) {
					continue;
				}
				
				int yUp = points.get(0).getY();

				int yDown = points.get(points.size()-1).getY();
				
				if(yUp-2 >= 0) { //Check bounds
					matchedUpperClarkTemplate = staffImage[x][yUp-1] && (staffImage[x-1][yUp-2] || staffImage[x][yUp-2] || staffImage[x+1][yUp-2]);
				}
				if(yDown+2 < staffImage[x].length) { //Check bounds
					matchedLowerClarkTemplate = staffImage[x][yDown+1] && (staffImage[x-1][yDown+2] || staffImage[x][yDown+2] || staffImage[x+1][yDown+2]);
				}
				
				//Check clarke template at the upper and lower part of the staffline and if it does not match, we can remove the line segment
				if(!( matchedUpperClarkTemplate || matchedLowerClarkTemplate )){ 
					//For every vertical pixel of this staffline segment
					for (Point point : points) {
						copy[x][point.getY()] = false;
					}
				}
				
			}
			
		}
		
		return copy;
	}

}
