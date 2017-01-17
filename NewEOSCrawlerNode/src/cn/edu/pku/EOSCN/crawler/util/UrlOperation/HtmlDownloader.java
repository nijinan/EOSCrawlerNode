package cn.edu.pku.EOSCN.crawler.util.UrlOperation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.eclipse.core.runtime.Path;
import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.pku.EOSCN.business.NetWorkDaemon;
import cn.edu.pku.EOSCN.crawler.proxy.ProxyAddress;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;

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
	
	public static String downloadClient(String urlString, Map<String, List<String>> headers, ProxyAddress proxyaddr){
		while (!NetWorkDaemon.isok)
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		System.out.println("connecting to :" + urlString);
        HttpClient client = new HttpClient();  
        client.getHttpConnectionManager().getParams().setConnectionTimeout(5000 );     

        GetMethod httpGet = new GetMethod(urlString);  
        httpGet.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,5000 ); 
        StringBuffer document = new StringBuffer();
        try {  
            client.executeMethod(httpGet);  
              
            InputStream in = httpGet.getResponseBodyAsStream();  
             
             
            byte[] b = new byte[4096];  
            int len = 0;  
            while((len=in.read(b))!= -1){  
            	document.append(new String(b, 0, len));
            }  
            in.close();  
              
        } catch (IOException e) {  
        	System.out.println("IOException when connecting to URL: " + urlString);
	 		
	 		return "";
        }finally{  
            httpGet.releaseConnection();  
        }  
	 	return document.toString(); 
	}
	
	public static String downloadOrin(String urlString, Map<String, List<String>> headers, ProxyAddress proxyaddr){
		StringBuffer document = new StringBuffer();
		while (!NetWorkDaemon.isok)
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		//System.out.println("connecting to :" + urlString);
 		if (proxyaddr != null) System.out.println("using "+proxyaddr.getIP()+":"+proxyaddr.getPort());
	 	try { 
	 		URL url = new URL(urlString); 
	 		URLConnection conn;
	 		if (proxyaddr != null){
	 			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyaddr.getIP(), proxyaddr.getPort()));  
	 			conn = url.openConnection(proxy); 
	 		}else conn = url.openConnection();
	 		conn.setConnectTimeout(100000);
	 		conn.setReadTimeout(120000);
	 		((HttpURLConnection)conn).setInstanceFollowRedirects(false);
			String headUrl[] ={"IBM WebExplorer /v0.94', 'Galaxy/1.0 [en] (Mac OS X 10.5.6; U; en)","Opera/9.27 (Windows NT 5.2; U; zh-cn)","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20130406 Firefox/23.0", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0",  "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)", "Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14",  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko)"  ,  "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0; TheWorld)"}; 
			Random rd1 = new Random();
			int randomIndex = rd1.nextInt(headUrl.length-1);
			//System.out.println(headUrl[randomIndex]);
			conn.setRequestProperty("User-Agent", headUrl[randomIndex]);
			if (headers != null){
				Map header = conn.getHeaderFields();
				headers.putAll(header);
			}
			if (conn.getContentLength() > 100000000) return "";
			if (((HttpURLConnection)conn).getResponseCode() != 200) {
				System.err.println(((HttpURLConnection)conn).getResponseCode() + " " + urlString);
				return "";
			}
	 		//BufferedReader reader = new BufferedReader(new InputStreamReader(conn. getInputStream(),"utf-8")); 
	 		String line = null; 
	 		byte[] c = new byte[2];
	 		c[0]=0x0d;
	 		c[1]=0x0a; 
	 		InputStream is = conn.getInputStream();
	 		String c_string = new String(c);   
            byte[] b = new byte[4096]; 
            for (int n; (n = is.read(b)) != -1;)   { 
            	document.append(new String(b, 0, n, "ISO-8859-1"));
	 			if (document.length() > 100000000){
	 				is.close();
	 				return "";
	 			}            	
            } 
//	 		while ( (line = reader.readLine()) != null) { 
//	 			document.append( line+c_string );
//	 			if (document.length() > 100000000){
//	 				reader.close();
//	 				return "";
//	 			}
//	 		} 
	 		
            is.close(); 
	 		}  
	 	catch (Exception e) { 
	 		System.out.println("IOException when connecting to URL: " + urlString);
	 		
	 		return "";
	 	}
	 	return document.toString(); 
	}
	
	public static String getHost(String url){
		if (url.startsWith("https://")) url = url.substring(8);
		if (url.startsWith("http://")) url = url.substring(7);
		return url.split("/")[0];
	}
	public static String getHost2(String url){
		if (url.startsWith("https://")) url = url.substring(8);
		if (url.startsWith("http://")) url = url.substring(7);
		return url;
	}
	
	public static String url2path(String url){
		if (url.startsWith("https://")) url = url.substring(8);
		if (url.startsWith("http://")) url = url.substring(7);
		url = url.replaceAll("[<>:*?]", "");
		while (url.endsWith("/")){
			url = url.substring(0, url.length() - 1);
		}
		url = url.replaceAll("/+", ""+Path.SEPARATOR);
		url = url+"__FILE";
		return url;
	}
	
	public static void main(String[] args) throws InterruptedException, IOException{
		String urlString = "https://ci.trafficserver.apache.org/files/src/trafficserver-6.0.x.tar.bz2";

		String content = HtmlDownloader.downloadOrin(urlString, null, null);
		FileUtil.write("E:\\download\\"+HtmlDownloader.url2path(urlString), content);
	}
}
