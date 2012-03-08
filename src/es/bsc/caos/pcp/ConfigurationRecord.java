package es.bsc.caos.pcp;

public class ConfigurationRecord implements Comparable<ConfigurationRecord> {

	int pid; //processor ID
	int inStartQueueNumber;
	int outStartQueueNumber;
	int noOfInQueues;
	int noOfOutQueus;
	int memPool;
	String port;
	int channel;
	
	public ConfigurationRecord(int PID, int inStNumber, 
			int outStNumber, int memP, int chan) {
		// TODO Auto-generated constructor stub
		inStartQueueNumber = inStNumber;
		outStartQueueNumber = outStNumber;
		noOfInQueues = noOfOutQueus = 1;
		memPool = memP;
		
		port = ScheduleConfigurationWriter.startPort;
		channel = chan;
	}
	
	 //TID, openHandler, PORT, CHAN, ROLE, MEMPOOL, startQRX, #RxQ, startQTx, #TxQ
	@Override
	public int compareTo(ConfigurationRecord o) {
		// TODO Auto-generated method stub
		return this.pid - o.pid;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj.getClass().equals(this.getClass()))
			return ((ConfigurationRecord)obj).pid==this.pid;
		return super.equals(obj);
	}
	
	

}
