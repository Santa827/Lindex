package edu.psu.chemxseer.structure.subsearch.Lindex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.iso.FastSUCompleteEmbedding;
import edu.psu.chemxseer.structure.postings.Impl.VerifierISO;
import edu.psu.chemxseer.structure.postings.Interface.IGraphFetcher;
import edu.psu.chemxseer.structure.postings.Interface.IGraphResult;
import edu.psu.chemxseer.structure.postings.Interface.IPostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchableIndexAdvInterface;
import edu.psu.chemxseer.structure.subsearch.Interfaces.ISearcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;
/**
 * Implementation of LindexPlus (Lindex + disck_based Lindex)
 * @author dayuyuan
 *
 */
public class SubSearch_LindexPlus implements ISearcher {
	private SearchableIndexAdvInterface indexSearcher;
	private IPostingFetcher in_memFetcher;
	private VerifierISO verifier;

	private IPostingFetcher on_diskFetcher;
	private String baseName;

	public SubSearch_LindexPlus(SearchableIndexAdvInterface indexSearcher,
			IPostingFetcher in_memFetcher, IPostingFetcher on_diskFetcher,
			VerifierISO verifier, String baseName) {
		this.indexSearcher = indexSearcher;
		this.in_memFetcher = in_memFetcher;
		this.on_diskFetcher = on_diskFetcher;
		this.verifier = verifier;
		this.baseName = baseName;
	}

	@Override
	public int[][] getAnswerIDs(Graph query) {
		List<IGraphResult> answer = this.getAnswer(query,new SearchStatus());
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
		List<Integer> maxSubgraphs = this.indexSearcher.maxSubgraphs(query,
				status);
		// In Memory Hit
		if (maxSubgraphs != null && maxSubgraphs.get(0) == -1) {
			IGraphFetcher answerFetcher = this.in_memFetcher.getPosting(
					maxSubgraphs.get(1), status);
			answer = answerFetcher.getAllGraphs(status);
			status.setVerifiedCount(0);
		} else {
			// On Disk Hit
			List<Integer> superGraphs = this.indexSearcher.minimalSupergraphs(
					query, status, maxSubgraphs);
			// Decide whether to load on-disk Lindex.
			boolean loadOnDisk = true;
			boolean onDisk = false;
			List<Integer> onDiskMaxSubs = null;
			int[] maximumSub = new int[1];
			maximumSub[0] = -1;

			if (superGraphs == null || superGraphs.size() == 0)
				loadOnDisk = false;
			if (loadOnDisk) {
				// Find the maximum subgraph
				FastSUCompleteEmbedding fastSu = this.indexSearcher
						.designedSubgraph(query, maxSubgraphs, maximumSub,
								status);
				// Load the on-disk index
				LindexSearcher on_diskIndex = this.loadOndiskIndex(
						maximumSub[0], status);
				if (on_diskIndex != null) {
					onDiskMaxSubs = on_diskIndex.maxSubgraphs(fastSu,
							status);
					if (onDiskMaxSubs != null && onDiskMaxSubs.get(0) == -1) {
						onDisk = true;
						IGraphFetcher answerFetcher = this.on_diskFetcher
								.getPosting(
										maximumSub[0] + "_"
												+ onDiskMaxSubs.get(1),
										status);
						answer = answerFetcher.getAllGraphs(status);
						status.setVerifiedCount(0);
					}
				}
			}
			if (!loadOnDisk || !onDisk) {
				IGraphFetcher candidateFetcher = this.in_memFetcher.getJoin(
						maxSubgraphs, status);
				IGraphFetcher trueFetcher = null;
				if (superGraphs != null && superGraphs.size() != 0) {
					trueFetcher = this.in_memFetcher.getUnion(superGraphs,
							status);
					candidateFetcher = candidateFetcher.remove(trueFetcher);
				}
				status.setVerifiedCount(candidateFetcher.size());
				answer = this.verifier.verify(query, candidateFetcher, true,
						status);
				if (trueFetcher != null && trueFetcher.size() > 0) {
					answer.addAll(trueFetcher.getAllGraphs(status));
				}
			}
		}
		status.setTrueAnswerCount(answer.size());
		return answer;
	}

	private LindexSearcher loadOndiskIndex(int in_memoryFeatureID,
			SearchStatus status) {
		long start = System.currentTimeMillis();
		LindexSearcher on_diskIndex = null;
		try {
			on_diskIndex = LindexConstructor.loadSearcher(baseName,
					getOnDiskIndexName(in_memoryFeatureID));
		} catch (IOException e) {
			e.printStackTrace();
		}
		boolean loadSuccess = false;
		status.addFilteringTime(System.currentTimeMillis() - start);
		if (loadSuccess == false)
			return null;
		return on_diskIndex;
	}

	/********** This part will be replace to configuration file latter ***************/
	private static String onDiskBase = "onDiskIndex/";

	public static String getOnDiskIndexName(int id) {
		return onDiskBase + id;
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
}
