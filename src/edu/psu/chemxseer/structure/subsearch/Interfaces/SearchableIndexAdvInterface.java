package edu.psu.chemxseer.structure.subsearch.Interfaces;

import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.iso.FastSUCompleteEmbedding;

public interface SearchableIndexAdvInterface extends SearchableIndexInterface {

	/**
	 * Given a query graph 'query', return the minimal supergraph of this query.
	 * @param query
	 * @param status
	 * @param maxSubs
	 * @return
	 */
	public List<Integer> minimalSupergraphs(Graph query, SearchStatus status,
			List<Integer> maxSubs);
	
	/**
	 * Given a query graph 'query', return the maximal subgraph of this query.
	 * @param fastSu
	 * @param status
	 * @return
	 */
	public List<Integer> maxSubgraphs(FastSUCompleteEmbedding fastSu,
			SearchStatus status);

	/**
	 * Given the max subgraph & min supergraph of the query, 
	 * Return a designed features (implementation dependent)
	 * @param query
	 * @param maxSubs
	 * @param maximumSubgraph
	 * @param status
	 * @return
	 */
	public FastSUCompleteEmbedding designedSubgraph(Graph query,
			List<Integer> maxSubs, int[] maximumSubgraph, SearchStatus status);
	
	/**
	 * Given the max subgraph of the query, 
	 * Return a design feature (implementation dependent)
	 * @param maxSubs
	 * @param status
	 * @return
	 */
	public int designedSubgraph(List<Integer> maxSubs, SearchStatus status);
}
