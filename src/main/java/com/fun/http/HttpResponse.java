package com.fun.http;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fun.common.ServerContext;

/**
 * 封装Http响应信息
 */
public class HttpResponse {
	private int status;//状态码
	private String contentType;//响应数据的类型
	private int contentLength;//响应数据的长度
	
	/* 指向客户端浏览器的输出流，
	 * 可以向浏览器发送响应数据 */
	private OutputStream out;
	
	/* 用于存储常见的状态码及对应的描述短语 */
	private Map<Integer,String> statusMap = null;
	
	public HttpResponse(OutputStream out){
		this.out = out;
		
		//对statusMap进行初始化
		statusMap = new HashMap<Integer,String>();
		statusMap.put(200, "OK");
		statusMap.put(404, "Not Found");
		statusMap.put(500, "Internal Server Error");
		statusMap.put(302, "FOUND");
	}
	
	/* 表示是否发送了响应头信息，默认是false
	 * 表示还没有发送头信息 */
	private boolean isPrintHeader = false;
	
	
	/* 只提供getOut方法，方便外界获取out流
	 */
	public OutputStream getOut() {
		if(!isPrintHeader){//false表示还未曾发送
			PrintStream ps = new PrintStream(out);
			//>>2.1 发送状态行
			ps.println(ServerContext.protocol
							+" "+status+" "
							+statusMap.get(status));
			//>>2.2 发送响应数据的类型
			ps.println("Content-Type:"+contentType);
			//>>2.3 发送响应数据的长度
			ps.println("Content-Length:"+contentLength);
			//>>2.4 发送一个空行
			ps.println();
			
			isPrintHeader = true;
		}
		return out;
	}
	public void setRedirect(String adress) {	
			PrintStream ps = new PrintStream(out);
		
			ps.println("HTTP/1.0 302 FOUND");
			Date now=new Date();  
			ps.println("Date: "+now);
			ps.println("Server: Redirector 1.0");
			ps.println("Location: "+adress);
			ps.println("Content-Type: text/html");
			ps.println();
	}
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public int getContentLength() {
		return contentLength;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}
}