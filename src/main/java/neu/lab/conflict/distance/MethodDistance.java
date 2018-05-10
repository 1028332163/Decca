package neu.lab.conflict.distance;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MethodDistance {

	private Map<String, Map<String, Double>> distances;// <source,<target,distance>>

	private MethodDistance() {
		distances = new HashMap<String, Map<String, Double>>();
	}

	public static MethodDistance i() {
		return new MethodDistance();
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
		 for(String source:distances.keySet()) {
			 Map<String,Double> dises = distances.get(source);
			 for(String target:dises.keySet()) {
				 sb.append(source+","+target+","+dises.get(target));
			 }
		 }
		 return sb.toString();
	 }
}
