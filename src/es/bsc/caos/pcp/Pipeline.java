/**
 * 
 */
package es.bsc.caos.pcp;

/**
 * @author vcakarev
 *
 */
/**
 * @author vcakarev
 *
 */
public interface Pipeline {
	/**
	 * @return Number of stages in the pipeline
	 */
	int getNumberOfStages();
	
	/**
	 * @param stageNumber - The stage ID
	 * @return The duration of the stage in cycles
	 */
	int getStageDuration(int stageNumber);
	
	/**
	 * @param stageNumber - The stage ID
	 * @return The duration of the stage in cycles, measured in single pipe mode
	 */
	int getSingleModeDuration(int stageNumber);
	
	/**
	 * @param stageNumber  The stage ID
	 * @return The number of the memory accesses (L2 misses) per one execution of the stage
	 */
	double getStageMemAccesses(int stageNumber);
	
	
	/**
	 * @param stageNumber The stage ID
	 * @return The number of the memory accesses per 1 execution in single pipe mode
	 */
	double getStageSingleModeMemAccesses(int stageNumber);
	
	/**
	 * @return Durations of all stages 
	 */
	int[] getStageDurations();
	
	
	/**
	 * @return Number of memory accesses per execution for all stages
	 */
	double[] getMemAccesses();

	/**
	 * @return Durations of all stages in the order stages are used in
	 * the pipeline 
	 */
	int[] getOrderedStageDurations();
	
	/**
	 * 
	 * @return Number of cycles for queue access in single mode
	 */
	int getSingleModeQueueLockingDuration();
	
	/**
	 * 
	 * @return Number of cycles for queue access.
	 */
	int getQueueLockingDuration();

	int getInCommTime(int indx);

	int getOutCommTime(int indx);

	void setInCommDuration(int indx, int newCommInDuration);

	void setOutCommDuration(int indx, int newCommOutDuration);

	int[] getStageRepetitions();

}
