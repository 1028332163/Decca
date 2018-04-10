package neu.lab.conflict.graph;

import java.util.Collection;

public interface NodeI {
	
	public String getName();
	
	public Collection<String> getNexts();//next nodes that dog should go when writes book about this node.
	
	public BookI getBook();
	
}
