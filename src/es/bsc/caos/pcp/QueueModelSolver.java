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
 *
 */
public abstract class QueueModelSolver implements UnivariateRealFunction {

	int serviceDuration;
	int offDur;
	int customerNo;
	double ro;
	/* (non-Javadoc)
	 * @see org.apache.commons.math.analysis.UnivariateRealFunction#value(double)
	 */
	@Override
	public double value(double Dq) throws FunctionEvaluationException {

		double res;
		res = (double)serviceDuration/2;
		res *= 1 + (offDur+Dq)/(offDur + Dq - customerNo*serviceDuration);
		res -= Dq;
		return res;
	}
	
	/**
	 * @param serviceDuration The duration of the service provided 
	 * at the end of the queue. It should be given in cycles. 
	 */
	public QueueModelSolver(int serviceDuration)
	{
		this.serviceDuration = serviceDuration;
	}
	
	/**
	 * @param noCustomers The number of customers using the queue
	 * @param customerOffQueueDuration The duration (in cycles) of the code
	 * outside of the queue
	 * @return The duration of waiting plus service (in cycles)
	 * 
	 */
	public int calculateServiceDuration(int noCustomers, int customerOffQueueDuration) throws MaxIterationsExceededException, FunctionEvaluationException
	{
		offDur = customerOffQueueDuration;
		customerNo = noCustomers;
		
		int reEnterTime = offDur + serviceDuration;
		if(reEnterTime < serviceDuration*noCustomers)
			return noCustomers * serviceDuration;
		
		BisectionSolver bSolver = new BisectionSolver();
		double res = bSolver.solve(MemoryQueueModelSolver.MAX_EVAL, this, serviceDuration-1, serviceDuration*noCustomers+1); 
		return (int)Math.ceil(res);
	}

	public abstract double getAccessTime(SWPSystem ourImprovedSystem) throws MaxIterationsExceededException, FunctionEvaluationException;

	public abstract double getStandardResourceTime();

	public abstract double getNumberOfResourcePipStages();

	public abstract SWPSystem.Resource getResCode();

}
