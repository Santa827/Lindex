package edu.psu.chemxseer.structure.subsearch.Gindex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.factory.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeaturesWoPostings;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeaturesWithPostings;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IFeature;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SearchStatus;
import edu.psu.chemxseer.structure.util.OrderedIntSet;

public class GindexSearcherConstructor {

	private Map<Integer, int[]> postings;

	/**
	 * For Index Construction, all features are stored in the in-memory index.
	 * The in-memory posting only stores postings for those 
	 * selected features that are marked "discriminative".
	 * 
	 * @param allFeatures
	 * @param exhaustSearch
	 */
	public GindexSearcher constructWithPostings(FeaturesWithPostings rawFeatures,
			boolean exhaustSearch) {
		int maxEdgeNum = 0;
		FeaturesWoPostings<IFeature> allFeatures = rawFeatures.getFeatures();
		boolean[] discriminative = new boolean[allFeatures.getfeatureNum()];
		for (int i = 0; i < discriminative.length; i++)
			discriminative[i] = false;

		this.postings = new HashMap<Integer, int[]>();
		HashMap<String, Integer> gHash = new HashMap<String, Integer>();
		for (int i = 0; i < allFeatures.getfeatureNum(); i++) {
			IFeature feature = allFeatures.getFeature(i);
			String gString = MyFactory.getDFSCoder().serialize(
					feature.getFeatureGraph());
			gHash.put(gString, i);
			if (feature.isSelected()) {
				discriminative[i] = true;
				if (maxEdgeNum < feature.getFeatureGraph().getEdgeCount())
					maxEdgeNum = feature.getFeatureGraph().getEdgeCount();
				// Populate the posting File for selected features only
				postings.put(i, rawFeatures.getPosting(feature));
			}
		}
		return new GindexSearcher(gHash, discriminative, maxEdgeNum,
				exhaustSearch);
	}

	/**
	 * No posting stored construction of GindexSearcher
	 * @param allFeatures
	 * @param exhaustSearch
	 */
	public static GindexSearcher constructWoPostings(
			FeaturesWoPostings<IFeature> allFeatures, boolean exhaustSearch) {
		int maxEdgeNum = 0;
		boolean[] discriminative = new boolean[allFeatures.getfeatureNum()];
		for (int i = 0; i < discriminative.length; i++)
			discriminative[i] = false;

		HashMap<String, Integer> gHash = new HashMap<String, Integer>();
		for (int i = 0; i < allFeatures.getfeatureNum(); i++) {
			IFeature feature = allFeatures.getFeature(i);
			String gString = MyFactory.getDFSCoder().serialize(
					feature.getFeatureGraph());
			gHash.put(gString, i);
			if (feature.isSelected()) {
				discriminative[i] = true;
				if (maxEdgeNum < feature.getFeatureGraph().getEdgeCount())
					maxEdgeNum = feature.getFeatureGraph().getEdgeCount();
			}
		}
		return new GindexSearcher(gHash, discriminative, maxEdgeNum,
				exhaustSearch);
	}
	/**
	 * Given the query graph (wrapped in the IFeature).
	 * Search on the GindexSearcher, and return all database graphs that are candidate
	 * supergraph of the query. (this is used during index construction)
	 * @param searcher
	 * @param candidateFeatures
	 * @param query
	 * @return
	 */
	public int[] getCandidateResultDuringConstruction(GindexSearcher searcher,
			FeaturesWithPostings candidateFeatures, IFeature query) {
		Graph g = query.getFeatureGraph();
		if (g == null)
			g = MyFactory.getDFSCoder().parse(query.getDFSCode(),
					MyFactory.getGraphFactory());
		OrderedIntSet candidates = new OrderedIntSet();

		List<Integer> subIndices = searcher.maxSubgraphs(g, new SearchStatus());

		if (subIndices.size() > 1) {
			candidates
					.add(this.getPosting(candidateFeatures, subIndices.get(0)));
			for (int i = 1; i < subIndices.size(); i++) {
				int index = subIndices.get(i);
				int[] postings = this.getPosting(candidateFeatures, index);
				candidates.join(postings);
			}
		} else if (subIndices.size() == 1)
			candidates
					.add(this.getPosting(candidateFeatures, subIndices.get(0)));

		if (candidates.size() < query.getFrequency())
			System.out.println("Error: in GindexM: getCandidateBuild: "
					+ candidates.size());
		return candidates.getItems();
	}

