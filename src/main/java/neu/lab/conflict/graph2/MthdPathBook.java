package neu.lab.conflict.graph2;

import java.util.ArrayList;
import java.util.List;

import neu.lab.conflict.Conf;

public class MthdPathBook extends BookI {

	public MthdPathBook(MthdRltNode node) {
		this.node = node;
		this.records = new ArrayList<RecordI>();
	}

	@Override
	public void addSelf() {
		MthdRltNode mthdNode = (MthdRltNode) node;
		if (records.isEmpty()) {
			RecordI path = new MthdPathRecord(node.getName(), mthdNode.isHostNode(), 1);
			records.add(path);
		} else {
			addNdToAll(node.getName());
		}
	}

	@Override
	public void addChild(BookI doneBook) {
		List<RecordI> childRecords = doneBook.getRecords();
		for (RecordI recordI : childRecords) {
			MthdPathRecord mthdPathRecord = (MthdPathRecord) recordI;
			records.add(mthdPathRecord.clone());
		}
	}

	public void addNdToAll(String node) {
		for (RecordI recordI : records) {
			MthdPathRecord mthdPathRecord = (MthdPathRecord) recordI;
			mthdPathRecord.addTail(node);
		}
	}

	/**
	 * @return path from host to conflict node
	 */
	public List<MthdPathRecord> getRiskPath() {
		List<MthdPathRecord> riskPaths = new ArrayList<MthdPathRecord>();
		for (RecordI recordI : getRecords()) {
			MthdPathRecord path = (MthdPathRecord)recordI;
			if (path.isFromHost())
//				if (path.getPathLen() >= Conf.MIN_PATH_DEP)// path whose depth is 2 is unreasonable.
				if(path.getPathLen()<=Conf.MAX_PATH_DEP)
					riskPaths.add(path);
		}
		return riskPaths;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(node.getName() + "\n");
		for (RecordI recordI : getRecords()) {
			MthdPathRecord path = (MthdPathRecord)recordI;
			sb.append("-");
			sb.append(path.getPathStr());
			sb.append("\n");
		}
		return sb.toString();
	}
}
