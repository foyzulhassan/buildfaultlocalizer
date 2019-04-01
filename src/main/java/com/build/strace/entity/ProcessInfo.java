package com.build.strace.entity;

import java.util.ArrayList;
import java.util.List;

public class ProcessInfo {
	private long processPID;	
	private List<Long> childProcessIDs;
	private List<ProcessInfo> childProcessList;
	private List<FileInfo> fileAccessList;
	private List<FileInfo> inputFiles;
	private List<FileInfo> outputFiles;
	
	
	public ProcessInfo(long pid)
	{
		this.processPID=pid;
		childProcessIDs=new ArrayList<>();
		childProcessList=new ArrayList<>();
		fileAccessList=new ArrayList<>();
		inputFiles=new ArrayList<>();
		outputFiles=new ArrayList<>();		
	}
	
	public long getProcessPID() {
		return processPID;
	}


	public void setProcessPID(long processPID) {
		this.processPID = processPID;
	}


	public List<Long> getChildProcessIDs() {
		return childProcessIDs;
	}


	public void setChildProcessIDs(List<Long> childProcessIDs) {
		this.childProcessIDs = childProcessIDs;
	}
	
	public void addChildProcessIDs(long pid)
	{
		childProcessIDs.add(pid);
	}


	public List<ProcessInfo> getChildProcessList() {
		return childProcessList;
	}


	public void setChildProcessList(List<ProcessInfo> childProcessList) {
		this.childProcessList = childProcessList;
	}
	
	public void addChildProcess(ProcessInfo process)
	{
		childProcessList.add(process);
	}

	public void addChildProcess(List<ProcessInfo> processes)
	{
		childProcessList.addAll(processes);
	}


	public List<FileInfo> getFileAccessList() {
		return fileAccessList;
	}


	public void setFileAccessList(List<FileInfo> fileAccessList) {
		this.fileAccessList = fileAccessList;
	}
	
	public void addToFileAccessList(FileInfo file)
	{
		for(FileInfo in:fileAccessList)
		{
			if(in.getTracefile().equals(file.getTracefile()))
			{
				return;
			}
		}
		fileAccessList.add(file);
	}


	public List<FileInfo> getInputFiles() {
		return inputFiles;
	}


	public void setInputFiles(List<FileInfo> inputFiles) {
		this.inputFiles = inputFiles;
	}
	
	public void addToInputFileList(FileInfo file)
	{
		
		for(FileInfo in:inputFiles)
		{
			if(in.getTracefile().equals(file.getTracefile()))
			{				
				return;
			}
		}
		
		inputFiles.add(file);
		
	}


	public List<FileInfo> getOutputFiles() {
		
		return outputFiles;
	}


	public void setOutputFiles(List<FileInfo> outputFiles) {
		this.outputFiles = outputFiles;
	}

	public void addToOutputFileList(FileInfo file)	{

		for(FileInfo in:outputFiles)
		{
			if(in.getTracefile().equals(file.getTracefile()))
			{
				return;
			}
		}
		outputFiles.add(file);
	}	
}
