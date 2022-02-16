/**
 * 构建灵活查询
 *
 * @type {null}
 */
var jso_queryTempletVO = null;  //实际表的模板VO
var ary_newQueryItems = [];  //当前选中的
var tab_name = "";
var tab_name_en = "";
var queryFormValue = {};

function AfterInit() {
    JSPFree.createSplitByBtn("d1", "左右", 450, ["确定/onConfirm/icon-ok", "重置/onReset/icon-p39", "取消/onCancel/icon-no"]);
    JSPFree.createTabb("d1_B", ["指标", "字段"], 200);
    tab_name = jso_OpenPars.tab_name;
    tab_name_en = jso_OpenPars.tab_name_en;
    queryFormValue = jso_OpenPars.queryFormValue;
    //取得模板编码
    var str_templetCode = jso_OpenPars.templetcode;
    jso_queryTempletVO = JSPFree.getTempletVO(str_templetCode);
    
    //创建列表--所有列
    JSPFree.createBillList("d1_B_1", FreeUtil.CommXMLPath + "/TempletItems.xml");
    
    //创建列表-排序列
    JSPFree.createBillList("d1_B_2", FreeUtil.CommXMLPath + "/TempletItems.xml");
    
    //从前端模板解析出所有字段,并加入右边表格中
    // 获取可以作为自定义灵活查询的字段
    var resultTemplateVo = JSPFree.doClassMethodCall("com.yusys.bfd.checktab.service.BfdModelTempletBuilderForContinuity", "getFreeQueryColumns",
        {tab_name_en: jso_queryTempletVO.templet_option.savetable});
    var jsy_itemVOs = resultTemplateVo.templateVo;
    var array_AllItems_index = [];
    var array_AllItems_col = [];
    for (var i = 0; i < jsy_itemVOs.length; i++) {
        var str_isQuery = "";
        if ("Y" == jsy_itemVOs[i].query_isshow || "Y" == jsy_itemVOs[i].query2_isshow || "Y" == jsy_itemVOs[i].query3_isshow) {
            str_isQuery = "Y";
        } else {
            str_isQuery = "N";
        }
        
        if ("Y" == jsy_itemVOs[i].list_isshow) {  //有人提出必须是列表显示的才参与
            if ("按钮" == jsy_itemVOs[i].itemtype || "N" == jsy_itemVOs[i].issave) {  //按钮不参与查询,不参与保存的也不查询,因为可能是加载公式带来的
                
            } else {
                var itemKey = jsy_itemVOs[i].itemkey;
                var itemName = jsy_itemVOs[i].itemname;
                var rid = jsy_itemVOs[i].rid;
                if ("1" === jsy_itemVOs[i].col_type) {
                    array_AllItems_index.push({
                        itemkey: itemKey,
                        itemname: itemName,
                        isquery: str_isQuery,
                        rid: rid
                    });
                } else {
                    array_AllItems_col.push({
                        itemkey: itemKey,
                        itemname: itemName,
                        isquery: str_isQuery,
                        rid: rid
                    });
                }
            }
        }
    }
    
    // 指标
    JSPFree.setBillListDatas(d1_B_1_BillList, array_AllItems_index);
    // 字段
    JSPFree.setBillListDatas(d1_B_2_BillList, array_AllItems_col);
    
    //再取出所有字段,然后渲染左边的查询条件
    doRefreshQueryItems(true, d1_B_1_BillList);
    
    
}

function AfterBodyLoad() {
    FreeUtil.loadBillQueryData(d1_A_BillQuery, queryFormValue);
}

//从模板子表VO[]中找到某一项
function findTempletItemVOByKey(_itemVOs, _itemkey) {
    for (var i = 0; i < _itemVOs.length; i++) {
        if (_itemVOs[i].itemkey == _itemkey) {
            return _itemVOs[i];
        }
    }
    return null;
}

//当选择了一个新的列时,立即刷新左边的查询条件
function onChooseOneColumn(_billList, _index, _field, _newValue) {
    doRefreshQueryItems(true, _billList);
}

