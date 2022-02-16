/**
 *
 * <pre>
 * Title: 【引擎管理】
 * Description:【检核任务】--查看日志
 * </pre>
 * @author wangxy31
 * @version 1.00.00
 @date 2020年8月17日
 */
var sql = "";
var str_sqlWhere = "";

function AfterInit() {
    JSPFree.createBillList("d1", "/biapp-bfd/freexml/engineTask/bfd_engine_task_log_ref.xml");
    
    var task_id = jso_OpenPars.task_id;
    str_sqlWhere = "task_id='" + task_id + "'";
    
    var param = {
        taskId: jso_OpenPars.task_id,
        tabNames: jso_OpenPars.tabNames,
        dataDt: jso_OpenPars.dataDt,
        str_sqlWhere: str_sqlWhere,
        status: jso_OpenPars.status
    }
    var result = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngineLogViewHandle", "handleTaskLog", param);
    sql = result.sql;
    JSPFree.queryDataBySQL(d1_BillList, sql);
    
    setTimeout('refresh()', 10000); // 每三秒执行一次
}

function AfterBodyLoad() {
}

/**
 * 刷新
 * @returns
 */
function refresh() {
    JSPFree.queryDataBySQL(d1_BillList, sql);
}