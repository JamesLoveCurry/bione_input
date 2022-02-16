/**
 *
 * <pre>
 * Title:【配置管理】-【报表管理】
 * Description:检核数据表定义：主页面
 * 在此页面可以对外汇局报送的报表进行管理，实现新增，编辑等操作
 * </pre>
 * @author liangzy5
 * @version 1.00.00
 * @date 2020/6/15 14:03
 */

var tab_name = "";
var tab_name_en = "";
var str_subfix = "";

function AfterInit(){
	// 获取路径参数
	if (jso_OpenPars != '') {
		if(jso_OpenPars.type != null){
			str_subfix = jso_OpenPars.type; // 获取后缀
		}
	}
	
	// 获取"检核数据表"
	tab_name = SafeFreeUtil.getTableNames().SAFE_CR_TAB;
	// 获取英文表名
	var jso_data = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeCommonBS", "getTabNameByEn", {tab_name:tab_name});
	tab_name_en = jso_data.tab_name_en;
	
	var str_className = "Class:com.yusys.safe.tabmanage.template.TabConfigBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "')";
	JSPFree.createBillList("d1",str_className,null,{list_btns:"$INSERT;$UPDATE;[icon-remove]删除/deleteTab;[icon-edit]维护字段/maintainCols;" +
			"[icon-p27]校验表结构/checkCols;[icon-p20]批量生成物理表/createTables;[icon-p21]一键重新生成物理表/createAllTables;"
		,isSwitchQuery:"N",list_ischeckstyle:"Y",list_ismultisel:"Y",autoquery:"N"});
	
	JSPFree.queryDataByConditon2(d1_BillList, "tab_belong = '"+SafeFreeUtil.getSafeTabBelongto().business+"'");
	JSPFree.setBillListForceSQLWhere(d1_BillList, "tab_belong = '"+SafeFreeUtil.getSafeTabBelongto().business+"'");
}

/**
 * 维护字段
 * 对报表的各个字段信息进行维护，包括字段的中文名称，英文名称等
 * @returns
 */
function maintainCols() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length ==0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	// 弹出窗口,传入参数,然后接收返回值!
	JSPFree.openDialog("表字段","/yujs/safe/tabConfig/col_config_main.js",900,485,json_data[0],function(_rtdata){
		
	});
}

/**
 * 校验表结构
 * 对报表的物理表结构与系统中该报表的虚拟结构进行校验，判断是否有差异。
 * 若结构一致则提示校验通过，若不一致则提示差异字段
 * @returns
 */
function checkCols() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	if (json_data.length>1) {
		$.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
		return;
	}
	var jso_Pars = {tabNameEn:json_data[0].tab_name_en,tabName:json_data[0].tab_name};
	
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.tabmanage.service.SafeCrTabBS", "verificationData", jso_Pars);

	if(jsn_result.result=="false"){
		bottomRight(jsn_result.msg);
	} else{
		// 弹出窗口,传入参数,然后接收返回值
		var str_html = jsn_result.msg;
		JSPFree.openHtmlMsgBox("系统中未匹配上字段",900,560,str_html);
	}
}

/**
 * 删除按钮
 * 删除一张报表，同时级联删除该报表的safe_cr_col中的数据
 * @returns
 */
function deleteTab(){
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}
	
	JSPFree.confirm('提示', '你真的要删除选中的记录吗?', function(_isOK){
		if (_isOK){
			var jso_alltabs=[]; // 存放所有选中表名和英文表名的数组
			for(var i=0; i<json_data.length; i++){
				var jso_tab=[]; // 存放一条表名和英文表名的数组
				jso_tab.push(json_data[i].tab_name_en);
				jso_tab.push(json_data[i].tab_name);
				jso_alltabs.push(jso_tab);
			}
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.tabmanage.service.SafeCrTabBS", "deleteColsByTabName", {alltabs:jso_alltabs});
			if (jsn_result.msg == 'OK') {
				$.messager.alert('提示', '删除数据成功!', 'info');
			}
			if (jsn_result.msg == 'OK') {
				JSPFree.queryDataByConditon(d1_BillList);
			}
		}
	});
}

/**
 * 右下角弹出框，消息提示
 * @param _msg
 * @returns
 */
function bottomRight(_msg){
	$.messager.show({
		title:'消息提示',
		msg:_msg,
		showType:'show'
	});
}

/**
 * 批量生成物理表
 */
function createTables() {
	var json_data = d1_BillList.datagrid('getSelections');
	if (json_data == null || json_data == undefined || json_data.length == 0) {
		$.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
		return;
	}
	var tableNames = []; // 所有的英文表名
	for (var i = 0; i<json_data.length; i++) {
		if (json_data[i].tab_belong != '业务表') {
			$.messager.alert('提示', '只能生成业务表！', 'warning');
			return;
		}
		tableNames.push(json_data[i].tab_name_en);
	}
	$.messager.confirm('提示', '生成物理表如果已存在,则会删除原有数据,重新生成,是否继续?', function(_isConfirm) {
		if (!_isConfirm) {
			return;
		}
		var jso_data = JSPFree.doClassMethodCall("com.yusys.safe.tabmanage.service.SafeCrTabBS", "createTables", {tableNames:tableNames});
		if (jso_data.code == "success") {
			$.messager.alert('提示', '生成物理表成功!', 'info');
		} else {
			$.messager.alert('提示', jso_data.msg, 'warning');
		}

	});
}

/**
 * 一键重新生成物理表
 */
function createAllTables() {
	$.messager.confirm('提示', '一键重新生成物理表,会删除原有数据,是否继续?', function(_isConfirm) {
		if (!_isConfirm) {
			return;
		}
		var jso_data = JSPFree.doClassMethodCall("com.yusys.safe.tabmanage.service.SafeCrTabBS", "createAllTables");
		if (jso_data.code == "success") {
			$.messager.alert('提示', '重新生成物理表成功!', 'info');
		} else {
			$.messager.alert('提示', jso_data.msg, 'warning');
		}
	});
}