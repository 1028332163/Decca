package neu.lab.conflict.soot.tf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.graph.IGraph;
import neu.lab.conflict.risk.jar.DepJarJRisk;
import neu.lab.conflict.util.MavenUtil;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.util.queue.QueueReader;

/**
 * to get call-graph.
 * 
 * @author asus
 *
 */
public abstract class JRiskCgTf extends SceneTransformer {

	// private DepJarJRisk depJarJRisk;
	protected Set<String> entryClses;
	protected Set<String> conflictJarClses;
	protected Set<String> riskMthds;
	protected Set<String> rchMthds;
	protected IGraph graph;

	public JRiskCgTf(DepJarJRisk depJarJRisk) {
		super();
		// this.depJarJRisk = depJarJRisk;
		entryClses = depJarJRisk.getEntryJar().getAllCls(true);
		conflictJarClses = depJarJRisk.getConflictJar().getAllCls(true);
		riskMthds = depJarJRisk.getThrownMthds();
		rchMthds = new HashSet<String>();

	}

	@Override
	protected void internalTransform(String arg0, Map<String, String> arg1) {

		MavenUtil.i().getLog().info("JRiskCgTf start..");
		Map<String, String> cgMap = new HashMap<String, String>();
		cgMap.put("enabled", "true");
		cgMap.put("apponly", "true");
		cgMap.put("all-reachable", "true");
		// // set entry
		// List<SootMethod> entryMthds = new ArrayList<SootMethod>();
		// for (SootClass sootClass : Scene.v().getApplicationClasses()) {
		// if (entryClses.contains(sootClass.getName())) {// entry class
		// for (SootMethod method : sootClass.getMethods()) {
		// entryMthds.add(method);
		// }
		// }
		// }
		// Scene.v().setEntryPoints(entryMthds);

		CHATransformer.v().transform("wjtp", cgMap);

		// get reachedMthds.
		QueueReader<MethodOrMethodContext> entryRchMthds = Scene.v().getReachableMethods().listener();
		while (entryRchMthds.hasNext()) {
			SootMethod method = entryRchMthds.next().method();
			if (conflictJarClses.contains(method.getDeclaringClass().getName())) {// is method in duplicate jar
				rchMthds.add(method.getSignature());
			}
		}
		formGraph();
		MavenUtil.i().getLog().info("JRiskCgTf end..");
	}

	protected abstract void formGraph();

	public Set<String> getRchMthds() {
		return rchMthds;
	}

	public IGraph getGraph() {
		return graph;
	}

}