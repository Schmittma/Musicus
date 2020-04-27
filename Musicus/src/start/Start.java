package start;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import binarization.NiblackBinarization;
import general.Color;
import interfaces.Binarization;
import utils.ImageConverter;

public class Start implements Runnable{

	public static final boolean DEBUG = true;
	public static final String DATAPATH = "C:\\Users\\Marius\\Musicus\\Musicus\\data\\";

	
	public static void main(String[] args) {
		
		File f1 = new File(DATAPATH + "OdeToJoy.png");
		
		//Repeat this try/catch block for multiple images
		try {
			//Read first image
			BufferedImage bi = ImageIO.read(f1);
			Color[][] odeToJoy = ImageConverter.bufferedImageToColorArray(bi);
			
			if(DEBUG) ImageIO.write(ImageConverter.ColorArrayToBuffered(odeToJoy), "png", new File(DATAPATH + "OdeToJoy_Reconverted.png"));
			
			//Start the mainthread
			Start instance = new Start(odeToJoy);
			Thread mainThread = new Thread(instance);
			mainThread.start();
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("File couldn't be read, skipping image");
		}
					
	}

//---------- Static end, Class Start start ----------------	
	
	private Color[][] inputImage;
			
	public Start(Color[][] inputImage) {
		this.inputImage = inputImage;
	}
	
	//Main Thread
	public void run() {
		
		int window_size = 15;
		double weight = -0.2;
		
		Binarization binarization = new NiblackBinarization(window_size, weight);
		boolean[][] binaryImage = binarization.binarize(inputImage);
		
		if(DEBUG) {
			try {
				ImageIO.write(ImageConverter.BinaryImageToBuffered(binaryImage), "png", new File(DATAPATH + "OdeToJoy_Binarized.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		
	}
	
}


