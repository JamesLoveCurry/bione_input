//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
 JSPFree.createSplit("d1", "左右", 660); // 页签先左右分割

 JSPFree.createBillList("d1_A", "east_cr_rule_summary_CODE"); //
 JSPFree.createBillList("d1_B", "east_cr_tab_summary_CODE"); 
}

function createRuleData(){
	var jso_par = {type:"rule"};
  	var jso_data = JSPFree.doClassMethodCall(
				"com.yusys.east.checkresult.summary.service.EastCheckResultBS",
				"createSummaryData", jso_par);
  	if(jso_data.data == 1){
  		$.messager.alert('提示','新增数据成功!','info');
  	}
  	JSPFree.queryDataByConditon(d1_A_BillList,"");
}


function createTabData(){
	var jso_par = {type:"tab"};
	var jso_data = JSPFree.doClassMethodCall(
				"com.yusys.east.checkresult.summary.service.EastCheckResultBS",
				"createSummaryData", jso_par);
	if(jso_data.data == 1){
  		$.messager.alert('提示','新增数据成功!','info');
  	}
	JSPFree.queryDataByConditon(d1_B_BillList,""); 
}


