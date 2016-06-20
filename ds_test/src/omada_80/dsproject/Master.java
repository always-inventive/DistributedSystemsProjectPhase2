package omada_80.dsproject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * Master class is actually the coordinator of the whole Map-Reduce project.
 * Holds sockets,addresses,coordinates-limit all read by the user probably from the dummy client,
 * creates workers by connecting to the servers specified,splits the tasks by the coordinates and
 * finally after creating threads to help reading from MapWorkers, acknowledges the reduce worker
 * in order to proceed with the reduce() process and store the result or abort.
 */
public class Master {

    private ArrayList<Socket> mapConnections;	  // A list with socket-connections in order to read/write with map-workers
    private Socket reduceConnection;			  // Socket-connection in order to read and write back to the reduce-worker
    private String[][] mapAddresses; 			  // Addresses(IP,Port) stored to connect with map-workers spawning servers
    private String[][] reduceAddress;  			  // Addresses(IP,Port) stored to connect with reduce-workers spawning server
    private Coordinates coordinates;			  // Coordinates-range for splitting tasks to map-workers
    private Map<LocationPOI, Long> result;			  // Final result after map-reduce process
    private ArrayList<MWSpawn> helpThreads;  	  // A list with useful threads that help with reading from map-workers

    
    /**
     * Constructor for initializing addresses,coordinates and connections(sockets).
     * @param mAddresses Addresses(IP,Port) for map-workers spawning servers
     * @param rAddress Addresses(IP,Port) for reduce-workers spawning server
     * @param coordinates Coordinates-range read for map-workers
     */
    public Master(String[][] mapAddresses,String[][] reduceAddress,Coordinates coordinates) {
    	this.mapAddresses = mapAddresses;
    	this.reduceAddress = reduceAddress;
        this.coordinates = coordinates;
        this.mapConnections = new ArrayList<Socket>();
    }


    /**
     * Starts the whole process of 'mastering'.
     * Creates workers, splits tasks, waits for them to finish 
     * and acknowledges the reduce-worker to proceed or abort.
     */
	public void startProcess() {
		System.out.println("Map-Reduce process started!");
		System.out.println("Waiting . . .\n");
		
        createWorkers();
        splitTasks();
        waitForMapping();
        ackReduce();
    }

	
	/**
	 * Obtaining the final result stored as a global variable.
	 * @return result The final map result
	 */
	public Map<LocationPOI, Long> getFinalResult() {
        return result;
    }
	
	
	/**
	 * Creates workers by connecting to the servers specified plus storing the sockets.
	 */
	private void createWorkers() { 	
    	try {
    
    		for (int i = 0; i < mapAddresses.length; i++) {
    			mapConnections.add(new Socket(mapAddresses[i][0], Integer.parseInt(mapAddresses[i][1])));
    		}
            
    		reduceConnection = new Socket(reduceAddress[0][0], Integer.parseInt(reduceAddress[0][1]));
    		
        }catch(UnknownHostException uhe) {
        	System.err.println("You are trying to connect to an unknown host!");
            uhe.printStackTrace();
        }catch(IOException ioe) {
        	System.err.println("An I/O error on opening sockets has occured.");
            ioe.printStackTrace();
        }
    }
	
	
	/**
     * Splits the coordinates into smaller parts giving them to map-workers 
     * while creating MWSpawn threads in order each one of them to communicate
     * with a specific map-worker (writing/reading) and getting back the result
     * plus knowing when the mapping process has finished.
     */
    private void splitTasks() { 
    	helpThreads = new ArrayList<MWSpawn>();
    	
        double maxLatitude  = coordinates.getMaxLatitude();
        double minLatitude  = coordinates.getMinLatitude();
        double maxLongitude = coordinates.getMaxLongitude();
        double minLongitude = coordinates.getMinLongitude();
        String minDT = coordinates.getMinDT();
        String maxDT = coordinates.getMaxDT();
        
        double distance = (maxLatitude - minLatitude) / mapConnections.size();
        
        for(int i = 0; i < mapAddresses.length; i++) {
    		MWSpawn mHelp = new MWSpawn(mapConnections.get(i), 
    				new Coordinates(minLatitude+i*distance,minLongitude,minLatitude+(i+1)*distance,maxLongitude,minDT,maxDT));
			helpThreads.add(mHelp);	
    		mHelp.run();
        }
       


    }
	
	
    /**
     * Waits until all map-workers have finished and sent their results,
     * meaning all the MWSpawn threads have their result ready and have finished too.
     */
    private void waitForMapping() {
    	boolean hasFinished = false;
    	
    	while(!hasFinished) {
    		for (MWSpawn ht : helpThreads) {
        		if (!ht.hasFinished()) {
        			hasFinished = false;
        			break;
        		}else{
        			hasFinished = true;
        		}
        	}
        }
    }
    

