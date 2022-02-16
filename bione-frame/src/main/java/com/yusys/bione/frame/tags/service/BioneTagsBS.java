package com.yusys.bione.frame.tags.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.tags.entity.BioneTagInfo;

@Service
@Transactional(readOnly = true)
public class BioneTagsBS extends BaseBS<Object>{
	
	/**
	 * 获取所有标记
	 * @return
	 */
	public List<CommonComboBoxNode> getTagsInfo(){
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		List<BioneTagInfo> tags = this.getEntityList(BioneTagInfo.class);
		if(tags != null && tags.size() > 0){
			for(BioneTagInfo tag : tags){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId("_id"+tag.getTagName());
				node.setText(tag.getTagName());
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	/**
	 * 
	 * @param id 对象Id
	 * @param tagObjId 对象类型Id
	 * @return
	 */
	public List<CommonComboBoxNode> getObjTagsInfo(String id,String tagObjId){
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		String jql = "select distinct tag from BioneTagInfo tag ,BioneTagRelInfo rel where rel.id.objId = ?0 and rel.id.tagObjId = ?1 and rel.id.tagId = tag.tagId ";
		List<BioneTagInfo> tags = this.baseDAO.findWithIndexParam(jql, id, tagObjId);
		if(tags != null && tags.size() > 0){
			for(BioneTagInfo tag : tags){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId("_id"+tag.getTagName());
				node.setText(tag.getTagName());
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	/**
	 * 获取最新标记
	 * @param tagObjId
	 * @return
	 */
	public List<CommonComboBoxNode> getLatestTagsInfo(String tagObjId){
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		String jql = "select tag from BioneTagInfo tag ,BioneTagRelInfo rel where rel.id.tagObjId = ?0 and rel.id.tagId = tag.tagId group by tag.tagId,tag.tagName,tag.remark order by max(rel.createTime) desc";
		List<BioneTagInfo> tags = this.baseDAO.findWithIndexParam(jql, tagObjId);
		if(tags != null && tags.size() > 0){
			for(int i=0;i<((tags.size()<10)?tags.size():10);i++){
				BioneTagInfo tag = tags.get(i);
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId("_id"+tag.getTagName());
				node.setText(tag.getTagName());
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	/**
	 * 获取最多标记
	 * @param tagObjId
	 * @return
	 */
	public List<CommonComboBoxNode> getMostTagsInfo(String tagObjId){
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		String jql = "select tag from BioneTagInfo tag ,BioneTagRelInfo rel where rel.id.tagObjId = ?0 and rel.id.tagId = tag.tagId group by tag.tagId,tag.tagName,tag.remark order by count(tag.tagId) desc";
		List<BioneTagInfo> tags = this.baseDAO.findWithIndexParam(jql, tagObjId);
		if(tags != null && tags.size() > 0){
			for(int i=0;i<((tags.size()<10)?tags.size():10);i++){
				BioneTagInfo tag = tags.get(i);
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId("_id"+tag.getTagName());
				node.setText(tag.getTagName());
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	@Transactional(readOnly = false)
	public void removeTagInfo(String id,String tagObjId,String userId){
		String jql = "delete from BioneTagRelInfo rel where rel.id.objId = ?0 and rel.id.tagObjId = ?1 and rel.id.userId = ?2";
		this.baseDAO.batchExecuteWithIndexParam(jql, id,tagObjId,userId);
	}
}
