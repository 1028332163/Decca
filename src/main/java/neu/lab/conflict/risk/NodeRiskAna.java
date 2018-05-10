package neu.lab.conflict.risk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import neu.lab.conflict.Conf;
import neu.lab.conflict.graph.MthdPathBook;
import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.Dog;
import neu.lab.conflict.graph.MthdRltGraph;
import neu.lab.conflict.graph.MthdPathNode;
import neu.lab.conflict.graph.MthdPathRecord;
import neu.lab.conflict.soot.SootCg;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.MethodCall;
import neu.lab.conflict.vo.NodeAdapter;

/**
 * @author asus
 *
 */
public class NodeRiskAna {
	private LinkedList<NodeAdapter> anaAncestors;// there is order(from down to up)
	private Set<String> rchedMthds;// reached method in call-graph computed
	private Set<String> rchedServices;//

	private Set<String> risk1Mthds;// reached and thrown
	private Set<String> risk2Mthds;// reached and thrown and called by method in other jar.
	private DepJarRiskAna jarRiskAna;
	private MthdRltGraph graph;

	private Map<String, IBook> books;// reached path of method in risk2Mthds

	public Element getRiskPathEle() {
		Element ele = new DefaultElement("nodeRisk");
		ele.addAttribute("id", toString());
		ele.addAttribute("reached_size", "" + rchedMthds.size());
		ele.addAttribute("reached_thrown_size", "" + getRisk1Mthds().size());
		ele.addAttribute("reached_thrown_service", "" + getRisk2Mthds().size());

		Element pathsEle = ele.addElement("paths");
		List<String> confuseMthds = new ArrayList<String>();
		for (String risk2Mthd : getRisk2Mthds()) {
			MthdPathBook book = (MthdPathBook)getBooks().get(risk2Mthd);
			if (book == null) {
				confuseMthds.add(risk2Mthd);
			} else {
				List<MthdPathRecord> riskPath = book.getRiskPath();
				if (riskPath.size() == 0) {
					// this method is reached by host on soot-call-graph,but can't find path for
					// it.Confusion may be cause by algorithm or by call filter
					confuseMthds.add(risk2Mthd);
				} else {
					Element methodEle = pathsEle.addElement("method");
					methodEle.addAttribute("name", risk2Mthd.replace("<", "").replace(">", ""));
					for (MthdPathRecord path : riskPath) {
						Element pathEle = methodEle.addElement("path");
						pathEle.addAttribute("isFromHost", ""+path.isFromHost());
						pathEle.addAttribute("length",""+path.getPathLen());
						pathEle.addText(path.getPathStr().replace("<", "").replace(">", ""));
					}
				}
			}
		}
		
		
		if (Conf.PRINT_CONFUSED_METHOD) {
			Element confuseEle = ele.addElement("confusedMethods") ;
			confuseEle.addAttribute("size", ""+confuseMthds.size() );
			if (confuseMthds.size() != 0) {
				for (String confuseMthd : confuseMthds) {
					confuseEle.addElement("method").addText(confuseMthd.replace("<", "").replace(">", ""));
				}
			}
		}
		return ele;
	}

	public NodeRiskAna(NodeAdapter nodeAdapter, DepJarRiskAna jarRiskAna) {
		this.jarRiskAna = jarRiskAna;
		LinkedList<NodeAdapter> ancestors = nodeAdapter.getAncestors(true);
		if (ancestors.size() == 1) {// manageNode that don't have ancestor don‘t need analysis.
			this.anaAncestors = ancestors;
			rchedMthds = new HashSet<String>();
			rchedServices = new HashSet<String>();
			graph = new MthdRltGraph(new HashSet<MthdPathNode>(), new ArrayList<MethodCall>());
		} else {
			if (Conf.ANA_FROM_HOST) {// entry class is host class.
				this.anaAncestors = ancestors;
			} else {// entry class is up-jar-class.
				this.anaAncestors = new LinkedList<NodeAdapter>();
				this.anaAncestors.add(ancestors.get(0));
				this.anaAncestors.add(ancestors.get(1));
			}
			SootCg.i().cmpCg(this);
		}

	}

	public NodeAdapter getBottomNode() {
		return anaAncestors.getFirst();
	}

	public NodeAdapter getTopNode() {
		return anaAncestors.getLast();
	}

	public List<String> getJarFilePaths() {
		List<String> paths = new ArrayList<String>();
		for (NodeAdapter nodeAdapter : anaAncestors) {
			paths.addAll(nodeAdapter.getFilePath());
		}
		return paths;
	}

	@Override
	public String toString() {
		String str = "";
		for (NodeAdapter nodeAdapter : anaAncestors) {
			str = nodeAdapter.toString() + "->" + str;
		}
		return str;
	}

	public Set<String> getRchedMthds() {
		return rchedMthds;
	}

	public void setRchedMthds(Set<String> rchedMthds) {
		this.rchedMthds = rchedMthds;
	}

	public Set<String> getRisk1Mthds() {
		if (risk1Mthds == null) {
			risk1Mthds = getReplaceJar().getOutMthds(getRchedMthds());
		}
		return risk1Mthds;
	}

	public Set<String> getRisk2Mthds() {
		if (risk2Mthds == null) {
			risk2Mthds = getReplaceJar().getOutMthds(getRchedServices());
		}
		return risk2Mthds;
	}

	public Map<String, IBook> getBooks() {
		if (books == null)
			books = new Dog(graph).findRlt(getRisk2Mthds());
		return books;
	}

	public void setGraph(MthdRltGraph graph) {
		this.graph = graph;
	}
	
	public MthdRltGraph getGraph() {
		return this.graph;
	}

	public Set<String> getRchedServices() {
		return rchedServices;
	}

	public void setRchedServices(Set<String> rchedServices) {
		this.rchedServices = rchedServices;
	}

	public DepJarRiskAna getJarRiskAna() {
		return jarRiskAna;
	}

	public DepJar getReplaceJar() {
		return this.jarRiskAna.getReplaceJar();
	}

}
