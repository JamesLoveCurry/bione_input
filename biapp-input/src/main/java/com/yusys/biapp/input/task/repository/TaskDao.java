package com.yusys.biapp.input.task.repository;


import java.util.List;
import java.util.Map;

import com.yusys.biapp.input.task.entity.RptTskCatalog;
import com.yusys.biapp.input.task.entity.RptTskExeobjIns;
import com.yusys.biapp.input.task.entity.RptTskExeobjNodeinsRel;
import com.yusys.biapp.input.task.entity.RptTskExeobjRel;
import com.yusys.biapp.input.task.entity.RptTskFlow;
import com.yusys.biapp.input.task.entity.RptTskFlowNode;
import com.yusys.biapp.input.task.entity.RptTskInfo;
import com.yusys.biapp.input.task.entity.RptTskIns;
import com.yusys.biapp.input.task.entity.RptTskInsGenerateSts;
import com.yusys.biapp.input.task.entity.RptTskNode;
import com.yusys.biapp.input.task.entity.RptTskNodeExeobjRel;
import com.yusys.biapp.input.task.entity.RptTskNodeIns;
import com.yusys.biapp.input.task.entity.RptTskNodeTskinsRel;
import com.yusys.biapp.input.task.entity.RptTskNodeTskobjRel;
import com.yusys.biapp.input.task.entity.RptTskOrgRel;
import com.yusys.biapp.input.task.entity.RptTskTskobjIns;
import com.yusys.biapp.input.task.web.vo.TaskDeptUserList;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author 
 */
@MyBatisRepository
public interface TaskDao {	
	/**
	 * 根据机构标识查询机构及机构下的角色信息
	 * @param orgList
	 * @return
	 */
	public List<Map<String,Object>>getTaskRoleTreeOfOrgs(Map<String,Object>params);
	
	/**
	 * 根据机构标识查询机构及机构下的用户信息
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>>getTaskUserTreeOfOrgs(Map<String,Object>params);
	
	/**
	 * 保存任务主表信息
	 * @param tskInfo
	 */
	public void  saveRptTskInfo(RptTskInfo tskInfo);
	
	/**
	 * 更新任务主表信息
	 * @param tskInfo
	 */
	public void  updateRptTskInfo(RptTskInfo tskInfo);
	
	
	/**
	 * 保存任务节点信息
	 * @param rptTskNode
	 */
	public void saveRptTskNode(RptTskNode rptTskNode);
	
	/**
	 * 保存监管任务对象实例
	 * @param ins
	 */
	public void saveRptTskTskobjIns(RptTskTskobjIns ins);
	
	/**
	 * 保存任务执行对象实例
	 * @param exeObjIns
	 */
	public void saveRptTskExeobjIns(RptTskExeobjIns exeObjIns);
	
	/**
	 * 保存监管任务与执行对象关系
	 * @param exeobjRel
	 */
	public void saveRptTskExeobjRel(RptTskExeobjRel exeobjRel);
	
	/**
	 * 保存任务组织信息
	 * @param rptTskOrgRel
	 */
	public void saveRptTskOrgRel(RptTskOrgRel rptTskOrgRel);
	
	/**
	 * 保存监管任务节点与执行对象关系
	 * @param nodeRel
	 */
	public void saveRptTskNodeTskobjRel(RptTskNodeTskobjRel nodeRel);
	
	/**
	 * 保存监管任务节点与执行对象关系
	 * @param rel
	 */
	public void saveRptTskNodeExeobjRel(RptTskNodeExeobjRel rel);
	
	/**
	 * 更新监管任务实例生成状态
	 * @param insSts
	 */
	public void saveRptTskInsGenerateSts(RptTskInsGenerateSts insSts);
	
	/**
	 * 生成任务实例
	 * @param rptTskInsList
	 */
	public void saveRptTskIns(List<RptTskIns> rptTskInsList);

	/**
	 * 保存任务实例节点对应的执行对象信息
	 * @param rptTskNodeTskinsRelList
	 */
	public void saveRptTskNodeTskinsRel(List<RptTskNodeTskinsRel> rptTskNodeTskinsRelList);
	
