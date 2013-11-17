package edu.psu.chemxseer.structure.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import de.parmol.graph.Graph;
import de.parmol.graph.MutableGraph;

/**
 * Test the Connectivity of graphs
 * 
 * @author duy113
 * 
 */
public class GraphConnectivityTester {

	public static boolean isConnected(Graph g) {
		// visited nodes of graph g in breath first search
		boolean[] visited = new boolean[g.getNodeCount()];
		for (int i = 0; i < visited.length; i++)
			visited[i] = false;
		// starting from the first node
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.offer(0);
		visited[0] = true;
		while (!queue.isEmpty()) {
			int currentNode = queue.poll();
			// add currentNode's adjacent nodes in queue if not visited yet
			for (int j = 0; j < g.getDegree(currentNode); j++) {
				int adjacentEdge = g.getNodeEdge(currentNode, j);
				int adjacentNode = g.getOtherNode(adjacentEdge, currentNode);
				if (visited[adjacentNode] == false) {
					queue.offer(adjacentNode);
					visited[adjacentNode] = true;
				}
			}
		}
		// If these any unvisited node
		for (int i = 0; i < visited.length; i++) {
			if (visited[i] == false)
				return false;
		}
		return true;
	}
	
	public static List<Graph> getConnectedComponent(Graph inputG){
		List<Graph> result = new ArrayList<Graph>();
		boolean[] status = new boolean[inputG.getNodeCount()];
		Arrays.fill(status, false);
		Map<Integer, Integer> OriToNodeID = new HashMap<Integer, Integer>();
		int coveredNodeCount = 0;
		
		while (coveredNodeCount < status.length) {
			// 1. Find the first uncovered node & insert into the
			int firstNode = 0;
			for (boolean i : status)
				if (i)
					firstNode++;
				else
					break; 
			
			OriToNodeID.clear();
			Queue<Integer> candidates = new LinkedList<Integer>();
			MutableGraph newG = MyFactory.getGraphFactory().createGraph(null);
			// 2. Breath first search
			candidates.add(firstNode);
			while (!candidates.isEmpty()) {
				int node = candidates.poll();
				// insert node
				int nodeNew = -1;
				if (!status[node]) {
					nodeNew = newG.addNode(inputG.getNodeLabel(node));
					OriToNodeID.put(node, nodeNew);
					status[node] = true; // mark as covered
				} else
					nodeNew = OriToNodeID.get(node);
				// insert edges
				int w = inputG.getDegree(node);
				for (int i = 0; i< w; i++) {
					int adjEdge = inputG.getNodeEdge(node, i);
					int adjNode = inputG.getOtherNode(adjEdge, node);
					int adjNodeNew = -1;
					
					if (status[adjNode] == true) {
						adjNodeNew = OriToNodeID.get(adjNode);
						if (newG.getEdge(nodeNew, adjNodeNew) == -1)
							newG.addEdge(nodeNew, adjNodeNew, inputG.getEdgeLabel(adjEdge));
					} else {
						// insert node & edge
						adjNodeNew = newG.addNodeAndEdge(nodeNew, inputG.getNodeLabel(adjNode), inputG.getEdgeLabel(adjEdge));
						status[adjNode] = true;
						OriToNodeID.put(adjNode, adjNodeNew);
						candidates.add(adjNode);
					}
				}
			}
			newG.saveMemory();
			coveredNodeCount += newG.getNodeCount();
			result.add(newG);
		}
		return result;
	}
	
}
