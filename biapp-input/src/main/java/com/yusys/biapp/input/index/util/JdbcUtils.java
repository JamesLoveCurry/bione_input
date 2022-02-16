package com.yusys.biapp.input.index.util;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.index.util.vo.DsInfoVO;
import com.yusys.biapp.input.index.util.vo.GridVO;

public class JdbcUtils {

	private String sql;
	private DsInfoVO ds;
	
	
	public JdbcUtils(String sql, DsInfoVO ds) {
		this.sql = sql;
		this.ds = ds;
	}

	public Connection getConnection() throws Exception{
			Class.forName(ds.getDriverName());
			return  DriverManager.getConnection(ds.getConnUrl(), ds.getConnUser(),
					ds.getConnPwd());
	}
	

	public List<Map<String, Object>> executeQuerySql() throws Exception{
		Connection conn = null;
		PreparedStatement stamt=null;
		try{
			List< Map<String, String>>VOlist=Lists.newArrayList(); //grid中的column
			Map<String, Object>map=Maps.newHashMap();
			List<Map<String, Object>>valList=Lists.newArrayList();
			conn =  getConnection();
			conn.setReadOnly(true);  //设置为只读
			sql+="  with ur";  //db2数据库方言，避免脏读，只读取正在执行事务的执行前数据		
			stamt=conn.prepareStatement(sql);
			stamt.setMaxRows(1000);  //设置查询最大条数为1000
			ResultSet rset=stamt.executeQuery();
			

			//查询表结构
			ResultSetMetaData metadata=stamt.getMetaData();
			 int m=metadata.getColumnCount();
		     for(int n=1;n<=m;n++){
		    	 Map<String, String>columnList=Maps.newHashMap();
		    	 columnList.put("name", metadata.getColumnName(n));
		    	 columnList.put("type", metadata.getColumnTypeName(n));
		    	VOlist.add(columnList);
		     }
		   //  JSONArray jsonobject=JSONArray.fromObject(VOlist);
		     map.put("VOlist", createGridVoList(VOlist));
			//查询表数据
			while(rset.next()){
				Map<String, Object>colmap=Maps.newHashMap();
				for(int a=0;a<VOlist.size();a++){
					Object o=null;
					if(VOlist.get(a).get("type").equals("TIMESTAMP")){
					    o=rset.getTimestamp(VOlist.get(a).get("name"))+"";  //timestamp字段处理
					}
					else if(VOlist.get(a).get("type").equals("CLOB")){  //clob字段处理
						o=rset.getString(VOlist.get(a).get("name"));
					}
					else{
						o=rset.getObject(VOlist.get(a).get("name"));
					}
					colmap.put(VOlist.get(a).get("name"),o);  
				}
				valList.add(colmap);
			}
			return valList;
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}finally{

			if(stamt!=null){
				stamt.close();
			}
			if(conn!=null){
				conn.close();
			}
		}
	}

	private List<GridVO> createGridVoList(List< Map<String, String>>list){
		List<GridVO> listVO=Lists.newArrayList();
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				GridVO gv=new GridVO();
		    	gv.setDisplay(list.get(i).get("name"));
		    	gv.setName(list.get(i).get("name"));
		    	listVO.add(gv);
			}
		}
		return listVO;
	}
}
