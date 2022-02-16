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
var orgNo = "";
var rptOrgNo = "";
var distributeType="";
var dataDt = "";
//初始化界面
var maskUtil = "";
function AfterInit(){
    tab_name = jso_OpenPars.tab_name;
    tab_name_en = jso_OpenPars.tab_name_en;
    orgNo = jso_OpenPars.orgNo;
    rptOrgNo = jso_OpenPars.rptOrgNo;
    distributeType = jso_OpenPars.distributeType;
    dataDt = jso_OpenPars.dataDt;
	maskUtil = FreeUtil.getMaskUtil();
    document.getElementById("d1").innerHTML=
        "<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> "+
        "<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:450px;padding:60px 80px;\">" +
        "<div style=\"margin-bottom:50px\"> " +
        "<input id='orgNo' value='"+ orgNo +"' name='orgNo' hidden/>"+
        "<input id='rptOrgNo' value='"+ rptOrgNo +"' name='rptOrgNo' hidden/>"+
        "<input id='dataDt' value='"+ dataDt +"' name='dataDt' hidden/>"+
        "<input id='distributeType' value='"+ distributeType +"' name='distributeType' hidden/>"+
        "<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'选择一个文件...'\" style=\"width:100%\">" +
        "<span style='color: red;white-space:pre-wrap;'>注意：\n(1).文件名要以表中文名称或者表中文_数据日期命名\n</span>"+
        "<span style='color: red;white-space:pre-wrap;'>(2).错误数据和全量数据导出,在“修改”sheet页修改报送字段，修改完成后可直接导入，导入时会根据数据主键进行更新\n</span>"+
        "<span style='color: red;white-space:pre-wrap;'>(3).已修改数据导出：会有“修改”和“新增”sheet页,修改同上，新增会增量添加数据，或者可以直接下载新增的模板\n</span>"+
        "</div> " +
        "<div align=\"center\"> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:50%;margin:0 auto;\" onclick=\"downloadTemplate(this);\">下载新增模板</a> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"importdata(this);\">导入</a> " +
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
 * 批量上传
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
    if (file_typename != '.xlsx') {
        JSPFree.alert("只能导入xlsx表格文件");
        return;
    }
    
    var filename = fileName.substring(0, fileName.lastIndexOf('.'));
    if (filename.indexOf("_")>-1) {
    	filename = filename.split("_")[0];
    }
    if(tab_name!=filename){
    	JSPFree.alert("要导入的表名："+filename+"，和"+tab_name+"不一致，不能执行导入");
        return;
    }
	maskUtil.mask();
    var formData = new FormData($("#importFileForm")[0]);
    $('#importFileForm'). form('submit',{
        url: v_context + "/east/distribute/data/importchkData1",
        onSubmit: function(param){
        	param.tabName = tab_name
        },
        success: function(result){
            var result = eval('('+result+')');
            maskUtil.unmask();
            JSPFree.closeDialog(result);
        }
    });
}


/*
 * 下载模板
 */
function downloadTemplate() {
    var src = v_context + "/east/distribute/data/downloadTemplate?tabName="
        + tab_name + "&tabNameEn=" + tab_name_en ;
    var download=null;
    download = $('<iframe id="download" style="display: none;"/>');
    $('body').append(download);
    download.attr('src', src);
}