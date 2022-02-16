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
var org_no = "";

var parentRid = "";
var typeFlag = "";
var parentTabNmEn = "";
var queryParam = "";

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
	org_no = jso_OpenPars.org_no;
	fromType = jso_OpenPars.fromType;
	// 主表数据RID
    parentRid = jso_OpenPars.parentRid;
    // 字段l关联类型标识：0字表，1字典
    typeFlag = jso_OpenPars.typeFlag;
    parentTabNmEn = jso_OpenPars.parentTabNmEn;

	// 如果页面从报表数据维护中过来，则有增删改按钮，如果不是，只有查看按钮
	template = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tabname + "','" + tabnameen + "','" + report_type + "','" + str_LoginUserOrgNo + "')";
	var btnStr = "";
	if ("Y" == fromType) {
		if (typeFlag == '1') { // 字典项，无需修改操作
            btnStr = "[icon-add]新增/insert1;[icon-remove]删除/delete1;[icon-p81]查看/view1;";
        } else {
            btnStr = "[icon-add]新增/insert1;[icon-edit]编辑/update1;[icon-remove]删除/delete1;[icon-p81]查看/view1;";
        }
	} else if ("TB" == fromType) {// 填报页面，只能进行编辑或者查看
		btnStr = "[icon-edit]编辑/update1;[icon-p81]查看/view1;";
    } else {
        btnStr = "[icon-p81]查看/view1;";
	}
    JSPFree.createBillList("d1", template, null, {list_btns:btnStr,isSwitchQuery:"N",autoquery:"N",ishavebillquery:"N"});

    d1_BillList.pagerType="2"; // 第二种分页类型
	/*  执行数据查询sql示例：
	  select A.*
      from SAFE_CORP_FIN_INFO A
      LEFT JOIN SAFE_CR_DATA_RELA B
      ON A.RID = B.CHILD_RID
      WHERE 1 = 1
       and B.PARENT_TABNM_EN = 'SAFE_CORP_INFO'
       AND B.PARENT_REPORT_TYPE = 'BOP'
       AND B.PARENT_RID = 'f97304dd-3b40-4d02-ae30-ea2c5412bb8a'
	* */
    // 添加查询参数
	queryParam = "B.PARENT_TABNM_EN= '" +parentTabNmEn+"' AND B.PARENT_REPORT_TYPE LIKE '%" +report_type+ "%' AND B.PARENT_RID='" +parentRid+"'";
	queryDataByRid(d1_BillList, queryParam);
	JSPFree.setBillListForceSQLWhere(d1_BillList, queryParam);
}

/**
 * 新增
 * @returns
 */
