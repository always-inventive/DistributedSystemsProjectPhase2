package omada_80.dsproject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ReduceWorker along with MapWorker are the main runnable classes. 
 * Actually, ReduceWorker created by the server holding the socket-connection with Master/User/Client
 * reads maps created by other workers,uses the reduce() process and finally sends its results back.
 * Responsible for closing streams and socket still remains the Master/User/Client.
 */
public class ReduceWorker implements Runnable {
	
	private Socket connection;				  // Socket-connection in order to read and write back to the Master/User/Client
	private List<Map<LocationPOI, Long>> mapList;  // A list filled with maps<POI,Number of URL> which will get from reading (input stream)
	
	
	/**
	 * Constructor of ReduceWorker class for having initialized 
	 * the socket-connection and the map-list. 
	 * @param connection The socket that is shared with the Master/User/Client
	 */
	public ReduceWorker(Socket connection) {
		this.connection = connection;
		this.mapList = new ArrayList<Map<LocationPOI, Long>>();
	}
	
	
	/**
	 * Implementing the Runnable interface.
	 * ReduceWorker's class main method for running concurrently.
	 */
	public void run() {
		readMaps();
		sendResult();
	}
	
	
	/**
	 * Opens input stream at this end of the connection and reads the maps 
	 * created from other workers in order to proceed with reducing.
	 * !! The last map that ReduceWorker reads contains a String !!
	 * !! This String acts as a signal to continue or abort process !!
	 */
	private void readMaps() {
		ObjectInputStream in = null;		

		try {
			
			in = new ObjectInputStream(connection.getInputStream());
			
			boolean flag = true;
			
			while (flag) {
					@SuppressWarnings("unchecked")
					Map<LocationPOI, Long> map =  (Map<LocationPOI, Long>) in.readObject();
					mapList.add(map);
					
					/* Reduce stands for continuing the reduce process.
					 * Abort stands for aborting the reduce process.
					 * The last map read contains one of these Strings as a signal.
					 */
					for (LocationPOI POI : map.keySet()) {
						if (POI.getPOI().equals("Reduce") || POI.getPOI().equals("Abort")) {
							flag = false;
							break;
						};
					}
			}
			
		}catch(ClassNotFoundException cnfe) {
			System.err.println("Data received in unknown format.");
			cnfe.printStackTrace();
		}catch(IOException ioe) {
			System.err.println("An error on input-stream (opening/reading) has occured.");
			ioe.printStackTrace();
		}
	}
	
	
	/**
	 * Opens the output stream at this end of the connection and writes back
	 * to the Master/User/Client the result of reduce() process if the last
	 * map read contains "Reduce".Otherwise, aborts the process.
	 */
	private void sendResult(){
		 ObjectOutputStream out = null;
		
		try {
			
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			
			boolean flag = true;
			
			for (Iterator<Map<LocationPOI, Long>> iterator = mapList.iterator(); iterator.hasNext();) {
				if (flag) {
					Map<LocationPOI, Long> map = iterator.next();
				    for (LocationPOI location : map.keySet()) {
						if (location.getPOI().equals("Reduce")) {
							iterator.remove();  // Removes the map that contains "Reduce" String
							
							out.writeObject(reduce(10));
							out.flush();
							flag = false;
							break;
						}else if (location.getPOI().equals("Abort")) {
							flag = false;
							break;
						}
					}
				}else{
					break;
				}
			}
			
			
				
			
			
			
		}catch (IOException ioe){
			System.err.println("An error on output stream (writing) has occured.");
			ioe.printStackTrace();
		}
	}
	
	
	 /**
	 * The main method of a ReduceWorker instance.
	 * Processes pairs from the map list and creates a new ordered map with them
	 * after reducing them to top-K results provided by limit variable. 
	 * @param limit The top-K (limit) results
	 * @return 
	 */
	private Map<LocationPOI, Long> reduce(int limit) { 
		Map<LocationPOI, Long> result = new LinkedHashMap<LocationPOI, Long>();
		
		for(Map<LocationPOI, Long> map : mapList) {
			result = Stream.concat(result.entrySet().stream(), map.entrySet().stream())
					.parallel().unordered().distinct()
					.collect(Collectors.toConcurrentMap(key -> key.getKey(), value -> value.getValue(), (dup1, dup2) -> dup1 + dup2));
		}
		
		Map<LocationPOI, Long> orderedResult = new LinkedHashMap<LocationPOI, Long>();
		
		result.entrySet().stream().parallel().unordered()
		.sorted(Map.Entry.<LocationPOI, Long>comparingByValue().reversed()).limit(limit)
		.forEachOrdered(e -> orderedResult.put(e.getKey(),e.getValue()));
		
		return orderedResult;
	}

}
