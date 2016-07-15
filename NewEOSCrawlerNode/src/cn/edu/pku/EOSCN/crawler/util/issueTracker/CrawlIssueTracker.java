package cn.edu.pku.EOSCN.crawler.util.issueTracker;

import java.io.File;
import java.io.FileWriter;  
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.pku.EOSCN.crawler.util.issueTracker.json.*;
import cn.edu.pku.EOSCN.crawler.util.issueTracker.json.zip.*;


/**
 * @author Carrie
 *
 */
public class CrawlIssueTracker {

	public static void crawlIssueTrack(URL originalUrl, String writePath) throws Exception{	
		int stepNum = 100;//设置一个文件里包含的bug数

		int totalNum = Integer.parseInt(GetTotalNum.getTotal(originalUrl));//获得项目的总bug数
		System.out.println("总共爬了" + totalNum + "个bug report");
		
		int startAt = 0;		
		for(; startAt < totalNum-stepNum; startAt += stepNum){			
			URL crawUrl = new URL(GetJsonUrl.getCrawlUrl(originalUrl, startAt, stepNum));
			System.out.println("crawUrl is : " + crawUrl.toString());
			crawlWeb(crawUrl, writePath + "/" + startAt + "-" + (startAt+stepNum) + ".json");//写入文件
		}
		if(startAt < totalNum){
			//GetJsonUrl getJsonUrl = new GetJsonUrl();
			URL crawUrl = new URL(GetJsonUrl.getCrawlUrl(originalUrl, startAt, stepNum));
			//System.out.println(crawlUrl);
			crawlWeb(crawUrl, writePath + "/" + startAt + "-" + totalNum + ".json");//写入文件
		}
	}	
		
	public static void crawlWeb(URL wUrl, String writePath) {		
	    int responsecode;
	    HttpURLConnection urlConnection;
	    BufferedReader reader;
	    String line;
	    try{
	        //打开URL    		    	
	        urlConnection = (HttpURLConnection)wUrl.openConnection();
	        //获取服务器响应代码
	        responsecode=urlConnection.getResponseCode();
	        if(responsecode==200){	        		        	
	            //得到输入流，即获得了网页的内容 
	            reader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));	            
	            while((line=reader.readLine())!=null){
	            	//写文件	            	
	            	line.concat(line);	            	
	            	writeToFile(line, writePath);
	            }
	        }
	        else{
	            System.out.println("获取不到网页的源码，服务器响应代码为："+responsecode);
	        }
	    }
	    catch(Exception e){
	        System.out.println("获取不到网页的源码,出现异常："+e);
	    }
	}

	public static void writeToFile(String line, String path) throws IOException {          
	    File file = new File(path);// 要写入的文本文件  	    
	    if (!file.exists()) {// 如果文件不存在，则创建该文件  	    	
	        file.createNewFile();  
	    }  
	    FileWriter writer = new FileWriter(file);// 获取该文件的输出流  
	    writer.write(line);// 写内容  
	    writer.flush();// 清空缓冲区，立即将输出流里的内容写到文件里  	    
	    writer.close();// 关闭输出流，施放资源  	    
	}  
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub		
		URL originalUrl;		
		//originalUrl = new URL("https://issues.apache.org/jira/issues/?jql=project%20%3D%20LUCENE");
		originalUrl = new URL("https://issues.apache.org/jira/browse/LUCENE");
		String writePath = "C:/tmp";//爬下来的网页要写入的文件名
		//CrawlIssueTracker crawlIssueTracker = new CrawlIssueTracker();
		crawlIssueTrack(originalUrl, writePath);		
	}

}

