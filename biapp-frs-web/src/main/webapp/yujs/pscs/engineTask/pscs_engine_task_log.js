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

var str_sqlWhere = "";
function AfterInit() {
	JSPFree.createBillList("d1","/biapp-pscs/freexml/pscs/engineTask/pscs_engine_task_log_ref.xml");
	
	var task_id = jso_OpenPars.task_id;
	str_sqlWhere = "task_id='" + task_id + "'";
	JSPFree.queryDataByConditon(d1_BillList, str_sqlWhere);
	
	setTimeout('refresh()', 10000); // 每三秒执行一次
}

/**
 * 刷新
 * @returns
 */
function refresh() {
	JSPFree.queryDataByConditon(d1_BillList, str_sqlWhere);
}