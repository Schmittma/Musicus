package main.java.interfaces;

import java.util.ArrayList;

import main.java.general.Staffline;

public interface StafflineDetection {

    //Returns all stafflines of the given System
    public ArrayList<Staffline> detectStafflines(boolean[][] system);
}
