package com.fun.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import sun.applet.Main;

/**
 * 封装服务器的核心配置信息
 */
public class ServerContext {
	
	/* 用于存储浏览器请求文件的后缀名及对应的
	 * 响应数据类型 */
	public static Map<String, String> typesMap;
	
	//服务器端监听的端口
	public static int port;
	//线程池大小
	public static int maxThread;
	//协议和版本
	public static String protocol;
	//服务器端存放对外访问资源的目录
	public static String webRoot;
	//用户名，密码
	public static String username;
	public static String password;
	public static String config;
	public static String ip;
	
	static{
		init();
		try {
			ConfigJson.init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void init() {
		/* 读取server.xml文件，将其中的配置信息
		 * 保存到类的内部 */
		
		/* 读取所有的文件后缀名及对应的响应数据类型
		 * 并保存到typesMap集合中 */
		try {
			//1.创建一个解析器
			SAXReader reader = new SAXReader();
			Document dom = reader
					.read("config/server.xml");
			Element root = dom.getRootElement();
			@SuppressWarnings("unchecked")
			List<Element> list = root
					.element("type-mappings")
					.elements();
			
			//对typesMap进行实例化
			typesMap = new HashMap<String,String>();
			
			for(Element ele : list){
				String key = ele
						.attributeValue("ext");
				String value = ele
						.attributeValue("value");
				typesMap.put(key, value);
				System.out.println(key+" ："+value);
			}
			
			//解析xml获取connector元素
			Element connEle = root
						.element("service")
						.element("connector");
			//获得用户名密码
			username = root.elementTextTrim("username");
			password = root.elementTextTrim("password");
			//获得配置文件位置
			config = root.elementTextTrim("config");
			//设置端口
			port = Integer.parseInt(
				connEle.attributeValue("port")
			);
			//设置线程池大小
			maxThread = Integer.parseInt(
				connEle.attributeValue("maxThread")
			);
			//设置协议及版本
			protocol = connEle
					.attributeValue("protocol");
			//设置服务端程序存放资源的目录
			webRoot = root.element("service")
					.elementTextTrim("webroot");
			ip = root.element("service")
					.elementTextTrim("ip");
			System.out.println(
					port+" : "+maxThread+" : "
					+protocol+" : "+webRoot
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}