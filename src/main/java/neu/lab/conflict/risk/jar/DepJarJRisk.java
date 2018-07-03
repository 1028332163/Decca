package neu.lab.conflict.risk.jar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.Conf;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.distance.MethodProbDistances;
import neu.lab.conflict.graph.Dog;
import neu.lab.conflict.graph.Graph4branch;
import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IRecord;
import neu.lab.conflict.graph.Node4branch;
import neu.lab.conflict.graph.Record4branch;
import neu.lab.conflict.soot.SootJRiskCg;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.MethodCall;

public class DepJarJRisk {
	private DepJar depJar;
	private ConflictJRisk conflictRisk;
	private Set<String> thrownMthds;
	private Set<String> rchedMthds;
	private Graph4branch graph;
	private Map<String, IBook> books;

	public DepJarJRisk(DepJar depJar, ConflictJRisk conflictRisk) {
		this.depJar = depJar;
		this.conflictRisk = conflictRisk;
		// calculate thrownMthd
		
		// calculate call-graph
		if (getThrownMthds().size() > 0) {
			SootJRiskCg.i().cmpCg(this);
			// calculate distance
			books = new Dog(graph).findRlt(graph.getHostNds(), Conf.DOG_FIND_DEP);
		} else {
			this.setRchedMthds(new HashSet<String>());
			this.setGraph(new Graph4branch(new HashMap<String, Node4branch>(), new ArrayList<MethodCall>()));
			this.books = new HashMap<String, IBook>();
		}

	}

	public String getVersion() {
		return depJar.getVersion();
	}

	public Set<String> getThrownMthds() {
		if (thrownMthds == null) {
			thrownMthds = conflictRisk.getUsedDepJar().getRiskMthds(depJar.getAllMthd());
//			thrownMthds.add("<neu.lab.plug.testcase.homemade.host.prob.ProbBottom: void m()>");
			MavenUtil.i().getLog().info("thrownMthds size:" + thrownMthds.size());
		}
		return thrownMthds;
	}

	private Map<String, IBook> getBooks() {
		return books;
	}

	public MethodProbDistances getMethodProDistances() {
		MethodProbDistances distances = new MethodProbDistances();
		Map<String, IBook> books = getBooks();
		for (IBook book : books.values()) {
//			MavenUtil.i().getLog().info("book:"+book.getNodeName());
			for (IRecord iRecord : book.getRecords()) {
				
				Record4branch record = (Record4branch) iRecord;
//				MavenUtil.i().getLog().info("record:"+record.getName());
				distances.addDistance(record.getName(), book.getNodeName(), record.getDistance());
				distances.addProb(record.getName(), book.getNodeName(), record.getBranch());
			}
		}
		return distances;
	}

	public List<String> getPrcDirPaths() throws Exception {
		List<String> paths = new ArrayList<String>();
		paths.addAll(depJar.getJarFilePaths(true));
		boolean hasRepalce = false;
		for (DepJar usedDepJar : DepJars.i().getUsedDepJars()) {
			if (depJar.isSameLib(usedDepJar)) {// used depJar instead of usedDepJar.
				if (hasRepalce) {
					MavenUtil.i().getLog().warn("when cg, find multiple usedLib for " + depJar);
					throw new Exception("when cg, find multiple usedLib for " + depJar);
				}
				hasRepalce = true;
			} else {
				paths.addAll(usedDepJar.getJarFilePaths(true));
			}
		}
		if (!hasRepalce) {
			MavenUtil.i().getLog().warn("when cg,can't find mutiple usedLib for " + depJar);
			throw new Exception("when cg,can't find mutiple usedLib for " + depJar);
		}
		return paths;
	}

	public DepJar getEntryJar() {
		return DepJars.i().getHostDepJar();
	}

	public DepJar getConflictJar() {
		return depJar;
	}

	public void setRchedMthds(Set<String> rchedMthds) {
		this.rchedMthds = rchedMthds;
	}

	public void setGraph(Graph4branch graph) {
		this.graph = graph;
	}

	@Override
	public String toString() {
		return depJar.toString() + " in conflict " + conflictRisk.getConflict().toString();
	}

}
