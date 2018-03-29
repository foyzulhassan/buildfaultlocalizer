package com.build.commitanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import com.build.analyzer.config.Config;
import com.build.analyzer.diff.gradle.GradleChange;
import com.build.analyzer.diff.gradle.GradlePatchGenMngr;
import com.build.analyzer.diff.java.JavaPatchGenMngr;
import com.build.analyzer.entity.CommitChange;
import com.build.analyzer.entity.Gradlepatch;

//import edu.utsa.data.DataResultsHolder;
//import edu.utsa.data.DataStatsHolder;
//import edu.utsa.main.MainClass;

import com.github.gumtreediff.actions.model.Action;

import org.apache.commons.io.IOUtils;

/**
 * 
 * @author Foyzul Hassan
 *
 * 
 */

public class CommitAnalyzer {

	/** Various methods encapsulating methods to treats Git and commits datas */
	private CommitAnalyzingUtils commitAnalyzingUtils;

	/** All the statistical datas (number of faulty commit, actions, etc) */
	private DataStatsHolder statsHolder;

	/** File managing object for tables */
	private DataResultsHolder resultsHolder;

	/** Name of the project */
	private String project;

	/** Owner of the project (necessary for Markdown parsing) */
	private String projectOwner;

	/** Path to the directory */
	private String directoryPath;

	/** Repository object, representing the directory */
	private Repository repository;

	/** Git entity to treat with the Repository data */
	private Git git;

	/** Revision walker from JGit */
	private RevWalk rw;

	private CommitChange commitChangeTracker;

	private String gradleChanges;

	/** Classic constructor */
	public CommitAnalyzer(String projectOwner, String project) throws Exception {
		this.projectOwner = projectOwner;
		this.project = project;

		directoryPath = Config.repoDir + project + "/.git";

		commitAnalyzingUtils = new CommitAnalyzingUtils();
		statsHolder = new DataStatsHolder();
		repository = commitAnalyzingUtils.setRepository(directoryPath);
		git = new Git(repository);
		rw = new RevWalk(repository);
		this.commitChangeTracker = new CommitChange();
		this.gradleChanges = "";
	}

	public CommitChange getCommitChangeTracker() {
		return commitChangeTracker;
	}

