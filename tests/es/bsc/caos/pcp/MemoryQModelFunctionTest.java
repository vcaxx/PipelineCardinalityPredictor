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
public class MemoryQModelFunctionTest {

	static int sMinMemTime = 180;
	static int sMaxMemTime = 180 * 64;
	static int sNumOfBanks = 4;
	
	MemoryQueueModelSolver sMSolver;
	MemoryQModelFunction sMFunct;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		sMSolver = new MemoryQueueModelSolver(sMinMemTime, 
				sMaxMemTime, sNumOfBanks);
		sMFunct = new MemoryQModelFunction(sMSolver);
		
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link es.bsc.caos.pcp.MemoryQModelFunction#MemoryQModelFunction(es.bsc.caos.pcp.MemoryQueueModelSolver)}.
	 */
	@Test
	public void testMemoryQModelFunctionMemoryQueueModelSolver() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link es.bsc.caos.pcp.MemoryQModelFunction#MemoryQModelFunction(es.bsc.caos.pcp.Pipeline, double, int[], int)}.
	 */
	@Test
	public void testMemoryQModelFunctionPipelineDoubleIntArrayInt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link es.bsc.caos.pcp.MemoryQModelFunction#MemoryQModelFunction(es.bsc.caos.pcp.PipelineSystem, double, int)}.
	 */
	@Test
	public void testMemoryQModelFunctionPipelineSystemDoubleInt() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link es.bsc.caos.pcp.MemoryQModelFunction#MemoryQModelFunction(es.bsc.caos.pcp.Pipeline, double, int[])}.
	 */
	@Test
	public void testMemoryQModelFunctionPipelineDoubleIntArray() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link es.bsc.caos.pcp.MemoryQModelFunction#MemoryQModelFunction(es.bsc.caos.pcp.PipelineSystem, double)}.
	 */
	@Test
	public void testMemoryQModelFunctionPipelineSystemDouble() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link es.bsc.caos.pcp.MemoryQModelFunction#value(double)}.
	 */
	@Test
	public void testValue() {
		fail("Not yet implemented"); // TODO
	}

}
