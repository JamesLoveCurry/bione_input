/**
 * 检核表-连续性查询各个贷款表明细数据查询
 *
 * @type {string}
 */

var tab_name = "";
var report_type = "";
var str_ds = "";
var str_className = "";
var org_no = "";
var org_class = "";
var maskUtil = "";
var flag = false;
var d1_BillQuery = null;
var whereSql = "";

function AfterInit() {
    tab_name = jso_OpenPars.tab_name;
    tab_name_en = jso_OpenPars.tab_name_en;
    str_ds = jso_OpenPars.ds;
    maskUtil = FreeUtil.getMaskUtil();
    
    var jsn_result = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdGetReportBS", "getObjList", null);
    if (jsn_result.code == 'success') {
        whereSql = jsn_result.data;
    }
    str_className = "Class:com.yusys.bfd.checktab.service.BfdModelTempletBuilderForContinuity.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_LoginUserOrgNo + "')";
    JSPFree.createBillList("d1", str_className, null, {
        list_btns: "[icon-p81]查看/view1;[icon-p69]导出csv/exportCSV(this);[icon-p69]导出excel/exportExcel(this);",
        isSwitchQuery: "Y",
        autoquery: "N",
        refWhereSQL: whereSql
    });
    JSPFree.billListBindCustQueryEvent(d1_BillList, onQueryEvent);
    
    // 排序事件
    d1_BillList.datagrid({
        onSortColumn: function (sort, order) {
            var querySql = getQuerySql();
            JSPFree.queryDataBySQL(d1_BillList, querySql + " order by " + sort + " " + order);
        }
    });
    d1_BillQuery = d1_BillQuery;
    
}

function onDoubleClick() {
    alert("123123");
}

function onQueryEvent() {
    var querySql = getQuerySql();
    JSPFree.queryDataBySQL(d1_BillList, querySql + " order by cny_bal desc ");
}

function getQuerySql() {
    var condition = "";
    var jso_templetVO = d1_BillQuery.templetVO;
    var dom_form = document.getElementById('d1_QueryForm');
    var queryItems = FreeUtil.getQueryItemFromTempletVO(jso_templetVO, 1);
    var querySql = " select * from " + tab_name_en + " where ";
    // 如果存在上期余额时，需要单独处理，上期余额字段查询逻辑是 取当上月当前数据日期的 贷款余额折人民币 作为过滤条件
    var previous_cny_bal = JSPFree.getBillQueryFormValue(d1_BillQuery).previous_cny_bal;
    var data_dt = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt;
    if (previous_cny_bal) {
        // 获取上月同期的 贷款余额折人民币 的值
        var result = JSPFree.doClassMethodCall("com.yusys.bfd.checktab.service.BfdModelTempletBuilderForContinuity", "getLastMonthDay", {dataDt: data_dt});
        if ("ok" === result.status) {
            var lastMonthDataDt = result.lastMonthDay;
            // 不包括 上期余额查询字段
            var notExistsPreviousCnyBal = new Array();
            for (var i = 0; i < queryItems.length; i++) {
                if ('previous_cny_bal' != queryItems[i].itemkey) {
                    notExistsPreviousCnyBal.push(queryItems[i]);
                }
            }
            var otherColumnsSqlWhere = JSPFree.getQueryFormSQLConsByItems(dom_form, notExistsPreviousCnyBal);
            // 上期余额字段查询逻辑单独处理
            var sqlWhere = "( data_dt = '" + lastMonthDataDt + "'  and cny_bal = " + previous_cny_bal + ")";
            condition = otherColumnsSqlWhere + " or " + sqlWhere;
            querySql += condition;
        }
    } else {
        var condition = JSPFree.getQueryFormSQLConsByItems(dom_form, queryItems);
        querySql += condition;
    }
    return querySql;
}

/**
 * 加载页面之后处理
 * @returns
 */
