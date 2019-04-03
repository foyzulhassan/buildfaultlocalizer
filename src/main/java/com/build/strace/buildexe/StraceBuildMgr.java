package com.build.strace.buildexe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.build.strace.TraceParser;
import com.build.strace.entity.FileInfo;
import com.build.strace.touchbuild.JavaCodeToucher;
import org.apache.commons.io.FilenameUtils;

public class StraceBuildMgr {

	private String buildPath;
	private String straceFolder;
	private String straceLog;
	private String buildCmd;

	private final String straceCmd = "strace -ff -y -ttt -qq -a1 -s 500 -o ";

	public StraceBuildMgr(String buildpath, String stracefolder, String stracelog, String buildcmd) {
		this.buildPath = buildpath;
		this.straceFolder = stracefolder;
		this.straceLog = stracelog;
		this.buildCmd = buildcmd;
		
		
	}

	public void InitBuild() {
		InitLogPath();
		String cmd = this.straceCmd + "./" + straceFolder + "//" + straceLog + " " + buildCmd;
		CmdExecutor cmdexe = new CmdExecutor();
		cmdexe.ExecuteCommand(buildPath, cmd, buildPath);
	}

	public Map<String, List<String>> getCompileJavaDependency(List<String> repofiles, List<String> recentchangedfiles) {
		Map<String, List<String>> compileJavadeps = new HashMap<>();
		String cmd = this.straceCmd + "./" + straceFolder + "//" + straceLog + " " + buildCmd;

		for (String file : recentchangedfiles) {
			InitLogPath();
			if (file.contains(".java")) {

				JavaCodeToucher codetoucher = new JavaCodeToucher();
				codetoucher.touchJavaFile(new File(file));
			}

			CmdExecutor cmdexe = new CmdExecutor();
			cmdexe.ExecuteCommand(buildPath, cmd, buildPath);
			TraceParser parser = new TraceParser();
			List<FileInfo> dependency = parser.parseRawTraces(this.buildPath + "//" + this.straceFolder,
					this.buildPath);

			List<String> filelist = new ArrayList<>();

			for (FileInfo fileinfo : dependency) {
				if (repofiles.contains(fileinfo.getTracefile())) {
					filelist.add(fileinfo.getTracefile());
				}
			}

			compileJavadeps.put(file, filelist);
		}

		return compileJavadeps;
	}

	public Map<String, List<String>> getCompileTestJavaDependency(List<String> repofiles,
			List<String> recentchangedfiles, String testcmd, Map<String, List<String>> compiledeps) {

		// Run full test to find execution order
		InitLogPath();
		String cmd = this.straceCmd + "./" + straceFolder + "//" + straceLog + " " + testcmd;
		CmdExecutor cmdexe = new CmdExecutor();
		cmdexe.ExecuteCommand(buildPath, cmd, buildPath);
		TraceParser parser = new TraceParser();
		List<FileInfo> dependency = parser.parseRawTraces(this.buildPath + "//" + this.straceFolder, this.buildPath);
		List<String> testexecutionorder = new ArrayList<>();

		for (FileInfo fileinfo : dependency) {
			String basename = FilenameUtils.getBaseName(fileinfo.getTracefile());
			if (basename.toUpperCase().contains("TEST")) {
				testexecutionorder.add(basename);
			}
		}

		/// running each test seperately
		for (String testname : testexecutionorder) {
			String specifictestcmd = testcmd + " --tests *" + testname + ".*;";
			String testspecmd = this.straceCmd + "./" + straceFolder + "//" + straceLog + " " + specifictestcmd;
			InitLogPath();
			cmdexe.ExecuteCommand(buildPath, testspecmd, buildPath);
			List<FileInfo> testdependency = parser.parseRawTraces(this.buildPath + "//" + this.straceFolder,
					this.buildPath);

			for (FileInfo fileinfodep : testdependency) {
				String basename = FilenameUtils.getBaseName(fileinfodep.getTracefile());

				for (String compdep : compiledeps.keySet()) {
					if (IsInDependency(compdep, compiledeps.get(compdep), basename)) {
						String javafilename = getJavaFileFromClassName(repofiles, basename);

						if (javafilename != null && javafilename.length() > 0) {
							compiledeps.get(compdep).add(javafilename);
						}
					}
				}

			}

		}

		return compiledeps;
	}

	boolean deleteDirectory(File directoryToBeDeleted) {
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return directoryToBeDeleted.delete();
	}

	private void InitLogPath() {
		String logfolder = buildPath + "//" + straceFolder;
		File path = new File(logfolder);
		if (path.exists()) {
			deleteDirectory(path);
			path.mkdirs();
		} else {
			path.mkdirs();
		}
	}

	private boolean IsInDependency(String keyfile, List<String> deps, String filetochek) {
		if (keyfile.contains(filetochek))
			return true;
		for (String dep : deps) {
			if (dep.contains(filetochek)) {
				return true;
			}
		}

		return false;
	}

	private String getJavaFileFromClassName(List<String> repofiles, String classname) {
		String filename = "";

		for (String strfile : repofiles) {
			if (strfile.contains(classname)) {
				filename = strfile;
				return filename;
			}
		}

		return filename;
	}
}
