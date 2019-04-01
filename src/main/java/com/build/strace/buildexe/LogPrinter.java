package com.build.strace.buildexe;

import java.io.File;

public class LogPrinter {

	private String path = null;
	private String infologfilename = null;
	private boolean isInfoCmd = false;

	public LogPrinter() {
		this.infologfilename = "buildtrace.log";
	}

	public LogPrinter(String path) {

		this.path = path;
		this.infologfilename = "buildtrace.log";

		try {

			String tempFile = this.path + "//" + "buildtrace.log";
			// Delete if tempFile exists
			File fileTemp = new File(tempFile);
			if (fileTemp.exists()) {
				fileTemp.delete();
			}
		} catch (Exception e) {
			// if any error occurs
			e.printStackTrace();
		}

	}

	public LogPrinter(String path, boolean infocmd) {

		this.path = path;
		String tempFile = "";

		try {

			if (infocmd == true) {
				tempFile = this.path + "//" + this.infologfilename;
				File fileTemp = new File(tempFile);
				if (fileTemp.exists()) {
					fileTemp.delete();
				}
			} else {
				tempFile = this.path + "//" + this.infologfilename;
				File fileTemp = new File(tempFile);
				if (fileTemp.exists()) {
					fileTemp.delete();
				}
			}
			// Delete if tempFile exists

		} catch (Exception e) {
			// if any error occurs
			e.printStackTrace();
		}

	}

	public void println(String txt) {

		System.out.println(txt);

	}

}
