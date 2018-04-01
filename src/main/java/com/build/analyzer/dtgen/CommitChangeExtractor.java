package com.build.analyzer.dtgen;

import java.util.List;
import java.util.Map;

import com.build.analyzer.dtaccess.DBActionExecutorChangeData;
import com.build.analyzer.dtaccess.SessionGenerator;
import com.build.analyzer.entity.Gradlebuildfixdata;
import com.build.commitanalyzer.CommitAnalyzer;
import com.build.revertanalyzer.ReverAnalyzer;
import com.github.gumtreediff.actions.model.Action;

public class CommitChangeExtractor {

	public void updateCommitChange() throws Exception {
		DBActionExecutorChangeData dbexec = new DBActionExecutorChangeData();

		ReverAnalyzer revertcheker = new ReverAnalyzer();

		List<Gradlebuildfixdata> projects = dbexec.getRows();

		for (int index = 0; index < projects.size(); index++) {
			Gradlebuildfixdata proj = projects.get(index);

			String commit = proj.getGitCommit();
			String failintrocommit = proj.getGitFailintroCommit();

			String lastfailcommit = proj.getGitLastfailCommit();
			String passcommit = proj.getGitFixCommit();

			String project = proj.getGhProjectName();
			project = project.replace('/', '@');
			// project="D:\\test\\appsly-android-rest";
			CommitAnalyzer cmtanalyzer = null;

			try {
				cmtanalyzer = new CommitAnalyzer("test", project);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Get the changes in between Pass to Fail Transition commit
			Map<String, List<Action>> failchangemap = cmtanalyzer.extractChangeInBetweenCommit(commit, failintrocommit);

			// Get the changes in between Fail to Pass Transition commit
			Map<String, List<Action>> fixchangemap = cmtanalyzer.extractChangeInBetweenCommit(passcommit,
					failintrocommit);

			if (revertcheker.isCodeRevereted(failchangemap, fixchangemap)) {
				projects.get(index).setReveretedStatus("YES");
			} else {
				projects.get(index).setReveretedStatus("NO");
			}

			int revertfilecount = revertcheker.getRevertedFileCount(failchangemap, fixchangemap);
			int changefilecount = failchangemap.size();
			
			String failchangefiles=revertcheker.getChangeFileList(failchangemap);
			String fixchangefiles=revertcheker.getChangeFileList(fixchangemap);
			
			
			projects.get(index).setFailFilelist(failchangefiles);
			projects.get(index).setPassFilelist(fixchangefiles);
			projects.get(index).setChangefileCount(changefilecount);
			projects.get(index).setRevertfileCount(revertfilecount);

		}

		SessionGenerator.closeFactory();
		dbexec = new DBActionExecutorChangeData();
		dbexec.updateBatchExistingRecord(projects);

	}

}
