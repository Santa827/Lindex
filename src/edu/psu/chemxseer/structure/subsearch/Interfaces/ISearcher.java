package edu.psu.chemxseer.structure.subsearch.Interfaces;

import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.postings.Interface.IGraphResult;

/**
 * Subgraph search solver: including Gindex, FGindex,
 * Lindex, Lindex+, QuickSI, and TreeDelta.
 * One subgraph search solver contains 
 * (1) SearchableIndex (2) postingFetcher and (3) verifier
 * 
 * @author dayuyuan
 * 
 */
public interface ISearcher {
	
	/**
	 * Given the query graph q, search for database graphs containing the query.
	 * @param q
	 * @param result
	 * @return
	 */
	public List<IGraphResult> getAnswer(Graph q, SearchStatus searchStatus);

	/**
	 * Return the answer IDs: 
	 * Result[0] contains the graphs subgraph isomorphic to the query. 
	 * Result[1] contained the graphs isomorphic to to the query.
	 * @param query
	 * @return
	 */
	int[][] getAnswerIDs(Graph query);
}
