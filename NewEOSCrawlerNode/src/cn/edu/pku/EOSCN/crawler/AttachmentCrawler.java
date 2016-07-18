package cn.edu.pku.EOSCN.crawler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

import cn.edu.pku.EOSCN.crawler.util.mbox.*;
import cn.edu.pku.EOSCN.entity.Project;

public class AttachmentCrawler extends Crawler{
	
	public AttachmentCrawler(Project project, List<String> urlList) {
		super(project, urlList);
	}
	
	public AttachmentCrawler() {
		super();
	}
	
	/* 
	 * 对urllist中的每个网页解析附件的下载地址
	 * 解析以pdf和txt附件为主
	 * 将附件保存在d：//
	 */
	public void Crawl()
	{
		int returnValue = WAITING;
		int pdfNum=0;
		//AttachmentCrawler crawler = new AttachmentCrawler();
		Iterator<String> it = urlList.iterator();                            
		String content=null;
		while(it.hasNext())
		{
			String url = it.next();                                                        
			pdfNum = 0;
			Pattern pdfPattern =Pattern.compile("href=\"([^\"]*.pdf)\"",Pattern.DOTALL);              //相关pdf解析  
			Pattern txtPattern =Pattern.compile("href=\"([^\"]*.txt)\"",Pattern.DOTALL);              //相关txt解析  
			Matcher pdfUrlList=pdfPattern.matcher(getDocumentAt(url));   
			Matcher txtUrlList=txtPattern.matcher(getDocumentAt(url));   
			//pdf文件下载
			while( pdfUrlList.find() )
			{	
				String attachmentUrl = pdfUrlList.group(1);               
				String fileName = attachmentUrl.substring(attachmentUrl.lastIndexOf("/"));            //文件名
				String filePath = "d:/";                                                              //存储地址
				boolean flag = saveUrlAs(attachmentUrl, filePath + fileName);                 //完成存储过程
				System.out.println("Run ok! Get " + fileName + " " + flag);                            //成功与否输出
				if ( flag == false )
				{
					returnValue = ERROR;
				}
				else
				{
					pdfNum++;
				}
			}	
			//txt文件下载
			while( txtUrlList.find() )
			{
				String attachmentUrl = txtUrlList.group(1);               
				String fileName = attachmentUrl.substring(attachmentUrl.lastIndexOf("/"));         
				String filePath = "d:/";                                                            
				boolean flag = saveUrlAs(attachmentUrl, filePath + fileName);                
				System.out.println("Run ok! Get " + fileName + flag);                       
				if ( flag == false )
				{
					returnValue = ERROR;
				}
				else
				{
					pdfNum++;
				}
			}	
		}	
		System.out.println(pdfNum + " files has been downloaded");     
		if( returnValue != ERROR )
		{
			returnValue = SUCCESS;
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
		 	} 
		 	catch (IOException e) { 
		 		System.out.println("IOException when connecting to URL: " + urlString); 
		 	} 
		 	return document.toString(); 
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
	
	//测试使用
	public static void main(String[] args)
	{
		/*
		List<String> urllist = new LinkedList<String>();
		for( int i=1; i <= 3 ; i++)
		{
			String url = JOptionPane.showInputDialog("input");
			urllist.add(url);
		}
		AttachmentCrawler webget = new AttachmentCrawler(new Project(),urllist);
		webget.Crawl();
		*/
		System.out.println("IOException when connecting to URL: " ); 
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
}
