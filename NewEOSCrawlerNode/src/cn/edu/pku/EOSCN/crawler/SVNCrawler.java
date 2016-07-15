package cn.edu.pku.EOSCN.crawler;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import org.apache.commons.httpclient.HttpException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

//import com.sun.org.apache.xml.internal.security.Init;

import cn.edu.pku.EOSCN.TestUtil;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.Doc.URLReader;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.RemoteFileOperation;
import cn.edu.pku.EOSCN.entity.Project;
import cn.edu.pku.EOSCN.storage.StorageUtil;

public class SVNCrawler extends Crawler {

	/**
	 * @author yeting
	 */

	private static SVNClientManager ourClientManager;
	private String svnPath= null;
	String svnRoot;
	//String userName = null;
	//String password = null;
	SVNRepository repository;
	String svnLocalCopyPath = null;
	File svnLocalCopyDir = null;
	String svnRemoteCopyPath = null;
	SmbFile svnRemoteCopyDir = null;
	
	
	public SVNCrawler() {
		super();
	}

	public SVNCrawler(Project project, List<String> urllist) {
		super(project, urllist);
	}

	private void Init(Project project, List<String> urllist) throws HttpException, IOException {
		svnPath = getTrunkDirURL(urllist.get(0));//得到项目SVN的地址，svn地址是按照utf-8格式编码的
		svnLocalCopyPath = Config.getTempDir()+"/SourceCode/"+project.getName();//构造本地存储地址
		svnLocalCopyDir = new File(svnLocalCopyPath);
		svnRemoteCopyPath = StorageUtil.getSourceCodeFilePath(project);
			//svnCopyPath = StorageUtil.getSourceCodeFilePath(project);
			//得到项目SVN爬取到本地后保存的地址
	}

	private String getTrunkDirURL(String originalURL) throws HttpException, IOException {
		if (!originalURL.contains("svn")) {
			logger.error("NOT A SVN URL!");
			return null;
		}
		if (originalURL.contains("trunk")) {
			return originalURL;
		}
		String htmlString = URLReader.getHtmlContentWithTimeLimit(originalURL, 600000);
		if (htmlString.contains("trunk/")) {
			return originalURL + "/trunk";
		}
		if (htmlString.contains(">core/")) {
			return getTrunkDirURL(originalURL + "/core");
		} else if (htmlString.contains(">java/")) {
			return getTrunkDirURL(originalURL + "/java");
		} else if (htmlString.contains(">main/")) {
			return getTrunkDirURL(originalURL + "/main");
		} else if (htmlString.contains(">common/")) {
			return getTrunkDirURL(originalURL + "/common");
		}
		return originalURL;
	}

	/*
	 * 将SVN中的内容导出
	 */
	private void SVNCheckout() throws SVNException {
			/*
			 * 递归的把工作副本从repositoryURL check out 到 wcDir目录。 SVNRevision.HEAD
			 * 意味着把最新的版本checked out出来。
			 */
			SVNURL repositoryURL = SVNURL
					.parseURIEncoded(svnPath);//svn地址是按照utf-8格式编码的
			SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
			updateClient.setIgnoreExternals(false);
			updateClient.doCheckout(repositoryURL, svnLocalCopyDir, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, true);
		
	}

	/*
	 * 更新SVN
	 */
	private void SVNUpdate() throws SVNException {
		// 获得updateClient的实例
		SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);
		// 执行更新操作
		long versionNum;
			versionNum = updateClient.doUpdate(svnLocalCopyDir, SVNRevision.HEAD,
					SVNDepth.INFINITY, false, false);
			System.out.println("工作副本更新后的版本：" + versionNum);
		
	}

	/*
	 * 判断一个目录是否为空
	 */
	private boolean isEmptyDirectory(File dir)
	{
		if(dir.exists())
		{
			//System.out.println(dir.listFiles().length);
			if(dir.listFiles().length==0)
				return true;
			return false;
		}
		else {
			dir.mkdirs();
			return true;
		}
	}
	public void Crawl() throws SVNException, HttpException, IOException {
		Init(project, urlList);
		System.out.println(svnPath);
		SVNRepositoryFactoryImpl.setup();
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		ourClientManager = SVNClientManager.newInstance(
				(DefaultSVNOptions) options, this.getUserName(), this.getPassword());
		if (isEmptyDirectory(svnLocalCopyDir)) {
			SVNCheckout();
		} else {
			SVNUpdate();
		}
		synchronizationLocalRemote(svnRemoteCopyPath, svnLocalCopyPath);
		deleteFileInDir(svnLocalCopyDir);
		setStatus(SUCCESS);
		finish();
	}
	
	private void deleteFileInDir(File svnLocalCopyDir2) {
		if (!svnLocalCopyDir2.isDirectory()) {
			svnLocalCopyDir2.delete();
			return;
		}
		File[] files = svnLocalCopyDir2.listFiles();
		for (File file : files) {
			deleteFileInDir(file);
		}
		svnLocalCopyDir2.delete();
	}

	/*
	 * 将本地的文件内容保存到远程的服务器上
	 */
	private static void synchronizationLocalRemote(String remotePath, String localPath) throws MalformedURLException, SmbException
	{
		File dir = new File(localPath);
		File[] files = dir.listFiles();
		if(files == null)
			return;
		for(File file : files)
		{
			if(file.isDirectory())
			{
					SmbFile remoteFile = new SmbFile(remotePath+"/"+file.getName());
					if(remoteFile.exists() == false)
					{
						remoteFile.mkdir();
					}
					synchronizationLocalRemote(remotePath+"/"+file.getName(), file.getAbsolutePath());
				
			}
			else 
			{
				RemoteFileOperation.smbPut(remotePath, file.getAbsolutePath());
			}
		}
	}
	
	private boolean isFileChanged(String fileName, String localPath, String remotePath)
	{
		return false;
	}
	
	public static void main(String[] args) throws SVNException, HttpException, IOException {
		// TODO Auto-generated method stub
		Project project = new Project();
		project = TestUtil.getLuceneProject();
		System.out.println(Config.getTempDir()+"/SVN/"+project.getName());
		List<String> urllist = new ArrayList<String>();
		urllist.add("http://svn.apache.org/repos/asf/lucene/dev/trunk");
		//urllist.add("http://dev.sei.pku.edu.cn/svn/TSR/EasyOpensource");
		SVNCrawler svnCrawler = new SVNCrawler(project, urllist);
		svnCrawler.Crawl();
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
}
