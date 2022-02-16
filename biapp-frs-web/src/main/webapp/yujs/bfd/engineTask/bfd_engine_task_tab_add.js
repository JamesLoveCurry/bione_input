/**
 * 
 * <pre>
 * Title: 【引擎管理】
 * Description:【检核任务】--表：新增操作
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
    
	JSPFree.createBillCard("d1","/biapp-bfd/freexml/engineTask/bfd_engine_tab_task_CODE1.xml", ["保存/onSave/icon-ok"], null);
}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
	JSPFree.setBillCardItemEditable(d1_BillCard, "busi_type", false);
	if (org_class == BfdFreeUtil.getBfdOrgClass().zh) {
		// 如果是总行，则进行赋值
		JSPFree.setBillCardItemValue(d1_BillCard, "busi_org_no", org_no);
		JSPFree.setBillCardItemValue(d1_BillCard, "busi_type", "1");
	} else if (org_class == BfdFreeUtil.getBfdOrgClass().fh) {
		JSPFree.setBillCardItemValue(d1_BillCard, "busi_org_no", org_no);
		JSPFree.setBillCardItemValue(d1_BillCard, "busi_type", "2");
	}
	
	var taskCmd = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS", "getTaskCmd", {taskType: '表任务'});
	JSPFree.setBillCardItemValue(d1_BillCard, "command1", taskCmd.cmd);
    JSPFree.setBillCardItemValue(d1_BillCard, "create_at", taskCmd.create_at);
	
	$('#report_type').combobox({
		onSelect: function (_record) {
			JSPFree.setBillCardItemValue(d1_BillCard, "report_type", _record.text);
		}
	});
	
	if ($("#busi_org_no") && $("#busi_org_no").length > 0) {
		$("#busi_org_no").textbox({
			onChange: function (newValue, oldValue) {
				var org_class = BfdFreeUtil.getOrgClass(newValue)
				if (org_class == BfdFreeUtil.getBfdOrgClass().zh) {
					// 如果是总行，则进行赋值
					JSPFree.setBillCardItemValue(d1_BillCard, "busi_type", "1");
				} else if (org_class == BfdFreeUtil.getBfdOrgClass().fh) {
					JSPFree.setBillCardItemValue(d1_BillCard, "busi_type", "2");
				} else {
					JSPFree.setBillCardItemEditable(d1_BillCard, "busi_type", true);
				}
			}
		})
	}
}

/**
 * 【保存】按钮
 * @return {[type]} [description]
 */
function onSave() {
	var str_date = JSPFree.getBillCardItemValue(d1_BillCard, "data_dt");
	var str_busi_type = JSPFree.getBillCardItemValue(d1_BillCard, "busi_type");
	var str_busi_org_no = JSPFree.getBillCardItemValue(d1_BillCard, "busi_org_no");
	var str_report_type = JSPFree.getBillCardItemValue(d1_BillCard, "report_type");

	if (org_class == BfdFreeUtil.getBfdOrgClass().zh) {
		// 校验报送机构类型和报送机构号是否匹配
		var jso_org = JSPFree.doClassMethodCall("com.yusys.bfd.engineTask.service.BfdEngieTaskBS","checkOrgNoIsMatching", {busiOrgNo : str_busi_org_no});
		if (jso_org.msg == "OK") {
			if ((jso_org.orgClass==BfdFreeUtil.getBfdOrgClass().zh && str_busi_type=="1") 
					|| (jso_org.orgClass==BfdFreeUtil.getBfdOrgClass().fh && str_busi_type=="2")) {
				
			} else {
				JSPFree.alert("报送机构类型与报送机构号不匹配，总行报送对应总行机构号，分行报送对应分行机构号。请重新选择！");
				
				return;
			}
		} else {
			JSPFree.alert("当前获取机构类型与数据库配置不匹配！");
			
			return;
		}
	}
	JSPFree.setBillCardValues(d1_BillCard, {task_id:FreeUtil.getUUIDFromServer(), task_type:'表任务', status:'初始化', create_tm:getNowFormatDate});

	var jso_templetVO = d1_BillCard.templetVO; // 模板配置数据
	// 取得表单数据
	var jso_formData = JSPFree.getBillCardFormValue(d1_BillCard);
	// 空值校验
	var isValidateNullSucess = FreeUtil.validateNullBilldData(d1_BillCard,
		jso_templetVO, jso_formData);
	if (!isValidateNullSucess) {
		return false;
	}
	var quarter_report = JSPFree.doClassMethodCall("com.yusys.bfd.tabconfig.service.BfdCrTabBS","getQuarterReport", {});
	var tab_names_arr = JSPFree.getBillCardItemValue(d1_BillCard, "tab_names").split(";");
	tab_names_arr.pop();
	if(str_report_type == 1){
		for(var i=0; i<tab_names_arr.length; i++){
			if(quarter_report.tabNames.indexOf(tab_names_arr[i]) > -1){
				$.messager.alert('提示', '报表类型和表不匹配', 'warning');
				return;
			}
		}
	} else {
		for(var i=0; i<tab_names_arr.length; i++){
			if(quarter_report.tabNames.indexOf(tab_names_arr[i]) == -1){
				$.messager.alert('提示', '报表类型和表不匹配', 'warning');
				return;
			}
		}
	}

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