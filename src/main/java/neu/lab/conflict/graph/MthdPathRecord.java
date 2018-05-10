package neu.lab.conflict.graph;

public class MthdPathRecord extends PathRecord {

	protected boolean isFromHost;

	public boolean isFromHost() {
		return isFromHost;
	}

	public MthdPathRecord(String path, boolean isFromHost, int pathLen) {
		super(path, pathLen);
		this.isFromHost = isFromHost;
	}

	@Override
	public String toString() {
		return getPathStr() + " isFromHost:" + isFromHost() + " path length:" + pathLen;
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

	public PathRecord clone() {
		return new MthdPathRecord(pathStr, isFromHost, pathLen);
	}

}
