package com.build.strace;

import com.build.strace.entity.Entry;
import com.build.strace.entity.FileInfo;
import com.build.strace.entity.OperationType;
import com.build.strace.entity.ProcessInfo;
import com.build.strace.entity.SysCallTypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseProcessFiles {

	public static ProcessInfo getProcessNode(long pid, Map<Long, String> tracefilemap, String buildrootdir) {
		ProcessInfo process = new ProcessInfo(pid);

		if (!tracefilemap.containsKey(pid))
			return null;

		String stracefile = tracefilemap.get(pid);

		try (BufferedReader input = openFile(stracefile)) {
			String line = "";
			while ((line = input.readLine()) != null) {
				Entry entry = parse_entry(line,buildrootdir);
				//System.out.println(stracefile);
				//System.out.println(line);
				if (entry != null) {
					if (SysCallTypes.NEW_PROCESS_CALLS.contains(entry.getFunc())
							|| SysCallTypes.CLONE_CALLS.contains(entry.getFunc())) {
						process.addChildProcessIDs(Long.parseLong(entry.getResult()));
					} else if (SysCallTypes.READ_CALLS.contains(entry.getFunc())) {
						String filepath = entry.getArgs().get(0);
						filepath=new File(filepath).toString();
						buildrootdir=new File(buildrootdir).toString();
						if (filepath.contains(buildrootdir)) {
							FileInfo fileinfo = new FileInfo(pid, filepath, OperationType.Read, entry.getTstamp());
							process.addToInputFileList(fileinfo);
							process.addToFileAccessList(fileinfo);
						}
					} else if (SysCallTypes.WRITE_CALLS.contains(entry.getFunc())) {
						String filepath = entry.getArgs().get(0);
						filepath=new File(filepath).toString();
						buildrootdir=new File(buildrootdir).toString();
						if (filepath.contains(buildrootdir)) {

							FileInfo fileinfo = new FileInfo(pid, filepath, OperationType.Read, entry.getTstamp());
							process.addToOutputFileList(fileinfo);
							process.addToFileAccessList(fileinfo);
						}
						else
						{
							System.out.println(entry.getArgs());
						}
					}
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return process;
	}

	private static BufferedReader openFile(String fileName) throws IOException {
		// Don't forget to add buffering to have better performance!
		return new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8));
	}

	private static Entry parse_entry(String ln,String builddir) {
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
		
		if(index<0)
			return null;

		String result = line.substring(index + 1);
		result=result.trim();
		line = line.substring(0, index - 1);

		index = line.indexOf('(');
		
		if(index<0)
			return null;

		String func = line.substring(0, index);
		func = func.trim();
		int rindex = line.lastIndexOf(')');
		
		

		String strargs = line.substring(index + 1, rindex);

		String[] args = strargs.split(",");

		List<String> cleanedargs = cleanFuncArgs(args,builddir);

		Entry entry = new Entry(tracetime, result, func, cleanedargs);

		return entry;
	}

	public static List<String> cleanFuncArgs(String[] args,String buildir) {
		List<String> cleanedargs = new ArrayList<>();

		for (String arg : args) {
			arg = arg.trim();
			String cleaned = cleanline(arg,buildir);
			cleanedargs.add(cleaned);
		}

		return cleanedargs;
	}

	private static String cleanline(String args,String buildir) {
		String regx1 = "(\\d+<\\/home)";
		
		Pattern pattern = Pattern.compile(regx1);
		Matcher matcher = pattern.matcher(args);

		String cleanup = "";

		while (matcher.find()) {
			// cleanup=matcher.group(2);
			cleanup = args.substring(matcher.end()-5);
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

		String ret = cleanline(str,"build");

		System.out.println(ret);
	}

	public static ProcessInfo getBuildProcessGraph(long pid, Map<Long, ProcessInfo> processInfoMap) {
		Map<Long, ProcessInfo> processInfoMapWithDep=new HashMap<>();
		for (long procid : processInfoMap.keySet()) {
			List<Long> pidlist = processInfoMap.get(procid).getChildProcessIDs();
			
			if(procid==3437)
			{
				System.out.println("test");
			}

			ProcessInfo process=processInfoMap.get(procid);
			for (long childpid : pidlist) {				
				process.addChildProcess(processInfoMap.get(childpid));
			}
			
			processInfoMapWithDep.put(procid, process);
		}

		return processInfoMapWithDep.get(pid);
	}

	public static List<FileInfo> getDependencyFileList(ProcessInfo rootprocess) {
		List<FileInfo> fileinfolist = new ArrayList<>();

		Queue<ProcessInfo> q = new LinkedList<>();
		List<Long> visited = new ArrayList<>();

		q.add(rootprocess);
		visited.add(rootprocess.getProcessPID());

		while (!q.isEmpty()) {
			ProcessInfo process = q.poll();

			List<ProcessInfo> childprocs = process.getChildProcessList();

			for (ProcessInfo proc : childprocs) {
				if (!visited.contains(proc.getProcessPID())) {
					q.add(proc);
				}
			}

			for (FileInfo file : process.getInputFiles()) {
				fileinfolist.add(file);
			}
		}

		return fileinfolist;
	}
	
	public static String separatorsToSystem(String res) {
	    if (res==null) return null;
	    if (File.separatorChar=='\\') {
	        // From Windows to Linux/Mac
	        return res.replace('/', File.separatorChar);
	    } else {
	        // From Linux/Mac to Windows
	        return res.replace('\\', File.separatorChar);
	    }
	}

}
