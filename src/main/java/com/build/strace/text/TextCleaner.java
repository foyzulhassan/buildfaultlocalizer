package com.build.strace.text;

public class TextCleaner {
	
	public static String CleanText(String line)
	{
		String cleantext="";
		line=line.trim();
		//cleantext=line.replaceAll("[^a-zA-Z\\s+]","");
		cleantext=line;//.replaceAll("[0-9]", "");

		
		cleantext = cleantext.replaceAll("[^\\x00-\\x7F]", "");
		//cleantext = cleantext.replaceAll("[^\\x00-\\x7F]", "");
	    
	    // erases all the ASCII control characters
		cleantext = cleantext.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
	     
	    // removes non-printable characters from Unicode
		cleantext = cleantext.replaceAll("\\p{C}", "");
		cleantext=cleantext.replaceAll("\\[28D\\[0K", "");
		cleantext=cleantext.replaceAll("\\[1m", "");
		cleantext=cleantext.replaceAll("\\[22m", "");
		cleantext=cleantext.replaceAll("\\[15D\\[0K", "");
		cleantext=cleantext.replaceAll("\\[13D\\[0K", "");
	    
		
		cleantext=cleantext.trim();
		return cleantext;
	}
	
	public static void main(String[] args)
	{
		String test="\33[28D\33[0K/home/foyzulhassan/Research/Strace_Implementation/builddir/SpongePowered@SpongeAPI/src/main/java/org/spongepowered/api/";
		final String msgWithoutColorCodes =
			    test.replaceAll("\u001B\\[[;\\d]*m", "");
		String cleantext=CleanText(test);
		
		test="@param name not found\n\t * @param filer an IStackFilter to match against\n\t          ^\n";
		
		String[] lines=test.split("\n");
		
		for(String ln:lines)
		{
			//System.out.println(ln);
		}
		
		//System.out.println(test);
		
		System.out.println(cleantext);
		System.out.println(msgWithoutColorCodes);
	}

}
