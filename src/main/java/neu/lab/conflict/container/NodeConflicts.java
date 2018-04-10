package neu.lab.conflict.container;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import neu.lab.conflict.vo.NodeAdapter;
import neu.lab.conflict.vo.Conflict;

public class NodeConflicts {
	private static NodeConflicts instance;

	public static void init(NodeAdapters nodeAdapters) {
		if (instance == null) {
			instance = new NodeConflicts(nodeAdapters);
		}
	}

	public static NodeConflicts i() {
		return instance;
	}

	private List<Conflict> container;

	/**
	 * must initial NodeAdapters before this construct
	 */
	private NodeConflicts(NodeAdapters nodeAdapters) {
		container = new ArrayList<Conflict>();
		for (NodeAdapter node : nodeAdapters.getAllNodeAdapter()) {
			addNodeAdapter(node);
		}
		
		Iterator<Conflict> ite = container.iterator();
		while (ite.hasNext()) {
			Conflict conflict = ite.next();
			if (!conflict.isConflict()) {// delete conflict if there is only one version
				ite.remove();
			}
		}
	}

	public List<Conflict> getConflicts() {
		return container;
	}

	private void addNodeAdapter(NodeAdapter nodeAdapter) {
		Conflict conflict = null;
		for (Conflict existConflict : container) {
			if (existConflict.sameArtifact(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId())) {
				conflict = existConflict;
			}
		}
		if (null == conflict) {
			conflict = new Conflict(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId());
			container.add(conflict);
		}
		conflict.addNode(nodeAdapter);
	}

	@Override
	public String toString() {
		String str = "project has " + container.size() + " conflict-dependency:+\n";
		for (Conflict conflictDep : container) {
			str = str + conflictDep.toString() + "\n";
		}
		return str;
	}
}
