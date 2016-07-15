package cn.edu.pku.EOSCN.DAO;

import java.sql.SQLException;
import java.util.List;

import cn.edu.pku.EOSCN.entity.IssueTracker;
import cn.edu.pku.EOSCN.entity.RelativeWeb;

/*
 * @author Lin Zeqi
 * @Description: 将一个html页面存入数据库中
 */

public class RelativeWebDAO {

	public static String insertSql = "insert into RelativeWeb(uuid, projectuuid, url, title, updateTime, filepath) values"
			+ "(?,?,?,?,?,?)";

	
	public static void insertRelativeWeb(RelativeWeb web) throws SQLException{
		DAOUtils.update(insertSql, web.getUuid(), web.getProjectuuid(), web.getUrl(), web.getTitle(), web.getUpdateTime(), web.getFilepath());
	}
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		JDBCPool.initPool();
		RelativeWeb web = new RelativeWeb();
		web.setFilepath("dmb://fdfdfd");
		web.setTitle("123");
		web.setUrl("http://dfdfsf");
		web.setProjectuuid("12313243jg48fj93j8434r2-2e312d232");
		RelativeWebDAO.insertRelativeWeb(web);
	}

	public static int countNum(String puuid) throws SQLException {
		int result = DAOUtils.count("select count(*) from RelativeWeb where projectuuid = ?", puuid);
		return result;
	}
}
