/**
 *
 * <pre>
 * Title:【报表处理】【报表数据审核】
 * Description:报表数据审核：主页面
 * </pre>
 * @author baifukuan
 * @date 2021-02-28
 */
var tab_name = "";
var tab_name_en = "";
var str_subfix = "";
var str_className = "";

function AfterInit() {
    // 获取路径参数
    if (jso_OpenPars != '') {
        if(jso_OpenPars.type != null){
            str_subfix = jso_OpenPars.type;
        }
    }
    
    JSPFree.createSplit("d1", "左右", 245); // 页签先上下分割
    var templetCode = 'Class:com.yusys.safe.base.common.template.TabBuilderTemplate.getReportTabTemplet('+str_subfix+')';
    JSPFree.createBillTree("d1_A",templetCode);
    document.getElementById("d1_B").innerHTML="<div style='width:100%;text-align:center'><span style='font-size:16px;color:blue'>请选择左边表查询数据!</span></div>";
    
    JSPFree.bindBillTreeOnSelect(d1_A_BillTree, function(_node) {
        var isLeaf_1 = JSPFree.isBillTreeSelectNodeLeaf(d1_A_BillTree); // 是否选中的是叶子结点
        if(isLeaf_1) { // 如果选中了叶子结点
            var jso_data = JSPFree.getBillTreeSelectNode(d1_A_BillTree); // 取得选中的数据
            tab_name = jso_data.text;
            var jso_data = JSPFree.doClassMethodCall(
                "com.yusys.safe.base.common.service.SafeCommonBS",
                "getTabNameByEn", {tab_name:tab_name, report_type:str_subfix});
            tab_name_en = jso_data.tab_name_en;
            var str_ds = jso_data.ds_name;

            // 判断选中的叶子节点，是否含有字段信息
            var jso_par = { str_tab_name:tab_name };
            var jso_data = JSPFree.doClassMethodCall("com.yusys.safe.reporttask.service.SafeCrReportBS","checkTabCols",jso_par);
            var str_html = jso_data.msg;  // 返回的html
            if (str_html != null && str_html != "") {
                document.getElementById("d1_B").innerHTML="<div style='width:100%;text-align:center'><span style='font-size:16px;color:red'>"+jso_data.msg+"!</span></div>";

                return;
            }
            
            // 20200727 修复%传参问题 wangxy31
            if (tab_name.indexOf('%') != 0 && tab_name.indexOf('%25') == -1) {
            	tab_name = tab_name.replace(/%/, '%25');
            }
            var v_par = {"tab_name":tab_name,"tab_name_en":tab_name_en,"ds":str_ds,"report_type":str_subfix};
            JSPFree.createOrReplaceIFrame("d1_B","/yujs/safe/busidata/busi_data_report_approval_list.js", v_par);
        }
    });
}