package com.build.strace;

import java.io.File;
import java.util.*;

import com.build.strace.entity.FileInfo;
import com.build.strace.entity.FileScore;
import com.build.strace.entity.ProcessInfo;

public class TraceParser {
	
	public List<TraceListEntity> processedPIDs=new ArrayList<>();
	public List<ProcessInfo> listOfProcess=new ArrayList<>();
	public Map<Long,ProcessInfo> processInfoMap=new HashMap<>();
	private Map<String,Boolean> passedLines;
	private Map<String, Boolean> failedLines;
	
	private TraceParser()
	{
		
	}
	
	public TraceParser(Map<String,Boolean> passlines, Map<String,Boolean> faillines)
	{
		passedLines=passlines;
		failedLines=faillines;
	}
	
	public List<FileInfo> parseRawTraces(String inputdir,String builddir,List<String> repofiles, FileScore filescore,boolean updatescore) {
		TraceListEntity tracelist;

//		//Create output directory if not exists
//		File directory = new File(outputdir);
//		if (!directory.exists()) {
//			directory.mkdir();
//		}

		tracelist = TraceValidator.getTracesAndRootPid(inputdir);
		
		Map<Long,String> pidtracefile=tracelist.getTraces();
		
		ProcessInfo process=ParseProcessFiles.getProcessNode(tracelist.getRootpid(),pidtracefile,builddir,passedLines,failedLines,repofiles,filescore);
		
	    processInfoMap.put(tracelist.getRootpid(), process);
		
		for(long pid:pidtracefile.keySet())
		{
			if(!processInfoMap.containsKey(pid))
			{
				ProcessInfo proc=ParseProcessFiles.getProcessNode(pid,pidtracefile,builddir,passedLines,failedLines,repofiles,filescore);				
				if(proc!=null)
				{
					processInfoMap.put(pid, proc);
				}
			}
		}
		
		ProcessInfo rootprocess=ParseProcessFiles.getBuildProcessGraph(tracelist.getRootpid(),processInfoMap);
		List<FileInfo> dependencyfiles=ParseProcessFiles.getDependencyFileList(rootprocess,filescore,passedLines,failedLines,updatescore);
		
//		for(FileInfo file:dependencyfiles)
//		{
//			if(file.getTracefile().contains(".java")||file.getTracefile().contains(".class"))
//			{
//				System.out.println(file.getOpCode()+"====>"+file.getPid()+"==>"+file.getTracefile());
//		
//			}
//		}
		
		return dependencyfiles;
	}

}
