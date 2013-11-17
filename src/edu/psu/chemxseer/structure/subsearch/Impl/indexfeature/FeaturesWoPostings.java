package edu.psu.chemxseer.structure.subsearch.Impl.indexfeature;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import edu.psu.chemxseer.structure.subsearch.Interfaces.IFeature;
import edu.psu.chemxseer.structure.util.ArrayIterator;

/**
 * Implementation of a collection of features <type T> without postings.
 * 
 * @author dayuyuan
 * 
 */
public class FeaturesWoPostings<T extends IFeature> implements Iterable<T> {
	protected boolean graphAvailabel;
	protected IFeature[] features; // The whole set of features

	/**
	 * Construct FeaturesWoPostings<T> the input features are part of the
	 * FeaturesWoPostings
	 * 
	 * @param features
	 */
	public FeaturesWoPostings(T[] features) {
		this.features = features;
		this.graphAvailabel = false;
	}

	/**
	 * Construct FeaturesWoPostings<T> The input features is part of the
	 * FeaturesWoPostings rename the feature id if reserveID = false;
	 * 
	 * @param features
	 * @param reserveID
	 */
	public FeaturesWoPostings(List<T> features, boolean reserveID) {
		this.features = new IFeature[features.size()];
		this.graphAvailabel = false;
		int i = 0;
		for (T oneFeature : features) {
			this.features[i] = oneFeature;
			if (reserveID == false)
				oneFeature.setFeatureId(i);
			i++;
		}
	}

	/**
	 * Construct NoPostingFeatures by loading features from a file
	 * 
	 * @param newFeatureFile
	 * @param factory
	 * @return
	 */
	public static FeaturesWoPostings<IFeature> LoadFeaturesWoPostings(
			String featureFileName, FeatureFactory factory) {
		FeaturesWoPostings<IFeature> features = new FeaturesWoPostings<IFeature>(
				new IFeature[0]);
		try {
			features.loadFeatures(featureFileName, factory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return features;
	}

	private void loadFeatures(String featureFileName, FeatureFactory factory)
			throws IOException {
		// First read file head
		BufferedReader bin = null;
		bin = new BufferedReader(new InputStreamReader(new FileInputStream(
				featureFileName)));
		String firstLine = bin.readLine();
		if (firstLine == null) {
			bin.close();
			return;
		}
		features = new IFeature[Integer.parseInt(firstLine)];
		// System.out.println(this.features.length);
		// Read one Feature from feature file and Then load it into
		// this.features
		int iter = 0;
		while ((firstLine = bin.readLine()) != null) {
			features[iter] = factory.genFeature(iter, firstLine);
			iter++;
		}
		// Finish Loading features into memory
		bin.close();
	}

	public boolean createGraphs() throws ParseException {
		if (this.graphAvailabel)
			return false;
		for (int i = 0; i < this.features.length; i++)
			features[i].creatFeatureGraph(i);
		this.graphAvailabel = true;
		return true;
	}

	public boolean sortFeatures(Comparator<IFeature> comparator) {
		Arrays.sort(this.features, comparator);
		return true;
	}

	@SuppressWarnings("unchecked")
	public T getFeature(int index) {
		return (T) features[index];
	}

	public int getfeatureNum() {
		return features.length;
	}

	public void saveFeatures(String newFileName) throws IOException {
		BufferedWriter featureWritter = new BufferedWriter(new FileWriter(
				newFileName));
		featureWritter.write(this.features.length + "\n");
		// Write the feature file
		for (int i = 0; i < this.features.length; i++) {
			featureWritter.write(features[i].toFeatureString() + '\n');
		}
		featureWritter.close();
	}

	public void setAllSelected() {
		for (int i = 0; i < this.features.length; i++)
			this.features[i].setSelected();
	}

	public void setAllUnSelected() {
		for (int i = 0; i < this.features.length; i++)
			this.features[i].setUnselected();
	}

	/**
	 * Merge/Copy two FeaturesWoPostings to one And serialize to newFeatureFile
	 * if newFeatureFile is not null
	 * 
	 * @param featureTwo
	 * @param newFeatureFile
	 * @return
	 * @throws IOException
	 */
	public FeaturesWoPostings<IFeature> mergeFeatures(
			FeaturesWoPostings<IFeature> featureTwo, String newFeatureFile)
			throws IOException {
		List<IFeature> newFeatures = new ArrayList<IFeature>();
		for (int i = 0; i < this.getfeatureNum(); i++)
			newFeatures.add(this.getFeature(i).duplicate());
		for (int j = 0; j < featureTwo.getfeatureNum(); j++)
			newFeatures.add(featureTwo.getFeature(j).duplicate());
		FeaturesWoPostings<IFeature> merged = new FeaturesWoPostings<IFeature>(
				newFeatures, false);
		merged.saveFeatures(newFeatureFile);
		return merged;
	}

	/**
	 * Return the List of Selected Features
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> getSelectedFeatures() {
		List<T> results = new ArrayList<T>();
		for (int i = 0; i < this.features.length; i++) {
			if (features[i].isSelected())
				results.add((T) features[i]);
		}
		return results;
	}

	/**
	 * Return the List of Unselected Features
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> getUnSelectedFeatures() {
		List<T> results = new ArrayList<T>();
		for (int i = 0; i < this.features.length; i++) {
			if (!features[i].isSelected())
				results.add((T) features[i]);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() {
		return new FeatureIterable(this.features);
	}

	class FeatureIterable extends ArrayIterator {

		public FeatureIterable(IFeature[] array) {
			super(array);
		}

		@Override
		@SuppressWarnings("unchecked")
		public T next() {
			return (T) super.next();
		}
	}
}
