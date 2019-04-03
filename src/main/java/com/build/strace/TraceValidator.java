package com.build.strace;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TraceValidator {
	
	public static TraceListEntity getTracesAndRootPid(String input_dir)
	{
		TraceListEntity tracelist=new TraceListEntity();

	    double oldest_ts =-1.0;
	    long oldest_pid = -1;
	    
	    File folder = new File(input_dir);
	    
	    File[] listOfFiles = folder.listFiles();

	    for (int i = 0; i < listOfFiles.length; i++)
	    {
	      if (listOfFiles[i].isFile())
	      {
	         String path = listOfFiles[i].getPath();
	        
	         if(path.contains(".git")||path.contains(".svn") || path.contains(".hg"))
	             continue;
	         
	         int index=path.lastIndexOf('.');
	         
	         String strpid=path.substring(index+1);
	         
	         long tracepid=Long.parseLong(strpid);
	         
	         try
	         {
	         FileReader fileReader = new FileReader(path);	     
	         if(path.contains("3344"))
	         {
	        	 System.out.println("small");
	         }
	         BufferedReader bufferedReader = new BufferedReader(fileReader);
	           String line;
	           while((line = bufferedReader.readLine()) != null) {
	        	   String[] tokens=line.split(" ");
	        	   
	        	   if(tokens!=null && tokens.length>0)
	        	   {
	        		   double ts=Double.parseDouble(tokens[0]);
	        		   
	        		   if(oldest_ts>=0.0)
	        		   {
	        			   if(ts<oldest_ts)
	        			   {
	                           oldest_ts = ts;
	                           oldest_pid = tracepid; 		
	        			   }
	        		   }
	        		   else
	        		   {
	        			   oldest_ts = ts;
	        	           oldest_pid = tracepid;
	        	       }
	        		   
	        		   break;
	        	   }
	           }
	           fileReader.close(); 
	         }catch(Exception e)
	         {
	        	 System.out.println(e.getMessage());
	         }
	        
	        
	         tracelist.addTraces(tracepid, path);
	      }
	    }
	    
	    
	    Map<Long,String> traces=tracelist.getTraces();
	    
	    List<Long> sortedKeys = new ArrayList<Long>(traces.size());
	    sortedKeys.addAll(traces.keySet());
	    Collections.sort(sortedKeys); //sorts in ascending date order 
	   
	    long smallest_pid = sortedKeys.get(0);
	    long root_pid = smallest_pid;
	    
	    if(smallest_pid != oldest_pid && oldest_pid!=-1)
	        root_pid = oldest_pid;
	    
	    tracelist.setRootpid(root_pid);
	    
		return tracelist;
	}

}
