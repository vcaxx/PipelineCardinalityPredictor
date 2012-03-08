package es.bsc.caos.pcp;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

public class FPQModelFunction implements UnivariateRealFunction {

	double minFPTime;
	SWPipeline pipe;
	int noCores;
	int cards[];
	
	/**
	 * @param FPQModelSolver
	 */
	public FPQModelFunction(FPQueueModelSolver queueModelSolver) {
		this.minFPTime = queueModelSolver.getMinStageDuration();
		this.noCores = queueModelSolver.noCores;
		this.pipe = new SWPipeline((SWPipeline)queueModelSolver.pSystem.getPipeline());
		this.cards = new int[queueModelSolver.pSystem.getCardinalities().length];
	    System.arraycopy(queueModelSolver.pSystem.getCardinalities(), 0, cards, 0, queueModelSolver.pSystem.getCardinalities().length);
		
	}
	
	@Override
	public double value(double Dm) throws FunctionEvaluationException {
		double M = minFPTime/noCores;
		double res = 1 - (minFPTime/2)/(Dm-minFPTime/2);
		for(int indx=0; indx < pipe.getNumberOfStages(); indx++)
		{
			double numAccesses = pipe.getFPAccesses(indx);
//			double numAccesses = pipe.getStageMemAccesses(indx);
			int noThreads = cards[indx];
			double constTime = pipe.getSingleModeDuration(indx);
//			double constTime = pipe.getStageDuration(indx);
			constTime -= numAccesses * minFPTime;
//			constTime -= numAccesses * Dm;
			res -= M*noThreads*numAccesses/(constTime+Dm*numAccesses);
		}
		return res;
	}

}
