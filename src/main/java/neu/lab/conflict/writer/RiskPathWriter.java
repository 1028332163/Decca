package neu.lab.conflict.writer;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import neu.lab.conflict.Conf;
import neu.lab.conflict.container.NodeConflicts;
import neu.lab.conflict.util.MavenUtil;
import neu.lab.conflict.vo.DepJar;
import neu.lab.conflict.vo.Conflict;

public class RiskPathWriter {

	public void writePath(String outPath, boolean append) {
		try {
			Writer fileWriter;
			if (outPath == null) {
				fileWriter = new CharArrayWriter();
			} else {
				fileWriter = new FileWriter(outPath, append);
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("project");
			root.addAttribute("project",
					MavenUtil.i().getProjectGroupId() + ":" + MavenUtil.i().getProjectArtifactId());
			root.addAttribute("projectInfo", MavenUtil.i().getProjectInfo());
			for (Conflict conflict : NodeConflicts.i().getConflicts()) {
				root.add(conflict.getRiskAna().getRiskPathEle());
			}
			xmlWriter.write(document);
			xmlWriter.close();
			if (null == outPath) {// output to console
				MavenUtil.i().getLog().info(fileWriter.toString());
			} else {// output to file
				MavenUtil.i().getLog().info("The result was exported to the file " + outPath);
			}
			fileWriter.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write jar duplicate risk:", e);
		}
	}

	public void writeRefRisk(String outPath, boolean append) {
		try {
			Writer fileWriter;
			if (outPath == null) {
				fileWriter = new CharArrayWriter();
			} else {
				File f = new File(outPath);
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
				fileWriter = new FileWriter(outPath, append);
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("project");
			root.addAttribute("project",
					MavenUtil.i().getProjectGroupId() + ":" + MavenUtil.i().getProjectArtifactId());
			root.addAttribute("projectInfo", MavenUtil.i().getProjectInfo());
			for (Conflict conflict : NodeConflicts.i().getConflicts()) {
				root.add(conflict.getRefRisk().getRiskEle());
			}
			xmlWriter.write(document);
			xmlWriter.close();
			if (null == outPath) {// output to console
				MavenUtil.i().getLog().info(fileWriter.toString());
			} else {// output to file
				MavenUtil.i().getLog().info("The result was exported to the file " + outPath);
			}
			fileWriter.close();
		} catch (Exception e) {
			MavenUtil.i().getLog().error("can't write jar duplicate risk:", e);
		}
	}

}