	public void commitSampleTry(String ID) {
		List<Action> totalactions = new ArrayList<Action>();
		List<Action> act = new ArrayList<Action>();
		List<String> debugging = new ArrayList<String>();
		String r = "";
		// File debug = new File("debug-" + ID + ".txt");

		try {
			ObjectId objectid = repository.resolve(ID);

			if (objectid == null)
				return;

			RevCommit commit = rw.parseCommit(objectid);

			// System.out.println(commit.getFullMessage());

			if (commit.getParentCount() > 0) {
				RevCommit parent = rw.parseCommit(commit.getParent(0).getId());

				DiffFormatter df = commitAnalyzingUtils.setDiffFormatter(repository, true);

				List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());

				for (DiffEntry diff : diffs) {
					if (diff.getNewPath().contains("build.gradle")) {

						commitChangeTracker.setBuildFileChange(commitChangeTracker.getBuildFileChange() + 1);

					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public void extractGradleFileChange(String ID) {
		// File debug = new File("debug-" + ID + ".txt");

		try {
			ObjectId objectid = repository.resolve(ID);

			if (objectid == null)
				return;

			RevCommit commit = rw.parseCommit(objectid);

			/// System.out.println(commit.getFullMessage());

			if (commit.getParentCount() > 0) {
				RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
				GradlePatchGenMngr gradlechgmgr = new GradlePatchGenMngr();
				DiffFormatter df = commitAnalyzingUtils.setDiffFormatter(repository, true);

				List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());

				for (DiffEntry diff : diffs) {

					if (diff.getNewPath().contains(".gradle")) {
						String currentContent = commitAnalyzingUtils.getContent(repository, diff, commit)[0];
						String previousContent = commitAnalyzingUtils.getContent(repository, diff, commit)[1];

						File f1 = commitAnalyzingUtils.writeContentInFile("g1.gradle", currentContent);
						File f2 = commitAnalyzingUtils.writeContentInFile("g2.gradle", previousContent);

						if (f1 != null && f2 != null) {
							try {

								gradlechgmgr.generatePatch(f2.toString(), f1.toString());

								// gradleChanges = gradleChanges + change;

								f1.delete();
								f2.delete();
							}

							catch (Exception e) {
								System.out.println("Exception Commit ID:" + ID);

							}

						}
					}

				}
				gradleChanges = gradlechgmgr.getXMLChange();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public Map<String, List<Action>> extractChangeInBetweenCommit(String ID1, String ID2) {

		Map<String, List<Action>> filechangemap = new HashMap<String, List<Action>>();
		GradlePatchGenMngr gradlechgmgr = new GradlePatchGenMngr();
		JavaPatchGenMngr javachngmsr = new JavaPatchGenMngr();

		try {
			ObjectId objectid1 = repository.resolve(ID1);
			ObjectId objectid2 = repository.resolve(ID2);

			if (objectid2 == null)
				return null;

			if (objectid1 == null)
				return null;

			RevCommit commit1 = rw.parseCommit(objectid1);
			RevCommit commit2 = rw.parseCommit(objectid2);

			/// System.out.println(commit.getFullMessage());

			// RevCommit parent = rw.parseCommit(commit.getParent(0).getId());

			DiffFormatter df = commitAnalyzingUtils.setDiffFormatter(repository, true);

			List<DiffEntry> diffs = df.scan(commit1.getTree(), commit2.getTree());

			for (DiffEntry diff : diffs) {

				if (diff.getNewPath().contains(".gradle")) {

					String currentContent = commitAnalyzingUtils.getContentOnCommit(repository, diff, commit2);
					String previousContent = commitAnalyzingUtils.getContentOnCommit(repository, diff, commit1);

					File f1 = commitAnalyzingUtils.writeContentInFile("g1.gradle", currentContent);
					File f2 = commitAnalyzingUtils.writeContentInFile("g2.gradle", previousContent);

					if (f1 != null && f2 != null) {
						try {

							// gradlechgmgr.generatePatch(f2.toString(),
							// f1.toString());
							List<Action> changes = gradlechgmgr.getGradleChanges(f2.toString(), f1.toString());
							filechangemap.put(diff.getNewPath(), changes);

							// gradleChanges = gradleChanges + change;

							f1.delete();
							f2.delete();
						}

						catch (Exception e) {
							System.out.println("Exception Parent Commit ID:" + ID1 + "Exception Child Commit ID:" + ID2
									+ ">>>" + e.getMessage());

						}

					}
				} else if (diff.getNewPath().contains(".java")) {
					String currentContent = commitAnalyzingUtils.getContentOnCommit(repository, diff, commit2);
					String previousContent = commitAnalyzingUtils.getContentOnCommit(repository, diff, commit1);

					File f1 = commitAnalyzingUtils.writeContentInFile("j1.java", currentContent);
					File f2 = commitAnalyzingUtils.writeContentInFile("j2.java", previousContent);

					if (f1 != null && f2 != null) {
						try {

							// gradlechgmgr.generatePatch(f2.toString(),
							// f1.toString());
							List<Action> changes = javachngmsr.getJavaChanges(f2.toString(), f1.toString());
							filechangemap.put(diff.getNewPath(), changes);

							// gradleChanges = gradleChanges + change;

							f1.delete();
							f2.delete();
						}

						catch (Exception e) {
							System.out.println("Exception Parent Commit ID:" + ID1 + "Exception Child Commit ID:" + ID2
									+ ">>>" + e.getMessage());

						}

					}

				}

			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return filechangemap;
	}

	// convert InputStream to String
	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}

	public String getStringFile(RevTree tree, String filter) throws IOException {
		// now try to find a specific file
		try (TreeWalk treeWalk = new TreeWalk(repository)) {

			treeWalk.addTree(tree);
			treeWalk.setRecursive(true);

			treeWalk.setFilter(PathFilter.create(filter));
			if (!treeWalk.next()) {
				throw new IllegalStateException("Did not find expected file:" + filter);
			}

			// FileMode specifies the type of file, FileMode.REGULAR_FILE for
			// normal file, FileMode.EXECUTABLE_FILE for executable bit
			// set
			FileMode fileMode = treeWalk.getFileMode(0);
			ObjectLoader loader = repository.open(treeWalk.getObjectId(0));

			// loader.copyTo(System.out);
			byte[] butestr = loader.getBytes();

			String str = new String(butestr);

			return str;

		}
	}

	public String getFileContentAtCommit(String commitid, DiffEntry diff) {
		String content = "";
		try {
			ObjectId objectid1 = repository.resolve(commitid);

			if (objectid1 == null)
				return null;

			RevCommit parent = rw.parseCommit(objectid1);

			RevTree tree = getTree(commitid);

			content = getStringFile(tree, diff.getNewPath());

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return content;
	}

	public String extractContaintOfTrainFile(String ID1, String ID2, String fixstatement, boolean withfix) {

		String traincontent = null;
		boolean foundflag = false;

		try {
			ObjectId objectid1 = repository.resolve(ID1);
			ObjectId objectid2 = repository.resolve(ID2);

			if (objectid2 == null)
				return null;

			if (objectid1 == null)
				return null;

			RevCommit parent = rw.parseCommit(objectid1);
			RevCommit commit = rw.parseCommit(objectid2);

			if (commit.getParentCount() > 0) {
				parent = rw.parseCommit(commit.getParent(0).getId());
			}

			DiffFormatter df = commitAnalyzingUtils.setDiffFormatter(repository, true);

			List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());

			GradlePatchGenMngr gradlechgmgr = new GradlePatchGenMngr();
			for (DiffEntry diff : diffs) {

				if (diff.getNewPath().contains(".gradle")) {

					String currentContent = getFileContentAtCommit(ID2, diff);
					String previousContent = getFileContentAtCommit(ID1, diff);

					File f1 = commitAnalyzingUtils.writeContentInFile("g1.gradle", currentContent);
					File f2 = commitAnalyzingUtils.writeContentInFile("g2.gradle", previousContent);

					if (f1 != null && f2 != null) {
						try {

							gradlechgmgr.generatePatch(f2.toString(), f1.toString());

							// gradleChanges = gradleChanges + change;

							f1.delete();
							f2.delete();

							List<GradleChange> gradlechanges = gradlechgmgr.getGradlechanges();

							for (int index = 0; index < gradlechanges.size(); index++) {
								GradleChange change = gradlechanges.get(index);

								if (change.getStatementExp().equals(fixstatement) && foundflag == false) {

									if (withfix)
										traincontent = currentContent;
									else
										traincontent = previousContent;

									foundflag = true;
								}
							}

						}

						catch (Exception e) {
							System.out.println("Exception Parent Commit ID:" + ID1 + "Exception Child Commit ID:" + ID2
									+ ">>>" + e.getMessage());

						}

					}
				}

			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return traincontent;
	}

	public List<Action> extractActions(String ID1, String ID2) {

		List<Action> actions = null;

		try {
			ObjectId objectid1 = repository.resolve(ID1);
			ObjectId objectid2 = repository.resolve(ID2);

			if (objectid2 == null)
				return null;

			if (objectid1 == null)
				return null;

			RevCommit parent = rw.parseCommit(objectid1);
			RevCommit commit = rw.parseCommit(objectid2);

			if (commit.getParentCount() > 0) {
				parent = rw.parseCommit(commit.getParent(0).getId());
			}

			DiffFormatter df = commitAnalyzingUtils.setDiffFormatter(repository, true);

			List<DiffEntry> diffs = df.scan(parent.getTree(), commit.getTree());

			GradlePatchGenMngr gradlechgmgr = new GradlePatchGenMngr();
			for (DiffEntry diff : diffs) {

				if (diff.getNewPath().contains(".gradle")) {

					String currentContent = getFileContentAtCommit(ID2, diff);
					String previousContent = getFileContentAtCommit(ID1, diff);

					File f1 = commitAnalyzingUtils.writeContentInFile("g1.gradle", currentContent);
					File f2 = commitAnalyzingUtils.writeContentInFile("g2.gradle", previousContent);

					if (f1 != null && f2 != null) {
						try {

							gradlechgmgr.generatePatch(f2.toString(), f1.toString());

							// gradleChanges = gradleChanges + change;

							f1.delete();
							f2.delete();

							actions = gradlechgmgr.changeActions;

						}

						catch (Exception e) {
							System.out.println("Exception Parent Commit ID:" + ID1 + "Exception Child Commit ID:" + ID2
									+ ">>>" + e.getMessage());

						}

					}
				}

			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return actions;
	}

	public CommitAnalyzingUtils getCommitAnalyzingUtils() {
		return commitAnalyzingUtils;
	}

	public void setCommitAnalyzingUtils(CommitAnalyzingUtils commitAnalyzingUtils) {
		this.commitAnalyzingUtils = commitAnalyzingUtils;
	}

	public String getGradleChanges() {
		return gradleChanges;
	}

	public void setCommitChangeTracker(CommitChange commitChangeTracker) {
		this.commitChangeTracker = commitChangeTracker;
	}

	/** Main method, probes all commits of a given repo and analyzes it */
	public void probeAllCommits() throws Exception {
		long startTime = System.nanoTime();
		resultsHolder = new DataResultsHolder(project, projectOwner, "all-commits");

		Iterable<RevCommit> commits = commitAnalyzingUtils.getAllCommits(git);

		/** Goes through every commit of a given branch */
		for (RevCommit commit : commits) {
			boolean assigned = false;
			boolean returned = false;
			boolean fielded = false;
			boolean localed = false;
			String action = "";
			boolean faulty = false;
			List<Action> totalactions = new ArrayList<Action>();

			statsHolder.increment("commit");
			// System.out.println("\n-------------------------------------");
			// System.out
			// .println("--- Files of commit n°" + statsHolder.getNbCommits() +
			// " with ID : " + commit.getName());
			// System.out.println("-------------------------------------");

			if (commit.getParentCount() > 0) {
				RevCommit targetCommit = rw.parseCommit(repository.resolve(commit.getName()));
				RevCommit targetParent = rw.parseCommit(commit.getParent(0).getId());

				DiffFormatter df = commitAnalyzingUtils.setDiffFormatter(repository, true);

				List<DiffEntry> diffs = df.scan(targetParent.getTree(), targetCommit.getTree());

				for (DiffEntry diff : diffs) {
					String currentContent = commitAnalyzingUtils.getContent(repository, diff, commit)[0];
					String previousContent = commitAnalyzingUtils.getContent(repository, diff, commit)[1];

					if (diff.getNewPath().contains(".java")) {
						File f1 = commitAnalyzingUtils.writeContentInFile("c1.java", currentContent);
						File f2 = commitAnalyzingUtils.writeContentInFile("c2.java", previousContent);

						if (f1 != null && f2 != null) {
							try {

								f1.delete();
								f2.delete();
							}

							catch (Exception e) {
								statsHolder.increment("file_error");
								faulty = true;
							}
						}
					}
				}

				if (faulty)
					statsHolder.increment("commit_error");

				if (totalactions.size() == 1) {
					resultsHolder.addOneOnly(action, commit);
					statsHolder.incrementOnlyOne(action);

					// System.out.println("Hello !");
					action = "";
				}
			}

			if (statsHolder.getNbCommits() % 20 == 0) {
				// System.out.println("Save !");
				statsHolder.saveResults(project, "all-commits");
				resultsHolder.saveResults();
			}
		}

		statsHolder.printResults();
		resultsHolder.saveResults();

		statsHolder.reset();
		long endTime = System.nanoTime();

		long duration = (endTime - startTime) / 1000000;
		System.out.println("Execution time : " + duration + "ms (" + duration / 1000 + "s)");
	}

	public void probeOddCodeCommit(String filepath) throws Exception {
		long startTime = System.nanoTime();
		resultsHolder = new DataResultsHolder(project, projectOwner, "odd-code");

		for (String line : Files.readAllLines(Paths.get("../bugfixRepoSamples/" + project + "/" + filepath), null)) {
			boolean assigned = false;
			boolean returned = false;
			boolean fielded = false;
			boolean localed = false;
			String action = "";
			boolean faulty = false;

			List<Action> totalactions = new ArrayList<Action>();
			String[] parts = line.split(",");

			// System.out.println("\n-------------------------------------");
			// System.out.println("--- Files of commit " + parts[0]);
			// System.out.println("-------------------------------------");

			RevCommit bf_sha = rw.parseCommit(repository.resolve(parts[0]));
			RevCommit bi_sha = rw.parseCommit(repository.resolve(parts[1]));

			DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);

			df.setRepository(repository);
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);

			List<DiffEntry> diffs = df.scan(bf_sha.getTree(), bi_sha.getTree());

			for (DiffEntry diff : diffs) {
				String currentContent = commitAnalyzingUtils.getContent(repository, diff, bf_sha)[0];
				String previousContent = commitAnalyzingUtils.getContent(repository, diff, bi_sha)[0];

				if (diff.getNewPath().contains(".java")) {
					File f1 = commitAnalyzingUtils.writeContentInFile("c1.java", currentContent);
					File f2 = commitAnalyzingUtils.writeContentInFile("c2.java", previousContent);

					if (f1 != null && f2 != null) {
						try {

							f1.delete();
							f2.delete();
						}

						catch (Exception e) {
							statsHolder.increment("file_error");
							faulty = true;
						}
					}
				}
			}
			if (faulty)
				statsHolder.increment("commit_error");

			if (totalactions.size() == 1) {
				resultsHolder.addOneOnly(action, bf_sha);
				statsHolder.incrementOnlyOne(action);

				action = "";
			}

			if (statsHolder.getNbCommits() % 20 == 0) {
				System.out.println("Save !");
				statsHolder.saveResults(project, "odd-code");
				resultsHolder.saveResults();
			}
		}

		statsHolder.printResults();
		resultsHolder.saveResults();

		statsHolder.reset();
		long endTime = System.nanoTime();

		long duration = (endTime - startTime) / 1000000;
		System.out.println("Execution time : " + duration + "ms (" + duration / 1000 + "s)");
	}

	public void probeFileCommit(String filepath) throws Exception {
		resultsHolder = new DataResultsHolder(project, projectOwner, "xmls-commits");

		for (String line : Files.readAllLines(Paths.get("../bugfixRepoSamples/" + project + "/" + filepath), null)) {
			boolean assigned = false;
			boolean returned = false;
			boolean fielded = false;
			boolean localed = false;
			String action = "";
			boolean faulty = false;
			List<Action> totalactions = new ArrayList<Action>();
			// System.out.println("\n-------------------------------------");
			// System.out.println("--- Files of commit " + line);
			// System.out.println("-------------------------------------");

			RevCommit commit = rw.parseCommit(repository.resolve(line));

			if (commit.getParentCount() > 0) {
				RevCommit parent = rw.parseCommit(commit.getParent(0).getId());

				DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);

				df.setRepository(repository);
				df.setDiffComparator(RawTextComparator.DEFAULT);
				df.setDetectRenames(true);

				List<DiffEntry> diffs = df.scan(commit.getTree(), parent.getTree());

				for (DiffEntry diff : diffs) {
					String currentContent = commitAnalyzingUtils.getContent(repository, diff, commit)[0];
					String previousContent = commitAnalyzingUtils.getContent(repository, diff, commit)[1];

					if (diff.getNewPath().contains(".java")) {
						File f1 = commitAnalyzingUtils.writeContentInFile("c1.java", currentContent);
						File f2 = commitAnalyzingUtils.writeContentInFile("c2.java", previousContent);

						if (f1 != null && f2 != null) {
							try {

								f1.delete();
								f2.delete();

							}

							catch (Exception e) {
								statsHolder.increment("file_error");
								faulty = true;
							}
						}
					}
				}
				if (faulty)
					statsHolder.increment("commit_error");

				if (totalactions.size() == 1) {
					System.out.println("Hello !");
					resultsHolder.addOneOnly(action, commit);
					statsHolder.incrementOnlyOne(action);

					action = "";
				}

				if (statsHolder.getNbCommits() % 20 == 0) {
					System.out.println("Save !");
					statsHolder.saveResults(project, "xmls-commits");
					resultsHolder.saveResults();
				}
			}
			statsHolder.saveResults(project, "xmls-commits");
			resultsHolder.saveResults();

			statsHolder.reset();
		}
	}

	public RevTree getTree(String cmtid) throws IOException {
		ObjectId lastCommitId = repository.resolve(cmtid);

		// a RevWalk allows to walk over commits based on some filtering
		try (RevWalk revWalk = new RevWalk(repository)) {
			RevCommit commit = revWalk.parseCommit(lastCommitId);

			System.out.println("Time of commit (seconds since epoch): " + commit.getCommitTime());

			// and using commit's tree find the path
			RevTree tree = commit.getTree();
			System.out.println("Having tree: " + tree);
			return tree;
		}
	}

	public String getStringAsFile(RevTree tree, String foldername) throws IOException {
		// now try to find a specific file

		try (TreeWalk treeWalk = new TreeWalk(repository)) {

			String filter = "";

			if (foldername != null && foldername.length() > 0) {
				filter = foldername + "/" + "build.gradle";
			} else {
				filter = "build.gradle";
			}
			treeWalk.addTree(tree);
			treeWalk.setRecursive(true);

			treeWalk.setFilter(PathFilter.create(filter));
			if (!treeWalk.next()) {
				// throw new IllegalStateException("Did not find expected
				// file:"+filter);

				filter = "build.gradle";
				treeWalk.addTree(tree);
				treeWalk.setRecursive(true);
				treeWalk.setFilter(PathFilter.create(filter));

				if (!treeWalk.next()) {

					throw new IllegalStateException("Did not find expected file:" + filter);
				}
			}

			// FileMode specifies the type of file, FileMode.REGULAR_FILE for
			// normal file, FileMode.EXECUTABLE_FILE for executable bit
			// set
			FileMode fileMode = treeWalk.getFileMode(0);
			ObjectLoader loader = repository.open(treeWalk.getObjectId(0));
			// System.out.println("README.md: " + getFileMode(fileMode) + ",
			// type: " + fileMode.getObjectType() + ", mode: " + fileMode +
			// " size: " + loader.getSize());

			// loader.copyTo(System.out);
			byte[] butestr = loader.getBytes();

			String str = new String(butestr);

			return str;

		}
	}

	public String getStringAsFile(RevCommit commit, String foldername) throws IOException {
		// now try to find a specific file
		String str = "";
		TreeWalk treeWalk = null;
		String filter = "";

		if (foldername != null && foldername.length() > 0) {
			filter = foldername + "/" + "build.gradle";
		} else {
			filter = "build.gradle";
		}

		try {
			treeWalk = TreeWalk.forPath(repository, filter, commit.getTree());

			if (treeWalk == null)
				treeWalk = TreeWalk.forPath(repository, "build.gradle", commit.getTree());

			InputStream inputStream = repository.open(treeWalk.getObjectId(0), Constants.OBJ_BLOB).openStream();

			StringWriter writer = new StringWriter();
			String encoding = "UTF-8";
			IOUtils.copy(inputStream, writer, encoding);

			str = writer.toString();

			// Read more:
			// http://javarevisited.blogspot.com/2012/08/convert-inputstream-to-string-java-example-tutorial.html#ixzz4qenalXkH
			treeWalk.close(); // use release() in JGit < 4.0

		} catch (IOException e) {
			e.printStackTrace();
		}

		return str;
	}

	public long getTimeDiffOfTwoCommitInMin(String commit1, String commit2) {
		long diffMinutes = 0;
		try {
			ObjectId objectid1 = repository.resolve(commit1);
			ObjectId objectid2 = repository.resolve(commit2);

			if (objectid2 == null)
				return -1;

			if (objectid1 == null)
				return -1;

			RevCommit parent = rw.parseCommit(objectid1);
			RevCommit commit = rw.parseCommit(objectid2);

			Date d1 = new Date(parent.getCommitTime() * 1000L);

			Date d2 = new Date(commit.getCommitTime() * 1000L);

			long diff = d2.getTime() - d1.getTime();

			diffMinutes = diff / (60 * 1000);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return diffMinutes;

	}

	private int length(String foldername) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * Getters and setters Below this point
	 */
	public CommitAnalyzingUtils getBugfixUtils() {
		return commitAnalyzingUtils;
	}

	public void setBugfixUtils(CommitAnalyzingUtils commitAnalyzingUtils) {
		this.commitAnalyzingUtils = commitAnalyzingUtils;
	}

	public DataStatsHolder getStatsHolder() {
		return statsHolder;
	}

	public void setStatsHolder(DataStatsHolder statsHolder) {
		this.statsHolder = statsHolder;
	}

	public DataResultsHolder getResultsHolder() {
		return resultsHolder;
	}

	public void setResultsHolder(DataResultsHolder resultsHolder) {
		this.resultsHolder = resultsHolder;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getProjectOwner() {
		return projectOwner;
	}

	public void setProjectOwner(String projectOwner) {
		this.projectOwner = projectOwner;
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public Git getGit() {
		return git;
	}

	public void setGit(Git git) {
		this.git = git;
	}

	public RevWalk getRw() {
		return rw;
	}

	public void setRw(RevWalk rw) {
		this.rw = rw;
	}
}
