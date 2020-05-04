package systemdetection;

import java.util.ArrayList;

import interfaces.SystemDetection;

public class FloodfillSystemDetection implements SystemDetection{

	private int fd;
	private double htp; //Horizontal threshold percentage. -> The minimum width of a found object to be considered a system
	
	public FloodfillSystemDetection(int fd, double horizontal_threshold_percentage) {
		super();
		this.fd = fd;
		this.htp = horizontal_threshold_percentage;
	}

	@Override
	public ArrayList<boolean[][]> detectSystems(boolean[][] image) {
		
		ArrayList<boolean[][]> systems = new ArrayList<>();
		
		//Copy the array
		boolean[][] map = image.clone();
		
		//False means either, that the pixel is white (And thus cannot be an object)
		// or it means, that we allready visited it.
		//In both cases we need to ignore it
		
		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[x].length; y++) {
				if(map[x][y] == true){
					//Found a new object, that has not yet been connected to anything
					//We create a List of integer pairs (int array[2]) where index 1 is x and index 2 is y coordinate
					ArrayList<int[]> entries = new ArrayList<>(); // Storage for all connected pairs
					floodFill(x, y, map, entries);
					boolean[][] bitmap = entriesToBitmap(entries);
					
					if(bitmap.length > (image.length * this.htp)) {
						systems.add(bitmap);
					}
					
					
				}
			}
		}
		return systems;
	}


	private boolean[][] entriesToBitmap(ArrayList<int[]> entries) {
		int indexX = 0;
		int indexY = 1;
		
		//Find the max and min X and Y values:
		int maxX = entries.get(0)[indexX];
		int minX = maxX;
		
		int minY = entries.get(0)[indexY];
		int maxY = minY;
		
		for(int x = 1; x < entries.size(); x++) {
			maxX = Math.max(entries.get(x)[indexX], maxX);
			minX = Math.min(entries.get(x)[indexX], minX);
			maxY = Math.max(entries.get(x)[indexY], maxY);
			minY = Math.min(entries.get(x)[indexY], minY);
		}
		
		int sizeX = maxX - minX + 1;
		int sizeY = maxY - minY + 1;
		
		boolean[][] bitmap = new boolean[sizeX][sizeY];
		
		for(int[] entry : entries) {
			int x = entry[indexX] - minX;
			int y = entry[indexY] - minY;
			bitmap[x][y] = true;
		}
		
		return bitmap;
	}

	
	//Searches for coordinates connected to this object
	private void floodFill(int x, int y, boolean[][] map, ArrayList<int[]> entries) {
		//Check bounds
		if(x < 0 || y < 0 || x >= map.length || y >= map[x].length){
			return;
		}
		
		//Check if we allready visited or if the pixel is white
		if(map[x][y] == false){
			return;
		}
		int[] entry = {x, y};
		entries.add(entry);
		map[x][y] = false;
		
		for(int i = -1 * fd; i <= fd; i++){
			for(int j = -1 * fd; j <= fd; j++){
				if(!(i == 0 && j == 0)){
					floodFill(x + i, y + j, map, entries);
				}
			}
		}
	}
	

	
}
