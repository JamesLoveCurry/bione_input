<%--
  Date: 2021/10/20
  Time: 16:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <meta name="decorator" content="/template/template5.jsp">
    <!-- 基础的JS和CSS文件 -->
    <script type="text/javascript">
        var mainform = null;
        $(function () {
            initTab();
            initMainForm();
            initData();
        })

        function initTab() {
            $("#tab").ligerTab({
                changeHeightOnResize: true,
                contextmenu: false,
                onAfterSelectTabItem: function (tabId) {
                    if (tabId == "child") {
                        initGrid();
                    }
                }
            });
            tabObj = $("#tab").ligerGetTabManager();
        }

        function initMainForm() {
            $mainform = $("#mainform");
            $mainform.ligerForm({
                inputWidth: 160,
                labelWidth: 120,
                space: 30,
                fields: [{
                    display: '任务名称',
                    id: "taskNm",
                    name: 'taskNm',
                    newline: false,
                    width: 140,
                    type: 'text',
                    attr: {
                        readonly: true
                    }
                }, {
                    display: '刷新类型',
                    id: "taskType",
                    name: 'taskType',
                    newline: false,
                    width: 140,
                    type: 'select',
                    options: {
                        url: "${ctx}/frs/frame/engineRefresh/taskTypeList.json",
                        readonly: true
                    }
                }, {
                    display: '任务状态',
                    id: "sts",
                    name: 'sts',
                    newline: false,
                    width: 140,
                    type: 'select',
                    options: {
                        url: "${ctx}/report/frame/enginelog/rpt/exeStsList.json",
                        readonly: true
                    }
                },/* {
                    display: '开始时间',
                    id: "startTime",
                    name: 'startTime',
                    newline: true,
                    width: 220,
                    type: 'date',
                    options: {
                        format: "yyyy-MM-dd hh:mm:ss",
                        readonly: true
                    }
                }, {
                    display: '结束时间',
                    id: "endTime",
                    name: 'endTime',
                    newline: true,
                    width: 220,
                    type: 'date',
                    options: {
                        format: "yyyy-MM-dd",
                        readonly: true
                    }
                }, */{
                    display: '运行日志',
                    name: 'runLog',
                    newline: true,
                    width: 600,
                    height: "260px",
                    type: 'textarea'
                }]
            });
            $("#runLog").attr("readonly","true");
        }

        function initData() {
            $.ajax({
                cache: false,//设置false,不缓存此页面
                async: true,
                url: "${ctx}/frs/frame/engineRefresh/getDetail",
                dataType: 'json',
                data: {
                    taskNo: "${taskNo}"
                },
                type: "post",
                success: function (result) {
                    $("#taskNm").val(result.baseData.taskNm);
                    $.ligerui.get("taskType").selectValue(result.baseData.taskType);
                    $.ligerui.get("sts").selectValue(result.baseData.sts);
                   /* $("#startTime").val(result.baseData.startTime);
                    $("#endTime").val(result.baseData.endTime);*/
                    $("#runLog").val(result.baseData.runLog);
                }

            });
        }

        function initGrid() {
            grid = $("#maingrid").ligerGrid({
                //toolbar : {},
                height: "380px",
                columns: [{
                    display: "刷新任务主键",
                    id: "taskNo",
                    name: "taskNo",
                    type: 'hidden',
                    frozen: true
                }, {
                    display: '任务名称',
                    name: 'taskNm',
                    width: "15%",
                    align: 'center',
                    render: function (row, index, val) {
                        return "<a style='color:blue' onclick='f_open_view(\"" + row.taskNo + "\")'>" + val + "</a>"
                    }
                }, {
                    display: '刷新类型',
                    name: 'taskType',
                    width: "15%",
                    align: 'center',
                    render: function (row) {
                        switch (row.taskType) {
                            case "RefreshNodeInfo":
                                return "节点刷新";
                            case "RefreshDSCache":
                                return "数据源刷新";
                            case "RefreshConfCache":
                                return "配置刷新";
                        }
                    }
                }, {
                    display: '任务状态',
                    name: 'sts',
                    width: "15%",
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
                    width: "15%",
                    align: 'center',
                    type: "date",
                    format: "yyyy-MM-dd hh:mm:ss"
                }, {
                    display: '结束时间',
                    name: 'endTime',
                    width: "15%",
                    align: 'center',
                    type: "date",
                    format: "yyyy-MM-dd hh:mm:ss"
                }, {
                    display: "母表编号",
                    name: "parentTaskId",
                    type: "hidden",
                    frozen: true
                }, {
                    display: '运行日志',
                    name: 'runLog',
                    width: "18%",
                    align: 'center',
                    type: "text"
                }],
                dataAction: 'server',
                usePager: true,
                alternatingRow: true,
                colDraggable: true,
                url: "${ctx}/frs/frame/engineRefresh/getEngRefrChild.json?parentTaskId=${taskNo}",
                sortName: 'startTime',
                sortOrder: 'desc',
                pageParmName: 'page',
                pagesizeParmName: 'pagesize',
                checkbox: false,
                rownumbers: true,
            });
        };

        //任务名称蓝字，可点开查看子任务刷新引擎详情
        function f_open_view(taskNoChild) {
            dialog = BIONE.commonOpenDialog("子任务引擎刷新状态",
                "EngineRefreshChildDetail", 800, 500,
                "${ctx}/frs/frame/engineRefresh/detailChildLog?taskNo="
                + taskNoChild, null);
        }

    </script>
</head>
<body>
<div id="template.center">
    <div id="tab" style="width: 100%; overflow: hidden;">
        <div tabid="basic" title="基本信息" lselected="true">
            <form id="mainform" action="" method="POST"></form>
        </div>
        <div tabid="child" title="子任务信息" lselected="false">
            <div class="content">
                <div id="maingrid" class="maingrid"></div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
