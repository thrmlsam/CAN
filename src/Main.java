import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Main {
	
	private static InetAddress bootStrap;
	
	
	public static void main(String[] args){
		
		boolean bootstrapIP = true;
		
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String peerInfo = "";
		while(bootstrapIP){
			System.out.println("Please Enter the address of Bootstrap Server");
			try {
				String ip = br.readLine();
				bootStrap = InetAddress.getByName(ip);
				Socket client = new Socket(bootStrap, Constants.BOOTSTARPPORT);
				//PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
				//out.write(client.getLocalAddress().toString());
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				peerInfo = in.readLine();
				
				bootstrapIP = false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("Please enter a proper address");
			}
			
			if(peerInfo.equalsIgnoreCase("null")){
				Zone z1 = new Zone(new Point(0,0),new Point(10,10));
				try {
					Random r = new Random();
					double randomValue1 = Constants.MINBOUNDARY +(Constants.MAXBOUNDARY-Constants.MAXBOUNDARY)* r.nextDouble();
					double randomValue2 = Constants.MINBOUNDARY +(Constants.MAXBOUNDARY-Constants.MAXBOUNDARY)* r.nextDouble();
					Point destination = new Point(randomValue1, randomValue2);
					Peer p1 = new Peer(destination,z1,InetAddress.getLocalHost(),bootStrap);
					System.out.println("Opening Connection");
					p1.openConnection();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else{
				try {
					InetAddress contactPeer = InetAddress.getByName(peerInfo.substring(1));
					
					Random r = new Random();
					double randomValue1 = Constants.MINBOUNDARY +(Constants.MAXBOUNDARY-Constants.MAXBOUNDARY)* r.nextDouble();
					double randomValue2 = Constants.MINBOUNDARY +(Constants.MAXBOUNDARY-Constants.MAXBOUNDARY)* r.nextDouble();
					Point destination = new Point(randomValue1, randomValue2);
					Peer temp = new Peer(destination,InetAddress.getLocalHost(),bootStrap);
					List<InetAddress> path = new ArrayList<InetAddress>();
					path.add(temp.getAddress());
					path.add(contactPeer);
					Message joinMessage = new Message(temp, path, destination, 1);
					Socket s1 = new Socket(contactPeer,Constants.PEERPORT);
					ObjectOutputStream oo =  new ObjectOutputStream(s1.getOutputStream());
					System.out.println("writing object");
					oo.writeObject(joinMessage);
					ObjectInputStream oi = new ObjectInputStream(s1.getInputStream());
					try {
						Zone z2 = (Zone)oi.readObject();
						temp.setZone(z2);
						temp.setPoint(z2.getMidPoint());
						Map<InetAddress,Zone> neighborList = (Map)oi.readObject();
						temp.setNeighbors(neighborList);
						temp.adjustNeighbors(true);
						temp.displayInformation();
						
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					temp.openConnection();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}
		
		
		
	}

}
