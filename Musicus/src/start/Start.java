package start;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import binarization.GTBinarization;
import binarization.NiblackBinarization;
import binarization.SauvoldaBinarization;
import general.Color;
import general.Objektausschnitt;
import general.Point;
import general.Staffline;
import interfaces.Binarization;
import interfaces.ObjectFinder;
import interfaces.StafflineDetection;
import interfaces.StafflineRemoval;
import interfaces.SystemDetection;
import objectdetection.FloodfillObjectdetection;
import stafflinedetection.OrientationStafflineDetection;
import stafflineremoval.LinetrackingStafflineRemoval;
import stafflineremoval.SimpleStafflineRemoval;
import systemdetection.FloodfillSystemDetection;
import utils.ImageConverter;
import utils.Util;

public class Start implements Runnable{
	
	private static volatile int mainthread_counter;
	
	public static void main(String[] args) {
		
		//Get parameters from user
		if(args.length < 2 || args.length > 3) {
			System.out.println("Usage: java -Xss50m -jar Musicus.jar [data_path] [base_image] [opt: Number of max threads]");
			System.out.println("Bitte als erstes Argument den Pfad zu einem Ordner angeben, in welchem die Daten gespeichert werden können");
			System.out.println("Bitte als zweites Argument den Pfad zum Bild angeben");
			System.out.println("Als drittes Argument kann optional die Anzahl der maximal zu verwendenden Kerne angegeben werden");
			return;
		}
		if(args.length == 3) {
			Globals.NUMBER_OF_CORES = Integer.parseInt(args[2]);
		}
		
		String datapath_base = args[0];
		File imageOrImagefolder = new File(args[1]);
		int numberOfImages = 1;

		//Figure out if the given parameter was a folder and if yes, get the number of images in that folder.
		if(imageOrImagefolder.isDirectory()) {
			numberOfImages = imageOrImagefolder.listFiles().length;
		}
		
		//Clear the data directory
		Globals.purgeDirectory(new File(datapath_base));
		mainthread_counter = 0;
		
		//Repeat this try/catch block for multiple images
		for(int imageCounter = 0; imageCounter < numberOfImages; imageCounter++) {
			String datapath_current_image = datapath_base + "score"+(imageCounter+1)+"\\";
			File currentImage;
			
			if(imageOrImagefolder.isDirectory()) {
				currentImage = imageOrImagefolder.listFiles()[imageCounter];
			}
			else {
				currentImage = imageOrImagefolder;
			}
			
			//Create folder structure
			Globals.mkdir(datapath_current_image);
			Globals.initFileSystem(datapath_current_image);
			
			
			try {
				Files.copy(currentImage.toPath(), new File(datapath_current_image+"original_image.png").toPath(), StandardCopyOption.REPLACE_EXISTING);
				
				BufferedImage bi = ImageIO.read(currentImage);
				Color[][] image = ImageConverter.bufferedImageToColorArray(bi);
				
				//Start the mainthread if the number of running threads doesn't exceed the number of available cores
				while(mainthread_counter >= Globals.NUMBER_OF_CORES) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				mainthread_counter++;
				Start instance = new Start(image, datapath_current_image);
				Thread mainThread = new Thread(instance);
				mainThread.start();
				
			}catch(IOException e){
				e.printStackTrace();
				System.out.println("File couldn't be read, skipping image");
			}
		}
		
					
	}

//---------- Static end, Class start ----------------	
	
	private Color[][] inputImage;
	private String datapath;
			
	public Start(Color[][] inputImage, String datapath) {
		this.inputImage = inputImage;
		this.datapath = datapath;
	}
	
