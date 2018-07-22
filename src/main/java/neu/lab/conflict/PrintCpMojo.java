package neu.lab.conflict;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import neu.lab.conflict.container.DepJars;
import neu.lab.conflict.util.MavenUtil;

@Mojo(name = "printCp", defaultPhase = LifecyclePhase.VALIDATE)
public class PrintCpMojo extends ConflictMojo{

	@Override
	public void run() {
		for(String cp:DepJars.i().getUsedJarPaths()) {
			System.out.println("argsList.add(\"-process-dir\");");
			System.out.println("argsList.add(\"" + cp.replace("\\", "\\\\") + "\");");
		}
	}
	
}
