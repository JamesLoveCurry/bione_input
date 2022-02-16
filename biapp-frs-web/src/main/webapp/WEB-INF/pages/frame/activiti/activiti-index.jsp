<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid, btns, url, ids = [];

	$(init);

	function init() {
		url = "${ctx}/frs/activiti/list.json";
		searchForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	}

	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "流程名称",
				name : "name",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "=",
					field : "T.name"
				}
			} ]
		});
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid(
				{
					width : '100%',
					height : '99%',
					columns : [
							{
								display : '流程ID',
								name : 'id',
								align : 'center',
								width : '15%'
							} ,
							{
								display : '流程部署ID',
								name : 'deploymentId',
								align : 'center',
								width : '15%'
							} ,
							{
								display : '流程名称',
								name : 'name',
								align : 'center',
								width : '20%'
							},
							{
								display : '创建时间',
								name : 'createTime',
								align : 'center',
								width : '20%',
								type : 'date',
								format : 'yyyy-MM-dd hh:mm:ss'
							},
							{
								display : '上次修改时间',
								name : 'lastUpdateTime',
								align : 'center',
								width : '20%',
								type : 'date',
								format : 'yyyy-MM-dd hh:mm:ss'
							}],
					checkbox : true,
					usePager : true,
					isScroll : true,
					rownumbers : true,
					alternatingRow : true, /* 附加奇偶行效果行 */
					colDraggable : false,	/* 是否允许表头拖拽 */
					dataAction : 'server',	/* 从后台获取数据 */
					method : 'post',
					url : url,
					sortName : 'lastUpdateTime', //第一次默认排序的字段
					sortOrder : 'asc',
					toolbar : {}
				});
	}

	
	
	function initButtons() {
		btns = [ {
			text : '增加',
			click : module_add,
			icon : 'fa-plus',
			operNo : 'module_add'
		}, {
			text : '修改',
			click : module_modify,
			icon : 'fa-pencil-square-o',
			operNo : 'module_modify'
		}, {
			text : '删除',
			click : module_delete,
			icon : 'fa-trash-o',
			operNo : 'module_delete'
		} /* , {
			text : '查看',
			click : module_look,
			icon : 'detail',
			operNo : 'module_look'
		} */, {
			text : '部署',
			click : module_deloy,
			icon : 'fa-tasks',
			operNo : 'module_deloy'
		} ];
		BIONE.loadToolbar(grid, btns, function() { });
	}

	function module_add(item) {
		BIONE.commonOpenSmallDialog('创建流程', 'moduleUpdate',
				'${ctx}/frs/activiti/new');
	}
	function module_modify(item) {
		achieveIds();
		if (ids.length == 1) {
		var modleId = grid.getSelectedRows()[0].id
		var port=window.location.host;
		window.open("http://"+port+"${ctx}/process-edit/modeler.html?modelId="+modleId,'_blank');
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			BIONE.tip('请选择记录');
		}
	}
	
	function module_look(item) {
		
	}
	
	function module_deloy(item) {
	achieveIds();
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/frs/activiti/validate/' + ids.join(','),
			dataType : 'text',
			type : "post",
			success : function(result) {
				if(result==true){
					BIONE.showLoading("正在部署中");
					$.ajax({
						cache : false,
						async : true,
						url : '${ctx}/frs/activiti/deploy/' + ids.join(','),
						dataType : 'text',
						type : "post",
						success : function(result) {
							BIONE.tip('部署成功');
							grid.loadData();
							BIONE.hideLoading();
						},
						error : function() {
							BIONE.tip('部署失败，请检查流程定义图是否正确');
							BIONE.hideLoading();
						}
					});
				}else{
					BIONE.tip('已经发起任务的流程无法部署');
				}
				
			}
		});

	}
	function module_delete(item) {
		achieveIds();
		if (ids.length > 0) {
			$.ligerDialog.confirm('确实要删除这些记录吗?', function(yes) {
				if (yes) {
					$.ajax({
						cache : false,
						async : false,
						type : "POST",
						url : '${ctx}/frs/activiti/delete/' + ids.join(','),
						dataType : "text",
						success : function(result) {
							BIONE.tip('删除成功');
							grid.loadData();
						},
						error : function() {
							BIONE.tip('删除失败,已发起实例的流程无法删除');
						}
					});
				}
			});
		} else {
			BIONE.tip('请选择记录');
		}
	}

	// 获取选中的行
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].id);
		}
	}
</script>
</head>
<body>
</body>
</html>