/**
 * 
 */
package es.bsc.caos.pcp;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.solvers.*;

/**
 * @author vcakarev
 *
 */
public class MemoryQueueModelSolver {
	final static int MAX_EVAL = 50000;
	double minMemTime;
	private double maxMemTime;
//	private double FQ;
	int noMemBanks;
	PipelineSystem pSystem;
	
	
	public MemoryQueueModelSolver(double minMemoryTime, double maxMemoryTime, int numberOfMemBanks)
	{
		this.minMemTime = minMemoryTime;
		this.maxMemTime = maxMemoryTime;
//		this.FQ = Freqeuency;
		this.noMemBanks = numberOfMemBanks;
	}
	
	public double getMemAccessTime(PipelineSystem pSys) throws MaxIterationsExceededException, FunctionEvaluationException
	{
		pSystem = new SWPSystem((SWPSystem)pSys);
		
		MemoryQModelFunction f = new MemoryQModelFunction(this);
		BisectionSolver biSolve = new BisectionSolver();
		int totalThreads = 0;
		for(int i=0; i < pSys.getPipeline().getNumberOfStages(); i++)
			totalThreads += pSys.getCardinality(i);
		double upperMemtime = Math.min(maxMemTime, minMemTime * totalThreads);
//		double oldResult = minMemTime;
		double result = upperMemtime;
		
		
		result = biSolve.solve(MAX_EVAL, f, minMemTime, upperMemtime);
		
		return result;
	}
	
	public double checkSolution(double value)
	{
		MemoryQModelFunction f = new MemoryQModelFunction(this);
		try {
			return f.value(value);
		} catch (FunctionEvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -666.666;
		}
	}
	
}
