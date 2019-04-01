package com.build.strace;

import java.util.*;

public class TraceListEntity {
	
	private long rootpid;
	private Map<Long,String> traces;
	
	public TraceListEntity()
	{
		traces=new HashMap<>();
	}
	
	public long getRootpid() {
		return rootpid;
	}
	public void setRootpid(long rootpid) {
		this.rootpid = rootpid;
	}
	
	public void addTraces(long pid,String filepath)
	{
		if(!traces.containsKey(pid))
			traces.put(pid, filepath);
	}
	
	public Map<Long, String> getTraces() {
		return traces;
	}
	
	
	
}
