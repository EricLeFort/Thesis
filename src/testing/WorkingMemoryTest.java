package testing;

import static org.junit.Assert.*;

import org.junit.Test;

import memory.WorkingMemory;

/**
 *@author Eric Le Fort
 *@version 1.0
 */
public class WorkingMemoryTest{
	
	@Test
	public void testNormalize(){
		final double DELTA = 1E-9;
		double[][] data = WorkingMemory.normalize();
		//double[][] data = WorkingMemory.getFlattened();
		double sd, sum, n = data.length;
		
		for(int i = 0; i < 43; i++){				//Skip confirmation/registration, they're not normalized
			if(i == 7){
				i = 39;
			}
			
			sd = sum = 0;
			for(int j = 0; j < n; j++){
				sum += data[j][i];
				sd += Math.pow(data[j][i], 2);
			}
			assertEquals(0, sum / n, DELTA);
			assertEquals(1, Math.sqrt(sd / (n - 1)), DELTA);
		}
		
		for(int i = 0; i < 2; i++){				//Courses
			sd = sum = 0;
			for(int j = 15; j < 39; j += 2){	//Get both fields, one at a time
				for(int k = 0; k < n; k++){
					sum += data[k][i + j];
					sd += Math.pow(data[k][i + j], 2);
				}
			}
			assertEquals(0, sum / n / 12, DELTA);
			assertEquals(1, Math.sqrt(sd / (n * 12 - 1)), DELTA);
		}
		
		for(int i = 0; i < 8; i++){				//Applications, skip offers
			sd = sum = 0;
			for(int j = 43; j < 263; j += 11){	//Get all 8 fields, one at a time
				for(int k = 0; k < n; k++){
					sum += data[k][i + j];
					sd += Math.pow(data[k][i + j], 2);
				}
			}
			assertEquals(0, sum / n / 20.0, DELTA);
			assertEquals(1, Math.sqrt(sd / (n * 20.0 - 1)), DELTA);
		}
	}//testNormalize()
	
	@Test
	public void testLoadApplicantData(){
		assertEquals(3979, WorkingMemory.getApplicants()[0].length);
	}//testLoadApplicantData()
	
	@Test
	public void testUpdateModels(){
		fail();
	}//testUpdateModels()
	
	@Test
	public void testPredictAverage(){
		//TODO test functionality
		//TODO test accuracy meets requirements
		fail();
	}//testUpdateModels()
	
	@Test
	public void testAttendance(){
		//TODO test functionality
		//TODO test accuracy meets requirements
		fail();
	}//testAttendance()
	
}//WorkingMemoryTest