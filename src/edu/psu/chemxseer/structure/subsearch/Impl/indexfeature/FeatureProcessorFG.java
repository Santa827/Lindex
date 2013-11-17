package edu.psu.chemxseer.structure.subsearch.Impl.indexfeature;

import de.parmol.parsers.GraphParser;
import edu.psu.chemxseer.structure.parmolExtension.GSpanMiner_Frequent;
import edu.psu.chemxseer.structure.postings.Interface.IGraphDatabase;

/**
 * The Feature Processor Steps [Feature Mining] for FGFeatures (raw)
 * 
 * @author dayuyuan
 * 
 */
public class FeatureProcessorFG {
	/**
	 * When there are too many frequent subgraphs that will take forever to mine, 
	 * we use the sampling techniques to conquer the difficulty 
	 * At each time, the choose of growing a pattern depends upon some random generator
	 * @param gDBFileName
	 * @param featureFileName
	 * @param postingFileName
	 * @param minimumFrequency
	 * @param maxNonSelectDepth
	 * @param gParser
	 * @param prob: taken both frequency & edge into consideration. 
	 * 
	 * @return
	 */
	public static FeaturesWithPostings frequentSubgraphMiningProbK(String gDBFileName,
			String featureFileName, String postingFileName,
			double minimumFrequency, int maxNonSelectDepth, GraphParser gParser, double prob, int maxK) {

		String[] args = {
				"-minimumFrequencies=" + (-minimumFrequency),
				"-maximumFragmentSize=" + maxNonSelectDepth,
				"-graphFile=" + gDBFileName,
				"-closedFragmentsOnly=flase",
				"-outputFile=temp",
				"-parserClass=" + gParser.getClass().getName(),
				"-serializerClass=edu.psu.chemxseer.structure.iso.CanonicalDFS",
				"-memoryStatistics=false", "-debug=1" };
		return GSpanMiner_Frequent.gSpanMining(args, featureFileName,
				postingFileName, prob, maxK);
	}
	
	public static FeaturesWithPostings frequentSubgraphMining(String gDBFileName,
			String featureFileName, String postingFileName,
			double minimumFrequency, int maxNonSelectDepth, GraphParser gParser) {

		String[] args = {
				"-minimumFrequencies=" + (-minimumFrequency),
				"-maximumFragmentSize=" + maxNonSelectDepth,
				"-graphFile=" + gDBFileName,
				"-closedFragmentsOnly=flase",
				"-outputFile=temp",
				"-parserClass=" + gParser.getClass().getName(),
				"-serializerClass=edu.psu.chemxseer.structure.iso.CanonicalDFS",
				"-memoryStatistics=false", "-debug=1" };
		return GSpanMiner_Frequent.gSpanMining(args, featureFileName,
				postingFileName);
	}

	public static FeaturesWithPostings frequentSubgraphMining(IGraphDatabase gDB,
			String featureFileName, String postingFileName,
			double minimumFrequency, int maxNonSelectDepth, GraphParser gParser) {
		String[] args = {
				"-minimumFrequencies=" + (-minimumFrequency),
				"-maximumFragmentSize=" + maxNonSelectDepth,
				"-closedFragmentsOnly=flase",
				"-outputFile=temp",
				"-parserClass=" + gParser.getClass().getName(),
				"-serializerClass=edu.psu.chemxseer.structure.iso.CanonicalDFS",
				"-memoryStatistics=false", "-debug=0" };
		return GSpanMiner_Frequent.gSpanMining(gDB, args, featureFileName,
				postingFileName);
	}
}
