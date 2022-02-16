package com.yusys.bione.frame.security.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authres.entity.BioneResOperInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.common.ResOperInfoHolder;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * <pre>
 * Title:权限许可Action
 * Description: 用户给前端界面提供授权许可信息
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
@RequestMapping("/bione/permission")
public class PermissionController extends BaseController {

	@Autowired
	private AuthBS authBS;


	/**
	 * 获取受系统权限控制的资源操作标识（即操作按钮）
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getProtectedResOperNo.json", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getProtectedResOperNo() {

		Map<String, Object> protectedResOperNo = Maps.newHashMap();

		Boolean success = Boolean.TRUE;

		/*
		 * 20170706 chenhx1
		 * 判断当前用户是否是管理员,如果是管理员清除权限信息,前台就不会验证权限信息
		 */
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
		    try {
	            List<String> protectedResOperNoList = ResOperInfoHolder
	                    .getResOperNoInfo(BioneSecurityUtils.getCurrentUserInfo()
	                            .getCurrentLogicSysNo());

	            protectedResOperNo.put("data", protectedResOperNoList);

	        } catch (Exception e) {
	            success = Boolean.FALSE;
	            logger.error("获取系统权资源操作标识失败.",e);
	        }
		}
		

		protectedResOperNo.put("success", success+"");

		return protectedResOperNo;
	}

	/**
	 * 获取受系统权限控制的资源操作标识（即操作按钮）
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAuthorizedResOperNo.json", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object>  getAuthorizedResOperNo() {
		
		Map<String, Object> protectedResOperNo = Maps.newHashMap();

		Boolean success = Boolean.TRUE;
		
		try{

		BioneUser currentUser = BioneSecurityUtils.getCurrentUserInfo();

		if (currentUser != null) {

			String userId = currentUser.getUserId();

			List<BioneAuthObjResRel> authObjResRelList = Lists.newArrayList();

			// 查找用户具有的权限
			List<String> objIdList = new ArrayList<String>();
			objIdList.add(userId);
			List<BioneAuthObjResRel> userResRelList = this.authBS
					.findCurrentUserAuthObjResRels(currentUser.getCurrentLogicSysNo(),
							GlobalConstants4frame.AUTH_OBJ_DEF_ID_USER, objIdList);

			if (userResRelList != null)
				authObjResRelList.addAll(userResRelList);

			// 查找当前用户是否属于某个授权组
			List<String> authGrpIdList = this.authBS
					.findAuthGroupIdOfCurrentUser(currentUser.getCurrentLogicSysNo());

			List<BioneAuthObjResRel> grpResRelList = this.authBS
					.findCurrentUserAuthObjResRels(currentUser.getCurrentLogicSysNo(),
							GlobalConstants4frame.AUTH_OBJ_DEF_ID_GROUP,
							authGrpIdList);

			if (grpResRelList != null)
				authObjResRelList.addAll(grpResRelList);

			// 当前用户授权对象的集合
			Map<String, List<String>> userAuthObjMap = currentUser
					.getAuthObjMap();
			Iterator<String> it = userAuthObjMap.keySet().iterator();

			String objDefNo = null;
			List<String> authObjIdList = null;
			while (it.hasNext()) {

				objDefNo = it.next();
				authObjIdList = userAuthObjMap.get(objDefNo);

				if (authObjIdList != null) {
					List<BioneAuthObjResRel> objDefResRelList = this.authBS
							.findCurrentUserAuthObjResRels(currentUser.getCurrentLogicSysNo(),objDefNo,
									authObjIdList);
					if (objDefResRelList != null)
						authObjResRelList.addAll(objDefResRelList);
				}
			}

			Map<String, BioneResOperInfo> resOperInfoMap = this.authBS
					.findAllResOperInfoMap();

			List<String> authorizedResOperNoList = Lists.newArrayList();

			for (BioneAuthObjResRel authObjResRel : authObjResRelList) {

				String permissionId = authObjResRel.getId().getPermissionId();
				String permissionType = authObjResRel.getId()
						.getPermissionType();

				// 操作权限
				if (GlobalConstants4frame.RES_PERMISSION_TYPE_OPER
						.equals(permissionType)) {

					BioneResOperInfo resOperInfo = resOperInfoMap
							.get(permissionId);

					if (resOperInfo != null) {
						authorizedResOperNoList.add(resOperInfo.getOperNo());
					}
				}
			}
			
			protectedResOperNo.put("data", authorizedResOperNoList);
		}
		} catch (Exception e) {

			success = Boolean.FALSE;
			logger.error("获取系统权资源操作标识失败.",e);
		}

		protectedResOperNo.put("success", success);
		return protectedResOperNo;
	}

	@ResponseBody
	@RequestMapping("/getPremissionByUrl")
	public Map<String, Object> getPremissionByUrl(String url){
		Map<String, Object> result = new HashMap<>();
		result.put("status", "success");
		if(BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			return result;
		}
		List<BioneAuthObjResRel> list = authBS.getPremissionByUrl(url);
		if(list == null || list.size() == 0){
			result.put("status", "fail");
		}
		return result;
	}

}
