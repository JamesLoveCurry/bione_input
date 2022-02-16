/**
 *
 * <pre>
 * Title:【报表处理】【报表数据维护】
 * 默认查看已报送状态的数据
 * 没有新增功能
 * Description:各个报表列表
 * </pre>
 * @author baifk
 * @version 1.00.00
 * @date 2021年03月05日
 */

var tab_name = "";
var tab_name_en = "";
var report_type = "";
var str_ds = "";
var str_className = null;
var isRptNo;
function AfterInit() {
    tab_name = jso_OpenPars.tab_name;
    tab_name_en = jso_OpenPars.tab_name_en;
    report_type = jso_OpenPars.report_type;
    str_ds = jso_OpenPars.ds;

	str_className = "Class:com.yusys.safe.analysis.template.SafeReportSpecialBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + report_type + "','" + str_LoginUserOrgNo + "')";


	var typeColumnName="";
	if ("CRD" === report_type || "CRX" === report_type) {
		typeColumnName = "oper_type_code";
	} else {
		typeColumnName = "actiontype"
	}

    // 获取子表查询按钮
	// var childBtnStr = JSPFree.getChildTabBtn(tab_name_en, report_type);
	JSPFree.createTabb("d1", [ "已报送数据", "已修改待报送数据" ]);
	// 此处要对【单位基本情况表】进行特殊处理
	if (tab_name_en =="SAFE_CORP_INFO"){
		JSPFree.createBillList("d1_1", str_className,null,{
			list_btns: "[icon-edit]编辑/update1('d1_1');[icon-p81]查看/view1('d1_1');[icon-ok1]提交/onSaveSubmit('d1_1');",
			isSwitchQuery: "N",
			autoquery: "Y",
			refWhereSQL:"bw_status = '1'"
		}); // 按表查询
		d1_1_BillList.pagerType="2"; //第二种分页类型

		JSPFree.createBillList("d1_2", str_className,null,{
			list_btns: "[icon-edit]编辑/update1('d1_2');[icon-p81]查看/view1('d1_2');[icon-ok1]提交/onSaveSubmit('d1_2');",
			isSwitchQuery: "N",
			autoquery: "Y",
			refWhereSQL:"bw_status != '1'"
		}); // 按规则查询
		d1_2_BillList.pagerType="2";  //第二种分页类型
	}else {
		JSPFree.createBillList("d1_1", str_className,null,{
			list_btns: "[icon-edit]编辑/update1('d1_1');[icon-p81]查看/view1('d1_1');[icon-ok1]提交/onSaveSubmit('d1_1');",
			isSwitchQuery: "N",
			autoquery: "Y",
			refWhereSQL:"bw_status = '1'"
		}); // 按表查询
		d1_1_BillList.pagerType="2"; //第二种分页类型

		JSPFree.createBillList("d1_2", str_className,null,{
			list_btns: "[icon-edit]编辑/update1('d1_2');[icon-p81]查看/view1('d1_2');[icon-ok1]提交/onSaveSubmit('d1_2');",
			isSwitchQuery: "N",
			autoquery: "Y",
			refWhereSQL: typeColumnName + "!='A' and bw_status != '1' "
		}); // 按规则查询
		d1_2_BillList.pagerType="2";  //第二种分页类型
	}
}
/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
	var templetVO= d1_1_BillList.templetVO;
	isRptNo = templetVO.templet_option.isrptno;
	
	// 初始化完成后，先暂时将另一个页签的机构编号id 修改
	setOrgNoAnotherId("d1_2_BillQueryNorth",'org_no_d1_2','org_no',1);
	
	JSPFree.addTabbSelectChangedListener(d1_tabb,onSelect);
}

function onSelect(_index,_title) {
	var newIndex = _index + 1;
	if (!$("#d1_1_BillQueryNorth") || !$("#d1_2_BillQueryNorth")) {
		return;
	}
	if (newIndex === 1) {
		setOrgNoAnotherId("d1_1_BillQueryNorth", 'org_no', 'org_no_d1_1', _index);
		setOrgNoAnotherId("d1_1_BillQueryNorth", 'org_no', 'org_no', _index);
		setOrgNoAnotherId("d1_2_BillQueryNorth", 'org_no_d1_2', 'org_no_d1_2', _index);
		setOrgNoAnotherId("d1_2_BillQueryNorth", 'org_no_d1_2', 'org_no', _index);
	} else if (newIndex === 2) {
		setOrgNoAnotherId("d1_2_BillQueryNorth", 'org_no', 'org_no_d1_2', _index);
		setOrgNoAnotherId("d1_2_BillQueryNorth", 'org_no', 'org_no', _index);
		setOrgNoAnotherId("d1_1_BillQueryNorth", 'org_no_d1_1', 'org_no_d1_1', _index);
		setOrgNoAnotherId("d1_1_BillQueryNorth", 'org_no_d1_1', 'org_no', _index);
	}
	
}

