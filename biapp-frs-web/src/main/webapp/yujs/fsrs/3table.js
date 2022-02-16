//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
function AfterInit(){
	JSPFree.createBillList("d1","/biapp-fsrs/freexml/fsrs/fsrs_cr_tab_CODE1.xml",null,{isSwitchQuery:"N"});
}

// 维护字段
function maintainCols() {
	var jso_OpenPars = d1_BillList.datagrid('getSelected');
	if (jso_OpenPars == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	//弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog("表字段","/yujs/fsrs/maintaincols.js",900,485,jso_OpenPars,function(_rtdata){
		
	});
}

// 校验表结构
function checkCols() {
	var json_data = d1_BillList.datagrid('getSelected');
	if (json_data == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var jso_Pars = {tabNameEn:json_data.tab_name_en,tabName:json_data.tab_name};

	var jsn_result = JSPFree.doClassMethodCall("com.yusys.fsrs.business.service.FsrsCrTabBS", "verificationData", jso_Pars);

	if(jsn_result.result=="false"){
		bottomRight(jsn_result.msg);
	} else{
		//弹出窗口,传入参数,然后接收返回值!
		var str_html = jsn_result.msg;
		JSPFree.openHtmlMsgBox("系统中未匹配上字段",900,560,str_html);
	}
}

//初始化检核字段
function initEastCrCol(){
	var jso_OpenPars = d1_BillList.datagrid('getSelected');
	var jso_par = {condition: jso_OpenPars.tab_name_en};
	JSPFree.doClassMethodCall("com.yusys.east.business.model.service.EastCrColBS", "initEastCrColData", jso_par);
}

//删除tab表，同时级联删除col表数据
function deleteTab(){
	var json_data = d1_BillList.datagrid('getSelected');
	if (json_data == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	JSPFree.confirm('提示', '你真的要删除选中的记录吗?', function(_isOK){
		if (_isOK){
			var jso_Pars = {tabNameEn:json_data.tab_name_en,tabName:json_data.tab_name};
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.fsrs.business.service.FsrsCrTabBS", "deleteColsByTabName", jso_Pars);
			if (jsn_result.msg == 'OK') {
				d1_BillList.datagrid('deleteRow', JSPFree.getBillListSelectRow(d1_BillList));
			}
			if (jsn_result.msg == 'OK') {
				$.messager.alert('提示', '删除数据成功!', 'info');
			}
		}
	});
}

function bottomRight(_msg){
	$.messager.show({
		title:'消息提示',
		msg:_msg,
		showType:'show'
	});
}
