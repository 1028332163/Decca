package neu.lab.conflict.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import neu.lab.conflict.risk.ref.tb.RefTb;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;
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

	public void addErs(String ee, Collection<String> ers) {
		ClsRefNode eeNode = name2node.get(ee);
		if (eeNode != null) {
			for (String er : ers) {
				eeNode.addInCls(er);
			}
		}
	}

	public void addEes(String er, Collection<String> ees) {
		for (String ee : ees) {
			this.mustGetNode(ee).addInCls(er);
		}
	}

	public static ClsRefGraph getGraph(NodeAdapter node, boolean filterEdge) {
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
		for (int i = 1; i < ancestors.size(); i++) {// set i from 1 to ignore conflict jar
			NodeAdapter ancestorNode = ancestors.get(i);
			RefTb refTb = ancestorNode.getDepJar().getRefTb();
			for (String ee : refTb) {
				graph.addErs(ee, refTb.getErs(ee));
			}
		}
		if (filterEdge)
			graph.filterEdge();
		return graph;
	}

	private void filterEdge() {
		// filter edge that host to host.Edge that is in conflict-jar needn't be
		// filtered because conflict-jar wasn't added in graph-factory.
		for (ClsRefNode node : name2node.values()) {
			node.delGhostRefer(name2node);
			if (node.isHostNode()) {
				node.delHostRefer(name2node);
			}
		}
	}

	@Override
	public INode getNode(String nodeName) {
		return name2node.get(nodeName);
	}

	/**
	 * if there's not a node,then create one.
	 * 
	 * @return
	 */
	public ClsRefNode mustGetNode(String nodeName) {
		ClsRefNode node = name2node.get(nodeName);
		if (node == null) {
			node = new ClsRefNode(nodeName, MavenUtil.i().isHostClass(nodeName));
			name2node.put(nodeName, node);
		}
		return node;
	}

	public void addNode(String nodeName) {
		name2node.put(nodeName, new ClsRefNode(nodeName, MavenUtil.i().isHostClass(nodeName)));
	}

	@Override
	public Collection<String> getAllNode() {
		return name2node.keySet();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String name:name2node.keySet()) {
			sb.append(name2node.get(name).toString());
			sb.append(System.lineSeparator());
		}
		return sb.toString();
	}

}
