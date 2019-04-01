package com.build.strace.entity;

public class FileInfo {
	private long pid;
	private String tracefile;
	private OperationType opCode; 
	private double accessTime;


	public FileInfo(long id)
	{
		this.pid=id;		
	}
	
	public FileInfo(long id, String path)
	{
		this.pid=id;
		this.tracefile=path;
	}
	
	public FileInfo(long id, String path,OperationType opcode)
	{
		this.pid=id;
		this.tracefile=path;
		this.opCode=opcode;
	}
	public FileInfo(long id, String path,OperationType opcode,double accesstime)
	{
		this.pid=id;
		this.tracefile=path;
		this.opCode=opcode;
		this.accessTime=accesstime;
	}
	
	public long getPid() {
		return pid;
	}
	public void setPid(long pid) {
		this.pid = pid;
	}
	public String getTracefile() {
		return tracefile;
	}
	public void setTracefile(String tracefile) {
		this.tracefile = tracefile;
	}
	
	public OperationType getOpCode() {
		return opCode;
	}

	public void setOpCode(OperationType opCode) {
		this.opCode = opCode;
	}
	
	
	public double getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(double accessTime) {
		this.accessTime = accessTime;
	}

}
