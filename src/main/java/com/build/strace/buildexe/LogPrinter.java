package com.build.strace.buildexe;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.build.strace.text.TextCleaner;

public class LogPrinter {
	private Map<String, Boolean> lineMaps;
	private boolean infocmd;
	int failcount=0;
	int passcount=0;

	public Map<String, Boolean> getLineMaps() {
		return lineMaps;
	}

	public void setLineMaps(Map<String, Boolean> lineMaps) {
		this.lineMaps = lineMaps;
	}

	private LogPrinter() {
		lineMaps = new HashMap<>();
	}

	public LogPrinter(boolean flag) {
		lineMaps = new HashMap<>();
		this.infocmd = flag;
	}

	public void println(String txt) {
		
		System.out.println(txt);

		if (this.infocmd) {

			System.out.println("Error=>"+txt);
			String strlow = txt.toLowerCase();

			if (strlow.contains("error") || strlow.contains("fail") || strlow.contains("exception")
					|| strlow.contains("wrong") || !strlow.contains("warning")) {

				String cleantxt = TextCleaner.CleanText(txt);

				if (!lineMaps.containsKey(cleantxt)) {
					lineMaps.put(cleantxt, false);
					failcount++;
				}
			}

		} else {

			String cleantxt = TextCleaner.CleanText(txt);
			System.out.println("Passed=>"+txt);

			if (!lineMaps.containsKey(cleantxt)) {
				lineMaps.put(cleantxt, false);
				passcount++;
			}
		}
		
		//System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^Fail Count "+failcount);
		//System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^Pass Count "+passcount);

	}

}
