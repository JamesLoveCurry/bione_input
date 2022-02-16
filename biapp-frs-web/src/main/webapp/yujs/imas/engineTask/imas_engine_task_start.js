/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 报送管理-报文生成：启动生成报文页面
 * 此页面提供了启动生成报文的相关操作和管理，可以允许用户启动生成报文，监控报文生成状态
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月21日
 */
var str_sqlWhere = "";
function AfterInit() {
	JSPFree.createTabb("d1", ["表任务", "规则任务"]); // 创建多页签
	JSPFree.createBillList("d1_1","/biapp-imas/freexml/engineTask/imas_engine_task_tab_ref.xml");
	JSPFree.createBillList("d1_2","/biapp-imas/freexml/engineTask/imas_engine_task_rule_ref.xml");
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

