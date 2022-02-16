/**
 * 
 * <pre>
 * Title: 【报表处理】-【报表填报】
 * Description: 报表填报：填报页面-TAB填报（编辑）
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年6月22日
 */

var tabName = "";
var tabNameEn = "";
var dataDt = "";
var reportType = "";
var rid = "";
var strDs = "";

// 子表信息
var childFlags; // 子表标识，用来设置列表div id
var childTabs;  // 子表英文名
var childTabNms;// 子表中文名
var heightAlone = 100; // 每个模块框高度百分比
var orgNo = "";
var operType = "";

function AfterInit() {
	tabName = jso_OpenPars2.tabName;
	tabNameEn = jso_OpenPars2.tabNameEn;
	dataDt = jso_OpenPars2.dataDt;
	reportType = jso_OpenPars2.reportType;
	rid = jso_OpenPars2.json_data.rid;
    strDs = jso_OpenPars2.json_data.strDs;
    orgNo = jso_OpenPars2.json_data.org_no;
    operType = jso_OpenPars2.operType;


    // 获取子表信息
    getChildTabInfo(tabNameEn, reportType);
    // 如果不存在子表，则子表集合设置为空
    if(typeof(childFlags) == "undefined" || childFlags == "undefined") {
        childFlags = [];
        childTabs = [];
        childTabNms = [];
    }
    heightAlone = 100 / (childFlags.length + 1);
    if (operType == "view") {
        createSplitByBtn("d1","上下",heightAlone,["保存/onSave/icon-p21","取消/onCancel/icon-undo"],heightAlone +"%",childFlags);
    } else {
        createSplitByBtn("d1","上下",heightAlone,["取消/onCancel/icon-undo"],heightAlone +"%",childFlags);

    }


	var str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('" + tabName + "','" + tabNameEn + "','" + reportType + "','" + str_LoginUserOrgNo + "')";
	JSPFree.createBillCard("d1_A", str_className, null, null);
	
	JSPFree.execBillCardDefaultValueFormula(d1_A_BillCard);

    d1_A_BillCard.OldData = jso_OpenPars2.json_data;
	//赋值
	JSPFree.queryBillCardData(d1_A_BillCard, "rid = '"+rid+"'");



    // 展示子表数据
    for (var i=0;i<childFlags.length;i++) {
        str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTempletChild('" + childTabNms[i] + "','" + tabNameEn + "','" + reportType + "','" +childTabs[i]+"',"+rid+"')";
        var param = "('"+tabNameEn+"','"+rid+"','"+childTabs[i]+"','"+reportType+"','"+childFlags[i]+"','"+childTabNms[i]+"')";
        var strBtn;
        if (operType == "view") {
            strBtn =  childTabNms[i] + "/nullFunc;[icon-p81]查看/viewChild"+param+";";
        } else {
            strBtn =  childTabNms[i] + "/nullFunc;[icon-add]新增/insertChild"+param+";[icon-edit]编辑/updateChild"+param+";[icon-remove]删除/deleteChild"+param+";[icon-p81]查看/viewChild"+param+";";
        }
        JSPFree.createBillList("d1_"+childFlags[i], str_className, null, {
            list_btns:  strBtn,
            isSwitchQuery: "N",
            autoquery: "Y",
            ishavebillquery:"N"
        });
    }
}


// 修改子表数据
function updateChild(parentTabEn, parentRid, childTabEn, reportType, childFlag, childTabNm) {
    var childListObj = getChildListObj(childFlag);

    var json_rowdata = childListObj.datagrid('getSelected'); // 先得到数据

    if (json_rowdata == null || json_rowdata == undefined) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }
    var defaultVal = {type:"Edit",tabname:childTabNm,tabnameen:childTabEn,str_ds:strDs,report_type:reportType,data_dt:dataDt,org_no:orgNo,parentTabNmEn:parentTabEn,parentRid:parentRid};
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
    var defaultVal = {type:"View",tabname:childTabNm,tabnameen:childTabEn,str_ds:strDs,report_type:reportType};
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
    var defaultVal = {type:"Add",tabname:childTabNm,tabnameen:childTabEn,report_type:reportType,str_ds:strDs,parentTabNmEn:parentTabEn,parentRid:parentRid,data_dt:data_dt,org_no:org_no};
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
	// 设置表单中的索引不可进行编辑
	var jso_data = JSPFree.doClassMethodCall(
			"com.yusys.safe.base.common.service.SafeCommonBS",
			"getIndexesByTabName", {tab_name:tabName, report_type:reportType});
	var jso_col = jso_data.colName;
	
	if(jso_col != null && jso_col.length > 0) {
		for (var i=0;i<jso_col.length;i++) {
			JSPFree.setBillCardItemEditable(d1_A_BillCard, jso_col[i].toLowerCase(), false);
		}
	}

	JSPFree.setBillCardItemEditable(d1_A_BillCard,"data_dt",false);
	JSPFree.setBillCardItemEditable(d1_A_BillCard,"org_no",false);
	// 只要修改数据就给是否生成报文状态为0
	JSPFree.setBillCardItemValue(d1_A_BillCard, "bw_status", SafeFreeUtil.getBwStatus().NO_GENERATE);
}

