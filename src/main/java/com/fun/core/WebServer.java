package com.fun.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fun.common.ServerContext;

/**
 * 基于HTTP协议的WEB服务端程序
 */
public class WebServer {
	//1.声明一个ServerSocket，表示服务端程序
	private ServerSocket server = null;
	
	//6.创建一个ExectorService实例（线程池），
	//  用于管理处理客户端请求的线程
	private ExecutorService threadPool = null;
	
	//2.提供构造函数
	/**
	 * 构造函数，对server进行初始化
	 */
	public WebServer(){
		try {
			//3.对server进行实例化，并监听8080端口
			server = new ServerSocket(
				ServerContext.port
			);
			//7.对threadPool进行实例化(线程池大小为100)
			threadPool = Executors
					.newFixedThreadPool(
						ServerContext.maxThread
					);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 4.开始接受客户端的请求并处理之
	 */
	public void start(){
		try {
			System.out.println("等待客户端连接...");
			while(true){
				Socket socket = server.accept();
				//8.处理浏览器的请求并作出回应
				threadPool.execute(new ClientHandler(socket));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 5.创建WebServer实例，调用start方法开始
	 *   接收客户端的请求并作出回应
	 */
	public static void main(String[] args) {
		WebServer server = new WebServer();
		server.start();
	}
}