package cn.edu.pku.EOSCN.crawler.util.issueTracker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.pku.EOSCN.crawler.util.issueTracker.json.*;
import cn.edu.pku.EOSCN.crawler.util.issueTracker.json.zip.*;


/**
 * @author Carrie
 *给个主页url，给个startAt和maxResult，就构造出要爬的网址。
 */
public class GetJsonUrl {	
	public static String getCrawlUrl(URL originalUrl, int startAt, int maxResults) throws Exception
	{		 
		 String jsonUrl;
		 //String projectName = getProjectName(originalUrl);
		 
	  	 String urlString = originalUrl.toString();
 		 String temStr[] = urlString.split("/browse/");
 		 String hostUrl = temStr[0];//以/browse/分割网址，得前面的一串为host
 		 String projectName = temStr[1];//后面的一串为项目名 		 
		 
		 jsonUrl = hostUrl + "/rest/api/2/search?jql=project=" + projectName +"&startAt=" + startAt +"&maxResults=" + maxResults;
		 System.out.println("jsonUrl is : " + jsonUrl);
		 return jsonUrl;
	}
	 
//	public String getProjectName(URL originalUrl) throws Exception{
//		String projectName = null;
//		String sUrl = originalUrl.toString();
//		
//		String patt = "[A-Za-z]+$";//用正则表达式匹配project的值
//        Pattern pattern = Pattern.compile(patt);
//        
//        Matcher matcher = pattern.matcher(sUrl);
//        if(matcher.find()) {//如果找到了total
//           	projectName = matcher.group();//赋值给     
//        }
//        return projectName;
//	}
	
	public static void main(String[] args) throws Exception{		
		URL originalUrl;
		String jsonUrl;
		//originalUrl = new URL("https://issues.apache.org/jira/issues/?jql=project%20%3D%20LUCENE");
		originalUrl = new URL("https://issues.apache.org/jira/browse/LUCENE");
		jsonUrl = getCrawlUrl(originalUrl, 0, 2);	
		System.out.println(jsonUrl);
			
	}
}

