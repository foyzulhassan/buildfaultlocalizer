package com.build.strace.ranking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.build.analyzer.config.Config;
import com.build.analyzer.dtaccess.DBActionExecutorChangeData;
import com.build.analyzer.dtaccess.SessionGenerator;
import com.build.analyzer.entity.Gradlebuildfixdata;
import com.build.commitanalyzer.CommitAnalyzer;
import com.build.metrics.RankingCalculator;
import com.build.strace.dependency.DependencyGenerator;
import com.build.strace.entity.FileScore;
import com.build.strace.spectrum.SpectrumCalculator;
import com.util.sorting.SortingMgr;

public class RankingMgr {
	
	public void generateStraceRanking()
	{
		DBActionExecutorChangeData dbexec = new DBActionExecutorChangeData();

		RankingCalculator rankmetric = new RankingCalculator();

		List<Gradlebuildfixdata> projects = dbexec.getProjectRows("BuildCraft/BuildCraft");

		int totaltopn = 0;
		double totalmrr = 0.0;
		double totalmap = 0.0;

		try {

			totaltopn = 0;
			totalmrr = 0.0;
			totalmap = 0.0;

			for (int index = 0; index < projects.size(); index++) {
				Gradlebuildfixdata proj = projects.get(index);
				String project = proj.getGhProjectName();
				project = project.replace('/', '@');				
				String projecth = Config.repoDir + project;
				System.out.println(proj.getRow() + "=>" + project);

				CommitAnalyzer cmtanalyzer = null;

				try {
					cmtanalyzer = new CommitAnalyzer("test", project);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//this gives path based on Git repo relative path
				List<String> recentchangefile = cmtanalyzer.extractFileChangeListInBetweenCommit(proj.getGitCommit(),
						proj.getGitLastfailCommit());
				
				//Git repo relative path to local path list
				List<String> localpathtochange=new ArrayList<>();				
				for(String strfile:recentchangefile)
				{
					String localpath=Config.dynamicBuildDir+project+"//"+strfile;
					localpathtochange.add(localpath);
				}
				
				DependencyGenerator depgen=new DependencyGenerator();
				FileScore filescore=depgen.getFileSuspicionScore(projecth,project,proj.getGitLastfailCommit(),localpathtochange);
				
				Map<String,Boolean> passedlines=depgen.getPassedlines();
				Map<String,Boolean> failedlines=depgen.getFailedlines();				

				String actualfixfile = proj.getF2passFilelist();
				//String failintrofiles = proj.getFailFilelist();
				String[] actualfixs = actualfixfile.split(";");
				
				SpectrumCalculator spectrumcalc=new SpectrumCalculator();
				
				
				ArrayList<String> tarantulalist=spectrumcalc.getTarantulaBasedRanking(filescore, passedlines, failedlines);
				ArrayList<String> ochiailist=spectrumcalc.getOchiaiBasedRanking(filescore, passedlines, failedlines);
				ArrayList<String> op2list=spectrumcalc.getOp2BasedRanking(filescore, passedlines, failedlines);
				ArrayList<String> barinellist=spectrumcalc.getBarinelBasedRanking(filescore, passedlines, failedlines);

			
				int tarantulatopn = rankmetric.getTopN(tarantulalist, actualfixs);
				double tarantulamrr = rankmetric.getMeanAverageReciprocal(tarantulalist, actualfixs);
				double tarantulamap = rankmetric.getMeanAveragePrecision(tarantulalist, actualfixs);
				
				int ochiaitopn = rankmetric.getTopN(ochiailist, actualfixs);
				double ochiaimrr = rankmetric.getMeanAverageReciprocal(ochiailist, actualfixs);
				double ochiaimap = rankmetric.getMeanAveragePrecision(ochiailist, actualfixs);
				
				int op2topn = rankmetric.getTopN(op2list, actualfixs);
				double op2mrr = rankmetric.getMeanAverageReciprocal(op2list, actualfixs);
				double op2map = rankmetric.getMeanAveragePrecision(op2list, actualfixs);
				
				int barineltopn = rankmetric.getTopN(barinellist, actualfixs);
				double barinelmrr = rankmetric.getMeanAverageReciprocal(barinellist, actualfixs);
				double barinelmap = rankmetric.getMeanAveragePrecision(barinellist, actualfixs);
				
				
				System.out.println("Tarantula "+"TopN:"+tarantulatopn+" MRR:"+tarantulamrr+" MAP:"+tarantulamap);
				System.out.println("Oochiai "+"TopN:"+ochiaitopn+" MRR:"+ochiaimrr+" MAP:"+ochiaimap);
				System.out.println("Op2 "+"TopN:"+op2topn+" MRR:"+op2mrr+" MAP:"+op2map);
				System.out.println("Barinel "+"TopN:"+barineltopn+" MRR:"+barinelmrr+" MAP:"+barinelmap);
//				projects.get(index).setEvDiffdepboostPos(topn);
//				projects.get(index).setEvDiffdepboostMrr(mrr);
//				projects.get(index).setEvDiffdepboostMap(map);
//
//				totaltopn = totaltopn + topn;
//				totalmrr = totalmrr + mrr;
//				totalmap = totalmap + map;

			}

//			System.out.println("\n\n\n*******Diff Filter+Dependency+BoostScore********");
//			System.out.println("\n*******For Param: " + Config.thresholdForSimFilter + "********");
//			System.out.println("\nTopN: " + (totaltopn / projects.size()) + " MRR: " + (totalmrr / projects.size()) + " MAP: "
//					+ (totalmap / projects.size()));
			
			SessionGenerator.closeFactory();
			dbexec = new DBActionExecutorChangeData();
			dbexec.updateBatchExistingRecord(projects);

		} catch (Exception ex) {
			/* ignore */}

	}

}
