package com.build.util;

import java.util.Map;
import java.util.Set;

public class MapContains {

	public static boolean IsMapContainsPartial(Map<String, Boolean> passelines, String line) {
		boolean iscontains = false;
		String text=line.trim();
		Set<String> keys = passelines.keySet();
		
		if(text.length()<=2)
			return false;

		for (String key : keys) {
			if (key.contains(text)) {
				iscontains = true;
				return iscontains;
			} else if (text.contains(key)) {
				iscontains = true;
				return iscontains;
			}
		}

		return iscontains;
	}

	public static String GetContainsKey(Map<String, Boolean> passelines, String line) {
		Set<String> keys = passelines.keySet();
		String text=line.trim();
		
		if(text.length()<=2)
			return null;
		
		for (String key : keys) {
			if (key.contains(text)) {
				return key;
			} else if (text.contains(key)) {
				return key;
			}
		}

		return null;
	}

}
