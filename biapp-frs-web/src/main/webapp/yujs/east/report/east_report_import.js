/**
 * 导入报文
 * @author bfk
 * @type {string}
 */
var tab_name_en = "";

//初始化界面
function AfterInit() {
    tab_name_en = jso_OpenPars.tab_name_en;
    document.getElementById("d1").innerHTML =
        "<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> " +
        "<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:550px;padding:30px 80px;\">" +
        "<div style=\"margin-bottom:50px;\"> " +
        "<label style='color: red' for=\"rpt_org_no_label\">报送机构：</label>" +
        " <input id=\"rpt_org_no\" name=\"rpt_org_no\" style='width: 300px'> " +
        "<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'选择一个文件...'\" style=\"width:100%;margin-left: 100px;margin-top: 10px\">" +
        "<span style='color: red;white-space:pre-wrap;'>\n注意：\n请注意导入文件的文件名,请按要求命名！\n</span>" +
        "<span id='currentImportProcess' style='color: red;white-space:pre-wrap;'></span>" +
        "</div> " +
        "<div align=\"center\"> " +
        "	<a id='importButtonId' href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"importReportData(this);\">导入</a> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"closeWin(this);\">关闭</a> " +
        "</div> " +
        "</div> " +
        "</form>";
}

function AfterBodyLoad() {
    
    var result = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.EastImportReportDataFileService", "getRptOrgNoData");
    
    $('#rpt_org_no').combobox({
        valueField: 'id',
        textField: 'name',
        data: JSON.parse(JSON.stringify(result.value))
    });
}

/**
 * 关闭
 * @returns
 */
function closeWin() {
    JSPFree.closeDialog();
}

/**
 * 上传
 * @returns
 */
function importReportData() {
    var result = getCheckResult("check");
    if (result.success == "ok") {
        var rptOrgNo = $("#rpt_org_no").combobox('getValue');
        $('#importButtonId').linkbutton('disable');
        $('#importFileForm').form('submit', {
            url: v_context + "/east/export/business/importReportDat?rptOrgNo=" + rptOrgNo + "&tabNameEn=" + tab_name_en,
            onSubmit: function () {
            },
            success: function (result) {
                var result = eval('(' + result + ')');
                if (result.success == "true") {
                    JSPFree.closeDialog("success");
                } else {
                    JSPFree.alert("导入失败！");
                }
            }
        });
    } else {
        JSPFree.alert(result.msg + (result.process ? result.process : ""));
    }
    self.setInterval(getImportProcess, 5000);
}

function getCheckResult(flag) {
    var file = $("#fileImport").filebox('getValue');
    if (file == null) {
        $.messager.alert('提示', '文件不能为空，请重新选择文件！', 'warning');
        return;
    }
    var file_typename = file.substring(file.lastIndexOf('.'), file.length);
    if (file_typename != '.txt') {
        JSPFree.alert("请选择txt文件!");
        return;
    }
    var rptOrgNo = $("#rpt_org_no").combobox('getValue');
    if (!rptOrgNo) {
        JSPFree.alert("请选择报送机构!");
        return;
    }
    var fileName = file.substring(file.lastIndexOf('\\') + 1, file.length);
    var json = {
        tabNameEn: tab_name_en,
        fileName: fileName,
        rptOrgNo: rptOrgNo,
        flag: flag
    }
    var result = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.EastImportReportDataFileService", "importBeforeCheck", json);
    var process = result.process;
    if (process) {
        document.getElementById("currentImportProcess").innerHTML =  result.deleteProcess + "\n" + process ;
    }
    return result;
}

function getImportProcess() {
    var file = $("#fileImport").filebox('getValue');
    var rptOrgNo = $("#rpt_org_no").combobox('getValue');
    var fileName = file.substring(file.lastIndexOf('\\') + 1, file.length);
    var json = {
        tabNameEn: tab_name_en,
        fileName: fileName,
        rptOrgNo: rptOrgNo
    }
    var result = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.EastImportReportDataFileService", "getImportProcess", json);
    var process = result.process;
    if (process) {
        document.getElementById("currentImportProcess").innerHTML = "";
        document.getElementById("currentImportProcess").innerHTML =  result.deleteProcess + "\n" + process ;
    }
}