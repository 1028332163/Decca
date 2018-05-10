package neu.lab.conflict.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import neu.lab.conflict.risk.ref.tb.RefTb;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.NodeAdapter;

public class ClsRefGraph implements IGraph {
	private Map<String, ClsRefNode> name2node;

	public ClsRefGraph() {
		name2node = new HashMap<String, ClsRefNode>();
	}

	public void addNode(String nodeName, boolean isHostNode) {
		if (name2node.get(nodeName) != null) {
			MavenUtil.i().getLog().info("duplicate class for " + nodeName + " when forming class-refrence-graph");
		}
		name2node.put(nodeName, new ClsRefNode(nodeName, isHostNode));
	}

	public void addEdges(String ee, Collection<String> ers) {
		ClsRefNode eeNode = name2node.get(ee);
		if (eeNode != null) {
			for (String er : ers) {
				eeNode.addReferCls(er);
			}
		}
	}

	public static ClsRefGraph getGraph(NodeAdapter node) {
		LinkedList<NodeAdapter> ancestors = node.getAncestors(true);// from down to top
		ClsRefGraph graph = new ClsRefGraph();
		// add node to graph
		for (int i = 0; i < ancestors.size(); i++) {
			for (String clsName : ancestors.get(i).getDepJar().getAllCls(true)) {
				if (i == ancestors.size() - 1) {// host node
					graph.addNode(clsName, true);
				} else {// else nodes
					graph.addNode(clsName, false);
				}
			}

		}
		// add edge to garph
		for(int i = 1; i < ancestors.size(); i++) {//set i from 1 to ignore conflict jar
			NodeAdapter ancestorNode = ancestors.get(i);
			RefTb refTb = ancestorNode.getDepJar().getRefTb();
			for (String ee : refTb) {
				graph.addEdges(ee, refTb.getErs(ee));
			}
		}

		graph.filterEdge();
		return graph;
	}

	public void filterEdge() {
		// filter edge that host to host.Edge that is in conflict-jar needn't be
		// filtered because conflict-jar wasn't added in graph-factory.
		for (ClsRefNode node : name2node.values()) {
			node.delGhostRefer(name2node);
			if (node.isHostNode()) {
				node.delHostRefer(name2node);
			}
		}
	}

	// private ClsRefNode getNotNullNode(String nodeName) {
	// ClsRefNode node = name2node.get(nodeName);
	// if(node==null) {
	// node = new ClsRefNode(nodeName);
	// name2node.put(nodeName, node);
	// }
	// return node;
	// }

	@Override
	public INode getNode(String nodeName) {
		return name2node.get(nodeName);
	}

	@Override
	public Collection<String> getAllNode() {
		return name2node.keySet();
	}

}
