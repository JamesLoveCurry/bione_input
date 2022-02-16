/**
 * 外汇引擎任务表
 *
 * @type {string}
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
    // 传参中文表名+type=1，主界面
    str_className = "Class:com.yusys.safe.base.common.template.CommonBuilderTemplate.getTemplet('引擎任务表','SAFE_ENGINE_TASK'," + str_subfix + "," + org_no + ")";
    // 获取机构类型
    var org_class = SafeFreeUtil.getOrgClass(str_LoginUserOrgNo, str_subfix);
    JSPFree.createBillList("d1", str_className, null, {
        list_btns: "[icon-add]新增/addTask(this);[icon-p41]编辑/modifyTask(this);[icon-remove]删除/deleteTask;" +
            "[icon-tip]启动/startEngineTask(this);[icon-reload]刷新/refresh;[icon-p60]取消排队/cancelQueue;" +
            "[icon-p60]中断任务/extracted;[icon-p77]校验日志/onEngineLog;",
        isSwitchQuery: "N",
        list_ismultisel: "Y",
        list_ischeckstyle: "Y",
        orderbys: " create_tm DESC ",
        refWhereSQL: "总行" === org_class ? "business_type = '1'" : "business_type = '2'"
    });
}

/**
 * 新建
 * @returns
 */
function addTask() {
    var pars = {reportType: str_subfix, operateType: "add"};
    JSPFree.openDialog("新建任务", "/yujs/safe/enginetask/safe_add_engine_task.js", 700, 560, pars,
        function (_rtdata) {
            if ("success" === _rtdata.code) {
                JSPFree.queryDataByConditon(d1_BillList, null);
                if (_rtdata.isStartTask) {
                    var msg = _rtdata.is_all_no_start ? "" : "引擎任务已启动，请稍等！";
                    if (_rtdata.resultMsg) {
                        msg += " <br/>【" + _rtdata.resultMsg + " 】任务已存在，无需创建！ ";
                    }
                    if (_rtdata.no_start_task) {
                        msg += " <br/> 以下任务【" + (_rtdata.no_start_task + "】没有可校验数据，无需启动！")
                    }
                    FreeUtil.openHtmlMsgBox2("提示", 450, 300, "<span style='margin-top: 20px'>" + msg + "</span>");
                } else if (_rtdata.resultMsg) {
                    JSPFree.alert(_rtdata.resultMsg + "任务已存在，无需创建！");
                } else {
                    JSPFree.alert(_rtdata.msg);
                }
                var condition = JSPFree.getQueryFormSQLCons(d1_BillQuery);
                JSPFree.queryDataByConditon(d1_BillList, condition);
            } else if ("error" === _rtdata.code) {
                JSPFree.alert("引擎任务创建失败！");
            }
        });
}

/**
 * 修改任务
 */
function modifyTask() {
    var select = JSPFree.getBillListSelectDatas(d1_BillList);
    if (select.length == 0) {
        JSPFree.alert("请选择引擎任务！");
        return;
    }
    if (select.length > 1) {
        JSPFree.alert("请选择一条引擎任务！");
        return;
    }
    
    var selectRecord = select[0];
    // 进行中 或者 排队中任务不能修改
    if ("排队中" === selectRecord.status || "进行中" === selectRecord.status) {
        JSPFree.alert("排队中/进行中任务不能修改！");
        return;
    }
    var pars = {
        reportType: str_subfix,
        operateType: "modify",
        taskId: selectRecord.task_id,
        dataDt: selectRecord.data_dt,
        tabName:selectRecord.tab_name,
        orgNo:selectRecord.org_no
    };
    JSPFree.openDialog("编辑任务", "/yujs/safe/enginetask/safe_add_engine_task.js", 700, 560, pars,
        function (_rtdata) {
            if ("success" === _rtdata.code) {
                JSPFree.queryDataByConditon(d1_BillList, null);  //立即查询刷新数据
                if (_rtdata.isStartTask) {
                    var msg = _rtdata.is_all_no_start ? "" : "引擎已启动，请稍等！";
                    if (_rtdata.is_exists_no_start_task) {
                        msg += " <br/> 任务已存在，无需创建！ ";
                    }
                    if (_rtdata.no_start_task) {
                        msg += " <br/> 以下任务【" + (_rtdata.no_start_task + "】没有可校验数据，无需启动！")
                    }
                    FreeUtil.openHtmlMsgBox2("提示", 450, 300, "<span style='margin-top: 20px'>" + msg + "</span>");
                } else if (_rtdata.is_exists_no_start_task) {
                    JSPFree.alert("任务已存在，无需创建！");
                }
                var condition = JSPFree.getQueryFormSQLCons(d1_BillQuery);
                JSPFree.queryDataByConditon(d1_BillList, condition);
            } else if ("error" === _rtdata.code) {
                JSPFree.alert("引擎任务创修改失败！");
            }
        });
}

/**
 * 删除引擎任务
 */
function deleteTask() {
    var selects = JSPFree.getBillListSelectDatas(d1_BillList);
    if (selects.length == 0) {
        JSPFree.alert("请选择引擎任务！");
        return;
    }
    var params = new Array();
    for (var i = 0; i < selects.length; i++) {
        var data = selects[i];
        var temp = {
            taskId: data.task_id,
            taskName: data.task_name,
        }
        params.push(temp);
    }
    JSPFree.confirm('提示', '你真的要删除选中的记录吗?', function (_isOK) {
        if (_isOK) {
            var result = JSPFree.doClassMethodCall("com.yusys.safe.reportCheck.service.SafeEngineBS",
                "deleteEngineTask", {taskIds: params});
            if ('ok' === result.status) {
                var condition = JSPFree.getQueryFormSQLCons(d1_BillQuery);
                JSPFree.queryDataByConditon(d1_BillList, condition);
                if (result.taskName) {
                    JSPFree.alert("任务【 " + result.taskName + " 】 排队中/进行中，无法删除！");
                    return;
                }
            } else {
                JSPFree.alert("任务删除失败！");
            }
        }
    });
}

