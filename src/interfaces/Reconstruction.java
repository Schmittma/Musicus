package interfaces;

import general.MusicalObject;
import general.Staffline;
import general.Tone;

import java.util.ArrayList;

public interface Reconstruction {

    //Reconstruct the musical representation by using the rules of music to combine and interpret the musicalobjects
    public ArrayList<Tone> reconstruct(ArrayList<MusicalObject> musicalObjects, ArrayList<Staffline> stafflines);
}
