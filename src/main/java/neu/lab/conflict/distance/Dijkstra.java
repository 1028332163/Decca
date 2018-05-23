package neu.lab.conflict.distance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import neu.lab.conflict.graph.MthdRltGraph;
import neu.lab.conflict.graph.MthdPathNode;

public abstract class Dijkstra {

	protected Map<String, DijkstraNode> name2node;

	public Dijkstra() {
		name2node = new HashMap<String, DijkstraNode>();
	}

	public Dijkstra(MthdRltGraph graph) {
		name2node = new HashMap<String, DijkstraNode>();
		for (String node : graph.getAllNode()) {
			// neu.lab.conflict.util.MavenUtil.i().getLog().info(node);
			name2node.put(node, new DijkstraNode((MthdPathNode) graph.getNode(node)));
		}
	}

	public void addNode(DijkstraNode node) {
		name2node.put(node.getName(), node);
	}

	public Map<String, Map<String, Double>> getDistanceTb(Collection<String> startNds) {
		Map<String, Map<String, Double>> distances = new HashMap<String, Map<String, Double>>();
		for (String startNd : startNds) {
			distances.put(startNd, getDistanceTb(startNd));
		}

		return distances;
	}

	public abstract Map<String, Double> getDistanceTb(String startNd);


	protected Set<String> getAllNd() {
		return name2node.keySet();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("graph:" + System.lineSeparator());
		for (DijkstraNode node : name2node.values()) {
			sb.append(node.toString());
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

}