/**
 * 启动任务
 */
function startEngineTask() {
    var selects = JSPFree.getBillListSelectDatas(d1_BillList);
    if (selects.length == 0) {
        JSPFree.alert("请选择引擎任务！");
        return;
    }
    var params = new Array();
    for (var i = 0; i < selects.length; i++) {
        var data = selects[i];
        var temp = {
            dataDt: data.data_dt,
            taskId: data.task_id,
            command1: data.command1,
            reportType: data.report_type,
            orgNo: data.org_no,
            businessType: data.business_type,
            tabName: data.tab_name,
            taskName: data.task_name
        }
        params.push(temp);
    }
    var result = JSPFree.doClassMethodCall("com.yusys.safe.reportCheck.service.SafeEngineBS",
        "startEngineTask", {tasks: params});
    if ('success' === result.code) {
        if (result.is_all_no_start) {
            JSPFree.alert("所选任务下没有可以校验的数据，不需要启动！");
        } else {
            var msg = "引擎已启动，请稍等！";
            if (result.no_start_task) {
                msg += " <br/> 以下任务" + (result.no_start_task + "没有可校验数据，无需启动！")
            }
            JSPFree.queryDataByConditon(d1_BillList, null);
            FreeUtil.openHtmlMsgBox2("提示", 450, 300, "<span style='margin-top: 20px'>" + msg + "</span>");
        }
    } else {
        JSPFree.alert("引擎任务启动失败！");
    }
}

/**
 * 取消排队
 */
function cancelQueue() {
    var selects = JSPFree.getBillListSelectDatas(d1_BillList);
    if (selects.length == 0) {
        JSPFree.alert("请选择引擎任务！");
        return;
    }
    var params = new Array();
    for (var i = 0; i < selects.length; i++) {
        var data = selects[i];
        var temp = {
            dataDt: data.data_dt,
            taskId: data.task_id,
            command1: data.command1,
            reportType: data.report_type,
            orgNo: data.org_no,
            businessType: data.business_type,
            tabName: data.tab_name,
        }
        params.push(temp);
    }
    var result = JSPFree.doClassMethodCall("com.yusys.safe.reportCheck.service.SafeEngineBS",
        "cancelQueue", {tasks: params});
    
    if ("ok" === result.status) {
        if (!result.isCancelQueue) {
            $.messager.alert('提示', '只能选择排队中的任务,或任务已经启动引擎还未返回状态,请稍后!', 'warning');
            return;
        }
        $.messager.alert('提示', '取消排队任务成功！', 'warning');
        var condition = JSPFree.getQueryFormSQLCons(d1_BillQuery);
        JSPFree.queryDataByConditon(d1_BillList, condition);
    } else {
        $.messager.alert('提示', '取消排队出错!', 'warning');
        return;
    }
}

/**
 * 中断任务公共方法
 * @param selectData
 */
function extracted() {
    var selectData = d1_BillList.datagrid('getSelections');
    
    if (selectData == null || selectData == undefined || selectData.length == 0) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    if (selectData.length>1) {
        $.messager.alert('提示', '只能选择一条记录进行操作', 'warning');
        return;
    }
    
    var jso_par = {task_id: selectData[0].task_id};
    var jso_status = JSPFree.doClassMethodCall("com.yusys.safe.reportCheck.service.SafeEngineBS", "getBreakTaskStatus", jso_par);
    if (!jso_status.flag) {
        $.messager.alert('提示', '当前任务不在进行中，无需中断！', 'warning');
        return;
    }
    JSPFree.confirm('提示', '是否中断任务?', function (_isOK) {
        if (_isOK) {
            var _rt = JSPFree.doClassMethodCall("com.yusys.safe.reportCheck.service.SafeEngineBS", "forceCloseEngineTask", {task_id: selectData[0].task_id});
            if (_rt.status == "OK") {
                JSPFree.refreshBillListCurrRow(d1_BillList); // 刷新当前行
                $.messager.alert('提示', '任务中断成功！', 'warning');
            }
        }
    });
}

/**
 * 刷新
 */
function refresh() {
    var condition =  JSPFree.getQueryFormSQLCons(d1_BillQuery);
    JSPFree.queryDataByConditon(d1_BillList, condition);
}

/**
 * 查看日志
 */
function onEngineLog() {
    var select = JSPFree.getBillListSelectDatas(d1_BillList);
    if (select.length == 0) {
        JSPFree.alert("请选择引擎任务！");
        return;
    }
    if (select.length > 1) {
        JSPFree.alert("请选择一条引擎任务！");
        return;
    }
    
    JSPFree.openDialog("日志", "/yujs/safe/enginetask/safe_engine_log_view.js", 900, 600,
        {taskId:select[0].task_id}, function(_rtdata) {
        });
}

/**
 * 查看实际命令
 * @param _btn
 */
function onLookCommand2(_btn){
    var str_cmd = _btn.dataset.itemvalue;
    FreeUtil.openHtmlMsgBox2("查看实际命令",800,500,"<span style='font-size:14px;color:blue'>" + str_cmd + "</span>");
}