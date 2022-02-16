//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/east_cr_report_config_inserts.js】
function AfterInit(){
	JSPFree.createSplitByBtn("d1","上下",50,["确定/onConfirm","取消/onCancel"]);
	JSPFree.createBillCard("d1_A","/biapp-fsrs/freexml/fsrs/fsrs_cr_report_config_CODE1.xml",null,{onlyItems:"report_type"});  //卡片
	JSPFree.createBillTree("d1_B","/biapp-fsrs/freexml/fsrs/rpt_org_info_CODE1.xml",{isCheckbox:true});
}

function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
	if(jso_cardData.report_type==null || jso_cardData.report_type==""){
		JSPFree.alert("报送频率不能为空!");
		return;
	}
	
	//选择树中的的数据..
	var jsy_orgs = JSPFree.getBillTreeCheckedDatas(d1_B_BillTree);
	if(jsy_orgs.length<=0){
		JSPFree.alert("必须选择一个机构!");
		return;
	}
	
	//远程调用
	var jso_par = {report_type:jso_cardData.report_type,allOrgs:jsy_orgs};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.fsrs.report.service.FsrsCrReportBSDMO", "batchInsertReportConfig", jso_par);
	JSPFree.closeDialog(jso_rt);  //关闭窗口,并有返回值
}

function onCancel(){
	JSPFree.closeDialog();
}