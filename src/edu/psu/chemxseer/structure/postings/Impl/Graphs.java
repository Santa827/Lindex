package edu.psu.chemxseer.structure.postings.Impl;

import java.text.ParseException;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.postings.Interface.IGraphs;
import edu.psu.chemxseer.structure.factory.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeaturesWoPostings;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IFeature;

public class Graphs implements IGraphs {
	private FeaturesWoPostings<IFeature> features;

	public Graphs(FeaturesWoPostings<IFeature> features) {
		this.features = features;
	}

	@Override
	public int getSupport(int gID) {
		return this.features.getFeature(gID).getFrequency();
	}

	@Override
	public boolean createGraphs() throws ParseException {
		return this.features.createGraphs();
	}

	@Override
	public Graph getGraph(int gID) {
		IFeature feature = this.features.getFeature(gID);
		if (feature.getFeatureGraph() == null)
			return MyFactory.getDFSCoder().parse(feature.getDFSCode(),
					MyFactory.getGraphFactory());
		else
			return feature.getFeatureGraph();
	}

	@Override
	public int getGraphNum() {
		return this.features.getfeatureNum();
	}

	@Override
	public String getLabel(int gID) {
		return this.features.getFeature(gID).getDFSCode();
	}
}
