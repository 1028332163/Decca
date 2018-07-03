package neu.lab.conflict;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

import neu.lab.conflict.container.AllCls;
import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.container.NodeAdapters;
import neu.lab.conflict.graph.Dog;
import neu.lab.conflict.container.Conflicts;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;

public abstract class ConflictMojo extends AbstractMojo {
	@Parameter(defaultValue = "${session}", readonly = true, required = true)
	public MavenSession session;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	public MavenProject project;

	@Parameter(defaultValue = "${reactorProjects}", readonly = true, required = true)
	public List<MavenProject> reactorProjects;

	@Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
	public List<ArtifactRepository> remoteRepositories;

	@Parameter(defaultValue = "${localRepository}", readonly = true)
	public ArtifactRepository localRepository;

	@Component
	public DependencyTreeBuilder dependencyTreeBuilder;

	@Parameter(defaultValue = "${project.build.directory}", required = true)
	public File buildDir;

	@Component
	public ArtifactFactory factory;

	@Component
	public ArtifactHandlerManager artifactHandlerManager;
	@Component
	public ArtifactResolver resolver;
	DependencyNode root;

	@Parameter(defaultValue = "${project.compileSourceRoots}", readonly = true, required = true)
	public List<String> compileSourceRoots;

	@Parameter(property = "ignoreTestScope", defaultValue = "false")
	public boolean ignoreTestScope;

	@Parameter(property = "ignoreProvidedScope", defaultValue = "false")
	public boolean ignoreProvidedScope;

	@Parameter(property = "ignoreRuntimeScope", defaultValue = "false")
	public boolean ignoreRuntimeScope;

	@Parameter(property = "append", defaultValue = "false")
	public boolean append;

	protected void initGlobalVar() throws Exception {
		MavenUtil.i().setMojo(this);
		NodeAdapters.init(root);
		DepJars.init(NodeAdapters.i());// occur jar in tree

		validateSysSize();

		AllCls.init(DepJars.i());
		Conflicts.init(NodeAdapters.i());// version conflict in tree
	}
	
	private void validateSysSize() throws Exception{
		int systemSize = 0;
		long systemFileSize = 0;
		for (DepJar depJar : DepJars.i().getAllDepJar()) {
			if (depJar.isSelected()) {
				systemSize++;
				for (String filePath : depJar.getJarFilePaths(true)) {
					systemFileSize = systemFileSize + new File(filePath).length();
				}
			}
		}

		MavenUtil.i().getLog().warn("tree size:" + DepJars.i().getAllDepJar().size() + ", used size:" + systemSize
				+ ", usedFile size" + systemFileSize / 1000);

		if (DepJars.i().getAllDepJar().size() > 50) {
			throw new Exception("too large project.");
		}
	}

	public void execute() throws MojoExecutionException {
		this.getLog().info("method detect start:");
		if ("jar".equals(project.getPackaging()) || "war".equals(project.getPackaging())) {
			try {
				// project.
				root = dependencyTreeBuilder.buildDependencyTree(project, localRepository, null);
			} catch (DependencyTreeBuilderException e) {
				throw new MojoExecutionException(e.getMessage());
			}
			try {
				initGlobalVar();
			} catch (Exception e) {
				MavenUtil.i().getLog().error(e);
				throw new MojoExecutionException("too large project!");
			}
			run();
			this.getLog().info("dog-run-time:" + Dog.runtime);
		} else {
			this.getLog()
					.info("this project fail because package type is neither jar nor war:" + project.getGroupId() + ":"
							+ project.getArtifactId() + ":" + project.getVersion() + "@"
							+ project.getFile().getAbsolutePath());
		}

		this.getLog().debug("method detect end");

	}

	public abstract void run();
}
