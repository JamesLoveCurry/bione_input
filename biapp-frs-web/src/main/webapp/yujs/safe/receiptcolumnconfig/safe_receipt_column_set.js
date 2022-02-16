//列表购物车参照,即可以实现翻页多选.
var ary_cartkeys = ['col_name', 'col_name_en'];   //购物车的字段
var ary_cartnames = [];  //名称
var ary_cartwidths = [100, 180];  //宽度
var tab_name_en = "";

function AfterInit() {
    tab_name_en = jso_OpenPars.tab_name_en;
    
    JSPFree.createSplitByBtn("d1", "左右", 620, ["确定/onConfirm", "取消/onCancel"]);
    //创建左边的表
    JSPFree.createBillList("d1_A", "/biapp-safe/freexml/common/safe_receipt_column_set_left.xml", null, {
        isSwitchQuery: "N",
        refWhereSQL: " tab_name_en ='" + tab_name_en + "'"
    });
    JSPFree.queryDataBySQL(d1_A_BillList, getLeftBillListSQL());
    
    //计算出右边表格的列表与宽度
    var jsy_itemVOs = d1_A_BillList.templetVO.templet_option_b;
    for (var i = 0; i < ary_cartkeys.length; i++) {
        var itemVO = FreeUtil.findItemVOByItemkey(jsy_itemVOs, ary_cartkeys[i]);
        if (itemVO == null) {
            ary_cartnames.push(ary_cartkeys[i]);
        } else {
            ary_cartnames.push(itemVO.itemname);
        }
    }
    //创建右边的购物车表
    JSPFree.createBillListByItems("d1_B", "/biapp-safe/freexml/common/safe_receipt_column_set_right.xml", ary_cartkeys, ary_cartnames, ary_cartwidths);
    JSPFree.queryDataBySQL(d1_B_BillList, getRightBillListSQL());
    
    //双击左边表格数据,选择到右边!!
    JSPFree.addBillListDoubleClick(d1_A_BillList, function (_rowIndex, _rowData) {
        var str_key = _rowData[ary_cartkeys[0]];
        var isHave = isAlreadyHaveData(str_key);
        if (isHave) {
            FreeUtil.alert(ary_cartnames[0] + "为【" + str_key + "】数据已经有了!");
            return;
        }
        var jso_newdata = {};
        jso_newdata["rid"] = "";
        for (var i = 0; i < ary_cartkeys.length; i++) {
            jso_newdata[ary_cartkeys[i]] = _rowData[ary_cartkeys[i]];
            jso_newdata['rid'] = _rowData['rid'];
        }
        d1_B_BillList.datagrid('appendRow', jso_newdata);
    });
    //双击左边表格数据,选择到右边!!
    JSPFree.addBillListDoubleClick(d1_B_BillList, function (_rowIndex, _rowData) {
        d1_B_BillList.datagrid('deleteRow', _rowIndex);
    });
    
}

/**
 * 左侧列表sql
 * @returns {string}
 */
function getLeftBillListSQL() {
    var sql = "select * from safe_cr_col c " +
        " where not exists(select 1 from  safe_receipt_import_pk_col r where r.COL_RID = c.RID) " +
        " and c.TAB_NAME_EN = '" + tab_name_en + "' ";
    var orderBy = "order by c.COL_NO asc";
    
    return sql + orderBy;
}

/**
 * 右侧列表sql
 */
function getRightBillListSQL() {
    var sql = "select " +
        " col_name " +
        " ,col_name_en " +
        " ,col_rid as rid " +
        " from safe_receipt_import_pk_col " +
        " where TAB_NAME_EN = '" + tab_name_en + "'" +
        " order by COL_NO asc";
    return sql;
}

/**
 * 检查字段是否已经存在
 * @param _value
 * @returns {boolean}
 */
function isAlreadyHaveData(_value) {
    var jsy_datas = JSPFree.getBillListAllDatas(d1_B_BillList);
    for (var i = 0; i < jsy_datas.length; i++) {
        if (jsy_datas[i][ary_cartkeys[0]] == _value) {
            return true;
        }
    }
    return false;
}

/**
 * 保存
 */
function onConfirm() {
    //购物车的所有数据
    var jsy_datas = JSPFree.getBillListAllDatas(d1_B_BillList);
    var colData = new Array();
    //处理各个列,把各行的数据拼接
    for (var j = 0; j < jsy_datas.length; j++) {
        var rowData = jsy_datas[j];
        var rid = rowData['rid'];
        var colName = rowData['col_name'];
        var colNameEn = rowData['col_name_en'];
        var json = {
            rid: rid,
            colName: colName,
            colNameEn: colNameEn
        }
        colData.push(json);
    }
    var tabData = {
        tab_name_en: tab_name_en
    }
    var jso_org = JSPFree.doClassMethodCall("com.yusys.safe.receipt.service.SafeReceiptImportColSetBS", "setReceiptImportPkColumn", {
        colNames: colData,
        tabData: tabData
    });
    if (jso_org.success === 'ok') {
        JSPFree.closeDialog(true);
    }
}

//取消
function onCancel() {
    JSPFree.closeDialog();
}
