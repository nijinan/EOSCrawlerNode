package cn.edu.pku.EOSCN.crawler;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpException;

import jcifs.smb.SmbException;

import cn.edu.pku.EOSCN.TestUtil;
import cn.edu.pku.EOSCN.DAO.RelativeWebDAO;
import cn.edu.pku.EOSCN.crawler.util.Doc.URLReader;
import cn.edu.pku.EOSCN.entity.Project;
import cn.edu.pku.EOSCN.entity.RelativeWeb;
import cn.edu.pku.EOSCN.exception.GoogleApiLimitExceededException;
import cn.edu.pku.EOSCN.storage.StorageUtil;

public class RelativeWebCrawlerVer2 extends Crawler{
	
	public RelativeWebCrawlerVer2(Project project, List<String> urlList) {
		super(project, urlList);
	}
	
	public RelativeWebCrawlerVer2() {
		super();
	}
	
	public void Crawl(){

		Random rd = new Random();
		//检索为blog类别，每次100个结果，语言为英文
		String GoogleSearchUrl = "https://www.google.com.hk/search?hl=en&tbm=blg&num=100&q=";
		int num=0;   //总的链接数
		String projectName = null;
		try {
			projectName = URLEncoder.encode(project.getName(), "utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		GoogleSearchUrl = GoogleSearchUrl+projectName;   //补充好检索内容
		
		
		

		for( int i=0;i<=4;i++)
		{
			String url = GoogleSearchUrl+"&start="+i;   //第i页
			int index=0;   //这页检索得到的url数量
			System.out.println("Iteration " + i + " begins: " + url);
			
			Pattern p =Pattern.compile("<h3 class=\"r\"><a href=\"(http[^\"]*)\"",Pattern.DOTALL);      //地址解析
			Pattern ti =Pattern.compile("<h3 class=\"r\"><a [^>]*>(.*?)</a></h3>",Pattern.DOTALL);      //标题解析
			String html=getDocumentAt(url);   //页面html
			Matcher m=p.matcher(html);
			Matcher mtitle=ti.matcher(html);
			while(m.find())
			{	
				mtitle.find();
				String webUrl = m.group(1);                 //依次找到所有的地址     
//				System.out.println(num+": "+webUrl);
				String title = mtitle.group(1);
				System.out.println(num+": \t"+webUrl+"  "+title);
				num++;
				index++;
				
				
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
				String urlContent = null;
				
				try {
					urlContent = URLReader.getHtmlContentWithTimeLimit(webUrl, 180000);
				} catch (HttpException e1) {
					System.out.println("CONNECT ERROR!");
				} catch (IOException e1) {
					System.out.println("CONNECT ERROR!");
				}
				if (urlContent == null) {
					continue;
				}
				
				int flag = StorageUtil.storeTextFileRemote(urlContent,saveAddr);   //将相关的html保存到指定地址
				if( flag == 0 )
				{
					System.out.println("page : "+webUrl+"  can't save!!!");
					saveAddr = "NONE";          //存储不成功的，用NONE来标示
				}
				
				
				web.setFilepath(saveAddr);
				web.setProjectuuid(project.getUuid());
				web.setTitle(title);
				web.setUrl(webUrl);
				try {
					RelativeWebDAO.insertRelativeWeb(web);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}	
			//System.out.println(index+"b   ");
			if( index < 100 && index != 0 )       //全部找出来了
			{
				break;
			}
//			
			try {
				Thread.sleep(5000+rd.nextInt(7000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(num+" urls crawled!");
		finish();
	}
	
	//测试使用
	public static void main(String[] args) throws ClassNotFoundException, SQLException, GoogleApiLimitExceededException
	{
		//JDBCPool.initPool();
		Project p = TestUtil.getLuceneProject();
		List<String> urllist = new LinkedList<String>();
		RelativeWebCrawlerVer2 webget = new RelativeWebCrawlerVer2(p ,urllist);
		webget.Crawl();
	}
		
	
	
	public String getDocumentAt(String urlString)                //从url获取网页内容
	{ 
		 StringBuffer document = new StringBuffer(); 
		 	try { 
		 		URL url = new URL(urlString); 
		 		URLConnection conn = url.openConnection(); 
		 		
		 		conn.setConnectTimeout(180000);
		 		conn.setReadTimeout(600000);
				String headUrl[] ={"IBM WebExplorer /v0.94', 'Galaxy/1.0 [en] (Mac OS X 10.5.6; U; en)","Opera/9.27 (Windows NT 5.2; U; zh-cn)","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20130406 Firefox/23.0", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:18.0) Gecko/20100101 Firefox/18.0",  "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)", "Opera/9.80 (Windows NT 6.0) Presto/2.12.388 Version/12.14",  "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko)"  ,  "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.0; Trident/5.0; TheWorld)"}; 
				
				Random rd1 = new Random();
				int randomIndex = rd1.nextInt(headUrl.length-1);
				//System.out.println(headUrl[randomIndex]);
				conn.setRequestProperty("User-Agent", headUrl[randomIndex]);
		 		
		 		
		 		BufferedReader reader = new BufferedReader(new InputStreamReader(conn. getInputStream(),"utf-8")); 
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
		 	} 
		 	catch (IOException e) { 
		 		System.out.println("IOException when connecting to URL: " + urlString); 
		 	}
		 	return document.toString(); 
	 }

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	} 	

}
