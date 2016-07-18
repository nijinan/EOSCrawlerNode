package cn.edu.pku.EOSCN.crawler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import cn.edu.pku.EOSCN.TestUtil;
import cn.edu.pku.EOSCN.DAO.IssueTrackerDAO;
import cn.edu.pku.EOSCN.DAO.JDBCPool;
import cn.edu.pku.EOSCN.business.CrawlerBusiness;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.issueTracker.*;
import cn.edu.pku.EOSCN.crawler.util.issueTracker.json.JSONException;
import cn.edu.pku.EOSCN.entity.IssueTracker;
import cn.edu.pku.EOSCN.entity.Project;


/**
 * @author Carrie
 * @comment by 张灵箫
 * 该爬虫得到的jira网址如下：http://issues.apache.org/jira/browse/LUCENE
 * 该网址应该按/browse/分开
 * 之后的LUCENE是jira数据库中该项目名称
 * 之前的http://issues.apache.org/jira/ 是jira数据库远程API的host
 * api的形式应该是：       host  +  /rest/api/2/search?jql=。。。
 * 
 * 比如现在又给了jira网址：
 * http://jira.codehaus.org/browse/MNG
 * 那么项目名是MNG，api形式为：
 *      http://jira.codehaus.org/rest/api/2/search?jql=project=MNG&startAt=0&maxResults=0
 *      
 * 以上API中的maxresult=0,这样可以返回一个issue数量：
 * 结果为：{"startAt":0,"maxResults":0,"total":4308,"issues":[]}
 * 
 */
public class JiraCrawler extends Crawler{
	String issueTrackerWritePath = null;
	URL issueTrackerUrl = null;
	
	private void Init() throws MalformedURLException {
		issueTrackerUrl = new URL(urlList.get(0));//取出缺陷库的URL
		
//    	String urlString = issueTrackerUrl.toString();
//    	String temStr1[] = urlString.split("/");
//		String projectName = temStr1[temStr1.length-1];//取URL的最后一个/后的字符串为项目名称     
//		String temStr2[] = urlString.split("/browse/");
//		String hostUrl = temStr2[0];
		//URL temURL = new URL(issueTrackerUrl.getProtocol() + "://" + issueTrackerUrl.getHost() + "/jira/issues/?jql=project%3D" + projectName);        
		//issueTrackerUrl = temURL;//更新！
        //System.out.println("issueTrackerUrl is : "+issueTrackerUrl.toString());
        
		issueTrackerWritePath = Config.getTempDir() + "/issues/" + project.getUuid();//指定爬下来的缺陷库放的文件路径
		//issueTrackerWritePath = "F:/EOSdir/b7914db3-caa7-4d70-96cd-bd4b5b4ed029/IssueTracker/";//因为我没有D盘！！！！！！！！！！！！！！！！！！！！！！
	}
	
    public void Crawl() throws JSONException, org.json.JSONException{
    	try {
			Init();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setStatus(ERROR);
			return;			
		}//初始化
    	
		File file  = new File(issueTrackerWritePath); 
    	if (!file.exists()) {
			file.mkdirs();
		}
		
    	int status = SUCCESS;
		long startTime = System.currentTimeMillis();//记录爬虫起始时间
		
		//爬取网页部分
		try {
			CrawlIssueTracker.crawlIssueTrack(issueTrackerUrl, issueTrackerWritePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//从目录里读取每个json文件，插入数据库
		File[] array = file.listFiles();//取目录下的所有文件名
		
		//建立数据库		
		IssueTrackerDAO issueTrackerDAO = new IssueTrackerDAO();
												
		for(int i = 0; i < array.length; i++){
			if(array[i].isFile()){			
				//文件解析和数据库操作
				JsonExtractor je=null;
				try {					
					je = new JsonExtractor(array[i].toString(),project.getUuid());//在这里将文件解析
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (IssueTracker d : je.list) {//将json里的内容都提取出来
					IssueTracker issueTracker = new IssueTracker(project.getUuid());
					issueTracker.setProjectUuid(project.getUuid());
					issueTracker.setUuid(UUID.randomUUID().toString());
					issueTracker.setType(d.getType());
					issueTracker.setKeyname(d.getKeyname());
					issueTracker.setSummary(d.getSummary());
					issueTracker.setAssignee(d.getAssignee());
					issueTracker.setReporter(d.getReporter());
					issueTracker.setPriority(d.getPriority());
					issueTracker.setStatus(d.getStatus());
					issueTracker.setResolution(d.getResolution());
					issueTracker.setCreated(d.getCreated());
					issueTracker.setUpdated(d.getUpdated());
					issueTracker.setVersion(d.getVersion());
					issueTracker.setDescription(d.getDescription());
					//一条条插入数据库
					try {
						issueTrackerDAO.insertIssueTracker(issueTracker);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}
			}
		}		
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("Issue Tracker Crawler time : "+(endTime - startTime) +"milis");
		setStatus(status);
		
		for (File file2 : array) {
			file2.delete();
		}
		
		finish();
		//return status;
	
    }
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		JDBCPool.initPool();
		List<String> list = new ArrayList<String>();
		//list.add("https://issues.apache.org/jira/issues/?jql=project%20%3D%20LUCENE");

		JiraCrawler issueTrackerCrawler = new JiraCrawler();
		Project project = TestUtil.getLuceneProject();
//		CrawlerBusiness pBusiness = new CrawlerBusiness();
//		Project project = pBusiness.getProjectByUuid("b7914db3-caa7-4d70-96cd-bd4b5b4ed029");
		
		list.add("https://issues.apache.org/jira/browse/LUCENE");
		issueTrackerCrawler.setProject(project);
		issueTrackerCrawler.setUrlList(list);
		issueTrackerCrawler.Crawl();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
}
