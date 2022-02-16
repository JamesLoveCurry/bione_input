<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">

	var dialog;
	var grid;
	
	$(function() {
		$("#search").ligerForm({
			fields : [ {
				display : "数据源名称 ",
				name : "dsName",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : "dsName",
					op : "like"
				}
			}]
		});

		initGrid();
		
		var btns = [{
			text : '添加',
			click : create,
			operNo : 'data-source-add',
			icon : 'fa-plus'
		},{
			text : '修改',
			click : modify,
			operNo : 'data-source-modify',
			icon : 'fa-pencil-square-o'
		},{
			text : '删除',
			click : deleteDs,
			operNo : 'data-source-delete',
			icon : 'fa-trash-o'
		}];

		BIONE.loadToolbar(grid, btns, function() {});
		
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		if ("${id}") {
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '选择',
				icon : 'fa-compass',
				//width : '50px', //按钮位置修正:不加宽度防止计算错误
				click : function() {
					var selectedRow = grid.getSelecteds();
					//只允许选一个
					if(selectedRow.length!=1){
						BIONE.tip('请选择一行记录');
						return;
					}
					var dsName = [], dsId = []
					for ( var k = 0; k < selectedRow.length; k++) {
						dsName.push(selectedRow[k].dsName);
						dsId.push(selectedRow[k].dsId);
					}
					var dsName2 = dsName;
					var dsId2 = dsId;
					window.parent.dsName.val(dsName2);
					window.parent.dsId.val(dsId2);
					window.parent.dsName.focusout();
					try {
						parent.selectPopupEdit('dsName', dsName2[0], dsId2[0]);
					} catch(e) {
					}
					var combo = parent.liger.get('dsNameCombox');
					if (combo) {
						combo.setValue(dsId2[0]);
						combo.setText(dsName2[0]);
					}
					BIONE.closeDialog("dsList");
				}
			});
		}
		
	});
	//初始化表格 
	function initGrid() {
		//alert("进入initDrid");
		grid = $("#maingrid").ligerGrid({
			//InWindow : false,
			width : '100%',
			height : '99%',
			columns : [ {
				name : 'dsId',
				hide :1
			}, {
				display : '数据源名称',
				name : 'dsName',
				align : 'center',
				width : '15%'
			},/*  {
				display : '逻辑系统标识 ',
				name : 'logicSysNo',
				align : 'left',
				width : '10%'
			}, */ {
				display : '驱动类型',
				name : 'driverId',
				align : 'center',
				width : '15%'
			},{
				display : '地址',
				name : 'host',
				align : 'center',
				width : '20%'
			},{
				display : '端口',
				name : 'port',
				align : 'center',
				width : '10%'
			},{
				display : '数据库名',
				name : 'dbname',
				align : 'center',
				width : '15%'
			}, /* {
				display : '连接URL',
				name : 'connUrl',
				align : 'left',
				width : '25%'
			}, */ {
				display : '连接用户',
				name : 'connUser',
				align : 'center',
				width : '15%'
			}],
			checkbox : true,
			isScroll : true,
			rownumbers : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/bione/mtool/datasource/list.json",
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			sortName : 'dsId',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			toolbar : {}
		});
	}
	//新加
	function create() {
		dialog = BIONE.commonOpenDialog('新加数据源', 'addDsWin','660','350',
				'${ctx}/bione/mtool/datasource/new');
	}
	//修改
	function modify(){
		//获得被选中 的行
		var rows = grid.getSelectedRows();
		if(rows.length==1){
			BIONE.commonOpenDialog('修改数据源', 'addDsWin','660','350',
					'${ctx}/bione/mtool/datasource/' + rows[0].dsId + '/edit');
		}else{
			BIONE.tip("请选择一条数据进行修改 ");
		}
	}

	//删除
	function deleteDs() {
		//获得被选中 的行
		var rows = grid.getSelectedRows();
		for(var i=0; i< rows.length; i++){
			var dsId = rows[i].dsId;
			if(dsId == "1"){
				BIONE.tip('您选择的数据源中包含默认数据源，不能删除！');
				return;
			}
		}
		//判断被选中的行数  ，分别进行 不同的操作 
		if (rows.length > 0) {
			$.ligerDialog
					.confirm(
							'您确定删除这' + rows.length + "条记录么？",
							function(yes) {
								if (yes) {
									var ids="";
									for ( var i = 0; i < rows.length; i++) {
										ids=ids+"/"+rows[i].dsId;
									}
									$.ajax({
										type : "POST",
										url : "${ctx}/bione/mtool/datasource/removeAll",
										data:{
											dsId :ids
										},
										dataType : "json",
										success : function(result) {
												if(result.msg){
													BIONE.tip(result.msg);
												}else{
													BIONE.tip('删除成功');
													initGrid();
												}
										} 
									});
									
									
								}
							});
		} else {
			BIONE.tip('请选择要删除的行');
		}
	}
	
	
</script>
</head>
<body>
</body>
</html>