<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <meta name="decorator" content="/template/template1_BS.jsp">
    <script type="text/javascript">
        var grid, btns, url, ids = [], user = [], searchForm1;
        var rId;

        //刷新方法
        function reload() {
            var qwer = grid.getData();
            grid.reload();
        }

        //初始化函数
        $(function () {
            url = "${ctx}/bfd/datasummary/list.json";
            initGrid();
            searchForm(rptOrgNo);
            var rptOrgNo = f_set();

            //initButtons();
            BIONE.addSearchButtons("#search", grid, "#searchbtn");

            BIONE.validate($("#formsearch"));
            if ($("#formsearch").valid()) {//edit by fangjuan 20150707
                var rule = BIONE.bulidFilterGroup($("#search"));
                /**
                 * edit in 2014-08-14 by caiqy
                 */
                if (rule.rules.length) {
                    grid.setParm("condition", JSON2.stringify(rule));
                    grid.setParm("newPage", 1);
                    grid.options.newPage = 1
                } else {
                    grid.setParm("condition", "");
                    grid.setParm('newPage', 1);
                    grid.options.newPage = 1
                }
                grid.loadData();
            }
        });

        //搜索表单应用ligerui样式
        function searchForm(rptOrgNo) {
            searchForm1 = $("#search").ligerForm({
                inputWidth: 170, labelWidth: 150, space: 40, validate: true,
                fields: [{
                    display: "表名",
                    width: '180',
                    name: "tabName",
                    newline: false,
                    type: "select",
                    cssClass: "field",
                    options: {
                        //向后台发送的请求路径
                        url: "${ctx}/bfd/datasummary/getTabName",
                    },
                    attr: {
                        op: "like",
                        field: "tabName"
                    }
                }, {
                    display: "数据日期",
                    name: "dataDT",
                    newline: false,
                    width: 150,
                    type: "date",
                    cssClass: "field",
                    validate: {
                        required: true
                    },
                    options: {
                        format: 'yyyy-MM-dd',
                        showTime: true
                    },
                    attr: {
                        op: "=",
                        field: "dataDT"
                    }
                }, {
                    display: "报送机构号",
                    name: "rptOrgNo",
                    newline: false,
                    selectBoxHeight: 300,
                    type: "select",
    				width : '140',
    				cssClass : "field",
    				labelWidth : '90',
                    cssClass : "field",
                    comboboxName: "rptOrgNo",
                    validate: {
                        required: true
                    },
                    attr: {
                        op: "in",
                        field: "rptOrgNo"
                    },
                    options: {
                    	onBeforeOpen : function() {
                    		var height = $(window).height() - 120;
							var width = $(window).width() - 480;
							window.BIONE .commonOpenDialog(
									"报送机构树", "taskOrgWin", width, height,
									"${ctx}/bfd/datasummary/searchOrgSetInfo?orgType=BFD",
									null);
							return false;
    					},
    					cancelable : true
                    }
                }]
            });
        }

        function f_set() {
            var rptOrgNo;

            $.ajax({
                async: false,
                type: "POST",
                dataType: "JSON",
                url: "${ctx}/bfd/datasummary/getRptOrgNo",

                success: function (data) {
                    rptOrgNo = data.rptOrgNo;
                },
                error: function (result) {
                    BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
                }
            });
            searchForm1.setData({
                dataDT: new Date(),
                rptOrgNo: rptOrgNo
            });

            return rptOrgNo;
        }

        //初始化grid
        function initGrid() {
            grid = $("#maingrid").ligerGrid(
                {
                    width: '100%',
                    height: '99%',
                    columns: [
                        {
                            display: '报送机构号',
                            name: 'rptOrgNo',
                            align: 'center',
                            width: '200',
                        },
                        {
                            display: '数据表',
                            name: 'tabName',
                            align: 'left',
                            width: '200',
                            sortname: "enterAssocia"
                        },
                        {
                            display: '数据加工', columns:
                                [
                                    {display: '自动抽取', name: 'etlCount', align: 'center', width: 80},
                                    {display: '人工录入', name: 'entryCount', align: 'center', width: 80},
                                    {display: '总数据量', name: 'totalCount', align: 'center', width: 80},
                                    {display: '修改总数据量', name: 'updateCount', align: 'center', width: 80},
                                ]
                        },{
                            display: '数据变化量', columns:
                                [
                                    {
                                        display: '同比（%）', name: 'ringRatio', align: 'center', width: 80,
                                        render: function (rowData) {
                                            return getFormatNumber(rowData.ringRatio);
                                        }
                                    },
                                    {
                                        display: '环比（%）', name: 'yearOnYear', align: 'center', width: 80,
                                        render: function (rowData) {
                                            return getFormatNumber(rowData.yearOnYear);
                                        }
                                    },
                                ]
                        },
                        {
                            display: '数据校验', columns:
                                [
                                    // {display: '校验次数', name: 'checkTimes', align: 'center', width: 80},
                                    // {display: '执行规则', name: 'execRuleNum', align: 'center', width: 80},
                                    {display: '触发规则', name: 'triggerRule', align: 'center', width: 80},
                                    // {display: '首次校验可疑数据量', name: 'firstCheckNum', align: 'center', width: 120},
                                    {display: '最新校验可疑数据量', name: 'latestCheckNum', align: 'center', width: 120},
                                ]
                        },
                        {
                            display: '数据下发', columns:
                                [
                                    {display: '下发数据量', name: 'distributedNum', align: 'center', width: 80},
                                ]
                        },
                        {
                            display: '补录', columns:
                                [
                                    {display: '补录任务数', name: 'addTastNum', align: 'center', width: 80},
                                    {display: '补录数据', name: 'addDataNum', align: 'center', width: 80},
                                    {display: '强制保存', name: 'forcePreserve', align: 'center', width: 80},
                                    {display: '补录强制提交', name: 'addForceSubNum', align: 'center', width: 80},
                                ]
                        },
                        {
                            display: '审核', columns:
                                [
                                    {display: '审核', name: 'examine', align: 'center', width: 80},
                                ]
                        },
                        {
                            display: '数据状态', columns:
                                [
                                    {
                                        display: '锁定', name: 'lockStatus', align: 'center', width: 80,
                                        render: function (rowData) {
                                            if (rowData.lockStatus == "锁定") {
                                                return "已锁定";
                                            } else {
                                                return "未锁定";
                                            }
                                        }
                                    },
                                ]
                        }],
                    delayLoad: true,
                    checkbox: false,
                    usePager: true,
                    isScroll: true,
                    rownumbers: true,
                    alternatingRow: true,//附加奇偶行效果行
                    colDraggable: true,
                    dataAction: 'server',//从后台获取数据
                    method: 'post',
                    url: url,
                    sortName: 'tabNameEn', //第一次默认排序的字段
                    sortOrder: 'asc',
                    toolbar: {},
                    onAfterShowData: function () {

                    }
                }, []);

        }


        //初始化按钮
        function initButtons() {
            btns = [
                /*动态维护功能按钮*/
                // {
                // 	text : '增加',
                // 	click : user_add_dynamic,
                // 	icon : 'fa-plus',
                // 	operNo : 'user_add'
                // }, {
                // 	text : '修改',
                // 	click : user_modify_dynamic,
                // 	icon : 'fa-pencil-square-o',
                // 	operNo : 'user_modify'
                // },{
                //     text : "企业关联信息导入导出",
                //     icon : "fa-pencil-square-o",
                //     operNo : "excel_modify",
                //     menu : {
                //         items : [ {
                //             icon : 'import',
                //             text : '导入',
                //             click : user_upload
                //         }, {
                //             icon : 'export',
                //             text : '导出',
                //             click : user_download
                //         } ]
                //     }
                /* },{
                     text : '用户解锁',
                     click : user_unlock,
                     icon : 'lock',
                     operNo : 'user_unlock'
                 } */];
            BIONE.loadToolbar(grid, btns, function () {
            });
        }

        function getFormatNumber(val) {
            var n = 0;

            var s = val * 100;

            s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";

            var l = s.split(".")[0].split("").reverse(), r = s.split(".")[1];

            var t = "";

            for (var i = 0; i < l.length; i++) {
                 t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");

            }

            return t.split('').reverse().join('');

        }

    </script>
</head>
<body>
</body>
</html>