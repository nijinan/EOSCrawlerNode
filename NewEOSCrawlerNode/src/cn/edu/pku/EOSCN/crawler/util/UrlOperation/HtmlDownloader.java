package cn.edu.pku.EOSCN.crawler.util.UrlOperation;

import org.eclipse.core.runtime.Path;

public class HtmlDownloader {
	public static String downloadHU(String url){
		try {
			return ProxyUtil.DocFromUrl(url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return null;
		}
	}
	public static String downloadOrin(String url){
		String tmp = ProxyUtil.getDocumentAt(url);
		if (tmp == null) tmp = "";
		return tmp;
	}
	
	public static String getHost(String url){
		if (url.startsWith("https://")) url = url.substring(8);
		if (url.startsWith("http://")) url = url.substring(7);
		return url.split("/")[0];
	}
	public static String url2path(String url){
		if (url.startsWith("https://")) url = url.substring(8);
		if (url.startsWith("http://")) url = url.substring(7);
		url = url.replaceAll("[<>:*?]", "");
		if (url.endsWith("/")){
			url = url.substring(0, url.length() - 1);
		}
		url = url.replaceAll("/+", ""+Path.SEPARATOR);
		url = url+"__FILE";
		return url;
	}
}
