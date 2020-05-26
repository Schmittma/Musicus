package interfaces;

import java.util.ArrayList;

import general.Objektausschnitt;

public interface ObjectFinder {

	
	//Searches the image for concatenated objects and stores them in a List.
	//Each foreground pixel in the image shall be included in atleast 1 but not more than 1 Objektausschnitte.
	public ArrayList<Objektausschnitt> findObjects(boolean[][] image);
}
