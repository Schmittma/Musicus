package start;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import binarization.NiblackBinarization;
import general.Color;
import general.Staffline;
import interfaces.Binarization;
import interfaces.StafflineDetection;
import interfaces.SystemDetection;
import stafflinedetection.ProjectionStafflineDetection;
import systemdetection.FloodfillSystemDetection;
import utils.ImageConverter;

public class Start implements Runnable{
	
	public static void main(String[] args) {
		
		Globals.initFileSystem();
		File f1 = new File(Globals.DATAPATH_BASE + "OdeToJoy.png");
		
		//Repeat this try/catch block for multiple images
		try {
			//Read first image
			BufferedImage bi = ImageIO.read(f1);
			Color[][] odeToJoy = ImageConverter.bufferedImageToColorArray(bi);
			
			if(Globals.DEBUG) ImageIO.write(ImageConverter.ColorArrayToBuffered(odeToJoy), "png", new File(Globals.DATAPATH_BASE + "OdeToJoy_Reconverted.png"));
			
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
		
		// BINARISATION
		int window_size = 15;
		double weight = -0.2;
		
		Binarization binarization = new NiblackBinarization(window_size, weight);
		boolean[][] binaryImage = binarization.binarize(inputImage);
		
		if(Globals.DEBUG) {
			try {
				ImageIO.write(ImageConverter.BinaryImageToBuffered(binaryImage), "png", new File(Globals.DATAPATH_BASE + Globals.BINARISATION_DATA + "OdeToJoy_Binarized.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//SYSTEM DETECTION
		int fill_depth = 3;
		double hotizontal_threshold_percentage = 0.5;
		SystemDetection systemDetection = new FloodfillSystemDetection(fill_depth, hotizontal_threshold_percentage);
		ArrayList<boolean[][]> systems = systemDetection.detectSystems(binaryImage);

		if(Globals.DEBUG) {
			for (int i = 0; i < systems.size(); i++) {
				try {
					ImageIO.write(ImageConverter.BinaryImageToBuffered(systems.get(i)), "png", new File(Globals.DATAPATH_BASE + Globals.SYSTEM_DETECTION_DATA + "system"+i+".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//STAFFLINE DETECTION
		double staffline_threshold = 0.7;
		StafflineDetection stafflineDetection = new ProjectionStafflineDetection(staffline_threshold);
		ArrayList<Staffline> stafflines;
		
		for(boolean[][] system : systems) {
			stafflines = stafflineDetection.detectStafflines(system);
		}
	}
	
	
}


