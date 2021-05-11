package main.java.stafflineremoval;

import main.java.general.Point;
import main.java.general.Staffline;
import main.java.interfaces.StafflineRemoval;
import main.java.utils.Util;

import java.util.ArrayList;

//This class implements the Linetracking Chord algorithm of "A Comparative Study of Staff Removal Algorithms" Page 3
public class LinetrackingStafflineRemoval implements StafflineRemoval {

    private int minimumAngle;
    private double lengthMultiplier;
    private int resolution;

    /**
     * @param minimumAngle     The minimum angle for a chord to be considered a distinct peak
     * @param lengthMultiplier this value is multiplied with the line width and determines the threshold for the chordlength
     * @param resolution       this value determines the resolution of the angles at which the chords are calculated
     */
    public LinetrackingStafflineRemoval(int minimumAngle, double lengthMultiplier, int resolution) {
        super();
        this.minimumAngle = minimumAngle;
        this.lengthMultiplier = lengthMultiplier;
        this.resolution = Math.min(resolution, 90);
    }

    @Override
    public boolean[][] removeStafflines(boolean[][] staffImage, ArrayList<Staffline> stafflines) {

        boolean[][] copy = Util.copyArray(staffImage);

        boolean[][] stafflineLookup = createStafflineLookupTable(stafflines, staffImage.length, staffImage[0].length);

        //For every Staffline
        for (Staffline line : stafflines) {
            //Over the whole line length
            for (int x = 0; x <= staffImage.length; x++) {

                ArrayList<Point> points = line.getPointsOnXCoordinate(x);
                if (points.size() <= 0) {
                    continue;
                }

                int y = points
                    .get((points.size() - 1) / 2)
                    .getY();

                double angle = 0; //TODO: Calculate angle of current line segment
                boolean remove = true;

                for (int theta = (int) (Math.round(angle)) - 90; theta <= angle + 90; theta += resolution) {

                    int length = chordlength(x, y, staffImage, theta, stafflineLookup);

                    if (Math.abs(theta) > minimumAngle && length > lengthMultiplier * line.getWidth()) {
                        remove = false;
                        break;
                    }
                }

                if (remove) {
                    for (Point point : points) {
                        copy[point.getX()][point.getY()] = false;
                    }
                }

            }

        }
        return copy;
    }

    public int chordlength(int x, int y, boolean[][] image, int angle, boolean[][] stafflineLookup) {

        double rad = angle * Math.PI / 180;

        double gradHyp = Math.tan(rad);
        double gradX = Math.cos(rad) * gradHyp;
        double gradY = Math.sin(rad) * gradHyp;
        double max = Math.max(Math.abs(gradX), Math.abs(gradY));

        //Normalize to 0-1
        gradX /= max;
        gradY /= max;

        int countPos = 0;
        int tempPosi = 0;

        while (true) {
            int nextX = (int) ((x + (tempPosi + 1) * gradX) + 0.5);
            int nextY = (int) ((y + (tempPosi + 1) * gradY) + 0.5);

            if (nextX < 0 || nextX >= image.length || nextY < 0 || nextY >= image[nextX].length || image[nextX][nextY] == false) {
                break;
            }

            //Experimental: Only count those pixels, that are not on the detected staffline
            if (!stafflineLookup[nextX][nextY]) {
                countPos++;
            }

            tempPosi++;

        }

        int countNeg = 0;
        tempPosi = 0;
        while (true) {
            //Check bounds
            int nextX = (int) ((x - (tempPosi + 1) * gradX) + 0.5);
            int nextY = (int) ((y - (tempPosi + 1) * gradY) + 0.5);

            if (nextX < 0 || nextX >= image.length || nextY < 0 || nextY >= image[nextX].length || image[nextX][nextY] == false) {
                break;
            }

            if (!stafflineLookup[nextX][nextY]) {
                countNeg++;
            }

            tempPosi++;
        }

        return countPos + countNeg;
    }

    private boolean[][] createStafflineLookupTable(ArrayList<Staffline> lines, int width, int height) {
        boolean[][] ret = new boolean[width][height];

        for (Staffline line : lines) {
            ArrayList<Point> points = line.getPointsOnStaffline();

            for (Point p : points) {
                ret[p.getX()][p.getY()] = true;
            }

        }
        return ret;
    }

}
