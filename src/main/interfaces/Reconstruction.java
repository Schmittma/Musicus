package main.interfaces;

import java.util.ArrayList;

import main.general.MusicalObject;
import main.general.Staffline;
import main.general.Tone;

public interface Reconstruction {

    //Reconstruct the musical representation by using the rules of music to combine and interpret the musicalobjects
    public ArrayList<Tone> reconstruct(ArrayList<MusicalObject> musicalObjects, ArrayList<Staffline> stafflines);
}
