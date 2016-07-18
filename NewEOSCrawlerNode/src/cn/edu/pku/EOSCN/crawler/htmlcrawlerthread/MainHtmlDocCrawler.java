package cn.edu.pku.EOSCN.crawler.htmlcrawlerthread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
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

import org.apache.log4j.Logger;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import com.sun.org.apache.bcel.internal.generic.NEW;


import cn.edu.pku.EOSCN.DAO.DocumentationDao;
import cn.edu.pku.EOSCN.DAO.JDBCPool;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.Crawler;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.RemoteFileOperation;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLExtractor;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.URLReader;
import cn.edu.pku.EOSCN.entity.CrawlerURL;
import cn.edu.pku.EOSCN.entity.Documentation;
import cn.edu.pku.EOSCN.entity.Project;
import cn.edu.pku.EOSCN.storage.StorageUtil;

public class MainHtmlDocCrawler  implements Runnable{
	private final static int maxDepth=4;	//最大爬取深度
	protected static final Logger logger = Logger.getLogger(MainHtmlDocCrawler.class.getName());
	
	private int myID;
	

	private CrawlerController controller;
	
	private Thread myThread;
	
	private URLManager urlManager;
	
	private boolean isWaitingForNewURL;
	
	
	private Project project;
	
	//各种文档的类型 subType 
	private final static int DOCUMENTATION=0;
	private final static int WIKI=1;
	private final static int FAQ=2;
	private final static int USERGUIDE=3;
	private final static int DEVELOP=4;
	
	private HashSet<String> visitURLSet=new HashSet<String>();	//存放已经访问过的URL 
	private Queue<CrawlerURL> queue=new LinkedList<CrawlerURL>();	//队列  放新获得的URL
	private String homeURL=new String();
	
	
	public void init(int myID,CrawlerController controller, Project project) {
		 this.myID = myID;
		 this.controller = controller;
		 this.urlManager = controller.getUrlManager();
		 this.isWaitingForNewURL = false;
		 this.project = project;
	}
	
	public int getMyID() {
		return myID;
	}


	public void setMyID(int myID) {
		this.myID = myID;
	}


	public CrawlerController getController() {
		return controller;
	}


	public void setController(CrawlerController controller) {
		this.controller = controller;
	}


	public Thread getMyThread() {
		return myThread;
	}


	public void setMyThread(Thread myThread) {
		this.myThread = myThread;
	}


	public URLManager getUrlManager() {
		return urlManager;
	}


	public void setUrlManager(URLManager urlManager) {
		this.urlManager = urlManager;
	}


	public boolean isWaitingForNewURL() {
		return isWaitingForNewURL;
	}


	public void setWaitingForNewURL(boolean isWaitingForNewURL) {
		this.isWaitingForNewURL = isWaitingForNewURL;
	}


	public HashSet<String> getVisitURLSet() {
		return visitURLSet;
	}


	public void setVisitURLSet(HashSet<String> visitURLSet) {
		this.visitURLSet = visitURLSet;
	}


	public Queue<CrawlerURL> getQueue() {
		return queue;
	}


	public void setQueue(Queue<CrawlerURL> queue) {
		this.queue = queue;
	}


	public String getHomeURL() {
		return homeURL;
	}


	public void setHomeURL(String homeURL) {
		this.homeURL = homeURL;
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
	
	
	private void processURL(CrawlerURL assignedURL) 
	{
		DocumentationDao documentationDao = new DocumentationDao();
		Documentation documentation  = null;
		
		CrawlerURL tempCrawlerURL=new CrawlerURL();
		CrawlerURL newCrawlerURL = assignedURL;
		String tempURL;
		String htmlContent=null;
		List<CrawlerURL> tempURLList=null;
	    if (newCrawlerURL.getDepth()>maxDepth)	//已经访问过或者尝试大于最大限制，则不处理该URL
	    	return;
	    tempURL=newCrawlerURL.getUrl();
	    String[] tempSplit;
	    String filePath;
	    String projectFilePath = "";
	    try {
			projectFilePath = StorageUtil.getDocumentationsFilePath(project);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			
			e1.printStackTrace();
			return;
		} catch (SmbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
				
		}
		
		
		htmlContent=URLReader.getHtmlContent(newCrawlerURL.getUrl());
		System.out.println("爬取"+newCrawlerURL.getUrl()+" 中....");
		if (htmlContent!=null)	//可访问  有内容
		{
			//存储爬取到的网页
			
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
			if (newCrawlerURL.getDepth()>=maxDepth) return;	//当前深度已经等于最大深度  则不再爬取它包含的URL
			//System.out.println("Start to get urls");
			tempURLList=URLExtractor.getAllUrls(htmlContent, newCrawlerURL.getUrl(),homeURL);
			this.urlManager.insertVisitedQueue(newCrawlerURL.getUrl());
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
				this.urlManager.insertWaitingQueue(tempCrawlerURL);
			}
		}
		
		
	}
		
	
	@Override
	public void run() {
		while(true) {
			this.isWaitingForNewURL = true;
			CrawlerURL assignedURL = (this.urlManager).getURLFromWaitingQueue();
			if(assignedURL == null) return;
			if (assignedURL.getUrl() == null || this.urlManager.checkVisited(assignedURL.getUrl())) continue;
			this.isWaitingForNewURL = false;
			logger.info("now is process URL: "+assignedURL);
			processURL(assignedURL);
		}
		
	}
	
	
	public static void main(String args[]) {
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
		project.setUuid("b7914db3-caa7-4d70-96cd-bd4b5b4ed029");
		project.setName("Apache Lucene");
		System.out.println("projectuuid="+project.getUuid());
		urlList.add("http://lucene.apache.org/core/");
		homeURL=urlList.get(0);
		//urlList.add("http://mprc.pku.edu.cn/courses/architecture/spring2013/compress.log");
		HtmlDocCrawler htmlDocCrawler=new HtmlDocCrawler(project,urlList,homeURL);
	//	htmlDocCrawler.saveUrlAs("http://mprc.pku.edu.cn/courses/architecture/spring2013/compress.log", "d:/Compression.log");
		htmlDocCrawler.Crawl();
		
	}





	

	


	
}
