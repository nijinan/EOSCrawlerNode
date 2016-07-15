package cn.edu.pku.EOSCN.business;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbFile;

import org.apache.log4j.Logger;

import cn.edu.pku.EOSCN.TestUtil;
import cn.edu.pku.EOSCN.DAO.CrawlerTaskDao;
import cn.edu.pku.EOSCN.DAO.DocumentationDao;
import cn.edu.pku.EOSCN.DAO.EmailDao;
import cn.edu.pku.EOSCN.DAO.IssueTrackerDAO;
import cn.edu.pku.EOSCN.DAO.JDBCPool;
import cn.edu.pku.EOSCN.DAO.ProjectDAO;
import cn.edu.pku.EOSCN.DAO.RelativeWebDAO;
import cn.edu.pku.EOSCN.DAO.ResourceMetaDataDAO;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.Crawler;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.RemoteFileOperation;
import cn.edu.pku.EOSCN.crawlerTask.CrawlerTaskManager;
import cn.edu.pku.EOSCN.entity.Project;
import cn.edu.pku.EOSCN.entity.ResourceMetaData;
import cn.edu.pku.EOSCN.storage.StorageUtil;
import cn.edu.pku.EOSCN.util.XMLUtils;
/**
 * project相关业务逻辑
 * @author 张灵箫
 *
 */
public class CrawlerBusiness {
	private static final Logger logger = Logger.getLogger(CrawlerBusiness.class.getName());
	private static final String REMOTE_SUCCESS_RESPONCE = "success";
	
	
	/**
	 * 根据uuid得到project对象，包含所有的resourceMetadata
	 * @author 张灵箫
	 * @param uuid
	 * @return
	 */
	public Project getProjectByUuid(String uuid) {
		Project project = null;
		List<ResourceMetaData> rDatas = null;
		ProjectDAO projectDAO = new ProjectDAO();
		ResourceMetaDataDAO resourceMetaDataDAO = new ResourceMetaDataDAO();
		try {
			project = projectDAO.getProjectByUuid(uuid);
			rDatas = resourceMetaDataDAO.getDataListByProjectUuid(uuid);
			project.getResources().addAll(rDatas);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 
		return project;
	}
	
	public static List<Project> getAllProject() {
		List<Project> projects = null;
		try {
			projects = new ProjectDAO().getAllProject();
			for (Project project : projects) {
				project.getResources().addAll(new ResourceMetaDataDAO().getDataListByProjectUuid(project.getUuid()));
			}
		} catch (SQLException e) {
			System.out.println("database error!");
		}
		return projects;
	}
	/**
	 * 根据用户上传的XML文件更改已有项目的信息，包括资源信息
	 * XML上传到项目对应的文件夹中，以uuid命名
	 * @param uuid
	 * @return 1表示成功，0表示出错
	 */
	public int updateProjectInfoFromXML(String uuid) {
		String xmlFilePath = StorageUtil.getProjectXmlFile(uuid);
		Project project = null;
		project = XMLUtils.getProjectFromXmlFile(xmlFilePath);
		ProjectDAO pDao = new ProjectDAO();
		ResourceMetaDataDAO rDao = new ResourceMetaDataDAO();
		try {
			//System.out.println(project.getDescription());
			pDao.updateProjectInfo(uuid, project);
			rDao.deleteAllMetaDataOfProject(uuid);
			for (ResourceMetaData data : project.getResources()) {
				rDao.insertResourceMetaData(uuid, data);
			}
		} catch (SQLException e) {
			System.out.println("Database error!");
			e.printStackTrace();
			return 0;
		}
		return 1;
	}
	
	
	/**
	 * @author 张灵箫
	 * 调用爬虫爬去某个项目的某个指定类型的资源
	 * @param string
	 * @param project
	 */
	public String crawlResource(String resoucetype, Project project, String taskuuid) {
		ResourceMetaData data = project.getResourceByType(resoucetype);
		//System.out.println(data);
		Crawler crawler = null;
		try {
			//System.out.println(Crawler.class.getPackage().getName() + "." + data.getCrawler());
			crawler = (Crawler) Class.forName(Crawler.class.getPackage().getName() + "." + data.getCrawler()).newInstance();
			crawler.setProject(project);
			crawler.setUrlList(data.getBaseUrls());
			crawler.setTaskuuid(taskuuid);
			crawler.setResourceType(resoucetype);
		} catch (Exception e) {
			logger.info("Can't instantiate crawler class: " + data.getCrawler());
			e.printStackTrace();
			return "Can't instantiate crawler class: " + data.getCrawler();
		} 
		CrawlerTaskManager.addCrawlerTask(crawler);
		return REMOTE_SUCCESS_RESPONCE;
	}
	
	/**
	 * @author 张灵箫
	 * 根据xml文件得到新的project对象，存入数据库
	 * @param xmlFilePath xml文件路径
	 * @return
	 */
	public Project createNewProjectFromXml(String xmlFilePath) {
		Project project = null;
		project = new Project(xmlFilePath);
		ProjectDAO projectDAO = new ProjectDAO();
		ResourceMetaDataDAO dataDAO = new ResourceMetaDataDAO();
		try {
			projectDAO.insertProject(project);
			List<ResourceMetaData> datas = project.getResources();
			for (ResourceMetaData resourceMetaData : datas) {
				dataDAO.insertResourceMetaData(project.getUuid(), resourceMetaData);
			}
		} catch (SQLException e) {
			System.out.println("Database error!");
			e.printStackTrace();
		}
		return project;
	}
	
	public static void UpdateDataNum(String puuid, String type) {
		int count = 0;
		if (type.equals("SourceCode")) {
			String filepath = Config.getEOSDir() + puuid + "/SourceCode/";
			SmbFile file = null;
			try {
				file = new SmbFile(filepath);
			} catch (MalformedURLException e) {
				logger.info("Read sourceCodeNum Error!");
			}
			count = RemoteFileOperation.countFiles(file);
		} else if (type.equals("Documentation")) {
			try {
				count = DocumentationDao.countNum(puuid);
			} catch (SQLException e) {
				logger.info("Count Documentation Error");
				e.printStackTrace();
			}
		} else if (type.equals("MailingList")) {
			try {
				count = EmailDao.countNum(puuid);
			} catch (SQLException e) {
				logger.info("Count Mail Error");
				e.printStackTrace();
			}
		} else if (type.equals("IssueTracker")) {
			try {
				count = IssueTrackerDAO.countNum(puuid);
			} catch (SQLException e) {
				logger.info("Count Issue Error");
				e.printStackTrace();
			}
		} else if (type.equals("RelativeWeb")) {
			try {
				count = RelativeWebDAO.countNum(puuid);
			} catch (SQLException e) {
				logger.info("Count blog Error");
				e.printStackTrace();
			}
		}
		try {
			ResourceMetaDataDAO.updateCount(puuid, type, count);
		} catch (SQLException e) {
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		JDBCPool.initPool(); 
		List<Project> projects = CrawlerBusiness.getAllProject();
//		projects = projects.subList(0, 1);
		for (Project project : projects) {
//			List<ResourceMetaData> datas = project.getResources();

			UpdateDataNum(project.getUuid(), "RelativeWeb");
//			for (ResourceMetaData resourceMetaData : datas) {
//				System.out.println("UPDATING " + resourceMetaData.getType() + " COUNT FOR " + project.getName());
//				UpdateDataNum(project.getUuid(), resourceMetaData.getType());
//			}
		}
	}


}
