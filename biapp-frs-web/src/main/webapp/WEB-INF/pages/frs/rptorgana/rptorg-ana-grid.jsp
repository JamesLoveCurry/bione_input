<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script>
	var rptId = "${rptId}";
	var tmpId = "${tmpId}";
	var verId = "${verId}";
	var orgNo = "${orgNo}";
	var queryDate = "${queryDate}";
	var busiType = "${busiType}";
	var grid = null;
	var columns = [];//表格列
	var queryOrgNo = [];
	$(function(){
		initGrid();
		initExport();
		initOrgCols();
	});
	
	//初始化grid
	function initGrid() {
		columns = [];
		columns.push({
			isSort: true,
			display: "指标编号",
			name: 'idxNo',
			text : '指标编号',
			hide : 'true',
			sortname : "$IDXNO",
			businessType : 'IDXNO'
		},{
			isSort: true,
			display: "人行指标编号",
			name: 'busiNo',
			text : '指标编号',
			sortname : "$BUSINO",
			businessType : 'BUSINO',
			width: 120
		},{
			isSort: true,
			display: "指标名称",
			name: 'cellNm',
			text : '指标名称',
			sortname : "$IDXNM",
			businessType : 'IDXNM',
			width: 120
		});
		grid = $('#maingrid').ligerGrid(
				{
					toolbar : {},
					width : '100%',
					height : '98%',
					columns : columns,
					usePager : false,
					checkbox : false,
					data :{
						Rows :[]
					},
					dataAction : 'local',
					allowHideColumn : false,
					delayLoad : false,
					mouseoverRowCssClass : null
				});
		var buttons = [{
			text : "导出",
			icon : "export",
			operNo : "idx-show-export",
			click : function() {
				fexport();
			}
		}];
		BIONE.loadToolbar(grid, buttons);
	}
	
	function initExport(){
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	}
	
	//初始化指标名称和编号
	function initGridRowHead(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/datashow/rptOrgAna/getGridRowHead",
			dataType : 'json',
			data : {
				rptId : rptId,
				tmpId : tmpId,
				verId : verId
			},
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载报表指标...");
			},
			success : function(result) {
				if (result) {
					grid.loadData(result);
					BIONE.loading = false;
					BIONE.hideLoading();
					indexQuery();
				}
			},
			error : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
				BIONE.tip("数据加载异常，请联系系统管理员");
			}
		});
	}
	
	//初始化机构列头
	function initOrgCols(){
		if(orgNo){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/datashow/rptOrgAna/getInitOrgCols",
				dataType : 'json',
				data : {
					orgNo : orgNo,
					orgType : busiType
				},
				type : "post",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载下级机构...");
				},
				success : function(result) {
					queryOrgNo = [];
					if (result && result.orglist) {
						var orglist = result.orglist;
						columns.splice(3, columns.length-3);
						for(var i = 0; i < orglist.length; i++){
							queryOrgNo.push(orglist[i].id.orgNo);
							columns.push({
								isSort: true,
								display: orglist[i].orgNm,
								name: orglist[i].id.orgNo,
								text : orglist[i].orgNm,
								businessType : 'ORG',
								width: 120,
								render : function(a, b, c) {
									if (!c) {
										return "Na";
									} else {
										return c;
									}
								}
							});
						}
						grid.set("columns", columns);
						grid.reRender();
						BIONE.loading = false;
						BIONE.hideLoading();
						initGridRowHead();
					}
				},
				error : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
					BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}
	
	//指标数据查询
	function indexQuery(){
		var queryIdxNo = [];//查询指标编号
		var gridData = grid.getData();
		var columns = [];//查询列头
		var gridColumns = grid.getColumns();
		var gridDataList = [];
		if(gridData && (gridData.length > 0)){
			for(var i = 0; i < gridData.length; i++){
				queryIdxNo.push(gridData[i].indexNo);
				var gridDataObj = {
						"indexNo" : gridData[i].indexNo,
						"busiNo" : gridData[i].busiNo,
						"cellNm" : gridData[i].cellNm
				}
				gridDataList.push(gridDataObj);
			}		
			for(var j = 0; j < gridColumns.length; j++){
				columns.push(gridColumns[j].name);
			}	
			if(queryOrgNo.length>0 ){
				$.ajax({
					url: '${ctx}/report/frame/datashow/rptOrgAna/indexQuery',
					type: 'post',
					async : true,
					data: {
						queryOrgNo : JSON2.stringify(queryOrgNo),
						queryIdxNo : JSON2.stringify(queryIdxNo),
						columns : JSON2.stringify(columns),
						gridDataList : JSON2.stringify(gridDataList),
						queryDate : queryDate,
						tmpId : tmpId
					},
					dataType: 'json',
					beforeSend : function() {
						BIONE.loading = true;
						BIONE.showLoading("正在查询数据中...");
					},
					complete : function() {
						BIONE.loading = false;
						BIONE.hideLoading();
					},
					success: function(json) {
						if (json && json.Rows) {
							grid.loadData(json);
						}
					},
					error : function(result, b) {
						BIONE.tip("查询异常");
					}
				});
			}else{
				BIONE.tip("查询日期未选择或未配置查询指标！");
			}
		}
	}
	
	//导出grid函数
	function fexport(){
		var queryResult = grid.getData();
		var cols = [];
		var gridColumns = grid.getColumns();
		for(var i in gridColumns){
			if("IDXNO" != gridColumns[i].businessType){
				var col = {};
				col[gridColumns[i].name] = gridColumns[i].display;
				cols.push(col);
			}
		}
		$.ajax({
			url: '${ctx}/report/frame/datashow/rptOrgAna/resultExport',
			type: 'post',
			data: {
				colums: JSON2.stringify(cols),
				result : JSON2.stringify(queryResult)
			},
			dataType: 'json',
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在生成数据文件中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success: function(json) {
				if(json.fileName){
					var src = '';
					src = "${ctx}/report/frame/datashow/rptOrgAna/download?fileName="+json.fileName;
					downdload.attr('src', src);
				}
				else{
					BIONE.tip("数据文件生成失败");
				}
			},
			error : function(result, b) {
			}
		});
	}
</script>
</head>
<body>
	<div id="template.center">
        <div id="maingrid" class="maingrid"></div>
	</div>
</body>
</html>