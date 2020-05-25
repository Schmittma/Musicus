package binarization;

import general.Color;
import general.Color.Grayscale;
import interfaces.Binarization;

/**
 * This Binarization algorithm use the given threshold and compare it against the grayscale value of the pixel.
 * If the threshold is greater than the grayscale value, the pixel is considered foreground (black) otherwise its background.
 * This algorithm should only be used on allready binarized images to revert colors.
 */
public class GTBinarization implements Binarization {

	enum CompareMode {
		SMALLER_EQ_FOREGROUND,
		LARGER_EQ_FOREGROUND
	}
	private int threshold;
	private CompareMode compareMode;
	
	/**
	 * Constructs a new Global threshold binarization with a given global threshold
	 * @param threshold the global threshold (0-255) to be used for differencing between foreground and background color
	 */
	public GTBinarization(int threshold, CompareMode compareMode) {
		this.threshold = Math.min(255, Math.max( 0,threshold));
		this.compareMode = compareMode;
	}
	
	
	@Override
	public boolean[][] binarize(Color[][] imageRGB) {
		
		//if the image has a black foreground and white foreground, reverse it.
		
		boolean[][] binaryIm = new boolean[imageRGB.length][imageRGB[0].length];
		
		for(int x = 0; x < imageRGB.length; x++) {
			for (int y = 0; y < imageRGB[x].length; y++) {
				if(isForeground(imageRGB[x][y].getGrayscale(Grayscale.AVERAGE),threshold)) {
					//foreground
					binaryIm[x][y] = true;
				}
				else {
					//Background
					binaryIm[x][y] = false;
				}
			}
		}
		
		
		return binaryIm;
	}


	private boolean isForeground(int grayscale, int threshold) {
		if(this.compareMode == CompareMode.SMALLER_EQ_FOREGROUND) {
			return grayscale < threshold;
		}
		if(this.compareMode == CompareMode.LARGER_EQ_FOREGROUND) {
			return grayscale > threshold;
		}
		
		//default
		return false;
	}

}
