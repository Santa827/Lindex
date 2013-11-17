package edu.psu.chemxseer.structure.subsearch.Impl.indexfeature;

import java.util.LinkedList;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IFeature;


/**
 * Single Feature With Parent/Children Relationship
 * A pattern m is n's parent, if m 's subgraph isomorphic to n
 * and there is not other pattern between m and n.
 * @author dayuyuan
 *
 */
public class SingleFeatureWithRelation implements IFeature {

	private IFeature oneFeature;
	private LinkedList<SingleFeatureWithRelation> parents;
	private LinkedList<SingleFeatureWithRelation> children;
	private boolean visited;

	public SingleFeatureWithRelation duplicate(){
		return new SingleFeatureWithRelation(
				oneFeature.duplicate());
	}
	// Assign feature as part of SingleFeatureWithRelation
	public SingleFeatureWithRelation(IFeature oneFeature) {
		this.oneFeature = oneFeature;
		this.parents = null;
		this.children = null;
		this.visited = false;
	}

	public boolean addParent(SingleFeatureWithRelation parent) {
		if (parents == null)
			parents = new LinkedList<SingleFeatureWithRelation>();
		if (!parents.contains(parent)) {
			parents.add(parent);
			return true;
		} else
			return false;
	}

	public boolean removeParent(SingleFeatureWithRelation parent) {
		return this.parents.remove(parent);
	}

	public boolean addChild(SingleFeatureWithRelation child) {
		if (children == null)
			children = new LinkedList<SingleFeatureWithRelation>();
		if (!children.contains(child)) {
			children.add(child);
			return true;
		} else
			return false;
	}

	public boolean removeChild(SingleFeatureWithRelation child) {
		return this.children.remove(child);
	}

	public void setVisited() {
		visited = true;
	}

	public void setUnvisited() {
		visited = false;
	}

	public boolean isVisited() {
		return visited;
	}

	public List<SingleFeatureWithRelation> getChildren() {
		return this.children;
	}

	public List<SingleFeatureWithRelation> getParents() {
		return this.parents;
	}

	public void removeChildren() {
		if (this.children != null)
			this.children.clear();
	}

	public void removeParents() {
		if (this.parents != null)
			this.parents.clear();
	}

	public IFeature getOriFeature() {
		return this.oneFeature;
	}

	@Override
	public boolean isSelected() {
		return this.oneFeature.isSelected();
	}

	@Override
	public void setSelected() {
		this.oneFeature.setSelected();
	}

	@Override
	public void setUnselected() {
		this.oneFeature.setUnselected();
	}

	@Override
	public Graph getFeatureGraph() {
		return this.oneFeature.getFeatureGraph();
	}

	@Override
	public void creatFeatureGraph(int gID) {
		this.oneFeature.creatFeatureGraph(gID);
	}

	@Override
	public String getDFSCode() {
		return this.oneFeature.getDFSCode();
	}

	@Override
	public int getFrequency() {
		return this.oneFeature.getFrequency();
	}

	@Override
	public void setFrequency(int frequency) {
		this.oneFeature.setFrequency(frequency);
	}

	@Override
	public long getPostingShift() {
		return this.oneFeature.getPostingShift();
	}

	@Override
	public void setPostingShift(long shift) {
		this.oneFeature.setPostingShift(shift);
	}

	@Override
	public int getFeatureId() {
		return this.oneFeature.getFeatureId();
	}

	@Override
	public void setFeatureId(int id) {
		this.oneFeature.setFeatureId(id);
	}

	@Override
	public String toFeatureString() {
		return this.oneFeature.toFeatureString();
	}
}
