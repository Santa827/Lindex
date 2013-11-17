package edu.psu.chemxseer.structure.subsearch.Impl.indexfeature;

import java.util.Comparator;

import edu.psu.chemxseer.structure.subsearch.Interfaces.IFeature;

/**
 * Feature Comparator Based on EdgeCount, NodeCount and Frequency
 * 
 * @author dayuyuan
 * 
 */
public class FeatureComparator implements Comparator<IFeature> {

	@Override
	public int compare(IFeature o1, IFeature o2) {
		int edge1 = o1.getFeatureGraph().getEdgeCount();
		int edge2 = o2.getFeatureGraph().getEdgeCount();
		if (edge1 < edge2)
			return -1;
		else if (edge1 == edge2) {
			if (o1.getFrequency() > o2.getFrequency())
				return -1;
			else if (o1.getFrequency() == o2.getFrequency()) {
				return o1.getDFSCode().compareTo(o2.getDFSCode());
			} else
				return 1;
		} else
			return 1;
	}

}
