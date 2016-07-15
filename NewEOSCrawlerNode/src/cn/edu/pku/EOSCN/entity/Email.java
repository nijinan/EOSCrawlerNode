package cn.edu.pku.EOSCN.entity;

import java.util.Date;
import java.util.UUID;

public class Email {
	private String fromMail;
	private String fromMailName;
	
	private String toMail;
	private String toMailName;
	
	private String subject;
	
	private Date date;
	
	private String content;
	
	private String uuid;
	
	private String projectUuid;
	
	public Email(String projectUuid) {
		this.projectUuid = projectUuid;
		uuid = UUID.randomUUID().toString();
	}
	
	public Email() {
	}

	public String getFromMail() {
		return fromMail;
	}

	public void setFromMail(String fromMail) {
		this.fromMail = fromMail;
	}

	public String getFromMailName() {
		return fromMailName;
	}

	public void setFromMailName(String fromMailName) {
		this.fromMailName = fromMailName;
	}

	public String getToMail() {
		return toMail;
	}

	public void setToMail(String toMail) {
		this.toMail = toMail;
	}

	public String getToMailName() {
		return toMailName;
	}

	public void setToMailName(String toMailName) {
		this.toMailName = toMailName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

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
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("###########################################" + "\n");
		sb.append("Project ID : " + this.getProjectUuid() + "\n");
		sb.append("Email ID : " + this.getUuid() + "\n");
		sb.append("From : " + this.getFromMail() + "\n");
		sb.append("To : " + this.getToMail() + "\n");
		sb.append("TO name:" + this.getToMailName() + "\n");
		sb.append("Subject : " + this.getSubject() + "\n");
		sb.append("Date : " + this.getDate() + "\n");
//		System.out.println("Content : " + this.getContent() + "\n");
		sb.append("###########################################");
		return sb.toString();
	}
}
