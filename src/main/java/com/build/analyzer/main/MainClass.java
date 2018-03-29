package com.build.analyzer.main;

import java.util.Scanner;

import com.build.analyzer.dtaccess.DBActionExecutor;
import com.build.analyzer.dtaccess.SessionGenerator;
import com.build.analyzer.dtgen.DataGenerationMngr;


public class MainClass {

	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		DBActionExecutor dbobj = new DBActionExecutor();
//		long rowcount = dbobj.getTotalNumberofRows();
//		
//		System.out.println(rowcount);
		
		System.out.println("Enter your action:");
		
		System.out.println("1->Data Generation\n2->Data Analysis");
		
		//create an object that reads integers:
        Scanner cin = new Scanner(System.in); 
        
        System.out.println("Enter an integer wiseguy: ");
        int inputid = cin.nextInt(); 
		
        
        if(inputid==1)
        {
        	
        	
        }
        else if(inputid==2)
        {
        	
        }
        else
        {
        	System.out.println("Wrong Function Id Entered");
        }
		
		cleanupResource();
	}
	
	
	private static void dataFiltering()
	{
		//This part is for data filtering to extract required data
		DataGenerationMngr dtgen=new DataGenerationMngr();		
		dtgen.genenrateData();
		
		//Perform Java and Gradle code diff in between commit and save the diff information in database
		
		
	}
	
	
	
	
	
	private static void cleanupResource() {
		SessionGenerator.closeFactory();
	}

}
