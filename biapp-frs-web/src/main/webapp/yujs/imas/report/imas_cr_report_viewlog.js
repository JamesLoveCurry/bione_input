/**
 * 
 * <pre>
 * Title: 【报送管理】-【报文生成】-【查看日志】
 * Description: 报送管理-报文生成-查看日志：主页面
 * 此页面提供了报送管理的查看日志相关操作，可以允许查看生成报文的日志
 * </pre>
 * @author liangzy5 
 * @version 1.00.00
   @date 2020年8月27日
 */

var str_rid = null;
var str_sql = null;
function AfterInit(){
	str_rid = jso_OpenPars.rid;
	str_sql = "report_rid='" + str_rid + "'";
	JSPFree.createBillList("d1","/biapp-imas/freexml/report/imas_cr_report_log_CODE1.xml"); //创建列表
	JSPFree.queryDataByConditon(d1_BillList,str_sql);
}

/**
 * 手动刷新
 * @returns
 */
function onRefresh(){
	str_sql = "report_rid='" + str_rid + "'";
	JSPFree.createBillList("d1","/biapp-imas/freexml/report/imas_cr_report_log_CODE1.xml");
	JSPFree.queryDataByConditon(d1_BillList,str_sql);
}
