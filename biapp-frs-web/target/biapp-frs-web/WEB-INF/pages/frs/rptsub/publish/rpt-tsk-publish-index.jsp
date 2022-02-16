<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<meta name="decorator" content="/template/template1_BS.jsp">
	<script type="text/javascript">
        var grid;
        var moduleType= '${moduleType}';
		var busiTypeMap=new Map();
        $(function() {
        	initBusiType();
            initForm();
            initGrid();
            initButtons();
        });

        //初始化form
        function initForm() {
            $("#search").ligerForm({
                fields : [{
                    display : "任务名称",
                    name : "taskNm",
                    comboboxName : 'taskNmBox',
                    newline : false,
                    type : "select",
                    width : '140',
                    options : {
                        onBeforeOpen : function() {
                            $.ajax({
                                cache : false,
                                async : false,
                                url : '${ctx}/frs/rpttsk/publish/getTaskBox',
                                dataType : 'json',
                                type : "get",
                                data : {
                                    moduleType : moduleType
                                },
                                success : function(result) {
                                    if(result.tasklist){
                                        $.ligerui.get('taskNmBox').setData (result.tasklist);
                                    }
                                }
                            });
                        }
                    },
                    attr : {
                        op : "=",
                        field : "taskNm"
                    }
                } ,  {
                    display : "状态",
                    name : "taskSts",
                    comboboxName : 'taskStsBox',
                    newline : false,
                    type : "select",
                    width : "140",
                    options : {
                        data : [  {
                            text : "--请选择--",
                            id : ""
                        },{
                            text : '启用',
                            id : "1"
                        },  {
                            text : '停用',
                            id : '0'
                        }]
                    },
                    attr : {
                        op : "=",
                        field : "taskSts"
                    }
                } ]
            });
        }

        //初始化grid
        function initGrid() {
            grid = $("#maingrid").ligerGrid({
                width : '100%',
                height : '99%',
                columns : [ {
                    display : '任务名称',
                    name : 'taskNm',
                    width : '40%',
                    align : 'left'
                },{
                    display : '任务类型',
                    name : 'taskType',
                    width : '20%',
                    align : 'center',
					render:function (rowdata,index,value){
						return busiTypeMap.get(value);
					}
                }, {
                    display : '频度',
                    name : 'taskFreq',
                    width : '10%',
                    align : 'center',
                    render :function(rowdata, index, value){
                        if(value=="01"){
                            return "日";
                        }else if(value=="02"){
                            return "周";
                        }else if(value=="03"){
                            return "旬";
                        }else if(value=="04"){
                            return "月";
                        }else if(value=="05"){
                            return "季";
                        }else if(value=="06"){
                            return "半年";
                        }else if(value=="07"){
                            return "年";
                        }else{
                            return "未知频度";
                        }
                    }
                }, {
                    display : '状态',
                    name : 'taskSts',
                    width : '20%',
                    align : 'center',
                    render : qybzRenderSts
                } ],
                checkbox : true,
                rownumbers : true,
                isScroll : true,
                alternatingRow : true,//附加奇偶行效果行
                colDraggable : false,
                dataAction : 'server',//从后台获取数据
                method : 'post',
                url : '${ctx}/frs/rpttsk/publish/getGridTask?moduleType=' + moduleType,
                sortName : 'taskNm',//第一次默认排序的字段
                sortOrder : 'asc', //排序的方式
                toolbar : {}
            });
        }

        //初始化按钮
        function initButtons() {
            var btns = [ {
                text : '新增',
                click : create,
                icon : 'fa-plus'
            }, {
                text : '修改',
                click : edit,
                icon : 'fa-pencil-square-o'
            }, {
                text : '删除',
                click : deleteBatch,
                icon : 'fa-trash-o'
            }, {
                text : '停用/启用',
                click : isStop,
                icon : 'fa-power-off'
            }, {
                text : '临时下发',
                click : publishTsk,
                icon : 'fa-code-fork'
            },{
                text : '查看',
                click : taskquery,
                icon : 'fa-book'
            }];
            BIONE.loadToolbar(grid, btns, function() {});
            BIONE.addSearchButtons("#search", grid, "#searchbtn");
        }

        //新增任务
        function create() {
            BIONE.commonOpenDialog("新建任务", "addNewTskWin", 960, $("#center").height()-30, "${ctx}/frs/rpttsk/publish/addtask");
        }

        //修改任务
        function edit() {
            var rows = grid.getSelectedRows();
            if (rows.length < 1) {
                BIONE.tip('请选择记录');
            } else if (rows.length > 1) {
                BIONE.tip('只能选择一条记录');
            } else {
                var taskId = rows[0].taskId;
                BIONE.commonOpenDialog("修改任务", "addNewTskWin", 960, $("#center").height()-30, '${ctx}/frs/rpttsk/publish/edittask?taskId='+taskId);
            }
        }

        //查看任务
        function taskquery() {
            var rows = grid.getSelectedRows();
            if (rows.length < 1) {
                BIONE.tip('请选择记录');
            } else if (rows.length > 1) {
                BIONE.tip('只能选择一条记录');
            } else {
                var taskId = rows[0].taskId;
                BIONE.commonOpenDialog("查看任务", "addNewTskWin", 960, $("#center").height()-30, '${ctx}/frs/rpttsk/publish/taskquery?taskId='+taskId);
            }
        }

        //临时下发任务
        function publishTsk() {
            var rows = grid.getSelectedRows();
            if (rows.length < 1) {
                BIONE.tip('请选择记录');
            } else if (rows.length > 1) {
                BIONE.tip('只能选择一条记录');
            } else {
                var taskSts = rows[0].taskSts;
                if(taskSts=="1"){
                    var taskId = rows[0].taskId;
                    BIONE.commonOpenDialog("临时下发", "publishTskWin", 900, $("#center").height()-30, '${ctx}/frs/rpttsk/publish/showPublishTsk?taskId='+taskId);
                }else{
                    BIONE.tip("停用的任务不允许下发！");
                    return ;
                }

            }
        }

        //批量删除任务
        function deleteBatch() {
            var rows = grid.getSelectedRows();
            if(rows.length>0){
                // 物理表删除
                $.ligerDialog.confirm('您确定删除这' + rows.length + "条任务和任务相关信息吗？", function(yes) {
                    var tskIds = [];
                    for(var i =0;i<rows.length;i++){
                        var rowData = rows[i];
                        tskIds.push(rowData.taskId);
                    }
                    if (yes) {
                        $.ajax({
                            async : false,
                            type : "get",
                            dataType : "json",
                            url : '${ctx}/frs/rpttsk/publish/deleteTsk?tskIds=' + tskIds.join(','),
                            success : function(result) {
                                if(result && result.deleteNo == "ok"){
                                    BIONE.tip('删除成功');
                                    grid.loadData();
                                }
                            },
                            error : function(result, b) {
                                BIONE.tip('删除错误 <BR>错误码：' + result.status);
                            }
                        });

                    }
                });
            }else{
                BIONE.tip('请至少选择一条记录');
            }
        }

        // 停用/启用
        function isStop(){
            var rows = grid.getSelectedRows();
            if(rows.length>0){
                var tskIds = [];
                for(var i =0;i<rows.length;i++){
                    var rowData = rows[i];
                    tskIds.push(rowData.taskId);
                }
                $.ajax({
                    async : false,
                    type : "get",
                    dataType : "json",
                    url : '${ctx}/frs/rpttsk/publish/isStop?tskIds=' + tskIds.join(','),
                    success : function(result) {
                        if(result && result.updateNo == "ok"){
                            grid.loadData();
                        }
                    },error : function(result, b) {
                        BIONE.tip('错误 <BR>错误码：' + result.status);
                    }
                });
            }else{
                BIONE.tip('请至少选择一条记录');
            }
        }

        // 状态显示,停/启用等
        function qybzRenderSts(rowdata, index, value) {
            if (value == "1") {
                return "启用";
            } else if(value == "0"){
                return "停用";
            }else{
                return "未知状态";
            }
        }

		//加载业务类型
		function initBusiType(){
			$.ajax({
				async :false,
				type : "POST",
				url : "${ctx}/report/frame/datashow/idx/busiTypeList.json",
				success: function(data) {
					if(data){
						for(var i = 0; i < data.length ; i++){
							busiTypeMap.set(data[i].id, data[i].text);
						}
					}
				},
				error: function() {
					top.BIONE.tip('加载失败');
				}
			});
		}

	</script>
</head>
<body>
</body>
</html>