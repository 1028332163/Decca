package neu.lab.conflict.soot;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import neu.lab.conflict.GlobalVar;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;
import neu.lab.conflict.vo.DepJar;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.util.Chain;

public class SootRiskMthdFilter2 extends SootAna{
	public void filterRiskMthds(DepJar depjar,Collection<String> mthds2test) {
		long start = System.currentTimeMillis();
		try {
			SootUtil.modifyLogOut();

			RiskMthdFilter2Tf transformer = new RiskMthdFilter2Tf(mthds2test);
			PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));

			soot.Main.main(getArgs(depjar.getRepalceCp().toArray(new String[0])).toArray(new String[0]));

		} catch (Exception e) {
			MavenUtil.i().getLog().warn("error when filter risk methods: ", e);

		}
		soot.G.reset();
		long runtime = (System.currentTimeMillis() - start) / 1000;
		GlobalVar.time2filterRiskMthd += runtime;
	}

	@Override
	protected void addCgArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "cg", "off", }));
	}
}
class RiskMthdFilter2Tf extends SceneTransformer {

	private Collection<String> mthds2test;

	public RiskMthdFilter2Tf(Collection<String> mthds2test) {
		super();
		this.mthds2test = mthds2test;
	}

	@Override
	protected void internalTransform(String arg0, Map<String, String> arg1) {
		filterRiskMthds();
		MavenUtil.i().getLog().info("riskMethod size after filter:" + mthds2test.size());
	}

	private void filterRiskMthds() {
		
		Iterator<String> ite = mthds2test.iterator();
		while (ite.hasNext()) {
			String testMthd = ite.next();
			// <neu.lab.plug.testcase.homemade.b.B2: void m1()>
			String[] pre_suf = testMthd.split(":");
			String className = pre_suf[0].substring(1);// neu.lab.plug.testcase.homemade.b.B2
			String mthdSuffix = pre_suf[1];// void m1()>
			if (!Scene.v().containsClass(className)) {// weird class
				MavenUtil.i().getLog().info("remove weird method:" + testMthd);
				ite.remove();
			} else if (hasSuperDefine(className, mthdSuffix)) {
//				MavenUtil.i().getLog().info("remove father-implement-method:" + testMthd);
				ite.remove();
			}
//			MavenUtil.i().getLog().info(hasFatherImpl(className, mthdSuffix)+"");
		}
	}
	
	private boolean hasSuperDefine(String className, String mthdSuffix) {
		Set<SootClass> allSuper = new HashSet<SootClass>();
		for(SootClass superClass:allSuper) {
			String fathMthdSig = "<" + superClass.getName() + ":" + mthdSuffix;
			if (Scene.v().containsMethod(fathMthdSig)) 
				return true;
		}
		return false;
	}
	
	private void getSuper(SootClass cls, Set<SootClass> allSuper) {
		Set<SootClass> allDirectSuper = new HashSet<SootClass>();

		if (cls.hasSuperclass()) {
			allDirectSuper.add(cls.getSuperclass());
			allSuper.add(cls.getSuperclass());
		}

		Chain<SootClass> superInters = cls.getInterfaces();
		if (null != superInters) {
			for (SootClass superInter : superInters) {
				allDirectSuper.add(superInter);
				allSuper.add(superInter);
			}
		}
		if (!allDirectSuper.isEmpty()) {
			for (SootClass superC : allDirectSuper) {
				getSuper(superC, allSuper);
			}
		}
		
	}
}