package memory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import database.DatabaseAccessor;
import ml.ApplicantFilter;
import ml.ModelInterface;

/**
 *@author Eric Le Fort
 *@version 0.0.1
 */
public abstract class WorkingMemory{
	private static final int COURSE_COLS = 3,
			APPLICATION_COLS = 16,
			HS_START = 1,
			APPLICANT_START = 5,
			CONF_START = 17,
			REG_START = 23,
			COURSE_START = 29,
			SUMMARY_START = 65,
			APPLICATION_START = 71,
			SEQ_NO_START = 391;
	
	private static Applicant[][] applicants;
	private static HashMap<Double, Integer> predictions;
	private static HashMap<String, Integer> courseMap, locationMap, programMap, sexMap, termMap;
	private static String localDataFilePath = "";
	
	static{
		predictions = new HashMap<Double, Integer>();
		courseMap = new HashMap<String, Integer>();
		locationMap = new HashMap<String, Integer>();
		programMap = new HashMap<String, Integer>();
		sexMap = new HashMap<String, Integer>();
		termMap = new HashMap<String, Integer>();
	}
	
	/**
	 * Performs a line search along all possible averages to find one that is close enough to the <code>target</code>.
	 * Once one that satisfies the stopping criteria is found, it is returned.
	 * 
	 * @param target - The target attendance to aim for using the predictions.
	 * @return The acceptance average that satisfied the stopping criteria.
	 */
	public static double predictAverage(int target) throws FileNotFoundException, IOException{
		final double MIN_AVG = 75.0f;
		int low, mid, high, a = (int)(MIN_AVG * 100), c = 10000, b, diffHigh, diffLow;
		
		low = predictAttendance(a / 100.0f);
		if(target > low){							//Return even if too small because this is the theoretical max
			return a;
		}
		
		high = predictAttendance(c / 100.0f);
		if(high > target){							//Return even if too large because this is the theoretical min
			return c;
		}
		
		do{
			b = (high - low) / 2;
			mid = predictAttendance(b / 100.0f);
			
			if(target < mid){						//Left segment
				c = b;
				high = mid;
			}else{									//Right segment
				a = b;
				low = mid;
			}
		}while(high - low >= 2);
		
		diffHigh = Math.abs(target - high);
		diffLow = Math.abs(target - low);
		
		if(diffLow < diffHigh){						//Bottom of range was more accurate
			return a;
		}
		
		return c;
	}//predictAverage()
	
	/**
	 * Predicts the number of attendees given an acceptance average. The prediction is rounded to the nearest integer.
	 * 
	 * @param averge - The acceptance average.
	 * @return The predicted number of attendees.
	 */
	public static int predictAttendance(double average) throws FileNotFoundException, IOException{
		Applicant[] currentApplicants = applicants[applicants.length - 1];
		double rawCount = 0;
		int count;
		
		if(predictions.containsKey(average)){
			return predictions.get(average);
		}
		
		for(int i = 0; i < applicants.length; i++){
			rawCount += ModelInterface.predict(currentApplicants[i]);
		}
		count = (int)Math.round(rawCount);
		predictions.put(average, count);
		
		return count;
	}//predictAttendance()
	
