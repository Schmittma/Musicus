package utils;

import java.util.ArrayList;

public class Util {

	/**
	 * Extracts the vertical run lengths of the image with the white pixels beeing the first run.
	 * This also means that white runs can be found at even (considering 0 as even) indices and black runs at odd indices of the ArrayList
	 * Every entry in the ArrayList corresponds to the next vertical line in the image beginning from left and traversing to the right 
	 * 
	 * @param image binary base image
	 * @return the vertical run lengths with the first run being white
	 */
	public static ArrayList<ArrayList<Integer>> GetVerticalRunLengths(boolean[][] image){
		ArrayList<ArrayList<Integer>> runlengths = new ArrayList<>();
		
		for(int x = 0; x < image.length; x++) {
			ArrayList<Integer> runlength = new ArrayList<>();
			int count = image[x][0] == false ? 1 : 0; //Check if first pixel is black or white (foreground / background)
			
			for(int y = 1; y < image[x].length; y++) {
				if(image[x][y] == image[x][y-1]) {
					count++;
				}
				else {
					runlength.add(count);
					count = 1;
				}
			}
			runlengths.add(runlength);
		}
		
		
		return runlengths;
	}
}
