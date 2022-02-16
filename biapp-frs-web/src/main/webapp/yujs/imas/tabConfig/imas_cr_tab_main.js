/**
 *
 * <pre>
 * Title:【配置管理】【报表管理】
 * Description:报表管理主页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/8/7 11:30
 */
function AfterInit(){
    var whereSql = '';
    var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasGetReportBS", "getReportList", null);
    if (jsn_result.code == 'success') {
        whereSql = jsn_result.data;
    }
    JSPFree.createBillList("d1","/biapp-imas/freexml/tabConfig/imas_cr_tab_code.xml",null,{isSwitchQuery:"N", refWhereSQL: whereSql});
}

/**
 * 删除tab表，同时删除col字段
 */
function deleteTab(){
    var json_data = d1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length == 0) {
        $.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
        return;
    }

    JSPFree.confirm('提示', '你真的要删除选中的记录吗?', function(_isOK){
        if (_isOK){
            var jso_alltabs=[]; //存放所有选中表名和英文表名的数组
            for(var i=0; i<json_data.length; i++){
                var jso_tab=[]; //存放一条表名和英文表名的数组
                jso_tab.push(json_data[i].tab_name_en);
                jso_tab.push(json_data[i].tab_name);
                jso_alltabs.push(jso_tab);
            }
            var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.tabconfig.service.ImasCrTabBS", "deleteColsByTabName", {alltabs:jso_alltabs});
            if (jsn_result.msg == 'OK') {
                $.messager.alert('提示', '删除数据成功!', 'info');
            }
            if (jsn_result.msg == 'OK') {
                JSPFree.queryDataByConditon(d1_BillList);
            }
        }
    });
}

/**
* 校验表结构
* 对报表的物理表结构与系统中该报表的虚拟结构进行校验，判断是否有差异。
* 若结构一致则提示校验通过，若不一致则提示差异字段
* @returns
*/
function checkCols() {
    var json_data = d1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data.length>1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }
    var jso_Pars = {tabNameEn:json_data[0].tab_name_en,tabName:json_data[0].tab_name, dsName: json_data[0].ds_name};

    var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.tabconfig.service.ImasCrTabBS", "verificationData", jso_Pars);

    if(jsn_result.result=="false"){
        bottomRight(jsn_result.msg);
    } else{
        // 弹出窗口,传入参数,然后接收返回值
        var str_html = jsn_result.msg;
        JSPFree.openHtmlMsgBox("系统中未匹配上字段",900,560,str_html);
    }
}

/**
 * 右下角弹出框，消息提示
 * @param _msg
 * @returns
 */
function bottomRight(_msg){
    $.messager.show({
        title:'消息提示',
        msg:_msg,
        showType:'show'
    });
}

/**
 * 批量生成物理表
 */
function createTables() {
    var json_data = d1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length == 0) {
        $.messager.alert('提示', '必须至少选择一条记录进行操作', 'warning');
        return;
    }
    var tableNames = []; // 所有的英文表名
    for (var i = 0; i<json_data.length; i++) {
        if (json_data[i].tab_belong != '业务表') {
            $.messager.alert('提示', '只能生成业务表！', 'warning');
            return;
        }
        tableNames.push(json_data[i].tab_name_en);
    }
    $.messager.confirm('提示', '生成物理表如果已存在,则会删除原有数据,重新生成,是否继续?', function(_isConfirm) {
        if (!_isConfirm) {
            return;
        }
        var jso_data = JSPFree.doClassMethodCall("com.yusys.imas.tabconfig.service.ImasCrTabBS", "createTables", {tableNames:tableNames});
        if (jso_data.code == "success") {
            $.messager.alert('提示', '生成物理表成功!', 'info');
        } else {
            $.messager.alert('提示', jso_data.msg, 'warning');
        }

    });
}

/**
 * 一键重新生成物理表
 */
function createAllTables() {
    $.messager.confirm('提示', '一键重新生成物理表,会删除原有数据,是否继续?', function(_isConfirm) {
        if (!_isConfirm) {
            return;
        }
        var jso_data = JSPFree.doClassMethodCall("com.yusys.imas.tabconfig.service.ImasCrTabBS", "createAllTables");
        if (jso_data.code == "success") {
            $.messager.alert('提示', '重新生成物理表成功!', 'info');
        } else {
            $.messager.alert('提示', jso_data.msg, 'warning');
        }
    });
}

/**
 * 维护字段
 * 对报表的各个字段信息进行维护，包括字段的中文名称，英文名称等
 * @returns
 */
function maintainCols() {
    var json_data = d1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length ==0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data.length>1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }
    var jsonData = json_data[0];
    jsonData.ds_url = "";
    // 弹出窗口,传入参数,然后接收返回值!
    JSPFree.openDialog("表字段","/yujs/imas/tabConfig/imas_cr_col_main.js",1000,535,jsonData,function(_rtdata){

    });
}
/**
 *维护关联字段的sql
 */
function editSql() {
    var json_data = d1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length ==0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data.length>1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }
    var jsonData = json_data[0];
    jsonData.ds_url = "";
    // 弹出窗口,传入参数,然后接收返回值!
    JSPFree.openDialog("SQL维护","/yujs/imas/tabConfig/imas_cr_tab_sql.js",800,435,jsonData,function(_rtdata){
        if (_rtdata == "保存成功") {
            JSPFree.alert("编辑成功!");
        }
        JSPFree.queryDataByConditon(d1_BillList);
    });
}

/**
 *维护关联字段的sql
 */
function executeSql() {
    var json_data = d1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length ==0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data.length>1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }
    var strSql  = json_data[0].execute_sql;
    var jso_data = JSPFree.doClassMethodCall("com.yusys.imas.tabconfig.service.ImasCrTabBS", "executeSql",{"sqlStr": strSql});
    if (jso_data.code == "success") {
        $.messager.alert('提示', '执行SQL成功!', 'info');
    } else {
        $.messager.alert('提示', jso_data.msg, 'warning');
    }
}