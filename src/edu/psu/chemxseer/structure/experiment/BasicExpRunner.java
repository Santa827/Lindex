package edu.psu.chemxseer.structure.experiment;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.math.MathException;

import de.parmol.graph.Graph;
import de.parmol.parsers.GraphParser;
import edu.psu.chemxseer.structure.factory.InFrequentQueryGenerater;
import edu.psu.chemxseer.structure.factory.MyFactory;
import edu.psu.chemxseer.structure.postings.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.postings.Interface.IGraphResult;
import edu.psu.chemxseer.structure.postings.Interface.IGraphs;
import edu.psu.chemxseer.structure.subsearch.FGindex.SubSearch_FGindex;
import edu.psu.chemxseer.structure.subsearch.FGindex.SubSearch_FGindexBuilder;
import edu.psu.chemxseer.structure.subsearch.Gindex.SubSearch_GindexBuilder;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeaturesWoPostings;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IFeature;
import edu.psu.chemxseer.structure.subsearch.Interfaces.ISearcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubSearch_LindexSimpleBuilder;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubSearch_LindexSimplePlusBuilder;
import edu.psu.chemxseer.structure.subsearch.QuickSI.SubSearch_QuickSIBuilder;

public class BasicExpRunner extends BasicExpBuilder {

	// /**
	// * Run the Experiment For Basic Exp
	// * @param args
	// * @throws IOException
	// * @throws ParseException
	// */
	// public static void main(String[] args) throws IOException,
	// ParseException{
	//
	// // String dbFileName = "/data/santa/VLDBJExp/BasicExp/DBFile";
	// // String baseName = "/data/santa/VLDBJExp/BasicExp/";
	// String dbFileName =
	// "/home/duy113/Experiment/LindexJournal/BasicExp/DBFile";
	// String baseName = "/home/duy113/Experiment/LindexJournal/BasixExp/";
	// GraphParser dbParser = MyFactory.getSmilesParser();
	// BasicExpRunner exp = new BasicExpRunner(dbFileName, dbParser, baseName);
	// //Generate Queries:
	// try {
	// exp.genQueries(true);
	// } catch (MathException e) {
	// e.printStackTrace();
	// }
	// // exp.buildGindexTreeDelta();
	// // exp.buildLindexTreeDelta();
	//
	// //
	// // SubgraphSearch searcher = null;
	// //
	// // String queryFile =
	// "/home/duy113/Experiment/VLDBJExp/BasicExp/Queries/NormalQueryTest";
	// // InFrequentQueryGenerater qGen = new
	// InFrequentQueryGenerater(queryFile);
	// // IGraphs[] queries = qGen.loadInfrequentQueries();
	// //
	// // for(int i = 1; i< 11; i++){
	// // searcher = exp.loadIndex(i);
	// // exp.runExp(queries, searcher);
	// // }
	// }
	//
	public void genQueries(boolean genTrainQueries) throws IOException,
			ParseException, MathException {
		GraphDatabase_OnDisk gDB = new GraphDatabase_OnDisk(this.dbFileName,
				this.dbParser);
		File QueryFolder = new File(baseName + "Queries");
		if (!QueryFolder.exists())
			QueryFolder.mkdir();

		SubSearch_FGindexBuilder builder = new SubSearch_FGindexBuilder();
		SubSearch_FGindex searcher = builder.loadIndex(gDB, baseName
				+ "FGindex/", MyFactory.getDFSCoder(), false);

		String queryFileName1 = baseName + "Queries/UniformQueryTest";

		InFrequentQueryGenerater.generateInFrequentQueriesNormal(4, 24, 1, gDB,
				searcher.getEdgeIndex(), searcher,
				queryFileName1);

		String queryFileName2 = baseName + "Queries/NormalQueryTest";
		InFrequentQueryGenerater.generateInFrequentQueriesUniform(4, 24, 1,
				gDB, searcher.getEdgeIndex(), queryFileName2);

		if (genTrainQueries) {
			String queryFileName3 = baseName + "Queries/NormalQueryTrain";
			InFrequentQueryGenerater.generateInFrequentQueriesNormal2(4, 24,
					1000, gDB, searcher.getEdgeIndex(),
					searcher, queryFileName3);

			String queryFileName4 = baseName + "Queries/UniformQueryTrain";
			InFrequentQueryGenerater.generateInFrequentQueriesUniform2(4, 24, 50,
					gDB, searcher.getEdgeIndex(), queryFileName4);
		}

	}

	public BasicExpRunner(String dbFileName, GraphParser dbParser,
			String baseName) {
		super(dbFileName, dbParser, baseName);
	}

