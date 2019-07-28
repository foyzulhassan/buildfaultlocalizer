package com.build.strace.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileScore {
	private Map<String,Score> fileScore;
	
    public Map<String, Score> getFileScore() {
		return fileScore;
	}

	private FileScore()
    {
    	
    }
    
    public FileScore(List<String> files)
    {
    	fileScore=new HashMap<>();    	
    	for(String file:files)
    	{
    		Score score=new Score();
    		fileScore.put(file, score);
    	}
    }
    
    public void IncrementFilePassedScore(String file)
    {
    	if(fileScore.containsKey(file))
    	{
    		Score score=fileScore.get(file);
    		score.incrementPassed();
    		fileScore.put(file,score);
    	}
    }
    
    public void DecrementFilePassedScore(String file,int val)
    {
    	if(fileScore.containsKey(file))
    	{
    		Score score=fileScore.get(file);
    		score.decrementPassedValue(val);
    		fileScore.put(file,score);
    	}
    }
    
    public void IncrementFileFailedScore(String file)
    {
    	if(fileScore.containsKey(file))
    	{
    		Score score=fileScore.get(file);
    		score.incrementFailed();
    		fileScore.put(file,score);
    	}
    }
    
    public void IncrementFileFailedScoreByValue(String file, int value)
    {
    	if(fileScore.containsKey(file))
    	{
    		Score score=fileScore.get(file);
    		score.incrementFailedByValue(value);
    		fileScore.put(file,score);
    	}
    }
    
}
