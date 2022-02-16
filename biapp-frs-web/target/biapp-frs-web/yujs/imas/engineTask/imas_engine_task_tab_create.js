/**
 * 
 * <pre>
 * Title: 【引擎管理】
 * Description:【检核任务】--表任务--复制任务
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月17日
 */

var org_no = "";
var org_class = "";
function AfterInit() {
    org_no = jso_OpenPars.org_no;
    org_class = jso_OpenPars.org_class;
	JSPFree.createBillList("d1","/biapp-imas/freexml/engineTask/imas_engine_task_tab_create_ref.xml",["下一步/onNext/icon-p72"]);

	JSPFree.setBillListForceSQLWhere(d1_BillList, getCondition());
}

/**
 * 增加查询条件
 * @returns
 */
function getCondition() {
	var condition = " 1=1 ";
	
	if (org_class == ImasFreeUtil.getImasOrgClass().zh) {

	} else if (org_class == ImasFreeUtil.getImasOrgClass().fh) {
		// 如果是分行，则进行置灰，并赋值
		condition = " busi_org_no = '"+ org_no +"' ";
	}
	
	return condition;
}

/**
 * 【下一步】按钮
 * @returns
 */
function onNext(){
	var jso_queryValue = JSPFree.getBillQueryFormValue(d1_BillQuery);
	var str_fromDate = jso_queryValue.data_dt;  //来源日期

	var jsy_datas = JSPFree.getBillListAllDatas(d1_BillList);
	if (jsy_datas == null || jsy_datas.length<=0) {
		$.messager.alert('提示', '表格中没有一条数据,无法复制!<br>必须查询一下保证该日期内有任务数据才可以复制!', 'warning');
		return;
	}

	JSPFree.openDialog("把【" + str_fromDate + "】的任务复制到哪个目标日期","/yujs/imas/engineTask/imas_engine_task_tab_dialog_time.js", 500, 420, {fromDate:str_fromDate});
}

