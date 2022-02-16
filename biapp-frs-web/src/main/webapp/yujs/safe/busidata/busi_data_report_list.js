/**
 *
 * <pre>
 * Title:【报表处理】【报表数据维护】
 * Description:各个报表列表
 * </pre>
 * @author wangxy31
 * @version 1.00.00
 * @date 2020年6月28日
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
    
    var typeColumnName = "";
    var contion = "";
    if ("CRD" === report_type || "CRX" == report_type) {
        typeColumnName = "oper_type_code";
    } else {
        typeColumnName = "actiontype"
    }
    if (tab_name_en != "SAFE_CORP_INFO") {
        contion = typeColumnName + "= 'A' and bw_status!='1' ";
    } else {
        contion = " bw_status!='1'  ";
    }
    // 此处要对【单位基本情况表】进行特殊处理
    str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tab_name + "','" + tab_name_en + "','" + report_type + "','" + str_LoginUserOrgNo + "')";
    
    // if (SafeFreeUtil.getTabNameEn() == tab_name_en) {
    // 	JSPFree.createBillList("d1", str_className, null, {
    // 		list_btns: "[icon-add]新增/insert1;[icon-edit]编辑/update1;[icon-remove]删除/delete1;[icon-p81]查看/view1;[icon-p81]开户|经办信息/childView;[icon-ok1]提交/onSaveSubmit;[icon-p68]导入excel/importExcel(this)",
    // 		isSwitchQuery: "N",
    // 		autoquery: "N",
    // 		querycontion: contion+" and bw_status != '1' ",// 只展示操作类型未 新建的数据
    // 		autocondition:contion+" and bw_status != '1' "
    // 	});
    // } else {
    // 	JSPFree.createBillList("d1", str_className, null, {
    // 		list_btns: "[icon-add]新增/insert1;[icon-edit]编辑/update1;[icon-remove]删除/delete1;[icon-p81]查看/view1;[icon-ok1]提交/onSaveSubmit;[icon-p68]导入excel/importExcel(this)",
    // 		isSwitchQuery: "N",
    // 		autoquery: "N",
    // 		querycontion: contion+" and bw_status != '1' ",
    // 		autocondition:contion+" and bw_status != '1' "
    // 	});
    // }
    // 获取子表查询按钮
    // var childBtnStr = JSPFree.getChildTabBtn(tab_name_en, report_type);
    var orgClass = SafeFreeUtil.getOrgClass(str_LoginUserOrgNo, report_type);;
    var buttons = "[icon-add]新增/insert1;[icon-edit]编辑/update1;[icon-remove]删除/delete1;[icon-p81]查看/view1;[icon-ok1]提交/onSaveSubmit;[icon-p68]导入excel/importExcel(this);";
    JSPFree.createBillList("d1", str_className, null, {
        list_btns: buttons,
        isSwitchQuery: "N",
        autoquery: "Y",
        list_ischeckstyle: "Y",
        list_ismultisel: "Y",
        refWhereSQL: contion
        // querycontion: contion,// 只展示操作类型未报送和新建的数据
        // autocondition: contion
    });
    
}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
    isRptNo = d1_BillList.templetVO.templet_option.isrptno;
}

/**
 * 新增
 * @returns
 */
function insert1() {
    if (tab_name.indexOf('%') != 0 && tab_name.indexOf('%25') == -1) {
        tab_name = tab_name.replace(/%/, '%25');
    }
    var defaultVal = {
        type: "Add",
        tabname: tab_name,
        tabnameen: tab_name_en,
        report_type: report_type,
        str_ds: str_ds,
        isRptNo: isRptNo
    };
    JSPFree.openDialog3("新增", "/yujs/safe/busidata/safe_check_data_edit.js", 1100, 560, defaultVal, function (_rtdata) {
        if (_rtdata == "CHECK_OK") {
            JSPFree.alert("校验并保存成功!");
        } else if (_rtdata == "OK") {
            JSPFree.alert("保存成功!");
        }
    }, true);
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
    
    var defaultVal = {
        type: "Edit", tabname: tab_name, tabnameen: tab_name_en, str_ds: str_ds, report_type: report_type,
        actiontype_is_editable: false
    };
    defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数
    
    JSPFree.openDialog3("编辑", "/yujs/safe/busidata/safe_check_data_edit.js", null, null, defaultVal, function (_rtdata) {
        if (_rtdata == "CHECK_OK") {
            JSPFree.alert("校验并保存成功!");
            JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
        } else if (_rtdata == "OK") {
            JSPFree.alert("保存成功!");
            JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
        }
    }, true);
}

