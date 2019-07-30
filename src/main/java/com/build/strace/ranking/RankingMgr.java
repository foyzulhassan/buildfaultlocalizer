package com.build.strace.ranking;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.build.analyzer.config.Config;
import com.build.analyzer.dtaccess.DBActionExecutorChangeData;
import com.build.analyzer.dtaccess.SessionGenerator;
import com.build.analyzer.entity.Gradlebuildfixdata;
import com.build.analyzer.entity.SpectrumGradlebuildfixdata;
import com.build.commitanalyzer.CommitAnalyzer;
import com.build.metrics.RankingCalculator;
import com.build.strace.dependency.DependencyGenerator;
import com.build.strace.entity.FileScore;
import com.build.strace.spectrum.SpectrumCalculator;
import com.util.sorting.SortingMgr;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import static java.time.temporal.ChronoUnit.SECONDS;

public class RankingMgr {

	public void generateStraceRanking() {
		DBActionExecutorChangeData dbexec = new DBActionExecutorChangeData();

		RankingCalculator rankmetric = new RankingCalculator();

		// List<Gradlebuildfixdata> projects =
		// dbexec.getProjectRows("BuildCraft/BuildCraft");
//		 List<Gradlebuildfixdata> projects =
//		 dbexec.getProjectWithRowID(1343788);
//		 List<Gradlebuildfixdata> projects =
//		 dbexec.getProjectWithRowID(1735200);
		//List<Gradlebuildfixdata> projects = dbexec.getProjectWithRowID(1088847);
		List<SpectrumGradlebuildfixdata> projects=dbexec.getSpectrumRows();

		int totaltopn = 0;
		double totalmrr = 0.0;
		double totalmap = 0.0;

		try {

			totaltopn = 0;
			totalmrr = 0.0;
			totalmap = 0.0;

			for (int index = 0; index < projects.size(); index++) {
				Instant start = Instant.now();
				SpectrumGradlebuildfixdata proj = projects.get(index);
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

				// this gives path based on Git repo relative path
				List<String> recentchangefile = cmtanalyzer.extractFileChangeListInBetweenCommit(proj.getGitCommit(),
						proj.getGitLastfailCommit());

				// Git repo relative path to local path list
				List<String> localpathtochange = new ArrayList<>();
				for (String strfile : recentchangefile) {
					String localpath = Config.dynamicBuildDir + project + "/" + strfile;
					localpathtochange.add(localpath);
				}

				Config.projBuildCmd=proj.getSpBuildCmd();
				DependencyGenerator depgen = new DependencyGenerator();
				FileScore filescore = depgen.getFileSuspicionScore(projecth, project, proj.getGitLastfailCommit(),
						localpathtochange);

				Map<String, Boolean> passedlines = depgen.getPassedlines();
				Map<String, Boolean> failedlines = depgen.getFailedlines();

				String actualfixfile = proj.getF2passFilelist();
				// String failintrofiles = proj.getFailFilelist();
				String[] actualfixs = actualfixfile.split(";");

				SpectrumCalculator spectrumcalc = new SpectrumCalculator();

				ArrayList<String> tarantulalisttemp = spectrumcalc.getTarantulaBasedRanking(filescore, passedlines,
						failedlines, Config.dynamicBuildDir + project);
				ArrayList<String> ochiailisttemp = spectrumcalc.getOchiaiBasedRanking(filescore, passedlines, failedlines,
						Config.dynamicBuildDir + project);
				ArrayList<String> op2listtemp = spectrumcalc.getOp2BasedRanking(filescore, passedlines, failedlines,
						Config.dynamicBuildDir + project);
				ArrayList<String> barinellisttemp = spectrumcalc.getBarinelBasedRanking(filescore, passedlines, failedlines,
						Config.dynamicBuildDir + project);

				ArrayList<String> tarantulalist = getFilteredFiles(tarantulalisttemp);
				ArrayList<String> ochiailist=getFilteredFiles(ochiailisttemp);
				ArrayList<String> op2list = getFilteredFiles(op2listtemp);
				ArrayList<String> barinellist=getFilteredFiles(barinellisttemp);

				int tarantulatopn = rankmetric.getTopN(tarantulalist, actualfixs);
				double tarantulamrr = rankmetric.getMeanAverageReciprocal(tarantulalist, actualfixs);
				double tarantulamap = rankmetric.getMeanAveragePrecision(tarantulalist, actualfixs);
				
				projects.get(index).setSpEVTarantulaPos(tarantulatopn);
				projects.get(index).setSpEVTarantulaMrr(tarantulamrr);
				projects.get(index).setSpEVTarantulaMap(tarantulamap);

				int ochiaitopn = rankmetric.getTopN(ochiailist, actualfixs);
				double ochiaimrr = rankmetric.getMeanAverageReciprocal(ochiailist, actualfixs);
				double ochiaimap = rankmetric.getMeanAveragePrecision(ochiailist, actualfixs);
				
				projects.get(index).setSpEVOchiaiPos(ochiaitopn);
				projects.get(index).setSpEVOchiaiMrr(ochiaimrr);
				projects.get(index).setSpEVOchiaiMap(ochiaimap);

				int op2topn = rankmetric.getTopN(op2list, actualfixs);
				double op2mrr = rankmetric.getMeanAverageReciprocal(op2list, actualfixs);
				double op2map = rankmetric.getMeanAveragePrecision(op2list, actualfixs);
				
				projects.get(index).setSpEVOp2Pos(op2topn);
				projects.get(index).setSpEVOp2Mrr(op2mrr);
				projects.get(index).setSpEVOp2Map(op2map);

				int barineltopn = rankmetric.getTopN(barinellist, actualfixs);
				double barinelmrr = rankmetric.getMeanAverageReciprocal(barinellist, actualfixs);
				double barinelmap = rankmetric.getMeanAveragePrecision(barinellist, actualfixs);
				
				projects.get(index).setSpEVBarinelPos(barineltopn);
				projects.get(index).setSpEVBarinelMrr(barinelmrr);
				projects.get(index).setSpEVBarinelMap(barinelmap);
				Instant end = Instant.now();
				Duration interval = Duration.between(start, end);
				
				projects.get(index).setSpExecTime(interval.get(SECONDS));

				System.out.println(
						"Tarantula " + "TopN:" + tarantulatopn + " MRR:" + tarantulamrr + " MAP:" + tarantulamap);
				System.out.println("Oochiai " + "TopN:" + ochiaitopn + " MRR:" + ochiaimrr + " MAP:" + ochiaimap);
				System.out.println("Op2 " + "TopN:" + op2topn + " MRR:" + op2mrr + " MAP:" + op2map);
				System.out.println("Barinel " + "TopN:" + barineltopn + " MRR:" + barinelmrr + " MAP:" + barinelmap);
				

			}

			SessionGenerator.closeFactory();
			dbexec = new DBActionExecutorChangeData();
			dbexec.updateSpectrumBatchExistingRecord(projects);

		} catch (Exception ex) {
			/* ignore */}

	}

	public ArrayList<String> getFilteredFiles(ArrayList<String> filelist) {
		ArrayList<String> list = new ArrayList<>();

		for (String s : filelist) {
			if (!s.startsWith(".") && !s.startsWith(".git") && (s.endsWith(".java") || s.endsWith(".gradle"))) {
				list.add(s);
			}
		}

		return list;
	}

}