	private int[] getPosting(FeaturesWithPostings candidateFeatures, int fID) {
		if (this.postings == null) {
			return candidateFeatures.getPosting(fID);
		} else
			return this.postings.get(fID);
	}

	public void addDisriminativeTerm(GindexSearcher searcher,
			FeaturesWithPostings candidateFeatures, int fID, int featureEdgeNum)
			throws IOException {
		if (searcher.discriminative[fID] == false) {
			searcher.discriminative[fID] = true;
			this.postings.put(fID, candidateFeatures.getPosting(fID));
		} else
			System.out
					.println("An Exception in Add Dicriminative Term: the term is already discriminative");
		if (searcher.maxEdgeNum < featureEdgeNum)
			searcher.maxEdgeNum = featureEdgeNum;
	}


	public static void saveSearcher(GindexSearcher searcher, String baseName,
			String indexName) throws IOException {
		BufferedWriter gindexFileWriter = new BufferedWriter(new FileWriter(
				new File(baseName, indexName)));
		int num = 0;
		if (!searcher.exhaustSearch) {
			for (int i = 0; i < searcher.discriminative.length; i++)
				if (searcher.discriminative[i])
					num++;
			gindexFileWriter.write(num + " " + searcher.discriminative.length
					+ " " + searcher.maxEdgeNum + "\n");
		} else {
			gindexFileWriter.write(searcher.gHash.size() + " "
					+ searcher.gHash.size() + " " + searcher.maxEdgeNum + "\n");
		}

		Iterator<Entry<String, Integer>> iter = searcher.gHash.entrySet()
				.iterator();
		while (iter.hasNext()) {
			StringBuffer buf = new StringBuffer(1024);
			Entry<String, Integer> currentEntry = iter.next();
			buf.append(currentEntry.getKey());
			buf.append(",");
			int value = currentEntry.getValue();
			buf.append(value);
			if (searcher.exhaustSearch || searcher.discriminative[value]) {
				buf.append(",");
				buf.append(1);
			}
			buf.append('\n');
			gindexFileWriter.write(buf.toString());
		}
		gindexFileWriter.close();
	}

	public static GindexSearcher loadSearcher(String baseName,
			String indexName, boolean exhaustSearch) throws IOException {
		BufferedReader indexFileReader = new BufferedReader(new FileReader(
				new File(baseName, indexName)));
		String aLine = indexFileReader.readLine();
		String[] aLineToken = aLine.split(" ");
		int featureNum = Integer.parseInt(aLineToken[1]); // total number of
															// patterns stored
		// int maxEdgeNum = Integer.parseInt(aLineToken[2]);

		aLine = indexFileReader.readLine();
		String[] tokens = null;
		int indexTermIndex = -1;
		boolean[] discriminative = null;
		if (!exhaustSearch)
			discriminative = new boolean[featureNum];
		else
			discriminative = null; // for exhaustSearch, the discriminative
									// array is assigned null

		HashMap<String, Integer> gHash = new HashMap<String, Integer>();
		while (aLine != null) {
			tokens = aLine.split(",");
			indexTermIndex = Integer.parseInt(tokens[1]);// Index
			gHash.put(tokens[0], indexTermIndex);// DFS code

			if (!exhaustSearch) {
				if (tokens.length == 2) {// redundant, not discriminative index
											// term
					discriminative[indexTermIndex] = false;
				}
				// else, discriminative index term
				else
					discriminative[indexTermIndex] = true;
			}
			// else discriminative == null, continue
			aLine = indexFileReader.readLine();
		}
		indexFileReader.close();
		return new GindexSearcher(gHash, discriminative, indexTermIndex,
				exhaustSearch);
	}

}