	/**
	 * Uses the <code>DatabaseAccessor</code> to gather <code>Applicant</code> data from the database. Once this data
	 * is collected, it builds this class' <code>Applicant</code> array. This method also ensures the data is
	 * normalized.
	 */
	public static void loadApplicantData(){
		String[][][] data = null;
		Application[] applications;
		Course[] courses;
		CourseInfo courseInfo;
		Program confirmation, registration, appProgram, offerProgram;
		Highschool highschool;
		CitizenshipInfo citizenshipInfo;
		Location residence, citizenship;
		String location;
		double currentAvg, totalAvg, courseCount;
		int birthYear, tongue, yearsInSchool;
		
		try{
			data = DatabaseAccessor.queryAll();
		}catch(Exception e){
			applicants = null;
			System.err.println("Error querying applicant data.");
			return;
		}
		
		programMap.put("", 0);									//Ensure empty fields map to 0
		courseMap.put("", 0);
		locationMap.put("", 0);
		sexMap.put("", 0);
		termMap.put("", 0);
		
		applicants = new Applicant[data.length][];				//Clear old data to allow for adding new data.
		for(int i = 0; i < data.length; i++){
			applicants[i] = new Applicant[data[i].length];
			for(int j = 0; j < data[i].length; j++){
				applications = new Application[20];
				for(int k = APPLICATION_START; k < SEQ_NO_START; k += APPLICATION_COLS){
					if(data[i][j][k + 1] == null){
						appProgram = new Program(0, 0, 0, 0, 0);
					}else{
						termMap.putIfAbsent(data[i][j][k + 5], termMap.size());
						programMap.putIfAbsent(data[i][j][k + 2], programMap.size());
						appProgram = new Program(
								Integer.parseInt(data[i][j][k + 1]),
								programMap.get(data[i][j][k + 2]),
								Integer.parseInt(data[i][j][k + 8]),
								Integer.parseInt(data[i][j][k + 3]),
								termMap.get(data[i][j][k + 5]));
					}
					
					if(data[i][j][k + 1] == null || data[i][j][k + 10] == null){
						offerProgram = new Program(0, 0, 0, 0, 0);
					}else{
						termMap.putIfAbsent(data[i][j][k + 12], termMap.size());
						programMap.putIfAbsent(data[i][j][k + 10], programMap.size());
						offerProgram = new Program(
								Integer.parseInt(data[i][j][k + 1]),
								programMap.get(data[i][j][k + 10]),
								Integer.parseInt(data[i][j][k + 13]),
								Integer.parseInt(data[i][j][k + 11]),
								termMap.get(data[i][j][k + 12]));
					}
					
					if(appProgram.getProgramName() == 0){
						applications[(k - APPLICATION_START) / APPLICATION_COLS] = new Application(
								appProgram,
								offerProgram,
								0,
								0,
								0,
								0);
					}else{
						applications[(k - APPLICATION_START) / APPLICATION_COLS] = new Application(
								appProgram,
								offerProgram,
								Integer.parseInt(data[i][j][k + 6]),
								Integer.parseInt(data[i][j][k]),
								Integer.parseInt(data[i][j][k + 4]),
								Integer.parseInt(data[i][j][k + 7]));
					}
				}
				
				courses = new Course[12];
				courseCount = 12;
				for(int k = COURSE_START; k < SUMMARY_START; k += COURSE_COLS){
					if(data[i][j][k] == null){
						courses[(k - COURSE_START) / COURSE_COLS] = new Course(0, 0, 0);
						courseCount--;
					}else{
						courseMap.putIfAbsent(data[i][j][k], courseMap.size());
						courses[(k - COURSE_START) / COURSE_COLS] = new Course(
								courseMap.get(data[i][j][k]),
								Integer.parseInt(data[i][j][k + 2]),
								Integer.parseInt(data[i][j][k + 1]));
					}
				}
				courseInfo = new CourseInfo(courses);
				courseInfo.setCourseCount(courseCount);
				
				termMap.putIfAbsent(data[i][j][CONF_START + 4], termMap.size());
				programMap.putIfAbsent(data[i][j][CONF_START + 1], programMap.size());
				confirmation = new Program(
						Integer.parseInt(data[i][j][CONF_START]),
						programMap.get(data[i][j][CONF_START + 1]),
						Integer.parseInt(data[i][j][CONF_START + 3]),
						Integer.parseInt(data[i][j][CONF_START + 2]),
						termMap.get(data[i][j][CONF_START + 4]));
				
				termMap.putIfAbsent(data[i][j][REG_START + 4], termMap.size());
				programMap.putIfAbsent(data[i][j][REG_START + 1], programMap.size());
				registration = new Program(
						Integer.parseInt(data[i][j][CONF_START]),
						programMap.get(data[i][j][REG_START + 1]),
						Integer.parseInt(data[i][j][REG_START + 3]),
						Integer.parseInt(data[i][j][REG_START + 2]),
						termMap.get(data[i][j][REG_START + 4]));
				
				highschool = new Highschool(
						Integer.parseInt(data[i][j][HS_START]),
						Integer.parseInt(data[i][j][HS_START + 1]),
						Integer.parseInt(data[i][j][HS_START + 2]),
						Integer.parseInt(data[i][j][HS_START + 3]));
				
				location = data[i][j][APPLICANT_START + 8] + data[i][j][APPLICANT_START + 9];
				locationMap.putIfAbsent(location, locationMap.size());
				citizenship = new Location(
						locationMap.get(location),
						Integer.parseInt(data[i][j][APPLICANT_START + 8]),
						Integer.parseInt(data[i][j][APPLICANT_START + 9]));
				
				citizenshipInfo = new CitizenshipInfo(
						citizenship,
						Integer.parseInt(data[i][j][APPLICANT_START + 7]));
				
				location = data[i][j][APPLICANT_START + 4] +
						data[i][j][APPLICANT_START + 5] +
						data[i][j][APPLICANT_START + 2] +
						data[i][j][APPLICANT_START + 6] +
						data[i][j][APPLICANT_START + 3];
				locationMap.putIfAbsent(location, locationMap.size());
				residence = new Location(
						locationMap.get(location),
						data[i][j][APPLICANT_START + 4],
						Integer.parseInt(data[i][j][APPLICANT_START + 5]),
						Integer.parseInt(data[i][j][APPLICANT_START + 2]),
						Integer.parseInt(data[i][j][APPLICANT_START + 6]),
						Integer.parseInt(data[i][j][APPLICANT_START + 3]));
				
				birthYear = Integer.parseInt(data[i][j][APPLICANT_START + 1]);
				sexMap.putIfAbsent(data[i][j][APPLICANT_START], sexMap.size());
				tongue = Integer.parseInt(data[i][j][APPLICANT_START + 10]);
				yearsInSchool = Integer.parseInt(data[i][j][SUMMARY_START]);
				currentAvg = Integer.parseInt(data[i][j][SUMMARY_START + 3]);
				totalAvg = Integer.parseInt(data[i][j][SUMMARY_START + 4]);				
				
				applicants[i][j] = new Applicant(applications, courseInfo, confirmation, registration, highschool,
						citizenshipInfo, residence, birthYear, sexMap.get(data[i][j][APPLICANT_START]),
						currentAvg, totalAvg, tongue, yearsInSchool);
			}
		}
		
		for(int i = 0; i < applicants.length; i++){
			applicants[i] = ApplicantFilter.filter(applicants[i]);
		}
		//TODO normalize
	}//loadApplicantData()
	