/**
 * 保存
 * 保存之前，先判断是否要进行单条数据检核操作
 * @return {[type]} [description]
 */
var saveFlag = "";
function onSave() {
	// 是否进行单条数据检核操作
	var jso_check = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeCommonBS", 
			"getSafeCheckProperties", {});
	var ischeck = jso_check.ischeck;
	
	var str_ds = "";
	var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.tabmanage.service.SafeCrTabBS", "getDsNameByEnglishName", {tabNameEn: tabNameEn});
	
	if (jsn_result.msg == 'OK') {
		str_ds = jsn_result.ds_name;
	} else {
		$.messager.alert('提示', '当前表尚未配置数据源', 'warning');
	}

	// * 赋值数据源
    d1_A_BillCard.templetVO.templet_option.ds = str_ds;

	// 单条数据检核
	if ("Y" == ischeck) {
		var backValue = JSPFree.editTableCheckData(d1_A_BillCard, "Edit", tabName, tabNameEn.toUpperCase(), str_ds, "8");
		if (backValue == "" || "undifind" == backValue) {
			return;
		} else if (backValue == "OK") {
			saveFlag = JSPFree.doBillCardUpdate(d1_A_BillCard, null);
			if (saveFlag == true) {
				// 此处在更新数据表：下发状态值、校验状态值
				var dd = {rid:d1_A_BillCard.OldData.rid,tabNameEn:tabNameEn,xfstatus:SafeFreeUtil.getXfStatus().DOWN, datastatus:SafeFreeUtil.getCheckStatus().CHECK};
				var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS", "updateTabXfAndDataStatus", dd);
				
				if (jsn_result.msg == 'OK') {
					JSPFree.closeDialog("CHECK_OK");
				}
			}
		} else if (backValue == "Fail") {
			return;
		}
	} else {
		saveFlag = JSPFree.doBillCardUpdate(d1_A_BillCard, null);
		if (saveFlag == true) {
			// 此处在更新数据表：下发状态值、校验状态值
			var dd = {rid:d1_A_BillCard.OldData.rid,tabNameEn:tabNameEn,xfstatus:SafeFreeUtil.getXfStatus().DOWN, datastatus:SafeFreeUtil.getCheckStatus().CHECK};
			var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.fillingProcess.service.SafeFillingProcessBS", "updateTabXfAndDataStatus", dd);
			
			if (jsn_result.msg == 'OK') {
				JSPFree.closeDialog("OK");
			}
		}
	}
}

/**
 * 取消
 * @returns
 */
function onCancel() {
	JSPFree.closeDialog(null);
}


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