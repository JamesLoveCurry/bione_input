/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】
 * Description: 报送管理-报文生成：启动生成报文页面
 * 此页面提供了启动生成报文的相关操作和管理，可以允许用户启动生成报文，监控报文生成状态
 * </pre>
   @date 2021年10月22日
 */
var str_sqlWhere = "";
function AfterInit() {
	JSPFree.createBillList("d1","/biapp-crrs/freexml/crrs/engineTask/crrs_engine_task_ref.xml");
	str_sqlWhere = "status in('排队中', '进行中', '生成明细数据')"
	JSPFree.queryDataByConditon(d1_BillList, str_sqlWhere);
}

/**
 * 刷新数据
 * @returns
 */
function refresh() {
	JSPFree.queryDataByConditon(d1_BillList, str_sqlWhere);
}
