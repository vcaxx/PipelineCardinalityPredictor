package es.bsc.caos.schedulers;

import java.util.ArrayList;

import es.bsc.caos.pcp.PipelineSystem;
import es.bsc.caos.pcp.SWPipeline;

public class CoreRoundRobinScheduler implements Scheduler {

	int noCores;
	int pipesPerCore;
	int strandsPP;
	ArrayList<Core> cores;
	ArrayList<Task> tasks;
	
	public CoreRoundRobinScheduler(int nCores, int pPCore, int stPP) {
		noCores = nCores;
		pipesPerCore = pPCore;
		strandsPP = stPP;
		cores = new ArrayList<Core>(noCores);
		for(int indx = 0; indx < noCores; indx++)
		{
			Core core = new Core(pipesPerCore);
			for(int ind2 = 0; ind2 < pipesPerCore; ind2++)
			{
				HWPipe pipe = new HWPipe(strandsPP);
				core.addPipe(pipe);
			}
			cores.add(core);
		}
		
	}
	
	@Override
	public ThreadToStrandBinding getTSB(PipelineSystem system) {
		int[] cards = system.getOrderedCardinalities();
		int[] order = ((SWPipeline)system.getPipeline()).getOrderedStageIndexes();
		
		ArrayList<Integer> chan = new ArrayList<Integer>();
		ArrayList<Integer> mPools = new ArrayList<Integer>();
		
		String stageNames[] = ((SWPipeline)system.getPipeline()).getStageNames();
//		String stageNames[] = new String[stNamesTemp.length];
		int stageReps[] = system.getPipeline().getStageRepetitions();
//		for(String name : stNamesTemp)
//		{
//			stageNames[order[cnt++]] = name;
//		}
		
		InOutQueues inOutQs = new InOutQueues(cards);
		
		int nextCore = 0;
		int nextPipe = 0;
		
		int memPool = 0;
		int channel = 0;
		
		int taskID = 0;
		
		for(int stageIndx = 0; stageIndx < cards.length; stageIndx++)
		{
			if(stageIndx == cards.length-1)
			{
				memPool = 0;
				channel = 0;
			}
//			currIndx = 0;
//			for(int tempStInd : order)
//				if(tempStInd < stageIndx)
//					currIndx += cards[order[tempStInd]];
			for(int indx = 0; indx < cards[stageIndx]; indx++)
			{
				Task task = new Task(stageNames[order[stageIndx]],
						stageReps[order[stageIndx]],
						inOutQs.inQueues[taskID], 
						inOutQs.outQueues[taskID]);
				
				if(stageIndx == 0)
				{
					chan.add(channel);
					mPools.add(memPool);
					task.setChannel(channel++);
					task.setMemPool(memPool++);
					
				}
				else
				{
//					mPools.add(mPools.get(inOutQs.getInQueue(currIndx)));
					if(stageIndx==cards.length-1)
					{
						chan.add(channel);
						task.setChannel(channel++);
					}
					else
					{
						chan.add(chan.get(inOutQs.getInQueue(taskID)));
						task.setChannel(inOutQs.getInQueue(taskID));
					}
				}
				cores.get(nextCore).getPipe(nextPipe).addTask(task);
				nextCore = (nextCore+1) % noCores;
				if(nextCore == 0)
					nextPipe = (nextPipe+1) % cores.get(0).getCapacity();
				taskID++;
			}
		}
		int[] copyChan = new int[chan.size()];
		int indxCnt = 0;
		for(Integer a : chan)
		{
			copyChan[indxCnt++] = a;
		}
		
		return new ThreadToStrandBinding(cores, inOutQs.getInQueues(), inOutQs.getOutQueues(), copyChan);
	}

}
