package com.yusys.bione.frame.variable.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.authobj.service.RoleExtBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.frame.variable.web.vo.BioneParamInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Service("paramBS")
@Transactional(readOnly = true)
public class ParamBS extends BaseBS<BioneParamInfo> {
	@Autowired
	private RoleExtBS roleExtBS;
	
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
	public List<BioneParamInfo> getParamList(int firstResult, int pageSize,
			String orderBy, String orderType, Map<String, Object> conditionMap,
			String paramTypeNo) {
		StringBuilder jql = new StringBuilder("");
		jql.append("select param from BioneParamInfo param where 1 = 1");
		if (paramTypeNo != null || "".equals(paramTypeNo)) {
			jql.append(" and " + "param.paramTypeNo='" + paramTypeNo + "'");
		}
		jql.append(" and "
				+ "param.logicSysNo='"
				+ BioneSecurityUtils.getCurrentUserInfo()
						.getCurrentLogicSysNo() + "'");
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by param." + orderBy + " " + orderType);
		}
		Map<String, Object> values = Maps.newHashMap();
		List<BioneParamInfo> paramList = this.baseDAO.findWithNameParm(
				jql.toString(), values);

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
		String jql = "select param from BioneParamInfo param where param.paramTypeNo=?0 and param.logicSysNo=?1";
		return this.baseDAO.findWithIndexParam(jql, paramTypeNo,
				BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
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
		List<BioneParamInfo> paramList = null;
		String jql = "SELECT param FROM BioneParamInfo param where param.logicSysNo=?0 and param.paramTypeNo=?1";
		paramList = this.baseDAO.findWithIndexParam(jql, logicSysNo,
				paramTypeNo);
		return paramList;
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
	@SuppressWarnings("unchecked")
	public Map<String, Object> searchParamsAsTree(Pager pager, String logicSysNo, String paramTypeNo) {
		StringBuilder jql = new StringBuilder(
				"select paramInfo from BioneParamInfo paramInfo where paramInfo.logicSysNo='"
						+ logicSysNo + "'");
		if (paramTypeNo != null && !"".equals(paramTypeNo)) {
			jql.append(" and paramInfo.paramTypeNo='" + paramTypeNo + "'");
		}

		Map<String, Object> conditionMap = pager.getSearchCondition();
		Map<String, Object> values = new HashMap<>();
		if(!conditionMap.get("jql").equals("")){
			jql.append(" and " + conditionMap.get("jql"));
			values = (Map<String, Object>) conditionMap.get("params");
		}
		if(StringUtils.isNotBlank(pager.getSortname())){
			jql.append(" order by paramInfo." + pager.getSortname() + " " + pager.getSortorder());
		}
		SearchResult<BioneParamInfo> sr = this.baseDAO.findPageWithNameParam(pager.getPageFirstIndex(), pager.getPagesize(), jql.toString(), values);
		List<BioneParamInfo> params = sr.getResult();
		return this.buildTree(params, sr.getTotalCount());
	}

	/**
	 * 根据upNo构建树形结构
	 * 
	 * @param params
	 *            待构建节点集合
	 * @return
	 */
	private Map<String, Object> buildTree(List<BioneParamInfo> params, Object totalCount) {
		int count = (Integer) totalCount;
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
		//修改为分页逻辑后，会有部分游离的子节点，需要处理一下
		if((roots.size() == 0) && (noRoots.size() != 0)) {
			for (int i = 0; i < noRoots.size(); i++) {
				BioneParamInfo noRoot = noRoots.get(i);
				BioneParamInfoVO vo = new BioneParamInfoVO();
				vo.setParamId(noRoot.getParamId());
				vo.setLogicSysNo(noRoot.getLogicSysNo());
				vo.setOrderNo(noRoot.getOrderNo());
				vo.setParamName(noRoot.getParamName());
				vo.setParamTypeNo(noRoot.getParamTypeNo());
				vo.setParamValue(noRoot.getParamValue());
				vo.setRemark(noRoot.getRemark());
				vo.setUpNo(noRoot.getUpNo());
				vos.add(vo);
			}
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
		String jql = "select param from BioneParamInfo param where param.logicSysNo=?0 and param.upNo!='0'";
		return this.baseDAO.findWithIndexParam(jql, logicSysNo);
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
			String jql = "select p from BioneParamInfo p where p.logicSysNo=?0 and p.paramId in (?1)";
			List<BioneParamInfo> fatherList = this.baseDAO.findWithIndexParam(
					jql, BioneSecurityUtils.getCurrentUserInfo()
							.getCurrentLogicSysNo(), upNoList);
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
			BioneParamInfo param = this.getEntityById(id);
			if(GlobalConstants4frame.TREE_PARAM_TYPE.equals(param.getParamTypeNo())) {//如果是监管机构，同步删除平台角色
				this.roleExtBS.removeEntityById(param.getParamValue());
			}
			this.removeEntityById(id);
		}
	}

	/**
	 * 获取指定参数类型下的参数树形结构
	 */
	public List<CommonTreeNode> getParamsTreeByType(String paramType, String ctx) {
		if (StringUtils.isEmpty(ctx)) {
			ctx = com.yusys.bione.frame.base.common.GlobalConstants4frame.APP_CONTEXT_PATH;
		}
		String iconPath = ctx + "/images/classics/icons/house.png";
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		if (!StringUtils.isEmpty(paramType)) {
			String jql = "select param from BioneParamInfo param where param.paramTypeNo=?0 and param.logicSysNo=?1 order by param.orderNo asc";
			List<BioneParamInfo> params = this.baseDAO.findWithIndexParam(jql,
					paramType, BioneSecurityUtils.getCurrentUserInfo()
							.getCurrentLogicSysNo());
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

	/**
	 * 通过逻辑系统标识和参数类型标识,参数值获得参数list
	 *
	 * @param logicSysNo 逻辑系统编号
	 * @param paramTypeNo 参数类型标识
	 * @param paramValue 参数值
	 * @return paramTypeList 参数类型列表
	 */
	public List<BioneParamInfo> findParam(String logicSysNo, String paramTypeNo, String paramValue) {
		String jql = "SELECT param FROM BioneParamInfo param where param.logicSysNo=?0 and param.paramTypeNo=?1 and param.paramValue=?2";
		List<BioneParamInfo> paramList = this.baseDAO.findWithIndexParam(jql, logicSysNo,
				paramTypeNo,paramValue);
		return paramList;
	}
}
