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

var data_dt;
var org_no;

// 子表信息
var childFlags; // 子表标识，用来设置列表div id
var childTabs;  // 子表英文名
var childTabNms;// 子表中文名
var heightAlone = 100; // 每个模块框高度百分比

// 主表rid
var uuid;
function AfterInit() {
	type = jso_OpenPars.type;
	tabname = jso_OpenPars.tabname;
	tabnameen = jso_OpenPars.tabnameen;
	str_ds = jso_OpenPars.str_ds;
	report_type = jso_OpenPars.report_type;
	isRptNo = jso_OpenPars.isRptNo;
	var jso_listData = self.jso_OpenPars2; // 列表中传入的数据
	rid = jso_listData.rid;
    data_dt = jso_listData.data_dt;
    org_no = jso_listData.org_no;

    if(jso_OpenPars.actiontype_is_editable){
		actiontype_is_editable = jso_OpenPars.actiontype_is_editable;
	}
	// 获取子表信息
    getChildTabInfo(tabnameen, report_type);
	//var childFlags = ["B","C", "D"];
	//var childTabs = ["SAFE_CFA_CHILD_SYR", "SAFE_CFA_CHILD_BDBR", "SAFE_CFA_CHILD_FDBR"];
    //var childTabNms = ["受益人信息", "被担保人信息", "反担保人信息"];

    // 如果不存在子表，则子表集合设置为空
    if(typeof(childFlags) == "undefined" || childFlags == "undefined") {
        childFlags = [];
        childTabs = [];
        childTabNms = [];
    }
    heightAlone = 100 / (childFlags.length + 1);


    // 主表列表
    template = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tabname + "','" + tabnameen + "','" + report_type + "','" + str_LoginUserOrgNo + "')";

	if (type == "Add") {
        // 页面上下分隔
        createSplitByBtn("d1","上下",heightAlone,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],heightAlone +"%",childFlags);
		// 如果为新增，则进入页面先生成UUID，因为生成子表的时候有用到
        uuid = FreeUtil.getUUIDFromServer();
        rid = uuid;
        JSPFree.createBillCard("d1_A",template,null);
		setHidden("approval_status,approval_refuse_reason");
		JSPFree.execBillCardDefaultValueFormula(d1_A_BillCard);
        // 展示子表数据
        for (var i=0;i<childFlags.length;i++) {
            str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTempletChild('" + childTabNms[i] + "','" + tabnameen + "','" + report_type + "','" +childTabs[i]+"',"+rid+"')";
            var param = "('"+tabnameen+"','"+rid+"','"+childTabs[i]+"','"+report_type+"','"+childFlags[i]+"','"+childTabNms[i]+"')";
            JSPFree.createBillList("d1_"+childFlags[i], str_className, null, {
                list_btns:  childTabNms[i] + "/nullFunc;[icon-add]新增/insertChild"+param+";[icon-edit]编辑/updateChild"+param+";[icon-remove]删除/deleteChild"+param+";[icon-p81]查看/viewChild"+param+";",
                isSwitchQuery: "N",
                autoquery: "Y",
                ishavebillquery:"N"
            });
        }
	} else if (type == "Edit") {
        // 页面上下分隔
        createSplitByBtn("d1","上下",heightAlone,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],heightAlone +"%",childFlags);
		JSPFree.createBillCard("d1_A",template,null);
		JSPFree.execBillCardDefaultValueFormula(d1_A_BillCard);
		setHidden("approval_status,approval_refuse_reason");
		d1_A_BillCard.OldData = jso_listData;
		// 赋值
		JSPFree.queryBillCardData(d1_A_BillCard, "rid = '"+rid+"'");
        // 展示子表数据
        for (var i=0;i<childFlags.length;i++) {
            str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTempletChild('" + childTabNms[i] + "','" + tabnameen + "','" + report_type + "','" +childTabs[i]+"',"+rid+"')";
            var param = "('"+tabnameen+"','"+rid+"','"+childTabs[i]+"','"+report_type+"','"+childFlags[i]+"','"+childTabNms[i]+"')";
            JSPFree.createBillList("d1_"+childFlags[i], str_className, null, {
                list_btns:  childTabNms[i] + "/nullFunc;[icon-add]新增/insertChild"+param+";[icon-edit]编辑/updateChild"+param+";[icon-remove]删除/deleteChild"+param+";[icon-p81]查看/viewChild"+param+";",
                isSwitchQuery: "N",
                autoquery: "Y",
                ishavebillquery:"N"
            });
        }
	} else if (type == "View") {
        // 页面上下分隔
        createSplitByBtn("d1","上下",heightAlone,["取消/onCancel/icon-undo"],heightAlone +"%",childFlags);
		JSPFree.createBillCard("d1_A",template,null);
		JSPFree.execBillCardDefaultValueFormula(d1_A_BillCard);
		
		d1_A_BillCard.OldData = jso_listData;
		// 赋值
		JSPFree.queryBillCardData(d1_A_BillCard, "rid = '"+rid+"'");
        // 展示子表数据
        for (var i=0;i<childFlags.length;i++) {
            str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTempletChild('" + childTabNms[i] + "','" + tabnameen + "','" + report_type + "','" +childTabs[i]+"',"+rid+"')";
            var param = "('"+tabnameen+"','"+rid+"','"+childTabs[i]+"','"+report_type+"','"+childFlags[i]+"','"+childTabNms[i]+"')";
            JSPFree.createBillList("d1_"+childFlags[i], str_className, null, {
                list_btns:  childTabNms[i] + "/nullFunc;[icon-p81]查看/viewChild"+param+";",
                isSwitchQuery: "N",
                autoquery: "Y",
                ishavebillquery:"N"
            });
        }
	}


}

// 根据父表查询所有字表，返回所有子表信息
var getChildTabInfo = function (tab_name_en, report_type) {
    var childTabBtn = '';
    var str;
    $.ajax({
        cache: false,
        async: false,
        url: v_context + "/frs/safe/rptDataMgr/getChildTabInfo",
        type: "get",
        data: {
            tabNameEn: tab_name_en,
            reportType: report_type
        },
        success: function (result) {
            childFlags = result.childFlags;
            childTabs = result.childTabs;
            childTabNms = result.childTabNms;
        }
    });
    return childTabBtn;
};

// 修改子表数据
function updateChild(parentTabEn, parentRid, childTabEn, reportType, childFlag, childTabNm) {
	var childListObj = getChildListObj(childFlag);

    var json_rowdata = childListObj.datagrid('getSelected'); // 先得到数据

    if (json_rowdata == null || json_rowdata == undefined) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }
    var defaultVal = {
        type: "Edit",
        tabname: childTabNm,
        tabnameen: childTabEn,
        str_ds: str_ds,
        report_type: reportType,
        data_dt: data_dt,
        org_no: org_no,
        parentTabNmEn: parentTabEn,
        parentRid: parentRid,
        approval_status: JSPFree.getBillCardFormValue(d1_A_BillCard).approval_status
    };
    defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数

    JSPFree.openDialog("编辑","/yujs/safe/busidata/safe_check_childdata_edit.js",1100,560,defaultVal,function(_rtdata) {
        if (_rtdata == "CHECK_OK") {
            JSPFree.alert("校验并保存成功!");
            JSPFree.refreshBillListCurrRow(childListObj); // 刷新当前行
        } else if (_rtdata == "OK") {
            JSPFree.alert("保存成功!");
            JSPFree.refreshBillListCurrRow(childListObj); // 刷新当前行
        }
    },true);
}