function AfterBodyLoad() {
    // 判断当前用户是总行，还是分行
    var jso_data = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS", "checkUserOrgNo", {str_LoginUserOrgNo: str_LoginUserOrgNo});
    var result = jso_data.result;
    if (result == "OK") {
        org_no = jso_data.orgNo;
        org_class = jso_data.orgClass;
    }
    // 重载灵活查询
    onOpenFreeQuery_d1 = function () {
        // 打开灵活查询之前，把当前查询条件字段的值带过去
        var queryFormValue = {};
        var jso_templetVO = d1_BillQuery.templetVO;
        var queryItems = FreeUtil.getQueryItemFromTempletVO(jso_templetVO, 1);
        var dom_form = document.getElementById('d1_QueryForm');
        for (var i = 0; i < queryItems.length; i++) {
            var str_sqlitem = FreeUtil.getFormItemValue(dom_form, queryItems[i]);
            queryFormValue[queryItems[i].itemkey] = str_sqlitem;
        }
        if (typeof d1_BillQuery.popDialogId != "undefined") {
            FreeUtil.showDialogById(d1_BillQuery.popDialogId);  //直接显示!
        } else {
            var str_templetCode = d1_BillQuery.templetVO.templet_option.templetcode;
            var jso_par = {templetcode: str_templetCode, tab_name: tab_name, tab_name_en: tab_name_en,queryFormValue:queryFormValue};
            JSPFree.openDialog("灵活查询【勾选右边数据列会自动创建查询条件】", "/yujs/bfd/checktab/bfd_check_build_continuity_dialog.js", 900, 560, jso_par, function (_rtdata) {
                if ("Confirm" == _rtdata.result) {
                    d1_BillQuery["popDialogId"] = _rtdata.popDialogId; //
                    var beforeBuildListFreeQuery = onOpenFreeQuery_d1;
                    // 重新渲染查询表单
                    JSPFree.createBillList("d1", str_className, null, {
                        list_btns: "[icon-p81]查看/view1;[icon-p69]导出csv/exportCSV(this);[icon-p69]导出excel/exportExcel(this);",
                        isSwitchQuery: "Y",
                        autoquery: "N"
                    });
                    $.parser.parse('#d1');
                    onOpenFreeQuery_d1 = beforeBuildListFreeQuery;
                    FreeUtil.loadBillQueryData(d1_BillList, _rtdata.callBackQueryItemValue);
                    JSPFree.billListBindCustQueryEvent(d1_BillList, onQueryEvent);
                    JSPFree.queryDataBySQL(d1_BillList, getQuerySql());
                } else {
                    d1_BillQuery["popDialogId"] = _rtdata.popDialogId; //
                }
            }, false);
        }
    }
    
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
        templetcode: str_className,
        type: "View",
        tabname: tab_name,
        tabnameen: tab_name_en,
        str_ds: str_ds
    };
    defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数
    
    JSPFree.openDialog3("查看", "/yujs/bfd/busidata/bfd_check_data_edit.js", null, null, defaultVal, function (_rtdata) {
    }, true);
}


