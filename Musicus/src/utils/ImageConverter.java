package utils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import general.Color;
import general.Color.Grayscale;
import general.Objektausschnitt;
import general.Point;



//This is a utility class used for converting various image data types into other data image types.
public final class ImageConverter {

	//Restrict the instantiation of this object
	private ImageConverter() {}
	
	/**
	 * Converts a BufferedImage into a 2D-{@linkplain general.Color Color} array in the order [X][Y]
	 * 
	 * @param inputImage the BufferedImage to be converted
	 * @return a 2D-{@linkplain general.Color Color} array
	 */
	public static Color[][] bufferedImageToColorArray(BufferedImage inputImage){
		
		Color[][] ret = new Color[inputImage.getWidth()][inputImage.getHeight()];
		
		for(int x = 0; x < ret.length; x++) {
			for(int y = 0; y < ret[x].length; y++) {
				ret[x][y] = new Color(inputImage.getRGB(x, y));
			}
		}
		return ret;
	}
	
	public static BufferedImage ColorArrayToBuffered(Color[][] inputImage) {
		
		BufferedImage ret = new BufferedImage(inputImage.length, inputImage[0].length, BufferedImage.TYPE_4BYTE_ABGR);
		
		for(int x = 0; x < inputImage.length; x++) {
			for(int y = 0; y < inputImage[x].length; y++) {
				ret.setRGB(x, y, inputImage[x][y].getARGB());
			}
		}
		
		return ret;
	}
	
	public static BufferedImage BinaryImageToBuffered(boolean[][] inputImage) {
		BufferedImage ret = new BufferedImage(inputImage.length, inputImage[0].length, BufferedImage.TYPE_3BYTE_BGR);
		
		for(int x = 0; x < inputImage.length; x++) {
			for(int y = 0; y < inputImage[x].length; y++) {
				if(inputImage[x][y]) {
					ret.setRGB(x, y, 0);
				}
				else {
					ret.setRGB(x, y, 0xffffffff);
				}
				
			}
		}
		
		return ret;
		
	}

	public static BufferedImage objektausschnittToImage(Objektausschnitt objektausschnitt) {
		int width = objektausschnitt.getOffsetXright() - objektausschnitt.getOffsetXleft() + 1;
		int height = objektausschnitt.getOffsetYdown() - objektausschnitt.getOffsetYup() + 1;
		
		BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		
		ArrayList<Point> points = objektausschnitt.getCoordinates();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				ret.setRGB(i, j, 0xffffffff);
			}
		}
		
		for(Point point : points) {
			ret.setRGB(point.getX() - objektausschnitt.getOffsetXleft(), point.getY() - objektausschnitt.getOffsetYup(), 0x00000000);
		}
		
		
		return ret;
	}
	

	public static BufferedImage horizontalProjectionToImage(int[] projection, int width){
		BufferedImage image = new BufferedImage(width, projection.length, BufferedImage.TYPE_BYTE_BINARY);
		
		for (int y = 0; y < projection.length; y++) {
			for(int x = 0; x < width; x++){
				if(x < projection[y]){
					image.setRGB(x, y, 0);
				}
				else{
					image.setRGB(x,y, 0xFFFFFFFF);
				}
			}
		}
		
		return image;
	}
	

	public static BufferedImage verticalProjectionToPImage(int[] projection, int height){
		BufferedImage image = new BufferedImage(projection.length, height, BufferedImage.TYPE_BYTE_BINARY);
		
		for (int x = 0; x < projection.length; x++) {
			for(int y = 0; y < height; y++){
				if(y >= height - projection[x]){
					image.setRGB(x, y, 0);
				}
				else{
					image.setRGB(x, y, 0xFFFFFFFF);
				}
				
			}
		}
		
		return image;
	}
	
	public static int[][] calculateGrayscale(Color[][] imageRGB){
		int[][] ret = new int[imageRGB.length][imageRGB[0].length];
		
		for (int x = 0; x < ret.length; x++) {
			for (int y = 0; y < ret[x].length; y++) {
				ret[x][y] = imageRGB[x][y].getGrayscale(Grayscale.AVERAGE);
			}
		}
		
		return ret;
	}
}
