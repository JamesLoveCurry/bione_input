/**
 * 导入的错误提示窗口
 * @author bfk
 * @type {string}
 */
var message = "";

function AfterInit() {
    message = jso_OpenPars.message;
    document.getElementById("d1").innerHTML =
        "<div  class=\"easyui-panel\" style=\"width:100%;height:85%;max-width:600px;padding:60px 80px;\">" +
            "<div style=\"margin-bottom:50px;overflow-y: auto\"> " +
                "<span style='color: red;white-space:pre-wrap;'>" + message.msg + "</span>" +
            "</div> " +
        "</div> " +
        "<div align=\"center\"> " +
        "<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;\" onclick=\"closeWin(this);\">关闭</a> " +
        "</div>";
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
                // $.messager.alert({
                //     title: '系统提示',
                //     msg: '<div style="height:500px;overflow-y:scroll">' + result.msg + '</div>',
                //     width: 320,
                //     top: 130,
                //     icon: 'info'
                // });
                
                JSPFree.openDialog("文件上传", "/yujs/east/busiModel/east_busi_data_import.js", 500, 300, {
                    tab_name: tab_name,
                    tab_name_en: tab_name_en
                }, function (_rtdata) {
                    if (_rtdata == "success") {
                        JSPFree.alert("导入成功！");
                    }
                });
            }
        }
    });
}
