package edu.psu.chemxseer.structure.postings.Interface;

import java.util.List;

import edu.psu.chemxseer.structure.postings.Impl.PostingBuilderMem;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchableIndexBaseInterface;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;

/**
 * Interface for fetching postings prefix: Given several fIDs, do operation of
 * the postings of those pIDs return GraphFetcherPrefix
 * 
 * @author dayuyuan
 * 
 */
public interface IPostingFetcherPrefix extends IPostingFetcher0 {
	/**
	 * Given the featureID, retrieve the postings of this feature
	 * TimeComponent[0] = posting retrieval time
	 * 
	 * @param featureID
	 * @param TimeComponent
	 *            [0]
	 * @return
	 */
	public IGraphFetcherPrefix getPosting(int featureID, SearchStatus status);

	@Override
	public int[] getPostingID(int featureID);

	/**
	 * Given the featureString, retrieve the posting of this featureString
	 * TimeComponent[0] = posting retrieval time
	 * 
	 * @param featureString
	 * @param TimeComponent
	 * @return
	 */
	public IGraphFetcherPrefix getPosting(String featureString,
			SearchStatus status);

	/**
	 * Given the featureIDs, retrieve the Union of those features postings
	 * TimeComponent[0] = posting retrieval time
	 * 
	 * @param featureIDs
	 * @param TimeComponent
	 *            [0]
	 * @return
	 */
	public IGraphFetcherPrefix getUnion(List<Integer> featureIDs,
			SearchStatus status);

	/**
	 * Given the featureIDs, retrieve the Join of those features postings;
	 * TimeComponent[0] = posting retrieval time
	 * 
	 * @param featureIDs
	 * @param TimeComponent
	 *            [0]
	 * @return
	 */
	public IGraphFetcherPrefix getJoin(List<Integer> featureIDs,
			SearchStatus status);

	/**
	 * TimeComponent[0] = posting retrieval time
	 * 
	 * @param featureStrings
	 * @param TimeComponent
	 * @return
	 */
	public IGraphFetcherPrefix getJoin(String[] featureStrings,
			SearchStatus status);

	/**
	 * Given the featureIDs, retrieve the compliment of the Union of those
	 * features Postings
	 * 
	 * @param featureIDs
	 * @param TimeComponent
	 * @return
	 */
	public IGraphFetcherPrefix getComplement(List<Integer> featureIDs,
			SearchStatus status);

	/**
	 * Load all the postings into memory for fast access
	 * 
	 * @param indexSearcher
	 * @return
	 */
	public PostingBuilderMem loadPostingIntoMemory(SearchableIndexBaseInterface indexSearcher);

	/**
	 * Return the size of the database
	 * 
	 * @return
	 */
	@Override
	public int getDBSize();
}
