package neu.lab.conflict.distance;

import java.util.HashMap;
import java.util.Map;


public abstract class NodeDistances {

	private Map<String, Map<String, Double>> b2t2d;// <bottom,<top,distance>>


	public NodeDistances() {
		b2t2d = new HashMap<String, Map<String, Double>>();
	}

//	public static NodeDistances i() {
//		if (instance == null) {
//			instance = new NodeDistances();
//		}
//		return instance;
//	}

	public void addDistances(Map<String, Map<String, Double>> newData) {
		for (String bottom : newData.keySet()) {
			if (b2t2d.containsKey(bottom)) {// has this bottom.
				Map<String, Double> oldT2d = b2t2d.get(bottom);
				Map<String, Double> newT2d = newData.get(bottom);
				for (String top : newT2d.keySet()) {
					if (oldT2d.containsKey(top)) {// has this bottom
						if (newT2d.get(top) < oldT2d.get(top)) {
							oldT2d.put(top, newT2d.get(top));
						}
					} else {// new target
						oldT2d.put(top, newT2d.get(top));
					}
				}
			} else {// new source
				b2t2d.put(bottom, newData.get(bottom));
			}
		}
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String source : b2t2d.keySet()) {
			Map<String, Double> dises = b2t2d.get(source);
			for (String target : dises.keySet()) {
				sb.append(source + "," + target + "," + dises.get(target)+","+isHostNode(target));
				sb.append(System.lineSeparator());
			}
		}
		return sb.toString();
	}
	public abstract boolean isHostNode(String nodeName);
}
