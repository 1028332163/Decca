package neu.lab.conflict.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClsRefNode extends PathNode {

	public boolean isHostNode() {
		return isHostNode;
	}

	private String clsName;
	private boolean isHostNode;
	private List<String> referClses;// class who references this class

	public ClsRefNode(String clsName, boolean isHostNode) {
		this.clsName = clsName;
		this.isHostNode = isHostNode;
		referClses = new ArrayList<String>();
	}

	public void addReferCls(String refCls) {
		referClses.add(refCls);
	}

	/**
	 * delete referNode that is only a String(not node). e.g.,java.lang.Object
	 * 
	 * @param name2node
	 */
	public void delGhostRefer(Map<String, ClsRefNode> name2node) {
		Iterator<String> referIte = referClses.iterator();
		while (referIte.hasNext()) {
			if (null == name2node.get(referIte.next()))
				referIte.remove();
		}
	}

	/**
	 * delete referNode who is in host.
	 * 
	 * @param name2node
	 */
	public void delHostRefer(Map<String, ClsRefNode> name2node) {
		Iterator<String> referIte = referClses.iterator();
		while (referIte.hasNext()) {
			ClsRefNode referNode = name2node.get(referIte.next());
			if (referNode != null && referNode.isHostNode)
				referIte.remove();
		}
	}

	@Override
	public String getName() {
		return clsName;
	}

	@Override
	public Collection<String> getNexts() {
		return referClses;
	}

	@Override
	public IBook getBook() {
		return new ClsRefPathBook(this);
	}

	@Override
	public IRecord formNewRecord() {
		return new ClsRefPathRecord(getName(), 1, isHostNode);
	}

}
