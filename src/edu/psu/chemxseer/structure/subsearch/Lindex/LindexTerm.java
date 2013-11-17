package edu.psu.chemxseer.structure.subsearch.Lindex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A Lindex term
 * @author dayuyuan
 *
 */
public class LindexTerm {
	private int m_id;
	private int frequency;
	private LindexTerm[] m_children;
	// only one parent per IndexTerm, minimum spanning tree parent
	private LindexTerm t_parent;
	// the extension of this index Term based on it t_paren
	private int[][] extension; 
	private boolean childDirty;
	private Set<LindexTerm> m_child_change;

	public LindexTerm(int id, int frequency) {
		this.m_id = id;
		this.frequency = frequency;
		this.m_children = new LindexTerm[0];
		this.t_parent = null;
		this.extension = null;
		this.childDirty = false;
		this.m_child_change = null;
	}

	public LindexTerm(int[][] label, int id, int frequency) {
		this.m_id = id;
		this.frequency = frequency;
		this.m_children = new LindexTerm[0];
		this.t_parent = null;
		this.extension = label;
		this.childDirty = false;
		this.m_child_change = null;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getId() {
		return m_id;
	}

	public void setId(int m_id) {
		this.m_id = m_id;
	}

	public LindexTerm[] getChildren() {
		if (this.childDirty) {
			if (this.m_child_change == null)
				throw new NullPointerException();
			this.m_children = new LindexTerm[m_child_change.size()];
			this.m_child_change.toArray(this.m_children);
			this.childDirty = false;
		}
		return m_children;
	}

	public void setChildren(LindexTerm[] m_children) {
		if (m_children == null)
			throw new NullPointerException();
		this.m_children = m_children.clone();
		if (this.m_child_change != null) {
			this.m_child_change = new HashSet<LindexTerm>();
			this.m_child_change.addAll(Arrays.asList(m_children));
			this.childDirty = false;
		}
	}

	public LindexTerm getParent() {
		return t_parent;
	}

	public int[][] getExtension() {
		return extension;
	}

	public void setExtension(int[][] graphLabel) {
		this.extension = graphLabel;
	}

	public void setParent(LindexTerm parent) {
		this.t_parent = parent;
	}

	public void addChild(LindexTerm child) {
		if (this.m_child_change == null) {
			this.m_child_change = new HashSet<LindexTerm>();
			this.m_child_change.addAll(Arrays.asList(m_children));
		}
		if (this.m_child_change.add(child))
			this.childDirty = true;
	}

	public void removeChild(LindexTerm child) {
		if (this.m_child_change == null) {
			this.m_child_change = new HashSet<LindexTerm>();
			this.m_child_change.addAll(Arrays.asList(m_children));
		}
		if (this.m_child_change.remove(child))
			this.childDirty = true;
	}

	public int getMaxNodeIndex() {
		int maxNodeIndex = Integer.MIN_VALUE;
		for (int i = 0; i < extension.length; i++) {
			if (extension[i][0] < maxNodeIndex)
				maxNodeIndex = extension[i][0];
			if (extension[i][1] < maxNodeIndex)
				maxNodeIndex = extension[i][1];
		}
		return maxNodeIndex;
	}

	/**
	 * DFSCode[Extension]=>Index=>postingFileShift => childrenIndex 1,2,3 =>
	 * tParentIndex
	 * 
	 * @param ithTerm
	 * @return
	 */
	public String toString(LindexTerm dummyHead) {
		StringBuffer buf = new StringBuffer(1024);
		// label or extension of this code
		int[][] label = this.extension;
		for (int i = 0; i < label.length; i++) {
			buf.append('<');
			buf.append(label[i][0]);
			buf.append(',');
			buf.append(label[i][1]);
			buf.append(',');
			buf.append(label[i][2]);
			buf.append(',');
			buf.append(label[i][3]);
			buf.append(',');
			buf.append(label[i][4]);
			buf.append('>');
		}
		buf.append(" => ");
		// Index: why m_id twice??
		buf.append(this.m_id);
		buf.append(" => ");
		buf.append(this.m_id);
		buf.append(" => ");
		buf.append(this.frequency);
		buf.append(" => ");
		// Add children
		LindexTerm[] c = this.m_children;
		if (c != null) {
			if (c.length > 0)
				buf.append(c[0].getId());
			// else do nothing
			if (c.length > 1)
				for (int j = 1; j < c.length; j++) {
					buf.append(",");
					buf.append(c[j].getId());
				}
		}
		// Add t_parent
		if (this.t_parent != null && this.t_parent != dummyHead) {
			buf.append(" => ");
			buf.append(this.t_parent.m_id);
		}
		buf.append('\n');
		return buf.toString();
	}

}
