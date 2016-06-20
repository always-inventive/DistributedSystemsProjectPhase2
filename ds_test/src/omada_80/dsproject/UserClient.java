package omada_80.dsproject;

import java.util.Scanner;

/**
 * Dummy client that asks for coordinates - spatial,time limits in order to
 * pass them to a Master instance and let it run to start the map-reduce process.
 */
public class UserClient {
	
	/* */
	public static void main(String [] args){
		new UserClient().startClient();
	}
	
	
	public void startClient() {
		Coordinates coordinates = new Coordinates();
		
		@SuppressWarnings("resource")
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		 
		/*
		System.out.println("-- Give all the right coordinates please.\n");
		System.out.print("Max longitude : ");
		coordinates.setMaxLongitude(Double.parseDouble(reader.nextLine()));
		System.out.println();
		
		System.out.print("Min longitude : ");
		coordinates.setMinLongitude(Double.parseDouble(reader.nextLine()));
		System.out.println();
		
		System.out.print("Max latitude : ");
		coordinates.setMaxLatitude(Double.parseDouble(reader.nextLine()));
		System.out.println();
		
		System.out.print("Min latitude : ");
		coordinates.setMinLatitude(Double.parseDouble(reader.nextLine()));
		System.out.println("\n");
		*/
		
		coordinates.setMaxLongitude(-65.5);
		coordinates.setMinLongitude(-85.5);
		coordinates.setMaxLatitude(50.5);
		coordinates.setMinLatitude(39.50);
		coordinates.setMinDT("2012-04-03 18:02:24");
		coordinates.setMaxDT("2012-08-16 03:00:00");
		
		System.out.println("-- Choose up to how many map server nodes you want to connect.\n");
		System.out.print("Number of map-workers server(s) : ");
		int n = reader.nextInt();
		System.out.println();


		String[][] mapConnections = new String[n][2];

		System.out.println("-- Give their IP Addresses and ports correctly.\n");
		for (int i = 1; i <= n; i++) { 
			System.out.print(i + ". IP Address : ");
			mapConnections[i-1][0] = reader.next();
			System.out.print(i + ". Port : ");
			mapConnections[i-1][1] = Integer.toString(reader.nextInt());
			System.out.println();
		}
		
		String[][] reduceConnection = new String[1][2];
		
		System.out.println("-- Give now IP Address and port for reduce worker.\n");
		System.out.print("IP Address : ");
		reduceConnection[0][0] = reader.next();
		System.out.print("Port : ");
		reduceConnection[0][1] = Integer.toString(reader.nextInt());
		System.out.println("\n");
		
		Master mrpMaster = new Master(mapConnections,reduceConnection,coordinates);
		mrpMaster.startProcess();
	}
	
}
