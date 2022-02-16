/**
 * 导入的错误提示窗口
 * @author bfk
 * @type {string}
 */
var message = "";

function AfterInit() {
    message = jso_OpenPars.message;
    document.getElementById("d1").innerHTML =
        "<div  class=\"easyui-panel\" style=\"width:100%;height:100%;max-width:600px;padding:60px 80px;\">" +
            "<div style=\"margin-bottom:50px;overflow-y: auto\"> " +
                "<span style='color: red;white-space:pre-wrap;'>" + message.msg + "</span>" +
            "</div> " +
        "</div> " +
        "<div align=\"center\"> " +
        "<a href=\"javascript:void(0)\" class=\"easyui-linkbutton\" style=\"width:20%;margin:0 auto;\" onclick=\"closeWin(this);\">关闭</a> " +
        "</div>";
}

/**
 * 关闭
 * @returns
 */
function closeWin() {
    JSPFree.closeDialog();
}
