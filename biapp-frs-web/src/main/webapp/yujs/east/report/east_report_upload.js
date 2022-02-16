//初始化界面,菜单配置路径是【/frs/yufreejs?js=/yujs/east/east_report_list.js】
var org_no = "";

function AfterInit() {
    //通过当前登录人所属内部机构获取报送机构号
    var jso_report_org = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition", "getReportOrgNo", {
        _loginUserOrgNo: str_LoginUserOrgNo,
        report_type: "04"
    });
    if (jso_report_org.msg == "ok") {
        org_no = jso_report_org.data;
    }

    // 获取最新一期的日期
    var data_dt1 = "";
    var jso_data = JSPFree.getHashVOs("select max(data_dt) data_dt from east_cr_data_upload");
    if (jso_data != null && jso_data.length > 0) {
        data_dt = jso_data[0].data_dt;
    }

    JSPFree.createBillList("d1", "/biapp-east/freexml/east/report/east_cr_data_update_CODE.xml", null, {
        autoquery: "N",
        isSwitchQuery: "N"
    });
    FreeUtil.loadBillQueryData(d1_BillList, {data_dt: data_dt, org_no: org_no});
    var _sql = "select * from east_cr_data_upload where data_dt='" + jso_data[0].data_dt + "' and org_no='" + org_no + "'";
    JSPFree.queryDataBySQL(d1_BillList, _sql);
//	JSPFree.setBillListForceSQLWhere(d1_BillList, "org_no='"+org_no+"'");
}

//页面加载结束后
function AfterBodyLoad() {
    // 判断是否是总行，如果是总行则不禁用，如果是其他则禁用
    var org_class = "";
    var jso_org = JSPFree.getHashVOs("SELECT org_class FROM rpt_org_info where org_type='04' and org_no = '" + org_no + "'");
    if (jso_org != null && jso_org.length > 0) {
        org_class = jso_org[0].org_class;
    }
    if (org_class != '总行') {
        JSPFree.setBillQueryItemEditable("org_no", "下拉框", false);
    }
}

//获得本级和下属机构
function getQueryCondition() {
    var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition", "getQueryCondition", {"org_no": str_LoginUserOrgNo});
    var condition = "";
    if (jso_rt.msg == "ok") {
        condition = jso_rt.condition;
    }
    return condition;
}

/**
 * 新增
 */
function insert() {

    JSPFree.openDialog("新增", "/yujs/east/report/east_cr_upload_insert.js", 350, 350, {org_no: org_no}, function (_rtdata) {
        if (_rtdata != "" && _rtdata != null && _rtdata != undefined) {
            JSPFree.queryDataByConditon(d1_BillList, _rtdata);  //立即查询刷新数据
        }
    });

}

function doZipCall(_par) {
    JSPFree.doClassMethodCall2("com.yusys.east.business.report.service.EastCrDataUpload", "msgZip", _par,function (_rt) {
        if (_rt.status == "success") {
            JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
            $.messager.alert("提示", "生成报文包成功！", "info");
        } else {
            $.messager.alert("提示", "生成报文包失败！", "warning");
        }
    });
    $.messager.alert("提示", "报文包正在生成,请稍候......", "info");
}

/**
 * 生成zip包
 */
function msgZip() {
    //选择选中的数据..
    var json_data = d1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data.length > 1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }
    var hashVOs = JSPFree.getHashVOs("select count(*) as count from east_cr_task  where busi_org_no='" + org_no + "' and data_dt='" + data_dt + "' and status!='成功'");
    if (hashVOs[0].count > 0) {
        $.messager.alert('提示', '该机构在当前日期下存在未完成的报文任务!', 'warning');
        return;
    }

    var _par = {org_no: json_data[0].org_no, data_dt: json_data[0].data_dt};

    if (json_data[0].zip_num != 0) {
        JSPFree.confirm('提示', '该数据已有报文压缩包，是否覆盖?', function (_isOK) {
            if (_isOK) {
                doZipCall(_par);
            }
        });
    } else {
        doZipCall(_par);
    }
}

/**
 * 上传pdf
 */
function uploadPDF() {
    //选择选中的数据..
    var json_data = d1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data.length > 1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }

    // 根据状态判断，如果是分发，完成状态，则详细页面置灰，不可编辑分发说明
    var jso_Pars = {
        data_dt: json_data[0].data_dt,
        org_no: json_data[0].org_no,
        zljh_status: json_data[0].zljh_status,
        bsqd_status: json_data[0].bsqd_status
    };
    JSPFree.openDialog("上传", "/yujs/east/report/easti_data_pfd_import.js", 500, 500, jso_Pars, function (_rtdata) {
        if (_rtdata == true) {
            JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
        }
    });
}

function doXMLCall(json_data) {
    var jso_Pars = {
        dataDt: json_data[0].data_dt.replace(/^(\d{4})(\d{2})(\d{2})$/, "$1-$2-$3"),
        orgNo: json_data[0].org_no
    };
    JSPFree.doClassMethodCall2("com.yusys.east.business.report.service.CreateReportXmlFileUtil", "startGenerateReportXMLFile", jso_Pars,function (_rt) {
        if (_rt.status == "success") {
            JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
            $.messager.alert('提示', 'xml报文生成成功!', 'info');
        } else {
            $.messager.alert('提示', _rt.msg, 'warning');
        }
    });
    $.messager.alert('提示', 'xml报文正在生成中,请稍候..........', 'info');
}

