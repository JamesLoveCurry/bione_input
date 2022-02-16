package com.yusys.biapp.input.input.web;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.input.service.InputTaskBS;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * @author lujs1 数据补录任务进度查询
 */
@Controller
@RequestMapping("/rpt/input/taskProcess")
public class InputTaskProcessController extends BaseController {
	protected static Logger log = LoggerFactory.getLogger(InputTaskProcessController.class);

	@Autowired
	private InputTaskBS udipTaskBS;
	
	
	//	/**
//	 * 初始化方法
//	 */
//	@RequestMapping(method = RequestMethod.GET)
//	public String index() {
//		return "/udip/task/input-task-process-index";
//	}
//
//	/**
//	 * @param taskId
//	 * @param caseId
//	 * @param orgId
//	 * @param templeId
//	 * @return
//	 */
//	@RequestMapping(value = "/sendMail", method = RequestMethod.GET)
//	public ModelAndView sendMail(String taskId, String caseId, String orgId, String templeId) {
//		ModelAndView mav = new ModelAndView();
//		mav.setViewName("udip/task/task-process-sendMail");// 要转向的页面
//		mav.addObject("taskId", taskId);
//		mav.addObject("orgId", orgId);
//		mav.addObject("caseId", caseId);
//		mav.addObject("templeId", templeId);
//		return mav;
//	}
//
//	/**
//	 * 
//	 * @return
//	 */
//	@RequestMapping(value = "/mailUserList", method = RequestMethod.GET)
//	public ModelAndView mailUserList() {
//		ModelAndView mav = new ModelAndView();
//		mav.setViewName("udip/task/task-mail-user-list");// 要转向的页面
//		return mav;
//	}
//	
//	/**
//	 * 跳转到补录任务统计
//	 * @return
//	 */
//	@RequestMapping(value = "/count", method = RequestMethod.GET)
//	public ModelAndView taskCount() {
//		ModelAndView mav = new ModelAndView();
//		mav.setViewName("udip/task/input-task-process-count");// 要转向的页面
//		return mav;
//	}
//
//	/**
//	 * 跳转到补录任务统计详细查看页面
//	 * @return
//	 */
//	@RequestMapping(value = "/viewCountDetail", method = RequestMethod.GET)
//	public ModelAndView viewCountDetail(String upTaskId, String taskId, String taskId1) {
//		String[] taskIds = taskId.split("-");
//		ModelAndView mav = new ModelAndView();
//		mav.addObject("upTaskId", upTaskId);
//		mav.addObject("taskId", taskIds[0]);
//		mav.addObject("taskId1", taskId1);
//		mav.setViewName("udip/task/input-task-count-detail");// 要转向的页面
//		return mav;
//	}
//
//	/**
//	 * 根据任务ID获取邮件组
//	 * @param id
//	 * @return
//	 */
//	@RequestMapping(value = "/findMailGroup")
//	@ResponseBody
//	public UdipMailGroupInfo findMailGroup(String id) {
//		UdipMailGroupInfo model = udipTaskProcessBS.getEntityById(UdipMailGroupInfo.class, id);
//		return model;
//	}
//
//	/**
//	 * 获取补录任务实例树
//	 */
//	@RequestMapping("/list.*")
//	@ResponseBody
//	public List<CommonTreeNode> list(String allOrSomeMission) {
//		String logicSysNo = BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
//		String userName = BiOneSecurityUtils.getCurrentUserInfo().getLoginName();
//		List<String> roles = BiOneSecurityUtils.getCurrentUserInfo().getRoleList();
//		List<String> roleType = Lists.newArrayList();
////		roleType.add(UdipConstants.ROLE_TYPE_MANAGE);
////		roleType.add(UdipConstants.ROLE_TYPE_VIEW);
//		return udipTaskBS.buildTaskTreeWithCase(userName, logicSysNo, roles, roleType, true, true, allOrSomeMission,null);
//	}
	/**
	 * 
	 * @param allOrSomeMission
	 * @param caseId
	 * @param click
	 * @return
	 */
	@RequestMapping("/notemple/list.*")
	@ResponseBody
	public List<CommonTreeNode> notemplelist(String allOrSomeMission,String caseId,String click) {
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		String userName = BioneSecurityUtils.getCurrentUserInfo().getLoginName();
		List<String> roles = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE);
		List<String> roleType = Lists.newArrayList();
		//roleType.add(UdipConstants.ROLE_TYPE_MANAGE);
		roleType.add(UdipConstants.ROLE_TYPE_INPUT);
		if(StringUtils.isNotBlank(click)){//如果是首页双击进入，那么取全部
			return udipTaskBS.buildTaskTreeWithCase(userName, logicSysNo, roles, roleType, false, false, allOrSomeMission,null);
		}else{//如果是首页点击统计数进入，那么按ID取
			return udipTaskBS.buildTaskTreeWithCase(userName, logicSysNo, roles, roleType, false, false, allOrSomeMission,caseId);
		}
		
	}