function delete1() {
    var json_rowdata = d1_BillList.datagrid('getSelected');
    if (json_rowdata == null || json_rowdata == undefined) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if ("1" === json_rowdata.approval_status) {
        $.messager.alert('提示', '已提交记录不能删除，请重新选择！', 'warning');
        return;
    }
    doBillListDelete(d1_BillList);
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
    
    var defaultVal = {
        type: "View",
        tabname: tab_name,
        tabnameen: tab_name_en,
        str_ds: str_ds,
        report_type: report_type
    };
    defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数
    
    JSPFree.openDialog3("查看", "/yujs/safe/busidata/safe_check_data_edit.js", null, null, defaultVal, function (_rtdata) {
        
    }, true);
}

/**
 * 查看子信息
 * @returns
 */
// function childView() {
// 	var json_rowdata = d1_BillList.datagrid('getSelected'); // 先得到数据
//
// 	if (json_rowdata == null || json_rowdata == undefined) {
// 		$.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
// 		return;
// 	}
//
// 	var tabname = "单位金融机构标识码信息表" ;
// 	var tabnameen = "SAFE_CORP_FIN_INFO";
// 	var defaultVal = {tabname:tabname,tabnameen:tabnameen,str_ds:str_ds,report_type:report_type,custcode:json_rowdata.custcode,custname:json_rowdata.custname,data_dt:json_rowdata.data_dt,fromType:"Y"};
// 	JSPFree.openDialog3("单位金融机构标识码信息","/yujs/safe/busidata/safe_corp_fin_info_list.js",null,null,defaultVal,function(_rtdata) {},true);
// }

/**
 * 查看子表信息
 * @param tabNmEn 字表表名
 */
function childView(childTabNmEn, childTabNm, btnNm, typeFlag) {
    var json_rowdata = d1_BillList.datagrid('getSelected'); // 先得到数据
    
    if (json_rowdata == null || json_rowdata == undefined) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }
    var defaultVal = {
        tabname: childTabNm,
        tabnameen: childTabNmEn,
        typeFlag: typeFlag,
        str_ds: str_ds,
        report_type: report_type,
        parentTabNmEn: tab_name_en,
        parentRid: json_rowdata.rid,
        data_dt: json_rowdata.data_dt,
        org_no: json_rowdata.org_no,
        fromType: "Y"
    };
    JSPFree.openDialog3("查看" + btnNm, "/yujs/safe/busidata/safe_childtab_info_list.js", null, null, defaultVal, function (_rtdata) {
    }, true);
    
}

/**
 * 导入
 * @return {[type]} [description]
 */
function importExcel() {
    JSPFree.openDialog("文件上传", "/yujs/safe/busidata/busi_data_report_import.js", 500, 300, {
        tab_name: tab_name,
        tab_name_en: tab_name_en,
        report_type: report_type
    }, function (_rtdata) {
        if (_rtdata == "success") {
            //JSPFree.queryDataByConditon(d1_BillList,null);  //立即查询刷新数据
            JSPFree.alert("导入成功！");
        }
    });
}

/**
 * 提交
 */
