/**
 *
 * <pre>
 * Title:金综与大集中校验详情页面
 * Description
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/8/7 10:21
 */
var data_dt = "";
var rule_id = "";
var curr_cd = "";
var sys_id = "";
var rpt_name = "";
function AfterInit(){
    data_dt = jso_OpenPars.data_dt;
    rule_id = jso_OpenPars.rule_id;
    curr_cd = jso_OpenPars.curr_cd;
    sys_id = jso_OpenPars.sys_id;
    rpt_name = jso_OpenPars.rpt_name;
    // 查询条件
    var whereSql = "data_dt='" + data_dt + "' AND  rule_id='" + rule_id  +"' AND sys_id='" + sys_id +"' AND rpt_name='" + rpt_name +"'"
    if (curr_cd) {
        whereSql += " AND curr_cd='" + curr_cd + "'";
    }
    JSPFree.createBillList("d1","/biapp-bfd/freexml/resultDjzCheck/bfd_check_result_djz_detail_code.xml",null,{isSwitchQuery:"N",ishavebillquery:"N",autoquery:"Y",refWhereSQL:whereSql});
}


/**
 * 导出
 */
function tabDownload() {
    var fileName = "金综与大集中校验-"+sys_id + "-" +data_dt;
    JSPFree.downloadBillListDataAsExcel1(d1_BillList,fileName,"金综与大集中校验");
}