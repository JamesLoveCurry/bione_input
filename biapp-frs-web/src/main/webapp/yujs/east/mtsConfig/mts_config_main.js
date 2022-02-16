/**
 *
 * <pre>
 * Title:MTS 配置
 * Description:
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/6/5 16:24
 */

var tab_name = "";
var tab_name_en = "";
var str_subfix = "";
var str_className = "";
function AfterInit() {

    //JSPFree.createBillList("d1","/biapp-east/freexml/east/report/east_cr_report_config_CODE1.xml",null,{isSwitchQuery:"N"});



    str_className = "/biapp-east/freexml/east/mtsConfig/east_mts_config.xml";
    JSPFree.createBillList("d1", "/biapp-east/freexml/east/mtsConfig/east_mts_config.xml", null, {list_btns:"[icon-p99]新增/onInsert;[icon-edit]编辑/onUpdate;[icon-remove]删除/onDeleteConfig;$VIEW;", isSwitchQuery:"N", list_ischeckstyle:"Y" ,list_ismultisel:"Y", orderbys:"rpt_org_no,server_ip"});
}

/**
 * 新增
 * @return {[type]} [description]
 */
function onInsert() {
    var defaultVal = {templetcode:str_className};
    JSPFree.openDialog("新增", "/yujs/east/mtsConfig/mts_config_add.js", 900, 560, defaultVal, function(_rtdata) {
        if (_rtdata == "保存成功") {
            JSPFree.alert("保存成功!");
        }
        JSPFree.queryDataByConditon(d1_BillList);
    }, true);
}

/**
 * 删除
 * @return {[type]} [description]
 */
function onDeleteConfig(){
    // 执行删除(批量)
    JSPFree.doBillListBatchDelete(d1_BillList);
}

/**
 * 编辑
 * @return {[type]} [description]
 */
function onUpdate() {
    var mts = d1_BillList.datagrid('getSelected');
    if (mts == null) {
        $.messager.alert('提示', '必须选择一条数据!', 'info');
        return;
    }
    var jsy_datas = d1_BillList.datagrid('getSelections'); //
    if (jsy_datas.length > 1) {
        JSPFree.alert('只能选择一条数据进行操作!<br>目前共选择了【' + jsy_datas.length
            + '】条,请注意滚动框下面是否还有其他选择!');
        return;
    }
    
    var defaultVal = {templetcode:str_className,mts:mts};
    JSPFree.openDialog("编辑", "/yujs/east/mtsConfig/mts_config_update.js", 900, 560, defaultVal, function(_rtdata) {
        if (_rtdata == "保存成功") {
            JSPFree.alert("编辑成功!");
        }
        JSPFree.queryDataByConditon(d1_BillList);
    }, true);
}