/**
 *
 * <pre>
 * Title:【报表处理】【报表数据维护】
 * Description:【单位金融机构标识码信息】列表页面
 * </pre>
 * @author wangxy31
 * @version 1.00.00
 * @date 2020年8月20日
 */

var template = "";
var tabname = "";
var tabnameen = "";
var jso_listData = "";
var str_ds = "";
var report_type = "";
var custcode = "";
var custname = "";
var data_dt = "";

var fromType = ""; // 从那个页面过来的，页面上按钮时不同的

function AfterInit() {
	type = jso_OpenPars.type;
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	str_ds = jso_OpenPars.str_ds;
	report_type = jso_OpenPars.report_type;
	custcode = jso_OpenPars.custcode;
	custname= jso_OpenPars.custname;
	data_dt = jso_OpenPars.data_dt;
	fromType = jso_OpenPars.fromType;

	// 如果页面从报表数据维护中过来，则有增删改按钮，如果不是，只有查看按钮
	template = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tabname + "','" + tabnameen + "','" + report_type + "','" + str_LoginUserOrgNo + "')";
	if ("Y" == fromType) {
		JSPFree.createBillList("d1", template, null, {list_btns:"[icon-add]新增/insert1;[icon-edit]编辑/update1;$DELETE;[icon-p81]查看/view1;",isSwitchQuery:"N",autoquery:"N",ishavebillquery:"N"});
	} else {
		JSPFree.createBillList("d1", template, null, {list_btns:"[icon-p81]查看/view1;",isSwitchQuery:"N",autoquery:"N",ishavebillquery:"N"});
	}
	d1_BillList.pagerType="2"; // 第二种分页类型
	JSPFree.queryDataByConditon(d1_BillList, "custcode = '"+custcode+"' and data_dt = '"+data_dt+"'");
	JSPFree.setBillListForceSQLWhere(d1_BillList, "custcode = '"+custcode+"' and data_dt = '"+data_dt+"'");
}

/**
 * 新增
 * @returns
 */
function insert1() {
	if (tabname.indexOf('%') != 0 && tabname.indexOf('%25') == -1) {
		tabname = tabname.replace(/%/, '%25');
	}
	var defaultVal = {type:"Add",tabname:tabname,tabnameen:tabnameen,report_type:report_type,str_ds:str_ds,custcode:custcode,custname:custname,data_dt:data_dt};
	JSPFree.openDialog3("新增","/yujs/safe/busidata/safe_check_data_edit2.js",1100,560,defaultVal,function(_rtdata){
		if (_rtdata == "CHECK_OK") {
			JSPFree.alert("校验并保存成功!");
		} else if (_rtdata == "OK") {
			JSPFree.alert("保存成功!");
		}
	},true);
}

/**
 * 修改
 * @returns
 */
function update1() {
	var json_rowdata = d1_BillList.datagrid('getSelected'); // 先得到数据
	
	if (json_rowdata == null || json_rowdata == undefined) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}

	var defaultVal = {type:"Edit",tabname:tabname,tabnameen:tabnameen,str_ds:str_ds,report_type:report_type};
	defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数
		
	JSPFree.openDialog3("编辑","/yujs/safe/busidata/safe_check_data_edit2.js",null,null,defaultVal,function(_rtdata) {
		if (_rtdata == "CHECK_OK") {
			JSPFree.alert("校验并保存成功!");
			JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
		} else if (_rtdata == "OK") {
			JSPFree.alert("保存成功!");
			JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
		}
	},true);
}

/**
 * 查看
 * @returns
 */
function view1() {
	var json_rowdata = d1_BillList.datagrid('getSelected'); // 先得到数据
	
	if (json_rowdata == null || json_rowdata == undefined) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}

	var defaultVal = {type:"View",tabname:tabname,tabnameen:tabnameen,str_ds:str_ds,report_type:report_type};
	defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数
		
	JSPFree.openDialog3("查看","/yujs/safe/busidata/safe_check_data_edit2.js",null,null,defaultVal,function(_rtdata) {
		
	},true);
}