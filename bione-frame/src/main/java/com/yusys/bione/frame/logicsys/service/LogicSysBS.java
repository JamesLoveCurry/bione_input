package com.yusys.bione.frame.logicsys.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.CollectionsUtils;
import com.yusys.bione.frame.auth.entity.BioneAuthObjDef;
import com.yusys.bione.frame.auth.entity.BioneAuthResDef;
import com.yusys.bione.frame.authres.entity.BioneFuncInfo;
import com.yusys.bione.frame.authres.entity.BioneMenuInfo;
import com.yusys.bione.frame.authres.entity.BioneModuleInfo;
import com.yusys.bione.frame.authres.util.ComparatorMenu;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.logicsys.entity.BioneAdminUserInfo;
import com.yusys.bione.frame.logicsys.entity.BioneLogicSysInfo;
import com.yusys.bione.frame.logicsys.util.ComparatorFunc;
import com.yusys.bione.frame.schedule.entity.BioneTaskInfo;
import com.yusys.bione.frame.schedule.service.TaskBS;
import com.yusys.bione.frame.user.entity.BioneUserInfo;

/**
 * <pre>
 * Title:逻辑系统管理类
 * Description: 提供逻辑系统管理相关业务逻辑处理功能，提供事务控制
 * </pre>
 * 
 * @author songxf songxf@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class LogicSysBS extends BaseBS<BioneLogicSysInfo> {

	protected static Logger log = LoggerFactory.getLogger(LogicSysBS.class);
	@Autowired
	private TaskBS taskBS;
	/* @Revision 20130403105200-liuch */
	private String basePath = GlobalConstants4frame.APP_CONTEXT_PATH;

	/* @Revision 20130403105200-liuch END */

	/**
	 * 获得现有逻辑系统列表
	 * 
	 * @return list
	 */
	public List<BioneLogicSysInfo> findLogicSysInfo() {
		String jql = "select logicSysInfo from BioneLogicSysInfo logicSysInfo where logicSysInfo.logicSysSts=?0 order by logicSysInfo.orderNo asc";
		List<BioneLogicSysInfo> logicSysInfoList = this.baseDAO.findWithIndexParam(jql,
				GlobalConstants4frame.COMMON_STATUS_VALID);
		return logicSysInfoList;
	}

	/**
	 * 查询逻辑系统
	 * 
	 * @param currentPage
	 *            当前页数
	 * @param maxResult
	 *            每一页最大数目
	 * @param values
	 *            查询条件
	 * @return map 查询结果集
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<BioneLogicSysInfo> findResults(int firstResult, int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap) throws Exception {

		StringBuilder jql = new StringBuilder("");
		// jql.append("select obj from BioneLogicSysInfo obj where obj.isBuiltin <>1 ");
		jql.append("select obj from BioneLogicSysInfo obj where 1=1 ");
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by obj." + orderBy + " " + orderType);
		}
		Map<String, ?> values = (Map<String, ?>) conditionMap.get("params");

		return this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), values);
	}

	/**
	 * 批量删除
	 * 
	 * @param ids
	 *            记录ID号
	 */
	@Transactional(readOnly = false)
	public void deleteBatch(String[] ids) {
		String jql = "delete from BioneLogicSysInfo where logicSysId in (?0)";
		this.baseDAO.batchExecuteWithIndexParam(jql, Arrays.asList(ids));
	}

	/**
	 * 获取最大序号
	 * 
	 * @return
	 */
	public Long getMaxOrder() {
		String jql = "select max(logicSysInfo.orderNo) from BioneLogicSysInfo logicSysInfo";
		List<Long> maxList = this.baseDAO.findWithIndexParam(jql);
		Long m = 0l;
		if (maxList.size() == 1) {
			m = maxList.get(0) + 1;
		}
		return m;
	}

	/**
	 * 加载认证方式
	 * 
	 * @return
	 */
	public <X> List<X> findAuthType() {
		String jql = "from BioneAuthInfo authInfo where 1=?0";
		List<X> list = this.baseDAO.findWithIndexParam(jql, 1);
		return list;
	}

	/**
	 * 获取所有的用户信息
	 * 
	 * @param id
	 * @param params
	 * @return
	 */
	public List<BioneUserInfo> getUserList(String id, Map<String, String> params) {
		StringBuilder jpa = new StringBuilder("select obj from BioneUserInfo obj where 1=?0");
		String userName = params.get("userName");
		if (!"".equals(userName) && userName != null) {
			jpa.append(" and obj.userName like '%");
			jpa.append(userName);
			jpa.append("%'");
		}
		List<BioneUserInfo> list = this.baseDAO.findWithIndexParam(jpa.toString(), 1);
		List<BioneUserInfo> adminList = getAdminList(id);
		list.removeAll(adminList);
		return list;
	}

	/**
	 * 用户信息映射匹配Tree的响应数据
	 * 
	 * @param userList
	 * @return
	 */
	public List<CommonTreeNode> userToTree(List<BioneUserInfo> userList) {
		List<CommonTreeNode> list = new ArrayList<CommonTreeNode>();
		for (BioneUserInfo userInfo : userList) {
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(userInfo.getUserId());
			treeNode.setText(userInfo.getUserName());
			treeNode.setUpId(CommonTreeNode.ROOT_ID);
			treeNode.setIcon(basePath + GlobalConstants4frame.LOGIC_ADMIN_ICON);
			list.add(treeNode);
		}
		return list;
	}

	/**
	 * 根据逻辑系统ID获取逻辑系统授权信息
	 * 
	 * @return
	 */
	public List<BioneUserInfo> getAdminList(String id) {

		List<BioneAdminUserInfo> adminList = getEntityListByProperty(BioneAdminUserInfo.class, "id.logicSysId", id);
		List<BioneUserInfo> userList = new ArrayList<BioneUserInfo>();
		for (BioneAdminUserInfo adminUser : adminList) {
			userList.add((BioneUserInfo) getEntityById(BioneUserInfo.class, adminUser.getId().getUserId()));
		}
		return userList;
	}

	/**
	 * 获得功能树 不含主页
	 * 
	 * @return
	 */
	public List<CommonTreeNode> funcToTree(List<BioneFuncInfo> funcList) {
		return this.funcToTree(funcList, false,null);
	}

	public CommonTreeNode getMenuRoot() {
		CommonTreeNode menuRoot = new CommonTreeNode();
		menuRoot.setId(CommonTreeNode.ROOT_ID);
		menuRoot.setText("菜单根");
		menuRoot.setUpId("-1");
		menuRoot.setIcon(basePath + GlobalConstants4frame.LOGIC_ADMIN_ICON);
		return menuRoot;
	}

	/**
	 * 获得功能树
	 * 
	 * @param funcList
	 * @param isIndex
	 *            true:该功能点是主页
	 * @return
	 */
	public List<CommonTreeNode> funcToTree(List<BioneFuncInfo> funcList, boolean isIndex,String funcName) {
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		boolean isLogicFunc = true;
		if(funcName == null){
			if (funcList == null && !isIndex) {// 如果传入的功能集合为空，哪就将所有的功能点都取出
				isLogicFunc = false;
				List<BioneModuleInfo> modulList = getEntityList(BioneModuleInfo.class);
				for (BioneModuleInfo moduleInfo : modulList) {
					CommonTreeNode treeNode = new CommonTreeNode();
					treeNode.setId(moduleInfo.getModuleId());
					treeNode.setText(moduleInfo.getModuleName());
					treeNode.setUpId(CommonTreeNode.ROOT_ID);
					treeNode.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
					Map<String, String> params = new HashMap<String, String>();
					params.put("type", GlobalConstants4frame.MENU_TYPE_MODULE);
					treeNode.setParams(params);
					treeNodes.add(treeNode);
				}
				funcList = getEntityList(BioneFuncInfo.class);
			}
		}else{
			funcList = this.baseDAO.findWithIndexParam("select info from BioneFuncInfo info where info.funcName like ?0 ", "%"+funcName+"%");
			if(funcList.size()<= 0){
				funcList = getEntityList(BioneFuncInfo.class);
			}
		}
		

		// 对功能进行排序
		Collections.sort(funcList, new ComparatorFunc());
		Set<String> funcSet = new HashSet<String>();
		for (int i = 0; i < funcList.size(); i++) {

			BioneFuncInfo funcInfo = funcList.get(i);
			funcSet.add(funcInfo.getModuleId());
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(funcInfo.getFuncId());
			if (isIndex) {
				treeNode.setText("<font color='red'>" + funcInfo.getFuncName() + "</font>");
			} else {
				treeNode.setText(funcInfo.getFuncName());
			}
			Map<String, String> params = new HashMap<String, String>();
			params.put("realName", funcInfo.getFuncName());
			params.put("type", GlobalConstants4frame.MENU_TYPE_FUNCTION);
			if (!isLogicFunc) {
				params.put("isNewNode", GlobalConstants4frame.COMMON_STATUS_VALID);
				if (GlobalConstants4frame.COMMON_STATUS_INVALID.equals(funcInfo.getUpId())) {
					// 为最顶端方法
					treeNode.setUpId(funcInfo.getModuleId());
				} else {
					treeNode.setUpId(funcInfo.getUpId());
				}
			} else {
				treeNode.setUpId(funcInfo.getUpId());
			}

			if(funcName !=null){
				treeNode.setUpId(funcInfo.getModuleId());
			}
			treeNode.setParams(params);

			if (funcInfo.getNavIcon() != null && !"".equals(funcInfo.getNavIcon())) {
				treeNode.setIcon(basePath + "/" + funcInfo.getNavIcon());
			} else {
				treeNode.setIcon(basePath + GlobalConstants4frame.LOGIC_FUNC_ICON);
			}

			treeNodes.add(treeNode);
		}
		if(funcName !=null){
			if(funcSet.size()>0){
				for(String moduleId :funcSet ){
					BioneModuleInfo moduleInfo = this.getEntityById(BioneModuleInfo.class,moduleId);
					if(moduleInfo !=null){
						CommonTreeNode moduleNode = new CommonTreeNode();
						moduleNode.setId(moduleInfo.getModuleId());
						moduleNode.setText(moduleInfo.getModuleName());
						moduleNode.setUpId(CommonTreeNode.ROOT_ID);
						treeNodes.add(0,moduleNode);
					}
				}
			}
		}
		
		return treeNodes;
	}

	/**
	 * 根据逻辑系统标识 获取逻辑系统菜单功能
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public List<BioneFuncInfo> getMenuByLogicSysNo(String logicSysNo) {
		List<BioneMenuInfo> menuList = getEntityListByProperty(BioneMenuInfo.class, "logicSysNo", logicSysNo);
		// 对菜单进行排序
		Collections.sort(menuList, new ComparatorMenu());

		List<BioneFuncInfo> funcList = new ArrayList<BioneFuncInfo>();
		for (BioneMenuInfo menuInfo : menuList) {
			if (!GlobalConstants4frame.COMMON_STATUS_VALID.equals(menuInfo.getIndexSts())) {
				BioneFuncInfo funcInfo = this.getEntityById(BioneFuncInfo.class, menuInfo.getFuncId());
				if (funcInfo != null) {
					funcInfo.setOrderNo(menuInfo.getOrderNo());
					BioneMenuInfo upMenuInfo = this.getEntityById(BioneMenuInfo.class, menuInfo.getUpId());
					if (upMenuInfo != null) {
						funcInfo.setUpId(upMenuInfo.getFuncId());
					} else {
						funcInfo.setUpId("0");
					}
					funcList.add(funcInfo);
				}
			}
		}
		return funcList;
	}

	/**
	 * 获得查询节点
	 * 
	 * @param funcName
	 * @return
	 */
	public List<CommonTreeNode> searchNodes(String funcName) {
		List<BioneFuncInfo> funcInfos = this.searchFunc(funcName);
		Set<String> modules = new HashSet<String>();
		//Map map = new HashMap();
		List<CommonTreeNode> treeNodes = new ArrayList<CommonTreeNode>();
		for (BioneFuncInfo funcInfo : funcInfos) {
			if("0".equals(funcInfo.getUpId())){
				modules.add(funcInfo.getModuleId());
			}
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(funcInfo.getFuncId());
			treeNode.setText(funcInfo.getFuncName());
			treeNode.setData(funcInfo);
			treeNode.setUpId(funcInfo.getModuleId());
			treeNodes.add(treeNode);
			//treeNodes = getResultNode(funcInfo, map, treeNodes);
		}
		for(String moduleId :modules ){
			BioneModuleInfo moduleInfo = this.getEntityById(BioneModuleInfo.class,moduleId);
			if(moduleInfo !=null){
				CommonTreeNode moduleNode = new CommonTreeNode();
				moduleNode.setId(moduleInfo.getModuleId());
				moduleNode.setText(moduleInfo.getModuleName());
				moduleNode.setUpId(CommonTreeNode.ROOT_ID);
				treeNodes.add(moduleNode);
			}
		}
		return treeNodes;
	}

	/**
	 * 根据查询的结果 构造显示数据
	 * 
	 * @param funcInfo
	 * @param map
	 * @param treeNodes
	 * @return
	 */
	public List<CommonTreeNode> getResultNode(BioneFuncInfo funcInfo, Map<String, BioneFuncInfo> map, List<CommonTreeNode> treeNodes) {
		// String basePath = ((ServletRequestAttributes)
		// RequestContextHolder.getRequestAttributes()).getRequest()
		// .getSession().getServletContext().getServletContextName();
		if (map.get(funcInfo.getFuncId()) == null) {
			map.put(funcInfo.getFuncId(), funcInfo);
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(funcInfo.getFuncId());
			treeNode.setText(funcInfo.getFuncName());

			treeNode.setIcon(basePath + funcInfo.getNavIcon());
			if (GlobalConstants4frame.COMMON_STATUS_INVALID.equals(funcInfo.getUpId())) {
				// 为最顶端方法
				treeNode.setUpId(funcInfo.getModuleId());
				BioneModuleInfo moduleInfo = this.getEntityById(BioneModuleInfo.class, funcInfo.getModuleId());
				if (moduleInfo != null) {
					CommonTreeNode moduleNode = new CommonTreeNode();
					moduleNode.setId(moduleInfo.getModuleId());
					moduleNode.setText(moduleInfo.getModuleName());
					moduleNode.setUpId(CommonTreeNode.ROOT_ID);
					treeNodes.add(moduleNode);
				}
			} else {
				treeNode.setUpId(funcInfo.getUpId());
				BioneFuncInfo bioneFuncInfo = this.getEntityById(BioneFuncInfo.class, funcInfo.getUpId());
				treeNodes = getResultNode(bioneFuncInfo, map, treeNodes);
			}
			treeNodes.add(treeNode);
		}
		return treeNodes;
	}

	/**
	 * 查询功能节点
	 * 
	 * @param funcName
	 * @return
	 */
	public List<BioneFuncInfo> searchFunc(String funcName) {
		String jql = "select obj from BioneFuncInfo obj where obj.funcName like ?0";
		List<BioneFuncInfo> funcInfos = this.baseDAO.findWithIndexParam(jql, "%" + funcName + "%");
		return funcInfos;
	}

	/**
	 * 通过逻辑系统标识获取逻辑系统信息表对象
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public BioneLogicSysInfo getBioneLogicSysInfoByLogicSysNo(String logicSysNo) {
		List<BioneLogicSysInfo> list = this.getEntityListByProperty(BioneLogicSysInfo.class, "logicSysNo", logicSysNo);
		return list.get(0);
	}

	/**
	 * 根据逻辑系统号获取已经授权的资源
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public List<BioneAuthResDef> getAuthResByLogicSysNo(String logicSysNo) {
		String jql = "select obj from BioneAuthResDef obj,BioneAuthResSysRel resSysRel where obj.resDefNo=resSysRel.id.resDefNo "
				+ " and resSysRel.id.logicSysNo =?0 ";
		List<BioneAuthResDef> authResList = this.baseDAO.findWithIndexParam(jql, logicSysNo);
		return authResList;
	}

	/**
	 * 获取该逻辑系统没有授权的资源
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public List<BioneAuthResDef> getAuthRes(String logicSysNo) {
		List<BioneAuthResDef> resDefs = this.getEntityList(BioneAuthResDef.class);
		List<BioneAuthResDef> authResDefs = this.getAuthResByLogicSysNo(logicSysNo);
		resDefs.removeAll(authResDefs);
		return resDefs;
	}

	/**
	 * 将授权资源组装成树
	 * 
	 * @param authResByLogicSysNo
	 * @return
	 */
	public List<CommonTreeNode> AuthResToTree(List<BioneAuthResDef> authResByLogicSysNo) {
		// String basePath = ((ServletRequestAttributes)
		// RequestContextHolder.getRequestAttributes()).getRequest()
		// .getSession().getServletContext().getServletContextName();
		List<CommonTreeNode> resTree = new ArrayList<CommonTreeNode>();
		for (int i = 0; i < authResByLogicSysNo.size(); i++) {
			BioneAuthResDef authResDef = authResByLogicSysNo.get(i);
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(authResDef.getResDefNo());
			treeNode.setText(authResDef.getResName());
			treeNode.setIcon(basePath + GlobalConstants4frame.LOGIC_AUTH_RES_ICON);
			resTree.add(treeNode);
		}
		return resTree;
	}

	/**
	 * 根据逻辑系统号获取已经授权对象
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public List<BioneAuthObjDef> getAuthObjByLogicSysNo(String logicSysNo) {
		String jql = "select obj from BioneAuthObjDef obj,BioneAuthObjSysRel objSysRel where obj.objDefNo=objSysRel.id.objDefNo "
				+ " and objSysRel.id.logicSysNo =?0 ";
		List<BioneAuthObjDef> authObjList = this.baseDAO.findWithIndexParam(jql, logicSysNo);
		return authObjList;
	}

	/**
	 * 获取该逻辑系统没有授权的资源
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public List<BioneAuthObjDef> getAuthObj(String logicSysNo) {
		List<BioneAuthObjDef> objDefs = this.getEntityList(BioneAuthObjDef.class);
		List<BioneAuthObjDef> authObjDefs = this.getAuthObjByLogicSysNo(logicSysNo);
		objDefs.removeAll(authObjDefs);
		return objDefs;
	}

	/**
	 * 将授权资源组装成树
	 * 
	 * @param authObjByLogicSysNo
	 * @return
	 */
	public List<CommonTreeNode> AuthObjToTree(List<BioneAuthObjDef> authObjByLogicSysNo) {
		// String basePath = ((ServletRequestAttributes)
		// RequestContextHolder.getRequestAttributes()).getRequest()
		// .getSession().getServletContext().getServletContextName();
		List<CommonTreeNode> objTree = new ArrayList<CommonTreeNode>();
		for (int i = 0; i < authObjByLogicSysNo.size(); i++) {
			BioneAuthObjDef authObjDef = authObjByLogicSysNo.get(i);
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(authObjDef.getObjDefNo());
			treeNode.setText(authObjDef.getObjDefName());
			treeNode.setIcon(basePath + GlobalConstants4frame.LOGIC_AUTH_OBJ_ICON);
			objTree.add(treeNode);
		}
		return objTree;
	}

	/**
	 * 检查该资源或对象 是否能被移除
	 * 
	 * @param logicSysNo
	 * @param authResNo
	 */
	public <X> List<X> checkResOrObj(String logicSysNo, String authResNo, String authObj) {
		List<X> list = new ArrayList<X>();
		if (logicSysNo != null && !"".equals(logicSysNo)) {
			String param = "";
			StringBuilder jql = new StringBuilder("select obj from BioneAuthObjResRel obj ");
			jql.append(" where obj.id.logicSysNo =?0 ");
			if (authResNo != null && !"".equals(authResNo)) {
				jql.append(" and obj.id.resDefNo =?1 ");
				param = authResNo;
			} else if (authObj != null && !"".equals(authObj)) {
				jql.append(" and obj.id.objDefNo =?1 ");
				param = authObj;
			} else {
				jql.append(" and 1=2 ");
			}
			list = this.baseDAO.findWithIndexParam(jql.toString(), logicSysNo, param);
		}
		return list;
	}

	/**
	 * 检查该资源 是否能被移除
	 * 
	 * @param logicSysNo
	 * @param authResNo
	 * @return
	 */
	public boolean checkRes(String logicSysNo, String authResNo) {
		List<?> list = checkResOrObj(logicSysNo, authResNo, null);
		if (list != null && list.size() > 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 检查该对象 是否能被移除
	 * 
	 * @param logicSysNo
	 * @param authObjNo
	 * @return
	 */
	public boolean checkObj(String logicSysNo, String authObjNo) {
		List<?> list = checkResOrObj(logicSysNo, null, authObjNo);
		if (list != null && list.size() > 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取首页
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public List<BioneFuncInfo> indexToTree(String logicSysNo) {
		String jql = "select func from BioneFuncInfo func,BioneMenuInfo obj "
				+ "where obj.logicSysNo =?0 and obj.indexSts=?1 and func.funcId=obj.funcId ";
		List<BioneFuncInfo> indexFuncInfos = this.baseDAO.findWithIndexParam(jql, logicSysNo, "1");
		if (indexFuncInfos != null && indexFuncInfos.size() > 0) {
			indexFuncInfos.get(0).setUpId("0");
		} else {
			indexFuncInfos = new ArrayList<BioneFuncInfo>();
		}
		return indexFuncInfos;
	}

	/**
	 * 判断是否已存在该逻辑系统的记录
	 * 
	 * @param logicSysNo
	 * @return
	 */
	public boolean isExistLogicSys(String logicSysNo) {
		String jql = "select sys.LOGIC_SYS_ID from BioneLogicSysInfo sys where sys.logicSysNo=?0";
		List<String> sysList = this.baseDAO.findWithIndexParam(jql, logicSysNo);
		return CollectionsUtils.isEmpty(sysList) ? false : true;
	}

	/**
	 * 逻辑系统导入更新数据
	 * 
	 * @param content
	 */
	@Transactional(readOnly = false)
	public String saveWithJsonStr(String content) {
		if (StringUtils.isNotEmpty(content)) {
			Set<String> res = Sets.newHashSet();
			JSONObject jsonObject = JSON.parseObject(content);
			for (String logicSysNo : (Set<String>) jsonObject.keySet()) { // 对
																			// json
																			// 的最外层,
																			// 逻辑系统信息进行迭代
				if (this.isExistLogicSys(logicSysNo)) { // 是否存在当前逻辑系统,
														// 存在则跳过
					res.add(logicSysNo);
					continue;
				}
				JSONObject jsonObj = jsonObject.getJSONObject(logicSysNo); // 获取逻辑系统下的实体信息
				for (String entityName : (Set<String>) jsonObj.keySet()) { // 对实体名称层进行迭代
					JSONArray jsonArray = jsonObj.getJSONArray(entityName); // 获取实体对应的数据
					for (int i = 0; i < jsonArray.size(); i ++) {
						JSONObject arrayObj = jsonArray.getJSONObject(i); // 对实体数据数组进行迭代
						if (entityName.contains("BioneTaskInfo")) {
							this.taskBS.updateJob(arrayObj.toJavaObject(BioneTaskInfo.class));
						} else {
							this.baseDAO.merge(arrayObj.toJavaObject(BioneLogicSysInfo.class));
						}
					}
				}
			}
			if (CollectionUtils.isNotEmpty(res)) {
				return StringUtils.join(res, ", "); // 向前台指出“已存在, 不能导入”的逻辑系统信息
			}
		}
		return null;
	}
}
