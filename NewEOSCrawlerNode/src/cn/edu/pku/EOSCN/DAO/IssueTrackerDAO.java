package cn.edu.pku.EOSCN.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

//import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;

import cn.edu.pku.EOSCN.entity.Email;
import cn.edu.pku.EOSCN.entity.IssueTracker;
import cn.edu.pku.EOSCN.entity.Project;


/**
 * @author Carrie
 *
 */
public class IssueTrackerDAO {
//	public Connection connect() throws Exception{
//		if(JDBCPool.getConnection() == null) JDBCPool.initPool();
//		Connection conn = JDBCPool.getConnection();		
//		return conn;
//	}
//	
//	public void create(Connection conn, String tableName) {
//		try {
//			Statement statement = conn.createStatement();
//			String sql;
//			sql = "create table " + tableName + " ( " + "projectuuid char(50),"
//					+ "uuid char(50)," + "type char(20)," + "keyname char(20),"
//					+ "summary text," + "assignee char(50),"
//					+ "priority char(20)," + "status char(20),"
//					+ "resolution char(50)," + "created char(50),"
//					+ "updated char(50)," + "description longtext" + " );";
//			try {
//				System.out.println(sql);
//				statement.executeUpdate(sql);
//				System.out.println("Succeeded create Table "+tableName+"!");
//			} catch (MySQLSyntaxErrorException e) {
//				statement.executeUpdate("drop table " + tableName + ";");
//				statement.executeUpdate(sql);
//				System.out.println("Succeeded create Table "+tableName+"!");
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public void insertIssueTracker(IssueTracker issueTracker) throws SQLException, ClassNotFoundException {
		String insertSql = "insert into issueTracker(projectuuid,uuid,type,keyname,summary,assignee," +
				"priority,status,resolution,created,updated,description,reporter,version) values"+
				"(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		//System.out.println(insertSql);		
		DAOUtils.update(insertSql, issueTracker.getProjectUuid(), issueTracker.getUuid(), issueTracker.getType(), 
				issueTracker.getKeyname(), issueTracker.getSummary(), issueTracker.getAssignee(),
				issueTracker.getPriority(), issueTracker.getStatus(), issueTracker.getResolution(), issueTracker.getCreated(), 
				issueTracker.getUpdated(), issueTracker.getDescription(), issueTracker.getReporter(), issueTracker.getVersion());
			
	}

	public static int countNum(String puuid) throws SQLException {
		int result = DAOUtils.count("select count(*) from issueTracker where projectuuid = ?", puuid);
		return result;
	}
	

}
