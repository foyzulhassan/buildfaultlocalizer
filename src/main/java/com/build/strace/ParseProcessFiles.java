package com.build.strace;

import com.build.strace.entity.Entry;
import com.build.strace.entity.FileInfo;
import com.build.strace.entity.FileScore;
import com.build.strace.entity.OperationType;
import com.build.strace.entity.OutputEntry;
import com.build.strace.entity.ProcessInfo;
import com.build.strace.entity.SysCallTypes;
import com.build.strace.text.TextCleaner;
import com.build.util.MapContains;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

public class ParseProcessFiles {

	public static ProcessInfo getProcessNode(long pid, Map<Long, String> tracefilemap, String buildrootdir,
			Map<String, Boolean> passelines, Map<String, Boolean> faillines, List<String> repofiles,
			FileScore filescore) {
		ProcessInfo process = new ProcessInfo(pid);

		// List<String> javaclasslist = new ArrayList<>();
		Map<String, Double> javaclasslist = new HashMap<>();

		if (!tracefilemap.containsKey(pid))
			return null;

		String stracefile = tracefilemap.get(pid);

		try (BufferedReader input = openFile(stracefile)) {
			String line = "";
			while ((line = input.readLine()) != null) {
				Entry entry = parse_entry(line, buildrootdir);
				// System.out.println(stracefile);
				// System.out.println(line);
				if (entry != null) {
					if (SysCallTypes.NEW_PROCESS_CALLS.contains(entry.getFunc())
							|| SysCallTypes.CLONE_CALLS.contains(entry.getFunc())) {
						process.addChildProcessIDs(Long.parseLong(entry.getResult()));
					} else if (SysCallTypes.READ_CALLS.contains(entry.getFunc())) {
						String filepath = entry.getArgs().get(0);
						filepath = new File(filepath).toString();
						buildrootdir = new File(buildrootdir).toString();

						if (repofiles.contains(filepath) && !isBinaryFile(filepath)) {

							if (!filepath.endsWith(".java")) {
								FileInfo fileinfo = new FileInfo(pid, filepath, OperationType.Read, entry.getTstamp());
								process.addToInputFileList(fileinfo);
								process.addToFileAccessList(fileinfo);
							} else {
								// we will only add when .class files are
								// generated
								if (!javaclasslist.containsKey(filepath)) {
									javaclasslist.put(filepath, entry.getTstamp());
									// javaclasslist.add(filepath);
								}
							}
						}
					} else if (SysCallTypes.WRITE_CALLS.contains(entry.getFunc())) {

						String filepath = entry.getArgs().get(0);
						filepath = new File(filepath).toString();

						buildrootdir = new File(buildrootdir).toString();
						// if (repofiles.contains(filepath)) {

						if (repofiles.contains(filepath) && !isBinaryFile(filepath)) {
							FileInfo fileinfo = new FileInfo(pid, filepath, OperationType.Write, entry.getTstamp());
							process.addToOutputFileList(fileinfo);
							process.addToFileAccessList(fileinfo);
						}

						if (filepath.contains(".class")) {
							String basename = FilenameUtils.getBaseName(filepath);
							String extension = FilenameUtils.getExtension(filepath);

							String javafilename = getJavaFileFromClassName(repofiles, basename, extension);

							if (javaclasslist.containsKey(javafilename) && repofiles.contains(javafilename)) {
								FileInfo fileinforead = new FileInfo(pid, javafilename, OperationType.Read,
										javaclasslist.get(javafilename));
								process.addToInputFileList(fileinforead);
								process.addToFileAccessList(fileinforead);
								// System.out.println(filepath);
							}
						}
						// }

						if (entry.getArgs().size() > 1) {
							// For write second parameter is the text

							String writetxt = entry.getArgs().get(1);
							if (writetxt.contains("Constructor definition in wrong order")) {
								System.out.println("Constructor definition in wrong order");
								System.out.println(writetxt);
							}

							int index = writetxt.indexOf("\\n");

							if (index >= 0) {
								writetxt = writetxt.substring(0, index);
							}

							String[] lines = writetxt.split("\\r?\\n", -1);

							for (String ln : lines) {
								double writetime = entry.getTstamp();
								String cleantext = TextCleaner.CleanText(ln);

								if (cleantext.length() > 2 && cleantext.startsWith("\"") && cleantext.endsWith("\"")) {
									cleantext = cleantext.substring(1, cleantext.length() - 1);
								}
								if ((MapContains.IsMapContainsPartial(passelines, cleantext)
										|| MapContains.IsMapContainsPartial(faillines, cleantext))
										&& cleantext.length() > 0) {
									OutputEntry outentry = new OutputEntry(writetime, cleantext);
									process.addToOutputTxt(outentry);
								}
								// String
								// strfilename=getFileNameInMsg(ln,filescore);
								//
								// if(strfilename!=null)
								// {
								// filescore.IncrementFilePassedScore(fileinfo.getTracefile());
								// }
							}

						}

					}

				}
			}

			// This code is for the case where Java file is read; but .class
			// file not created due to compilatio or
			// other error
			List<FileInfo> filelist = process.getFileAccessList();
			List<String> strfilelist = new ArrayList<>();
			for (FileInfo fileinfo : filelist) {
				strfilelist.add(fileinfo.getTracefile());
			}

			Set<String> accessfiles = javaclasslist.keySet();

			for (String strfile : accessfiles) {
				if (!strfilelist.contains(strfile)) {
					if (isFailedLogContainsFileLine(faillines, strfile)) {

						FileInfo fileinforead = new FileInfo(pid, strfile, OperationType.Read,
								javaclasslist.get(strfile));
						process.addToInputFileList(fileinforead);
						process.addToFileAccessList(fileinforead);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return process;
	}

	private static boolean isFailedLogContainsFileLine(Map<String, Boolean> faillines, String file) {

		Set<String> logtext = faillines.keySet();

		for (String line : logtext) {
			if (line.contains(file))
				return true;
		}

		return false;

	}

	private static String getFileNameInMsg(String line, FileScore filescore) {
		String filename = null;

		for (String strfile : filescore.getFileScore().keySet()) {
			if (line.contains(strfile)) {
				filename = strfile;
				break;
			}
		}

		return filename;
	}

	private static BufferedReader openFile(String fileName) throws IOException {
		// Don't forget to add buffering to have better performance!
		return new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
	}

	private static Entry parse_entry(String ln, String builddir) {
		String line;
		double tracetime = 0;

		line = ln.trim();

		if (!ln.contains("=") || !ln.contains(" "))
			return null;

		String[] tokens = ln.split(" ");

		if (tokens[0].length() > 0) {
			tracetime = Double.parseDouble(tokens[0]);
		}
		// if (tokens[1].length() > 0) {
		// line = tokens[1];
		// }
		int spindex = line.indexOf(" ");
		line = line.substring(spindex);

		int index = line.lastIndexOf('=');

		if (index < 0)
			return null;

		String result = line.substring(index + 1);
		result = result.trim();
		line = line.substring(0, index - 1);

		index = line.indexOf('(');

		if (index < 0)
			return null;

		String func = line.substring(0, index);
		func = func.trim();
		int rindex = line.lastIndexOf(')');

		String strargs = line.substring(index + 1, rindex);

		String[] args = strargs.split(",");

		List<String> cleanedargs = cleanFuncArgs(args, builddir);

		Entry entry = new Entry(tracetime, result, func, cleanedargs);

		return entry;
	}

	public static List<String> cleanFuncArgs(String[] args, String buildir) {
		List<String> cleanedargs = new ArrayList<>();

		for (String arg : args) {
			arg = arg.trim();
			String cleaned = cleanline(arg, buildir);
			cleanedargs.add(cleaned);
		}

		return cleanedargs;
	}

	private static String cleanline(String args, String buildir) {
		String regx1 = "(\\d+<\\/home)";

		Pattern pattern = Pattern.compile(regx1);
		Matcher matcher = pattern.matcher(args);

		String cleanup = "";

		while (matcher.find()) {
			// cleanup=matcher.group(2);
			cleanup = args.substring(matcher.end() - 5);
			int lastindex = cleanup.lastIndexOf('>');
			cleanup = cleanup.substring(0, lastindex);
		}

		if (cleanup.length() > 0)
			return cleanup;
		else
			return args;
	}

	public static void main(String args[]) {

		String str = "18<//home/foyzulhassan//.gradle//caches//journal-1//file-access.properties>";

		String ret = cleanline(str, "build");

		System.out.println(ret);
	}

	public static ProcessInfo getBuildProcessGraph(long pid, Map<Long, ProcessInfo> processInfoMap) {
		Map<Long, ProcessInfo> processInfoMapWithDep = new HashMap<>();
		for (long procid : processInfoMap.keySet()) {
			List<Long> pidlist = processInfoMap.get(procid).getChildProcessIDs();

			ProcessInfo process = processInfoMap.get(procid);
			for (long childpid : pidlist) {
				process.addChildProcess(processInfoMap.get(childpid));
			}

			processInfoMapWithDep.put(procid, process);
		}

		return processInfoMapWithDep.get(pid);
	}

	public static List<FileInfo> getDependencyFileList(ProcessInfo rootprocess, FileScore filescore,
			Map<String, Boolean> passedlines, Map<String, Boolean> failedlines, boolean updatescore) {
		List<FileInfo> fileinfolist = new ArrayList<>();
		Map<String, Boolean> localpassed = passedlines;
		Map<String, Boolean> localfailedlines = failedlines;

		Queue<ProcessInfo> q = new LinkedList<>();
		List<Long> visited = new ArrayList<>();

		q.add(rootprocess);
		visited.add(rootprocess.getProcessPID());
		int i = 0;
		try {
			while (!q.isEmpty()) {

				ProcessInfo process = q.poll();

				if (updatescore) {
					updateFileScore(process, filescore, localpassed, localfailedlines);
				}

				List<ProcessInfo> childprocs = process.getChildProcessList();

				for (ProcessInfo proc : childprocs) {
					if (!visited.contains(proc.getProcessPID())) {

						// This part if for propagating parent processes file
						// list
						// to child process to get file score
						List<FileInfo> parentinputfiles = process.getInputFiles();
						for (FileInfo finfo : parentinputfiles) {
							proc.addToFileFromParentAndCurr(finfo);
						}

						q.add(proc);
					}
				}

				for (FileInfo file : process.getInputFiles()) {
					fileinfolist.add(file);
				}
				i++;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return fileinfolist;
	}

	public static String separatorsToSystem(String res) {
		if (res == null)
			return null;
		if (File.separatorChar == '\\') {
			// From Windows to Linux/Mac
			return res.replace('/', File.separatorChar);
		} else {
			// From Linux/Mac to Windows
			return res.replace('\\', File.separatorChar);
		}
	}

	public static void updateFileScore(ProcessInfo process, FileScore filescore, Map<String, Boolean> passedlines,
			Map<String, Boolean> failedlines) {

		for (OutputEntry output : process.getOutputText()) {
			if (MapContains.IsMapContainsPartial(passedlines, output.getStrMsg())) {
				List<FileInfo> depfile = process.getFileFromParentAndCurr();

				for (FileInfo fileinfo : depfile) {
					if (fileinfo.getAccessTime() < output.getMsgWriteTime()
							&& passedlines.get(MapContains.GetContainsKey(passedlines, output.getStrMsg())) == false) {
						filescore.IncrementFilePassedScore(fileinfo.getTracefile());
						if (output.getStrMsg().contains(fileinfo.getTracefile())) {
							for (int i = 0; i < 10; i++) {
								filescore.IncrementFilePassedScore(fileinfo.getTracefile());
							}
						}
						// passedlines.put(fileinfo.getTracefile(), true);
					}
				}
			} else if (MapContains.IsMapContainsPartial(failedlines, output.getStrMsg())) {
				List<FileInfo> depfile = process.getFileFromParentAndCurr();

				for (FileInfo fileinfo : depfile) {
					if (fileinfo.getAccessTime() < output.getMsgWriteTime()
							&& failedlines.get(MapContains.GetContainsKey(failedlines, output.getStrMsg())) == false) {
						filescore.IncrementFileFailedScore(fileinfo.getTracefile());

						if (output.getStrMsg().contains(fileinfo.getTracefile())) {
							filescore.IncrementFileFailedScoreByValue(fileinfo.getTracefile(), 10);
							// filescore.DecrementFilePassedScore(fileinfo.getTracefile(),10);
						}
						// failedlines.put(fileinfo.getTracefile(), true);
					}
				}

			}
		}
	}

	// public String getFileNameForMsg()

	public static boolean isBinaryFile(String strf) throws FileNotFoundException, IOException {
		File f = new File(strf);
		FileInputStream in = new FileInputStream(f);
		int size = in.available();
		if (size > 1024)
			size = 1024;
		byte[] data = new byte[size];
		in.read(data);
		in.close();

		int ascii = 0;
		int other = 0;

		for (int i = 0; i < data.length; i++) {
			byte b = data[i];
			if (b < 0x09)
				return true;

			if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D)
				ascii++;
			else if (b >= 0x20 && b <= 0x7E)
				ascii++;
			else
				other++;
		}

		if (other == 0)
			return false;

		return 100 * other / (ascii + other) > 95;
	}

	private static String getJavaFileFromClassName(List<String> repofiles, String classname, String extension) {
		String filename = "";

		for (String strfile : repofiles) {
			String basename = FilenameUtils.getBaseName(strfile);
			String repoextension = FilenameUtils.getExtension(strfile);
			if (basename.equals(classname) && extension.equals("class") && repoextension.equals("java")) {
				filename = strfile;
				return filename;
			}
		}

		return filename;
	}

}
