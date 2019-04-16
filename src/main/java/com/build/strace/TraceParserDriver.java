package com.build.strace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringEscapeUtils;

//Class to test newly implemented Strace Parser
public class TraceParserDriver {

	public static void main(String[] args)
	{
		TraceParser parser=new TraceParser(null,null);
		String inputdir="/home/foyzulhassan/Research/Strace_Implementation/builddir/gradle-build-scan-quickstart/teststrace/";
		String builddir="/home/foyzulhassan/Research/Strace_Implementation/builddir/gradle-build-scan-quickstart/";
		List<String> repofiles=new ArrayList<>();
		parser.parseRawTraces(inputdir,builddir,repofiles,null,true);
		

	}

}
