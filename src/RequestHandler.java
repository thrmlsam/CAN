import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;


public class RequestHandler extends Thread{
	
	private Socket connection;
	private Peer peer;
	
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	public RequestHandler(Socket connection,Peer peer){
		
		this.connection = connection;
		this.peer = peer;
		
		try {
			this.out = new ObjectOutputStream(this.connection.getOutputStream());
			this.in = new ObjectInputStream(this.connection.getInputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		System.out.println("REquest handler started");
		try {
			Message cmd = (Message)this.in.readObject();
			switch(cmd.getCommand()){
			case 1:
				joinPeer(cmd);
				break;
			case 2:
				removeNeighbor(cmd);
				break;
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private void removeNeighbor(Message cmd) {
		// TODO Auto-generated method stub
		InetAddress key = cmd.getIntiater().getAddress();
		this.peer.remove(key);
		
	}

	private void joinPeer(Message cmd) {
		// TODO Auto-generated method stub
		Point p1 = cmd.getDestination();
		
		Zone z1 = this.peer.getZone();
		if(z1.checkPoint(p1)) {
			System.out.println("Point is in the zone.Spliting "+this.peer.getAddress().toString());
		if(z1.isSquare())
		{
			System.out.println("Zone is a square");
			double temp = (z1.getTopRight().getX()-z1.getBottomLeft().getX())/2;
			Zone z2 = new Zone(new Point(temp,z1.getBottomLeft().getY()),new Point(temp,z1.getTopRight().getY()));
			z1.setTopRight(new Point(temp,z1.getTopRight().getY()));
			this.peer.getNeighbors().put(cmd.getIntiater().getAddress(), z2);
			try {
				System.out.println("sending back message");
				this.out.writeObject(z2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		else{
			
			double temp = (z1.getTopRight().getY() - z1.getBottomLeft().getY())/2;
			Zone z2 = new Zone(new Point(z1.getBottomLeft().getX(),temp),new Point(z1.getTopRight().getX(),temp));
			z1.setTopRight(new Point(z1.getTopRight().getX(),temp));
			this.peer.getNeighbors().put(cmd.getIntiater().getAddress(), z2);
			try {
				this.out.writeObject(z2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		this.peer.setPoint(this.peer.getZone().getMidPoint());
		this.peer.displayInformation();
		try {
			this.out.writeObject(this.peer.getNeighbors());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.peer.adjustNeighbors(false);
		}
		else
		{
			System.out.println("Not in "+this.peer.getAddress().toString()+" Zone. Forwarding connection");
			InetAddress nearestNeighbor = this.peer.findNextClosestNeighbor(cmd.getDestination());
			List<InetAddress> temp = cmd.getPath();
			temp.add(nearestNeighbor);
			cmd.setPath(temp);
			
			try {
				System.out.println("Connection forwarded to "+nearestNeighbor.toString());
				Socket client = new Socket(nearestNeighbor,Constants.PEERPORT);
				ObjectOutputStream oo =  new ObjectOutputStream(client.getOutputStream());
				System.out.println("writing object");
				oo.writeObject(cmd);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}

}
