package com.yusys.biapp.input.dict.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.catalog.service.DataInputCatalogBS;
import com.yusys.biapp.input.dict.entity.RptInputListDataDictInfo;
import com.yusys.biapp.input.dict.repository.RptInputListDataDictInfoDao;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.dict.vo.InputListDataDictInfoVO;
import com.yusys.biapp.input.dict.web.DataListDictController;
import com.yusys.biapp.input.security.authres.DictResImpl;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;

@Service
@Transactional(readOnly = true)
public class InputDataDictBS extends BaseBS<RptInputListDataDictInfo>{
	private final Log log = LogFactory.getLog(DataListDictController.class);
	
	@Autowired
	private DataInputCatalogBS dirBS;
	
	@Autowired
	private DataSourceBS dataSourceBS;
	
	@Autowired
	private RptInputListDataDictInfoDao dictDao;
	
	/**
	 * 根据数据字典目录和逻辑编号分页搜索数据字典信息
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @param dir
	 * @return
	 */
	public SearchResult<RptInputListDataDictInfo> getSearch(int firstResult, int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap, String dir,boolean all,String dsId) {
		StringBuilder jql = new StringBuilder(1000);
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		jql.append("select s.dictId,s.dictName,s.dictType,s.sqlText,s.createTime,s.createUser,s.dsId from RptInputListDataDictInfo s where s.logicSysNo ='").append(logicSysNo).append("'");
		if (StringUtils.isNotBlank(dir) && !"0".equals(dir)) {
			jql.append(" and s.catalogId ='").append(dir).append("'");
		}
		if (!conditionMap.get("jql").equals("")) {
			Object obj = conditionMap.get("jql");
			jql.append(" and ").append(obj.toString().replaceAll("dsId =", "s.dsId =").replaceAll("dictName", "s.dictName").replaceAll("dictType",  "s.dictType"));
		}
		if(StringUtils.isNotEmpty(dsId)){
			jql.append(" and s.dsId = :initDsId");
			values.put("initDsId", dsId);
		}
		/**有权限看的*/
		if(!all){
			DictResImpl libResImpl = SpringContextHolder.getBean(DictResImpl.name);
			List<String> idList = libResImpl.getLibId();
			
			if(idList==null || idList.isEmpty())
				return null;
			jql.append(" and s.dictId in :dictId");
			values.put("dictId", idList);
		}
		
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by s." + orderBy + " " + orderType);
		}
		SearchResult<Object[]> tempArray =  this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), values);
		List<RptInputListDataDictInfo>infoList=Lists.newArrayList();
		SearchResult<RptInputListDataDictInfo>searResult = new SearchResult<RptInputListDataDictInfo>();
		for(Object[] temp:tempArray.getResult()){
			RptInputListDataDictInfo info = new RptInputListDataDictInfo();
			info.setDictId(temp[0]==null?"":(String)temp[0]);
			info.setDictName(temp[1]==null?"":(String)temp[1]);
			info.setDictType(temp[2]==null?"":(String)temp[2]);
			info.setSqlText(temp[3]==null?"":(String)temp[3]);
			info.setCreateTime(temp[4]==null?"":(String)temp[4]);
			info.setCreateUser(temp[5]==null?"":(String)temp[5]);
			String sDsId = temp[6]==null?"":(String)temp[6];
			if(StringUtils.isNotEmpty(sDsId)){
				BioneDsInfo dsInfo = dataSourceBS.getEntityById(sDsId);
				if(dsInfo!=null && StringUtils.isNotEmpty(dsInfo.getDsName()))
					info.setDsName(dsInfo.getDsName());
			}
			infoList.add(info);
		}
		searResult.setResult(infoList);
		searResult.setTotalCount(tempArray.getTotalCount());
		return searResult;
	}

	/**
	 * 根据逻辑编号获取数据字典树目录
	 * @param logicSysNo
	 * @return
	 */
	public List<CommonTreeNode> buildLibTreeForDir(String nodeId,String logicSysNo) {
		List<CommonTreeNode> dirList = null;
		if(StringUtils.isNotBlank(nodeId)) {
			dirList = dirBS.getByTypeById(nodeId,UdipConstants.DIR_TYPE_LIB, logicSysNo);
		}else {
			dirList = dirBS.getByType(UdipConstants.DIR_TYPE_LIB, logicSysNo);
		}
		return dirList;
	}
	
	/**
	 * 根据字典Id，数据日期，节点ID获取指定节点及其子节点的数据
	 * @param libId
	 * @param dataDate
	 * @param parentCol
	 * @return
	 */
	public List<CommonTreeNode> getNodeListById(String libId, String dataDate,String nodeId) {
		List<CommonTreeNode> nodeList= buildLibTreeByLibId(libId, dataDate);
		List<CommonTreeNode> childrens = Lists.newArrayList();
		//如果为树形结构
		if(nodeList!=null && nodeList.size()>0 && nodeList.get(0)!=null && StringUtils.isNotBlank(nodeList.get(0).getUpId())){
			if(StringUtils.isNotBlank(nodeId)){
				// 获取该节点下的所有子节点。
				//TreeUtils.childrens(nodeList, nodeId, childrens);
				//获取本节点
				for(CommonTreeNode node:nodeList){
					if(nodeId.equals(node.getId())){
						childrens.add(node);
						break;
					}
				}
			}
			
		}
		return childrens;
	}
	/**
	 * 通过字典ID和日期字段获取数据字典信息，如果日期字段有值，就查询当前日期的数据，如果日期字段没有值，就查询所有的数据。 对于常量的字典，是全部查询。
	 * @param libId
	 * @param dataDate
	 * @return
	 */
	public List<CommonTreeNode> buildLibTreeByLibId(String libId, String dataDate) {
		List<CommonTreeNode> nodeList = Lists.newArrayList();
		List<RptInputListDataDictInfo> libList = dirBS.getEntityListByProperty(RptInputListDataDictInfo.class, "dictId", libId);
		if (libList != null && !libList.isEmpty() && libList.get(0) != null) {
			RptInputListDataDictInfo udipDataLib = libList.get(0);
			if (UdipConstants.LIB_LIB_TYPE_DIS.equals(udipDataLib.getDictType())) { // 字典类型,1：常量,2：数据库表
				Connection conn = null;
				Statement state = null;
				ResultSet rs = null;
				try {
					conn = dataSourceBS.getConnection(udipDataLib.getDsId());
					state = conn.createStatement();
					StringBuilder sql = new StringBuilder(1000);
					BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
					String sqlText = udipDataLib.getSqlText().replaceAll("@USERORG", "'"+user.getOrgNo()+"'").replaceAll("@USERID", " '"+user.getUserId()+"' ");
					sql.append(sqlText);
					rs = state.executeQuery(sql.toString());
					// 根据ShowType的值来判断展示类型是列表还是树,需创建树类型 1:是树，2：是列表
					if ("01".equals(udipDataLib.getShowType())) {
						CommonTreeNode treeNode = new CommonTreeNode();
						while (rs.next()) {
							treeNode = new CommonTreeNode();
							treeNode.setId(rs.getString(1));
							treeNode.setText(rs.getString(2));
							treeNode.setUpId(rs.getString(3));
							if(treeNode.getUpId() == null){
								treeNode.setUpId("0");
							}
							nodeList.add(treeNode);
						}
					} else { // 以列表形式显示字典数据
						CommonTreeNode treeNode = null;
						while (rs.next()) {
							treeNode = new CommonTreeNode();
							treeNode.setId(rs.getString(1));
							treeNode.setText(rs.getString(2));
							nodeList.add(treeNode);
						}
					}
				} catch (SQLException e) {
					log.error(e);
				} catch (Exception e) {
					log.error(e);
				} finally {
					dataSourceBS.releaseConnection(rs, state, conn);
				}
			} else {
				if(StringUtils.isNotBlank(udipDataLib.getStaticContent())){
					String[] contents = udipDataLib.getStaticContent().split(";;");
					for (String content : contents) {
						String[] con = content.split(":");
						CommonTreeNode treeNode = new CommonTreeNode();// 添加名称字段
						treeNode.setId(con[1]);
						treeNode.setText(con[0]);
						nodeList.add(treeNode);
					}
				}
			}
		}
		return nodeList;
	}

	/**
	 * 通过字典ID字典键值表(代码:名称)
	 * @param libId
	 * @return
	 */
	public Map<String, String> buildLibMapById(String libId) {

		Map<String, String> map = Maps.newHashMap();
		List<RptInputListDataDictInfo> libList = dirBS.getEntityListByProperty(RptInputListDataDictInfo.class, "dictId", libId);
		if (libList != null) {
			RptInputListDataDictInfo dataInputDictInfo = libList.get(0);
			if (UdipConstants.LIB_LIB_TYPE_DIS.equals(dataInputDictInfo.getDictType())) { // 字典类型
				Connection conn = null;
				Statement state = null;
				ResultSet rs = null;
				try {
					conn = dataSourceBS.getConnection(dataInputDictInfo.getDsId());
					state = conn.createStatement();
					// 展示方式是数据树
					if ("1".equals(dataInputDictInfo.getShowType())) {
						StringBuilder sql = new StringBuilder(1000);
						sql.append(dataInputDictInfo.getSqlText());
						rs = state.executeQuery(sql.toString());
						while (rs.next()) {
							map.put(rs.getString(1), rs.getString(2));
						}
					} else {
						StringBuilder sql = new StringBuilder(1000);
						sql.append(dataInputDictInfo.getSqlText());
						rs = state.executeQuery(sql.toString());
						while (rs.next()) {
							map.put(rs.getString(1), rs.getString(2));
						}
					}
				} catch (Exception e) {
					log.error(e);
				} finally {
					dataSourceBS.releaseConnection(rs, state, conn);
				}

			} else {
				if(StringUtils.isNotBlank(dataInputDictInfo.getStaticContent())){
					String[] contents = dataInputDictInfo.getStaticContent().split(";;");
					for (String content : contents) {
						String[] con = content.split(":");
						map.put(con[1], con[0]);
					}
				}
			}
		}
		return map;
	}

	/**
	 * 测试该数据字典输入的条件是否查询通过
	 * @param mode
	 */
	public boolean testDataLib(RptInputListDataDictInfo mode){
		//测试不通过，flag返回false
		boolean flag=true;
		// String message = "";
		Connection conn = null;
		Statement state = null;
		if(StringUtils.isNotBlank(mode.getDsId())){
			try {
				conn = dataSourceBS.getConnection(mode.getDsId());
				if(conn == null){
					flag = false;
				}
				state = conn.createStatement();
				StringBuilder sql = new StringBuilder(1000);
				BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
				String sqlText = mode.getSqlText().replaceAll("@USERORG", "'"+user.getOrgNo()+"'").replaceAll("@USERID", " '"+user.getUserId()+"' ");
				sql.append(sqlText);
				state.executeQuery(sql.toString());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				// message = "SQL编写的不正确，请检查SQL";
				flag = false;
				e.printStackTrace();
			}finally{
					try {
						if(state != null){
							state.close();
						}
						if(conn != null ){
							conn.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
		return flag;
	}
//	
	/**
	 * 通过数据源和表名获取数据字典信息
	 * @param dsId
	 * @param tableName
	 * @return
	 */
	public List<RptInputListDataDictInfo> getUdipDataLibByDsIdAndTableName(String dsId, String tableName) {
		StringBuilder jql = new StringBuilder(1000);
		jql.append("select s from UdipDataLibInfo s where dsId =:dsId ");
		jql.append("and tableName =:tableName ");
		Map<String, String> values = Maps.newHashMap();
		values.put("dsId", dsId);
		values.put("tableName", tableName);
		return this.baseDAO.findWithNameParm(jql.toString(), values);
	}
//	
	public void saveDataLib(RptInputListDataDictInfo mode,List<Map<String, Object>> mapList,boolean isParent,boolean isDate) throws Exception{
		Connection conn = null;
		Statement state = null;
		try{
			conn = dataSourceBS.getConnection(mode.getDsId());
			state = conn.createStatement();
			// StringBuilder sql = new StringBuilder(1000);
//			//如果有上级节点和日期字段
//			if(isParent && isDate){
//				sql.append("insert into ").append(mode.getTableName()).append("(").append(mode.getNameCol()).append(",").append(mode.getCodeCol()).append(",").append(mode.getParentCol()).append(",").append(mode.getDateCol()).append(") ");
//				for (int i = 0; i < mapList.size(); i++) {
//					StringBuilder sql1 = new StringBuilder();
//					Map<String, Object> colMap = mapList.get(i);
//					sql1.append(sql.toString());
//					sql1.append(" values('").append(colMap.get("0")).append("','").append(colMap.get("1")).append("','").append(colMap.get("2")).append("','").append(((String)colMap.get("3")).replaceAll("-", "")).append("')");
//					state.executeUpdate(sql1.toString());
//				}
//			}else if(isParent && !isDate){//如果有上级节点,但没有日期字段
//				sql.append("insert into ").append(mode.getTableName()).append("(").append(mode.getNameCol()).append(",").append(mode.getCodeCol()).append(",").append(mode.getParentCol()).append(") ");
//				for (int i = 0; i < mapList.size(); i++) {
//					StringBuilder sql1 = new StringBuilder();
//					Map<String, Object> colMap = mapList.get(i);
//					sql1.append(sql.toString());
//					sql1.append(" values('").append(colMap.get("0")).append("','").append(colMap.get("1")).append("','").append(colMap.get("2")).append("')");
//					state.executeUpdate(sql1.toString());
//				}
//			}else if(!isParent && isDate){//如果没有上级节点,但有日期字段
//				sql.append("insert into ").append(mode.getTableName()).append("(").append(mode.getNameCol()).append(",").append(mode.getCodeCol()).append(",").append(mode.getDateCol()).append(") ");
//				for (int i = 0; i < mapList.size(); i++) {
//					StringBuilder sql1 = new StringBuilder();
//					Map<String, Object> colMap = mapList.get(i);
//					sql1.append(sql.toString());
//					sql1.append(" values('").append(colMap.get("0")).append("','").append(colMap.get("1")).append("','").append(((String)colMap.get("3")).replaceAll("-", "")).append("')");
//					state.executeUpdate(sql1.toString());
//				}
//			}else{//如果没有上级节点,也没有日期字段
//				sql.append("insert into ").append(mode.getTableName()).append("(").append(mode.getNameCol()).append(",").append(mode.getCodeCol()).append(") ");
//				for (int i = 0; i < mapList.size(); i++) {
//					StringBuilder sql1 = new StringBuilder();
//					Map<String, Object> colMap = mapList.get(i);
//					sql1.append(sql.toString());
//					sql1.append(" values('").append(colMap.get("0")).append("','").append(colMap.get("1")).append("')");
//					state.executeUpdate(sql1.toString());
//				}
//			}
		}catch (Exception e) {
			log.error(e);
			throw e;
		} finally {
			if(state != null){
				state.close();
			}
			if(conn != null){
				conn.close();
			}
		}
	}
//	
	public void removeDataLib(String logicSysNo,String libId){
		this.removeEntityByProperty("dictId", libId);
		//this.authObjBS.removeAuthObjResRel(logicSysNo, GlobalConstants.LOCAL_AUTH_OBJ_DEF_ID_ROLE,LibResImpl.RES_OBJ_DEF_NO, libId);
	}
	
	/**
	 * 删除字典和字典关联资源关系
	 * @param logicSysNo
	 * @param libId
	 */
	@Transactional(readOnly = false)
	public void deleteDictData(String logicSysNo,String[] dictIds){
		Map<String,String> params = Maps.newHashMap();
		for (String dictId : dictIds){
			this.dictDao.deleteDictInfoById(dictId);
			params.put("dictId", dictId);
			params.put("resDefNo",DictResImpl.name);
			this.dictDao.deleteDictResRel(params);
		}
	}
	
	/**
	 * 增加字典画面，点击保存按钮，将字典信息保存到数据库中
	 * @param dictInfo 字典信息
	 */
	@Transactional(readOnly = false)
	public void saveDictInfo(RptInputListDataDictInfo dictInfo){
		dictDao.saveDataInputDict(dictInfo);
	}

	public InputListDataDictInfoVO findUdipDataById(String dictId) {
		String jql = "select dict,catalog.catalogName as catalogName " +
				"from RptInputListDataDictInfo dict,RptInputListCatalogInfo catalog" +
				" where dict.catalogId = catalog.catalogId and dict.dictId = ?0";
		Object[] object = this.baseDAO.findUniqueWithIndexParam(jql, dictId);
		InputListDataDictInfoVO vo = new  InputListDataDictInfoVO((RptInputListDataDictInfo)object[0]);
		if(vo.getDsId() != null){
			BioneDsInfo ds = this.baseDAO.findUniqueWithIndexParam("select ds from BioneDsInfo ds where ds.dsId = ?0", vo.getDsId());
			vo.setDsName(ds.getDsName());
		}
		vo.setCatalogName(object[1].toString());
		return vo;
	}
	
	public List<RptInputListDataDictInfo> findWithId(Collection<String> ids) {
		List<RptInputListDataDictInfo> result = Lists.newArrayList();
		if (ids != null && ids.size() > 0) {
			String jql = "select t from RptInputListDataDictInfo t where t.dictId in ?0";
			result = this.baseDAO.findWithIndexParam(jql, ids);
		}
		return result;
	}
	
	public String getLibId (String catalogId, String dictName) {
		String sql = "select count(1) from rpt_input_list_data_dict_info where catalog_id = ?0 and dict_name = ?1";
		String libIds = String.valueOf(this.baseDAO.findByNativeSQLWithIndexParam(sql, catalogId, dictName).get(0));
		return libIds;
	}
}
