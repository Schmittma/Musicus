package start;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import binarization.NiblackBinarization;
import general.Color;
import general.Objektausschnitt;
import general.Staffline;
import interfaces.Binarization;
import interfaces.ObjectFinder;
import interfaces.StafflineDetection;
import interfaces.StafflineRemoval;
import interfaces.SystemDetection;
import objectdetection.FloodfillObjectdetection;
import stafflinedetection.ProjectionStafflineDetection;
import stafflineremoval.ClarkeStafflineRemoval;
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

//---------- Static end, Class start ----------------	
	
	private Color[][] inputImage;
			
	public Start(Color[][] inputImage) {
		this.inputImage = inputImage;
	}
	
	//Main Thread
	public void run() {
		
		//Variables
		int binarisation_window_size = 15;
		double binarisation_weight = -0.2;
		
		int systemdetection_fill_depth = 3;
		double systemdetection_threshold = 0.5;
		
		double stafflinedetection_threshold = 0.5;
		
		int objectfinder_fill_depth = 4;
		
		// BINARISATION
		Binarization binarization = new NiblackBinarization(binarisation_window_size, binarisation_weight);
		boolean[][] binaryImage = binarization.binarize(inputImage);
		
		if(Globals.DEBUG) {
			try {
				ImageIO.write(ImageConverter.BinaryImageToBuffered(binaryImage), "png", new File(Globals.DATAPATH_BASE + Globals.BINARISATION_DATA + "OdeToJoy_Binarized.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//SYSTEM DETECTION
		SystemDetection systemDetection = new FloodfillSystemDetection(systemdetection_fill_depth, systemdetection_threshold);
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
		StafflineDetection stafflineDetection = new ProjectionStafflineDetection(stafflinedetection_threshold);
		ArrayList<ArrayList<Staffline>> stafflinesOfSystems = new ArrayList<>();
		
		for(boolean[][] system : systems) {
			stafflinesOfSystems.add(stafflineDetection.detectStafflines(system));
		}
		
		
		//Calculate Avg. Whitespace and Avg. Linethickness
		//Assuming we only have 5 stafflines per staff
		int numberOfStafflinesPerStaff = 5;
		double avgWhitespace = 0;
		double avgLineWidth = 0;
		int countWhite = 0;
		int countLine = 0;

		for (int i = 0; i < stafflinesOfSystems.size(); i++) {
			Staffline previous = null;
			Staffline current = null;
			
			for (int j = 0; j < stafflinesOfSystems.get(i).size(); j++) {
				current = stafflinesOfSystems.get(i).get(j);
				
				if(j % numberOfStafflinesPerStaff == 0) {
					previous = current;
				}
				else {
					//Distance between current line topmost and previous line bottommost for start and endpoint.
					double whitespaceStart = Math.abs((previous.getStartPoint().getY() + (previous.getWidth() - 1)) - current.getStartPoint().getY() - 1);
					double whitespaceEnd = Math.abs((previous.getEndPoint().getY() + (previous.getWidth() - 1)) - current.getEndPoint().getY() - 1);
					avgWhitespace +=  (whitespaceStart + whitespaceEnd) / 2;
					
					countWhite++;
					previous = current;
				}
				
				avgLineWidth += current.getWidth();
				countLine++;
				
			}
		}
		
		avgLineWidth /= countLine;
		avgWhitespace /= countWhite;
			
		if(Globals.DEBUG) {
			System.out.println("Average line width: " + avgLineWidth);
			System.out.println("Average whitespace: " + avgWhitespace);
		}
		 
		//STAFFLINE REMOVAL
		ArrayList<boolean[][]> systemsWithoutLines = new ArrayList<>();
		
		StafflineRemoval stafflineRemoval = new ClarkeStafflineRemoval();
		for(int x = 0; x < systems.size() && x < stafflinesOfSystems.size(); x++) {
			systemsWithoutLines.add(stafflineRemoval.removeStafflines(systems.get(x), stafflinesOfSystems.get(x)));
		}
		
		if(Globals.DEBUG) {
			for (int i = 0; i < systemsWithoutLines.size(); i++) {
				try {
					ImageIO.write(ImageConverter.BinaryImageToBuffered(systemsWithoutLines.get(i)), "png", new File(Globals.DATAPATH_BASE + Globals.STAFFLINE_REMOVAL_DATA + "system"+i+".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//OBJECT DETECTION

		ArrayList<ArrayList<Objektausschnitt>> objectsOfSystems = new ArrayList<>();
		
		ObjectFinder finder = new FloodfillObjectdetection(objectfinder_fill_depth);
		
		for(int i = 0; i < systemsWithoutLines.size(); i++) {
			ArrayList<Objektausschnitt> objects = finder.findObjects(systemsWithoutLines.get(i));
			objectsOfSystems.add(objects);
		}
		
		if(Globals.DEBUG) {
			for (int i = 0; i < objectsOfSystems.size(); i++) {
				for(int j = 0; j < objectsOfSystems.get(i).size(); j++) {
					try {
						File f = new File(Globals.DATAPATH_BASE + Globals.OBJECT_DETECTION_DATA + "system"+i+"\\");
						if(!f.exists()) {
							f.mkdir();
						}
						ImageIO.write(ImageConverter.objektausschnittToImage(objectsOfSystems.get(i).get(j)), "png", new File(Globals.DATAPATH_BASE + Globals.OBJECT_DETECTION_DATA + "system"+i+ "\\object"+j+".png"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	
}


