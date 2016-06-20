package omada_80.dsproject;

/**
 * Useful class acting as the 'holder' of the coordinates as its name implies,
 * that pass from the master class to the workers representing the spatial,time
 * range-limits given by the user-client.
 */
public class Coordinates  {

    /* Global variables representing the coordinates class itself */
    private double minLatitude;
    private double minLongitude;
    private double maxLatitude;
    private double maxLongitude;

    /* DT standing for date-time */
    private String minDT;
    private String maxDT;


    /**
     * Constructor for initiliazing latitudes,longitudes meaning the min,max coordinates
     * plus the minimum and maximum date-time to be used as limits with values provided.
     * @param minLatitude  Minimum latitude  on the map
     * @param minLongitude Minimum longitude on the map
     * @param maxLatitude  Maximum latitude  on the map
     * @param maxLongitude Maximum longitude on the map
     * @param minDT Minimum date-time
     * @param maxDT Maximum date-time
     */
    public Coordinates(double minLatitude, double minLongitude, double maxLatitude, double maxLongitude, String minDT, String maxDT) {
        this.minLatitude = minLatitude;
        this.minLongitude = minLongitude;
        this.maxLatitude = maxLatitude;
        this.maxLongitude = maxLongitude;
        this.minDT = minDT;
        this.maxDT = maxDT;
    }


    /**
     * Constructor for initiliazing latitudes,longitudes meaning the min,max coordinates
     * plus the minimum and maximum date-time to be used as limits with no values,
     * usually set by its instance methods afterwards.
     */
    public Coordinates() {
        this.minLatitude = this.minLongitude = this.maxLatitude = this.maxLongitude = 0;
        this.minDT = this.maxDT = "";
    }


    /**
     * Setting new max longitude.
     * @param maxLongtitude Coordinates maximum longitude-variable
     */
    public void setMaxLongitude(double maxLongtitude) {
        this.maxLongitude = maxLongtitude;
    }


    /**
     * Setting new min longitude.
     * @param minLongtitude Coordinates minimum longitude-variable
     */
    public void setMinLongitude(double minLongtitude) {
        this.minLongitude = minLongtitude;
    }


    /**
     * Setting new max latitude.
     * @param maxLatitude Coordinates max latitude-variable
     */
    public void setMaxLatitude(double maxLatitude) {
        this.maxLatitude = maxLatitude;
    }


    /**
     * Setting new min latitude.
     * @param minLatitude Coordinates minimum latitude-variable
     */
    public void setMinLatitude(double minLatitude) {
        this.minLatitude = minLatitude;
    }


    /**
     * Setting new min date-time.
     * @param minDT Coordinates minimum date-time-variable
     */
    public void setMinDT(String minDT) {
        this.minDT = minDT;
    }


    /**
     * Setting new max date-time.
     * @param maxDT Coordinates maximum date-time-variable
     */
    public void setMaxDT(String maxDT) {
        this.maxDT = maxDT;
    }


    /**
     * Obtaining max longitude.
     * @return maxLongitude Coordinates maximum longitude
     */
    public double getMaxLongitude() {
        return this.maxLongitude;
    }


    /**
     * Obtaining min longitude.
     * @return minLongitude Coordinates minimum longitude
     */
    public double getMinLongitude() {
        return this.minLongitude;
    }


    /**
     * Obtaining max latitude.
     * @return maxLaitude Coordinates maximum latitude
     */
    public double getMaxLatitude() {
        return this.maxLatitude;
    }


    /**
     * Obtaining min latitude.
     * @return minLatitude Coordinates minimum latitude
     */
    public double getMinLatitude() {
        return this.minLatitude;
    }


    /**
     * Obtaining min date-time.
     * @return minDT Coordinates minimum date-time limit
     */
    public String getMinDT() {
        return this.minDT;
    }


    /**
     * Obtaining max date-time.
     * @return Coordinates maximum date-time limit
     */
    public String getMaxDT() {
        return this.maxDT;
    }

}
