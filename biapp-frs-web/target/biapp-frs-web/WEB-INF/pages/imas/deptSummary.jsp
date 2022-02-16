<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
    <meta name="decorator" content="/template/template1_BS.jsp">
    <script type="text/javascript">
        var grid, btns, url, ids = [], user = [],searchForm1;
        var rId;

        //刷新方法
        function reload() {
            var qwer=grid.getData();
            grid.reload();
        }

        //初始化函数
        $(function() {
            url = "${ctx}/imas/deptsummary/list.json";
            initGrid();
            searchForm();
            f_set();
            //initButtons();
            BIONE.addSearchButtons("#search", grid, "#searchbtn");

            BIONE.validate($("#formsearch"));
            if($("#formsearch").valid()){//edit by fangjuan 20150707
                var rule = BIONE.bulidFilterGroup($("#search"));
                /**
                 * edit in 2014-08-14 by caiqy
                 */
                if (rule.rules.length) {
                    grid.setParm("condition",JSON2.stringify(rule));
                    grid.setParm("newPage",1);
                    grid.options.newPage=1
                } else {
                    grid.setParm("condition","");
                    grid.setParm('newPage', 1);
                    grid.options.newPage=1
                }
                grid.loadData();
            }
        });

        //搜索表单应用ligerui样式
        function searchForm() {
            searchForm1=$("#search").ligerForm({
                inputWidth: 170, labelWidth: 150, space: 40,validate : true,
                fields : [{
                    display : "数据日期",
                    name : "dataDT",
                    newline : false,
                    width : 150,
                    type : "date",
                    cssClass : "field",
                    validate:{
                        required : true
                    },
                    options : {
                        format: 'yyyy-MM-dd',
                        showTime : true
                    },
                    attr : {
                        op : "=",
                        field : "data_dt"
                    }
                }, {
                    display : "报送机构号",
                    name : "rptOrgNo",
                    newline : false,
                    width : 200,
                    selectBoxHeight: 300,
                    type : "select",
                    cssClass : "field",
                    validate:{
                        required : false
                    },
                    options : {
                        //向后台发送的请求路径
                        url: "${ctx}/imas/deptsummary/rptOrgNoList",
                    },
                    attr : {
                        op : "=",
                        field : "rpt_org_no"
                    }
                } ]
            });
        }

        function f_set() {
            searchForm1.setData({
                dataDT : new Date()
            });

        }

        //初始化grid
        function initGrid() {
            grid = $("#maingrid").ligerGrid(
                {
                    width : '100%',
                    height : '99%',
                    columns : [
                        {
                            display : '报送机构号',
                            name : 'rptOrgNo',
                            align : 'center',
                            width : '200',
                        },
                        { display: '数据加工', columns:
                                [
                                    { display: '总数据量', name: 'totalCount', align: 'center', width: 80},
                                    { display: '修改总数据量', name: 'updateCount', align: 'center', width: 80},
                                ]
                        },
                        { display: '数据校验', columns:
                                [
                                    { display: '执行规则', name: 'execRuleNum', align: 'center', width: 80},
                                    { display: '触发规则', name: 'triggerRule', align: 'center', width: 80},
                                    { display: '首次校验可疑数据量', name: 'firstCheckNum', align: 'center', width: 120},
                                ]
                        },
                        { display: '补录', columns:
                                [
                                    { display: '待处理任务', name: 'toAuditTask', align: 'center', width: 80},
                                    { display: '待处理数据', name: 'distributedNum', align: 'center', width: 80},
                                    { display: '已处理数据', name: 'addDataNum', align: 'center', width: 80}
                                ]
                        },
                        { display: '上报', columns:
                                [
                                    { display: '数据表', name: 'uploadTable', align: 'center', width: 80},
                                    { display: '报文数据量', name: 'msgDataNum', align: 'center', width: 80},
                                    { display: '正确回执', name: 'receipt', align: 'center', width: 80},
                                    { display: '错误回执', name: 'errorReceipt', align: 'center', width: 80},
                                    { display: '强制提交', name: 'pbcForceSub', align: 'center', width: 80},
                                ]
                        }],
                    delayLoad : true,
                    checkbox : true,
                    usePager : true,
                    isScroll : true,
                    rownumbers : true,
                    alternatingRow : true,//附加奇偶行效果行
                    colDraggable : true,
                    dataAction : 'server',//从后台获取数据
                    method : 'post',
                    url : url,
                    sortName : 'tabNameEn', //第一次默认排序的字段
                    sortOrder : 'asc',
                    toolbar : {} ,
                    onAfterShowData : function(){

                    }
                },[]);

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
            BIONE.loadToolbar(grid, btns, function() { });
        }

    </script>
</head>
<body>
</body>
</html>