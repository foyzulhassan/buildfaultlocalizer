package com.build.strace.spectrum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.build.strace.entity.FileScore;
import com.build.strace.entity.Score;
import com.util.sorting.SortingMgr;

public class SpectrumCalculator {
	public ArrayList<String> getTarantulaBasedRanking(FileScore filescore,Map<String,Boolean> passedlines,Map<String,Boolean> failedlines,String localpath)
	{
		 Map<String, Score> rawfilescore=filescore.getFileScore();
		 Map<String,Double> rankfilescore=new HashMap<>();
		 int totalpassed=passedlines.size();
		 int totalfailed=failedlines.size();
		 
		 for(String file:rawfilescore.keySet())
		 {
			 Score score=rawfilescore.get(file);
			 int passeds=score.getPassedcount();
			 int faileds=score.getFailedcount();			 

			 passeds=passeds+1;
			 faileds=faileds+1;
				 
			 double d1= (double)faileds/totalfailed;
			 double d2=(double)passeds/totalpassed;
			 double n1=(double)faileds/totalfailed;
			 
			 
			 double tarantulascore=(n1)/((d1)+(d2));
			 int startpos=file.indexOf(localpath);
			 String basepath=file.substring(startpos+localpath.length()+1);
			 rankfilescore.put(basepath, tarantulascore);
		 }
		 
		Map<String, Double> sortedsimmap = SortingMgr.sortByValue(rankfilescore);

		ArrayList<String> reankedfiles = new ArrayList<String>(sortedsimmap.keySet()); 
		
		return reankedfiles;
		 
	}
	
	public ArrayList<String> getOchiaiBasedRanking(FileScore filescore,Map<String,Boolean> passedlines,Map<String,Boolean> failedlines)
	{
		 Map<String, Score> rawfilescore=filescore.getFileScore();
		 Map<String,Double> rankfilescore=new HashMap<>();
		 int totalpassed=passedlines.size();
		 int totalfailed=failedlines.size();
		 
		 for(String file:rawfilescore.keySet())
		 {
			 Score score=rawfilescore.get(file);
			 int passeds=score.getPassedcount();
			 int faileds=score.getFailedcount();	
			 passeds=passeds+1;
			 faileds=faileds+1;
			 double sqrtval=Math.sqrt(totalfailed*(faileds+passeds));
			 double ochiaicore=(faileds/sqrtval);
			 rankfilescore.put(file, ochiaicore);
		 }
		 
		Map<String, Double> sortedsimmap = SortingMgr.sortByValue(rankfilescore);

		ArrayList<String> reankedfiles = new ArrayList<String>(sortedsimmap.keySet()); 
		
		return reankedfiles;
		 
	}
	
	public ArrayList<String> getOp2BasedRanking(FileScore filescore,Map<String,Boolean> passedlines,Map<String,Boolean> failedlines)
	{
		 Map<String, Score> rawfilescore=filescore.getFileScore();
		 Map<String,Double> rankfilescore=new HashMap<>();
		 int totalpassed=passedlines.size();
		 int totalfailed=failedlines.size();
		 
		 for(String file:rawfilescore.keySet())
		 {
			 Score score=rawfilescore.get(file);
			 int passeds=score.getPassedcount();
			 int faileds=score.getFailedcount();	 
			 passeds=passeds+1;
			 faileds=faileds+1;
			
			 double op2score=faileds-(passeds/(totalpassed+1));
			 rankfilescore.put(file, op2score);
		 }
		 
		Map<String, Double> sortedsimmap = SortingMgr.sortByValue(rankfilescore);

		ArrayList<String> reankedfiles = new ArrayList<String>(sortedsimmap.keySet()); 
		
		return reankedfiles;
		 
	}
	
	public ArrayList<String> getBarinelBasedRanking(FileScore filescore,Map<String,Boolean> passedlines,Map<String,Boolean> failedlines)
	{
		 Map<String, Score> rawfilescore=filescore.getFileScore();
		 Map<String,Double> rankfilescore=new HashMap<>();
		 int totalpassed=passedlines.size();
		 int totalfailed=failedlines.size();
		 
		 for(String file:rawfilescore.keySet())
		 {
			 Score score=rawfilescore.get(file);
			 int passeds=score.getPassedcount();
			 int faileds=score.getFailedcount();
			 passeds=passeds+1;
			 faileds=faileds+1;
			
			 double barinelscore=1-(passeds/(passeds+faileds));
			 rankfilescore.put(file, barinelscore);
		 }
		 
		Map<String, Double> sortedsimmap = SortingMgr.sortByValue(rankfilescore);

		ArrayList<String> reankedfiles = new ArrayList<String>(sortedsimmap.keySet()); 
		
		return reankedfiles;
		 
	}
	
	public static void main(String[] args)
	{
		 int passeds=0;
		 int faileds=0;	
		 
		 int totalfailed=3380;
		 int totalpassed=3440;

		 passeds=passeds+1;
		 faileds=faileds+1;
			 
		 double d1= (double)faileds/totalfailed;
		 double d2=(double)passeds/totalpassed;
		 double n1=(double)faileds/totalfailed;
		 
		 double tarantulascore=(n1)/((d1)+(d2));
		 
		 System.out.println(tarantulascore);
	}

}
