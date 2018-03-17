package com.build.analyzer.main;

import com.build.analyzer.dtaccess.DBActionExecutor;
import com.build.analyzer.dtaccess.SessionGenerator;





public class MainClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBActionExecutor dbobj = new DBActionExecutor();
		long rowcount = dbobj.getTotalNumberofRows();
		
		System.out.println(rowcount);
		
		cleanupResource();


	}
	
	private static void cleanupResource() {
		SessionGenerator.closeFactory();
	}

}
