package com.build.strace.dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.build.analyzer.config.Config;
import com.build.commitanalyzer.CommitAnalyzer;
import com.build.strace.buildexe.StraceBuildMgr;
import com.build.strace.entity.FileScore;

public class DependencyGenerator {

	public DependencyGenerator() {
		File analysisdir = new File(Config.dynamicBuildDir);

		if (!analysisdir.exists()) {
			System.out.println("Setup Proper Analysis Dependency Path. Check Config file for Directory Setup");
			System.exit(0);
		}

		File folder = new File(Config.dynamicBuildDir);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				listOfFiles[i].delete();
			} else if (listOfFiles[i].isDirectory()) {
				try {
					FileUtils.deleteDirectory(listOfFiles[i]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void BuildDependency(String strsrcdir, String commitid) {
		File srcdir = new File(strsrcdir);
		File desdir = new File(Config.dynamicBuildDir);

		try {
			FileUtils.copyDirectory(srcdir, desdir);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File folder = new File(Config.dynamicBuildDir);
		File[] listOfFiles = folder.listFiles();

		String repodir = "";
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isDirectory()) {
				repodir = listOfFiles[i].toString();
				break;
			}
		}
		
		///repodir=repodir+"//";
		
		try {
			CommitAnalyzer commitanalyzer=new CommitAnalyzer("test",repodir,commitid);
			commitanalyzer.gitCheckOut(commitid, "AnaLysis");			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		List<String> files=getRepoFiles(repodir);
		
		StraceBuildMgr stracebuildmgr=new StraceBuildMgr(repodir,"teststrace","tracelog","./gradlew build -x test");
		stracebuildmgr.InitBuild();
		Map<String,Boolean> passedlines=stracebuildmgr.getPassedLines();
		Map<String, Boolean> failedlines=stracebuildmgr.getFailedLines();
		
		List<String> recentchangedfiles=new ArrayList<>();
		recentchangedfiles.add("/home/foyzulhassan/Research/Strace_Implementation/builddir/gradle-build-scan-quickstart/src/main/java/example/UtilTwo.java");
		
		//This class is responsible for holding scores of files
		FileScore filescore=new FileScore(files);
		
		Map<String, List<String>> compiledef=stracebuildmgr.getCompileJavaDependency(files, recentchangedfiles,filescore);
		
		Map<String, List<String>> compiletestdef=stracebuildmgr.getCompileTestJavaDependency(files, recentchangedfiles, "./gradlew test", compiledef,filescore);
		
	}
	
	private List<String> getRepoFiles(String repodir)
	{
		List<String> strfiles=new ArrayList<>();
		List<File> files = (List<File>) FileUtils.listFiles(new File(repodir), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		
		for(File strfile:files)
		{
			strfiles.add(strfile.toString());
		}
		
		return strfiles;
	}
}