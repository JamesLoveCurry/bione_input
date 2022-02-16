//初始化界面
var tabName = "";
var tabNameEn = "";
var dataDt = "";
function AfterInit() {
    tabName = jso_OpenPars2.tabName;
    tabNameEn = jso_OpenPars2.tabNameEn;
    dataDt = jso_OpenPars2.dataDt;
    
    var str_className1 = "Class:com.yusys.east.business.model.service.East58ModelTempletBuilder2.getTemplet('" + tabName + "','" + tabNameEn + "','" + str_LoginUserOrgNo + "','_R')";
    JSPFree.createBillCard("d1", str_className1, ["保存/onSave/icon-p21", "取消/onCancel/icon-undo"], null);
    
    JSPFree.setBillCardValues(d1_BillCard, jso_OpenPars2.json_data);
    d1_BillCard.OldData = jso_OpenPars2.json_data;
}

var col_arr = [];

function AfterBodyLoad() {
    // 设置表单中的主键不可进行编辑
    var tabName = jso_OpenPars2.tabName;
    var jso_col = JSPFree.getHashVOs("select col_name_en from east_cr_col where tab_name='" + tabName + "' and is_pk='Y' order by col_no");
    if (jso_col != null && jso_col.length > 0) {
        for (var i = 0; i < jso_col.length; i++) {
            col_arr.push(jso_col[i].col_name_en);
            JSPFree.setBillCardItemEditable(d1_BillCard, jso_col[i].col_name_en.toLowerCase(), false);
        }
    }
    
    JSPFree.setBillCardItemEditable(d1_BillCard, "cjrq", false);
    JSPFree.setBillCardItemEditable(d1_BillCard, "kjrq", false);
    JSPFree.setBillCardItemEditable(d1_BillCard, "nbjgh", false);
	
	var param = {
		tabNameEn: jso_OpenPars2.tabNameEn,
		colArry: col_arr,
		dataDt: dataDt,
		jsonData: jso_OpenPars2.json_data,
        orgNo:str_LoginUserOrgNo
	};
	var ruleIds = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "getSingleDataCheckRuleIds", param);
	var _formData = JSPFree.doBillCardUpdateByCheck(d1_BillCard, null);
	var str_ds = "";
	var ds_name = JSPFree.getHashVOs("select ds_name from east_cr_tab where tab_name='" + tabName + "'");
	if (ds_name != null && ds_name.length > 1) {
		str_ds = ds_name[0].da_name;
	}
	doShowSingleCheckResult(ruleIds, d1_BillCard, _formData, tabName, tabNameEn, str_ds)
}

/**
 * 保存
 * @return {[type]} [description]
 */
var saveFlag = "";

function onSave() {
    var str_ds = "";
    var ds_name = JSPFree.getHashVOs("select ds_name from east_cr_tab where tab_name='" + tabName + "'");
    if (ds_name != null && ds_name.length > 1) {
        str_ds = ds_name[0].da_name;
    }
    
    var backValue = JSPFree.editTableCheckData(d1_BillCard, "Edit", tabName, tabNameEn.toUpperCase(), str_ds, "1");
    if (backValue == "" || "undifind" == backValue) {
        return;
    } else if (backValue == "OK") {
        saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);
        
        if (saveFlag == true) {
            // 此处在更新一个数据表状态值
            var dd = {
                tabNameEn: jso_OpenPars2.tabNameEn,
                colArry: col_arr,
                status: '1',
                jsonData: jso_OpenPars2.json_data
            };
            var jsn_result = JSPFree.doClassMethodCall("com.yusys.east.process.service.EastCrProcessBSDMO", "updateTemplateTabs", dd);
            
            if (jsn_result.msg == 'OK') {
                JSPFree.closeDialog(true);
            }
        }
    } else if (backValue == "Fail") {
        return;
    }
}

function onCancel() {
    JSPFree.closeDialog(null);
}

function doShowSingleCheckResult(jso_rt, _billCard, _formData, _tabname, _tabnameen, _ds) {
    if (jso_rt.status = "OK") {
        var ruleIds = jso_rt.ruleId;
        // 判断返回的ruleId是否为null，如果为null则表示数据验证通过，如果不为null则表示验证失败
        if (ruleIds == "" || ruleIds == null) {
            
            // 有可能重复点击保存按钮，要清空页面高亮显示
            JSPFree.setBillCardItemWarnMsgVisible(_billCard, false);
            JSPFree.setBillCardItemColor(_billCard, "*", "");
            
            return "OK";
        }
        
        // 有可能重复点击保存按钮，要清空页面高亮显示
        JSPFree.setBillCardItemWarnMsgVisible(_billCard, false);
        JSPFree.setBillCardItemColor(_billCard, "*", "");
        
        // 验证失败，则页面进行规则展示
        var ruleArr = ruleIds.split(',');
        
        // east传过来的tabName为中文表名
        var jso_data1 = JSPFree.getHashVOs("select distinct c.col_name_en,c.col_name from east_cr_rule r,east_cr_col c " +
            "where r.tab_name = c.tab_name " +
            "and r.col_name = c.col_name " +
            "and c.IS_EXPORT='Y' " +
            "and (rule_sts='Y' or rule_sts is null) " +
            "and r.tab_name='" + _tabname + "'" +
            "and id in (" + ruleArr + ")");
        
        var colnames = [];
        if (jso_data1 != null && jso_data1 != "" && jso_data1.length > 0) {
            for (var j = 0; j < jso_data1.length; j++) {
                var colname_en = jso_data1[j].col_name_en;
                var colname = jso_data1[j].col_name;
                var jso_data2 = JSPFree.getHashVOs("select rule_name problemmsg from east_cr_rule " +
                    "where (rule_sts='Y' or rule_sts is null) and tab_name ='" + _tabname + "' " +
                    "and col_name = '" + colname + "' and id in (" + ruleArr + ")");
                
                var problemmsg = "";
                if (jso_data2 != null && jso_data2 != "" && jso_data2.length > 0) {
                    for (var k = 0; k < jso_data2.length; k++) {
                        problemmsg = problemmsg + (k + 1) + "." + jso_data2[k].problemmsg + "<br>";
                    }
                }
                JSPFree.setBillCardItemWarnMsg(_billCard, colname_en.toLowerCase(), problemmsg);
                JSPFree.setBillCardItemColor(_billCard, colname_en.toLowerCase(), "yellow");
            }
        }
        return "Fail";
    } else {
        JSPFree.alert("请求异常!");
    }
}
