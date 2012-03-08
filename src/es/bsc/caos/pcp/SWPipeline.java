/**
 * 
 */
package es.bsc.caos.pcp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author vcakarev
 *
 */
public class SWPipeline implements Pipeline {
	
	public static String basicStageOrder[] = {"RX", "IPFWD", "TX"};
	private int numStages;
	private int stageDurations[];
	private int originalStageDurations[];
	private double originalL2Misses[];
	private double stageL2Misses[];
	private String stageNames[];
	
	private int lockDurationSingleStage;
	private int lockDuration;
	
	private String stageOrder[];
	private int commCostIn[];
	private int commCostOut[];
	private double[] stageFPinstr;
	private int[] stageReps;
	

	/**
	 * @param numStages The number of stages in the pipeline
	 * @param stageDurations The duration of each stage in cycles
	 * @param stageL2Misses The number of memory accesses per one execution of the stage, for each stage
	 * @param lockDurat The average duration of interstage locking access
	 * @param stageNombres The array containing the names of the stages - important for stage ordering
	 */
	public SWPipeline(int numStages, int[] stageDurations,
			double[] stageL2Misses, int lockDurat, String[] stageNombres,
			double[] stageFpMisses) {
		super();
		
		int ordIndx[] = getOrdIndexes(stageNombres, SWPipeline.basicStageOrder);
		
		
		this.numStages = numStages;
		this.stageDurations = new int[numStages]; 
		
		this.stageNames = new String[numStages];
		this.commCostIn = new int[numStages];
		this.commCostOut = new int[numStages];
		
		this.stageL2Misses = new double[numStages];
		this.originalL2Misses = new double[numStages];
		this.stageOrder = new String[numStages];
		
		
		this.originalStageDurations = new int[numStages];
		this.stageFPinstr = new double[numStages];
		
		lockDuration = lockDurationSingleStage = lockDurat;
		Arrays.fill(commCostIn, lockDurat);
		Arrays.fill(commCostOut, lockDurat);
		
		for(int ind = 0; ind < numStages; ind++)
		{
			int ordIndex = ordIndx[ind];
			this.originalStageDurations[ind] = 
				this.stageDurations[ind] = stageDurations[ordIndex];
			this.stageNames[ind] = stageNombres[ordIndex];
			this.originalL2Misses[ind] = 
				this.stageL2Misses[ind] = stageL2Misses[ordIndex];
			this.stageFPinstr[ind] = stageFpMisses[ordIndex];
			
		}
		
		System.arraycopy(SWPipeline.basicStageOrder, 0, this.stageOrder, 0, SWPipeline.basicStageOrder.length);
	}
	
	public SWPipeline(int numStages, int[] stageDurations,
			double[] stageL2Misses, int commDurat, double[] fpInst)
	{
		this(numStages, stageDurations, stageL2Misses, commDurat, SWPipeline.basicStageOrder, fpInst);
	}

//	/**
//	 * @param numStages The number of stages in the pipeline
//	 */
//	public SWPipeline(int numStages)
//	{
//		super();
//		this.numStages = numStages;
//		stageDurations = new int[numStages];
//		this.originalStageDurations = new int[numStages];
//		stageL2Misses = new double[numStages];
//		this.originalL2Misses = new double[numStages];
//	}
	
	public SWPipeline(SWPipeline pipe) {
		super();
		this.numStages = pipe.numStages;
		this.stageDurations = new int[numStages]; 
		
		this.stageNames = new String[numStages];
		this.commCostIn = new int[numStages];
		this.commCostOut = new int[numStages];
		
		
		this.originalStageDurations = new int[numStages];
		
		this.lockDurationSingleStage = pipe.lockDurationSingleStage;
		this.lockDuration = pipe.lockDuration;
		
		System.arraycopy(pipe.stageDurations, 0, this.stageDurations, 0, numStages);
		System.arraycopy(pipe.originalStageDurations, 0, this.originalStageDurations, 0, numStages);
		System.arraycopy(pipe.stageNames, 0, this.stageNames, 0, numStages);
		System.arraycopy(pipe.commCostIn, 0, this.commCostIn, 0, numStages);
		System.arraycopy(pipe.commCostOut, 0, this.commCostOut, 0, numStages);
		
		this.stageL2Misses = new double[numStages];
		this.originalL2Misses = new double[numStages];
		this.stageOrder = new String[numStages];
		System.arraycopy(pipe.stageL2Misses, 0, this.stageL2Misses, 0, numStages);
		System.arraycopy(pipe.originalL2Misses, 0, this.originalL2Misses, 0, numStages);
		System.arraycopy(pipe.stageOrder, 0, this.stageOrder, 0, numStages);
		
		this.stageFPinstr = new double[numStages];
		
		System.arraycopy(pipe.stageFPinstr, 0, this.stageFPinstr, 0, numStages);
	}

	/**
	 * @param stageID The stage ID
	 * @param stageDuration The duration to be set
	 */
	public void setStageDuration(int stageID, int stageDuration)
	{
		/*originalStageDurations[stageID] = */stageDurations[stageID] = stageDuration;
	}
	
	/**
	 * @param stageID The stage ID
	 * @param stageMisses The memory accesses per execution to be set
	 */
	public void setStageL2Misses(int stageID, double stageMisses)
	{
		/*originalL2Misses[stageID] = */stageL2Misses[stageID]=stageMisses;
	}
	
	
	/**
	 * @return the stageDurations
	 */
	public int[] getStageDurations() {
		return stageDurations;
	}

