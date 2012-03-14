package es.bsc.caos.pcp;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.bsc.caos.pcp.SWPSystem.Resource;

public class SWPSystemTest {

	private static double delta = 0.00001;
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
	SWPipeline sTestPipe;	
	SWPSystem sTestSystem;
	
	int sCardins[] = {3, 5, 9, 7, 2};
	double sMinDur = Double.MAX_VALUE;
	int sMinCard = -1;
	
	@Before
	public void setUp() throws Exception {
		sTestPipe = new SWPipeline(sNumStages, sDurations, sMises, 
				sLockDur, sNombres, sFpMisses, sStOrder);
		sTestSystem = new SWPSystem(sTestPipe, sCardins);
		
		
		for(int i=0; i<sNumStages; i++)
		{
			if((double)sTestPipe.getSingleModeDuration(i)
					/sCardins[i]< sMinDur)
			{
				sMinDur = (double)sTestPipe.getSingleModeDuration(i)
									/sCardins[i];
				sMinCard = i;
			}
		}
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSWPSystemSWPipelineIntArray() {
		SWPSystem testSys = new SWPSystem(sTestPipe, sCardins);
		
		assertEquals(sTestSystem.toString(), testSys.toString());
		
		
	}

	@Test
	public void testSWPSystemSWPipeline() {
		SWPSystem testSystem = new SWPSystem(sTestPipe);
		assertNotSame(sTestPipe, testSystem.getPipeline());
		assertEquals(sTestPipe.toString(), testSystem.getPipeline().toString());
		
		for(int i=0; i<sNumStages; i++)
			assertEquals(1, testSystem.getCardinality(i));
		
	}

	@Test
	public void testSWPSystemSWPSystem() {
		SWPSystem testSys = new SWPSystem(sTestSystem);
		
		
		
		//TODO: proveri zasto se bottleneck ID racuna samo za single pipe
//		assertEquals(sMinCard, testSys.getBottleneckID());

		assertArrayEquals(sCardins, testSys.getCardinalities());
		
		assertArrayEquals(sCardins, testSys.getOrderedCardinalities());
		
		assertNotSame(sTestPipe, testSys.getPipeline());
		assertNotSame(sTestSystem.getPipeline(), testSys.getPipeline());
		
		assertEquals(sTestPipe.toString(), testSys.getPipeline().toString());
		
		for(int i=0; i<sNumStages; i++)
			assertEquals(sCardins[i], testSys.getCardinality(i));
		
		
		
	}

	@Test
	public void testSetCardinality() {
		SWPSystem testSys = new SWPSystem(sTestSystem);
		
		assertArrayEquals(sCardins, testSys.getCardinalities());
		for(int i=0; i < sNumStages; i++)
			testSys.setCardinality(sCardins[i] + i, i);
		
		for(int i=0; i < sNumStages; i++)
			assertEquals(sCardins[i]+i, testSys.getCardinality(i));
		
	}

	@Test
	public void testSetCardinalities() {
		SWPSystem testSys = new SWPSystem(sTestSystem);
		
		assertArrayEquals(sCardins, testSys.getCardinalities());
		int newCards[] = {15, 23, 1, 3, 4};
		
		testSys.setCardinalities(newCards);
		
		for(int i=0; i < sNumStages; i++)
			assertEquals(newCards[i], testSys.getCardinality(i));
		
		assertArrayEquals(newCards, testSys.getCardinalities());
	}

	@Test
	public void testAdjustMemDuration() {
		SWPSystem testSys = new SWPSystem(sTestSystem);
		
		double oldMemAccessTime = 30.05;
		double newMemAccessTime = 75.11;
		
		int newDurations[] = new int[sNumStages]; 
		int newDurations2[] = new int[sNumStages];
		
		for(int i=0; i < sNumStages; i++)
		{
			newDurations[i] = sOrderedDurations[i] + 
			 (int)Math.ceil(sTestPipe.getStageMemAccesses(i) 
					 * (newMemAccessTime - oldMemAccessTime));
			
			newDurations2[i] = newDurations[i] + 
			 (int)Math.ceil(sTestPipe.getStageMemAccesses(i) 
					 * (newMemAccessTime - oldMemAccessTime));
		}
		
		assertArrayEquals(sOrderedDurations, testSys.getPipeline().getStageDurations());
		
		testSys.adjustMemDuration(oldMemAccessTime, newMemAccessTime);
		
		assertArrayEquals(newDurations, testSys.getPipeline().getStageDurations());
		
		testSys.adjustMemDuration(oldMemAccessTime, newMemAccessTime);
		
		assertArrayEquals(newDurations2, testSys.getPipeline().getStageDurations());
		
	}

	@Test
	public void testAdjustMemDurationVSSingle() {
		SWPSystem testSys = new SWPSystem(sTestSystem);
		
		double oldMemAccessTime = 30.05;
		double newMemAccessTime = 75.11;
		
		int newDurations[] = new int[sNumStages]; 
		
		for(int i=0; i < sNumStages; i++)
		{
			newDurations[i] = sOrderedDurations[i] + 
			 (int)Math.ceil(sTestPipe.getStageMemAccesses(i) 
					 * (newMemAccessTime - oldMemAccessTime));
		}
		
		assertArrayEquals(sOrderedDurations, testSys.getPipeline().getStageDurations());
		
		testSys.adjustMemDurationVSSingle(oldMemAccessTime, newMemAccessTime);
		
		assertArrayEquals(newDurations, testSys.getPipeline().getStageDurations());
		
		testSys.adjustMemDurationVSSingle(oldMemAccessTime, newMemAccessTime);
		
		assertArrayEquals(newDurations, testSys.getPipeline().getStageDurations());
	}

	@Test
	public void testAdjustDurationForCode() {
		SWPSystem testSys = new SWPSystem(sTestSystem);
		
		Resource resCode = Resource.FP;
		double oldAccessTime = 25.1;
		double accessTime = 37.2;
		
		double accesses[] = new double[sNumStages];
		for(int i=0; i<sNumStages; i++)
			accesses[i] = sTestPipe.getFPAccesses(i);
		
		assertArrayEquals(sOrderedDurations, testSys.getPipeline().getStageDurations());
		
		testSys.adjustDurationForCode(resCode, oldAccessTime, accessTime);
		
		for(int i=0; i<sNumStages; i++)
		{
			int expected = sTestPipe.getStageDuration(i);
			expected += (int)Math.ceil(accesses[i] * (accessTime-oldAccessTime));
						
			assertEquals(expected, testSys.getPipeline().getStageDuration(i));
		}
		
	}

}
