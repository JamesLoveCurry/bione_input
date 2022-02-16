/**
 *
 * <pre>
 * Title:回执管理 文件导入
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/7/6 16:24
 */
function AfterInit() {
    var reportType = jso_OpenPars.reportType
    document.getElementById("d1").innerHTML=
        "<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> "+
        "<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:350px;padding:60px 80px;\">" +
        "<div style=\"margin-bottom:50px\"> " +
        "<input name=\"reportType\" value=\""+ reportType +"\" type=\"hidden\"> "  +
        "<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'选择一个文件...'\" style=\"width:100%\">" +
        "</div> " +
        "<div align=\"center\"> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"add(this);\">上传</a> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"closeWin(this);\">关闭</a> " +
        "</div> "+
        "</div> "+
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
function add() {
    var file = document.getElementById('filebox_file_id_1').files[0];
    if (file == null) {
        $.messager.alert('提示', '文件不能为空，请重新选择文件！', 'warning');
        return;
    }
    var fileName = file.name;
    var file_typename = fileName.substring(fileName.lastIndexOf('.'), fileName.length);
    if ((file_typename.toLocaleLowerCase() != '.xml') && (file_typename.toLocaleLowerCase() != '.zip')) {
        $.messager.alert('提示', '文件格式错误，只能上传xml和zip文件！', 'warning');
        return;
    }

    var formData = new FormData($("#importFileForm")[0]);

    $('#importFileForm').form('submit',{
        url: v_context + "/frs/safe/receipt/uploadReceipt",
        onSubmit: function(){

        },
        success: function(result){
            var result = eval('('+result+')');
            if (result.code){
                if("success" == result.code){
                    JSPFree.closeDialog({ title: 'success', msg: "回执导入成功" });
                }else {
                    JSPFree.closeDialog({ title: 'error', msg: result.msg });
                }
            } else {
                JSPFree.alert("上传失败");
            }
        }
    });
}
