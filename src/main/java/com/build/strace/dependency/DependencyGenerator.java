package com.build.strace.dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.build.analyzer.config.Config;
import com.build.commitanalyzer.CommitAnalyzer;
import com.build.strace.buildexe.StraceBuildMgr;
import com.build.strace.entity.FileScore;

public class DependencyGenerator {
	private Map<String,Boolean> passedlines;
	public Map<String, Boolean> getPassedlines() {
		return passedlines;
	}

	public Map<String, Boolean> getFailedlines() {
		return failedlines;
	}

	private Map<String, Boolean> failedlines;	

	public DependencyGenerator() {
		File analysisdir = new File(Config.dynamicBuildDir);
		passedlines=new HashMap<>();
		failedlines=new HashMap<>();

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

	public FileScore getFileSuspicionScore(String strsrcdir, String projname, String commitid,List<String> recentchangedfiles) {
		///Copy to Local Build Directory
		File srcdir = new File(strsrcdir);
		File desdir = new File(Config.dynamicBuildDir+projname);
		try {
			FileUtils.copyDirectory(srcdir, desdir);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		File folder = new File(Config.dynamicBuildDir);
//		File[] listOfFiles = folder.listFiles();
//
		String repodir = "";
//		for (int i = 0; i < listOfFiles.length; i++) {
//			if (listOfFiles[i].isDirectory()) {
//				repodir = listOfFiles[i].toString();
//				break;
//			}
//		}
		repodir=Config.dynamicBuildDir+"/"+projname;
		//copy to local build dir completed
		
		//checkout specific version of project to build 
		//repodir=repodir+"/.git";		
		try {
			CommitAnalyzer commitanalyzer=new CommitAnalyzer("test",repodir,commitid);
			commitanalyzer.gitCheckOut(commitid, "AnaLysis");			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//complete repo checkout
		
		//List of files in repo
		List<String> files=getRepoFiles(repodir);
		
		//Build command
		StraceBuildMgr stracebuildmgr=new StraceBuildMgr(repodir,"teststrace","tracelog","./gradlew build");
		stracebuildmgr.InitBuild();
		
		this.passedlines=stracebuildmgr.getPassedLines();
		this.failedlines=stracebuildmgr.getFailedLines();	
		
		//This class is responsible for holding scores of files
		FileScore filescore=new FileScore(files);
		
		Map<String, List<String>> compiledef=stracebuildmgr.getCompileJavaDependency(files, recentchangedfiles,filescore,"./gradlew build -x test");
		
		Map<String, List<String>> compiletestdef=stracebuildmgr.getCompileTestJavaDependency(files, recentchangedfiles, "./gradlew test", compiledef,filescore);
		
		return filescore;
		
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