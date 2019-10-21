package com.config.datasource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.esserver.SearchApplication;
import com.esserver.config.ConfigEngine;
import com.esserver.config.application.vo.ESApplication;
import com.esserver.config.common.datasource.DynamicDataSourceFactory;
import com.esserver.config.datasync.vo.ESSyncDataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestDynamicDataSourceFactory {
    @Autowired
	private DynamicDataSourceFactory factory;
	
	@Autowired
	private ConfigEngine configEngine;
	
	@Autowired
	private DataSource dataSource;
	
    @Test
    public void test(){
    	ESApplication application = new ESApplication();
    	application.setId(1L);
    	ESSyncDataSource syncDataSource = new ESSyncDataSource();
		syncDataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		syncDataSource.setUrl("jdbc:oracle:thin:@47.104.78.118:1521:orcl");
		syncDataSource.setUser("arch6splatform");
		syncDataSource.setPassword("arch6splatform");
		factory.addOrUpdateSyncDataSource(syncDataSource);
		configEngine.switchDataSourceByAppID(application.getId());
		System.out.println("切换数据源");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement("select count(*) as c from USERMENU");
			rs = ps.executeQuery();
			if (rs.next()) {
				int count = rs.getInt("c");
				System.out.println("菜单总数为" + count);
			} else {
				System.out.println("未查询到数据");
			}
		} catch (SQLException e) {
			try {
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
    }
}
