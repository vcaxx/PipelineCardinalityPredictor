package es.bsc.caos.pcp;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SWPipelineTest {

	static double delta = 0.000000001;
	
	SWPipeline sTestPipe;
	
	int sNumStages = 5;
	int sDurations[] = {200, 1500, 1500, 460, 701};
	double sMises[] = {0.0, 12.0, 3.333, 4.01, 0.0};
	int sLockDur = 200;
	String sNombres[] = {"Ars", "Ders", "DD", "Teex", "Bh"};
	double sFpMisses[] = {0.0, 0.0, 0.0, 0.0, 15.0};
	int sStOrder[] = {1, 2, 4, 0, 3};
	
	int sOrderedDurations[] = {460, 200, 1500, 701, 1500};
	double sOrderdMisses[] = {4.01, 0.0, 12.0, 0.0, 3.333};
	double sOrderdFPMisses[] = {0.0, 0.0, 0.0, 15.0, 0.0};
	
	@Before
	public void setUp() throws Exception {
		sTestPipe = new SWPipeline(sNumStages, sDurations, sMises, 
				sLockDur, sNombres, sFpMisses, sStOrder);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSWPipelineIntIntArrayDoubleArrayIntStringArrayDoubleArray() {
		int numStages1 = 3;
		int durations1[] = {100, 200, 300};
		double mises1[] = {0.01, 4.12, 0.9};
		int lockDur1 = 3;
		String nombres1[] = {"Px", "Px", "Tx"};
		double fpMisses1[] = {0.0, 0.1, 3.1};
		int stOrder1[] = {0, 1, 2};
		
		int numStages2 = 5;
		int durations2[] = {20, 150, 15, 46, 71};
		double mises2[] = {0.0, 0.0, 0.0, 0.0, 0.0};
		int lockDur2 = 2;
		String nombres2[] = {"Ars", "Ders", "DD", "Teex", "Bh"};
		double fpMisses2[] = {0.0, 0.0, 0.0, 0.0, 15.0};
		int stOrder2[] = {1, 2, 3, 0, 4};
		
		int numStages3 = 1;
		int durations3[] = {1000};
		double mises3[] = {1.9};
		int lockDur3 = 0;
		String nombres3[] = {"030"};
		double fpMisses3[] = {0.0};
		int stOrder3[] = {0};
		
		SWPipeline pipe1 = new SWPipeline(numStages1, durations1, 
				mises1, lockDur1, nombres1, fpMisses1,stOrder1);
		SWPipeline pipe2 = new SWPipeline(numStages2, durations2, 
				mises2, lockDur2, nombres2, fpMisses2, stOrder2);
		SWPipeline pipe3 = new SWPipeline(numStages3, durations3, 
				mises3, lockDur3, nombres3, fpMisses3, stOrder3);
		
		assertEquals(numStages1, pipe1.getNumberOfStages());
		assertEquals(numStages2, pipe2.getNumberOfStages());
		assertEquals(numStages3, pipe3.getNumberOfStages());
		
		assertEquals(lockDur1, pipe1.getQueueLockingDuration());
		assertEquals(lockDur2, pipe2.getQueueLockingDuration());
		assertEquals(lockDur3, pipe3.getQueueLockingDuration());
			
	}

	@Test
	public void testSWPipelineIntIntArrayDoubleArrayIntDoubleArray() {
		
		int noStages  = 4;
		int stageDur[] = {1245, 3422, 2322, 333};
		double l2Miss[] = {3.3, 0.001, 0.13, 0.002};
		int lockTime = 550;
		double fpMisses[] = {0.0, 0.1, 2.1, 2.0};
		
		SWPipeline testPipe = new SWPipeline(noStages, stageDur, l2Miss, 
				lockTime,	fpMisses);
		
		assertEquals(noStages, testPipe.getNumberOfStages());
		assertEquals(lockTime, testPipe.getQueueLockingDuration());
		
		assertArrayEquals(stageDur, testPipe.getStageDurations());
		for(int i=0; i<noStages; i++)
		{
			assertEquals(l2Miss[i], testPipe.getStageMemAccesses(i), delta);
			assertEquals(fpMisses[i], testPipe.getFPAccesses(i), delta);
		}
		assertArrayEquals(l2Miss, testPipe.getMemAccesses(), delta);
		
		
		
		
//		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testSWPipelineSWPipeline() {
		
		SWPipeline tPipe = new SWPipeline(sTestPipe);
		
		assertEquals(sNumStages, tPipe.getNumberOfStages());
		assertEquals(sLockDur, tPipe.getQueueLockingDuration());
		
		assertArrayEquals(sOrderedDurations, tPipe.getStageDurations());
		for(int i=0; i<sNumStages; i++)
		{
			assertEquals(sOrderdMisses[i], tPipe.getStageMemAccesses(i), delta);
			assertEquals(sOrderdFPMisses[i], tPipe.getFPAccesses(i), delta);
		}
		assertArrayEquals(sOrderdMisses, tPipe.getMemAccesses(), delta);
		
		assertNotSame(sTestPipe.getMemAccesses(), tPipe.getMemAccesses());
		assertNotSame(sTestPipe.getStageL2Misses(), tPipe.getStageL2Misses());
		
		assertNotSame(sTestPipe.getStageNames(), tPipe.getStageNames());
		
				
		
	}

	@Test
	public void testSetStageDuration() {
		int numStages2 = 5;
		int durations2[] = {20, 150, 15, 46, 71};
		double mises2[] = {0.0, 0.0, 0.0, 0.0, 0.0};
		int lockDur2 = 2;
		String nombres2[] = {"Ars", "Ders", "DD", "Teex", "Bh"};
		double fpMisses2[] = {0.0, 0.0, 0.0, 0.0, 15.0};
		int stOrder2[] = {1, 2, 3, 0, 4};
		
		int orderedDurations[] = {46, 20, 150, 15, 71};
		
		SWPipeline pipe2 = new SWPipeline(numStages2, durations2, mises2, lockDur2, nombres2, fpMisses2, stOrder2);
		
		assertArrayEquals(orderedDurations, pipe2.getStageDurations());
		
		for(int i=0; i< numStages2; i++)
			pipe2.setStageDuration(i, orderedDurations[i]+i*10);
		
		for(int i=0; i< numStages2; i++)
		{
			assertEquals(orderedDurations[i]+i*10, pipe2.getStageDuration(i));
			assertEquals(orderedDurations[i], pipe2.getSingleModeDuration(i));
		}
		
	}

	@Test
	public void testSetStageL2MissesIntDouble() {
		SWPipeline tPipe = new SWPipeline(sTestPipe);
		double fix = 1.99;
		double value = 0.53212;
		
		assertArrayEquals(sOrderdMisses, tPipe.getMemAccesses(), delta);
		for(int i=0; i<tPipe.getNumberOfStages(); i++)
			tPipe.setStageL2Misses(i, sOrderdMisses[i]+fix);
		for(int i=0; i<tPipe.getNumberOfStages(); i++)
		{
			assertEquals(sOrderdMisses[i]+fix, tPipe.getStageMemAccesses(i), delta);
			
		}	
		for(int i=0; i<tPipe.getNumberOfStages(); i++)
			tPipe.setStageL2Misses(i, value);
		for(int i=0; i<tPipe.getNumberOfStages(); i++)
			assertEquals(value, tPipe.getStageMemAccesses(i), delta);
			
		
//		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testSetStageDurations() {
		SWPipeline tPipe = new SWPipeline(sTestPipe);
		SWPipeline tPipe2 = new SWPipeline(sTestPipe);
		SWPipeline tPipe3 = new SWPipeline(sTestPipe);
		int fix = 15;
		int value = 324;
		int fixArr[] = new int[tPipe.getNumberOfStages()];
		int fixArr2[] = new int[tPipe.getNumberOfStages()];
		
		for(int i=0; i < sNumStages; i++)
		{
			fixArr[i] = sOrderedDurations[i] + fix;
			fixArr2[i] = value;
		}
		
		tPipe.setStageDurations(fixArr);
		tPipe2.setStageDurations(fixArr2);
		
		for(int i=0; i < sNumStages; i++)
			tPipe3.setStageDuration(i, fixArr[i]);
		
		assertArrayEquals(fixArr, tPipe.getStageDurations());
		assertArrayEquals(fixArr2, tPipe2.getStageDurations());
		
		for(int i=0; i < sNumStages; i++)
		{
			assertEquals(sOrderedDurations[i], tPipe3.getSingleModeDuration(i));
			assertEquals(fixArr[i], tPipe3.getStageDuration(i));
			assertEquals(sOrderedDurations[i], tPipe.getSingleModeDuration(i));
			assertEquals(sOrderedDurations[i], tPipe2.getSingleModeDuration(i));
		}
		
		
	}

	@Test
	public void testSetStageL2MissesDoubleArray() {
		SWPipeline tPipe = new SWPipeline(sTestPipe);
		SWPipeline tPipe2 = new SWPipeline(sTestPipe);
		SWPipeline tPipe3 = new SWPipeline(sTestPipe);
		
		double fix = 15.01;
		double value = 324/432;
		double fixArr[] = new double[tPipe.getNumberOfStages()];
		double fixArr2[] = new double[tPipe.getNumberOfStages()];
		
		for(int i=0; i<sNumStages; i++)
		{
			fixArr[i] = sOrderdMisses[i] + fix;
			fixArr2[i] = value;
			tPipe3.setStageL2Misses(i, fixArr[i]);
		}
		
		tPipe.setStageL2Misses(fixArr);
		tPipe2.setStageL2Misses(fixArr2);
		
		assertArrayEquals(fixArr, tPipe.getMemAccesses(), delta);
		assertArrayEquals(fixArr2, tPipe2.getMemAccesses(), delta);
		
		assertArrayEquals(fixArr, tPipe.getStageL2Misses(),delta);
		assertArrayEquals(fixArr2, tPipe2.getStageL2Misses(),delta);
		
		for(int i=0; i<sNumStages; i++)
		{
			assertEquals(fixArr[i], tPipe3.getStageMemAccesses(i), delta);
		}
		
		
	}

	@Test
	public void testAddToDurationOfStage() {
		SWPipeline tPipe = new SWPipeline(sTestPipe);
		int add = 111;
		for (int i=0; i<sNumStages; i++)
			tPipe.addToDurationOfStage(i, add*i);
		
		for(int i=0; i<sNumStages; i++)
		{
			assertEquals(sOrderedDurations[i]+add*i, tPipe.getStageDuration(i));
			assertEquals(sOrderedDurations[i], tPipe.getSingleModeDuration(i));
		}

		
	}

	@Test
	public void testSetCommunicationDuration() {
		SWPipeline tPipe = new SWPipeline(sTestPipe);
		int add = 111;
		
		assertEquals(sLockDur, tPipe.getQueueLockingDuration());
		
		tPipe.setCommunicationDuration(add);
		
		assertEquals(add, tPipe.getQueueLockingDuration());
		
		
	}

	@Test
	public void testSetInCommDuration() {
		SWPipeline tPipe = new SWPipeline(sTestPipe);
		int add = 111;
		
		for(int i=0;i<sNumStages;i++)
		{
			assertEquals(sLockDur, tPipe.getInCommTime(i));
			tPipe.setInCommDuration(i, add*i);
		}
		
		for(int i=0;i<sNumStages;i++)
			assertEquals(add*i, tPipe.getInCommTime(i));
		
	}

	@Test
	public void testSetOutCommDuration() {
		SWPipeline tPipe = new SWPipeline(sTestPipe);
		int add = 111;
		
		for(int i=0;i<sNumStages;i++)
		{
			assertEquals(sLockDur, tPipe.getOutCommTime(i));
			tPipe.setOutCommDuration(i, add*i);
		}
		
		for(int i=0;i<sNumStages;i++)
			assertEquals(add*i, tPipe.getOutCommTime(i));
	}

}
