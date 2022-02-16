//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/east_cr_report_config_inserts.js】
//函数
var array = new Array(1);
var map = {};
map['id'] = "日报";
map['name'] = "日报";
var map1 = {};
map1['id'] = "月报";
map1['name'] = "月报";
array[0] = map;
array[1] = map1;
			
function AfterInit(){
	JSPFree.createSplitByBtn("d1","上下",50,["确定/onConfirm","取消/onCancel"]);
	JSPFree.createBillCard("d1_A","/biapp-east/freexml/east/report/east_cr_report_config_CODE1.xml",null,{onlyItems:"report_type"});  //卡片
	JSPFree.createBillTree("d1_B","/biapp-east/freexml/east/rpt_org_info_CODE1.xml",{isCheckbox:true});
}

function AfterBodyLoad(){
	var dom_div = document.getElementById("d1_A_BillCardDiv");
	dom_div.style.overflow="hidden";  //隐藏滚动框
}

function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);

	//选择树中的的数据..
	var jsy_orgs = JSPFree.getBillTreeCheckedDatas(d1_B_BillTree);
	if(jsy_orgs.length<=0){
		JSPFree.alert("必须选择一个机构!");
		return;
	}
	
	//远程调用
	var jso_par = {report_type:jso_cardData.report_type,allOrgs:jsy_orgs};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.EastCrReportBSDMO", "batchInsertReportConfig", jso_par);

	JSPFree.closeDialog(jso_rt);  //关闭窗口,并有返回值
}

function onCancel(){
	JSPFree.closeDialog();
}