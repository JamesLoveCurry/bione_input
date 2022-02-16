/**
 * 
 * <pre>
 * Title: 【引擎任务】-【启动监控】
 * Description: 引擎任务-启动监控页面
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月21日
 */
var str_sqlWhere = "";
function AfterInit() {
	JSPFree.createTabb("d1", ["表任务", "规则任务"]); // 创建多页签
	JSPFree.createBillList("d1_1","/biapp-east/freexml/east/engineTask/east_engine_task_tab_ref.xml");
	JSPFree.createBillList("d1_2","/biapp-east/freexml/east/engineTask/east_engine_task_rule_ref.xml");
	str_sqlWhere = "status in('排队中', '进行中', '生成明细数据')"
	JSPFree.queryDataByConditon(d1_1_BillList, str_sqlWhere);
	JSPFree.queryDataByConditon(d1_2_BillList, str_sqlWhere);
}

/**
 * 刷新数据
 * @returns
 */
function refresh() {
	JSPFree.queryDataByConditon(d1_1_BillList, str_sqlWhere);
	JSPFree.queryDataByConditon(d1_2_BillList, str_sqlWhere);
}

