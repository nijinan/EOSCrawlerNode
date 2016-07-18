package cn.edu.pku.EOSCN.crawler.util.issueTracker;

import java.io.File;
import java.io.FileNotFoundException;  
import java.io.FileReader;  
import java.io.FileWriter;  
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Carrie
 *
 */
public class GetLargestDataIssueId {	
	public String getLargestDataIssueId(String wUrl){
		URL url;
        int responsecode;
        String largestId = null;
        HttpURLConnection urlConnection;
        BufferedReader reader;		        
        try{
            //生成一个URL对象，给出要获取源代码的网页地址为
            url=new URL(wUrl);
            //打开URL
            urlConnection = (HttpURLConnection)url.openConnection();
            //获取服务器响应代码
            responsecode=urlConnection.getResponseCode();
            if(responsecode==200){
                //得到输入流，即获得了网页的内容 
                reader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"GBK"));
                String patt = "data-issuekey=\"[A-Z]+-[0-9]+\"";//用正则表达式匹配第一个data-issuekey的值
                Pattern pattern = Pattern.compile(patt);
                String strTemp;
                while ((strTemp = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(strTemp);
                    if(matcher.find()) {//如果找到了第一个data-issuekey
                    	largestId = matcher.group();//赋值给largestId
                    	break;
                    }
                }
            	//return largestId;
            }
            else{
                System.out.println("获取不到网页的源码，服务器响应代码为："+responsecode);
            }
        }
        catch(Exception e){
            System.out.println("获取不到网页的源码,出现异常："+e);
        }
		return largestId;//返回largestId
	}
	
	public void main(String[] args) throws Exception{
		String result;
		String webUrl = "https://issues.apache.org/jira/issues/?jql=project%20%3D%20LUCENE";
		//String writeToPath = "G:/EclipseSpace/SimpleCrawler/LargestDataIssueId.txt";
		result = getLargestDataIssueId(webUrl);
		System.out.println("The result is : "+result);
	}
}