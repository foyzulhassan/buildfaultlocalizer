package com.build.analyzer.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.build.ASTAnalyzer.JavaASTParser;
import com.build.analyzer.config.Config;
import com.build.analyzer.dtaccess.DBActionExecutor;
import com.build.analyzer.dtaccess.DBActionExecutorChangeData;
import com.build.analyzer.dtaccess.SessionGenerator;
import com.build.analyzer.dtgen.CommitChangeExtractor;
import com.build.analyzer.dtgen.DataGenerationMngr;
import com.build.analyzer.dtgen.SimGenerationMngr;
import com.build.analyzer.entity.Gradlebuildfixdata;
import com.build.analyzer.result.ResultGenMngr;
import com.build.builddependency.BuildDependencyGenerator;
import com.build.commitanalyzer.CommitAnalyzer;
import com.build.docsim.CosineDocumentSimilarity;
import com.build.evaluation.EvaluationMgr;
import com.build.evaluation.FixWithBuildFailChange;
import com.build.failanalyzer.GenerateTestFailType;
import com.build.gradlescript.GradleBuildScriptASTSelector;
import com.build.java.ast.selection.JavaASTSelector;
import com.build.logfilter.LogFailPartSimGenMgr;
import com.build.param.tunning.DiffFilterRecentDependencyTunning;
import com.build.param.tunning.DiffFilterRecentTunning;
import com.build.param.tunning.DiffFilterTunning;
import com.build.param.tunning.ParformanceAnalysis;
import com.build.param.tunning.TunningDTSetter;
import com.build.revertanalyzer.ReverAnalyzer;
import com.build.util.TextFileReaderWriter;
import com.github.gumtreediff.actions.model.Action;

public class MainClass {

