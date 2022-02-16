//锁定与解锁数据
function AfterInit() {
    //通过当前登录人所属内部机构获取报送机构号
    var jso_report_org = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdValidateQueryConditionBS", "getReportOrgNoCondition", {_loginUserOrgNo: str_LoginUserOrgNo});
    if (jso_report_org.msg == "ok") {
        org_no = jso_report_org.data;
    }
    org_class = BfdFreeUtil.getOrgClass(org_no); //获取机构层级
    org_nm = BfdFreeUtil.getOrgNm(org_no); //获取机构名称

    JSPFree.createBillList("d1", "/biapp-bfd/freexml/fillingProcess/bfd_lock_data_code.xml", null, {
        list_btns: "[icon-p38]新增/createLock;[icon-p18]锁定/onLock_1;[icon-p20]解锁/onUnLock_1;[icon-remove]删除/del",
        isSwitchQuery: "N"
    });

    JSPFree.queryDataByConditon2(d1_BillList, getCondition());
    // 如果当前机构为报送机构，则设置默认值，否则不设置值
    if (org_no) {
        var result = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdValidateQueryConditionBS", "checkOrgIsReportOrg", {
            rptOrgNo: org_no,
            isSingle: false
        });
        if (result.result) {
            FreeUtil.loadBillQueryData(d1_BillList, {org_no: result.result});
        }
    }
}

function AfterBodyLoad() {
}

function getCondition() {
    var condition = "";
    var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdValidateQueryConditionBS", "getQueryCondition", {"_loginUserOrgNo": str_LoginUserOrgNo});
    if (jso_rt.msg == "ok") {
        condition = jso_rt.condition;
    }

    return condition;
}

//创建任务
function createLock() {
    JSPFree.openDialog("新增", "/yujs/bfd/fillingProcess/lock_data_insert.js", 500, 500, {currSQL: d1_BillList.CurrSQL}, function (_rtdata) {
        if (_rtdata != null) {
            if (_rtdata.status == "OK") {
                JSPFree.refreshBillListCurrRows(d1_BillList);  //刷新当前行
                $.messager.alert('提示', '创建成功!', 'info');
            } else if ("dirclose" == _rtdata.type) {
                return;
            } else {
                $.messager.alert('提示', '创建失败，原因提交重复数据!', 'info');
            }
        }
    });

}

//锁定
function onLock_1() {
    var jso_rt = null;
    var selectDatas = d1_BillList.datagrid('getSelections');
    if (selectDatas.length == 0) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }

    var selectDatas = d1_BillList.datagrid('getSelections');
    var rid = new Array();
    for (var i = 0; i < selectDatas.length; i++) {
        var status = selectDatas[i].status;
        if ("1" == selectDatas[i].status) {
            $.messager.alert('提示', '当前选择的数据中存在已全部锁定的数据，请选择未锁定的数据进行锁定', 'warning');
            return;
        }
        rid.push(selectDatas[i].rid);
    }


    JSPFree.openDialog("加锁方式", "/yujs/bfd/fillingProcess/lock_data_choseLock.js", 400, 200, {
        rid: rid,
        type: '1'
    }, function (_rtdata) {
        if (_rtdata != null) {
            if (_rtdata.status == "OK") {
                JSPFree.refreshBillListCurrRows(d1_BillList);  //刷新当前行
                $.messager.alert('提示', '加锁成功!', 'info');
            } else if ("dirclose" == _rtdata.type) {
                return;
            } else {
                $.messager.alert('提示', '加锁失败，原因提交重复数据!', 'info');
            }
        }
    });
}


function del() {
    var jso_rt = null;
    var selectDatas = d1_BillList.datagrid('getSelections');
    if (selectDatas.length == 0) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }

    var str_id = [];
    for (var i = 0; i < selectDatas.length; i++) {
        var status = selectDatas[i].status;
        if ("1" == selectDatas[i].status || "2" == selectDatas[i].status || "2" == selectDatas[i].status) {
            $.messager.alert('提示', '当前选择的数据中存在已锁定的数据，请先解锁后再删除', 'warning');
            return;
        }
        str_id.push(selectDatas[i].rid);
    }
    var idStr = "('" + str_id.join("','") + "')";
    $.messager.confirm('提示', '你真的要删除选中的记录吗?', function (_isConfirm) {
        if (!_isConfirm) {
            return;
        }

        var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdLockData", "del", {str_id: idStr});
        if (jso_rt.msg == 'ok') {
            $.messager.alert('提示', '删除成功!', 'info');
            JSPFree.queryDataByConditon2(d1_BillList, getCondition());
        } else {
            $.messager.alert('提示', '删除失败!', 'info');
        }

    });

}

//解锁
function onUnLock_1() {
    var jso_rt = null;
    var selectDatas = d1_BillList.datagrid('getSelections');
    if (selectDatas.length == 0) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    }

    var rid = new Array();
    for (var i = 0; i < selectDatas.length; i++) {
        var status = selectDatas[i].status;
        if ("4" == selectDatas[i].status) {
            $.messager.alert('提示', '当前选择的数据中存在已解锁的数据，请选择锁定的数据进行解锁', 'warning');
            return;
        }
        rid.push(selectDatas[i].rid);
    }

    JSPFree.openDialog("解锁", "/yujs/bfd/fillingProcess/lock_data_choseLock.js", 400, 200, {
        rid: rid,
        type: '2'
    }, function (_rtdata) {
        if (_rtdata != null) {
            if (_rtdata.status == "OK") {
                JSPFree.refreshBillListCurrRows(d1_BillList);  //刷新当前行
                $.messager.alert('提示', '解锁成功!', 'info');
            } else if ("dirclose" == _rtdata.type) {
                return;
            } else {
                $.messager.alert('提示', '解锁失败，原因提交重复数据!', 'info');
            }
        }
    });
}

function doCommitUpdateDB(tab_name, data_dt, status) {
    jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.process.service.BfdLockData", "startLockData", {
        tab_name: tab_name,
        data_dt: data_dt,
        status: status
    });
    if (jso_rt != null && jso_rt.status == "OK") {
        $.messager.show({
            title: '消息提示',
            msg: '操作成功',
            showType: 'show'
        });
        JSPFree.refreshBillListCurrRows(d1_BillList);  //刷新当前行
    }
}