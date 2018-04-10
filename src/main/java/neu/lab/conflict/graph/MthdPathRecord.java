package neu.lab.conflict.graph;

public class MthdPathRecord extends RecordI{
	private String pathStr;
	private boolean isFromHost;
	private int pathLen;

	public int getPathLen() {
		return pathLen;
	}

	public boolean isFromHost() {
		return isFromHost;
	}

	public MthdPathRecord(String path, boolean isFromHost, int pathLen) {
		this.pathStr = path;
		this.isFromHost = isFromHost;
		this.pathLen = pathLen;
	}

	public MthdPathRecord clone() {
		return new MthdPathRecord(pathStr, isFromHost, pathLen);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof MthdPathRecord) {
			MthdPathRecord otherPath = (MthdPathRecord) other;
			return pathStr.equals(otherPath.getPathStr());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return pathStr.hashCode();
	}

	@Override
	public String toString() {
		return getPathStr() + " isFromHost:" + isFromHost() + " path length:" + pathLen;
	}

	public void addTail(String node) {
		pathStr = pathStr + "->" + node;
		pathLen++;
	}

	public String getPathStr() {
		return pathStr;
	}
}
