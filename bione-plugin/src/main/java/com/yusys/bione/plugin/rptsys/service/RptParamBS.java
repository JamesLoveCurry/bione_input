package com.yusys.bione.plugin.rptsys.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.frame.variable.web.vo.BioneParamInfoVO;
import com.yusys.bione.plugin.rptsys.repository.RptParamMybatisDao;

/**
 * <pre>
 * Title:参数的业务逻辑类
 * Description: 提供参数管理相关业务逻辑处理功能，提供事务控制
 * </pre>
 * 
 * @author yangyuhui yangyh4@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptParamBS  {
	@Autowired
	private RptParamMybatisDao paramDao;
	/**
	 * 根据条件获取参数信息
	 * 
	 * @param firstResult
	 *            第一条记录
	 * @param pageSize
	 *            每页记录数
	 * @param orderBy
	 *            排序字段
	 * @param orderType
	 *            排序方式
	 * @param conditionMap
	 *            查询参数列表
	 * @param paramTypeNo
	 *            参数类型标识
	 * @return
	 */
	public List<BioneParamInfo> getParamList(Pager pager,
			String paramTypeNo) {
		PageHelper.startPage(pager);
		Map<String, Object> values = Maps.newHashMap();
		if(paramTypeNo!=null&&!paramTypeNo.equals(""))
			values.put("paramTypeNo", paramTypeNo);
		List<BioneParamInfo> paramList = this.paramDao.paramInfolist(values);

		/*
		 * Map<String, BioneParamInfo> resultMap = Maps.newHashMap(); for
		 * (BioneParamInfo param : paramList) {
		 * resultMap.put(param.getParamId()+ "_" +param.getParamTypeNo(),
		 * param); }
		 */
		return paramList;
	}

	/**
	 * 通过参数类型标识查询参数
	 * 
	 * @param paramTypeNo
	 * @return
	 */
	public List<BioneParamInfo> findParamByParamTypeNo(String paramTypeNo) {
		Map<String, Object> values = Maps.newHashMap();
		values.put("paramTypeNo", paramTypeNo);
		//values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		return this.paramDao.paramInfolist(values);
	}

	public BioneParamInfo getParamById(String paramId){
		Map<String, Object> values = Maps.newHashMap();
		values.put("paramId", paramId);
		List<BioneParamInfo> lists=this.paramDao.paramInfolist(values);
		if(lists!=null&&lists.size()>0)
			return lists.get(0);
		return null;
	}
	
	public BioneParamInfo getParamByParam(Map<String,Object> params){
		List<BioneParamInfo> lists=this.paramDao.paramInfolist(params);
		if(lists!=null&&lists.size()>0)
			return lists.get(0);
		return null;
	}
	/**
	 * 通过逻辑系统标识和参数类型标识获得参数list
	 * 
	 * @param logicSysNo
	 *            逻辑系统编号
	 * @param paramTypeNo
	 *            参数类型标识
	 * @return paramTypeList 参数类型列表
	 */
	public List<BioneParamInfo> findParamBySysAndType(String logicSysNo,
			String paramTypeNo) {
		Map<String, Object> values = Maps.newHashMap();
		values.put("paramTypeNo", paramTypeNo);
		//values.put("logicSysNo", logicSysNo);
		return this.paramDao.paramInfolist(values);
	}

	/**
	 * 查询逻辑系统下所有顶层参数
	 * 
	 * @param logicSysNo
	 *            逻辑系统编号
	 * @param paramTypeNo
	 *            参数类型标识
	 * @return paramTypeList 参数类型列表
	 */
	public Map<String, Object> searchParamsAsTree(
			Pager pager, String logicSysNo,
			String paramTypeNo) {
		List<BioneParamInfo> params = null;
		Map<String, Object> values = Maps.newHashMap();
		values.put("paramTypeNo", paramTypeNo);
		//values.put("logicSysNo", logicSysNo);
		if (!pager.getSearchCondition().get("jql").equals("")) {// 有查询条件时，从搜索结果向根节点查询路径上所有节点
			PageHelper.startPage(pager);
			params = this.paramDao.paramInfolist(values);
			this.getParamsToRoot(params);
		} else {// 无查询条件时搜索所有节点
			params =  this.paramDao.paramInfolist(values);
		}
		return this.buildTree(params);
	}

	/**
	 * 根据upNo构建树形结构
	 * 
	 * @param params
	 *            待构建节点集合
	 * @return
	 */
	private Map<String, Object> buildTree(List<BioneParamInfo> params) {
		int count = params.size();
		if (params == null || params.size() == 0)
			return null;
		// 筛选根节点
		List<BioneParamInfo> roots = new ArrayList<BioneParamInfo>();
		List<BioneParamInfo> noRoots = new ArrayList<BioneParamInfo>();
		for (int i = 0; i < params.size(); i++) {
			BioneParamInfo param = params.get(i);
			if ("0".equals(param.getUpNo()) || param.getUpNo() == null) {
				roots.add(param);
			} else {
				noRoots.add(param);
			}
		}
		// 迭代构建
		List<BioneParamInfoVO> vos = Lists.newArrayList();
		for (int i = 0; i < roots.size(); i++) {
			BioneParamInfo root = roots.get(i);
			BioneParamInfoVO vo = new BioneParamInfoVO();
			vo.setParamId(root.getParamId());
			vo.setLogicSysNo(root.getLogicSysNo());
			vo.setOrderNo(root.getOrderNo());
			vo.setParamName(root.getParamName());
			vo.setParamTypeNo(root.getParamTypeNo());
			vo.setParamValue(root.getParamValue());
			vo.setRemark(root.getRemark());
			vo.setUpNo(root.getUpNo());
			generateVoList(noRoots, vo);
			vos.add(vo);
		}

		Map<String, Object> map = Maps.newHashMap();
		map.put("Rows", vos);
		map.put("Total", count);
		return map;
	}

	/**
	 * 查询逻辑系统下所有参数
	 * 
	 * @param logicSysNo
	 *            逻辑系统编号
	 * @param paramTypeNo
	 *            参数类型标识
	 * @return paramTypeList 参数类型列表
	 */
	public List<BioneParamInfo> findAllParams(String logicSysNo) {
		Map<String, Object> map = Maps.newHashMap();
		//map.put("logicSysNo", logicSysNo);
		map.put("noUpNo", "0");
		return this.paramDao.paramInfolist(map);
	}

	/**
	 * 迭代找到所有父节点，直到根节点
	 * 
	 * @param resOperVOList
	 */
	public void getParamsToRoot(List<BioneParamInfo> paramList) {
		if (paramList == null || paramList.size() == 0)
			return;

		Map<String, BioneParamInfo> map = new HashMap<String, BioneParamInfo>();
		for (int i = 0; i < paramList.size(); i++) {
			map.put(paramList.get(i).getParamId(), paramList.get(i));
		}
		List<String> upNoList = new ArrayList<String>();
		String upNo = null;
		for (String paramId : map.keySet()) {
			upNo = map.get(paramId).getUpNo();
			while (!"0".equals(upNo)) {
				if (map.get(upNo) == null) {
					upNoList.add(upNo);
					break;
				} else {
					upNo = map.get(upNo).getUpNo();
				}
			}
		}
		if (upNoList != null && upNoList.size() > 0) {
			Map<String, Object> values = Maps.newHashMap();
			//values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
			//		.getCurrentLogicSysNo());
			values.put("paramIds",upNoList);
			List<BioneParamInfo> fatherList =this.paramDao.paramInfolist(values);
			if (fatherList != null && fatherList.size() > 0) {
				paramList.addAll(fatherList);
				this.getParamsToRoot(paramList);
			}
		}
	}

	private void generateVoList(List<BioneParamInfo> paramInfoList,
			BioneParamInfoVO vo) {
		for (BioneParamInfo param : paramInfoList) {
			if (param.getUpNo().equals(vo.getParamId())) {
				BioneParamInfoVO infoVO = new BioneParamInfoVO();
				infoVO.setParamId(param.getParamId());
				infoVO.setLogicSysNo(param.getLogicSysNo());
				infoVO.setOrderNo(param.getOrderNo());
				infoVO.setParamName(param.getParamName());
				infoVO.setParamTypeNo(param.getParamTypeNo());
				infoVO.setParamValue(param.getParamValue());
				infoVO.setRemark(param.getRemark());
				infoVO.setUpNo(param.getUpNo());
				vo.getChildren().add(infoVO);
				generateVoList(paramInfoList, infoVO);
			}
		}
	}

	// 删除参数
	@Transactional(readOnly = false)
	public void removeEntityBatch(String ids) {
		if (ids.endsWith(",")) {
			ids = ids.substring(0, ids.length() - 1);
		}
		String[] idArray = StringUtils.split(ids, ',');
		for (String id : idArray) {
			Map<String,Object> map=new HashMap<String, Object>();
			map.put("paramId", id);
			this.paramDao.paramInfodelete(map);
		}
	}
	@Transactional(readOnly = false)
	public void saveParam(BioneParamInfo info) {
		this.paramDao.paramInfosave(info);
	}
	
	@Transactional(readOnly = false)
	public void updateParam(BioneParamInfo info) {
		this.paramDao.paramInfoupdate(info);
	}


	/**
	 * 获取指定参数类型下的参数树形结构
	 */
	public List<CommonTreeNode> getParamsTreeByType(String paramType, String ctx) {
		if (StringUtils.isEmpty(ctx)) {
			ctx = GlobalConstants4frame.APP_CONTEXT_PATH;
		}
		String iconPath = ctx + "/images/classics/icons/house.png";
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		if (!StringUtils.isEmpty(paramType)) {
			Map<String, Object> values = Maps.newHashMap();
//			values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
//					.getCurrentLogicSysNo());
			values.put("paramType", paramType);
			List<BioneParamInfo> params = this.paramDao.paramInfoOrderlist(values);
			if (params != null) {
				for (int i = 0; i < params.size(); i++) {
					BioneParamInfo pTmp = params.get(i);
					CommonTreeNode nTmp = new CommonTreeNode();
					nTmp.setId(pTmp.getParamId());
					nTmp.setText(pTmp.getParamName());
					nTmp.setUpId(pTmp.getUpNo());
					Map<String, String> pMap = new HashMap<String, String>();
					pMap.put("paramNo", pTmp.getParamValue());
					nTmp.setParams(pMap);
					nTmp.setIcon(iconPath);

					nodes.add(nTmp);
				}
			}
		}
		return nodes;
	}
}
