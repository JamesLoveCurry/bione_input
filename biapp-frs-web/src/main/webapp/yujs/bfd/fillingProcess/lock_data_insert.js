var org_no = "";
var org_class = "";
var org_nm = "";

function AfterInit() {
	org_no = jso_OpenPars.org_no;
	org_class = jso_OpenPars.org_class;
	org_nm = jso_OpenPars.org_nm;

    JSPFree.createBillCard("d1","/biapp-bfd/freexml/fillingProcess/bfd_lock_data_insert.xml",["确定/onConfirm","取消/onCancel"]);
}

// 创建任务
function onConfirm(){
    var jso_rt = null;

    var jso_cardData = JSPFree.getBillCardFormValue(d1_BillCard);
    if(jso_cardData.tab_name==null || jso_cardData.tab_name==""){
        JSPFree.alert("报表不能为空!");
        return;
    }
    if(jso_cardData.data_dt==null || jso_cardData.data_dt==""){
        JSPFree.alert("数据日期不能为空!");
        return;
    }

    var str_org = jso_cardData.org_no;
	if (str_org == null || str_org == "") {
        $.messager.alert('提示', '必须选择机构!', 'info');
        return;
    }

	var status = jso_cardData.status;
	if (status == null || status == "") {
        $.messager.alert('提示', '必须加锁类型!', 'info');
        return;
    }

    var arr_org = str_org.split(";");
    var orgStr = ""; //970110,990100，后面可以考虑这里验证机构号！！因为自定义参照放开了编辑，str_org不指定能拿到什么东西呢
	//但是当前对程序无碍，不影响运行
	for(var i=0; i<arr_org.length; i++){
		orgStr = orgStr + arr_org[i].substring(0, arr_org[i].indexOf("/"))+",";
	}

	var arr_tab = jso_cardData.tab_name.split(";");
    var tabStr = "";
	//但是当前对程序无碍，不影响运行
	for(var i=0; i<arr_tab.length; i++){
		tabStr = tabStr + arr_tab[i]+",";
	}

    jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdLockData", "createLockData", {status:status,str_org:orgStr,tab_name:tabStr,data_dt:jso_cardData.data_dt,operator:str_LoginUserCode,operator_name:str_LoginUserName});

    JSPFree.closeDialog(jso_rt);
}

function onCancel(){
    JSPFree.closeDialog(null);
}