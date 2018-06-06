package neu.lab.conflict.writer;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;
import neu.lab.conflict.container.AllCls;
import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.distance.ClassDistances;
import neu.lab.conflict.distance.Dijkstra;
import neu.lab.conflict.distance.DijkstraSorted;
import neu.lab.conflict.distance.MethodDistances;
import neu.lab.conflict.distance.NodeDistances;
import neu.lab.conflict.graph.ClsRefGraph;
import neu.lab.conflict.graph.ClsRefNode;
import neu.lab.conflict.risk.DepJarRiskAna;
import neu.lab.conflict.risk.NodeRiskAna;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.Conflict;
import neu.lab.conflict.vo.DepJar;

public class DistanceWriter {

	public void writeClsDistance(String outPath, boolean append) {
		try {
			// boolean exit = false;
			NodeDistances distances = new ClassDistances();
			PrintWriter printer = new PrintWriter(new FileWriter(outPath, append));
			for (Conflict conflict : Conflicts.i().getConflicts()) {
				for (DepJar depJar : conflict.getDepJars()) {
					if (depJar != conflict.getUsedDepJar()) {
						Collection<String> thrownClses = AllCls.i()
								.getNotInClses(depJar.getOnlyClses(conflict.getUsedDepJar()));
						if (thrownClses.size() > 0) {
							// System.out.println();
							Dijkstra dj = new DijkstraSorted(depJar.getWholeClsRefG());
							distances.addDistances(dj.getDistanceTb(thrownClses));
						}
					}
				}
			}
			printer.println(distances.toString());
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write distance risk:", e);
		}

	}

	public void writeOnceClsDistance(String outPath, boolean append) {
		try {
			//get class that not in selected-jars.
			Collection<String> thrownClses = new HashSet<String>();
			for (Conflict conflict : Conflicts.i().getConflicts()) {
				for (DepJar depJar : conflict.getDepJars()) {
					if (depJar != conflict.getUsedDepJar()) {
						thrownClses.addAll(AllCls.i().getNotInClses(depJar.getOnlyClses(conflict.getUsedDepJar())));
					}
				}
			}
			if(thrownClses.size()>0) {
				//form class-graph
				ClsRefGraph graph = new ClsRefGraph();
				ClassPool pool = new ClassPool();
				Set<String> allSysCls = new HashSet<String>();
				for(String thrownCls:thrownClses) {
					graph.addNode(thrownCls);
				}
				for (DepJar jar : DepJars.i().getAllDepJar()) {
					if (jar.isSelected()) {
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
						if (!sysCls.equals(ee)) {//don't add relation of self.
							ClsRefNode node = (ClsRefNode) graph.getNode((String) ee);
							if (node != null)
								node.addInCls(sysCls);
						}
					}
				}
				//calculate distance
				Dijkstra dj = new DijkstraSorted(graph);
				NodeDistances distances = new ClassDistances();
				distances.addDistances(dj.getDistanceTb(thrownClses));
				PrintWriter printer = new PrintWriter(new FileWriter(outPath, append));
				printer.println(distances.toString());
				printer.close();
			}
			
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write distance risk:", e);
		}

	}

	public void writeMthdDistance(String outPath, boolean append) {
		try {
			// boolean exit = false;
			PrintWriter printer = new PrintWriter(new FileWriter(outPath, append));
			NodeDistances distances = new MethodDistances();
			for (Conflict conflict : Conflicts.i().getConflicts()) {
				for (DepJarRiskAna jarRisk : conflict.getRiskAna().getJarRiskAnas()) {
					for (NodeRiskAna nodeRisk : jarRisk.getNodeRiskAnas()) {
						MavenUtil.i().getLog().info("risk Mthd:" + nodeRisk.getRisk2Mthds().size());
						MavenUtil.i().getLog().info("dj start:");
						Dijkstra dj = new DijkstraSorted(nodeRisk.getGraph());
						MavenUtil.i().getLog().info("write graph start:");
						// DebugUtil.i().print("d:\\djGraph.txt", dj.toString(), false);
						StringBuilder sb = new StringBuilder();
						for (String startNd : nodeRisk.getRisk2Mthds()) {
							sb.append(startNd);
							sb.append(System.lineSeparator());
						}
						// DebugUtil.i().print("d:\\startNds.txt", sb.toString(), false);
						MavenUtil.i().getLog().info("write graph end:");
						distances.addDistances(dj.getDistanceTb(nodeRisk.getRisk2Mthds()));
						MavenUtil.i().getLog().info("dj end:");
						// break;
					}
					// break;
				}
				// break;
			}
			printer.println(distances.toString());
			printer.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write distance risk:", e);
		}

	}
}