function setOrgNoAnotherId(divId, newId,oldId,index) {
	var div = $("#" + divId)[0];
	var elements = div.childNodes[1];
	for (var i = 0; i < elements.length; i++) {
		if (oldId === div.childNodes[1][i].id) {
			div.childNodes[1][i].id = newId;
		}
	}
}

/**
 * 修改
 * @returns
 */
function update1(_par) {
	var billList=getBillList(_par);
	var json_rowdata = billList.datagrid('getSelected'); // 先得到数据
	var currSQL = billList.CurrSQL;
	var wheresql = currSQL.split("where")[1];
	
	if (json_rowdata == null || json_rowdata == undefined) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}

	var defaultVal = {type:"Edit",tabname:tab_name,tabnameen:tab_name_en,str_ds:str_ds,report_type:report_type,actiontype_is_editable:true};
	defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数
		
	JSPFree.openDialog3("编辑","/yujs/safe/busidata/safe_check_data_edit.js",null,null,defaultVal,function(_rtdata) {
		if (_rtdata == "CHECK_OK") {
			JSPFree.queryDataByConditon(billList,wheresql);
			JSPFree.alert("校验并保存成功!");
		} else if (_rtdata == "OK") {
			JSPFree.queryDataByConditon(billList,wheresql);
			JSPFree.alert("保存成功!");
		}
	},true);
}


/**
 * 查看
 * @returns
 */
function view1(_par) {
	var billList = getBillList(_par);
	json_rowdata = billList.datagrid('getSelected'); // 先得到数据
	
	if (json_rowdata == null || json_rowdata == undefined) {
		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
		return;
	}

	var defaultVal = {type:"View",tabname:tab_name,tabnameen:tab_name_en,str_ds:str_ds,report_type:report_type};
	defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数
		
	JSPFree.openDialog3("查看","/yujs/safe/busidata/safe_check_data_edit.js",null,null,defaultVal,function(_rtdata) {
		
	},true);
}



/**
 * 查看子表信息
 * @param tabNmEn 字表表名
 */
function childView(childTabNmEn, childTabNm, btnNm, typeFlag,_par) {
	var billList = getBillList(_par);
    var json_rowdata = billList.datagrid('getSelected'); // 先得到数据

    if (json_rowdata == null || json_rowdata == undefined) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }
    var defaultVal = {tabname:childTabNm,tabnameen:childTabNmEn,typeFlag:typeFlag,str_ds:str_ds,report_type:report_type,parentTabNmEn:tab_name_en,parentRid:json_rowdata.rid,data_dt:json_rowdata.data_dt,org_no:json_rowdata.org_no,fromType:"Y"};
    JSPFree.openDialog3("查看"+btnNm,"/yujs/safe/busidata/safe_childtab_info_list.js",null,null,defaultVal,function(_rtdata) {},true);

}


/**
 * 提交
 */
function onSaveSubmit(_par) {
	var billList = getBillList(_par);
	var group = billList.datagrid('getSelected');
	if (group == null) {
		$.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
		return;
	}
	var dataStatus = group.data_status;
	if ("0" === dataStatus || !dataStatus) {
		$.messager.alert('提示', '存在校验不通过的数据，请重新选择！', 'warning');
		return;
	}
	if ("1" === group.approval_status) {
		$.messager.alert('提示', '选中记录已经提交，请重新选择！', 'warning');
		return;
	}
	if ("2" === group.approval_status) {
		$.messager.alert('提示', '选中记录已审核通过，请重新选择！', 'warning');
		return;
	}
	var selectedRidArray = [];
	selectedRidArray.push(group.rid)
	JSPFree.confirm("提示", "你真的要提交选中的记录吗?", function (_isOK) {
		if (_isOK) {
			var jso_rt = JSPFree.doClassMethodCall("com.yusys.safe.reportapproval.service.SafeReportApprovalBS", "safeSubmitReportData", {
				rids: selectedRidArray,
				tableNameEN: tab_name_en
			});
			if (jso_rt.code == "success") {
				// 刷新当前行
				JSPFree.refreshBillListCurrRow(billList);
				$.messager.show({title: '消息提示', msg: '提交成功', showType: 'show'});
			} else {
				JSPFree.alert(jso_rt.msg);
			}
		}
	});
}

/**
 * 根据已报送、修改取数据
 */
function getBillList(_par){
	if (_par==="d1_1"){
		return d1_1_BillList;
	}
	else if (_par==="d1_2"){
		return d1_2_BillList;
	}
}

String.prototype.replaceAll = function(s1, s2) {
    return this.replace(new RegExp(s1, "gm"), s2);
}