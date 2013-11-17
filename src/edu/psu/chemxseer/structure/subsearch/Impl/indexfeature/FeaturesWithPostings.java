package edu.psu.chemxseer.structure.subsearch.Impl.indexfeature;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import edu.psu.chemxseer.structure.subsearch.Interfaces.IFeature;

/**
 * A collection of features with postings.
 * 
 * @author dayuyuan
 * 
 */
public class FeaturesWithPostings {
	protected FeaturesWoPostings<IFeature> features;
	protected FeaturePosting postingFetcher;
	protected FeaturePostingMem memRepresent;

	/**
	 * Construct FeaturesWithPostings with input features
	 * The input features are part of the FeaturesWithPostings
	 * @param postingFile
	 * @param features
	 */
	public FeaturesWithPostings(String postingFile,
			FeaturesWoPostings<IFeature> features) {
		if (postingFile != null)
			this.postingFetcher = new FeaturePosting(postingFile);
		else
			this.postingFetcher = null;

		this.features = features;
		this.memRepresent = null;
	}

	/**
	 * Construct FeaturesWithPostings with existing postings.
	 * Both the existing postings and the input features are part of the FeaturesWithPostings.
	 * @param postings
	 * @param features
	 */
	public FeaturesWithPostings(FeaturePosting postings,
			FeaturesWoPostings<IFeature> features) {
		this.postingFetcher = postings;
		this.features = features;
		this.memRepresent = null;
	}

	/**
	 * Load the posting file into memory
	 */
	public void loadPostingIntoMemory() {
		if (memRepresent != null)
			System.out
					.println("Error in loadPostingIntoMemory:: PostingFeatures, the posting already"
							+ "exists");
		else {
			this.memRepresent = new FeaturePostingMem();
			for (int i = 0; i < features.getfeatureNum(); i++) {
				long shift = features.getFeature(i).getPostingShift();
				this.memRepresent.insertPosting(shift,
						postingFetcher.getPosting(shift));
			}
		}
	}

	/**
	 * Drop in-memory postings.
	 */
	public void discardInMemPosting() {
		this.memRepresent = null;
	}

	public int[] getPosting(IFeature feature) {
		if (this.memRepresent == null) {
			return this.postingFetcher.getPosting(feature.getPostingShift());
		} else
			return this.memRepresent.getPosting(feature.getPostingShift());
	}

	public int[] getPosting(Integer featureID) {
		return this.getPosting(this.features.getFeature(featureID));
	}

	/**
	 * Return selected features
	 * Sore the selected features in newPostingFile when not null.
	 * @param newFeatureFile
	 * @param newPostingFile
	 * @param reserveID
	 * @return
	 */
	public FeaturesWithPostings getSelectedFeatures(String newFeatureFile,
			String newPostingFile, boolean reserveID) {
		// 1. Get Selected Features
		List<IFeature> selectedFeatures = this.features.getSelectedFeatures();
		// 2. Store
		try {
			return this.saveFeatures(newFeatureFile, newPostingFile, reserveID,
					selectedFeatures);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	/**
	 * Return unselected features
	 * Store the unselected feature in newPostingFiles when not null.
	 * @param newFeatureFile
	 * @param newPostingFile
	 * @param reserveID
	 * @return
	 * @throws IOException
	 */
	public FeaturesWithPostings getUnSelectedFeatures(String newFeatureFile,
			String newPostingFile, boolean reserveID) throws IOException {
		// 1. Get Selected Features
		List<IFeature> selectedFeatures = this.features.getUnSelectedFeatures();
		// 2. Store
		try {
			return this.saveFeatures(newFeatureFile, newPostingFile, reserveID,
					selectedFeatures);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private FeaturesWithPostings saveFeatures(String newFeatureFile,
			String newPostingFile, boolean reserveID,
			List<IFeature> selectedFeatures) throws IOException {
		// 2. Record the Postings
		if (newPostingFile != null) {
			FileOutputStream tempStream = new FileOutputStream(newPostingFile);
			FileChannel postingChannel = tempStream.getChannel();
			int index = 0;
			for (IFeature oneFeature : selectedFeatures) {
				int fID = index;
				if (reserveID)
					fID = oneFeature.getFeatureId();
				long shift = oneFeature.getPostingShift();
				long newShift = this.postingFetcher.savePostings(
						postingChannel, shift, fID);
				oneFeature.setPostingShift(newShift);
				index++;
			}
			tempStream.close();
			postingChannel.close();
		}
		// 3. Save the Features
		FeaturesWoPostings<IFeature> newFeatures = new FeaturesWoPostings<IFeature>(
				selectedFeatures, reserveID);
		// 4. Return
		if (newPostingFile != null) {
			newFeatures.saveFeatures(newFeatureFile);
			return new FeaturesWithPostings(newPostingFile, newFeatures);
		} else
			return new FeaturesWithPostings(this.postingFetcher, newFeatures);
	}

	public FeaturesWoPostings<IFeature> getFeatures() {
		return this.features;
	}

}
