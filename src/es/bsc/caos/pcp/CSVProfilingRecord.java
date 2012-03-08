package es.bsc.caos.pcp;

public class CSVProfilingRecord {
	
	String tID;
	double CPP;
	double activeCPP;
	
	double CPP_casx;
	double activeCPP_casx;
	
	double L2misses;
	double lockTime;
	double qTime;
	double enQueus;
	double deQueues;
	
	double L2misses_casx;
	double lockTime_casx;
	double qTime_casx;
	double enQueus_casx;
	double deQueues_casx;
	private double fpInstr_casx;
	private double fpInstr;
	private double atomics_casx;
	private double atomics;
	private double ipc_casx;
	
	private double ipc;
	private double instrPP;
	private double instrPP_casx;
	private int stagePos;
	private int stagePos_casx;
		
	public int getStagePos()
	{
		return stagePos;
	}
	
	public int getStagePos_casx()
	{
		return stagePos_casx;
	}
	
	
	public double getFpInstr_casx() {
		return fpInstr_casx;
	}


	public double getFpInstr() {
		return fpInstr;
	}


	public double getIpc_casx() {
		return ipc_casx;
	}


	public double getIpc() {
		return ipc;
	}


	public double getInstrPP() {
		return instrPP;
	}


	public double getInstrPP_casx() {
		return instrPP_casx;
	}


	public CSVProfilingRecord(String thID)
	{
		tID = thID;
	}
	
	
	public CSVProfilingRecord(String tskID_norm, double cpp2, double activeCPP2,
			double l2mPP2, double lockPP2, double qTimePP2, double enQPP2,
			double deQPP2, double fPInstr, double ipc2, double ipp2,
			double atomicPP2, int stagePos2, boolean casxLine) {

		tID = tskID_norm;
		if(casxLine)
		{
			CPP_casx = cpp2;
			activeCPP_casx = activeCPP2;
			L2misses_casx = l2mPP2;
			lockTime_casx = lockPP2;
			qTime_casx = qTimePP2;
			enQueus_casx = enQPP2;
			deQueues_casx = deQPP2;
			fpInstr_casx = fPInstr;
		}
		else
		{
			CPP = cpp2;
			activeCPP = activeCPP2;
			L2misses = l2mPP2;
			lockTime = lockPP2;
			qTime = qTimePP2;
			enQueus = enQPP2;
			deQueues = deQPP2;
			fpInstr = fPInstr;
		}
	}


	public double getCPP() {
		return CPP;
	}
	public void setCPP(double cPP) {
		CPP = cPP;
	}
	public double getActiveCPP() {
		return activeCPP;
	}
	public void setActiveCPP(double activeCPP) {
		this.activeCPP = activeCPP;
	}
	public double getL2misses() {
		return L2misses;
	}
	public void setL2misses(double l2misses) {
		L2misses = l2misses;
	}
	public double getLockTime() {
		return lockTime;
	}
	public void setLockTime(double lockTime) {
		this.lockTime = lockTime;
	}
	public double getqTime() {
		return qTime;
	}
	public void setqTime(double qTime) {
		this.qTime = qTime;
	}
	public double getEnQueus() {
		return enQueus;
	}
	public void setEnQueus(double enQueus) {
		this.enQueus = enQueus;
	}
	public double getDeQueues() {
		return deQueues;
	}
	public void setDeQueues(double deQueues) {
		this.deQueues = deQueues;
	}
	public double getL2misses_casx() {
		return L2misses_casx;
	}
	public void setL2misses_casx(double l2misses_casx) {
		L2misses_casx = l2misses_casx;
	}
	public double getLockTime_casx() {
		return lockTime_casx;
	}
	public void setLockTime_casx(double lockTime_casx) {
		this.lockTime_casx = lockTime_casx;
	}
	public double getqTime_casx() {
		return qTime_casx;
	}
	public void setqTime_casx(double qTime_casx) {
		this.qTime_casx = qTime_casx;
	}
	public double getEnQueus_casx() {
		return enQueus_casx;
	}
	public void setEnQueus_casx(double enQueus_casx) {
		this.enQueus_casx = enQueus_casx;
	}
	public double getDeQueues_casx() {
		return deQueues_casx;
	}
	public void setDeQueues_casx(double deQueues_casx) {
		this.deQueues_casx = deQueues_casx;
	}
	public String gettID() {
		return tID;
	}
	
	public double getCPP_casx() {
		return CPP_casx;
	}


	public void setCPP_casx(double cPP_casx) {
		CPP_casx = cPP_casx;
	}


	public double getActiveCPP_casx() {
		return activeCPP_casx;
	}


	public void setActiveCPP_casx(double activeCPP_casx) {
		this.activeCPP_casx = activeCPP_casx;
	}


	public double getFPInstr() {
		return fpInstr;
	}
	
	public double getFPInstr_casx() {
		return fpInstr_casx;
	}


	public void setFPinst(double fpInstructions) {
		fpInstr = fpInstructions;
		
	}


	public void setFPinst_casx(double fpInstructions) {
		fpInstr_casx = fpInstructions;
		
	}


	public void setAtomics_casx(double atomicsPP) {
		atomics_casx = atomicsPP;
		
	}


	public void setAtomics(double atomicsPP) {
		atomics = atomicsPP;
	}
	
	public double getAtomics_casx()
	{
		return atomics_casx;
	}
	
	public double getAtomics()
	{
		return atomics;
	}


	public void setIPC_casx(double ipc) {
		this.ipc_casx = ipc;
		
	}
	
	public void setIPC(double ipc) {
		this.ipc = ipc;
		
	}
	
	
	public void setInstPP(double instrPP){
		this.instrPP = instrPP;
	}
	
	public void setInstPP_casx(double instrPP){
		this.instrPP_casx = instrPP;
	}


	public void setStagePos(int stagePosition) {
		this.stagePos = stagePosition;
		
	}
	
	public void setStagePos_casx(int stagePosition) {
		this.stagePos_casx = stagePosition;
		
	}
	

}
