package stafflineremoval;

import java.util.ArrayList;

import general.Staffline;
import interfaces.StafflineRemoval;

//This class implements the Linetracking Chord algorithm of "A Comparative Study of Staff Removal Algorithms" Page 3
public class LinetrackingStafflineRemoval implements StafflineRemoval{

	@Override
	public boolean[][] removeStafflines(boolean[][] staffImage, ArrayList<Staffline> stafflines) {
		
		boolean[][] ret = staffImage.clone();
		
		
		for(Staffline staffline : stafflines) {
			
		}
		
		return null;
	}

}
