import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class BootStrap implements Runnable {

	private static InetAddress node = null;
	Socket client;
	public BootStrap(Socket client) {
	      this.client = client;
	   }

	   public static void main(String args[]) 
	   {
	      ServerSocket bootstrap = null;
		try {
			bootstrap = new ServerSocket(Constants.BOOTSTARPPORT);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	      System.out.println("Listening");
	      while (true) {
	         Socket sock;
			try {
				sock = bootstrap.accept();
				System.out.println("Connected");
		         new Thread(new BootStrap(sock)).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	         
	      }
	   }
	   public void run() {
	      try {
	    	  System.out.println("in run");
	    	  //BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
		        // System.out.println(br.readLine());
	         PrintWriter pw = new PrintWriter(client.getOutputStream(),true);
	         System.out.println("in run1");
	         if(node == null){
	        	 System.out.println("First Node");
	        	 pw.write("null");
	        	 node = client.getInetAddress();
	        	 
	         }
	         else{
	        	 pw.write(node.toString());
	         }
	         pw.close();
	         client.close();
	      }
	      catch (IOException e) {
	         System.out.println(e);
	      }
	   }

	public static InetAddress getNode() {
		return node;
	}

	public static void setNode(InetAddress node) {
		BootStrap.node = node;
	}
}
