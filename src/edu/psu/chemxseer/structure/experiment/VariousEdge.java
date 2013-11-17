package edu.psu.chemxseer.structure.experiment;

import java.io.IOException;

import org.apache.lucene.queryParser.ParseException;

import de.parmol.parsers.GraphParser;

import edu.psu.chemxseer.structure.factory.InFrequentQueryGenerater;
import edu.psu.chemxseer.structure.factory.MyFactory;
import edu.psu.chemxseer.structure.postings.Interface.IGraphs;
import edu.psu.chemxseer.structure.subsearch.Interfaces.ISearcher;

public class VariousEdge {
	 public static void main(String[] args) throws IOException,
	 ParseException, java.text.ParseException{
	 for(int i = 30; i < 60; i = i+10){
	 String baseName = "/data/santa/VLDBJExp/VaringEdge/" + i + "/";
	 String dbFileName = baseName + "DBFile";
	 GraphParser dbParser = MyFactory.getSmilesParser();
	 BasicExpBuilder builder = new BasicExpBuilder(dbFileName, dbParser,
	 baseName);
	 builder.buildGIndexDF(0.01, 0);
	 builder.buildLindexDF(0);
	 builder.buildFGindex(0.01, 0);
	 builder.buildLindexAdvTCFG(0);
	
	
	 BasicExpRunner runner = new BasicExpRunner(dbFileName, dbParser,
	 baseName);
	

	 runner.genQueries(false);
	 String queryFile = baseName + "Queries/QueryInf";
	  IGraphs[] queries = InFrequentQueryGenerater.loadInfrequentQueries(queryFile);
	 
	  ISearcher searcher = null;
	  System.out.println("DF");
	  searcher = runner.loadIndex(1);
	  runner.runExp(queries, searcher);
	  searcher = runner.loadIndex(2);
	  runner.runExp(queries, searcher);
	 
	  System.out.println("TCFG");
	  searcher = runner.loadIndex(6);
	  runner.runExp(queries, searcher);
	  searcher = runner.loadIndex(7);
	  runner.runExp(queries, searcher);
	 }
	 }
}