	/**
	 * 生成实例节点信息
	 * @param rptTskNodeInsList
	 */
	public void saveRptTskNodeIns(List<RptTskNodeIns> rptTskNodeInsList);
	
	
	
	
	/**
	 * 更新任务执行对象信息
	 * @param taskNodeInstanceId
	 * @param exeObjId
	 */
	public void saveRptTskExeobjNodeinsRel(RptTskExeobjNodeinsRel rel);
	
	
	/**
	 * 保存节点操作对象信息
	 * @param taskInstanceId
	 * @param taskNodeInstanceId
	 * @param userNo
	 * @param remark
	 */
	public void updateRptTskIns(String taskInstanceId,String taskNodeInstanceId,String  userNo,String remark);
	
	/**
	 * 更新任务状态
	 * @param list
	 */
	public void changeTaskSts(List<String> list);
	
	/**
	 * 创建目录
	 * @param catalog
	 */
	public void saveRptTskCatalog(RptTskCatalog catalog);
	
	/**
	 * 更新目录
	 * @param catalog
	 */
	public void updateRptTskCatalog(RptTskCatalog catalog);
	
	/**
	 * 获取任务的名称和状态
	 * @param taskIds
	 * @return
	 */
	public List<Map<String,String>>getTaskNmsByIds(List<String>taskIds);
	
	/**
	 * 得到所有的目录信息
	 * @return
	 */
	public List<RptTskCatalog>getRptTskCatalog(Map<String, Object> map);
	
	/**
	 * 根据条件查询目录下的任务信息
	 * @param catalogId
	 * @return
	 */
	public List<RptTskInfo>getTaskInfos(Map<String,Object>map);
	
	/**
	 * 获取任务相关的执行对象信息
	 * @param taskid
	 * @return
	 */
	public RptTskExeobjNodeinsRel getTskExeobjNodeinsRel(String taskid);
	
	
	/**
	 * 获取任务相关任务对象信息
	 * @param taskId
	 * @return
	 */
	public List<RptTskNodeTskobjRel> getTskNodeTskobjRelList(String taskId);
	
	/**
	 * 获取流程相关的节点信息
	 * @param taskId
	 * @return
	 */
	public List<Map<String,Object>>getTaskNodeInfos(String taskId);
	
	/**
	 * 查询节点任务对象信息
	 * @param map
	 * @return
	 */
	public  List<Map<String,Object>>getTaskObjOfNode(Map<String,Object>map);
	
	/**
	 * 查询节点相关的任务对象信息
	 * @param taskNodeId
	 * @return
	 */
	public Map<String,Object>getTaskObjOfNode(String taskNodeId);

	/**
	 * 通过任务查询执行对象信息
	 * @param taskId
	 * @return
	 */
	public Map<String,Object> getTskExeobjRelInfo(String taskId);
	
	/**
	 * 根据条件查询目录下的任务信息
	 * @param catalogId
	 * @return
	 */
	public Integer getTaskInfosCount(Map<String,Object>map);
	
	/**
	 * 判断目录名称是否被占用
	 * @param map
	 * @return
	 */
	public Integer testSameIndexCatalogNm(Map<String,Object>map);

	/**
	 * 得到任务同名任务数
	 * @param map
	 * @return
	 */
	public Integer getFlowCnt(Map<String,String> map);
	
	/**
	 * 判断任务是否已经下发
	 * @param map
	 * @return
	 */
	public List<String> getDulpDeployTask(Map<String,Object>map);
	
	/**
	 * 判断目录名称是否被占用
	 * @param map
	 * @return
	 */
	public RptTskCatalog  getRptTskCatalogByCatalogId(String catalogId);
	
	/**
	 * 删除目录
	 * @param catalogId
	 */
	public void deleteTskCatalog(List<String> catalogIdList);
	
	/**
	 * 删除任务流程
	 * @param taskDefId
	 */
	public void delTaskFlow(String taskDefId);
	
