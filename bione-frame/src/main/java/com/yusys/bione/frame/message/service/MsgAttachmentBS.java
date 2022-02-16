package com.yusys.bione.frame.message.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.message.entity.BioneMsgAttachInfo;

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
public class MsgAttachmentBS extends BaseBS<BioneMsgAttachInfo> {

	/**edit by fangjuan 2014-02-12
	 * 查询与某一条消息相关的附件
	 * 
	 * @param msgId
	 *            消息ID
	 * @return
	 */
	public List<BioneMsgAttachInfo> getAttachList(String msgId) {
		String jql = "select info from BioneMsgAttachInfo info, BioneMsgAttachRel rel where rel.id.attachId = info.attachId and rel.id.msgId=?0";
//		List<BioneMsgAttachInfo> msgAttachList = this.baseDAO
//				.findByProperty(BioneMsgAttachInfo.class, "attachId", msgId);
		List<BioneMsgAttachInfo> msgAttachList = this.baseDAO.findWithIndexParam(jql, msgId);
		return msgAttachList;
	}
	
	/**edit by hubing 2014-02-12
	 * 查询与某一条消息相关的附件
	 * 
	 * @param msgId
	 *            消息ID
	 * @return
	 */
	public List<BioneMsgAttachInfo> getLogAttachList(String msgId) {
		String jql = "select info from BioneMsgAttachInfo info, BioneLogAttachRel rel where rel.id.msgId=?0 and rel.id.attachId = info.attachId ";
		List<BioneMsgAttachInfo> logAttachList = this.baseDAO.findWithIndexParam(jql, msgId);
		return logAttachList;
	}

	/**
	 * 批量删除附件
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
	 * 批量删除附件
	 * 
	 * @param ids
	 */
	@Transactional(readOnly = false)
	public void deleteBatch(List<String> ids) {
		if(ids != null && ids.size() > 0){
			String jql = "delete from BioneMsgAttachInfo attr where attr.attachId in ?0";
			this.baseDAO.batchExecuteWithIndexParam(jql, ids);
		}
	}
	
	/**
	 * 批量附件信息
	 * @param entitys
	 */
	@Transactional(readOnly = false)
	public void saveBatch(List<BioneMsgAttachInfo> entitys) {
		for (BioneMsgAttachInfo entity : entitys) {
			this.saveEntity(entity);
		}
	}

	public void saveAttachInfo(BioneMsgAttachInfo attach, File file){
		PreparedStatement ps = null;
		Connection conn = null;
		InputStream is = null;
		try {
			FileInputStream input = new FileInputStream(file);
			StringBuffer buff = new StringBuffer();
			buff.append(" insert into BIONE_MSG_ATTACH_INFO (ATTACH_ID, ATTACH_STS, ATTACH_TYPE, ATTACH_NAME, ATTACH_PATH, ATTACH_SIZE, REMARK,ATTACH_CONTENT) VALUES( ?,?,?,?,?,?,?,?)");

			conn = this.jdbcBaseDAO.getCon();
			is = new BufferedInputStream(input);
//			byte[] bt = new byte[input.available()];
//			int len = bt.length;
//			int offset =0;
//			int read = 0 ;
//			while(offset<len&&(read=is.read(bt, offset, len-offset))>=0){
//				offset += read;
//			}
			ps = conn.prepareStatement(buff.toString());
			ps.setString(1, attach.getAttachId());
			ps.setString(2, attach.getAttachSts());
			ps.setString(3, attach.getAttachType());
			ps.setString(4, attach.getAttachName());
			ps.setString(5, attach.getAttachPath());
			ps.setInt(6, attach.getAttachSize().intValue());
			ps.setString(7, attach.getRemark());
//			ps.setBytes(8, bt);
			ps.setBinaryStream(8, is,is.available());
			conn.setAutoCommit(false);
			ps.execute();
			conn.commit();
		}catch(SQLException se){
			se.printStackTrace();
			if(se.getNextException() != null){
				se.getNextException().printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			this.jdbcBaseDAO.releaseCon(conn);
			IOUtils.closeQuietly(is);
		}
	}

}
