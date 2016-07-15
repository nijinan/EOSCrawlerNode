package cn.edu.pku.EOSCN.entity;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.james.mime4j.dom.datetime.DateTime;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import cn.edu.pku.EOSCN.DAO.DocumentationDao;
import cn.edu.pku.EOSCN.DAO.EmailDao;
import cn.edu.pku.EOSCN.DAO.JDBCPool;

public class Documentation {
	private String uuid;
	private String projectUuid;
	private int subType;
	private String url;
	private String docname;	//文档标题
	private String filePath;
	private Date updateTime;
	
	public Documentation(String projectUuid) {
		
		this.projectUuid=projectUuid;
		uuid = UUID.randomUUID().toString();
		updateTime = new Date();
	}
	public Documentation() {}
	
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getProjectUuid() {
		return projectUuid;
	}
	public void setProjectUuid(String projectUuid) {
		this.projectUuid = projectUuid;
	}
	public int getSubType() {
		return subType;
	}
	public void setSubType(int subType) {
		this.subType = subType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getDocName() {
		return docname;
	}
	public void setDocName(String name) {
		this.docname = name;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		JDBCPool.initPool();
		
		
		Documentation documentation=new Documentation("wwwwwwwwww");
		System.out.println(documentation.getUpdateTime());
		DocumentationDao documentationDao=new DocumentationDao();
		documentationDao.insertDocumentation(documentation);
	}
	
}
