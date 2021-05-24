package main.java.general;

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

    //Return the 4 corner points of the bounding box
    //1: Upper left
    //2: Upper right
    //3: Lower left
    //4: Lower right
    public Point[] getBoundingBox()
    {
        Point[] corners = new Point[4];

        corners[0] = new Point(getOffsetXleft(), getOffsetYup());
        corners[1] = new Point(getOffsetXright(), getOffsetYup());
        corners[2] = new Point(getOffsetXleft(), getOffsetYdown());
        corners[3] = new Point(getOffsetXright(), getOffsetYdown());
        return corners;
    }

    public int getWidth(){
        return offsetXright - offsetXleft + 1; 
    }

    public int getHeight(){
        return offsetYdown - offsetYup + 1; 
    }

    public int[] toHorizontalProjection(){
        int[] projection = new int[this.getWidth()];

        for(Point p : this.coordinates){
            projection[p.getX() - this.getOffsetXleft()]++;
        }

        return projection;
    }

    // Splits this object at "split_x" and returns the resulting other 
    // half of the Object as a new Objektausschnitt   
    //"keep_left" determines, whether this object contains the left half of the split or the right one
    // The points at x=split_x will allways be in the left half. 
    public Objektausschnitt split_at_x_cooordinate(int split_x, boolean keep_left)
    {
        Objektausschnitt new_obj = new Objektausschnitt();
        
        for(int x = 0; x < coordinates.size(); x++){
            if(keep_left)
            {
                if(coordinates.get(x).getX() > split_x){
                    new_obj.addCoordinate(new Point(coordinates.get(x).getX(), coordinates.get(x).getY()));
                    this.coordinates.remove(x);
                    x--;
                }
            }
            else{
                if(coordinates.get(x).getX() <= split_x){
                    new_obj.addCoordinate(new Point(coordinates.get(x).getX(), coordinates.get(x).getY()));
                    this.coordinates.remove(x);
                    x--;
                }
            }
           
        }

        updateBoundaries();
        return new_obj;
    }
}