//
//	/**
//	 * 获取补录实例进度列表
//	 * @param pager
//	 * @param taskId
//	 * @return
//	 */
//	@RequestMapping("/query.*")
//	@ResponseBody
//	public Map<String, Object> query(Pager pager, String caseId, String taskId, String templeId, String orgIds) {
//		SearchResult<UdipTaskProcessVO> searchResult = this.udipTaskProcessBS.getInputTaskProcessList(orgIds, caseId, taskId, templeId);
//
//		Map<String, Object> objDefMap = Maps.newHashMap();
//		objDefMap.put("Rows", searchResult.getResult());
//		objDefMap.put("Total", searchResult.getTotalCount());
//		return objDefMap;
//	}
//
//	// 查找机构树
//	@RequestMapping("/orgTreeForTask")
//	public ModelAndView orgTreeForTask() {
//		ModelAndView mav = new ModelAndView();
//		mav.setViewName("udip/task/org-tree");// 要转向的页面
//		return mav;
//	}
//
//	/**
//	 * 获取所有机构
//	 */
//	@RequestMapping("/getAllOrgInfo.*")
//	@ResponseBody
//	public Map<String, Object> getAllOrgInfo(Pager rf) {
//		List<orgNodeVO> list = udipTaskProcessBS.getOrgData(rf.getSearchCondition(), (rf.getPage() - 1) * rf.getPagesize(), rf.getPagesize());
//		Map<String, Object> objDefMap = Maps.newHashMap();
//		int index = (rf.getPage() - 1) * rf.getPagesize();
//		int pageEnd = (rf.getPage() - 1) * rf.getPagesize() + rf.getPagesize();
//		if (rf.getCondition() != null && !rf.getCondition().equals("")) {// 不是初始化或者搜索全部
//			index = 0;
//			if (list.size() > rf.getPagesize()) {
//				pageEnd = (rf.getPage() - 1) * rf.getPagesize() + rf.getPagesize();
//			} else {
//				pageEnd = list.size();
//			}
//
//		} else {
//			if (list.size() <= index) {
//				index = list.size();
//			}
//			if (list.size() <= pageEnd) {
//				pageEnd = list.size();
//			}
//
//		}
//		List<orgNodeVO> subList = new ArrayList<orgNodeVO>();
//		if (list.size() > 0) {
//			subList = list.subList(index, pageEnd);
//		}
//		objDefMap.put("Rows", subList);
//		objDefMap.put("Total", list.size());
//		return objDefMap;
//	}
//
//	/**
//	 * 获取统计信息
//	 * @param pager
//	 * @return
//	 */
//	@RequestMapping("/countlist.*")
//	@ResponseBody
//	public Map<String, Object> getCountList(Pager pager) {
//		String logicSysNo = BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
//		String userName = BiOneSecurityUtils.getCurrentUserInfo().getLoginName();
//		String orgCode = BiOneSecurityUtils.getCurrentUserInfo().getOrgNo();
//
//		List<String> roles = BiOneSecurityUtils.getCurrentUserInfo().getRoleList();
//		List<String> roleType = Lists.newArrayList();
////		roleType.add(UdipConstants.ROLE_TYPE_MANAGE);
////		roleType.add(UdipConstants.ROLE_TYPE_VIEW);
//		SearchResult<UdipTaskStateVO> searchResult = this.udipTaskProcessBS.getInputTaskStateList(pager.getPageFirstIndex(), pager.getPagesize(), pager.getSearchCondition(), userName, logicSysNo, roles, roleType, orgCode);
//		Map<String, Object> objDefMap = Maps.newHashMap();
//		objDefMap.put("Rows", searchResult.getResult());
//		objDefMap.put("Total", searchResult.getTotalCount());
//		return objDefMap;
//	}
//
//	/**
//	 * 跳转到任务实例统计
//	 * @return
//	 */
//	@RequestMapping(value = "/tempCount", method = RequestMethod.GET)
//	public ModelAndView tempCount() {
//		ModelAndView mav = new ModelAndView();
//		mav.setViewName("udip/task/input-case-process-count");// 要转向的页面
//		return mav;
//	}
//
//	/**
//	 * 按照任务实例统计
//	 * @param pager
//	 * @return
//	 */
//	@RequestMapping("/getTempList.*")
//	@ResponseBody
//	public Map<String, Object> getTempList(Pager pager, String taskId, String caseId) {
//		SearchResult<UdipTaskCountVO> searchResult = this.udipTaskProcessBS.getInputTempCountList(taskId, caseId);
//		Map<String, Object> objDefMap = Maps.newHashMap();
//		objDefMap.put("Rows", searchResult.getResult());
//		objDefMap.put("Total", searchResult.getTotalCount());
//		return objDefMap;
//	}
//
//	/**
//	 * 获取统计详细信息
//	 * @param pager
//	 * @return
//	 */
//	@RequestMapping(value = "/countDetailList", method = RequestMethod.GET)
//	public Map<String, Object> countDetailList(String upTaskId, String taskId, String taskId1) {
//		SearchResult<UdipTaskProcessVO> searchResult = this.udipTaskProcessBS.getInputTaskCountDetails(upTaskId, taskId, taskId1);
//		Map<String, Object> objDefMap = Maps.newHashMap();
//		objDefMap.put("Rows", searchResult.getResult());
//		objDefMap.put("Total", searchResult.getTotalCount());
//		return objDefMap;
//	}
//
//	/**
//	 * 接收参数发送邮件
//	 * @param taskId
//	 * @param orgId
//	 * @param caseId
//	 * @param templeId
//	 * @param mailTitle
//	 * @param remark
//	 * @param mailContent
//	 * @param mailCopierCode
//	 * @param roleUser
//	 * @param editAddr
//	 * @param send
//	 * @return
//	 */
//	@RequestMapping(value = "/task-process-sendMails", method = RequestMethod.POST)
//	@ResponseBody
//	public Map<String,List<UdipMailVO>> sendMailFinal(String taskId, String orgId, String caseId, String templeId, String mailTitle, String remark, String mailContent, String mailCopierCode,String roleUser, String editAddr,boolean send) {
//		
//		Map<String,List<UdipMailVO>> map = Maps.newHashMap();
//		
//		
//		if (StringUtils.isNotBlank(editAddr)) {// 如果是用户自己编辑的地址,就直接发送
//			this.sendMailEditAddr(taskId, caseId, templeId, mailTitle, mailContent, remark, editAddr);
//			return map;
//		}
//		
//		List<UdipMailVO> sendList = Lists.newArrayList();
//		List<UdipMailVO> copyList = Lists.newArrayList();
//		Map<String, Collection<String>> roleUserMap = new HashMap<String, Collection<String>>();
//		
//		try {
//			if(StringUtils.isNotBlank(roleUser)){
//				String roles[] = roleUser.split(split);
//				for (String role : roles) {
//					String rn[] = role.split(":");
//					roleUserMap.put(rn[0], ArrayUtils.asCollection(rn[1]));
//				}
//			}
//			
//			if (StringUtils.isNotBlank(taskId)) {
//				List<String> orgs = new ArrayList<String>();// 所选发送邮件的机构[0799V,0798V]
//				if (orgId != null) {
//					if (!orgId.equals("") && !orgId.equals("AllOrgs")) {
//						String[] orgCodes = orgId.split(",");
//						for (int i = 0; i < orgCodes.length; i++) {
//							orgs.add(orgCodes[i]);
//						}
//					} else if (orgId.equals("AllOrgs")) {// 批量发送邮件
//						String userOrg = BiOneSecurityUtils.getCurrentUserInfo().getOrgNo();// 获取用户所属机构
//						String logicSysNo = BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
//						
//						List<CommonTreeNode> orgsys = udipTaskProcessBS.getAuthObjectUtils().getOrgList(logicSysNo);// 获取当前逻辑系统的所有机构信息
//						List<UdipOrgInfo> orgss = udipTaskProcessBS.getChildListByCode(userOrg, orgsys);// 获取用户及其下级机构集合
//						List<UdipCaseOrg> orgList = udipTaskProcessBS.getDispatchOrg(caseId);// 补录任务对应的下发机构
//						
//						for (int i = 0; i < orgList.size(); i++) {// 从下发机构的集合中选取用户所能看到的机构
//							UdipCaseOrg tp = (UdipCaseOrg) orgList.get(i);
//							for (int j = 0; j < orgss.size(); j++) {
//								UdipOrgInfo vo = (UdipOrgInfo) orgss.get(j);
//								if (vo.getOrgCode().equals(tp.getOrgCode())&&!orgs.contains(tp.getOrgCode())) {
//									orgs.add(tp.getOrgCode());
//									break;
//								}
//							}
//						}
//					}
//				}
//				
//				UdipInputTaskInfo task = udipTaskBS.getEntityById(taskId);// 获取任务实体类
//				UdipTaskCaseInfo taskCase = udipTaskBS.getEntityById(UdipTaskCaseInfo.class, caseId);// 获取任务实例
//				List<UdipDataStateInfo> stateList = udipTaskBS.findByPropertys(UdipDataStateInfo.class, new String[] { "caseId", "templeId" }, new Object[] { caseId, templeId });
//				List<UdipTaskRole> tRoleList = udipTaskProcessBS.findByPropertys(UdipTaskRole.class, new String[] { "taskId" }, new Object[] { taskId });// 获取任务对应的角色
//
//				// 找到邮件组
//				UdipMailGroupInfo group = udipTaskProcessBS.getEntityById(UdipMailGroupInfo.class, task.getMailId());
//				UdipMailSrvInfo server = udipTaskProcessBS.getEntityById(UdipMailSrvInfo.class, group.getServerId());
//
//				Map<String, List<UdipDataStateInfo>> stateMap = Maps.newHashMap();
//				Map<String, List<UdipTaskRole>> roleMap = Maps.newHashMap();
//
//				for (UdipDataStateInfo info : stateList) {
//					if (!stateMap.containsKey(info.getOrgCode())) {
//						stateMap.put(info.getOrgCode(), new ArrayList<UdipDataStateInfo>());
//					}
//					stateMap.get(info.getOrgCode()).add(info);
//				}
//
//				for (UdipTaskRole info : tRoleList) {
//					if (!roleMap.containsKey(info.getRoleType())) {
//						roleMap.put(info.getRoleType(), new ArrayList<UdipTaskRole>());
//					}
//					roleMap.get(info.getRoleType()).add(info);
//				}
//
//				List<CommonTreeNode> userList = authObjectUtils.getUserList(task.getLogicSysNo());
//				List<CommonTreeNode> orgList = authObjectUtils.getOrgList(task.getLogicSysNo());// 获取当前逻辑系统的所有机构信息
//				
//				Collection<String> addr = new HashSet<String>();
//				Collection<String> addr_copy = new HashSet<String>();
//				
//				// 循环获取每个下发机构的任务状态，判断任务状态来决定类型：补录或者是审核
//				for (String orgCode : orgs) {
//					List<UdipDataStateInfo> caseList = stateMap.get(orgCode);
//
//					if (caseList != null && caseList.size() > 0) {
//						List<String> orgs1 = new ArrayList<String>();
//						orgs1.add(orgCode);
//						UdipDataStateInfo aro = caseList.get(0);
//						String roleType = UdipConstants.ROLE_TYPE_INPUT;// 默认为补录角色
//
//						if (aro.getDataState().equals(UdipConstants.TASK_STATE_DISPATCH) || aro.getDataState().equals(UdipConstants.TASK_STATE_SAVE) || aro.getDataState().equals(UdipConstants.TASK_STATE_REFUSE)) {// 如果是处于下发、保存或者回退的状态，都是催办补录
//							roleType = UdipConstants.ROLE_TYPE_INPUT;
//						} else if (aro.getDataState().equals(UdipConstants.TASK_STATE_SUBMIT)) {// 如果是处于提交状态，则是催办审核
//							roleType = UdipConstants.ROLE_TYPE_AUTH;
//						}
//
//						/* 发送邮件给接收人和抄送人 */
//						addr.addAll(udipTaskProcessBS.getMailAddsByRole(orgCode,task.getLogicSysNo(), orgs1, roleMap.get(roleType), userList,sendList));// 获取接收人的地址
//						addr_copy.addAll(udipTaskProcessBS.getMailAddsByRoles(orgCode,task.getLogicSysNo(), orgs1, mailCopierCode, userList, orgList,copyList,roleUserMap));// 获取抄送人地址
//					}
//				}
//				
//				try{
//					if(StringUtils.isNotBlank(mailContent)){
//						mailContent = SysFunction.replaceALL(mailContent, taskCase.getDispatchDate(), taskCase.getEndDate());
//					}
//					if(!send)
//						sendMails(server, addr, addr_copy, mailTitle, remark, mailContent);
//				}catch (Exception e) {
//					log.error("邮件发送失败!", e);
//				}
//			}
//		} catch (ServiceException e) {
//			log.error("邮件发送失败!", e);
//		} finally {
//			map.put("sendList", sendList);
//			map.put("copyList", copyList);
//			UdipInputTaskInfo udipInputTaskInfo = udipTaskProcessBS.getEntityByProperty(UdipInputTaskInfo.class, "taskId", taskId);
//			logBS.addLog(this.getRequest().getRemoteAddr(), BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BiOneSecurityUtils.getCurrentUserInfo().getUserId(), "邮件催办补录任务【" + udipInputTaskInfo.getTaskName() + "】");
//		}
//		return map ;
//	}
//
//	/**
//	 * 按照用户编辑的地址来发送邮件.
//	 * @param taskId
//	 * @param caseId
//	 * @param templeId
//	 * @param mailTitle
//	 * @param mailContent
//	 * @param remark
//	 * @param editAddr
//	 */
//	public void sendMailEditAddr(String taskId, String caseId, String templeId, String mailTitle, String mailContent,String remark, String editAddr) {
//		
//		try{
//			UdipInputTaskInfo task = udipTaskBS.getEntityById(taskId);// 获取任务实体类
//			UdipTaskCaseInfo taskCase = udipTaskBS.getEntityById(UdipTaskCaseInfo.class, caseId);// 获取任务实例
//			UdipMailGroupInfo group = udipTaskProcessBS.getEntityById(UdipMailGroupInfo.class, task.getMailId());// 找到邮件组
//			UdipMailSrvInfo server = udipTaskProcessBS.getEntityById(UdipMailSrvInfo.class, group.getServerId());
//	
//			String[] rows = editAddr.split(split);
//			Collection<String> addr=new HashSet<String>();
//			Collection<String> addr_copy=new HashSet<String>();
//			
//			for(String row:rows){
//				String data[]=row.split("##");
//				String copy = StringUtils.EMPTY;
//				if(data.length==3)
//					copy = data[2];
//				addr.addAll(ArrayUtils.asCollection(data[1]));
//				addr_copy.addAll(ArrayUtils.asCollection(copy));
//			}
//			if(StringUtils.isNotBlank(mailContent)){
//				mailContent = SysFunction.replaceALL(mailContent, taskCase.getDispatchDate(), taskCase.getEndDate());
//			}
//			sendMails(server,addr, addr_copy, mailTitle, remark, mailContent);
//		}catch (Exception e) {
//			log.error("邮件发送失败!", e);
//		}
//		UdipInputTaskInfo udipInputTaskInfo = udipTaskProcessBS.getEntityByProperty(UdipInputTaskInfo.class, "taskId", taskId);
//		logBS.addLog(this.getRequest().getRemoteAddr(), BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BiOneSecurityUtils.getCurrentUserInfo().getUserId(), "邮件催办补录任务【" + udipInputTaskInfo.getTaskName() + "】");
//	}
//	
//	@RequestMapping(value = "/mailUserList.*")
//	@ResponseBody
//	public Map<String,List<UdipMailVO>> mailUserList(String taskId, String orgId, String caseId, String templeId,String mailCopierCode,String roleUser) {
//		Map<String,List<UdipMailVO>> map = this.sendMailFinal(taskId, orgId, caseId, templeId, null, null, null, mailCopierCode, roleUser,null, true);
//		List<UdipMailVO> sendList = map.get("sendList");
//		List<UdipMailVO> copyList = map.get("copyList");
//		
//		for(UdipMailVO send:sendList){
//			List<String> copyU = Lists.newArrayList();
//			List<String> copyA = Lists.newArrayList();
//			for(UdipMailVO copy:copyList){
//				if(send.getOrgCode().equals(copy.getOrgCode())){
//					copyU.add(copy.getUserName());
//					copyA.add(copy.getMailAddr());
//				}
//			}
//			send.setCopyUserName(ArrayUtils.toString(copyU));
//			send.setCopyMailAddr(ArrayUtils.toString(copyA));
//		}
//		return map;
//	}
//	
//	/**
//	 * 邮件发送
//	 * @param server
//	 * @param addr
//	 * @param addr_copy
//	 * @param mailTitle
//	 * @param remark
//	 * @param mailContent
//	 * @throws Exception
//	 */
//	private void sendMails(UdipMailSrvInfo server, Collection<String> addr, Collection<String> addr_copy, String mailTitle, String remark, String mailContent) throws Exception {
//		if (!addr.isEmpty()) {
//			MailContext mailContext = MailContext.getMailContext(server.getMailClass());
//			Mail mail = new Mail();
//			mail.setSubject(mailTitle + " 紧急情况【" + remark + "】");
//			mail.setContent(mailContent);
//			mail.setFromer(server.getFromAddr());
//			mail.setReceiver(addr.toArray(new String[addr.size()]));// 设置接收人
//			
//			if (addr_copy != null && !addr_copy.isEmpty()) {// 设置抄送人
//				mail.setCc(addr_copy.toArray(new String[addr_copy.size()]));
//			}
//			mailContext.sendMail(server, mail);
//		}
//	}
//
//	@RequestMapping(value = "/findTaskById")
//	@ResponseBody
//	public UdipInputTaskInfo findTaskById(String id) {
//		UdipInputTaskInfo model = udipTaskBS.getEntityById(UdipInputTaskInfo.class, id);
//		return model;
//	}
//
//	// 生成excel文件
//	@RequestMapping(value = "/excel_down_checked")
//	@ResponseBody
//	public void excelDownChecked(HttpServletResponse response, String taskIds, String types) {
//
//	}
//
//	// 下载excel文件
//	@RequestMapping(value = "/excel_down_all")
//	public void excelDownAll(HttpServletResponse response, String taskId, String caseId) {
//
//		List<UdipTaskCountVO> list = Lists.newArrayList();
//
//		UdipTaskCountVO udipTaskCountVO = new UdipTaskCountVO(); // 模板VO
//		udipTaskCountVO.setTaskName("模板名称");
//		udipTaskCountVO.setCreateDate("下发日期");
//		udipTaskCountVO.setDispatchs("下发机构数");
//		udipTaskCountVO.setSaves("已保存");
//		udipTaskCountVO.setSumbits("已提交");
//		udipTaskCountVO.setSuccess("审核通过");
//		udipTaskCountVO.setRefuses("审核不通过");
//		list.add(list.size(), udipTaskCountVO);
//
//		SearchResult<UdipTaskCountVO> searchResult = this.udipTaskProcessBS.getInputTempCountList(taskId, caseId);
//		File file = null;
//		try {
//			list.addAll(searchResult.getResult());
//			file = smbFileUpAndDown.XlsDownload(list, "补录任务统计");
//			DownloadUtils.download(response, file, "补录任务统计.xls");
//		} catch (Exception e) {
//			log.error(e.toString());
//		} finally {
//			if (file != null)
//				file.delete();
//		}
//		// 记录日志
//		logBS.addLog(this.getRequest().getRemoteAddr(), BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), BiOneSecurityUtils.getCurrentUserInfo().getUserId(), "下载任务统计");
//	}
//	
//	@RequestMapping(value = "/getOrgTree", method = RequestMethod.GET)
//	public ModelAndView getOrgTree(String caseId) {
//		return new ModelAndView("/udip/utils/org-tree","caseId",caseId);
//	}
//	
//	@RequestMapping(value = "/openUserList", method = RequestMethod.GET)
//	public ModelAndView openUserList(String flag) {
//		return new ModelAndView("/udip/utils/user-list", "flag", flag);
//	}
//	
//	/**
//	 * 获取所有用户的列表
//	 */
//	@RequestMapping("/getUserList")
//	@ResponseBody
//	public Map<String,Object> getUserList() {
//		List<UdipUserInfo> userLists=Lists.newArrayList();
//		List<CommonTreeNode> userList = authObjectUtils.getUserList(BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
//		for(CommonTreeNode node:userList){
//			UdipUserInfo bean = (UdipUserInfo)node.getData();
//			userLists.add(bean);
//		}
//		Map<String,Object> userMaps = Maps.newHashMap();
//		userMaps.put("Rows", userLists);
//		userMaps.put("Total", userLists.size());
//		return userMaps;
//	}
//	
//	/**
//	 * 根据机构代码获取机构号
//	 * @param orgCode
//	 * @return
//	 */
//	@RequestMapping("/getOrgNameByCode")
//	@ResponseBody
//	public String getOrgNameByCode(String orgCode){
//		String logicsysno = BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
//		List<CommonTreeNode> orgList = authObjectUtils.getOrgList(logicsysno);
//		if (orgList != null) {
//			for (CommonTreeNode node : orgList) {
//				if(StringUtils.isNotBlank(node.getId()) && node.getId().equals(orgCode)){
//					return node.getText();
//				}
//			}
//		}
//		return "";
//	}
}
