<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template2.jsp">
<script type="text/javascript">
	var items, currentNode, grid;

	$(function() {
		initTree();
		searchForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		//隐藏全选按钮
		var oo = $(".l-grid-hd-cell-inner");
		if (oo.length > 0) {
			$(oo[1]).children(".l-grid-hd-cell-btn-checkbox").hide();
		}
		$("#search input[name=orgName]").attr("readonly", "readonly");
		$("#search input[name=orgNo]").attr("readonly", "readonly");
	});

	function initTree() {
		window['orgTreeInfo'] = $.fn.zTree.init($("#tree"), {
			async:{
				enable : true,
				autoParam : [ "id=nodeId" ],
				url : "${ctx}/bione/admin/org/list.json?d="+new Date().getTime(),
				dataType : "json",
				type : "post"
			},
			data : {
				key : {
					name : "text"
				}
			},
			view : {
				selectedMulti : false,
				showLine : false
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					currentNode = treeNode;
					if (currentNode.id == "0") {
						return;
					}
					grid.set('parms', {
						orgId : $.trim(currentNode.data.orgId),
						orgNo : $.trim(currentNode.data.orgNo)
					});
					$("#search input[name=posiNo]").val("");
					$("#search input[name=posiName]").val("");
					$("#search input[name=orgId]").val(
							$.trim(currentNode.data.orgId));
					$("#search input[name=orgNo]").val(
							$.trim(currentNode.data.orgNo));
					grid.loadData();
				}
			}
		});
	}

	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : '岗位编码',
				name : "posiNo",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'posiNo',
					op : "like"
				}
			}, {
				display : '岗位名称',
				name : 'posiName',
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'posiName',
					op : "like"
				}
			}, {
				name : 'orgNo',
				type : 'hidden',
				attr : {
					field : 'orgNo',
					op : "="
				}
			} ]
		});
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid(
				{
					//height:"100%",
					width : "100%",
					columns : [ {
						display : '岗位编码',
						name : 'posiNo',
						id : 'posiNo',
						width : '20%',
						align : 'left',
						isSort : true
					}, {
						display : '岗位名称',
						name : 'posiName',
						width : '30%',
						align : 'left',
						isSort : true
						
					}, {
						display : '岗位状态',
						name : 'posiSts',
						width : "10%",
						align : 'left',
						render : QYBZRender,
						isSort : true
					// 			}, {
					// 				display : '岗位类型',
					// 				name : 'posiType',
					// 				width:"25%",
					// 				render:function(data){
					// 					if(data.posiType=='01'){
					// 						return "报表岗位";
					// 					}else if(data.posiType=='02'){
					// 						return "补录岗位";
					// 					}else if(data.posiType=='03'){
					// 							return "普通岗位";
					// 					}else{
					// 							return "其他";
					// 					} 
					// 					}
					}, {
						display : '备注',
						name : 'remark',
						width : '30%',
						align : 'left',
						isSort : true
					} ],
					usePager : true,
					checkbox : true,
					//tree: { columnId: 'deptNo' },
					dataAction : 'server', //从后台获取数据
					//usePager : false, 		//服务器分页
					alternatingRow : true, //附加奇偶行效果行
					colDraggable : true,
					url : "${ctx}/bione/admin/posi/findPosiInfoByOrg.json?d="
							+ new Date().getTime(),
					method : 'post', // get
					sortName : 'posiNo', //第一次默认排序的字段
					sortOrder : 'asc', //排序的方式
					rownumbers : true,
					onCheckRow : function(checked, rowdata, rowindex,
							rowDomElement) {
						//只允许勾选一行
						if (checked) {
							var all = grid.getData();
							$.each(all, function(i, n) {
								var x = grid.getRow(i);
								if (rowindex != x.__id) {
									grid.unselect(grid.getRow(i));
								}
							});
						}
					},
					onBeforeCheckAllRow : function() {
						return false;
					},
					toolbar : {}
				});

	}
	function QYBZRender(rowdata) {
		if (rowdata.posiSts == '1') {
			return "启用";
		}
		if (rowdata.posiSts == '0') {
			return "停用";
		} else {
			return rowdata.roleSts;
		}
	}
	function initButtons() {
		if ("${canEdit}" == "0") {
			initToolbar_1();
		} else {
			initToolbar_2();
		}
	}

	function initToolbar_1() {
		items = [ {
			text : '查看',
			click : function() {
				var selectedDept = grid.getSelectedRow();
				if (!selectedDept) {
					BIONE.tip("请先选择要查看的岗位");
					return;
				}
				BIONE.commonOpenLargeDialog("岗位信息", "posiModifyWin",
						"${ctx}/bione/admin/posi/" + selectedDept.deptId
								+ "/edit?isBasicDept=${isBasicDept}&flag=scan");
			},
			icon : 'calendar'
		} ];
		BIONE.loadToolbar(grid, items, function() {
		});
	}

	function initToolbar_2() {
		items = [
				{
					text : '增加',
					click : function() {
						if (!currentNode) {
							BIONE.tip("请先选择机构");
							return;
						}
						if (currentNode.id == "0") {
							BIONE.tip("请先选择机构");
							return;
						}
						BIONE.commonOpenDialog("岗位添加","posiAddWin",700,350,"${ctx}/bione/admin/posi/new?orgId="+ currentNode.data.orgId);
						/* BIONE.commonOpenLargeDialog("岗位添加", "posiAddWin",
								"${ctx}/bione/admin/posi/new?orgId="
										+ currentNode.data.orgId); */

					},
					icon : 'add'
				},
				{
					text : '修改',
					click : function() {
						var selectedDept = grid.getSelectedRow();
						if (!selectedDept) {
							BIONE.tip("请先选择需要修改的岗位");
							return;
						}
						BIONE.commonOpenDialog("岗位修改","posiModifyWin",700,350,"${ctx}/bione/admin/posi/"+selectedDept.posiId+"/edit");
					/* 	BIONE.commonOpenLargeDialog("岗位修改", "posiModifyWin",
								"${ctx}/bione/admin/posi/"
										+ selectedDept.posiId + "/edit"); */
					},
					icon : 'modify'
				}, {
					text : '删除',
					click : f_delete,
					icon : 'delete'
				}, {
					text : '启用',
					click : f_use,
					icon : 'modify'
				}, {
					text : '停用',
					click : f_unuse,
					icon : 'modify'
				} ];
		BIONE.loadToolbar(grid, items, function() {
		});
	}

	function f_use() {
		var rows = grid.getSelectedRows();
		if (rows.length == 0) {
			BIONE.tip("请选择行");
			return;
		}
		if (rows.length > 1) {
			BIONE.tip("请选择唯一行进行处理");
			return;
		}
		var rowInfo = rows[0];
		if (rowInfo.posiSts == "1") {
			BIONE.tip("当前岗位为启用状态不能重复启用");
			return;
		} else {
			$.ajax({
				type : "POST",
				url : '${ctx}/bione/admin/posi/updatePosiFlag',
				dataType : "json",
				data : {
					"posiId" : rowInfo.posiId,
					"posiSts" : rowInfo.posiSts
				},
				success : function(result) {
					BIONE.tip('启用成功');
					grid.loadData();
				},
				error : function() {
					BIONE.tip('启用失败,请联系管理员!');
				}
			});
		}
	}

	function f_unuse() {
		var rows = grid.getSelectedRows();
		if (rows.length == 0) {
			BIONE.tip("请选择行");
			return;
		}
		if (rows.length > 1) {
			BIONE.tip("请选择唯一行进行处理");
			return;
		}
		var rowInfo = rows[0];
		if (rowInfo.posiSts == "0") {
			BIONE.tip("当前岗位为停用状态不能重复停用");
			return;
		} else {
			$.ajax({
				type : "POST",
				url : '${ctx}/bione/admin/posi/updatePosiFlag',
				dataType : "json",
				data : {
					"posiId" : rowInfo.posiId,
					"posiSts" : rowInfo.posiSts
				},
				success : function(result) {
					BIONE.tip('停用成功');
					grid.loadData();
				},
				error : function() {
					BIONE.tip('停用失败,请联系管理员!');
				}
			});
		}
	}

	var str = "";

	function f_delete() {
		var selectedRow = grid.getSelecteds();
		if (selectedRow.length == 0) {
			BIONE.tip('请选择行');
			return;
		}
		var rows = grid.getSelectedRows();
		for ( var i = 0; i < rows.length; i++) {
			str += rows[i].posiId + ',';
		}

		$.ligerDialog.confirm('确实要删除这些记录吗!', function(yes) {
			if (yes) {
				$.ajax({
					async : false,
					type : "POST",
					url : '${ctx}/bione/admin/posi/' + str,
					success : function(result) {
						if (result) {
							var str = result.msg;
							if (str == "1") {
								BIONE.tip("当前选中岗位被用户引用,不能删除!");
							} else if (str == "2") {
								BIONE.tip("当前选中岗位与资源绑定，不能删除!");
							} else if (str == "0") {
								BIONE.tip('删除成功');
								grid.loadData();
							}
							str = "";

						} else {
							BIONE.tip("删除失败,请联系管理员");
							str = "";
						}
					},
					error : function(data) {
						BIONE.tip(data.responseText);
						str = "";
						grid.loadData();
					}
				});
			} else {
				BIONE.tip("取消删除");
			}
		});
	}
</script>

<title>岗位管理</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">机构信息</span>
	</div>
</body>
</html>