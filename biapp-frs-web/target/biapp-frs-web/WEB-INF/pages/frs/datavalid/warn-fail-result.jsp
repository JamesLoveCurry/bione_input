<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="/common/taglibs.jsp" %>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
    <!-- 基础的JS和CSS文件 -->
    <script type="text/javascript">
        var grid, ids;
        var orgType = '17';
        var download;

        $(function () {
            initSearchForm();
            initGrid();
            initToolBar();
            BIONE.addSearchButtons("#search", grid, "#searchbtn");
            downdload = $('<iframe id="download"  style="display: none;"/>');
            $('body').append(downdload);
        });

        function initSearchForm() {
            $("#search").ligerForm({
                fields: [{
                    display: "数据日期",
                    name: "dataDate",
                    comboboxName: 'dataDateBox',
                    newline: true,
                    type: "date",
                    width: '110%',
                    cssClass: "field",
                    labelWidth: '90',
                    attr: {op: "=", field: "c.data_date"},
                    options: {format: "yyyyMMdd"}
                }, {
                    display: "机构名称",
                    name: "orgNo",
                    newline: false,
                    type: "select",
                    cssClass: "field",
                    labelWidth: '90',
                    comboboxName: "orgNm_sel",
                    attr: {
                        op: "in",
                        field: "c.orgNo"
                    },
                    options: {
                        onBeforeOpen: function () {
                            var height = $(window).height() - 120;
                            var width = $(window).width() - 480;
                            window.BIONE.commonOpenDialog(
                                "机构树",
                                "taskOrgWin",
                                width,
                                height,
                                "${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo?orgType=" + orgType,
                                null);
                            return false;
                        },
                        cancelable: true
                    }
                }, {
                    display: '报表名称',
                    name: 'rptIdx',
                    comboboxName: 'rptIdxBox',
                    type: 'select',
                    newline: false,
                    attr: {
                        op: "in",
                        field: "a.rptId"
                    },
                    options: {
                        onBeforeOpen: function () {
                            BIONE.commonOpenDialog(
                                "报表选择",
                                "rptIdxTreeWin",
                                480,
                                380,
                                '${ctx}/report/frame/design/cfg/rptIdxTreeWin?busiType=' + orgType,
                                null);
                            return false;
                        }
                    }
                }
                ]
            });
        };

        function initGrid() {
            grid = $("#maingrid").ligerGrid({
                toolbar: {},
                checkbox: false,
                columns: [
                    // {
                    //     display: '序号',
                    //     name: 'serialNum'
                    // },
                    {
                        display: '行',
                        name: 'rowId',
                        width: "5%",
                        align: 'center',
                    }, {
                        display: '列',
                        name: 'colId',
                        width: "5%",
                        align: 'center',
                    }, {
                        display: '机构',
                        name: 'org',
                        width: "10%",
                        align: 'center'
                    }, {
                        display: '数据日期',
                        name: 'dataDate',
                        width: "10%",
                        align: 'center'
                    }, {
                        display: '报表ID',
                        name: 'rptNum',
                        width: "10%",
                        align: 'center'
                    }, {
                        display: '报表名称',
                        name: 'rptNm',
                        width: "10%",
                        align: 'center'
                    }, {
                        display: '币种',
                        name: 'currency',
                        width: "10%",
                        align: 'center'
                    }, {
                        display: '指标编号',
                        name: 'indexNo',
                        width: "15%",
                        align: 'center'
                    }, {
                        display: '指标名称',
                        name: 'indexNm',
                        width: "10%",
                        align: 'center'
                    }, {
                        display: '校验类型',
                        name: 'verifyType',
                        width: "15%",
                        align: 'center'
                    }, {
                        display: '校验公式',
                        name: 'verifyFomula',
                        width: "15%",
                        align: 'center'
                    }, {
                        display: '本期值',
                        name: 'currVal',
                        width: "10%",
                        align: 'center'
                    }, {
                        display: '校验值',
                        name: 'verifyVal',
                        width: "10%",
                        align: 'center'
                    }, {
                        display: '差值',
                        name: 'diffVal',
                        width: "10%",
                        align: 'center'
                    }, {
                        display: '校验结果',
                        name: 'isPass',
                        width: "10%",
                        align: 'center'
                    }, {
                        display: '说明内容',
                        name: 'validDesc',
                        width: "10%",
                        align: 'center'
                    }, {
                        display: '公式解释',
                        name: 'fomulaExp',
                        width: "10%",
                        align: 'center'
                    }, {
                        display: '校验详情',
                        name: 'verifyDetail',
                        width: "15%",
                        align: 'center'
                    }],
                dataAction: 'server', //从后台获取数据
                usePager: true, //服务器分页
                alternatingRow: true, //附加奇偶行效果行
                colDraggable: true,
                rownumbers: true,
                url: "${ctx}/frs/verificationWarning/getWarnFailList.json",
                sortName: 'rowId,colId',//第一次默认排序的字段
                sortOrder: 'desc', //排序的方式
                rownumbers: true,
                width: '100%',
                height: '99%',
                isScroll: true
            });
            grid.setHeight($("#center").height() - 115);
        }

        function initToolBar() {
            var btns = [];
            btns = [{
                text: '列表全部导出',
                click: f_export,
                icon: 'fa-download'
            }];
            BIONE.loadToolbar(grid, btns, function () {
            });
        }

        //预警校验结果导出
        function f_export() {
            $.ajax({
                type: "POST",
                dataType: "json",
                url: "${ctx}/frs/verificationWarning/exportWarnFailList",
                beforeSend: function (a, b, c) {
                    BIONE.showLoading('正在导出数据中...');
                },
                success: function (data) {
                    BIONE.hideLoading();
                    if (data.filename == "") {
                        BIONE.tip('导出异常');
                        return;
                    }
                    downdload.attr('src', "${ctx}/frs/verificationWarning/download?fileName=" + data.filename);
                },
                error: function (result) {
                    BIONE.hideLoading();
                }
            });
        }

    </script>
</head>
<body>
</body>
</html>