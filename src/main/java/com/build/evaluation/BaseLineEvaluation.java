package com.build.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.build.analyzer.config.Config;
import com.build.analyzer.dtaccess.DBActionExecutorChangeData;
import com.build.analyzer.dtaccess.SessionGenerator;
import com.build.analyzer.entity.Gradlebuildfixdata;
import com.build.commitanalyzer.CommitAnalyzer;
import com.build.metrics.RankingCalculator;
import com.util.sorting.SortingMgr;

/*
 * This class is for BaseLine Evaluation for ISSTA paper
 * BaseLine performance calculation Process: Failed Part Build Log+Java code AST(Saha et al.)+Then build scripts
 * at the end of ranking
 */
public class BaseLineEvaluation {
	
	public void calculateBaseLineEvaluationScore() throws Exception {
		DBActionExecutorChangeData dbexec = new DBActionExecutorChangeData();
		RankingCalculator rankmetric = new RankingCalculator();

		List<Gradlebuildfixdata> projects = dbexec.getEvalRows();
		
		int totaltopn = 0;
		double totalmrr = 0.0;
		double totalmap = 0.0;
		
		for (int index = 0; index < projects.size(); index++) {
			
			Gradlebuildfixdata proj = projects.get(index);

			String project = proj.getGhProjectName();
			project = project.replace('/', '@');

			System.out.println(project);

			CommitAnalyzer cmtanalyzer = null;

			try {
				cmtanalyzer = new CommitAnalyzer("test", project);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Map<String, Double> simmapjava = cmtanalyzer.getBaselineFailLogPartTreeSimilarityMap(proj.getGitLastfailCommit(),
					proj.getF2row(), proj, true);

			Map<String, Double> sortedsimmap = SortingMgr.sortByValue(simmapjava);
			
			Map<String, Double> simmapgradle = cmtanalyzer.getBaselineFailLogPartTreeSimilarityMap(proj.getGitLastfailCommit(),
					proj.getF2row(), proj, false);
			
			//Appending Gradle files at the end of Sorted rank map
			for (Map.Entry<String, Double> entry : simmapgradle.entrySet()) {		
				sortedsimmap.put(entry.getKey(), entry.getValue());				
			}
			
			ArrayList<String> keys = new ArrayList<String>(sortedsimmap.keySet());

			String actualfixfile = proj.getF2passFilelist();

			String[] actualfixs = actualfixfile.split(";");

			projects.get(index).setTotalfileCount(sortedsimmap.size());
			int topn = rankmetric.getTopN(keys, actualfixs);
			double mrr = rankmetric.getMeanAverageReciprocal(keys, actualfixs);
			double map = rankmetric.getMeanAveragePrecision(keys, actualfixs);

			totaltopn = totaltopn + topn;
			totalmrr = totalmrr + mrr;
			totalmap = totalmap + map;		


			projects.get(index).setEvBaselineISSTAPos(topn);
			projects.get(index).setEvBaselineISSTAMrr(mrr);
			projects.get(index).setEvBaselineISSTAMap(map);
		}
		
		System.out.println("\n\n\n*******Baseline ISSTA********");
		System.out.println("\n*******For Param: " + Config.thresholdForSimFilter + "********");
		System.out.println("\nTopN: " + (totaltopn / projects.size()) + " MRR: " + (totalmrr / projects.size()) + " MAP: "
				+ (totalmap / projects.size()));
		
		SessionGenerator.closeFactory();
		dbexec = new DBActionExecutorChangeData();
		dbexec.updateBatchExistingRecord(projects);
	}
	

}
