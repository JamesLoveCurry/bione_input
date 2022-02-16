package com.yusys.bione.frame.logicsys.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.CollectionsUtils;
import com.yusys.bione.comp.utils.DownloadUtils;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.comp.utils.FilesUtils;
import com.yusys.bione.comp.utils.FormatUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authconfig.entity.BioneAuthInfo;
import com.yusys.bione.frame.authres.entity.BioneMenuInfo;
import com.yusys.bione.frame.authres.service.MenuBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.common.LogicSysInfoHolder;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.logicsys.entity.BioneAdminUserInfo;
import com.yusys.bione.frame.logicsys.entity.BioneAdminUserInfoPK;
import com.yusys.bione.frame.logicsys.entity.BioneLogicSysInfo;
import com.yusys.bione.frame.logicsys.service.AdminUserBS;
import com.yusys.bione.frame.logicsys.service.AuthObjSysRelBS;
import com.yusys.bione.frame.logicsys.service.AuthResSysRelBS;
import com.yusys.bione.frame.logicsys.service.ExportEntityBS;
import com.yusys.bione.frame.logicsys.service.LogicSysBS;
import com.yusys.bione.frame.logicsys.web.vo.BioneLogicSysInfoVO;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.IAuthObject;
import com.yusys.bione.frame.user.entity.BioneUserInfo;

