package ml;

import java.util.ArrayList;

import memory.Applicant;
import memory.Course;

/**
 *@author Eric Le Fort
 *@version 0.0.1
 */
public abstract class ApplicantFilter{
	private static final String COURSE_CODES[] = new String[]{
			//TODO fill in these values
	};
	private static final double MIN_COURSE_AVGS[] = new double[]{
			//TODO fill in these values
	}, MIN_TOTAL_AVG = 0;
	
	public static Applicant[] filter(Applicant[] applicants){
		ArrayList<Applicant> filtered = new ArrayList<Applicant>(applicants.length);
		Course[] courses;
		boolean eligible;
		
		for(int i = 0; i < applicants.length; i++){
			eligible = true;
			
			if(applicants[i].getTotalAvg() >= MIN_TOTAL_AVG){			//Verify total average acceptable
				courses = applicants[i].getCourseInfo().getCourses();
				for(int j = 0; j < courses.length; j++){
					for(int k = 0; k < COURSE_CODES.length; k++){		//Verify course grades acceptable
						if(""+courses[i].getCode() == COURSE_CODES[k]//TODO fix this..
								&& courses[i].getGrade() < MIN_COURSE_AVGS[k]){
							eligible = false;
							break;
						}
					}
					if(!eligible){
						break;
					}
				}
				
				if(eligible){											//This applicant can pass through the filter
					filtered.add(applicants[i]);
				}
			}
		}
		
		return filtered.toArray(new Applicant[0]);
	}//filter()

}//ApplicantFilter