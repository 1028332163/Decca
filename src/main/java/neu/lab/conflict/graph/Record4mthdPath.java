package neu.lab.conflict.graph;

public class Record4mthdPath extends IRecord{
	private String pathStr;
	private int pathlen;
	
	public Record4mthdPath(String pathStr, int pathlen) {
		super();
		this.pathStr = pathStr;
		this.pathlen = pathlen;
	}

	public String getPathStr() {
		return pathStr;
	}

	public int getPathlen() {
		return pathlen;
	}

	@Override
	public IRecord clone() {
		return null;
	}

}
