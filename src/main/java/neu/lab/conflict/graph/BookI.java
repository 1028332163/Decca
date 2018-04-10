package neu.lab.conflict.graph;

import java.util.List;

public abstract class BookI {
	
	protected NodeI node;
	protected List<RecordI> records;
	
	public abstract void addSelf();//when dog is back,add self information to book.

	public abstract void addChild(BookI doneBook);//add child book path to self.
	
	public  List<RecordI> getRecords(){
		return records;
	} 
}
