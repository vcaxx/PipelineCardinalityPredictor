/**
 * 
 */
package es.bsc.caos.pcp;

/**
 * @author vcakarev
 *
 */
public interface PipelineSystem {
	Pipeline getPipeline();
	int[] getCardinalities();
	int getCardinality(int stageID);
	int[] getOrderedCardinalities();

}
