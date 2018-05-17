package neu.lab.conflict.distance;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class DijkstraTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws Exception {
//		File f = new File("projectFile/distance.txt");
//		System.out.println(f.getAbsolutePath());
//		Dijkstra dj = loadGraph(f);
//		Set<String> startNds = new HashSet<String>();
//		startNds.add("<neu.lab.plug.testcase.homemade.b.B1: void m2()>");
//		startNds.add("<neu.lab.plug.testcase.homemade.b.B2: void m1()>");
//		 Map<String, Map<String, Double>> distances = dj.getDistanceTb(startNds);
//		
//		 for(String source:distances.keySet()) {
//		 Map<String,Double> dises = distances.get(source);
//		 for(String target:dises.keySet()) {
//			 System.out.println(source+","+target+","+dises.get(target));
//		 }
//		 
//		 MethodDistance.i().addDistances(distances);
//		 System.out.println(MethodDistance.i().toString());
//	 }
	}

	private Dijkstra loadGraph(File graphFile) throws Exception {
		Dijkstra dj = new Dijkstra();
		BufferedReader reader = new BufferedReader(new FileReader(graphFile));
		String line = reader.readLine();
		DijkstraNode currentNd = null;
		while (line != null) {
			if (!"".equals(line)) {
				line = line.replace(",", "");
				if(line.startsWith("node:")) {
					currentNd = new DijkstraNode(line.replace("node:", ""));
					dj.addNode(currentNd);
				}else {
					currentNd.addIn(line);
				}
			}
			line = reader.readLine();
		}
		reader.close();
		return dj;
	}
}
