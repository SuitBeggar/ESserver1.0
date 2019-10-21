package com.esserver.config.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.esserver.config.application.vo.ESApplication;
import com.esserver.config.datasearch.vo.ESQueryColumnConfig;
import com.esserver.config.datasearch.vo.ESQueryPowerConfig;
import com.esserver.config.datasync.vo.ESAnalyzeConfig;
import com.esserver.config.datasync.vo.ESDataFilterConfig;
import com.esserver.config.datasync.vo.ESSyncChildTableConfig;
import com.esserver.config.datasync.vo.ESSyncDataSource;
import com.esserver.config.datasync.vo.ESSyncPlanConfig;
import com.esserver.config.datasync.vo.ESSyncPrimaryTableConfig;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

public class XStreamUtils {
	public static final XmlFriendlyNameCoder nameCoder = new XmlFriendlyNameCoder();

	// 编码格式
	private static final String ENCODING = "UTF-8";

	// dom解析驱动
	private static final DomDriver fixDriver = new DomDriver(ENCODING, nameCoder);

	// 通用解析器
	public static final XStream XSTREAM = new XStream(fixDriver);

	// 默认时区 上海
	private static final String CHINA_TIME_ZONE = "Asia/Shanghai";

	static {
		// 时区处理
		TimeZone zone = TimeZone.getTimeZone(CHINA_TIME_ZONE);
		XSTREAM.registerConverter(new DateConverter(zone), XStream.PRIORITY_NORMAL);
		XSTREAM.autodetectAnnotations(true);

	}

	private XStreamUtils() {
	}
	
	
	
	/**
	 * 解析XML文件，获取制定节点内容   弃用
	 * @param filePath：XML文件路径    xml：文件路径     
	 * @param nodeName  节点
	 * @return 
	 */
	/*public static List<Map<String,String>> parseXmlFile(String filePath,String nodeName){
		
//		https://www.cnblogs.com/Qian123/p/5231303.html
		 //1、创建一个DocumentBuilderFactory的对象
		 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         //2、创建一个DocumentBuilder的对象
         DocumentBuilder db;
         List<Map<String,String>> nodeList = new ArrayList<Map<String,String>>();
         Map<String,String> nodeMap = null;
		 try {
//			创建DocumentBuilder的对象
			db = dbf.newDocumentBuilder();
            注意导入Document对象时，要导入org.w3c.dom.Document包下的
	        Document document = db.parse(filePath);//传入文件名可以是相对路径也可以是绝对路径
//          Document document = db.parse(XStreamUtils.XSTREAM.getClass().getResource("/").getPath() + "ESApplication.xml");
            //获取所有book节点的集合
            NodeList bookList = document.getElementsByTagName(nodeName);
            System.out.println("一共有" + bookList.getLength());
            for (int i = 0; i < bookList.getLength(); i++) {
	           //通过 item(i)方法 获取一个book节点，nodelist的索引值从0开始
	           Node book = bookList.item(i);
	           //解析book节点的子节点
	           NodeList childNodes = book.getChildNodes();
//	           System.out.println("第 " + (i + 1) + "本书共有" + childNodes.getLength() + "个属性");
	           //遍历childNodes获取每个节点的节点名和节点值
	           nodeMap = new HashMap<String,String>();
	            for (int k = 0; k < childNodes.getLength(); k++) {
	               //区分出text类型的node以及element类型的node
                 if(childNodes.item(k).getNodeType() == Node.ELEMENT_NODE){
//                	 System.out.println("aa"+childNodes.item(k));
//                	 System.out.println("bb"+childNodes.item(k).getFirstChild());
//                	 System.out.println("cc"+childNodes.item(k).getFirstChild().getNodeValue());
                	 if(childNodes.item(k).getFirstChild()!=null){
                         //获取了element类型节点的节点名
                       System.out.print("第" + (k + 1) + "个节点的节点名：" + childNodes.item(k).getNodeName());
                     //获取了element类型节点的节点值
                       System.out.println("--节点值是：" + childNodes.item(k).getFirstChild().getNodeValue());
                		 nodeMap.put(childNodes.item(k).getNodeName(), childNodes.item(k).getFirstChild().getNodeValue());
                	 }
                 }
	           }  
	            nodeList.add(nodeMap);
            }
//            System.out.println(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 return nodeList;
	}*/
	
	
	
