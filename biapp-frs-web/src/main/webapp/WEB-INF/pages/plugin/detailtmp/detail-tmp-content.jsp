<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">
<%@ include file="/common/plugin_load.jsp"%>
<script type="text/javascript">
	// 全局变量
	var grid;
	
	$(function() {
		var parent = window.parent;
		//初始化
		ligerSearchForm();
		ligerGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	//渲染查询表单
	function ligerSearchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "模板名称",
				name : "templateNm",
				newline : true,
				type : "text",
				attr : {
					field : "templateNm",
					op : "like"
				}
			}]
		});
	}

	//渲染GRID
	function ligerGrid() {
		parent.tmpGrid = grid = $("#maingrid").ligerGrid({
			width : "100%",
			height : "99%",
			columns : [{
				display : "模板编号",
				name : "templateId",
				width : "10%",
				align: "left"
			}, {
				display : "模板名称",
				name : "templateNm",
				width : "40%",
				align: "left"
			},{
				display : "模板类型",
				name : "templateType",
				width : "20%",
				render : function(data, row, val) {
					if ("01" == val){
						return "模型配置";
					} else if ("02" == val){
						return "SQL";
					} else {
						return "未知";
					}
				}
			},{
				display : "模板状态",
				name : "templateSts",
				width : "20%",
				type : 'switcher',
				options :{
					onText: '启用',
					offText: '停用',
					onTurnOn : function(row){
						BIONE.ajax({
							type : 'GET',
							url : "${ctx}/report/frame/detailtmp/changeSts?templateId="+row.templateId+"&templateSts=1&d="+new Date().getTime()
						}, function(result) {
						});
					},
					onTurnOff : function(row){
						BIONE.ajax({
							type : 'GET',
							url : "${ctx}/report/frame/detailtmp/changeSts?templateId="+row.templateId+"&templateSts=0&d="+new Date().getTime()
						}, function(result) {
						});
					}
				}
			}],
			checkbox : true,
			userPager : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			selectRowButtonOnly : true ,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/report/frame/detailtmp/list",
			toolbar : {}
		});
	}
	//初始化按钮
	function initButtons() {
		var btns = [ {
			text : "添加",
			icon : "add",
			click : detail_add,
			operNo : 'detail-tmp-add'
		}, {
			text : "修改",
			icon : "modify",
			operNo : 'detail-tmp-modify',
			menu : {
				items : [ {
					text : '基本信息',
					click : function(){
						detail_modify();
					},
					icon:"view"
				}, {
					text : '配置信息',
					click : function(){
						detail_config();
					},
					icon:"config"
				}]
			}
		}, {
			text : "删除",
			icon : "delete",
			click : detail_delete,
			operNo : 'detail-tmp-delete'
		}];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	
	function detail_add(){
		var catalogId = ""
		var currentNode = window.parent.currentNode;
		if(currentNode !=null && currentNode.id != "0")
			catalogId = currentNode.id;
		window.parent.BIONE.commonOpenFullDialog("明细模板添加","addWin","${ctx}/report/frame/detailtmp/add?catalogId="+catalogId);
	}
	
	function detail_modify(){
		var rows = grid.getSelectedRows();
		if(rows.length !=1){
			BIONE.tip("请选择一个明细模板进行修改");
			return;
		}
		var templateId = rows[0].templateId;
		window.parent.BIONE.commonOpenDialog("基本信息修改","infoEdit",600, 350,"${ctx}/report/frame/datashow/detail/tmpinfo?templateId="+templateId);
	}
	
	function detail_delete(){
		var selectedRow = grid.getSelecteds();
		if(selectedRow.length == 0){
			BIONE.tip('请选择明细模板');
			return;
		}
		$.ligerDialog.confirm('确实要删除这' + selectedRow.length + '条记录吗!',
				function(yes) {
					var length = selectedRow.length;
					if (yes) {
						var ids = "";
						for ( var i = 0; i < length; i++) {
							if (ids != "") {
								ids += ",";
							}
							ids += selectedRow[i].templateId;
						}
						$.ajax({
							type : "POST",
							url : '${ctx}/report/frame/detailtmp/deleteTmpInfo',
							dataType : "json",
							type : "post",
							data : {
								"templateId" : ids
							},
							success : function(result) {
								if (result.msg == "ok") {
									BIONE.tip("删除成功");
									grid.loadData();
								} else {
									BIONE.tip('删除失败');
								}
							}
						});
					}
		});
		
	}
	
	function detail_config(){
		var rows = grid.getSelectedRows();
		if(rows.length !=1){
			BIONE.tip("请选择一个明细模板进行修改");
			return;
		}
		var templateId = rows[0].templateId;
		window.parent.BIONE.commonOpenFullDialog("配置信息","addWin","${ctx}/report/frame/detailtmp/add?templateId="+templateId);
	}
	
</script>
</head>
<body>
</body>
</html>