/**
 *
 * <pre>
 * Title:【报送管理】【任务触发器】
 * Description:任务触发器主页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/8/20 10:39
 */
function AfterInit(){
    JSPFree.createSplit("d1", "上下", 400); // 页签先上下分割
    JSPFree.createBillList("d1_A", "/biapp-bfd/freexml/reportJob/bfd_report_job_code.xml",null,{isSwitchQuery:"N"});
    JSPFree.createBillList("d1_B", "/biapp-bfd/freexml/reportJob/bfd_report_job_log_code.xml",null,{isSwitchQuery:"N"});
    // 绑定表格选择事件,d1_A_BillList会根据命名规则已创建
    JSPFree.bindSelectEvent(d1_A_BillList, function(rowIndex, rowData) {
        var rid = rowData.rid; // 取得选中记录中的id值
        var str_sqlWhere = "jobid='" + rid + "'"; // 拼SQL条件
        JSPFree.queryDataByConditon(d1_B_BillList, str_sqlWhere); // 锁定规则表查询数据
    });
}

/**
 * 启动
 * @returns
 */
function startJob(){
    var selectDatas = d1_A_BillList.datagrid('getSelections');
    if (selectDatas.length == 0) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    } else if (selectDatas[0].activeflag == "N") {
        $.messager.alert('提示', '请先激活！', 'warning');
        return;
    } else if (selectDatas[0].jobstat == "启动") {
        $.messager.alert('提示', '任务已启动无需再启动', 'warning');
        return;
    }

    var jso_par = {jobName:selectDatas[0].name};
    var jso_data = JSPFree.doClassMethodCall("com.yusys.bfd.reportJob.service.BfdReportJobBS", "startJob", jso_par);
    if(jso_data.code == "success") {
        $.messager.alert('提示', '启动成功');
    } else {
        $.messager.alert('提示', jso_data.msg, 'warning');
    }

    JSPFree.refreshBillListCurrRow(d1_A_BillList);
}

/**
 * 停止
 * @returns
 */
function endJob(){
    var selectDatas = d1_A_BillList.datagrid('getSelections');
    if (selectDatas.length == 0) {
        $.messager.alert('提示', '至少选择一条记录进行操作', 'warning');
        return;
    } else if (selectDatas[0].jobstat == "停止") {
        $.messager.alert('提示', '任务已经是停止状态', 'warning');
        return;
    }

    var jso_par = {jobName:selectDatas[0].name};
    var jso_data = JSPFree.doClassMethodCall("com.yusys.bfd.reportJob.service.BfdReportJobBS", "stopJob", jso_par);
    if(jso_data.code == "success"){
        $.messager.alert('提示', '停止成功');
    } else {
        $.messager.alert('提示', jso_data.msg, 'warning');
    }

    JSPFree.refreshBillListCurrRow(d1_A_BillList);
}
/*
*删除
 */
function delete1(){
    var job = d1_A_BillList.datagrid('getSelected');
    if (job == null) {
        $.messager.alert('提示', '必须选择一条记录进行操作', 'warning');
        return;
    }
    //判断状态
    if(job.jobstat == "启动"){
        $.messager.alert('提示', '当前任务处于启动状态，请先停止在删除！', 'warning');
        return;
    }
    JSPFree.confirm("提示", "你真的要删除选中的记录吗?", function(_isOK){
        if(_isOK){
            var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.reportJob.service.BfdReportJobBS", "deleteJob",{jobId:job.rid});
            if(jso_rt.code == 0){
                $.messager.show({title:'消息提示',msg: jso_rt.msg ,showType:'show'});
                JSPFree.queryDataByConditon(d1_A_BillList);  //立即查询刷新数据
            }
            else{
                JSPFree.alert(jso_rt.msg);
            }
        }
    });
}