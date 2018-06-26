package neu.lab.conflict.risk.jar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.graph.MthdRltGraph;
import neu.lab.conflict.soot.SootJRiskCg;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.MethodCall;

public class DepJarJRisk {
	private DepJar depJar;
	private ConflictJRisk conflictRisk;
	private Set<String> thrownMthds;
	private Set<String> rchedMthds;
	private MthdRltGraph graph;

	public DepJarJRisk(DepJar depJar, ConflictJRisk conflictRisk) {
		this.depJar = depJar;
		this.conflictRisk = conflictRisk;
		// calculate thrownMthd

		// calculate call-graph
		if (getThrownMthds().size() > 0) {
			SootJRiskCg.i().cmpCg(this);
		}
		// calculate distance

	}

	public Set<String> getThrownMthds() {
		if (thrownMthds == null) {
			thrownMthds = conflictRisk.getUsedDepJar().getRiskMthds(depJar.getAllMthd());
		}
		return thrownMthds;
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

	public void setGraph(MthdRltGraph graph) {
		this.graph = graph;
	}

	@Override
	public String toString() {
		return depJar.toString() + " in conflict " + conflictRisk.getConflict().toString();
	}

}
