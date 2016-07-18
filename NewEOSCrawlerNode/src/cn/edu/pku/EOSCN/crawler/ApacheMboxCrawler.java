package cn.edu.pku.EOSCN.crawler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.EOSCN.TestUtil;
import cn.edu.pku.EOSCN.business.CrawlerBusiness;
import cn.edu.pku.EOSCN.business.InitBusiness;
import cn.edu.pku.EOSCN.crawler.mailcrawlerthread.CrawlerConfig;
import cn.edu.pku.EOSCN.crawler.mailcrawlerthread.CrawlerController;
import cn.edu.pku.EOSCN.crawler.mailcrawlerthread.MainCrawler;
import cn.edu.pku.EOSCN.crawler.mailcrawlerthread.URLManager;
import cn.edu.pku.EOSCN.crawler.util.mbox.MBoxURLExtractor;
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
* 爬虫已经改为多线程实现 
* TODO 对于已经爬取过的地址做判断（类似于断点续传 后期实现）
* TODO 每一封邮件的大小需要做限定
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-5-24 下午9:45:11
* @修改：张灵箫
* 修改内容：将构造函数中的初始化工作移到init中，将System.getProperty("user.dir")+"/data"临时
* 文件夹的初始化放入init中，防止爬取过程中报错
*/

public class ApacheMboxCrawler extends Crawler {
	protected static final Logger logger = Logger.getLogger(ApacheMboxCrawler.class.getName());
	
	protected CrawlerController controller;
	protected CrawlerConfig config;
	protected URLManager urlManager = new URLManager();
	private static final int MAX_CRAWLER_NUM = 1;
	
	public static int getMaxCrawlerNum() {
		return MAX_CRAWLER_NUM;
	}

	public ApacheMboxCrawler(Project project, List<String> urlList) {
		super(project, urlList);
		init();
		
	}
	
	public ApacheMboxCrawler() {
		super();
	}

	public int getStatus() {
		if(controller.isFinished()) {
			return SUCCESS;
		}
		return IN_PROGRESS;
	}
	@Override
	public void Crawl() {
		this.controller.start(new MainCrawler(), MAX_CRAWLER_NUM, true);
	}
	
	public static void main(String args[]) {
		//测试一下
		InitBusiness.initEOS();
		Project p = TestUtil.getLuceneProject();
		List<String> list = new ArrayList<String>();
		list.add("http://mail-archives.apache.org/mod_mbox/lucene-java-user/");
		
		ApacheMboxCrawler mailingListCrawler = new ApacheMboxCrawler(p, list);
		mailingListCrawler.Crawl();
	}

	@Override
	public void init() {
		config = new CrawlerConfig();
		logger.info("Init the url waiting queue......");
		for(String url : urlList) {
			ArrayList<String> mboxURLList = new ArrayList<String>();
			mboxURLList = new MBoxURLExtractor(url).getMBoxURLList();
			for(String mboxUrlString : mboxURLList) {
				urlManager.insertWaitingQueue(mboxUrlString);
			}
		}
		controller = new CrawlerController(this,config, urlManager, project);
		this.setStatus(IN_PROGRESS);
	}
	
}
