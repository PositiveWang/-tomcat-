package com.yc.tomcat.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 解析web.xml获取后缀对应的类型
 * @author 蛙
 *
 */
public class ParseXml {
	private static Map<String, String> map = new HashMap<String, String>();
	
	@SuppressWarnings("unchecked")
	public ParseXml() {
		SAXReader read = new SAXReader();
		Document doc = null;
		
		try {
			doc = read.read(this.getClass().getClassLoader().getResourceAsStream("web.xml"));
			
			List<Element> mimes = doc.selectNodes("//mime-mapping");
			
			//循环解析
			for( Element el : mimes ) {
				map.put(el.selectSingleNode("extension").getText().trim(), el.selectSingleNode("mime-type").getText().trim());
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, String> getMap(){
		return map;
	}
	
	public static String getContentType(String key) {
		return map.getOrDefault(key, "text/html;charset=utf-8");
	}
	
}
