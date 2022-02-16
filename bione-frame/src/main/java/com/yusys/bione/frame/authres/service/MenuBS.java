package com.yusys.bione.frame.authres.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.authres.entity.BioneFuncInfo;
import com.yusys.bione.frame.authres.entity.BioneFuncMethodurlInfo;
import com.yusys.bione.frame.authres.entity.BioneMenuInfo;
import com.yusys.bione.frame.authres.web.vo.BioneMenuInfoVO;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Title:菜单管理业务逻辑类
 * Description:  提供菜单管理相关业务逻辑处理功能，提供事务控制
 * </pre>
 * 
 * @author mengzx
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class MenuBS extends BaseBS<BioneMenuInfo> {

	protected static Logger log = LoggerFactory.getLogger(MenuBS.class);

	/**
	 * 查找所有状态为有效的菜单
	 * 
	 * @param logicSystem
	 * @param parentId
	 * @return
	 */
	public List<Object[]> findAllValidMenu(String logicSystem, String parentId) {
		String jql = "select func,menu from BioneFuncInfo func,BioneMenuInfo menu where func.funcId=menu.funcId and func.funcSts=?0 and menu.logicSysNo=?1";
		List<Object> paramValues = Lists.newArrayList();
		paramValues.add(GlobalConstants4frame.COMMON_STATUS_VALID);
		paramValues.add(logicSystem);
		if (parentId != null) {
			jql += " and menu.upId=?2";
			paramValues.add(parentId);
		}
		jql += " order by menu.indexSts,menu.orderNo,func.orderNo asc";
		// 获取所有效菜单
		List<Object[]> menuInfoList = this.baseDAO.findWithIndexParam(jql, paramValues.toArray());
		return menuInfoList;
	}

	/**
	 * 构造菜单显示树，并返回第一层的节点
	 * 
	 * @param parentId
	 *            上级节点id
	 * @param isLoop
	 *            是否遍历子节点
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CommonTreeNode> buildMenuTree(String logicSystem, String parentId, boolean isLoop) {
		List<CommonTreeNode> firstLevelNodeList = new ArrayList<CommonTreeNode>();
		List<Object[]> menuInfoList = null;
		if (isLoop) {
			menuInfoList = this.findAllValidMenu(logicSystem, null);
		} else {
			menuInfoList = this.findAllValidMenu(logicSystem, parentId);
		}

		// 以父菜单id->子菜单列表的MAP结构对才菜单进行分组
		Map<String, List<BioneMenuInfoVO>> upMenuIdMap = Maps.newHashMap();
		if (menuInfoList != null) {
			boolean isSuperUser = BioneSecurityUtils.getCurrentUserInfo().isSuperUser();
			PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-frame/index/index.properties");
			String isOpenCache = propertiesUtils.getProperty("isOpenCache");
			List<String> authMenuIdList = new ArrayList<String>();
			if("Y".equals(isOpenCache)) {//是否走缓存模式，缓存模式菜单就走缓存
				log.info("进入菜单缓存模式！！！");
				authMenuIdList = (List<String>) EhcacheUtils.get(GlobalConstants4frame.AUTH_RES_DEF_ID_MENU, logicSystem);
				if(null == authMenuIdList) {
					authMenuIdList = BioneSecurityUtils.getResIdListOfUser(GlobalConstants4frame.AUTH_RES_DEF_ID_MENU);
					EhcacheUtils.put(GlobalConstants4frame.AUTH_RES_DEF_ID_MENU, logicSystem, authMenuIdList);//缓存菜单资源
				}
			}else {
				authMenuIdList = BioneSecurityUtils.getResIdListOfUser(GlobalConstants4frame.AUTH_RES_DEF_ID_MENU);
			}
			// 将机构按层级进行分类
			List<BioneMenuInfoVO> oneLevelMenuInfoList = null;
			for (Object[] menuObj : menuInfoList) {
				BioneFuncInfo func = (BioneFuncInfo) menuObj[0];
				BioneMenuInfo menu = (BioneMenuInfo) menuObj[1];
				BioneMenuInfoVO vo = new BioneMenuInfoVO();
				BeanUtils.copy(func, vo);
				vo.setMenuId(menu.getMenuId());
				vo.setUpId(menu.getUpId());
				vo.setIndexSts(menu.getIndexSts());// 设置首页
				if (!isSuperUser) {// 如果不是超级管理员，对菜单进行过滤
					if (!authMenuIdList.contains(vo.getMenuId()))
						continue;
				}
				oneLevelMenuInfoList = upMenuIdMap.get(vo.getUpId());
				if (oneLevelMenuInfoList == null) {
					oneLevelMenuInfoList = Lists.newArrayList();
					upMenuIdMap.put(vo.getUpId(), oneLevelMenuInfoList);
				}
				// 若是一级菜单首页，放进集合第一个位置
				if (GlobalConstants4frame.COMMON_STATUS_VALID.equals(vo.getIndexSts())) {
					oneLevelMenuInfoList.add(0, vo);
				} else {
					oneLevelMenuInfoList.add(vo);
				}
				
			}
		}

		// 循环遍历，构造树形结构
		// 获取最顶层的菜单信息
		List<BioneMenuInfoVO> rootMenuInfoList = upMenuIdMap.get(parentId);
		if (rootMenuInfoList != null) {
			for (BioneMenuInfoVO menuInfo : rootMenuInfoList) {
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId(menuInfo.getMenuId() + "");
				treeNode.setUpId(menuInfo.getUpId() + "");
				treeNode.setText(menuInfo.getFuncName());
				treeNode.setData(menuInfo);
				// 循环构造子节点
				buildChildTreeNode(treeNode, upMenuIdMap);
				firstLevelNodeList.add(treeNode);
				
			}
		}
		return firstLevelNodeList;
	}

	/**
	 * 循环构造子节点
	 * 
	 * @param parentNode
	 *            父亲节点
	 * @param upMenuIdMap
	 * 
	 */
	private void buildChildTreeNode(CommonTreeNode parentNode,
			Map<String, List<BioneMenuInfoVO>> upMenuIdMap) {
		// 构造当前节点的子节点
		List<BioneMenuInfoVO> childMenuInfoList = upMenuIdMap.get(parentNode.getId());
		if (childMenuInfoList != null) {
			for (BioneMenuInfoVO menuInfo : childMenuInfoList) {
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId(menuInfo.getMenuId() + "");
				treeNode.setUpId(menuInfo.getUpId() + "");
				treeNode.setText(menuInfo.getFuncName());
				treeNode.setData(menuInfo);
				parentNode.addChildNode(treeNode);
				buildChildTreeNode(treeNode, upMenuIdMap);
			}
		}
	}

	/**
	 * 保存菜单配置
	 * @param logicSysNo
	 * 逻辑系统标识
	 * @param menuMap
	 *  菜单Map key：上一级菜单的功能Id ; value: 当前菜单
	 * 
	 */
	@Transactional(readOnly = false)
	public void saveMenuList(String logicSysNo, Map<String, List<BioneMenuInfo>> menuMap) {
		List<String> menuIdList = new ArrayList<String>();
		menuIdList = saveMenu(logicSysNo, "0", "0", menuMap, menuIdList);
		if (menuIdList.size() > 0) {
			String sql = "delete from BioneMenuInfo menu where menu.logicSysNo =?0 and menu.menuId not in (?1)";
			this.baseDAO.batchExecuteWithIndexParam(sql, logicSysNo, menuIdList);
		} else if (menuMap.size() == 0) {
			String sql = "delete from BioneMenuInfo menu where menu.logicSysNo =?0 ";
			this.baseDAO.batchExecuteWithIndexParam(sql, logicSysNo);
		}
	}

	/**
	 * 迭代保存 菜单上下级关系
	 * 
	 * @param logicSysNo
	 *            逻辑系统编号
	 * @param upFuncId
	 *            上级功能Id
	 * @param upMenuId
	 *            上级菜单Id
	 * @param menuMap
	 *            菜单Map key：上一级菜单的功能Id ; value: 当前菜单
	 * @param menuIdList
	 *            菜单Id组
	 * @return menuIdList 菜单Id组
	 */
	private List<String> saveMenu(String logicSysNo, String upFuncId,
			String upMenuId, Map<String, List<BioneMenuInfo>> menuMap,
			List<String> menuIdList) {
		List<BioneMenuInfo> menuList = menuMap.get(upFuncId);
		if (menuList == null) {
			menuList = new ArrayList<BioneMenuInfo>();
		}
		int i = 0;
		for (BioneMenuInfo bioneMenuInfo : menuList) {
			List<BioneMenuInfo> menuInfos = findMenuByFuncId(logicSysNo, bioneMenuInfo.getFuncId());
			String menuId = "";
			if (menuInfos.size() > 0) {
				this.baseDAO.flush();
				menuId = menuInfos.get(0).getMenuId();

			}
			if ("".equals(menuId)) {
				menuId = RandomUtils.uuid2();
			}
			bioneMenuInfo.setMenuId(menuId);
			bioneMenuInfo.setUpId(upMenuId);
			bioneMenuInfo.setOrderNo(new BigDecimal(i));
			updateEntity(bioneMenuInfo);
			menuIdList.add(menuId);
			saveMenu(logicSysNo, bioneMenuInfo.getFuncId(), menuId, menuMap, menuIdList);
			i++;
		}
		return menuIdList;
	}

	/**
	 * 根据逻辑系统 和 功能 Id 获取菜单
	 * 
	 * @param logicSysNo
	 *            逻辑系统标识
	 * @param funcId
	 *            功能Id
	 * @return List<BioneMenuInfo> 菜单集合
	 * 
	 */
	private List<BioneMenuInfo> findMenuByFuncId(String logicSysNo, String funcId) {
		String jql = "select menu from BioneMenuInfo menu where menu.logicSysNo =?0 and menu.funcId =?1";
		return this.baseDAO.findWithIndexParam(jql, logicSysNo, funcId);
	}
	
	/**
	 * 查询所有菜单包含的功能
	 * @return
	 */
	public List<BioneFuncMethodurlInfo> findAllFuncMethd() {
		String jql = "select method from BioneFuncMethodurlInfo method where method.methodSts =?0";
		return this.baseDAO.findWithIndexParam(jql, "Y");
	}

	public List<BioneMenuInfo> getMenuInfoByUrl(String url){
		String jql = "select menu from BioneFuncInfo func,BioneMenuInfo menu where func.funcId=menu.funcId and func.navPath = ?0";
		return this.baseDAO.findWithIndexParam(jql, url);
	}
}

