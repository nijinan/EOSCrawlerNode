package cn.edu.pku.EOSCN.DAO;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.jmx.snmp.Timestamp;

import cn.edu.pku.EOSCN.entity.Documentation;


public class DocumentationDao {
	private static final Logger logger = Logger.getLogger(DocumentationDao.class.getName());
	public void insertDocumentation(Documentation documentation) throws SQLException, ClassNotFoundException {
		
		String insertSql = "insert into Documentation (Uuid,ProjectUuid,subType,url,docname,filePath,Updatetime) values" + "(?,?,?,?,?,?,?)";
		
		/*
		Date date=new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		*/
		
		DAOUtils.update(insertSql, documentation.getUuid(),documentation.getProjectUuid(),documentation.getSubType(),documentation.getUrl(),documentation.getDocName(),documentation.getFilePath(),documentation.getUpdateTime());
//		this.update(insertSql, email.getUuid(),email.getProjectUuid(),email.getFromMail(),email.getFromMailName(),email.getToMail(),email.getToMailName(),email.getDate(),content,email.getSubject());
	}
	public static int countNum(String puuid) throws SQLException {
		int result = DAOUtils.count("select count(*) from Documentation where ProjectUuid = ?", puuid);
		return result;
	}
}
