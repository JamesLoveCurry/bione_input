<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <meta name="decorator" content="/template/template1_BS.jsp">
    <!-- 基础的JS和CSS文件 -->
    <script type="text/javascript">
        var grid;
        var objTimer;

        function initSearchForm() {
            $("#search").ligerForm({
                fields: [{
                    display: '任务组编号',
                    name: "instanceId",
                    newline: true,
                    labelAlign: "center",//默认标签对齐方式
                    inputWidth: 5,
                    width: 220,//控件宽度,标签宽度是labelWidth
                    space: 30,//默认间隔宽度,这个搜索条件和下一个搜索条件
                    type: "text",
                    cssClass: "field",
                    attr: {
                        field: 'instanceId',
                        op: "like"
                    }
                }, {
                    display: '任务组名称',
                    name: "taskNm",
                    newline: false,
                    labelAlign: "center",//标签对齐方式
                    width: 220,
                    space: 30,
                    type: "text",
                    cssClass: "field",
                    attr: {
                        field: 'taskNm',
                        op: "like"
                    }
                }, {
                    display: '数据日期',
                    name: "dataDate",
                    id: "dataDate",
                    newline: false,
                    labelAlign: "center",//标签对齐方式
                    width: 150,
                    space: 30,
                    type: "date",
                    cssClass: "field",
                    options: {
                        format: "yyyyMMdd"
                    },
                    attr: {
                        field: 'dataDate',
                        op: "="
                    }
                }, {
                    display: '任务状态',
                    name: 'sts',
                    newline: false,
                    labelAlign: 'center',//标签对齐方式
                    width: 150,
                    space: 30,
                    type: "select",
                    comboboxName: "exe_sts_box",
                    cssClass: "field",
                    attr: {
                        field: 'sts',
                        op: "="
                    },
                    options: {
                        url: "${ctx}/report/frame/enginelog/rpt/exeStsList.json"
                    }
                }]
            });
        };

        function initGrid() {
            grid = $("#maingrid").ligerGrid({
                toolbar: {},
                columns: [
                    {
                        display: '任务组编号',
                        name: 'id.instanceId',//引擎表的instanceTaskId就是任务组表的taskNo,而引擎表里的TaskNo是随机生成
                        width: "20%",
                        align: 'center',//控件对齐方式
                        render: function (row, index, val) {
                            return "<a style='color:blue' onclick='f_open_view(\"" + row.taskType + "\",\"" + row.id.taskNo + "\")'>" + val + "</a>"
                        }
                    }, {
                        display: "任务组名称",
                        name: "taskNm",
                        width: "20%",
                        align: "center"
                    }, {
                        display: "任务组类型",
                        name: "taskType",
                        width: "7%",
                        render: function (row, index, val) {
                            if (val == "RptIdGroup") {
                                return "报表组"
                            }
                            if (val == "IndexNoGroup") {
                                return "指标组"
                            }
                        }
                    }, {
                        display: '数据日期',
                        name: 'id.dataDate',
                        width: "7%",
                        align: 'center'
                    }, {
                        display: '任务组状态',
                        name: 'sts',
                        width: "7%",
                        align: 'center',
                        render: function (row) {
                            switch (row.sts) {
                                case  "01":
                                    return "等待执行";
                                case  "02":
                                    return "正在执行";
                                case  "03":
                                    return "执行成功";
                                case  "04":
                                    return "执行失败";
                                case  "07":
                                    return "手动停止";
                                case  "08":
                                    return "超时停止";
                                default  :
                                    return "未知";
                            }
                        }
                    }, {
                        display: '开始时间',
                        name: 'startTime',
                        width: "13%",
                        align: 'center',
                        type: "date",
                        format: "yyyy-MM-dd hh:mm:ss"
                    }, {
                        display: '结束时间',
                        name: 'endTime',
                        width: "14%",
                        align: 'center',
                        type: "date",
                        format: "yyyy-MM-dd hh:mm:ss"
                    }, {
                        display: '操作',
                        name: 'oper',
                        width: "7%",
                        align: 'center',
                        render: function (row, index, val) {
                            if (row.sts == "01" || row.sts == "02") {
                                return "<a style='color:blue' onclick='f_stop(\"" + row.id.taskNo + "\")'>停止</a>";
                            }
                            if (row.sts == "03" || row.sts == "04" || row.sts == "07" || row.sts == "08") {
                                return "<a style='color:blue' onclick='f_redo(\"" + row.id.taskNo + "\",\"" + row.id.dataDate + "\",\"" + row.taskType + "\")'>重跑</a>";
                            }
                        }
                    }],
                dataAction: 'server',
                usePager: true,
                alternatingRow: true,
                colDraggable: true,
                url: "${ctx}/report/frame/taskrundata/getTaskRundata.json",
                sortName: 'startTime',
                sortOrder: 'desc',
                pageParmName: 'page',
                pagesizeParmName: 'pagesize',
                checkbox: true,
                rownumbers: true,
                width: '100%',
                height: '99%'
            });
        };

        function initToolBar() {
            var toolBars = [{
                text: '新增',
                click: f_add_run,
                icon: 'fa-plus'
            }, {
                text: '删除',
                click: f_open_delete,
                icon: 'fa-trash'
            }];
            BIONE.loadToolbar(grid, toolBars, function () {
            });
        }

        $(function() {
            initSearchForm();
            initGrid();
            initToolBar();
            BIONE.addSearchButtons("#search", grid, "#searchbtn");
            $(".l-panel-bbar-inner").find(".l-clear").remove();
            var btmInnerHtml = '<div  style="float:left;margin-right:1px;position: relative;top:-2px;"><span>刷新间隔：</span></div><div class="l-bar-group"><div><a><input id="refresh"></a></div></div><div class="l-bar-separator"></div><div class="l-clear"></div>';
            $(".l-panel-bbar-inner").append(btmInnerHtml);
            //加载底部样式
            $("#refresh").ligerComboBox({
                cancelable : false,
                width : 70,
                data:[{
                    text : "10秒",
                    id : "10"
                },{
                    text : "20秒",
                    id : "20"
                },{
                    text : "30秒",
                    id : "30"
                },{
                    text : "60秒",
                    id : "60"
                },{
                    text : "不刷新",
                    id : "off"
                }],
                onSelected : function(id,value){
                    if(id!="off") {
                        grid.reload();
                        refreshTimer(id);
                    }
                }
            });
            initBtmSts();
        });

        function initBtmSts() {
            var sts = queryEngineSts();
            if (sts == "exsi") {
                if ($.ligerui.get("refresh").getValue() == "off" ||
                    $.ligerui.get("refresh").getValue() == "") {
                    $.ligerui.get("refresh").selectValue("10");
                }
            } else {
                $.ligerui.get("refresh").selectValue("off");
            }
        }

        //查询引擎状态
        function queryEngineSts() {
            var sts;
            $.ajax({
                cache: false,
                async: false,
                url: "${ctx}/report/frame/taskrundata/getEnginePendingSts",
                dataType: 'json',
                type: "GET",
                success: function (result) {
                    sts = result.msg;
                }
            });
            return sts;
        }

        //定时器
        function refreshTimer(sec) {
            if (sec == 'off' && objTimer) {
                window.clearInterval(objTimer);
            } else {
                if (sec != "") {
                    objTimer = window.setInterval("excuteRefresh()", sec * 1000);
                }
            }
        }

        //执行刷新
        function excuteRefresh() {
            grid.reload();
           /* if (queryEngineSts() == "unExsi") {
                window.clearInterval(objTimer);
                $.ligerui.get("refresh").selectValue("off");
            }*/
        }

        //改刷新状态,点击运行或重跑时调用
        function changeRefreshSts() {
            window.clearInterval(objTimer);
            $.ligerui.get("refresh").selectValue("10");
        }

        //发送指令后立马执行刷新,点击运行或重跑时调用,默认10秒刷新
        function quickRefresh() {
            window.clearInterval(objTimer);
            objTimer = window.setInterval("excuteRefresh()", 10000);
        }


        function f_open_view(taskType, taskNo) {
            var dialogNm = '';
            if (taskType == "RptIdGroup") {
                dialogNm = "报表任务组跑数状态";
            }
            if (taskType == "IndexNoGroup") {
                dialogNm = "指标任务组跑数状态";
            }
            dialog = BIONE.commonOpenDialog(dialogNm,
                "taskRunDataDetail", 800, 500,
                "${ctx}/report/frame/taskrundata/detailTask?taskNo="
                + taskNo, null);
        }

        //跑数任务
        function f_add_run() {
            dialog = BIONE.commonOpenDialog("新建任务组跑数",
                "taskRunDataNew", 800, 500,
                "${ctx}/report/frame/taskrundata/newTaskRunData", null);
        }

        function f_stop(taskNo,taskType) {
            $.ajax({
                cache: false,
                async: false,
                url: "${ctx}/report/frame/taskrundata/stopTask",
                dataType: 'json',
                data: {
                    taskNo: taskNo
                },
                type: "post",
                success: function (result) {
                    if (result.msg) {
                        initBtmSts();
                        BIONE.tip(reuslt.msg);
                    } else {
                        grid.loadData();
                        BIONE.tip("任务已停止");

                    }
                }
            });
        }

        function f_redo(taskNo, dataDate, taskType) {
            //不查询报表或者指标状态是否是提交过，因为即使提交过也可以重跑
            $.ligerDialog.confirm("是否重跑任务组", function (yes) {
                if (yes) {
                    redoTask(taskNo, dataDate, taskType);
                }
            });
        }

        function redoTask(taskNo, dataDate, taskType) {
            changeRefreshSts();//当点击重跑，立马显示10秒自动刷新
            $.ajax({
                cache: false,
                async: false,
                url: "${ctx}/report/frame/taskrundata/redoTask",//调用的是
                dataType: 'json',
                data: {
                    taskNo: taskNo,
                    dataDate: dataDate,
                    taskType: taskType
                },
                type: "post",
                success: function (result) {
                    if (result.msg) {
                        quickRefresh();//指令发送完成后立即执行自动刷新
                        BIONE.tip(reuslt.msg);
                    } else {
                        grid.loadData();
                        BIONE.tip("任务已重跑");

                    }
                }
            });
        }


        //跑数任务删除
        function f_open_delete() {
            var rows = grid.getSelectedRows();
            var params = [];
            if (rows.length > 0) {
                for(var i=0; i<rows.length; i++) {
                    if(rows[i].sts=="01"||rows[i].sts=="02"){
                        BIONE.tip("任务组等待执行或者正在执行中，不能删除！");
                        return;
                    }
                    params.push(rows[i].id.taskNo);
                }
                    $.ligerDialog
                    .confirm(
                        '您确定删除' + rows.length + "条任务组跑数记录么？",
                        function (yes) {
                            if (yes) {
                                $.ajax({
                                    type: "POST",
                                    url: "${ctx}/report/frame/taskrundata/deleteTaskRunData",
                                    data: {
                                        taskNos: JSON.stringify(params)
                                    },
                                    dataType: "json",
                                    success: function (result) {
                                        BIONE.tip(result.msg);
                                        grid.loadData();
                                    },
                                    error: function () {
                                        BIONE.tip("删除任务组跑数记录异常，请联系系统管理员");
                                    }
                                });


                            }
                        });
            } else {
                BIONE.tip('请选择要删除的行');
            }
        }

    </script>
</head>
<body>
</body>
</html>