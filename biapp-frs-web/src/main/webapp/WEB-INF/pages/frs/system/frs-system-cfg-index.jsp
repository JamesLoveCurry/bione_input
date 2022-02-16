<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid;
	var busiTypeMap = new Map();
	
	$(function() {
		initBusiType();
		initGrid();
		initSearch();
		initButtons();
	});

	//加载业务类型
	function initBusiType(){
		$.ajax({
			async :false,
			type : "POST",
			url : "${ctx}/report/frame/datashow/idx/getIdxTaskType.json",
			dataType : 'json',
			success: function(data) {
				if(data){
					for(var i = 0; i < data.length ; i++){
						busiTypeMap.set(data[i].id, data[i].text);
					}
				}
			},
			error: function() {
				top.BIONE.tip('保存失败');
			}
		});
	}
	
	//监管制度列表
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '业务类型',
				name : 'id.busiType',
				width : '15%',
				align : 'center',
				render : function(rowdata, index, value) {
					return busiTypeMap.get(value);
				}
			}, {
				display : '制度版本名称',
				name : 'systemName',
				width : '25%',
				align : 'center'
			}, {
				display : '制度版本号',
				name : 'id.systemVerId',
				width : '10%',
				align : 'center'
			}, {
				display : '开始日期',
				name : 'verStartDate',
				width : '15%',
				align : 'left'
			}, {
				display : '结束日期',
				name : 'verEndDate',
				width : '15%',
				align : 'center'
			} ],
			checkbox : false,
			rownumbers : true,
			isScroll : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/frs/system/cfg/getSystemList",
			enabledSort: false,
			sortName : 'systemName',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			width : '100%',
			height : '99%',
			toolbar : {}
		});
	}

	//搜索框初始化
	function initSearch() {
		$("#search").ligerForm({
			labelWidth : 100,
			fields : [ {
				display : "业务类型",
				name : "busiType",
				newline : true,
				type : "select",
				cssClass : "field",
				options : {
					url : "${ctx}/report/frame/datashow/idx/getIdxTaskType.json",
				},
				attr : {
					field : "busiType",
					op : "="
				}
			}, {
				display : "制度版本名称",
				name : "systemName",
				comboboxName : 'taskNmBox',
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : "systemName",
					op : "like"
				}
			} ]
		});
	}

	//初始化按钮
	function initButtons() {
		var btns = [ {
			text : '新建制度版本',
			click : addSystem,
			icon : 'fa-plus'
		}, {
			text : '修改制度版本',
			click : editSystem,
			icon : 'fa-pencil-square-o'
		}, {
			text : '删除制度版本',
			click : deleteSystem,
			icon : 'fa-trash-o'
		} ];

		BIONE.loadToolbar(grid, btns, function() {
		});

		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	}

	//修改制度版本
	function editSystem() {
		var rows = grid.getSelectedRows();
		if (rows.length < 1) {
			BIONE.tip('请选择记录');
		} else if (rows.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			var systemVerId = rows[0].id.systemVerId;
			var busiType = rows[0].id.busiType;
			BIONE.commonOpenDialog("修改制度版本", "addSystem", 800, 420,
					'${ctx}/frs/system/cfg/edit?systemVerId=' + systemVerId
							+ '&busiType=' + busiType);
		}
	}

	//新建制度版本
	function addSystem() {
		BIONE.commonOpenDialog("新建制度版本", "addSystem", 800, 420,
				'${ctx}/frs/system/cfg/edit');
	}

	//删除制度版本
	function deleteSystem() {
		var rows = grid.getSelectedRows();
		if (rows.length < 1) {
			BIONE.tip('请选择记录');
		} else if (rows.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			var systemVerId = rows[0].id.systemVerId;
			var busiType = rows[0].id.busiType;
			$.ajax({
				cache : false,
				async : false,
				url : '${ctx}/frs/system/cfg/delete',
				dataType : 'json',
				type : "GET",
				data : {
					systemVerId : systemVerId,
					busiType : busiType
				},
				success : function(result) {
					if(result && result.mag){
						BIONE.tip(result.mag);
						grid.reload();
					}
				}
			});
		}
	}
</script>
</head>
<body>
</body>
</html>