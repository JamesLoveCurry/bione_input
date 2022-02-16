//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/east_cr_report_query.js】
var org_class = "";
var org_no = "";
function AfterInit(){
	//通过当前登录人所属内部机构获取报送机构号
	var jso_report_org = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getReportOrgNo",{_loginUserOrgNo:str_LoginUserOrgNo,report_type:"04"});
	if(jso_report_org.msg=="ok"){
		org_no = jso_report_org.data;
	}
	
    JSPFree.createBillList("d1","/biapp-east/freexml/east/report/east_cr_report_ref.xml",null,{isSwitchQuery:"N"});
    
	JSPFree.queryDataByConditon2(d1_BillList, getQueryCondition());
	JSPFree.setBillListForceSQLWhere(d1_BillList,getQueryCondition());
	
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	var jso_org = JSPFree.getHashVOs("SELECT org_class FROM rpt_org_info where org_type='04' and is_org_report='Y' and org_no = '"+org_no+"'");
	if(jso_org != null && jso_org.length > 0){
		org_class = jso_org[0].org_class;
	}
	
	FreeUtil.loadBillQueryData(d1_BillList, {org_no:org_no});
}

//页面加载结束后
function AfterBodyLoad(){
	// 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
	if (org_class == '总行') {
		JSPFree.setBillQueryItemEditable("org_no","下拉框",true);
	} else if (org_class == '分行') {
		JSPFree.setBillQueryItemEditable("org_no","下拉框",false);
	} else {
		
	}
}

function getQueryCondition(){
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : str_LoginUserOrgNo});
	var condition = "";
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.common.service.EastGetReportBS", "getReportList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	condition += " and " + whereSql;
	return condition;
}

/**
 * 打包下载报文
 * @returns
 */
function onZipAndDownload() {
	var selectDatas = d1_BillList.datagrid('getSelections');
	if (selectDatas.length == 0) {
		JSPFree.openDialog("打包压缩下载报文", "/yujs/east/report/superviseReport_choosedate.js", 400, 350, {org_no:org_no,org_class:org_class});
	}else{
		var dates = new Array();
		var orgs = new Array();
		var addrs = new Array();
		var str_date="";
		var orgStr="";
		var addrStr="";
		var rids = "";
		for (var i=0; i<selectDatas.length; i++) {
			str_date = selectDatas[0].data_dt;
			if(dates.indexOf(selectDatas[i].data_dt)==-1){
				dates.push(selectDatas[i].data_dt);
			}
			if(orgs.indexOf(selectDatas[i].org_no)==-1 && selectDatas[i].org_no){
				orgs.push(selectDatas[i].org_no);
			}
			if(orgStr.indexOf(selectDatas[i].org_no)==-1 && selectDatas[i].org_no){
				orgStr = orgStr + selectDatas[i].org_no;
			}
			if(addrs.indexOf(selectDatas[i].addr)==-1 && selectDatas[i].addr){
				addrs.push(selectDatas[i].addr);
			}

			if(addrStr.indexOf(selectDatas[i].addr)==-1 && selectDatas[i].addr){
				addrStr = addrStr + selectDatas[i].addr;
			}
			rids = rids + selectDatas[i].rid+",";
		}

		if(dates.length>1){
			$.messager.alert('提示', '请选择同日期数据进行打包下载操作', 'info');
			return;
		}

		if(orgs.length>1){
			$.messager.alert('提示', '请选择同机构数据进行打包下载操作', 'info');
			return;
		}

		var create_type = '1';
		if(addrs.length>0){
			create_type = '2';
		}

		JSPFree.openDialog("打包压缩下载报文","/yujs/east/report/superviseReport_zip.js",780,300,{rids:rids,data_dt:str_date,org_no:orgStr,create_type:create_type,addr:addrStr,org_class:org_class});
	}

}