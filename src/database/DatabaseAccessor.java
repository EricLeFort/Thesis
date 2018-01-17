package database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This class handles database operations such as querying and adding data to the applicant database. This includes
 * handling prevention of SQL injection attacks.
 * 
 * @author Eric Le Fort
 * @version 0.0.1
 */
public abstract class DatabaseAccessor{
	private static final String[] APPLICANT_TABLES = {
			"HighSchool",
			"Course",
			"University",
			"Applicant",
			"Program",
			"Confirmation",
			"Registration",
			"Grade",
			"Application",
			"Offer"
	};
	private static final String DB = "advol",
			USER = "root",
			PASS = "1qaZ..2wsX",			//TODO extract to file for safety
			HOST = "jdbc:mysql://localhost:3306/" + DB;
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
	
	static{
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}catch(ClassNotFoundException cnfe){
			throw new IllegalStateException("Cannot find the driver in the classpath", cnfe);
		}
	}
	
	/**
	 * Queries the database for all applicant data from the most recent year. The format of the returned array will
	 * be the same as that in <code>query(int)</code>.
	 * 
	 * @return A 2-D <code>String</code> array containing the applicant data queried.
	 */
	public static String[][] query() throws SQLException{
		Connection connection;
		PreparedStatement queryMaxYear;
		ResultSet resultSet;
		String statement = "SELECT MAX(applicationYear) AS currentYear FROM " + APPLICANT_TABLES[3];
		int currentYear;
		
		connection = DriverManager.getConnection(HOST, USER, PASS);
		connection.setCatalog(DB);
		
		queryMaxYear = connection.prepareStatement(statement);			//Query for latest year
		resultSet = queryMaxYear.executeQuery();
		
		resultSet.next();
		currentYear = Integer.parseInt(resultSet.getString("currentYear"));
		
		queryMaxYear.close();
		connection.close();
		return query(currentYear);
	}//query()
	
	/**
	 * Queries the database for all applicant data from the specified year.<br><br>
	 * 
	 * The returned array will hold data in the following format for each applicant:<br>
	 * (REFYR,SCHOOL,BOARD,SBREGN,SBCNTY,SEX,BIRTHD,PRORES,COURES,<br>
	 * HPOST,CUNRES,RGNRES,IMSTAT,CUNCIT,RGNCIT,TONGUE,<br>
	 * CNFUNI,CNFPGM,CNFPGP,CNFYRL,CNFENR,CNFCHS,<br>
	 * REGUNI,REGPGM,REGPGP,REGYRL,REGENR,REGCHS,<br>
	 * CRS1,CRD1,MRK1, ..., CRS12,CRD12,MRK12,<br>
	 * YRSSCH,CNTCRS,TOTCRR,AVERG1,AVERG2,<br>
	 * CHS1,UNI1,PGM1,PGP1,FPT1,ENR1,MAJ1,COP1,YRL1,01-Dec,PGMD1,PGPD1,ENRD1,YRLD1,CNF1,REG1,<br>
	 * ...,<br>
	 * CHS20,UNI20,PGM20,PGP20,FPT20,ENR20,MAJ20,COP20,YRL20,20-Dec,PGMD20,PGPD20,ENRD20,YRLD20,<br>CNF20,REG20)
	 * 
	 * @param year - The year to query data for.
	 * @return A 2-D <code>String</code> array containing the applicant data queried.
	 */
	public static String[][] query(int year) throws SQLException{
		Connection connection;
		PreparedStatement queryApplicants, queryHighschools, queryConfirmations, queryRegistrations, queryGrades,
		queryApplications, queryOffers, queryPrograms, queryCourses;
		ArrayList<String[]> applicants = new ArrayList<String[]>();
		ResultSet applicantSet, resultSet, innerResultSet;
		String[] statements = new String[]{
				"SELECT * FROM " + APPLICANT_TABLES[3] + " WHERE applicationYear = " + year,
				"SELECT * FROM " + APPLICANT_TABLES[0] + " WHERE highschoolID = ?",
				"SELECT * FROM " + APPLICANT_TABLES[5] + " WHERE applicantID = ?",
				"SELECT * FROM " + APPLICANT_TABLES[6] + " WHERE applicantID = ?",
				"SELECT * FROM " + APPLICANT_TABLES[7] + " WHERE applicantID = ?",
				"SELECT * FROM " + APPLICANT_TABLES[8] + " WHERE applicantID = ?",
				"SELECT * FROM " + APPLICANT_TABLES[9] + " WHERE applicantID = ?",
				"SELECT * FROM " + APPLICANT_TABLES[4] + " WHERE universityID = ? AND programCode = ?",
				"SELECT * FROM " + APPLICANT_TABLES[1] + " WHERE courseCode = ?"
		}, row;
		int count;
		
		connection = DriverManager.getConnection(HOST, USER, PASS);
		connection.setCatalog(DB);
		
		queryApplicants = connection.prepareStatement(statements[0]);
		applicantSet = queryApplicants.executeQuery();
		
		queryHighschools = connection.prepareStatement(statements[1]);
		queryConfirmations = connection.prepareStatement(statements[2]);
		queryRegistrations = connection.prepareStatement(statements[3]);
		queryGrades = connection.prepareStatement(statements[4]);
		queryApplications = connection.prepareStatement(statements[5]);
		queryOffers = connection.prepareStatement(statements[6]);
		queryPrograms = connection.prepareStatement(statements[7]);
		queryCourses = connection.prepareStatement(statements[8]);
		
		while(applicantSet.next()){
			row = new String[SEQ_NO_START + 1];										//APPLICANT FIELDS
			row[0] = applicantSet.getString("applicationYear");						//REFYR
			row[APPLICANT_START] = applicantSet.getString("sex");					//SEX
			row[APPLICANT_START + 1] = applicantSet.getString("birthYear");			//BIRTHD
			row[APPLICANT_START + 2] = applicantSet.getString("resProvince");		//PRORES
			row[APPLICANT_START + 3] = applicantSet.getString("resCounty");			//COURES
			row[APPLICANT_START + 4] = applicantSet.getString("postalCode");		//HPOST
			row[APPLICANT_START + 5] = applicantSet.getString("resCountry");		//CUNRES
			row[APPLICANT_START + 6] = applicantSet.getString("resRegion");			//RGNRES
			row[APPLICANT_START + 7] = applicantSet.getString("status");			//IMSTAT
			row[APPLICANT_START + 8] = applicantSet.getString("citCountry");		//CUNCIT
			row[APPLICANT_START + 9] = applicantSet.getString("citRegion");			//RGNCIT
			row[APPLICANT_START + 10] = applicantSet.getString("tongue");			//TONGUE
			row[APPLICANT_START + 11] = "1";										//APPTYP
			row[SUMMARY_START] = applicantSet.getString("yearsInSchool");			//YRSSCH
			row[SUMMARY_START + 1] = applicantSet.getString("numCourses");			//CNTCRS
			row[SUMMARY_START + 2] = applicantSet.getString("credits");				//TOTCRR
			row[SUMMARY_START + 3] = applicantSet.getString("currentAvg");			//AVERG1
			row[SUMMARY_START + 4] = applicantSet.getString("totalAvg");			//AVERG2
			
			queryHighschools.setString(1, applicantSet.getString("highschoolID"));	//HIGHSCHOOL FIELDS
			resultSet = queryHighschools.executeQuery();
			resultSet.next();
			row[HS_START] = resultSet.getString("highschoolID");					//SCHOOL
			row[HS_START + 1] = resultSet.getString("board");						//BOARD
			row[HS_START + 2] = resultSet.getString("region");						//SBREGN
			row[HS_START + 3] = resultSet.getString("county");						//SBCNTY
			
			queryConfirmations.setString(1, applicantSet.getString("applicantID"));	//CONFIRMATION FIELDS
			resultSet = queryConfirmations.executeQuery();
			resultSet.next();
			queryPrograms.setString(1, resultSet.getString("universityID"));
			queryPrograms.setString(2, resultSet.getString("programCode"));
			innerResultSet = queryPrograms.executeQuery();
			innerResultSet.next();
			row[CONF_START] = resultSet.getString("universityID");					//CNFUNI
			row[CONF_START + 1] = resultSet.getString("programCode");				//CNFPGM
			row[CONF_START + 2] = innerResultSet.getString("programGroup");			//CNFPGP
			row[CONF_START + 3] = resultSet.getString("yearLevel");					//CNFYRL
			row[CONF_START + 4] = resultSet.getString("enrollTerm");				//CNFENR
			row[CONF_START + 5] = resultSet.getString("confirmationChoice");		//CNFCHS
			
			queryRegistrations.setString(1, applicantSet.getString("applicantID"));	//REGISTRATION FIELDS
			resultSet = queryRegistrations.executeQuery();
			resultSet.next();
			queryPrograms.setString(1, resultSet.getString("universityID"));
			queryPrograms.setString(2, resultSet.getString("programCode"));
			innerResultSet = queryPrograms.executeQuery();
			innerResultSet.next();
			row[REG_START] = resultSet.getString("universityID");					//REGUNI
			row[REG_START + 1] = resultSet.getString("programCode");				//REGPGM
			row[REG_START + 2] = innerResultSet.getString("programGroup");			//REGPGP
			row[REG_START + 3] = resultSet.getString("yearLevel");					//REGYRL
			row[REG_START + 4] = resultSet.getString("enrollTerm");					//REGENR
			row[REG_START + 5] = resultSet.getString("registrationChoice");			//REGCHS
			
			queryGrades.setString(1, applicantSet.getString("applicantID"));		//GRADES FIELDS
			resultSet = queryGrades.executeQuery();
			
			count = COURSE_START;
			while(resultSet.next()){											//Add stored grade fields
				queryCourses.setString(1, resultSet.getString("courseCode"));
				innerResultSet = queryCourses.executeQuery();
				innerResultSet.next();
				row[count] = resultSet.getString("courseCode");						//CRS
				row[count + 1] = innerResultSet.getString("credits");				//CRD
				row[count + 2] = resultSet.getString("grade");						//MRK
				count += COURSE_COLS;
			}
			for(int i = count; i < SUMMARY_START; i += COURSE_COLS){			//Add empty fields to fill all 12
				row[count] = null;													//CRS/courseCode
				row[count + 1] = null;												//CRD/credits
				row[count + 2] = null;												//MRK/grade
			}
			
			queryApplications.setString(1, applicantSet.getString("applicantID"));	//APPLICATIONS FIELDS
			resultSet = queryApplications.executeQuery();
			
			count = APPLICATION_START;
			while(resultSet.next()){
				queryPrograms.setString(1, resultSet.getString("universityID"));
				queryPrograms.setString(2, resultSet.getString("programCode"));
				innerResultSet = queryPrograms.executeQuery();
				innerResultSet.next();
				row[count] = resultSet.getString("applicationChoice");				//CHS
				row[count + 1] = resultSet.getString("universityID");				//UNI
				row[count + 2] = resultSet.getString("programCode");				//PGM
				row[count + 3] = innerResultSet.getString("programGroup");			//PGP
				row[count + 4] = resultSet.getString("fulltime");					//FPT
				row[count + 5] = resultSet.getString("enrollTerm");					//ENR
				row[count + 6] = resultSet.getString("major");						//MAJ
				row[count + 7] = resultSet.getString("coop");						//COP
				row[count + 8] = resultSet.getString("yearLevel");					//YRL
				row[count + 9] = resultSet.getString("accepted");					//01-Dec
				
				count += APPLICATION_COLS;
			}
			for(int i = count; i < SEQ_NO_START; i += APPLICATION_COLS){		//Add empty fields to fill all 20
				row[count] = null;													//CHS
				row[count + 1] = null;												//UNI
				row[count + 2] = null;												//PGM
				row[count + 3] = null;												//PGP
				row[count + 4] = null;												//FPT
				row[count + 5] = null;												//ENR
				row[count + 6] = null;												//MAJ
				row[count + 7] = null;												//COP
				row[count + 8] = null;												//YRL
				row[count + 9] = null;												//01-Dec
			}
			
			queryApplications.setString(1, applicantSet.getString("applicantID"));	//OFFERS FIELDS
			resultSet = queryApplications.executeQuery();
			
			count = APPLICATION_START;
			while(resultSet.next()){
				queryOffers.setString(1, applicantSet.getString("applicantID"));
				innerResultSet = queryOffers.executeQuery();
				innerResultSet.next();
				row[count + 10] = innerResultSet.getString("programCode");			//PGMD
				row[count + 12] = innerResultSet.getString("enrollTerm");			//ENRD
				row[count + 13] = innerResultSet.getString("yearLevel");			//YRLD
				row[count + 14] = resultSet.getString("isConfirmed");				//CNF
				row[count + 15] = resultSet.getString("isRegistered");				//REG
				queryPrograms.setString(1, innerResultSet.getString("universityID"));
				queryPrograms.setString(2, innerResultSet.getString("programCode"));
				innerResultSet = queryPrograms.executeQuery();
				innerResultSet.next();
				row[count + 11] = innerResultSet.getString("programGroup");			//PGPD
				
				count += APPLICATION_COLS;
			}
			for(int i = count; i < SEQ_NO_START; i += APPLICATION_COLS){		//Add empty fields to fill all 20
				row[count + 10] = null;												//PGMD
				row[count + 11] = null;												//PGPD
				row[count + 12] = null;												//ENRD
				row[count + 13] = null;												//YRLD
				row[count + 14] = null;												//CNF
				row[count + 15] = null;												//REG
			}

			applicants.add(row);
		}
		
		queryHighschools.close();
		queryConfirmations.close();
		queryRegistrations.close();
		queryGrades.close();
		queryApplications.close();
		queryOffers.close();
		queryPrograms.close();
		queryCourses.close();
		queryApplicants.close();

		connection.close();
		return applicants.toArray(new String[0][0]);
	}//query()
	
	/**
	 * Queries the database for applicant data from all years. The format of the returned array will
	 * be similar to that in <code>query(int)</code>.
	 * 
	 * @return A 3-D <code>String</code> array containing all applicant data for all years.
	 */
	public static String[][][] queryAll() throws SQLException{
		Connection connection;
		PreparedStatement queryApplicantYears;
		ResultSet resultSet;
		ArrayList<Integer> years = new ArrayList<Integer>();
		String[][][] results;
		String statement = "SELECT DISTINCT applicationYear FROM " + APPLICANT_TABLES[3];
		
		connection = DriverManager.getConnection(HOST, USER, PASS);		//Determine which years exist in the database
		connection.setCatalog(DB);
		queryApplicantYears = connection.prepareStatement(statement);
		resultSet = queryApplicantYears.executeQuery();
		
		while(resultSet.next()){										//Grab years from query
			years.add(resultSet.getInt("applicationYear"));
		}
		
		results = new String[years.size()][][];
		for(int i = 0; i < years.size(); i++){							//Execute query on each year's data
			results[i] = query(years.get(i));
		}
		
		queryApplicantYears.close();
		connection.close();
		return results;
	}//queryAll()
	
	/**
	 * Parses the applicant data contained by a given .csv file into the database for the year specified.<br><br>
	 * 
	 * The .csv file should follow the format (shown in separate lines for easy viewing,
	 * all this data should be on the same line): <br><br>
	 * (REFYR,SCHOOL,BOARD,SBREGN,SBCNTY,SEX,BIRTHD,PRORES,COURES,<br>
	 * HPOST,CUNRES,RGNRES,IMSTAT,CUNCIT,RGNCIT,TONGUE,APPTYP,<br>
	 * CNFUNI,CNFPGM,CNFPGP,CNFYRL,CNFENR,CNFCHS,<br>
	 * REGUNI,REGPGM,REGPGP,REGYRL,REGENR,REGCHS,<br>
	 * CRS1,CRD1,MRK1, ..., CRS12,CRD12,MRK12,<br>
	 * YRSSCH,CNTCRS,TOTCRR,AVERG1,AVERG2,CHSOVR,<br>
	 * CHS1,UNI1,PGM1,PGP1,FPT1,ENR1,MAJ1,COP1,YRL1,01-Dec,PGMD1,PGPD1,ENRD1,YRLD1,CNF1,REG1,<br>
	 * ...,<br>
	 * CHS20,UNI20,PGM20,PGP20,FPT20,ENR20,MAJ20,COP20,YRL20,20-Dec,PGMD20,PGPD20,ENRD20,YRLD20,<br>CNF20,REG20,<br>
	 * SEQNO)
	 * 
	 * @param file - The .csv file to import data from.
	 */
	public static void addData(File file)
			throws FileNotFoundException, InputMismatchException, SQLException{
		Connection connection;
		ArrayList<String[]> data = new ArrayList<String[]>();
		Scanner in = new Scanner(new FileReader(file));
		PreparedStatement insertHighschool, insertCourse, insertUniversity, insertApplicant, insertProgram,
		insertConfirmation, insertRegistration, insertGrade, insertApplication, insertOffer;
		String[] insertionStatements = new String[]{
				"INSERT IGNORE INTO `" + DB + "`.`" + APPLICANT_TABLES[0]
						+ "` (highSchoolID, board, region, county) VALUES (?,?,?,?)",
				"INSERT IGNORE INTO `" + DB + "`.`" + APPLICANT_TABLES[1]
						+ "` (courseCode, credits,isRequired) VALUES (?,?,?)",
				"INSERT IGNORE INTO `" + DB + "`.`" + APPLICANT_TABLES[2]
						+ "` (universityID, universityName) VALUES (?,?)",
				"INSERT IGNORE INTO `" + DB + "`.`" + APPLICANT_TABLES[3]
						+ "` (applicantID, applicationYear, highschoolID, yearsInSchool, numCourses, credits,"
						+ "currentAvg, totalAvg, sex, postalCode, resCountry, resProvince, resCounty, resRegion,"
						+ "citCountry, citRegion, birthYear, tongue, status)"
						+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				"INSERT IGNORE INTO `" + DB + "`.`" + APPLICANT_TABLES[4]
						+ "` (universityID, programCode, programGroup) VALUES (?,?,?)",
				"INSERT IGNORE INTO `" + DB + "`.`" + APPLICANT_TABLES[5]
						+ "` (applicantID, universityID, programCode, yearLevel, enrollTerm, confirmationChoice)"
						+ " VALUES (?,?,?,?,?,?)",
				"INSERT IGNORE INTO `" + DB + "`.`" + APPLICANT_TABLES[6]
						+ "` (applicantID, universityID, programCode, yearLevel, enrollTerm, registrationChoice)"
						+ "VALUES (?,?,?,?,?,?)",
				"INSERT IGNORE INTO `" + DB + "`.`" + APPLICANT_TABLES[7]
						+ "` (applicantID, courseCode, grade) VALUES (?,?,?)",
				"INSERT IGNORE INTO `" + DB + "`.`" + APPLICANT_TABLES[8]
						+ "` (applicantID, programCode, universityID, applicationChoice, fulltime, enrollTerm, major,"
						+ "coop, yearLevel, accepted, isConfirmed, isRegistered) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
				"INSERT IGNORE INTO `" + DB + "`.`" + APPLICANT_TABLES[9]
						+ "` (applicantID, programCode, universityID, enrollTerm, yearLevel) VALUES (?,?,?,?,?)",
		};
		
		in.nextLine();
		while(in.hasNext()){
			data.add(in.nextLine().split(","));
		}
		in.close();
		
		connection = DriverManager.getConnection(HOST, USER, PASS);
		connection.setCatalog(DB);
		
		insertHighschool = connection.prepareStatement(insertionStatements[0]);
		insertCourse = connection.prepareStatement(insertionStatements[1]);
		insertUniversity = connection.prepareStatement(insertionStatements[2]);
		insertApplicant = connection.prepareStatement(insertionStatements[3]);
		insertProgram = connection.prepareStatement(insertionStatements[4]);
		insertConfirmation = connection.prepareStatement(insertionStatements[5]);
		insertRegistration = connection.prepareStatement(insertionStatements[6]);
		insertGrade = connection.prepareStatement(insertionStatements[7]);
		insertApplication = connection.prepareStatement(insertionStatements[8]);
		insertOffer = connection.prepareStatement(insertionStatements[9]);
		
		for(String[] row : data){													//Convert missing data to NULL
			for(String token : row){
				if(token.equals("")){
					token = "NULL";
				}
			}
		}
		
		for(String[] row : data){													//Insert high-schools
			insertHighschool.setString(1, row[HS_START]);							//highschoolID
			insertHighschool.setString(2, row[HS_START + 1]);						//board
			insertHighschool.setString(3, row[HS_START + 2]);						//region
			insertHighschool.setString(4, row[HS_START + 3]);						//county
			insertHighschool.executeUpdate();
			
			for(int i = COURSE_START; i < SUMMARY_START; i += COURSE_COLS){			//Insert courses
				if(row[i].equals("NULL")){											//Ignore empty courses
					break;
				}
				insertCourse.setString(1, row[i]);									//courseCode
				insertCourse.setString(2, row[i + 1]);								//credits
				insertCourse.setString(3, "NULL");									//isRequired -- may be added later
				insertCourse.executeUpdate();
			}
			
			for(int i = APPLICATION_START; i < SEQ_NO_START; i += APPLICATION_COLS){//Insert universities
				if(row[i + 1].equals("NULL")){										//Ignore empty universities
					break;
				}
				insertUniversity.setString(1, row[i + 1]);							//universityID
				insertUniversity.setString(2, "NULL");								//name -- may be added later
				insertUniversity.executeUpdate();
			}
																					//Insert applicants
			insertApplicant.setString(1, row[SEQ_NO_START]);						//applicantID
			insertApplicant.setString(2, row[0]);									//applicationYear
			insertApplicant.setString(3, row[HS_START]);							//highschoolID
			insertApplicant.setString(4, row[SUMMARY_START]);						//yearsInSchool
			insertApplicant.setString(5, row[SUMMARY_START + 1]);					//numCourses
			insertApplicant.setString(6, row[SUMMARY_START + 2]);					//credits
			insertApplicant.setString(7, row[SUMMARY_START + 3]);					//currentAvg
			insertApplicant.setString(8, row[SUMMARY_START + 4]);					//totalAvg
			insertApplicant.setString(9, row[APPLICANT_START]);						//sex
			insertApplicant.setString(10, row[APPLICANT_START + 4]);				//postalCode
			insertApplicant.setString(11, row[APPLICANT_START + 5]);				//resCountry
			insertApplicant.setString(12, row[APPLICANT_START + 2]);				//resProvince
			insertApplicant.setString(13, row[APPLICANT_START + 3]);				//resCounty
			insertApplicant.setString(14, row[APPLICANT_START + 6]);				//resRegion
			insertApplicant.setString(15, row[APPLICANT_START + 8]);				//citCountry
			insertApplicant.setString(16, row[APPLICANT_START + 9]);				//citRegion
			insertApplicant.setString(17, row[APPLICANT_START + 1]);				//birthYear
			insertApplicant.setString(18, row[APPLICANT_START + 10]);				//tongue
			insertApplicant.setString(19, row[APPLICANT_START + 7]);				//immigrationStatus
			insertApplicant.executeUpdate();
																					//Insert confirmation program
			if(!row[CONF_START].equals("NULL")){									//Ignore empty confirmation
				insertProgram.setString(1, row[CONF_START]);						//universityID
				insertProgram.setString(2, row[CONF_START + 1]);					//programCode
				insertProgram.setString(3, row[CONF_START + 2]);					//programGroup
				insertProgram.executeUpdate();
			}
																					//Insert registration program
			if(!row[REG_START].equals("NULL")){										//Ignore empty registration
				insertProgram.setString(1, row[REG_START]);							//universityID
				insertProgram.setString(2, row[REG_START + 1]);						//programCode
				insertProgram.setString(3, row[REG_START + 2]);						//programGroup
				insertProgram.executeUpdate();
			}
			
			for(int i = APPLICATION_START; i < SEQ_NO_START; i += APPLICATION_COLS){//Insert application/offer programs
				if(row[i + 1].equals("NULL")){										//Ignore empty programs
					break;
				}
				insertProgram.setString(1, row[i + 1]);								//universityID
				insertProgram.setString(2, row[i + 2]);								//programCode
				insertProgram.setString(3, row[i + 3]);								//programGroup
				insertProgram.executeUpdate();
				
				insertProgram.setString(1, row[i + 1]);								//universityID
				insertProgram.setString(2, row[i + 10]);							//programCode
				insertProgram.setString(3, row[i + 11]);							//programGroup
				insertProgram.executeUpdate();
			}
																					//Insert confirmations
			if(!row[CONF_START].equals("NULL")){									//Ignore empty confirmations
				insertConfirmation.setString(1, row[SEQ_NO_START]);					//applicantID
				insertConfirmation.setString(2, row[CONF_START]);					//universityID
				insertConfirmation.setString(3, row[CONF_START + 1]);				//programCode
				insertConfirmation.setString(4, row[CONF_START + 3]);				//yearLevel
				insertConfirmation.setString(5, row[CONF_START + 4]);				//enrollTerm
				insertConfirmation.setString(6, row[CONF_START + 5]);				//confirmationChoice
				insertConfirmation.executeUpdate();
			}
																					//Insert Registrations
			if(!row[REG_START].equals("NULL")){										//Ignore empty registrations
				insertRegistration.setString(1, row[SEQ_NO_START]);					//applicantID
				insertRegistration.setString(2, row[REG_START]);					//universityID
				insertRegistration.setString(3, row[REG_START + 1]);				//programCode
				insertRegistration.setString(4, row[REG_START + 3]);				//yearLevel
				insertRegistration.setString(5, row[REG_START + 4]);				//enrollTerm
				insertRegistration.setString(6, row[REG_START + 5]);				//registrationChoice
				insertRegistration.executeUpdate();
			}
			
			for(int i = COURSE_START; i < SUMMARY_START; i += COURSE_COLS){			//Insert grades
				if(row[i].equals("NULL")){											//Ignore empty grades
					break;
				}
				insertGrade.setString(1, row[SEQ_NO_START]);						//applicantID
				insertGrade.setString(2, row[i]);									//courseCode
				insertGrade.setString(3, row[i + 2]);								//grade
				insertGrade.executeUpdate();
			}
			
			for(int i = APPLICATION_START; i < SEQ_NO_START; i += APPLICATION_COLS){//Insert applications
				if(row[i].equals("NULL")){											//Ignore empty applications
					break;
				}
				insertApplication.setString(1, row[SEQ_NO_START]);					//applicantID
				insertApplication.setString(2, row[i + 2]);							//programCode
				insertApplication.setString(3, row[i + 1]);							//universityID
				insertApplication.setString(4, row[i]);								//applicationChoice
				insertApplication.setString(5, row[i + 4]);							//fulltime
				insertApplication.setString(6, row[i + 5]);							//enrollTerm
				insertApplication.setString(7, row[i + 6]);							//major
				insertApplication.setString(8, row[i + 7]);							//coop
				insertApplication.setString(9, row[i + 8]);							//yearLevel
				insertApplication.setString(10, row[i + 9]);						//accepted
				insertApplication.setString(11, row[i + 14]);						//isConfirmed
				insertApplication.setString(12, row[i + 15]);						//isRegistered
				insertApplication.executeUpdate();
				
				if(!row[i + 10].equals("NULL")){									//Ignore empty offers
					insertOffer.setString(1, row[SEQ_NO_START]);					//Insert offers
					insertOffer.setString(2, row[i + 10]);							//programCode
					insertOffer.setString(3, row[i + 1]);							//universityID
					insertOffer.setString(4, row[i + 12]);							//enrollTerm
					insertOffer.setString(5, row[i + 13]);							//yearLevel
					insertOffer.executeUpdate();
				}
			}
		}
		insertHighschool.close();
		insertCourse.close();
		insertUniversity.close();
		insertApplicant.close();
		insertConfirmation.close();
		insertRegistration.close();
		insertProgram.close();
		insertGrade.close();
		insertApplication.close();
		insertOffer.close();
		
		connection.close();
		
		//TODO delete stale models
		//WorkingMemory.deleteModels();
	}//addData()
	
}//DatabaseAccessor