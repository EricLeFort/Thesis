package memory;

/**
 *@author Eric Le Fort
 *@version 0.0.1
 */
public class Applicant{
	private static final int FLAT_SIZE = 263;
	
	private Application[] applications;
	private CourseInfo courseInfo;
	private Program confirmation, registration;
	private Highschool highschool;
	private CitizenshipInfo citizenship;
	private Location residence;
	private double currentAvg, totalAvg, tongue, sex, birthYear, yearsInSchool;
	
	/**
	 * Initializes a new <code>Applicant</code> with the given parameters.
	 * 
	 * @param applications - The set of applications for this user.
	 * @param courseInfo - This user's course information.
	 * @param confirmation - This user's confirmed choice.
	 * @param registration - This user's confirmed registration.
	 * @param highschool - This user's high school.
	 * @param citizenship - This user's citizenship information.
	 * @param residence - This user's location of residence.
	 * @param birthYear - This user's birth year.
	 * @param sex - This user's sex.
	 * @param currentAvg - This user's average for their last 6 senior courses.
	 * @param totalAvg - This user's average for all senior courses.
	 * @param tongue - This user's native tongue.
	 * @param yearsInSchool - The number of years this student spent in Ontario secondary schools.
	 */
	public Applicant(Application[] applications, CourseInfo courseInfo, Program confirmation, Program registration,
			Highschool highschool, CitizenshipInfo citizenship, Location residence, int birthYear, int sex,
			double currentAvg, double totalAvg, int tongue, int yearsInSchool){
		this.applications = applications;
		this.courseInfo = courseInfo;
		this.confirmation = confirmation;
		this.registration = registration;
		this.highschool = highschool;
		this.citizenship = citizenship;
		this.residence = residence;
		this.birthYear = birthYear;
		this.sex = sex;
		this.currentAvg = currentAvg;
		this.totalAvg = totalAvg;
		this.tongue = tongue;
		this.yearsInSchool = yearsInSchool;
	}//Constructor
	
