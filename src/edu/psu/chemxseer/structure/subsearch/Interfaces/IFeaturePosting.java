package edu.psu.chemxseer.structure.subsearch.Interfaces;

/**
 * The postings of a feature It contains all database graphs (IDS) that are
 * supergrpah isomorphic to the feature graph.
 * 
 * @author dayuyuan
 * 
 */
public interface IFeaturePosting {
	/**
	 * Return the graphIDS given the postingShift in the posting files.
	 * 
	 * @param postingShift
	 * @return
	 */
	public int[] getPosting(long postingShift);
}
