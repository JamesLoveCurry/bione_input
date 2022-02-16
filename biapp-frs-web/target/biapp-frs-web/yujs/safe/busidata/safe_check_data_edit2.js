/**
 *
 * <pre>
 * Title:【报表处理】【报表数据维护】
 * Description:单条数据检核
 * </pre>
 * @author wangxy31
 * @version 1.00.00
 * @date 2020年6月28日
 */

var rid = "";
var type = ""; // 新增Add、修改Edit
var template = ""; // 模板路径
var tabname = "";
var tabnameen = "";
var jso_listData = "";
var str_ds = "";
var report_type = "";
var custcode = "";
var custname = "";
var data_dt = "";

function AfterInit() {
	type = jso_OpenPars.type;
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	str_ds = jso_OpenPars.str_ds;
	report_type = jso_OpenPars.report_type;
	
	custcode = jso_OpenPars.custcode;
	custname = jso_OpenPars.custname;
	data_dt = jso_OpenPars.data_dt;

	var jso_listData = self.jso_OpenPars2; // 列表中传入的数据

	rid = jso_listData.rid;
	template = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tabname + "','" + tabnameen + "','" + report_type + "','" + str_LoginUserOrgNo + "')";
    
	if (type == "Add") {
		JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
		JSPFree.execBillCardDefaultValueFormula(d1_BillCard);
	} else if (type == "Edit") {
		JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
		JSPFree.execBillCardDefaultValueFormula(d1_BillCard);
		
		d1_BillCard.OldData = jso_listData;
		// 赋值
		JSPFree.queryBillCardData(d1_BillCard, "rid = '"+rid+"'");
	} else if (type == "View") {
		JSPFree.createBillCard("d1",template,["取消/onCancel/icon-undo"],null);
		JSPFree.execBillCardDefaultValueFormula(d1_BillCard);
		
		d1_BillCard.OldData = jso_listData;
		// 赋值
		JSPFree.queryBillCardData(d1_BillCard, "rid = '"+rid+"'");
	}
}

/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
	var str_fnName = "onBillCardItemEdit"; // 在创建表单时会创建这个函数
    if (typeof self[str_fnName] == "function") {  // 如果的确定义了这个函数
        FreeUtil.addBillCardItemEditListener(d1_BillCard, self[str_fnName]);  // 增加监听事件
    }
    
	// 如果是查看页面，要进行置灰，不可编辑
	if (type == "View") {
		JSPFree.setBillCardItemEditable(d1_BillCard, "*", false);
	} else if (type == "Add") {
		JSPFree.setBillCardItemValue(d1_BillCard, "custcode", custcode);
		JSPFree.setBillCardItemValue(d1_BillCard, "custname", custname);
		JSPFree.setBillCardItemValue(d1_BillCard, "data_dt", data_dt);
		JSPFree.setBillCardItemEditable(d1_BillCard, "data_dt,custcode,custname", false);
		JSPFree.setBillCardItemVisible(d1_BillCard, "taxfreecode", false);
	} else if (type == "Edit") {
		JSPFree.setBillCardItemEditable(d1_BillCard, "data_dt,custcode,custname", false);
		var jso_istaxfree = JSPFree.getBillCardFormValue(d1_BillCard).istaxfree;
		if (jso_istaxfree == null || jso_istaxfree == "" || jso_istaxfree == "N") {
			JSPFree.setBillCardItemVisible(d1_BillCard, "taxfreecode", false);
		}
	}

    FreeUtil.setBillCardLabelHelptip(d1_BillCard); // 必须写在最后一行
}

/**
 * 如果选择“是”，“特殊经济区内企业类型”提示必输“*”，
 * 选择“否”，“特殊经济区内企业类型”隐藏
 * @param _billCard
 * @param _itemkey
 * @param _jsoValue
 */
