/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表填报】
 * Description: 报表处理-报表填报：查看处理日志
 * 列表展示子任务中的处理日志
 * </pre>
 * @author mkx
 * @version 1.00.00
   @date 2021年4月18日
 */

var child_task_id = "";
function AfterInit(){
	child_task_id = jso_OpenPars.child_task_id;
	JSPFree.createBillList("d1","/biapp-imas/freexml/fillingProcess/imas_filling_reason_CODE1.xml",null,{isSwitchQuery:"N",autoquery:"N",ishavebillquery:"N"});
	
	JSPFree.queryDataByConditon2(d1_BillList, "child_task_id = '"+child_task_id+"'");
	JSPFree.setBillListForceSQLWhere(d1_BillList,"child_task_id = '"+child_task_id+"'");
}