	//Main Thread
	public void run() {
		
		System.out.println("Main Thread started");
		//Variables
		int binarisation_window_size = 15;
		double binarisation_weight = -0.2;
		int GT_Binarisation_threshold = 127;
		
		int systemdetection_fill_depth = 3;
		double systemdetection_threshold = 0.5;
		
		double stafflinedetection_threshold = 0.5;
		
		double stafflineremoval_lengthMul = 2.5; //1.75
		int stafflineremoval_resolution = 5; //3
		int stafflineremoval_minimumAngle = 30; //30
		
		int objectfinder_fill_depth = 4;
		
		// BINARISATION
		Binarization binarization = new NiblackBinarization(binarisation_window_size, binarisation_weight);
		boolean[][] binaryImage = binarization.binarize(inputImage);
		
		if(Globals.DEBUG) {
			try {
				ImageIO.write(ImageConverter.BinaryImageToBuffered(binaryImage), "png", new File(datapath + Globals.BINARISATION_DATA + "score_binarized.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int estimatedStafflineHeight = Util.estimateStaffLineHeight(binaryImage);
		int estimatedWhiteSpace = Util.estimateStaffSpaceHeight(binaryImage);
		
		//SYSTEM DETECTION
		SystemDetection systemDetection = new FloodfillSystemDetection(systemdetection_fill_depth, systemdetection_threshold);
		ArrayList<boolean[][]> systems = systemDetection.detectSystems(binaryImage);

		if(Globals.DEBUG) {
			for (int i = 0; i < systems.size(); i++) {
				try {
					ImageIO.write(ImageConverter.BinaryImageToBuffered(systems.get(i)), "png", new File(datapath + Globals.SYSTEM_DETECTION_DATA + "system"+i+".png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		//STAFFLINE DETECTION
		StafflineDetection stafflineDetection = new OrientationStafflineDetection(estimatedStafflineHeight, estimatedWhiteSpace, datapath);
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
			
				//We do not want to count the whitespace between two staves
				if(j % numberOfStafflinesPerStaff == 0) {
					previous = current;
				}
				else {
					for(int x = 0; x < systems.get(i).length; x++) {
						ArrayList<Point> pointsOnXcurr = current.getPointsOnXCoordinate(x);
						ArrayList<Point> pointsOnXprev = previous.getPointsOnXCoordinate(x);
						
						if(pointsOnXcurr.size() > 0) {
							if( pointsOnXprev.size() > 0) {
								avgWhitespace += Math.abs((pointsOnXcurr.get(0).getY() - pointsOnXprev.get(pointsOnXprev.size()-1).getY())); 
								countWhite++;
							}
							avgLineWidth += pointsOnXcurr.size();
							countLine++;
						}
					}
					previous = current;
				}
			}
		}
		
		avgLineWidth = Math.round(avgLineWidth / countLine);
		avgWhitespace = Math.round(avgWhitespace / countWhite);
		
		if(Globals.DEBUG) {
			System.out.println("Average line width: " + avgLineWidth + " | Estimation: " + estimatedStafflineHeight);
			System.out.println("Average whitespace: " + avgWhitespace + " | Estimation: " + estimatedWhiteSpace);
		}
		 
		//STAFFLINE REMOVAL
		ArrayList<boolean[][]> systemsWithoutLines = new ArrayList<>();
		
		StafflineRemoval stafflineRemoval = new SimpleStafflineRemoval();
		for(int x = 0; x < systems.size() && x < stafflinesOfSystems.size(); x++) {
			systemsWithoutLines.add(stafflineRemoval.removeStafflines(systems.get(x), stafflinesOfSystems.get(x)));
		}
		
		if(Globals.DEBUG) {
			for (int i = 0; i < systemsWithoutLines.size(); i++) {
				try {
					ImageIO.write(ImageConverter.BinaryImageToBuffered(systemsWithoutLines.get(i)), "png", new File(datapath + Globals.STAFFLINE_REMOVAL_DATA + "system"+i+".png"));
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
						File f = new File(datapath + Globals.OBJECT_DETECTION_DATA + "system"+i+"\\");
						if(!f.exists()) {
							f.mkdir();
						}
						ImageIO.write(ImageConverter.objektausschnittToImage(objectsOfSystems.get(i).get(j)), "png", new File(datapath + Globals.OBJECT_DETECTION_DATA + "system"+i+ "\\object"+j+".png"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		
		mainthread_counter--;
		System.out.println("Main Thread finished");
	}
	
	
}


