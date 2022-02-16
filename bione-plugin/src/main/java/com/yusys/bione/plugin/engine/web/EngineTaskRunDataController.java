package com.yusys.bione.plugin.engine.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.engine.entity.RptTaskInstanceInfo;
import com.yusys.bione.plugin.engine.service.EngineTaskRunDataBS;
import com.yusys.bione.plugin.engine.service.IdxEngineBS;
import com.yusys.bione.plugin.engine.service.RptEngineBS;
import com.yusys.bione.plugin.rptmgr.util.SplitStringBy1000;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/report/frame/taskrundata")
public class EngineTaskRunDataController extends BaseController {
    @Autowired
    private EngineTaskRunDataBS taskRunDataBS;
    @Autowired
    private RptEngineBS rptEngineBS;
    @Autowired
    private IdxEngineBS idxEngineBS;


    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        return new ModelAndView("/plugin/taskrundata/taskrundata-index");
    }

    /**
     * 任务组跑数新增页面
     */
    @RequestMapping(value = "/newTaskRunData")
    public ModelAndView newEngineBatch() {
        return new ModelAndView("/plugin/taskrundata/taskrundata-new");
    }

    @RequestMapping("/getTaskRundata")
    @ResponseBody
    public Map<String, Object> search(Pager page) {
        Map<String, Object> map = Maps.newHashMap();
        List<String> taskTypes = new ArrayList<String>();
        taskTypes.add(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_RPTIG);//"RptIdGroup",任务组报表类型
        taskTypes.add(GlobalConstants4plugin.RPT_ENGINE_TASK_TYPE_IDXNG);//"IndexNoGroup",任务组指标类型
        map.put("taskTypes", taskTypes);
        if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
            List<String> authRptIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_RPT_VIEW");
            if (authRptIds != null && authRptIds.size() > 0) {
                map.put("authRptIds", SplitStringBy1000.change(authRptIds));
            } else {
                map.put("authRptIds", "-999");
            }
        }
        SearchResult<RptTaskInstanceInfo> searchResult = this.taskRunDataBS.getTaskRunData(page, map);
        Map<String, Object> rowData = Maps.newHashMap();
        rowData.put("Rows", searchResult.getResult());
        rowData.put("Total", searchResult.getTotalCount());
        return rowData;
    }

    /**
     * 任务组详情界面
     *
     * @param taskNo
     * @return
     */
    @RequestMapping(value = "/detailTask")
    public ModelAndView detailLog(String taskNo) {
        ModelMap map = new ModelMap();
        map.put("taskNo", StringUtils2.javaScriptEncode(taskNo));
        return new ModelAndView("/plugin/taskrundata/taskrundata-detailTask", map);
    }

    /**
     * 任务下所有的报表或者指标，展示为grid
     */
    @RequestMapping(value = "/getTaskRptIdx.*")
    @ResponseBody
    public Map<String, Object> getTaskRptIdx(Pager pager, String taskNo) {
        SearchResult<RptTaskInstanceInfo> searchResult = this.taskRunDataBS
                .getTaskRptIdx(pager, taskNo);
        Map<String, Object> rowData = Maps.newHashMap();
        List<RptTaskInstanceInfo> list = searchResult.getResult();
        rowData.put("Rows", list);
        rowData.put("Total", searchResult.getTotalCount());
        return rowData;
    }

    /**
     * 报表或者指标详情界面
     *
     * @param taskNo 报表或者指标编号
     * @return
     */
    @RequestMapping(value = "/detailTaskChild")
    public ModelAndView detailTaskChild(String taskNo, String taskType) {
        ModelMap map = new ModelMap();
        map.put("taskNo", StringUtils2.javaScriptEncode(taskNo));
        map.put("taskType", StringUtils2.javaScriptEncode(taskType));
        return new ModelAndView("/plugin/taskrundata/taskrundata-detailTaskChild", map);
    }


    /**
     * @return
     */
    @RequestMapping(value = "/getDetailTaskRunData")
    @ResponseBody
    public Map<String, Object> getDetailTaskRunData(String taskNo) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("baseData", this.taskRunDataBS.getDetailTaskRunData(taskNo));
        return result;
    }

    /**
     * @return
     */
    @RequestMapping(value = "/getTaskRunDataChild")
    @ResponseBody
    public Map<String, Object> getTaskRunDataChild(String taskNo) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("baseData", this.taskRunDataBS.getTaskRunDataChild(taskNo));
        return result;
    }


    /**
     * 报表或者指标下所有的子任务，展示为grid
     */
    @RequestMapping(value = "/getTaskRptIdxChild.*")
    @ResponseBody
    public Map<String, Object> getTaskRptIdxChild(Pager pager, String taskNo) {
        SearchResult<RptTaskInstanceInfo> searchResult = this.taskRunDataBS
                .getTaskRptIdx(pager, taskNo);
        Map<String, Object> rowData = Maps.newHashMap();
        List<RptTaskInstanceInfo> list = searchResult.getResult();
        rowData.put("Rows", list);
        rowData.put("Total", searchResult.getTotalCount());
        return rowData;
    }


    /**
     * 报表引擎详细信息子页面
     *
     * @return
     */
    @RequestMapping(value = "/detailChildLog")
    public ModelAndView detailChildLog(String taskNo) {
        ModelMap map = new ModelMap();
        map.put("taskNo", StringUtils2.javaScriptEncode(taskNo));
        return new ModelAndView("/plugin/taskrundata/taskrundata-detailTaskChildLog", map);
    }

    /**
     * @return
     */
    @RequestMapping(value = "/getTaskRunDataChildLog")
    @ResponseBody
    public Map<String, Object> getTaskRunDataChildLog(String taskNo) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("baseData", this.taskRunDataBS.getTaskRunDataChild(taskNo));
        return result;
    }

    /**
     * 手工停止任务
     */
    @RequestMapping(value = "/stopTask", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> stopTask(String taskNo) {
        Map<String, Object> params = Maps.newHashMap();
        List<RptTaskInstanceInfo> list = Lists.newArrayList();
        boolean pingFlag = true;
        try {
            pingFlag = CommandRemote.testConnection(CommandRemote.CommandRemoteType.INDEX);
        } catch (Throwable e1) {
            pingFlag = false;
            e1.printStackTrace();
        }
        if (!pingFlag) {
            logger.info("引擎连接失败！");
            params.put("msg", "引擎连接失败！");
            return params;
        }
        list = this.taskRunDataBS.getRptIdx(taskNo);
        String info = "";
        for (int i = 0; i < list.size(); i++) {
            if (info == "") {
                RptTaskInstanceInfo rtii = list.get(i);
                String taskNo1 = rtii.getId().getTaskNo();
                info = this.rptEngineBS.stopTask(taskNo1);//停止报表和指标任务都是这个方法
                params.put("msg", info);
            }
        }
        return params;
    }

    /**
     * 自动刷新获取引擎状态
     */
    @RequestMapping(value = "/getEnginePendingSts")
    public Map<String, Object> getEnginePendingSts() {
        Map<String, Object> returnMap = Maps.newHashMap();
        List<RptTaskInstanceInfo> pendingCount = this.taskRunDataBS.getEnginePendingSts();
        returnMap.put("msg", "unExsi");
        if (null != pendingCount && pendingCount.size() > 0) {
            returnMap.put("msg", "exsi");
        }
        return returnMap;
    }

    /**
     * 手工重跑任务
     */
    @RequestMapping(value = "/redoTask", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> redoTask(String taskNo, String dataDate, String taskType) {
        Map<String, Object> params = Maps.newHashMap();
        boolean pingFlag = true;
        try {
            pingFlag = CommandRemote.testConnection(CommandRemote.CommandRemoteType.INDEX);
        } catch (Throwable e1) {
            pingFlag = false;
            e1.printStackTrace();
        }
        if (!pingFlag) {
            logger.info("引擎连接失败！");
            params.put("msg", "引擎连接失败！");
            return params;
        }
        String info = "";
        info=this.taskRunDataBS.redoTask(taskNo, dataDate, taskType);
        params.put("msg", info);
        return params;
    }

    /**
     * @param taskNo         传过来的是任务表中的taskNo，不是引擎表的taskNo
     * @param taskType
     * @param dataDate
     * @return
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> create(String taskNo, String taskType, String dataDate) {
        //查看任务组的状态，如果为01和02，等待执行和正在执行，就不可以再添加相同的任务
        String isRunning=taskRunDataBS.getTaskSts(taskNo,dataDate);
        Map<String, Object> result = new HashMap<String, Object>();
        if(isRunning.equals("N")) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("taskNo", taskNo);
            map.put("dataDate", dataDate);
            map.put("taskType", taskType);
            boolean pingFlag = true;
            try {
                pingFlag = CommandRemote.testConnection(CommandRemote.CommandRemoteType.INDEX);
            } catch (Throwable e1) {
                pingFlag = false;
                e1.printStackTrace();
            }
            if (!pingFlag) {
                logger.info("引擎连接失败！");
                result.put("msg", "引擎连接失败！");
                return result;
            }
            try {
                this.taskRunDataBS.runData(map);
                if (taskType.equals("01")) {
                    result.put("msg", "任务组的报表跑数成功！");
                } else if (taskType.equals("03")) {
                    result.put("msg", "任务组的指标跑数成功！");
                }
            } catch (Exception e) {
                result.put("msg", "启动失败！");
                e.printStackTrace();
            }
        }else{
            result.put("msg", "任务组等待执行或者正在执行中，请勿重复操作!");
        }
        return result;
    }

    /**
     * 删除报表跑数信息
     *
     * @param taskNos 任务组编号
     */
    @RequestMapping(value = "/deleteTaskRunData", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deleteTaskRunData(String taskNos) {
        Map<String, Object> params = Maps.newHashMap();
        this.taskRunDataBS.deleteTaskRunData(taskNos);
        params.put("msg", "删除成功");
        return params;
    }

}
