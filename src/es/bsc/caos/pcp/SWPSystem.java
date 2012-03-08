/**
 * 
 */
package es.bsc.caos.pcp;

import es.bsc.caos.pcp.SWPSystem.Resource;

/**
 * @author vcakarev
 *
 */
public class SWPSystem implements PipelineSystem {
	
	public enum Resource { MEM, FP, L1Dm, L2Dm, L1Im, L2Im, ATOMIC, COMM };

	private SWPipeline pipe;
	private int cards[];
	
	public SWPSystem(SWPipeline pipe, int[] cardins)
	{
		this.pipe = new SWPipeline(pipe);
		this.cards = new int[cardins.length]; 
		System.arraycopy(cardins, 0, this.cards, 0, cardins.length);
	}
	
	public SWPSystem(SWPipeline pipe)
	{
		this.pipe = new SWPipeline(pipe);
		cards = new int[pipe.getNumberOfStages()];
		for(int i=0; i < pipe.getNumberOfStages(); i++ )
			cards[i] = 1;
	}
	
	public SWPSystem(SWPSystem pSys) {
		this((SWPipeline)pSys.getPipeline(), pSys.getCardinalities());
	}

	
	public void setCardinality(int card, int stageID)
	{
		cards[stageID] = card;
	}
	
	public void setCardinalities(int[] cardins)
	{
		cards = cardins;
	}
	
	
	/* (non-Javadoc)
	 * @see es.bsc.caos.pcp.PipelineSystem#getPipeline()
	 */
	@Override
	public Pipeline getPipeline() {
		return pipe;
	}

	/* (non-Javadoc)
	 * @see es.bsc.caos.pcp.PipelineSystem#getCardinalities()
	 */
	@Override
	public int[] getCardinalities() {
		return cards;
	}

	/* (non-Javadoc)
	 * @see es.bsc.caos.pcp.PipelineSystem#getCardinality(int)
	 */
	@Override
	public int getCardinality(int stageID) {
		return cards[stageID];
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String output = "";
		for(int dur : pipe.getOrderedStageDurations())
			output += dur + " ";
		output += "\n";
		int[] ordIndxs = pipe.getOrderedStageIndexes();
		for(int indx = 0; indx < cards.length; indx++)
			output += cards[ordIndxs[indx]] + " ";
		return output;
	}
//	public String toString() {
//		String output = "";
//		for(int dur : pipe.getStageDurations())
//			output += dur + " ";
//		output += "\n";
//		for(int card : cards)
//			output += card + " ";
//		return output;
//	}

	public int getBottleneckID() {
		int curMin = -1;
		double curDur = Double.MIN_VALUE;
		double tempDur;
		for(int indx = 0; indx < pipe.getNumberOfStages(); indx++)
		{
			tempDur = (double)pipe.getSingleModeDuration(indx)/(double)cards[indx];
			if(tempDur >  curDur)
			{
				curMin = indx;
				curDur = tempDur;
			}
		}
		return curMin;
	}

	public void adjustMemDuration(double oldMemAccessTime, double newMemAccessTime) {
		for(int i = 0; i<pipe.getNumberOfStages(); i++)
		{
			int newDuration = pipe.getStageDuration(i);
			double memAccesses = pipe.getStageMemAccesses(i);
			newDuration += Math.ceil((newMemAccessTime - oldMemAccessTime) * memAccesses); 
			pipe.setStageDuration(i, newDuration);
		}
		return;
		
	}
	
	public void adjustMemDurationVSSingle(double oldMemAccessTime, double newMemAccessTime) {
		for(int i = 0; i<pipe.getNumberOfStages(); i++)
		{
			int newDuration = pipe.getSingleModeDuration(i);
			double memAccesses = pipe.getStageSingleModeMemAccesses(i);
			newDuration += Math.ceil((newMemAccessTime - oldMemAccessTime) * memAccesses); 
			pipe.setStageDuration(i, newDuration);
		}
		return;
		
	}
	
	/*
	 * Updates @arg stageID duration
	 * Gets the number of memory accesses of the stage, and adds the difference between newMemAccessTime and oldMemAccessTime
	 */
	void adjustMemDurationOneStage(double oldMemAccessTime, double newMemAccessTime, int stageID) {
			int newDuration = pipe.getStageDuration(stageID);
			double memAccesses = pipe.getStageMemAccesses(stageID);
			newDuration += Math.ceil((newMemAccessTime - oldMemAccessTime) * memAccesses); 
			pipe.setStageDuration(stageID, newDuration);
		return;
		
	}
	
	void adjustMemDurationOneStageVSSingle(double oldMemAccessTime, double newMemAccessTime, int stageID) {
		int newDuration = pipe.getSingleModeDuration(stageID);
		double memAccesses = pipe.getStageSingleModeMemAccesses(stageID);
		newDuration += Math.ceil((newMemAccessTime - oldMemAccessTime) * memAccesses); 
		pipe.setStageDuration(stageID, newDuration);
	return;
	
}

	
	@Override
	public int[] getOrderedCardinalities() {
		// TODO Needs to be fixed. Add stage order member array in 
		// the pipeline class. Use it here!
		int[] order = pipe.getOrderedStageIndexes();
		int[] ordCards = new int[cards.length];
		int indx = 0;
		for(int ord : order)
			ordCards[indx++] = cards[ord];
		return ordCards;
	}

	public void adjustDurationForCode(Resource resCode, double oldAccessTime,
			double accessTime) {
		switch (resCode) {
		case MEM:
			adjustMemDuration(oldAccessTime, accessTime);
			break;
		case FP:
			adjustFPDuration(oldAccessTime, accessTime);
			break;

		default:
			break;
		}
		// TODO Auto-generated method stub
		
	}

	private void adjustFPDuration(double oldAccessTime, double accessTime) {
		for(int i = 0; i<pipe.getNumberOfStages(); i++)
		{
			int newDuration = pipe.getStageDuration(i);
			double FPAccesses = pipe.getFPAccesses(i);
			newDuration += Math.ceil((accessTime - oldAccessTime) * FPAccesses); 
			pipe.setStageDuration(i, newDuration);
		}
		return;
		
	}

}
