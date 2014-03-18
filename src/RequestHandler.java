import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;


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
			case 3:
				adjusjZone(cmd);
				break;
			case 4:
				this.peer.addNeighbour(cmd.getIntiater().getAddress(), cmd.getIntiater().getZone());
				break;
			case 5:
				insert(cmd);
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

	private void insert(Message cmd) {
		// TODO Auto-generated method stub
Point p1 = cmd.getDestination();
		
		Zone z1 = this.peer.getZone();
		if(z1.checkPoint(p1)) {
			this.peer.addFile(cmd.getFile());
			try {
				this.out.writeObject(cmd);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
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

	private void search(Message cmd) {
		// TODO Auto-generated method stub
		Point p1 = cmd.getDestination();
		
		Zone z1 = this.peer.getZone();
		if(z1.checkPoint(p1)) {
			if(this.peer.checkFile(cmd.getFile()))
			{
				
			}
		}
		
	}

	private void adjusjZone(Message cmd) {
		// TODO Auto-generated method stub
		InetAddress key = cmd.getIntiater().getAddress();
		this.peer.remove(key);
		this.peer.addNeighbour(key, cmd.getIntiater().getZone());
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
			double temp = (z1.getWidth())/2;
			Zone z2 = new Zone(new Point(z1.getBottomLeft().getX()+temp,z1.getBottomLeft().getY()),new Point(z1.getTopRight().getX(),z1.getTopRight().getY()));
			z1.setTopRight(new Point(z1.getBottomLeft().getX()+temp,z1.getTopRight().getY()));
			this.peer.addNeighbour(cmd.getIntiater().getAddress(), z2);
			try {
				System.out.println("sending back message");
				this.out.writeObject(z2);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		else{
			
			double temp = (z1.getHeight())/2;
			Zone z2 = new Zone(new Point(z1.getBottomLeft().getX(),temp+z1.getBottomLeft().getY()),new Point(z1.getTopRight().getX(),z1.getTopRight().getY()));
			z1.setTopRight(new Point(z1.getTopRight().getX(),temp+z1.getBottomLeft().getY()));
			this.peer.addNeighbour(cmd.getIntiater().getAddress(), z2);
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
			Map<InetAddress,Zone> nbh = this.peer.getNeighbors();
			nbh.put(this.peer.getAddress(), this.peer.getZone());
			this.out.writeObject(nbh);
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
