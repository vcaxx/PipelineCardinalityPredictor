package es.bsc.caos.pcp;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

class MemoryQModelFunction implements UnivariateRealFunction
{

	/**
	 * 
	 */
	public static int DEFAULT_MEM_BANKS = 4;
//	private static int maxEval = 50000;
	double minMemTime;
//	private double FQ;
	int noMemBanks;
	Pipeline pipe;
//	PipelineSystem pSystem;
	int cards[];
	
	
	/**
	 * @param queueModelSolver
	 */
	public MemoryQModelFunction(MemoryQueueModelSolver queueModelSolver) {
		this.minMemTime = queueModelSolver.minMemTime;
		this.noMemBanks = queueModelSolver.noMemBanks;
		this.pipe = new SWPipeline((SWPipeline)queueModelSolver.pSystem.getPipeline());
		this.cards = new int[queueModelSolver.pSystem.getCardinalities().length];
	    System.arraycopy(queueModelSolver.pSystem.getCardinalities(), 0, cards, 0, queueModelSolver.pSystem.getCardinalities().length);
		
	}
	
	public MemoryQModelFunction(Pipeline pipel, double minMemoT, int[] cards, int noMemBanki)
	{
		this.pipe = pipel;
		this.noMemBanks = noMemBanki;
		this.cards = cards;
		this.minMemTime = minMemoT;
	}
	
	public MemoryQModelFunction(PipelineSystem pSist, double minMemoT, int noMemBanki) {
		this(pSist.getPipeline(), minMemoT, pSist.getCardinalities(), noMemBanki);
	}
	
	public MemoryQModelFunction(Pipeline pipel, double minMemoT, int[] cards)
	{
		this(pipel, minMemoT, cards, DEFAULT_MEM_BANKS);
	}
	
	public MemoryQModelFunction(PipelineSystem pSist, double minMemoT)
	{
		this(pSist.getPipeline(), minMemoT, pSist.getCardinalities());
	}

	@Override
	public double value(double Dm) throws FunctionEvaluationException {
		double M = minMemTime/noMemBanks;
		double res = 1 - (minMemTime/2)/(Dm-minMemTime/2);
		for(int indx=0; indx < pipe.getNumberOfStages(); indx++)
		{
			double numAccesses = pipe.getStageSingleModeMemAccesses(indx);
//			double numAccesses = pipe.getStageMemAccesses(indx);
			int noThreads = cards[indx];
			double constTime = pipe.getSingleModeDuration(indx);
//			double constTime = pipe.getStageDuration(indx);
			constTime -= numAccesses * minMemTime;
//			constTime -= numAccesses * Dm;
			res -= M*noThreads*numAccesses/(constTime+Dm*numAccesses);
		}
		return res;
	}
}