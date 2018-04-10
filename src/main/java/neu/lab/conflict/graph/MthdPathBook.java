package neu.lab.conflict.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import neu.lab.conflict.Conf;

public class MthdPathBook {
	protected MthdRltNode node;
	private Set<MthdPathRecord> paths;

	public MthdPathBook(MthdRltNode node) {
		this.node = node;
		paths = new HashSet<MthdPathRecord>();
	}

	public void addChild(MthdPathBook childBook) {
		Set<MthdPathRecord> childRecords = childBook.getPaths();
		for (MthdPathRecord path : childRecords) {
			paths.add(path.clone());
		}
	}

	public void addSelf() {
		if (paths.isEmpty()) {
			MthdPathRecord path = new MthdPathRecord(node.getName(), node.isHostNode(), 1);
			paths.add(path);
		} else {
			addNdToAll(node.getName());
		}
	}

	public void addNdToAll(String node) {
		for (MthdPathRecord path : paths) {
			path.addTail(node);
		}
	}

	public Set<MthdPathRecord> getPaths() {
		return paths;
	}

	/**
	 * @return path from host to conflict node
	 */
	public List<MthdPathRecord> getRiskPath() {
		List<MthdPathRecord> riskPaths = new ArrayList<MthdPathRecord>();
		for (MthdPathRecord path : getPaths()) {
			if (path.isFromHost())
//				if (path.getPathLen() >= Conf.MIN_PATH_DEP)// path whose depth is 2 is unreasonable.
				if(path.getPathLen()<=Conf.MAX_PATH_DEP)
					riskPaths.add(path);
		}
		return riskPaths;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(node.getName() + "\n");
		for (MthdPathRecord path : paths) {
			sb.append("-");
			sb.append(path.getPathStr());
			sb.append("\n");
		}
		return sb.toString();
	}
}
