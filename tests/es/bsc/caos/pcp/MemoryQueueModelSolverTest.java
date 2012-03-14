/**
 * 
 */
package es.bsc.caos.pcp;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author vcakarev
 *
 */
public class MemoryQueueModelSolverTest {

	static int sMinMemTime = 180;
	static int sMaxMemTime = 180 * 64;
	static int sNumOfBanks = 4;
	
	int sNumStages = 5;
	int sDurations[] = {200, 1500, 1500, 460, 701};
	double sMises[] = {0.0, 9.0, 3.333, 1.51, 0.0};
	int sLockDur = 200;
	String sNombres[] = {"Ars", "Ders", "DD", "Teex", "Bh"};
	double sFpMisses[] = {0.0, 0.0, 0.0, 0.0, 15.0};
	int sStOrder[] = {1, 2, 4, 0, 3};
	
	int sOrderedDurations[] = {460, 200, 1500, 701, 1500};
	double sOrderdMisses[] = {4.01, 0.0, 12.0, 0.0, 3.333};
	double sOrderdFPMisses[] = {0.0, 0.0, 0.0, 15.0, 0.0};
	
	
	SWPipeline sPipe;
	SWPSystem onePipeSystem;
	SWPSystem overloadedPipeSystem;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		sPipe = new SWPipeline(sNumStages, sDurations, sMises, 
				sLockDur, sNombres, sFpMisses, sStOrder);
		
		
		
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link es.bsc.caos.pcp.MemoryQueueModelSolver#MemoryQueueModelSolver(double, double, int)}.
	 */
	@Test
	public void testMemoryQueueModelSolver() {
		MemoryQueueModelSolver mSolver = new MemoryQueueModelSolver(
				sMinMemTime, sMaxMemTime, sNumOfBanks);
		
//		mSolver.getMemAccessTime(onePipeSystem);
//		mSolver.getMemAccessTime(overloadedPipeSystem);
		
		
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link es.bsc.caos.pcp.MemoryQueueModelSolver#getMemAccessTime(es.bsc.caos.pcp.PipelineSystem)}.
	 */
	@Test
	public void testGetMemAccessTime() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link es.bsc.caos.pcp.MemoryQueueModelSolver#checkSolution(double)}.
	 */
	@Test
	public void testCheckSolution() {
		fail("Not yet implemented"); // TODO
	}

}