	public static double[][] getFlattened(){
		double[][] allYears;
		int n = 0, index = 0;
		
		for(int i = 0; i < applicants.length; i++){
			n += applicants[i].length;
		}
		allYears = new double[n][];
		
		for(Applicant[] year : applicants){		//Copy data into a flat array containing all years
			for(int i = 0; i < year.length; i++){
				allYears[index + i] = year[i].flatten();
			}
			index += year.length;
		}
		
		return allYears;
	}//flattenAll()
	
	/**
	 * Normalizes the "columns" of the 2D array of applicant data passed in such that it ends up with a
	 * standard deviation of 1 and a mean of 0.
	 * 
	 * @param applicants - A 2D array of floats to normalize.
	 */
	public static double[][] normalize(){
		double[][] norm = getFlattened();
		long total;
		double mean, sd, n = norm.length;
		
		for(int i = 0; i < 43; i++){						//Perform normalization for single element features
			mean = sd = total = 0;
			if(i == 7){										//Skip courses (not single element features)
				i = 39;
			}
			
			for(int j = 0; j < n; j++){						//Compute mean
				total += norm[j][i];
			}
			mean = total / n;
			
			for(int j = 0; j < n; j++){						//Compute standard deviation
				sd += Math.pow(mean - norm[j][i], 2);
			}
			sd = Math.sqrt(sd / (n - 1));
			
			for(int j = 0; j < n; j++){						//Adjust values
				norm[j][i] = (norm[j][i] - mean) / sd;
			}
		}
		
		for(int i = 0; i < 2; i++){							//Perform normalization on course data
			mean = sd = total = 0;
			for(int j = 15; j < 39; j += 2){				//Normalize both fields
				for(int k = 0; k < n; k++){
					total += norm[k][i + j];
				}
			}
			mean = total / n / 12;
			for(int j = 15; j < 39; j += 2){
				for(int k = 0; k < n; k++){
					sd += Math.pow(mean - norm[k][i + j], 2);
				}
			}
			sd = Math.sqrt(sd / (12 * n - 1));
			for(int j = 15; j < 39; j += 2){
				for(int k = 0; k < n; k++){					//Adjust values
					norm[k][i + j] = (norm[k][i + j] - mean) / sd;
				}
			}
		}
		
		for(int i = 0; i < 11; i++){						//Perform normalization on application data
			mean = sd = total = 0;
			for(int j = 43; j < norm[0].length; j += 11){	//Normalize all fields
				for(int k = 0; k < n; k++){
					total += norm[k][i + j];
				}
			}
			mean = total / n / 20;
			for(int j = 43; j < norm[0].length; j += 11){	
				for(int k = 0; k < n; k++){
					sd += Math.pow(mean - norm[k][i + j], 2);
				}
			}
				sd = Math.sqrt(sd / (20 * n - 1));
			for(int j = 43; j < norm[0].length; j += 11){
				for(int k = 0; k < n; k++){	//Adjust values
					norm[k][i + j] = (norm[k][i + j] - mean) / sd;
				}
			}
		}
		return norm;
	}//normalize()
	