	/**
	 * 删除流程节点
	 * @param params
	 */
	public void delTaskFlowNode(Map<String,Object>params);
	/**
	 * 删除监管任务节点信息
	 * @param taskId
	 */
	public void deleteRptTskNodeByTask(String taskId);
	/**
	 * 删除监管任务与任务对象关系
	 * @param taskId
	 */
	public void deleteRptTskTskobjRelByTask(String taskId);
	/**
	 * 删除监管任务节点与执行对象关系
	 * @param taskId
	 */
	public void deleteRptTskNodeExeobjRelByTask(String taskId);
	
	/**
	 * 删除报表任务机构定义
	 * @param taskId
	 */
	public void deleteRptTskOrgRelByTask(String taskId);
	/**
	 * 删除监管任务与执行对象关系
	 * @param taskId
	 */
	public void deleteRptTskExeobjRelByTask(String taskId);
	/**
	 * 删除监管任务节点与任务对象关系
	 * @param taskId
	 */
	public void deleteRptTskNodeTskobjRelByTask(String taskId);
	

	/**
	 * 批量删除监管任务节点与任务对象关系
	 * @param taskId
	 */
	public void batchDeleteRptTskNodeTskobjRel(List<String> taskId);
	/**
	 * 批量删除监管任务与执行对象关系
	 * @param taskId
	 */
	public void batchDeleteRptTskExeobjRel(List<String> taskId);
	/**
	 * 批量删假删任务
	 * @param taskId
	 */
	public void batchLogicDelRptTskInfo(List<String> taskId);
	/**
	 * 批量删除监管任务节点信息
	 * @param taskId
	 */
	public void batchDeleteRptTskNode(List<String> taskId);
	

	/**
	 * 批量删除任务信息
	 * @param taskId
	 */
	public void batchDeleteRptTskInfo(List<String> taskId);


	/**
	 * 批量删除任务信息
	 * @param taskId
	 */
	public void batchDeleteRptTskIns(List<String> taskId);
	
	
	/**
	 * 取出所有任务定义信息
	 * @return
	 */
	public List<RptTskFlow>getTaskList();
	
	/**
	 * 获取任务类型
	 * @return
	 */
	public List<Map<String,Object>>getTaskType(String taskType);
	
	/**
	 * 生成报表任务节点信息
	 * @param taskDefId
	 * @return
	 */
	public List<Map<String,Object>>getTaskNodeList(String taskDefId);

	/**
	 * 生成报表任务节点信息
	 * @param taskDefId
	 * @return
	 */
	public List<RptTskFlow>getListWorkFlow(Map<String,Object>map);
	/**
	 * 查询任务的执行对象
	 * @param taskInstanceId
	 * @return
	 */
	public RptTskExeobjRel  getRptTskExeobjRelByTaskInstanceId(String taskInstanceId);
	
	/**
	 * 查询任务实例信息
	 * @param params
	 * @return
	 */
	public Map<String,Object>getRptTskInsOrgInfo(String taskInstanceId);
	
	/**
	 * 通过ID查询实例信息
	 * @param taskInstanceId
	 * @return
	 */
	public RptTskIns  getRptTskInsById(String taskInstanceId);
	
	
	public List<CommonTreeNode> findOrgNode(Map<String,Object>map);
	
	public List<CommonTreeNode> findPubOrgNode(Map<String,Object>map);
	
	/**
	 * 得到任务统一设置的组织
	 * @param taskId
	 * @return
	 */
	public List<String>getTaskOrgs(String taskId);

    //根据机构号列表获取父级机构集列表
	public List<String> getUpOrgNos(List<List<String>> list);
	
	/**
	 * 保存工作流
	 * @param map
	 */
	public void saveFlow(Map<String,String>map);
	
	/**
	 * 修改工作流
	 * @param map
	 */
	public void updateFlow(Map<String,String>map);
	
	/**
	 * 保存流程节点信息
	 * @param flowNodeList
	 */
	public void saveTaskFlowNode(RptTskFlowNode lowNodeList);
	
	public List<Map<String,String>> getTskInsNode(String insId);
	
	public void delTskInsNode(Map<String,String> param);

	List<TaskDeptUserList>  getTaskDeptList(String taskId);
	
	
}
