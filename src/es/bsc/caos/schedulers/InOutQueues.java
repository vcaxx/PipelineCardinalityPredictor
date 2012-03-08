package es.bsc.caos.schedulers;

public class InOutQueues {
	int[] inQueues;
	int[] outQueues;
	int[] cards;
	int[] numOfQueuesAfterStages;
	int totalTasks;
	int[] maxInQSize;
	int[] maxOutQSize;
	
	/*
	 * InOutQueues sets list of communication queues between stages
	 * given by cardinalities @param crds.
	 * The algorithm is that number of queues will be equal to the number
	 * of threads in the stage with less threads.
	 * The stage with more threads will have equal number of threads per queue
	 * And first M queues will have one more thread each - where:
	 * N1>N2; N1%N2 = M; N1/N2 = P
	 * Hence - there will be N2 queues of which first M has P+1 threads on N1 side
	 * And the remaining N1-M has P threads each.
	 * 
	 * The output is given in the form of two arrays of queue ID numbers
	 * The first array is the input queue for the thread, and the second
	 * is the output queue ID.
	 * Input threads have -1 as their input queue ID.
	 * In the same manner output threads have -1 for their output ID.
	 * 
	 * The threads are numbered starting from the first thread in the first
	 * stage card[0], then the second thread in card[0] and so on, until the
	 * end of the first stage, when we start with the first thread in the
	 * second stage. The last thread is the last thread of the last stage.
	 */
	public InOutQueues(int[] crds){
		this.cards = new int[crds.length];
		System.arraycopy(crds, 0, this.cards, 0, crds.length);
		numOfQueuesAfterStages = new int[crds.length];
		totalTasks = 0;
		
		maxInQSize = new int[crds.length];
		maxOutQSize = new int[crds.length];
		
		for(int card : cards)
			totalTasks += card;
		
		inQueues = new int[totalTasks];
		outQueues = new int[totalTasks];
		
		int lastIn = 0;
		int lastOut = 0;
		int nextIn = 0;
		int nextOut = 0;
		for (int qIndex = 0; qIndex < this.cards.length; qIndex++)
		{
//			int P = -1, Q = -1, M = -1, N=-1;
			int inQueueCnt = 0;
			int outQueueCnt = 0;
			
			
			if(qIndex == 0)
			{
				inQueueCnt = cards[qIndex];
				maxInQSize[qIndex] = 1;
				for(int indx = lastIn; indx < lastIn+cards[qIndex]; indx++)
				{
					inQueues[indx] = -1;
				}
			}
			else
			{
				inQueueCnt = numOfQueuesAfterStages[qIndex-1];
				if(inQueueCnt == cards[qIndex])
				{
					maxInQSize[qIndex] = 1;
					for(int indx = lastIn; indx < lastIn+cards[qIndex]; indx++)
						inQueues[indx] = nextIn++;
				}
				else
				{
					int secondIndx = cards[qIndex] / numOfQueuesAfterStages[qIndex-1];
					int firstIndx = secondIndx + 1;
					maxInQSize[qIndex] = firstIndx;
					int borderIndex = firstIndx 
					* (cards[qIndex] % numOfQueuesAfterStages[qIndex-1])
					+ lastIn;
					for(int indx = lastIn; indx < lastIn+cards[qIndex]; indx++)
					{
						inQueues[indx] = nextIn;
						if(indx <  borderIndex)
						{
							if((indx + 1 - lastIn) % firstIndx == 0)
								nextIn++;
						}
						else
							if((indx + 1 - borderIndex) % secondIndx == 0)
								nextIn++;
					}
				}
			}
			lastIn += cards[qIndex];
						
			if(qIndex != cards.length-1)
			{
				outQueueCnt = Math.min(cards[qIndex], cards[qIndex+1]);
				numOfQueuesAfterStages[qIndex] = outQueueCnt;
				if(outQueueCnt == cards[qIndex])
				{
					maxOutQSize[qIndex] = 1;
					for(int indx = lastOut; indx < lastOut+cards[qIndex]; indx++)
						outQueues[indx] = nextOut++;
				}
				else
				{
					int secondIndx = cards[qIndex] / cards[qIndex+1];
					int firstIndx = secondIndx + 1;
					maxOutQSize[qIndex] = firstIndx;
					int borderIndx = firstIndx * (cards[qIndex] % cards[qIndex+1]) 
									+ lastOut;
					for(int indx = lastOut; indx < lastOut + cards[qIndex]; indx++)
					{
						outQueues[indx] = nextOut;
						if(indx < borderIndx)
						{
							if((indx+1-lastOut) % firstIndx == 0)
								nextOut++;
						}
						else
							if((indx+1-borderIndx) % secondIndx == 0)
								nextOut++;
					}
				}
				lastOut += cards[qIndex];
			}
			else
			{
				for(int indx = lastOut; indx < lastOut + cards[cards.length-1]; indx++)
					outQueues[indx] = -1;
				maxOutQSize[qIndex] = 1;
			}
		}
			
		
		
	}
	
	public int[] getInQueues()
	{
		return inQueues;
	}
	
	public int[] getOutQueues()
	{
		return outQueues;
	}

	public int getInQueue(int indx)
	{
		return inQueues[indx];
	}
	
	public int getOutQueue(int indx)
	{
		return outQueues[indx];
	}

	public int[] getMaxInQSizes() {
		return maxInQSize;
	}

	public int[] getMaxOutQSizes() {
		return maxOutQSize;
	}
	
}
