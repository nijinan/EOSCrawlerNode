package cn.edu.pku.EOSCN.business;

import java.io.File;

import cn.edu.pku.EOSCN.DAO.JDBCPool;
import cn.edu.pku.EOSCN.config.Config;
import cn.edu.pku.EOSCN.crawler.util.FileOperation.FileUtil;

/**
 * 鏈嶅姟鍣ㄥ惎鍔ㄦ椂澶勭悊鍒濆鍖栦簨鍔＄殑绫伙紝鍖呮嫭鍒濆鍖栨暟鎹簱杩炴帴姹犵瓑
 * @author 寮犵伒绠�
 *
 */

public class InitBusiness {
	public static void initEOS(){
		try {
			//JDBCPool.initPool();
			FileUtil.init();
			NetWorkDaemon.init();
			ThreadManager.initCrawlerTaskManager();
			File file = new File(Config.getTempDir());
			if (!file.exists()) {
				file.mkdir();
			}
		} catch (Exception e) {
			System.out.println("initiation failed!");
			e.printStackTrace();
		} 
	}
}
