//数据校验
var str_data_dt = "";
function AfterInit(){
	if("Y"==self["v_isPOC"]){
		jsy_btns.push("POC假设校验/onCreatePOCDemoData");
	}
	
	JSPFree.createBillList("d1","/biapp-fsrs/freexml/fsrs/fsrs_result_standard_CODE1.xml",null,{list_btns:"[icon-search]校验/onValidate;[icon-p68]导出文件/onExportBySelect1;[icon-p48]查看日志/onTaskLog;$VIEW;[icon-p09]查看规则/viewRule1",list_ischeckstyle:"Y",list_ismultisel:"Y",isSwitchQuery:"N",autoquery:"N"});
	
	// 获取最新一期的日期
	var jso_data = JSPFree.getHashVOs("SELECT max(reportdate) data_dt FROM fsrs_result_standard");
	if(jso_data != null && jso_data.length > 0){
		str_data_dt = jso_data[0].data_dt;
	}
	FreeUtil.loadBillQueryData(d1_BillList, {reportdate:str_data_dt});
	JSPFree.queryDataByConditon2(d1_BillList,"reportdate ='"+str_data_dt+"'");
}

function onValidate(){
	JSPFree.openDialog("选择日期","/yujs/fsrs/supervise_date.js",350,350,null,function(_rtdata){
		if (_rtdata != null && _rtdata.code == 1) {
			$.messager.show({title:'消息提示',msg: '后台校验中，请稍后...',showType:'show'});
			JSPFree.queryDataByConditon(d1_BillList,null);  //立即查询刷新数据
		}
	});
}

function onExportAll(){
	var jsy_datas = JSPFree.getBillListAllDatas(d1_BillList);
	if(jsy_datas==null || jsy_datas.length<=0){
		$.messager.alert('提示', '当前无数据不可导出', 'warning');
		return;
	}
	
	var d1_sql_where = d1_BillList.CurrSQL.split("where")[1];
	var report_date = jsy_datas[0]["reportdate"];
	report_date = report_date.substring(0,4)+report_date.substring(5,7)+report_date.substring(8,10);
	
	var src = v_context + "/fsrs/validateresult/exportCSV?where1=" 
	+ d1_sql_where + "&reportdate=" + report_date;
	
	var download=null;
	download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);
	
	download.attr('src', src);
}

//勾选导出确定性校验信息
function onExportBySelect1(){
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_BillList);
	var d1_sql = "select s.reportdate, s.tablename, ( select r.colname from fsrs_rule r where s.colname = r.colname_en and rownum=1 ) as colname, s.colvalue, s.problemmsg, s.result_status, " +
			" ( select o.org_nm from rpt_org_info o where s.org_no = o.org_no and org_type='07' and rownum=1 ) as org_name from fsrs_result_standard s " +
			" where 1=1 and s.rid in (";
	if (jsy_datas==null || jsy_datas.length<=0) {
		onExportAll();
		return;
	} else{
		for(var i=0;i<jsy_datas.length;i++){
			if(i==jsy_datas.length-1){
				d1_sql += "'"+jsy_datas[i]["rid"]+"')";
			} else{
				d1_sql += "'"+jsy_datas[i]["rid"]+"',";
			}
		}
	}
	
	var report_date = jsy_datas[0]["reportdate"];
	report_date = report_date.substring(0,4)+report_date.substring(5,7)+report_date.substring(8,10);
	JSPFree.downloadExcelBySQL("金标校验结果-"+report_date+".xls", d1_sql, "规范性校验","数据日期,表名,字段名,字段值,问题提示,处理状态,经办机构号");
}

/**
 * 查看校验规则
 * @return {[type]} [description]
 */
function viewRule1(){
	var jso_par1 = {list_btns:"",isSwitchQuery:"N",autoquery:"Y",ishavebillquery:"Y",list_ispagebar:"Y",autocondition:"ruletype='规范性校验'"};
	var jso_par ={jso_par1:jso_par1};
	JSPFree.openDialog2("规范性校验规则","/yujs/fsrs/ViewRule.js",960,600,jso_par);
}

function onTaskLog(){
	JSPFree.openDialog("日志", "/yujs/fsrs/viewtasklog.js", 900, 600,
			null, function(_rtdata) {

	});
}