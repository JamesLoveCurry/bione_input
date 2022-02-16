//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/58table.js】
var str_ds = null;
var str_className = null;
var tab_name = "";
var tab_name_en = "";
function AfterInit(){
	str_ds = jso_OpenPars.ds;  //数据源
	tab_name = jso_OpenPars.tab_name;
	tab_name_en = jso_OpenPars.tab_name_en;
	
	str_className = "Class:com.yusys.pscs.datamain.Pscs11ModelTempletBuilder.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_LoginUserOrgNo + "')";

	JSPFree.createBillList("d1",str_className,null,{list_btns:"[icon-add]新增/insert1;[icon-edit]编辑/update1;[icon-remove]删除/delete1;$VIEW;",isSwitchQuery:"N"});
}

//新增
function insert1() {
	var _par = {templet:str_className,tabname:tab_name,tabnameen:tab_name_en,ds:str_ds};
	JSPFree.openDialog("新增","/yujs/pscs/datamaintain/pscsAddTabDetail.js",760,500,_par,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
		}
	},true);
}

//修改
function update1() {
	var json_rowdata = d1_BillList.datagrid('getSelected'); // 先得到数据
	
	//20200330 liangzy5 若未选中则提示先选中再编辑
	if (json_rowdata == null || json_rowdata == undefined) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}
	
	var str_date = "";
	// 若页面上定义了data_dt或者trans_date，那么这一项就肯定不等于undefined。
	var data_dt = json_rowdata.data_dt;
	var trans_date = json_rowdata.trans_date;
	
	if (data_dt != undefined) { // 如果定义了data_dt，则把data_dt的值赋给str_date
		str_date = data_dt;
	}
	
	// 在查询框中，日期是必输项，所以日期必定不为空
	str_date = str_date.substring(0, 4) + "-" + str_date.substring(4, 6) + "-" + str_date.substring(6, 8);
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.lock.service.PscsLockBSDMO","checkDateLock",{data_dt:str_date,last_date:getLastDayOfMonth(str_date)});
	if (jso_rt != null && jso_rt.status == "锁定") {
		JSPFree.alert("日期[" + str_date
				+ "]的数据已经被锁定,不能编辑!<br>请在【数据处理->数据锁定】中进行设置");
		return;
	}
	
	var defaultVal = {
			templetcode : str_className,
			tabname:tab_name,
			tabnameen:tab_name_en,
			ds:str_ds,
			data:json_rowdata // 列表数据,这样设置就是从前端取数
		};
		
	JSPFree.openDialog2("编辑","/yujs/pscs/datamaintain/pscsEditTabDetail.js",760,500,defaultVal,function(_rtdata){
		if (_rtdata == "校验并保存成功") {
			JSPFree.alert("校验并保存成功");
			JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
		}
	},true);
}

/**
 * 删除
 * @return {[type]} [description]
 */
function delete1(){
	var json_rowdata = d1_BillList.datagrid('getSelected');
	if (json_rowdata == null || json_rowdata==undefined) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	
	var str_date = "";
	// 若页面上定义了data_dt或者trans_date，那么这一项就肯定不等于undefined。
	var data_dt = json_rowdata.data_dt;
	var trans_date = json_rowdata.trans_date;
	
	if (data_dt != undefined) { // 如果定义了data_dt，则把data_dt的值赋给str_date
		str_date = data_dt;
	}
	
	// 在查询框中，日期是必输项，所以日期必定不为空
	str_date = str_date.substring(0, 4) + "-" + str_date.substring(4, 6) + "-" + str_date.substring(6, 8);
	
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.pscs.lock.service.PscsLockBSDMO","checkDateLock",{data_dt:str_date,last_date:getLastDayOfMonth(str_date)});
	if (jso_rt != null && jso_rt.status == "锁定") {
		JSPFree.alert("日期[" + str_date
				+ "]的数据已经被锁定,不能删除!<br>请在【数据处理->数据锁定】中进行设置");
		return;
	}
	
	JSPFree.doBillListDelete(d1_BillList);
}

/**
 * 参数是10位的数据日期 获取月底日期，返回一个10位的数据日期
 */
function getLastDayOfMonth(data_dt) {
	// 录入日期
	var year = data_dt.substring(0, 4);
	var month = data_dt.substring(5, 7);

	var lastDay = new Date(year, month, 0);

	return year + "-" + month + "-" + lastDay.getDate();
}