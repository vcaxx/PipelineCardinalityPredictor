package es.bsc.caos.pcp;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;

import es.bsc.caos.pcp.SWPSystem.Resource;

public class CommQueueModelSolver extends QueueModelSolver {

	public CommQueueModelSolver(int serviceDuration) {
		super(serviceDuration);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double getAccessTime(SWPSystem ourImprovedSystem)
			throws MaxIterationsExceededException, FunctionEvaluationException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getStandardResourceTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getNumberOfResourcePipStages() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Resource getResCode() {
		// TODO Auto-generated method stub
		return Resource.COMM;
	}

}
