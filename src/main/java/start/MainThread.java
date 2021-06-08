package main.java.start;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import main.java.binarization.GTBinarization;
import main.java.binarization.OtsuBinarization;
import main.java.general.Color;
import main.java.general.Objektausschnitt;
import main.java.general.Point;
import main.java.general.Staffline;
import main.java.interfaces.Binarization;
import main.java.interfaces.ObjectFinder;
import main.java.interfaces.StafflineDetection;
import main.java.interfaces.StafflineRemoval;
import main.java.interfaces.SystemDetection;
import main.java.objectdetection.FloodfillObjectdetection;
import main.java.stafflinedetection.OrientationStafflineDetection;
import main.java.stafflineremoval.BellissantStafflineRemoval;
import main.java.stafflineremoval.ClarkeStafflineRemoval;
import main.java.stafflineremoval.LinetrackingStafflineRemoval;
import main.java.stafflineremoval.SimpleStafflineRemoval;
import main.java.systemdetection.FloodfillSystemDetection;
import main.java.utils.ImageConverter;
import main.java.utils.Util;
import main.java.utils.UtilMath;

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
        
        /* ------ Variables ------ */
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

        /* ------ BINARIZATION ------ */
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

        /* ------ SYSTEM DETECTION ------ */
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

        /* ------ STAFFLINE DETECTION ------ */
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
        int numberOfSystemsInStaff = stafflinesOfSystems.get(0).size() / numberOfStafflinesPerStaff; //Zu deutsch, Anzahl von Akkoladen pro system
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


        /* ------ STAFFLINE REMOVAL ------ */
        ArrayList<boolean[][]> systemsWithoutLines = new ArrayList<>();
  
        //StafflineRemoval stafflineRemoval = new LinetrackingStafflineRemoval(stafflineremoval_minimumAngle, stafflineremoval_lengthMul, stafflineremoval_resolution);
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

        /* ------ OBJECT DETECTION ------ */

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

        /* ------ OUTPUT BOUNDING BOXES ------ */
        ArrayList<BufferedImage> systems_as_image = new ArrayList<>();

        JSONObject json = new JSONObject();
        JSONArray json_systems = new JSONArray();

        for(int x = 0; x < systems.size(); x++){
            // Filter some objects out and split possible notes into several ones.
            ArrayList<Objektausschnitt> objectList = split_notes(filter_non_notes(objectsOfSystems.get(x), estimatedWhiteSpace, estimatedStafflineHeight), estimatedWhiteSpace, estimatedStafflineHeight );

            BufferedImage curr_system = ImageConverter.BinaryImageToBuffered(systems.get(x));
            Graphics2D curr_system_graphics = curr_system.createGraphics();

            JSONArray json_objectsArray = new JSONArray();

            for(Objektausschnitt object : objectList){
                Point[] bounds = object.getBoundingBox();
                
                JSONObject top_left = new JSONObject();
                JSONObject bottom_right = new JSONObject();
                
                top_left.put("x", bounds[0].getX());
                top_left.put("y", 0);

                bottom_right.put("x", bounds[3].getX());
                bottom_right.put("y", systems.get(0)[0].length - 1);

                JSONObject object_entry = new JSONObject();

                object_entry.put("top_left", top_left);
                object_entry.put("bottom_right", bottom_right);

                json_objectsArray.add(object_entry);

                curr_system_graphics.setColor(java.awt.Color.RED);
                curr_system_graphics.drawRect(bounds[0].getX(), 0, bounds[3].getX() - bounds[0].getX(), systems.get(0)[0].length - 1);
            }

            json_systems.add(json_objectsArray);
            systems_as_image.add(curr_system);
        }

        json.put("systems", json_systems);


        try{
            new File(datapath + Globals.OBJECT_BOUNDING_BOXES_DATA).mkdir();
            
            //Write JSON
            File json_file = new File(datapath + Globals.OBJECT_BOUNDING_BOXES_DATA + "bounding_boxes.json");
            json_file.createNewFile();
            FileWriter file_writer = new FileWriter(json_file);
            file_writer.write(json.toJSONString());
            file_writer.close();

            for(int x = 0; x < systems_as_image.size(); x++) {
                ImageIO.write(systems_as_image.get(x), "png", new File(datapath + Globals.OBJECT_BOUNDING_BOXES_DATA + "system" + x + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        changeCounter(-1);
        System.out.println("Main Thread finished");
    }

    //Tries to detect connected music notes and cut them into single ones.
    private ArrayList<Objektausschnitt> split_notes(ArrayList<Objektausschnitt> objects, int whitespace, int staffheight) {
        ArrayList<Objektausschnitt> return_list = new ArrayList<Objektausschnitt>();


        for(Objektausschnitt obj : objects){
            //Probably no multinote
            if(obj.getWidth() < 3*whitespace){
                return_list.add(obj);
                continue;
            }

            //Split 
            int[] projection = obj.toHorizontalProjection();
            int avg = (int)UtilMath.average(projection);
            ArrayList<SimpleEntry<Integer, Integer>> spikes = new ArrayList<>(); //Entry: x-start, width
            
            boolean in_spike = false;
            for(int x = 0; x < projection.length; x++)
            {
                if(projection[x] > 3*avg){
                    if(in_spike == true)
                    {
                        spikes.get(spikes.size() - 1).setValue(spikes.get(spikes.size() - 1).getValue() + 1);
                    }
                    else{
                        in_spike = true;
                        spikes.add(new SimpleEntry<Integer,Integer>(x, 1));
                    }
                }
                else{
                    in_spike = false;
                }
            }

            if(spikes.size() > 1)
            {
                int lastMiddle = 0;

                for(int i = 0; i < spikes.size()-1; i++){
                    int lows_start = -1;
                    int lows_width = 0;

                    for(int x = spikes.get(i).getKey() + spikes.get(i).getValue()-1; x <= spikes.get(i+1).getKey(); x++){
                        if(projection[x] <= avg){
                            if(lows_start == -1){
                                lows_start = x;
                            }
                            lows_width++;
                        }
                    }

                    int middleOfSpikes = lows_start + (lows_width - 1)/2;
                    Objektausschnitt new_obj = obj.split_at_x_cooordinate(obj.getOffsetXleft() + middleOfSpikes - lastMiddle, false);
                    return_list.add(new_obj);
                    lastMiddle = middleOfSpikes;
                }
            }
            return_list.add(obj);
        }

        return return_list;
    }

    //Tries to detect non-note symbols and remove them from the resulting list.
    private ArrayList<Objektausschnitt> filter_non_notes(ArrayList<Objektausschnitt> objects, int whitespace, int staffheight) {

        ArrayList<Objektausschnitt> return_list = new ArrayList<Objektausschnitt>();

        for(Objektausschnitt obj : objects){
            boolean is_line = obj.getHeight() > (obj.getWidth() * 4);
            boolean is_g_clef = (obj.getHeight() > (5*whitespace + 5*staffheight)) && (obj.getWidth() > 2*whitespace) && (obj.getWidth() < 3*whitespace);
            boolean is_not_head = (obj.getHeight() > whitespace+staffheight) && (obj.getHeight() < 2.5*whitespace + 2.5*staffheight);
            boolean is_very_small = (obj.getHeight() < 0.5 * whitespace) && (obj.getWidth() < 0.5 * whitespace);

            if(!is_line && !is_g_clef && !is_not_head && !is_very_small){
                return_list.add(obj);
            }
        }
        
        return return_list;
    }

}
