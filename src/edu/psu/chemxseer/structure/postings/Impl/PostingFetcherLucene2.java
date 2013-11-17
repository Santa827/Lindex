package edu.psu.chemxseer.structure.postings.Impl;

import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import de.parmol.parsers.GraphParser;
import edu.psu.chemxseer.structure.postings.Interface.IGraphFetcher;
import edu.psu.chemxseer.structure.postings.Interface.IPostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchableIndexBaseInterface;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;

/**
 * Each Posting List is partitioned into "direct" & "indirect" postings The join
 * operation is on direct part of the postings The union operation is on the
 * "whole" postings The direct return is on the "whole" postings Corresponds to
 * the PostingBuilderLuceneVectorizerPartition
 * 
 * @author dayuyuan
 * 
 */
public class PostingFetcherLucene2 extends PostingFetcherLucene implements
		IPostingFetcher {

	public PostingFetcherLucene2(String lucenePath, int dbSize,
			GraphParser gParser, boolean lucene_in_mem) {
		super(lucenePath, dbSize, gParser, lucene_in_mem);
	}

	/**
	 * Either d or i:
	 * 
	 * @param featureID
	 * @param TimeComponent
	 * @return
	 */
	@Override
	public IGraphFetcher getPosting(int featureID, SearchStatus status) {
		String byteString = new Integer(featureID).toString();
		Term queryTerm_d = new Term("subGraphs_d", byteString);
		Term queryTerm_i = new Term("subGraphs_i", byteString);
		TermQuery termQ_i = new TermQuery(queryTerm_i);
		TermQuery termQ_d = new TermQuery(queryTerm_d);
		BooleanQuery bQuery = new BooleanQuery();
		bQuery.add(termQ_d, Occur.SHOULD);
		bQuery.add(termQ_i, Occur.SHOULD);
		IGraphFetcher result = this.searchIndex(bQuery, status);
		// if(result.size()==0)
		// System.out.println("testLala");
		return result;
	}

	@Override
	public IGraphFetcher getPosting(String featureString, SearchStatus status) {
		Term queryTerm_d = new Term("subGraphs_d", featureString);
		Term queryTerm_i = new Term("subGraphs_i", featureString);
		TermQuery termQ_i = new TermQuery(queryTerm_i);
		TermQuery termQ_d = new TermQuery(queryTerm_d);
		BooleanQuery bQuery = new BooleanQuery();
		bQuery.add(termQ_d, Occur.SHOULD);
		bQuery.add(termQ_i, Occur.SHOULD);
		return this.searchIndex(bQuery, status);

	}

	/**
	 * Jond of d
	 * 
	 * @param featureIDs
	 * @param TimeComponent
	 * @return
	 */
	@Override
	public IGraphFetcher getJoin(List<Integer> featureIDs, SearchStatus status) {
		BooleanQuery bQuery = new BooleanQuery();
		for (int i = 0; i < featureIDs.size(); i++) {
			String byteString = featureIDs.get(i).toString();
			Term queryTerm = new Term("subGraphs_d", byteString);
			TermQuery luceneQuery = new TermQuery(queryTerm);
			bQuery.add(luceneQuery, Occur.MUST);
		}

		return this.searchIndex(bQuery, status);
	}

	@Override
	public IGraphFetcher getJoin(String[] graphStrings, SearchStatus status) {
		BooleanQuery bQuery = new BooleanQuery();
		for (int i = 0; i < graphStrings.length; i++) {
			Term queryTerm = new Term("subGraphs_d", graphStrings[i]);
			TermQuery luceneQuery = new TermQuery(queryTerm);
			bQuery.add(luceneQuery, Occur.MUST);
		}

		return this.searchIndex(bQuery, status);
	}

	/**
	 * Either d or i:
	 * 
	 * @param featureIDs
	 * @param TimeComponent
	 * @return
	 */
	@Override
	public IGraphFetcher getUnion(List<Integer> featureIDs, SearchStatus status) {
		BooleanQuery bQuery = new BooleanQuery();
		for (int i = 0; i < featureIDs.size(); i++) {
			String byteString = featureIDs.get(i).toString();
			Term queryTerm = new Term("subGraphs_d", byteString);
			TermQuery luceneQuery = new TermQuery(queryTerm);
			bQuery.add(luceneQuery, Occur.SHOULD);
		}
		for (int i = 0; i < featureIDs.size(); i++) {
			String byteString = featureIDs.get(i).toString();
			Term queryTerm = new Term("subGraphs_i", byteString);
			TermQuery luceneQuery = new TermQuery(queryTerm);
			bQuery.add(luceneQuery, Occur.SHOULD);
		}

		IGraphFetcher result = this.searchIndex(bQuery, status);
		// if(result.size() == 0)
		// System.out.println("testLala");
		return result;
	}

	@Override
	public PostingBuilderMem loadPostingIntoMemory(SearchableIndexBaseInterface indexSearcher) {
		// TODO Auto-generated method stub
		return null;
	}
}
