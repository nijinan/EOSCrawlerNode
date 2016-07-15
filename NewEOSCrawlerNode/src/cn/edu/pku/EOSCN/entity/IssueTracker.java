package cn.edu.pku.EOSCN.entity;

import java.util.UUID;


/**
 * @author Carrie
 *
 */
public class IssueTracker {
	
	private String type;
	private String keyname;	
	private String summary;
	private String assignee;
	private String reporter;	
	private String priority;	
	private String status;	
	private String resolution;
	private String created;
	private String updated;
	private String version;
	private String description;
	private String projectUuid;
	private String uuid;	

	
	public IssueTracker(String projectUuid) {
		this.projectUuid = projectUuid;
		uuid = UUID.randomUUID().toString();
	}
	
	public IssueTracker() {
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKeyname(){
		return keyname;
	}
	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getAssignee() {
		return assignee;
	}
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}
	public String getReporter() {
		return reporter;
	}
	public void setReporter(String reporter) {
		this.reporter = reporter;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResolution(){
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getCreated(){
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getUpdated(){
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public String getVersion(){
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDescription(){
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getProjectUuid() {
		return projectUuid;
	}
	public void setProjectUuid(String projectUuid) {
		this.projectUuid = projectUuid;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	

}
