package cn.edu.pku.EOSCN.crawler.htmlcrawlerthread;


import java.util.List;



import org.apache.log4j.Logger;



import cn.edu.pku.EOSCN.crawler.Crawler;
import cn.edu.pku.EOSCN.crawler.mailcrawlerthread.CrawlerConfig;
import cn.edu.pku.EOSCN.entity.CrawlerURL;
import cn.edu.pku.EOSCN.entity.Project;


public class HtmlDocCrawler extends Crawler{
protected static final Logger logger = Logger.getLogger(HtmlDocCrawler.class.getName());
	
	protected CrawlerController controller;
	protected CrawlerConfig config;
	protected URLManager urlManager = new URLManager();
	protected String homeURL = "";
	private static final int MAX_CRAWLER_NUM = 100;
	
	public static int getMaxCrawlerNum() {
		return MAX_CRAWLER_NUM;
	}

	public HtmlDocCrawler(Project project, List<String> urlList, String homeURL) {
		super(project, urlList);
		this.homeURL = homeURL;
		init();
		
	}
	
	public HtmlDocCrawler() {
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
		this.controller.start(new MainHtmlDocCrawler(), MAX_CRAWLER_NUM, false);
	}
	
	@Override
	public void init() {
		config = new CrawlerConfig();
		logger.info("Init the url waiting queue......");
		for(String url : urlList) {
			CrawlerURL crawlerURL=new CrawlerURL();
			crawlerURL.setDepth(0);
			crawlerURL.setUrl(url);
			urlManager.insertWaitingQueue(crawlerURL);
			
		}
		controller = new CrawlerController(this,config, urlManager, project);
		this.setStatus(IN_PROGRESS);
	}
	
}
