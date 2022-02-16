/**
 *
 * <pre>
 * Title:填报页面导入功能js
 * Description:
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/10/26 11:10
 */

//初始化界面
function AfterInit(){
    document.getElementById("d1").innerHTML=
        "<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> "+
        "<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:350px;padding:60px 80px;\">" +
        "<div style=\"margin-bottom:50px\"> " +

        "<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'选择一个文件...'\" style=\"width:100%\">" +
        "<span style='color: red;white-space:pre-wrap;'>注意:\n1.只能上传zip文件。\n2.zip文件中为大集中报文的DAT和IDX文件。\n3.可以将多个大集中报文的zip压缩包放入一个压缩包中导入。\n4.压缩包中最多包含10个压缩包。</span>"+
        "</div> " +
        "<div align=\"center\"> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"importdata(this);\">上传</a> " +
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
    if (file_typename != '.zip') {
        JSPFree.alert("请选择zip文件");
        return;
    }

    var formData = new FormData($("#importFileForm")[0]);
    $('#importFileForm').form('submit',{
        url: v_context + "/bfd/resultDjz/importDjzData",
        onSubmit: function(){

        },
        success: function(result){
            var result = eval('('+result+')');
            if (result.status == "success"){
                JSPFree.closeDialog("success");
            } else {
                $.messager.alert({
                    title:'系统提示',
                    msg:'<div style="height:100px;overflow-y:scroll">' + result.msg + '</div>',
                    width:320,
                    top: 130,
                    icon:'info'
                });
            }
        }
    });
}