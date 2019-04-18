package com.build.strace.buildexe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;


public class CmdExecutor {
    private LogPrinter infologprinter;
    private LogPrinter errorlogprinter;
    
    
    public Map<String,Boolean> getPassedLines()
    {
    	if(infologprinter!=null)
    		return infologprinter.getLineMaps();
    	else
    		return null;
    }
    
    public Map<String,Boolean> getFailedLines()
    {
    	if(errorlogprinter!=null)
    		return errorlogprinter.getLineMaps();
    	else
    		return null;
    }
    

    public CmdExecutor(String path) {
    	infologprinter = new LogPrinter(false);
    	errorlogprinter= new LogPrinter(true);
    }

    public CmdExecutor(String path, boolean infocmd) {
    	infologprinter = new LogPrinter(false);
    	errorlogprinter= new LogPrinter(true);
    }

    public CmdExecutor() {
    	infologprinter = new LogPrinter(false);
    	errorlogprinter= new LogPrinter(true);
    }

    public boolean ExecuteCommand(String executionpath, String cmd,
	    String restorepath) {
	boolean ret = false;
	String cmdtoexecute = "";
	StringBuffer output = new StringBuffer();

//	if (cmd.startsWith("gradlew ")) {
//	    if (!cmd.startsWith("./"))
//		cmdtoexecute = "./" + cmd;
//	    else
//		cmdtoexecute = cmd;
//	} else {
//	    //cmdtoexecute = "./gradlew build";
//		cmdtoexecute=cmd;
//	}
	cmdtoexecute=cmd;

	String[] cmds = cmdtoexecute.split(" ");

	File exefolder = new File(executionpath);

	Process p;

	try {
	    p = Runtime.getRuntime().exec(cmds, null, exefolder);
	    
	    ProcessHandler inputStream = new ProcessHandler(p.getInputStream(),
			    "INPUT", this.infologprinter);
		    ProcessHandler errorStream = new ProcessHandler(p.getErrorStream(),
			    "ERROR", this.errorlogprinter);

	    /* start the stream threads */
	    inputStream.start();
	    errorStream.start();

	    inputStream.join(2000000);
	    errorStream.join(2000000);

	} catch (Exception e) {
	    e.printStackTrace();
	}

	return ret;
    }

    public boolean ExecuteGitCommand(String executionpath, String cmd,
	    String restorepath) {
	boolean ret = false;
	String cmdtoexecute = "";
	StringBuffer output = new StringBuffer();

	if (cmd.contains("gradlew ")) {
	    if (!cmd.startsWith("./"))
		cmdtoexecute = "./" + cmd;
	    else
		cmdtoexecute = cmd;
	} else {
	    cmdtoexecute = cmd;
	}

	String[] cmds = cmdtoexecute.split(" ");

	File exefolder = new File(executionpath);

	Process p;

	try {
	    p = Runtime.getRuntime().exec(cmdtoexecute, null, exefolder);

	    ProcessHandler inputStream = new ProcessHandler(p.getInputStream(),
		    "INPUT", this.infologprinter);
	    ProcessHandler errorStream = new ProcessHandler(p.getErrorStream(),
		    "ERROR", this.errorlogprinter);

	    /* start the stream threads */
	    inputStream.start();
	    errorStream.start();

	    inputStream.join(2000000);
	    errorStream.join(2000000);

	} catch (Exception e) {
	    e.printStackTrace();
	}

	return ret;
    }

    public boolean ExecuteCommand(String executionpath, String cmd,
	    String restorepath, boolean infocmd) {
	boolean ret = false;
	String cmdtoexecute = "";
	StringBuffer output = new StringBuffer();

	if (cmd.contains("gradlew ")) {
	    if (!cmd.startsWith("./"))
		cmdtoexecute = "./" + cmd;
	    else
		cmdtoexecute = cmd;
	} else {
	    cmdtoexecute = cmd;
	}

	String[] cmds = cmdtoexecute.split(" ");

	File exefolder = new File(executionpath);

	Process p;

	try {
	    p = Runtime.getRuntime().exec(cmds, null, exefolder);

	    ProcessHandler inputStream = new ProcessHandler(p.getInputStream(),
		    "INPUT", this.infologprinter);
	    ProcessHandler errorStream = new ProcessHandler(p.getErrorStream(),
		    "ERROR", this.errorlogprinter);

	    /* start the stream threads */
	    inputStream.start();
	    errorStream.start();

	    inputStream.join(2000000);
	    errorStream.join(2000000);

	} catch (Exception e) {
	    e.printStackTrace();
	}

	return ret;
    }

    public static String GetCurrentPath() {
	String pathto = "";

	Process p;
	try {
	    p = Runtime.getRuntime().exec("sh -c pwd");
	    p.waitFor();
	    BufferedReader reader = new BufferedReader(
		    new InputStreamReader(p.getInputStream()));

	    String line = "";

	    while ((line = reader.readLine()) != null) {
		pathto = line;
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}

	return pathto;

    }

}
