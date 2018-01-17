package ml;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import memory.Applicant;
import memory.WorkingMemory;

/**
 *@author Eric Le Fort
 *@version 0.0.1
 */
public abstract class ModelInterface{
	private static final String SCRIPT_FILE = "src/scripts/Model.lua",
			outputFile = "resources/out.txt",
			inputFile = "resources/in.txt";
	
	public static void start()
			throws FileNotFoundException, IOException{
		String cmd = "../../../bin/torch/install/bin/th " + SCRIPT_FILE;
		
		Runtime.getRuntime().exec(cmd);
	}//startLua()
	
	public static void stop()
			throws IOException{
		request("done", null);
	}//stopLua()
	
	public static void dimReduce(Applicant[][] applicants, int type)
			throws FileNotFoundException, IOException{
		request("kpca", WorkingMemory.getFlattened());
		getResponse();//TODO do I need to do anything with this?
	}//selectFeatures()
	
	public static void train(Applicant[][] applicants, int type)
			throws FileNotFoundException, IOException{
		train(applicants, type, -1);
	}//train()
	
	public static void train(Applicant[][] applicants, int type, int numModels)
			throws FileNotFoundException, IOException{
		//TODO use the type!
		//TODO Lua end should automatically set up a directory for new models and save
		//at checkpoints
		request("train", WorkingMemory.getFlattened());
		getResponse();//TODO do I need to do anything with this?
	}//train()
	
	public static void validate(Applicant[][] applicants)
			throws FileNotFoundException, IOException{
		request("validate", WorkingMemory.getFlattened());
		//TODO this should happen automatically during training I think
		getResponse();//TODO do I need to do anything with this?
	}//validate
	
	public static double predict(Applicant applicant)
			throws FileNotFoundException, IOException{
		double[][] data = new double[][]{ applicant.flatten() };
		
		request("predict", data);
		
		return Double.valueOf(getResponse());
	}//predict()
	
	private static String getResponse()
			throws IOException{
		BufferedInputStream output = new BufferedInputStream(new FileInputStream(outputFile));
		PrintWriter writer;
		String response = "";
		char next;
		final int SLEEP_CYCLE = 20;
		
		for(;;){
			if(output.available() < 1){								//Wait until output has data
				try{
					Thread.sleep(SLEEP_CYCLE);
				}catch(InterruptedException ie){ /*Do nothing*/ }
			}else{
				next = (char)output.read();
				if(next == '\n'){
					output.close();
					
					writer = new PrintWriter(new File(outputFile));	//Empty file
					writer.write("");
					writer.close();
					return response;
				}
				response += next;
			}
		}
	}//getResponse()
	
	private static void request(String action, double[][] data)
			throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(inputFile)));
		String newline = System.getProperty("line.separator");
		
		bw.write(action);
		bw.write(newline);
		
		if(data == null){						//Allow simple requests
			bw.close();
			return;
		}
		
		for(int i = 0; i < data.length; i++){
			for(int j = 0; j < data[i].length - 1; j++){
				bw.write(data[i][j] + ", ");
			}
			bw.write(data[i][data[i].length - 1] + newline);
			//bw.write("0" + newline);//TODO add actual target here
		}
		
		bw.close();
	}//request()
	
}//ModelInterface