package neu.lab.conflict.container;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.NodeAdapter;

public class DepJars {
	private static DepJars instance;

	public static DepJars i() {
		return instance;
	}

	public static void init(NodeAdapters nodeAdapters) throws Exception {
		if (instance == null) {
			instance = new DepJars(nodeAdapters);
		}
	}

	private Set<DepJar> container;
	private DepJar hostDepJar;

	private DepJars(NodeAdapters nodeAdapters) throws Exception {
		container = new HashSet<DepJar>();
		for (NodeAdapter nodeAdapter : nodeAdapters.getAllNodeAdapter()) {
			container.add(new DepJar(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId(), nodeAdapter.getVersion(),
					nodeAdapter.getClassifier(), nodeAdapter.getFilePath()));
		}
		int systemSize = 0;
		long systemFileSize = 0;
		for (DepJar depJar : getAllDepJar()) {
			if (depJar.isSelected()) {
				systemSize++;
				for (String filePath : depJar.getJarFilePaths(true)) {
					systemFileSize = systemFileSize + new File(filePath).length();
				}
			}
		}
		MavenUtil.i().getLog().warn("tree size:" + container.size() + ", used size:" + systemSize + ", usedFile size"
				+ systemFileSize / 1000);
		if (container.size() > 50) {
			throw new Exception("too large project.");
		}

	}

	public Set<DepJar> getUsedDepJars() {
		Set<DepJar> usedDepJars = new HashSet<DepJar>();
		for (DepJar depJar : container) {
			if (depJar.isSelected()) {
				usedDepJars.add(depJar);
			}
		}
		return usedDepJars;
	}

	public DepJar getHostDepJar() {
		if (hostDepJar == null) {

			for (DepJar depJar : container) {
				if (depJar.isHost()) {
					if (hostDepJar != null) {
						MavenUtil.i().getLog().warn("multiple depjar for host ");
					}
					hostDepJar = depJar;
				}
			}
		}
		return hostDepJar;
	}

	public DepJar getDep(String groupId, String artifactId, String version, String classifier) {
		for (DepJar dep : container) {
			if (dep.isSame(groupId, artifactId, version, classifier)) {
				return dep;
			}
		}
		MavenUtil.i().getLog().warn("cant find dep:" + groupId + ":" + artifactId + ":" + version + ":" + classifier);
		return null;
	}

	public Set<DepJar> getAllDepJar() {
		return container;
	}

	public DepJar getDep(NodeAdapter nodeAdapter) {
		return getDep(nodeAdapter.getGroupId(), nodeAdapter.getArtifactId(), nodeAdapter.getVersion(),
				nodeAdapter.getClassifier());
	}

}
