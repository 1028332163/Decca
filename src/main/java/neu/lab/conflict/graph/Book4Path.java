package neu.lab.conflict.graph;

import java.util.ArrayList;

public abstract class Book4Path extends IBook{
	public Book4Path(Node4Path node) {
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
			Record4Path mthdPathRecord = (Record4Path) recordI;
			mthdPathRecord.addTail(node);
		}
	}
}
