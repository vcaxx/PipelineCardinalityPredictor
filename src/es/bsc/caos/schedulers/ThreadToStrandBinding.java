package es.bsc.caos.schedulers;

import java.util.List;
import java.util.ArrayList;


public class ThreadToStrandBinding {
	
	ArrayList<Core> cores;
	ArrayList<Task> tasks;
	ArrayList<Integer> inQueues;
	ArrayList<Integer> outQueues;
	ArrayList<Integer> channels;
	
	public ThreadToStrandBinding(List<Core> corss, int[] inQ, 
			int[] outQ, int[] chans)
	{
		cores = new ArrayList<Core>(corss);
		tasks = new ArrayList<Task>();
		inQueues = new ArrayList<Integer>(inQ.length);
		outQueues = new ArrayList<Integer>(outQ.length);
		channels = new ArrayList<Integer>(chans.length);
		for(Core cor : cores)
			for(HWPipe pip : cor.getPipes())
			{
				if(pip!=null)
					for(Task tsk : pip.getTasks())
						if(tsk!=null)
						{
							tasks.add(tsk);
							inQueues.add(tsk.getInQueue());
							outQueues.add(tsk.getOutQueue());
							channels.add(tsk.getChannel());
						}
			}
		
//		for(int inq : inQ)
//			inQueues.add(inq);
		
//		for(int outq : outQ)
//			outQueues.add(outq);
		//TODO: Maybe we would need separate mempool and chan
		
//		for(int ch : chans)
//			channels.add(ch);
		
	}
	
	public int getNoOfUsedCores()
	{
		return cores.size();
	}
	
	public int getNoOfInQueues()
	{
		return inQueues.size();
	}

	public int getNoOfOutQueues()
	{
		return outQueues.size();
	}
	
	
	public List<Task> getTasksAtCore(int coreID)
	{
		ArrayList<Task> rezTasks = new ArrayList<Task>();
		for(HWPipe pip : cores.get(coreID).getPipes())
			rezTasks.addAll(pip.getTasks());
		return rezTasks;
	}

	public ArrayList<Core> getCores() {
		return cores;
	}

	public int getInQueue(Task tsk) {
		
		return inQueues.get(tasks.indexOf(tsk));
	}

	public int getOutQueue(Task tsk) {
		return outQueues.get(tasks.indexOf(tsk));
	}

	public int getChannel(Task tsk) {
		return channels.get(tasks.indexOf(tsk));
	}

	public int getMPool(Task tsk) {
		// TODO Maybe implement a separate list for memory pools
		return channels.get(tasks.indexOf(tsk));
	}
	
	
	
}


