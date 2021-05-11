package main.start;

import main.binarization.GTBinarization;
import main.binarization.OtsuBinarization;
import main.general.Color;
import main.general.Objektausschnitt;
import main.general.Point;
import main.general.Staffline;
import main.interfaces.Binarization;
import main.interfaces.ObjectFinder;
import main.interfaces.StafflineDetection;
import main.interfaces.StafflineRemoval;
import main.interfaces.SystemDetection;
import main.objectdetection.FloodfillObjectdetection;
import main.stafflinedetection.OrientationStafflineDetection;
import main.stafflineremoval.SimpleStafflineRemoval;
import main.systemdetection.FloodfillSystemDetection;
import main.utils.ImageConverter;
import main.utils.Util;

import javax.imageio.ImageIO;

import org.deeplearning4j.nn.conf.layers.objdetect.BoundingBoxesDeserializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainThread implements Runnable {

    private Color[][] inputImage;
    private String datapath;
    public volatile static int counter = 0;

    public MainThread(Color[][] inputImage, String datapath) {
        this.inputImage = inputImage;
        this.datapath = datapath;
    }

    public static synchronized void changeCounter(int changeValue) {
        MainThread.counter += changeValue;
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
        Binarization binarization = new OtsuBinarization(GTBinarization.CompareMode.SMALLER_EQ_FOREGROUND);
        boolean[][] binaryImage = binarization.binarize(inputImage);

        if (Globals.DEBUG) {
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

        if (Globals.DEBUG) {
            for (int i = 0; i < systems.size(); i++) {
                try {
                    ImageIO.write(ImageConverter.BinaryImageToBuffered(systems.get(i)),
                                  "png",
                                  new File(datapath + Globals.SYSTEM_DETECTION_DATA + "system" + i + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //STAFFLINE DETECTION
        StafflineDetection stafflineDetection = new OrientationStafflineDetection(estimatedStafflineHeight, estimatedWhiteSpace, "");
        ArrayList<ArrayList<Staffline>> stafflinesOfSystems = new ArrayList<>();

        int count = 0;
        for (boolean[][] system : systems) {
            new File(datapath + Globals.STAFFLINE_DETECTION_DATA + "system" + count + "\\").mkdir();
            ((OrientationStafflineDetection) (stafflineDetection)).setDebugPath(datapath + Globals.STAFFLINE_DETECTION_DATA + "system" + count + "\\");
            stafflinesOfSystems.add(stafflineDetection.detectStafflines(system));
            count++;
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

            for (int j = 0; j < stafflinesOfSystems
                .get(i)
                .size(); j++) {
                current = stafflinesOfSystems
                    .get(i)
                    .get(j);

                //We do not want to count the whitespace between two staves
                if (j % numberOfStafflinesPerStaff == 0) {
                    previous = current;
                } else {
                    for (int x = 0; x < systems.get(i).length; x++) {
                        ArrayList<Point> pointsOnXcurr = current.getPointsOnXCoordinate(x);
                        ArrayList<Point> pointsOnXprev = previous.getPointsOnXCoordinate(x);

                        if (pointsOnXcurr.size() > 0) {
                            if (pointsOnXprev.size() > 0) {
                                avgWhitespace += Math.abs((pointsOnXcurr
                                                               .get(0)
                                                               .getY() - pointsOnXprev
                                                               .get(pointsOnXprev.size() - 1)
                                                               .getY()));
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

        if (Globals.DEBUG) {
            System.out.println("Average line width: " + avgLineWidth + " | Estimation: " + estimatedStafflineHeight);
            System.out.println("Average whitespace: " + avgWhitespace + " | Estimation: " + estimatedWhiteSpace);
        }

        //STAFFLINE REMOVAL
        ArrayList<boolean[][]> systemsWithoutLines = new ArrayList<>();

        StafflineRemoval stafflineRemoval = new SimpleStafflineRemoval();
        for (int x = 0; x < systems.size() && x < stafflinesOfSystems.size(); x++) {
            systemsWithoutLines.add(stafflineRemoval.removeStafflines(systems.get(x), stafflinesOfSystems.get(x)));
        }

        if (Globals.DEBUG) {
            for (int i = 0; i < systemsWithoutLines.size(); i++) {
                try {
                    ImageIO.write(ImageConverter.BinaryImageToBuffered(systemsWithoutLines.get(i)),
                                  "png",
                                  new File(datapath + Globals.STAFFLINE_REMOVAL_DATA + "system" + i + ".png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //OBJECT DETECTION

        ArrayList<ArrayList<Objektausschnitt>> objectsOfSystems = new ArrayList<>();

        ObjectFinder finder = new FloodfillObjectdetection(objectfinder_fill_depth);

        for (int i = 0; i < systemsWithoutLines.size(); i++) {
            ArrayList<Objektausschnitt> objects = finder.findObjects(systemsWithoutLines.get(i));
            objectsOfSystems.add(objects);
        }

        if (Globals.DEBUG) {
            for (int i = 0; i < objectsOfSystems.size(); i++) {
                for (int j = 0; j < objectsOfSystems
                    .get(i)
                    .size(); j++) {
                    try {
                        File f = new File(datapath + Globals.OBJECT_DETECTION_DATA + "system" + i + "\\");
                        if (!f.exists()) {
                            f.mkdir();
                        }
                        ImageIO.write(ImageConverter.objektausschnittToImage(objectsOfSystems
                                                                                 .get(i)
                                                                                 .get(j)),
                                      "png",
                                      new File(datapath + Globals.OBJECT_DETECTION_DATA + "system" + i + "\\object" + j + ".png"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        //Get the bounding boxes of each object 
        ArrayList<Point[]> boundingBoxes = new ArrayList<Point[]>();

        for(ArrayList<Objektausschnitt> objectLists : objectsOfSystems){
            for(Objektausschnitt object : objectLists){
                boundingBoxes.add(object.getBoundingBox());
                System.out.println(object.getBoundingBox());
            }
        }

        changeCounter(-1);
        System.out.println("Main Thread finished");
    }

}
