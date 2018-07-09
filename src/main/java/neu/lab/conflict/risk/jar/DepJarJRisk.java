package neu.lab.conflict.risk.jar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.Conf;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.distance.MethodProbDistances;
import neu.lab.conflict.graph.Dog;
import neu.lab.conflict.graph.Graph4branch;
import neu.lab.conflict.graph.Graph4mthdPath;
import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IGraph;
import neu.lab.conflict.graph.IRecord;
import neu.lab.conflict.graph.Node4branch;
import neu.lab.conflict.graph.Node4mthdPath;
import neu.lab.conflict.graph.Record4branch;
import neu.lab.conflict.soot.SootJRiskCg;
import neu.lab.conflict.soot.SootRiskMthdFilter2;
import neu.lab.conflict.soot.tf.JRiskBranchCgTf;
import neu.lab.conflict.soot.tf.JRiskMthdPathCgTf;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.MethodCall;

public class DepJarJRisk {
	private DepJar depJar;
	private ConflictJRisk conflictRisk;
	private Set<String> thrownMthds;
	// private Set<String> rchedMthds;
	private Graph4branch graph4branch;
	private Map<String, IBook> books;

	public DepJarJRisk(DepJar depJar, ConflictJRisk conflictRisk) {
		this.depJar = depJar;
		this.conflictRisk = conflictRisk;
		// calculate thrownMthd

		// calculate call-graph

	}

	public String getVersion() {
		return depJar.getVersion();
	}

	public Set<String> getThrownMthds() {
		// "<neu.lab.plug.testcase.homemade.host.prob.ProbBottom: void m()>"
		if (thrownMthds == null) {

			// //TODO1
			// thrownMthds = new HashSet<String>();
			// thrownMthds.add("<com.fasterxml.jackson.core.JsonFactory: boolean
			// requiresPropertyOrdering()>");

			thrownMthds = conflictRisk.getUsedDepJar().getRiskMthds(depJar.getAllMthd());

			MavenUtil.i().getLog().info("riskMethod size before filter" + thrownMthds.size());
			if (thrownMthds.size() > 0)
				new SootRiskMthdFilter2().filterRiskMthds(depJar, thrownMthds);
			MavenUtil.i().getLog().info("riskMethod size after filter" + thrownMthds.size());

			// //TODO1
			// if(thrownMthds.contains("<com.fasterxml.jackson.core.JsonFactory: boolean
			// requiresPropertyOrdering()>")) {
			// MavenUtil.i().getLog().info("thronMethods has
			// <com.fasterxml.jackson.core.JsonFactory: boolean
			// requiresPropertyOrdering()>");
			// }

		}
		return thrownMthds;
	}

	public MethodProbDistances getMethodProDistances() {
		MethodProbDistances distances = new MethodProbDistances();
		Map<String, IBook> books = getBooks();
		for (IBook book : books.values()) {
			// MavenUtil.i().getLog().info("book:"+book.getNodeName());
			for (IRecord iRecord : book.getRecords()) {

				Record4branch record = (Record4branch) iRecord;
				// MavenUtil.i().getLog().info("record:"+record.getName());
				distances.addDistance(record.getName(), book.getNodeName(), record.getDistance());
				distances.addProb(record.getName(), book.getNodeName(), record.getBranch());
			}
		}
		return distances;
	}

	public Collection<String> getPrcDirPaths() throws Exception {
		return depJar.getRepalceCp();
	}

	public DepJar getEntryJar() {
		return DepJars.i().getHostDepJar();
	}

	public DepJar getConflictJar() {
		return depJar;
	}

	public Graph4branch getGraph4branch() {
		if (graph4branch == null) {
			if (getThrownMthds().size() > 0) {
				MavenUtil.i().getLog().info("first riskmthd:" + getThrownMthds().iterator().next());
				IGraph iGraph = SootJRiskCg.i().getGraph4branch(this,new JRiskBranchCgTf(this));
				if (iGraph != null) {
					graph4branch = (Graph4branch) iGraph;
				} else {
					graph4branch = new Graph4branch(new HashMap<String, Node4branch>(), new ArrayList<MethodCall>());
				}
			} else {
				graph4branch = new Graph4branch(new HashMap<String, Node4branch>(), new ArrayList<MethodCall>());
			}
		}
		return graph4branch;
	}

	public Graph4mthdPath getGraph4mthdPath() {
		if (getThrownMthds().size() > 0) {
			IGraph iGraph = SootJRiskCg.i().getGraph4branch(this,new JRiskMthdPathCgTf(this));
			if(iGraph!=null)
				return (Graph4mthdPath)iGraph;
		}
		return new Graph4mthdPath(new HashMap<String, Node4mthdPath>(), new ArrayList<MethodCall>());
	}

	private Map<String, IBook> getBooks() {
		if (this.books == null) {
			if (getThrownMthds().size() > 0) {
				// calculate distance
				books = new Dog(getGraph4branch()).findRlt(getGraph4branch().getHostNds(), Conf.DOG_FIND_DEP);
			} else {
				books = new HashMap<String, IBook>();
			}
		}
		return books;
	}

	@Override
	public String toString() {
		return depJar.toString() + " in conflict " + conflictRisk.getConflict().toString();
	}

}
