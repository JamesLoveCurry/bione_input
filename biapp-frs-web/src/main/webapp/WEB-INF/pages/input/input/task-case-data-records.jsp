<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template8.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var grid,manager;
	var ids = [];
	var tableList;
	$(function() {
		initGrid();
		getGridData();
	});
	function getGridData(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/taskcase/getDataFileRecords.json",
			dataType : 'json',
			type : "get",
			data : {
				"templeId" : "${templeId}",
				"caseId" : "${caseId}"
			},
			success : function(data) {
				tableList = data['Rows'];
				var manager = $("#maingrid").ligerGetGridManager();
				for (i = 0; i < tableList.length; i++) {
					var obj = {};
					if(tableList[i]['SYS_OPER_DATE']!= null){
						obj["SYS_OPER_DATE"] = tableList[i]['SYS_OPER_DATE'];
					}else{
						obj['SYS_OPER_DATE'] = "";
					}
						
					manager.addRow(obj); 
				}
			}
		});
	}
	function initGrid() {

		grid = manager = $("#maingrid").ligerGrid(
			{
				height : '99%',
				width : '100%',
				columns : [{
					display : "时间",
					name : "SYS_OPER_DATE",
					align : 'center',
					minWidth : 100,
					width : "70%"
				},{
					display : '操作',
					isSort : false,
					width : '23%',
					render : function(rowdata, rowindex, value) {
						var h = "";
						if (!rowdata._editing) {
							h += "<a href='javascript:deleteRow("
									+ rowindex + ")'>删除</a> ";
							h += "<a href='javascript:dataLook("
								+ rowindex + ")'>查看</a> ";
						}

						return h;
					}
				} ],
				checkbox : true,
				usePager : false,
				frozen : false,
				isScroll : true,
				colDraggable : true,
				rownumbers : true,
				enabledEdit : false,
				clickToEdit : true,
				resizable : true,
				alternatingRow : true,//附加奇偶行效果行
				data : {Rows:[]},
				onCheckRow : onCheckRow,
				onCheckAllRow : onCheckAllRow
			});

	}
	function onCheckRow(checked, rowdata, rowindex, rowDomElement) {
		UDIP.onCheckRow(this, checked, rowindex)
	}
	function onCheckAllRow(checked, grip) {
		UDIP.onCheckAllRow(this);
	}
	function deleteRow(rowid) {
		achieveIds();
		if (ids.length == 1) {
			$.ligerDialog.confirm('确实要删除这些记录吗?', function(yes) {
				if (yes) {
					var flag = false;
					$.ajax({
						async : true,
						url : '${ctx}/rpt/input/taskcase/deleteDataFileRecords.json',
						dataType : 'json',
						type : "get",
						data : {
							"templeId" : "${templeId}",
							"caseId" : "${caseId}",
							"times" : ids+""
						},
						success : function(data2) {
							flag = true;
							if(data2.length==0){
								BIONE.showSuccess("删除成功");
								grid.set('data', {Rows:[]}); 
								grid.reRender();
								getGridData();
							}else{
								BIONE.showError("删除失败"+data2);
							}
						}
					});
				}
			});
		}else {
			BIONE.tip("请选择一条数据进行修改");
			return;
		}
	}
	function dataLook(rowid) {
		var manager = $("#maingrid").ligerGetGridManager();
		var data = manager.getData();
		var searchStr="SYS_OPER_DATE#@#"+data[rowid].SYS_OPER_DATE+"@@"
		grid.reRender();
		UDIP.commonOpenFullDialog("数据预览",
				"inputTaskInfo", "${ctx}/rpt/input/taskcase/inputTaskInfoLook?templeId=" + "${templeId}"+"&caseId=" +"${caseId}"+"&searchTerms="+encodeURIComponent(encodeURIComponent(searchStr)));
		
	}
	// 获取选中的行
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].SYS_OPER_DATE)
		}
	}
	
</script>
</head>
<body>
	<div id="content" style="width: 100%; overflow: auto; clear: both;">
		<div id="template.center">
			<form id="mainsearch" action="${ctx}/rpt/input/temple/tab1-save-update"
				method="post"></form>
		</div>
	</div>
</body>
</html>