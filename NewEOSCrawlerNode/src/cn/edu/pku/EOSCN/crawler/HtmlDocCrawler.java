package cn.edu.pku.EOSCN.crawler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.Queue;  
import java.util.LinkedList; 

import javax.print.attribute.standard.Compression;

import jcifs.smb.SmbException;

import org.apache.commons.httpclient.HttpException;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import com.sun.org.apache.bcel.internal.generic.NEW;

import cn.edu.pku.EOSCN.DAO.DocumentationDao;
import cn.edu.pku.EOSCN.DAO.JDBCPool;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.RemoteFileOperation;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLExtractor;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLReader;
import cn.edu.pku.EOSCN.entity.CrawlerURL;
import cn.edu.pku.EOSCN.entity.Documentation;
import cn.edu.pku.EOSCN.entity.Project;
import cn.edu.pku.EOSCN.storage.StorageUtil;

public class HtmlDocCrawler extends Crawler{
	private final static int maxDepth=3;	//最大爬取深度
	
	//各种文档的类型 subType 
	private final static int DOCUMENTATION=0;
	private final static int WIKI=1;
	private final static int FAQ=2;
	private final static int USERGUIDE=3;
	private final static int DEVELOP=4;
	private final static int RETRY_TIME=3;
	
	private HashSet<String> visitURLSet=new HashSet<String>();	//存放已经访问过的URL 
	private Queue<CrawlerURL> queue=new LinkedList<CrawlerURL>();	//队列  放新获得的URL
	private String homeURL=new String();
	/*
	 * author: Luo Yuxiang
	 */
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
	
	
	@Override
	public void Crawl() {
		
		
		this.setStatus(IN_PROGRESS);
		System.out.println("**********************************************************************************************************************");
		System.out.println("开始爬取项目"+project.getName()+".........");
		DocumentationDao documentationDao=new DocumentationDao();
		
		
		// TODO Auto-generated method stub
		CrawlerURL tempCrawlerURL=new CrawlerURL();
		CrawlerURL newCrawlerURL;
		String tempURL;
		String htmlContent=null;
		List<CrawlerURL> tempURLList=null;
	//	URLReader urlReader=new URLReader();
    //  URLExtractor urlExtractor=new URLExtractor();
		String projectFilePath=null;
		try {
			projectFilePath = StorageUtil.getDocumentationsFilePath(project);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SmbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String filePath;
		String[] tempSplit;
		Documentation documentation;
		queue=new LinkedList<CrawlerURL>();
		visitURLSet=new HashSet<String>();
		System.out.println("**********************************************************************************************************************");
		for (int i=0;i<urlList.size();i++)
		{
			tempCrawlerURL=new CrawlerURL();
			tempCrawlerURL.setDepth(0);
			tempCrawlerURL.setUrl(urlList.get(i));
			tempCrawlerURL.setDocName(project.getName());
			queue.add(tempCrawlerURL);
			
		}
		System.out.println(queue.size());
		while (!queue.isEmpty())
		{
			newCrawlerURL=queue.poll();
			if (newCrawlerURL==null || newCrawlerURL.getUrl()==null || visitURLSet.contains(newCrawlerURL.getUrl())  ||  newCrawlerURL.getDepth()>maxDepth)	//已经访问过或者尝试大于最大限制，则不处理该URL
				continue;
			tempURL=newCrawlerURL.getUrl();
			visitURLSet.add(tempURL);
			
			
			if (tempURL.contains("issues.") || tempURL.contains(".mbox") || tempURL.contains("/changes/")) {
				continue;
			}
			//过滤无关的网页
			
			String simpleProjectNameString = project.getName();
			simpleProjectNameString = simpleProjectNameString.replace("Apache ", "");
			simpleProjectNameString = simpleProjectNameString.replace(" (incubating)", "");
			String[] namelist = simpleProjectNameString.split(" ");
			simpleProjectNameString = simpleProjectNameString.replace(" ", "");
			boolean found = false;
			
			if (!(tempURL.contains(simpleProjectNameString)||tempURL.contains(simpleProjectNameString.toLowerCase())
					|| tempURL.contains(simpleProjectNameString.toUpperCase()))) {
				for (String name : namelist) {
					System.out.println(name);
					if (tempURL.contains(name)||tempURL.contains(name.toLowerCase())
							|| tempURL.contains(name.toUpperCase())) {
						found = true;
						break;
					}
				}
				if (!found) {
					continue;
				}
			}
			
			//爬取文本文件
			if (tempURL.endsWith(".txt")||tempURL.endsWith(".pdf")||tempURL.endsWith(".log"))
			{
				documentation=new Documentation(project.getUuid());
				documentation.setDocName(newCrawlerURL.getDocName());
				documentation.setUrl(tempURL);
				//System.out.println(tempURL);
				tempSplit=tempURL.split("\\.");
				//System.out.println(tempSplit.length);
				//System.out.println("prjectFilePath"+projectFilePath);
				filePath=projectFilePath+documentation.getDocName()+"_"+documentation.getUuid()+"."+tempSplit[tempSplit.length-1];
				String localFilePath = Config.getTempDir()+"/"+documentation.getDocName()+"_"+documentation.getUuid()+"."+tempSplit[tempSplit.length-1];
				saveUrlAs(tempURL, localFilePath);
				System.out.println("远程存储文件位置"+filePath);
				documentation.setFilePath(filePath);
				documentation.setDocName(newCrawlerURL.getDocName());
				RemoteFileOperation.smbPutFullRemoteSite(filePath, localFilePath);
				try {
					documentationDao.insertDocumentation(documentation);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
				
			}
			//根据http header判断文件类型
			try {
				String urlTypeString = URLReader.getUrlType(newCrawlerURL.getUrl());
				if (urlTypeString == null || !urlTypeString.contains("text")) {
					continue;
				}
			} catch (IOException e2) {
				continue;
			}
			
			int n = RETRY_TIME;
			while(n > 0) {
				try {
//					System.out.println("NEWURL:\t"+newCrawlerURL.getUrl());
					n--;
					htmlContent=URLReader.getHtmlContentWithTimeLimit(newCrawlerURL.getUrl(), 600000);
					if (htmlContent != null) {
						break;
					}
				} catch (HttpException e1) {
					continue;
				} catch (IOException e1) {
					continue;
				}
			}
			if (htmlContent == null) {
				logger.info(newCrawlerURL.getUrl()+" 访问失败！");
				File failedURLFile = new File("d:/failedURLFile");
				try {
					FileWriter fWriter = new FileWriter(failedURLFile, true);
					fWriter.write(newCrawlerURL.getUrl() + "\n");
					fWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				//存储爬取到的网页
				logger.info("爬取"+newCrawlerURL.getUrl()+" 中....");
				documentation=new Documentation(project.getUuid());
				documentation.setDocName(newCrawlerURL.getDocName());
				documentation.setUrl(newCrawlerURL.getUrl());
				//documentation.setProjectUuid(project.getUuid());
				//System.out.println("prjectFilePath"+projectFilePath);
				filePath=projectFilePath+documentation.getDocName()+"_"+documentation.getUuid()+".html";
				System.out.println("存储文件位置"+filePath);
				StorageUtil.storeTextFileRemote(htmlContent, filePath);
				documentation.setFilePath(filePath);
				
				//System.out.println(htmlContent);
				try {
					documentationDao.insertDocumentation(documentation);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				//*********************************************************************//
				if (newCrawlerURL.getDepth()>=maxDepth) continue;	//当前深度已经等于最大深度  则不再爬取它包含的URL
				//System.out.println("Start to get urls");
				tempURLList=URLExtractor.getAllUrls(htmlContent, newCrawlerURL.getUrl(),homeURL);
				//System.out.println("get URL number:"+tempURLList.size());
				for (int i=0;i<tempURLList.size();i++)
				{
					if (visitURLSet.contains(tempURLList.get(i))) continue;
					tempCrawlerURL=new CrawlerURL();
					tempCrawlerURL.setUrl(tempURLList.get(i).getUrl());
					tempCrawlerURL.setDocName(tempURLList.get(i).getDocName());
				//	System.out.println(tempURLList.get(i));
					tempCrawlerURL.setDepth(newCrawlerURL.getDepth()+1);
					queue.add(tempCrawlerURL);
				}
			}
			
			
		}
		System.out.println("**********************************************************************************************************************");
		System.out.println("项目 "+project.getName()+" 爬取完成!");
		this.setStatus(SUCCESS);
		this.finish();
		
	}
	
	public HtmlDocCrawler() {}
	
	public HtmlDocCrawler(Project project, List<String> urllist,String homeURL){
		this.project = project;
		this.urlList = urllist;
		this.homeURL=homeURL;
	}
	public static void main(String args[]) throws IOException {
		/* test Queue
		Queue<String> queue=new LinkedList<String>();
		queue.add("aaa");
		queue.add("bbb");
		queue.add("ccc");
		System.out.println(queue);
		queue.poll();
		System.out.println(queue);
		queue.poll();
		queue.poll();
		System.out.println(queue);
		*/
		//仅测试用
		String homeURL;
		File failedURLFile = new File("d:/failedURLFile");
		FileWriter fWriter = null;
		fWriter = new FileWriter(failedURLFile);
		fWriter.write("This is the list of failed URLs" + "\n");
		fWriter.close();
		
		try {
			JDBCPool.initPool();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> urlList=new ArrayList<String>();
		Project project=new Project();
		project.setUuid("Apache Lucene 4_6 20140310");
		project.setName("Apache Lucene 4_6");
		System.out.println("projectuuid="+project.getUuid());
		urlList.add("http://lucene.apache.org/core/4_6_0/index.html");
		homeURL=urlList.get(0);
		//urlList.add("http://mprc.pku.edu.cn/courses/architecture/spring2013/compress.log");
		HtmlDocCrawler htmlDocCrawler=new HtmlDocCrawler(project,urlList,homeURL);
	//	htmlDocCrawler.saveUrlAs("http://mprc.pku.edu.cn/courses/architecture/spring2013/compress.log", "d:/Compression.log");
		htmlDocCrawler.Crawl();
		  
	}


	@Override
	public void init() {
		homeURL = urlList.get(0);
		
	}
}
