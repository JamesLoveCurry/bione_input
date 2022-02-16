/**
 *
 * <pre>
 * Title: 【配置管理】-【回执主键字段管理】
 * Description:
 * </pre>
 * @author baifk
 @date 2021年06月28日
 */

function AfterInit() {
    
    JSPFree.createBillList("d1", "/biapp-safe/freexml/common/safe_receipt_column_list.xml", null, {});
    d1_BillList.pagerType = "1";
    JSPFree.queryDataBySQL(d1_BillList, getSql() + " order by t.tab_belong, t.tab_no asc ");
    JSPFree.billListBindCustQueryEvent(d1_BillList, onQuery);
}

function getSql() {
    var sql = "select * from (" +
        " select " +
        "  t.tab_name" +
        ", t.tab_name_en" +
        ", t.report_type" +
        ",t.tab_no" +
        ",t.tab_belong" +
        ", listagg(s.COL_NAME, ',') within group ( ORDER BY s.COL_NO asc) as pk_column" +
        ", listagg(s.COL_NAME_EN, ',') within group ( ORDER BY s.COL_NO asc) as pk_columns_en" +
        " from safe_cr_tab t" +
        " LEFT JOIN safe_receipt_import_pk_col s on t.tab_name_en = s.tab_name_en" +
        " where t.report_code is not null and tab_belong = '业务表'" +
        " group by t.TAB_NAME" +
        "  , t.TAB_NAME_EN" +
        "  , t.REPORT_TYPE" +
        "  ,t.tab_no" +
        "  ,t.tab_belong" +
        " ) t ";
    return sql;
}

/**
 * 设置
 *
 */
function setPkColumn() {
    debugger
    var select = JSPFree.getBillListSelectData(d1_BillList);
    if (!select || select.length == 0) {
        JSPFree.alert("请选择数据！");
        return;
    }
    var tab_name_en = select.tab_name_en;
    JSPFree.openDialog("字段设置", "/yujs/safe/receiptcolumnconfig/safe_receipt_column_set.js", 950, 520, {
        tab_name_en: tab_name_en
    }, function (_rtdata) {
        if (_rtdata == true) {
            onQuery();
        }
    });
}

function onQuery() {
    var sql = getSql();
    var condition = JSPFree.getQueryFormSQLCons(d1_BillList);
    if (condition) {
        sql = sql + " where " + condition + " order by t.tab_belong, t.tab_no asc";
    }
    JSPFree.queryDataBySQL(d1_BillList, sql);
}
