/**
 *
 * <pre>
 * Title:pdf上传
 * Description:
 * </pre>
 * @author liqiang
 * @version 1.00.00
 * @date 2021/05/24 11:10
 */
var data_dt = null;
var org_no = null;
var zljh_status = null;
var bsqd_status = null;

//初始化界面
function AfterInit() {

    data_dt = jso_OpenPars.data_dt;
    org_no = jso_OpenPars.org_no;
    zljh_status = jso_OpenPars.zljh_status;
    bsqd_status = jso_OpenPars.bsqd_status;

    document.getElementById("d1").innerHTML =
        "<form id=\"importFileForm\" method=\"post\" enctype=\"multipart/form-data\"> " +
        "<div class=\"easyui-panel\"  id=\"import-excel-template\" style=\"width:100%;heisht:100%;max-width:500px;max-height:450px;padding:60px 80px;\">" +
        "<div style=\"margin-bottom:50px\"> " +

        "<input class=\"easyui-filebox\" id=\"fileImport\" name=\"fileImport\" data-options=\"buttonText:'选择文件',prompt:'选择一个文件...'\" style=\"width:100%\">" +
        "<span style='color: red;white-space:pre-wrap;'>注意：\n(1).报送清单：四类机构代码+\"-\"+YYYYMMDD+\"-bsqd\".pdf,如：L0241H211000001-20210930-bsqd.pdf\n</span>" +
        "<span style='color: red'>(2).质量检核：四类机构代码+\"-\"+YYYYMMDD+\"-zljh\".pdf,如：L0241H211000001-20210930-zljh.pdf\n</span>" +
        "</div> " +
        "<div align=\"center\"> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"importdata(this);\">上传</a> " +
        "	<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"closeWin(this);\">关闭</a> " +
        "</div> " +
        "</div> " +
        "</form>";
}

/**
 * 关闭
 * @returns
 */
function closeWin() {
    JSPFree.closeDialog();
}

function extracted() {
    var formData = new FormData($("#importFileForm")[0]);
    $('#importFileForm').form('submit', {
        url: v_context + "/east/data/upload/pdf",
        onSubmit: function () {

        },
        success: function (result) {

            if (result == "success") {
                JSPFree.closeDialog(true);
            } else {
                $.messager.alert({
                    title: '系统提示',
                    msg: '<div style="height:100px;overflow-y:scroll">' + "上传失败" + '</div>',
                    width: 320,
                    top: 130,
                    icon: 'info'
                });
            }
        }
    });
}

/**
 * 上传
 * @returns
 */
function importdata() {
    var file = document.getElementById('filebox_file_id_1').files[0];
    if (file == null) {
        $.messager.alert('提示', '文件不能为空，请重新选择文件！', 'warning');
        return;
    }
    var fileName = file.name;
    var file_typename = fileName.substring(fileName.lastIndexOf('.'), fileName.length);
    if (file_typename != '.pdf') {
        JSPFree.alert("请选择pdf文件");
        return;
    }
    var flag = true;
    var split = fileName.split(".")[0].split("-");
    if (split.length == 3) {
        var fileType = split[2];
        if ("bsqd" != fileType && "zljh" != fileType) {
            JSPFree.alert("文件名应包含\"bsqd\"或\"zljh\"");
            return;
        }
        if ("bsqd" ==fileType){
            if (bsqd_status == "Y"){
                flag = false;
            }
        }else {
            if (zljh_status == "Y"){
                flag = false;
            }
        }
        if (data_dt != split[1]) {
            JSPFree.alert("日期与当前数据对不上");
            return;
        }
        var hashVOs = JSPFree.getHashVOs("select finance_org_no from rpt_org_info where org_no='" + org_no + "' and org_type='04' and is_org_report='Y'");
        if (hashVOs == null || hashVOs.length == 0) {
            var financeOrgNo = hashVOs[0].finance_org_no;
            if (split[0] != financeOrgNo) {
                JSPFree.alert("四类机构代码与当前数据对不上");
            }
        }
    } else {
        JSPFree.alert("文件名格式错误");
        return;
    }

    if (flag){
        extracted();
    } else {
        JSPFree.confirm('提示', '该PDF已经上传，是否覆盖?', function(_isOK){
            if (_isOK){
                extracted();
            }
        });
    }

}