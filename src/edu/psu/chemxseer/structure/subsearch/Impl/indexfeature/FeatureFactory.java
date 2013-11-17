package edu.psu.chemxseer.structure.subsearch.Impl.indexfeature;

import edu.psu.chemxseer.structure.subsearch.Interfaces.IFeature;

/**
 * An abstract factory of IFeature
 * 
 * @author dayuyuan
 * 
 */
public abstract class FeatureFactory {

	public enum FeatureFactoryType {
		SingleFeature, // Feature mined from a single database
		MultiFeature // Feature mined from multiple databases
	}
	
	/**
	 * Parse a feature from the a string.
	 * @param id
	 * @param featureString
	 * @return
	 */
	public abstract IFeature genFeature(int id, String featureString);
}
