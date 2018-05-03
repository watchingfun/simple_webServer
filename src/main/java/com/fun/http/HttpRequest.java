package com.fun.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于封装Http请求信息
 */
public class HttpRequest {


    private String method;//请求方式
    private String uri;//请求的资源路径
    private String protocol;//协议及版本
    private String ip;


    /* 保存所有请求参数信息的map集合 */
    private Map<String, String> parameterMap;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public HttpRequest(InputStream in) {
        /* in -- 浏览器发送请求信息的流，通过
         * 这个流可以获取请求信息，如请求信息的
         * 第一行：GET /index.html HTTP/1.1
         */
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(in)
            );
            // 获取请求信息中的第一行（即请求行信息）
            String line = br.readLine();
            if (line != null && line.length() > 0) {
                String[] data = line.split("\\s");
                if (data.length >= 3) {
                    method = data[0];
                    uri = data[1];
                    protocol = data[2];
                }else {
                    for(String s:data){
                        System.out.println(s);
                    }
                }

                System.out.println(method + " : "
                        + uri + " : " + protocol);

                //设置默认的主页
                if ("/".equals(uri)) {
                    uri = "/login.html";
                }

                /* uri=/RegistUser?username=zs&password=123
                 * 如果uri不为null和“”并且包含？，则说明uri
                 * 中包含参数
                 */
                if (uri != null && uri.length() > 0
                        && uri.contains("?")) {

                    //根据uri中的问号截取问号后面的参数组成的字符串
                    String paramStr = uri.substring(
                            uri.indexOf("?") + 1);

                    //获取所有参数组成的数组
                    String[] params = paramStr.split("&");

                    parameterMap = new HashMap<>();
                    //遍历参数数组，将参数名和值存入的map集合中
                    try {
                        for (String param : params) {
                            String name = param.split("=")[0];
                            String value = param.split("=")[1];
                            parameterMap.put(name, value);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("没有参数");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据请求参数名称返回对应的参数值
     *
     * @param name 参数名
     * @return String 参数值
     */
    public String getParameter(String name) {
        /* /RegistUser?username=zs&password=123
         * parameterMap中就会包含如下内容
         * username ：zs
         * password ：123
         */
        return parameterMap.get(name);
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getProtocol() {
        return protocol;
    }
}