package neu.lab.conflict.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import javassist.ClassPool;
import neu.lab.conflict.Conf;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.graph.ClsRefGraph;
import neu.lab.conflict.graph.ClsRefNode;
import neu.lab.conflict.risk.ConflictRiskAna;
import neu.lab.conflict.risk.DepJarRiskAna;
import neu.lab.conflict.risk.ref.tb.NoLimitRefTb;
import neu.lab.conflict.soot.JarAna;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.util.SootUtil;

/**
 * @author asus
 *
 */
public class DepJar {
	private String groupId;
	private String artifactId;// artifactId
	private String version;// version
	private String classifier;
	private List<String> jarFilePaths;// host project may have multiple source.
	private Map<String, ClassVO> clsTb;// all class in jar
	private Set<NodeAdapter> nodeAdapters;// all
	private DepJarRiskAna jarRisk;
	private Set<String> allMthd;

	public DepJar(String groupId, String artifactId, String version, String classifier, List<String> jarFilePaths) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.classifier = classifier;
		this.jarFilePaths = jarFilePaths;
	}

	/**
	 * get jar may have risk thinking same class in different dependency,selected
	 * jar may have risk; Not thinking same class in different dependency,selected
	 * jar is safe
	 * 
	 * @return
	 */
	public boolean isRisk() {
		return !this.isSelected();
	}

	public boolean containsCls(String clsSig) {
		return this.getClsTb().containsKey(clsSig);
	}

	public Element getRchNumEle() {
		Element nodeEle = new DefaultElement("version");
		nodeEle.addAttribute("versionId", getVersion());
		nodeEle.addAttribute("loaded", "" + isSelected());
		for (NodeAdapter node : this.getNodeAdapters()) {
			nodeEle.add(node.getPathElement());
		}
		return nodeEle;
	}

	public Element getClsConflictEle(int num) {
		Element nodeEle = new DefaultElement("jar-" + num);
		nodeEle.addAttribute("id", toString());
		for (NodeAdapter node : this.getNodeAdapters()) {
			nodeEle.add(node.getPathElement());
		}
		return nodeEle;
	}

	public DepJarRiskAna getJarRiskAna(ConflictRiskAna conflictRiskAna) {
		// if (jarRisk == null) {
		// jarRisk = new DepJarCg(this);
		// }
		//
		// return jarRisk;
		return new DepJarRiskAna(this, conflictRiskAna);
	}

	public Set<NodeAdapter> getNodeAdapters() {
		if (nodeAdapters == null)
			nodeAdapters = NodeAdapters.i().getNodeAdapters(this);
		return nodeAdapters;
	}

	public String getAllDepPath() {
		StringBuilder sb = new StringBuilder(toString() + ":");
		for (NodeAdapter node : getNodeAdapters()) {
			sb.append("  [");
			sb.append(node.getWholePath());
			sb.append("]");
		}
		return sb.toString();

	}

	/**
	 * @return the import path of depJar.
	 */
	public String getValidDepPath() {
		StringBuilder sb = new StringBuilder(toString() + ":");
		for (NodeAdapter node : getNodeAdapters()) {
			if (node.isNodeSelected()) {
				sb.append("  [");
				sb.append(node.getWholePath());
				sb.append("]");
			}
		}
		return sb.toString();

	}

	public NodeAdapter getSelectedNode() {
		for (NodeAdapter node : getNodeAdapters()) {
			if (node.isNodeSelected()) {
				return node;
			}
		}
		return null;
	}

	public boolean isProvided() {
		for (NodeAdapter node : getNodeAdapters()) {
			if (node.isNodeSelected()) {
				return "provided".equals(node.getScope());
			}
		}
		return false;
	}

	public boolean isSelected() {
		for (NodeAdapter nodeAdapter : getNodeAdapters()) {
			if (nodeAdapter.isNodeSelected())
				return true;
		}
		return false;
	}

	public Map<String, ClassVO> getClsTb() {
		if (clsTb == null) {
			if (null == this.getJarFilePaths(true)) {
				// no file
				clsTb = new HashMap<String, ClassVO>();
				MavenUtil.i().getLog().warn("can't find jarFile for:" + toString());
			} else {
				clsTb = JarAna.i().deconstruct(this.getJarFilePaths(true));
				if (clsTb.size() == 0) {
					MavenUtil.i().getLog().warn("get empty clsTb for " + toString());
				}
				for (ClassVO clsVO : clsTb.values()) {
					clsVO.setDepJar(this);
				}
			}
		}
		return clsTb;
	}

	public ClassVO getClassVO(String clsSig) {
		return getClsTb().get(clsSig);
	}

	public Set<String> getAllMthd() {
		if (allMthd == null) {
			allMthd = new HashSet<String>();
			for (ClassVO cls : getClsTb().values()) {
				for (MethodVO mthd : cls.getMthds()) {
					allMthd.add(mthd.getMthdSig());
				}
			}
		}
		return allMthd;
	}

	public boolean containsMthd(String mthd) {
		return getAllMthd().contains(mthd);
	}

	public Set<String> getOnlyClses(DepJar otherJar) {
		Set<String> onlyCls = new HashSet<String>();
		Set<String> otherAll = otherJar.getAllCls(true);
		for (String clsSig : getAllCls(true)) {
			if (!otherAll.contains(clsSig)) {
				onlyCls.add(clsSig);
			}
		}
		return onlyCls;
	}

	public Set<String> getOnlyMthds(DepJar otherJar) {
		Set<String> onlyMthds = new HashSet<String>();
		for (String clsSig : getClsTb().keySet()) {
			ClassVO otherCls = otherJar.getClassVO(clsSig);
			if (otherCls != null) {
				ClassVO cls = getClassVO(clsSig);
				for (MethodVO mthd : cls.getMthds()) {
					if (!otherCls.hasMethod(mthd.getMthdSig())) {
						onlyMthds.add(mthd.getMthdSig());
					}
				}
			}
		}
		return onlyMthds;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DepJar) {
			return isSelf((DepJar) obj);

		}
		return false;
	}

	@Override
	public int hashCode() {
		return groupId.hashCode() * 31 * 31 + artifactId.hashCode() * 31 + version.hashCode()
				+ classifier.hashCode() * 31 * 31 * 31;
	}

	@Override
	public String toString() {
		return groupId + ":" + artifactId + ":" + version + ":" + classifier;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getClassifier() {
		return classifier;
	}

	public boolean isSame(String groupId2, String artifactId2, String version2, String classifier2) {
		return groupId.equals(groupId2) && artifactId.equals(artifactId2) && version.equals(version2)
				&& classifier.equals(classifier2);
	}

	public boolean isSelf(DepJar dep) {
		return isSame(dep.getGroupId(), dep.getArtifactId(), dep.getVersion(), dep.getClassifier());
	}

	public boolean isSameLib(DepJar depJar) {
		return getGroupId().equals(depJar.getGroupId()) && getArtifactId().equals(depJar.getArtifactId());
	}

	public void setClsTb(Map<String, ClassVO> clsTb) {
		this.clsTb = clsTb;
	}

	public boolean hasClsTb() {
		return null != this.clsTb;
	}

	public List<String> getInnerMthds(Collection<String> testMthds) {
		Set<String> jarMthds = getAllMthd();
		List<String> innerMthds = new ArrayList<String>();
		for (String mthd : testMthds) {
			if (jarMthds.contains(mthd))
				innerMthds.add(mthd);
		}
		return innerMthds;
	}

	public Set<String> getRiskMthds(Collection<String> testMthds) {
		if (Conf.CNT_RISK_CLASS_METHOD) {
			return this.getOutMthds(testMthds);
		} else {
			Set<String> riskMthds = new HashSet<String>();
			for (String testMthd : testMthds) {
				if (!this.containsMthd(testMthd) && this.containsCls(SootUtil.mthdSig2cls(testMthd))) {
					// System.out.println("jar class size"+this.getClsTb().size()+"jar
					// contains:"+SootUtil.mthdSig2cls(testMthd));
					riskMthds.add(testMthd);
				}
			}
			return riskMthds;
		}
	}

	/**
	 * methods that this jar don't have.
	 * 
	 * @param testMthds
	 * @return
	 */
	private Set<String> getOutMthds(Collection<String> testMthds) {
		Set<String> jarMthds = getAllMthd();
		Set<String> outMthds = new HashSet<String>();
		for (String mthd : testMthds) {
			if (!jarMthds.contains(mthd))
				outMthds.add(mthd);
		}
		return outMthds;
	}

	private NoLimitRefTb refTb;

	public NoLimitRefTb getRefTb() {
		if (refTb == null) {
			refTb = new NoLimitRefTb();
			try {
				ClassPool pool = new ClassPool();
				for (String path : this.getJarFilePaths(true)) {
					pool.appendClassPath(path);
				}
				for (String jarCls : getAllCls(true)) {
					refTb.addByEr(jarCls, pool.get(jarCls).getRefClasses());
				}
			} catch (Exception e) {
				MavenUtil.i().getLog().error("get refedCls error:", e);
			}
		}
		return refTb;
	}

	public Set<String> getAllCls(boolean useTarget) {
		return SootUtil.getJarsClasses(this.getJarFilePaths(useTarget));
	}

	/**
	 * @param useTarget:
	 *            host-class-name can get from source directory(false) or target
	 *            directory(true). using source directory: advantage: get class
	 *            before maven-package disadvantage:classcan't deconstruct by
	 *            soot,miss class that generated.
	 * @return
	 */
	public List<String> getJarFilePaths(boolean useTarget) {
		if (!useTarget) {// use source directory
			// if node is inner project,will return source directory(using source directory
			// can get classes before maven-package)
			if (isHost())
				return MavenUtil.i().getSrcPaths();
		}
		return jarFilePaths;
	}

	public boolean isHost() {
		if (getNodeAdapters().size() == 1) {
			NodeAdapter node = getNodeAdapters().iterator().next();
			if (MavenUtil.i().isInner(node))
				return true;
		}
		return false;
	}

	/**
	 * graph-nodes contains all the jar packaged in jar-with-dependency.
	 * specially,this dep-jar will replace selected-jar.
	 * 
	 * @return
	 */
	public ClsRefGraph getWholeClsRefG() {
		ClsRefGraph graph = new ClsRefGraph();
		try {
			ClassPool pool = new ClassPool();
			Set<String> allSysCls = new HashSet<String>();
			for (DepJar jar : DepJars.i().getAllDepJar()) {
				if (jar == this || (jar.isSelected() && !jar.isSameLib(this))) {
					for (String path : jar.getJarFilePaths(true)) {
						pool.appendClassPath(path);
					}
					for (String jarCls : jar.getAllCls(true)) {
						graph.addNode(jarCls);
						allSysCls.add(jarCls);
					}
				}
			}
			for (String sysCls : allSysCls) {// each er
				for (Object ee : pool.get(sysCls).getRefClasses()) {
					if (!sysCls.equals(ee)) {//don't add relation of self.
						ClsRefNode node = (ClsRefNode) graph.getNode((String) ee);
						if (node != null)
							node.addInCls(sysCls);
					}
				}
			}

		} catch (Exception e) {
			MavenUtil.i().getLog().error("get refedCls error:", e);
		}
		return graph;
	}
}
