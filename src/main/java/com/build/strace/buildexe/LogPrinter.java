package com.build.strace.buildexe;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.build.strace.text.TextCleaner;

public class LogPrinter {
	private Map<String,Boolean> lineMaps;
	
	public Map<String, Boolean> getLineMaps() {
		return lineMaps;
	}

	public void setLineMaps(Map<String, Boolean> lineMaps) {
		this.lineMaps = lineMaps;
	}

	public LogPrinter() {
		lineMaps=new HashMap<>();
	}
	
	public void println(String txt) {

		//if(isInfoCmd) 
			//System.out.println(txt);
		
		String cleantxt=TextCleaner.CleanText(txt);
		
		if(!lineMaps.containsKey(cleantxt))
		{
			lineMaps.put(cleantxt, false);
		}

	}

}
