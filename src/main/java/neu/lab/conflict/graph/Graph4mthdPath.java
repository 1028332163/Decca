package neu.lab.conflict.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.vo.MethodCall;

public class Graph4mthdPath implements IGraph{
	
	Map<String,Node4mthdPath> name2node;
	public Graph4mthdPath(Map<String, Node4mthdPath> name2node, Collection<MethodCall> calls) {
		this.name2node = name2node;
		for (MethodCall call : calls) {
			addEdge(call);
		}
	}

	private void addEdge(MethodCall call) {
		name2node.get(call.getSrc()).addOutNd(call.getTgt());
	}

	public Set<String> getHostNds() {
		Set<String> hostNds = new HashSet<String>();
		for (Node4mthdPath node : name2node.values()) {
			if (node.isHostNode())
				hostNds.add(node.getName());
		}
		return hostNds;
	}
	
	@Override
	public INode getNode(String nodeName) {
		return name2node.get(nodeName);
	}

	@Override
	public Collection<String> getAllNode() {
		return name2node.keySet();
	}
}
