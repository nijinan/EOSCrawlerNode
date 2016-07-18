package cn.edu.pku.EOSCN.entity;

import java.util.Date;
import java.util.UUID;

public class RelativeWeb {

	private String uuid = UUID.randomUUID().toString();
	private Date updateTime = new Date();
	
	
	private String projectuuid;
	private String url;
	private String title;
	private String filepath;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getProjectuuid() {
		return projectuuid;
	}
	public void setProjectuuid(String projectuuid) {
		this.projectuuid = projectuuid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
}
