package com.yusys.bione.plugin.frsorg.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.businessline.entity.RptMgrBusiLine;
import com.yusys.bione.plugin.frsorg.entity.RptMgrFrsLine;
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
public interface RptMgrFrsLineMybatisDao {

	public List<RptMgrBusiLine> getInfo(Map<String, Object> map);

	public List<RptMgrFrsLine> findNm(Map<String,Object> lineNm);

	public void addLine(RptMgrFrsLine line);

	public void editLine(Map<String, Object> map);

	public void deleteInfo(List<String> list);

}