function insert1() {
	if (tabname.indexOf('%') != 0 && tabname.indexOf('%25') == -1) {
		tabname = tabname.replace(/%/, '%25');
	}
	var defaultVal = {type:"Add",tabname:tabname,tabnameen:tabnameen,report_type:report_type,str_ds:str_ds,parentTabNmEn:parentTabNmEn,parentRid:parentRid,data_dt:data_dt,org_no:org_no};
	JSPFree.openDialog("新增","/yujs/safe/busidata/safe_check_childdata_edit.js",1100,560,defaultVal,function(_rtdata){
        if (_rtdata == "CHECK_OK") {
			JSPFree.alert("校验并保存成功!");
		} else if (_rtdata == "OK") {
			JSPFree.alert("保存成功!");
		}
        queryDataByRid(d1_BillList, queryParam);
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

	var defaultVal = {type:"Edit",tabname:tabname,tabnameen:tabnameen,str_ds:str_ds,report_type:report_type,data_dt:data_dt,org_no:org_no,parentTabNmEn:parentTabNmEn,parentRid:parentRid};
	defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数
		
	JSPFree.openDialog("编辑","/yujs/safe/busidata/safe_check_childdata_edit.js",1100,560,defaultVal,function(_rtdata) {
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
 * 子表数据删除
 *  create by wm 2021-4-12 16:08:19
 */
function delete1(){
    var json_rowdata = d1_BillList.datagrid('getSelected');
    if (json_rowdata == null || json_rowdata==undefined) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if ("1" === json_rowdata.approval_status) {
        $.messager.alert('提示', '已提交记录不能删除，请重新选择！', 'warning');
        return;
    }
    doBillListDeleteChild(d1_BillList, parentTabNmEn, parentRid, report_type);
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
		
	JSPFree.openDialog3("查看","/yujs/safe/busidata/safe_check_childdata_edit.js",null,null,defaultVal,function(_rtdata) {
		
	},true);
}

// 子表中删除数据 create by wm 2021-4-12 16:11:28
var doBillListDeleteChild = function(_grid, parentTabNmEn, parentRid, reportType) {
    var json_rowdata = _grid.datagrid('getSelected'); // 先得到数据
    if (json_rowdata == null) {
        $.messager.alert('提示', '必须选择一条数据!', 'info');
        return;
    }

    var str_divid = _grid.divid; //
    var str_beforeDeleteFn = "beforeDelete_" + str_divid + "_BillList"; // 删除前校验
    if (typeof self[str_beforeDeleteFn] == "function") { // 如果有这个函数
        var isOK = self[str_beforeDeleteFn](_grid, json_rowdata); // 执行
        if (!isOK) {
            return; // 如果失败则返回
        }
    }

    // 警告提醒是否真的删除?
    $.messager.confirm('提示', '你真的要删除选中的记录吗?', function(_isConfirm) {
        if (!_isConfirm) {
            return;
        }

        var str_rownumValue = json_rowdata['_rownum']; // 取得行号数据
        var int_selrow = _grid.datagrid("getRowIndex", str_rownumValue); // 根据_rownum的值,计算出是第几行,后面刷新就是这一行!

        var jso_templetVO = _grid.templetVO; // 模板对象
        var str_ds = jso_templetVO.templet_option.ds; // 数据源
        var str_templetcode = jso_templetVO.templet_option.templetcode; // 模板编码
        var str_savetable = jso_templetVO.templet_option.savetable; // 保存的表名,删除时不要重新查模板了,直接送表名,性能高一点!
        var str_sqlwhere = FreeUtil.getSQLWhereByPK(jso_templetVO,
            json_rowdata); // 拼出SQL
        // 远程调用,真正删除数据库
        try {
            execDeleteBillListdDataChild(str_ds, str_templetcode,
                str_savetable, str_sqlwhere, parentTabNmEn, parentRid, reportType, json_rowdata.rid);

            // 从界面上删除行
            _grid.datagrid('deleteRow', int_selrow);
            $.messager.alert('提示', '删除数据成功!', 'info');
        } catch (_ex) {
            console.log(_ex);
            FreeUtil.openHtmlMsgBox("发生异常", 500, 250, _ex)
        }
    });
};


// 子表中删除数据 create by wm 2021-4-12 16:11:28
var execDeleteBillListdDataChild = function (_ds, _templetcode, _table, _sqlwhere, parentTabNmEn, parentRid, reportType, childRid) {
    var jso_par = {ds: _ds, templetcode: _templetcode, savetable: _table, SQLWhere: _sqlwhere,parentTabNmEn:parentTabNmEn, parentRid:parentRid, reportType: reportType,childRid: childRid };  //
    var jso_rt = FreeUtil.doClassMethodCall("com.yusys.safe.business.service.SafeBusinessBS", "deleteBillListDataChild", jso_par);  //保存数据
};
// 根据数据关联表查询子表数据 -- create by wm 2021-4-12 10:39:43
var queryDataByRid= function(_grid, _cons) {
    queryDataByRidReal(_grid, _cons, false, 1, null, true);
};

// 根据数据关联表查询字表数据 -- create by wm 2021-4-12 10:39:43
var queryDataByRidReal = function (_grid, _cons, _isYiBu, _currPage, _orderBy, _isSpanXmlCons) {
    var str_templetcode = _grid.templetVO.templet_option.templetcode;  //取得模板编码
    var str_table = _grid.templetVO.templet_option.fromtable;  //取得模板编码
    var isPager = true;  //默认是分页
    if ("N" == _grid.templetVO.templet_option.list_ispagebar) {
        isPager = false;  //不分页
    }

    //先把表名拼出来
    var str_sql = "select A.* from " + str_table + " A LEFT JOIN SAFE_CR_DATA_RELA B ON A.RID = B.CHILD_RID WHERE 1=1 ";

    //如果列表在前端设置了强制条件则加上,即有时需要在前端再加上强制条件
    if (typeof _grid.forceSQLWhere != "undefined" && _grid.forceSQLWhere != null && _grid.forceSQLWhere != "") {
        str_sql = str_sql + " and " + _grid.forceSQLWhere;  //
    }

    //拼接条件
    if (_cons != undefined && _cons != null && _cons != "") {
        str_sql = str_sql + " and " + _cons;  //
    }

    //拼接模板中配置的条件
    //xml模板中定义的查询条件,如果有,则与传入的条件再合并!
    // if (typeof _isSpanXmlCons != "undefined" && _isSpanXmlCons) {
    //     var str_xmlCons = _grid.templetVO.templet_option.querycontion;
    //     if (typeof str_xmlCons != "undefined" && str_xmlCons != null && str_xmlCons.trim() != "") {
    //         str_sql = str_sql + " and " + str_xmlCons;  //
    //     }
    // }

    //处理排序,如果入参指定了排序,则使用入参的,否则使用模板中定义的
    if (typeof _orderBy != "undefined" && _orderBy != null && _orderBy != "") {
        str_sql = str_sql + " order by " + _orderBy;
    } else {
        var str_orderby = _grid.templetVO.templet_option.orderbys;
        if (typeof str_orderby != "undefined" && str_orderby != null && str_orderby != "") {
            str_sql = str_sql + " order by " + str_orderby;
        }
    }

    FreeUtil.queryDataByConditonRealBySQL(_grid, str_sql, _isYiBu, isPager, _currPage);
};