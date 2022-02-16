function AfterInit(){
	JSPFree.createSplitByBtn("d1","上下",120,["确定/onConfirm","取消/onCancel"]);
	JSPFree.createBillCard("d1_A","/biapp-wmp/freexml/wmp/wmp_filling_process_CODE1_2.xml");  //卡片
	JSPFree.createBillList("d1_B","/biapp-wmp/freexml/wmp/wmp_cr_tab_ref.xml",null,{isSwitchQuery:"N",ishavebillquery:"N"});
}

function AfterBodyLoad(){
	var dom_div = document.getElementById("d1_A_BillCardDiv");
	dom_div.style.overflow="hidden";  //隐藏滚动框
	
	JSPFree.setBillQueryItemEditable("data_dt","日历",true);
}

// 确定按钮
function onConfirm(){
	var jso_cardData = JSPFree.getBillCardFormValue(d1_A_BillCard);
	if(jso_cardData.data_dt == null || jso_cardData.data_dt == ""){
		JSPFree.alert("数据日期，不能为空！");
		
		return;
	}
	
	//选择选中的数据..
	var jsy_tabs = JSPFree.getBillListSelectDatas(d1_B_BillList);
	if(jsy_tabs.length <= 0){
		JSPFree.alert("必须选择一条数据!");
		
		return;
	}
	
	//远程调用
	var jso_par = {data_dt:jso_cardData.data_dt,explain:jso_cardData.explain,allTabs:jsy_tabs,org_no:str_LoginUserOrgNo};
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.wmp.process.service.WmpCrProcessBS", "batchInsertTask", jso_par);

	JSPFree.closeDialog(jso_rt);  //关闭窗口,并有返回值
}

function onCancel(){
	JSPFree.closeDialog();
}