package edu.psu.chemxseer.structure.subsearch.Gindex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.iso.CanonicalDFS;
import edu.psu.chemxseer.structure.factory.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchableIndexInterface;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;
import edu.psu.chemxseer.structure.factory.SubgraphGenerator2;

public class GindexSearcher implements SearchableIndexInterface {
	protected HashMap<String, Integer> gHash;
	protected int maxEdgeNum;
	protected CanonicalDFS dfsParser;
	protected boolean[] discriminative;
	protected boolean exhaustSearch;

	/**
	 * Build a Gindex Searcher: If exhaustSearch == true, then discriminative ==
	 * null Else discriminative !=null
	 * 
	 * @param exhaustSearch
	 */
	public GindexSearcher(HashMap<String, Integer> gHash,
			boolean[] discriminative, int maxEdgeNum, boolean exhaustSearch) {
		this.gHash = gHash;
		this.maxEdgeNum = maxEdgeNum;
		this.discriminative = discriminative;
		this.exhaustSearch = exhaustSearch;
		this.dfsParser = MyFactory.getDFSCoder();
	}

	@Override
	public List<Integer> maxSubgraphs(Graph query, SearchStatus status) {
		long start = System.currentTimeMillis();
		// 1. first step: return all subgraphs features of the query
		Map<String, Integer> allIndexSubs = this.getAllSubgraphs(query);
		// 2. second step: order by DFS order and return only the max subgraphs.
		List<Integer> results = new ArrayList<Integer>();
		String[] allSubStrings = new String[allIndexSubs.size()];
		allIndexSubs.keySet().toArray(allSubStrings);
		Arrays.sort(allSubStrings, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < allSubStrings.length; i++) {
			boolean isMaximum = true;
			for (int j = i + 1; j < allSubStrings.length; j++) {
				if (allSubStrings[j].startsWith(allSubStrings[i])) {
					isMaximum = false;
					break;
				}
			}
			if (isMaximum) {
				int index = allIndexSubs.get(allSubStrings[i]);
				results.add(index);
			} else
				continue;
		}
		status.addFilteringTime(System.currentTimeMillis() - start);
		return results;
	}
	/**
	 * Enumerate all possible subgraph of the query, and see whether it equals
	 * to any indexed feature
	 * @param query
	 * @return
	 */
	private Map<String, Integer> getAllSubgraphs(Graph query) {
		SubgraphGenerator2 subgraphs = new SubgraphGenerator2(query,
				this.maxEdgeNum);
		Integer indexID = -1;
		HashMap<String, Integer> allIndexSubs = new HashMap<String, Integer>();
		if (query.getEdgeCount() < this.maxEdgeNum) {
			String gLabels = MyFactory.getDFSCoder().serialize(query);
			Integer gID = gHash.get(gLabels);
			if (this.exhaustSearch && gID != null)
				allIndexSubs.put(gLabels, gID);
			else if (gID != null && discriminative != null
					&& discriminative[gID] != false)
				allIndexSubs.put(gLabels, gID);
		}

		while (true) {
			// String label = subgraphs.nextCode();
			String label = subgraphs.nextSubgraph();
			if (label == null)
				break; // all subgraphs has been
			else if (label.contains("-1"))
				continue;
			else {
				indexID = gHash.get(label);
				if (!exhaustSearch) {
					if (indexID == null) {
						subgraphs.earlyPruning();// No frequent at all, early
													// pruning
						continue;
					}
					// non frequent
					else if (discriminative[indexID] == false)
						continue;// A in-discriminative frequent feature
					else {
						allIndexSubs.put(label, indexID);
					}
				} else {
					// exhaustSearch: either continue searching or adding into
					// allSubs
					if (indexID == null)
						continue;
					else
						allIndexSubs.put(label, indexID);
				}
			}
		}
		return allIndexSubs;
	}

	@Override
	public List<Integer> subgraphs(Graph query, SearchStatus status) {
		long start = System.currentTimeMillis();
		Map<String, Integer> allSubs = this.getAllSubgraphs(query);
		List<Integer> allIds = new ArrayList<Integer>();
		allIds.addAll(allSubs.values());
		status.addFilteringTime(System.currentTimeMillis() - start);
		return allIds;
	}

	@Override
	// Find the index feature (ID) isomorphic to the query
	// return -1 if the feature has not been indexed or not discriminative feature.
	public int designedSubgraph(Graph query, boolean[] exactMatch,
			SearchStatus status) {
		long start = System.currentTimeMillis();
		// / Test whether Graph g's canonical form equals to any index terms
		String gString = MyFactory.getDFSCoder().serialize(query);
		Integer index = this.gHash.get(gString);
		if (index != null) {
			if (this.exhaustSearch || this.discriminative[index] == true)
				exactMatch[0] = true;
			status.addFilteringTime(System.currentTimeMillis() - start);
			return index;
		} else {
			exactMatch[0] = false;
			status.addFilteringTime(System.currentTimeMillis() - start);
			return -1;
		}
	}

	public int getFeatureCount() {
		return this.gHash.size();
	}

	@Override
	public int[] getAllFeatureIDs() {
		throw new UnsupportedOperationException();
	}

}