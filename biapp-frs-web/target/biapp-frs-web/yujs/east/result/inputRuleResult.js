var type = "";
//初始化界面
function AfterInit(){
	type = jso_OpenPars.type;
	document.getElementById("d1").innerHTML=
		"<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> "+
		"<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:350px;padding:60px 80px;\">" +
		"<div style=\"margin-bottom:50px\"> " +

		"<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'选择一个文件...'\" style=\"width:100%\">" +
		"<input type=\"text\" name=\"type\" style=\"display:none\" value="+type+">" +
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
	if (file_typename != '.xlsx' && file_typename != '.xls') {
		return;
	}
	
	$('#importFileForm').form('submit',{
        url: v_context + "/east/rules/checkRule/uploadRuleResult",
        success: function(_data){
        	var result = JSON.parse(_data);
        	if(result.success == "true"){
        		$.messager.show({
                    title: 'Success',
                    msg: '上传成功'
                });
        	}else if(result.msg!=null && result.msg!=""){
        		$.messager.show({
                    title: 'Error',
                    msg: result.msg
                });
        	}else{
        		$.messager.show({
                    title: 'Error',
                    msg: '上传失败'
                });
        	}
        	
        	var str_html = result.str_html;
        	if (str_html != null && str_html != "") {
        		var str_htmlleft = str_html.replace(new RegExp("&lt;","gm"),"<");
            	var str_htmlright = str_htmlleft.replace(new RegExp("&gt;","gm"), ">");
            	var str_json = {"str_html":str_htmlright};
    			JSPFree.openDialog2("导入文件中有未匹配到的数据，未执行导入","/yujs/east/result/inputRuleRestltData.js", 900, 560, str_json,function(_rtdata){
           	
    			});
        	}
        }
    });
}	
