package neu.lab.conflict.container;

import java.util.HashSet;
import java.util.Set;

import javassist.ClassPool;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;

public class AllRefedCls {
	private static AllRefedCls instance;
	private Set<String> refedClses;

	private AllRefedCls() {
		long start = System.currentTimeMillis();
		refedClses = new HashSet<String>();
		try {
			ClassPool pool = new ClassPool();
			for (DepJar depJar : DepJars.i().getAllDepJar()) {
				for (String path : depJar.getJarFilePaths(true)) {
					pool.appendClassPath(path);
				}
			}
			for (String cls : AllCls.i().getAllCls()) {
				refedClses.add(cls);
				if (pool.getOrNull(cls) != null) {
					refedClses.addAll(pool.get(cls).getRefClasses());
				}
			}
		} catch (Exception e) {
			MavenUtil.i().getLog().error("get refedCls error:", e);
		}
		long runtime = (System.currentTimeMillis() - start) / 1000;
		MavenUtil.i().getLog().info("time to get all refedClass:" + runtime);
	}

	public static AllRefedCls i() {
		if (instance == null) {
			instance = new AllRefedCls();
		}
		return instance;
	}
	
	public boolean contains(String cls) {
		return refedClses.contains(cls);
	}

}
