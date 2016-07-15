package cn.edu.pku.EOSCN.crawlerTask;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.helpers.CyclicBuffer;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import cn.edu.pku.EOSCN.DAO.CrawlerTaskDao;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.Crawler;
import cn.edu.pku.EOSCN.entity.CrawlerTask;

/**
 * 线程池类, 为每个爬虫单独维护一个线程
 * @author 张灵箫
 *
 */
public class CrawlerTaskManager {
	protected static final Logger logger = Logger.getLogger(ThisReference.class.getName());
	private static final int MAX_ONGOING_TASK_NUM = Integer.parseInt(Config.getMaxTaskNum());
	private static ExecutorService threadPool = null;
	private static List<Crawler> ongoingTasklist = new ArrayList<Crawler>();
	private static List<Crawler> waitingTasklist = new ArrayList<Crawler>();
	
	
	public static void initCrawlerTaskManager() {
		if (threadPool != null) {
			return;
		}
		System.out.println("initiating crawler pool...");
		threadPool = Executors.newFixedThreadPool(MAX_ONGOING_TASK_NUM);
	}
	
	/**
	 * 每个爬虫单开一个线程，用该方法执行该线程
	 * @author 张灵箫
	 * @param t
	 */
	public static synchronized void addCrawlerTask(Crawler crawler) {
//		if (threadPool == null) {
//			initCrawlerPool();
//		}
		if (ongoingTasklist.size() < MAX_ONGOING_TASK_NUM) {
			startCrawlerTask(crawler);
		} else {
			waitingTasklist.add(crawler);
	        logger.info(crawler.getClass().getName() + " for project \"" + crawler.getProject().getName() + "\" is in the waiting list");
		}

	}

	private static void startCrawlerTask(Crawler crawler) {
		ongoingTasklist.add(crawler);
		threadPool.execute(crawler);
		logger.info(crawler.getClass().getName() + " for project \"" + crawler.getProject().getName() + "\" is crawling");
	}
	
	public static void printTaskStatus() {
        for (Crawler c : ongoingTasklist) {
			System.out.println(c.getTaskuuid() + " is " + c.getStatus());
		}
	}
	public static void main(String[] args) {
		
	}

	public static synchronized void removeFinishedTask(String taskuuid) {
        for (int i = 0; i < ongoingTasklist.size(); i++) {
        	Crawler c = ongoingTasklist.get(i);
			if (c.getTaskuuid().equals(taskuuid)) {
				ongoingTasklist.remove(i);
				NotifyWaitingList();
				break;
			}
		}// TODO Auto-generated method stub
        //printTaskStatus();
	}

	private static void NotifyWaitingList() {
		if (waitingTasklist.size() > 0) {
			Crawler crawler =  waitingTasklist.remove(0);
			ongoingTasklist.add(crawler);
			threadPool.execute(crawler);
		    logger.info(crawler.getClass().getName() + " for project \"" + crawler.getProject().getName() + "\" is crawling");
		}
	}

	public static void reportCompletedTask(String taskuuid){
//		System.out.println("123934872394872394729384%^$^$%^#$!@#$@%#^#^$%^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		try {
			CrawlerTaskDao.updateTaskStatus(taskuuid, CrawlerTask.SUCCESS, new Date(), null);
		} catch (SQLException e) {
			logger.info("database error: task " + taskuuid + "completed but not reported!");
			e.printStackTrace();
		}
	}
	
	public static void reportErrorTask(String taskuuid, String errorLog){
		try {
			CrawlerTaskDao.updateTaskStatus(taskuuid, CrawlerTask.ERROR, new Date(), errorLog);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int getTaskStatus(String uuid) {
		for (Crawler ct : ongoingTasklist) {
			if (ct.getTaskuuid().equals(uuid)) {
				return CrawlerTask.IN_PROGRESS;
			}
		}
		for (Crawler ct : waitingTasklist) {
			if (ct.getTaskuuid().equals(uuid)) {
				return CrawlerTask.WAITING;
			}
		}
		return CrawlerTask.ERROR;
	}
}
