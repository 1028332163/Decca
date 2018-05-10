package neu.lab.conflict.graph;

import java.util.List;

public abstract class IBook {
	
	protected INode node;
	protected List<IRecord> records;
	
	public abstract void addSelfNode();//when dog is back,add self information to book.

	public abstract void addChild(IBook doneBook);//add child book path to self.
	
	public  List<IRecord> getRecords(){
		return records;
	} 
}
