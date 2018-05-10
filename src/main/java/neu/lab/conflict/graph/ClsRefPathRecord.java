package neu.lab.conflict.graph;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

public class ClsRefPathRecord extends PathRecord {
	private boolean isFromHost;

	public ClsRefPathRecord(String pathStr, int pathLen, boolean isFromHost) {
		super(pathStr, pathLen);
		this.isFromHost = isFromHost;
	}

	public Element getPathEle() {
		Element ele = new DefaultElement("path");
		ele.addAttribute("isFromHost", "" + isFromHost);
		ele.addAttribute("pathLength", "" + this.pathLen);
		ele.addText(pathStr);
		return ele;
	}

	@Override
	public IRecord clone() {
		return new ClsRefPathRecord(pathStr, pathLen, isFromHost);
	}

}
