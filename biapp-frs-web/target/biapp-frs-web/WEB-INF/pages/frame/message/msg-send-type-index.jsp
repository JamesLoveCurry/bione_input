<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid, btns, url, ids = [];
	
	$(init);

	/* 全局初始化 */
	function init() {
		url = "${ctx}/bione/msgSendType/list.json";
		initSearchForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	}

	function initSearchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "发送类型名称",
				name : "sendTypeName",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "msgDef.sendTypeName"
				}
			}, {
				display : "实现类名称",
				name : "beanName",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "msgDef.beanName"
				}
			} ]
		});
	}
	
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '100%',
			columns : [ {
				display : "发送类型名称",
				name : 'sendTypeName',
				align : 'center',
				width : '20%'
			}, {
				display : "实现类名称",
				name : 'beanName',
				align : 'center',
				width : '20%'
			}, {
				display : '备注',
				name : 'remark',
				align : 'center',
				width : '33%'
			} ],
			checkbox : true,
			usePager : true,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : url,
			sortName : 'sendTypeName', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}

	function initButtons() {
		btns = [ {
			text : '增加',
			click : msgType_add,
			icon : 'fa-plus',
			operNo : 'msgType_add'
		}, {
			text : '修改',
			click : msgType_modify,
			icon : 'fa-pencil-square-o',
			operNo : 'msgType_modify'
		}, {
			text : '删除',
			click : msgType_delete,
			icon : 'fa-trash-o',
			operNo : 'msgType_delete'
		} ];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}

	function msgType_add(item) {
		BIONE.commonOpenSmallDialog('添加消息发送类型', 'msgTypeManage',
				'${ctx}/bione/msgSendType/edit');
	}

	function msgType_modify(item) {
		achieveIds();
		if (ids.length == 1) {
			BIONE.commonOpenSmallDialog('修改消息发送类型', 'msgTypeManage',
					'${ctx}/bione/msgSendType/edit?id='+ ids[0]);
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			BIONE.tip('请选择记录');
		}
	}

	function msgType_delete(item) {
		achieveIds();
		if (ids.length > 0) {
			$.ligerDialog.confirm('确实要删除这些记录吗?', function(yes) {
				if (yes) {
					var flag = false;
					$.ajax({
						async : false,
						type : "POST",
						url : "${ctx}/bione/msgSendType/deMsgSend?ids=" + ids.join(','),
						success : function() {
							flag = true;
						}
					});
					if (flag == true) {
						BIONE.tip('删除成功');
						initGrid();
					} else {
						BIONE.tip('删除失败');
					}
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
			ids.push(rows[i].sendTypeNo);
		}
	}
</script>

</head>
<body>
</body>
</html>