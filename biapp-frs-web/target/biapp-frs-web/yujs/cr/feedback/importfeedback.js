//初始化界面
function AfterInit(){
	document.getElementById("d1").innerHTML=
		"<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> "+
		"<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:350px;padding:60px 80px;\">" +
		"<div style=\"margin-bottom:50px\"> " +

		"<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'选择一个文件...'\" style=\"width:100%\">" +
		"</div> " +
		"<div align=\"center\"> " +
		"	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"importMData(this);\">上传</a> " +
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
function importMData(){
	var file = document.getElementById('filebox_file_id_1').files[0];
	if (file == null) {
		$.messager.alert('提示', '文件不能为空，请重新选择文件！', 'warning');
		return;
	}
	var fileName = file.name;
	var file_typename = fileName.substring(fileName.lastIndexOf('.'), fileName.length);
	if (file_typename != '.enc' && file_typename != '.zip') {
		JSPFree.alert("请选择.enc文件或.zip文件");
		return;
	}
	$('#importFileForm').form('submit',{
		url: v_context + "/cr/feedback/upload/file",
		onSubmit: function(){
		},
		//回调函数
		success: function(result){
			var result = eval('('+result+')');
			if (result.status == "SUCCESS"){
			    JSPFree.closeDialog();
			} else {
			    JSPFree.alert(result.msg);
			}
		}
	});
}
