package neu.lab.conflict.writer;

import java.io.FileWriter;
import java.io.PrintWriter;

import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.distance.Dijkstra;
import neu.lab.conflict.distance.DijkstraSorted;
import neu.lab.conflict.distance.MethodDistance;
import neu.lab.conflict.risk.DepJarRiskAna;
import neu.lab.conflict.risk.NodeRiskAna;
import neu.lab.conflict.risk.ref.WholeRefGraph;
import neu.lab.conflict.util.DebugUtil;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.Conflict;
import neu.lab.conflict.vo.DepJar;

public class DistanceWriter {
	
	public void writeClsDistance(String outPath,boolean append) {
		try {
//			boolean exit = false;
			PrintWriter printer = new PrintWriter(new FileWriter(outPath,append));
			for(Conflict conflict : Conflicts.i().getConflicts()) {
				for(DepJar depJar:conflict.getDepJars()) {
					if(depJar!=conflict.getUsedDepJar()) {
						printer.println(new WholeRefGraph(depJar));
					}
				}
			}
			printer.close();
		}catch(Exception e) {
			MavenUtil.i().getLog().error("can't write distance risk:", e);
		}
		
	}
	
	
	public void writeMthdDistance(String outPath,boolean append) {
		try {
//			boolean exit = false;
			PrintWriter printer = new PrintWriter(new FileWriter(outPath,append));
			for(Conflict conflict : Conflicts.i().getConflicts()) {
				for(DepJarRiskAna jarRisk:conflict.getRiskAna().getJarRiskAnas()) {
					for(NodeRiskAna nodeRisk:jarRisk.getNodeRiskAnas()) {
						MavenUtil.i().getLog().info("risk Mthd:"+nodeRisk.getRisk2Mthds().size());
						MavenUtil.i().getLog().info("dj start:");
						Dijkstra dj = new DijkstraSorted(nodeRisk.getGraph());
						MavenUtil.i().getLog().info("write graph start:");
//						DebugUtil.i().print("d:\\djGraph.txt", dj.toString(), false);
						StringBuilder sb = new StringBuilder();
						for(String startNd:nodeRisk.getRisk2Mthds()) {
							sb.append(startNd);
							sb.append(System.lineSeparator());
						}
//						DebugUtil.i().print("d:\\startNds.txt", sb.toString(), false);
						MavenUtil.i().getLog().info("write graph end:");
						MethodDistance.i().addDistances(dj.getDistanceTb(nodeRisk.getRisk2Mthds()));
						MavenUtil.i().getLog().info("dj end:");
//						break;
					}
//					break;
				}
//				break;
			}
			printer.println(MethodDistance.i().toString());
			printer.close();
		}catch(Exception e) {
			MavenUtil.i().getLog().error("can't write distance risk:", e);
		}
		
	}
}
