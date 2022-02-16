package com.yusys.bione.plugin.yuformat.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
/**
 * @author wangxy31
 * @date 2020-02-13
 */
public class HttpURLConnectionUtil {
	/**
     * Http post请求
     * @param url 连接
     * @param param 参数
     * @return
     */
	public static String doPost(String url, String param) {
	    PrintWriter out = null;
	    InputStream is = null;
	    BufferedReader br = null;
	    String result = "";
	    HttpURLConnection conn = null;
	    StringBuffer strBuffer = new StringBuffer();
	    
	    try {
	        URL realUrl = new URL(url);
	        conn = (HttpURLConnection) realUrl.openConnection();
	        // 设置通用的请求属性
	        conn.setRequestMethod( "POST");
	        conn.setConnectTimeout(20000);
	        conn.setReadTimeout(300000);
	        conn.setRequestProperty("Charset", "UTF-8");
	
	        // 传输数据为json，如果为其他格式可以进行修改
	        conn.setRequestProperty( "Content-Type", "text/json");
	        conn.setRequestProperty( "Content-Encoding", "utf-8");
	        // 发送POST请求必须设置如下两行
	        conn.setDoOutput( true);
	        conn.setDoInput( true);
	        conn.setUseCaches( false);
	        
	        // 获取URLConnection对象对应的输出流
//	        out = new PrintWriter(conn.getOutputStream());
	        OutputStreamWriter outwriter = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
	        out = new PrintWriter(outwriter);
	        // 发送请求参数
	        out.print(param);
	        // flush输出流的缓冲
	        out.flush();
	        
	        if (conn.getResponseCode() == 200) {
	        	// 服务器的HTTP返回码是200时，标志服务器端操作正常
	        	System.out.println("服务器的HTTP返回码是200时，标志服务器端操作正常");
	        	is = conn.getInputStream();
		        br = new BufferedReader( new InputStreamReader(is));
		        String line = null;
		        while ((line=br.readLine())!= null) {
		            strBuffer.append(line);
		        }
            } else if (conn.getResponseCode() == 400) {
            	// 服务器的HTTP返回码是400时，标志HTTP请求格式错误，HTTP报文解析失败
            	return "400";
            } else if (conn.getResponseCode() == 500) {
            	// 服务器的HTTP返回码是500时，标志服务器端抛出异常，content-type是"text/plain; charset=UTF-8"，报文内容是异常描述
            	return "500";
            } else {
            	// 其他
            	return "999";
            }
	        
	        result = strBuffer.toString();
	    } catch (Exception e) {
	        System. out.println( "发送 POST 请求出现异常！" + e);
	        e.printStackTrace();
	    }
	    // 使用finally块来关闭输出流、输入流
	    finally {
	        try {
	            if (out != null) {
	                out.close();
	            }
	            if (br != null) {
	                br.close();
	            }
	            if (conn!= null) {
	                conn.disconnect();
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
	    
	    return result;
	}
}
