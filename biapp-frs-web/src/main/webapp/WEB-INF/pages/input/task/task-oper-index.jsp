<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <%@ include file="/common/meta.jsp" %>
    <meta name="decorator" content="/template/template31.jsp">
    <script type="text/javascript">
        var ROOT_NO = '0';
        //授权资源根节点图标
        var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png";
        //记录当前点击的授权对象id
        var selectedObjId = "";
        //初始化选中的对象
        var selectedObjs = '${selectedObjs}';
        var parentRowIndex = '${parentRowIndex}';
        var taskIndexType = '${taskIndexType}';
        var grid;
        var rows;

        var taskExeObjType = "1";

        $(function () {
            initTaskTypeList();
            initGrid();
            initSearchForm();
            initToolBar();
        });


        function initToolBar() {
            if (taskExeObjType == "1" && taskIndexType == "01") {
                var toolBars = [{
                    text: '填报',
                    click: f_report,
                    icon: 'icon-modify',
                    operNo: "input"
                }];
                BIONE.loadToolbar(grid, toolBars, function () {
                });
            } else if (taskExeObjType == "2" && taskIndexType == "01") {//填报已处理 - 查看 20190603
                var toolBars = [{
                    text: '查看',
                    click: f_report,
                    icon: 'icon-modify',
                    operNo: "input"
                }];
                BIONE.loadToolbar(grid, toolBars, function () {
                });
            } else if (taskExeObjType == "1" && taskIndexType != "01") {
                var toolBars = [{
                    text: '通过',
                    click: function () {
                        var rows = grid.getSelectedRows();
                        if (rows.length == 0) {
                            BIONE.tip("请选择一条任务进行" + this.text);
                            return;
                        }
                        $.ligerDialog.confirm("确定批量审批通过？", function (btn) {
                            if (btn) {
                                f_banchReport();
                            }
                        });
                    },
                    icon: 'icon-modify',
                    operNo: "banchApproval"
                }, {
                    text: '驳回',
                    click: onRebut,
                    icon: 'icon-modify',
                    operNo: "rebutTask"
                }];
                BIONE.loadToolbar(grid, toolBars, function () {
                });
            } else {
                var tlbmgr = grid.toolbarManager;
                tlbmgr.removeItem("report");
                tlbmgr.removeItem("banchApproval");//审批已处理去除 批量审批按钮 20190603
                var toolBars = [{
                    text: '驳回',
                    click: function () {
                        var rows = grid.getSelectedRows();
                        if (rows.length == 0) {
                            BIONE.tip("请选择任务进行" + this.text);
                            return;
                        }
                        $.ligerDialog.confirm("确定批量解锁吗？", function (btn) {
                            if (btn) {
                                batchOpenLock();
                            }
                        });
                    },
                    icon: 'icon-lock',
                    operNo: "batchOpenLock"
                }];
                BIONE.loadToolbar(grid, toolBars, function () {
                });
            }
        }

        //审批处-单个驳回，可选驳回节点
        function onRebut() {
            rows = grid.getSelectedRows();
            if (rows.length == 1) {
                $.ligerDialog.confirm("是否要驳回任务？", function (yes) {
                    if (yes) {
                        BIONE.commonOpenDialog("选择驳回节点", "tskNodesSel", 350, 180,
                            "${ctx}/rpt/input/taskoper/taskNodes/" + rows[0].taskInstanceId + "?flag=rebut");
                    }
                });
            } else {
                BIONE.tip("请选择一条任务进行驳回");
            }
        }

        function rebutIt(rebutNode) {
            $.ajax({
                cache: false,
                async: true,
                url: "${ctx}/rpt/input/taskoper/rebutTask",
                dataType: 'json',
                type: "post",
                data: {
                    "taskInstanceId": rows[0].taskInstanceId,
                    "taskNodeInstanceId": rows[0].taskNodeInstanceId,
                    "templateId": rows[0].templateId,
                    "rebutNode": rebutNode
                },
                beforeSend: function () {
                    BIONE.loading = true;
                    BIONE.showLoading("正在驳回中...");
                },
                complete: function () {
                    BIONE.loading = false;
                    BIONE.hideLoading();
                },
                success: function (result) {
                    BIONE.hideLoading();
                    if (!result) {
                        BIONE.tip('不能驳回,上一个节点为空');
                        return;
                    }
                    refreshGrid();
                    BIONE.tip('驳回成功');
                },
                error: function (result, b) {
                    BIONE.tip('驳回失败');
                }
            });
        }

        //批量解锁
        function batchOpenLock() {
            var rows = grid.getSelectedRows();
            if (rows.length == 0) {
                BIONE.tip("请选择一条任务进行" + this.text);
                return;
            }
            BIONE.showLoading("批量驳回中...");
            var banchSubArr = [];
            for (var i = 0; i < rows.length; i++) {
                banchSubArr.push({
                    'taskInstanceId': rows[i].taskInstanceId,
                    'taskNodeInstanceId': rows[i].taskNodeInstanceId,
                    'templateId': rows[i].templateId
                });
            }
            $.ajax({
                cache: false,
                url: "${ctx}/rpt/input/taskoper/batchOpenLock",
                dataType: 'json',
                type: "post",
                data: {
                    'openLockTasks': JSON2.stringify(banchSubArr)
                },
                success: function (result) {
                    BIONE.hideLoading();
                    if (result.flag != "true") {
                        BIONE.tip('驳回失败,请联系管理员');
                        return;
                    }
                    refreshGrid();
                    BIONE.tip('批量驳回成功!');
                },
                error: function (result, b) {
                    BIONE.hideLoading();
                    BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
                }
            });

        }

        function deleteChoose(i) {
            var row = grid.getRow(i);
            grid.deleteRow(row);
        }

        function refreshGrid() {
            grid.loadData();
        }

        var taskTypeList;

        function initTaskTypeList() {
            var leftObj = $("#left");
            taskTypeList = $("#taskTypeList").ligerListBox({
                isShowCheckBox: false,
                isMultiSelect: false,
                data: [{
                    text: '待处理的任务',
                    id: '1'
                }, {
                    text: '已处理的任务',
                    id: '2'
                }/*, {
				text : '已分发的任务',
				id : '3'
			} */],
                width: leftObj.width() - 1,
                height: leftObj.height() - 1,
                onSelected: function (data) {
                    taskExeObjType = data;
                    grid.setParm("taskExeObjType", data);
                    grid.setParm('newPage', 1);
                    grid.options.newPage=1;
                    grid.loadData();
                    initToolBar();
                }
            });
            taskTypeList.setValue("1");
        }

        // 加工图标
        function initIcon(nodes) {
            if (nodes && nodes instanceof Array === true) {
                for (var i = 0; i < nodes.length; i++) {
                    var r = nodes[i];
                    r.icon = "${ctx}" + (r.icon.indexOf("/") != 0 ? "/" : "")
                        + r.icon;
                    if (r.children && r.children != null) {
                        r.children = initIcon(r.children);
                    }
                }
            }
            return nodes;
        }

        function initSearchForm() {
            //搜索表单应用ligerui样式
            $("#search").ligerForm({
                fields: [{
                    display: '任务名称',
                    name: "taskName",
                    newline: false,
                    type: "text",
                    cssClass: "field",
                    attr: {
                        field: 'taskNm',
                        op: "like"
                    }
                }, {
                    display: '任务类型',
                    name: 'taskType',
                    newline: false,
                    type: "hidden",
                    comboboxName: "task_type_box",
                    cssClass: "field",
                    attr: {
                        field: 'taskType',
                        op: "="
                    },
                    options: {
                        url: "${ctx}/rpt/input/task/getTaskType.json"
                    }
                }, {
                    display : '数据日期',
                    name : 'dataDate',
                    newline : false,
                    type : 'date',
                    format : 'yyyy-MM-dd',
                    attr : {
                        field : 'dataDate',
                        op : "="
                    }
                }, /*{
                    display : '创建人',
                    name : 'deployUserNm',
                    newline : false,
                    type : 'text',
                    attr : {
                        field : 'deployUserNm',
                        op : "like"
                    }
                } , */{
                    display : '下发机构',
                    name : 'orgNoID',
                    newline : false,
                    type : 'select',
                    attr : {
                        field : 'orgNoID',
                        op : "="
                    },
                    options : {
                        onBeforeOpen: function() {
                            BIONE.commonOpenIconDialog('选择机构', 'orgComBoBox',
                                '${ctx}/bione/admin/orgtree/asyncOrgTree', 'orgNoID');
                        },
                        hideOnLoseFocus : true,
                        slide : false,
                        selectBoxHeight : 1,
                        selectBoxWidth : 1,
                        resize : false,
                        switchPageSizeApplyComboBox : false
                    }
                }/*,{
                    display : '查询类型',
                    name : 'queryType',
                    newline : false,
                    type : "select",
                    comboboxName : "queryType_box",
                    cssClass : "field",
                    attr : {
                        field : 'queryType',
                        op : "="
                    },
                    options : {
                        data : [ {
                            text : '所有的',
                            id : '0'
                        },{
                            text : '我下发的',
                            id : '1'
                        }]
                    }
                },{
				display : '任务状态',
				name : 'sts',
				newline : false,
				type : "select",
				comboboxName : "sts_box",
				cssClass : "field",
				attr : {
					field : 'sts',
					op : "="
				},
				options : {
					data : [{
						text : '--请选择--',
						id : ''
					}, {
						text : "流转中",
						id : "1"
					}, {
						text : "已完成",
						id : "2"
					} ]
				}
			}
			*/]
            });
            BIONE.addSearchButtons("#search", grid, "#searchbtn");
        }

        function onShowOperLog(taskInstanceId, taskNm) {
            var selectedItems = $("#taskTypeList").ligerListBox().getSelectedItems();
            var title = "任务[" + taskNm + "]处理信息";

            dialog = window.BIONE.commonOpenDialog(title,
                "operLogBox", $(document).width() - 50, $(document).height() - 30,
                "${ctx}/rpt/input/taskoper/initOperLog?taskInstanceId=" + encodeURI(encodeURI(taskInstanceId)), null);

        }

        function f_report() {
            var rows = grid.getSelectedRows();
            if (rows.length != 1) {
                BIONE.tip("请选择一条任务进行" + this.text);
                return;
            }
            onSelect(rows[0].taskInstanceId, rows[0].taskNodeInstanceId, rows[0].taskNm);
        }

        function f_banchReport() {
            var rows = grid.getSelectedRows();
            if (rows.length == 0) {
                BIONE.tip("请选择一条任务进行" + this.text);
                return;
            }
            BIONE.showLoading("批量审批中...");
            var banchSubArr = [];
            for (var i = 0; i < rows.length; i++) {
                banchSubArr.push({
                    'taskInstanceId': rows[i].taskInstanceId,
                    'taskNodeInstanceId': rows[i].taskNodeInstanceId,
                    'templateId': rows[i].templateId
                });
            }

            $.ajax({
                cache: false,
                url: "${ctx}/rpt/input/taskoper/banchSubmitTask",
                dataType: 'json',
                type: "post",
                data: {
                    'submitTasks': JSON2.stringify(banchSubArr)
                },
                success: function (result) {
                    BIONE.hideLoading();
                    if (result.flag != "true") {
                        BIONE.tip('提交错误,请联系管理员');
                        return;

                    }
                    refreshGrid();
                    BIONE.tip('批量提交成功!');
                },
                error: function (result, b) {
                    BIONE.hideLoading();
                    BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
                }
            });

        }


        function onSelect(taskInstanceId, taskNodeInstanceId, taskNm) {
            var selectedValue, title;
            var selectedItems = $("#taskTypeList").ligerListBox().getSelectedItems();
            if (selectedItems == null) {
                selectedValue = 1;
                title = "处理任务[" + taskNm + "]";
            } else {

                selectedValue = selectedItems[0].id;

                if (selectedValue == "1")
                    title = "处理任务[" + taskNm + "]";
                else
                    title = "查看任务[" + taskNm + "]";
            }
            var selectedValue = 1;
            var selectedItems = $("#taskTypeList").ligerListBox().getSelectedItems();
            if (selectedItems != null) {
                selectedValue = selectedItems[0].id;
            }
            dialog = window.BIONE.commonOpenDialog(title,
                "operTaskBox", $(document).width() - 50, $(document).height() - 30,
                "${ctx}/rpt/input/taskoper/initOperTask?taskInstanceId=" + encodeURI(encodeURI(taskInstanceId)) + "&taskNodeInstanceId=" + encodeURI(encodeURI(taskNodeInstanceId)) + "&showType=" + encodeURI(encodeURI(selectedValue)) + "&taskIndexType=" + taskIndexType + "&taskTypeList=" + selectedValue + "&d=" + new Date().getTime(), null);

        }

        function initGrid() {
            grid = $("#maingrid").ligerGrid({
                toolbar: {},
                columns: [
                    {
                        display: '模板id',
                        name: 'templateId',
                        align: 'center',
                        hide: true,
                        width: "1%"
                    }, {
                        display: '创建人',
                        name: 'creator',
                        align: 'center',
                        hide: true,
                        width: "1%"
                    }, {
                        display: '任务类型',
                        name: 'taskTypeNm',
                        align: 'center',
                        hide: "true",
                        width: "1%"
                    }, {
                        display: '补录类型',
                        name: 'taskExeObjTypeNm',
                        align: 'center',
                        hide: "true",
                        width: "1%"
                    }, {
                        display: '任务名称',
                        name: 'taskNm',
                        width: "20%",
                        align: 'left',
                        render: function (row) {
                            return "<a href='javascript:void(0)'   onclick='onSelect(\""
                                + row.taskInstanceId + "\",\"" + row.taskNodeInstanceId + "\",\"" + row.taskNm + "\")'>" + row.taskNm + "</a>";
                        }
                    }, {
                        display: '标题',
                        name: 'taskTitle',
                        width: "20%",
                        align: 'left'
                    }, {
                        display: '填报机构',
                        name: 'orgName',
                        width: "10%",
                        align: 'left'
                    },/* {
                        display: '填报机构号',
                        name: 'orgNo',
                        width: "8%",
                        align: 'center'
                    },*/ {
                        display: '下发机构',
                        name: 'deployOrg',
                        width: "8%",
                        align: 'center'
                    }, {
                        display: '数据日期',
                        name: 'dataDate',
                        width: "8%",
                        align: 'creator'
                    }, {
                        display: '任务状态',
                        name: 'sts',
                        width: "8%",
                        align: 'center',
                        render: function (rowData, rowNum, rowValue) {
                            if (rowValue == "1") {
                                return "流转中"
                            }
                            if (rowValue == "2") {
                                return "已完成";
                            }
                        }
                    },
                    {
                        display: '下发时间',
                        name: 'startTime',
                        width: "15%",
                        align: 'center',
                        render: function (row) {
                            return row.startTime.substring(0,19);
                        }
                    }, {
                        display: '操作',
                        name: 'taskInstanceId',
                        width: "10%",
                        align: 'center',
                        render: function (row) {
                            return "<a href='javascript:void(0)'   onclick='onShowOperLog(\""
                                + row.taskInstanceId + "\",\"" + row.taskNm + "\")'>" + "任务处理信息" + "</a>";
                        }
                    }],
                checkbox : true,
                isScroll : true,
                alternatingRow : true,//附加奇偶行效果行
                colDraggable : true,
                dataAction: 'server', //从后台获取数据
                usePager: true, //服务器分页
                url: "${ctx}/rpt/input/taskoper/getTaskList.json?taskIndexType=" + taskIndexType + "&d=" + new Date().getTime(),
                sortName: 'start_time',//第一次默认排序的字段
                sortOrder: 'desc', //排序的方式
                delayLoad: true,
                pageParmName: 'page',
                pagesizeParmName: 'pagesize',
                rownumbers: true,
                width: '100%',
                height: '99%'
            });
            grid.setParm("taskExeObjType", "1");
            grid.loadData();
        }
    </script>

    <title>指标信息</title>
</head>
<body>
<div id="template.left.up">查看任务</div>
<div id="template.left.up.icon">
    <img src="${ctx}/images/classics/icons/application_side_tree.png"/>
</div>
</body>
</html>
