package neu.lab.conflict.risk.ref;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeAdapter;

public class DepJarRefRisk {

	private DepJar depJar;
	private ConflictRefRisk conflictRisk;
	private List<NodeRefRisk> nodeRisks;
	private Set<String> throwClses;

	public DepJarRefRisk(DepJar depJar, ConflictRefRisk conflictRisk) {
		this.depJar = depJar;
		this.conflictRisk = conflictRisk;
		nodeRisks = new ArrayList<NodeRefRisk>();
		for (NodeAdapter node : depJar.getNodeAdapters()) {
			nodeRisks.add(new NodeRefRisk(node, this));
		}
	}

	public Set<String> getThrowedClses() {
		if (throwClses == null) {
			throwClses = depJar.getOnlyClses(conflictRisk.getUsedDepJar());
		}
		return throwClses;
	}
	
	public Element getRiskEle() {
		Element ele = new DefaultElement("jarRisk");
		ele.addAttribute("id", depJar.toString());
		for (NodeRefRisk nodeRiskAna : nodeRisks) {
			ele.add(nodeRiskAna.getRiskEle());
		}
		return ele;
	}

}
