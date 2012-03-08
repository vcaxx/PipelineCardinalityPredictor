/**
 * 
 */
package es.bsc.caos.pcp;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;

import es.bsc.caos.schedulers.InOutQueues;

/**
 * @author vcakarev
 *
 */
public class PCPredictor {

	private static final double MEM_DURATION_PRECISION = 5E-1;
	public static final long FQ = 1415 * 1024 * 1024;
	public static final int memPipelineStages = 6;
	public static final int fpPipelineStages = 1;
	public static final double memStageTime = 83;
	private static final int PRECISION_ITERATIONS = 3;
	private static final boolean logging = false;
	private static final double FP_DURATION_PRECISION = 5E-1;
	private static final int LONGEST_FP_STAGE_DURATION = 33; //Double precision
	private static final boolean testing = false;
	private static double targetedMaxMemUtilization = 0.90;
	private static double targetedMaxFPUtilization = 0.7;
	private static int noCores = 55;
	private static int memBanks = 4;
	
	
	public static Pipeline[] generatePipelines(int numPipes, int[] pDurations, double[] pMisses)
	{
		
		SWPipeline plines[] = new SWPipeline[numPipes];
		int rDuration[]  = {1154, 1130, 1094, 920}; //This can be moved to args
		int tDuration[] = {1060, 1165, 1382, 1120}; //the same
		int noStages = 3; //the same
		int lockTime = 350;
		double tMisses = 3.0;
		double rMisses = 0.0;

		double misses[][] =  new double[numPipes][noStages];
		int durations[][]  = new int[numPipes][noStages];
		double fpInstr[][] = new double[numPipes][noStages];
		
		for(int i = 0; i<numPipes; i++)
		{
			durations[i][0] = rDuration[i];
			durations[i][noStages-1] = tDuration[i];
			misses[i][0] = rMisses;
			misses[i][noStages-1] = tMisses;
			fpInstr[i][0]=fpInstr[i][1]=fpInstr[i][2] = 1.0;
			
			durations[i][1] = pDurations[i];
			misses[i][1] = pMisses[i];
			
			plines[i] = new SWPipeline(noStages, durations[i], misses[i], lockTime, fpInstr[i]);
		}
		
		return plines;
		
	}
	
	public static double convertToTime(double durationCycles, long FQ)
	{
		return durationCycles / (double)FQ;
	}
	
	public static long convertToPPS(double durationCycles, long FQ)
	{
		return FQ / (long)durationCycles;
	}
	
