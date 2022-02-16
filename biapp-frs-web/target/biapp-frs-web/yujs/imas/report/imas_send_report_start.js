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
function AfterInit() {
	JSPFree.createBillList("d1","/biapp-imas/freexml/report/imas_cr_report_ref_CODE.xml");
	JSPFree.queryDataByConditon(d1_BillList);
}

/**
 * 刷新数据
 * @returns
 */
function refresh() {
	JSPFree.queryDataByConditon(d1_BillList);
}
