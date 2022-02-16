package com.yusys.bione.plugin.engine.service;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.engine.entity.RptEngineRefreshInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class EngineRefreshBS extends BaseBS<RptEngineRefreshInfo> {

    protected static Logger log= LoggerFactory.getLogger(EngineRefreshBS.class);
    /**
     * 分页查询度量信息
     *
     * @param firstResult
     *            分页的开始索引,第一条记录
     * @param pageSize
     *            每页记录数
     * @param orderBy
     *            排序字段
     * @param orderType
     *            排序方式
     * @param conditionMap
     *            搜索条件
     */
    @SuppressWarnings("unchecked")
    public SearchResult<RptEngineRefreshInfo> getEngineRefrList(int firstResult,
                                                         int pageSize, String orderBy, String orderType,
                                                         Map<String, Object> conditionMap) {
        SearchResult<RptEngineRefreshInfo> roleList;
        StringBuilder sql=new StringBuilder("");
        Map<String,Object> values=(Map<String, Object>)conditionMap.get("params");

        sql.append("select t from RptEngineRefreshInfo t where 1=1 and t.parentTaskId is null");
        if (!conditionMap.get("jql").equals("")) {
            sql.append(" and " + conditionMap.get("jql"));
        }
        if (!StringUtils.isEmpty(orderBy)) {
            sql.append(" order by " + orderBy + " " + orderType);
        }
        roleList=this.baseDAO.findPageWithNameParam(firstResult,pageSize,sql.toString(),values);
        return roleList;
    }


    public List<CommonComboBoxNode> taskTypeList() {
        List<CommonComboBoxNode> nodes = Lists.newArrayList();
        CommonComboBoxNode node = null;
        node = new CommonComboBoxNode();
        node.setId("");
        node.setText("全部");
        nodes.add(node);
        node = new CommonComboBoxNode();
        node.setId("RefreshNodeInfo");
        node.setText("节点刷新");
        nodes.add(node);
        node = new CommonComboBoxNode();
        node.setId("RefreshDSCache");
        node.setText("数据源刷新");
        nodes.add(node);
        node = new CommonComboBoxNode();
        node.setId("RefreshConfCache");
        node.setText("配置刷新");
        nodes.add(node);
        return nodes;
    }

    //主刷新引擎任务的详情
    public RptEngineRefreshInfo getDetailEngRefr(String taskNo){
        StringBuilder jql=new StringBuilder();
        jql.append("select t from RptEngineRefreshInfo t where 1=1 and t.taskNo=?0");
        RptEngineRefreshInfo info=this.baseDAO.findUniqueWithIndexParam(jql.toString(),taskNo);
        return info;
    }

    //子刷新任务的列表
    public SearchResult<RptEngineRefreshInfo> getEngRefrChild(Pager pager,String parentTaskId){
        SearchResult<RptEngineRefreshInfo> result;
        Map<String,Object> values= Maps.newHashMap();
        values.put("parentTaskId", parentTaskId);
        StringBuilder sql=new StringBuilder();
        sql.append("select t from RptEngineRefreshInfo t where 1=1 and t.parentTaskId = :parentTaskId");

        if (!StringUtils.isEmpty(pager.getSortname())) {
            sql.append(" order by t."+pager.getSortname()+" "+pager.getSortorder());
        }
        result = this.baseDAO.findPageWithNameParam(pager.getPageFirstIndex(), pager.getPagesize(), sql.toString(),values);
        return result;
    }

    //子刷新任务的详情
    public RptEngineRefreshInfo getDetailChild(String taskNo){
        String sql="select t from RptEngineRefreshInfo t where t.taskNo=?0";
        RptEngineRefreshInfo info = this.baseDAO.findUniqueWithIndexParam(sql, taskNo);
        return info;
    }

    //删除引擎刷新任务
    public Map<String,Object> deleteEngineRefresh(String taskNos){
        Map<String,Object> result = Maps.newHashMap();
        result.put("status", "success");
        StringBuilder msg = new StringBuilder();

        JSONArray taskNoArray = JSONArray.parseArray(taskNos);
        JSONArray array = new JSONArray();
        for (Object taskNo : taskNoArray) {
            RptEngineRefreshInfo detailChild = getDetailChild(taskNo.toString());
            if(!detailChild.getSts().equals("02")){
                array.add(taskNo);
            } else {
                Timestamp startTime = detailChild.getStartTime();
                if((System.currentTimeMillis() - startTime.getTime())/(1000*60)  > 10){
                    array.add(taskNo);
                } else {
                    result.put("status", "fail");
                    msg.append("任务【").append(detailChild.getTaskNm()).append("】正在执行，无法删除！<br/>");
                }
            }
        }
        if(array.size() > 0){
            //删除主任务
            String jql="delete from RptEngineRefreshInfo t where t.taskNo in :taskNos";
            Map<String,Object> map = Maps.newHashMap();
            map.put("taskNos", array);
            this.baseDAO.batchExecuteWithNameParam(jql, map);
            //删除子任务
            String sql = "delete from RptEngineRefreshInfo info where info.parentTaskId in :taskNos";
            this.baseDAO.batchExecuteWithNameParam(sql, map);
        }
        result.put("msg", msg.toString());
        return result;
    }

}

