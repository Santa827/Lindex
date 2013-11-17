package edu.psu.chemxseer.structure.postings.Impl;

import java.util.ArrayList;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.iso.FastSU;
import edu.psu.chemxseer.structure.postings.Interface.IGraphFetcher;
import edu.psu.chemxseer.structure.postings.Interface.IGraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;

/**
 * Verification using the fastSu verifier.
 * 
 * @author dayuyuan
 * 
 */
public class VerifierISO {

	protected FastSU fastSu;

	public VerifierISO() {
		fastSu = new FastSU();
	}

	/**
	 * If order == true, query subgraph isomorphic to answer [subgraph search]
	 * If order == false, query supergraph isomorphic to answer [supergraph
	 * search]
	 * 
	 * @param query
	 * @param candidateFetcher
	 * @param order
	 * @param TimeComponent
	 * @return
	 */
	public List<IGraphResult> verify(Graph query,
			IGraphFetcher candidateFetcher, boolean order, SearchStatus searchResult) {
		if (candidateFetcher == null || candidateFetcher.size() == 0)
			return new ArrayList<IGraphResult>();
		else {
			List<IGraphResult> answerSet = new ArrayList<IGraphResult>();
			List<IGraphResult> candidates = candidateFetcher
					.getGraphs(searchResult);
			long start = System.currentTimeMillis();
			while (candidates != null) {
				for (int i = 0; i < candidates.size(); i++) {
					Graph g = candidates.get(i).getG();
					if (g == null)
						continue;
					if (order && fastSu.isIsomorphic(query, g))
						answerSet.add(candidates.get(i));

					else if (!order && fastSu.isIsomorphic(g, query))
						answerSet.add(candidates.get(i));
				}
				searchResult.addVerifyTime(System.currentTimeMillis() - start);
				candidates = candidateFetcher.getGraphs(searchResult);
				start = System.currentTimeMillis();
			}
			return answerSet;
		}
	}

	/**
	 * If order == true, query subgraph not isomorphic to answer [subgraph
	 * search] If order == false, query supergraph not isomorphic to answer
	 * [supergraph search]
	 * 
	 * @param query
	 * @param candidateFetcher
	 * @param order
	 * @param TimeComponent
	 * @return
	 */
	public List<IGraphResult> verifyFalse(Graph query,
			IGraphFetcher candidateFetcher, boolean order, SearchStatus searchResult) {
		if (candidateFetcher == null || candidateFetcher.size() == 0)
			return new ArrayList<IGraphResult>();
		else {
			List<IGraphResult> answerSet = new ArrayList<IGraphResult>();
			List<IGraphResult> candidates = candidateFetcher
					.getGraphs(searchResult);
			long start = System.currentTimeMillis();
			while (candidates != null) {
				for (int i = 0; i < candidates.size(); i++) {
					Graph g = candidates.get(i).getG();
					if (g == null)
						continue;
					if (order && !fastSu.isIsomorphic(query, g))
						answerSet.add(candidates.get(i));

					else if (!order && !fastSu.isIsomorphic(g, query))
						answerSet.add(candidates.get(i));
				}
				searchResult.addVerifyTime(System.currentTimeMillis() - start);
				candidates = candidateFetcher.getGraphs(searchResult);
				start = System.currentTimeMillis();
			}
			return answerSet;
		}
	}
}
