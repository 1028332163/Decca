package neu.lab.conflict.soot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;

public class SootRiskMthdFilter extends SootAna {
	
	public void filterRiskMthds(Collection<String> mthds2test) {
		try {
			SootUtil.modifyLogOut();

			RiskMthdFilterTf transformer = new RiskMthdFilterTf(mthds2test);
			PackManager.v().getPack("wjtp").add(new Transform("wjtp.myTrans", transformer));

			soot.Main.main(getArgs(DepJars.i().getUsedJarPaths().toArray(new String[0])).toArray(new String[0]));

		} catch (Exception e) {
			MavenUtil.i().getLog().warn("error when filter risk methods: ", e);

		}
		soot.G.reset();
	}

	@Override
	protected void addCgArgs(List<String> argsList) {
		argsList.addAll(Arrays.asList(new String[] { "-p", "cg", "off", }));
	}

}

class RiskMthdFilterTf extends SceneTransformer {

	private Collection<String> mthds2test;

	public RiskMthdFilterTf(Collection<String> mthds2test) {
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
			} else if (hasFatherImpl(className, mthdSuffix)) {
				MavenUtil.i().getLog().info("remove father-implement-method:" + testMthd);
				ite.remove();
			}
		}
	}

	private boolean hasFatherImpl(String className, String mthdSuffix) {
		SootClass sootCls = Scene.v().getSootClass(className);
		while (sootCls.hasSuperclass()) {
			sootCls = sootCls.getSuperclass();
			String fathMthdSig = "<" + sootCls.getName() + ":" + mthdSuffix;
			if (Scene.v().containsMethod(fathMthdSig)) {
				SootMethod fatherMthd = Scene.v().getMethod(fathMthdSig);
				if (fatherMthd.isConcrete() || fatherMthd.isNative()) {
					return true;
				}
			}
		}
		return false;
	}
}