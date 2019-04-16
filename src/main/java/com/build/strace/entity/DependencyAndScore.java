package com.build.strace.entity;

import java.util.List;

public class DependencyAndScore {
	List<FileInfo> dependencyFiles;	
	FileScore fileScore;
	
	public DependencyAndScore()
	{
		
	}
	
	public List<FileInfo> getDependencyFiles() {
		return dependencyFiles;
	}

	public void setDependencyFiles(List<FileInfo> dependencyFiles) {
		this.dependencyFiles = dependencyFiles;
	}

	public FileScore getFileScore() {
		return fileScore;
	}

	public void setFileScore(FileScore fileScore) {
		this.fileScore = fileScore;
	}

}
