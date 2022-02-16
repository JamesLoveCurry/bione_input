package com.yusys.bione.plugin.engine.repository.mybatis;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.engine.entity.RptEngineProcess;
import com.yusys.bione.plugin.engine.entity.RptTaskInstanceInfo;

/**
 * 
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author aman aman@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@MyBatisRepository
public interface FrsEngineDao extends EngineDao {
	
	public List<RptEngineProcess> getProcessByDsId(String dsId);

	public RptEngineProcess getProcessByTaskId(String taskId);
	
	public void createProcess(RptEngineProcess rptEngineProcess);
	
	public void updateProcessByTaskId(RptEngineProcess rptEngineProcess);
	
	public void deleteProcessByTaskId(Map<String, Object> params);
	
	public void deleteProcessByDsId(String dsId);

	public List<RptTaskInstanceInfo> getEngineRptStsListNew(Map<String, Object> map);

	public List<RptTaskInstanceInfo> getEngineIdxStsListNew(Map<String, Object> map);
}
