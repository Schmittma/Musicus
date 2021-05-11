package main.interfaces;

import java.util.ArrayList;

import main.general.Staffline;

public interface StafflineRemoval {

    //This method returns the given Staff image without the stafflines
    //The method may not alter the original staffImage.
    public boolean[][] removeStafflines(boolean[][] staffImage, ArrayList<Staffline> stafflines);
}
