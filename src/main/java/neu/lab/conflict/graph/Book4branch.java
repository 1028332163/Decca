package neu.lab.conflict.graph;

import java.util.ArrayList;


public class Book4branch extends IBook {

	public Book4branch(INode node) {
		super(node);
		this.records = new ArrayList<IRecord>();
	}

	@Override
	public void afterAddAllChildren() {
		if(getNode().isRisk()) {
			this.addRecord(getNodeName(), 0, 0);
		}
	}

	@Override
	public void addChild(IBook doneChildBook) {
		int thisBranch = getNode().getBranch();
		for (IRecord recordI : doneChildBook.getRecords()) {
			Record4branch record = (Record4branch) recordI;
			addRecord(record.getName(), record.getBranch()+thisBranch, record.getDistance()+1);
		}
	}
	
	private Node4branch getNode() {
		return (Node4branch)this.node;
	}

	private void addRecord(String nodeName, double branch, double distance) {
		for (IRecord iRecord : this.records) {
			Record4branch record = (Record4branch) iRecord;
			if (nodeName.equals(record.getName())) {
				record.updateBranch(branch);
				record.updateDistance(distance);
				return;
			}
		}
		this.records.add(new Record4branch(nodeName, branch, distance));
	}

}
