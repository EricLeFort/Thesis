package memory;

/**
 *@author Eric Le Fort
 *@version 0.0.1
 */
public class CourseInfo{
	private Course[] courses;
	private double totalCredit, courseCount;
	
	/**
	 * Initializes a new <code>CourseInfo</code> with the given parameters.
	 * 
	 * @param courses - The courses taken.
	 */
	public CourseInfo(Course[] courses){
		this.courses = courses;
		this.courseCount = courses.length;
		
		totalCredit = 0;
		for(int i = 0; i < courses.length; i++){
			totalCredit += courses[i].getCredits();
		}
	}//Constructor
	
	public void setCourseCount(double courseCount){ this.courseCount = courseCount; }//setCourseCount()
	public void setTotalCredits(double totalCredit){ this.totalCredit = totalCredit; }//setTotalCredit()
	
	public Course[] getCourses(){ return courses; }//getCourses()
	public double getCourseCount(){ return courseCount; }//getCourseCount()
	public double getTotalCredits(){ return totalCredit; }//getTotalCredits()

}//CourseInfo