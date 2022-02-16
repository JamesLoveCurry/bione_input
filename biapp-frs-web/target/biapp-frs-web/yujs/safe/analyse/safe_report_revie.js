/**
 *
 * <pre>
 * Title:【统计与监控】-【复核情况统计】
 * 机构权限控制：总行统计全行的数据，分行统计辖内的数据 支行统计自己数据
 * Description: 计算各个报表的 总数，未确认数，未复核数，已复核数
 * SafeReportAnalysisBuilderTemplate.getTemplet() 参数是中文表名和type=1，主界面。后面会继续用到这个方法，是数据下钻的页面，传参数 3
 * </pre>
 * @author baifk
 * @date 2021年03月03日
 */
var str_subfix = "";
var tab_name = "";
var str_className = "";
var org_no = "";

function AfterInit() {
    // 获取路径参数
    if (jso_OpenPars != '') {
        if (jso_OpenPars.type != null) {
            str_subfix = jso_OpenPars.type; //获取后缀
        }
    }

    // 通过当前登录人所属内部机构获取报送机构号
    var jso_report_org = JSPFree.doClassMethodCall("com.yusys.safe.base.common.service.SafeValidateQueryConditionBS", "getReportOrgNoCondition", {
        _loginUserOrgNo: str_LoginUserOrgNo,
        report_type: str_subfix
    });
    if (jso_report_org.msg == "ok") {
        org_no = jso_report_org.data;
    }
    tab_name = SafeFreeUtil.getSafeAnalysisTab().review;
    // 传参中文表名+type=1，主界面
    str_className = "Class:com.yusys.safe.analysis.template.SafeReportAnalysisBuilderTemplate.getTemplet('" + tab_name + "','3',"+str_subfix+")";
    JSPFree.createBillList("d1", str_className, null, {
        list_btns: "[icon-p69]导出/exportApproval(this);",
        isSwitchQuery: "N"
    });
    //绑定查询事件，即点击查询按钮触发此事件
   JSPFree.billListBindCustQueryEvent(d1_BillList, onQuery);
}

/**
 * 点击查询按钮之后的触发事件，获取查询框的内容，拼接sql查询数据
 * 'data_dt' as data_dt的原因是把数据日期，机构号，业务类型也加到表单中，传到后台
 * 通过SafeReportAnalysisFormulaBS，计算此数据日期下的统计信息，例如总条数。
 * @param _condition 获取到的查询框的条件
 * @returns
 */
function onQuery(_condition) {
    var conditionOrgNo = "";
    if (_condition.indexOf('org_no ') != -1) {
        if (_condition.indexOf('tab_name') != -1) {
            conditionOrgNo =  _condition.substring(_condition.indexOf('and'), _condition.lastIndexOf("and"));
        } else {
            conditionOrgNo = _condition.substring(0, _condition.lastIndexOf("and"));
        }
    }
    var jsn_result = JSPFree.doClassMethodCall("com.yusys.safe.analysis.service.SafeReportReviewBS", "findOrgChild", {conditionOrgNo:conditionOrgNo,orgNo:org_no, orgType:str_subfix, isAllChild:true, isContainFa:true});
    
    // 数据日期是必输项，不需要判断非空
    var data_dt = _condition.substring(_condition.indexOf('data_dt') + 11, _condition.length - 2);
   
    var orgArray = jsn_result.org;
    var totalSql = "";
    if (orgArray.length > 0) {
        for (var i = 0; i < orgArray.length; i++) {
            var sql = "select tab_name,tab_name_en, '" + data_dt + "' as data_dt, orgNo as org_no, " + "'" + str_subfix +
                "' as report_type,tab_no as tabNo from safe_cr_tab where report_code is not null and  tab_belong='" + SafeFreeUtil.getSafeTabBelongto().business + "' and report_type like '%" + str_subfix + "%' ";
            if (_condition.indexOf('tab_name') != -1) {
                // 如果表名like条件不为空，则把表名也加到过滤条件里面
                sql = sql + " and " + _condition.substring(0, _condition.indexOf('and'));
            }
            var orgNo = orgArray[i].id.orgNo;
            sql = sql.replace("orgNo", "'" + orgNo + "'");
            totalSql += sql + " union all ";
        }
        totalSql = totalSql.substring(0, totalSql.lastIndexOf("union all"));
        d1_BillList.pagerType = "2";
        JSPFree.queryDataBySQL(d1_BillList, "select * from ( " + totalSql + " ) t" + " order by t.tabNo"); //查询数据
    } 
    
}

/**
 * 点击【总数】【通过数】【未通过数】数据展示
 *
 * @param _this
 */
function onExplore(_this) {
    var rows = d1_BillList.datagrid("getRows");
    var row = rows[_this.dataset.rowindex];//index为行号
    var itemKey = _this.dataset.itemkey;
    var jso_par = {
        data_dt: row.data_dt,
        org_no: row.org_no,
        report_type: row.report_type,
        tab_name_en: row.tab_name_en,
        str_tab_name: row.tab_name
    }
    if ('unsubmitted_count' === itemKey) {
        // 未确认数
        jso_par.data_status = SafeFreeUtil.getApprovalStatus().UNSUBMITTED;
    } else if ('submitted_count' === itemKey) {
        // 未复核数
        jso_par.data_status = SafeFreeUtil.getApprovalStatus().SUBMITTED;
    } else if ('approved_rate' === itemKey) {
        // 已复核数
        jso_par.data_status = SafeFreeUtil.getApprovalStatus().APPROVED;
    }
    JSPFree.openDialog("查看详情", "/yujs/safe/analyse/safe_report_review_view.js", 1000, 600, jso_par, function (_rtdata) {
    });
}

/**
 * 导出审核情况统计，先查询，后导出
 *
 * @returns
 */
function exportApproval(_btn) {
    if (d1_BillList.CurrSQL == null || d1_BillList.CurrSQL == "") {
        JSPFree.alert("请先查询后导出！");
        return;
    }

    var data_dt = JSPFree.getBillQueryFormValue(d1_BillQuery).data_dt; // 查询框中的日期

    var _downloadName = str_subfix + "-审核情况统计-" + data_dt + ".xlsx";

    var jso_par = {
        "templetcode": str_className, //模板编码
        "SQL": d1_BillList.CurrSQL,  //实际SQL
    };

    JSPFree.downloadFile("com.yusys.safe.analysis.service.SafeReportReviewExportBS", _downloadName, jso_par);
}