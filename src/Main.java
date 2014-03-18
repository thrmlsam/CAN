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
import java.util.Scanner;

public class Main {

	private static InetAddress bootStrap;
	private static Peer peer;

	public static void main(String[] args) {
		
		boolean isJoin = false;
		boolean exit = true;
		Scanner sc = new Scanner(System.in);
		while(exit){
			displayMenu();
			char ch =sc.next().charAt(0);
			
			switch(ch){
			case 'J':
			case'j':
				isJoin = true;
				join();
				break;
			case 'I':
			case 'i':
				{
					if(isJoin){
						insert();
				}
					
				else{
					System.out.println("The Peer had to join first");
				}
				break;
				}
			case 'S':
			case 's':
			{
				if(isJoin){
					search();
			}
				
			else{
				System.out.println("The Peer had to join first");
			}
			break;
			}
			case 'E':
			case 'e':
				System.exit(0);
				break;
			default:
					System.out.println("Please enter a correct option");
					
			}
			
		}

	}

	private static void search() {
		// TODO Auto-generated method stub
		
	}

	private static void insert() {
		// TODO Auto-generated method stub
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the file Name");
		try {
			String fileName = br.readLine();
			Point p = hash(fileName);
			System.out.println("File is to be stored at "+p);
			List<InetAddress> path = new ArrayList<InetAddress>();
			path.add(peer.getAddress());
			InetAddress receiver = peer.findNextClosestNeighbor(p);
			path.add(receiver);
			Message msg = new Message(peer, path, p, Message.INSERT);
			msg.setFile(fileName);
			Socket s1 = new Socket(receiver, Constants.PEERPORT);
			ObjectOutputStream oo = new ObjectOutputStream(
					s1.getOutputStream());
			oo.writeObject(msg);
			ObjectInputStream oi = new ObjectInputStream(
					s1.getInputStream());
			try {
				Message cmd = (Message)oi.readObject();
				System.out.println("Path taken to reach file is");
				for(int i=0;i<cmd.getPath().size();i++){
					System.out.println(" "+cmd.getPath().get(i)+" -->");
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static Point hash(String fileName) {
		// TODO Auto-generated method stub
		double x = 0,y = 0;
		for(int i=0;i<fileName.length();i++){
			if(i%2 == 0){
				x += fileName.charAt(i);
			}
			else{
				y+=fileName.charAt(i);
			}
		}
		x=x%10;
		y= y%10;
		return new Point(x,y);
	}

	private static void displayMenu() {
		// TODO Auto-generated method stub
		System.out.println("1.[J]oin");
		System.out.println("2.[I]nsert File");
		System.out.println("3.[S]earch File");
		System.out.println("4.[E]xit");
	}

	public static void join() {
		boolean bootstrapIP = true;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String peerInfo = "";
		while (bootstrapIP) {
			System.out.println("Please Enter the address of Bootstrap Server");
			try {
				String ip = br.readLine();
				bootStrap = InetAddress.getByName(ip);
				Socket client = new Socket(bootStrap, Constants.BOOTSTARPPORT);
				// PrintWriter out = new PrintWriter(new
				// OutputStreamWriter(client.getOutputStream()));
				// out.write(client.getLocalAddress().toString());
				BufferedReader in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				peerInfo = in.readLine();

				bootstrapIP = false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println("Please enter a proper address");
			}

			if (peerInfo.equalsIgnoreCase("null")) {
				Zone z1 = new Zone(new Point(0, 0), new Point(10, 10));
				try {
					Random r = new Random();
					double randomValue1 = Constants.MINBOUNDARY
							+ (Constants.MAXBOUNDARY - Constants.MAXBOUNDARY)
							* r.nextDouble();
					double randomValue2 = Constants.MINBOUNDARY
							+ (Constants.MAXBOUNDARY - Constants.MAXBOUNDARY)
							* r.nextDouble();
					Point destination = new Point(randomValue1, randomValue2);
					peer = new Peer(destination, z1,
							InetAddress.getLocalHost(), bootStrap);
					System.out.println("Opening Connection");
					peer.openConnection();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				try {
					InetAddress contactPeer = InetAddress.getByName(peerInfo
							.substring(1));

					Random r = new Random();
					double randomValue1 = Constants.MINBOUNDARY
							+ (Constants.MAXBOUNDARY - Constants.MAXBOUNDARY)
							* r.nextDouble();
					double randomValue2 = Constants.MINBOUNDARY
							+ (Constants.MAXBOUNDARY - Constants.MAXBOUNDARY)
							* r.nextDouble();
					Point destination = new Point(randomValue1, randomValue2);
					peer = new Peer(destination,
							InetAddress.getLocalHost(), bootStrap);
					List<InetAddress> path = new ArrayList<InetAddress>();
					path.add(peer.getAddress());
					path.add(contactPeer);
					Message joinMessage = new Message(peer, path, destination,
							1);
					Socket s1 = new Socket(contactPeer, Constants.PEERPORT);
					ObjectOutputStream oo = new ObjectOutputStream(
							s1.getOutputStream());
					System.out.println("writing object");
					oo.writeObject(joinMessage);
					ObjectInputStream oi = new ObjectInputStream(
							s1.getInputStream());
					try {
						Zone z2 = (Zone) oi.readObject();
						peer.setZone(z2);
						peer.setPoint(z2.getMidPoint());
						Map<InetAddress, Zone> neighborList = (Map) oi
								.readObject();
						peer.setNeighbors(neighborList);
						peer.adjustNeighbors(true);

					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					peer.openConnection();
					peer.displayInformation();
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
