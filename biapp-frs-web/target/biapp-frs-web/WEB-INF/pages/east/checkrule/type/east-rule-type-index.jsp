<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template8.jsp">
<script type="text/javascript">
	var dialog;
	var grid;
	var ids;

    $(function() {
        initGrid();
        initButtons();
    });
    
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
		columns : [ {
			display : '规则类型',
			name : 'typeName',
			width : '30%',
			align : 'center'
		},{
			display : '检核逻辑说明',
			name : 'typeDesc',
			width : '50%',
			align : 'center'
		}],
		checkbox : true,
		rownumbers : true,
		isScroll : false,
		alternatingRow : true,//附加奇偶行效果行
		colDraggable : false,
		dataAction : 'server',//从后台获取数据
		method : 'post',
		url : "${ctx}/east/rules/checkRule/type/queryList",
		sortName : 'id',//第一次默认排序的字段
		sortOrder : 'asc', //排序的方式
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
        }, {
            text : '测试生成xml文件',
            click : modelDown,
            icon : 'fa-book'
        },  {
            text : '导入',
            click : importExcel,
            icon : 'fa-code-fork'
        }];

		BIONE.loadToolbar(grid, btns, function() {});
	};

	// 导入
	function importExcel(){
		BIONE.commonOpenDialog("校验规则导入", "importWin", 600, 480,
				"${ctx}/east/rules/checkRule/import?type=Rule");
	}
	
	//新增任务
    function create() {
		BIONE.commonOpenDialog("新增", "addRuleType", 960, $("#center").height()-30, 
				"${ctx}/east/rules/checkRule/type/add");
    }

	// 修改
    function edit() {
    	achieveTabs();
		if(ids.length == 1){
			BIONE.commonOpenLargeDialog("修改", "editRuleType",
					"${ctx}/east/rules/checkRule/type/" + ids[0].typeName + "/edit");
		} else if(ids.length>1) {
			BIONE.tip("只能选择一行进行修改");
		} else {
			BIONE.tip("请选择需要修改的规则类型信息");
			return;
		}
		
		var row = grid.getSelectedRow();
		if (!row) {
			BIONE.tip('请选择行');
			return;
		}
	}

    // 批量删除任务
    function deleteBatch() {
        var rows = grid.getSelectedRows();
        if(rows.length>0){
            // 物理表删除
            $.ligerDialog.confirm('您确定删除这' + rows.length + "条任务和任务相关信息吗？", function(yes) {
                var typeIds = [];
                for(var i =0;i<rows.length;i++){
                    var rowData = rows[i];
                    typeIds.push(rowData.typeId);
                }
                if (yes) {
                    $.ajax({
                        async : false,
                        type : "get",
                        dataType : "json",
                        url : '${ctx}/east/rules/checkRule/type/delete?ids=' + typeIds.join(','),
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
    
	function achieveTabs() {
		ids = [];
		var rows = grid.getSelectedRows();
		for(var i in rows) {
			ids.push(rows[i]);
		}
	}

    function modelDown() {
    	var download=null;
		download = $('<iframe id="download1"  style="display: none;"/>');
		$('body').append(download);
		var src = "${ctx}/east/rules/checkRule/download/model/test";
		download.attr('src', src);
    }
</script>
</head>
<body>
</body>
</html>