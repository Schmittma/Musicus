package stafflineremoval;

import java.util.ArrayList;

import general.Staffline;
import interfaces.StafflineRemoval;

public class ClarkeStafflineRemoval implements StafflineRemoval{

	@Override
	public boolean[][] removeStafflines(boolean[][] staffImage, ArrayList<Staffline> stafflines) {
		
		//Copy staffline array
		boolean[][] copy = staffImage.clone();
		
		//For every Staffline
		for(Staffline line : stafflines) {
			double gradient = (double)(line.getEndPoint().getY() - line.getStartPoint().getY()) / (double)(line.getEndPoint().getX() - line.getStartPoint().getX());
			int y = line.getStartPoint().getY();
			int w = (int) (line.getWidth() - 1);
			
			//Over the whole line length
			for(int x = line.getStartPoint().getX(); x <= line.getEndPoint().getX(); x++) {
				
				boolean matchedUpperClarkTemplate = false;
				boolean matchedLowerClarkTemplate = false;
				
				if(y-2 >= 0) { //Check bounds
					matchedUpperClarkTemplate = staffImage[x][y-1] && (staffImage[x-1][y-2] || staffImage[x][y-2] || staffImage[x+1][y-2]);
				}
				if(y+w+2 < staffImage[x].length) { //Check bounds
					matchedLowerClarkTemplate = staffImage[x][y+w+1] && (staffImage[x-1][y+w+2] || staffImage[x][y+w+2] || staffImage[x+1][y+w+2]);
				}
				
				//Check clarke template at the upper and lower part of the staffline and if it does not match, we can remove the line segment
				if(!( matchedUpperClarkTemplate || matchedLowerClarkTemplate )){ 
					//For every vertical pixel of this staffline segment
					for (int i = y; i <= y+w; i++) {
						copy[x][i] = false;
					}
				}
				
				y = (int) (line.getStartPoint().getY() + x*gradient);
			}
			
		}
		
		return copy;
	}

}
