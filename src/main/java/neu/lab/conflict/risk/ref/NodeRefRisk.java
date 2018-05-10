package neu.lab.conflict.risk.ref;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import neu.lab.conflict.container.AllCls;
import neu.lab.conflict.graph.ClsRefGraph;
import neu.lab.conflict.graph.ClsRefPathRecord;
import neu.lab.conflict.graph.Dog;
import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IRecord;
import neu.lab.conflict.risk.ref.tb.LimitRefTb;
import neu.lab.conflict.vo.NodeAdapter;

public class NodeRefRisk {
	private NodeAdapter node;
	private DepJarRefRisk jarRisk;
	private LimitRefTb limitRefTb;

	public NodeRefRisk(NodeAdapter node1, DepJarRefRisk depJarRefRisk) {
		this.node = node1;
		this.jarRisk = depJarRefRisk;
	}

	public Element getRiskEle() {
		return getRiskPathEle() ;
	}
	
	private Element getRiskRefEle() {
		Element ele = new DefaultElement("nodeRisk");
		ele.addAttribute("id", node.getWholePath());
		Element clsesEle = ele.addElement("riskClasses");
		for (String riskCls : getlimitRefTb()) {
			Set<String> ers = limitRefTb.getErs(riskCls);
			if (ers.size() > 0) {
				Element clsEle = clsesEle.addElement("riskClass");
				clsEle.addAttribute("name", riskCls);
				clsEle.addAttribute("otherHas", "" + AllCls.i().contains(riskCls));
				for (String erCls : ers) {
					Element sourceEle = clsEle.addElement("source");
					sourceEle.addText(erCls);
				}
			}

		}
		return ele;
	}
	
	private Element getRiskPathEle() {
		Element ele = new DefaultElement("nodeRisk");
		ele.addAttribute("id", node.getWholePath());
		Element clsesEle = ele.addElement("riskClasses");
		if(getlimitRefTb().eeSize()>0) {
			Map<String, IBook> books = new Dog(getClsRefGraph()).findRlt(getlimitRefTb().getRefedEes());
			for (String riskCls : getlimitRefTb()) {
				Set<String> ers = limitRefTb.getErs(riskCls);
				if (ers.size() > 0) {
					Element clsEle = clsesEle.addElement("riskClass");
					clsEle.addAttribute("name", riskCls);
					clsEle.addAttribute("otherHas", "" + AllCls.i().contains(riskCls));
					
					if(books.get(riskCls)!=null) {
						for(IRecord record:books.get(riskCls).getRecords()) {
							ClsRefPathRecord pathRecord = (ClsRefPathRecord)record;
							clsEle.add(pathRecord.getPathEle());
						}
					}
				}
			}
		}
		return ele;
	}

	public ClsRefGraph getClsRefGraph() {
		return ClsRefGraph.getGraph(this.node);
	}

	private LimitRefTb getlimitRefTb() {
		if (null == limitRefTb) {
			LinkedList<NodeAdapter> ancestors = node.getAncestors(false);// from down to top
			limitRefTb = new LimitRefTb(getThrowedClses());
			for (NodeAdapter node : ancestors) {
				limitRefTb.union(node.getDepJar().getRefTb());
			}
		}
		return limitRefTb;
	}

	private Set<String> getThrowedClses() {
		return jarRisk.getThrowedClses();
	}

}