function onBillCardItemEdit(_billCard, _itemkey, _jsoValue) {
	if (_itemkey == "istaxfree") {
		var str_value = _jsoValue;
		if (str_value == "Y") {
			JSPFree.setBillCardItemVisible(_billCard, "taxfreecode", true);
			JSPFree.setBillCardItemIsMust(_billCard,"taxfreecode",true);
		} else {
			JSPFree.setBillCardItemVisible(_billCard,"taxfreecode",false);
			JSPFree.setBillCardItemIsMust(_billCard,"taxfreecode",false);
		}
	}
	
	if (_jsoValue.newValue != _jsoValue.oldValue && _jsoValue.newValue != "" && _jsoValue.newValue != null) {
		var jsa_items = _billCard.templetVO.templet_option_b;
		for(var i=0;i<jsa_items.length;i++){
			var str_colname = jsa_items[i].itemkey; // 字段名
			var str_formatvalidate = jsa_items[i].formatvalidate;
            var str_itemname = jsa_items[i].itemname;
            var str_maxlen = jsa_items[i].maxlen;

			if (str_colname == _itemkey) {
				if (typeof str_formatvalidate != "undefined" && str_formatvalidate != null && str_formatvalidate != "" && str_formatvalidate.indexOf("数字文本") != -1) {
					var stringResult = str_formatvalidate.split('/');
					var isOK = false; 
                	var p_str = "";
                	var m_str = "";

            		if (stringResult[1].indexOf("精度") != -1) {
            			p_str = stringResult[1].slice(3);
            		}
            		
                	if (stringResult[2].indexOf("提示信息") != -1) {
            			var p_ts = stringResult[2].slice(5);
            			if (p_ts != "") {
            				m_str = "【" + p_ts + "】!<br>";
            			} else {
            				m_str = m_str + "【" + str_itemname + "】必须是数字!<br>";
            			}
            		}

					// 调取校验之前，先验证整数位长度
                	var len_msg =  FreeUtil.validateStrNumLength(str_itemname, _jsoValue.newValue, p_str, str_maxlen);
                	if (len_msg != "") {
                		$.messager.alert('提示', len_msg);
                	} else {
                		var newV = FreeUtil.validateStrNum(_jsoValue.newValue, p_str, m_str);
    					if (newV != "") {
    						JSPFree.setBillCardItemValue(_billCard, _itemkey, newV);
    					}
                	}
				}
			}
		}
	}
}

/**
 * 保存
 * 保存之前，先判断是否要进行单条数据检核操作
 * @return {[type]} [description]
 */
var saveFlag;
function onSave() {
	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeCommonBS", 
			"getSafeCheckProperties", {});
	var ischeck = jso_check.ischeck;
	
	if ("Y" == ischeck) {
		if(type == "Add" && !saveFlag) {
			var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(),str_ds,"8");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				JSPFree.setBillCardValues(d1_BillCard, {rid:FreeUtil.getUUIDFromServer(), report_type:report_type});
				saveFlag = JSPFree.doBillCardInsert(d1_BillCard, null);
				if (saveFlag) {
					JSPFree.closeDialog("CHECK_OK");
				}
			} else if (backValue == "Fail") {
				return;
			}
		} else {
			var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(),str_ds,"8");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);
				if (saveFlag) {
					JSPFree.closeDialog("CHECK_OK");
				}
			} else if (backValue == "Fail") {
				return;
			}
		}
	} else {
		if(type == "Add" && !saveFlag) {
			JSPFree.setBillCardValues(d1_BillCard, {rid:FreeUtil.getUUIDFromServer(), report_type:report_type});
			saveFlag = JSPFree.doBillCardInsert(d1_BillCard, null);
			if (saveFlag) {
				JSPFree.closeDialog("OK");
			}
		} else {
			saveFlag = JSPFree.doBillCardUpdate(d1_BillCard, null);
			if (saveFlag) {
				JSPFree.closeDialog("OK");
			}
		}
	}
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel() {
	JSPFree.closeDialog();
}