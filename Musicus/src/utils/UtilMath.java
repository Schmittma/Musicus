package utils;

public class UtilMath {

	/**
	 * Takes any amount of integers and returns the maxmimum value
	 * @param values
	 * @return
	 */
	public static int max(int ... values) {
	
		int maxVal = values[0];
		for(int x = 1; x < values.length - 1; x++) {
			maxVal = Math.max(maxVal, values[x]);
		}
		
		return maxVal;
	}
	
	/**
	 * Takes any amount of integers and returns the minimum value
	 * @param values
	 * @return
	 */
	public static int min(int ... values) {
		
		int minVal = values[0];
		for(int x = 1; x < values.length - 1; x++) {
			minVal = Math.min(minVal, values[x]);
		}
		
		return minVal;
	}
}