// 删除子表数据
function deleteChild(parentTabEn, parentRid, childTabEn, reportType, childFlag, childTabNm){
    var childListObj = getChildListObj(childFlag);
    var json_rowdata = childListObj.datagrid('getSelected');
    if (json_rowdata == null || json_rowdata==undefined) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    doBillListDeleteChild(childListObj, parentTabEn, parentRid, reportType);
}
// 查看子表数据
function viewChild(parentTabEn, parentRid, childTabEn, reportType, childFlag, childTabNm) {
	// 根据子表标识获取子表列表对象
    var childListObj = getChildListObj(childFlag);
    var json_rowdata = childListObj.datagrid('getSelected'); // 先得到数据
    if (json_rowdata == null || json_rowdata == undefined) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }
    var defaultVal = {type:"View",tabname:childTabNm,tabnameen:childTabEn,str_ds:str_ds,report_type:reportType};
    defaultVal["_BillCardData"] = json_rowdata; // 列表数据,这样设置就是从前端取数

    JSPFree.openDialog3("查看","/yujs/safe/busidata/safe_check_childdata_edit.js",null,null,defaultVal,function(_rtdata) {

    },true);
}


// 新增子表数据
function insertChild(parentTabEn, parentRid, childTabEn, reportType, childFlag, childTabNm) {
    var childListObj = getChildListObj(childFlag);
    var data_dt = JSPFree.getBillCardFormValue(d1_A_BillCard).data_dt;
    var org_no = JSPFree.getBillCardFormValue(d1_A_BillCard).org_no;
    if (data_dt == "") {
        JSPFree.alert("请先输入主表[数据日期]!");
        return ;
	}
    if (org_no == "") {
        JSPFree.alert("请先输入主表[机构编号]!");
        return ;
    }
    var defaultVal = {
        type: "Add",
        tabname: childTabNm,
        tabnameen: childTabEn,
        report_type: reportType,
        str_ds: str_ds,
        parentTabNmEn: parentTabEn,
        parentRid: parentRid,
        data_dt: data_dt,
        org_no: org_no,
        approval_status: JSPFree.getBillCardFormValue(d1_A_BillCard).approval_status
    };
    JSPFree.openDialog("新增","/yujs/safe/busidata/safe_check_childdata_edit.js",1100,560,defaultVal,function(_rtdata){
        if (_rtdata == "CHECK_OK") {
            JSPFree.alert("校验并保存成功!");
        } else if (_rtdata == "OK") {
            JSPFree.alert("保存成功!");
        }
        var queryParam = "B.PARENT_TABNM_EN= '" +parentTabEn+"' AND B.PARENT_REPORT_TYPE LIKE '%" +reportType+ "%' AND B.PARENT_RID='" +parentRid+"'";
        queryDataByRid(childListObj, queryParam);
    },true);
}


