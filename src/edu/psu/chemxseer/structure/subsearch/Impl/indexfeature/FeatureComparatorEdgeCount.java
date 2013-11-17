package edu.psu.chemxseer.structure.subsearch.Impl.indexfeature;

import java.util.Comparator;

import edu.psu.chemxseer.structure.subsearch.Interfaces.IFeature;

/**
 * Feature Comparator Based on EdgeCount
 * 
 * @author dayuyuan
 * 
 */
public class FeatureComparatorEdgeCount implements Comparator<IFeature> {
	@Override
	public int compare(IFeature arg0, IFeature arg1) {
		int edge1 = arg0.getFeatureGraph().getEdgeCount();
		int edge2 = arg1.getFeatureGraph().getEdgeCount();
		if (edge1 < edge2)
			return -1;
		else if (edge1 == edge2)
			return 0;
		else
			return 1;
	}
}
