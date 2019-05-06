package com.test;

import java.util.ArrayList;
import java.util.List;

public class StringJoinTest {
	
	public static void main(String[] args)
	{
		
		List<String> strlist=new ArrayList<>();
		//strlist.add("1");
		
		String str=String.join(" ", strlist);
		
		System.out.println(str.length());
		
	}

}
