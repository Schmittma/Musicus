package interfaces;

import java.util.ArrayList;

public interface SystemDetection {

	//This method takes a binarized image and returns a List of binarized images.
	//The returned images are the Systems of the Music sheet in the order they are played in music.
	//This means the uppermost System will be the first entry in the list and the bottommost will be the last entry.
	public ArrayList<boolean[][]> detectSystems(boolean[][] image);
}
