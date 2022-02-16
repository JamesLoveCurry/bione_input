package com.yusys.biapp.input.task.repository;

import java.util.List;
import java.util.Map;

import com.yusys.biapp.input.input.entity.RptListOperLog;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.task.entity.RptTskInsLog;
import com.yusys.biapp.input.task.entity.RptTskNodeIns;
import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author
 */
@MyBatisRepository
public interface TaskOperDao {

	/**
	 * 查询需要当前用户处理的任务
	 * 
	 * @param userNo
	 * @param deptNo
	 * @param orgNo
	 * @return
	 */
	public List<Map<String,Object>> getNeedOperTask(Map<String, Object> map);

	/**
	 * 查询当前用户处理过的任务
	 * 
	 * @param userNo
	 * @param deptNo
	 * @param orgNo
	 * @return
	 */
	public List<Map<String,Object>>getOperedTask(Map<String, Object> map);
	/**
	 * 查询当前用户分发过的任务
	 * 
	 * @param userNo
	 * @param deptNo
	 * @param orgNo
	 * @return
	 */
	//public List<Map<String,Object>>getDeployTask(Map<String, Object> map);
	
	/**
	 * 根据条件查询所有的任务信息
	 * @param map
	 * @return
	 */
	public List<Map<String,Object>>getQueryTask(Map<String, Object> map);
	
	/**
	 * 更新当前任务节点
	 * 
	 * @param taskNodeInstanceId
	 */
	public void updateRptTskNodeIns(RptTskNodeIns nodeIns);

	/**
	 * 更新任务实例信息
	 * 
	 * @param ins
	 */
	public void updateRptTskIns(RptTskIns ins);

	/**
	 * 记录操作日志
	 * 
	 * @param log
	 */
	public void saveTskInsLog(RptTskInsLog log);
	/**
	 * 记录操作日志
	 * 
	 * @param log
	 */
	public void saveRptListOperLog(RptListOperLog operLog);

	/**
	 * 取出上一节点的信息
	 * 
	 * @param taskInstanceId
	 */
	public RptTskNodeIns getPreRptTskNodeIns(String taskNodeInsId);

	/**
	 * 取出上一节点的信息
	 * 
	 * @param taskInstanceId
	 */
	public List<RptTskNodeIns> getRptTskNodeIns(String taskNodeInsId);
		
	/**
	 * 查询任务实例信息
	 * @param map
	 * @return
	 */
	public Map<String,Object>getRptTskInsInfoWithNode(Map<String,Object> params);
	/**
	 * 查询系统日志
	 * 
	 * @param userNo
	 * @param deptNo
	 * @param orgNo
	 * @return
	 */
	public List<Map<String,Object>>getTaskInsLog(String taskInstanceId);

	/**
	 * 查询系统日志
	 * 
	 * @param userNo
	 * @param deptNo
	 * @param orgNo
	 * @return
	 */
	public List<RptListOperLog>getListLogDetail(Map<String,Object> params);
	
	/**
	 * 判断是否有回退功能
	 * @param params
	 * @return
	 */
	public List<Integer> canReOpen(Map<String,Object> params);
	
	/**
	 * 取出可以回退的任务
	 * @param map
	 * @return
	 */
	public List<Map<String,Object>> getNeedBackTask(Map<String, Object> map);
	
	/**
	 * 通过节点实例ID取出执行任务的对象类型及ID
	 * @param taskNodeInstanceId
	 * @return
	 */
	public List<Map<String,Object>>getRptTskNodeTskinsByTaskNode(String taskNodeInstanceId);
	
	/**
	 * 查询是否发送短信
	 * @param taskNodeInstanceId
	 * @return
	 */
	public String getIsSendNotifyByTaskNodeInstanceId(String taskNodeInstanceId);
	
	public Map<String,Object>getTskDealInfoByNodeInfo(String taskInstanceId);
	
	public List<String>getAllTaskNodeInsIds(String taskInstanceId);
	
	
	public void deleteRptTskNodeTskinsRelByInsId(String taskInstanceId);

	public void deleteRptTskNodeInsByInsId(String taskInstanceId);

	public void deleteRptTskInsByInsId(String taskInstanceId);

	public List<Map<String, Object>> getDeployAuthObjInfo(Map<String, Object> params);

	public List<Map<String, String>> getTaskObjNames(List<String> taskObjIds);
}
