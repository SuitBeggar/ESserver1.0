package com.esserver.datasync.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import ins.framework.mybatis.MybatisBaseDao;

@Repository
public interface DataSynchronizedDao extends MybatisBaseDao<Object, Object> {

	/**
	 * 查询全量主键值
	 * 
	 * @param primaryKeysql
	 * @return
	 */
	public List<String> queryPrimaryKey(@Param("primaryKeySQL") String primaryKeySQL);

	/**
	 * 查询惟一结果集
	 * 
	 * @param dynamicSql
	 * @return
	 */
	public Map<String, Object> queryUniqueData(@Param("dynamicSql") String dynamicSql);

	/**
	 * 查询全量数据
	 * 
	 * @param dynamicSql
	 * @return
	 */
	public List<Map<String, Object>> queryAllData(String dynamicSql);
}
