package com.yc.web.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.yc.tomcat.core.ParseXml;
import com.yc.tomcat.core.ReadConfig;
import com.yc.tomcat.core.TomcatConstants;

public class HttpServletResponse implements ServletResponse {
	private OutputStream os;
	private String basePath = TomcatConstants.BASE_PATH;
	private String projectName;
	
	public HttpServletResponse(String projectName, OutputStream os) {
		this.projectName = projectName;
		this.os = os;
	}


	@Override
	public PrintWriter getWriter() throws IOException {
		String msg = "HTTP/1.1 200 OK\r\nContent-Type:text/html;charset=utf-8\r\n\r\n";
		os.write(msg.getBytes());
		os.flush();
		return new PrintWriter(os);
	}
	
	/**
	 * 重定向到指定的资源
	 * @param url
	 */
	public void sendRedirect(String url) {
		if( url == null || "".equals(url)) {
			error404(url);
			return;
		}
		
		if( !url.startsWith(projectName) ) { //如果不是以项目名开头，说明是动态处理里面的跳转
			url = projectName + "/" + url;
		}
		
		if( url.indexOf("/") == url.lastIndexOf("/") && url.indexOf("/") < url.length() ) {  //  /DayFresh  即只指定了项目名
			send302(url);
		}else {
			if( url.endsWith("/") ) {  //说明没有具体指定资源    /DayFresh/
				String defaultPath = ReadConfig.getInstance().getProperty("default");
				//读取默认资源
				File fl = new File(basePath, url.substring(1).replace("/", "\\")+ defaultPath);
				if( !fl.exists() ) {
					error404(url);
					return;
				}
				send200(readFile(fl), defaultPath.substring(defaultPath.lastIndexOf(".") + 1).toLowerCase());
			}else {
				File fl = new File(basePath, url.substring(1).replace("/", "\\"));
				if( !fl.exists() || !fl.isFile() ) {
					error404(url);
					return;
				}
				send200(readFile(fl), url.substring(url.lastIndexOf(".") + 1).toLowerCase());
			}
		}
	}

	//读取指定资源
	private byte[] readFile(File fl) {
		try( FileInputStream fis = new FileInputStream(fl) ){
			byte[] bt = new byte[fis.available()];
			fis.read(bt);
			return bt;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void send302(String url) {
		try {
			String msg = "HTTP/1.1 302 Moved Temporarily\r\nContent-type: text/html;charset=utf-8\r\nLocation:"+ url +"/\r\n\r\n";
			os.write(msg.getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if( os != null ) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void send200(byte[] bt, String extension) {
		try {
			String contentType = ParseXml.getContentType(extension);
			String msg = "HTTP/1.1 200 Ok\r\nContent-type:" + contentType + "\r\nContent-Length:" + bt.length + "\r\n\r\n";
			os.write(msg.getBytes());
			os.write(bt);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if( os != null ) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 报错404
	 * @param url
	 */
	private void error404(String url) {
		try {
			String errInfo = "<h1>HTTP Status 404 -" + url + "</h1>";
			String msg = "HTTP/1.1 404 File Not Found\r\nContent-Type:text/html;charset=utf-8\r\nContent-Length:" + errInfo.length() + "\r\n\r\n";
			os.write(msg.getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if( os != null ) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
