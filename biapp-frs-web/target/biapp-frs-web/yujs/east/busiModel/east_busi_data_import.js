/**
 * 导入exel
 * @author bfk
 * @type {string}
 */
var tab_name = "";
var tab_name_en = "";

//初始化界面
function AfterInit() {
    tab_name = jso_OpenPars.tab_name;
    tab_name_en = jso_OpenPars.tab_name_en;
    document.getElementById("d1").innerHTML =
        "<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> " +
        "<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:450px;padding:60px 80px;\">" +
        "<div style=\"margin-bottom:50px;\"> " +
        "<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'选择一个文件...'\" style=\"width:100%\">" +
        "<span style='color: red;white-space:pre-wrap;'>注意：\n文件名要以表中文名称命名！\n</span>" +
        "</div> " +
        "<div align=\"center\"> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"downloadTemplate(this);\">下载模板</a> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"importExcel(this);\">上传</a> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"closeWin(this);\">关闭</a> " +
        "</div> " +
        "</div> " +
        "</form>";
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
function importExcel() {
    var file = document.getElementById('filebox_file_id_1').files[0];
    if (file == null) {
        $.messager.alert('提示', '文件不能为空，请重新选择文件！', 'warning');
        return;
    }
    var fileName = file.name;
    var file_typename = fileName.substring(fileName.lastIndexOf('.'), fileName.length);
    if (file_typename != '.xlsx' && file_typename != '.xls') {
        JSPFree.alert("请选择Excel表格文件");
        return;
    }
    
    $('#importFileForm').form('submit', {
        url: v_context + "/east/export/business/importData",
        onSubmit: function () {
        },
        success: function (result) {
            var result = eval('(' + result + ')');
            
            if (result.success == "true") {
                JSPFree.closeDialog("success");
            } else {
                JSPFree.openDialog("导入错误提示", "/yujs/east/busiModel/east_import_error_message.js", 500, 300, {
                    message:result
                });
            }
        }
    });
}

/**
 * 下载模板
 */
function downloadTemplate() {
    var src = v_context + "/east/export/business/downloadExcelTemplate?tabName=" + tab_name + "&tabNameEn=" + tab_name_en;
    var download = null;
    download = $('<iframe id="download" style="display: none;"/>');
    $('body').append(download);
    download.attr('src', src);
}