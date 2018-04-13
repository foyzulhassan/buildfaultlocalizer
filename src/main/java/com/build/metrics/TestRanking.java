package com.build.metrics;

import java.util.ArrayList;

public class TestRanking {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		RankingCalculator rc=new RankingCalculator();
		
		ArrayList<String> candidates=new ArrayList<String>();
		
		candidates.add("A1");
		candidates.add("A");
		candidates.add("B1");
		candidates.add("C1");
		candidates.add("D");
		candidates.add("F");
		candidates.add("B");
		candidates.add("F2");
		candidates.add("C");
		candidates.add("E");
		
		String myStrings[];
		myStrings = new String[] { "A", "B", "C","D","E","F" };
		
		double map=rc.getMeanAveragePrecision(candidates, myStrings);
		
		System.out.println(map);
		
		
		ArrayList<String> candidates1=new ArrayList<String>();
		
		candidates1.add("A");
		candidates1.add("B");
		candidates1.add("C");
		
		String myStrings1[];
		myStrings1 = new String[] { "C", "B", "A"};
		
		double mrr=rc.getMeanAverageReciprocal(candidates1, myStrings1);
		
		System.out.println(mrr);
		

	}

}
