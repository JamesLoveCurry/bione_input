package com.yusys.bione.frame.label.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.label.entity.BioneLabelObjInfo;
import com.yusys.bione.frame.label.entity.BioneLabelObjRel;
import com.yusys.bione.frame.label.entity.BioneLabelObjRelPK;
/**
 * <pre>
 * Title: 标签与对象关系
 * Description:
 * </pre>
 * 
 * @author kangligong kanglg@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：		  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class LabelObjRelBS extends BaseBS<BioneLabelObjRel> {
	/**
	 * 添加对象与标签关系
	 * 
	 * @param objId
	 * @param labelId
	 */
	public void addRel(String objId, List<String> labelIds) {
		for (Iterator<String> iterator = labelIds.iterator(); iterator.hasNext();) {
			String labelId = iterator.next();
			BioneLabelObjRel rel = new BioneLabelObjRel();
			BioneLabelObjRelPK relPK = new BioneLabelObjRelPK();
			relPK.setLabelId(labelId);
			relPK.setObjId(objId);
			BioneLabelObjInfo objInfo = this.getObjByLabel(labelId);
			relPK.setLabelObjId(objInfo.getLabelObjId());
			rel.setId(relPK);
			this.saveEntity(rel);
		}
	}

	/**
	 * 通过标签获取标签对象
	 * 
	 * @param labelId
	 * @return
	 */
	private BioneLabelObjInfo getObjByLabel(String labelId) {
		String jql = "select t1 from BioneLabelObjInfo t1, BioneLabelTypeInfo t2, BioneLabelInfo t3 where t1.labelObjId=t2.labelObjId and t2.typeId=t3.typeId and t3.labelId=?0";
		List<BioneLabelObjInfo> infoList = baseDAO.findWithIndexParam(jql,
				labelId);
		if (infoList != null && infoList.size() > 0) {
			return infoList.get(0);
		}
		return null;
	}


}
