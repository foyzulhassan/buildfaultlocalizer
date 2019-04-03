package com.build.strace;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;

//Class to test newly implemented Strace Parser
public class TraceParserDriver {

	public static void main(String[] args)
	{
		TraceParser parser=new TraceParser();
		String inputdir="C:\\Users\\foyzul\\Desktop\\strace\\April1\\May30_21_2";
		String builddir="/home/foyzulhassan/Desktop/strace/analysis_projects/gradle-build-scan-quickstart/";
		parser.parseRawTraces(inputdir,builddir);
		

	}

}
