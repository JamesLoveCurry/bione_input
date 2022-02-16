package com.yusys.biapp.input.data.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yusys.biapp.input.data.entity.MtoolDbTableProBO;

/**
 * <pre>
 * 	Title:RDB数据源管理公用类
 * </pre>
 * 
 * @author 
 * @version 1.00.00
 */
@Service
public class RdbConnectionManagerBS implements DisposableBean, IRdbConnectionManager {

	@Override
	public void destroy() throws Exception {
		
	}

	public List<MtoolDbTableProBO> getTableMoreList(String dsId,
			String tableName) {
		return null;
	}
	 /**
		 * 根据数据源获取该数据库下的所有表名和表中文名称
		 * @param dsId
		 * @return
		 */
		public List<Map<String,String>> getTableNameAndCommentsList(String dsId){
			List<Map<String,String>> tableList = Lists.newArrayList();
//			Connection conn = null;
//			Statement state = null;
//			ResultSet rs = null;
//			try {
//				conn = getConnection(dsId);
//				state = conn.createStatement();
//				rs = state.executeQuery("select table_name,comments from user_tab_comments where table_name not like 'BIN$%'");
//				Map<String,String> tableMap = null;
//				while(rs.next()){
//					tableMap = Maps.newHashMap();
//					tableMap.put("dsName", rs.getString(1));
//					tableMap.put("commName", rs.getString(2));
//					tableList.add(tableMap);
//				}
//				
//			} catch (Exception e) {
//				log.error(e);
//			} finally {
//				RdbConnectionManagerBS.closeResultSet(rs);
//				RdbConnectionManagerBS.closeStatement(state);
//				RdbConnectionManagerBS.closeConnection(conn);
//			}
			return tableList;
		}
		
	    /**
	     * 根据数据源主键ID得到一个Connection对象
	     * 
	     * @param id
	     *            数据源主键ID
	     * 
	     * 
	     * @return Connection jdbc数据库连接对象
	     * 
	     * @throws Exception
	     * 
	     */
//	    public Connection getConnection(String id) throws Exception {
//	       // UdipMtoolRdbDsBO mtoolRdbDsBO = change(id);
////	        if (mtoolRdbDsBO != null)
////	            return getConnection(mtoolRdbDsBO);
////	        else
////	            return null;
//	    }
}
