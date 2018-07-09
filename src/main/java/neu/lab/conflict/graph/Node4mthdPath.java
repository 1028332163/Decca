package neu.lab.conflict.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import neu.lab.conflict.graph.path1.Node4Path;

public class Node4mthdPath implements INode{
	private String name;
	private boolean isHost;
	private boolean isRisk;
	private Set<String> outs;
	
	
	public Node4mthdPath(String name, boolean isHost, boolean isRisk) {
		super();
		this.name = name;
		this.isHost = isHost;
		this.isRisk = isRisk;
		outs = new HashSet<String>();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Collection<String> getNexts() {
		return outs;
	}

	@Override
	public IBook getBook() {
		return new Book4mthdPath(this);
	}

	@Override
	public IRecord formNewRecord() {
		return new Record4mthdPath(this.name,1);
	}

	public void addOutNd(String tgt) {
		outs.add(tgt);
	}

	public boolean isRisk() {
		return isRisk;
	}

	public boolean isHostNode() {
		return isHost;
	}

}
