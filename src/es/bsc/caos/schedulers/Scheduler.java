package es.bsc.caos.schedulers;

import es.bsc.caos.pcp.PipelineSystem;

public interface Scheduler {
	public ThreadToStrandBinding getTSB(PipelineSystem system); 

}
