package com.build.strace.text;

public class TextCleaner {
	
	public static String CleanText(String line)
	{
		String cleantext="";
		
		cleantext=line.replaceAll("[^a-zA-Z\\s+]","");
		
		return cleantext;
	}
	
	public static void main(String[] args)
	{
		String test="Execution failed for task ':test'.";
		String cleantext=CleanText(test);
		
		System.out.println(cleantext);
	}

}
