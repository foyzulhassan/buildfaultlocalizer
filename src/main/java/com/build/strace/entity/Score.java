package com.build.strace.entity;

public class Score {
	private int passedcount;
	private int failedcount;
	
	public Score()
	{
		this.passedcount=0;
		this.failedcount=0;
	}
	
	public int getPassedcount() {
		return passedcount;
	}

	public void setPassedcount(int passedcount) {
		this.passedcount = passedcount;
	}

	public int getFailedcount() {
		return failedcount;
	}

	public void setFailedcount(int failedcount) {
		this.failedcount = failedcount;
	}
	
	public void incrementPassed()
	{
		this.passedcount++;
	}
	
	public void incrementFailed()
	{
		this.failedcount++;
	}
	
	public void incrementFailedByValue(int value)
	{
		for(int i=0;i<value;i++)
		{
			this.failedcount++;
		}
	}
	
}
