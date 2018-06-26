package neu.lab.conflict.graph;

import java.util.ArrayList;
import java.util.List;

import neu.lab.conflict.Conf;

public class Book4MthdPath extends Book4Path {

	public Book4MthdPath(Node4Path node) {
		super(node);
	}

	/**
	 * @return path from host to conflict node
	 */
	public List<Record4MthdPath> getRiskPath() {
		List<Record4MthdPath> riskPaths = new ArrayList<Record4MthdPath>();
		for (IRecord recordI : getRecords()) {
			Record4MthdPath path = (Record4MthdPath) recordI;
			if (path.isFromHost())
				// if (path.getPathLen() >= Conf.MIN_PATH_DEP)// path whose depth is 2 is
				// unreasonable.
				if (path.getPathLen() <= Conf.MAX_PATH_DEP)
					riskPaths.add(path);
		}
		return riskPaths;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(node.getName() + "\n");
		for (IRecord recordI : getRecords()) {
			Record4MthdPath path = (Record4MthdPath) recordI;
			sb.append("-");
			sb.append(path.getPathStr());
			sb.append("\n");
		}
		return sb.toString();
	}

}
