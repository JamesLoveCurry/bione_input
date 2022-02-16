package com.yusys.bione.frame.base.web;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.common.LteTreeNode;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authres.entity.BioneFuncInfo;
import com.yusys.bione.frame.authres.entity.BioneMenuInfo;
import com.yusys.bione.frame.authres.service.FuncBS;
import com.yusys.bione.frame.authres.service.MenuBS;
import com.yusys.bione.frame.authres.util.MenuUtils;
import com.yusys.bione.frame.authres.web.vo.BioneMenuInfoVO;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.common.LogicSysInfoHolder;
import com.yusys.bione.frame.logicsys.entity.BioneLogicSysInfo;
import com.yusys.bione.frame.logicsys.service.LogicSysBS;
import com.yusys.bione.frame.passwd.entity.BionePwdHis;
import com.yusys.bione.frame.passwd.entity.BionePwdSecurityCfg;
import com.yusys.bione.frame.passwd.service.PwdSecurityCfgBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.lock.service.UserLockManageBS;
import com.yusys.bione.frame.user.service.UserBS;

/**
 * <pre>
 * Title:首页Action
 * Description: 负责处理首页菜单展示、导航等相关请求
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
@Controller
@RequestMapping("/index")
public class IndexController extends BaseController {
	protected static Logger log = LoggerFactory.getLogger(IndexController.class);
	@Autowired
	private MenuBS menuBS;
	@Autowired
	private UserBS userBS;
	@Autowired
	private FuncBS funcBS;
	@Autowired
	private LogicSysBS logicSysBS;

	@Autowired
	private AuthBS authBS;

	@Autowired
	private PwdSecurityCfgBS pscBS;

	@Autowired
	private UserLockManageBS userLockManageBS;

	/**
	 * 显示系统首页
	 *
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		PropertiesUtils propertiesUtils = PropertiesUtils.get(
				"bione-frame/index/index.properties");
		String indexType = propertiesUtils.getProperty("index_type");
		boolean loop = true;
		String indexConfig = "index/index";
		if(indexType.equals("1")){
			loop = false;
			indexConfig = "index/indexBS";
		}
		else{
			loop = true;
			indexConfig = "index/indexBS";
		}
		String isOpenCache = propertiesUtils.getProperty("isOpenCache");
		if("Y".equals(isOpenCache)) {//缓存模式走一个极简页面，应对压测
			indexConfig = "index/indexBS-min";
		}
		ModelAndView mav = new ModelAndView();
		mav.addObject("indexUrl", "index/welcome");
		BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();
		BioneLogicSysInfo logicsys = logicSysBS.getBioneLogicSysInfoByLogicSysNo(bioneUser.getCurrentLogicSysNo());
		mav.addObject("logicSysName", logicsys.getLogicSysName());
		List<CommonTreeNode> menuInfoList = this.menuBS.buildMenuTree(bioneUser.getCurrentLogicSysNo(), "0", loop);// 只查找第一层的节点
		if (menuInfoList != null) {
			for (CommonTreeNode node : menuInfoList) {
				BioneMenuInfoVO menuInfo = (BioneMenuInfoVO) node.getData();
				if (menuInfo != null && menuInfo.getIndexSts() != null
						&& menuInfo.getIndexSts().equals(GlobalConstants4frame.COMMON_STATUS_VALID)) {
					mav.addObject("indexUrl", menuInfo.getNavPath());
					mav.addObject("indexNm",menuInfo.getFuncName());
					mav.addObject("indexId",menuInfo.getMenuId());
				}
			}
			List<LteTreeNode>  menuObjList = MenuUtils.list2AccordionMenuLTEWithIframe(menuInfoList);
			String menu_json = JSONArray.toJSONString(menuObjList, SerializerFeature.WriteNullListAsEmpty);
			String menuInfoHTML = StringUtils2.javaScriptEncode(menu_json);
			mav.addObject("menuInfoHTML", menuInfoHTML);
		}
		StringBuilder strBuilder = new StringBuilder();

		// 获取当前用户相关的信息
		strBuilder.append("欢迎您:" + bioneUser.getUserName());

		String userInfo = strBuilder.toString();

		String userIcon = this.userBS.getUserIcon(bioneUser.getUserId());
		// 系统首页
		mav.setViewName(indexConfig);
		/* @Revision 2013-5-10 添加了[userId]和[userName]两个属性。 */
		mav.addObject("userId", bioneUser.getUserId());
		mav.addObject("userName", bioneUser.getUserName());
		mav.addObject("userOrgName", bioneUser.getOrgName());
		mav.addObject("userInfo", userInfo);
		if(userIcon!=null&&userIcon.length()>0) {
			mav.addObject("userIcon", userIcon);
		} else {
			mav.addObject("userIcon", "/images/classics/index/users/user0.png");
		}
		mav.addObject("logicSysNo", bioneUser.getCurrentLogicSysNo());
		mav.addObject("items", getLogicSysOption());
        mav.addObject("lastUpdateTime",bioneUser.getLastUpdateTime()!=null&&bioneUser.getLastUpdateTime().length()>=8?bioneUser.getLastUpdateTime().substring(0,10):"");
		//添加密码时间校验和首次登陆校验
        boolean isFirst = validIsFirst(bioneUser.getUserNo(), bioneUser.getCurrentLogicSysNo());
		mav.addObject("isFirst", isFirst);
        return mav;
	}

	public List<BioneLogicSysInfo> getLogicSysOption() {
		List<BioneLogicSysInfo> logicSysList = LogicSysInfoHolder.getLogicSysInfo();
		List<BioneLogicSysInfo> newlist = new ArrayList<BioneLogicSysInfo>();
		newlist.addAll(logicSysList);
		BioneLogicSysInfo logicSysInfo = null;
		for (BioneLogicSysInfo logicSys : newlist) {
			if (logicSys.getLogicSysNo().equals(GlobalConstants4frame.SUPER_LOGIC_SYSTEM)) {
				logicSysInfo = logicSys;
			}
		}
		newlist.remove(logicSysInfo);
		return newlist;
	}

	/**
	 * @方法描述: 判断用户是否首次登陆
	 * @创建人: huzq1
	 * @创建时间: 2021/7/15 14:33
	 * @param username
	 * @param logicSysNo
	 * @return
	 **/
	public boolean validIsFirst(String username, String logicSysNo) {
		BionePwdSecurityCfg bionePwdSecurityCfg = pscBS.getEntityById("1");
		List<BionePwdHis> bionePwdHisList = new ArrayList<>();
		boolean isSuperUser = authBS.findAdminUserInfo(username, logicSysNo);
		if(isSuperUser){
			logicSysNo = GlobalConstants4frame.SUPER_LOGIC_SYSTEM;
		}
		String userId = userLockManageBS.getUserIdByNo(username, logicSysNo);
		bionePwdHisList = userLockManageBS.getHisByUser(userId, logicSysNo);
		//判断是否首次登陆
		if(GlobalConstants4frame.COMMON_YES.equals(bionePwdSecurityCfg.getIsFirst())){
			if(bionePwdHisList == null || bionePwdHisList.size() == 0){
				return true;
			}
		}
//		//判断密码有效期
//		try {
//			BionePwdHis bionePwdHis = bionePwdHisList.get(0);
//			Timestamp updateTime = bionePwdHis.getUpdateTime();
//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String str = df.format(updateTime);
//			Date oldDate = df.parse(str);
//
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(oldDate);
//			cal.add(Calendar.MONTH, bionePwdSecurityCfg.getValidTime().intValue());
//
//			Date time = cal.getTime();
//			return time.before(new Date());
//		} catch (Exception e){
//			e.printStackTrace();
//		}
		return false;
	}

	/**
	 * 获取左侧边栏可折叠菜单数据
	 *
	 * @return
	 */
	@RequestMapping(value = "/initAccordionMenu.json", method = RequestMethod.POST)
	@ResponseBody
	public String initAccordionMenu(String parentId) {
		BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();
		String menuId = parentId;
		List<CommonTreeNode> menuInfoList = this.menuBS.buildMenuTree(bioneUser.getCurrentLogicSysNo(), menuId, true);

		BioneMenuInfo parentMenuInfo = this.menuBS.getEntityById(BioneMenuInfo.class, menuId);
		BioneFuncInfo funcInfo = this.funcBS.getEntityById(BioneFuncInfo.class, parentMenuInfo.getFuncId());

		String menuInfoHTML = MenuUtils.list2AccordionMenu(funcInfo, menuInfoList);
		return menuInfoHTML;

	}

	// 跳转欢迎页面
	@RequestMapping("/welcome")
	public String welcome() {
		return "/index/welcome";
	}
}