    /**
     * Asks the user if he wants to continue with reduce-process or abort it.
     * After opening output stream at this end of the connection and sending the maps
     * stored in MWSpawn threads, sends one last map to the reduce-worker working
     * as a signal to continue/abort.
     * Responsible for finally closing streams and connection with the reduce-workers server.
     */
    private void ackReduce() {
    	ObjectInputStream in = null;
    	ObjectOutputStream out = null;
    	
    	/* Asking the user to type his decision */
    	Scanner reader = new Scanner(System.in);
    	System.out.println();
    	System.out.println("All map workers have finished their task.");
    	System.out.println("The reduce worker waits for your decision to start.");
    	System.out.print("Type 'R' or 'A' (Reduce-continue or Abort) : ");
    	char t = Character.toUpperCase(reader.next().charAt(0));
    	System.out.println();
		while (t != 'R' && t != 'A') {
			System.out.print("Type again one of these in order to proceed 'R' or 'A' : ");
			t = Character.toUpperCase(reader.next().charAt(0));
			System.out.println("\n");
		}
		
        
    	try {
    		
    		out = new ObjectOutputStream(reduceConnection.getOutputStream());
    		out.flush();
    		
	        if (t == 'R') {  // If user chose to continue with reducing process
	        	
	        	for (MWSpawn mt : helpThreads) {
	        		out.writeObject(mt.getMap());
	        		out.flush();
	        	}
	        	
	        	/* A specially made map sent to ReduceWorker as a signal from Master to start the Reduce procedure */
	        	Map<LocationPOI, Long> signalToReduce = new HashMap<LocationPOI, Long>();
	        	signalToReduce.put(new LocationPOI("Reduce"), -1L);
	        
	        	out.writeObject(signalToReduce);
	        	out.flush();
	        	
	        	
	        	in = new ObjectInputStream(reduceConnection.getInputStream());
	        	
	    		@SuppressWarnings("unchecked")
				Map<LocationPOI, Long> finalResult = (Map<LocationPOI, Long>) in.readObject();
	    		
	    		result = finalResult;
	    		
	    		System.out.println("The reduce-worker finished its task successfully!\n");
	    		
	    		for (Entry<LocationPOI, Long> entry : result.entrySet()) {
	    			System.out.println(entry.getKey().getPOIName() + " " + entry.getValue());
	    		}
	    			
	    		
	        }else{
	        	
	        	/* A specially made map sent to ReduceWorker as a signal from Master to abort the Reduce procedure */
	        	Map<LocationPOI, Long> signalToAbort = new HashMap<LocationPOI, Long>();
	        	signalToAbort.put(new LocationPOI("Abort"), -1L);
	        	out.writeObject(signalToAbort);
	        	out.flush();
	        	
	        	System.out.println("Aborting process . . .");
	        	System.out.println("Done!");
	        	
	        }
    		
	    }catch(ClassNotFoundException cnfe) {
	    	System.err.println("Data received in unknown format.");
			cnfe.printStackTrace();
	    }catch(IOException ioe){
	    	System.err.println("An error on I/O streams has occured.");
			ioe.printStackTrace();
		}finally {
			
			try {
				
				/* Closing streams,connection */
				if (in != null) in.close();
				if (out != null) out.close();
    			reduceConnection.close();
    			
    			reader.close();
    			
			}catch (IOException ioe) {
				System.err.println("An I/O has occured while trying to close.");
				ioe.printStackTrace();
			}
		}
    }
    
    
    
