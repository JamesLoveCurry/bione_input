<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	//全局变量
	var grid;
	var dialog;
	var dataDate = "";
	var templateId = '${templateId}' != "" ? '${templateId}' : window.parent.currentNode?(window.parent.currentNode.params.templateId?window.parent.currentNode.params.templateId:""):""; ;
	$(function() {	
		//初始化
		initGrid();
		if(dataDate == ""){
			searchForm();
			initSystemVer();
			initToolbar();
			BIONE.addSearchButtons("#search", grid, "#searchbtn");
			downdload = $('<iframe id="download"  style="display: none;"/>');
			$('body').append(downdload);
			initTool();
		}else{
			$("#mainsearch").hide();
			$(".l-panel-topbar").hide();
			$(".l-panel-topbar").empty();
		}
	});
	
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "制度版本",
				name : "endDate",
				newline : true,
				type : "select",
				options : {
					data : null,
					cancelable:true
				},
				attr : {
					field : "t.endDate",
					op : ">="
				}
			}, {
				display : '校验公式ID',
				name : "checkId",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 't.checkId',
					op : "like"
				}
			}, {
				display : '总行指标编号',
				name : "sumIndexNo",
				newline : false,
				type : "text",
				labelWidth : "100px",
				cssClass : "field",
				attr : {
					field : 't.sumIndexNo',
					op : "like"
				}
			}]
		});
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid(
				{
					height:"99%",
					width : "100%",
					columns : [{
						display : '校验公式ID',
						name : 'checkId',
						width : '15%',
						align : 'center'
					}, {
						display : '总行指标编号',
						name : 'sumIndexNo',
						width : '15%',
						align : 'center'
					}, {
						display : "分行报表名称",
						name : 'branchRptNm',
						width : '20%',
						align : 'center'
					}, {
						display : '分行指标编号',
						name : 'branchIndexNo',
						width : '15%',
						align : 'center'
					}, {
						display : '开始时间',
						name : 'startDate',
						width : '10%',
						align : 'center'
					}, {
						display : '结束时间',
						name : 'endDate',
						width : '10%',
						align : 'center'
					}, {
						display : '校验描述',
						name : 'checkDesc',
						width : '20%',
						align : 'left'
					}],
					usePager : true,
					checkbox : true,
					dataAction : 'server', //从后台获取数据
					alternatingRow : true, //附加奇偶行效果行
					url : "${ctx}/report/frame/valid/orgmerge/list.json?templateId=" + templateId,
					method : 'post', // get
					sortName : 'sumIndexNo', //第一次默认排序的字段
					sortOrder : 'asc', //排序的方式
					rownumbers : true,
					toolbar : {}
				});
		grid.setHeight($("#center").height() - 58);
	}
	
	function reloadGrid(){
		initSystemVer();
		initToolbar();
		grid.set('parms', {
			condition : ''
		});
		if(window.parent.currentNode){
			grid.set('url', "${ctx}/report/frame/valid/orgmerge/list.json?templateId=" + window.parent.currentNode.params.templateId);
		}
	}

	function initToolbar() {
		var items = [];
		var endDate = $.ligerui.get("endDate").getValue();
		if(("29991231" == endDate) || ("" == endDate)){
			items = [{
				text : '增加',
				click : function() {
					if(window.parent.currentNode){
						window.parent.BIONE.commonOpenDialog('新增总分校验', 'orgMergeAdd', 800, 500,
								"${ctx}/report/frame/valid/orgmerge/new?templateId=" + templateId +"&startDate="+window.parent.currentNode.params.verStartDate);
					}else{
						BIONE.tip("请先选择报表!");
					}
				},
				icon : 'fa-plus'
			}, {
				text : '修改',
				click : function() {
					var selected = grid.getSelectedRows();
					if(selected.length != 1){
						BIONE.tip("请选择一条记录!");
						return ;
					}
					window.parent.BIONE.commonOpenDialog("修改总分校验", "orgMergeAdd", 800, 500,
							"${ctx}/report/frame/valid/orgmerge/new?templateId=" + templateId + "&checkId=" + selected[0].checkId);
				},
				icon : 'fa-pencil-square-o'
			}, {
				text : '删除',
				click : f_delete,
				icon : 'fa-trash-o'
			}, {
				text : '校验公式上传',
				click : f_import,
				icon : 'fa-upload'
			}];
		}
		items.push({
			text : '校验公式下载',
			icon : 'fa-download',
			menu:{
				items:[{
					icon:'export',
					text:'导出所选项',
					click : exp_orgmerge('sel')
				},{
					icon:'export',
					text:'导出当前报表',
					click : exp_orgmerge('all')
				},{
					icon:'export',
					text:'批量导出',
					click : exp_batch
				}]
			}
		});
		items.push({
			text : '查看校验公式',
			click : function() {
				var selected = grid.getSelectedRows();
				if (!selected) {
					BIONE.tip("请先选择需要修改的校验！");
					return;
				}
				if(selected.length != 1){
					BIONE.tip("选择一个校验公式！");
					return ;
				}
				window.parent.BIONE.commonOpenDialog("查看校验公式", "orgMergeAdd",
						800, 500,
						"${ctx}/report/frame/valid/orgmerge/new?templateId="
						+ templateId + "&checkId=" + selected[0].checkId + "&isQuery=" + true);
			},
			icon : 'fa-pencil-square-o'
		});
		BIONE.loadToolbar(grid, items, function() {});
	}
	
	//删除
	function f_delete(){
		var selected = grid.getSelectedRows();
		if (!selected || selected.length == 0) {
			BIONE.tip("请先选择需要删除的校验");
			return;
		}
		$.ligerDialog.confirm("确定删除这些校验吗？", function(yes) {
			if (yes) {
				var ids = [];
				for(var i=0;i<selected.length;i++){
					ids.push(selected[i].checkId);
				}
				$.ajax({
					type : "POST",
					url : "${ctx}/report/frame/valid/orgmerge/delete",
					data : {
						ids : ids.join(",")
					},
					success : function() {
						BIONE.tip("删除成功！");
						reloadGrid();
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						BIONE.tip('删除失败,错误信息:' + textStatus);
					}
				});
			}
		});
	}
	
 	function initSystemVer(){
		if(window.parent.currentNode){
			var busiType = window.parent.currentNode.params.busiType;
		}
		if(busiType){
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/frs/system/cfg/getSystemEndDate",
				dataType : 'json',
				data : {
					busiType : busiType
				},
				type : "post",
				success : function(result){
					if(result){
						$.ligerui.get("endDate").setData(result);
					}
				},
				error:function(){
					BIONE.tip("数据加载异常，请联系系统管理员！");
				}
			});
		}
	}
	 
 	// 校验公式上传
 	function f_import(){
		var $content = $(window);
		var height = $content.height();
		BIONE.commonOpenDialog("校验公式上传", "upload", 600, height-50, "${ctx}/report/frame/valid/orgmerge/impTabWin");
	}
	function exp_orgmerge(expType) {
		var endDate = $("#endDate").val();
		return function () {
			var ids = "all";
			if(expType == 'sel'){
				ids = "";
				var selected = grid.getSelectedRows();
				if (!selected || selected.length == 0) {
					BIONE.tip("请先选择需要下载的记录！");
					return;
				}
				for ( var i = 0; i < selected.length; i++) {
					ids += selected[i].checkId + ",";
				}
			}
			if(expType == 'all' && !window.parent.currentNode){
				BIONE.tip("请先选择需要下载的报表");
				return;
			}

			if (ids.length == 0 && expType == 'sel') {
				return;
			}

			$.ajax({
				type : "POST",
				dataType : "json",
				url : "${ctx}/report/frame/valid/orgmerge/exp",
				data : {
					"ids" : ids,
					"templateId"  : templateId,
					"endDate" : endDate,
					"sumRptNm" : window.parent.currentNode.text
				},
				beforeSend : function(a, b, c) {
					BIONE.showLoading('正在导出数据中...');
				},
				success : function(data) {
					BIONE.hideLoading();
					if (data.filepath == "") {
						BIONE.tip('导出异常');
						return;
					}
					downdload.attr('src', "${ctx}/report/frame/valid/orgmerge/download?fileName=" + data.filepath + "&sumRptNm=" + encodeURI(encodeURI(window.parent.currentNode.text)));
				},
				error : function(result) {
					BIONE.hideLoading();
				}
			});
		}
	}

	function exp_batch(){
		var $content = $(window);
		var height = $content.height();
		BIONE.commonOpenDialog("批量导出", "export", 600, height-50,
				"${ctx}/report/frame/valid/orgmerge/exportBatch");
	}

	function initTool(){
		$("#searchbtn").find("button").click(function(){
			initToolbar();
		});
	}
</script>
<title>Insert title here</title>
</head>
<body>
</body>
</html>