//刷新
function doRefreshQueryItems(_isFirst, _billList) {
    // 指标
    var jsy_datas_index = JSPFree.getBillListAllDatas(d1_B_1_BillList);
    // 字段
    var jsy_datas_column = JSPFree.getBillListAllDatas(d1_B_2_BillList);
    ary_newQueryItems = [];
    for (var i = 0; i < jsy_datas_index.length; i++) {
        if ("Y" == jsy_datas_index[i].isquery) {
            ary_newQueryItems.push(jsy_datas_index[i].itemkey);
        }
    }
    for (var i = 0; i < jsy_datas_column.length; i++) {
        if ("Y" == jsy_datas_column[i].isquery) {
            ary_newQueryItems.push(jsy_datas_column[i].itemkey);
        }
    }
    var str_templetcode = jso_OpenPars.templetcode; //模板编码
    
    var jso_par = {
        webcontext: v_context,
        divid: "d1_A",
        templetCode: str_templetcode,
        checkitems: ary_newQueryItems,
        isFirst: true
    };
    var jso_data = FreeUtil.doClassMethodCall(FreeUtil.JSPBuilderClassName, "getBillQueryFreeBuildHtml", jso_par);
    var str_html = jso_data.html;  //只生成html
    var str_jstext = jso_data.jstext;  //只生成html
    
    //再加入实际内容
    var dom_div = document.getElementById("d1_A");
    dom_div.innerHTML = str_html;  //重新输入Html
    
    //再搞JavaScript代码
    if (str_jstext != null && str_jstext != "") {
        var newJSObj = document.createElement("script");  //一定要用这个创建,否则浏览器不解析
        newJSObj.id = "d1_A";
        newJSObj.type = "text/javascript";  //一定设置类型
        newJSObj.text = str_jstext;  //直接设置文本
        document.body.appendChild(newJSObj);  //在</body>前加上
    }
    $.parser.parse('#d1_A');
    FreeUtil.loadBillQueryData(d1_A_BillQuery, queryFormValue);
}

//取得所有VOs
function getAllItemVOsByKey(_itemArray) {
    var jsy_itemVOs = jso_queryTempletVO.templet_option_b;
    var ary_rt = [];
    for (var i = 0; i < _itemArray.length; i++) {
        var itemVO = null;  //
        for (var j = 0; j < jsy_itemVOs.length; j++) {
            if (jsy_itemVOs[j].itemkey == _itemArray[i]) {
                itemVO = jsy_itemVOs[j];  //
                break;
            }
        }
        
        if (itemVO != null) {
            ary_rt.push(itemVO);
        }
    }
    return ary_rt;
}


//确定
function onConfirm() {
    //拼SQL的where部分
    var jsy_itemVOs = getAllItemVOsByKey(ary_newQueryItems);
    
    var dom_form = document.getElementById("d1_A_QueryForm");
    var str_sql = JSPFree.getQueryFormSQLConsByItems(dom_form, jsy_itemVOs);
    if (str_sql == null) {
        return;
    }
    //xml模板中定义的查询条件,如果有,则与传入的条件再合并!
    var str_xmlCons = jso_queryTempletVO.templet_option.querycontion;
    if (typeof str_xmlCons != "undefined" && str_xmlCons != null && str_xmlCons.trim() != "") {
        if (str_sql == "") {
            str_sql = str_sql + str_xmlCons;  //
        } else {
            str_sql = str_sql + " and " + str_xmlCons + " "
        }
    }
    // 获取查询字段的值
    var callBackQueryItemValue = {};
    for (var i = 0; i < jsy_itemVOs.length; i++) {
        var str_sqlitem = FreeUtil.getFormItemValue(dom_form, jsy_itemVOs[i]);
        callBackQueryItemValue[jsy_itemVOs[i].itemkey] = str_sqlitem;
    }
    
    var str_OrderBySQL = "";
    // 将选中的灵活查询字段设置为默认查询字段
    var result = JSPFree.doClassMethodCall("com.yusys.bfd.checktab.service.CheckTableServiceBS", "saveSelectedFreeColumnToTable",
        {
            tabNameEn: tab_name_en,
            tabName: tab_name,
            columnItems: jsy_itemVOs
        });
    if ("ok" === result.status) {
        FreeUtil.hiddenDialog({
            result: "Confirm",
            buildWhereSQL: str_sql,
            buildOrderBy: " cny_bal desc ",
            popDialogId: self.v_dialogid,
            callBackQueryItemValue: callBackQueryItemValue
        });
    } else {
        $.messager.alert('提示', '设置连续性查询字段出错！', 'warning');
        return;
    }
}

//重置
function onReset() {
    $('#d1_A_QueryForm').form('clear');
}

//取消
function onCancel() {
    FreeUtil.hiddenDialog({popDialogId: self.v_dialogid});
}
