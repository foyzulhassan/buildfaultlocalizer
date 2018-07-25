package com.build.builddependency;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.build.analyzer.config.Config;
import com.build.analyzer.diff.gradle.GradlePatchGenMngr;
import com.build.analyzer.entity.Gradlebuildfixdata;
import com.build.commitanalyzer.CommitAnalyzer;
import com.build.commitanalyzer.CommitAnalyzingUtils;
import com.build.docsim.CosineDocumentSimilarity;
import com.github.gumtreediff.tree.TreeContext;

import edu.utsa.buildlogparser.util.TextFileReaderWriter;
import edu.utsa.gradlediff.GradleASTParseMngr;
import edu.utsa.gradlediff.StringMenupulator;

public class BuildDependencyGenerator {

	
	
	public void generateBuildDependency() {

		StringMenupulator strmenu = new StringMenupulator();

		// File f1 = new File(
		// "D:\\Researh_Works\\ASE_2018\\dependency_analysis\\Sample_Project\\spockframework\\spock\\spock-guice\\guice.gradle");

		File f1 = new File("D:\\Researh_Works\\ASE_2018\\dependency_analysis\\Sample_Project\\spockframework\\test\\spock\\spock-specs\\build1.gradle");

		List<String> strlist = TextFileReaderWriter.GetFileContentByLine(f1.toString());
		for (int index = 0; index < strlist.size(); index++) {
			String str = strlist.get(index);

			str = strmenu.getMarkedString(str);

			strlist.set(index, str);
		}

		TreeContext tsrc =GradleASTParseMngr.getTreeContext(strlist);

		System.out.println(tsrc.toString());

	}

//	public List<String> getDependentProjectList(Gradlebuildfixdata proj, String ID, long rowid) {
//
//		List<String> depsubprojs = new ArrayList<String>();
//		String lastprojexecuted = "";
//
//		List<String> subprojs = generateSubProjectList(proj, ID, rowid);
//		
//		System.out.println("List of Subprojects:=>"+subprojs);
//
//		if (subprojs != null && subprojs.size() > 0)
//		{
//			lastprojexecuted = getLastProjectExecuted(proj, subprojs);			
//		}
//
//		System.out.println("Last Executed:=>"+lastprojexecuted);
//		
//		Map<String,List<String>> projconnection=generateSubProjectConnectivity(proj,ID,rowid);
//		
//		depsubprojs=getFailSubProjDependencies(lastprojexecuted,projconnection);
//				
//		return depsubprojs;
//	}
//
//	public String getLastProjectExecuted(Gradlebuildfixdata proj, List<String> subprojs) {
//		String executedproj = null;
//
//		if (subprojs != null && subprojs.size() > 0) {
//
//			String largelog = proj.getBlLargelog();
//
//			List<String> buildlines = new ArrayList<String>(Arrays.asList(largelog.split("\n")));
//
//			int lineindex = buildlines.size() - 1;
//			boolean match = false;
//
//			while (lineindex >= 0) {
//				String strline = buildlines.get(lineindex);
//
//				int subprojindex = 0;
//
//				while (subprojindex < subprojs.size()) {
//					String subprojstr = subprojs.get(subprojindex);
//
//					if (strline.contains(subprojstr + ":")) {
//						executedproj = subprojstr;
//						match = true;
//					}
//
//					subprojindex++;
//				}
//
//				if (match)
//					break;
//
//				lineindex--;
//			}
//		}
//
//		return executedproj;
//
//	}
//
//	public List<String> generateSubProjectList(Gradlebuildfixdata proj, String ID, long rowid) {
//
//		String project = proj.getGhProjectName();
//		project = project.replace('/', '@');
//		CommitAnalyzer cmtanalyzer = null;
//		TreeWalk treeWalk = null;
//		Repository repository = null;
//
//		File gradlefile = null;
//		CommitAnalyzingUtils commitAnalyzingUtils = new CommitAnalyzingUtils();
//		StringMenupulator strmenu = new StringMenupulator();
//
//		List<String> subprojlist = new ArrayList<String>();
//		
//		
//
//		try {
//			cmtanalyzer = new CommitAnalyzer("test", project);
//
//			treeWalk = cmtanalyzer.getCommitTree(proj.getGitLastfailCommit(), proj.getF2row(), proj);
//			repository = cmtanalyzer.getRepository();
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		int index = 0;
//
//		try {
//			while (treeWalk.next()) {
//				System.out.println("Here we are:=>"+treeWalk.getPathString());
//				if (treeWalk.isSubtree()) {
//					treeWalk.enterSubtree();
//				} else if (treeWalk.getPathString().contains(".gradle")) {
//					ObjectId objectId = treeWalk.getObjectId(0);
//					ObjectLoader loader = repository.open(objectId);
//					
//					String testabc=treeWalk.getPathString();
//
//					byte[] butestr = loader.getBytes();
//
//					String str = new String(butestr);
//
//					String sourcefile = Config.workDir + Config.tempFolder + "buildscriptdep" + index + ".txt";
//					index++;
//
//					gradlefile = commitAnalyzingUtils.writeContentInFile(sourcefile, str);
//
//					List<String> strlist = TextFileReaderWriter.GetFileContentByLine(gradlefile.toString());
//
//					for (int lineindex = 0; lineindex < strlist.size(); lineindex++) {
//						String strline = strlist.get(lineindex);
//						strline = strmenu.getMarkedString(strline);
//						strlist.set(index, strline);
//					}
//
//					List<String> subprjs = GradlePatchGenMngr.getSubProjList(strlist);
//
//					subprojlist.addAll(subprjs);
//					
//					
//					if(gradlefile.exists())
//						gradlefile.delete();
//				}
//
//			}			
//
//			treeWalk.reset();
//		} catch (Exception ex) {
//			System.out.print(ex.getMessage());
//		}
//		
//		
//
//		subprojlist = new ArrayList<>(new HashSet<>(subprojlist));
//
//		return subprojlist;
//	}
//	
//	public Map<String,List<String>> generateSubProjectConnectivity(Gradlebuildfixdata proj, String ID, long rowid) {
//
//		String project = proj.getGhProjectName();
//		project = project.replace('/', '@');
//		CommitAnalyzer cmtanalyzer = null;
//		TreeWalk treeWalk = null;
//		Repository repository = null;
//
//		File gradlefile = null;
//		CommitAnalyzingUtils commitAnalyzingUtils = new CommitAnalyzingUtils();
//		StringMenupulator strmenu = new StringMenupulator();
//
//		List<String> subprojlist = new ArrayList<String>();
//		
//		Map<String,List<String>> projectDependencyies= new HashMap<String, List<String>>();
//		
//		try {
//			cmtanalyzer = new CommitAnalyzer("test", project);
//
//			treeWalk = cmtanalyzer.getCommitTree(proj.getGitLastfailCommit(), proj.getF2row(), proj);
//			repository = cmtanalyzer.getRepository();
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		int index = 0;
//
//		try {
//			while (treeWalk.next()) {
//
//				if (treeWalk.isSubtree()) {
//					treeWalk.enterSubtree();
//				} else if (treeWalk.getPathString().contains(".gradle")) {
//					ObjectId objectId = treeWalk.getObjectId(0);
//					ObjectLoader loader = repository.open(objectId);
//
//					byte[] butestr = loader.getBytes();
//
//					String str = new String(butestr);
//
//					String sourcefile = Config.workDir + Config.tempFolder + "buildscriptdep" + index + ".txt";
//					index++;
//
//					gradlefile = commitAnalyzingUtils.writeContentInFile(sourcefile, str);
//
//					List<String> strlist = TextFileReaderWriter.GetFileContentByLine(gradlefile.toString());
//
//					for (int lineindex = 0; lineindex < strlist.size(); lineindex++) {
//						String strline = strlist.get(lineindex);
//						strline = strmenu.getMarkedString(strline);
//						strlist.set(index, strline);
//					}
//
//					String gitfile=treeWalk.getPathString();
//					File f = new File(gitfile);
//					String parent=null;
//					
//					if(f.getParentFile()!=null)
//					{
//						parent=f.getParentFile().getName();
//					}
//					else
//					{
//						parent="root";
//					}
//
//					
//					Map<String,List<String>>  projconnection = GradlePatchGenMngr.getSubProjConnectivity(strlist,parent);
//
//					if(projconnection!=null && projconnection.keySet().size()>0)
//					{
//						projectDependencyies=mergeDependencyMap(projectDependencyies,projconnection);
//					}
//					
//					
//					if(gradlefile.exists())
//						gradlefile.delete();
//				}
//
//			}
//
//			treeWalk.reset();
//
//		} catch (Exception ex) {
//			System.out.print(ex.getMessage());
//		}
//
//		subprojlist = new ArrayList<>(new HashSet<>(subprojlist));
//
//		return projectDependencyies;
//	}
//	
//	public Map<String, List<String>> mergeDependencyMap(Map<String, List<String>> projectDependencyies,Map<String, List<String>> depdencymap) {
//
//		for (String key : depdencymap.keySet()) {
//			if (projectDependencyies.containsKey(key)) {
//				List<String> deps = projectDependencyies.get(key);
//
//				deps.addAll(depdencymap.get(key));
//
//				List<String> deDupStringList = new ArrayList<>(new HashSet<>(deps));
//
//				projectDependencyies.put(key, deDupStringList);
//			} else {
//				projectDependencyies.put(key, depdencymap.get(key));
//			}
//
//		}
//
//		return projectDependencyies;
//	}
//	
//	//BFS implementation to find Gradle Fail project dependency
//	public List<String> getFailSubProjDependencies(String failedsubproj, Map<String, List<String>> projconnection) {
//		List<String> failsubdependencies = new ArrayList<String>();
//		Queue<String> queue = new LinkedList<>();
//
//		// A HashMap to keep track already visited subproject;
//		Map<String, Boolean> visitedstatus = new HashMap<String, Boolean>();
//		for (String key : projconnection.keySet()) {
//			visitedstatus.put(key, false);
//		}
//
//		// initilize with root node that is failed projects
//		if (visitedstatus.containsKey(failedsubproj)) {
//			visitedstatus.put(failedsubproj, true);
//		}
//
//		while (!queue.isEmpty()) {
//			// Dequeue a vertex from queue and print it
//			String node = queue.peek();
//			failsubdependencies.add(node);
//			queue.remove();
//
//			// Get all adjacent vertices of the dequeued
//			// vertex s. If a adjacent has not been visited,
//			// then mark it visited and enqueue it
//			List<String> connections = projconnection.get(node);
//
//			for (String subnode : connections) {
//				if (visitedstatus.get(subnode) == false) {
//					visitedstatus.put(subnode, true);
//					queue.add(subnode);
//				}
//			}
//		}
//
//		return failsubdependencies;
//
//	}

}
