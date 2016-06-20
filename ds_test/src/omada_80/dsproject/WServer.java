package omada_80.dsproject;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

/**
 * WServer represents a server that is established in a node (for example a PC) and dynamically
 * is given by the user, a port for the server socket and the type of workers it will spawn.
 * So that means that 'W' in its name stands for Workers, and is responsible for creating
 * new Threads running ReduceWorkers/MapWorkers depending on what the user chose.
 */
public class WServer {
	
	public ServerSocket serverSocket;  // The server-socket given by the user that runs WServer

	
	/* Main method for running */
	public static void main(String [] args){
		new WServer().openServer();
	}
	
	
	/**
	 * Dynamically asks the user that runs the server to choose the type of workers
	 * plus a server socket in order to establish the server.
	 */
	private void openServer() {
		serverSocket = null;
		
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		
		/* Choosing the kind of workers the servers will spawn (Map or Reduce) */
		System.out.print("Type 'M' or 'R' (Map-workers and Reduce-workers server) for the correct type : ");
		char t = Character.toUpperCase(reader.next().charAt(0));
		while (t != 'M' && t != 'R') {
			System.out.print("Type again one of these in order to proceed 'M' or 'R' : ");
			t = Character.toUpperCase(reader.next().charAt(0));
			System.out.println();
		}
		
		/* Getting the server socket */
		System.out.print("Now type a free server socket in order to establish the server : ");
		int sSocket = reader.nextInt();
		System.out.println();
		
		
		System.out.printf("Establishing %s-workers server with server-socket number %d!\n", 
				(t == 'M' ? "map" : "reduce"), sSocket);
		System.out.println("Ready! You can now connect . . .\n");
		
		try {
			
			serverSocket = new ServerSocket(sSocket);
			
			if (t == 'M') {
				while (true) {
					Thread mapWorker = new Thread(new MapWorker(serverSocket.accept()));
					mapWorker.start();
				}
			}else{
				while (true) {
					Thread reduceWorker = new Thread(new ReduceWorker(serverSocket.accept()));
					reduceWorker.start();
				}
			}
			
		}catch(IOException ioe) {
			System.err.println("An error on I/O has occured while trying to open server-socket.");
			ioe.printStackTrace();
		}finally {
			
			try {
				
				/* Closing the server-socket,scanner */
				if (serverSocket != null) {
					serverSocket.close();
					reader.close();
				}
				
			}catch(IOException ioe) {
				System.err.println("An error on I/O has occured while trying to close.");
				ioe.printStackTrace();
			}
			
		}	
	}

}