// 导出CSV
function exportCSV() {
    if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
        JSPFree.alert("当前无记录！");
        return;
    }
    var dataDt = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt;
    // 如果超过50w数据,页面提示语
    var sql = d1_BillList.CurrSQL3;
    var new_sql = 'select count(*) c from (' + sql + ') t';
    var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);
    
    var c = jso_data[0].c;
    var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS", "getDownloadMaxDataNum", {});
    if (parseInt(c) > parseInt(jso_rt.downloadNum)) {
        JSPFree.confirm('提示', '当前导出数据量较大,可能导出时间很长,是否导出?', function (_isOK) {
            if (_isOK) {
                maskUtil.mask();
                setTimeout(function () {
                    var json = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS", "exportCSV", {
                        tabName: tab_name,
                        tabNameEn: tab_name_en,
                        dsName: str_ds,
                        dataSql: d1_BillList.CurrSQL3,
                        orgNo: org_no,
                        dataDt: dataDt
                    });
                    if (json.code = 'success') {
                        var filepath = json.data;
                        var src = v_context + "/bfd/export/business/downloadData?filepath=" + filepath;
                        var download = $('<iframe id="download" style="display: none;"/>');
                        $('body').append(download);
                        download.attr('src', src);
                    } else {
                        $.messager.alert('提示', json.msg, 'warning');
                    }
                    maskUtil.unmask();
                }, 100);
                
            }
        });
    } else {
        maskUtil.mask();
        setTimeout(function () {
            var json = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS", "exportCSV", {
                tabName: tab_name,
                tabNameEn: tab_name_en,
                dsName: str_ds,
                dataSql: d1_BillList.CurrSQL3,
                orgNo: org_no,
                dataDt: dataDt
            });
            if (json.code = 'success') {
                var filepath = json.data;
                var src = v_context + "/bfd/export/business/downloadData?filepath=" + filepath;
                var download = $('<iframe id="download" style="display: none;"/>');
                $('body').append(download);
                download.attr('src', src);
            } else {
                $.messager.alert('提示', json.msg, 'warning');
            }
            maskUtil.unmask();
        }, 100);
    }
}

/**
 * 导出excel
 */
function exportExcel() {
    if (d1_BillList.CurrSQL3 == null || "undefined" == d1_BillList.CurrSQL3) {
        JSPFree.alert("当前无记录！");
        return;
    }
    // 如果超过50w数据,页面提示语
    var sql = d1_BillList.CurrSQL3;
    var new_sql = 'select count(*) c from (' + sql + ') t';
    var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);
    
    var c = jso_data[0].c;
    var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS", "getDownloadMaxDataNum", {});
    if (parseInt(c) > parseInt(jso_rt.downloadNum)) {
        JSPFree.confirm('提示', '数据量越大,导出等待时间越长,是否导出?', function (_isOK) {
            if (_isOK) {
                maskUtil.mask();
                setTimeout(function () {
                    var sql = d1_BillList.CurrSQL3;
                    var new_sql = 'select count(*) c from (' + sql + ') t';
                    var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);
                    var dataDt = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt;
                    var c = jso_data[0].c;
                    var json = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS", "exportExcel", {
                        tabName: tab_name,
                        tabNameEn: tab_name_en,
                        dsName: str_ds,
                        dataSql: d1_BillList.CurrSQL3,
                        dataCount: c,
                        orgNo: org_no,
                        dataDt: dataDt
                    });
                    if (json.code = 'success') {
                        var filepath = json.data;
                        var src = v_context + "/bfd/export/business/downloadData?filepath=" + filepath;
                        var download = $('<iframe id="download" style="display: none;"/>');
                        $('body').append(download);
                        download.attr('src', src);
                    } else {
                        $.messager.alert('提示', json.msg, 'warning');
                    }
                    maskUtil.unmask();
                }, 100);
            }
        });
    } else {
        maskUtil.mask();
        setTimeout(function () {
            var sql = d1_BillList.CurrSQL3;
            var new_sql = 'select count(*) c from (' + sql + ') t';
            var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);
            var dataDt = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt;
            var c = jso_data[0].c;
            var json = JSPFree.doClassMethodCall("com.yusys.bfd.checktab.service.CheckTableServiceBS", "exportExcel", {
                tabName: tab_name,
                tabNameEn: tab_name_en,
                dsName: str_ds,
                dataSql: d1_BillList.CurrSQL3,
                dataCount: c,
                orgNo: org_no,
                dataDt: dataDt
            });
            if (json.code = 'success') {
                var filepath = json.data;
                var src = v_context + "/bfd/export/business/downloadData?filepath=" + filepath;
                var download = $('<iframe id="download" style="display: none;"/>');
                $('body').append(download);
                download.attr('src', src);
            } else {
                $.messager.alert('提示', json.msg, 'warning');
            }
            maskUtil.unmask();
        }, 100);
    }
}
