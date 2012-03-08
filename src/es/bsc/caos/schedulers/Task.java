package es.bsc.caos.schedulers;

public class Task {
	private String taskID;
	private int inQueue;
	private int outQueue;
	private int memPool;
	private int channel;
	private int repetitions;
	
	Task(String id, int reps){
		taskID = id;
		repetitions = reps;
	}
	
	Task(String id, int reps, int inQueueID, int outQueueID)
	{
		this(id, reps);
		inQueue = inQueueID;
		outQueue = outQueueID;
	}
	
	Task(String id, int reps, int inQueueID, int outQueueID, int chan, int mPool)
	{
		this(id, reps, inQueueID, outQueueID);
		memPool = mPool;
		channel = chan;
	}
	
	public String getID()
	{
		return taskID;
	}
	
	public int getInQueue() {
		return inQueue;
	}

	public void setInQueue(int inQueue) {
		this.inQueue = inQueue;
	}

	public int getOutQueue() {
		return outQueue;
	}

	public void setOutQueue(int outQueue) {
		this.outQueue = outQueue;
	}

	public int getMemPool() {
		return memPool;
	}

	public void setMemPool(int memPool) {
		this.memPool = memPool;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getPxReps() {
		return repetitions;
	}

}
