package com.buildlogparser.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.build.logdiff.LogDiff;

public abstract class BaseLogParser {

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

	public BaseLogParser() {

	}

	public BaseLogParser(String logtype, String logfile) {
		this.buildLogFile = logfile;
		this.buildType = logtype;
	}

	public String getFullBuildLog() {

		StringBuilder strbuild = new StringBuilder();

		try {
			File file = new File(this.buildLogFile);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				// replace all no ASCI Characters
				line = line.replaceAll("\u001B\\[[\\d;]*[^\\d;]", "");
				line = line.replaceFirst("> Loading", "");
				line = line.replaceAll("[^\\x00-\\x7F]", "");

				if (line.length() < 3000) {
					strbuild.append(line);
					strbuild.append("\n");
				}
			}

			fileReader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return strbuild.toString();
	}
	
	public String getDifferentialBuildLog(String passlogfile, String faillogfile) {

		List<String> passtextlist = new ArrayList<String>();
		List<String> failtextlist = new ArrayList<String>();

		try {
			File filepass = new File(passlogfile);
			FileReader fileReader = new FileReader(filepass);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				// replace all no ASCI Characters
				line = line.replaceAll("\u001B\\[[\\d;]*[^\\d;]", "");
				line = line.replaceFirst("> Loading", "");
				line = line.replaceAll("[^\\x00-\\x7F]", "");

				// If line length is over 3000 character we can consider those
				// as noise
				if (line.length() < 3000) {
					passtextlist.add(line);
				}
			}

			fileReader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			File filefail = new File(faillogfile);
			FileReader fileReader = new FileReader(filefail);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;

			while ((line = bufferedReader.readLine()) != null) {
				// replace all no ASCI Characters
				line = line.replaceAll("\u001B\\[[\\d;]*[^\\d;]", "");
				line = line.replaceFirst("> Loading", "");
				line = line.replaceAll("[^\\x00-\\x7F]", "");

				// If line length is over 3000 character we can consider those
				// as noise
				if (line.length() < 3000) {
					failtextlist.add(line);
				}
			}

			fileReader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String str=LogDiff.getLogDiff(passtextlist, failtextlist);

		return str;
	}

	public abstract List<String> getBuildErrors();

}