	/**
	 * @param stageDurations the stageDurations to set
	 */
	public void setStageDurations(int[] stageDurations) {
		System.arraycopy(stageDurations, 0, this.stageDurations, 0, numStages);
//		System.arraycopy(stageDurations, 0, this.originalStageDurations, 0, numStages);
	}

	/**
	 * @return the stageL2Misses
	 */
	public double[] getStageL2Misses() {
		return stageL2Misses;
	}

	/**
	 * @param stageL2Misses the stageL2Misses to set
	 */
	public void setStageL2Misses(double[] stageL2Misses) {
		System.arraycopy(stageL2Misses, 0, this.stageL2Misses, 0, numStages);
//		System.arraycopy(stageL2Misses, 0, this.originalL2Misses, 0, numStages);
	}

	/* (non-Javadoc)
	 * @see es.bsc.caos.pcp.Pipeline#getNumberOfStages()
	 */
	@Override
	public int getNumberOfStages() {
		return numStages;
	}

	/* (non-Javadoc)
	 * @see es.bsc.caos.pcp.Pipeline#getStageDuration(int)
	 */
	@Override
	public int getStageDuration(int stageNumber) {
		return stageDurations[stageNumber];
	}

	/* (non-Javadoc)
	 * @see es.bsc.caos.pcp.Pipeline#getSingleModeDuration(int)
	 */
	@Override
	public int getSingleModeDuration(int stageNumber) {
		return originalStageDurations[stageNumber];
	}

	/* (non-Javadoc)
	 * @see es.bsc.caos.pcp.Pipeline#getStageMemAccesses(int)
	 */
	@Override
	public double getStageMemAccesses(int stageNumber) {
		return stageL2Misses[stageNumber];
	}

	/* (non-Javadoc)
	 * @see es.bsc.caos.pcp.Pipeline#getStageSingleModeMemAccesses(int)
	 */
	@Override
	public double getStageSingleModeMemAccesses(int stageNumber) {
		return originalL2Misses[stageNumber];
	}
	
	public void addToDurationOfStage(int stageID, int durationChange)
	{
		this.stageDurations[stageID] += durationChange;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		String output = "" + numStages + " stages\n";
		for (int dur : stageDurations) {
			output += "" + dur + " ";
		}
		output += "\n";
		for (double mis : stageL2Misses)
		{
			output += "" + mis + " ";
		}
		
		return output;
		
	}

	@Override
	public double[] getMemAccesses() {
		return stageL2Misses;
	}

	
	private int[] getOrdIndexes(String names[], String ordNames[])
	{
		int[] orderedIndexes = new int[names.length];
		
		Set<Integer> usedIndexes = new HashSet<Integer>();
		
		int indx = 0;
		for(; indx < ordNames.length; indx++)
			for(int indx2=0; indx2 < names.length; indx2++)
				if(ordNames[indx].equalsIgnoreCase(names[indx2]))
				{
					orderedIndexes[indx] = indx2;
					usedIndexes.add(indx2);
				}
		
		for(int indx2=0;indx < names.length; indx2++)
		{
			if(!usedIndexes.contains(indx2))
				orderedIndexes[indx++] = indx2;
		}
		return orderedIndexes;
	}
	
	public int[] getOrderedStageIndexes()
	{
		int[] orderedIndexes = new int[stageDurations.length];
//		int[] usedIndexes = new int[SWPipeline.basicStageOrder.length];
//		Arrays.fill(usedIndexes, -1);
		
		Set<Integer> usedIndexes = new HashSet<Integer>();
		
		int indx = 0;
		for(; indx < stageOrder.length; indx++)
			for(int indx2=0; indx2 < stageNames.length; indx2++)
				if(stageOrder[indx].equalsIgnoreCase(stageNames[indx2]))
				{
					orderedIndexes[indx] = indx2;
					usedIndexes.add(indx2);
				}
		
		for(int indx2=0;indx < stageDurations.length; indx2++)
		{
			if(!usedIndexes.contains(indx2))
				orderedIndexes[indx++] = indx2;
		}
		return orderedIndexes;
	}
	
	@Override
	public int[] getOrderedStageDurations() {
		//TODO: This needs to be fixed. Put the order of the stages
		// as the class member
		int[] rezDurs = new int[stageDurations.length];
		int[] orderedIndxs = getOrderedStageIndexes();
		int inIndx = 0;
		for(int index : orderedIndxs)
			rezDurs[index] = stageDurations[inIndx++];
		
		return rezDurs;
	}

	public String[] getStageNames() {
		return stageNames;
	}

	@Override
	public int getSingleModeQueueLockingDuration() {
		return lockDurationSingleStage;
	}

	@Override
	public int getQueueLockingDuration() {
		return lockDuration;
	}
	
	public void setCommunicationDuration(int dur)
	{
		lockDuration = dur;
	}

	@Override
	public int getInCommTime(int indx) {
		return commCostIn[indx];
	}

	@Override
	public int getOutCommTime(int indx) {
		return commCostOut[indx];
	}

	@Override
	public void setInCommDuration(int indx, int newCommInDuration) {
		stageDurations[indx] -= commCostIn[indx];
		commCostIn[indx] = newCommInDuration;
		stageDurations[indx] += commCostIn[indx];
	}

	@Override
	public void setOutCommDuration(int indx, int newCommOutDuration) {
		stageDurations[indx] -= commCostOut[indx];
		commCostOut[indx] = newCommOutDuration;
		stageDurations[indx] += commCostOut[indx];
	}

	public double getFPAccesses(int i) {
		// TODO Auto-generated method stub
		return stageFPinstr[i];
	}

	@Override
	public int[] getStageRepetitions() {
		return stageReps;
	}
	
	

}
