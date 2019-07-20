package com.build.util;

import java.util.Map;
import java.util.Set;

public class MapContains {

	public static boolean IsMapContainsPartial(Map<String, Boolean> passelines, String text) {
		boolean iscontains = false;
		Set<String> keys = passelines.keySet();

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

	public static String GetContainsKey(Map<String, Boolean> passelines, String text) {
		Set<String> keys = passelines.keySet();

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
