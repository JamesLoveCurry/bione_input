/**
 *
 * <pre>
 * Title:关联亲属页面导入功能js
 * Description:
 * </pre>
 * @author zzw
 * @version 1.00.00
 */

//初始化界面
function AfterInit(){
    document.getElementById("d1").innerHTML=
        "<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> "+
        "<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:350px;padding:60px 80px;\">" +
        "<div style=\"margin-bottom:50px\"> " +

        "<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'选择一个文件...'\" style=\"width:100%\">" +
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
    if (file_typename != '.zip' && file_typename != '.txt') {
        JSPFree.alert("请选择压缩包或文本文件文件");
        return;
    }

    var formData = new FormData($("#importFileForm")[0]);
    $('#importFileForm').form('submit',{
        url: v_context + "/imas/report/importRepFile",
        onSubmit: function(){

        },
        success: function(result){

            if (result == ""){
            	JSPFree.alert("上传成功");
                JSPFree.closeDialog();
            } else if(result=='300'){
                JSPFree.alert("文件格式不合法");
            }else if(result=='301'){
                JSPFree.alert("文件命名不合法");
            }else if(result=='302'){
                JSPFree.alert("文件名中的日期不合法");
            }else if(result=='303'){
                JSPFree.alert("文件名中的机构号不合法");
            }else if(result=='304'){
            	JSPFree.alert("文件名中的表名不合法");
            }
        }
    });
}