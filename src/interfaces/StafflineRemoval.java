package interfaces;

import general.Staffline;

import java.util.ArrayList;

public interface StafflineRemoval {

    //This method returns the given Staff image without the stafflines
    //The method may not alter the original staffImage.
    public boolean[][] removeStafflines(boolean[][] staffImage, ArrayList<Staffline> stafflines);
}
