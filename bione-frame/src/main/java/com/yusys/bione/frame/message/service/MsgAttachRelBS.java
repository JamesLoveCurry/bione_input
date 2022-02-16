package com.yusys.bione.frame.message.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.message.entity.BioneMsgAttachRel;

/**
 * <pre>
 * Title: 消息模块-附件服务
 * Description: 消息模块-附件服务
 * </pre>
 * 
 * @author fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class MsgAttachRelBS extends BaseBS<BioneMsgAttachRel> {


	/**
	 * 批量删除附件-公告关系
	 * 
	 * @param ids
	 */
	@Transactional(readOnly = false)
	public void deleteBatch(String[] ids) {
		for (String id : ids) {
			removeEntityById(id);
		}
	}
	
	/**
	 * 批量保存公告-附件关系
	 * @param entitys
	 */
	@Transactional(readOnly = false)
	public void saveBatch(List<BioneMsgAttachRel> entitys) {
		for (BioneMsgAttachRel entity : entitys) {
			this.saveEntity(entity);
		}
	}

}
