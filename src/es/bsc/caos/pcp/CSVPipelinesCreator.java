package es.bsc.caos.pcp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class CSVPipelinesCreator {
	
	int numOfStages;
	int durations[];
	double l2Misses[];
	int qTime;
	int lockTime;
	String stageNames[];
	double[] fpInstr;
	double[] atomics;
	
	public CSVPipelinesCreator(String csvFile) throws FileNotFoundException, IOException
	{
		
		CSVInputReader reader = new CSVInputReader(csvFile);
		
		List<CSVProfilingRecord> stages = reader.getStages();
		int cnt = 0;
		int len = stages.size();
		durations = new int[len];
		l2Misses = new double[len];
		stageNames = new String[len];
		fpInstr = new double[len];
		atomics = new double[len];
		
		
		double qTimeTotal = 0.0;
		double lockTimeTotal = 0.0;
		for (CSVProfilingRecord stage : stages)
		{
			durations[cnt] = (int)stage.getActiveCPP_casx();
			stageNames[cnt] = stage.gettID();
			fpInstr[cnt] = stage.getFPInstr();
			atomics[cnt] = stage.getAtomics();
			l2Misses[cnt++] = stage.getL2misses_casx();
			qTimeTotal += stage.qTime; /// (stage.enQueus + stage.deQueues);
			qTimeTotal += stage.qTime_casx; /// (stage.enQueus_casx + stage.deQueues_casx);
			lockTimeTotal += stage.lockTime; 
			lockTimeTotal += stage.lockTime_casx;
		}
		qTime = (int)Math.round(qTimeTotal/(len*2));
		lockTime = (int)Math.round(lockTimeTotal/(len*2));
		numOfStages = cnt;
		
	}

	
	public int getNumberOfStages()
	{
		return numOfStages;
	}
	
	public int[] getDurations()
	{
		return durations;
	}
	
	public double[] getL2Misses()
	{
		return l2Misses;
	}
	
	public int getAvgQueueTime()
	{
		return qTime;
	}
	
	public int getAvgLockTime()
	{
		return lockTime;
	}


	public String[] getStageNames() {
		// TODO Auto-generated method stub
		return stageNames;
	}


	public double[] getFPInstr() {
		return fpInstr;
	}
	
	public double[] getAtomics() {
		return atomics;
	}
}
