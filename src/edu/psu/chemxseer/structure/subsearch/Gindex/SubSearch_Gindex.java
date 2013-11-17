package edu.psu.chemxseer.structure.subsearch.Gindex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.postings.Impl.PostingFetcherLucene;
import edu.psu.chemxseer.structure.postings.Impl.VerifierISO;
import edu.psu.chemxseer.structure.postings.Interface.IGraphFetcher;
import edu.psu.chemxseer.structure.postings.Interface.IGraphResult;
import edu.psu.chemxseer.structure.postings.Interface.IPostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchableIndexInterface;
import edu.psu.chemxseer.structure.subsearch.Interfaces.ISearcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;

public class SubSearch_Gindex implements ISearcher {
	public SearchableIndexInterface indexSearcher;
	public IPostingFetcher postingFetcher;
	public VerifierISO verifier;

	public SubSearch_Gindex(SearchableIndexInterface indexSearcher,
			PostingFetcherLucene postingFetcher, VerifierISO verifier) {
		this.indexSearcher = indexSearcher;
		this.postingFetcher = postingFetcher;
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
		int[] temp = new int[1];
		answer = this.hitAndReturn(query, temp, status);
		if (answer != null) {
			status.setVerifiedCount(0); // No verification is needed
		} else {
			IGraphFetcher candidateFetcher;
			candidateFetcher = this
					.candidateByFeatureJoin(query, status);
			status.addVerifiedCount(candidateFetcher.size());
			answer = this.verifier.verify(query, candidateFetcher, true,
					status);
		}
		status.setTrueAnswerCount(answer.size());
		return answer;
	}

	private List<IGraphResult> hitAndReturn(Graph query, int[] hitIndex,
			SearchStatus searchResult) {

		boolean[] exactMatch = new boolean[1];
		exactMatch[0] = false;
		hitIndex[0] = indexSearcher.designedSubgraph(query, exactMatch,
				searchResult);
		if (hitIndex[0] == -1)
			return null;
		else if (exactMatch[0]) {
			List<IGraphResult> result = null;
			IGraphFetcher gf = this.postingFetcher.getPosting(hitIndex[0],
					searchResult);
			result = gf.getAllGraphs(searchResult);
			return result;
		} else
			return null;
	}

	public IGraphFetcher candidateByFeatureJoin(Graph query,
			SearchStatus status) {
		List<Integer> features = indexSearcher.maxSubgraphs(query,
				status);
		if (features == null || features.size() == 0)
			return null;
		else
			return postingFetcher.getJoin(features, status);
	}

	/********* The following is used for index files *****************************/
	public static String getLuceneName() {
		return "lucene/";
	}

	public static String getIndexName() {
		return "index";
	}

}
