package main.interfaces;

import java.util.ArrayList;

import main.general.Staffline;

public interface StafflineDetection {

    //Returns all stafflines of the given System
    public ArrayList<Staffline> detectStafflines(boolean[][] system);
}
