package cn.edu.pku.EOSCN.crawler;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import jcifs.smb.SmbException;








import cn.edu.pku.EOSCN.TestUtil;
import cn.edu.pku.EOSCN.DAO.JDBCPool;
import cn.edu.pku.EOSCN.DAO.RelativeWebDAO;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLReader;
import cn.edu.pku.EOSCN.crawler.util.issueTracker.json.JSONArray;
import cn.edu.pku.EOSCN.crawler.util.issueTracker.json.JSONException;
import cn.edu.pku.EOSCN.crawler.util.issueTracker.json.JSONObject;
import cn.edu.pku.EOSCN.entity.Project;
import cn.edu.pku.EOSCN.entity.RelativeWeb;
import cn.edu.pku.EOSCN.exception.GoogleApiLimitExceededException;
import cn.edu.pku.EOSCN.storage.StorageUtil;


/**
 * @author Luo Yuxiang
 * @Description:  实现对Project相关google网页的爬取
 */

public class RelativeWebCrawler extends Crawler{

	public RelativeWebCrawler(Project project, List<String> urlList) {
		super(project, urlList);
	}
	
	public RelativeWebCrawler() {
		super();
	}
	
	
	
	
	
	public void Crawl() throws GoogleApiLimitExceededException
	{
		int returnValue = WAITING;

		
		//google的API接口，可以替换不同的APP ID和Cse ID以及检索内容
		String szAPI = "https://www.googleapis.com/customsearch/v1?key=%API_KEY%&cx=%UNIQUE_ID%&q=%queryExpression%";  
		String szAppId = "AIzaSyCGrFrHC3KlfzCZkh8DWLcRqK04-UrV8o0";
		String szCseId = "016257362362124237583:2v5ebbv1wra";
		String projectName = null;
		try {
			projectName = URLEncoder.encode(project.getName(), "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//RelativeWebDAO relativeWebDAO=new RelativeWebDAO(projectName);
	
		
		//inurl:blog来限制链接的url中要包含blog
		String queryExpression = "inurl:blog%20"+projectName;
		
		int start = 0;

	    szAPI = szAPI.replace("%API_KEY%", szAppId)  
	                .replace("%UNIQUE_ID%", szCseId)  
	                .replace("%queryExpression%", queryExpression); 
 
	    JSONArray jsonArray = null;
	    int nIndex = 0;
    	String szJson = null,szLink = null,szTitle=null;
	    while( start <= 99 )
	    {    
	    	if (jsonArray == null || nIndex >= jsonArray.length())					//没有json数组或者已读取完
			{
				if (jsonArray != null && jsonArray.length() < 10) break;			//如果最后一个json不足10个网址
				try {
					String url = szAPI+"&start="+(start + 1);
					//url = URLEncoder.encode(url, "utf-8");
					szJson = download(url);                 //按照顺序爬取相应的网页
					
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//如果获取不到数据，直接跳出
				if( szJson == null )
				{
					logger.info("error: google API limit exceed!");
					throw new GoogleApiLimitExceededException();
				}
				
				try {
					jsonArray = new JSONObject(szJson).getJSONArray("items");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			//获取json的数据
				
				if (jsonArray.length() == 0) break;
				nIndex = 0;
			}
			
			try {
				szLink = jsonArray.getJSONObject(nIndex).getString("link");
				szTitle = jsonArray.getJSONObject(nIndex).getString("title");
				System.out.println(szTitle);
				nIndex ++;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			//获取链接

			System.out.println(szLink);
			

			//String saveAddr = "d:/"+projectName+urlnum+".html";		
			
			String saveAddr = null;
			RelativeWeb web = new RelativeWeb();
			
			try {
				saveAddr = StorageUtil.getRelativeWebFilePath(project)+"_"+web.getUuid()+".html";
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SmbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	        //获取远程存储位置	
				
			String content = null;
			try {
				content = URLReader.getHtmlStringFromUrl(szLink);
			} catch (HttpException e1) {
				e1.printStackTrace();
				start ++;
				continue;
			} catch (IOException e1) {
				e1.printStackTrace();
				start ++;
				continue;
			}
			int flag = StorageUtil.storeTextFileRemote(content,saveAddr);   //将相关的html保存到指定地址
			if( flag == 0 )
			{
				System.out.println("page : "+szLink+"  can't save!!!");
				saveAddr = "NONE";          //存储不成功的，用NONE来标示
			}

			
			
			web.setFilepath(saveAddr);
			web.setProjectuuid(project.getUuid());
			web.setTitle(szTitle);
			web.setUrl(szLink);
			
			try {
				RelativeWebDAO.insertRelativeWeb(web);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			++start;  
			System.out.println(start);
	    }
	    //System.out.println(urlnum+"    "+relativeUrl.get(0)+"   "+relativeUrl.size());
	    

		if( returnValue != ERROR )
		{
			returnValue = SUCCESS;
		}
		System.out.println(returnValue);
		finish();
	}
	
	//测试使用
	public static void main(String[] args) throws ClassNotFoundException, SQLException, GoogleApiLimitExceededException
	{
		//JDBCPool.initPool();
		Project p = TestUtil.getLuceneProject();
		List<String> urllist = new LinkedList<String>();
		RelativeWebCrawler webget = new RelativeWebCrawler(p ,urllist);
		webget.Crawl();
	}
	
	
	
	private String download(String szUrl) throws UnsupportedEncodingException	//下载
	{	
		try {
			final int BUFFER_SIZE = 2097152;
			int nByteIndex = 0, nByteCount;
			
			byte bytes[] = new byte[BUFFER_SIZE]; 
			String szHtml, szContentType, szEncoding;
			
			Pattern pat;
			Matcher mat;
			
			if (szUrl.subSequence(0, 5).equals("https"))						//安全链接
			{
				SSLContext context = SSLContext.getInstance("SSL");
				context.init(null,new TrustManager[] { new TrustAnyTrustManager() }, new SecureRandom());
				
				URL url = new URL(szUrl);
				HttpsURLConnection connection;
				
				connection = (HttpsURLConnection) url.openConnection();
				connection.setSSLSocketFactory(context.getSocketFactory());
				connection.setHostnameVerifier(new TrustAnyHostnameVerifier());	//屏蔽一切验证
				connection.connect();
				
				szContentType = connection.getContentType();					//获取格式
				pat = Pattern.compile("[^;]*");
				mat = pat.matcher(szContentType);
				if (!mat.find()) return null;
				szContentType = mat.group().toString();
				if (!szContentType.equals("application/json") && !szContentType.equals("text/html")) 
					return null;												//必须是json或html
				
				InputStream input = connection.getInputStream();
			    nByteCount = input.read(bytes, nByteIndex, BUFFER_SIZE);
			    
			    while (nByteCount != -1) {										//html保存到字节数组
			      nByteIndex += nByteCount;
			      nByteCount = input.read(bytes, nByteIndex, 1);
			    }
			    
			    szHtml = new String(bytes, 0, nByteIndex, "utf-8");				//试转为utf-8
				
				pat = Pattern.compile("charset=[^>]*");
				mat = pat.matcher(szHtml);
				if (!mat.find()) return szHtml;
				
				szEncoding = mat.group().toString().substring(8);
				pat = Pattern.compile("[a-zA-Z0-9\\-]+");
				mat = pat.matcher(szEncoding);
				if (mat.find()) szEncoding = mat.group().toString();
				else return szHtml;
				
				return new String(bytes, 0, nByteIndex, szEncoding);			//获取编码集后重新转换
			}
			else
			{
				URL url = new URL(szUrl);
				URLConnection connection;
				
				connection = url.openConnection();
				szContentType = connection.getContentType();					//此部分下同安全连接
				pat = Pattern.compile("[^;]*");
				mat = pat.matcher(szContentType);
				if (!mat.find()) return null;
				szContentType = mat.group().toString();
				if (!szContentType.equals("application/json") && !szContentType.equals("text/html")) 
					return null;

				InputStream input = connection.getInputStream();
				nByteCount = input.read(bytes, nByteIndex, BUFFER_SIZE);
			    
			    while (nByteCount != -1) {
			      nByteIndex += nByteCount;
			      nByteCount = input.read(bytes, nByteIndex, 1);
			    }
			    
			    szHtml = new String(bytes, 0, nByteIndex, "utf-8");
				
				pat = Pattern.compile("charset=[^>]*");
				mat = pat.matcher(szHtml);
				if (!mat.find()) return szHtml;
				
				szEncoding = mat.group().toString().substring(8);
				pat = Pattern.compile("[a-zA-Z0-9\\-]+");
				mat = pat.matcher(szEncoding);
				if (mat.find()) szEncoding = mat.group().toString();
				else return szHtml;
				
				return new String(bytes, 0, nByteIndex, szEncoding);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	class TrustAnyTrustManager implements X509TrustManager {					//和安全连接有关内容
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
	 
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
	 
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}
	
	class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;														//屏蔽掉验证，全部返回真
		}
	}
	
	private boolean saveUrlAs(String contentUrl, String fileName) {           //http协议抓取文件存到fileName
		// 此方法只能用于HTTP协议
		try {
			URL url = new URL(contentUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			DataInputStream in = new DataInputStream(
					connection.getInputStream());
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					fileName));
			byte[] buffer = new byte[4096];
			int count = 0;
			while ((count = in.read(buffer)) > 0) {
				out.write(buffer, 0, count);
			}
			out.close();
			in.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	} 

	private String getDocumentAt(String urlString)                //从url获取网页内容
	{ 
		 StringBuffer document = new StringBuffer(); 
		 	try { 
		 		URL url = new URL(urlString); 
		 		URLConnection conn = url.openConnection(); 
		 		BufferedReader reader = new BufferedReader(new InputStreamReader(conn. getInputStream())); 
		 		String line = null; 
		 		byte[] c = new byte[2];
		 		c[0]=0x0d;
		 		c[1]=0x0a;
		 		String c_string = new String(c);   
		 		while ( (line = reader.readLine()) != null) { 
		 			document.append( line+c_string ); 
		 		} 
		 		reader.close(); 
		 		} 
		 	catch (MalformedURLException e) { 
		 		System.out.println("Unable to connect to URL: " + urlString); 
		 		return null;
		 	} 
		 	catch (IOException e) { 
		 		System.out.println("IOException when connecting to URL: " + urlString); 
		 		return null;
		 	} 
		 	return document.toString(); 
	 } 
	
	
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
}
