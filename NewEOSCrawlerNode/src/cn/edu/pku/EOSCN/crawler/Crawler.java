package cn.edu.pku.EOSCN.crawler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

//import org.apache.jasper.tagplugins.jstl.core.ForEach;
//import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import cn.edu.pku.EOSCN.business.CrawlerBusiness;
import cn.edu.pku.EOSCN.crawlerTask.CrawlerTaskManager;
import cn.edu.pku.EOSCN.entity.Project;
import cn.edu.pku.EOSCN.exception.GoogleApiLimitExceededException;
/**
 * 所有爬虫需要实现该抽象类中的crawl方法
 * 子类中必须实现无参数的构造方法，urllist和project变量由爬虫管理模块统一set
 * @author 张灵箫
 *
 */
public abstract class Crawler extends Thread{
	public static final int SUCCESS = 1;
	public static final int WAITING = 0;
	public static final int ERROR = 2;
	public static final int IN_PROGRESS = 3;
	protected static final Logger logger = Logger.getLogger(Crawler.class.getName());
	private String taskuuid;
	private String resourceType;
	
	public boolean isServerTask = true;
	

	private String userName = null;
	private String password = null;
	private int status = WAITING;
	private int percentage = 0;
	protected Project project = null;
	public Project getProject() {
		return project;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<String> getUrlList() {
		return urlList;
	}

	public void setUrlList(List<String> urlList) {
		this.urlList = urlList;
	}

	protected List<String> urlList = new ArrayList<String>();
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @author 张灵箫
	 * 默认构造方法
	 * 测试时请调用带参数的构造方法
	 */
	public Crawler(){}

	/**
	 * @author 张灵箫
	 * 默认构造方法，普通爬虫不需要用户名密码
	 */
	public Crawler(Project project, List<String> urllist){
		this.project = project;
		this.urlList = urllist;
	}
	
	/**
	 * @author 张灵箫
	 * svn或bug可能需要用户名密码
	 * 
	 */
	public Crawler(String username, String pw, Project project, List<String> urllist){
		userName = username;
		password = pw;
		this.project = project;
		this.urlList = urllist;
	}
	
    @Override
    public final void run() {
		this.init();
        status = IN_PROGRESS;
        try {
            this.Crawl();
		} catch (Exception e) {
			e.printStackTrace();
			CrawlerTaskManager.removeFinishedTask(taskuuid);
			CrawlerTaskManager.reportErrorTask(taskuuid, e.getMessage());
		} finally {
			CrawlerBusiness.UpdateDataNum(project.getUuid(), resourceType);
		}
    } 
    

	public void addPercentageBy(int i) {
		percentage += i;
	}
    
	/**
	 * @author 张灵箫
	 * 取得爬虫时将调用该方法初始化爬虫
	 * 需要做初始化工作的爬虫请在这里实现初始化操作
	 * 否则也可以什么都不做
	 */
	abstract public void init();
	
	/**
	 * @author 张灵箫
	 * 每个爬虫线程将调用该方法
	 * @throws Exeption 
	 */
	abstract public void Crawl() throws Exception;

	public void setTaskuuid(String taskuuid) {
		this.taskuuid = taskuuid;
	}

	public String getTaskuuid() {
		return taskuuid;
	}
	
	public final void finish() {
		if (isServerTask) {
			CrawlerTaskManager.removeFinishedTask(taskuuid);
		}
		CrawlerTaskManager.reportCompletedTask(taskuuid);
	}
}
