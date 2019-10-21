package com.datasynchronize.datasync;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.esserver.SearchApplication;
import com.esserver.config.ConfigEngine;
import com.esserver.config.application.vo.ESApplication;
import com.esserver.datasync.DataSyncEngine;
import com.esserver.datasync.service.DataSynchronizedService;

/**
 * 测试数据同步
 * @author du
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchApplication.class)
public class TestDataSynchronizeService {

	@Autowired
	private ConfigEngine configEngine;
	
	@Autowired
	private DataSynchronizedService dataSynchronizedService;
	
	@Autowired
	private DataSyncEngine dataSyncEngine;
	
	@Test
	public void testQueryAllPrimaryKey() {//从数据库取数据
		ESApplication app = configEngine.queryApplicationByAppID(1L);
		configEngine.switchDataSourceByAppID(1L);
		List<String> primaryKeyList = dataSynchronizedService.queryAllPrimaryKeyListByConfig(app.getSyncPrimaryTableConfig());
		System.out.println("primaryKeyList.size()=" + primaryKeyList.size());
		System.out.println(primaryKeyList.get(0));
		
//		在xml文件中在加一个ESApplication节点，然后把下面的放开修改下，测试动态数据源是否切换过来了
//		ESApplication app2 = configEngine.queryApplicationByAppID(2L);
//		configEngine.switchDataSourceByAppID(2L);
//		List<String> primaryKeyList2 = dataSynchronizedService.queryAllPrimaryKeyListByConfig(app2.getSyncPrimaryTableConfig());
//		System.out.println("primaryKeyList2.size()=" + primaryKeyList2.size());
//		System.out.println(primaryKeyList2.get(0));
		
	}
	
	@Test
	public void testFullDataSync() {//向ES中全量同步
		//单独执行这一个方法就行，spring启动的时候会进行xml文件解析  
		//@RunWith(SpringJUnit4ClassRunner.class)  模拟启动spring
		dataSyncEngine.startupFullDataSynchronize(1L);
		try {
			Thread.sleep(100000000L);//只有单元测试要这样处理  ，不睡的话 ，主线程testFullDataSync方法执行完了，就会关闭  会同步不完
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testIncreDataSync() {//向ES中增量同步  需要在xml中的ESApplication加个最后同步时间lastSyncTime 
		//单独执行这一个方法就行，spring启动的时候会进行xml文件解析  
		//@RunWith(SpringJUnit4ClassRunner.class)  模拟启动spring
		dataSyncEngine.startupIncreDataSynchronize(1L);
		try {
			Thread.sleep(100000000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
