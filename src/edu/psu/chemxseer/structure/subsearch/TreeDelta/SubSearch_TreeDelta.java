package edu.psu.chemxseer.structure.subsearch.TreeDelta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.postings.Impl.VerifierISO;
import edu.psu.chemxseer.structure.postings.Interface.IGraphFetcher;
import edu.psu.chemxseer.structure.postings.Interface.IGraphResult;
import edu.psu.chemxseer.structure.postings.Interface.IPostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchableIndexInterface;
import edu.psu.chemxseer.structure.subsearch.Interfaces.ISearcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;

public class SubSearch_TreeDelta implements ISearcher {
	private SearchableIndexInterface treeSearcher;
	private SearchableIndexInterface deltaSearcher;
	private VerifierISO verifier;
	private IPostingFetcher treeFetcher;
	private IPostingFetcher deltaFetcher;

	public SubSearch_TreeDelta(SearchableIndexInterface treeSearcher, SearchableIndexInterface deltaSearcher,
			IPostingFetcher posting1, IPostingFetcher posting2,
			VerifierISO verif) {
		this.treeSearcher = treeSearcher;
		this.deltaSearcher = deltaSearcher;
		this.verifier = verif;
		this.treeFetcher = posting1;
		this.deltaFetcher = posting2;
	}

	@Override
	public int[][] getAnswerIDs(Graph query) {
		List<IGraphResult> answer = this.getAnswer(query, new SearchStatus());
		int[] result = new int[answer.size()];
		List<Integer> result2 = new ArrayList<Integer>();
		int counter1 = 0;
		for (IGraphResult oneAnswer : answer) {
			if (oneAnswer.getG().getEdgeCount() == query.getEdgeCount())
				result2.add(oneAnswer.getID());
			else
				result[counter1++] = oneAnswer.getID();
		}
		int[][] finalResult = new int[2][];
		finalResult[0] = Arrays.copyOf(result, counter1);
		finalResult[1] = new int[result2.size()];
		for (int w = 0; w < result2.size(); w++)
			finalResult[1][w] = result2.get(w);
		return finalResult;
	}

	@Override
	public List<IGraphResult> getAnswer(Graph query, SearchStatus status) {
		status.refresh();
		List<IGraphResult> answer = null;
		// 0. Step one: hit & return
		boolean[] exactMatch = new boolean[1];
		int fID = treeSearcher.designedSubgraph(query, exactMatch,
				status);
		if (fID > 0) {
			IGraphFetcher answerFetcher = treeFetcher.getPosting(fID,
					status);
			answer = answerFetcher.getAllGraphs(status);
			status.setTrueAnswerCount(answer.size());
			return answer;
		}
		if (query.getEdgeCount() >= query.getNodeCount()) {
			fID = deltaSearcher.designedSubgraph(query, exactMatch,
					status);
			if (fID > 0) {
				IGraphFetcher answerFetcher = deltaFetcher.getPosting(fID,
						status);
				answer = answerFetcher.getAllGraphs(status);
				status.setTrueAnswerCount(answer.size());
				return answer;
			}
		}

		// 1. first step: find the sudo maxSubgraphs of graph g
		List<Integer> maxSubs1 = treeSearcher
				.maxSubgraphs(query, status);
		List<Integer> maxSubs2 = null;
		if (query.getEdgeCount() >= query.getNodeCount()) {
			maxSubs2 = deltaSearcher.maxSubgraphs(query, status);
		}
		// 2. get the candidate set
		IGraphFetcher candidateFetcher = null;
		IGraphFetcher candidateFetcher1 = null, candidateFetcher2 = null;
		if (maxSubs1 != null && maxSubs1.size() == 0) {
			candidateFetcher1 = treeFetcher.getJoin(maxSubs1, status);
		}
		if (maxSubs2 != null && maxSubs2.size() != 0) {
			candidateFetcher2 = deltaFetcher.getJoin(maxSubs2, status);
		}
		if (candidateFetcher1 != null && candidateFetcher2 != null) {
			candidateFetcher = candidateFetcher1.join(candidateFetcher2);
		} else if (candidateFetcher1 != null)
			candidateFetcher = candidateFetcher1;
		else if (candidateFetcher2 != null)
			candidateFetcher = candidateFetcher2;
		else {
			return null;
		}
		status.setVerifiedCount(candidateFetcher.size());
		// 3. verification
		answer = this.verifier.verify(query, candidateFetcher, true,
				status);
		status.setTrueAnswerCount(answer.size());
		return answer;
	}

	public static String getLuceneName() {
		return "lucene/";
	}

	public static String getDeltaIndexName() {
		return "dIndex";
	}

	public static Object getDeltaFeature() {
		return "dFeatures";
	}
}
