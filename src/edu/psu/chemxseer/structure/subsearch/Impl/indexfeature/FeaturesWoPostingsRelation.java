package edu.psu.chemxseer.structure.subsearch.Impl.indexfeature;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import edu.psu.chemxseer.structure.iso.FastSU;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IFeature;

/**
 * Features without postings but with children/parents relationships. This
 * features are mainly used for Lindex (related) indexes.
 * 
 * @author dayuyuan
 * 
 */
public class FeaturesWoPostingsRelation<T extends IFeature> {
	// Features Extension
	private SingleFeatureWithRelation[] featuresExt;
	protected boolean subsuperRelationExist;

	private FeaturesWoPostingsRelation(SingleFeatureWithRelation[] featureExt) {
		this.featuresExt = featureExt;
		this.subsuperRelationExist = false;
	}

	/**
	 * Given normal Features: construct a "super-sub" graph relationship
	 * preserved FeaturesExt. normalFeatures can be destroyed after the
	 * construction
	 * 
	 * @param newNormalFeatures
	 */
	public static FeaturesWoPostingsRelation<IFeature> buildFeaturesWoPostingsRelation(
			FeaturesWoPostings<IFeature> normalFeatures) {
		// New Features
		SingleFeatureWithRelation[] featuresExt = new SingleFeatureWithRelation[normalFeatures
				.getfeatureNum()];
		for (int i = 0; i < normalFeatures.getfeatureNum(); i++)
			featuresExt[i] = new SingleFeatureWithRelation(normalFeatures
					.getFeature(i).duplicate());
		return new FeaturesWoPostingsRelation<IFeature>(featuresExt);
	}

	/**
	 * Mine parent/children relationships.
	 * 
	 * @return
	 * @throws ParseException
	 */
	public boolean mineSubSuperRelation() throws ParseException {
		// First sort the candidateFeatures
		this.createGraphs();
		Arrays.sort(this.featuresExt, new FeatureComparator());
		for (int i = 0; i < featuresExt.length; ++i)
			featuresExt[i].setFeatureId(i);

		HashSet<SingleFeatureWithRelation> offsprings = new HashSet<SingleFeatureWithRelation>();
		FastSU fastSu = new FastSU();
		for (int i = featuresExt.length - 1; i >= 0; i--) {
			// Initialize offsprings, children
			offsprings.clear();
			for (int j = i + 1; j < featuresExt.length; j++) {
				if (offsprings.contains(featuresExt[j]))
					continue;
				boolean isSub = fastSu.isIsomorphic(
						featuresExt[i].getFeatureGraph(),
						featuresExt[j].getFeatureGraph());
				if (isSub) {
					featuresExt[i].addChild(featuresExt[j]);
					featuresExt[j].addParent(featuresExt[i]);
					// add j and all j's children into offsprings of i
					offsprings.add(featuresExt[j]);
					addOffspring(offsprings, featuresExt[j]);
				} else
					continue;
			}
		}
		return true;
	}

	public void createGraphs() throws ParseException {
		for (SingleFeatureWithRelation feature : featuresExt)
			feature.creatFeatureGraph(feature.getFeatureId());
	}

	/**
	 * Add all terms that descent to term as offsprings
	 * 
	 * @param offsprings
	 *            : output
	 * @param term
	 */
	private void addOffspring(Collection<SingleFeatureWithRelation> offsprings,
			SingleFeatureWithRelation term) {
		Queue<SingleFeatureWithRelation> queue = new LinkedList<SingleFeatureWithRelation>();
		queue.offer(term);
		while (!queue.isEmpty()) {
			SingleFeatureWithRelation oneFeature = queue.poll();
			List<SingleFeatureWithRelation> children = oneFeature.getChildren();
			if (children == null || children.size() == 0)
				continue;
			else {
				for (int i = 0; i < children.size(); i++) {
					if (offsprings.contains(children.get(i)))
						continue;
					else {
						offsprings.add(children.get(i));
						queue.offer(children.get(i));
					}
				}
			}
		}
	}

	public boolean existSubSuperRelation() {
		if (this.subsuperRelationExist)
			return true;
		else
			return false;
	}

	public boolean clearSubSuperRelation() {
		for (int i = 0; i < this.featuresExt.length; i++) {
			featuresExt[i].removeChildren();
			featuresExt[i].removeParents();
		}
		this.subsuperRelationExist = false;
		return true;
	}

	public void setAllUnvisited() {
		for (SingleFeatureWithRelation oneFeature : this.featuresExt)
			oneFeature.setUnvisited();
	}

	public void setAllVisited() {
		for (SingleFeatureWithRelation oneFeature : this.featuresExt)
			oneFeature.setVisited();
	}

	public SingleFeatureWithRelation getFeatureWithRelation(int featureIndex) {
		return this.featuresExt[featureIndex];
	}

	public int getfeatureNum() {
		return this.featuresExt.length;
	}

	public IFeature getFeature(int featureIndex) {
		return this.featuresExt[featureIndex].getOriFeature();
	}

	// Return Original Features (the same set of features, no clone)
	public FeaturesWoPostings<IFeature> getFeatures() {
		IFeature[] features = new IFeature[this.getfeatureNum()];
		for (int w = 0; w < features.length; w++) {
			features[w] = featuresExt[w].getOriFeature();
		}
		return new FeaturesWoPostings<IFeature>(features);
	}

}
