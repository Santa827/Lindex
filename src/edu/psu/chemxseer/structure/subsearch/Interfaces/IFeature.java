package edu.psu.chemxseer.structure.subsearch.Interfaces;

import de.parmol.graph.Graph;

/**
 * Interface for a candidate index feature.
 * 
 * @author dayuyuan
 * 
 */
public interface IFeature {
	public boolean isSelected();

	public void setSelected();

	public void setUnselected();

	public Graph getFeatureGraph();

	public void creatFeatureGraph(int gID);

	public String getDFSCode();

	public int getFrequency();

	public void setFrequency(int frequency);

	public long getPostingShift();

	public void setPostingShift(long shift);

	public int getFeatureId();

	public void setFeatureId(int id);

	public String toFeatureString();
	
	public IFeature duplicate();
}
