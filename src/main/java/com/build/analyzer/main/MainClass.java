package com.build.analyzer.main;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.build.analyzer.dtaccess.DBActionExecutor;
import com.build.analyzer.dtaccess.DBActionExecutorChangeData;
import com.build.analyzer.dtaccess.SessionGenerator;
import com.build.analyzer.dtgen.CommitChangeExtractor;
import com.build.analyzer.dtgen.DataGenerationMngr;
import com.build.analyzer.dtgen.SimGenerationMngr;
import com.build.analyzer.entity.Gradlebuildfixdata;
import com.build.commitanalyzer.CommitAnalyzer;
import com.build.revertanalyzer.ReverAnalyzer;
import com.github.gumtreediff.actions.model.Action;

public class MainClass {

	public static void main(String[] args) {
		// // TODO Auto-generated method stub
		// DBActionExecutor dbobj = new DBActionExecutor();
		// long rowcount = dbobj.getTotalNumberofRows();
		//
		// System.out.println(rowcount);

		System.out.println("Enter your action:");

		System.out
				.println("1->Data Generation"
						+ "\n2->Commit Change Analysis and Import Full Log"
						+ "\n3->Differential Log Analysis and Import"
						+ "\n4->Perform Fault Localization on Full Log"
						+ "\n5->Perform Fault Localization on Differenttial Log"
						+ "\n6->Perform Fault Localization on Differenttial Log + File Change");

		// create an object that reads integers:
		Scanner cin = new Scanner(System.in);

		System.out.println("Enter an integer: ");
		int inputid = cin.nextInt();
		//int inputid=5;

		if (inputid == 1) {
			dataFiltering();
		} else if (inputid == 2) {
			commitChangeAnalysis();
		} else if (inputid == 3) {
			genDifferentialBuildLog();
		} else if (inputid == 4) {
			generateSimilarity();
			generateSimilarityDifferentialLog();
			genSimDifferentialLogOnChange();
		} else if (inputid == 5) {
			generateSimilarityDifferentialLog();
		} else if (inputid == 6) {
			genSimDifferentialLogOnChange();
		}

		else {
			CommitChangeExtractor obj = new CommitChangeExtractor();
			obj.testCommit();

			System.out.println("Wrong Function Id Entered");
		}

		cleanupResource();
	}

	private static void dataFiltering() {
		// This part is for data filtering to extract required data
		DataGenerationMngr dtgen = new DataGenerationMngr();
		dtgen.genenrateData();

		// Perform Java and Gradle code diff in between commit and save the diff
		// information in database

	}

	private static void commitChangeAnalysis() {

		CommitChangeExtractor cmtext = new CommitChangeExtractor();

		try {
			cmtext.updateCommitChange();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void genDifferentialBuildLog() {

		CommitChangeExtractor cmtext = new CommitChangeExtractor();

		try {
			cmtext.updateDifferentialCommitChange();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void generateSimilarity() {
		SimGenerationMngr simgen = new SimGenerationMngr();

		try {
			simgen.simAnalyzerFullLog();
			//simgen.simAnalyzerFilteredLog();
			// simgen.simtesting();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void generateSimilarityDifferentialLog() {
		SimGenerationMngr simgen = new SimGenerationMngr();

		try {
			simgen.simAnalyzerDifferemtialLog();			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void genSimDifferentialLogOnChange() {
		SimGenerationMngr simgen = new SimGenerationMngr();

		try {
			simgen.simAnalyzerDifferemtialLogWithChange();	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void cleanupResource() {
		SessionGenerator.closeFactory();
	}

	// private void test()
	// {
	//
	// DBActionExecutorChangeData dbexec = new DBActionExecutorChangeData();
	//
	// ReverAnalyzer revertcheker = new ReverAnalyzer();
	//
	// List<Gradlebuildfixdata> projects = dbexec.getRows();
	//
	// for (int index = 0; index < projects.size(); index++) {
	// Gradlebuildfixdata proj = projects.get(index);
	//
	// String commit = proj.getGitCommit();
	// String failintrocommit = proj.getGitFailintroCommit();
	//
	// String lastfailcommit = proj.getGitLastfailCommit();
	// String passcommit = proj.getGitFixCommit();
	//
	// String project = proj.getGhProjectName();
	// project = project.replace('/', '@');
	// // project="D:\\test\\appsly-android-rest";
	// CommitAnalyzer cmtanalyzer = null;
	//
	// try {
	// cmtanalyzer = new CommitAnalyzer("test", project);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// // Get the changes in between Pass to Fail Transition commit
	// Map<String, List<Action>> failchangemap =
	// cmtanalyzer.extractChangeInBetweenCommit(commit, failintrocommit);
	// }
	// }
}