// 获取子表列表对象
function getChildListObj(childFlag) {
	if(childFlag == "B"){
		return d1_B_BillList;
	} else if (childFlag == "C") {
		return d1_C_BillList;
	} else if (childFlag == "D") {
		return d1_D_BillList
	} else if (childFlag == "E") {
        return d1_E_BillList
    } else if (childFlag == "F") {
        return d1_F_BillList
    }
	return null;
}
/**
 * 页面初始化后
 * @returns
 */
function AfterBodyLoad() {
	var str_fnName = "onBillCardItemEdit"; // 在创建表单时会创建这个函数
    if (typeof self[str_fnName] == "function") {  // 如果的确定义了这个函数
        FreeUtil.addBillCardItemEditListener(d1_A_BillCard, self[str_fnName]);  // 增加监听事件
    }
    var typeColumnName = "actiontype";
	if ("CRD" === report_type || "CRX" == report_type) {
		typeColumnName = "oper_type_code";
	}
	// 如果是查看页面，要进行置灰，不可编辑
	if (type == "View") {
		JSPFree.setBillCardItemEditable(d1_A_BillCard, "*", false);
	} else if (type == "Add") {
		JSPFree.setBillCardItemVisible(d1_A_BillCard, "taxfreecode", false);
		// 新建时，操作类型只读，默认新建
		JSPFree.setBillCardItemValue(d1_A_BillCard, typeColumnName, "A");
		JSPFree.setBillCardItemEditable(d1_A_BillCard, typeColumnName, false);
        JSPFree.setBillCardItemValue(d1_A_BillCard, "approval_status", SafeFreeUtil.getApprovalStatus().UNSUBMITTED);//修改审核状态
		// --先注释，现场需求有变 miaokx
		/*if (isRptNo) {
			JSPFree.setBillCardItemEditable(d1_A_BillCard, "rptno", false);
			JSPFree.setBillCardItemValue(d1_A_BillCard, "rptno", isRptNo);
		}*/
	} else if (type == "Edit") {
		var jso_istaxfree = JSPFree.getBillCardFormValue(d1_A_BillCard).istaxfree;
		if (jso_istaxfree == null || jso_istaxfree == "" || jso_istaxfree == "N") {
			JSPFree.setBillCardItemVisible(d1_A_BillCard, "taxfreecode", false);
		}
		JSPFree.setBillCardItemEditable(d1_A_BillCard, typeColumnName, actiontype_is_editable);
		// 操作类型可编辑时，过滤掉新建值
		if (actiontype_is_editable) {
			var actiontype = JSPFree.getBillCardItemValue(d1_A_BillCard, "actiontype");
			if (actiontype == 'A') {
				JSPFree.setBillCardItemValue(d1_A_BillCard,"actiontype", "");
			}
			JSPFree.setBillCardComboBoxData2(d1_A_BillCard, typeColumnName,
				[{id: "C", name: "修改"}, {
				id: "D",
				name: "删除"
			}, {id: "R", name: "申报无误"}]);
		}
		//JSPFree.setBillCardItemEditable(d1_A_BillCard, "rptno", false); //--先注释，现场需求有变 miaokx
		// 只要修改数据就给是否生成报文状态为0
		JSPFree.setBillCardItemValue(d1_A_BillCard, "bw_status", SafeFreeUtil.getBwStatus().NO_GENERATE);
	}

    FreeUtil.setBillCardLabelHelptip(d1_A_BillCard); // 必须写在最后一行
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
    // 检测子表数量，如果子表为必填，且子表数量为0，则提示子表必须存在数据
    var jsonParm = {parentTabEn:tabnameen, parentRid: rid, reportType: report_type};
    if("SAFE_CFA_BA" === tabnameen){
        jsonParm.cgyn = JSPFree.getBillCardItemValue(d1_A_BillCard, "CGYN");
    }
    var jso_childcheck = JSPFree.doClassMethodCall("com.yusys.safe.business.service.SafeBusinessBS", "checkChildTabNum", jsonParm);
    if (jso_childcheck.flag == "fail") {
        $.messager.alert('提示', jso_childcheck.failMsg);
        return ;
    }

	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeCommonBS", 
			"getSafeCheckProperties", {});
	var ischeck = jso_check.ischeck;
	if ("Y" == ischeck) {
		if(type == "Add" && !saveFlag) {
			var backValue = JSPFree.editTableCheckData(d1_A_BillCard, type, tabname, tabnameen.toUpperCase(),str_ds,"8");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				JSPFree.setBillCardValues(d1_A_BillCard, {rid:uuid, report_type:report_type});
				saveFlag = JSPFree.doBillCardInsert(d1_A_BillCard, null);
				if (saveFlag) {
					JSPFree.closeDialog("CHECK_OK");
				}
			} else if (backValue == "Fail") {
				return;
			}
		} else {
			var backValue = JSPFree.editTableCheckData(d1_A_BillCard, type, tabname, tabnameen.toUpperCase(),str_ds,"8");
			if (backValue == "" || "undifind" == backValue) {
				return;
			} else if (backValue == "OK") {
				JSPFree.setBillCardItemValue(d1_A_BillCard, "approval_status", SafeFreeUtil.getApprovalStatus().UNSUBMITTED);//修改审核状态
				saveFlag = JSPFree.doBillCardUpdate(d1_A_BillCard, null);
				if (saveFlag) {
					JSPFree.closeDialog("CHECK_OK");
				}
			} else if (backValue == "Fail") {
				return;
			}
		}
	} else {
		if(type == "Add" && !saveFlag) {
			JSPFree.setBillCardValues(d1_A_BillCard, {rid:uuid, report_type:report_type});
			saveFlag = JSPFree.doBillCardInsert(d1_A_BillCard, null);
			if (saveFlag) {
				JSPFree.closeDialog("OK");
			}
		} else {
			JSPFree.setBillCardItemValue(d1_A_BillCard, "approval_status", SafeFreeUtil.getApprovalStatus().UNSUBMITTED);//修改审核状态
			saveFlag = JSPFree.doBillCardUpdate(d1_A_BillCard, null);
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
    //如果是add,则取消时删除脏数据
    if ("Add" == type) {
        var _par = {
            tabNameEn:tabnameen,
            rid:rid,
            reportType:report_type
        }
        JSPFree.doClassMethodCall("com.yusys.safe.business.service.SafeBusinessBS","deleteDirtyData",_par);
    }
	JSPFree.closeDialog();
}

/**
 * 隐藏 审核状态与审核不通过理由
 */
function setHidden(columns) {
	JSPFree.setBillCardItemVisible(d1_A_BillCard, columns, false);
}

// createSplitByBtn("d1","上下",heightAlone,["确定/onConfirm","取消/onCancel"],"undefined",childNUm,childFlags);

// 创建分割器
var createSplitByBtn = function(_divid, _type, _location, _btns,
                                    areaNum, areaFlags, _isCanDrag) {
    var isCanDrag = true;
    if (typeof _isCanDrag != "undefined") {
        isCanDrag = _isCanDrag;
    }

    if (_type == "左右") {
        FreeUtil.createSplit_X_ByBtn(_divid, _location, _btns, isCanDrag); // 水平分割
    } else if (_type == "上下") {
        createSplit_Y_ByBtn(_divid, _location, _btns, isCanDrag,areaNum, areaFlags); // 上下分割
    } else {
        console.log("错误的类型【" + _type + "】");
    }
};


//创建分割器
var createSplit_Y_ByBtn = function (_divid, _location, _btns, _isCanDrag,areaNum, areaFlags) {
    var dom_root = document.getElementById(_divid);  //
    var str_html = "";
    str_html = str_html + "<div class=\"easyui-layout\" data-options=\"border:false\" style=\"width:100%;height:100%;overflow:true\">\r\n";
    str_html = str_html + "<div data-options=\"region:'south',border:false\" style=\"height:45px;text-align:center;padding-top:5px;overflow:true;\">\r\n";

    //拼出各个按钮
    if (typeof _btns != "undefined") {
        for (var i = 0; i < _btns.length; i++) {
            var btn_defs = _btns[i].split("/");
            var str_btnText = btn_defs[0];
            var str_btnAction = "";
            var str_btnImg = "";
            if (btn_defs.length >= 2) {
                str_btnAction = btn_defs[1];
            }
            if (btn_defs.length >= 3) {
                str_btnImg = btn_defs[2];
            }
            str_html = str_html + "<a id=\"" + _divid + "_SplitBtn" + (i + 1) + "\"   href=\"JavaScript:" + str_btnAction + "();\" class=\"easyui-linkbutton\" ";
            if (str_btnImg != "") {
                str_html = str_html + "data-options=\"iconCls:'" + str_btnImg + "'\" ";
            }
            str_html = str_html + "style=\"width:80px\">" + str_btnText + "</a>\r\n";
        }
    }
    str_html = str_html + "</div>\r\n";

    //原来的分割器,放在中间
    str_html = str_html + "<div data-options=\"region:'center',border:false\">\r\n"
    str_html = str_html + "  <div class=\"easyui-layout\" style=\"width:100%;height:100%;\">\r\n";
    str_html = str_html + "    <div id=\"" + _divid + "_A\" data-options=\"split:" + _isCanDrag + ",border:false\" style=\"height:" + areaNum + "\"></div>\r\n";
    for (var i=0;i<areaFlags.length;i++) {
        str_html = str_html + "  <div id=\"" + _divid + "_"+areaFlags[i]+"\" data-options=\"split:" + _isCanDrag + ",border:false\" style=\"height:" + areaNum + "\"></div>\r\n";
    }
    str_html = str_html + "  </div>\r\n";
    str_html = str_html + "</div>\r\n";
    str_html = str_html + "</div>\r\n";
    dom_root.innerHTML = str_html;
};

// 子表中删除数据 create by wm 2021-4-12 16:11:28
var doBillListDeleteChild = function(_grid, parentTabNmEn, parentRid, reportType) {
    var json_rowdata = _grid.datagrid('getSelected'); // 先得到数据
    if (json_rowdata == null) {
        $.messager.alert('提示', '必须选择一条数据!', 'info');
        return;
    }

    var str_divid = _grid.divid; //
    var str_beforeDeleteFn = "beforeDelete_" + str_divid + "_BillList"; // 删除前校验
    if (typeof self[str_beforeDeleteFn] == "function") { // 如果有这个函数
        var isOK = self[str_beforeDeleteFn](_grid, json_rowdata); // 执行
        if (!isOK) {
            return; // 如果失败则返回
        }
    }

    // 警告提醒是否真的删除?
    $.messager.confirm('提示', '你真的要删除选中的记录吗?', function(_isConfirm) {
        if (!_isConfirm) {
            return;
        }

        var str_rownumValue = json_rowdata['_rownum']; // 取得行号数据
        var int_selrow = _grid.datagrid("getRowIndex", str_rownumValue); // 根据_rownum的值,计算出是第几行,后面刷新就是这一行!

        var jso_templetVO = _grid.templetVO; // 模板对象
        var str_ds = jso_templetVO.templet_option.ds; // 数据源
        var str_templetcode = jso_templetVO.templet_option.templetcode; // 模板编码
        var str_savetable = jso_templetVO.templet_option.savetable; // 保存的表名,删除时不要重新查模板了,直接送表名,性能高一点!
        var str_sqlwhere = FreeUtil.getSQLWhereByPK(jso_templetVO,
            json_rowdata); // 拼出SQL
        // 远程调用,真正删除数据库
        try {
            execDeleteBillListdDataChild(str_ds, str_templetcode,
                str_savetable, str_sqlwhere, parentTabNmEn, parentRid, reportType, json_rowdata.rid);

            // 从界面上删除行
            _grid.datagrid('deleteRow', int_selrow);
            $.messager.alert('提示', '删除数据成功!', 'info');
        } catch (_ex) {
            console.log(_ex);
            FreeUtil.openHtmlMsgBox("发生异常", 500, 250, _ex)
        }
    });
};


// 子表中删除数据 create by wm 2021-4-12 16:11:28
var execDeleteBillListdDataChild = function (_ds, _templetcode, _table, _sqlwhere, parentTabNmEn, parentRid, reportType, childRid) {
    var jso_par = {ds: _ds, templetcode: _templetcode, savetable: _table, SQLWhere: _sqlwhere,parentTabNmEn:parentTabNmEn, parentRid:parentRid, reportType: reportType,childRid: childRid };  //
    var jso_rt = FreeUtil.doClassMethodCall("com.yusys.safe.business.service.SafeBusinessBS", "deleteBillListDataChild", jso_par);  //保存数据
};

// 根据数据关联表查询子表数据 -- create by wm 2021-4-12 10:39:43
var queryDataByRid= function(_grid, _cons) {
    queryDataByRidReal(_grid, _cons, false, 1, null, true);
};

// 根据数据关联表查询字表数据 -- create by wm 2021-4-12 10:39:43
var queryDataByRidReal = function (_grid, _cons, _isYiBu, _currPage, _orderBy, _isSpanXmlCons) {
    var str_templetcode = _grid.templetVO.templet_option.templetcode;  //取得模板编码
    var str_table = _grid.templetVO.templet_option.fromtable;  //取得模板编码
    var isPager = true;  //默认是分页
    if ("N" == _grid.templetVO.templet_option.list_ispagebar) {
        isPager = false;  //不分页
    }

    //先把表名拼出来
    var str_sql = "select A.* from " + str_table + " A LEFT JOIN SAFE_CR_DATA_RELA B ON A.RID = B.CHILD_RID WHERE 1=1 ";

    //如果列表在前端设置了强制条件则加上,即有时需要在前端再加上强制条件
    if (typeof _grid.forceSQLWhere != "undefined" && _grid.forceSQLWhere != null && _grid.forceSQLWhere != "") {
        str_sql = str_sql + " and " + _grid.forceSQLWhere;  //
    }

    //拼接条件
    if (_cons != undefined && _cons != null && _cons != "") {
        str_sql = str_sql + " and " + _cons;  //
    }

    //拼接模板中配置的条件
    //xml模板中定义的查询条件,如果有,则与传入的条件再合并!
    // if (typeof _isSpanXmlCons != "undefined" && _isSpanXmlCons) {
    //     var str_xmlCons = _grid.templetVO.templet_option.querycontion;
    //     if (typeof str_xmlCons != "undefined" && str_xmlCons != null && str_xmlCons.trim() != "") {
    //         str_sql = str_sql + " and " + str_xmlCons;  //
    //     }
    // }

    //处理排序,如果入参指定了排序,则使用入参的,否则使用模板中定义的
    if (typeof _orderBy != "undefined" && _orderBy != null && _orderBy != "") {
        str_sql = str_sql + " order by " + _orderBy;
    } else {
        var str_orderby = _grid.templetVO.templet_option.orderbys;
        if (typeof str_orderby != "undefined" && str_orderby != null && str_orderby != "") {
            str_sql = str_sql + " order by " + str_orderby;
        }
    }

    FreeUtil.queryDataByConditonRealBySQL(_grid, str_sql, _isYiBu, isPager, _currPage);
};

// 按钮区表名必须存在一个函数，定义为null
function nullFunc() {

}