package neu.lab.conflict.util;

import java.io.FileWriter;
import java.io.PrintWriter;

import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.distance.Dijkstra;
import neu.lab.conflict.distance.NodeDistances;
import neu.lab.conflict.risk.node.DepJarNRisk;
import neu.lab.conflict.risk.node.NodeNRisk;
import neu.lab.conflict.vo.Conflict;

public class DebugUtil {
	private static DebugUtil instance;

	public DebugUtil() {

	}

	public static DebugUtil i() {
		if (instance == null) {
			instance = new DebugUtil();
		}
		return instance;
	}
	
	public void print(String outFilePath,String content,boolean append) {
		try {
			PrintWriter printer = new PrintWriter(new FileWriter(outFilePath,append));
			printer.println(content);
			printer.close();
		}catch(Exception e) {
			MavenUtil.i().getLog().error("ioException:", e);
		}
	}
}
