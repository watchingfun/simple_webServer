package com.fun.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.UUID;

import com.fun.common.ConfigJson;
import com.fun.common.FileUtil;
import com.fun.common.ServerContext;
import com.fun.http.HttpRequest;
import com.fun.http.HttpResponse;
import com.fun.http.Session;

public class ClientHandler implements Runnable {
	// 1.将Socket保存在类的内部
	private Socket socket;

	// 2.构造方法，接受socket并保存在类的内部
	protected ClientHandler(Socket socket) {
		this.socket = socket;
	}

	// 3.线程类的核心方法
	public void run() {
		try {
			/*
			 * >>1.获取指向客户端的输出流 用于向客户端浏览器发送响应结果
			 */
			OutputStream out = socket.getOutputStream();

			/*
			 * >>2.获取浏览器发送请求信息的输入流 GET /news.html HTTP/1.1
			 */
			InputStream in = socket.getInputStream();
			if (in==null){
				System.out.println("null inputStream ! thread stop");
				Thread.currentThread().stop();
			}
				HttpRequest request = new HttpRequest(in);
				request.setIp(socket.getInetAddress().getHostAddress());
			/*
			 * 创建HttpResponse对象，将响应信息 封装在该对象中
			 */
			HttpResponse response = new HttpResponse(out);

			String uri = request.getUri();
			if (uri != null && uri.length() > 0) {

				if (uri.startsWith("/Login")) {
					loginService(request, response);
					return;
				}
				if (uri.startsWith("/Manage")) {
					manageService(request, response);
					return;
				}
				if(uri.startsWith("/change")) {
					changeSercive(request,response);
					return;
				}

				/* 将响应结果发送给浏览器 */
				File file = new File(ServerContext.webRoot + request.getUri());
				if (uri.startsWith("/back")) {
					notFoundService(response);
					return;
				}
				response.setStatus(200);// 设置状态码
				// 将指定的文件响应给浏览器
				responseFile(response, file);

			} // 如果line为null或者为"",则什么也不处理！
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				socket = null;
			}
		}
	}

	private void changeSercive(HttpRequest request, HttpResponse response) throws IOException {
		// TODO Auto-generated method stub
		String request_id = null;
		Integer port = null;
		String password = null;
		String method = null;
		try {
			request_id = request.getParameter("session_id");
			port =Integer.valueOf(request.getParameter("port"));
			password = request.getParameter("password");
			method = request.getParameter("method");
		} catch (NullPointerException e) {
			response.setRedirect("/500.html");
		}
		String id = Session.sessions.get(request.getIp());
		
		if (id != null && id.equals(request_id)) {
			ConfigJson.change(port, password, method);
			response.setRedirect("/success.html");
		} else {
			response.setRedirect("/500.html");	
		}
	}

	private void notFoundService(HttpResponse response) {
		File file = new File(ServerContext.webRoot + "/404.html");
		response.setStatus(404);// 设置状态码
		responseFile(response, file);
	}

	private boolean isPrintHeader = false;

	private void manageService(HttpRequest request, HttpResponse response) throws IOException {
		// TODO Auto-generated method stub
		if (!isPrintHeader) {
			String request_id = null;
			try {
				request_id = request.getParameter("session_id");
			} catch (NullPointerException e) {
				response.setRedirect("/login.html");
			}
			String id = Session.sessions.get(request.getIp());
			if (id != null && id.equals(request_id)) {
				String templet = FileUtil.fileToString(new File(ServerContext.webRoot + "/back/manage.html"));
				String newfile = templet.replace("$port", ConfigJson.port.toString())
						.replace("$password", ConfigJson.password).replace("$method", ConfigJson.method).replace("$session_id", Session.sessions.get(request.getIp()));
				response.setContentType("text/html");
				response.setContentLength(newfile.length());
				response.getOut().write(newfile.getBytes("utf-8"));
				response.getOut().flush();
				isPrintHeader = true;
			} else {
				response.setRedirect("/login.html");
				isPrintHeader = true;
			}
		}
	}

	/**
	 * 处理登录请求的方法
	 * 
	 * @param request
	 *            请求信息对象
	 * @param response
	 *            响应信息对象
	 * @throws IOException
	 */
	private void loginService(HttpRequest request, HttpResponse response) {
		// 1.获取用户的登录信息
		// >>获取用户名
		String username = request.getParameter("username");
		// >>获取密码
		String password = request.getParameter("password");
		if (ServerContext.username.equals(username) && ServerContext.password.equals(password)) {
			Session.sessions.put(request.getIp(), UUID.randomUUID().toString());
			response.setRedirect("/Manage?session_id=" + Session.sessions.get(request.getIp()));
		} else {
			responseFile(response, new File(ServerContext.webRoot + "/loginfail.html"));
		}

	}

	/**
	 * 将指定的文件发送给浏览器
	 * 
	 * @param response
	 * @param file
	 */
	private void responseFile(HttpResponse response, File file) {
		try {
			FileInputStream input = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(input);

			// >>声明一个byte数组（和文件一样长）
			byte[] buff = new byte[(int) file.length()];

			// >>直接将文件中的所有内容一次性读取到buff中
			bis.read(buff);

			String type = getContentTypeByFile(file);
			response.setContentType(type);// 设置响应数据类型
			response.setContentLength((int) file.length());// 设置响应数据的长度

			// >>2.5 发送真正的响应实体信息
			/*
			 * getOut()方法在执行的时候会先将响应头信息 发送给浏览器，再返回out流。
			 */
			response.getOut().write(buff);

			// >>2.6 刷新流
			response.getOut().flush();
		} catch (FileNotFoundException e) {
			notFoundService(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据浏览器请求的资源路径返回对应的响应数据类型
	 * 
	 * @param file
	 * @return String
	 */
	private String getContentTypeByFile(File file) {
		/*
		 * 1.获取文件的名称，如： hello.html 2.根据最后一个点截取文件的后缀名，如：html 3.根据文件后缀名到typesMap集合中获取对应的
		 * 响应数据类型
		 */
		// 1.获取文件的名称
		String fileName = file.getName();
		// 2.根据最后一个点截取文件的后缀名
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
		// 3.根据文件后缀名到typesMap集合中获取对应的响应数据类型

		System.out.println(ServerContext.typesMap.get(ext));
		return ServerContext.typesMap.get(ext);
	}

}