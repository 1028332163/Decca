package neu.lab.conflict.graph;

import java.util.ArrayList;
import java.util.List;

import neu.lab.conflict.Conf;

public class MthdPathBook extends PathBook {

	public MthdPathBook(PathNode node) {
		super(node);
	}

	/**
	 * @return path from host to conflict node
	 */
	public List<MthdPathRecord> getRiskPath() {
		List<MthdPathRecord> riskPaths = new ArrayList<MthdPathRecord>();
		for (IRecord recordI : getRecords()) {
			MthdPathRecord path = (MthdPathRecord) recordI;
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
			MthdPathRecord path = (MthdPathRecord) recordI;
			sb.append("-");
			sb.append(path.getPathStr());
			sb.append("\n");
		}
		return sb.toString();
	}

}
