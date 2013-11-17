package edu.psu.chemxseer.structure.subsearch.QuickSI;

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

public class SubSearch_QuickSI implements ISearcher {
	private SearchableIndexInterface indexSearcher;
	private IPostingFetcher postingFetcher;
	private VerifierISO verifier;

	public SubSearch_QuickSI(SearchableIndexInterface indexSearcher, IPostingFetcher postings,
			VerifierISO verifier) {
		this.indexSearcher = indexSearcher;
		this.postingFetcher = postings;
		this.verifier = verifier;
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
		// 1. first step: find the sudo maxSubgraphs of graph g
		List<Integer> maxSubs = indexSearcher
				.maxSubgraphs(query, status);
		if (maxSubs.get(0) == -1) {
			status.setVerifiedCount(0);
			IGraphFetcher answerFetcher = this.postingFetcher.getPosting(
					maxSubs.get(1), status);
			answer = answerFetcher.getAllGraphs(status);
		}
		// 2. get the candidate set
		else {
			IGraphFetcher candidateFetcher = this.postingFetcher.getJoin(
					maxSubs, status);
			status.setVerifiedCount(candidateFetcher.size());
			// 3. verification
			answer = this.verifier.verify(query, candidateFetcher, true,
					status);
		}
		status.setTrueAnswerCount(answer.size());
		return answer;
	}

	public static String getLuceneName() {
		return "lucene/";
	}

	public static String getIndexName() {
		return "index/";
	}
}
