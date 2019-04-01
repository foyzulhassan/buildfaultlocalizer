package com.build.strace;

//Class to test newly implemented Strace Parser
public class TraceParserDriver {

	public static void main(String[] args)
	{
		TraceParser parser=new TraceParser();
		String inputdir="C:\\Users\\foyzul\\Desktop\\strace\\May30_20";
		String builddir="/home/foyzulhassan/Desktop/strace/analysis_projects/gradle-build-scan-quickstart/";
		parser.parseRawTraces(inputdir,builddir);
	}

}