function onSaveSubmit() {
    var currentSelections = d1_BillList.datagrid('getSelections');
    if (currentSelections.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    // 判断当前报表是否存在子表
    var isExistsChildrenTableResult = JSPFree.doClassMethodCall("com.yusys.safe.reportapproval.service.SafeReportApprovalBS", "mainTableIsExistsChildTable", {
        reportType: report_type,
        tableNameEN: tab_name_en
    });
    var isExistsChildrenTable = isExistsChildrenTableResult.flag;
    
    for (var i = 0; i < currentSelections.length; i++) {
        var temp = currentSelections[i];
        var dataStatus = temp.data_status;
       
        if (dataStatus === '0' || !dataStatus) {
            $.messager.alert('提示', '存在校验不通过或未校验的数据，无法提交，请重新选择！', 'warning');
            return;
        }
        // 存在子表则校验子表是否存在校验未通过的数据
        if (isExistsChildrenTable) {
            var mainRid = temp.rid;
            var result = JSPFree.doClassMethodCall("com.yusys.safe.reportapproval.service.SafeReportApprovalBS",
                "childrenTableIsCheckNotPassByMainRid", {
                    mainTableRid: mainRid
                });
            var tabName = result.tabName;
            if ("ok" === result.status && tabName) {
                $.messager.alert('提示', '子表【' + tabName + '】存在校验未通过数据，无法提交！', 'warning');
                return;
            } else {
                $.messager.alert('提示', '检查当前主表下子表是否存在校验未通过数据出错！', 'warning');
                return;
            }
        }
    }
    
    var selectedRidArray = [];
    getCurrentSelectedRidArray(currentSelections, selectedRidArray);
    if (selectedRidArray.length == 0) {
        $.messager.alert('提示', '选择的数据中，不存在需要提交的数据，请重新选择！', 'warning');
        return;
    }
    JSPFree.confirm("提示", "你真的要提交选中的记录吗?", function (_isOK) {
        if (_isOK) {
            var jso_rt = JSPFree.doClassMethodCall("com.yusys.safe.reportapproval.service.SafeReportApprovalBS", "safeSubmitReportData", {
                rids: selectedRidArray,
                tableNameEN: tab_name_en
            });
            if (jso_rt.code == "success") {
                // 刷新当前行
                JSPFree.refreshBillListCurrPage(d1_BillList);
                $.messager.show({title: '消息提示', msg: '提交成功', showType: 'show'});
            } else {
                JSPFree.alert(jso_rt.msg);
            }
        }
    });
}

/**
 * 校验
 * @returns
 */
function validateTask(){
    var pars = { reportType:report_type };
    JSPFree.openDialog("选择日期", "/yujs/safe/reportCheck/report_check_date.js", 700, 560, pars, function (_rtdata) {
        if (_rtdata != null && _rtdata.code == "finish") {
            JSPFree.alert("校验完成！");
            JSPFree.refreshBillListCurrPage(d1_BillList);
        }
    });
}

function getCurrentSelectedRidArray(currentSelections, selectedRidArray) {
    for (var i = 0; i < currentSelections.length; i++) {
        var temp = currentSelections[i];
        var approvalStatus = temp.approval_status;
        // 0 代表未提交审批
        if ("0" === approvalStatus) {
            selectedRidArray.push(temp.rid);
        }
    }
}

// 列表中删除一条记录 ，同时需要删除子表数据，所以在此重新公共删除方法
var doBillListDelete = function (_grid) {
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
    $.messager.confirm('提示', '你真的要删除选中的记录吗?', function (_isConfirm) {
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
            execDeleteBillListdData(str_ds, str_templetcode,
                str_savetable, str_sqlwhere);
            
            
            // 从界面上删除行
            _grid.datagrid('deleteRow', int_selrow);
            $.messager.alert('提示', '删除数据成功!', 'info');
        } catch (_ex) {
            console.log(_ex);
            FreeUtil.openHtmlMsgBox("发生异常", 500, 250, _ex)
        }
    });
};

//执行保存删除数据,新增与修改与card,删除直接是list
var execDeleteBillListdData = function (_ds, _templetcode, _table, _sqlwhere) {
    var jso_par = {ds: _ds, templetcode: _templetcode, savetable: _table, SQLWhere: _sqlwhere};  //
    var jso_rt = FreeUtil.doClassMethodCall("com.yusys.safe.business.service.SafeBusinessBS", "deleteSafeBillListData", jso_par);  //保存数据
};