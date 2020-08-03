package general;

import java.util.ArrayList;

public class Objektausschnitt {

    private ArrayList<Point> coordinates;

    //These offsets determine the the offset from the zero point to the edges of the
    //Screenshot. These values are determined by the given coordinates (leftmost coordinate determines offsetXleft, etc.)
    private int offsetXleft;
    private int offsetXright;
    private int offsetYup;
    private int offsetYdown;

    public Objektausschnitt() {
        this.coordinates = new ArrayList<>();
    }

    public ArrayList<Point> getCoordinates() {
        return coordinates;
    }

    public void addCoordinates(ArrayList<Point> coordinates) {
        this.coordinates.addAll(coordinates);
        updateBoundaries();
    }

    public void addCoordinate(Point coordinate) {
        this.coordinates.add(coordinate);
        updateBoundaries();
    }

    public void updateBoundaries() {
        int tempLeft = coordinates
            .get(0)
            .getX();
        int tempRight = coordinates
            .get(0)
            .getX();
        int tempUp = coordinates
            .get(0)
            .getY();
        int tempDown = coordinates
            .get(0)
            .getY();

        for (Point point : coordinates) {
            tempLeft = Math.min(tempLeft, point.getX());
            tempRight = Math.max(tempRight, point.getX());
            tempUp = Math.min(tempUp, point.getY());
            tempDown = Math.max(tempDown, point.getY());
        }
        offsetXleft = tempLeft;
        offsetXright = tempRight;
        offsetYup = tempUp;
        offsetYdown = tempDown;
        //TODO: determine the offsets from coordinates
    }

    public void checkForDoubles() {
        //TODO: Check the coordinates for double entrances (exactly same coordinates)
    }

    public int getOffsetXleft() {
        return offsetXleft;
    }

    public int getOffsetXright() {
        return offsetXright;
    }

    public int getOffsetYup() {
        return offsetYup;
    }

    public int getOffsetYdown() {
        return offsetYdown;
    }

}
