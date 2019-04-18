package com.build.strace.text;

public class TextCleaner {
	
	public static String CleanText(String line)
	{
		String cleantext="";
		line=line.trim();
		cleantext=line.replaceAll("[^a-zA-Z\\s+]","");
		
		return cleantext;
	}
	
	public static void main(String[] args)
	{
		String test="Execution failed for task ':test'.";
		String cleantext=CleanText(test);
		
		test="@param name not found\n\t * @param filer an IStackFilter to match against\n\t          ^\n";
		
		String[] lines=test.split("\n");
		
		for(String ln:lines)
		{
			System.out.println(ln);
		}
		
		System.out.println(test);
		
		System.out.println(cleantext);
	}

}
