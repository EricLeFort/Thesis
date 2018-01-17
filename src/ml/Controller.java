package ml;

import java.io.FileNotFoundException;
import java.io.IOException;

import memory.WorkingMemory;

/**
 *@author Eric Le Fort
 *@version 0.0.1
 */
public abstract class Controller{
		
	public static void main(String[] args){
//		try{
//			setup();
//		}catch(IOException ioe){
//			System.err.println("Issue in setup process.");
//		}
		//TODO handle requests
	}//main()
	
	public static long authenticate(String user, String pass){
		//TODO look up the standard for this using Java
		return 0;
	}//authenticate()
	
	public static void setup(int dimReduceType, int modelType) throws IOException{
		boolean check = false;
		
		//ModelInterface.startLua();//TODO uncomment
		WorkingMemory.loadApplicantData();				//Load data so that it's ready in memory
		
		//TODO check for modelType and dimReduceType
		if(check){						//TODO perform updateModel() if data is stale
			updateModels(dimReduceType, modelType);
		}
	}//setup()
	
	public static void shutdown(){
		try{
			ModelInterface.stop();
		}catch(IOException ioe){
			System.err.println("Unable to communicate halt command to lua program.");
		}
	}//shutdown()
	
	public static void updateModels(int dimReduceType, int modelType) throws IOException{
		ModelInterface.dimReduce(WorkingMemory.getApplicants(), dimReduceType);
		ModelInterface.train(WorkingMemory.getApplicants(), modelType);
		//TODO trigger the current model and predictions to be saved
	}//updateModels()
	
	private static int requestAttendance(double average){
		try{
			return WorkingMemory.predictAttendance(average);
		}catch(FileNotFoundException e){
			System.err.println("Couldn't find input and/or output files.");
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		return -1;
	}//requestAttendance()
	
	private static double requestAverage(int target){
		try{
			return WorkingMemory.predictAverage(target);
		}catch(FileNotFoundException e){
			System.err.println("Couldn't find input and/or output files.");
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		return -1;
	}//requestAverage()
		
}//Controller