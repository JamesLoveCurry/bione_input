<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <meta name="decorator" content="/template/template5.jsp">
    <script type="text/javascript">
        //全局变量
        var mainform = '';
        var grid;
        var urlIsRunningPath;
        $(function () {
            $("#search").ligerForm({
                fields: [{
                    display: "任务组编号 ",
                    name: "taskNo",
                    newline: true,
                    type: "text",
                    cssClass: "field",
                    attr: {
                        field: "taskNo",
                        op: "like"
                    }
                }, {
                    display: "任务组名称 ",
                    name: "taskNm",
                    newline: false,
                    type: "text",
                    cssClass: "field",
                    attr: {
                        field: "taskNm",
                        op: "like"
                    }
                },{
                    display: "任务组类型",
                    name:"objType",
                    newline: false,
                    type:"select",
                    width:100,
                    options:{
                        data:[
                            {
                                id:"01",
                                text:"报表组"
                            },{
                                id:"03",
                                text: "指标组"
                            }
                        ],
                     cancelable:true
                    },
                    attr:{
                        field: "objType",
                        op:"="
                    }
                }]
            });
            initGrid();
            BIONE.addSearchButtons("#search", grid, "#searchbtn");
            initMainForm();
            $("#maingrid").height($("#center").height() - $("#mainsearch").height() - $("#mainform").height() - 20);
            var btns = [{
                text: "取消",
                onclick: function () {
                    BIONE.closeDialog("taskRunDataNew");
                }
            }, {
                text: "运行",
                onclick: f_save
            }];
            BIONE.addFormButtons(btns);
        });

        function initMainForm() {
            //渲染表单
            $("#mainform").ligerForm({
                inputWidth: 160,
                labelWidth: 120,
                space: 30,
                fields: [{
                    display: '数据日期',
                    name: 'dataDate',
                    newline: false,
                    width: 220,
                    type: 'date',
                    options: {
                        format: "yyyyMMdd"
                    },
                    validate: {
                        required: true
                    }
                }]
            });
        }

        function f_save() {
            var dataDate = $("#mainform  input[name='dataDate']").val();
            if (dataDate == '') {
                BIONE.tip("数据日期不能为空！");
                return;
            }
            var rowSelected = grid.getSelecteds();
            if (rowSelected.length == 0) {
                BIONE.tip("请选择一条任务组进行跑数!");
                return;
            } else if (rowSelected.length > 1) {
                BIONE.tip("只能选择一条任务组进行跑数!");
                return;
            }
            parent.changeRefreshSts();
            $.ajax({
                async: false,//同步
                type: "POST",
                url: "${ctx}/report/frame/taskrundata/create.json",
                dataType: 'json',
                data: {
                    taskNo: rowSelected[0].taskNo,
                    taskType: rowSelected[0].objType,
                    dataDate: dataDate
                },
                success: function (rs) {
                    BIONE.closeDialogAndReloadParent("taskRunDataNew", "maingrid", rs.msg);
                    /*window.parent.grid.loadData();
                    parent.quickRefresh();//指令发送完成后立即执行自动刷新*/
                },
                beforeSend: function () {
                    BIONE.loading = true;
                    BIONE.showLoading("正在操作中...");
                },
                complete: function () {
                    BIONE.loading = false;
                    BIONE.hideLoading();
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    BIONE.tip('操作失败,错误信息:' + textStatus);
                }
            });
        }

        //初始化表格
        function initGrid() {
            grid = $("#maingrid").ligerGrid({
                columns: [{
                    display: '任务组编号',
                    name: 'taskNo',
                    width: '30%'
                }, {
                    display: '任务组名称',
                    name: 'taskNm',
                    width: '30%'
                }, {
                    display: '任务组类型 ',
                    name: 'objType',
                    width: '20%',
                    render: function (a, b, c) {
                        if (c == "01")
                            return "报表组";
                        if (c == "03")
                            return "指标组";
                    }
                }],
                checkbox: false,
                isScroll: true,
                rownumbers: true,
                dataAction: 'server',//从后台获取数据
                method: 'post',
                url: "${ctx}/report/frame/taskobjrel/list.json",
                usePager: true, //服务器分页
                alternatingRow: true, //附加奇偶行效果行
                colDraggable: true,
                pageParmName: 'page',
                pagesizeParmName: 'pagesize',
            });
        }
    </script>
    <title></title>
</head>
<body>
<div id="template.center">
    <div id="mainsearch" style="width:99%">
        <div id="searchbox" class="searchbox">
            <form id="formsearch">
                <div id="search"></div>
                <div class="l-clear"></div>
            </form>
            <div id="searchbtn" class="searchbtn"></div>
        </div>

    </div>
    <div id="maingrid"
         style="width: 99%; overflow: auto; clear: both; background-color: #FFFFFF;">
    </div>
    <form id="mainform"
          action=""
          method="POST">
    </form>
</div>
</body>
</html>