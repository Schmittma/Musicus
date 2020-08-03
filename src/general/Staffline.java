package general;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class gua
 * @author Marius
 *
 */
public class Staffline {

    //The Line is defined by a starting point and an endpoint.
    //Both of these are located at the topmost pixel of the starting line (either at the end or at the start).
    private ArrayList<Point> pointsOnStaffline;

    //Together with the width, even  lines at an angle could be interpolated
    private double width;

    public Staffline(ArrayList<Point> points, double width) {
        this();
        pointsOnStaffline.addAll(points);
        orderStaffline();
        this.width = width;
    }

    public Staffline() {
        super();
        pointsOnStaffline = new ArrayList<>();
        this.width = 0;
    }

    public void addPoint(Point newPoint) {
        pointsOnStaffline.add(newPoint);
        orderStaffline();
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * This method has to guarantee, that the pixels inside the List are sorted by the x coordinate and then the y coordinate in ascending order.
     * Meaning that the most upper left pixel of the staffline will be at index 0 followed by pixels underneath it followed by p
     * @return
     */
    public ArrayList<Point> getPointsOnStaffline() {
        return pointsOnStaffline;
    }

    public void orderStaffline() {
        pointsOnStaffline.sort(new Comparator<Point>() {

            @Override
            public int compare(Point o1, Point o2) {
                if(o1.getX() == o2.getX()) {
                    return o1.getY() - o2.getY();
                }

                return o1.getX() - o2.getX();
            }
        });
    }

    public ArrayList<Point> getPointsOnXCoordinate(int xCoord) {
        ArrayList<Point> ret = new ArrayList<>();
        for(Point point : this.pointsOnStaffline) {
            if(point.getX() == xCoord) {
                ret.add(new Point(point.getX(), point.getY()));
            }
            else if(point.getX() > xCoord) {
                break;
            }
        }

        return ret;
    }


}
