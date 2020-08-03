package general;

public class MusicalObject {

    private ClassificationE type;

    // This point can be used for tracking in which line the object is located.
    private Point significantPoint;

    public MusicalObject(ClassificationE type, Point significantPoint) {
        super();
        this.type = type;
        this.significantPoint = significantPoint;
    }

    public ClassificationE getType() {
        return type;
    }

    public void setType(ClassificationE type) {
        this.type = type;
    }

    public Point getSignificantPoint() {
        return significantPoint;
    }

    public void setSignificantPoint(Point significantPoint) {
        this.significantPoint = significantPoint;
    }

}
