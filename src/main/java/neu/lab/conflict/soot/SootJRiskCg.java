package neu.lab.conflict.soot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.graph.Graph4branch;
import neu.lab.conflict.graph.Node4branch;
import neu.lab.conflict.graph.mthdprob.Graph4MthdProb;
import neu.lab.conflict.graph.mthdprob.Node4MthdProb;
import neu.lab.conflict.risk.jar.DepJarJRisk;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.vo.MethodCall;
import soot.Body;
import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.internal.JIfStmt;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.util.queue.QueueReader;

public class SootJRiskCg extends SootAna {
	public static long runtime = 0;
	private static SootJRiskCg instance = new SootJRiskCg();

	private SootJRiskCg() {

	}

	public static SootJRiskCg i() {
		return instance;
	}

	public void cmpCg(DepJarJRisk depJarJRisk) {
		MavenUtil.i().getLog().info("use soot to compute reach methods for " + depJarJRisk.toString());

		try {
			long startTime = System.currentTimeMillis();
			SootUtil.modifyLogOut();

			JRiskCgTf transformer = new JRiskCgTf(depJarJRisk);
			PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));

			soot.Main.main(getArgs(depJarJRisk.getPrcDirPaths().toArray(new String[0])).toArray(new String[0]));

			depJarJRisk.setRchedMthds(transformer.getRchMthds());

			depJarJRisk.setGraph(transformer.getGraph());

			runtime = runtime + (System.currentTimeMillis() - startTime) / 1000;
		} catch (Exception e) {
			MavenUtil.i().getLog().warn("cg error: ", e);

			depJarJRisk.setRchedMthds(new HashSet<String>());

			depJarJRisk.setGraph(new Graph4branch(new HashMap<String, Node4branch>(), new ArrayList<MethodCall>()));
		}
		soot.G.reset();
	}

	@Override
	protected void addCgArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "cg", "off", }));
	}
}

/**
 * to get reachedMthd and call-graph.
 * 
 * @author asus
 *
 */
class JRiskCgTf extends SceneTransformer {

	// private DepJarJRisk depJarJRisk;
	private Set<String> entryClses;
	private Set<String> conflictJarClses;
	private Set<String> riskMthds;
	private Set<String> rchMthds;
	private Graph4branch graph;

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
		// set entry
		List<SootMethod> entryMthds = new ArrayList<SootMethod>();
		for (SootClass sootClass : Scene.v().getApplicationClasses()) {
			if (entryClses.contains(sootClass.getName())) {// entry class
				for (SootMethod method : sootClass.getMethods()) {
					entryMthds.add(method);
				}
			}
		}

		Scene.v().setEntryPoints(entryMthds);
		CHATransformer.v().transform("wjtp", cgMap);

		// get reachedMthds.
		QueueReader<MethodOrMethodContext> entryRchMthds = Scene.v().getReachableMethods().listener();
		while (entryRchMthds.hasNext()) {
			SootMethod method = entryRchMthds.next().method();
			if (conflictJarClses.contains(method.getDeclaringClass().getName())) {// is method in duplicate jar
				rchMthds.add(method.getSignature());
			}
		}
		if (graph == null) {
			MavenUtil.i().getLog().info("start form graph...");
			// get call-graph.
			Map<String, Node4branch> name2node = new HashMap<String, Node4branch>();
			List<MethodCall> mthdRlts = new ArrayList<MethodCall>();
			CallGraph cg = Scene.v().getCallGraph();
			Iterator<Edge> ite = cg.iterator();
			while (ite.hasNext()) {
				Edge edge = ite.next();

				String srcMthdName = edge.src().getSignature();
				String tgtMthdName = edge.tgt().getSignature();
				String srcClsName = edge.src().getDeclaringClass().getName();
				String tgtClsName = edge.tgt().getDeclaringClass().getName();
				if (edge.src().isJavaLibraryMethod() || edge.tgt().isJavaLibraryMethod()) {
					//filter relation contains javaLibClass
				} else if (conflictJarClses.contains(SootUtil.mthdSig2cls(srcMthdName))
						&& conflictJarClses.contains(SootUtil.mthdSig2cls(tgtMthdName))) {
					// filter relation inside conflictJar
				} else {
					if (!name2node.containsKey(srcMthdName)) {
						name2node.put(srcMthdName, new Node4branch(srcMthdName, entryClses.contains(srcClsName),
								riskMthds.contains(srcMthdName), getBranchNum(edge.src())));
					}
					if (!name2node.containsKey(tgtMthdName)) {
						name2node.put(tgtMthdName, new Node4branch(tgtMthdName, entryClses.contains(tgtClsName),
								riskMthds.contains(tgtMthdName), getBranchNum(edge.tgt())));
					}
					mthdRlts.add(new MethodCall(srcMthdName, tgtMthdName));
				}
			}
			graph = new Graph4branch(name2node, mthdRlts);
			MavenUtil.i().getLog().info("end form graph.");
		}
		MavenUtil.i().getLog().info("JRiskCgTf end..");
	}

	public Set<String> getRchMthds() {
		return rchMthds;
	}

	public Graph4branch getGraph() {

		return graph;
	}

	private int getBranchNum(SootMethod sootMethod) {

		if(sootMethod.getSource()==null) {
			return 0;
		}
		int cnt = 0;
		Body body = sootMethod.retrieveActiveBody();
		for (Unit unit : body.getUnits()) {
			if (isBranchNode(unit)) {
				cnt++;
			}
		}
		return cnt;
	}

	private boolean isBranchNode(Unit unit) {
		if (unit instanceof soot.jimple.internal.JIfStmt) {
			JIfStmt ifS = (JIfStmt) unit;
			if (!ifS.getTargetBox().getUnit().branches()) {
				return true;
			}
		}
		if (unit instanceof soot.jimple.internal.AbstractSwitchStmt) {
			return true;
		}
		return false;
	}

}
