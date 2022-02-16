package com.yusys.bione.frame.activiti.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.activiti.entity.ActIdGroup;
import com.yusys.bione.frame.activiti.entity.ActReModel;
import com.yusys.bione.frame.activiti.entity.ActReProcdef;
import com.yusys.bione.frame.activiti.entity.ActRuExecution;

@Service
@Transactional
public class ActivitiBS extends BaseBS<Object>{
	ProcessEngine processEngine = SpringContextHolder.getBean("processEngine");
	RepositoryService repositoryService = processEngine.getRepositoryService();
	/**
	 * 
	 * @Title: getModuleList
	 * @Description: 查询流程定义列表
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @return SearchResult<ActReModel>  
	 * @throws
	 */
	public SearchResult<ActReModel> getModuleList(int firstResult, int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap) {
		StringBuilder jql = new StringBuilder("");
		jql.append("select T from ActReModel T where 1=1");
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by T." + orderBy + " " + orderType);
		}
		@SuppressWarnings("unchecked")
		Map<String, ?> values = (Map<String, ?>) conditionMap.get("params");
		SearchResult<ActReModel> moduleList = this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), values);
		return moduleList;
	}
	/**
	 * 
	 * @Title: getRoleName
	 * @Description: 查询Activiti角色表获取角色列表
	 * @return List<ActIdGroup>  
	 * @throws
	 */
	public List<ActIdGroup> getRoleName() {
		List<ActIdGroup> ActIdGroup=Lists.newArrayList();
		String jql="select a.name from ActIdGroup a";
		ActIdGroup=this.baseDAO.findWithIndexParam(jql);
		return ActIdGroup;
	}
	/**
	 * 
	 * @Title: createModel
	 * @Description: 创建流程模型
	 * @param name
	 * @param description
	 * @return Model  
	 * @throws
	 */
	public Model createModel(String name,String description) {
		ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.set("stencilset", stencilSetNode);
        Model modelData = repositoryService.newModel();

        ObjectNode modelObjectNode = objectMapper.createObjectNode();
        modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelData.setMetaInfo(modelObjectNode.toString());
        modelData.setName(name);
        modelData.setKey(RandomUtils.uuid2());

        //保存模型
        repositoryService.saveModel(modelData);
        try {
			repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return modelData;
	}
	/**
	 * 
	 * @Title: deployModel
	 * @Description: 部署流程模型
	 * @param modleID void  
	 * @throws
	 */
	public void deployModel(String[] modleID) {
		Model modelData=null;
		ObjectNode modelNode=null;
		byte[] bpmnBytes = null;
		BpmnModel model=null;
		try {
			for (int i = 0; i < modleID.length; i++) {
				modelData = repositoryService.getModel(modleID[i]);
				String oldDeploymentId=modelData.getDeploymentId();
				modelNode = (ObjectNode) new ObjectMapper().readTree(repositoryService.getModelEditorSource(modelData.getId()));
				String key=modelNode.get("properties").get("process_id").textValue();
				model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
				bpmnBytes = new BpmnXMLConverter().convertToXML(model);
				String processName = modelData.getName() + ".bpmn20.xml";
				Deployment deployment = repositoryService.createDeployment().name(modelData.getName()).addString(processName, new String(bpmnBytes)).deploy();
				
				modelData.setDeploymentId(deployment.getId());
				modelData.setKey(key);
				repositoryService.saveModel(modelData);
				//删除之前部署过的旧数据 避免垃圾数据
				if (StringUtils.isNotEmpty(oldDeploymentId)) {
					repositoryService.deleteDeployment(oldDeploymentId);
				}
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @Title: deleteModel
	 * @Description: 删除流程模型
	 * @param modleID void  
	 * @throws
	 */
	public void deleteModel(String[] modleID) {
		for (int i = 0; i < modleID.length; i++) {
			String DeploymentId=repositoryService.getModel(modleID[i]).getDeploymentId();
			if (StringUtils.isNotEmpty(DeploymentId)) {
				repositoryService.deleteDeployment(DeploymentId);
			}
			repositoryService.deleteModel(modleID[i]);
		}
	}
	/**
	 * 
	 * @Title: validate
	 * @Description: 增加已经发起任务的流程不能再次部署的校验
	 * @param modleID
	 * @return String  
	 * @throws
	 */
	public String validate(String[] modleID) {
		String validate="1";
		String jql1="SELECT id FROM ActReProcdef WHERE deploymentId=?0";
		String jql2="SELECT id FROM ActRuExecution WHERE procDefId=?0";
		for (int i = 0; i < modleID.length; i++) {
			String DeploymentId=repositoryService.getModel(modleID[i]).getDeploymentId();
			List<ActReProcdef> list= this.baseDAO.findWithIndexParam(jql1, DeploymentId);
			if (list.size()>0) {
				List<ActRuExecution> list2= this.baseDAO.findWithIndexParam(jql2, String.valueOf(list.get(0)));
				if (list2.size()>0) {
					validate="0";
				}
			}
		}
		return validate;
	}
}
