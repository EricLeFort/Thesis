package memory;

/**
 *@author Eric Le Fort
 *@version 0.0.1
 */
public class Program{
	private double universityID, programName, yearLevel, programGroup, enrolmentTerm;
	
	/**
	 * Initializes a new <code>Program</code> with the given parameters.
	 * 
	 * @param universityID - The id of the university this program is at.
	 * @param programName - The mapped value of the name of this program
	 * @param yearLevel - The year of this program.
	 * @param programGroup - The program group this program belongs to.
	 * @param enrolmentTerm - The enrolment term of this program.
	 */
	public Program(int universityID, int programName, int yearLevel, int programGroup, int enrolmentTerm){
		this.universityID = universityID;
		this.programName = programName;
		this.yearLevel = yearLevel;
		this.programGroup = programGroup;
		this.enrolmentTerm = enrolmentTerm;
	}//Constructor
	
	public void setUniversityID(double universityID){ this.universityID = universityID; }//setUniversityID()
	public void setProgramName(double programName){ this.programName = programName; }//setProgramName()
	public void setYearLevel(double yearLevel){ this.yearLevel = yearLevel; }//setYearLevel()
	public void setProgramGroup(double programGroup){ this.programGroup = programGroup; }//setProgramGroup()
	public void setEnrolmentTerm(double enrolmentTerm){ this.enrolmentTerm = enrolmentTerm; }//setEnrolmentTerm()
	
	public double getUniversityID(){ return universityID; }//getUniversityID()
	public double getProgramName(){ return programName; }//getProgramName()
	public double getYearLevel(){ return yearLevel; }//getYearLevel()
	public double getProgramGroup(){ return programGroup; }//getProgramGroup()
	public double getEnrolmentTerm(){ return enrolmentTerm; }//getEnrolmentTerm()

}//Program