package neu.lab.conflict.graph;

import java.util.ArrayList;

public class Book4MthdProb extends IBook {

	public Book4MthdProb(Node4MthdProb node) {
		this.node = node;
		this.records = new ArrayList<IRecord>();
	}

	@Override
	public void addSelfNode() {
//		if (records.isEmpty()) {
//			IRecord path = node.formNewRecord();
//			records.add(path);
//		}
	}

	@Override
	public void addChild(IBook doneBook) {
		Double this2done = ((Node4MthdProb) node).getCallProb(doneBook.getNodeName());
		addRecord(doneBook.getNodeName(),this2done);
		for (IRecord recordI : doneBook.getRecords()) {
			Record4MthdProb record = (Record4MthdProb) recordI;
			Double this2tgt = this2done * record.getProb();
			addRecord(record.getTgtMthd(),this2tgt);
		}
	}

	public void addRecord(String nodeName, Double prob) {
		for (IRecord iRecord : this.records) {
			Record4MthdProb record = (Record4MthdProb) iRecord;
			if (nodeName.equals(record.getTgtMthd())) {
				record.updateProb(prob);
				return ;
			}
		}
		this.records.add(new Record4MthdProb(nodeName, prob));
	}

}
