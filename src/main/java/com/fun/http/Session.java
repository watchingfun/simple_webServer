package com.fun.http;

import java.util.LinkedHashMap;
import java.util.Map;

public class Session {
	public static Map<String, String> sessions = new LinkedHashMap<String,String>(5,0.75f,true);
}
