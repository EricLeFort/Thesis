package memory;

/**
 *@author Eric Le Fort
 *@version 0.0.1
 */
public class CitizenshipInfo{
	private Location location;
	private double immigrationStatus;
	
	/**
	 * Initializes a new <code>CitizenshipInfo</code> with the given parameters.
	 * 
	 * @param immigrationStatus - This citizenship's immigration status.
	 * @param location - This citizenship's location.
	 */
	public CitizenshipInfo(Location location, int immigrationStatus){
		this.immigrationStatus = immigrationStatus;
		this.location = location;
	}//Constructor
	
	public void setImmigrationStatus(double immigrationStatus){
		this.immigrationStatus = immigrationStatus;
	}//setImmigrationStatus()
	
	public double getImmigrationStatus(){ return immigrationStatus; }//getImmigrationStatus()
	public Location getLocation(){ return location; }//getLocation()
}//CitizenshipInfo