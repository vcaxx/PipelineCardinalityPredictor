/**
 * 
 */
package es.bsc.caos.pcp;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.solvers.BisectionSolver;

/**
 * @author vcakarev
 * Takes the values obtained from single pipe profiling of the software pipelining and 
 * extracts the actual duration (in cycles) of memory latency
 */
public class MemoryTimeAdjuster implements UnivariateRealFunction {

	public static final int MAX_CORES = 64;
	private static final double DEFAULT_FQ = 1415*1024*1024;
	private static final double DEFAULT_ACCURACY = 1E-10;
	private static final int DEFAULT_MAX_ITERATION = 35000;
	private double DmMeasured;
	private double lambda;
	private double FQ;
	private double minMicro;
	private double maxMicro;
	private int memBanks;
	
	
	
	/**
	 * @param dmMeasured
	 * @param fQ
	 */
	public MemoryTimeAdjuster(double fQ, int memBanks) {
		super();
		FQ = fQ;
		this.memBanks = memBanks;
		
	}

	public MemoryTimeAdjuster(double fQ)
	{
		this(fQ, MemoryQModelFunction.DEFAULT_MEM_BANKS);
	}
	
	public MemoryTimeAdjuster()
	{
		this(DEFAULT_FQ);
	}


	/* (non-Javadoc)
	 * @see org.apache.commons.math.analysis.UnivariateRealFunction#value(double)
	 */
	@Override
	public double value(double micro) throws FunctionEvaluationException {
		double res = Math.pow(micro, 2) * DmMeasured - micro * (lambda*DmMeasured - FQ) + lambda*FQ/2;
		
		return res;
	}
	
	public double getMemLatencyDuration(Pipeline pipe, double dmMeasured) throws MaxIterationsExceededException, FunctionEvaluationException
	{
		
		DmMeasured = dmMeasured;
		double temp = 0.0;
		double sumAccesses = 0.0;
		for(int i=0; i<pipe.getNumberOfStages(); i++)
		{
			temp += pipe.getStageSingleModeMemAccesses(i)/pipe.getSingleModeDuration(i);
			sumAccesses += pipe.getStageSingleModeMemAccesses(i);
		}
		minMicro = FQ / (dmMeasured * pipe.getNumberOfStages());
		maxMicro = FQ / (dmMeasured *0.9);
		lambda = temp * FQ / memBanks;

		
		BisectionSolver bSolver = new BisectionSolver();
//		bSolver.setAbsoluteAccuracy(DEFAULT_ACCURACY);
//		bSolver.setMaximalIterationCount(DEFAULT_MAX_ITERATION);
//		bSolver.setRelativeAccuracy(MathUtils.SAFE_MIN);
		double micro = bSolver.solve(DEFAULT_MAX_ITERATION, this, minMicro, maxMicro);
		return FQ / micro;
		
	}
	
	

}
