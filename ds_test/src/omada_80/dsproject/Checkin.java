package omada_80.dsproject;

/**
 * Checkin class used in order to represent a pair of key-value (POI-URL)
 * useful for the whole mapping-reducing process, eventually filling
 * a map with keys as its POI and values as the final counting of URLs.
 */
public class Checkin {
	
	private LocationPOI POI;  // Point-of-Interest is the key of a check-in
	private String URL;  // Photo-URL goes together with a POI, useful for counting photos
	
	
	/**
	 * Constructor for initializing POI,URL variables.
	 * @param POI The Key (Point-of-Interest) global variable
	 * @param URL The Value (URL-Photo) global variable
	 */
	public Checkin(LocationPOI POI, String URL) {
		this.POI = POI;
		this.URL = URL;
	}
	
	
	/**
	 * Setting new Point-of-Interest.
	 * @param POI The key-variable (Point-of-Interest)
	 */
	public void setLocationPOI(LocationPOI POI) {
		this.POI = POI;
	}
	
	
	/**
	 * Setting new URL-Photo.
	 * @param URL The value-variable (URL-Photo)
	 */
	public void setURL(String URL) {
		this.URL = URL;
	}
	 
	
	/**
	 * Obtaining Point-of-Interest.
	 * @return POI The key-variable (Point-of-Interest)
	 */
	public LocationPOI getLocationPOI() {
		return POI;
	}
	
	
	/**
	 * Obtaining URL-Photo.
	 * @return URL The value-variable (URL-Photo)
	 */
	public String getURL() {
		return URL;
	}
	
	
	/**
	 * Overrides java.lang.Object.toString in order to be able
	 * to print itself as a String composed of POI and a URL.
	 * @return POI,URL A string representing a Checkin instance
	 */
	public String toString() {
		return POI + " " + URL;
	}
		
}