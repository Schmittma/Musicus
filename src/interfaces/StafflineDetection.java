package interfaces;

import general.Staffline;

import java.util.ArrayList;

public interface StafflineDetection {

    //Returns all stafflines of the given System
    public ArrayList<Staffline> detectStafflines(boolean[][] system);
}
