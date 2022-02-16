/**
 * 检核表数据查询
 *
 * @constructor
 */
var tabName = "";
var tabNameEn = "";
var maskUtil = "";
var opts1 = "";
function AfterInit() {
    maskUtil = FreeUtil.getMaskUtil();
    tabName = jso_OpenPars.tab_name;
    tabNameEn = jso_OpenPars.tab_name_en;
    document.write('<script src="../js/bfd/datagrid_detailview.js"><\/script>');
    
    JSPFree.createSplit("d1", "上下", 80);
    var str_className = "Class:com.yusys.bfd.checktab.service.BfdModelTemplateBuilderForCheckTab.getTemplet('" + tabName + "','" + tabNameEn + "','')";
    JSPFree.createBillQuery("d1_A", str_className);
    
}

function AfterBodyLoad() {
    // document.write('<script src="../../js/bfd/datagrid_detailview.js"><\/script>');
    
}

$(function () {
    $('#d1_B').datagrid({
        url: v_context + "/bfd/check/table/getCheckTableData",//向后台请求数据
        singleSelect: "true",
        fitColumns: "true",
        striped: true,
        checkOnSelect: 'true',
        columns: [getCurrentTabColumn()],
        toolbar: [
            {
                text: '导出',
                iconCls: 'icon-p69',
                handler: function () {
                    exportCheckTable();
                }
            }
        
        ],
        pagination: false,
        queryParams: {
            tabName: tabName,
            tabNameEn: tabNameEn,
            condition: ""
            
        },
        onLoadSuccess: function (data) {
            // 指定列进行合并操作
            if (isExtremumTable()) {
                $('#d1_B').datagrid("autoMergeCells", ['groupId', 'report_name']);
            } else {
                $('#d1_B').datagrid("autoMergeCells", ['groupId', 'field_name']);
            }
        },
        onClickCell: function (rowIndex, field, value) {
            if ("report_name" != field && "max_value" != field && "min_value" != field) {
                return;
            }
            
            if (isExtremumTable()) {
                var rows = $('#d1_B').datagrid('getRows');//获得所有行
                var row = rows[rowIndex];//根据index获得其中一行。
                openInfoData(rowIndex, field, value, row);
            }
        }
    });
    $("#d1_A_search_button_id").click(function () {
        var condition = JSPFree.getQueryFormSQLCons(d1_A_BillQuery);
        if (!condition) {
            return;
        }
        $("#d1_B").datagrid("load", {
            tabName: tabName,
            tabNameEn: tabNameEn,
            condition: condition
        });
    });
});

/**
 * 获取当前显示的字段列
 */
function getCurrentTabColumn() {
    var result = JSPFree.doClassMethodCall("com.yusys.bfd.checktab.service.CheckTableServiceBS",
        "getCheckTableListColumns", {tabNameEn: tabNameEn});
    
    var formatter = function (_value, _rowData, _rowIndex) {
        return "<span style='text-decoration:underline;color:blue;cursor:pointer'>" + _value + "</span>";
    };
    var columns = result.columns;
    var numberFormatter = function (value, row, index) {
        if (!opts1) {
            opts1 = $('#d1_B').datagrid('options');
        }
        return opts1.pageSize * (opts1.pageNumber - 1) + index + 1;
    }
    var number = {
        field: 'serialNumber',
        title: '序号',
        align: 'center',
        width: 60,
        formatter: numberFormatter
    }
    var columnsArray = new Array();
    columnsArray.push(number);
    for (var i = 0; i < columns.length; i++) {
        var temp = columns[i];
        var columnName = temp.field;
        if (isExtremumTable()) {
            if ("report_name" === columnName
                || "max_value" === columnName
                || "min_value" === columnName) {
                temp.formatter = formatter;
            }
        }
        columnsArray.push(temp);
    }
    if ("ok" === result.status) {
        return columnsArray;
    }
    return [];
}

/**
 * 【导出】
 * 点击按钮，系统下载excel文件，对校验规则进行导出
 *
 * @returns
 */
function exportCheckTable() {
    var condition = JSPFree.getQueryFormSQLCons(d1_A_BillQuery);
    if (!condition) {
        return;
    }
    var params = {
        tabNameEn: tabNameEn,
        tabName: tabName,
        condition: condition,
        orgNo: str_LoginUserOrgNo,
        dataDt: $("#data_dt").datebox('getValue')
    }
    maskUtil.mask();
    setTimeout(function () {
        JSPFree.downloadFile("com.yusys.bfd.checktab.export.ExportCheckTableDataToExcel", tabName + ".xlsx", params); //
        maskUtil.unmask();
    }, 1000)
}

/**
 * 查看明细
 *
 * @param rowIndex
 * @param field
 * @param value
 * @param rowData
 */
function openInfoData(rowIndex, field, value, rowData) {
    var rid = rowData.rid;
    var condition = JSPFree.getQueryFormSQLCons(d1_A_BillQuery);
    debugger;
    var dataDt = $("#data_dt").datebox('getValue');
    var issuedNoValue = $('#issued_no').textbox('getValue');
    
    var param = {
        rid: rid,
        fieldName: field,
        checkTableNameEn: tabNameEn,
        dataDt: dataDt,
        issuedNoValue: issuedNoValue
    }
    JSPFree.openDialog("查看", "/yujs/bfd/checktab/bfd_check_info_list.js", 1050, 500, param,
        function (_rtdata) {
        });
}

function isExtremumTable() {
    return "BFD_GRDKJD_DATA_CHECK_INFO" === tabNameEn
        || "BFD_DWDKJD_DATA_CHECK_INFO" === tabNameEn
        || "BFD_WTDKJD_DATA_CHECK_INFO" === tabNameEn;
}