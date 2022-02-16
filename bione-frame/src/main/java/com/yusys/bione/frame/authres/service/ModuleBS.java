package com.yusys.bione.frame.authres.service;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.authres.entity.BioneFuncInfo;
import com.yusys.bione.frame.authres.entity.BioneMenuTemplateInfo;
import com.yusys.bione.frame.authres.entity.BioneModuleInfo;
import com.yusys.bione.frame.authres.repository.MenuTemplateDao;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional(readOnly = true)
public class ModuleBS extends BaseBS<BioneModuleInfo> {

	@Autowired
	private MenuTemplateDao menuTemplateDao;
	
	/**
	 * 分页显示所有模块信息
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @return
	 */
	public SearchResult<BioneModuleInfo> getModuleList(int firstResult, int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap) {
		StringBuilder jql = new StringBuilder("");
		jql.append("select module from BioneModuleInfo module where 1=1");
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by module." + orderBy + " " + orderType);
		}
		@SuppressWarnings("unchecked")
		Map<String, ?> values = (Map<String, ?>) conditionMap.get("params");
		SearchResult<BioneModuleInfo> moduleList = this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), values);
		return moduleList;
	}
	
	/**
	 * 查找当前模块下的功能是否正在使用
	 * @param moduleId
	 * 			模块ID
	 * @return
	 */
	public String findUsedModuleName(String moduleId) {
		String jql = new String("select module.moduleName from BioneModuleInfo module where module.moduleId in ( select func.moduleId from BioneFuncInfo func"
				+ " where func.moduleId = :moduleId and func.funcId in ( select menu.funcId from BioneMenuInfo menu ) )");
		Map<String, String> values = Maps.newHashMap();
		values.put("moduleId", moduleId);
		List<Object> objList = this.baseDAO.findWithNameParm(jql, values);
		if(objList != null && objList.size() != 0) {
			return objList.get(0).toString();
		}
		return null;
	}
	
	@Transactional(readOnly = false)	
	public void removeModuleById(String moduleId) {
		String jql = new String("delete from BioneFuncInfo func where func.moduleId = :moduleId");
		Map<String, String> values = Maps.newHashMap();
		values.put("moduleId", moduleId);
		this.baseDAO.batchExecuteWithNameParam(jql, values);
//		this.baseDAO.removeById(moduleId);
		this.removeEntityById(moduleId);
	}

	/**
	 * @方法描述: 新建报送模板
	 * @创建人: huzq1 
	 * @创建时间: 2021/7/6 17:14
	 * @return
	 **/
	@Transactional(readOnly = true)
	public Map<String, Object> copyModule(String  orgType, String funcType, String moduleName, String moduleNo, String remark) {
		Map<String, Object> result = new HashMap<>();
		try {
			String moduleId = RandomUtils.uuid2();
			//添加模块表
			BioneModuleInfo bioneModuleInfo = new BioneModuleInfo();
			bioneModuleInfo.setModuleId(moduleId);
			bioneModuleInfo.setModuleName(moduleName);
			bioneModuleInfo.setModuleNo(moduleNo);
			bioneModuleInfo.setRemark(remark);
			menuTemplateDao.insertModule(bioneModuleInfo);

			//生成根目录
			String funcId = RandomUtils.uuid2();
			BioneFuncInfo bioneFuncInfo = new BioneFuncInfo();
			bioneFuncInfo.setFuncId(funcId);
			bioneFuncInfo.setFuncName(bioneModuleInfo.getModuleName());
			bioneFuncInfo.setModuleId(bioneModuleInfo.getModuleId());
			bioneFuncInfo.setFuncSts("1");
			bioneFuncInfo.setUpId("0");
			bioneFuncInfo.setNavIcon("icon-menu3");
			bioneFuncInfo.setOrderNo(new BigDecimal(1));
			bioneFuncInfo.setRemark(bioneModuleInfo.getRemark());

			//生成下级目录
			List<BioneFuncInfo> list = new ArrayList<>();
			copyFunc(list, moduleId, funcType, funcId,"0", orgType);
			list.add(bioneFuncInfo);
			menuTemplateDao.batchInsertFunc(list);
			result.put("status","success");
			result.put("msg","保存成功");
		} catch (Exception e){
			e.printStackTrace();
			result.put("status","fail");
			result.put("msg",e.getMessage());
		}
		return result;
	}

	private void copyFunc(List<BioneFuncInfo> list, String moduleId,String funcType,
							String upFuncId,String upId, String orgType){
		List<BioneMenuTemplateInfo> menuTemplateList = menuTemplateDao.getMenuTemplateList(funcType, upId);
		for (BioneMenuTemplateInfo bioneMenuTemplateInfo : menuTemplateList) {
			String funcId = RandomUtils.uuid2();
			BioneFuncInfo bioneFuncInfo = new BioneFuncInfo();
			bioneFuncInfo.setFuncId(funcId);
			bioneFuncInfo.setFuncName(bioneMenuTemplateInfo.getFuncName());
			bioneFuncInfo.setModuleId(moduleId);
			bioneFuncInfo.setFuncSts(bioneMenuTemplateInfo.getFuncSts());
			bioneFuncInfo.setUpId(upFuncId);
			bioneFuncInfo.setNavIcon(bioneMenuTemplateInfo.getNavIcon());
			bioneFuncInfo.setOrderNo(new BigDecimal(bioneMenuTemplateInfo.getOrderNo()));
			bioneFuncInfo.setRemark(bioneMenuTemplateInfo.getRemark());
			if(StringUtils.isNotBlank(bioneMenuTemplateInfo.getNavPath())){
				bioneFuncInfo.setNavPath(bioneMenuTemplateInfo.getNavPath().replace("【orgType】", orgType));
			}
			list.add(bioneFuncInfo);
			copyFunc(list,moduleId,funcType,funcId,bioneMenuTemplateInfo.getFuncId(),orgType);
		}
	}

	public List<CommonComboBoxNode> getOrgType() {
		Map<String, Object> map = new HashMap<>();
		map.put("logicSysNo", "FRS");
		map.put("paramTypeNo", "reportorgtype");
		List<BioneParamInfo> list = menuTemplateDao.getOrgType(map);

		List<CommonComboBoxNode> result = new ArrayList<>();
		for (BioneParamInfo bioneParamInfo : list) {
			CommonComboBoxNode node = new CommonComboBoxNode();
			node.setId(bioneParamInfo.getParamValue());
			node.setText(bioneParamInfo.getParamName());
			result.add(node);
		}
		return result;
	}
}
