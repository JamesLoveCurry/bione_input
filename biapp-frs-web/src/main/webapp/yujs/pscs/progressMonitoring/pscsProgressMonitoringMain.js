//数据校验
var str_data_dt = "";
function AfterInit(){
	var jso_data = JSPFree.getHashVOs("SELECT max(reportdate) data_dt FROM pscs_result_data");
	if(jso_data != null && jso_data.length > 0){
		str_data_dt = jso_data[0].data_dt;
	}
	
	JSPFree.createBillList("d1","/biapp-pscs/freexml/pscs/progressMonitoring/pscs_result_data_monitoring.xml",null,{isSwitchQuery:"N",autoquery:"N"});
	d1_BillList.pagerType="2"; //第二种分页类型
	FreeUtil.loadBillQueryData(d1_BillList, {reportdate:str_data_dt});
	var _sql = getSql();
	JSPFree.queryDataBySQL(d1_BillList, _sql);
	JSPFree.billListBindCustQueryEvent(d1_BillList, onErrorSummary);
}

function getSql() {
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.common.PscsOrgQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	// 判断登录进来的是，总行，分行，支行
	var org_sql = "select org_nm from rpt_org_info where org_type = '11' and org_no = '"+ str_LoginUserOrgNo +"'";
	var org_nm = "";
	var org_class = "";
	var jso_org = JSPFree.getHashVOs(org_sql);
	if(jso_org != null && jso_org.length > 0){
		org_nm = jso_org[0].org_nm;
	}

	var _sql = "select reportdate,tablename,tablename_en,'"+org_nm+"' as org_name,'"+str_LoginUserOrgNo+"' as org_no,count(*) t from pscs_result_data where 1=1 ";
	if (condition != "" && condition != null) {
		_sql +=	"and " + condition;
	}	
	if (str_data_dt != "" && str_data_dt != null) {
		_sql +=	"and reportdate = '" + str_data_dt + "'";
	}
	_sql +=	"group by reportdate,tablename,tablename_en ";
	_sql +=	"order by reportdate,tablename,tablename_en ";
	
	return _sql;
}

function onErrorSummary(_condition){
	var condition = "";
	var org_nm = "";
	var org_no = "";
	if(_condition!="") {
		if (_condition.indexOf('org_no') != -1) { 
			var _org_no = JSPFree.getBillQueryFormValue(d1_BillQuery).org_no;
			var arr = _org_no.split('/');
			org_no = arr[0];
			org_nm = arr[1].substring(0,arr[1].length-1);
			_condition = _condition.substring(0, _condition.lastIndexOf('and'));
		} else {
			// 判断登录进来的是，总行，分行，支行
			var org_sql = "select org_nm from rpt_org_info where org_type = '11' and org_no = '"+ str_LoginUserOrgNo +"'";
			var jso_org = JSPFree.getHashVOs(org_sql);
			if(jso_org != null && jso_org.length > 0){
				org_no = str_LoginUserOrgNo;
				org_nm = jso_org[0].org_nm;
			}
		}
	}
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.common.PscsOrgQueryCondition","getQueryCondition",{"_loginUserOrgNo" : org_no});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	var _sql = "select reportdate,tablename,tablename_en,'"+org_nm+"' as org_name,'"+org_no+"' as org_no,count(*) t from pscs_result_data where 1=1 ";
	if (condition != "" && condition != null) {
		_sql +=	"and " + condition;
	}	
	
	if(_condition!="") {
		_sql = _sql + " and " + _condition;
	}
	_sql +=	"group by reportdate,tablename,tablename_en ";
	_sql +=	"order by reportdate,tablename,tablename_en ";
	
	JSPFree.queryDataBySQL(d1_BillList, _sql);
	FreeUtil.resetToFirstPage(d1_BillList); //手工跳转到第一页
}

//查看详情
function viewDetail(_btn) {
	var dataset = _btn.dataset;  //数据都在这个map中
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号

	var jso_OpenPars = {};
	jso_OpenPars.reportdate = row.reportdate;
	jso_OpenPars.tablename = row.tablename;
	jso_OpenPars.tablename_en = row.tablename_en;
	jso_OpenPars.org_no = row.org_no;
	jso_OpenPars.org_name = row.org_name;

	JSPFree.openDialog("查看详情","/yujs/pscs/progressMonitoring/pscsProgressMonitoringDetial.js", 900, 600, jso_OpenPars,function(_rtdata){});
}