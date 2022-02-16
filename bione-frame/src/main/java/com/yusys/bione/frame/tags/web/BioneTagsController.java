package com.yusys.bione.frame.tags.web;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.tags.entity.BioneTagInfo;
import com.yusys.bione.frame.tags.entity.BioneTagRelInfo;
import com.yusys.bione.frame.tags.entity.BioneTagRelInfoPK;
import com.yusys.bione.frame.tags.service.BioneTagsBS;

@Controller
@RequestMapping("/bione/tags")
public class BioneTagsController extends BaseController {
	@Autowired 
	private BioneTagsBS tagsBS;
	
	@RequestMapping("/config")
	public ModelAndView config(String id,String tagObjId) {
		ModelMap map = new ModelMap();
		map.put("id", StringUtils2.javaScriptEncode(id));
		map.put("tagObjId", StringUtils2.javaScriptEncode(tagObjId));
		return new ModelAndView("/frame/tags/tags-info-config",map);
	}
	
	/**
	 * 获取全部标签
	 * @return
	 */
	@RequestMapping(value = "/getTagsInfo",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getTagsInfo(String id,String tagObjId){
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("allTags", this.tagsBS.getTagsInfo());
		result.put("objTags", this.tagsBS.getObjTagsInfo(id,tagObjId));
		return result;
	}
	
	/**
	 * 获取最新最多标签
	 * @return
	 */
	@RequestMapping(value = "/getMLTagInfo",method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getMLTagInfo(String tagObjId){
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("latest", this.tagsBS.getLatestTagsInfo(tagObjId));
		result.put("most", this.tagsBS.getMostTagsInfo(tagObjId));
		return result;
	}
	
	@RequestMapping(value = "/saveTagRel",method = RequestMethod.POST)
	@ResponseBody
	public void saveTagRel(String tagNms,String id,String tagObjId){
		this.tagsBS.removeTagInfo(id, tagObjId, "manager");
		String tagNm[] = StringUtils.split(tagNms,",");
		for(int i=0; i<tagNm.length; i++){
			BioneTagInfo tag =this.tagsBS.getEntityByProperty(BioneTagInfo.class, "tagName", tagNm[i]);
			String tagId = "";
			if(tag==null){
				tag = new BioneTagInfo();
				tagId = RandomUtils.uuid2();
				tag.setTagId(tagId);
				tag.setTagName(tagNm[i]);
				this.tagsBS.saveOrUpdateEntity(tag);
			}
			BioneTagRelInfoPK pk = new BioneTagRelInfoPK();
			pk.setObjId(id);
			pk.setTagObjId(tagObjId);
			pk.setTagId(tag.getTagId());
			pk.setUserId("manager");
			BioneTagRelInfo rel = new BioneTagRelInfo();
			rel.setId(pk);
			rel.setCreateTime(new Timestamp(System.currentTimeMillis()));
			this.tagsBS.saveOrUpdateEntity(rel);
		}
	}
}
