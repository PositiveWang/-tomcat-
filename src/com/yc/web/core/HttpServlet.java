package com.yc.web.core;

import com.yc.tomcat.core.TomcatConstants;

/**
 * 基于http协议的Servlet
 * @author 蛙
 *
 */
public class HttpServlet implements Servlet{

	@Override
	public void init() {
		
	}

	@Override
	public void service(ServletRequest request, ServletResponse response) {
		switch(request.getMethod()) {
		case TomcatConstants.REQUEST_METHOD_GET: doGet(request, response);break;
		case TomcatConstants.REQUEST_METHOD_POST: doPost(request, response);break;
		}
	}

	@Override
	public void doGet(ServletRequest request, ServletResponse response) {
		
	}

	@Override
	public void doPost(ServletRequest request, ServletResponse response) {
		
	}

}