/**
 * 
 * <pre>
 * Title:逻辑系统管理
 * Description: 逻辑系统管理
 * </pre>
 * 
 * @author yunlei yunlei@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/admin/logicSys")
public class LogicSysController extends BaseController {

	@Autowired
	private LogicSysBS logicSysBS; // 逻辑系统 操作类
	@Autowired
	private AdminUserBS adminUserBS; // 逻辑系统 管理员操作类
	@Autowired
	private MenuBS menuBS; // 菜单操作类
	@Autowired
	private AuthResSysRelBS authResSysRelBS;
	@Autowired
	private AuthObjSysRelBS authObjSysRelBS;
	@Autowired
	private ExportEntityBS exportEntityBS; // 导出逻辑系统的类
	@Autowired
	private AuthBS authBS;

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/logicSys/logic-sys-index";
	}

	// 跳转logic-manage.jsp
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String editNew() {
		return "/frame/logicSys/logic-sys-editNew";
	}

	/*
	 * // 版权管理页面 public String copyRight() {
	 * this.getRequest().setAttribute("logicSysId", id); return "copyRight"; }
	 */

	/**
	 * 系统的关于信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public ModelAndView about() {
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		List<BioneLogicSysInfo> list = this.adminUserBS.getEntityListByProperty(BioneLogicSysInfo.class, "logicSysNo",
				logicSysNo);
		PropertiesUtils version = PropertiesUtils.get(
				"bione-frame/index/about.properties");
		String currVersion = version.getProperty("cur_version");
		if(null != version.getProperty("cur_version")){
			currVersion = version.getProperty("cur_version");
		}
		ModelMap mm = new ModelMap();
		if (!CollectionsUtils.isEmpty(list)) {
			BioneLogicSysInfo model = list.get(0);
			mm.put("systemVersion", StringUtils2.htmlEncode(model.getSystemVersion()));
			mm.put("cnCopyright", StringUtils2.htmlEncode(model.getCnCopyright()));
			mm.put("enCopyright", StringUtils2.htmlEncode(model.getEnCopyright()));
			mm.put("version", StringUtils2.htmlEncode(currVersion));
		}
		return new ModelAndView("/index/about", mm);
	}

	@RequestMapping("/checkLogicSysNo")
	@ResponseBody
	public String checkLogicSysNo(String logicSysNo) {
		if (logicSysNo != null && !"".equals(logicSysNo)) {
			BioneLogicSysInfo logicSysInfo = logicSysBS.findUniqueEntityByProperty("logicSysNo", logicSysNo);
			if (logicSysInfo != null) {
				return "false";
			}
		}
		return "true";
	}

	// 保存新
	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneLogicSysInfo model) {
		if (model.getOrderNo() == null) {
			model.setOrderNo(logicSysBS.getMaxOrder());
		}
		model.setIsBuiltin(GlobalConstants4frame.COMMON_STATUS_INVALID);
		model.setLastUpdateTime(new Timestamp(new Date().getTime()));
		model.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());
		if (model.getLogicSysId() == null || model.getLogicSysId().equals("")) {
			model.setLogicSysId(RandomUtils.uuid2());
		}
		// 保存
		logicSysBS.updateEntity(model);
		LogicSysInfoHolder.refreshLogicSysInfo();
	}

	/**
	 * 跳转 修改 页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/logicSys/logic-sys-editNew", "id", id);
	}

	/**
	 * 修改。加载页面
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BioneLogicSysInfo show(@PathVariable("id") String id) {
		BioneLogicSysInfo model = logicSysBS.getEntityById(BioneLogicSysInfo.class, id);
		return model;
	}

	/**
	 * 新增时默认加载 BIONE 系统的版权信息
	 */
	@RequestMapping(value = "/getBioneLogicSys", method = RequestMethod.GET)
	@ResponseBody
	public BioneLogicSysInfo getBioneLogicSys() {
		BioneLogicSysInfo model = new BioneLogicSysInfo();
		List<BioneLogicSysInfo> list = this.adminUserBS.getEntityListByProperty(BioneLogicSysInfo.class, "logicSysNo",
				"BIONE");
		if (!CollectionsUtils.isEmpty(list)) {
			BioneLogicSysInfo model_ = list.get(0);
			model.setCnCopyright(model_.getCnCopyright());
			model.setEnCopyright(model_.getEnCopyright());
		}
		return model;
	}

	/**
	 * 批量删除
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public String destroy(@PathVariable("id") String id) {
		String[] idArr = StringUtils.split(id, ',');
		logicSysBS.deleteBatch(idArr);
		LogicSysInfoHolder.refreshLogicSysInfo();
		return "true";
	}

	/**
	 * 显示页面列表
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager rf) {
		Map<String, Object> moduleMap = Maps.newHashMap();

		try {
			SearchResult<BioneLogicSysInfo> searchResult = logicSysBS.findResults(rf.getPageFirstIndex(),
					rf.getPagesize(), rf.getSortname(), rf.getSortorder(), rf.getSearchCondition());

			List<BioneLogicSysInfoVO> rows = new ArrayList<BioneLogicSysInfoVO>();

			List<BioneLogicSysInfo> logicSysList = searchResult.getResult();

			for (BioneLogicSysInfo logicSysTemp : logicSysList) {

				BioneLogicSysInfoVO logicSysVO = new BioneLogicSysInfoVO();

				BeanUtils.copyProperties(logicSysVO, logicSysTemp);

				List<BioneAuthInfo> authTypeList = logicSysBS.getEntityListByProperty(BioneAuthInfo.class,
						"authTypeNo", logicSysTemp.getAuthTypeNo());

				if (authTypeList.size() > 0) {
					logicSysVO.setAuthTypeName(authTypeList.get(0).getAuthTypeName());
				} else {
					logicSysVO.setAuthTypeName(logicSysVO.getAuthTypeNo());
				}

				BioneUserInfo userInfo = logicSysBS
						.getEntityById(BioneUserInfo.class, logicSysTemp.getLastUpdateUser());
				if (userInfo != null) {
					logicSysVO.setLastUpdateUserName(userInfo.getUserName());
				}

				rows.add(logicSysVO);
			}
			moduleMap.put("Rows", rows);
			moduleMap.put("Total", searchResult.getTotalCount());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return moduleMap;
	}

	/**
	 * 给逻辑系统添加管理员
	 * 
	 * @return
	 */
	@RequestMapping("/{id}/adminUser")
	public ModelAndView adminUser(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/logicSys/logic-sys-adminUser", "id", id);

	}

	/**
	 * 加载下拉图片选择
	 */
	// public void findImgForCombo() {
	// list = buildIconCombox("sysicons");
	// }

	/**
	 * 选择图片
	 * 
	 * @return
	 */
	// public DefaultHttpHeaders selectImages() {
	// this.buildIconSelectHTML("sysicons");
	// return new DefaultHttpHeaders("images").disableCaching();
	// }

	/**
	 * 获取用户列表
	 */
	@RequestMapping("/{id}/getUserList.*")
	@ResponseBody
	public List<CommonTreeNode> getUserList(@PathVariable("id") String id, String userName) {
		List<String> beanNames = this.authBS.findAuthObjBeanNameByType(GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER);
		if (beanNames != null && beanNames.size() > 0) {
			// 存在至少一个授权对象实现类申明
			String beanName = beanNames.get(0);
			IAuthObject authObj = SpringContextHolder.getBean(beanName);
			List<CommonTreeNode> userList = authObj.doGetAuthObjectInfo();
			List<CommonTreeNode> removeList = new ArrayList<CommonTreeNode>();
			List<BioneUserInfo> adminList = logicSysBS.getAdminList(id);
			for (CommonTreeNode node : userList) {
				node.setIcon(this.getContextPath() + "/" + node.getIcon());
				for (BioneUserInfo user : adminList) {
					if (node.getId().equals(user.getUserId())) {
						removeList.add(node);
					}
				}
			}
			userList.removeAll(removeList);
			return userList;
		} else {
			return new ArrayList<CommonTreeNode>();
		}
	}

	/**
	 * 获取逻辑系统的管理信息
	 */
	@RequestMapping("/{id}/getAdminUserList.*")
	@ResponseBody
	public List<CommonTreeNode> getAdminUserList(@PathVariable("id") String id) {
		return logicSysBS.userToTree(logicSysBS.getAdminList(id));
	}

	/**
	 * 保存管理员
	 */
	@RequestMapping("/saveAdmin")
	public void saveAdmin(String id, String params) {
		String[] userIds = StringUtils.split(params, ';');
		List<BioneAdminUserInfo> adminList = new ArrayList<BioneAdminUserInfo>();
		for (String userId : userIds) {
			if (!"".equals(userId)) {
				BioneAdminUserInfo adminUser = new BioneAdminUserInfo();
				BioneAdminUserInfoPK adminUserInfoPK = new BioneAdminUserInfoPK();
				adminUserInfoPK.setLogicSysId(id);
				adminUserInfoPK.setUserId(userId);
				adminUser.setId(adminUserInfoPK);
				adminUser.setRemark("");
				adminUser.setUserSts("1");
				adminUser.setLastUpdateTime(new Timestamp(new Date().getTime()));
				adminUser.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());
				adminList.add(adminUser);
			}
		}
		adminUserBS.saveLogicSysAdmin(id, adminList);
	}

	/**
	 * 跳转 添加菜单页面
	 * 
	 * @return
	 */
	@RequestMapping("/addMenu")
	public ModelAndView addMenu(String logicSysNo) {
		logicSysNo = StringUtils2.javaScriptEncode(logicSysNo);
		return new ModelAndView("/frame/logicSys/logic-sys-menu", "logicSysNo", logicSysNo);
	}

	/**
	 * 获取功能列表
	 */
	@RequestMapping("/getFuncList.*")
	public Map<String, Object> getFuncList() {
		String funcName = getRequest().getParameter("funcName");
		List<?> resultList = new ArrayList<Object>();
		if (!"".equals(funcName) && funcName != null) {
			resultList = logicSysBS.searchNodes(funcName);
		} else {
			resultList = logicSysBS.funcToTree(null);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("nodes", resultList);
		return resultMap;
	}

	/**
	 * 根据逻辑系统标示获取功能列表
	 */
	@RequestMapping("/getMenuToTree.*")
	public Map<String, Object> getMenuToTree(String logicSysNo,String funcName) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		// resultList.add(logicSysBS.getMenuRoot());
		List<CommonTreeNode> indexList = logicSysBS.funcToTree(logicSysBS.indexToTree(logicSysNo), true,funcName);
		if (indexList.size() == 1) {
			resultMap.put("indexPage", indexList.get(0).getId());
			resultList.add(indexList.get(0));
		}
		if(funcName ==null ) {
			resultList.addAll(logicSysBS.funcToTree(logicSysBS.getMenuByLogicSysNo(logicSysNo)));
			resultMap.put("nodes", resultList);
		}else{
			resultMap.put("nodes", indexList);
		}
		
		return resultMap;
	}

	/**
	 * 保存菜单相关配置
	 */
	@RequestMapping("/saveMenu")
	public void saveMenu(String logicSysNo, String params, String indexPageId) {
		String[] funcIdAndUpIds = StringUtils.split(params, ';');

		Map<String, List<BioneMenuInfo>> menuMap = new HashMap<String, List<BioneMenuInfo>>();
		for (String funcIdAndUpId : funcIdAndUpIds) {
			if (!"".equals(funcIdAndUpId)) {

				String[] funcIdAndUpIdArr = StringUtils.split(funcIdAndUpId, ':');
				String funcId = funcIdAndUpIdArr[0];
				String upId = funcIdAndUpIdArr[1];

				BioneMenuInfo menuInfo = new BioneMenuInfo();

				menuInfo.setFuncId(funcId);
				menuInfo.setLogicSysNo(logicSysNo);
				menuInfo.setUpId(upId);

				if (funcId.equals(indexPageId)) {
					menuInfo.setIndexSts(GlobalConstants4frame.COMMON_STATUS_VALID);
				} else {
					menuInfo.setIndexSts(GlobalConstants4frame.COMMON_STATUS_INVALID);
				}
				List<BioneMenuInfo> menuList = menuMap.get(upId);
				if (menuList == null) {
					menuList = new ArrayList<BioneMenuInfo>();
					menuList.add(menuInfo);
					menuMap.put(upId, menuList);
				} else {
					menuList.add(menuInfo);
				}
			}
		}
		menuBS.saveMenuList(logicSysNo, menuMap);
	}

	/**
	 * 授权资源
	 */
	@RequestMapping("/authRes")
	public ModelAndView authRes(String logicSysNo) {
		logicSysNo = StringUtils2.javaScriptEncode(logicSysNo);
		return new ModelAndView("/frame/logicSys/logic-sys-authRes", "logicSysNo", logicSysNo);
	}

	/**
	 * 获取资源定义
	 */
	@RequestMapping("/getResList.*")
	@ResponseBody
	public List<CommonTreeNode> getResList(String logicSysNo) {
		return logicSysBS.AuthResToTree(logicSysBS.getAuthRes(logicSysNo));
	}

	/**
	 * 获取逻辑系统已经授权的资源
	 */
	@RequestMapping("/getAuthResList.*")
	@ResponseBody
	public List<CommonTreeNode> getAuthResList(String logicSysNo) {
		return logicSysBS.AuthResToTree(logicSysBS.getAuthResByLogicSysNo(logicSysNo));
	}

	/**
	 * 检查节点是否被引用
	 */
	@RequestMapping("/checkRes")
	@ResponseBody
	public boolean checkRes(String nodeRealId, String logicSysNo) {
		return logicSysBS.checkRes(logicSysNo, nodeRealId);

	}

	/**
	 * 保存授权资源
	 */
	@RequestMapping("/saveAuthRes")
	public void saveAuthRes(String logicSysNo, String params) {
		String[] authResIds = StringUtils.split(params, ';');
		authResSysRelBS.saveAuthRes(logicSysNo, authResIds);
	}

	/**
	 * 跳转授权对象
	 * 
	 * @return
	 */
	@RequestMapping("/authObj")
	public ModelAndView authObj(String logicSysNo) {
		logicSysNo = StringUtils2.javaScriptEncode(logicSysNo);
		return new ModelAndView("/frame/logicSys/logic-sys-authObj", "logicSysNo", logicSysNo);
	}

	/**
	 * 获取授权对象定义
	 */
	@RequestMapping("/getObjList.*")
	@ResponseBody
	public List<CommonTreeNode> getObjList(String logicSysNo) {
		return logicSysBS.AuthObjToTree(logicSysBS.getAuthObj(logicSysNo));
	}

	/**
	 * 检查节点是否被引用
	 */
	@RequestMapping("/checkObj")
	@ResponseBody
	public boolean checkObj(String logicSysNo, String nodeRealId) {
		return logicSysBS.checkObj(logicSysNo, nodeRealId);
	}

	/**
	 * 获取逻辑系统已经授权对象
	 */
	@RequestMapping("/getAuthObjList.*")
	@ResponseBody
	public List<CommonTreeNode> getAuthObjList(String logicSysNo) {
		return logicSysBS.AuthObjToTree(logicSysBS.getAuthObjByLogicSysNo(logicSysNo));
	}

	/**
	 * 保存授权对象
	 */
	@RequestMapping("/saveAuthObj")
	@ResponseBody
	public String saveAuthObj(String logicSysNo, String params) {
		String[] authObjIds = StringUtils.split(params, ';');
		authObjSysRelBS.saveAuthObj(logicSysNo, authObjIds);
		return "true";
	}

	/**
	 * 进行上传时的文件接收与保存
	 */
	@RequestMapping("/startUpload")
	public String startUpload(Uploader uploader, HttpServletResponse response) throws Exception {
		File file = null;
		try {
			file = this.uploadFile(uploader, GlobalConstants4frame.LOGIC_SYS_IMPORT_PATH, false);
		} catch (Exception e) {
			logger.info("文件上传出现异常", e);
		}
		response.setContentType("text/plain; charset=UTF-8");
		if (file != null) {
			logger.info("文件[" + file.getName() + "]上传完成");
			String content = unzipFile(file);
			String message = this.logicSysBS.saveWithJsonStr(content);
			response.getOutputStream().write(message.getBytes("UTF-8"));
			return message;
		}
		return null;
	}

	/**
	 * 导入逻辑系统信息
	 * 
	 * @return
	 */
	@RequestMapping("/importsys")
	public String importsys() {
		return "/frame/logicSys/logic-sys-upload";
	}

	/**
	 * 导出逻辑系统信息
	 * 
	 * @throws IOException
	 */
	@RequestMapping("/exportsys")
	public void exportsys(HttpServletResponse response, String logicSysNo) throws Exception {
		if (StringUtils.isNotEmpty(logicSysNo)) {

			// 所有需要导出的实体全路径名
			List<String> entitiesName = this.exportEntityBS.getExportEntitiesAll();

			// 获取所有包含逻辑系统信息的数据
			Map<String, Map<String, List<Object>>> result = this.adminUserBS.getObjectListWidthLogicSysNo(entitiesName,
					logicSysNo);
			String jsonString = JSON.toJSONString(result); // 生成 json 数据
			String path = this.getRealPath() + GlobalConstants4frame.LOGIC_SYS_EXPORT_PATH; // 下载临时路径
			String fileName = logicSysNo + "_" + FormatUtils.formatDate(new Date(), "yyyyMMddHHmmss"); // 文件名
			if (FilesUtils.createDir(path)) { // 创建下载临时路径
				if (FilepathValidateUtils.validateFilepath(fileName)) {
					File file = new File(path + File.separator + fileName + ".backUp"); // 生成数据文件描述
					PrintWriter pw = null;
					try {
						if (file.createNewFile()) { // 创建数据文件
							pw = new PrintWriter(file);
							pw.write(jsonString);
							pw.flush(); // 写入信息
						}
					} finally {
						if (pw != null) {
							pw.close();
						}
					}
					if (file.exists()) {
	
						// 将文件进行 zip 压缩处理
						FilesUtils.zip(path + File.separator + fileName + ".backUp", path + File.separator + fileName
								+ ".zip");
						FilesUtils.deleteFile(file); // 压缩后, 将原文件删除
						file = new File(path + File.separator + fileName + ".zip"); // 生成压缩文件描述
						DownloadUtils.download(response, file); // 提供下载
						file.delete(); // 删除压缩文件
					}
				}
			}
		}
	}

	/*
	 * 解压缩文件
	 * 
	 * @param file
	 * 
	 * @throws Exception
	 */
	private String unzipFile(File file) throws Exception {
		List<File> files = FilesUtils.unzip(file, file.getParent() + "/"); // 解压缩
		file.delete(); // 删除压缩文件
		if (files.size() > 0) {
			int BUFFER_SIZE = 10240; // 每次读取字符数
			file = files.get(0);
			BufferedReader br = new BufferedReader(new FileReader(file), BUFFER_SIZE);
			StringBuilder content = new StringBuilder();
			int len = 0;
			char[] buffer = new char[BUFFER_SIZE];
			while ((len = br.read(buffer, 0, BUFFER_SIZE)) > 0) { // 读取解压后文件内容
				content.append(String.valueOf(buffer, 0, len));
			}
			br.close();
			file.delete(); // 删除解压后文件
			return content.toString();
		}
		return null;
	}
}