	/*
	 * Calculate throughput of the pipeline system.
	 * Assumptions are the following:
	 * 1)	All communication and resource sharing side effects are already
	 * 		incorporated in the system via duration of the stages
	 * 2)   If the cardinalities of the two stages are P and Q, and Q>=P:
	 * 		The parallel stages are connected using P queues.
	 * 		From the side with the greater cardinality - Q
	 * 		each queue has  N or N+1 connected threads. Where N = Q/P 
	 * 		The exact number (N or N+1) is decided using the following
	 * 		simple rule: Lets say that M=Q mod P; We will connect N+1
	 * 		Q-side threads to the first M queues, the remaining P-M
	 * 		queues will have N Q-side threads connected to them.
	 * Example: If we have two stages, the first with 20 and the second
	 * 			with 30 threads.
	 * 			P=20, Q=30, N=1, N+1=2, M=10
	 * 			So, we will use 20(P) queues, the first 10(M) queues will have
	 * 			1 producer and 2(N+1)consumers, while the remaining ten 
	 * 			queues will have 1 producer and 1(N) consumer. 
	 */
	public static double calculateThroughput(PipelineSystem pSys)
	{
		int durations[] = pSys.getPipeline().getOrderedStageDurations();
		int cardinalities[] = pSys.getOrderedCardinalities();
		
		
		// 5-7-2 
		//per queue throughput
		double perQueueTP [][] = new double[cardinalities.length-1][];
		for (int qIndex = 0; qIndex < cardinalities.length-1; qIndex++)
		{
//			int P = -1, Q = -1, M = -1, N=-1;
			int inCard = cardinalities[qIndex];
			int outCard = cardinalities[qIndex+1];
			int numOfQueues = Math.max(inCard, outCard);
			int multiThreadsPerQueue = 0; //how many threads per queue
			boolean splitInput = false;
			if(inCard > outCard) //more input than output threads - each in thread has its own queue
			{
				multiThreadsPerQueue = inCard/outCard;
			}
			else //more output threads than in threads - each out thread has its own queue
			{
				multiThreadsPerQueue = outCard/inCard; 
				splitInput = true;
			}
			
			int biggerQueueCount = numOfQueues % Math.min(inCard, outCard); //first few queues have one more thread
			
			perQueueTP[qIndex] = new double[numOfQueues];
			
			double inThrough[] = new double[inCard];
			double outThrough[] = new double[outCard];
			
			
			if (qIndex>0)
			{
				int prevQueueCard = perQueueTP[qIndex-1].length;
				int numToSum = prevQueueCard/inCard;
				int firstPlusOne = prevQueueCard%inCard;
				for(int indexVert = 0; indexVert < inCard; indexVert++)
				{
					inThrough[indexVert] = 0;
					if(prevQueueCard>inCard)
					{
						int startIndex = indexVert*numToSum + Math.min(firstPlusOne, indexVert);
						int endIndex = startIndex + numToSum;
						if(indexVert < firstPlusOne)
							endIndex++;
						for(int indexPrev = startIndex;	indexPrev < endIndex; indexPrev++)
							inThrough[indexVert] += perQueueTP[qIndex-1][indexPrev]; 
						//TODO: Ovde ide suma svih onih koji se ulivaju u ovaj stage
					}
					else
						inThrough[indexVert] = perQueueTP[qIndex-1][indexVert];
				}
			}
			else
			{
				for(int indexVert = 0; indexVert < inCard; indexVert++)
					inThrough[indexVert] = (double)FQ / pSys.getPipeline().getStageDuration(qIndex);
			}
			for(int indexVert = 0; indexVert < outCard; indexVert++)
				outThrough[indexVert] = (double)FQ / pSys.getPipeline().getStageDuration(qIndex+1);
			
			for(int qVertIndex = 0; qVertIndex < numOfQueues; qVertIndex++)
			{
				double aggInThPut = 0.0;
				double aggOutThPut = 0.0;
				
				if(splitInput) //multiple in-threads go to the each queue
				{
					int myInThreadIndex = 0;
					
					if (qVertIndex < (multiThreadsPerQueue+1) * biggerQueueCount)
						myInThreadIndex = qVertIndex / (multiThreadsPerQueue+1);
					else
						myInThreadIndex = biggerQueueCount + 
										(qVertIndex - biggerQueueCount*(multiThreadsPerQueue+1))/ multiThreadsPerQueue;
					
					if(qVertIndex < biggerQueueCount)
						aggInThPut = inThrough[myInThreadIndex] * (multiThreadsPerQueue+1);
					else
						aggInThPut = inThrough[myInThreadIndex] * multiThreadsPerQueue;
					
					aggOutThPut = outThrough[qVertIndex];
				}
				else //either one-to-one or multiple out-threads form each queue
				{
					int myOutThreadIndex = 0;
					
					if (qVertIndex < (multiThreadsPerQueue+1) * biggerQueueCount)
						myOutThreadIndex = qVertIndex / (multiThreadsPerQueue+1);
					else
						myOutThreadIndex = biggerQueueCount + 
										(qVertIndex - biggerQueueCount*(multiThreadsPerQueue+1))/ multiThreadsPerQueue;
					
					if(qVertIndex < biggerQueueCount)
						aggOutThPut = outThrough[myOutThreadIndex] * (multiThreadsPerQueue+1);
					else
						aggOutThPut = inThrough[myOutThreadIndex] * multiThreadsPerQueue;
					
					aggInThPut = inThrough[qVertIndex];
				}
				perQueueTP[qIndex][qVertIndex] = Math.min(aggInThPut, aggOutThPut);
			}
		}
			
		double finalSum = 0.0;
//		System.out.println("PerQueueTP ima " + perQueueTP.length + " nizova u sebi");
//		for (double[] elems : perQueueTP)
//			System.out.println("Per Queue lengths: " + elems.length);
		
		for(double outputs : perQueueTP[perQueueTP.length-1])
			finalSum += outputs;
			
		return	finalSum;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String csvFileName;
		String outFileName = "outConf.c";
		if(args.length<1)
			printUsage();
		csvFileName = args[0];
		if(args.length>1)
			targetedMaxMemUtilization = Double.parseDouble(args[1]);
		if(args.length>2)
			outFileName = args[2];
		else
		{
			outFileName = getFileNameWOExtension(csvFileName);
			outFileName += "_config.c";
		}
		
		
		CSVPipelinesCreator csvPCreator = null;
		try {
			csvPCreator = new CSVPipelinesCreator(csvFileName);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(-1);
		}
		
		if(logging)
		{
		System.out.println("Durations:" );
		for (int a : csvPCreator.getDurations())
			System.out.print(a + ", ");
		
		System.out.println("\nL2Misses: ");
		for (double b : csvPCreator.getL2Misses())
			System.out.print(b + ", ");
		
		System.out.println("\navg queue:" + csvPCreator.getAvgQueueTime() + 
				"   avg lock time: " + csvPCreator.getAvgLockTime());
		
		System.out.println("\navg FP instr: " + csvPCreator.getFPInstr()[0] +
				"; " + csvPCreator.getFPInstr()[1] + "; " +
				csvPCreator.getFPInstr()[2]);
		
		}
		
		Pipeline profiledPipe = new SWPipeline(csvPCreator.getNumberOfStages(), 
				csvPCreator.getDurations(), csvPCreator.getL2Misses(),
				csvPCreator.getAvgLockTime(), csvPCreator.getStageNames(), 
				csvPCreator.getFPInstr());
		
		if(logging)
		{
			for(String stage : ((SWPipeline)profiledPipe).getStageNames())
				System.out.println(" stage: " + stage);
			
			for(int index : ((SWPipeline)profiledPipe).getOrderedStageIndexes())
				System.out.println(" index: " + index);
		
		
			double profiledQTime = csvPCreator.getAvgQueueTime();
			double profiledLockTime = csvPCreator.getAvgLockTime();
		}
		
		if(testing)
		{
			SWPSystem testUtil1 = new SWPSystem((SWPipeline)profiledPipe);
			SWPSystem testUtil2 = new SWPSystem(testUtil1);
			SWPSystem testUtil3 = new SWPSystem(testUtil1);
			SWPSystem testUtil4 = new SWPSystem(testUtil1);
			SWPSystem testUtil5 = new SWPSystem(testUtil1);
			int[] cardsU2 = testUtil2.getCardinalities();
			int[] cardsU3 = testUtil3.getCardinalities();
			int[] cardsU4 = testUtil4.getCardinalities();
			int[] cardsU5 = testUtil5.getCardinalities();
			for(int i=0; i<cardsU2.length; i++)
			{
				cardsU2[i]++;
				cardsU3[i] += 2;
				cardsU4[i] += 3;
				cardsU5[i] += 7;
			}
			testUtil2.setCardinalities(cardsU2);
			testUtil3.setCardinalities(cardsU3);
			testUtil4.setCardinalities(cardsU4);
			testUtil5.setCardinalities(cardsU5);
			
			System.out.println("Utilization in 1 pipe system = " + 
					simulateMemoryAccessTime(testUtil1, memBanks));
			
			System.out.println("Utilization in 2 pipe system = " + 
					simulateMemoryAccessTime(testUtil2, memBanks));
			
			System.out.println("Utilization in 3 pipe system = " + 
					simulateMemoryAccessTime(testUtil3, memBanks));
			
			System.out.println("Utilization in 4 pipe system = " + 
					simulateMemoryAccessTime(testUtil4, memBanks));
			System.out.println("Utilization in 5 pipe system = " + 
					simulateMemoryAccessTime(testUtil5, memBanks));
		}
		//get analyticaly calculated system
		SWPSystem analyticalSystem = (SWPSystem)PCPredictor.CalculateOptimalPSystemLinear(profiledPipe, noCores);
		//TODO: 1 step increment until local minimum system to implement
//		SWPSystem smartsSystem = createSMARTSSystem(profiledPipe, noCores);
		SWPSystem ourImprovedSystem = new SWPSystem(analyticalSystem);

		
		double memUtilization = 0.0;
		double fpUtilization = 0.0;
		int queueDelay = 0;
		
//		System.out.println("FP accessa u Pxu = " + 
//				((SWPipeline)ourImprovedSystem.getPipeline()).getFPAccesses(1));
		
//		ourImprovedSystem.setCardinality(28, 0);
		memUtilization = simulateMemoryAccessTime(ourImprovedSystem, memBanks);
		fpUtilization = simulateFPAccessTime(ourImprovedSystem);
		
		
		
		double primaryUtilization = memUtilization; 
		
//		targetedMaxMemUtilization = 0.55;
		int newNumCores = noCores;
		
		
		while(memUtilization >  targetedMaxMemUtilization
				||
			  fpUtilization > targetedMaxFPUtilization)
		{
			//TODO: Rehaul - ne treba smanjivati bottleneck nego
			// stage sa najvecim uticajem na mem, ako nije botneck
			// Onda, treba proveriti da li je otisao ispod minimalnog 
			// broja threadova, za taj stage (duzi stage, manje niti
			// nego kraci)
//			int botIndex = ourImprovedSystem.getBottleneckID(); 
//			int newCard = ourImprovedSystem.getCardinality(botIndex);
			boolean jumpOut = false;
			newNumCores--;
			
			
			for(int idx = 0; idx < ourImprovedSystem.getCardinalities().length; idx++)
			{
				//exit if we reduced  cardinality too much
				if(newNumCores== 0)
					jumpOut = true;
				ourImprovedSystem.getPipeline().setInCommDuration(idx, csvPCreator.getAvgLockTime());
				ourImprovedSystem.getPipeline().setOutCommDuration(idx, csvPCreator.getAvgLockTime());
			}
			
			if(jumpOut)
				break;
			ourImprovedSystem = (SWPSystem)CalculateOptimalPSystemLinear(ourImprovedSystem.getPipeline(), newNumCores);
//			ourImprovedSystem.setCardinality(newCard, botIndex);
			for(int reps = 0; reps < PRECISION_ITERATIONS; reps++)
			{
				simulateCommunicationQueuesContention(ourImprovedSystem);
				memUtilization = simulateMemoryAccessTime(ourImprovedSystem, memBanks);
				fpUtilization = simulateFPAccessTime(ourImprovedSystem);
			}
			System.out.println("FP utilization  = " + fpUtilization	);
			System.out.println("Mem utilization = " + memUtilization + 
					"\nSystem "  + ourImprovedSystem.toString());
			
			
		}
		//now recalculate optimal system with adjusted memory duration of the pipelines
		//ourImprovedSystem = (SWPSystem)PCPredictor.CalculateOptimalPSystemLinear(ourImprovedSystem.getPipeline(), noCores);
		
		if(logging)
		{
			System.out.println("Analytical system: \n" + analyticalSystem.toString());
			
			System.out.println("Improved system: \n" + ourImprovedSystem.toString());
			
			System.out.println("Analytical system throughput: " + calculateThroughput(analyticalSystem));
			System.out.println("Improved system throughput: " + calculateThroughput(ourImprovedSystem));
			
	//		System.out.println("Original mem delay: " + memStageTime*memPipelineStages);
	//		System.out.println("Adjusted mem delay: " + memAccessTime);
			System.out.println("Memory utilization by original model: " + primaryUtilization);
			System.out.println("Memory utilization by our model: " + memUtilization);
		}
		
		ScheduleConfigurationWriter scheduleWriter = new ScheduleConfigurationWriter(ourImprovedSystem);
		
		
		try{
			scheduleWriter.WriteSystemToFile(ourImprovedSystem, outFileName);
		}
		catch(FileNotFoundException fe)
		{
			fe.printStackTrace();
			System.exit(1);
		}
		catch(IOException ie)
		{
			ie.printStackTrace();
			System.exit(1);
		}
		
		//Analytical model performance calculation:
		for(int reps = 0; reps < PRECISION_ITERATIONS; reps++)
		{
			simulateCommunicationQueuesContention(analyticalSystem);
			simulateMemoryAccessTime(analyticalSystem, memBanks);
		}
		
		System.out.println("The proposed model:\n" + ourImprovedSystem.toString());
		System.out.println("Memory utilization in proposed model\nTargeted: " 
				+ targetedMaxMemUtilization + ", obtained memory utilization: "
				+ memUtilization);
		System.out.println("Throughput in our model: " + calculateThroughput(ourImprovedSystem));
		
		System.out.println("\nThe analytical model:\n" + analyticalSystem.toString());
		System.out.println("Throughput in analytical mode: " + calculateThroughput(analyticalSystem));
		
		
//						}
////						tableSystems[index].adjustMemDuration(memStageTime*memPipelineStages, memAccessTime);
//						tableSystems[index].adjustMemDurationOneStage(memStageTime*memPipelineStages, memAccessTime, 1);
//						
//						int pxDuration = tableSystems[index].getPipeline().getStageDuration(1);
//						QueueModelSolver qSolve = new QueueModelSolver(queueDuration[sysCnt]);
//						int queueingDuration = queueDuration[sysCnt];
//						
//						try {
//							queueingDuration = qSolve.calculateServiceDuration(j+1, pxDuration-2*queueDuration[sysCnt]);
//							queueDelay[index] = queueingDuration;
//	//						System.out.println("New additional queueinq time = " + (queueingDuration - queueDuration[sysCnt]));
//						} catch (MaxIterationsExceededException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						} catch (FunctionEvaluationException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						double stageDifference = tableSystems[index].getPipeline().getStageDuration(2);
//						stageDifference -= (double)tableSystems[index].getCardinality(2)*tableSystems[index].getPipeline().getStageDuration(1)/tableSystems[index].getCardinality(1);
//						
//						int adjustedQueing = Math.max((int)Math.ceil(stageDifference), 0) + queueDuration[sysCnt];
//						
//						((SWPipeline)tableSystems[index].getPipeline()).addToDurationOfStage(1, 2*(queueingDuration - queueDuration[sysCnt]));
//					}

		// TODO: A1) Generate combinations
		// TODO: A2) For each combination evaluate Tmem_req
		// TODO: A3) For each Tmem_req and combination evaluate total throughput
		
		// TODO: B1) Calculate optimal combination, using linear formula, and given durations
		// TODO: B2) Calculate Tmem_req for the calculated combination
		// TODO: B3) Calculate throughput with Tmem_req
		// TODO: B3) Do B1-3 in turn with recalculated Tmem_req, repeat while we are improving
		
//		int stages = 3;
//		cores = 12;
//		int a[][] = getAllCombCardinatilites(cores, stages, 0);
//		
//		for(int i=0; i < a.length; i++)
//		{
//			String output = "[ ";
//			for (int j=0; j<a[i].length; j++)
//				output+= "" + a[i][j] + " ";
//			output += " ]";
//			System.out.println(output);
//		}
//		
//		cores = 128;
//		stages = 10;
//		
//		long startTime = System.currentTimeMillis();
//		a = getAllCombCardinatilites(cores, stages, 0);
//		System.out.println("C = " + cores + ", S = " + stages + " generated in " + (System.currentTimeMillis() - startTime) + "ms");
//		
//		for(int i=0; i < a.length; i+=20)
//		{
//			String output = "[ ";
//			for (int j=0; j<a[i].length; j++)
//				output+= "" + a[i][j] + " ";
//			output += " ]";
//			System.out.println(output);
//		}
//		
//		
//		String output = "[ ";
//		for (int j=0; j<a[a.length-1].length; j++)
//			output+= "" + a[a.length-1][j] + " ";
//		output += " ]";
//		System.out.println(output);
		
		
	}