	/**
	 * Alters the values contained within the applicants such that they have a standard deviation of 1
	 * and a mean of 0.<br>
	 * Although this normalize is significantly more complicated and much less pretty, it has the benefit of
	 * 
	 * @param applicants - The 2D array of <code>Applicant</code>s to normalize.
	 */
	public static void normalize(Applicant[][] applicants){
		long schoolTotal = 0, sexTotal = 0, birthYearTotal = 0, residenceTotal = 0, immstatTotal = 0, citizenTotal = 0,
				tongueTotal = 0, yearsInSchoolTotal = 0, courseCountTotal = 0, totalCreditsTotal = 0,
				currentAvgTotal = 0, totalAvgTotal = 0, preferenceTotal = 0, universityTotal = 0, desiredTotal = 0,
				fulltimeTotal = 0, termTotal = 0, majorTotal = 0, coopTotal = 0, yearLevelTotal = 0, codeTotal = 0,
				gradeTotal = 0;
		int count = 0, applicationCount = 0, courseCount = 0;
		double schoolMean, sexMean, birthYearMean, residenceMean, immstatMean, citizenMean, tongueMean,
		yearsInSchoolMean, courseCountMean, totalCreditsMean, currentAvgMean, totalAvgMean, preferenceMean,
		universityMean, desiredMean, fulltimeMean, termMean, majorMean, coopMean, yearLevelMean, codeMean,
		gradeMean,
		schoolSD, sexSD, birthYearSD, residenceSD, immstatSD, citizenSD, tongueSD, yearsInSchoolSD, courseCountSD,
		totalCreditsSD, currentAvgSD, totalAvgSD, preferenceSD, universitySD, desiredSD, fulltimeSD, termSD,
		majorSD, coopSD, yearLevelSD, codeSD, gradeSD;
		
		for(Applicant[] year : applicants){									//Sum up features for all years
			for(Applicant applicant : year){
				schoolTotal += applicant.getHighschool().getID();
				sexTotal += applicant.getSex();
				birthYearTotal += applicant.getBirthYear();
				residenceTotal += applicant.getResidence().getMappedVal();
				immstatTotal += applicant.getCitizenship().getImmigrationStatus();
				citizenTotal += applicant.getCitizenship().getLocation().getMappedVal();
				tongueTotal += applicant.getTongue();
				yearsInSchoolTotal += applicant.getYearsInSchool();
				courseCountTotal += applicant.getCourseInfo().getCourseCount();
				totalCreditsTotal += applicant.getCourseInfo().getTotalCredits();
				currentAvgTotal += applicant.getCurrentAvg();
				totalAvgTotal += applicant.getTotalAvg();
				
				for(Application application : applicant.getApplications()){
					preferenceTotal += application.getPreference();
					universityTotal += application.getProgram().getUniversityID();
					desiredTotal += application.getProgram().getProgramName();
					fulltimeTotal += application.getFullTime();
					termTotal += application.getProgram().getEnrolmentTerm();
					majorTotal += application.getMajor();
					coopTotal += application.getCoop();
					yearLevelTotal += application.getProgram().getYearLevel();
				}
				applicationCount += applicant.getApplications().length;
				
				for(Course course : applicant.getCourseInfo().getCourses()){
					codeTotal += course.getCode();
					gradeTotal += course.getGrade();
				}
				courseCount += applicant.getCourseInfo().getCourses().length;
			}
			count += year.length;
		}
		
		schoolMean = schoolTotal / (double)count;								//Compute means
		sexMean = sexTotal / (double)count;
		birthYearMean = birthYearTotal / (double)count;
		residenceMean = residenceTotal / (double)count;
		immstatMean = immstatTotal / (double)count;
		citizenMean = citizenTotal / (double)count;
		tongueMean = tongueTotal / (double)count;
		yearsInSchoolMean = yearsInSchoolTotal / (double)count;
		courseCountMean = courseCountTotal / (double)count;
		totalCreditsMean = totalCreditsTotal / (double)count;
		currentAvgMean = currentAvgTotal / (double)count;
		totalAvgMean = totalAvgTotal / (double)count;
		preferenceMean = preferenceTotal / (double)applicationCount;
		universityMean = universityTotal / (double)applicationCount;
		desiredMean = desiredTotal / (double)applicationCount;
		fulltimeMean = fulltimeTotal / (double)applicationCount;
		termMean = termTotal / (double)applicationCount;
		majorMean = majorTotal / (double)applicationCount;
		coopMean = coopTotal / (double)applicationCount;
		yearLevelMean = yearLevelTotal / (double)applicationCount;
		codeMean = codeTotal / (double)courseCount;
		gradeMean = gradeTotal / (double)courseCount;
		
		schoolSD = 0;													//Reset sums
		sexSD = 0;
		birthYearSD = 0;
		residenceSD = 0;
		immstatSD = 0;
		citizenSD = 0;
		tongueSD = 0;
		yearsInSchoolSD = 0;
		courseCountSD = 0;
		totalCreditsSD = 0;
		currentAvgSD = 0;
		totalAvgSD = 0;
		preferenceSD = 0;
		universitySD = 0;
		desiredSD = 0;
		fulltimeSD = 0;
		termSD = 0;
		majorSD = 0;
		coopSD = 0;
		yearLevelSD = 0;
		codeSD = 0;
		gradeSD = 0;
		
		for(Applicant[] year: applicants){									//Sum up deviations
			for(Applicant applicant: year){
				schoolSD += Math.pow(applicant.getHighschool().getID() - schoolMean, 2);
				sexSD += Math.pow(applicant.getSex() - sexMean, 2);
				birthYearSD += Math.pow(applicant.getBirthYear() - birthYearMean, 2);
				residenceSD += Math.pow(applicant.getResidence().getMappedVal() - residenceMean, 2);
				immstatSD += Math.pow(applicant.getCitizenship().getImmigrationStatus() - immstatMean, 2);
				citizenSD += Math.pow(applicant.getCitizenship().getLocation().getMappedVal() - citizenMean, 2);
				tongueSD += Math.pow(applicant.getTongue() - tongueMean, 2);
				yearsInSchoolSD += Math.pow(applicant.getYearsInSchool() - yearsInSchoolMean, 2);
				courseCountSD += Math.pow(applicant.getCourseInfo().getCourseCount() - courseCountMean, 2);
				totalCreditsSD += Math.pow(applicant.getCourseInfo().getTotalCredits() - totalCreditsMean, 2);
				currentAvgSD += Math.pow(applicant.getCurrentAvg() - currentAvgMean, 2);
				totalAvgSD += Math.pow(applicant.getTotalAvg() - totalAvgMean, 2);
				
				for(Application application : applicant.getApplications()){
					preferenceSD += Math.pow(application.getPreference() - preferenceMean, 2);
					universitySD += Math.pow(application.getProgram().getUniversityID() - universityMean, 2);
					desiredSD += Math.pow(application.getProgram().getProgramName() - desiredMean, 2);
					fulltimeSD += Math.pow(application.getFullTime() - fulltimeMean, 2);
					termSD += Math.pow(application.getProgram().getEnrolmentTerm() - termMean, 2);
					majorSD += Math.pow(application.getMajor() - majorMean, 2);
					coopSD += Math.pow(application.getCoop() - coopMean, 2);
					yearLevelSD += Math.pow(application.getProgram().getYearLevel() - yearLevelMean, 2);
				}
				
				for(Course course : applicant.getCourseInfo().getCourses()){
					codeSD += Math.pow(course.getCode() - codeMean, 2);
					gradeSD += Math.pow(course.getGrade() - gradeMean, 2);
				}
			}
		}
		schoolSD = (double)Math.sqrt(schoolSD / (double)(count - 1));		//Compute standard deviations
		sexSD = (double)Math.sqrt(sexSD / (double)(count - 1));
		birthYearSD = (double)Math.sqrt(birthYearSD / (double)(count - 1));
		residenceSD = (double)Math.sqrt(residenceSD / (double)(count - 1));
		immstatSD = (double)Math.sqrt(immstatSD / (double)(count - 1));
		citizenSD = (double)Math.sqrt(citizenSD / (double)(count - 1));
		tongueSD = (double)Math.sqrt(tongueSD / (double)(count - 1));
		yearsInSchoolSD = (double)Math.sqrt(yearsInSchoolSD / (double)(count - 1));
		courseCountSD = (double)Math.sqrt(courseCountSD / (double)(count - 1));
		totalCreditsSD = (double)Math.sqrt(totalCreditsSD / (double)(count - 1));
		currentAvgSD = (double)Math.sqrt(currentAvgSD / (double)(count - 1));
		totalAvgSD = (double)Math.sqrt(totalAvgSD / (double)(count - 1));
		preferenceSD = (double)Math.sqrt(preferenceSD / (double)(applicationCount - 1));
		universitySD = (double)Math.sqrt(universitySD / (double)(applicationCount - 1));
		desiredSD = (double)Math.sqrt(desiredSD / (double)(applicationCount - 1));
		fulltimeSD = (double)Math.sqrt(fulltimeSD / (double)(applicationCount - 1));
		termSD = (double)Math.sqrt(termSD / (double)(applicationCount - 1));
		majorSD = (double)Math.sqrt(majorSD / (double)(applicationCount - 1));
		coopSD = (double)Math.sqrt(coopSD / (double)(applicationCount - 1));
		yearLevelSD = (double)Math.sqrt(yearLevelSD / (double)(applicationCount - 1));
		codeSD = (double)Math.sqrt(codeSD / (double)(courseCount - 1));
		gradeSD = (double)Math.sqrt(gradeSD / (double)(courseCount - 1));
		
		for(Applicant[] year: applicants){									//Update values
			for(Applicant applicant: year){
				applicant.getHighschool().setID(
						(applicant.getHighschool().getID() - schoolMean) / schoolSD);
				applicant.setSex(
						(applicant.getSex() - sexMean) / sexSD);
				applicant.setBirthYear(
						(applicant.getBirthYear() - birthYearMean) / birthYearSD);
				applicant.getResidence().setMappedVal(
						(applicant.getResidence().getMappedVal() - residenceMean) / residenceSD);
				applicant.getCitizenship().setImmigrationStatus(
						(applicant.getCitizenship().getImmigrationStatus() - immstatMean) / immstatSD);
				applicant.getCitizenship().getLocation().setMappedVal(
						(applicant.getCitizenship().getLocation().getMappedVal() - citizenMean) / citizenSD);
				applicant.setTongue(
						(applicant.getTongue() - tongueMean)/ tongueSD);
				applicant.setYearsInSchool(
						(applicant.getYearsInSchool() - yearsInSchoolMean) / yearsInSchoolSD);
				applicant.getCourseInfo().setCourseCount(
						(applicant.getCourseInfo().getCourseCount() - courseCountMean) / courseCountSD);
				applicant.getCourseInfo().setTotalCredits(
						(applicant.getCourseInfo().getTotalCredits() - totalCreditsMean) / totalCreditsSD);
				applicant.setCurrentAvg(
						(applicant.getCurrentAvg() - currentAvgMean) / currentAvgSD);
				applicant.setTotalAvg(
						(applicant.getTotalAvg() - totalAvgMean) / totalAvgSD);
				
				for(Application application : applicant.getApplications()){
					application.setPreference(
							(application.getPreference() - preferenceMean) / preferenceSD);
					application.getProgram().setUniversityID(
							(application.getProgram().getUniversityID() - universityMean) / universitySD);
					application.getProgram().setProgramName(
							(application.getProgram().getProgramName() - desiredMean) / desiredSD);
					application.setFullTime(
							(application.getFullTime() - fulltimeMean) / fulltimeSD);
					application.getProgram().setEnrolmentTerm(
							(application.getProgram().getEnrolmentTerm() - termMean) / termSD);
					application.setMajor(
							(application.getMajor() - majorMean) / majorSD);
					application.setCoop(
							(application.getCoop() - coopMean) / coopSD);
					application.getProgram().setYearLevel(
							(application.getProgram().getYearLevel() - yearLevelMean) / yearLevelSD);
				}
				
				for(Course course : applicant.getCourseInfo().getCourses()){
					course.setCode(
							(course.getCode() - codeMean) / codeSD);
					course.setGrade(
							(course.getGrade() - gradeMean) / gradeSD);
				}
			}
		}
	}//normalize()
	
	/**
	 * Returns this class' 2D array of <code>Applicant</code>s.
	 * 
	 * @return This class' 2D array of <code>Applicant</code>s.
	 */
	public static Applicant[][] getApplicants(){ return applicants; }//getApplicants()
	
	private static void readLocalData() throws FileNotFoundException, IllegalStateException{
		Scanner in = new Scanner(new FileReader(new File(localDataFilePath)));
		
		//TODO read logs
		in.close();
	}//readLocalData()
	
	//TODO rename saveModel(location, predictions)
	private static void writeLocalData() throws IOException{
		File f = new File(localDataFilePath);
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		String data = "";
		
		//TODO write logs 
		
		writer.write(data);
		writer.close();
	}//writeLocalData()
	
}//WorkingMemory