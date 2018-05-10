package neu.lab.conflict.writer;

import java.io.FileWriter;
import java.io.PrintWriter;

import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.distance.Dijkstra;
import neu.lab.conflict.distance.MethodDistance;
import neu.lab.conflict.risk.DepJarRiskAna;
import neu.lab.conflict.risk.NodeRiskAna;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.Conflict;

public class DistanceWriter {
	public void writeDistance(String outPath,boolean append) {
		try {
			PrintWriter printer = new PrintWriter(new FileWriter(outPath,append));
			for(Conflict conflict : Conflicts.i().getConflicts()) {
				for(DepJarRiskAna jarRisk:conflict.getRiskAna().getJarRiskAnas()) {
					for(NodeRiskAna nodeRisk:jarRisk.getNodeRiskAnas()) {
						MavenUtil.i().getLog().info("risk Mthd:"+nodeRisk.getRisk2Mthds().size());
						Dijkstra dj = new Dijkstra(nodeRisk.getGraph());
						printer.println(dj.toString());
						MethodDistance.i().addDistances(dj.getDistanceTb(nodeRisk.getRisk2Mthds()));
					}
				}
			}
			printer.println(MethodDistance.i().toString());
			printer.close();
		}catch(Exception e) {
			MavenUtil.i().getLog().error("can't write distance risk:", e);
		}
		
	}
}
