package cn.edu.pku.EOSCN.crawler.htmlcrawlerthread;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

import cn.edu.pku.EOSCN.entity.CrawlerURL;

/**   
* @Title: URLManager.java
* @Package cn.edu.pku.EOS.crawler.thread
* @Description: 管理所获得的所有URL
* @author jinyong     jinyonghorse@hotmail.com  
* @date 2013-6-2 下午2:22:07
* @revised by zhebang
*/

public class URLManager {
	protected Logger logger = Logger.getLogger(URLManager.class.getName());
	
	private  Queue<CrawlerURL> waitingQueue;
	private  Queue<CrawlerURL> runningQueue;
	private  Set<String> visitedSet;
	private  Queue<CrawlerURL> unreachableQueue;
	public URLManager() {
		waitingQueue = new LinkedBlockingDeque<CrawlerURL>();
		runningQueue = new LinkedBlockingDeque<CrawlerURL>();
		visitedSet = Collections.synchronizedSet(new HashSet<String>());
		unreachableQueue = new LinkedBlockingDeque<CrawlerURL>();
	}
	  
	public synchronized CrawlerURL getURLFromWaitingQueue() {
		if(waitingQueue.isEmpty()) {
			return null;
		}
		logger.info("get one url: "+ waitingQueue.peek()+" from the url waiting queue.");
		return waitingQueue.poll();
	}
	
	public synchronized void insertWaitingQueue(CrawlerURL url) {
		
		waitingQueue.add(url);
	}
	
	public synchronized void insertRunningQueue(CrawlerURL url) {
		runningQueue.add(url);
	}
	
	public synchronized void insertVisitedQueue(String url) {
		visitedSet.add(url);
	}
	
	public synchronized void insertUnreachableQueue(CrawlerURL url) {
		unreachableQueue.add(url);
	}

	public Queue<CrawlerURL> getWaitingQueue() {
		return waitingQueue;
	}

	public void setWaitingQueue(Queue<CrawlerURL> waitingQueue) {
		this.waitingQueue = waitingQueue;
	}

	public Queue<CrawlerURL> getRunningQueue() {
		return runningQueue;
	}

	public void setRunningQueue(Queue<CrawlerURL> runningQueue) {
		this.runningQueue = runningQueue;
	}

	public Set<String> getVisitedSet() {
		return visitedSet;
	}

	public void setVisitedQueue(Set<String> visitedSet) {
		this.visitedSet = visitedSet;
	}


	public Queue<CrawlerURL> getUnreachableQueue() {
		return unreachableQueue;
	}

	public void setUnreachableQueue(Queue<CrawlerURL> unreachableQueue) {
		this.unreachableQueue = unreachableQueue;
	}
	
	/**
	 * @param url
	 * @return true:visited        false:not visited
	 */
	public boolean checkVisited(String url)
	{
		if (visitedSet.contains(url)) return true;
		else return false;
	}
	
}
