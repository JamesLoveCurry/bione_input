//初始化界面
function AfterInit(){
	document.getElementById("d1").innerHTML=
		"<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> "+
		"<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:350px;padding:60px 80px;\">" +
		"<div style=\"margin-bottom:50px\"> " +

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
function closeWin(){
	JSPFree.closeDialog();
}

/**
 * 上传
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
	if ((file_typename != '.xml') && (file_typename != '.zip')) {
		return;
	}
	
	var formData = new FormData($("#importFileForm")[0]);
	
	$('#importFileForm').form('submit',{
        url: v_context + "/frs/cams/datagram/receipt/uploadReceipt",
        onSubmit: function(){
        	
        },
        success: function(result){
            var result = eval('('+result+')');
            if (result.state){
            	if("success" == result.state){
            		JSPFree.closeDialog({ title: 'success', msg: "回执导入成功" });	
            	}else if("error" == result.state){
            		if("lengthError" == result.errorCode){
            			JSPFree.closeDialog({ title: 'error', msg: "回执报文名称格式错误" });
            		}else if("partLengthError" == result.errorCode){
            			JSPFree.closeDialog({ title: 'error', msg: "压缩包内部分回执报文名称格式错误" });
            		}else if("receiptRepeat" == result.errorCode){
            			JSPFree.closeDialog({ title: 'error', msg: "回执不可重复导入" });
            		}
            	}
            } else {
            	JSPFree.alert("上传失败");
            }
        }
    });
}	
