package es.bsc.caos.pcp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;

//import es.bsc.caos.pcp.CSVProfilingRecord;


public class CSVInputReader {
	static String tIDS = "task_id";
	static String cppS = "CPP";
	static String L2mS = "L2MissesPP";
	static String lockS = "LockPA";
	static String qTimeS = "QTimePA";
	static String enQS = "enQueuesPP";
	static String deQS = "deQueueusPP";
	static String activCPPS = "ActiveCPP";
	static String fpInst = "FPointPP";
	static String atomicsField = "AtomicsPP";
	
	static String IPCField = "IPC";
	static String InstrPPField = "InstPP";
	static String StagePositionField = "StagePos";
	
	
	
	CsvReader inputFile;
	
	ArrayList<CSVProfilingRecord> stages;
	
	
	/*
	 * Reads CSV file and saves all records in stages CSVProfilingRecords
	 * CASX and non-CASX records are grouped in one stage.
	 */
	CSVInputReader(String fileName) throws FileNotFoundException, IOException
	{
		//initialization just in case
		double cpp= 0;
		double l2mPP = -0.00000666;
		double lockPP = -0.00000666;
		double qTimePP = -0.00000666;
		double enQPP = -0.00000666;
		double deQPP = -0.00000666;
		double activeCPP = -0.000000066;
		double fpInstructions = -0.00000666;
		double atomicsPP = -0.00000333666;
		
		double ipc = -0.0000000001111;
		double instPP = -0.0000000121212;
		int stagePosition = -1;
		
		stages = new ArrayList<CSVProfilingRecord>();
		inputFile = new CsvReader(fileName, ',', Charset.forName("US-ASCII"));
		inputFile.readHeaders(); //read headers from the first line of file
//		int counter = 0;
		String tskID = "";
		String tskID_norm = "";
		//read all lines of the file into array lists
		while(inputFile.readRecord())
		{
			
			tskID = inputFile.get(tIDS);
			tskID_norm = tskID.replace("_CASX", "");
			boolean alreadyInList = false;
			
//			System.out.println(inputFile.get(cppS) + "  "
//			+ inputFile.get(L2mS) + " " + inputFile.get(lockS) + " "
//			+ inputFile.get(qTimeS) + " " + inputFile.get(enQS) + " "
//			+ inputFile.get(deQS) + " " + inputFile.get(activCPPS));
			
			cpp = (Double.parseDouble(inputFile.get(cppS)));
			l2mPP = (Double.parseDouble(inputFile.get(L2mS)));
			lockPP = (Double.parseDouble(inputFile.get(lockS)));
			qTimePP = (Double.parseDouble(inputFile.get(qTimeS)));
			enQPP = (Double.parseDouble(inputFile.get(enQS)));
			deQPP = (Double.parseDouble(inputFile.get(deQS)));
			activeCPP = (Double.parseDouble(inputFile.get(activCPPS)));
			fpInstructions = (Double.parseDouble(inputFile.get(fpInst)));
			atomicsPP = (Double.parseDouble(inputFile.get(atomicsField)));
			ipc = Double.parseDouble(inputFile.get(IPCField));
			instPP = Double.parseDouble(inputFile.get(InstrPPField));
			stagePosition = (Integer.parseInt(inputFile.get(StagePositionField)));
			
			
			for (CSVProfilingRecord record : stages)
			{
				if(record.gettID().equalsIgnoreCase(tskID_norm))
				{
					if(tskID.contains("CASX"))
					{
						record.setActiveCPP_casx(activeCPP);
						record.setCPP_casx(cpp);
						record.setDeQueues_casx(deQPP);
						record.setEnQueus_casx(enQPP);
						record.setL2misses_casx(l2mPP);
						record.setLockTime_casx(lockPP);
						record.setqTime_casx(qTimePP);
						record.setFPinst_casx(fpInstructions);
						record.setAtomics_casx(atomicsPP);
						
						record.setIPC_casx(ipc);
						record.setInstPP_casx(instPP);
						record.setStagePos_casx(stagePosition);
					}
					else
					{
						record.setActiveCPP(activeCPP);
						record.setCPP(cpp);
						record.setDeQueues(deQPP);
						record.setEnQueus(enQPP);
						record.setL2misses(l2mPP);
						record.setLockTime(lockPP);
						record.setqTime(qTimePP);
						record.setFPinst(fpInstructions);
						record.setAtomics(atomicsPP);
						record.setIPC(ipc);
						record.setInstPP(instPP);
						record.setStagePos(stagePosition);
					}
					alreadyInList = true;
				}
			}
			
			if(!alreadyInList)
			{
				stages.add(new CSVProfilingRecord(tskID_norm, cpp, activeCPP, 
						l2mPP, lockPP, qTimePP, enQPP, deQPP, fpInstructions, 
						ipc, instPP, atomicsPP, stagePosition,
						tskID.contains("CASX")));
				
			}
		}
	}
	
	public List<CSVProfilingRecord> getStages()
	{
		return stages;
	}
	

}
