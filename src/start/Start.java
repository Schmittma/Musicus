package start;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import general.Color;
import utils.ImageConverter;

public class Start{
	
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
				while(MainThread.counter >= Globals.NUMBER_OF_CORES) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				MainThread.changeCounter(1);
				MainThread instance = new MainThread(image, datapath_current_image);
				Thread mainThread = new Thread(instance);
				mainThread.start();
				
			}catch(IOException e){
				e.printStackTrace();
				System.out.println("File couldn't be read, skipping image");
			}
		}
		
					
	}

//---------- Static end, Class start ----------------	
	
	
}


