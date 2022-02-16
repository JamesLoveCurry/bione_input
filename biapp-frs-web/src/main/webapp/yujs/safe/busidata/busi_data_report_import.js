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
var tab_name = "";
var tab_name_en = "";
var report_type = "";
//初始化界面
function AfterInit(){
    tab_name = jso_OpenPars.tab_name;
    tab_name_en = jso_OpenPars.tab_name_en;
    report_type = jso_OpenPars.report_type;
    document.getElementById("d1").innerHTML=
        "<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> "+
        "<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:450px;padding:60px 80px;\">" +
        "<div style=\"margin-bottom:50px\"> " +
        "<input type='hidden' id='reportType' name='reportType' value='"+ report_type +"'>" +
        "<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'选择一个文件...'\" style=\"width:100%\">" +
        "<span style='color: red;white-space:pre-wrap;'>注意：\n(1).文件名要以表中文名称命名！\n</span>"+
        "<span style='color: red'>(2).如果excel中'RID'存在,则更新数据;如果'RID'为空则新增数据,并自动生成'RID'！</span>"+
        "</div> " +
        "<div align=\"center\"> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"downloadTemplate(this);\">下载模板</a> " +
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
    if (file_typename != '.xlsx' && file_typename != '.xls') {
        JSPFree.alert("请选择Excel表格文件");
        return;
    }

    var formData = new FormData($("#importFileForm")[0]);
    $('#importFileForm').form('submit',{
        url: v_context + "/safe/business/importData",
        onSubmit: function(){

        },
        success: function(result){
            var result = eval('('+result+')');

            if (result.success == "true"){
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
/*
 * 下载模板
 */
function downloadTemplate() {
    var src = v_context + "/safe/business/downloadTemplate?tabName="
        + tab_name + "&tabNameEn=" + tab_name_en ;
    var download=null;
    download = $('<iframe id="download" style="display: none;"/>');
    $('body').append(download);
    download.attr('src', src);
}