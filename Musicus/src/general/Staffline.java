package general;

public class Staffline {

	//The Line is defined by a starting point and an endpoint.
	//Both of these are located at the topmost pixel of the starting line (either at the end or at the start).
	private Point startPoint;
	private Point endPoint;
	
	//Together with the width, even  lines at an angle could be interpolated
	private double width;

	public Staffline(Point startPoint, Point endPoint, double width) {
		super();
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.width = width;
	}

	public Point getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
	}

	public Point getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}
	
	
	
	
}
