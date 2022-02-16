/**
 *
 * <pre>
 * Title:【配置管理】-【校验配置】-导入
 * Description:校验配置导入页面
 * 在此界面选择上传的文件，点击确认按钮实现把文件上传到系统
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020年8月10日
 */

function AfterInit(){
	document.getElementById("d1").innerHTML=
		"<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> "+
		"<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:350px;padding:60px 80px;\">" +
		"<div style=\"margin-bottom:50px\"> " +

		"<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'选择一个文件...'\" style=\"width:100%\">" +
		// 隐藏域：将业务类型的值传到后台
		"</div> " +
		"<div align=\"center\"> " +
		"	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"add(this);\">上传</a> " +
		"	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"closeWin(this);\">关闭</a> " +
		"</div> "+
		"</div> "+
		"</form>";
}
/**
 * 【关闭】
 * 关闭当前上传窗口
 * @returns
 */
function closeWin(){
	JSPFree.closeDialog();
}

/**
 * 【上传】
 * 点击确认按钮实现把文件上传到系统
 * @returns
 */
function add(){
	var file = document.getElementById('filebox_file_id_1').files[0];
	if (file == null) { 
		$.messager.alert('提示', '文件不能为空，请重新选择文件！', 'warning');
		return; 
	}
	var fileName = file.name;
	var file_typename = fileName.substring(fileName.lastIndexOf('.'), fileName.length);
	if (file_typename != '.xlsx' && file_typename != '.xls') {
		return;
	}
	$('#importFileForm').form('submit',{
        url: v_context + "/bfd/rule/import",
        onSubmit: function(){
        	
        },
        success: function(result){
        	var data = JSON.parse(result);
            if (data.code == "success"){
            	JSPFree.closeDialog({ title: 'success', msg: "上传成功" });
            } else {
            	JSPFree.alert(data.msg);
            }
        },
		error: function (e) {
			console.log(e)
		}
    });
}	
