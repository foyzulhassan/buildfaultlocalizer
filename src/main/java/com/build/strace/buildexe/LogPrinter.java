package com.build.strace.buildexe;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.build.strace.text.TextCleaner;

public class LogPrinter {
	private Map<String,Boolean> lineMaps;
	private boolean infocmd;	
	
	public Map<String, Boolean> getLineMaps() {
		return lineMaps;
	}

	public void setLineMaps(Map<String, Boolean> lineMaps) {
		this.lineMaps = lineMaps;
	}

	private LogPrinter() {
		lineMaps=new HashMap<>();
	}
	
	
	public LogPrinter(boolean flag) {
		lineMaps=new HashMap<>();
		this.infocmd=flag;
	}
	
	public void println(String txt) {

		if(this.infocmd) {
		
			if(txt.contains("param name not found"))
			{
				System.out.println(txt);
			}
			System.out.println(txt);
			
		}
		
		String cleantxt=TextCleaner.CleanText(txt);
		
		if(!lineMaps.containsKey(cleantxt))
		{
			lineMaps.put(cleantxt, false);
		}

	}

}
