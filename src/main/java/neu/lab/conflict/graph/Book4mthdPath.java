package neu.lab.conflict.graph;

import java.util.ArrayList;

public class Book4mthdPath extends IBook {

	public Book4mthdPath(INode node) {
		super(node);
		this.records = new ArrayList<IRecord>();
	}

	@Override
	public void afterAddAllChildren() {
		//TODO print for what
//		if ("<org.apache.lucene.analysis.BaseCharFilter: void addOffCorrectMap(int,int)>"
//				.equals(getNode().getName())
//				|| "<com.google.common.base.Objects: com.google.common.base.Objects$ToStringHelper toStringHelper(java.lang.Class)>"
//						.equals(getNode().getName()))
			if (getNode().isRisk()) {
				this.records.add(getNode().formNewRecord());
			}
	}

	private Node4mthdPath getNode() {
		return (Node4mthdPath) this.node;
	}

	@Override
	public void addChild(IBook doneChildBook) {
		for (IRecord iRecord : doneChildBook.getRecords()) {
			Record4mthdPath record = (Record4mthdPath) iRecord;
			this.records
					.add(new Record4mthdPath(this.getNodeName() + "\n" + record.getPathStr(), record.getPathlen() + 1));
		}
	}

}
