package edu.psu.chemxseer.structure.subsearch.Lindex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.iso.CanonicalDFS;
import edu.psu.chemxseer.structure.postings.Impl.VerifierISO;
import edu.psu.chemxseer.structure.postings.Interface.IGraphFetcher;
import edu.psu.chemxseer.structure.postings.Interface.IGraphResult;
import edu.psu.chemxseer.structure.postings.Interface.IPostingFetcher;
import edu.psu.chemxseer.structure.factory.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Interfaces.ISearcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;
/**
 * Lindex with no posting splitting
 * @author dayuyuan
 *
 */
public class SubSearch_LindexSimple implements ISearcher {
	public LindexSearcher indexSearcher;
	public IPostingFetcher postingFetcher;
	protected VerifierISO verifier;

	public SubSearch_LindexSimple(LindexSearcher indexSearcher,
			IPostingFetcher postingFetcher, VerifierISO verifier) {
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
		// First look for g's subgraphs
		status.refresh();

		List<IGraphResult> answer = null;
		List<Integer> maxSubgraphs = indexSearcher.maxSubgraphs(query,
				status);
		if (maxSubgraphs == null || maxSubgraphs.size() == 0)
			answer = new ArrayList<IGraphResult>();
		else if (maxSubgraphs.get(0) == -1) {
			// graph g hits on one of the index term
			IGraphFetcher answerFetcher = this.postingFetcher.getPosting(
					maxSubgraphs.get(1), status);
			answer = answerFetcher.getAllGraphs(status);
			status.setVerifiedCount(0);
		} else {
			IGraphFetcher candidateFetcher = this.postingFetcher.getJoin(
					maxSubgraphs, status);
			status.setVerifiedCount(candidateFetcher.size());
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
		return "index";
	}

	public IPostingFetcher getPostingFetcher() {
		return this.postingFetcher;
	}

	public VerifierISO getVerifier() {
		return this.verifier;
	}

	/**
	 * Return the Term (String) to Term Frequency Pairs
	 */
	public Map<String, Integer> getKeyValuePairs() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		CanonicalDFS parser = MyFactory.getDFSCoder();

		for (LindexTerm aTerm : this.indexSearcher.getAllTerms()) {
			// get term string
			String gString = parser.serialize(parser.parse(
					indexSearcher.getTermFullLabel(aTerm),
					MyFactory.getGraphFactory()));
			int freq = this.postingFetcher.getPostingID(aTerm.getId()).length;
			result.put(gString, freq);
		}
		return result;

	}
}
