package memory;

/**
 *@author Eric Le Fort
 *@version 0.0.1
 */
public class Application{
	private Program program, offer;
	private double major, preference, fullTime, coop;
	
	/**
	 * Initializes a new <code>Application</code> with the given parameters.
	 * 
	 * @param program - The program this application is for.
	 * @param offer - The offer made for this application.
	 * @param major - The major this application is for.
	 * @param preference - The preference of this application.
	 * @param fullTime - Whether this application is for a full-time program (1 if it is, 0 otherwise).
	 * @param coop - Whether this application is for a coop program (1 if it is, 0 otherwise).
	 */
	public Application(Program program, Program offer, int major, int preference, int fullTime, int coop){
		this.program = program;
		this.offer = offer;
		this.major = major;
		this.preference = preference;
		this.fullTime = fullTime;
		this.coop = coop;
	}//Constructor
	
	public void setMajor(double major){ this.major = major; }//setMajor()
	public void setPreference(double preference){this.preference = preference; }//setPreference()
	public void setFullTime(double fullTime){ this.fullTime = fullTime; }//setFullTime()
	public void setCoop(double coop){ this.coop = coop; }//setCoop()
	
	public Program getProgram(){ return program; }//getProgram()
	public Program getOffer(){ return offer; }//getOffer()
	public double getMajor(){ return major; }//getMajor()
	public double getPreference(){ return preference; }//getPreference()
	public double getFullTime(){ return fullTime; }//getFullTime()
	public double getCoop(){ return coop; }//getCoop()

}//Application