package cn.edu.pku.EOSCN.DAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.Logger;



import cn.edu.pku.EOSCN.entity.Documentation;
import cn.edu.pku.EOSCN.entity.Email;

public class EmailDao {
	private static final Logger logger = Logger.getLogger(EmailDao.class.getName());
	public void insertEmail(Email email) throws SQLException, ClassNotFoundException {
		
		String insertSql = "insert into mail (mailID,projectID,fromMail,fromName,toMail,toName,sendDate,content,subject) values"+
		"(?,?,?,?,?,?,?,?,?)";
		
		String content = email.getContent();
		String[] lines = content.split("\n");
		StringBuilder sb = new StringBuilder();
		int index = 0;
		while(index < lines.length && lines[index].length() < 3) index++ ;
//		System.out.println(index);
		for(int i = index; i < lines.length; i++) {
			sb.append(lines[i]+"\n");
		}
		content = sb.toString();
		
		int result = DAOUtils.update(insertSql, email.getUuid(),email.getProjectUuid(),email.getFromMail(),email.getFromMailName(),email.getToMail(),email.getToMailName(),email.getDate(),content,email.getSubject());
//		System.out.println(result + " inseted!!");
//		this.update(insertSql, email.getUuid(),email.getProjectUuid(),email.getFromMail(),email.getFromMailName(),email.getToMail(),email.getToMailName(),email.getDate(),content,email.getSubject());
	}
	public static int countNum(String puuid) throws SQLException {
		int result = DAOUtils.count("select count(*) from mail where projectID = ?", puuid);
		return result;
	}
	
//	public  int update(String sqlString, Object... params) throws SQLException, ClassNotFoundException {
//		JDBCPool.initPool();
//		Connection connection2 = JDBCPool.getConnection();
//		QueryRunner runner = new QueryRunner();
//		int result = runner.update(connection2, sqlString, params);
//		DbUtils.close(connection2);
//		return result;
//	}
}
