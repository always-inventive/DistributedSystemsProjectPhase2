package omada_80.dsproject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MapWorker along with ReduceWorker are the main runnable classes. 
 * Actually, MapWorker created by the server holding the socket-connection with Master/User/Client
 * reads the coordinates-limits-range,connects to the database,fills a list with checkins,
 * uses the map() process and finally sends its results back.
 * Responsible for closing streams and socket still remains the Master/User/Client.
 */
public class MapWorker implements Runnable {
	
	private Socket connection;  		  // Socket-connection in order to read and write back to the Master/User/Client
	private Coordinates coords;  		  // Coordinates used as limits-range to connect to the database and create the query
	private ArrayList<Checkin> checkins;  // A list filled with checkins that MapWorker instance will get from the database
	
	
	/**
	 * Constructor of MapWorker class for having initialized 
	 * the socket-connection,coordinates and checkins list.
	 * @param connection The socket that is shared with the Master/User/Client
	 */
	public MapWorker(Socket connection) {
		this.connection = connection;
		this.coords = new Coordinates();
		this.checkins = new ArrayList<Checkin>();
	}
	

	/**
	 * Implementing the Runnable interface.
	 * MapWorker's class main method for running concurrently.
	 */
	public void run() {
		readCoordinates();
		connectToDatabase();
		sendMap();
	}
	
	
	/**
	 * Opens input stream at this end of the connection and reads the 
	 * coordinates-range from the Master in order to connect to the database.
	 */
	private void readCoordinates() {
		ObjectInputStream in = null;
		
		try {
			
			in = new ObjectInputStream(connection.getInputStream());

			coords.setMinLatitude((double) in.readObject());
			coords.setMinLongitude((double) in.readObject());
			coords.setMaxLatitude((double) in.readObject());
			coords.setMaxLongitude((double) in.readObject());
			coords.setMinDT((String) in.readObject());
			coords.setMaxDT((String) in.readObject());
			
		}catch(ClassNotFoundException cnfe) {
			System.err.println("Data received in unknown format.");
			cnfe.printStackTrace();
		}catch(IOException ioe) {
			System.err.println("An error on input-stream (opening/reading) has occured.");
			ioe.printStackTrace();
		}
	}
	
	
	/**
	 * Method used for connecting to the sql database after getting the coordinates,
	 * in order to create a statement/query and add to the checkins-list instances 
	 * that meets the criteria(coordinates).
	 */
	private void connectToDatabase() {
		/* Useful strings created in order to connect to the database  */
		String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	    String DB_URL = "jdbc:mysql://83.212.117.76:3306/ds_systems_2016?user=omada80&password=omada80db";
	    
		Connection conn = null;
		Statement stmt = null;

		try {
			
			/* Getting the connection and creating a statement for the DB */
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL);
			stmt = conn.createStatement();
			 
			/* Creating,executing the query plus getting the result */
			String sql = String.format(Locale.ENGLISH,"Select POI,POI_name,latitude,longitude,photos"
					+ " from ds_systems_2016.checkins "
					+ "where (latitude between %f and %f) "
					+ "and (longitude between %f and %f) "
					+ "and (time between '%s' and '%s');"
					,coords.getMinLatitude(),coords.getMaxLatitude()
					,coords.getMinLongitude(),coords.getMaxLongitude()
					,coords.getMinDT(),coords.getMaxDT());	
			
			ResultSet rs = stmt.executeQuery(sql);
			
			/* Filling the checkins-list */
			while (rs.next()) {
				String POI = rs.getString("POI");
				String POIName = rs.getString("POI_name");
				double latitude = rs.getDouble("latitude");
				double longitude = rs.getDouble("longitude");
				String url = rs.getString("photos");
				checkins.add(new Checkin(new LocationPOI(POI,POIName,latitude,longitude), 
						url.equals("Not exists") ? null : url));
			}
			
			stmt.close();
			conn.close();
			
		}catch(ClassNotFoundException cnfe) {
			System.err.println("Unknown format data error has occured.");
			cnfe.printStackTrace();
		}catch(SQLException sqle) {
			System.err.println("An error at SQL process has occured trying to connect to DB.");
			sqle.printStackTrace();
		}finally { 
			
			/* Closing the statement,connection */
			try {
				
				if (stmt != null) stmt.close();
				if (conn != null) conn.close();
				
		    }catch(SQLException sqle2) {
		    	System.err.println("An error at SQL process has occured trying to close.");
		    	sqle2.printStackTrace();
		    }

		}
	}
	
	
	
	/**
	 * Opens the output stream at this end of the connection and writes back
	 * to the Master/User/Client the result of map() process.
	 */
	private void sendMap() {
		ObjectOutputStream out = null;
		
		try {
			
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			
			out.writeObject((Map<LocationPOI, Long>) map());
			out.flush();
			
		}catch (IOException ioe) {
			System.err.println("An error on output stream (writing) has occured.");
			ioe.printStackTrace();
		}
	}
	
	
	/**
	 * The main method of a MapWorker instance.
	 * Processes checkins from the list, produces another pair of Key-Value 
	 * (POI,Number of URL-Photos) and creates a map with them. 
	 * @return map Map filled with pairs of POI and number of URL-Photos
	 */
	private Map<LocationPOI, Long> map() {
		
		Stream<Checkin> stream1 = checkins.stream().parallel().distinct().filter(p -> p.getURL() != null);
		Map<String, Long> map1 = stream1.map(p -> p.getLocationPOI().getPOI())
				.collect(Collectors.groupingByConcurrent(Function.identity(), Collectors.counting()));
		
		
		HashMap<LocationPOI, Long> hsMap = new HashMap<LocationPOI, Long>();
		
		for (Entry<String, Long> entry : map1.entrySet()) {
			
			for (Checkin ch : checkins) {
				if (entry.getKey().equals(ch.getLocationPOI().getPOI())) {
					hsMap.put(ch.getLocationPOI(), entry.getValue());
					break;
				}
			}
			
		}
		
		return hsMap;
		

	}

}
