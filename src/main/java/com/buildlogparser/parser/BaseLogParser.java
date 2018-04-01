package com.buildlogparser.parser;

import java.util.List;

public abstract class  BaseLogParser {	
	
	protected String buildLogFile;
	protected String buildType;
	
	public String getBuildLogFile() {
		return buildLogFile;
	}



	public void setBuildLogFile(String buildLogFile) {
		this.buildLogFile = buildLogFile;
	}



	public String getBuildType() {
		return buildType;
	}



	public void setBuildType(String buildType) {
		this.buildType = buildType;
	}
	
	public BaseLogParser()
	{
		
	}
	
	public BaseLogParser(String logtype, String logfile)
	{
		this.buildLogFile=logfile;
		this.buildType=logtype;
	}	
	
	public abstract List<String> getBuildErrors();

}
