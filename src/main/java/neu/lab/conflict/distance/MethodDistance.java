package neu.lab.conflict.distance;

import java.util.HashMap;
import java.util.Map;

import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;

public class MethodDistance {

	private Map<String, Map<String, Double>> distances;// <source,<target,distance>>

	private static MethodDistance instance;

	private MethodDistance() {
		distances = new HashMap<String, Map<String, Double>>();
	}

	public static MethodDistance i() {
		if (instance == null) {
			instance = new MethodDistance();
		}
		return instance;
	}

	public void addDistances(Map<String, Map<String, Double>> newData) {
		for (String source : newData.keySet()) {
			if (distances.containsKey(source)) {// old source
				Map<String, Double> oldDises = distances.get(source);
				Map<String, Double> newDises = distances.get(source);
				for (String target : newDises.keySet()) {
					if (oldDises.containsKey(target)) {// old target
						if (newDises.get(target) < oldDises.get(target)) {
							oldDises.put(target, newDises.get(target));
						}
					} else {// new target
						oldDises.put(target, newDises.get(target));
					}
				}
			} else {// new source
				distances.put(source, newData.get(source));
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String source : distances.keySet()) {
			Map<String, Double> dises = distances.get(source);
			for (String target : dises.keySet()) {
				sb.append(source + "," + target + "," + dises.get(target)+","+MavenUtil.i().isHostClass(SootUtil.mthdSig2cls(target)));
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}
}
