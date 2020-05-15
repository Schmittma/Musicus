package start;

import java.io.File;

public class Globals {
	
	public static final boolean DEBUG = true;
	//PATH TO DATA FOLDER ON FILE SYSTEM
	public static final String RESOURCES_PATH = "C:\\Users\\Marius\\Musicus\\Musicus\\resources\\";
	
	public static final String BINARISATION_DATA = "binarisierung\\";
	public static final String SYSTEM_DETECTION_DATA = "system_detection\\";
	public static final String STAFFLINE_DETECTION_DATA = "staffline_detection\\";
	public static final String STAFFLINE_REMOVAL_DATA = "staffline_removal\\";
	public static final String OBJECT_DETECTION_DATA = "object_detection\\";
	
	public static int NUMBER_OF_CORES = 1;
	
	//Add paths of folders here, that shall be deleted and newly created upon start of program.
	// All contents of the folders in the list will be deleted.
	private static final String[] REFRESH_LIST = {
			BINARISATION_DATA,
			SYSTEM_DETECTION_DATA,
			STAFFLINE_DETECTION_DATA,
			STAFFLINE_REMOVAL_DATA,
			OBJECT_DETECTION_DATA
	};

	public static void initFileSystem(String base) {
		for(String path : REFRESH_LIST) {
			File f = new File(base + path);
			if(f.exists()) {
				purgeDirectory(f);
			}
			if(!f.exists()) {
				f.mkdir();
			}
		}
	}
	
	public static void purgeDirectory(File dir) {
	    for (File file: dir.listFiles()) {
	        if (file.isDirectory())
	            purgeDirectory(file);
	        file.delete();
	    }
	}
	
	public static void mkdir(String path) {
		File f = new File(path);
		if(f.exists()) {
			purgeDirectory(f);
		}
		if(!f.exists()) {
			f.mkdir();
		}
	}

}
