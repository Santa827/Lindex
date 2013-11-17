package edu.psu.chemxseer.structure.experiment;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import de.parmol.parsers.GraphParser;
import edu.psu.chemxseer.structure.postings.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.postings.Impl.Graphs;
import edu.psu.chemxseer.structure.postings.Interface.IGraphs;
import edu.psu.chemxseer.structure.factory.MyFactory;
import edu.psu.chemxseer.structure.subsearch.FGindex.SubSearch_FGindex;
import edu.psu.chemxseer.structure.subsearch.FGindex.SubSearch_FGindexBuilder;
import edu.psu.chemxseer.structure.subsearch.Gindex.SubSearch_Gindex;
import edu.psu.chemxseer.structure.subsearch.Gindex.SubSearch_GindexBuilder;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureFactory.FeatureFactoryType;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorFG;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorG;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorL;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeaturesWoPostings;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeaturesWoPostingsRelation;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeaturesWithPostings;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IFeature;
import edu.psu.chemxseer.structure.subsearch.Interfaces.ISearcher;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubSearch_LindexBuilder;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubSearch_LindexPlusBuilder;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubSearch_LindexSimpleBuilder;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubSearch_LindexSimplePlusBuilder;
import edu.psu.chemxseer.structure.subsearch.QuickSI.SubSearch_QuickSIBuilder;
import edu.psu.chemxseer.structure.subsearch.TreeDelta.SubSearch_TreeDelta;
import edu.psu.chemxseer.structure.subsearch.TreeDelta.SubSearch_TreeDeltaBuilder;

/**
 * The class run the basic Experiment with AIDS 40K graphs
 * 
 * @author dayuyuan
 * 
 */
public class BasicExpBuilder {
	protected String dbFileName;
	protected GraphParser dbParser;
	protected String baseName;