	/**
	 * Creates a flattened version of this <code>Applicant</code>'s data.<br><br>
	 * 
	 * This data will be in the following order:<br>
	 * 0: highschoolID<br>
	 * 1: sex<br>
	 * 2: birthYear<br>
	 * 3: residence<br>
	 * 4: immstat<br>
	 * 5: citizenship<br>
	 * 6: tongue<br>
	 * 7: confirmUni<br>
	 * 8: confirmProgram<br>
	 * 9: confirmYearLevel<br>
	 * 10: confirmTerm<br>
	 * 11: regUni<br>
	 * 12: regProgram<br>
	 * 13: regYearLevel<br>
	 * 14: regTerm<br>
	 * 15: courseCode1<br>
	 * 16: courseGrade1<br>
	 * ...<br>
	 * 37: courseCode12<br>
	 * 38: courseGrade12<br>
	 * 39: yearsInSchool<br>
	 * 40: courseCount<br>
	 * 41: currentAvg<br>
	 * 42: totalAvg<br>
	 * 43: applicationChoice1<br>
	 * 44: applicationUni1<br>
	 * 45: applicationProgram1<br>
	 * 46: applicationFulltime1<br>
	 * 47: applicationTerm1<br>
	 * 48: applicationMajor1<br>
	 * 49: applicationCoop1<br>
	 * 50: applicationYearLevel1<br>
	 * 51: offerProgram1<br>
	 * 52: offerTerm1<br>
	 * 53: offerYearLevel1<br>
	 * ...<br>
	 * 252: applicationChoice20<br>
	 * 253: applicationUni20<br>
	 * 254: applicationProgram20<br>
	 * 255: applicationFulltime20<br>
	 * 256: applicationTerm20<br>
	 * 257: applicationMajor20<br>
	 * 258: applicationCoop20<br>
	 * 259: applicationYearLevel20<br>
	 * 260: offerProgram20<br>
	 * 261: offerTerm20<br>
	 * 262: offerYearLevel20<br>
	 * 
	 * @return A flattened version of this <code>Applicant</code>'s data.
	 */
	public double[] flatten(){
		final int NUM_COURSES = 12, NUM_APPS = 20,
				COURSE_COLS = 2, APP_COLS = 11;
		
		Application app;
		Course course;
		int start, end;
		double[] flat = new double[FLAT_SIZE];
		
		flat[0] = highschool.getID();
		flat[1] = sex;
		flat[2] = birthYear;
		flat[3] = residence.getMappedVal();
		flat[4] = citizenship.getImmigrationStatus();
		flat[5] = citizenship.getLocation().getMappedVal();
		flat[6] = tongue;
		flat[7] = confirmation.getUniversityID();
		flat[8] = confirmation.getProgramName();
		flat[9] = confirmation.getYearLevel();
		flat[10] = confirmation.getEnrolmentTerm();
		flat[11] = registration.getUniversityID();
		flat[12] = registration.getProgramName();
		flat[13] = registration.getYearLevel();
		flat[14] = registration.getEnrolmentTerm();
		
		start = 15;
		end = start + NUM_COURSES * COURSE_COLS;
		for(int i = start; i < end; i += COURSE_COLS){
			course = courseInfo.getCourses()[(i - start) / COURSE_COLS];
			flat[i] = course.getCode();
			flat[i + 1] = course.getGrade();
		}
		
		flat[39] = yearsInSchool;
		flat[40] = courseInfo.getCourseCount();
		flat[41] = currentAvg;
		flat[42] = totalAvg;
		
		start = 43;
		end = start + NUM_APPS * APP_COLS;
		for(int i = start; i < end; i += APP_COLS){
			app = applications[(i - start) / APP_COLS];
			flat[i] = applications[(i - start) / APP_COLS].getPreference();
			flat[i + 1] = app.getProgram().getUniversityID();
			flat[i + 2] = app.getProgram().getProgramName();
			flat[i + 3] = app.getFullTime();
			flat[i + 4] = app.getProgram().getEnrolmentTerm();
			flat[i + 5] = app.getMajor();
			flat[i + 6] = app.getCoop();
			flat[i + 7] = app.getProgram().getYearLevel();
			flat[i + 8] = app.getOffer().getProgramName();
			flat[i + 9] = app.getOffer().getEnrolmentTerm();
			flat[i + 10] = app.getOffer().getYearLevel();
		}
		
		return flat;
	}//flatten()
	
	public void setBirthYear(double birthYear){ this.birthYear = birthYear; }//setBirthYear()
	public void setSex(double sex){ this.sex = sex; }//setSex()
	public void setCurrentAvg(double currentAvg){ this.currentAvg = currentAvg; }//setCurrentAvg()
	public void setTotalAvg(double totalAvg){ this.totalAvg = totalAvg; }//setTotalAvg()
	public void setTongue(double tongue){ this.tongue = tongue; }//setTongue()
	public void setYearsInSchool(double yearsInSchool){ this.yearsInSchool = yearsInSchool; }//setYearsInSchool()
	
	public Application[] getApplications(){ return applications; }//getApplications()
	public CourseInfo getCourseInfo(){ return courseInfo; }//getCourseInfo()
	public Program getConfirmation(){ return confirmation; }//getConfirmation()
	public Program getRegistration(){ return registration; }//getRegistration()
	public Highschool getHighschool(){ return highschool; }//getHighschool()
	public CitizenshipInfo getCitizenship(){ return citizenship; }//getCitizenship()
	public Location getResidence(){ return residence; }//getResidence()
	public double getBirthYear(){ return birthYear; }//getBirthYear()
	public double getSex(){ return sex; }//getSex()
	public double getCurrentAvg(){ return currentAvg; }//getCurrentAvg()
	public double getTotalAvg(){ return totalAvg; }//getTotalAvg()
	public double getTongue(){ return tongue; }//getTongue()
	public double getYearsInSchool(){ return yearsInSchool; }//getYearsInSchool()
	public boolean isConfirmed(){ return confirmation != null; }//isConfirmed()
	public boolean isRegistered(){ return registration != null; }//isRegistered()
	
}//Applicant