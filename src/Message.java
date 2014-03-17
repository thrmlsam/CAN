import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;



public class Message implements Serializable {
	
	public Peer getIntiater() {
		return intiater;
	}
	public void setIntiater(Peer intiater) {
		this.intiater = intiater;
	}
	
	public Point getDestination() {
		return destination;
	}
	public void setDestination(Point destination) {
		this.destination = destination;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7886497181635603806L;
	/**
	 * 
	 */

	public int getCommand() {
		return command;
	}
	public void setCommand(int command) {
		this.command = command;
	}
	public static int JOIN = 1;
	public static int REMOVENEIGHBOR = 2;
	public static int ADJUSTZONE = 3;
	public static int ADDNEIGHBOR = 4;
	
	private Peer intiater;
	private List<InetAddress> path = new ArrayList<InetAddress>(); 
	public List<InetAddress> getPath() {
		return path;
	}
	public void setPath(List<InetAddress> path) {
		this.path = path;
	}
	private Point destination;
	private int command;
	public Message(Peer intiater, List<InetAddress> path1,
			Point destination, int command) {
		super();
		this.intiater = intiater;
		this.path.addAll(path1);
		this.destination = destination;
		this.command = command;
	}
	
	 

}
