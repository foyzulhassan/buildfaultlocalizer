package com.build.strace.buildexe;

public class TestCommandExecution {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path="E:\\Research_Works\\trace_analysis\\analysis_projects\\gradle-build-scan-quickstart\\";
		String cmdstr="E:\\Research_Works\\trace_analysis\\analysis_projects\\gradle-build-scan-quickstart\\gradlew.bat clean build test";
		
		CmdExecutor cmd=new CmdExecutor(path);
		cmd.ExecuteCommand(path, cmdstr, path);
		

	}

}
