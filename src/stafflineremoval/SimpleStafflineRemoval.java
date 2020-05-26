package stafflineremoval;

import java.util.ArrayList;

import general.Point;
import general.Staffline;
import interfaces.StafflineRemoval;
import utils.Util;

/**
 * Simply removes every pixel contained in the staffImages
 * @author Marius
 *
 */
public class SimpleStafflineRemoval implements StafflineRemoval {

	@Override
	public boolean[][] removeStafflines(boolean[][] staffImage, ArrayList<Staffline> stafflines) {
		
		boolean[][] copy = Util.copyArray(staffImage);
		for(Staffline staffline : stafflines) {
			ArrayList<Point> points = staffline.getPointsOnStaffline();
			
			for(Point point : points) {
				copy[point.getX()][point.getY()] = false;
			}
		}
		
		//Since this approach will probably result in a lot of artifacts, we remove everything that is a single, non-connected pixel.
		for(int x = 0; x < copy.length; x++) {
			for (int y = 0; y < copy[x].length; y++) {
				if( copy[x][y]) {
					boolean surrounded = false;
					
					for (int x1 = -1; x1 <= 1; x1++) {
						if(x + x1 < 0 || x + x1 >= copy.length) {
							continue;
						}
						
						for (int y1 = -1; y1 <= 1; y1++) {
							if(y + y1 < 0 || y + y1 >= copy[x].length || (x1 == 0 && y1 == 0)) {
								continue;
							}
							if(copy[x+x1][y+y1]) {
								surrounded = true;
							}
						}
					}
					if(!surrounded) {
						copy[x][y] = false;
					}
				}
			}
		}
		// TODO Auto-generated method stub
		return copy;
	}

}
