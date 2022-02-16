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
var isRptNo ;
var actiontype_is_editable = false;

var parentRid = "";
var typeFlag = "";
var parentTabNmEn = "";

var data_dt = "";
var org_no = "";
var queryParam = "";
var approval_status = "";
function AfterInit() {
	type = jso_OpenPars.type;
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	str_ds = jso_OpenPars.str_ds;
	report_type = jso_OpenPars.report_type;
	isRptNo = jso_OpenPars.isRptNo;
	approval_status = jso_OpenPars.approval_status;

    // 主表数据RID
    parentRid = jso_OpenPars.parentRid;
    // 字段l关联类型标识：0字表，1字典
    typeFlag = jso_OpenPars.typeFlag;
    parentTabNmEn = jso_OpenPars.parentTabNmEn;

    data_dt = jso_OpenPars.data_dt;
    org_no = jso_OpenPars.org_no;
    queryParam = "B.PARENT_TABNM_EN= '" +parentTabNmEn+"' AND B.PARENT_REPORT_TYPE LIKE '%" +report_type+ "%' AND B.PARENT_RID='" +parentRid+"'";

    var jso_listData = self.jso_OpenPars2; // 列表中传入的数据
	rid = jso_listData.rid;
	if(jso_OpenPars.actiontype_is_editable){
		actiontype_is_editable = jso_OpenPars.actiontype_is_editable;
	}
	template = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tabname + "','" + tabnameen + "','" + report_type + "','" + str_LoginUserOrgNo + "')";

    if (type == "Add") {
		JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
		setHidden("approval_status,approval_refuse_reason");
		JSPFree.execBillCardDefaultValueFormula(d1_BillCard);
	} else if (type == "Edit") {
		JSPFree.createBillCard("d1",template,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
		JSPFree.execBillCardDefaultValueFormula(d1_BillCard);
		setHidden("approval_status,approval_refuse_reason");
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
		JSPFree.setBillCardItemVisible(d1_BillCard, "taxfreecode", false);
		// 新建时，日期机构默认只读，默认主表数据
        JSPFree.setBillCardItemValue(d1_BillCard, "data_dt", data_dt);
        JSPFree.setBillCardItemValue(d1_BillCard, "org_no", org_no);
        JSPFree.setBillCardItemEditable(d1_BillCard, "data_dt", false);
        JSPFree.setBillCardItemEditable(d1_BillCard, "org_no", false);
        // --先注释，现场需求有变 miaokx
		/*if (isRptNo) {
			JSPFree.setBillCardItemEditable(d1_BillCard, "rptno", false);
			JSPFree.setBillCardItemValue(d1_BillCard, "rptno", isRptNo);
		}*/
	} else if (type == "Edit") {
        // 新建时，日期机构默认只读，默认主表数据
        JSPFree.setBillCardItemValue(d1_BillCard, "data_dt", data_dt);
        JSPFree.setBillCardItemValue(d1_BillCard, "org_no", org_no);
        JSPFree.setBillCardItemEditable(d1_BillCard, "data_dt", false);
        JSPFree.setBillCardItemEditable(d1_BillCard, "org_no", false);
		//JSPFree.setBillCardItemEditable(d1_BillCard, "rptno", false); //--先注释，现场需求有变 miaokx
		// 只要修改数据就给是否生成报文状态为0
		// JSPFree.setBillCardItemValue(d1_BillCard, "bw_status", SafeFreeUtil.getBwStatus().NO_GENERATE);
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
	
	if (_jsoValue.newValue != _jsoValue.oldValue) {
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
	JSPFree.setBillCardItemValue(d1_BillCard, "approval_status", approval_status);//修改审核状态
	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeCommonBS", 
			"getSafeCheckProperties", {});
	var ischeck = jso_check.ischeck;
	// ischeck = "N";
	if ("Y" == ischeck) {
		if(type == "Add" && !saveFlag) {
			var backValue = JSPFree.editTableCheckData(d1_BillCard, type, tabname, tabnameen.toUpperCase(),str_ds,"8");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				JSPFree.setBillCardValues(d1_BillCard, {rid:FreeUtil.getUUIDFromServer(), report_type:report_type});
				saveFlag = doBillCardInsertChild(d1_BillCard, null,parentTabNmEn, parentRid,report_type);
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
				// JSPFree.setBillCardItemValue(d1_BillCard, "approval_status", SafeFreeUtil.getApprovalStatus().UNSUBMITTED);//修改审核状态
                saveFlag = doBillCardUpdateChild(d1_BillCard, null,parentTabNmEn,parentRid);
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
			saveFlag = doBillCardInsertChild(d1_BillCard, null,parentTabNmEn, parentRid,report_type);
            if (saveFlag) {
                // JSPFree.queryDataByRid(d1_BillList, queryParam);
                JSPFree.closeDialog("OK");

            }
		} else {
            saveFlag = doBillCardUpdateChild(d1_BillCard, null,parentTabNmEn,parentRid);
            if (saveFlag) {
                // JSPFree.queryDataByRid(d1_BillList, queryParam);
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

/**
 * 隐藏 审核状态与审核不通过理由
 */
function setHidden(columns) {
	JSPFree.setBillCardItemVisible(d1_BillCard, columns, false);
}

// 新增字表数据 -- create by wm 2021-4-12 15:04:37
var doBillCardInsertChild = function(_billcard, _custValidate, parentTabNmEn, parentRid, reportType) {
    var dom_form = _billcard.form; // form对象
    var jso_templetVO = _billcard.templetVO; // 模板配置数据
    var str_ds = jso_templetVO.templet_option.ds; // 数据源
    var str_templetcode = jso_templetVO.templet_option.templetcode; // 模板编码
    // 取得表单数据
    var jso_formData = JSPFree.getBillCardFormValue(_billcard);

    // 空值校验
    var isValidateNullSucess = FreeUtil.validateNullBilldData(_billcard,
        jso_templetVO, jso_formData);
    if (!isValidateNullSucess) {
        return false;
    }

    // 宽度校验
    var isValidateLengthSucess = FreeUtil.validateLengthBilldData(
        _billcard, jso_templetVO, jso_formData);
    if (!isValidateLengthSucess) {
        return false;
    }

    // 格式校验
    var isValidateFormatSucess = FreeUtil.validateFormatBilldData(
        jso_templetVO, jso_formData);
    if (!isValidateFormatSucess) {
        return false;
    }

    // 唯一性校验
    var isValidateOnlyOneSucess = FreeUtil.validateOnlyOneBilldData(
        jso_templetVO, jso_formData, false);
    if (!isValidateOnlyOneSucess) {
        return false;
    }

    // 如果定义了自定义校验
    if (typeof _custValidate == "function") {
        return _custValidate(_billcard); //
    }

    // 远程调用,插入数据库
    execInsertBillCardDataChild(str_ds, str_templetcode, jso_formData, parentTabNmEn, parentRid, reportType);
    _billcard.OldData = jso_formData;
    _billcard.AlreadyInsert = true; // 记录是已新增
    _billcard.saveResult = "ok"; //

    return true;
};
// 新增子表数据 -- create by wm 2021-4-12 15:04:37
var execInsertBillCardDataChild = function (_ds, _templetcode, _formdata, parentTabNmEn, parentRid, reportType) {
    var jso_par = {ds: _ds, templetcode: _templetcode, formdata: _formdata, parentTabNmEn:parentTabNmEn, parentRid:parentRid,reportType:reportType};  //只有模板编码与表单数据
    var jso_rt = FreeUtil.doClassMethodCall("com.yusys.safe.business.service.SafeBusinessBS", "insertBillCardDataChild", jso_par);  //保存数据
};

// 保存表单数据 -子表修改，需要同时修改主表数据状态 create by wm 2021-5-7 19:32:15
var doBillCardUpdateChild = function(_billcard, _custValidate,parentTabNmEn,parentRid) {
    var dom_form = _billcard.form; // form对象
    var jso_templetVO = _billcard.templetVO; // 模板配置数据
    var str_ds = jso_templetVO.templet_option.ds; // 数据源
    var str_templetcode = jso_templetVO.templet_option.templetcode; // 模板编码
    var jso_oldData = _billcard.OldData; // 旧数据
    var str_sqlWhere = JSPFree.getSQLWhereByPK(jso_templetVO, jso_oldData); // 一定要根据旧数据拼出where条件,因为主键本身也可能修改!
    var jso_formData = JSPFree.getBillCardFormValue(_billcard); // 取得新数据

    // 空值校验
    var isValidateNullSucess = FreeUtil.validateNullBilldData(_billcard,
        jso_templetVO, jso_formData);
    if (!isValidateNullSucess) {
        return false;
    }

    // 宽度校验
    var isValidateLengthSucess = FreeUtil.validateLengthBilldData(
        _billcard, jso_templetVO, jso_formData);
    if (!isValidateLengthSucess) {
        return false;
    }

    // 格式校验
    var isValidateFormatSucess = FreeUtil.validateFormatBilldData(
        jso_templetVO, jso_formData);
    if (!isValidateFormatSucess) {
        return false;
    }

    // 唯一性校验
    var isValidateOnlyOneSucess = FreeUtil.validateOnlyOneBilldData(
        jso_templetVO, jso_formData, true);
    if (!isValidateOnlyOneSucess) {
        return false;
    }

    // 如果定义了自定义校验
    if (typeof _custValidate == "function") {
        return _custValidate(_billcard); //
    }

    // 远程调用,保存数据库
    execUpdateBillCardDataChild(str_ds, str_templetcode, jso_formData,
        str_sqlWhere,parentTabNmEn,parentRid);

    // 如果保存成功,则把新数据更新到旧数据上面去!这样再次修改时才会成功!因为DB中的值已经变化了,所以旧的where条件是没用了
    _billcard.OldData = jso_formData;
    _billcard.saveResult = "ok";
    return true;
};
// 修改子表数据
var execUpdateBillCardDataChild = function (_ds, _templetcode, _formdata, _sqlwhere,parentTabNmEn,parentRid) {
    var jso_par = {ds: _ds, templetcode: _templetcode, formdata: _formdata, SQLWhere: _sqlwhere,parentTabNmEn:parentTabNmEn,parentRid:parentRid,reportType:report_type};  //
    var jso_rt = FreeUtil.doClassMethodCall("com.yusys.safe.business.service.SafeBusinessBS", "updateBillCardDataChild", jso_par);  //保存数据
};