	/**
	 * 读取XML配置文件，组装list    弃用
	 * @param filePath  xml文件路径
	 * @param nodeName  节点
	 * @return
	 */
	/*public static List<ESApplication> assembleApplicationList(String filePath,Map<String,String> nodeNames){
		
		//Application
		List<Map<String,String>> nodeESApplicationList = XStreamUtils.parseXmlFile(filePath,nodeNames.get("ESApplication"));
		ESApplication app = new ESApplication();
		app.setId(Long.parseLong(nodeESApplicationList.get(0).get("id")));
		app.setAppName(nodeESApplicationList.get(0).get("appName"));
		app.setAppCode(nodeESApplicationList.get(0).get("appCode"));
		app.setIndexName(nodeESApplicationList.get(0).get("indexName"));
		app.setIndexCode(nodeESApplicationList.get(0).get("indexCode"));
		app.setEsCluster(nodeESApplicationList.get(0).get("esCluster"));
		app.setEsNodeHost(nodeESApplicationList.get(0).get("esNodeHost"));
		app.setDataSyncType(nodeESApplicationList.get(0).get("dataSyncType"));
		app.setLastSyncTime(nodeESApplicationList.get(0).get("lastSyncTime"));
		app.setStatus(nodeESApplicationList.get(0).get("status"));
		//同步数据源
		List<Map<String,String>> nodesyncDataSourceList = XStreamUtils.parseXmlFile(filePath,nodeNames.get("syncDataSource"));
		ESSyncDataSource syncDataSource = new ESSyncDataSource();
		syncDataSource.setId(Long.parseLong(nodesyncDataSourceList.get(0).get("id")));
		syncDataSource.setAppID(Long.parseLong(nodesyncDataSourceList.get(0).get("appID")));
		syncDataSource.setDatabaseType(nodesyncDataSourceList.get(0).get("databaseType"));
		syncDataSource.setUrl(nodesyncDataSourceList.get(0).get("url"));
		syncDataSource.setDriverClassName(nodesyncDataSourceList.get(0).get("driverClassName"));
		syncDataSource.setUser(nodesyncDataSourceList.get(0).get("user"));
		syncDataSource.setPassword(nodesyncDataSourceList.get(0).get("password"));
		app.setSyncDataSource(syncDataSource);
		//查询权限配置  
		List<Map<String,String>> queryPowerConfig = XStreamUtils.parseXmlFile(filePath,nodeNames.get("queryPowerConfig"));
		ESQueryPowerConfig eSQueryPowerConfig = new ESQueryPowerConfig();
		eSQueryPowerConfig.setId(Long.parseLong(queryPowerConfig.get(0).get("id")));
		eSQueryPowerConfig.setAppID(Long.parseLong(queryPowerConfig.get(0).get("appID")));
		eSQueryPowerConfig.setRequestOrigin(queryPowerConfig.get(0).get("requestOrigin"));
		eSQueryPowerConfig.setSecretKey(queryPowerConfig.get(0).get("secretKey"));
		app.setQueryPowerConfig(eSQueryPowerConfig);
		//同步主表配置
		List<Map<String,String>> nodesyncPrimaryTableConfigList = XStreamUtils.parseXmlFile(filePath,nodeNames.get("syncPrimaryTableConfig"));
		ESSyncPrimaryTableConfig  eSSyncPrimaryTableConfig  = new ESSyncPrimaryTableConfig(); 
		eSSyncPrimaryTableConfig.setId(Long.parseLong(nodesyncPrimaryTableConfigList.get(0).get("id")));
		eSSyncPrimaryTableConfig.setAppID(Long.parseLong(nodesyncPrimaryTableConfigList.get(0).get("appID")));
		eSSyncPrimaryTableConfig.setSimpleTableFlag(nodesyncPrimaryTableConfigList.get(0).get("simpleTableFlag"));
		eSSyncPrimaryTableConfig.setPrimarySQL(nodesyncPrimaryTableConfigList.get(0).get("primarySQL"));
		eSSyncPrimaryTableConfig.setPrimaryKeySQL(nodesyncPrimaryTableConfigList.get(0).get("primaryKeySQL"));
		eSSyncPrimaryTableConfig.setPrimaryKeyName(nodesyncPrimaryTableConfigList.get(0).get("primaryKeyName"));
		eSSyncPrimaryTableConfig.setIndexPrimaryKey(nodesyncPrimaryTableConfigList.get(0).get("indexPrimaryKey"));
		eSSyncPrimaryTableConfig.setIncrementKeySQL(nodesyncPrimaryTableConfigList.get(0).get("incrementKeySQL"));
		eSSyncPrimaryTableConfig.setDelPrimaryKeySQL(nodesyncPrimaryTableConfigList.get(0).get("delPrimaryKeySQL"));
		app.setSyncPrimaryTableConfig(eSSyncPrimaryTableConfig);
		//同步子表配置  一对多
		List<Map<String,String>> nodeESSyncChildTableConfigList = XStreamUtils.parseXmlFile(filePath,nodeNames.get("ESSyncChildTableConfig"));
		List<ESSyncChildTableConfig> eSSyncChildTableConfigList = new ArrayList<>();
		for(int i = 0;i<nodeESSyncChildTableConfigList.size();i++){
			ESSyncChildTableConfig eSSyncChildTableConfig = new ESSyncChildTableConfig();
			eSSyncChildTableConfig.setId(Long.parseLong(nodeESSyncChildTableConfigList.get(i).get("id")));
			eSSyncChildTableConfig.setAppID(Long.parseLong(nodeESSyncChildTableConfigList.get(i).get("appID")));
			eSSyncChildTableConfig.setPtConfigID(Long.parseLong(nodeESSyncChildTableConfigList.get(i).get("ptConfigID")));
			eSSyncChildTableConfig.setFkColumn(nodeESSyncChildTableConfigList.get(i).get("fkColumn"));
			eSSyncChildTableConfig.setFkRelationColumn(nodeESSyncChildTableConfigList.get(i).get("fkRelationColumn"));
			eSSyncChildTableConfig.setChildDataListName(nodeESSyncChildTableConfigList.get(i).get("childDataListName"));
			eSSyncChildTableConfig.setChildSQL(nodeESSyncChildTableConfigList.get(i).get("childSQL"));
			eSSyncChildTableConfigList.add(eSSyncChildTableConfig);
		}
		eSSyncPrimaryTableConfig.setSyncChildTableConfigList(eSSyncChildTableConfigList);
		//同步策略配置
		List<Map<String,String>> nodesyncPlanConfigList = XStreamUtils.parseXmlFile(filePath,nodeNames.get("syncPlanConfig"));
		ESSyncPlanConfig eSSyncPlanConfig = new ESSyncPlanConfig();
		eSSyncPlanConfig.setId(Long.parseLong(nodesyncPlanConfigList.get(0).get("id")));
		eSSyncPlanConfig.setAppID(Long.parseLong(nodesyncPlanConfigList.get(0).get("appID")));
		eSSyncPlanConfig.setFullSyncCronExp(nodesyncPlanConfigList.get(0).get("fullSyncCronExp"));
		eSSyncPlanConfig.setFullSyncSwitch(nodesyncPlanConfigList.get(0).get("fullSyncSwitch"));
		eSSyncPlanConfig.setIncreSyncCronExp(nodesyncPlanConfigList.get(0).get("increSyncCronExp"));
		eSSyncPlanConfig.setIncreSyncSwitch(nodesyncPlanConfigList.get(0).get("increSyncSwitch"));
     	eSSyncPlanConfig.setThreadCount(Integer.parseInt(nodesyncPlanConfigList.get(0).get("threadCount")));
     	eSSyncPlanConfig.setFirstStartSyncFlag(nodesyncPlanConfigList.get(0).get("firstStartSyncFlag"));
     	app.setSyncPlanConfig(eSSyncPlanConfig);
		//字段分词配置 一对多
		List<Map<String,String>> nodeESAnalyzeConfigList = XStreamUtils.parseXmlFile(filePath,nodeNames.get("ESAnalyzeConfig"));
		List<ESAnalyzeConfig> eSAnalyzeConfigList = new ArrayList<>();
		for(int i = 0;i<nodeESAnalyzeConfigList.size();i++){
			ESAnalyzeConfig eSAnalyzeConfig = new ESAnalyzeConfig();
			eSAnalyzeConfig.setId(Long.parseLong(nodeESAnalyzeConfigList.get(i).get("id")));
			eSAnalyzeConfig.setAppID(Long.parseLong(nodeESAnalyzeConfigList.get(i).get("appID")));
			eSAnalyzeConfig.setColumnName(nodeESAnalyzeConfigList.get(i).get("columnName"));
			eSAnalyzeConfig.setAnalyzeType(nodeESAnalyzeConfigList.get(i).get("analyzeType"));
			eSAnalyzeConfigList.add(eSAnalyzeConfig);
		}
		app.setAnalyzeConfigList(eSAnalyzeConfigList);
		 //数据过滤配置  一对多
		List<Map<String,String>> nodeESDataFilterConfigList = XStreamUtils.parseXmlFile(filePath,nodeNames.get("ESDataFilterConfig"));
		List<ESDataFilterConfig> eSDataFilterConfigList = new ArrayList<>();
		for(int i = 0;i<nodeESDataFilterConfigList.size();i++){
			ESDataFilterConfig eSDataFilterConfig = new ESDataFilterConfig();
			eSDataFilterConfig.setId(Long.parseLong(nodeESDataFilterConfigList.get(i).get("id")));
			eSDataFilterConfig.setAppID(Long.parseLong(nodeESDataFilterConfigList.get(i).get("appID")));
			eSDataFilterConfig.setDictFilePath(nodeESDataFilterConfigList.get(i).get("dictFilePath"));
			eSDataFilterConfig.setFilterColumns(nodeESDataFilterConfigList.get(i).get("filterColumns"));
			eSDataFilterConfigList.add(eSDataFilterConfig);
		}
		app.setDataFilterConfigList(eSDataFilterConfigList);
		//字段查询配置  一对多
		List<Map<String,String>> nodeESQueryColumnConfigList = XStreamUtils.parseXmlFile(filePath,nodeNames.get("ESQueryColumnConfig"));
		List<ESQueryColumnConfig> eSQueryColumnConfigList = new ArrayList<>();
		for(int i = 0;i<nodeESQueryColumnConfigList.size();i++){
			ESQueryColumnConfig eSQueryColumnConfig = new ESQueryColumnConfig();
			eSQueryColumnConfig.setId(Long.parseLong(nodeESQueryColumnConfigList.get(i).get("id")));
			eSQueryColumnConfig.setAppID(Long.parseLong(nodeESQueryColumnConfigList.get(i).get("appID")));
			eSQueryColumnConfig.setQueryColumn(nodeESQueryColumnConfigList.get(i).get("queryColumn"));
			eSQueryColumnConfig.setMatchType(nodeESQueryColumnConfigList.get(i).get("matchType"));
			eSQueryColumnConfig.setFilterCondFlag(nodeESQueryColumnConfigList.get(i).get("filterCondFlag"));
			eSQueryColumnConfig.setHighlightFlag(nodeESQueryColumnConfigList.get(i).get("highlightFlag"));
			eSQueryColumnConfig.setSortFlag(nodeESQueryColumnConfigList.get(i).get("sortFlag"));
			eSQueryColumnConfig.setSortPriority(nodeESQueryColumnConfigList.get(i).get("sortPriority"));
			eSQueryColumnConfigList.add(eSQueryColumnConfig);
		}
		app.setQueryColumnConfigList(eSQueryColumnConfigList);
		List<ESApplication> eSApplicationList  = new ArrayList<>();
		eSApplicationList.add(app);
		System.out.println(eSApplicationList);
		return eSApplicationList;
	}*/
	
	
	
