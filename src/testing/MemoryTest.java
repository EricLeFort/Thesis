package testing;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import memory.*;

/**
 * @author Eric Le Fort
 * @version 1.0
 */
public class MemoryTest{
	private static Applicant a;
	
	@BeforeClass
	public static void runConstructors(){
		Application[] applications = new Application[20];
		Course[] courses = new Course[12];
		CourseInfo courseInfo;
		Program confirmation = new Program(8, 9, 10, -1, 11),
				registration = new Program(12, 13, 14, -1, 15),
				applied, offered;
		Highschool highschool = new Highschool(1, -1, -1, -1);
		Location residence = new Location(4, "", -1, -1, -1, -1),
				citizenship = new Location(6, "", -1, -1, -1, -1);
		CitizenshipInfo citizenshipInfo = new CitizenshipInfo(citizenship, 5);
		int birthYear = 3, sex = 2, tongue = 7, yearsInSchool = 40;
		double currentAvg = 42, totalAvg = 43;
		
		for(int i = 0; i < courses.length; i++){
			courses[i] = new Course(16 + 2*i, 17 + 2*i, -1);
		}
		courseInfo = new CourseInfo(courses);
		courseInfo.setCourseCount(41);
		for(int i = 0; i < applications.length; i++){
			applied = new Program(
					45 + 11*i,
					46 + 11*i,
					51 + 11*i,
					-1,
					48 + 11*i);
			offered = new Program(
					-1,
					52 + 11*i,
					54 + 11*i,
					-1,
					53 + 11*i);
			applications[i] = new Application(
					applied,
					offered,
					49 + 11*i,
					44 + 11*i,
					47 + 11*i,
					50 + 11*i);
		}
		
		a = new Applicant(applications, courseInfo, confirmation, registration, highschool,
				citizenshipInfo, residence, birthYear, sex, currentAvg, totalAvg, tongue, yearsInSchool);
	}//testConstructors()
	
	@Test
	public void testConstructAndFlatten(){
		final double DELTA = 1E-9;
		double[] flat = a.flatten();
		
		assertEquals(263, flat.length);			//Assure the flattened array is the correct size
		
		for(int i = 0; i < flat.length; i++){	//Assure all values are as created.
			assertEquals(i + 1, flat[i], DELTA);
		}
	}//testConstructAndFlatten()
	
}//MemoryTest