/**
 * 生成XML报文
 */
function createXML() {
    //选择选中的数据..
    var json_data = d1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data.length > 1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data[0].zljh_status == "N" || json_data[0].bsqd_status == "N" || json_data[0].zip_num == 0) {
        $.messager.alert('提示', '只有存在报文ZIP包、报送清单PDF、质量检核PDF时才能创建xml报文', 'warning');
        return;
    }


    if (json_data[0].xml_status == "Y") {
        JSPFree.confirm('提示', '已生成xml报文，是否覆盖?', function (_isOK) {
            if (_isOK) {
                doXMLCall(json_data);
            }
        });
    } else {
        doXMLCall(json_data);
    }
}


/**
 * 导出
 * @returns
 */
function onDownload() {
    var json_data = d1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data.length > 1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data[0].zip_num == 0 && json_data[0].zljh_status == "N" && json_data[0].bsqd_status == "N" && json_data[0].xml_status == "N") {
        $.messager.alert('提示', '没有文件！', 'warning');
        return;
    }
    var _par = {org_no: json_data[0].org_no, data_dt: json_data[0].data_dt};
    JSPFree.openDialog("打包下载", "/yujs/east/report/superviseMsgDownload_zip.js", 780, 300, _par);
}

function doDataUploadCall(json_data) {
    var _par = {org_no: json_data[0].org_no, data_dt: json_data[0].data_dt};
    var _rt = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.ScheduledEastDayTaskBS", "handSendTask", _par);

    if (_rt.status == "success") {
        JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
        $.messager.alert('提示', '数据上传成功！', 'info');
    } else {
        $.messager.alert('提示', '数据上传失败！' + _rt.msg, 'warning');
    }
}

/**
 * 数据上传
 */
function dataUpload() {
    //选择选中的数据..
    var json_data = d1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data.length > 1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data[0].zljh_status == "N" || json_data[0].bsqd_status == "N" || json_data[0].zip_num == 0 || json_data[0].xml_status == "N") {
        $.messager.alert('提示', '只有存在报文ZIP包、报送清单PDF、质量检核PDF、报文XML时才能上传数据！', 'warning');
        return;
    }
    if (json_data[0].upload_status == "Y") {
        JSPFree.confirm('提示', '该数据已经上传，是否覆盖远程服务器文件?', function (_isOK) {
            if (_isOK) {
                doDataUploadCall(json_data);
            }
        });
    } else {
        doDataUploadCall(json_data);
    }
}


/**
 * 远程下载回执
 */
function onImportFromRemote() {
    //选择选中的数据..
    var json_data = d1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data.length > 1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }
    var _par = {org_no: json_data[0].org_no, data_dt: json_data[0].data_dt};
    if (json_data[0].receipt_import_status == "Y") {
        JSPFree.confirm('提示', '回执已导入，是否覆盖?', function (_isOK) {
            if (_isOK) {
                var _rt = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.ScheduledEastDayTaskBS", "handLoadReceiptTask", _par);
                if (_rt.status == "success") {
                    JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
                    $.messager.alert('提示', '远程下载回执文件成功！', 'info');
                } else {
                    $.messager.alert('提示', '远程下载回执文件失败！' + _rt.msg, 'warning');
                }
            }
        });
    } else {
        var _rt = JSPFree.doClassMethodCall("com.yusys.east.business.report.service.ScheduledEastDayTaskBS", "handLoadReceiptTask", _par);
        if (_rt.status == "success") {
            JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
            $.messager.alert('提示', '远程下载回执文件成功！', 'info');
        } else {
            $.messager.alert('提示', '远程下载回执文件失败！' + _rt.msg, 'warning');
        }
    }

}


/**
 * 回执导入
 * @returns
 */
function onImport() {
    //选择选中的数据..
    var json_data = d1_BillList.datagrid('getSelections');
    if (json_data == null || json_data == undefined || json_data.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (json_data.length > 1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }
    var _par = {data_dt: json_data[0].data_dt, org_no: json_data[0].org_no};
    if (json_data[0].receipt_import_status == "Y") {
        JSPFree.confirm('提示', '回执已导入，是否覆盖?', function (_isOK) {
            if (_isOK) {
                JSPFree.openDialog("文件上传", "/yujs/east/report/receipt_import.js", 500, 240, _par, function (_rtdata) {
                    if (_rtdata != null && _rtdata != "undefined") {
                        if (_rtdata.msg) {
                            JSPFree.alert(_rtdata.msg);
                        }
                        JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
                    }
                });
            }
        });
    } else {
        JSPFree.openDialog("文件上传", "/yujs/east/report/receipt_import.js", 500, 240, _par, function (_rtdata) {
            if (_rtdata != null && _rtdata != "undefined") {
                if (_rtdata.msg) {
                    JSPFree.alert(_rtdata.msg);
                }
                JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
            }
        });
    }
}