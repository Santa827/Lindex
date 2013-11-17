package edu.psu.chemxseer.structure.subsearch.Interfaces;

import java.util.List;

import de.parmol.graph.Graph;

public interface SearchableIndexBaseInterface {
	/**
	 * Given the query graph "query", return all the subgraphs feature IDs
	 * contained in "query".
	 * @param query
	 * @param status
	 * @return
	 */
	public List<Integer> subgraphs(Graph query, SearchStatus status);

	/**
	 * Return the index feature IDs of all the index features.
	 * @return
	 */
	public int[] getAllFeatureIDs();
}
