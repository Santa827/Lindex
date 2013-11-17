package edu.psu.chemxseer.structure.subsearch.FGindex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.postings.Impl.PostingFetcherLucene;
import edu.psu.chemxseer.structure.postings.Impl.VerifierISO;
import edu.psu.chemxseer.structure.postings.Interface.IGraphFetcher;
import edu.psu.chemxseer.structure.postings.Interface.IGraphResult;
import edu.psu.chemxseer.structure.postings.Interface.IPostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.ISearcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;

public class SubSearch_FGindex implements ISearcher {
	private FGindex in_memoryIndex;
	private VerifierISO verifier;
	private IPostingFetcher onDiskPostingFetcher;
	private String baseName;

	public SubSearch_FGindex(FGindex in_memoryIndex, VerifierISO verifier,
			PostingFetcherLucene onDiskPostings, String baseName) {
		this.in_memoryIndex = in_memoryIndex;
		this.verifier = verifier;
		this.onDiskPostingFetcher = onDiskPostings;
		this.baseName = baseName;
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
		List<IGraphResult> answers = null;

		int[] hitIndex = new int[1];
		hitIndex[0] = -1;
		// 1. In-memory Index lookup
		answers = in_memoryIndex.hitAndReturn(query, hitIndex, status);
		if (answers != null) {
			status.setTrueAnswerCount(answers.size());
			return answers; // find a hit and return
		}
		// 2. Load the on-disk index
		if (hitIndex[0] >= 0) {
			int onDiskIndexID = hitIndex[0];
			FGindex on_diskIndex = null;
			try {
				on_diskIndex = loadOndiskIndex(hitIndex[0], status);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (on_diskIndex != null) {
				hitIndex[0] = -1;
				answers = on_diskIndex.hitAndReturn(query, onDiskIndexID,
						hitIndex, status);
				if (hitIndex[0] >= 0) {
					status.setTrueAnswerCount(answers.size());
					return answers;
				}
			}
		}
		// 3. Filtering + verification
		IGraphFetcher candidateFetcher;
		IGraphFetcher r1 = in_memoryIndex.candidateByFeatureJoin(query,
				status);
		IGraphFetcher r2 = in_memoryIndex.candidateByEdgeJoin(query,
				status);
		if (r1 == null || r1.size() == 0)
			candidateFetcher = r2;
		else if (r2 == null || r2.size() == 0)
			candidateFetcher = r1;
		else {
			candidateFetcher = r1.join(r2);
		}
		status.setVerifiedCount(candidateFetcher.size());
		answers = verifier.verify(query, candidateFetcher, true, status);
		status.setTrueAnswerCount(answers.size());
		return answers;
	}

	/**
	 * Load the onDisk index, counted as the index loopup time
	 * 
	 * @param TCFGId
	 * @param TimeComponent
	 * @return
	 * @throws IOException
	 */
	private FGindex loadOndiskIndex(int TCFGId, SearchStatus status)
			throws IOException {
		long start = System.currentTimeMillis();
		FGindexSearcher searcher = FGindexConstructor.loadSearcher(baseName,
				getOnDiskIndexName(TCFGId), null); // empty graphdatabase
		FGindex onDiskIGI = new FGindex(searcher, onDiskPostingFetcher);
		status.addFilteringTime(System.currentTimeMillis() - start);
		return onDiskIGI;
	}

	/********** This part will be replace to configuration file latter ***************/
	private static String onDiskBase = "onDiskIndex/";

	public static String getOnDiskIndexName(int TCFGID) {
		return onDiskBase + TCFGID;
	}

	public static String getLuceneName() {
		return "lucene";
	}

	public static String getIn_MemoryIndexName() {
		return "in_memory_index";
	}

	public static String getOnDiskLuceneName() {
		return "onDiskLucene";
	}

	public static String getOnDiskFolderName() {
		return onDiskBase;
	}

	public EdgeIndex getEdgeIndex() {
		return this.in_memoryIndex.getEdgeIndex();
	}

}
