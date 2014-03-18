import java.io.Serializable;

/**
 * class to represent the Cartesian space.
 * @author thirumalaisamy
 *
 */
public class Zone implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8601524470570407342L;
	private Point bottomLeft;
	private Point topRight;

	public Zone(Point bottomLeft, Point topRight) {

		this.bottomLeft = bottomLeft;
		this.topRight = topRight;
	}

	public Point getBottomLeft() {
		return bottomLeft;
	}

	public void setBottomLeft(Point bottomLeft) {
		this.bottomLeft = bottomLeft;
	}

	public Point getTopRight() {
		return topRight;
	}

	public void setTopRight(Point topRight) {
		this.topRight = topRight;
	}

	/**
	 * 
	 * @return width of the zone
	 */
	public double getWidth() {

		return Math.abs(this.bottomLeft.getX() - this.topRight.getX());
	}

	/**
	 * @return height of the zone
	 */
	public double getHeight() {
		return Math.abs(this.bottomLeft.getY() - this.topRight.getY());
	}

	/**
	 * Checks whether the given point is present in the zone
	 * @param point point to check
	 * @return boolean
	 * 		
	 */
	public boolean checkPoint(Point point) {

		if (point.getX() >= this.bottomLeft.getX()
				&& point.getY() >= this.bottomLeft.getY())
			if (point.getX() <= this.topRight.getX()
					&& point.getY() <= this.topRight.getY())
				return true;
		return false;
	}

	/**
	 * checks whether the zone is a square
	 * @return boolean
	 */
	public boolean isSquare() {

		if (this.getWidth() == this.getHeight())
			return true;
		return false;
	}

	/**
	 * Returns the midpoint of the zone
	 * @return
	 */
	public Point getMidPoint() {

		return new Point(
				(this.topRight.getX() - this.getBottomLeft().getX()) / 2, (this
						.getTopRight().getY() - this.bottomLeft.getY()) / 2);
	}

}
