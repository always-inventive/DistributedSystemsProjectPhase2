package omada_80.dsproject;

/**
 * A serializable class used to 'gather' all the information needed of
 * a specific location, which is read/written between User/Master and
 * the Map/Reduce workers.The final result map created and given to the
 * user consists of LocationPOI objects.
 *
 */
public class LocationPOI implements java.io.Serializable {
	
	private static final long serialVersionUID = -7637686123299461551L;
	
	private String POI;		   // Point-of-Interest ID string
	private String POIName;    // Point-of-Interest actual name
	private double latitude;   // Latitude value of the location of POI
	private double longitude;  // Longitude value of the location of POI
	
	
	/**
	 * Initializes only the POI ID string.
	 * @param POI Point-of-Interest ID
	 */
	public LocationPOI(String POI) {
		this.POI = POI;
	}
	
	
	/**
	 * Initializes all the needed info for a specific location description.
	 * @param POI Point-of-Interest ID
	 * @param POIName Point-of-Interest name
	 * @param latitude Point-of-Interest latitude
	 * @param longitude Point-of-Interest longitude
	 */
	public LocationPOI(String POI, String POIName, double latitude, double longitude) {
		this.POI = POI;
		this.POIName = POIName;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	
	/**
	 * Getting the POI string-id.
	 * @return POI Point-of-Interest ID
	 */
	public String getPOI() {
		return this.POI;
	}
	
	
	/**
	 * Getting the POI name.
	 * @return POIName Point-of-Interest name
	 */
	public String getPOIName() {
		return this.POIName;
	}
	
	
	/**
	 * Getting POI latitude.
	 * @return latitude Point-of-Interest latitude
	 */
	public double getLatitude() {
		return this.latitude;
	}
	
	
	/**
	 * Getting POI longitude.
	 * @return longitude Point-of-Interest longitude
	 */
	public double getLongitude() {
		return this.longitude;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof LocationPOI)) return false;
		if (this.POI.equals(((LocationPOI) obj).getPOI()) || obj == this) return true;
		return false;
	}
		
}
