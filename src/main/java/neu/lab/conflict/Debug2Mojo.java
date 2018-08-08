package neu.lab.conflict;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.graph.Book4mthdPath;
import neu.lab.conflict.graph.Dog;
import neu.lab.conflict.graph.Graph4mthdPath;
import neu.lab.conflict.graph.GraphPrinter;
import neu.lab.conflict.graph.IBook;
import neu.lab.conflict.graph.IRecord;
import neu.lab.conflict.graph.Record4mthdPath;
import neu.lab.conflict.risk.jar.DepJarJRisk;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.MySortedMap;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.vo.Conflict;
import neu.lab.conflict.vo.DepJar;

@Mojo(name = "debug2", defaultPhase = LifecyclePhase.VALIDATE)
public class Debug2Mojo extends ConflictMojo {

	@Override
	public void run() {
		writePath();
	}

	public void writePath() {
		String outDir = "D:\\ws_testcase\\image\\path\\";
		java.io.File f = new java.io.File(outDir);
		if (!f.exists()) {
			f.mkdirs();
		}

		String projectSig = (MavenUtil.i().getProjectCor()).replace(":", "+");
		for (Conflict conflict : Conflicts.i().getConflicts()) {

			String conflictSig = conflict.getSig().replace(":", "+");
			for (DepJarJRisk jarRisk : conflict.getJRisk().getJarRisks()) {
				String outPath = outDir + projectSig + "@" + conflictSig + "@" + jarRisk.getVersion() + ".txt";
				writeJarRisk(jarRisk, outPath, append);
			}

		}
	}

	private void writeJarRisk(DepJarJRisk jarRisk, String outPath, boolean append) {
		try {
			Graph4mthdPath graph = jarRisk.getGraph4mthdPath();
			Set<String> hostNds = graph.getHostNds();
			//TODO printGraph_path
//			GraphPrinter.printGraph(graph, "d:\\graph_mthdPath.txt",hostNds);
			//TODO path depth
			Map<String, IBook> books = new Dog(graph).findRlt(hostNds,30);

			MySortedMap<Integer, Record4mthdPath> dis2records = new MySortedMap<Integer, Record4mthdPath>();
			// List<Record4mthdPath> records = new ArrayList<Record4mthdPath>();

			for (String topMthd : books.keySet()) {
				if (hostNds.contains(topMthd)) {
					Book4mthdPath book = (Book4mthdPath) (books.get(topMthd));
					for (IRecord iRecord : book.getRecords()) {
						Record4mthdPath record = (Record4mthdPath) iRecord;
						dis2records.add(record.getPathlen(), record);
					}
				}
			}
			if (dis2records.size() > 0) {
				PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(outPath)));
				printer.println("classPath:"+DepJars.i().getUsedJarPathsStr());
				printer.println("pomPath:"+MavenUtil.i().getBaseDir());
				for (Record4mthdPath record : dis2records.flat()) {
					printer.println("pathLen:" + record.getPathlen() + "\n" + addJarPath(record.getPathStr()));
				}
				printer.close();
			}
			// printer.println(distances);

		} catch (IOException e) {
			MavenUtil.i().getLog().error("can't write jarRisk ", e);
		}
	}

	private String addJarPath(String mthdCallPath) {
		StringBuilder sb = new StringBuilder();
		String[] mthds = mthdCallPath.split("\\n");
		for (int i = 0; i < mthds.length - 1; i++) {
			// last method is risk method,don't need calculate.
			String mthd = mthds[i];
			String cls = SootUtil.mthdSig2cls(mthd);
			DepJar depJar = DepJars.i().getClassJar(cls);
			sb.append(mthd + " " + depJar.getJarFilePaths(true).get(0) + "\n");
		}
		sb.append(mthds[mthds.length-1]);
		return sb.toString();
	}

}
