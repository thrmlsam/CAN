import java.io.Serializable;



public class Point implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4325258336308097875L;
	/**
	 * 
	 */
	private double x;
	private double y;

    public Point(double x,double y)
    {
        this.x = x;
        this.y = y;
    }

    public double[] getCoordinates()
    {
    	double[] coordinate = {this.x,this.y};
        return coordinate;
    }

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
    
	@Override
   public boolean equals(Object other)
   {
		Point p = (Point) other;
	   if(this.x == p.x && this.y == p.y)
		   return true;
	   else
		   return false;
   }
   
	@Override
    public String toString()
    {
		String s ="(";
		s = s+this.x+" , " +this.y+")";
		
		return s;
    }

}
