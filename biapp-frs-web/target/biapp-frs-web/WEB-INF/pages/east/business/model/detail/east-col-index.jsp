<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template8.jsp">
<script type="text/javascript">
	var dialog;
	var grid;
	var tabName;

    $(function() {
        initGrid();
        initButtons();
    });
    
	function initGrid() {
		tabName = "${tabName}";
		grid = $("#maingrid").ligerGrid({
		columns : [ {
			display : '表名',
			name : 'tabName',
			width : '20%',
			align : 'center'
		},{
			display : '字段名',
			name : 'colName',
			width : '20%',
			align : 'center'
		},{
			display : '字段名（英文）',
			name : 'colNameEn',
			width : '20%',
			align : 'center'
		},{
			display:'是否是主键',
			name:'isPk',
			width : '10%',
			align : 'center'
		},{
			display:'过滤序号',
			name:'filterNo',
			width : '10%',
			align : 'center'
		},{
			display:'流程序号',
			name:'flowNo',
			width : '10%',
			align : 'center'
		}],
		checkbox : true,
		rownumbers : true,
		isScroll : false,
		alternatingRow : true, // 附加奇偶行效果行
		colDraggable : false,
		dataAction : 'server', // 从后台获取数据
		method : 'post',
		url : "${ctx}/east/rules/business/col/queryList",
		parms :{
			tabName : tabName
		},
		sortName : 'colNo', // 第一次默认排序的字段
		sortOrder : 'asc', // 排序的方式
		width : '100%',
		height : '100%',
		toolbar : {}
	})
	};

	//初始化按钮
    function initButtons() {
		var btns = [{
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
        }];

		BIONE.loadToolbar(grid, btns, function() {});
	};
	
	//新增任务
    function create() {
		BIONE.commonOpenDialog("新增", "addCol", 960, $("#center").height()-30, 
				"${ctx}/east/rules/business/col/" + tabName + "/add");
    }

    // 修改
    function edit() {
    	achieveTabs();
		if(tabs.length == 1){
			BIONE.commonOpenLargeDialog("修改", "editCol",
					"${ctx}/east/rules/business/col/" + tabName + "/" + tabs[0].colName +"/edit");
		} else if(tabs.length>1) {
			BIONE.tip("只能选择一行进行修改");
		} else {
			BIONE.tip("请选择需要修改的字段信息");
			return;
		}
		
		var row = grid.getSelectedRow();
		if (!row) {
			BIONE.tip('请选择行');
			return;
		}
	}
	
	function achieveTabs() {
		tabs = [];
		var rows = grid.getSelectedRows();
		for(var i in rows) {
			tabs.push(rows[i]);
		}
	}
	
    // 批量删除任务
    function deleteBatch() {
        var rows = grid.getSelectedRows();
        if(rows.length>0){
            // 物理表删除
            $.ligerDialog.confirm('您确定删除这' + rows.length + "条任务和任务相关信息吗？", function(yes) {
                var colNos = [];
                for(var i =0;i<rows.length;i++){
                    var rowData = rows[i];
                    colNos.push(rowData.colNo);
                }
                if (yes) {
                    $.ajax({
                        async : false,
                        type : "get",
                        dataType : "json",
                        url : '${ctx}/east/rules/business/col/delete?colNos=' + colNos.join(','),
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
        } else {
            BIONE.tip('请至少选择一条记录');
        }
    }  
</script>
</head>
<body>
</body>
</html>