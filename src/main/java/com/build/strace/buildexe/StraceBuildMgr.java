package com.build.strace.buildexe;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.build.strace.TraceParser;
import com.build.strace.entity.FileInfo;
import com.build.strace.entity.FileScore;
import com.build.strace.touchbuild.JavaCodeToucher;
import org.apache.commons.io.FilenameUtils;

public class StraceBuildMgr {

	private String buildPath;
	private String straceFolder;
	private String straceLog;
	private String buildCmd;
	
	private Map<String, Boolean> passedLines;
	public Map<String, Boolean> getPassedLines() {
		return passedLines;
	}

	public void setPassedLines(Map<String, Boolean> passedLines) {
		this.passedLines = passedLines;
	}

	public Map<String, Boolean> getFailedLines() {
		return failedLines;
	}

	public void setFailedLines(Map<String, Boolean> failedLines) {
		this.failedLines = failedLines;
	}

	private Map<String, Boolean> failedLines;

	private final String straceCmd = "strace -ff -y -ttt -qq -a1 -s 500 -o ";

	public StraceBuildMgr(String buildpath, String stracefolder, String stracelog, String buildcmd) {
		this.buildPath = buildpath;
		this.straceFolder = stracefolder;
		this.straceLog = stracelog;
		this.buildCmd = buildcmd;
		
		
	}

	public void InitBuild() {
		InitLogPath();
		String cmd = this.straceCmd + "./" + straceFolder + "//" + straceLog + " " + buildCmd+">/home/foyzulhassan/Research/Strace_Implementation/builddir/gradle-build-scan-quickstart/log.txt";
		CmdExecutor cmdexe = new CmdExecutor();
		cmdexe.ExecuteCommand(buildPath+"//", "chmod 777 gradlew", buildPath+"//");
		
		cmdexe = new CmdExecutor();
		//cmdexe.ExecuteCommand(buildPath+"//", cmd, buildPath+"//");
		cmdexe.ExecuteCommand(buildPath+"//", buildCmd, buildPath+"//");
		passedLines=cmdexe.getPassedLines();
		failedLines=cmdexe.getFailedLines();
	}

	public Map<String, List<String>> getCompileJavaDependency(List<String> repofiles, List<String> recentchangedfiles,FileScore filescore) {
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
			TraceParser parser = new TraceParser(passedLines,failedLines);
			List<FileInfo> dependency = parser.parseRawTraces(this.buildPath + "//" + this.straceFolder,
					this.buildPath,repofiles,filescore,true);

			List<String> filelist = new ArrayList<>();


			for (FileInfo fileinfo : dependency) {
				String str=fileinfo.getTracefile();
				if (repofiles.contains(fileinfo.getTracefile())) {
					filelist.add(fileinfo.getTracefile());
				}
			}

			compileJavadeps.put(file, filelist);
		}

		return compileJavadeps;
	}

	public Map<String, List<String>> getCompileTestJavaDependency(List<String> repofiles,
			List<String> recentchangedfiles, String testcmd, Map<String, List<String>> compiledeps,FileScore filescore) {

		// Run full test to find execution order
		InitLogPath();
		String cmd = this.straceCmd + "./" + straceFolder + "//" + straceLog + " " + testcmd;
		CmdExecutor cmdexe = new CmdExecutor();
		
		
		//
		System.out.println("Test Order\n\n\n\n\n\n\n");
		
		cmdexe.ExecuteCommand(buildPath, cmd, buildPath);
		TraceParser parser = new TraceParser(passedLines,failedLines);
		List<FileInfo> dependency = parser.parseRawTraces(this.buildPath + "//" + this.straceFolder, this.buildPath,repofiles,filescore,false);
		List<String> testexecutionorder = new ArrayList<>();
		Map<String,String> testfilemap=new HashMap<>();

		System.out.println("Test Information Order\n\n\n\n\n\n\n");
		for (FileInfo fileinfo : dependency) {
			String basename = FilenameUtils.getBaseName(fileinfo.getTracefile());
			String extension=FilenameUtils.getExtension(fileinfo.getTracefile());
			if (basename.toUpperCase().contains("TEST") && extension.equals("class")) {
				System.out.println(fileinfo.getTracefile());
				if(!testexecutionorder.contains(basename))
				{
						testexecutionorder.add(basename);
						String strfile=getJavaFileFromClassName(repofiles,basename,extension);
						testfilemap.put(basename, strfile);
				}
			}
		}

		/// running each test seperately
		for (String testname : testexecutionorder) {
			String specifictestcmd = testcmd + " --tests *" +"*."+testname + ".*;";
			String testspecmd = this.straceCmd + "./" + straceFolder + "//" + straceLog + " " + specifictestcmd;
			InitLogPath();
			String strtestsrc=testfilemap.get(testname);
			
			if (strtestsrc.contains(".java")) {

				JavaCodeToucher codetoucher = new JavaCodeToucher();
				codetoucher.touchJavaFile(new File(strtestsrc));
			}
			
			cmdexe.ExecuteCommand(buildPath, testspecmd, buildPath);
			List<FileInfo> testdependency = parser.parseRawTraces(this.buildPath + "//" + this.straceFolder,
					this.buildPath,repofiles,filescore,true);

			System.out.println("test");
			for (FileInfo fileinfodep : testdependency) {
				String basename = FilenameUtils.getBaseName(fileinfodep.getTracefile());
				String extension=FilenameUtils.getExtension(fileinfodep.getTracefile());

				for (String compdep : compiledeps.keySet()) {
					if (IsInDependency(compdep, compiledeps.get(compdep), basename)) {
						if(extension.equals("class"))
						{
							System.out.println("test");
						}
						String javafilename = getJavaFileFromClassName(repofiles, basename,extension);

						if (javafilename != null && javafilename.length() > 0) {
							if(!compiledeps.get(compdep).contains(javafilename))
							{
									compiledeps.get(compdep).add(javafilename);
							}
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

	private String getJavaFileFromClassName(List<String> repofiles, String classname,String extension) {
		String filename = "";

		for (String strfile : repofiles) {
			if (strfile.contains(classname) && extension.equals("class")) {
				filename = strfile;
				return filename;
			}
		}

		return filename;
	}
}
