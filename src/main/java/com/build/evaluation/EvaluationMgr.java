package com.build.evaluation;

public class EvaluationMgr {
	
	//For Reverting
	public static void FixWithBuildFailChangeEval()
	{
		FixWithBuildFailChange obj=new FixWithBuildFailChange();
		try {
			obj.fixWithBuildFailChangeAnalysis();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//For Full Log
	public static void FullLogFaultLocalizationEval()
	{
		FullLogFaultLocalization obj=new FullLogFaultLocalization();
		try {
			obj.simAnalyzerFullLog();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	//For Full Log+AST
	public static void FullLogASTFaultLocalizationEval()
	{
		FullLogASTFaultLocalization obj=new FullLogASTFaultLocalization();
		try {
			obj.simAnalyzerFullLogAST();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	//Diff Filter+Dependency+Boost
	public static void DiffFilterDependencyWithBoostScoreSimEval()
	{
		DiffFilterDependencyWithBoostScoreSim obj=new DiffFilterDependencyWithBoostScoreSim();
		try {
			obj.simAnalyzerWithFailPartLineSimRecentDependency();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	//Diff Filter+Dependency
	public static void DiffFilterDependencySimEval()
	{
		DiffFilterDependencySim obj=new DiffFilterDependencySim();
		try {
			obj.simAnalyzerWithFailPartLineSimRecentDependency();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	//Diff Filter+Boost
	public static void DiffFilterBoostScoreSimEval()
	{
		DiffFilterBoostScoreSim obj=new DiffFilterBoostScoreSim();
		try {
			obj.simAnalyzerWithFailPartLineSimBoostScore();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	//Full Log+Dependency+Boost
	public static void FullLogDependencyBoostScoreSimEval()
	{
		FullLogDependencyBoostScoreSim obj=new FullLogDependencyBoostScoreSim();
		try {
			obj.simAnalyzerWithFullLogBoostScore();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}
