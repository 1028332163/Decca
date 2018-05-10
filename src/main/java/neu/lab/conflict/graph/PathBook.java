package neu.lab.conflict.graph;

import java.util.ArrayList;

public abstract class PathBook extends IBook{
	public PathBook(PathNode node) {
		this.node = node;
		this.records = new ArrayList<IRecord>();
	}
	
	@Override
	public void addSelfNode() {
		if (records.isEmpty()) {
			IRecord path = node.formNewRecord();
			records.add(path);
		} else {
			addNdToAllPath(node.getName());
		}
	}
	
	@Override
	public void addChild(IBook childBook) {
		for (IRecord recordI : childBook.getRecords()) {
			records.add(recordI.clone());
		}
	}

	public void addNdToAllPath(String node) {
		for (IRecord recordI : records) {
			PathRecord mthdPathRecord = (PathRecord) recordI;
			mthdPathRecord.addTail(node);
		}
	}
}
