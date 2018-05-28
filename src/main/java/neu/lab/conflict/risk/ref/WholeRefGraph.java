package neu.lab.conflict.risk.ref;

import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.graph.ClsRefGraph;
import neu.lab.conflict.graph.ClsRefNode;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;

public class WholeRefGraph {

	// private static WholeRefGraph instance;
	//
	// public static WholeRefGraph i() {
	// if (instance == null) {
	// instance = new WholeRefGraph();
	// }
	// return instance;
	// }

	private ClsRefGraph graph;

	public WholeRefGraph(DepJar throwedJar) {
		graph = new ClsRefGraph();
		try {
			ClassPool pool = new ClassPool();
			Set<String> allSysCls = new HashSet<String>();
			for (DepJar jar : DepJars.i().getAllDepJar()) {
				if (jar == throwedJar || (jar.isSelected() && !jar.isSameLib(throwedJar))) {
					for (String path : jar.getJarFilePaths(true)) {
						pool.appendClassPath(path);
					}
					for (String jarCls : jar.getAllCls(true)) {
						graph.addNode(jarCls);
						allSysCls.add(jarCls);
					}
				}
			}
			for (String sysCls : allSysCls) {// each er
				for (Object ee : pool.get(sysCls).getRefClasses()) {
					if(!sysCls.equals(ee)) {
						ClsRefNode node = (ClsRefNode) graph.getNode((String) ee);
						if (node != null)
							node.addInCls(sysCls);
					}
				}
			}

		} catch (Exception e) {
			MavenUtil.i().getLog().error("get refedCls error:", e);
		}
	}

	public String toString() {
		return graph.toString();
	}
}
