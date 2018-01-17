package memory;

/**
 *@author Eric Le Fort
 *@version 0.0.1
 */
public class Course{
	private double code, grade;
	private int credits;
	
	/**
	 * Initializes a new <code>Course</code> with the given parameters.
	 * 
	 * @param code - The mapped value of this course's code.
	 * @param grade - The grade acquired in this course.
	 * @param credits - The credits this course is worth.
	 */
	public Course(int code, int grade, int credits){
		this.code = code;
		this.grade = grade;
		this.credits = credits;
	}//Constructor

	public void setCode(double code){ this.code = code; }//setCode()
	public void setGrade(double grade){ this.grade = grade; }//setGrade()
	
	public double getCode(){ return code; }//getCode()
	public double getGrade(){ return grade; }//getGrade()
	public int getCredits(){ return credits; }//getCredits()
	
}//Course