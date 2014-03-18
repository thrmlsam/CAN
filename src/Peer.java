import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Peer implements Runnable, Serializable {

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7920785371042912223L;
	/**
	 * 
	 */
	private Zone zone;
	private Point point;
	private InetAddress address;
	private static InetAddress bootStrap;
	private Map<InetAddress,Zone> neighbors;

	private transient ServerSocket server;

	public Peer(Point point,Zone zone, InetAddress address, InetAddress bootstrap) {
		this.point = point;
		this.zone = zone;
		this.setAddress(address);
		Peer.bootStrap = bootstrap;
		this.neighbors = new ConcurrentHashMap<InetAddress,Zone>();
	}

	public Peer(Point point,InetAddress address, InetAddress bootstrap) {
		this.point = point;
		this.setAddress(address);
		Peer.bootStrap = bootstrap;
		this.neighbors = new ConcurrentHashMap<InetAddress,Zone>();
	}

	

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Map<InetAddress, Zone> getNeighbors() {
		return neighbors;
	}

	public void openConnection() {
		try {
			this.server = new ServerSocket(Constants.PEERPORT);
			Thread listenerThread = new Thread(this);
			listenerThread.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {

			System.out.println("Listening");

			while (true) {
				Socket sock = this.server.accept();

				RequestHandler handler = new RequestHandler(sock, this);
				handler.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void displayInformation() {
		System.out.println("Bottom Left " + this.zone.getBottomLeft());
		System.out.println("Top Right " + this.zone.getTopRight());
		System.out.println("Neighbors: ");
		for(InetAddress key : this.neighbors.keySet()){
			System.out.println(key.toString());
		}
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public InetAddress findNextClosestNeighbor(Point destination) {
		
		double min = Double.MAX_VALUE;
		InetAddress nearestNeighbor = this.address;
		
		for(InetAddress key :this.neighbors.keySet()){
			Zone temp = this.neighbors.get(key);
			
			Point midPoint = temp.getMidPoint();
			double distance = findDistance(midPoint,destination);
			if(distance < min){
				min = distance;
				nearestNeighbor = key;
			}
		}
		return nearestNeighbor;
	}

	private double findDistance(Point midPoint, Point destination) {

		double distance = Math.sqrt(Math.pow((midPoint.getX()- destination.getX()), 2)+Math.pow((midPoint.getY()- destination.getY()),2));
		return distance;
	}
	
	private boolean isNeighbors(Zone z2){
		
		Point myTopRight = new Point(this.getZone().getTopRight().getX(),this.getZone().getTopRight().getY());
		Point myBottomLeft = new Point(this.getZone().getBottomLeft().getX(),this.getZone().getBottomLeft().getY());
		Point myTopLeft = new Point(this.getZone().getTopRight().getX(),this.getZone().getBottomLeft().getY());
		Point myBottomRight = new Point(this.getZone().getBottomLeft().getX(),this.getZone().getTopRight().getY());
		
		
		Point z2TopLeft = new Point(z2.getTopRight().getX(),z2.getBottomLeft().getY());
		Point z2BottomRight = new Point(z2.getBottomLeft().getX(),z2.getTopRight().getY());
		Point z2TopRight = new Point(z2.getTopRight().getX(),z2.getTopRight().getY());
		Point z2BottomLeft = new Point(z2.getBottomLeft().getX(),z2.getBottomLeft().getY());
		
		
		/*
		if((z2BottomLeft.getY() == myTopLeft.getY()) && ((myTopLeft.getX() <= z2.getBottomLeft().getX() && z2.getBottomLeft().getX() < myTopRight.getX())||(myTopLeft.getX() < z2BottomRight.getX() && z2BottomRight.getX()<= myTopRight.getX())))
				return true;
		
		else if((z2BottomLeft.getY() == myBottomLeft.getY()) &&((myBottomLeft.getX() <= z2TopLeft.getX() && z2TopLeft.getX() < myBottomRight.getX())||(myBottomLeft.getX() < z2TopRight.getX() && z2TopRight.getX() <= myBottomRight.getX())))
				return true;
		
		else if((z2TopRight.getX() == myTopLeft.getX()) && ((myTopLeft.getY() <= z2TopRight.getY() && z2TopRight.getY() < myBottomLeft.getY())||(myTopLeft.getY() < z2BottomRight.getY() && z2BottomRight.getY() <=myBottomLeft.getY())))
				return true;
		else if((z2TopLeft.getX() == myTopRight.getX()) && ((myTopRight.getY() <= z2TopLeft.getY() && z2TopLeft.getY() < myBottomRight.getY())||(myTopRight.getY() < z2BottomLeft.getY() && z2BottomLeft.getY() <= myBottomRight.getY())))
				return true;
		*/
		
		if(myBottomRight.equals(z2BottomLeft)){
			if(this.zone.getHeight() >= z2.getHeight()){
				if(myTopRight.getY() >= z2TopLeft.getY())
					return true;
			}
			else if(this.zone.getHeight() < z2.getHeight()){
				if(myTopRight.getY() < z2TopLeft.getY())
					return true;
			}
		}
		
		if(myTopRight.equals(z2TopLeft)){
			if(this.zone.getHeight() >= z2.getHeight()){
				if(myBottomRight.getY() <= z2BottomLeft.getY())
					return true;
			}
			else if(this.zone.getHeight() < z2.getHeight()){
				if(myBottomRight.getY() < z2BottomLeft.getY())
					return true;
			}
		}
		
		if(myTopRight.equals(z2BottomRight)){
			if(this.zone.getWidth() >= z2.getWidth()){
				if(myTopLeft.getX() <= z2BottomLeft.getX())
					return true;
			}
			else if(this.zone.getWidth() < z2.getWidth()){
				if(myTopLeft.getX() > z2BottomLeft.getX())
					return true;
			}
		}
		
		if(myTopLeft.equals(z2BottomLeft)){
			if(this.getZone().getWidth() >= z2.getWidth()){
				if(myTopRight.getX() >= z2BottomRight.getX())
					return true;
			}
			else if(this.getZone().getWidth() < z2.getWidth()){
				if(myTopRight.getX() < z2BottomRight.getX())
					return true;
			}
		}
		
		if(myTopLeft.equals(z2TopRight)){
			if(this.zone.getHeight() >= z2.getHeight()){
				if(myBottomLeft.getY() <= z2BottomRight.getY())
					return true;
			}
			else if(this.zone.getHeight() < z2.getHeight()){
				if(myBottomLeft.getY() > z2BottomRight.getY())
					return true;
			}
		}
		
		if(myBottomLeft.equals(z2BottomRight)){
			if(this.zone.getHeight() >= z2.getHeight()){
				if(myTopLeft.getY() >= z2TopRight.getY())
					return true;
			}
			else if(this.zone.getHeight() < z2.getHeight()){
				if(myTopLeft.getY() < z2TopRight.getY())
					return true;
			}
		}
		
		if(myBottomLeft.equals(z2TopLeft)){
			if(this.zone.getWidth() >= z2.getWidth()){
				if(myBottomRight.getX() >= z2TopRight.getX())
					return true;
			}
			else if(this.zone.getWidth() < z2.getWidth()){
				if(myBottomRight.getX() < z2TopRight.getX())
					return true;
			}
		}
		
		if(myBottomRight.equals(z2TopRight)){
			if(this.zone.getWidth() >= z2.getWidth()){
				if(myBottomLeft.getX() <= z2TopLeft.getX())
					return true;
			}
			else if(this.zone.getWidth() < z2.getWidth()){
				if(myBottomLeft.getX() > z2TopLeft.getX())
					return true;
			}
		}
		
		return false;
	}

	public void setNeighbors(Map<InetAddress, Zone> neighbors) {
		this.neighbors = neighbors;
	}

	public void adjustNeighbors(boolean removeMessage) {
		// TODO Auto-generated method stub
		System.out.println("Adjusting neighbors ");
		for(InetAddress key :this.neighbors.keySet()){
			System.out.println("checking "+key.toString());
			if(isNeighbors(this.neighbors.get(key)))
			{
				System.out.println("is neighbor true");
				Zone temp = this.neighbors.get(key);
				Point destination = temp.getMidPoint();
				List<InetAddress> path = new ArrayList<InetAddress>();
				path.add(this.getAddress());
				path.add(key);
				Message msg = new Message(this,path,destination,Message.ADJUSTZONE);
				
				
				try {
					if(!removeMessage){
					Socket s1 = new Socket(key,Constants.PEERPORT);
					ObjectOutputStream oo =  new ObjectOutputStream(s1.getOutputStream());
					oo.writeObject(msg);
					}
					else{
						msg.setCommand(Message.ADDNEIGHBOR);
						Socket s1 = new Socket(key,Constants.PEERPORT);
						ObjectOutputStream oo =  new ObjectOutputStream(s1.getOutputStream());
						oo.writeObject(msg);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else
			{
				System.out.println("not a neighbor");
				Zone temp = this.neighbors.get(key);
				Point destination = temp.getMidPoint();
				List<InetAddress> path = new ArrayList<InetAddress>();
				path.add(this.getAddress());
				path.add(key);
				Message removeMsg = new Message(this,path,destination,Message.REMOVENEIGHBOR);
				try {
					if(!removeMessage){
					Socket s1 = new Socket(key,Constants.PEERPORT);
					ObjectOutputStream oo =  new ObjectOutputStream(s1.getOutputStream());
					oo.writeObject(removeMsg);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				this.neighbors.remove(key);
			}
		}
	}

	public void remove(InetAddress key) {
		// TODO Auto-generated method stub
		System.out.println("removing "+key.toString());
		this.neighbors.remove(key);
		
	}

	public void addNeighbour(InetAddress key, Zone zone2) {
		// TODO Auto-generated method stub
		this.neighbors.put(key, zone2);
	}
	
	
}
