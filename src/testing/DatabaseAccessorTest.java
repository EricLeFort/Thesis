package testing;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Test;

import database.DatabaseAccessor;

/**
 *@author Eric Le Fort
 *@version 1.0
 */
public class DatabaseAccessorTest{
	private static final String DB = "advol",
			USER = "root",
			PASS = "1qaZ..2wsX",			//TODO extract to file for safety
			HOST = "jdbc:mysql://localhost:3306/" + DB;
	
	@Test
	public void testAddDataAndQueryForSpecificYear(){
		Connection connection;
		String[][] year;
		
		try{
			connection = DriverManager.getConnection(HOST, USER, PASS);
			connection.setCatalog(DB);
			
			connection.prepareStatement("DELETE FROM Applicant WHERE applicationYear = 9;").executeUpdate();
			assertEquals(0, DatabaseAccessor.query(9).length);
			
			DatabaseAccessor.addData(new File("data/csv/RawData_09.csv"));
			
			year = DatabaseAccessor.query(9);
			assertEquals(3979, year.length);
			assertEquals(392, year[0].length);
		}catch(Exception e){
			fail(e.getMessage());
		}
	}//testAddDataAndQueryForSpecificYear()
	
}//DatabaseAccessorTest