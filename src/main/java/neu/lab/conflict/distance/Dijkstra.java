package neu.lab.conflict.distance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import neu.lab.conflict.graph.MthdRltGraph;
import neu.lab.conflict.graph.MthdPathNode;

public class Dijkstra {

	private Map<String, DijkstraNode> name2node;
	
	public Dijkstra() {
		name2node = new HashMap<String,DijkstraNode>();
	}

	public Dijkstra(MthdRltGraph graph) {
		name2node = new HashMap<String, DijkstraNode>();
		for (String node : graph.getAllNode()) {
//			neu.lab.conflict.util.MavenUtil.i().getLog().info(node);
			name2node.put(node, new DijkstraNode((MthdPathNode) graph.getNode(node)));
		}
	}
	
	public void addNode(DijkstraNode node) {
		name2node.put(node.getName(), node);
	}
	
	public Map<String, Map<String, Double>> getDistanceTb(Collection<String> startNds){
		 Map<String, Map<String, Double>> distances = new HashMap<String,Map<String, Double>>();
		 for(String startNd:startNds) {
			 distances.put(startNd, getDistanceTb(startNd));
		 }

		 return distances;
	}

	public Map<String, Double> getDistanceTb(String startNd) {
		TreeSet<NameAndDist> doingNds = initDoingDistes(startNd);// doing-distances;
		Map<String, Double> doneNds = new HashMap<String, Double>();
		while (!doingNds.isEmpty()) {
//			System.out.println("-----------");
//			for(NameAndDist nd:doingNds) {
//				System.out.println(nd);
//			}
			NameAndDist min = doingNds.pollFirst();
			if (min.distance == Double.MAX_VALUE) {// left-node is all unreachable.
				break;
			} else {
				doneNds.put(min.name, min.distance);// move from doing to done.
				// update doing.
				updateDoingDistes(min.name, doingNds, doneNds);
			}
		}
		return doneNds;
	}

	private void updateDoingDistes(String doneNdName, TreeSet<NameAndDist> doingNds, Map<String, Double> doneNds) {
		DijkstraNode doneNd = name2node.get(doneNdName);
		Set<String> neighbors = doneNd.getNeighbors();
		List<NameAndDist> newDistes = new ArrayList<NameAndDist>();
		Iterator<NameAndDist> ite = doingNds.iterator();
		while (ite.hasNext()) {
			NameAndDist doingNd = ite.next();
			if (neighbors.contains(doingNd.name)) {// the neighbor of doneNode.
				Double newDist = doneNds.get(doneNdName) + doneNd.getDistance(doingNd.name);
				if (newDist < doingNd.distance) {// should update.
					ite.remove();
					newDistes.add(new NameAndDist(doingNd.name, newDist));
				}
			}
		}
		doingNds.addAll(newDistes);
	}

	private TreeSet<NameAndDist> initDoingDistes(String startName) {
		TreeSet<NameAndDist> doingDistes = new TreeSet<NameAndDist>();// doing-distances;
		doingDistes.add(new NameAndDist(startName, new Double(0)));
		DijkstraNode startNd = name2node.get(startName);
		for (String nd : getAllNd()) {
//			System.out.println(nd);
			doingDistes.add(new NameAndDist(nd, startNd.getDistance(nd)));
		}
//		System.out.println(doingDistes.size());
		return doingDistes;
	}

	private Set<String> getAllNd() {
		return name2node.keySet();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("graph:"+System.lineSeparator());
		for(DijkstraNode node:name2node.values()) {
			sb.append(node.toString());
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}
	
}
