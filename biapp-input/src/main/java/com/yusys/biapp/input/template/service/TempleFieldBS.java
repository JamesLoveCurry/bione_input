package com.yusys.biapp.input.template.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.template.entity.RptInputLstTempleField;
import com.yusys.biapp.input.template.vo.TemplateRewriteColumnVO;
import com.yusys.biapp.input.template.web.vo.TemplateRewriteTableVO;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = false)
public class TempleFieldBS extends BaseBS<RptInputLstTempleField> {
	public List<RptInputLstTempleField> getTempleColumns(String tempId) {
		StringBuilder jql = new StringBuilder("");
		jql.append("select t from RptInputLstTempleField t where 1=1");
		if (!StringUtils.isEmpty(tempId)) {
			jql.append(" and templeId =?0 ");
		}
		jql.append(" order by t.orderNo ");

		List<RptInputLstTempleField> authObjDefList = this.baseDAO
				.findWithIndexParam(jql.toString(), tempId);

		return authObjDefList;
	}

	public List<RptInputLstTempleField> getAllTempleColumns(List<RptInputLstTempleField> authObjDefList) {
		RptInputLstTempleField sysDataCase = new RptInputLstTempleField();
		sysDataCase.setFieldType("VARCHAR2");
		sysDataCase.setFieldCnName("任务实例ID");
		sysDataCase.setFieldEnName("SYS_DATA_CASE");
		sysDataCase.setOrderNo(authObjDefList.size()+1);
		authObjDefList.add(sysDataCase);
		RptInputLstTempleField flowNodeId = new RptInputLstTempleField();
		flowNodeId.setFieldType("VARCHAR2");
		flowNodeId.setFieldCnName("流程节点ID");
		flowNodeId.setFieldEnName("FLOW_NODE_ID");
		flowNodeId.setOrderNo(authObjDefList.size()+1);
		authObjDefList.add(flowNodeId);
		RptInputLstTempleField errorMark = new RptInputLstTempleField();
		errorMark.setFieldType("VARCHAR2");
		errorMark.setFieldCnName("错误标记");
		errorMark.setFieldEnName("ERROR_MARK");
		errorMark.setOrderNo(authObjDefList.size()+1);
		authObjDefList.add(errorMark);
		RptInputLstTempleField comments = new RptInputLstTempleField();
		comments.setFieldType("VARCHAR2");
		comments.setFieldCnName("备注");
		comments.setFieldEnName("COMMENTS");
		comments.setOrderNo(authObjDefList.size()+1);
		authObjDefList.add(comments);
		RptInputLstTempleField dataInputId = new RptInputLstTempleField();
		dataInputId.setFieldType("VARCHAR2");
		dataInputId.setFieldCnName("主键");
		dataInputId.setFieldEnName("DATAINPUT_ID");
		dataInputId.setOrderNo(authObjDefList.size()+1);
		authObjDefList.add(dataInputId);
		RptInputLstTempleField sysDataDate = new RptInputLstTempleField();
		sysDataDate.setFieldType("VARCHAR2");
		sysDataDate.setFieldCnName("数据日期");
		sysDataDate.setFieldEnName("SYS_DATA_DATE");
		sysDataDate.setOrderNo(authObjDefList.size()+1);
		authObjDefList.add(sysDataDate);
		RptInputLstTempleField sysOperOrg = new RptInputLstTempleField();
		sysOperOrg.setFieldType("VARCHAR2");
		sysOperOrg.setFieldCnName("补录机构");
		sysOperOrg.setFieldEnName("SYS_OPER_ORG");
		sysOperOrg.setOrderNo(authObjDefList.size()+1);
		authObjDefList.add(sysOperOrg);
		RptInputLstTempleField sysOperator = new RptInputLstTempleField();
		sysOperator.setFieldType("VARCHAR2");
		sysOperator.setFieldCnName("补录人员");
		sysOperator.setFieldEnName("SYS_OPERATOR");
		sysOperator.setOrderNo(authObjDefList.size()+1);
		authObjDefList.add(sysOperator);
		RptInputLstTempleField sysDataState = new RptInputLstTempleField();
		sysDataState.setFieldType("VARCHAR2");
		sysDataState.setFieldCnName("数据状态");
		sysDataState.setFieldEnName("SYS_DATA_STATE");
		sysDataState.setOrderNo(authObjDefList.size()+1);
		authObjDefList.add(sysDataState);
		return authObjDefList;
	}
	public List<String> findUniqueListBytempleId(String tempId) {
		StringBuilder jql = new StringBuilder("");
		jql.append("select CONS.keyColumn  from RptInputLstTempleInfo INF,")
				.append(" RptInputListTableConstraint CONS, RptInputListTableInfo TBL ")
				.append(" WHERE CONS.tableId = TBL.tableId AND INF.tableEnName = TBL.tableEnName ")
				.append(" AND INF.templeId =?0 ");

		List<String> keyList = this.baseDAO
				.findWithIndexParam(jql.toString(), tempId);
		return keyList;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getDictMap() {
		Map<String, String> resMap = Maps.newHashMap();
		String sql =  "SELECT distinct a.dict_id ,b.dict_name FROM Rpt_Input_Lst_Temple_Field a" +
			" inner join Rpt_Input_List_Data_Dict_Info b" + " on a.dict_id = b.dict_id";
		List<Object[]> l = (List<Object[]>)this.baseDAO.createNativeQueryWithIndexParam(sql).getResultList();
		for(Object[] m : l){
			resMap.put(String.valueOf(m[0]), String.valueOf(m[1]));
		}
		return resMap;
	}
	

	@Transactional(readOnly=false)
	public void saveReWrite(TemplateRewriteTableVO templateRewriteTableVO){

		StringBuilder sqlBuff = new StringBuilder();
		String dsId = templateRewriteTableVO.getDsId();
		String tableNm = templateRewriteTableVO.getTableName();
		String updType = templateRewriteTableVO.getUpdateType();
		String templeId = templateRewriteTableVO.getTempleId();
		//查询是否有记录
		sqlBuff.append("SELECT 1 FROM RPT_INPUT_REWRITE_TEMPLE_INFO WHERE DS_ID= ? AND TEMPLE_ID = ?");
		String[] param1 = new String[]{dsId,templeId};
		List<Map<String, Object>> rs = this.jdbcBaseDAO.find(sqlBuff.toString(), param1);
		//保存指标补录模板回写表
		if(rs == null||rs.isEmpty())
		{
			//新增
			sqlBuff = new StringBuilder();
			sqlBuff.append(" INSERT INTO RPT_INPUT_REWRITE_TEMPLE_INFO(DS_ID,TEMPLE_ID,TABLE_NAME,UPDATE_TYPE,AUTO_REWRITE)  ")
				.append("VALUES (?,?,?,?,?)");
			param1 = new String[]{dsId,templeId,tableNm,updType,templateRewriteTableVO.getAutoRewrite()};
			this.jdbcBaseDAO.update(sqlBuff.toString(),param1);
		}else{
			//更新
			sqlBuff = new StringBuilder();
			sqlBuff.append(" UPDATE RPT_INPUT_REWRITE_TEMPLE_INFO SET TABLE_NAME =?,UPDATE_TYPE=?,AUTO_REWRITE=? WHERE DS_ID=? AND TEMPLE_ID=? ");
			param1 = new String[]{tableNm,updType,templateRewriteTableVO.getAutoRewrite(),dsId,templeId};
			this.jdbcBaseDAO.update(sqlBuff.toString(),param1);
		}
		

		//删除记录再重新插入
		sqlBuff = new StringBuilder();
		sqlBuff.append(" DELETE FROM RPT_INPUT_REWRITE_FIELD_INFO WHERE TEMPLE_ID = ? ");
		param1 = new String[]{templeId};
		this.jdbcBaseDAO.update(sqlBuff.toString(),param1);
		
		//保存指标补录回写字段表
		sqlBuff = new StringBuilder();
		sqlBuff.append(" INSERT INTO RPT_INPUT_REWRITE_FIELD_INFO(FIELD_ID,DS_ID,TEMPLE_ID,FIELD_NAME,UPDATE_FIELD_NAME,IS_ID,FIELD_TYPE,UPDATE_FIELD_TYPE ) ")
		.append("VALUES (?,?,?,?,?,?,?,?)");
		List<Object[]>paramList = Lists.newArrayList();
		for(TemplateRewriteColumnVO columnVO : templateRewriteTableVO.getColumnList()){
			
			String[]param2 = new String[]{RandomUtils.uuid2(),dsId,templeId,
						columnVO.getFieldEnName(),
						columnVO.getUpdFieldName(),
						columnVO.getIsprimary(),
						columnVO.getFieldType(),
						columnVO.getUpdFieldType()};
			paramList.add(param2);
		}
		this.jdbcBaseDAO.batchUpdate(sqlBuff.toString(),paramList);
		

//		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
//		StringBuilder buff = new StringBuilder();
//		buff.append("用户[").append(user.getLoginName()).append("]设置了模板:").append(templateRewriteTableVO.getTempleNm()).append("的回写数据");
//		saveLog("03", "明细补录模板", buff.toString(), user.getUserId(), user.getLoginName());
	}
}
