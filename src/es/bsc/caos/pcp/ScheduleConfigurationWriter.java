package es.bsc.caos.pcp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import es.bsc.caos.schedulers.Core;
import es.bsc.caos.schedulers.CoreRoundRobinScheduler;
import es.bsc.caos.schedulers.HWPipe;
import es.bsc.caos.schedulers.Scheduler;
import es.bsc.caos.schedulers.Task;
import es.bsc.caos.schedulers.ThreadToStrandBinding;

public class ScheduleConfigurationWriter {

	static int T2NumThreads = 56;
	static String openHandler = "OPEN_OP";
	static String startPort = "NXGE_10G_START_PORT";
	static String[] defRoles = {"TROLE_ETH_NETIF_RX", 
				"TROLE_APP_IPFWD", "TROLE_ETH_NETIF_TX"};
	static String noRole = "-1";

	static String header = "/*\n" + 
		"* Copyright 2008 Sun Microsystems, Inc.	All rights reserved.\n" + 
		"* Use is subject to license terms.\n" +
		"*/" +  
		"#pragma ident \"@(#)ipfwd_config.c 1.14		08/05/07 SMI\"\n" +
		"#include \"common.h\"\n" +
		"#include \"ipfwd_config.h\"\n" +
		"/*\n" +  
		"* This is the default configuration for NIU when running on LDoms environment.\n" +
		 "* When using the 8 CORE config utilizing 64 CPUs, please ensure the\n"+ 
		 "* ipfwd_swarch.c file enables 40 CPUs.\n"+
		 "* Format: TID, openHandler, PORT, CHAN, ROLE, MEMPOOL, startQRX, #RxQ, startQTx, #TxQ\n"+
		 "*/\n"+ 
		"#define NUM_OF_TCBS 1\n" +
		"ipfwd_thread_attr ipfwd_10g_niu_ldom_def[NUM_OF_TCBS][64] = {\n"+ 
		"{\n";
	
	static String endPlate = "}\n"+
		"};\n"+ 
		"/* NIU Configuration for platform setup for LDoms */\n" + 
		"ipfwd_thread_attr *ipfwd_thread_config = (ipfwd_thread_attr *)&ipfwd_10g_niu_ldom_def[0][0];\n";

	static String zeroLine = "{ XX, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0}";
	static String usedLine = "{ XX, OH, PP, CC, ROL, MPOOL, STINQu, INQC, STOUTQ, OUTQC, PXREPS}";
	
	int noCores;
	int threadsPerCore;
	int pipesPerCore;
	int threadsPerPipe;
	
	public ScheduleConfigurationWriter(SWPSystem ourImprovedSystem) {
		// TODO Auto-generated constructor stub
	}
	
	public void WriteSystemToFile(SWPSystem swpSystem, String filename) throws IOException
	{
		
		//TODO: Scheduler needs number of available cores
		Scheduler sched = new CoreRoundRobinScheduler(7, 2, 4);
		
		ThreadToStrandBinding TSB = sched.getTSB(swpSystem);
		ArrayList<Core> cores = TSB.getCores();

		int coreCount = 0;
		int hPipeCount = 0;
		int pipesPerCore = cores.get(0).getCapacity();
		int tasksPerPipe = cores.get(0).getPipe(0).getCapacity();
		
		String confLines[] = new String[ScheduleConfigurationWriter.T2NumThreads];
		for(int indx=0; indx < T2NumThreads; indx++)
		{
			confLines[indx] = zeroLine.replace("XX", ""+indx);
		}
		
		
		for(Core cor : cores)
		{
			hPipeCount = 0;
			for(HWPipe hPip : cor.getPipes())
			{
				int taskCount = 0;
				for(Task tsk : hPip.getTasks())
				{
					int hwTID = coreCount*pipesPerCore*tasksPerPipe +
							hPipeCount * tasksPerPipe +
							taskCount;
					int inQueue = TSB.getInQueue(tsk);
					int outQueue = TSB.getOutQueue(tsk);
					int inQuCnt = 1;
					int outQuCnt = 1;
					String opHandle = "0";
					if(hwTID == 0)
						opHandle = ScheduleConfigurationWriter.openHandler;
//					String port = TSB.getPort(tsk);
					String port = ScheduleConfigurationWriter.startPort;
					int chan = TSB.getChannel(tsk);
					int mpool = TSB.getMPool(tsk);
					
					int pxReps = tsk.getPxReps();
					
					String tRole = tsk.getID();
					if(tRole.contains("IPFWD") || tRole.contains("PX"))
						tRole = ScheduleConfigurationWriter.defRoles[1];
					else if(tRole.contains("RX"))
					{
						tRole = ScheduleConfigurationWriter.defRoles[0];
						inQueue = outQueue; //TODO: This is how it is codedin IP_FWD
					}
					else if(tRole.contains("TX"))
					{
						tRole = ScheduleConfigurationWriter.defRoles[2];
						outQueue = inQueue;//TODO: Possibly change RX and TX code of IPFWD
						//to use the same queues
					}
					
					confLines[hwTID] = usedLine.replace("XX", "" + hwTID);
					confLines[hwTID] = confLines[hwTID].replace("OH", "" + opHandle);
					confLines[hwTID] = confLines[hwTID].replace("PP", "" + port);
					confLines[hwTID] = confLines[hwTID].replace("CC", "" + chan);
					confLines[hwTID] = confLines[hwTID].replace("ROL", "" + tRole);
					confLines[hwTID] = confLines[hwTID].replace("MPOOL", "" + mpool);
					confLines[hwTID] = confLines[hwTID].replace("STINQu", "" + inQueue);
					confLines[hwTID] = confLines[hwTID].replace("INQC", "" + inQuCnt);
					confLines[hwTID] = confLines[hwTID].replace("STOUTQ", "" + outQueue);
					confLines[hwTID] = confLines[hwTID].replace("OUTQC", "" + outQuCnt);
					confLines[hwTID] = confLines[hwTID].replace("PXREPS", "" + pxReps);
					
					taskCount++;
				}
				hPipeCount++;
			}
			coreCount++;
		}
//		
//		for(String cline : confLines)
//			System.out.println(cline);
		
		File outFile = new File(filename);
		
		Writer out = new OutputStreamWriter(new FileOutputStream(outFile));
		BufferedWriter outBuff = new BufferedWriter(out);
		
		outBuff.write(ScheduleConfigurationWriter.header);
		
		for(int index = 0; index < confLines.length; index++)
		{
			if(index < confLines.length-1)
				confLines[index] += ", ";
			outBuff.write(confLines[index] + "\n");
		}
		
		outBuff.write(ScheduleConfigurationWriter.endPlate);
		outBuff.close();
		out.close();
	}
	
	

}
