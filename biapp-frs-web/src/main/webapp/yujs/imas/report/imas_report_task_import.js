/**
 *
 * <pre>
 * Title:强制提交
 * Description:
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/10/26 11:10
 */
var rid = "";
var orgNo = "";
//初始化界面
function AfterInit(){
    rid = jso_OpenPars.rid;
    orgNo = jso_OpenPars.orgNo;
    document.getElementById("d1").innerHTML=
        "<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> "+
        "<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;padding:60px 80px;\">" +
        "<div style=\"margin-bottom:50px\"> " +
        "<textarea style='width: 100%;outline: none;'  class=\"textarea easyui-validatebox\" rows=5 id=\"desc\" name=\"desc\" data-options=\"prompt:'请填写异常说明,异常说明不能为空。'\" ></textarea>" +
        "<span style='color: red'>* 在这里输入强制提交原因文字说明,此内容为必填项!</span>"+
        "<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'请选择要提交的说明文件,文件格式为doc或者docx'\" style=\"width:100%; margin-top: 10px\">" +
        "<span style='color: red'>* 在这里输入强制提交说明文档,此内容为必填项!</span>"+
        "<input hidden name='rid' id='rid' value='" +rid + "'/>"+
        "<input hidden name='orgNo' id='orgNo' value='" +orgNo + "'/>"+
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
    var desc = document.getElementById("desc").value;
    if (!desc) {
        JSPFree.alert("强制提交说明不能为空!");
        return;
    }
    if (desc.length > 300) {
        JSPFree.alert("最多输入300个字符!");
        return;
    }
    var file = document.getElementById('filebox_file_id_1').files[0];
    if (file) {
        var fileName = file.name;
        var file_typename = fileName.substring(fileName.lastIndexOf('.'), fileName.length);
        if (file_typename != '.doc' && file_typename != '.docx') {
            JSPFree.alert("只能导入doc和docx文件");
            return;
        }
        var formData = new FormData($("#importFileForm")[0]);
    } else {
        JSPFree.alert("强制提交文件不能为空!");
        return;
    }
    $('#importFileForm').form('submit',{
        url: v_context + "/imas/report/importFile",
        onSubmit: function(){

        },
        success: function(result){
            var result = eval('('+result+')');

            if (result.code == "success"){
                JSPFree.closeDialog("success");
            } else {
                JSPFree.alert(result.msg);
            }
        }
    });
}