	public static void main(String[] args) {
		// // TODO Auto-generated method stub
		// DBActionExecutor dbobj = new DBActionExecutor();
		// long rowcount = dbobj.getTotalNumberofRows();
		//
		// System.out.println(rowcount);

		System.out.println("Enter your action:");

		System.out.println("1->Data Generation" + "\n2->Commit Change Analysis and Import Full Log"
				+ "\n3->Differential Log Analysis and Import" + "\n4->Perform Fault Localization on Full Log"
				+ "\n5->Perform Fault Localization on Differenttial Log"
				+ "\n6->Perform Fault Localization on Differenttial Log + File Change" + "\n7->For Logging"
				+ "\n8->Log Fail Part Line Similarity Generator"
				+ "\n9->Perform Fault Localization on Fail Part Log with Similarity Limit"
				+ "\n10->Build Dependency Analysis" + "\n11->Log Analysis" + "\n12->ASTParser Checking"
				+ "\n13->Analyze Result" + "\n14->Generate Similarity on Build Dependency Graph"
				+ "\n21->Param Tunning for DiffFilter" + "\n22->Set Tunning Dataset Tag"
				+ "\n23->Set Failing Type"
				
				+ "\n31->Performance Analysis for Reverting File" 
				+ "\n32->Full Log Based" 
				+ "\n33->Full Log AST Based"
				+ "\n34->Diff filter+Dependency+BoostScore" 
				+ "\n35->Diff filter+Dependency"
				+ "\n36->Diff filter+BoostScore" 
				+ "\n37->Full Log+Dependency+BoostScore"
				+ "\n38->Baseline(Saha et al){Fail Part Log+Java File Rank+Then Gradle Build Script}"
				+ "\n39->All Evaluation Experiment"
				+ "\n40->Generate Fix File Count Data"
				+ "\n51->Gradle ASE Raw Label Log Generation"
				+ "\n52->Gradle ASE AST Matching Statistics"
				+ "\n53->Diff filter+Dependency+BoostScore+BuildScript AST"
				+ "\n54->Baseline(File Name Mentioned in Log Error Part)"
				+ "\n55->Java AST selection Statistics)");
		
		Scanner cin = new Scanner(System.in);

		System.out.println("Enter an integer: ");
		int inputid = cin.nextInt();

		if (inputid == 1) {
			dataFiltering();
		} else if (inputid == 2) {
			commitChangeAnalysis();
		} else if (inputid == 3) {
			genDifferentialBuildLog();
		} else if (inputid == 4) {
			generateSimilarity();
			// generateSimilarityDifferentialLog();
			// genSimDifferentialLogOnChange();
		} else if (inputid == 5) {
			generateSimilarityDifferentialLog();
		} else if (inputid == 6) {
			genSimDifferentialLogOnChange();
		} else if (inputid == 7) {
			genSimDifferentialLogOnChangeForLogging();
		} else if (inputid == 8) {
			generateStoreFailPartSimValue();
		} else if (inputid == 9) {
			genSimFailLogPartWithSimLimit();
		} else if (inputid == 10) {
			generateBuildDependencyTree();
		} else if (inputid == 11) {
			generateLogForAnalysis();
		} else if (inputid == 12) {
			astParserChecker();
		} else if (inputid == 13) {
			performResultAnalysis();
		} else if (inputid == 14) {
			generateSimilarityWithDependency();

		}
		// this is for 6,8,9,13 menu runnning together for analysis
		else if (inputid == 21) {
			parameterTunningDiffFilter();
		} else if (inputid == 22) {
			TunningDTSetter dtsetter = new TunningDTSetter();
			try {
				dtsetter.setTunningDataTags(100);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			;

		}
		else if(inputid==23)
		{
			GenerateTestFailType typesetobj=new GenerateTestFailType();
			typesetobj.generateFailType();
		}

		else if (inputid == 31) {
			EvaluationMgr.FixWithBuildFailChangeEval();
		} else if (inputid == 32) {
			EvaluationMgr.FullLogFaultLocalizationEval();
		} else if (inputid == 33) {
			EvaluationMgr.FullLogASTFaultLocalizationEval();
		} else if (inputid == 34) {
			EvaluationMgr.DiffFilterDependencyWithBoostScoreSimEval();
		} else if (inputid == 35) {
			EvaluationMgr.DiffFilterDependencySimEval();
		} else if (inputid == 36) {
			EvaluationMgr.DiffFilterBoostScoreSimEval();
		} else if (inputid == 37) {
			EvaluationMgr.FullLogDependencyBoostScoreSimEval();
		} else if (inputid == 38) {
			EvaluationMgr.BaseLineForISSTA();
		} 
		else if((inputid == 39))
		{
			EvaluationMgr.FixWithBuildFailChangeEval();
			EvaluationMgr.FullLogFaultLocalizationEval();
			EvaluationMgr.FullLogASTFaultLocalizationEval();
			EvaluationMgr.DiffFilterDependencyWithBoostScoreSimEval();
			EvaluationMgr.DiffFilterDependencySimEval();
			EvaluationMgr.DiffFilterBoostScoreSimEval();
			EvaluationMgr.FullLogDependencyBoostScoreSimEval();
			EvaluationMgr.BaseLineForISSTA();
			
		}		
		else if((inputid == 40))
		{
			EvaluationMgr.UpdateFixFileCount();

			
		}
		else if((inputid == 51))
		{
			GradleBuildScriptASTSelector astlogsel=new GradleBuildScriptASTSelector();
			astlogsel.performGradleTextAnalysis();			
		}
		else if((inputid == 52))
		{
			GradleBuildScriptASTSelector astlogsel=new GradleBuildScriptASTSelector();
			astlogsel.generateStatistics();			
		}
		else if((inputid == 53))
		{
			EvaluationMgr.DiffFilterDependencyWithBoostScoreBuildASTSimEval();		
		}
		else if((inputid == 54))
		{
			EvaluationMgr.BaseLineForFileName();	
		}
		else if((inputid == 55))
		{
			JavaASTSelector aststat=new JavaASTSelector();
			aststat.generateStatistics();
		}
		else if (inputid == 68913) {
//			genSimDifferentialLogOnChange();
//			genSimDifferentialLogOnChangeForLogging();
//			genSimFailLogPartWithSimLimit();
//			performResultAnalysis();
			parameterTunningDiffFilter();
			EvaluationMgr.DiffFilterDependencySimEval();
			EvaluationMgr.FullLogDependencyBoostScoreSimEval();
			EvaluationMgr.DiffFilterBoostScoreSimEval();
		}

		else {
			CommitChangeExtractor obj = new CommitChangeExtractor();
			obj.testCommit();

			System.out.println("Wrong Function Id Entered");

			Config.thresholdForSimFilter = 0.1;

			System.out.println(Config.thresholdForSimFilter);
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
			// simgen.simAnalyzerFilteredLog();
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

	private static void genSimFailLogPartWithSimLimit() {
		SimGenerationMngr simgen = new SimGenerationMngr();

		try {
			simgen.simAnalyzerWithFailPartLineSim();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void genSimDifferentialLogOnChangeForLogging() {
		SimGenerationMngr simgen = new SimGenerationMngr();

		try {
			simgen.simAnalyzerDifferemtialLogWithChangeforLogging();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void generateStoreFailPartSimValue() {
		LogFailPartSimGenMgr failpartsimgen = new LogFailPartSimGenMgr();

		failpartsimgen.generateAndStoreSimilarity();

	}

	private static void generateBuildDependencyTree() {
		BuildDependencyGenerator depgenerator = new BuildDependencyGenerator();

		depgenerator.generateBuildDependency();
	}

	private static void generateLogForAnalysis() {
		SimGenerationMngr simgen = new SimGenerationMngr();

		try {
			simgen.printDifferentLogs();
			System.out.println(
					"***************Logs are written at: " + Config.getInspectionLogDir() + "**********************");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void generateSimilarityWithDependency() {
		SimGenerationMngr simgen = new SimGenerationMngr();

		try {
			simgen.simAnalyzerWithBuildDependency();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void astParserChecker() {

		// String
		// str="D:\\Researh_Works\\ASE_2018\\dependency_analysis\\Sample_Project\\spockframework\\spock\\spock-core\\src\\main\\java\\spock\\util\\concurrent\\AsyncConditions.java";
		// JavaASTParser javaparser=new JavaASTParser();
		//
		// String code = null;
		// try {
		// code = javaparser.readFile(new File(str));
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		//
		// List<String> asts=javaparser.parseJavaMethodDecs(code);
		//
		// str=String.join(" ", asts);
		//
		// System.out.println(str);
		//
		// String str1="abcdefghij<=##=>0.99";
		//
		// if (str1.length() >= Config.lineSimSeperator.length()) {
		//
		// String[] strparts = str1.split(Config.lineSimSeperator);
		//
		// if (strparts[1] != null && Double.parseDouble(strparts[1]) <
		// Config.thresholdForSimFilter) {
		//
		// if (strparts[0] != null) {
		// System.out.println(strparts[0]);
		// }
		//
		// }
		//
		// }
		//
		// String str3="&gt; Building &gt; :test &gt; 66 tests
		// completedcom.soundcloud.api.CloudAPIIntegrationTest &gt;
		// shouldBeAbleToMakePublicRequests";
		//
		// try {
		// str3=CosineDocumentSimilarity.removeStopWordsAndStem(str3);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// System.out.println(str3);

		//
		// List<String>
		// buildfaillines=TextFileReaderWriter.GetFileContentByLine("D:\\Researh_Works\\ASE_2018\\data_analysis\\failpart.txt");
		// List<String>
		// buildpasslines=TextFileReaderWriter.GetFileContentByLine("D:\\Researh_Works\\ASE_2018\\data_analysis\\passpart.txt");
		//
		// buildfaillines = getListAfterRemoveDuplicate(buildfaillines);
		// buildpasslines = getListAfterRemoveDuplicate(buildpasslines);
		//
		// CosineDocumentSimilarity cosdocsim = new
		// CosineDocumentSimilarity(buildpasslines, buildfaillines);
		//
		// List<String> filteredlines = new ArrayList<String>();
		// for (int failindex = 0; failindex < buildfaillines.size();
		// failindex++) {
		//
		// int passindex = 0;
		// double maxsimval = 0.0;
		//
		// while (passindex < buildpasslines.size()) {
		// try {
		// double simval = 0.0;
		// if(passindex==633)
		// {
		// int test=1;
		// }
		// simval = cosdocsim.getCosineSimilarity(passindex, failindex);
		// if (simval > maxsimval) {
		// maxsimval = simval;
		// //System.out.println((passindex+1)+"==>"+buildpasslines.get(passindex));
		// }
		// // }
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// passindex++;
		// } // end while
		//
		// filteredlines.add(buildfaillines.get(failindex) +
		// Config.lineSimSeperator + maxsimval);
		// }
		//
		// StringBuilder strbuilder = new StringBuilder();
		//
		// for (int lineindex = 0; lineindex < filteredlines.size();
		// lineindex++) {
		// strbuilder.append(filteredlines.get(lineindex));
		// strbuilder.append("\n");
		// }
		//
		// System.out.println(strbuilder.toString());

		BuildDependencyGenerator gen = new BuildDependencyGenerator();
		gen.generateBuildDependency();

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

	private static void performResultAnalysis() {
		ResultGenMngr resultgen = new ResultGenMngr();

		resultgen.performResultAnalysis();
	}

	private static void parameterTunningDiffFilter() {
		// DiffFilterTunning tuner=new DiffFilterTunning();
		// try {
		// tuner.simAnalyzerWithFailPartLineSim();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// DiffFilterRecentTunning tuner=new DiffFilterRecentTunning();
		//
		// try {
		// tuner.simAnalyzerWithFailPartLineSimAndRecent();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		DiffFilterRecentDependencyTunning tuner = new DiffFilterRecentDependencyTunning();

		try {
			tuner.simAnalyzerWithFailPartLineSimRecentDependency();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ParformanceAnalysis perf=new ParformanceAnalysis();
		// try {
		// perf.simAnalyzerWithFailPartLineSimAndRecent();
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	public static List<String> getListAfterRemoveDuplicate(List<String> originallist) {

		List<String> withoutduplicate = new ArrayList<String>();

		for (int index = 0; index < originallist.size(); index++) {
			String str = originallist.get(index);

			if (!withoutduplicate.contains(str)) {
				withoutduplicate.add(str);
			}
		}

		return withoutduplicate;
	}

}
