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

import cn.edu.pku.EOSCN.crawler.util.issueTracker.json.*;
import cn.edu.pku.EOSCN.crawler.util.issueTracker.json.zip.*;

/**
 * @author Carrie
 *找到网页上的totalNum（就是bug的总数）
 */
public class GetTotalNum {	
	public static String getTotal(URL originalUrl){			
        int responsecode;
        String totalNum = null;
        HttpURLConnection urlConnection;
        BufferedReader reader;		        
        try{ 
        	//转换URL
        	String urlString = originalUrl.toString();
//        	String temStr1[] = urlString.split("/");
//    		String projectName = temStr1[temStr1.length-1];//取URL的最后一个/后的字符串为项目名称     
    		String temStr[] = urlString.split("/browse/");
    		String hostUrl = temStr[0];//以/browse/分割网址，得前面的一串为host
    		String projectName = temStr[1];//后面的一串为项目名
    		System.out.println("hostURL is : " + hostUrl + '\n' + "temStr is : " + projectName);
    		URL temURL = new URL(hostUrl + "/rest/api/2/search?jql=project%3D" + projectName + "&startAt=0&maxResults=0");//转换成可以取得totalNum的api网址         
    		originalUrl = temURL;//更新！    		
        	System.out.println("originalUrl is :" + originalUrl);
        	
            //打开URL
            urlConnection = (HttpURLConnection)originalUrl.openConnection();
            //获取服务器响应代码
            responsecode=urlConnection.getResponseCode();
            if(responsecode==200){            	
                //得到输入流，即获得了网页的内容 
                reader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"GBK"));
                //"total":5086
                String patt = "\"total\":([0-9]+)";//用正则表达式匹配total的值
                Pattern pattern = Pattern.compile(patt);
                String strTemp;
                while ((strTemp = reader.readLine()) != null) {
                    Matcher matcher = pattern.matcher(strTemp);
                    if(matcher.find()) {//如果找到了total
                    	totalNum = matcher.group();//赋值给largestId
                    	break;
                    }
                }
            }
            else{
                System.out.println("获取不到网页的源码，服务器响应代码为："+responsecode);
            }
        }
        catch(Exception e){
            System.out.println("获取不到网页的源码,出现异常："+e);
        }
        totalNum = totalNum.replace("\"total\":", "");
		return totalNum;//返回total
	}
	
	public static void main(String[] args) throws Exception{
		String result;
		//URL originalUrl = new URL("https://issues.apache.org/jira/issues/?jql=project%3DLUCENE");
		URL originalUrl = new URL("https://issues.apache.org/jira/browse/LUCENE");
			
		//String writeToPath = "G:/EclipseSpace/SimpleCrawler/LargestDataIssueId.txt";
		result = getTotal(originalUrl);
		System.out.println("The result is : "+result);
	}
}