    /**
     * A specifically created Thread class in order to help Master writing,reading with
     * map-workers while also being useful in other methods like waitForMapping().
     * Each MWSpawn instance is responsible for a specific map-worker and when all of them
     * have finished, that means that map process has also finished for Master.
     * Responsible for finally closing streams and connection with the map-workers servers.
     */
    private class MWSpawn extends Thread {
    	
    	private Socket mConnection;		   // The specific socket-connection this MWSpawn instance needs to interact
    	private Coordinates coords;		   // The new coordinates-range given by Master
    	private boolean isWorking;		   // Representing if MWSpawn as a thread is still running or has finished
    	private Map<LocationPOI, Long> result;  // Final result after reading from map-worker
    	
    	
    	/**
    	 * Constructor for initializing map socket and coordinates.
    	 * @param mConnection Specific socket for a single map-worker server
    	 * @param coords New coordinates-range
    	 */
    	public MWSpawn(Socket mConnection, Coordinates coords) {
    		this.mConnection = mConnection;
    		this.coords = coords;
    		isWorking = true;
    	}
    	
    	
    	/**
    	 * Implementing the Runnable interface.
    	 * MWSpawn's class main method for running concurrently.
    	 * Writes its coordinates to the map-worker and reads the result.
    	 */
    	public void run() {
    		ObjectOutputStream out = null;
    		ObjectInputStream in = null;
    		
    		try {
    			
    			out = new ObjectOutputStream(mConnection.getOutputStream());
    			out.flush();
    			
    			out.writeObject(coords.getMinLatitude());
    			out.flush();
    			
    			out.writeObject(coords.getMinLongitude());
    			out.flush();
    			
    			out.writeObject(coords.getMaxLatitude());
    			out.flush();
    			
    			out.writeObject(coords.getMaxLongitude());
    			out.flush();

    			out.writeObject(coords.getMinDT());
    			out.flush();

    			out.writeObject(coords.getMaxDT());
    			out.flush();
    			
				in = new ObjectInputStream(mConnection.getInputStream());
				
				@SuppressWarnings("unchecked")
				Map<LocationPOI, Long> mapResult = (Map<LocationPOI, Long>) in.readObject();
				result = mapResult;
				
        		isWorking = false;
        		System.out.println("The map-worker with ID " + this.getId() + " has just finished.");

    		}catch(IOException ioe) {
    			System.err.println("An error on I/O streams has occured.");
    			ioe.printStackTrace();
    		}catch(ClassNotFoundException cnfe) {
    			System.err.println("Data received in unknown format.");
				cnfe.printStackTrace();
    		}finally {
    			
    			try {
    				
    				/* Closing streams,connection */
    				if (in != null) in.close();
    				if (out != null) out.close();
	    			mConnection.close();	
	    			
				}catch(IOException ioe) {
					System.err.println("An I/O has occured while trying to close.");
					ioe.printStackTrace();
				}
    			
    		}
    	}
    	
    	
    	/**
    	 * Checks if this MWSpawn instance has finished or not.
    	 * @return  True/False depending on isWorking
    	 */
    	public boolean hasFinished() {
    		return !isWorking;
    	}
    	
    	
    	/**
    	 * Returns the final result-map read by the map-worker.
    	 * @return result Final result-map read
    	 */
    	public Map<LocationPOI, Long> getMap() {
    		return result;
    	}
    	
    }
    

}