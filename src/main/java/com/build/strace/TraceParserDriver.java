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
		String inputdir="/home/foyzulhassan/Research/Strace_Implementation/builddir/gradle-build-scan-quickstart/teststrace/";
		String builddir="/home/foyzulhassan/Research/Strace_Implementation/builddir/gradle-build-scan-quickstart/";
		parser.parseRawTraces(inputdir,builddir);
		

	}

}
