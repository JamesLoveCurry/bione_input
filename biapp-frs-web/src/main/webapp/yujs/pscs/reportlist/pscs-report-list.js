//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/east_report_list.js】
function AfterInit(){
    // 获取最新一期的日期
    var data_dt1 = "";
	//var jso_data = JSPFree.getHashVOs("select max(data_dt) data_dt from pscs_report_list");
//    if(jso_data != null && jso_data.length > 0){
//    	data_dt = jso_data[0].data_dt;
//    }
    
    JSPFree.createBillList("d1","/biapp-pscs/freexml/pscs/reportlist/pscs_report_list_CODE.xml",null,{autoquery:"N",isSwitchQuery:"N"});
    //FreeUtil.loadBillQueryData(d1_BillList, {data_dt:data_dt,org_no:str_LoginUserOrgNo});
    //var _sql = "select * from pscs_report_list where data_dt='"+jso_data[0].data_dt+"' and org_no='"+str_LoginUserOrgNo+"'";
	//JSPFree.queryDataBySQL(d1_BillList, _sql);
//	JSPFree.setBillListForceSQLWhere(d1_BillList, "org_no='"+str_LoginUserOrgNo+"'");
    JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition());
    JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());
}

function getQueryCondition(){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.common.PscsOrgQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	return condition;
}

//页面加载结束后
function AfterBodyLoad(){
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	var org_class = "";
	var jso_org = JSPFree.getHashVOs("SELECT org_class FROM rpt_org_info where org_type='11' and org_no = '"+str_LoginUserOrgNo+"'");
	if(jso_org != null && jso_org.length > 0){
		org_class = jso_org[0].org_class;
	}
	if (org_class != '总行') {
		JSPFree.setBillQueryItemEditable("org_no","下拉框",false);
	}
}

/**
 * 导出-若选择了数据则部分导出，若没选择则导出当前日期
 * @returns
 */
function onDownload(){
	var jsy_datas = JSPFree.getBillListSelectDatas(d1_BillList);
	var _sql = "SELECT * FROM pscs_report_list WHERE 1=1 AND RID IN (";
	
	if (jsy_datas==null || jsy_datas.length<=0) {
		downloadQuery();
		return;
	} else{
		for(var i=0;i<jsy_datas.length;i++){
			if(i==jsy_datas.length-1){
				_sql += "'"+jsy_datas[i]["rid"]+"')";
			} else{
				_sql += "'"+jsy_datas[i]["rid"]+"',";
			}
		}
	}
	var str_data_dt = jsy_datas[0]["data_dt"];
	var str_bank_name = jsy_datas[0]["org_name"];
	
	var _downloadName = str_bank_name+"-监管标准化数据报送清单-"+str_data_dt+".xlsx";
	var jso_par = {
			FileName : "29-PscsReportList.xlsx", //指定excel模板文件名称
			DownloadName : _downloadName, //下载文件名称
			SQL : _sql,
			Ds : null,
			HeadRow : "2" //表头占的行数
		};
	JSPFree.downloadFile("com.yusys.pscs.reportlist.ExportReportListAsExcelDMO", _downloadName, jso_par); //
}

/**
 * 导出查询日期当天的全部数据。默认的查询日期是最新日期，其他情况则是查询框中的日期
 * @returns
 */
function downloadQuery(){
	if (d1_BillList.CurrSQL == null || "undefined" == d1_BillList.CurrSQL) {
		JSPFree.alert("导出失败！当前页面没有数据，请确认是否有数据或点击数据日期进行查询！");
		return;
	}
	var jsy_datas = JSPFree.getBillListAllDatas(d1_BillList);
	
	if(jsy_datas[0]==null || jsy_datas[0]==undefined){
		JSPFree.alert("导出失败！当前页面没有查询记录，请确认是否有数据或点击数据日期进行查询！");
		return;
	}
	
	var str_data_dt = jsy_datas[0]["data_dt"];
	var str_bank_name = jsy_datas[0]["org_name"];
	
	var _downloadName = str_bank_name+"-监管标准化数据报送清单-"+str_data_dt+".xlsx";
	var jso_par = {
			FileName : "29-PscsReportList.xlsx", //指定excel模板文件名称
			DownloadName : _downloadName, //下载文件名称
			SQL : d1_BillList.CurrSQL,
			Ds : null,
			HeadRow : "2" //表头占的行数
		};
	JSPFree.downloadFile("com.yusys.pscs.reportlist.ExportReportListAsExcelDMO", _downloadName, jso_par); //
}
