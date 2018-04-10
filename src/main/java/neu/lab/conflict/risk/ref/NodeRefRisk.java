package neu.lab.conflict.risk.ref;

import java.util.LinkedList;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import neu.lab.conflict.container.AllCls;
import neu.lab.conflict.risk.ref.tb.LimitRefTb;
import neu.lab.conflict.vo.NodeAdapter;

public class NodeRefRisk {
	private NodeAdapter node;
	private DepJarRefRisk jarRisk;
	private LimitRefTb refTb;

	public NodeRefRisk(NodeAdapter node1, DepJarRefRisk depJarRefRisk) {
		this.node = node1;
		this.jarRisk = depJarRefRisk;
	}

	public Element getRiskEle() {
		Element ele = new DefaultElement("nodeRisk");
		ele.addAttribute("id", node.getWholePath());
		Element clsesEle = ele.addElement("riskClasses");
		for (String riskCls : getlimitRefTb()) {
			Set<String> ers = refTb.getErs(riskCls);
			if (ers.size() > 0) {
				Element clsEle = clsesEle.addElement("riskClass");
				clsEle.addAttribute("name", riskCls);
				clsEle.addAttribute("otherHas",""+ AllCls.i().contains(riskCls));
				for(String erCls:ers) {
					Element sourceEle = clsEle.addElement("source");
					sourceEle.addText(erCls);
				}
			}

		}
		return ele;
	}

	public LimitRefTb getlimitRefTb() {
		if (null == refTb) {
			LinkedList<NodeAdapter> ancestors = node.getAncestors(false);// from down to top
			refTb = new LimitRefTb(getThrowedClses());
			for (NodeAdapter node : ancestors) {
				refTb.union(node.getDepJar().getRefTb());
			}
		}
		return refTb;
	}

	public Set<String> getThrowedClses() {
		return jarRisk.getThrowedClses();
	}

	// public Set<String> getRiskClses(){
	// Set<String> riskMthds = new HashSet<String>();
	// Set<String> refedClses = getRefedClses();
	// for(String throwCls:getThrowedClses()) {
	// if(refedClses.contains(throwCls)) {
	// riskMthds.add(throwCls);
	// }
	// }
	// return riskMthds;
	// }
}
