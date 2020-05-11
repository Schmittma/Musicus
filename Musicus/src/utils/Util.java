package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
					System.out.print(count + " ");
					count = 1;
				}
			}
			System.out.println();
			runlength.add(count);
			runlengths.add(runlength);
		}
		
		
		return runlengths;
	}
	
	public static int estimateStaffLineHeight(boolean[][] base) {
		ArrayList<ArrayList<Integer>> runlengths = GetVerticalRunLengths(base);
		HashMap<Integer, Integer> count = new HashMap<>();
		//Stafflineheight -> Black runs -> Odd indicies
		for(int x = 0; x < runlengths.size(); x++) {
			for(int y = 1; y < runlengths.get(x).size();y+=2) {
				if(count.containsKey(runlengths.get(x).get(y))) {
					count.put(runlengths.get(x).get(y), count.get(runlengths.get(x).get(y))+1);
				}
				else {
					count.put(runlengths.get(x).get(y), 1);
				}
			}
		}
		
		int maxKey = 0;
		int maxVal = 0;
		
		for(Map.Entry<Integer,Integer> entry : count.entrySet()) {
			if(entry.getValue() > maxVal) {
				maxKey = entry.getKey();
				maxVal = entry.getValue();
			}
		}
		
		return maxKey;
	}
	
	public static int estimateStaffSpaceHeight(boolean[][] base) {
		ArrayList<ArrayList<Integer>> runlengths = GetVerticalRunLengths(base);
		HashMap<Integer, Integer> count = new HashMap<>();
		//Stafflineheight -> Black runs -> Odd indicies
		for(int x = 0; x < runlengths.size(); x++) {
			for(int y = 0; y < runlengths.get(x).size();y+=2) {
				if(count.containsKey(runlengths.get(x).get(y))) {
					count.put(runlengths.get(x).get(y), count.get(runlengths.get(x).get(y))+1);
				}
				else {
					count.put(runlengths.get(x).get(y), 1);
				}
			}
		}
		
		int maxKey = 0;
		int maxVal = 0;
		
		for(Map.Entry<Integer,Integer> entry : count.entrySet()) {
			if(entry.getValue() > maxVal) {
				maxKey = entry.getKey();
				maxVal = entry.getValue();
			}
		}
		
		return maxKey;
	}
	
	/**
	 * Copies n*m sized arrays
	 * 
	 * @param baseArray
	 * @return
	 */
	public static boolean[][] copyArray(boolean[][] baseArray){
		
		boolean[][] newArray = new boolean[baseArray.length][baseArray[0].length];
		for(int x = 0; x < baseArray.length; x++) {
			for (int y = 0; y < baseArray[x].length; y++) {
				newArray[x][y] = baseArray[x][y];
			}
		}
		
		return newArray;
	}
}
