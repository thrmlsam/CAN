import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

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

	private ServerSocket server;
	private Set<RequestHandler> handlers = new HashSet<RequestHandler>();

	public Peer(Point point,Zone zone, InetAddress address, InetAddress bootstrap) {
		this.point = point;
		this.zone = zone;
		this.setAddress(address);
		Peer.bootStrap = bootstrap;
		this.neighbors = new Hashtable<InetAddress,Zone>();
	}

	public Peer(Point point,InetAddress address, InetAddress bootstrap) {
		this.point = point;
		this.setAddress(address);
		Peer.bootStrap = bootstrap;
		this.neighbors = new Hashtable<InetAddress,Zone>();
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
				this.handlers.add(handler);
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
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}
}
