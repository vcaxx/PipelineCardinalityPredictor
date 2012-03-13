package es.bsc.caos.pcp;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SWPSystemTest {

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
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testSWPSystemSWPipeline() {
		fail("Not yet implemented"); // TODO
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
			assertEquals(sCardins[i],testSys.getCardinality(i));
		
		
		
	}

	@Test
	public void testSetCardinality() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testSetCardinalities() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testAdjustMemDuration() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testAdjustMemDurationVSSingle() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testAdjustMemDurationOneStage() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testAdjustMemDurationOneStageVSSingle() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testAdjustDurationForCode() {
		fail("Not yet implemented"); // TODO
	}

}
