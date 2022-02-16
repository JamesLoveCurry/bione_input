<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	$(function() {
		$("#search").ligerForm({
			fields : [ {
				display : '参数名称',
				name : "paramName",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "paramName"
				}
			}, {
				display : "参数值",
				name : 'paramValue',
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "paramValue"
				}
			}, {
				name : 'paramTypeNo',
				type : 'hidden',
				attr : {
					field : 'paramTypeNo',
					op : "="
				}
			} ]
		});
		grid = $("#maingrid").ligerGrid({
			checkbox : false,
			columns : [ {
				display : '参数名称',
				name : 'paramName',
				id : 'paramName',
				width : '45%',
				align : 'left'
			}, {
				display : '参数值',
				name : 'paramValue',
				width : '20%',
				align : 'left'
			}, {
				display : '备注',
				name : 'remark',
				width : '30%',
				align : 'left'
			} ],
			dataAction : 'server', //从后台获取数据
			width : '100%',
			height : '99%',
			isScroll : true,
			usePager : true, //服务器分页
			alternatingRow : false, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/bione/variable/param/list.json",
			sortName : 'orderNo',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			rownumbers : true,
			tree : {
				columnId : 'paramName'
			},
			toolbar : []
		});
		grid.setHeight($("#center").height() - 62);
		var btns = [ {
			text : '新增',
			click : addParam,
			icon : 'fa-plus'
		}, {
			text : '修改',
			click : modifyParam,
			icon : 'fa-pencil-square-o'
		}, {
			text : '删除',
			click : delParam,
			icon : 'fa-trash-o'
		}
		//暂时去掉标签相关内容
		/* , {
			text : '标签配置',
			icon : 'fa-puzzle-piece',
			menu : {
				items : [ {
					icon : 'modify',
					text : '同步标签',
					click : configLabel
				}, {
					icon : 'modify',
					text : '清空标签',
					click : deleteLabel
				} ]
			}
		} */ ];

		BIONE.loadToolbar(grid, btns, function() {
		});

		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		//新增
		function addParam() {
			var currentNode = parent.currentNode;
			if (currentNode == undefined) {
				BIONE.tip('请选择参数类型');
				return;
			}
			var upNo = 0;
			var rows = grid.getSelectedRows();
			if (rows.length == 1) {
				upNo = rows[0].paramId
			} else if (rows.length > 1) {
				BIONE.tip('请选择一条记录!');
			}

			dialog = parent.BIONE.commonOpenLargeDialog('新增参数值', 'paramAddWin',
					'${ctx}/bione/variable/param/new?paramTypeNo='
							+ currentNode.data.paramTypeNo + "&upNo=" + upNo);
		}
		//删除
		function delParam() {
			var selectedRow = grid.getSelecteds();
			if (selectedRow.length == 0) {
				BIONE.tip('请选择行');
				return;
			}
			for (var k = 0; k < selectedRow.length; k++) {
				getParamIdAndChildIdByInfo(selectedRow[k]);
			}
			parent.$.ligerDialog.confirm('确实要删除这些记录及其子记录吗!', function(yes) {
				if (yes) {
					BIONE.ajax({
						type : "POST",
						url : '${ctx}/bione/variable/param/' + str
					}, function() {
						BIONE.tip('删除成功');
						str = "";
						grid.loadData();
					});
				} else {
					BIONE.tip('取消删除');
					str = "";
				}
			});
		}
		//修改
		function modifyParam() {
			var rows = grid.getSelectedRows();
			if (rows.length == 1) {
				var id = rows[0].paramId;
				parent.BIONE.commonOpenLargeDialog("参数值修改", "paramModifyWin",
						"${ctx}/bione/variable/param/" + id + "/edit");
			} else {
				BIONE.tip('请选择一行记录进行修改!');
			}
		}
		//获取子参数
		var str = "";
		function getParamIdAndChildIdByInfo(rowInfo) {

			if (rowInfo == 1) {
				str = "";
			}
			var children = grid.getChildren(rowInfo);
			str += rowInfo.paramId + ",";
			if (children) {
				for (var i = 0; i < children.length; i++) {
					getParamIdAndChildIdByInfo(children[i]);
				}
			}
		}
		// 状态显示,停/启用等
		function QYBZRenderSts(rowdata) {
			if (rowdata.userSts == '1') {
				return "启用";
			} else if (rowdata.userSts == '0') {
				return "停用";
			} else {
				return rowdata.userSts;
			}
		}
		//参数类型标志转名称
		function QYBZRenderParTypeName(rowdata) {
			return BIONE.paramTransformer(rowdata.paramTypeNo,
					'${ctx}/bione/common/getParamTypeName');
		}

		//同步参数为标签（仅限监管机构参数使用）
		function configLabel() {
			var rows = grid.getSelectedRows();
			if (rows.length == 1) {
				var paramValue = rows[0].paramValue;
				var paramName = rows[0].paramName;
				var paramTypeNo = rows[0].paramTypeNo;
				if ("reportorgtype" == paramTypeNo) {
					$
							.ajax({
								async : false,
								type : "POST",
								url : '${ctx}/bione/label/labelConfig/configLabel?paramValue='
										+ paramValue
										+ '&paramName='
										+ paramName,
								success : function() {
									BIONE.tip('配置成功');
								},
								error : function() {
									BIONE.tip('配置失败');
								}
							});
				} else {
					BIONE.tip('只有监管机构参数可以进行标签配置!');
				}
			} else {
				BIONE.tip('请选择一行记录进行配置!');
			}
		}

		//清空标签（仅限监管机构参数使用）
		function deleteLabel() {
			$.ligerDialog.confirm('您确定要清空标签么？', function(yes) {
				if (yes) {
					$.ajax({
						async : false,
						type : "POST",
						url : '${ctx}/bione/label/labelConfig/deleteLabel',
						success : function() {
							BIONE.tip('删除成功');
						},
						error : function() {
							BIONE.tip('删除失败');
						}
					});
				}
			});
		}
	});
</script>
</head>
<body>
</body>
</html>