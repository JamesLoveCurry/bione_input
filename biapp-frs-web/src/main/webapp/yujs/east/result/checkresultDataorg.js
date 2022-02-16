//初始化界面,菜单配置url路径是【/frs/yufreejs?js=/yujs/east/warningLevel.js】
var tabName = "";
var dataDt = "";
var orgNo = null;
var orgType = null;
var orgClass = null;
var busiType = "";

function AfterInit(){
	tabName = jso_OpenPars.tab_name;
	dataDt=jso_OpenPars.data_dt;
	orgNo = jso_OpenPars.org_no;
	orgType = jso_OpenPars.org_type;
	orgClass = jso_OpenPars.org_class;
	
	JSPFree.createBillList("d1","/biapp-east/freexml/east/result/v_east_cr_summary6.xml");
	
	if (orgType == "Y") {
		if (orgClass == "总行") {
			busiType = "1_" + orgNo;
		} else if (orgClass == "分行") {
			busiType = "2_" + orgNo;
		}
	} else {
		busiType = "1_";
	}
	
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : orgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}
	
	var str_sqlWhere = "org_no = '"+orgNo+"' and tab_name='"  + tabName + "' and data_dt='"+dataDt+"' and "+condition;  //拼SQL条件
	JSPFree.queryDataByConditon(d1_BillList,str_sqlWhere);  //锁定规则表查询数据
}

/**
 * 历史
 * @returns
 */
function historyFn(_btn){
	var dataset = _btn.dataset;  //数据都在这个map中
	var index = dataset.rowindex;
	var rows = d1_BillList.datagrid("getRows");
	var row = rows[index];//index为行号
	
	var jso_OpenPars = {};
	jso_OpenPars.org_nm = row.org_nm;
	jso_OpenPars.tab_name = tabName;
	jso_OpenPars.data_dt = dataDt;
	jso_OpenPars.org_no = orgNo;
	jso_OpenPars.busi_type = busiType;
	
	JSPFree.openDialog(row.tab_name,"/yujs/east/result/checkresultInterOrgHistory.js", 700, 450, jso_OpenPars,function(_rtdata){});
}

/**
 * 导出
 * @returns
 */
function exports(){
	var download=null;
	download = $('<iframe id="download" style="display: none;"/>');
	$('body').append(download);
	
	var src = v_context + "/east/interface/order/orgdownload?tabName="
			+ tabName
			+ "&dataDt="
			+ dataDt;
	download.attr('src', src);
}