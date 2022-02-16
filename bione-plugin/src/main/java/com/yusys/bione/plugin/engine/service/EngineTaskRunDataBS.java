package com.yusys.bione.plugin.engine.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.engine.entity.RptTaskInstanceInfo;
import com.yusys.bione.plugin.engine.repository.mybatis.EngineDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class EngineTaskRunDataBS extends BaseBS<Object> {
    @Autowired
    EngineDao engineDao;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    //获取所有的任务组跑数记录
    public SearchResult<RptTaskInstanceInfo> getTaskRunData(Pager pager, Map<String, Object> map) {
        PageHelper.startPage(pager);
        PageMyBatis<RptTaskInstanceInfo> page = (PageMyBatis<RptTaskInstanceInfo>) this.engineDao
                .getEngineRptList(map);
        SearchResult<RptTaskInstanceInfo> results = new SearchResult<RptTaskInstanceInfo>();
        results.setResult(page.getResult());
        results.setTotalCount(page.getTotalCount());
        return results;
    }

    //获取一条任务组跑数记录详情
    public RptTaskInstanceInfo getDetailTaskRunData(String taskNo) {
        String jql = "select log from RptTaskInstanceInfo log where log.id.taskNo = ?0";
        RptTaskInstanceInfo info = this.baseDAO.findUniqueWithIndexParam(jql, taskNo);
        if (info != null) {
            try {
                JSONObject result = JSON.parseObject(info.getRunLog());
                String runLog = getEngineLogSts(result.getString("Code")) + "\n";
                runLog += result.get("Msg");
                info.setRunLog(runLog);
            } catch (Exception e) {
                info.setRunLog("\n");
            }
        }
        return info;
    }

    //报表或者指标详情
    public RptTaskInstanceInfo getTaskRunDataChild(String taskNo) {
        String jql = "select log from RptTaskInstanceInfo log where log.id.taskNo = ?0";
        RptTaskInstanceInfo info = this.baseDAO.findUniqueWithIndexParam(jql, taskNo);
        if (info != null) {
            try {
                JSONObject result = JSON.parseObject(info.getRunLog());
                String runLog = getEngineLogSts(result.getString("Code")) + "\n";
                runLog += result.get("Msg");
                info.setRunLog(runLog);
            } catch (Exception e) {
                info.setRunLog("\n");
            }
        }
        return info;
    }

    //获取这条报表或者指标下所有的单元格信息
    @SuppressWarnings("unchecked")
    public SearchResult<RptTaskInstanceInfo> getTaskRptIdx(Pager pager, String taskNo) {
        StringBuilder jql = new StringBuilder(1000);
        Map<String, Object> values = (Map<String, Object>) pager.getSearchCondition().get("params");
        List<String> taskTypes = new ArrayList<String>();
        taskTypes.add(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTIG);
        taskTypes.add(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_IDXNG);
        values.put("taskType", taskTypes);
        values.put("taskNo", taskNo);
        jql = new StringBuilder(1000);
        jql.append("select t from RptTaskInstanceInfo t where t.taskType in :taskType and t.parentTaskId = :taskNo");
        if (!StringUtils.isEmpty(pager.getSortname())) {
            jql.append(" order by t." + pager.getSortname() + " " + pager.getSortorder());
        }
        return this.baseDAO.findPageWithNameParam(pager.getPageFirstIndex(), pager.getPagesize(), jql.toString(), values);
    }

    //母任务Id为taskNo的所有任务编号
    @SuppressWarnings("unchecked")
    public List<RptTaskInstanceInfo> getRptIdx(String taskNo) {
        StringBuilder jql = new StringBuilder(1000);
        jql.append("select t from RptTaskInstanceInfo t where t.parentTaskId = :taskNo");
        Map<String, Object> values = Maps.newHashMap();
        values.put("taskNo", taskNo);
        return this.baseDAO.findWithNameParm(jql.toString(), values);
    }


    //查询引擎状态
    public List<RptTaskInstanceInfo> getEnginePendingSts() {
        List<String> list = Lists.newArrayList();
        String jql = " select t from RptTaskInstanceInfo t where t.taskType in (?0) and t.parentTaskId is null and (t.sts = ?1 or t.sts = ?2) ";
        return this.baseDAO.findWithIndexParam(jql, "RptIdGroup,IndexNoGroup", "02", "01");
    }

    public String redoTask(String taskNo, String dataDate, String taskType) {
        Map<String, Object> map_ = Maps.newHashMap();
        if (taskType.equals(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTIG)) {
            map_.put("RptIdGroup", taskNo);
        }
        if (taskType.equals(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_IDXNG)) {
            map_.put("IndexNoGroup", taskNo);
            //基础指标影响跑数未做
        }
        map_.put("DataDate", dataDate);
        String json = JSON.toJSONString(map_);
        try {
            CommandRemote.sendAync(json, CommandRemote.CommandRemoteType.INDEX);
        } catch (Throwable e) {
            logger.debug("重跑任务失败");
            return "重跑任务失败";
        }
        return "";
    }


    /**
     * 删除报表跑数任务记录
     *
     * @param taskNos
     */
    public void deleteTaskRunData(String taskNos) {
        JSONArray taskNoArray = JSONArray.parseArray(taskNos);
        String jql = "";
        Map<String, Object> params = Maps.newHashMap();
        params.put("taskNo", taskNoArray);

        //获取任务下报表的taskNo
        List<String> rptIdx = Lists.newArrayList();
        String sql = "select t.id.taskNo from RptTaskInstanceInfo t where t.parentTaskId in :taskNo";
        rptIdx = this.baseDAO.findWithNameParm(sql, params);
        String rptIdxs = "";
        if(rptIdx.size()>0) {
            for (int i = 0; i < rptIdx.size(); i++) {
                rptIdxs = rptIdx.get(i) + "," + rptIdxs;
            }
            rptIdxs = rptIdxs.substring(0, rptIdxs.length() - 1);//去掉拼接的最后一个字符“，”

            //删除单元格
            jql = "delete from RptTaskInstanceInfo a where a.parentTaskId in ?0";
            this.baseDAO.batchExecuteWithIndexParam(jql, rptIdxs);
        }

        //删除报表或者指标
        jql = "delete from RptTaskInstanceInfo info where info.parentTaskId in :taskNo";
        this.baseDAO.batchExecuteWithNameParam(jql, params);


        //删除主任务
        jql = "delete from RptTaskInstanceInfo info where info.id.taskNo in :taskNo";
        this.baseDAO.batchExecuteWithNameParam(jql, params);
    }

    //查看这个任务的状态
    public String getTaskSts(String taskNo,String dataDate){
        String sql = "select t.sts from RptTaskInstanceInfo t where t.id.instanceId=?0 and t.id.dataDate=?1 and t.taskType in ('RptIdGroup','IndexNoGroup')";
        String sts=this.baseDAO.findUniqueWithIndexParam(sql, taskNo,dataDate);
        if(sts!=null && (sts.equals("01")||sts.equals("02"))){
            return "Y";
        }else{
            return "N";
        }
    }



    //引擎跑数
    public void runData(Map<String, Object> map) throws Exception {
        String dataDate = (String) map.get("dataDate");
        String taskNo = (String) map.get("taskNo");
        Map<String, Object> map_ = Maps.newHashMap();
        map_.put("DataDate", dataDate);
        if (map.get("taskType").equals("01")) {//报表组
            map_.put("RptIdGroup", taskNo);//taskNo是任务表的taskNo，不是引擎表的taskNO
        } else if (map.get("taskType").equals("03")) {//指标组
            map_.put("IndexNoGroup", taskNo);
        }
        String json = JSON.toJSONString(map_);
        CommandRemote.sendAync(json, CommandRemote.CommandRemoteType.INDEX);
    }

    //获取引擎日记状态，runLog
    private String getEngineLogSts(String code) {
        if (GlobalConstants4plugin.RESULT_SUCCESS.equals(code)) {
            return "成功";
        }
        if (GlobalConstants4plugin.RESULT_DATABASE_ERROR.equals(code)) {
            return "数据库错误 ";
        }
        if (GlobalConstants4plugin.RESULT_INDEX_ERROR.equals(code)) {
            return "指标配置错误";
        }
        if (GlobalConstants4plugin.RESULT_DIM_ERROR.equals(code)) {
            return "维度配置错误";
        }
        if (GlobalConstants4plugin.RESULT_CACHE_ERROR.equals(code)) {
            return "cache出错";
        }
        if (GlobalConstants4plugin.RESULT_COMPUTE_ERROR.equals(code)) {
            return "计算出错";
        }
        if (GlobalConstants4plugin.RESULT_CHECK_ERROR.equals(code)) {
            return "检核配置错误";
        }
        if (GlobalConstants4plugin.RESULT_ORG_ERROR.equals(code)) {
            return "机构配置错误";
        }
        if (GlobalConstants4plugin.RESULT_NO_TASK_ERROR.equals(code)) {
            return "没有找到任务";
        }
        if (GlobalConstants4plugin.RESULT_NODE_ERROR.equals(code)) {
            return "节点配置错误";
        }
        if (GlobalConstants4plugin.RESULT_REBOOT_ERROR.equals(code)) {
            return "引擎重启导致任务失败";
        }
        if (GlobalConstants4plugin.RESULT_JSON_ERROR.equals(code)) {
            return "报文错误 ";
        }
        if (GlobalConstants4plugin.RESULT_FTP_ERROR.equals(code)) {
            return "文件传输错误";
        }
        if (GlobalConstants4plugin.RESULT_KILL_ERROR.equals(code)) {
            return "杀死任务失败";
        }

        if (GlobalConstants4plugin.RESULT_NETWORK_ERROR.equals(code)) {
            return "网络错误";
        }
        if (GlobalConstants4plugin.RESULT_PART_SUCCESS.equals(code)) {
            return "部分成功";
        }
        if (GlobalConstants4plugin.RESULT_UNKNOWN_ERROR.equals(code)) {
            return "未知错误";
        }
        if (GlobalConstants4plugin.RESULT_UNREALIZED_ERROR.equals(code)) {
            return "未实现的类";
        }
        if (GlobalConstants4plugin.RESULT_SYS_JAR_ERROR.equals(code)) {
            return "缺少jar";
        }
        if (GlobalConstants4plugin.RESULT_SYS_UNDEF_INTERFACE.equals(code)) {
            return "未定义的计算器";
        }
        if (GlobalConstants4plugin.RESULT_SYS_TIMEOUT.equals(code)) {
            return "执行超时";
        }
        if (GlobalConstants4plugin.RESULT_PARAMETER_ERROR.equals(code)) {
            return "参数错误";
        }
        if (GlobalConstants4plugin.RESULT_SYS_KILL.equals(code)) {
            return "手动杀死";
        }
        if (GlobalConstants4plugin.RESULT_UNDEF_METHOD_ERROR.equals(code)) {
            return "未实现的方法";
        }
        return "";
    }

}
