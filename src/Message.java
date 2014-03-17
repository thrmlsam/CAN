import java.io.Serializable;



public class Message implements Serializable {
	
	public Peer getIntiater() {
		return intiater;
	}
	public void setIntiater(Peer intiater) {
		this.intiater = intiater;
	}
	public Peer getSender() {
		return sender;
	}
	public void setSender(Peer sender) {
		this.sender = sender;
	}
	public Peer getReceiver() {
		return receiver;
	}
	public void setReceiver(Peer receiver) {
		this.receiver = receiver;
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
	
	private Peer intiater;
	private Peer sender;
	private Peer receiver;
	private Point destination;
	private int command;
	public Message(Peer intiater, Peer sender, Peer receiver,
			Point destination, int command) {
		super();
		this.intiater = intiater;
		this.sender = sender;
		this.receiver = receiver;
		this.destination = destination;
		this.command = command;
	}
	
	 

}