	public ISearcher loadIndex(int i) throws IOException {
		GraphDatabase_OnDisk gDB = new GraphDatabase_OnDisk(dbFileName,
				dbParser);
		ISearcher searcher = null;
		SubSearch_GindexBuilder Gbuilder = new SubSearch_GindexBuilder();
		SubSearch_LindexSimpleBuilder Lbuilder = new SubSearch_LindexSimpleBuilder();
		SubSearch_LindexSimplePlusBuilder LPbuilder = new SubSearch_LindexSimplePlusBuilder();
		SubSearch_QuickSIBuilder Qbuilder = new SubSearch_QuickSIBuilder();
		SubSearch_FGindexBuilder Fbuilder = new SubSearch_FGindexBuilder();
		boolean lucene_im_mem = false;
		switch (i) {
		case 1:
			System.out.println("Load GindexDF");
			searcher = Gbuilder.loadIndex(gDB, false, baseName + "GindexDF/",
					dbParser, lucene_im_mem);
			break;
		case 2:
			System.out.println("Load LindexDF");
			searcher = Lbuilder.loadIndex(gDB, baseName + "LindexDF/",
					dbParser, lucene_im_mem);
			break;
		case 3:
			System.out.println("Load GindexDT");
			searcher = Gbuilder.loadIndex(gDB, false, baseName + "GindexDT/",
					dbParser, lucene_im_mem);
			break;
		case 4:
			System.out.println("Load LindexDT");
			searcher = Lbuilder.loadIndex(gDB, baseName + "LindexDT/",
					dbParser, lucene_im_mem);
			break;
		case 5:
			System.out.println("Load SwiftIndex");
			searcher = Qbuilder.loadIndex(gDB, baseName + "SwiftIndex/",
					dbParser, lucene_im_mem);
			break;
		case 6:
			System.out.println("Load FGindex");
			searcher = Fbuilder.loadIndex(gDB, baseName + "FGindex/", dbParser,
					lucene_im_mem);
			break;
		case 7:
			System.out.println("Load LindexTCFG");
			searcher = LPbuilder.loadIndex(gDB, baseName + "LindexTCFG/",
					dbParser, lucene_im_mem);
			break;
		case 8:
			System.out.println("Load GindexMimR");
			searcher = Gbuilder.loadIndex(gDB, true, baseName + "GindexMimR/",
					dbParser, lucene_im_mem);
			break;
		case 9:
			System.out.println("Load LindexMimR");
			searcher = Lbuilder.loadIndex(gDB, baseName + "LindexMimR/",
					dbParser, lucene_im_mem);
			break;
		case 10:
			System.out.println("Load LindexMimRPlus");
			searcher = LPbuilder.loadIndex(gDB, baseName + "LindexMimRPlus/",
					dbParser, lucene_im_mem);
			break;
		}
		return searcher;
	}

	public void runExp(IGraphs[] queries, ISearcher searcher)
			throws IOException, ParseException {
		SearchStatus searchResult = new SearchStatus();
		SearchStatus tempResult = new SearchStatus();
		for (int i = 0; i < queries.length; i++) {
			searchResult.refresh();
			for (int j = 0; j < queries[i].getGraphNum(); j++) {
				tempResult.refresh();
				Graph g = queries[i].getGraph(j);
				List<IGraphResult> answers = searcher.getAnswer(g, tempResult);
				if (answers.size() == 0)
					continue;
				searchResult.addPostFetchingTime(tempResult
						.getPostFetchingTime());
				searchResult.addDbLoadingTime(tempResult.getDbLoadingTime());
				searchResult.addFilteringTime(tempResult.getFilteringTime());
				searchResult.addVerifyTime(tempResult.getVerifyTime());
				searchResult.addVerifiedCount(tempResult.getVerifiedCount());
				searchResult
						.addTrueAnswerCount(tempResult.getTrueAnswerCount());
			}
			System.out.print("For queries: " + (i + 4) + "\t");
			System.out.println(searchResult.toString());
		}
	}

	public void runExp2(FeaturesWoPostings<IFeature> queries, ISearcher searcher)
			throws IOException, ParseException {
		SearchStatus searchResult = new SearchStatus();
		SearchStatus tempResult = new SearchStatus();

		for (int i = 0; i < queries.getfeatureNum(); i++) {
			tempResult.refresh();
			Graph g = queries.getFeature(i).getFeatureGraph();
			List<IGraphResult> answers = searcher.getAnswer(g, tempResult);
			if (answers.size() == 0)
				continue;
			searchResult.addPostFetchingTime(tempResult.getPostFetchingTime());
			searchResult.addDbLoadingTime(tempResult.getDbLoadingTime());
			searchResult.addFilteringTime(tempResult.getFilteringTime());
			searchResult.addVerifyTime(tempResult.getVerifyTime());
			searchResult.addVerifiedCount(tempResult.getVerifiedCount());
			searchResult.addTrueAnswerCount(tempResult.getTrueAnswerCount());
		}
		System.out.println(searchResult.toString());
	}
}
