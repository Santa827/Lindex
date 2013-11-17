package edu.psu.chemxseer.structure.subsearch.FGindex;

import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.postings.Interface.IGraphFetcher;
import edu.psu.chemxseer.structure.postings.Interface.IGraphResult;
import edu.psu.chemxseer.structure.postings.Interface.IPostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchableIndexInterface;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;

/**
 * In-memory IGI and On-Disk IGI
 * 
 * @author duy113
 * 
 */
public class FGindex {
	SearchableIndexInterface indexSearcher;
	IPostingFetcher postingFetcher;
	EdgeIndex edgeIndex;

	public FGindex(FGindexSearcher searcher, IPostingFetcher in_memory_postings) {
		this.indexSearcher = searcher;
		this.edgeIndex = searcher.getEdgeIndex();
		this.postingFetcher = in_memory_postings;
	}

	/**
	 * Given the query graph, if it hits on any of the indexing feature return
	 * the postings directly without isomorphism test
	 * @param query
	 * @param hitIndex
	 * @param status
	 * @return
	 */
	public List<IGraphResult> hitAndReturn(Graph query, int[] hitIndex,
			SearchStatus status) {
		boolean[] exactMatch = new boolean[1];
		exactMatch[0] = false;
		hitIndex[0] = indexSearcher.designedSubgraph(query, exactMatch,
				status);
		if (hitIndex[0] == -1)
			return null;
		else if (exactMatch[0])
			return postingFetcher.getPosting(hitIndex[0], status)
					.getAllGraphs(status);
		else
			return null;
	}

	public List<IGraphResult> hitAndReturn(Graph query, int onDiskIndexID,
			int[] hitIndex, SearchStatus status) {
		boolean[] exactMatch = new boolean[1];
		exactMatch[0] = false;
		hitIndex[0] = indexSearcher.designedSubgraph(query, exactMatch,
				status);
		if (hitIndex[0] == -1)
			return null;
		else if (exactMatch[0])
			return postingFetcher.getPosting(onDiskIndexID + "_" + hitIndex[0],
					status).getAllGraphs(status);
		else
			return null;
	}

	/**
	 * Given the query graph, find all the candidate graphs by join operation of
	 * all the maximal subgraph features
	 * @param query
	 * @param status
	 * @return
	 */
	public IGraphFetcher candidateByFeatureJoin(Graph query,
			SearchStatus status) {
		List<Integer> features = indexSearcher.maxSubgraphs(query,
				status);
		if (features == null || features.size() == 0)
			return null;
		else
			return postingFetcher.getJoin(features, status);
	}

	/**
	 * Given the query graph, find all the candidate graph by join operation of
	 * the infrequent edges
	 * @param query
	 * @param TimeComponent
	 * @return
	 */
	public IGraphFetcher candidateByEdgeJoin(Graph query, SearchStatus status) {
		return edgeIndex.getInfrequentEdgeCandidates(query, status);
	}

	public EdgeIndex getEdgeIndex() {
		return this.edgeIndex;
	}
}
