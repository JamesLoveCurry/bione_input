/**
 * 极端数据查询，查看数据明细
 *
 * @type {string}
 */

var tab_name = "";
var tab_name_en = "";
var report_type = "";
var str_ds = "";
var str_className = "";
var org_no = "";
var maskUtil = "";

var rid = "";
var fieldName = "";
var checkTableNameEn = ""; // 极值检核表名称
var data_dt = "";
var issuedNoValue = "";

function AfterInit() {
    rid = jso_OpenPars.rid;
    fieldName = jso_OpenPars.fieldName;
    checkTableNameEn = jso_OpenPars.checkTableNameEn;
    data_dt = jso_OpenPars.dataDt;
    issuedNoValue = jso_OpenPars.issuedNoValue;
    
    maskUtil = FreeUtil.getMaskUtil();
    
    var whereSql = '';
    var jsn_result = JSPFree.doClassMethodCall("com.yusys.bfd.checktab.service.CheckTableServiceBS", "getReportTableQueryParam",
        {
            rid: rid,
            fieldName: fieldName,
            checkTableNameEn: checkTableNameEn
        });
    if (jsn_result.status == 'ok') {
        tab_name = jsn_result.tabName;
        tab_name_en = jsn_result.tabNameEn;
        str_ds = jsn_result.dsName;
        whereSql = jsn_result.sqlWhere
    }
    // 上下分为两层
    JSPFree.createSplit("d1", "上下", 80);
    
    document.getElementById("d1_A").style.marginLeft = '15px';
    document.getElementById("d1_A").style.marginTop = '15px';
    document.getElementById("d1_A").innerHTML =
        '<label>数据类型：</label>' +
        '<input id="data_type_id" name="data_type_name">' +
        '<label style="margin-left: 20px">字段名称：</label>' +
        '<input id="field_name_id" class="easyui-combobox" name="field_name_name" data-options="valueField:\'id\',textField:\'text\'">' +
        "<a id=\"query_info_data_button_id\" href=\"javascript:void(0)\" class=\"easyui-linkbutton l-btn l-btn-small\" style=\"margin-left:10px\">" +
        "<span class=\"l-btn-left l-btn-icon-left\" style=\"margin-top: 0px;\">" +
        "<span class=\"l-btn-text\" style=\"margin-left :3px\">查询</span>" +
        "<span class=\"l-btn-icon icon-search1\">&nbsp;</span></span></a>";
    
    // 创建列表
    str_className = "Class:com.yusys.bfd.business.service.BfdModelTempletBuilder.getTemplet('" + tab_name + "','" + tab_name_en + "','" + str_LoginUserOrgNo + "')";
    JSPFree.createBillList("d1_B", str_className, null, {
        list_btns: "[icon-p81]查看/view1;[icon-p69]导出excel/exportExcel(this);",
        isSwitchQuery: "N",
        autoquery: "Y",
        refWhereSQL: whereSql,
        ishavebillquery: "N"
    });
}

$(function () {
    
    $("#query_info_data_button_id").click(function () {
        var dataType = $('#data_type_id').combobox('getValue');
        var fieldName = $('#field_name_id').combobox('getValue');
        
        if (!dataType && !fieldName) {
            $.messager.alert('提示', '请选择数据类型或字段类型后查询！', 'warning');
            return;
        }
        // 不选择数据类型时，相当于从点击报文名称查看明细数据
        if (!dataType) {
            dataType = "report_name"
        }
        
        var jsn_result = JSPFree.doClassMethodCall("com.yusys.bfd.checktab.service.CheckTableServiceBS", "getReportTableQueryParam",
            {
                rid: rid,
                fieldName: dataType,
                colName: fieldName,
                checkTableNameEn: checkTableNameEn
            });
        if (jsn_result.status == 'ok') {
            debugger;
            var whereSql = jsn_result.sqlWhere;
            var querySql = "select * from " + tab_name_en + " where " + whereSql;
            JSPFree.queryDataBySQL(d1_B_BillList, querySql);
        }
    });
})

/**
 * 加载页面之后处理
 * @returns
 */
function AfterBodyLoad() {
    // 数据类型只有最大值 与 最小值
    $('#data_type_id').combobox({
        valueField: 'id',
        textField: 'name',
        data: [{
            "id": 'max_value',
            "name": "最大值"
        }, {
            "id": 'min_value',
            "name": "最小值"
        }]
    });
    
    // 获取下拉值
    var jsn_result = JSPFree.doClassMethodCall("com.yusys.bfd.checktab.service.CheckTableServiceBS",
        "getFileNameJsonData", {
            rid: rid,
            checkTableNameEn: checkTableNameEn
        });
    $('#field_name_id').combobox({
        valueField: 'id',
        textField: 'name',
        data: JSON.parse(JSON.stringify(jsn_result.data))
    });
}

/**
 * 查看
 * @returns
 */
function view1() {
    var json_rowdata = d1_B_BillList.datagrid('getSelected'); // 先得到数据
    
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

/**
 * 导出excel
 */
function exportExcel() {
    if (d1_B_BillList.CurrSQL3 == null || "undefined" == d1_B_BillList.CurrSQL3) {
        JSPFree.alert("当前无记录！");
        return;
    }
    // 如果超过50w数据,页面提示语
    var sql = d1_B_BillList.CurrSQL3;
    var new_sql = 'select count(*) c from (' + sql + ') t';
    var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);
    
    var c = jso_data[0].c;
    var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS", "getDownloadMaxDataNum", {});
    if (parseInt(c) > parseInt(jso_rt.downloadNum)) {
        JSPFree.confirm('提示', '数据量越大,导出等待时间越长,是否导出?', function (_isOK) {
            if (_isOK) {
                maskUtil.mask();
                setTimeout(function () {
                    var sql = d1_B_BillList.CurrSQL3;
                    var new_sql = 'select count(*) c from (' + sql + ') t';
                    var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);
                    var dataDt = data_dt;
                    var c = jso_data[0].c;
                    var json = JSPFree.doClassMethodCall("com.yusys.bfd.business.service.BfdBusinessBS", "exportExcel", {
                        tabName: tab_name,
                        tabNameEn: tab_name_en,
                        dsName: str_ds,
                        dataSql: d1_B_BillList.CurrSQL3,
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
            var sql = d1_B_BillList.CurrSQL3;
            var new_sql = 'select count(*) c from (' + sql + ') t';
            var jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);
            var dataDt = data_dt;
            var c = jso_data[0].c;
            var json = JSPFree.doClassMethodCall("com.yusys.bfd.checktab.service.CheckTableServiceBS", "exportExcel", {
                tabName: tab_name,
                tabNameEn: tab_name_en,
                dsName: str_ds,
                dataSql: d1_B_BillList.CurrSQL3,
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
