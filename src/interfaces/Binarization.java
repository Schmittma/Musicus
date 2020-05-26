package interfaces;

import general.Color;

public interface Binarization {

	//This method converts the given RGB image into a binarised image where 
	// true shall be the foreground of the image and
	// false be the background of the image
	//The coordinates of the RGB image have to match the binarized image
	//Thus the Color[][] will have the same length as the boolean[][]
	public boolean[][] binarize(Color[][] imageRGB);
}
