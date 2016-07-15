package cn.edu.pku.EOSCN.crawler.mailcrawlerthread;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


import cn.edu.pku.EOSCN.DAO.EmailDao;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.mbox.MBoxParser;
import cn.edu.pku.EOSCN.crawler.util.mbox.ReadFile;
import cn.edu.pku.EOSCN.entity.Email;
import cn.edu.pku.EOSCN.entity.Project;

/**   
* @Title: MailingListCrawler.java
* @Package cn.edu.pku.EOS.crawler
* @Description:  实现对邮件列表爬取的爬虫 
* TODO 由于对于邮件列表的类型不确定 现在暂时实现对类型为mbox的邮件列表进行爬取存储 
* mbox http://en.wikipedia.org/wiki/Mbox
* 邮件列表要存储的信息有
* 1. 发件人name及地址 sender 
* 2. 收件人name及地址 receiver
* 3. 发送日期 sendDate
* 4. 邮件内容 emailContent
* 5. 邮件主题 emailSubject
* TODO（暂时不考虑富文本格式邮件 后期改进
* TODO 6. 附件暂时不考虑 后期改进
* TODO 初期对爬虫类只使用单线程爬取 后期改为多线程
* TODO 对于已经爬取过的地址做判断（类似于断点续传 后期实现）
* TODO 每一封邮件的大小需要做限定
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-5-24 下午9:45:11
*/

/**   
* @Title: MainCrawler.java
* @Package cn.edu.pku.EOS.crawler.thread
* @Description: 主爬虫
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-6-2 下午2:20:03
*/

public class MainCrawler implements Runnable {
	protected static final Logger logger = Logger.getLogger(MainCrawler.class.getName());
	
	private int myID;
	

	private CrawlerController controller;
	
	private Thread myThread;
	
	private URLManager urlManager;
	
	private boolean isWaitingForNewURL;
	
	private Project project;
	
	public void init(int myID,CrawlerController controller, Project project) {
		 this.myID = myID;
		 this.controller = controller;
		 this.urlManager = controller.getUrlManager();
		 this.isWaitingForNewURL = false;
		 this.project = project;
	}
	
	@Override
	public void run() {
		while(true) {
			this.isWaitingForNewURL = true;
			String assignedURL = this.urlManager.getURLFromWaitingQueue();
			if(assignedURL == null) return;
			this.isWaitingForNewURL = false;
			logger.info("now is process URL: "+assignedURL);
			processURL(assignedURL);
		}
		
	}
	
	/**
	* @Title: splitMbox
	* @Description: 将Mbox文件分割成多封单独的email 存放到list中
	* @param @param mbox
	* @param @return    设定文件
	* @return ArrayList<String>    返回类型
	* @throws
	*/
	
	private ArrayList<String> splitMbox(File mbox) {
		String fileContent = ReadFile.read_file(mbox.getAbsolutePath());
//		String reg = FromLinePatterns.DEFAULT;
		String lines[] = fileContent.split("\n");
		ArrayList<String> emails = new ArrayList<String>();
		int preIndex = 0;
		int curIndex = -1;
		for(int i = 0; i < lines.length; i++) {
			if(lines[i].startsWith("From ")) {
				curIndex = i;
				StringBuilder sb = new StringBuilder();
				for(int j = preIndex; j < curIndex; j++) {
					sb.append(lines[j]+"\n");
				}
				emails.add(sb.toString());
				preIndex = curIndex;
			}
			
		}
		if(curIndex != -1) {
			StringBuilder sb = new StringBuilder();
			for(int i = curIndex; i < lines.length; i++) {
				sb.append(lines[i]+"\n");
			}
			emails.add(sb.toString());
		}
//		System.out.println(emails.size());
		return emails;
	}
	
	/**
	* @Title: downloadFromUrl
	* @Description: 从URL保存内容到文件中
	* @param @param url
	* @param @return    设定文件
	* @return File    返回类型
	* @throws
	*/
	
	private File downloadFromUrl(String url) {
		//TODO 该路径是否需要修改？
		String tempPath = Config.getTempDir() + "/mail";
		File tempFile = createFile(tempPath);
		logger.info("download url content to file path : " + tempPath);
		
		try {
			URL httpurl = new URL(url);
			FileUtils.copyURLToFile(httpurl,tempFile);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempFile;
	}
	
	private File createFile(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String[] list = dir.list();
		
		if(list != null && list.length >= 300) {
			for(String str : list) {
				File deleteFile = new File(str);
				deleteFile.delete();
			}
		}
		
		String randonName = UUID.randomUUID().toString();
		String filePath = path + "/" + randonName + ".txt";
		File file = new File(filePath);
		while(file.exists()) {
			randonName = UUID.randomUUID().toString();
			filePath = path + "/" + randonName + ".txt";
			logger.info("temp file path : " + filePath );
			file = new File(filePath);
		}
		
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return file;
	}
	
	public void processURL(String assignedURL) {
		long start = System.currentTimeMillis();
		
		File mbox = downloadFromUrl(assignedURL);
//		File mbox = new File("D:/11.txt");
		ArrayList<String> emails = splitMbox(mbox);
		
		for(String email : emails) {
			if(email.length() < 100) continue;
			MBoxParser parser = new MBoxParser();
			try {
				com.auxilii.msgparser.Message msg = parser.parse(email);
//				System.out.println("From: "+msg.getFromName()+" "+msg.getFromEmail());
//				System.out.println("To: "+msg.getToName()+" "+msg.getToEmail());
//				System.out.println("Subject: "+msg.getSubject());
//				System.out.println("Date: "+msg.getDate());
				String fromEmail = msg.getFromEmail();
				String fromEmailName = msg.getFromName();
				String toEmail = msg.getToEmail();
				String toEmailName = msg.getToName();
				String subject = msg.getSubject();
				Date date = msg.getDate();
				String content = msg.getBodyText();
				if(fromEmail != null && toEmail != null && subject != null && date != null && content != null) {
					Email e = new Email(project.getUuid());
//					Email e = new Email("b7914db3-caa7-4d70-96cd-bd4b5b4ed029");
					e.setFromMail(fromEmail);
					e.setFromMailName(fromEmailName);
					e.setToMail(toEmail);
					e.setToMailName(toEmailName);
					e.setSubject(subject);
					e.setContent(content);
					e.setDate(date);
//				    logger.info(e.toString());
					EmailDao emailDao = new EmailDao();
					emailDao.insertEmail(e);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	
		long end = System.currentTimeMillis();
	
		logger.info("Crawler time of URL : "+assignedURL + " is "+(start-end) +"milis");
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
	public int getMyID() {
		return myID;
	}

	public void setMyID(int myID) {
		this.myID = myID;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
}
