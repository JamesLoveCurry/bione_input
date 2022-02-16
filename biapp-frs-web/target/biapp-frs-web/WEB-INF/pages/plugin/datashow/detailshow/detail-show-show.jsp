<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>

<meta name="decorator" content="/template/template14.jsp">
<head>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/daterangepicker/daterangepicker.css" />
	<link rel="stylesheet" href="${ctx}/js/exlabel/exlabel.css">
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<script type="text/javascript" src="${ctx}/js/datashow/ParamComp.js"></script>
<script type="text/javascript" src="${ctx}/js/require/require.js"></script>


<style>
* {
  -webkit-box-sizing: content-box;
  -moz-box-sizing: content-box;
  box-sizing: content-box;
}
#center{
	background-color : #F1F1F1;
}
.toggleBtn {
	padding-right: 10px;
	cursor: pointer;
	float: right;
	position: relative;
	margin-top: 8px;
	background: url('${ctx}/images/classics/ligerui/toggle.gif') no-repeat
		0px 0px;
	width: 9px;
	height: 10px;
}

.togglebtn-down {
	background-position: 0px -10px;
}
</style>
<script type="text/javascript">
	var grid = null;
	var colInfos = {};
	var columns = [];
	var sqlcolumns = [];
	var params = [];
	var filterInfo = {};
	var colSorts = [];
	var colSums = [];
	var templateId = "${templateId}";
	var templateType = "";
	var paramId = "";
	var sql = "";
	var dsId ="";
	var colflag = true;
	var downdload = null;
	var usePager = true;
	var setId = "";
	var srcCode = "";
	$(function() {
		initData();
		initDownLoad();
	});
	
	//初始化下载模块
	function initDownLoad(){
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	}
	
	function initData() {
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/datashow/detail/getTmpInfo",
			dataType : 'json',
			data: {
				templateId : templateId
			},
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				if(result.configs.length == "0"){
					
				}else{
					setId = result.configs[0].setId;
					if(setId != ""){
					    $.ajax({
					    	cache : false,
					    	async : true,
					    	url : "${ctx}/report/frame/datashow/detail/getSrcCode",
					    	dataType : 'json',
					    	data: {
					    		setId : setId
					    	},
					    	type : "get",
					    	success : function(result){
					    		srcCode = result.srcCode;
					    	},
					    	error : function(result, b) {
					    		//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					    	}
					    });
					 }
				}
				templateType = result.tmp.templateType;
				if(result.tmp.isPage == "0"){
					window.usePager = false;
				}
				if(result.tmp.templateType == "01"){
					for(var i in result.configs){
						colInfos[result.configs[i].id.cfgId] = result.configs[i];
					}
					params = result.searchs;
					for(var i in params){
						params[i].uuid = params[i].id.paramId;
						if(params[i].required == "Y")
							params[i].required = true;
						else
							params[i].required = false;
						if(params[i].checkbox == "Y")
							params[i].checkbox = "true";
						else
							params[i].checkbox = "false";
						params[i].defValue = params[i].defVal;
					}
					colSorts = result.sorts;
					colSums = result.sums;
					if(result.filter && result.filter.filtInfo)
						filterInfo = JSON2.parse(result.filter.filtInfo);
					createParams("searchForm", params);
					if(params == null || params.length <=0){
						$("#searchView").hide();
						initGrid("${ctx}/report/frame/datashow/detail/showView",false);
						grid.setHeight($(document).height() - 20);
						setColumn();
						f_run();
					}
					else{
						initGrid("${ctx}/report/frame/datashow/detail/showView",true);
						setColumn();
					}
				}
				else{
					window.sql = result.sql.querysql;
					window.dsId = result.sql.dsId;
					if(result.sql.paramtmpId){
						window.paramId = result.sql.paramtmpId;
						createPage("searchForm", result.sql.paramtmpId);
						initGrid("${ctx}/report/frame/datashow/detail/showSql",true);
						grid.setHeight($(document).height() - $("#searchDiv").height()- 40);
					}
					else{
						initGrid("${ctx}/report/frame/datashow/detail/showSql",false);
						grid.setHeight($(document).height() - 20);
					}
					if(!result.sql.paramtmpId){
						$("#searchView").hide();
						f_run();
					}
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function initSearch() {
		$(".toggleBtn").bind("click",function() {
			if ($(this).hasClass("togglebtn-down")) {
				$(this).removeClass("togglebtn-down");
				$("#searchDiv").slideToggle('fast',function() {
					if (grid) {
						grid.setHeight($(document).height()- $("#searchDiv").height()- 40);
					}
				});
			} else {
				$(this).addClass("togglebtn-down");
				$("#searchDiv").slideToggle('fast',function() {
					if (grid) {
						grid.setHeight($(document).height() - 40);
					}
				});
			}
		});
	}

	function createParams(divName, params) {
		$('#temps-loading').css('display', 'block');
		var pJson = [];
		for ( var i in params) {
			var paramid = uuid.v1()
			paramid = paramid.split("-").join("");
			params[i].paramId = paramid;
			pJson.push(ParamTmp.generateModuleTmp(params[i]));
		}
		require.config({
			baseUrl : '${ctx}/js/',
			paths : {
				jquery : 'jquery/jquery-1.7.1.min',
				JSON2 : 'bione/json2.min'
			},
			shim : {
				JSON2 : {
					exports : 'JSON2'
				}
			}
		});
		require([ 'jquery', 'JSON2', '../plugin/js/template/viewMain' ], function() {
			checkBoxId = [];
			$('#' + divName).templateView({
				data : pJson
			});
			for ( var i in pJson) {
				if (pJson[i].checkbox == "true"
						|| pJson[i].isMultiSelect == "true") {
					checkBoxId.push(pJson[i].name);
				}
			}
			$.metadata.setType("attr", "validate");
			$('#temps-loading').css('display', 'none');
		});
	}
	
	
	function f_run(){
		if(templateType == "01"){
			var configs = [];
			for ( var i in colInfos) {
				configs.push(colInfos[i]);
			}
			try {
				BIONE.validate($("#searchForm"));
				grid.setParm("newPageSize",null);
				if(!window.usePager){
					grid.setParm("pagesize",2000);
					grid.options.pagesize=2000;
				}
				if (params.length <= 0 || $("#searchForm").valid()) {
					if (params.length > 0){
						var searchArgs = $('#searchForm').getJson();
						var sargs = [];
						for ( var i =0; i < searchArgs.length;i++) {
							if(searchArgs[i].name == "@rowNum"){
								if(window.usePager)
									grid.setParm("newPageSize",searchArgs[i].value);
								else{
									grid.setParm("pagesize",searchArgs[i].value);
									grid.options.pagesize=searchArgs[i].value;
								}
								continue;
							}
							var info = searchArgs[i].name.split("|");
							if (info.length >= 2) { 
								searchArgs[i].name = info[0];
								searchArgs[i].colId = info[1];
							}
							sargs.push(searchArgs[i]);
						}
						grid.setParm("searchInfo", JSON2.stringify(sargs));
					}
					grid.setParm("config", JSON2.stringify(configs));
					grid.setParm("filterInfo", JSON2.stringify(filterInfo));
					if (colSorts.length > 0){
						grid.setParm("sort", JSON2.stringify(colSorts));
					}
					if (colSums.length > 0){
						grid.setParm("sum", JSON2.stringify(colSums));
					}
					grid.loadData();
				}
			} 
			catch (e) {
			}
		}
		else{
			colflag = false;
			if(window.paramId != ""){
				BIONE.validate($("#searchForm"));
				if ($("#searchForm").valid()) {
					var searchArgs = $('#searchForm').getJson();
					grid.setParm("searchArgs",JSON2.stringify(searchArgs));
				}
				else{
					return;
				}
			}
			if(!window.usePager){
				grid.setParm("pagesize",2000);
				grid.options.pagesize=2000;
			}
			grid.setParm("sql",sql);
			grid.setParm("dsId",dsId);
			grid.loadData();
		}
	}
	
	
	function f_export(){
		var data = {};
		var url = "";
		if(templateType == "01"){
			var configs = [];
			for ( var i in colInfos) {
				configs.push(colInfos[i]);
			}
			try {
				BIONE.validate($("#searchForm"));
				if (params.length <= 0 || $("#searchForm").valid()) {
					if (params.length > 0){
						var searchArgs = $('#searchForm').getJson();
						var sargs = [];
						for ( var i = 0;i < searchArgs.length;i++) {
							if(searchArgs[i].name == "@rowNum"){
								data.pagesize = searchArgs[i].value;
								continue;
							}
							var info = searchArgs[i].name.split("|");
							if (info.length >= 2) {
								searchArgs[i].name = info[0];
								searchArgs[i].colId = info[1];
							}
							sargs.push(searchArgs[i]);
						}
						data.searchInfo = JSON2.stringify(sargs);
					}
					data.config = JSON2.stringify(configs);
					data.filterInfo = JSON2.stringify(filterInfo);
					if (colSorts.length > 0)
						data.sort = JSON2.stringify(colSorts);
					}
					if (colSums.length > 0){
						data.sum = JSON2.stringify(colSums);
					}
					url = "${ctx}/report/frame/datashow/detail/resultExport";
				} catch (e) {
				}
		}
		else{
			data.sql = sql;
			data.dsId = dsId;
			if(window.paramId != ""){
				BIONE.validate($("#searchForm"));
				if ($("#searchForm").valid()) {
					var searchArgs = $('#searchForm').getJson();
					data.searchArgs = JSON2
							.stringify(searchArgs);
				}
				else{
					return;
				}
			}
			url = "${ctx}/report/frame/datashow/detail/sqlExport";
		}
		
		downLoadAttach(data,url);//调用下载方式
	}


	function f_exportCSV(){
		var data = {};
		var url = "";
		if(templateType == "01"){
			var configs = [];
			for ( var i in colInfos) {
				configs.push(colInfos[i]);
			}
			try {
				BIONE.validate($("#searchForm"));
				if (params.length <= 0 || $("#searchForm").valid()) {
					if (params.length > 0){
						var searchArgs = $('#searchForm').getJson();
						var sargs = [];
						for ( var i = 0;i < searchArgs.length;i++) {
							if(searchArgs[i].name == "@rowNum"){
								data.pagesize = searchArgs[i].value;
								continue;
							}
							var info = searchArgs[i].name.split("|");
							if (info.length >= 2) {
								searchArgs[i].name = info[0];
								searchArgs[i].colId = info[1];
							}
							sargs.push(searchArgs[i]);
						}
						data.searchInfo = JSON2.stringify(sargs);
					}
					data.config = JSON2.stringify(configs);
					data.filterInfo = JSON2.stringify(filterInfo);
					if (colSorts.length > 0)
						data.sort = JSON2.stringify(colSorts);
					}
					if (colSums.length > 0){
						data.sum = JSON2.stringify(colSums);
					}
					url = "${ctx}/report/frame/datashow/detail/csvExport"
				} catch (e) {
				}
		}
		else{
			data.sql = sql;
			data.dsId = dsId;
			if(window.paramId != ""){
				BIONE.validate($("#searchForm"));
				if ($("#searchForm").valid()) {
					var searchArgs = $('#searchForm').getJson();
					data.searchArgs = JSON2
							.stringify(searchArgs);
				}
				else{
					return;
				}
			}
			url = "${ctx}/report/frame/datashow/detail/sqlCsvExport"
		}
		downLoadAttach(data,url);//调用下载方式
	}

	//导出文件
	function downLoadAttach(data,url){
		if(data.pagesize <= 20000){
			data.isBigData = "N";
			$.ajax({
				cache : false,
				async : true,
				url : url,
				dataType : 'json',
				data: data,
				type : "post",
				beforeSend : function() {
					BIONE.showLoading("数据文件生成中，请稍等");
				},
				complete : function() {
					BIONE.hideLoading();
				},
				success : function(result) {
					if(!result.path || result.path==""){
						BIONE.tip("数据文件生成失败");
						return;
					}
					else{
						var src = '';
						src = "${ctx}/report/frame/datashow/detail/download?path="+result.path;
						downdload.attr('src', src);
					}
				},
				error : function(result, b) {
					BIONE.tip("数据文件生成失败");
				}
			});
		}
		else{
			$.ajax({//先判断数据量在选择下载方式
				cache : false,
				async : false,
				url : "${ctx}/report/frame/datashow/detail/getExpDataCount",
				dataType : 'json',
				data : data,
				type : 'post',
				success : function(res){
					if(res.dataNum <= 20000){//数据小于2万走常规下载
						data.isBigData = "N";
						$.ajax({
							cache : false,
							async : true,
							url : url,
							dataType : 'json',
							data: data,
							type : "post",
							beforeSend : function() {
								BIONE.showLoading("数据文件生成中，请稍等");
							},
							complete : function() {
								BIONE.hideLoading();
							},
							success : function(result) {
								if(!result.path || result.path==""){
									BIONE.tip("数据文件生成失败");
									return;
								}
								else{
									var src = '';
									src = "${ctx}/report/frame/datashow/detail/download?path="+result.path;
									downdload.attr('src', src);
								}
							},
							error : function(result, b) {
								BIONE.tip("数据文件生成失败");
							}
						});
					}else{
						data.isBigData = "Y";
				 		$.ajax({
							cache : false,
							async : true,
							url : url,
							dataType : 'json',
							data: data,
							type : "post",
							beforeSend : function() {
								BIONE.tip("数据文件生成中，请等待消息生成附件！");
							},
							success : function(result) {
								if(result.path && result.path != ""){
									window.top.refreshMsg();
									BIONE.tip("有新消息！");
								}
							},
							error : function(result, b) {
								BIONE.tip("数据文件生成失败");
							}
						});
					}
				}
			});
		}
	}
	
	function initGrid(url,flag) {
		var columns = [];
		var config = {
				width : '100%',
				columns : columns,
				usePager : window.usePager,
				checkbox : false,
				dataAction : 'server',
				allowHideColumn : false,
				delayLoad : true,
				data :{
					Rows :[],
					Total :0
				},
				url : url,
				onSuccess:function(result){
					if(result.error){
						BIONE.tip(result.error);
						return;
					}
					if(!colflag && result.column){
						var columns = [];
						for(var i in result.column){
							var column = {
									display : result.column[i],
									name : "param"+i,
									width : '20%',
									align : "left"
							};
							columns.push(column);
							grid.set("columns", columns);
							grid.reRender();
						}
						colflag = true;
					}
				}
		};
		config.toolbar = {};
		grid = $('#grid').ligerGrid(config);
		var buttons = [];
		if(flag){
			buttons = [{
				text : "查询",
				icon : "search3",
				click : function() {
					f_run();
				}
			}];
			if(templateType == "01"){
				buttons.push({
					text : "重置",
					icon : "refresh2",
					click : function() {
						$("#searchForm").resetForm();
					}
				});
				buttons.push({
					text : '高级筛选',
					click : function() {
						BIONE
								.commonOpenDialog("高级筛选", "filterEdit", 800,
										450,
										"${ctx}/report/frame/datashow/detail/filterconfig");
					},
					icon : "config"
				});
			}
		}
		
		
		
		buttons.push({
			text : "导出EXCEL",
			icon : "export",
			click : function() {
				f_export();
			}
		});
		buttons.push({
			text : "导出CSV",
			icon : "export",
			click : function() {
				f_exportCSV();
			}
		});
		grid.setHeight($(document).height() - $("#searchDiv").height() - 40);
		BIONE.loadToolbar(grid, buttons);
		
	}

	
	
	function setColumn() {
		for(var i in colInfos){
			var column = {
				display : colInfos[i].displayNm,
				name : colInfos[i].id.cfgId,
				width : colInfos[i].width,
				align : "left"
			};
			columns.push(column);
		}
		grid.set("columns", columns);
		try{
			grid.reRender();
		}
		catch(e){
			
		}
	}
	
	function createPage(divName, paramtmpId) {
		$('#temps-loading').css('display', 'block');
		require.config({
			baseUrl : '${ctx}/js/',
			paths : {
				jquery : 'jquery/jquery-1.7.1.min',
				JSON2 : 'bione/json2.min'
			},
			shim : {
				JSON2 : {
					exports : 'JSON2'
				}
			}
		});
		require([ 'jquery', 'JSON2', '../plugin/js/template/viewMain' ], function() {
			$(function() {
				if (paramtmpId) {
					$.ajax({
						cache : false,
						async : true,
						url : "${ctx}/report/frame/param/templates/" + paramtmpId + "?type=view",
						dataType : 'json',
						success : function(data) {
							try {
								$('#' + divName).templateView({
									data : JSON2.parse(data.paramJson)
								});
								var params = JSON2.parse(data.paramJson);
								for(var i in params){
									if(params[i].checkbox == "true" || params[i].isMultiSelect == "true"){
										checkBoxId.push(params[i].name);
									}
								}
								
							} catch(e) {
							} finally {
								$('#temps-loading').css('display', 'none');
							}
						}
					});
				}
			});
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="view" oncopy="return false" oncut="return false" >
			<div id="searchView">
				<div id="lefttable" width="100%" border="0">
					<div width="100%"
						style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
						<div
							style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
							<img src="${ctx }/images/classics/icons/chart_pie_edit.png" />
						</div>
						<div>
							<span
								style="font-size: 12px; float: left; position: relative; line-height: 30px; padding-left: 2px">
								查询条件 </span>
						</div>
						<div class="toggleBtn"></div>
					</div>
				</div>
				<div id="searchDiv" style="height: 160px;">
					<div style="background-color: #fff; height: 160px; overflow-y: auto;">
						<div id='temps-loading' class='l-tab-loading' style="display: block;height:160px;top:68px;"></div>
						<form id="searchForm" style="width: 99%;"></form>
					</div>

				</div>
			</div>
			<div id="showGrid">
				<div id="grid"></div>
			</div>
		</div>
	</div>
</body>
</html>