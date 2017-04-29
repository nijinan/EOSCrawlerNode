package cn.edu.pku.EOSCN.crawler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.eclipse.jgit.api.ArchiveCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import cn.edu.pku.EOSCN.business.NetWorkDaemon;
import cn.edu.pku.EOSCN.business.ThreadManager;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;
import cn.edu.pku.EOSCN.crawler.util.UrlOperation.HtmlDownloader;
import cn.edu.pku.EOSCN.entity.Project;

public class GitCrawler extends Crawler {
	private String storageBasePath;
	private String projectGitBaseUrl;
	Git git;
	Repository repository;
	public List<String> urls = new ArrayList<String>();
	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		storageBasePath = String.format("%s%c%s%c%s%c%s%c%s", 
				Config.getTempDir(),
				Path.SEPARATOR,
				this.getProject().getOrgName(),
				Path.SEPARATOR,
				this.getClass().getName(),
				Path.SEPARATOR,
				this.getProject().getProjectName(),
				Path.SEPARATOR,
				this.getProject().getName()); 
		 projectGitBaseUrl = this.getEntrys();
	}

	@Override
	public void crawl_url() throws Exception {

		// TODO Auto-generated method stub
		File parent = FileUtil.createFile(storageBasePath, this.getProject().getName()).getParentFile();
		File dir = new File(storageBasePath + Path.SEPARATOR + this.getProject().getName() + "GIT");
		int k = parent.list().length;
		if (k < 4){
			if (dir.exists()){
				FileUtil.deleteFolder(parent.getAbsolutePath());
				
			}
			//return;
		
		
		if (!dir.exists()) {
			dir.mkdirs();
			int times = 0;
			//git = Git.init().setDirectory( dir ).call();
			long stimes = System.currentTimeMillis();
			git = getGit(dir);
			long etimes = System.currentTimeMillis();
			while ((git == null) && etimes - stimes > 60000){
				FileUtil.deleteFolder(parent.getAbsolutePath());
				dir.mkdirs();
				if (times > 5) return;
				times++;
				System.out.println("Retry: " + times + " " + this.getEntrys());
				
				stimes = System.currentTimeMillis();
				git = getGit(dir);
				etimes = System.currentTimeMillis();	
			}
		}
		git = Git.open(dir);
			while (!NetWorkDaemon.isok)
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			//git.remoteSetUrl().setUri(new URIish(projectGitBaseUrl));
			//git.fetch().setRemote(projectGitBaseUrl).setRefSpecs(new RefSpec("+refs/heads/*:refs/remotes/origin/*")).call();
//			git = Git.cloneRepository()
//					  .setURI( projectGitBaseUrl )
//					  .setDirectory( dir ).setTimeout(3600)
//					  .call();
		}//else {return ;}

		git = Git.open(dir);
		repository = git.getRepository();
	    LogCommand log = git.log();
        //log.setMaxCount(12);
        log = log.all();
        Iterable<RevCommit> logMsgs = log.call();
        RevCommit oldCommit = null;
        RevCommit newCommit = null;
        for (RevCommit commit : logMsgs) {
        	String commitName = commit.getId().getName();
        	String storagePath = storageBasePath + Path.SEPARATOR + "commit" + commitName;
            newCommit = oldCommit;
            oldCommit = commit;
            if (commit.getParentCount() == 0) continue;
        	if (this.needLog){
				if (FileUtil.logged(storagePath) && FileUtil.exist(storagePath)){
					continue;
				}else{
					String text = getString(commit);
					FileUtil.write(storagePath, text);
					FileUtil.logging(storagePath);
				}
        	}else{
        		String text = getString(commit);
				FileUtil.write(storagePath, text);
				if (!FileUtil.logged(storagePath)) FileUtil.logging(storagePath);
			}
            
        }
        ListTagCommand tags = git.tagList();
        ArchiveCommand.registerFormat("zip", new org.eclipse.jgit.archive.ZipFormat());    
        FileUtil.createPath(storageBasePath + "\\zip");
        ArchiveCommand archive = git.archive();
        String path = storageBasePath + "\\zip\\code";
        FileOutputStream fos = new FileOutputStream(new File(path+".zip"));
    	archive.setTree(git.getRepository().resolve("HEAD")).setFormat("zip").setOutputStream(fos).call();
//        for (org.eclipse.jgit.lib.Ref ref : tags.call()){
//        	String path = storageBasePath + "\\zip\\" + ref.getName().substring(ref.getName().lastIndexOf('/'));
//        	if (FileUtil.exist(path+".zip")) continue;
//        	FileOutputStream fos = new FileOutputStream(new File(path+".zip"));
//        	ArchiveCommand archive = git.archive();
//        	archive.setTree(ref.getObjectId()).setFormat("zip").setOutputStream(fos).call();
//        	fos.flush();
//        	fos.close();
//        }
	        
	}
    public Git getGit(File dir){
    	Git git = null;
		try {
			git = Git.cloneRepository()
			  .setURI( projectGitBaseUrl )
			  .setDirectory( dir ).setTimeout(1000)
			  .call();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return git;
    }
    public String getString(RevCommit newCommit){
    	StringBuffer sb = new StringBuffer();
    	sb.append("----------------------------------------\n");
    	sb.append(newCommit+"\n");
    	sb.append(newCommit.getAuthorIdent().getName()+"\n");
    	sb.append(newCommit.getAuthorIdent().getWhen()+"\n");
    	sb.append(" ---- " + newCommit.getFullMessage()+"\n");
    	sb.append("----------------------------------------\n");
    	for (RevCommit oldCommit : newCommit.getParents()){
	    	oldCommit = newCommit.getParent(0);
	    	sb.append("Parents : "+ oldCommit.getName() + "\n\n");
	    	//System.out.println(newCommit.getCommitterIdent());
	        AbstractTreeIterator newTree = prepareTreeParser(newCommit);  
	        AbstractTreeIterator oldTree = prepareTreeParser(oldCommit);  
	        List<DiffEntry> diff;
			try {
				
				diff = git.diff().setOldTree(oldTree).setNewTree(newTree).setShowNameAndStatusOnly(true).call();
			
	        
			
	        
	        //设置比较器为忽略空白字符对比（Ignores all whitespace）  
	 
	        for (DiffEntry diffEntry : diff) { 
	        	ByteArrayOutputStream out = new ByteArrayOutputStream();
	        	DiffFormatter df = new DiffFormatter(out); 
	            df.setDiffComparator(RawTextComparator.WS_IGNORE_ALL);  
	            df.setRepository(git.getRepository());  
	        	df.format(diffEntry);  
	            String diffText = out.toString("UTF-8");    
	            out.close();
	            sb.append(diffText + "\n"); 
	            //System.out.println(sb.length());
	            sb.append("---------------------------------------\n");
	        }
			} catch (GitAPIException | IOException  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}  
	        sb.append("---------------------------------------\n");
    	}
        return sb.toString();
    }
    public AbstractTreeIterator prepareTreeParser(RevCommit commit){  
        //System.out.println(commit.getId());
        
        try (RevWalk walk = new RevWalk(repository)) {  
            //System.out.println(commit.getTree().getId());  
            RevTree tree = walk.parseTree(commit.getTree().getId());  
  
            CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();  
            try (ObjectReader oldReader = repository.newObjectReader()) {  
                oldTreeParser.reset(oldReader, tree.getId());  
            }  
  
            walk.dispose();  
  
            return oldTreeParser;  
    }catch (Exception e) {  
        // TODO: handle exception  
    }  
        return null;  
    }  
	@Override
	public void crawl_middle(int id, Crawler crawler) {
		// TODO Auto-generated method stub

	}

	@Override
	public void crawl_data() {
		// TODO Auto-generated method stub

	}

	public static void main(String args[]){
		GitCrawler crawl = new GitCrawler();
		Project project = new Project();
		project.setOrgName("njn");
		project.setProjectName("lucene");
		project.setName("lucene");
		crawl.setProject(project);
		crawl.setEntrys("https://github.com/nijinan/EOSCrawlerNode.git");
		ThreadManager.initCrawlerTaskManager();
		crawl.needLog = true;
		crawl.crawlerType = Crawler.MAIN;
		ThreadManager.addCrawlerTask(crawl);
		//crawl.join();
		ThreadManager.finishCrawlerTaskManager();
	}
	
}