	/**
	 * 读取XML文件,返回对应报文
	 * @param path
	 * @return
	 */
	
	public static String readXmlFile(String path){
		String retStr = "";
		try {
			 //1.查找对象
	         FileReader fileReader = new FileReader(path);
	         //字符缓存区
	         BufferedReader buffReder = new BufferedReader(fileReader);
	         String lineTxt = "";  
	         int i = 0;
	         while((lineTxt = buffReder.readLine()) != null){
	             //当c的ASCII值等于换行符的时候
	        	 i++;
	        	 lineTxt += '\n';  
                 retStr += lineTxt;
//                 System.out.print(i+"\t"+lineTxt);
	         }
//	         System.out.print(retStr);
	         //终止资源
	         buffReder.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return retStr;
	}
	
	
	
	
	/**
	 * 注：SpringBoot打包为jar启动时不会将jar解压，所以不能以获取文件路径的方式读取资源文件，而是要直接获取文件流,返回对应报文
	 * @param path
	 * @return
	 */
	public static String getread(InputStream inputStream) throws IOException {
//		InputStream inputStream = XStreamUtils.XSTREAM.getClass().getResourceAsStream("/ESApplication.xml");
		BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
		String string = null;
		StringBuffer buffer = new StringBuffer();
		while ((string = bf.readLine()) != null) {
			buffer.append(string);
		}
		return buffer.toString();
	}
	
	public static void main(String[] args) {
		XSTREAM.alias("ESApplication", ESApplication.class);
		XSTREAM.alias("ESSyncChildTableConfig",ESSyncChildTableConfig.class);
		XSTREAM.alias("ESAnalyzeConfig",ESAnalyzeConfig.class);
		XSTREAM.alias("ESDataFilterConfig",ESDataFilterConfig.class);
		XSTREAM.alias("ESQueryColumnConfig",ESQueryColumnConfig.class);
		List<ESApplication> eSApplicationList  = new ArrayList<>();
		ESApplication app = new ESApplication();
		
//	    ESApplicationList  esApplicationList = new ESApplicationList();
//	    esApplicationList.setApplicationList(eSApplicationList);
	    
		app.setId(1L);
		app.setAppName("农险业务");
		app.setAppCode("nxyw");
		app.setIndexName("案件处理");
		app.setIndexCode("caseDispatch_index");
		app.setEsCluster("applicationTest");
		app.setEsNodeHost("localhost:9300");
		app.setDataSyncType("dataSource");
		app.setLastSyncTime("2018-04-17 00:00:00");
		app.setStatus("\"\"");
		//同步数据源
		ESSyncDataSource syncDataSource = new ESSyncDataSource();
		syncDataSource.setId(1L);
		syncDataSource.setAppID(1L);
		syncDataSource.setDatabaseType("oracle");
		syncDataSource.setUrl("jdbc:oracle:thin:@10.10.68.60:1521:argobj");
//		syncDataSource.setDriverClassName("com.sql.Oracle.Driver");
		syncDataSource.setDriverClassName("oracle.jdbc.OracleDriver");
		syncDataSource.setUser("prptest_nx");
		syncDataSource.setPassword("prptest_nx");
		app.setSyncDataSource(syncDataSource);
		
		//查询权限配置
		ESQueryPowerConfig eSQueryPowerConfig = new ESQueryPowerConfig();
		eSQueryPowerConfig.setId(0L);
		eSQueryPowerConfig.setAppID(0L);
		eSQueryPowerConfig.setRequestOrigin("2");
		eSQueryPowerConfig.setSecretKey("2");
		app.setQueryPowerConfig(eSQueryPowerConfig);
		
		//同步主表配置
		ESSyncPrimaryTableConfig  eSSyncPrimaryTableConfig  = new ESSyncPrimaryTableConfig(); 
		eSSyncPrimaryTableConfig.setId(1l);
		eSSyncPrimaryTableConfig.setAppID(1L);
		eSSyncPrimaryTableConfig.setSimpleTableFlag("Y");
		eSSyncPrimaryTableConfig.setPrimarySQL("select registno,policyno,reportorname,reportornumber,linkername,phonenumber from nx_baxxb where registno={primarykey}");
		eSSyncPrimaryTableConfig.setPrimaryKeySQL("select registno from nx_baxxb");
		eSSyncPrimaryTableConfig.setPrimaryKeyName("registno");
		eSSyncPrimaryTableConfig.setIndexPrimaryKey("registno");
		eSSyncPrimaryTableConfig.setIncrementKeySQL("select registno from nx_baxxb where operatetimeforhis >= {oldTime}");
		eSSyncPrimaryTableConfig.setDelPrimaryKeySQL("\"\"");
		app.setSyncPrimaryTableConfig(eSSyncPrimaryTableConfig);
		
		//同步子表配置
		ESSyncChildTableConfig eSSyncChildTableConfig = new ESSyncChildTableConfig();
		eSSyncChildTableConfig.setId(0L);
		eSSyncChildTableConfig.setAppID(0L);
		eSSyncChildTableConfig.setPtConfigID(0L);
		eSSyncChildTableConfig.setFkColumn("\"\"");
		eSSyncChildTableConfig.setFkRelationColumn("\"\"");
		eSSyncChildTableConfig.setChildDataListName("\"\"");
		eSSyncChildTableConfig.setChildSQL("\"\"");
		
		
		List<ESSyncChildTableConfig> eSSyncChildTableConfigList = new ArrayList<>();
		eSSyncChildTableConfigList.add(eSSyncChildTableConfig);
		eSSyncChildTableConfig = new ESSyncChildTableConfig();
		
		eSSyncChildTableConfig.setId(0L);
		eSSyncChildTableConfig.setAppID(0L);
		eSSyncChildTableConfig.setPtConfigID(0L);
		eSSyncChildTableConfig.setFkColumn("\"\"");
		eSSyncChildTableConfig.setFkRelationColumn("\"\"");
		eSSyncChildTableConfig.setChildDataListName("\"\"");
		eSSyncChildTableConfig.setChildSQL("\"\"");
		
//		List<ESSyncChildTableConfig> eSSyncChildTableConfigList = new ArrayList<>();
		eSSyncChildTableConfigList.add(eSSyncChildTableConfig);
		eSSyncPrimaryTableConfig.setSyncChildTableConfigList(eSSyncChildTableConfigList);

		//同步策略配置
		ESSyncPlanConfig eSSyncPlanConfig = new ESSyncPlanConfig();
		eSSyncPlanConfig.setId(0L);
		eSSyncPlanConfig.setAppID(0L);
		eSSyncPlanConfig.setFullSyncCronExp("\"\"");
		eSSyncPlanConfig.setFullSyncSwitch("N");
		eSSyncPlanConfig.setIncreSyncCronExp("\"\"");
		eSSyncPlanConfig.setIncreSyncSwitch("N");
		eSSyncPlanConfig.setThreadCount(25);
		eSSyncPlanConfig.setFirstStartSyncFlag("N");
		app.setSyncPlanConfig(eSSyncPlanConfig);
		
	
		
		
		//字段分词配置
		ESAnalyzeConfig eSAnalyzeConfig = new ESAnalyzeConfig();
		eSAnalyzeConfig.setId(0L);
		eSAnalyzeConfig.setAppID(0L);
		eSAnalyzeConfig.setColumnName("reportorname");
		eSAnalyzeConfig.setAnalyzeType("standard");
		
		List<ESAnalyzeConfig> eSAnalyzeConfigList = new ArrayList<>();
		eSAnalyzeConfigList.add(eSAnalyzeConfig);
		eSAnalyzeConfig = new ESAnalyzeConfig();
		
		eSAnalyzeConfig.setId(0L);
		eSAnalyzeConfig.setAppID(0L);
		eSAnalyzeConfig.setColumnName("linkername");
		eSAnalyzeConfig.setAnalyzeType("standard");
//		List<ESAnalyzeConfig> eSAnalyzeConfigList = new ArrayList<>();
		eSAnalyzeConfigList.add(eSAnalyzeConfig);
		app.setAnalyzeConfigList(eSAnalyzeConfigList);
		
		//数据过滤配置
		ESDataFilterConfig eSDataFilterConfig = new ESDataFilterConfig();
		eSDataFilterConfig.setId(0L);
		eSDataFilterConfig.setAppID(0L);
		eSDataFilterConfig.setDictFilePath("9");
		eSDataFilterConfig.setFilterColumns("linkername");
		
		List<ESDataFilterConfig> eSDataFilterConfigList = new ArrayList<>();
		eSDataFilterConfigList.add(eSDataFilterConfig);
		eSDataFilterConfig = new ESDataFilterConfig();
		eSDataFilterConfig.setId(0L);
		eSDataFilterConfig.setAppID(0L);
		eSDataFilterConfig.setDictFilePath("10");
		eSDataFilterConfig.setFilterColumns("10");
		
//		List<ESDataFilterConfig> eSDataFilterConfigList = new ArrayList<>();
		eSDataFilterConfigList.add(eSDataFilterConfig);
		app.setDataFilterConfigList(eSDataFilterConfigList);
		
		//字段查询配置
		ESQueryColumnConfig eSQueryColumnConfig = new ESQueryColumnConfig();
		eSQueryColumnConfig.setId(0L);
		eSQueryColumnConfig.setAppID(0L);
		eSQueryColumnConfig.setQueryColumn("11");
		eSQueryColumnConfig.setMatchType("11");
		eSQueryColumnConfig.setFilterCondFlag("11");
		eSQueryColumnConfig.setHighlightFlag("11");
		eSQueryColumnConfig.setSortFlag("11");
		eSQueryColumnConfig.setSortPriority("11");
		
		List<ESQueryColumnConfig> eSQueryColumnConfigList = new ArrayList<>();
		eSQueryColumnConfigList.add(eSQueryColumnConfig);
		eSQueryColumnConfig = new ESQueryColumnConfig();
		
		eSQueryColumnConfig.setId(0L);
		eSQueryColumnConfig.setAppID(0L);
		eSQueryColumnConfig.setQueryColumn("12");
		eSQueryColumnConfig.setMatchType("12");
		eSQueryColumnConfig.setFilterCondFlag("12");
		eSQueryColumnConfig.setHighlightFlag("12");
		eSQueryColumnConfig.setSortFlag("12");
		eSQueryColumnConfig.setSortPriority("12");
		
		
//		List<ESQueryColumnConfig> eSQueryColumnConfigList = new ArrayList<>();
		eSQueryColumnConfigList.add(eSQueryColumnConfig);
		app.setQueryColumnConfigList(eSQueryColumnConfigList);
		
		eSApplicationList.add(app);
		System.out.println(eSApplicationList);
		String configPath = XSTREAM.getClass().getResource("/").getPath() + "ESApplication.xml";
		configPath = configPath.replaceAll("%20", " ");
		try {
			System.out.println(configPath);
			File f = new File(configPath);
			if (!f.exists()) {
				f.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(f, false);
			XSTREAM.toXML(eSApplicationList, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
