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
//初始化界面
var maskUtil = "";
function AfterInit(){
    tab_name = jso_OpenPars.tab_name;
    tab_name_en = jso_OpenPars.tab_name_en;
	maskUtil = FreeUtil.getMaskUtil();
    document.getElementById("d1").innerHTML=
        "<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> "+
        "<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:450px;padding:60px 80px;\">" +
        "<div style=\"margin-bottom:50px\"> " +

        "<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'选择一个文件...'\" style=\"width:100%\">" +
        "<span style='color: red;white-space:pre-wrap;'>注意：\n(1).文件名要以表中文名称或者表中文_数据日期命名\n</span>"+
        "<span style='color: red;white-space:pre-wrap;'>(2).如需修改数据，请先导出数据，然后在“修改”sheet页修改报送字段，修改完成后可直接导入，导入时会根据数据主键进行更新\n</span>"+
        "<span style='color: red;white-space:pre-wrap;'>(3).增量导入：在已有数据基础上增加数据\n</span>"+
        "<span style='color: red;white-space:pre-wrap;'>(4).全量导入：删除该报告机构该数据日期下已有数据，重新导入excel文件内的新数据\n</span>"+
        "</div> " +
        "<div align=\"center\"> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"downloadTemplate(this);\">下载模板</a> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"importdata(this);\">增量导入</a> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"importdataAll(this);\">全量导入</a> " +
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
	var jsr = {result:"fail"};
    JSPFree.closeDialog(jsr);
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
        url: v_context + "/imas/export/business/importchkData1",
        onSubmit: function(param){
        	param.tabName = tab_name
        },
        success: function(result){
            var result = eval('('+result+')');
            if (result.success == "true"){
				maskUtil.unmask();
                JSPFree.closeDialog(result);
            }else if(result.success == "confirm"){
            	JSPFree.confirm("提示",result.msg,function(_isOK){
                    if(_isOK){
                    	$('#importFileForm').form('submit',{
                	        url: v_context + "/imas/export/business/importData",
                	        onSubmit: function(){

                	        },
                	        success: function(result){
								maskUtil.unmask();
                	            var result = eval('('+result+')');
                	            if (result.success == "true"){
                	                JSPFree.closeDialog(result);
                	            } else {
                	                $.messager.alert({
                	                    title:'系统提示',
                	                    msg:'<div style="height:100px;text-align:center;overflow-y:scroll">' + result.msg + '</div>',
                	                    width:320,
                	                    top: 130,
                	                    icon:'info'
                	                });
                	            }
                	        }
                	    });
                    }
                });
            } else {
				maskUtil.unmask();
                $.messager.alert({
                    title:'系统提示',
                    msg:'<div style="height:80px;text-align:center;overflow-y:scroll">' + result.msg + '</div>',
                    width:320,
                    top: 100,
                    icon:'info'
                });
            }
        }
    });
}

/**
 * 全量上传
 * @returns
 */
function importdataAll() {
	var confirmMsg = "进行全量导入注意事项\n" +
			" ①将清空该日期该报送机构下所有数据\n" +
			" ②将清空所有下发的明细数据(未处理)\n" +
			" ③需要人工删除下发任务\n";
		$.messager.confirm('提示', confirmMsg, function(_isConfirm) {
		if (!_isConfirm) {
			return;
		}
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
	    $('#importFileForm').form('submit',{
	        url: v_context + "/imas/export/business/importchkDataAll",
	        onSubmit: function(param){
	        	param.tabName = tab_name
	        },
	        success: function(result){
				maskUtil.unmask();
	            var result = eval('('+result+')');
	
	            if (result.success == "true"){
	                JSPFree.closeDialog(result);
	            } else {
	                $.messager.alert({
	                    title:'系统提示',
	                    msg:'<div style="height:80px;text-align:center;overflow-y:scroll">' + result.msg + '</div>',
	                    width:320,
	                    top: 100,
	                    icon:'info'
	                });
	            }
	        }
	    });
	});
}
/*
 * 下载模板
 */
function downloadTemplate() {
    var src = v_context + "/imas/export/business/downloadTemplate?tabName="
        + tab_name + "&tabNameEn=" + tab_name_en ;
    var download=null;
    download = $('<iframe id="download" style="display: none;"/>');
    $('body').append(download);
    download.attr('src', src);
}