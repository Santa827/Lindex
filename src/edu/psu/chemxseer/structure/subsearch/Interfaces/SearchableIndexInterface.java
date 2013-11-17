package edu.psu.chemxseer.structure.subsearch.Interfaces;

import java.util.List;
import de.parmol.graph.Graph;

public interface SearchableIndexInterface extends SearchableIndexBaseInterface {
	/**
	 * Given the query graph "query", return all the maximum subgraphs (for
	 * filtering) feature IDs.
	 * @param query
	 * @param status
	 * @return
	 */
	public List<Integer> maxSubgraphs(Graph query, SearchStatus status);

	/**
	 * Given the query graph "query", return the designed subgraphs.
	 * For example, FGindex return TCFG feature 
	 * Gindex/Lindex return the exact matching feature
	 * This is a implementation specific method.
	 * @param query
	 * @param exactMatch: exactMatch[0] == true, if the feature isomorphic to the query.
	 * @param result
	 * @return
	 */
	public int designedSubgraph(Graph query, boolean[] exactMatch, SearchStatus result);

}
