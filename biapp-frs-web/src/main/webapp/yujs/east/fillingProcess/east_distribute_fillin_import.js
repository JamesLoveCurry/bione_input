
//初始化界面
function AfterInit(){
    document.getElementById("d1").innerHTML=
        "<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> "+
        "<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:350px;padding:60px 80px;\">" +
        "<div style=\"margin-bottom:50px\"> " +

        "<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'选择一个文件...'\" style=\"width:100%\">" +
        "<span style='color: red'>注意:文件名要以'表名_8位日期'格式命名！</span>"+
        "</div> " +
        "<div align=\"center\"> " +
        "	<a href=\"javascript:void(0)\" id='importButtonId' class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"importdata(this);\">上传</a> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"closeWin(this);\">关闭</a> " +
        "</div> "+
        "</div> "+
        "</form>";
}

/**
 * 关闭
 * @returns
 */
function closeWin(){
    JSPFree.closeDialog();
}

/**
 * 上传
 * @returns
 */
function importdata(){
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
    var formData = new FormData($("#importFileForm")[0]);
    $("#importButtonId").linkbutton("disable");
    $('#importFileForm').form('submit',{
        url: v_context + "/east/distribute/data/importData",
        onSubmit: function(){

        },
        success: function(result){
            var result = eval('('+result+')');

            if (result.success == "true"){
                JSPFree.closeDialog("success");
            } else {
                JSPFree.openDialog("导入错误提示", "/yujs/east/busiModel/east_import_error_message.js", 500, 300, {
                    message:result
                });
            }
        }
    });
}