package com.fun.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;

public class ConfigJson {
	public static Integer port;
	public static String password;
	public static String method;
	public static HashMap<String, Object> map;

	@SuppressWarnings("unchecked")
	public static HashMap<String, Object> init() {
		try {
			map = JSON.parseObject(FileUtil.fileToString(new File(ServerContext.config + "/config.json")),
					HashMap.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		port = (Integer) map.get("server_port");
		password = (String) map.get("password");
		method = (String) map.get("method");
		return map;
	}

	synchronized public static void change(Integer port, String password, String method) throws IOException {
		map.put("server_port", port);
		map.put("password", password);
		map.put("method", method);
		System.out.println(JSON.toJSONString(map));
		FileUtil.stringToFile(JSON.toJSONString(map), new File(ServerContext.config + "/config.json"));
		ConfigJson.port = port;
		ConfigJson.password = password;
		ConfigJson.method = method;
	}

}
