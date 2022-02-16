package com.yusys.bione.frame.authobj.web;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.auth.service.AuthObjBS;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.authobj.entity.BionePosiInfo;
import com.yusys.bione.frame.authobj.service.PosiInfoBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;


@Controller
@RequestMapping("/bione/admin/posi")
public class PosiController {
	
	@Autowired
	private PosiInfoBS posiInfoBS;
	
	@Autowired
	private AuthBS authBS;
	@Autowired
	private AuthObjBS authObjBS;
	
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/posi/posi-index";
	}
	
	/**
	 * 跳转新增页面
	 * @param orgId
	 * @return
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView editNew(String orgId) {
		orgId = StringUtils2.javaScriptEncode(orgId);
		return new ModelAndView("/frame/posi/posi-editNew", "orgId", orgId);
	}

	/**
	 * 跳转 修改 页面
	 * @param posiId
	 * @return
	 */
	@RequestMapping(value = "/{posiId}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("posiId") String posiId) {
		posiId = StringUtils2.javaScriptEncode(posiId);
		return new ModelAndView("/frame/posi/posi-edit", "id", posiId);
	}
	
	@RequestMapping(value="/updatePosiFlag",method=RequestMethod.POST)
	public void updatePosiFlag(String posiId,String posiSts){
		BionePosiInfo posi=this.posiInfoBS.getEntityById(posiId);
		if("1".equals(posiSts)){
			posi.setPosiSts("0");
		}else{
			posi.setPosiSts("1");
		}
		posi.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
		posi.setLastUpdateUser(BioneSecurityUtils.getCurrentUserId());
		this.posiInfoBS.updateEntity(posi);
	}
	/**
	 * 根据编号显示岗位信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/showInfo.*", method = RequestMethod.GET)
	@ResponseBody
	public BionePosiInfo showInfo(String id) {
		BionePosiInfo model = this.posiInfoBS.getEntityById(id);
		return model;
	}
	/**
	 * 新增或修改
	 * @param model
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> create(BionePosiInfo model) {
		Map<String,Object> res = new HashMap<String, Object>();
		String string = null;
		try{
			this.posiInfoBS.create(model);
		}catch(RptException e){
			e.printStackTrace();
			string = "1";
		}
		res.put("msg", string);
		return res;
		
	}
	/**
	 * 通过机构编号查找岗位
	 * @param pager
	 * @param orgNo
	 * @return
	 */
	@RequestMapping(value = "/findPosiInfoByOrg.*",method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> findPosiInfoByOrg(Pager pager, String orgNo) {
		
		SearchResult<BionePosiInfo> list = posiInfoBS.findPosiInfoByOrg(orgNo, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(),
				pager.getPageFirstIndex(),pager.getPagesize(),pager.getSortname(),pager.getSortorder(),pager.getSearchCondition());
		Map<String, Object> posiMap = Maps.newHashMap();
		posiMap.put("Rows", list.getResult());
		posiMap.put("Total",list.getTotalCount());
		return posiMap;
	}
	/**
	 * 新增时验证该机构下岗位标识是否存在
	 * @param orgNo
	 * @param posiNo
	 * @return
	 */
	@RequestMapping(value = "/testPosiNo")
	@ResponseBody
	public boolean testDeptNo(String posiNo) {
		BionePosiInfo posi = posiInfoBS.findPosiInfoByOrgNoandPosiNo( posiNo);
		boolean flag = true;
		if (posi != null) {
			flag = false;
		}
		return flag;
	}
	/**
	 * 删除
	 * @param id
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,String> destroy(@PathVariable("id") String ids) {
		//this.deptBS.removeEntityById(id);
		Map<String,String> msgMap=Maps.newHashMap();
		if(ids!=null&&!"".equals(ids)){
			String[] idArray = StringUtils.split(ids, ',');
			
			//首先判断选中的角色信息是否与资源信息关联，或者包含用户。只要有一个角色信息有这种情况，全部打回不允许删除。
			//--author huangye
			boolean flag=false;
			for(String id:idArray){
				boolean flags=this.authBS.checkIsObjBeUsedByUser(id, GlobalConstants4frame.AUTH_OBJ_DEF_ID_POSI);
				if(flags){
					flag=true;
					break;
				}
			}
			if(flag){
				msgMap.put("msg","1");
				return msgMap;
			}
			for(String id:idArray){
				boolean flags=this.authObjBS.checkIsObjBeUsedByRes(id, GlobalConstants4frame.AUTH_OBJ_DEF_ID_POSI);
				if(flags){
					flag=true;
					break;
				}
			}
			if(flag){
				msgMap.put("msg", "2");
				return msgMap;
			}
			try {
				this.posiInfoBS.removeEntityBatch(ids);
				msgMap.put("msg", "0");
			} catch (RptException e) {
				e.printStackTrace();
			}
		}
		return msgMap;
	}
	
	@RequestMapping(value = "/getRealCode.*",method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> getRealCode(String orgId) {
		String orgIdTmp = orgId;
		String realOrgCode = "";
		Map<String, String> returnMap = new HashMap<String, String>();
		if (orgIdTmp != null && !"".equals(orgIdTmp)) {
			BioneOrgInfo orgTmp = this.posiInfoBS.getEntityById(BioneOrgInfo.class, orgIdTmp);
			if (orgTmp != null) {
				realOrgCode = orgTmp.getOrgNo();
			}
		}
		
		returnMap.put("orgNo", realOrgCode);
		return returnMap;
	}

}
