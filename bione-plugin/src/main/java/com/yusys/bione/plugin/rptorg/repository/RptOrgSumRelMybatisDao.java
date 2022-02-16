package com.yusys.bione.plugin.rptorg.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptorg.entity.RptOrgSumRel;
/**
 * <pre>
 * Description: 功能描述
 * </pre>
 * @author sunyuming  sunym@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:       修改人:       修改日期:       修改内容: 
 * </pre>
 */
@MyBatisRepository
public interface RptOrgSumRelMybatisDao {

	List<RptOrgSumRel> findCheck(Map<String,Object> map);

	List<String> findUpNode(List<List<String>> list);

	void deleteInfo(Map<String, Object> map);

	void saveInfo(Map<String, Object> map);
	
	List<String> findChildNode(List<List<String>> list);
	
}
