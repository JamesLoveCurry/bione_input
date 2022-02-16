/**
 * 
 * <pre>
 * Title: 【引擎管理】
 * Description:【检核任务】--规则：新增操作
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月17日
 */

var org_no = "";
var org_class = "";
function AfterInit() {
    org_no = jso_OpenPars.org_no;
    org_class = jso_OpenPars.org_class;

	JSPFree.createBillCard("d1","/biapp-imas/freexml/engineTask/imas_engine_rule_task_CODE1.xml",["保存/onSave/icon-ok"],null);
}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
	var taskCmd = JSPFree.doClassMethodCall("com.yusys.imas.engineTask.service.ImasEngieTaskBS", "getTaskCmd", {taskType: '规则任务'});
	JSPFree.setBillCardItemValue(d1_BillCard, "command1", taskCmd.cmd);
	JSPFree.setBillCardItemValue(d1_BillCard, "create_at", taskCmd.create_at);
	// 屏蔽日报和月报字段选择，默认日报。
	JSPFree.setBillCardItemValue(d1_BillCard, "report_type", '1');

	var str_fnName = "onBillCardItemEdit"; // 在创建表单时会创建这个函数
	if(typeof self[str_fnName] == "function") {  // 如果的确定义了这个函数
		FreeUtil.addBillCardItemEditListener(d1_BillCard, self[str_fnName]);  // 增加监听事件
	}
	// 处理After逻辑
	var str_fnName_after = "afterInsert";
	if (typeof self[str_fnName_after] == "function") {  // 如果的确定义了这个函数
		self[str_fnName_after](d1_BillCard);  // 增加监听事件
	}
	FreeUtil.setBillCardLabelHelptip(d1_BillCard); // 必须写在最后一行

	if (org_class == ImasFreeUtil.getImasOrgClass().fh) {
		JSPFree.setBillCardItemValue(d1_BillCard, "check_scope", '2');
		JSPFree.setBillCardItemEditable(d1_BillCard, "check_scope", false);
	}
}
/**
 * 新增之后将不展示项默认不展示
 * @param _billCard
 */
function afterInsert(_billCard) {
	JSPFree.setBillCardItemVisible(_billCard, "busi_org_no", false);
}
/**
 * 部分机构检核时，展示报送机构号
 * @param _billCard
 * @param _itemkey
 * @param _jsoValue
 */
function onBillCardItemEdit(_billCard, _itemkey, _jsoValue) {
	if (_itemkey == "check_scope") {
		var str_value = _jsoValue;
		if (str_value.value == "2") {
			JSPFree.setBillCardItemVisible(_billCard, "busi_org_no", true);
		} else {
			JSPFree.setBillCardItemVisible(_billCard, "busi_org_no", false);
			JSPFree.setBillCardItemValue(_billCard, "busi_org_no", "");
		}
	}
}
/**
 * 【保存】按钮
 * @return {[type]} [description]
 */
function onSave() {
	var str_busi_org_no = JSPFree.getBillCardItemValue(d1_BillCard, "busi_org_no");
	var check_scope = JSPFree.getBillCardItemValue(d1_BillCard, "check_scope");
	if (check_scope == '2') {
		if (str_busi_org_no == null || str_busi_org_no == "") {
			$.messager.alert('提示', '检核范围为部分机构校验时，报送机构不可为空!', 'warning');
			return;
		}
	}
	JSPFree.setBillCardValues(d1_BillCard, {task_id:FreeUtil.getUUIDFromServer(), task_type:'规则任务', status:'初始化', create_tm:getNowFormatDate});
	var flag = JSPFree.doBillCardInsert(d1_BillCard);
	if (flag) {
		JSPFree.closeDialog(flag);
	}
}

/**
 * 获取时间
 * @returns
 */
function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    // 外国的月份都是从0开始的，所以+1
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    // 1-9月用0补位
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    // 1-9日用0补位
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    // 获取当前时间 yyyy-MM-dd HH:mm:ss
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate + " " +date.getHours() + seperator2 + date.getMinutes() + seperator2 + date.getSeconds();
    
    return currentdate;
}

function getTabByUser() {
	var whereSql = '';
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.imas.common.service.ImasGetReportBS", "getReportList", null);
	if (jsn_result.code == 'success') {
		whereSql = jsn_result.data;
	}
	return whereSql;
}