	/**
	 * Build the Index for Basic Experiment
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void main(String[] args) throws IOException, ParseException {
		String dbFileName = "/data/santa/VLDBJExp/BasicExp/DBFile";
		String baseName = "/data/santa/VLDBJExp/BasicExp/";
		GraphParser dbParser = MyFactory.getSmilesParser();
		BasicExpBuilder basic = new BasicExpBuilder(dbFileName, dbParser,
				baseName);
		double minFreq = 0.01;
		
		System.out.println("DF");
		basic.buildGIndexDF(minFreq, 0);
		basic.buildLindexDF(0);

		System.out.println("DT");
		basic.buildGindexDT();
		basic.buildLindexDT();
		basic.buildSwiftIndex();

		System.out.println("TCFG");
		basic.buildFGindex(minFreq, 0);
		basic.buildLindexAdvTCFG(0);

		System.out.println("MimR");
		basic.buildGindexMimR();
		basic.buildLindexMimR();
		basic.buildLindexAdvMimR();

		System.out.println();
	}

	public BasicExpBuilder(String dbFileName, GraphParser dbParser,
			String baseName) {
		this.dbFileName = dbFileName;
		this.dbParser = dbParser;
		this.baseName = baseName;
	}

	public ISearcher buildGIndexDF(double minFreq, int flag)
			throws IOException, ParseException {
		// 0. Create Folder
		String temp = baseName + "GindexDF" + flag + "/";
		if (flag == 0)
			temp = baseName + "GindexDF/";
		File folder = new File(temp);
		if (!folder.exists())
			folder.mkdirs();
		// 1. Mine Features
		FeaturesWithPostings candidateFeatures = FeatureProcessorG
				.frequentSubgraphMining(dbFileName, temp + "patterns", temp
						+ "postings", minFreq, 4, 10, dbParser);
		// FeaturesWithPostings candidateFeatures = new
		// FeaturesWithPostings(temp +
		// "postings",
		// new FeaturesWoPostings(temp + "patterns",
		// MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature)));
		// 2. Build Index
		SubSearch_GindexBuilder builder = new SubSearch_GindexBuilder();
		SubSearch_Gindex gIndex = builder.buildIndex(candidateFeatures,
				new GraphDatabase_OnDisk(dbFileName, dbParser), false, temp,
				temp + "GPatterns", temp + "GPostings", dbParser);
		return gIndex;
	}

	public void buildLindexDF(int flag) throws CorruptIndexException,
			LockObtainFailedException, IOException, ParseException {
		// 0. Create Folder
		String temp = baseName + "LindexDF" + flag + "/";
		if (flag == 0)
			temp = baseName + "LindexDF/";
		File folder = new File(temp);
		if (!folder.exists())
			folder.mkdirs();
		// 1. Use DF features
		String gIndexPatterns = baseName + "GindexDF" + flag + "/GPatterns";
		if (flag == 0)
			gIndexPatterns = baseName + "GindexDF/GPatterns";
		FeaturesWoPostings<IFeature> features = FeaturesWoPostings
				.LoadFeaturesWoPostings(gIndexPatterns, MyFactory
						.getFeatureFactory(FeatureFactoryType.SingleFeature));
		FeaturesWoPostingsRelation<IFeature> lindexFeatures = FeaturesWoPostingsRelation
				.buildFeaturesWoPostingsRelation(features);
		SubSearch_LindexSimpleBuilder builder = new SubSearch_LindexSimpleBuilder();
		builder.buildIndex(lindexFeatures, new GraphDatabase_OnDisk(dbFileName,
				dbParser), temp, dbParser);
	}

	public void buildGindexDT() throws IOException, ParseException {
		// 0. Create Folder
		String temp = baseName + "GindexDT/";
		File folder = new File(temp);
		if (!folder.exists())
			folder.mkdirs();
		// 1. Mine Features
		FeaturesWithPostings candidateFeatures = FeatureProcessorG
				.frequentSubtreeMining(dbFileName, temp + "patterns", temp
						+ "postings", 0.05, 4, 10, dbParser);
		// 2. Build Index
		SubSearch_GindexBuilder builder = new SubSearch_GindexBuilder();
		builder.buildIndex(candidateFeatures, new GraphDatabase_OnDisk(
				dbFileName, dbParser), false, temp, temp + "GPatterns", temp
				+ "GPostings", dbParser);
	}

	public void buildLindexDT() throws CorruptIndexException,
			LockObtainFailedException, IOException, ParseException {
		// 0. Create Folder
		String temp = baseName + "LindexDT/";
		File folder = new File(temp);
		if (!folder.exists())
			folder.mkdirs();
		// 1. Use DF features
		FeaturesWoPostings<IFeature> normalFeatures = FeaturesWoPostings
				.LoadFeaturesWoPostings(
						baseName + "GindexDT/GPatterns",
						MyFactory
								.getFeatureFactory(FeatureFactoryType.SingleFeature));
		FeaturesWoPostingsRelation<IFeature> features = FeaturesWoPostingsRelation
				.buildFeaturesWoPostingsRelation(normalFeatures);
		SubSearch_LindexBuilder builder = new SubSearch_LindexBuilder();
		builder.buildIndex(features, new GraphDatabase_OnDisk(dbFileName,
				dbParser), temp, dbParser, false);
	}

	public void buildSwiftIndex() throws CorruptIndexException,
			LockObtainFailedException, IOException {
		// 0. Create Folder
		String temp = baseName + "SwiftIndex/";
		File folder = new File(temp);
		if (!folder.exists())
			folder.mkdirs();
		// 1. Use DF features
		FeaturesWoPostings<IFeature> features = FeaturesWoPostings
				.LoadFeaturesWoPostings(
						baseName + "GindexDT/GPatterns",
						MyFactory
								.getFeatureFactory(FeatureFactoryType.SingleFeature));
		SubSearch_QuickSIBuilder builder = new SubSearch_QuickSIBuilder();
		builder.buildIndex(features, new GraphDatabase_OnDisk(dbFileName,
				dbParser), temp, dbParser, false);
	}

	public SubSearch_FGindex buildFGindex(double minFreq, int flag)
			throws IOException {
		// 0. Create Folder
		String temp = baseName + "FGindex" + flag + "/";
		if (flag == 0)
			temp = baseName + "FGindex/";
		File folder = new File(temp);
		if (!folder.exists())
			folder.mkdirs();
		// 1. Mine Frequent Features
		FeaturesWithPostings features = FeatureProcessorFG
				.frequentSubgraphMining(dbFileName, temp + "patterns", temp
						+ "postings", minFreq, 10, dbParser);
		// 2. Mine Frequent TCFG & Construct Index
		SubSearch_FGindexBuilder builder = new SubSearch_FGindexBuilder();
		return builder.buildIndex(features.getFeatures(),
				new GraphDatabase_OnDisk(dbFileName, dbParser), temp, dbParser);
	}

	public void buildLindexAdvTCFG(int flag) throws IOException, ParseException {
		// 0. Create Folder
		String temp = baseName + "LindexTCFG" + flag + "/";
		if (flag == 0)
			temp = baseName + "LindexTCFG/";
		File folder = new File(temp);
		if (!folder.exists())
			folder.mkdirs();
		// 1. Mine-edge features & load previous mined FG features
		String fgFeatures = baseName + "FGindex" + flag
				+ "/StatusRecordedFeatures";
		if (flag == 0)
			fgFeatures = baseName + "FGindex/StatusRecordedFeatures";
		FeaturesWithPostings edgeFeatures = FeatureProcessorL
				.findEdgeOneFeatures(dbFileName, temp + "edge", temp
						+ "edgePosting", dbParser);
		FeaturesWoPostings<IFeature> freqFeatures = FeaturesWoPostings
				.LoadFeaturesWoPostings(fgFeatures, MyFactory
						.getFeatureFactory(FeatureFactoryType.SingleFeature));

		// 2. Combine this two features
		FeaturesWoPostings<IFeature> features = FeatureProcessorL
				.mergeFeatures(freqFeatures, edgeFeatures.getFeatures());
		FeaturesWoPostingsRelation<IFeature> selectedFeatures = FeaturesWoPostingsRelation
				.buildFeaturesWoPostingsRelation(new FeaturesWoPostings<IFeature>(
						features.getSelectedFeatures(), false));

		FeaturesWoPostings<IFeature> onDiskFeatures = new FeaturesWoPostings<IFeature>(
				features.getUnSelectedFeatures(), false);
		// 3. Build the Lindex-plus index with all those features
		SubSearch_LindexSimplePlusBuilder builder = new SubSearch_LindexSimplePlusBuilder();

		builder.buildIndex(selectedFeatures, onDiskFeatures,
				new GraphDatabase_OnDisk(dbFileName, dbParser), temp, dbParser);
	}

	public void buildLindexTCFG() throws IOException, ParseException {
		// 0. Create Folder
		String temp = baseName + "LindexTCFG/";
		File folder = new File(temp);
		if (!folder.exists())
			folder.mkdirs();
		// 1. Mine-edge features & load previous mined FG features
		FeaturesWoPostings<IFeature> edgeFeatures = FeatureProcessorL
				.findEdgeOneFeatures(dbFileName, temp + "edge",
						temp + "edgePosting", dbParser).getFeatures();
		FeaturesWoPostings<IFeature> subgraphFeatures = FeaturesWoPostings
				.LoadFeaturesWoPostings(
						baseName + "FGindex/patterns",
						MyFactory
								.getFeatureFactory(FeatureFactoryType.SingleFeature));
		// 2. Combine this two features
		FeaturesWoPostings<IFeature> features = FeatureProcessorL
				.mergeFeatures(subgraphFeatures, edgeFeatures);
		FeaturesWoPostingsRelation<IFeature> selectedFeatures = FeaturesWoPostingsRelation
				.buildFeaturesWoPostingsRelation(new FeaturesWoPostings<IFeature>(
						features.getSelectedFeatures(), false));
		// 3. Build the Lindex-plus index with all those features
		SubSearch_LindexBuilder builder = new SubSearch_LindexBuilder();
		builder.buildIndex(selectedFeatures, new GraphDatabase_OnDisk(
				dbFileName, dbParser), temp, dbParser, false);
	}

	public void buildGindexMimR() throws IOException, ParseException {
		// 0. Create Folder
		String temp = baseName + "GindexMimR/";
		File folder = new File(temp);
		if (!folder.exists())
			folder.mkdirs();
		// 1. Merge MimR Features with edge Patterns
		FeaturesWoPostings<IFeature> edgeFeatures = FeaturesWoPostings
				.LoadFeaturesWoPostings(baseName + "LindexTCFG/edge", MyFactory
						.getFeatureFactory(FeatureFactoryType.SingleFeature));

		FeaturesWoPostings<IFeature> mimrFeatures = FeaturesWoPostings
				.LoadFeaturesWoPostings(temp + "mimr", MyFactory
						.getFeatureFactory(FeatureFactoryType.SingleFeature));
		// set all mimr Features selected
		mimrFeatures.setAllSelected();
		FeaturesWoPostings<IFeature> subgraphFeatures = FeatureProcessorL
				.mergeFeatures(mimrFeatures, edgeFeatures);
		subgraphFeatures.saveFeatures(temp + "patterns");

		// 2. Build the Gindex Exhaust
		SubSearch_GindexBuilder builder = new SubSearch_GindexBuilder();
		builder.buildIndexWithoutFeatureSelection(FeaturesWoPostingsRelation
				.buildFeaturesWoPostingsRelation(subgraphFeatures),
				new GraphDatabase_OnDisk(dbFileName, dbParser), true, temp,
				dbParser, false);

	}

	public void buildLindexMimR() throws IOException, ParseException {
		// 0. Create Folder
		String temp = baseName + "LindexMimR/";
		File folder = new File(temp);
		if (!folder.exists())
			folder.mkdirs();
		// 1. Load MimR Features
		FeaturesWoPostings<IFeature> subgraphFeatures = FeaturesWoPostings
				.LoadFeaturesWoPostings(
						baseName + "GindexMimR/patterns",
						MyFactory
								.getFeatureFactory(FeatureFactoryType.SingleFeature));
		// 2. Build the Lindex MimR
		SubSearch_LindexBuilder builder = new SubSearch_LindexBuilder();
		builder.buildIndex(FeaturesWoPostingsRelation
				.buildFeaturesWoPostingsRelation(subgraphFeatures),
				new GraphDatabase_OnDisk(dbFileName, dbParser), temp, dbParser,
				false);
	}

	public void buildLindexAdvMimR() throws IOException, ParseException {
		// 0. Create Folder
		String temp = baseName + "LindexMimRPlus/";
		File folder = new File(temp);
		if (!folder.exists())
			folder.mkdirs();
		// 1. Load MimR Features
		FeaturesWoPostings<IFeature> subgraphFeatures = FeaturesWoPostings
				.LoadFeaturesWoPostings(
						baseName + "GindexMimR/patterns",
						MyFactory
								.getFeatureFactory(FeatureFactoryType.SingleFeature));

		FeaturesWoPostings<IFeature> allFreqFeatures = FeaturesWoPostings
				.LoadFeaturesWoPostings(
						baseName + "FGindex/patterns",
						MyFactory
								.getFeatureFactory(FeatureFactoryType.SingleFeature));
		FeaturesWoPostings<IFeature> onDiskFeatures = FeatureProcessorL
				.removeFeatures(allFreqFeatures, subgraphFeatures);
		// 2. Build the Index
		SubSearch_LindexPlusBuilder builder = new SubSearch_LindexPlusBuilder();
		builder.buildIndex(FeaturesWoPostingsRelation
				.buildFeaturesWoPostingsRelation(subgraphFeatures),
				onDiskFeatures, new GraphDatabase_OnDisk(dbFileName, dbParser),
				temp, dbParser, false);
	}

	public void buildGindexTreeDelta() throws IOException {
		// 0. Create Folder
		String temp = baseName + "GindexTreeDelta/";
		File folder = new File(temp);
		if (!folder.exists())
			folder.mkdirs();
		// 1. Load the GindexTree index
		GraphDatabase_OnDisk gDB = new GraphDatabase_OnDisk(dbFileName,
				dbParser);
		SubSearch_GindexBuilder gBuilder = new SubSearch_GindexBuilder();
		SubSearch_Gindex treeIndex = gBuilder.loadIndex(gDB, false, baseName
				+ "GindexDT/", dbParser, false);
		IGraphs testQueries = new Graphs(
				FeaturesWoPostings.LoadFeaturesWoPostings(baseName
						+ "Queries/UniformQueryTrain", MyFactory
						.getFeatureFactory(FeatureFactoryType.SingleFeature)));
		// 2. Build the GindexTreeDelta index
		SubSearch_TreeDeltaBuilder tBuilder = new SubSearch_TreeDeltaBuilder();
		FeaturesWoPostings<IFeature> deltaFeatures = tBuilder.mineDelta(gDB, treeIndex,
				testQueries, temp);
		tBuilder.buildIndex(treeIndex, deltaFeatures, gDB, temp, dbParser, false);
	}

	public void buildLindexTreeDelta() throws IOException, ParseException {
		// 0. Create Folder
		String temp = baseName + "LindexTreeDelta/";
		File folder = new File(temp);
		if (!folder.exists())
			folder.mkdirs();
		// 1. Load the GindexTree features & delta features and merge them
		FeaturesWoPostings<IFeature> tFeatures = FeaturesWoPostings
				.LoadFeaturesWoPostings(
						baseName + "GindexDT/GPatterns",
						MyFactory
								.getFeatureFactory(FeatureFactoryType.SingleFeature));

		FeaturesWoPostings<IFeature> dFeatures = FeaturesWoPostings
				.LoadFeaturesWoPostings(baseName + "GindexTreeDelta/"
						+ SubSearch_TreeDelta.getDeltaFeature(), MyFactory
						.getFeatureFactory(FeatureFactoryType.SingleFeature));

		FeaturesWoPostings<IFeature> totalFeatures = tFeatures.mergeFeatures(
				dFeatures, temp + "patterns");
		// 2. Build Lindex
		SubSearch_LindexBuilder builder = new SubSearch_LindexBuilder();
		builder.buildIndex(FeaturesWoPostingsRelation
				.buildFeaturesWoPostingsRelation(totalFeatures),
				new GraphDatabase_OnDisk(dbFileName, dbParser), temp, dbParser,
				false);
	}

	public void freMem() {
		Runtime r = Runtime.getRuntime();
		for (int i = 0; i < 100; i++)
			r.gc();
	}
}
