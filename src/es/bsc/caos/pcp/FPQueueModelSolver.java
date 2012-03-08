package es.bsc.caos.pcp;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.solvers.BisectionSolver;

import es.bsc.caos.pcp.SWPSystem.Resource;

public class FPQueueModelSolver extends QueueModelSolver {

	private static final int MAX_EVAL = 5000;
	public static int FPStages = 1;
	public static int FPStageDuration = 33;
	
	SWPSystem pSystem;
	double fpExecTime;
	public int noCores;
	
	public FPQueueModelSolver(int serviceDuration) {
		super(serviceDuration);
		fpExecTime = (double)serviceDuration;
		noCores = 8; // TODO Fix this to architecture parameters!!
	}

	
	
	@Override
	public double getAccessTime(SWPSystem ourImprovedSystem) throws MaxIterationsExceededException, FunctionEvaluationException {
		
		pSystem = new SWPSystem((SWPSystem)ourImprovedSystem);
		
		FPQModelFunction f = new FPQModelFunction(this);
		BisectionSolver biSolve = new BisectionSolver();
		int totalThreads = 0;
		for(int i=0; i < ourImprovedSystem.getPipeline().getNumberOfStages(); i++)
			totalThreads += ourImprovedSystem.getCardinality(i);
		double upperFPTime = 8* fpExecTime;
//		double oldResult = minMemTime;
		double result = upperFPTime;
		
		
		result = biSolve.solve(MAX_EVAL, f, fpExecTime, upperFPTime);
		
		return result;
		
	}

	@Override
	public double getStandardResourceTime() {
		return FPStageDuration;
	}

	@Override
	public double getNumberOfResourcePipStages() {
		return FPStages;
	}

	@Override
	public Resource getResCode() {
		return Resource.FP;
	}



	public double getMinStageDuration() {
		
		return FPStageDuration;
	}

}
