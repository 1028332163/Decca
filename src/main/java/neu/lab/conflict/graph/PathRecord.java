package neu.lab.conflict.graph;

public abstract class PathRecord extends IRecord{
	protected String pathStr;
	
	protected int pathLen;

	public int getPathLen() {
		return pathLen;
	}

	public PathRecord(String pathStr,  int pathLen) {
		this.pathStr = pathStr;
		this.pathLen = pathLen;
	}

	public void addTail(String node) {
//		System.out.println();
		pathStr = pathStr + " to " + node;
		pathLen++;
	}

	public String getPathStr() {
		return pathStr;
	}
}
