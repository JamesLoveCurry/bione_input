package com.yusys.bione.frame.message.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.message.entity.BioneMsgSendType;

/**
 * <pre>
 * Title: 消息模块-消息类型定义服务
 * Description: 消息模块-消息类型定义服务
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
public class MsgSendTypeBS extends BaseBS<BioneMsgSendType> {

	/**
	 * 分页显示所有的消息类型
	 * @param pageFirstIndex
	 * @param pagesize
	 * @param sortname
	 * @param sortorder
	 * @param Conditionmap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<BioneMsgSendType> getAll(int pageFirstIndex,
			int pagesize, String sortname, String sortorder,
			Map<String, Object> Conditionmap) {
		StringBuilder jql = new StringBuilder("");
		jql.append("select msgDef from BioneMsgSendType msgDef where 1=1 ");
		if (!Conditionmap.get("jql").equals("")) {
			jql.append(" and " + Conditionmap.get("jql"));
		}
		if (!StringUtils.isEmpty(sortname)) {
			jql.append(" order by msgDef." + sortname + " " + sortorder);
		}
		Map<String, Object> values = (Map<String, Object>) Conditionmap
				.get("params");
		SearchResult<BioneMsgSendType> msgSendTypeList = this.baseDAO
				.findPageWithNameParam(pageFirstIndex, pagesize,
						jql.toString(), values);

		return msgSendTypeList;
	}

	/**
	 * 新增时，检查消息名称是否存在
	 * @param id
	 * @param sendTypeName
	 * @return
	 */
	public boolean checkTypeName(String id, String sendTypeName) {
		String jql = "select type from BioneMsgSendType type where type.sendTypeName = ?0 ";
		List<BioneMsgSendType> list = new ArrayList<BioneMsgSendType>();
		if (id == null || id.equals("")) {
			list = this.baseDAO.findWithIndexParam(jql, sendTypeName);
		} else {
			jql = jql + " and type.sendTypeNo <> ?1";
			list = this.baseDAO.findWithIndexParam(jql, sendTypeName, id);
		}
		if (list == null || list.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 显示所有的消息类型
	 * @return
	 */
	public List<BioneMsgSendType> listAll() {
		String jql = "select msgDef from BioneMsgSendType msgDef where 1=1";
		return this.baseDAO.findWithIndexParam(jql);
	}

	public boolean checkTypeNo(String sendTypeNo) {
		String jql = "select type from BioneMsgSendType type where type.sendTypeNo = ?0 ";
		List<BioneMsgSendType> list = new ArrayList<BioneMsgSendType>();
		list = this.baseDAO.findWithIndexParam(jql, sendTypeNo);
		if (list == null || list.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

}