	/**
	 * @param ourImprovedSystem The system for which we are calculating 
	 * FP access time and utilization
//	 * @param profiledQTime Profiled Queuing time - single pipe mode
//	 * @param memBanks Number of memory banks in the system
	 * @return fpUtilization of the system
	 * Warning! As the intended side-effect this function will change
	 * the memory access duration in all threads in the SWPSystem
	 */
	private static double simulateFPAccessTime(SWPSystem ourImprovedSystem) {
		// TODO Auto-generated method stub
		
		double utilization = 0.0;
//		MemoryQueueModelSolver qSol = new MemoryQueueModelSolver(memStageTime, memStageTime*32, memBanks);
		QueueModelSolver qSol = new FPQueueModelSolver(LONGEST_FP_STAGE_DURATION);
		double accessTime = 0.0;
		double oldAccessTime = Double.MAX_VALUE;
		while(Math.abs(accessTime-oldAccessTime) > FP_DURATION_PRECISION)
			{
			
					oldAccessTime = accessTime;
				try {
					accessTime = qSol.getAccessTime(ourImprovedSystem);
//					System.out.println("Current mem access time = " + memAccessTime);
					utilization = accessTime/qSol.getStandardResourceTime() - 1;
					accessTime *= qSol.getNumberOfResourcePipStages();
					
				} catch (MaxIterationsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FunctionEvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ourImprovedSystem.adjustDurationForCode(qSol.getResCode(), 
						qSol.getStandardResourceTime()*qSol.getNumberOfResourcePipStages(), 
						accessTime);
				
			}
		return utilization;
		
	}
	
	
	/**
	 * @param ourImprovedSystem The system for which we are calculating 
	 * memory access time and utilization
//	 * @param profiledQTime Profiled Queuing time - single pipe mode
	 * @param memBanks Number of memory banks in the system
	 * @return memoryUtilization of the system
	 * Warning! As the intended side-effect this function will change
	 * the memory access duration in all threads in the SWPSystem
	 */
	private static double simulateMemoryAccessTime(SWPSystem ourImprovedSystem,
			/*double profiledQTime, */int memBanks) {
		double memUtilization = 0.0;
		MemoryQueueModelSolver qSol = new MemoryQueueModelSolver(memStageTime, memStageTime*32, memBanks);
		double memAccessTime = 0.0;
		double oldMemAccessTime = Double.MAX_VALUE;
		while(Math.abs(memAccessTime-oldMemAccessTime) > MEM_DURATION_PRECISION)
			{
			
					oldMemAccessTime = memAccessTime;
				try {
					memAccessTime = qSol.getMemAccessTime(ourImprovedSystem);
//					System.out.println("Current mem access time = " + memAccessTime);
					memUtilization = memAccessTime/memStageTime - 1;
					memAccessTime *= memPipelineStages;
					
				} catch (MaxIterationsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FunctionEvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ourImprovedSystem.adjustMemDurationVSSingle(memStageTime*memPipelineStages, memAccessTime);
				
			}
		return memUtilization;
	}

	private static void simulateCommunicationQueuesContention(
			SWPSystem ourImprovedSystem) {
		// TODO Auto-generated method stub
		int lockTime = ourImprovedSystem.getPipeline()
								.getSingleModeQueueLockingDuration();
		QueueModelSolver qSolv = new CommQueueModelSolver(lockTime);
		InOutQueues inOutQ = new InOutQueues(ourImprovedSystem.getOrderedCardinalities());
		
		int noCommDurationsIn[] = ourImprovedSystem.getPipeline().getOrderedStageDurations();
		int noCommDurationsOut[] = ourImprovedSystem.getPipeline().getOrderedStageDurations();
		
		for(int indx=0; indx < noCommDurationsIn.length; indx++)
		{
			noCommDurationsIn[indx] -= ourImprovedSystem.getPipeline().getInCommTime(indx);
			noCommDurationsOut[indx] -= ourImprovedSystem.getPipeline().getOutCommTime(indx);
		}
		
		//TODO: inOutQ treba da za svaki card da prosecan broj niti po redu
		//i da isto tako kaze koja strana je opterecena lockingom
		//onda to iskoristimo da bi izracunali prosecno zadrzavanje u lock-u
		//samo za taj thread koji je opterecen
		// kontam da je bolje ici red po red nego stage po stage
		int maxInQLength[] = inOutQ.getMaxInQSizes();
		int maxOutQLength[] = inOutQ.getMaxOutQSizes();
		for(int indx=0; indx < maxInQLength.length; indx++)
		{
			int newCommInDuration = ourImprovedSystem.getPipeline().getInCommTime(indx);
			
//			System.out.print("OldComm [" + indx + "]= " + newCommInDuration);
//			System.out.println("Stage durations old : " + ourImprovedSystem.toString());
			
			if(maxInQLength[indx] > 1)
			{
				try {
					newCommInDuration = 
						qSolv.calculateServiceDuration(maxInQLength[indx], 
								noCommDurationsIn[indx]);
//					System.out.println(", new = " + newCommInDuration + "\n");
					
				} catch (MaxIterationsExceededException e) {
					e.printStackTrace();
					System.exit(-1);
				} catch (FunctionEvaluationException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
			ourImprovedSystem.getPipeline().setInCommDuration(indx, newCommInDuration);
//			System.out.println("Stage durations: " + ourImprovedSystem.toString());
			
			int newCommOutDuration = ourImprovedSystem.getPipeline().getOutCommTime(indx);
			if(maxOutQLength[indx] > 1)
			{
				try {
					newCommOutDuration = 
						qSolv.calculateServiceDuration(maxOutQLength[indx], 
								noCommDurationsOut[indx]);
				} catch (MaxIterationsExceededException e) {
					e.printStackTrace();
					System.exit(-1);
				} catch (FunctionEvaluationException e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
			ourImprovedSystem.getPipeline().setOutCommDuration(indx, newCommOutDuration);
			
		}
		
		
	}

	/**
	 * @param fullPathFileName
	 * @return
	 */
	private static String getFileNameWOExtension(String fullPathFileName) {
		String outFileName;
		int slashIndx = fullPathFileName.lastIndexOf('/');
		int dotIndx = fullPathFileName.lastIndexOf('.');
		if(dotIndx==-1 || dotIndx < slashIndx)
			outFileName = fullPathFileName.substring(slashIndx+1);
		else
			outFileName = fullPathFileName.substring(slashIndx+1, dotIndx);
		
		return outFileName;
	}

	private static void printUsage() {
		System.out.println("Usage PCPredictor inputProfilingFile [output file]\n" +
				"The standard name is outConf.c");
		
	}



	private static SWPSystem createSMARTSSystem(Pipeline profiledPipe,
			int noCores) {
		// TODO Auto-generated method stub
		return null;
	}


	private static PipelineSystem CalculateOptimalPSystemLinear(
			Pipeline pipeline, int numCores) {
		
		SWPSystem pSys = new SWPSystem((SWPipeline)pipeline);
		
		int totalDur = 0;
		for(int dur : pipeline.getStageDurations())
			totalDur += dur;
		
		int downDur = 0;
		int coresUsed = 0;
		
		for(int indx=0; indx < pipeline.getNumberOfStages(); indx++)
		{
//			downDur = pipeline.getSingleModeDuration(indx) * numCores;
			downDur = pipeline.getStageDuration(indx) * numCores;
			downDur = Math.max((int)Math.floor((double)downDur / (double)totalDur), 1);
			pSys.setCardinality(downDur, indx);
			coresUsed += downDur;
		}
		
//		System.out.println(pSys.toString());
		
		while(coresUsed < numCores)
		{
			int curBotneckID = pSys.getBottleneckID();
			int curBottleneckCard = pSys.getCardinality(curBotneckID);
			pSys.setCardinality(curBottleneckCard+1, curBotneckID);
			coresUsed++;
		}
		
//		System.out.println(pSys.toString());
		
		return pSys;
	}
	
	public static PipelineSystem getAllCombinations(int cores, int stages, int shuffle)
	{
		
		
		return null;
	}
	
	private static int[][] getAllCombCardinatilites(int cores, int stages, int shuffle)
	{
		int combNum = getCombNumber(cores, stages, cores, shuffle);
		int rez[][] = new int[combNum][stages];
		int combID = 0;
		int cStage = 0;
//		int remCores = cores;
//		int startCores;
		int remCoresForStage[] = new int[stages];
		int curCoresForStage[] = new int[stages];
		
		
		remCoresForStage[0] = cores;
		curCoresForStage[0] = cores - stages + 1;
		
		
		while(cStage >= 0)
		{
			if(cStage==stages-1)
			{
//				if(remCores <= maxCoresForStage[cStage])
//				System.out.println("cStage=" + cStage + " curC=" + curCoresForStage[cStage] + " rmC = " + remCoresForStage[cStage]);
				for(int iA = 0; iA < stages; iA++)
					rez[combID][iA] = curCoresForStage[iA];
				combID++;
				cStage--;
				curCoresForStage[cStage]--;
				continue;
			}
			if(remCoresForStage[cStage] == stages-cStage)//minCores
			{
				for(int iA = 0; iA < cStage; iA++)
					rez[combID][iA] = curCoresForStage[iA];
				for(int iA = cStage; iA < stages; iA++)
					rez[combID][iA] = 1;
				cStage--;
				if(cStage >= 0)
					curCoresForStage[cStage]--;
				combID++;
				continue;
			}
			//(curCoresForStage[cStage] < stages - cStage - 1) ||
			if( 
				(curCoresForStage[cStage] < Math.ceil(((double)remCoresForStage[cStage])/(stages-cStage)))
			)
			{
				cStage--;
				if(cStage >= 0)
					curCoresForStage[cStage]--;
				continue;
			}
			
//			rez[combID][cStage] = curCoresForStage[cStage];
			remCoresForStage[cStage+1] = remCoresForStage[cStage] - curCoresForStage[cStage];
			curCoresForStage[cStage+1] = Math.min(remCoresForStage[cStage+1]-stages + cStage + 2, curCoresForStage[cStage]);
			cStage++;
				
			
		}
		
		return rez;
	}
	
	private static int getCombNumber(int cores, int stages, int maxCores, int shuffle)
	{
	    if (stages==1 || cores == 1)
	        if(cores <= maxCores)
	            return 1;
	        else
	            return 0;
	    int start = (int)(Math.ceil((double)cores/stages));
	    if(shuffle != 0)
	        start = Math.max(start-shuffle, 1);
	    int noCombs = 0;
	    for(int i = start; i < Math.min(maxCores+1, cores-stages+2); i++)
	        noCombs += getCombNumber(cores-i, stages-1, i+shuffle, shuffle);
	    return noCombs;
		
	}
	
	
	

}
