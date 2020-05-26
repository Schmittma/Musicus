package interfaces;

import java.util.ArrayList;

import general.MusicalObject;
import general.Objektausschnitt;

public interface Classification {

	//This method shall classify the objects in the list of objects and return them as Musicalobjects
	public ArrayList<MusicalObject> classify(ArrayList<Objektausschnitt> objects, double averageWhitespace, double averageLineWidth);
}
