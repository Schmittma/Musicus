package main.java.interfaces;

import java.util.ArrayList;

import main.java.general.MusicalObject;
import main.java.general.Staffline;
import main.java.general.Tone;

public interface Reconstruction {

    //Reconstruct the musical representation by using the rules of music to combine and interpret the musicalobjects
    public ArrayList<Tone> reconstruct(ArrayList<MusicalObject> musicalObjects, ArrayList<Staffline> stafflines);
}
