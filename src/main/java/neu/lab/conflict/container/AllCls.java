package neu.lab.conflict.container;

import java.util.HashSet;
import java.util.Set;

import neu.lab.conflict.vo.DepJar;

/**
 * @author asus
 *FinalClasses is set of ClassVO,but AllCls is set of class signature.
 */
public class AllCls {
	private static AllCls instance;
	private Set<String> clses;

	public static void init(DepJars depJars) {
		if (instance == null) {
			instance = new AllCls(depJars);
		}
	}
	public static AllCls i() {
		return instance;
	}
	
	private AllCls(DepJars depJars){
		clses = new HashSet<String>();
		for (DepJar depJar : depJars.getAllDepJar()) {
			if (depJar.isSelected()) {
				clses.addAll(depJar.getAllCls(true));
			}
		}
	}
	
	public boolean contains(String cls) {
		return clses.contains(cls);
	}
}
