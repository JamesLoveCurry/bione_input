<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template22A_BS.jsp">
<head>
<script type="text/javascript" 
	src="${ctx}/js/contextMenu/jquery.contextMenu.min.js"></script>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/contextMenu/jquery.contextMenu.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx }/js/datashow/img/iconfont.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/js/bootstrap3/css/bootstrap.css" />
<style>
body {
  font-size: 12px;
}
</style>
<script>
	var busiType = "03";//默认显示1104机构
	var result;
	var treeTab = null;
	var storeTab = null;
	var downdload = null;
	var columns = [];//表格列
	var type = "";
	var leftTreeObj = null;
	var storeTreeObj = null;
	var grid = null;
	var orgs = JSON2.parse('${orgs}');
	var initFlag = true;
	var queryRules = '';//查询规则
	var initUserOrgNm = "";//用户默认所属机构
	var initUserOrgNo = "";//用户默认所属机构编号
	var tmpId = "";//当前选择报表的报表模板编号
	var queryOrgNo = [];
	var onClickTreeNode = null;//当前点击的树节点
	var unit ={
		"01" : 1,
		"02" : 100,
		"03" : 1000,
		"04" : 10000,
		"05" : 100000000
	};
	
	$(function(){
		initform();
		initRptBusiType();
		initRptVar();
		initExport();
		initTreeTab();
		initTreeSearch();
		initGrid();
		getSystemVer();
		initTool();
		initOrgCols();
	});
	
	function initTool(){
		$('#maingridgrid').height($('#maingridgrid').height() + 20);
		$('#navtab').height($('#table').height() - 10);
        var $height = $('#navtab').height();
        $("#rptIdxTreeContainer").height($height - 100);
	}
	
	//初始化日期维度选择，默认选择当前日期
	function initDateFilter(){
		var nowDate = new Date();
		var year = nowDate.getFullYear();
	    var month = nowDate.getMonth() + 1;
	    var day = nowDate.getDate();   
	    var clock = year + "-";	        
        if(month < 10)
            clock += "0";       
        clock += month + "-";       
        if(day < 10)
            clock += "0";          
        clock += day;
		var drawDate = clock.split("-").join("");
		$("#dimFilter [name='queryDate']").val(drawDate);
	}
	
	//初始化机构维度选择，默认选择当前用户所属机构
	function initOrgFilter(){
		var checkIds =[];
		var selectedNodes =[];
		if(orgs && orgs.length>0){
			for(var i=0 ;i<orgs.length;i++){
				if(busiType == orgs[i].id.orgType){
					checkIds.push(orgs[i].id.orgNo);
					initUserOrgNm = orgs[i].orgNm;
					initUserOrgNo = orgs[i].id.orgNo;
				}
			}
		}
		$.ligerui.get("queryOrg").setValue(initUserOrgNo, initUserOrgNm);
	}
	
	function initTreeTab(){
		treeTab = $('#navtab').ligerTab({
			contextmenu: false,
			onAfterSelectTabItem : function(tabId) {
				if (tabId == 'rptIdx') {
					$("#showConfig").show();
					$("#storeConfig").hide();
				}
			}
		}); 
		treeTab.selectTabItem("rptIdx");
	}
	
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
					width : '97%',
					height : '100%',
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
			text : "全屏查询",
			icon : "search",
			operNo : "idx-show-search",
			click : function() {
				openGrid();
			}
		},{
			text : "查询",
			icon : "search",
			operNo : "idx-show-search",
			click : function() {
				if(onClickTreeNode){
					initGridRowHead(onClickTreeNode);
				}else{
					BIONE.tip("请选择报表");
				}
			}
		},{
			text : "导出",
			icon : "export",
			operNo : "idx-show-export",
			click : function() {
				fexport();
			}
		}];
		BIONE.loadToolbar(grid, buttons);
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
			var queryDate = $("#dimFilter [name='queryDate']").val();
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
	
	//初始化机构列头
	function initOrgCols(){
		var orgNo = $.ligerui.get("queryOrg").getValue();
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
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
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
					}
				},
				error : function() {
					parent.BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}
	
	function initExport(){
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	}
	
	function initTreeSearch(){
		$("#treeSearchIcon").live('click',function(){// 树搜索
			var selectTabId = treeTab.getSelectedTabItemID();
			if('rptIdx' != selectTabId){
				treeTab.selectTabItem('rptIdx');
			}
			var verId = $.ligerui.get("rptVerId").getValue();	
			initRptIdxTree($('#treeSearchInput').val(), verId);
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			var selectTabId = treeTab.getSelectedTabItemID();
			if (event.keyCode == 13) {
				if('rptIdx' != selectTabId){
					treeTab.selectTabItem('rptIdx');
				}
				var verId = $.ligerui.get("rptVerId").getValue();	
				initRptIdxTree($('#treeSearchInput').val(), verId);
	 		}
		});
	}
	
	//初始化业务信息
	function initRptBusiType(){
		$("#rptIdxTreeContainer").before('<div class="l-form" style="margin: 2px;"><ul><li style="width: 40%;">监管业务类型：</li><li style="width: 59%;"><input id="rptBusiType" /></li></ul></div>');
		$("#rptBusiType").ligerComboBox(
				{
					url : "${ctx}/report/frame/datashow/idx/getIdxTaskType.json",
					onSelected : function(id, text) {
						if (id && id != busiType) {
							busiType = id;
							getSystemVer();
						}
					}
				});
		$("#rptBusiType").parent().width("99%");
		$.ligerui.get("rptBusiType").selectValue(busiType);	
	}
	
	//初始化监管制度版本
	function initRptVar(){
		$("#rptIdxTreeContainer").before('<div class="l-form" style="margin: 2px;"><ul><li style="width: 40%;">制度版本选择：</li><li style="width: 59%;"><input id="rptVerId" /></li></ul></div>');
		$("#rptVerId").ligerComboBox(
				{
					onSelected : function(id, text) {
						if(id){
							$('#treeSearchInput').val("");
							initRptIdxTree(null, id);
							//removeAll();
							initOrgFilter();
							initDateFilter();
						}
					}
				});
		$("#rptVerId").parent().width("99%");
	}
	
	//清空全部信息
	function removeAll(){
		columns = [];//表格列
		queryResult = [];
		initGrid();
	}
	
	//初始化报表指标树
	function initRptIdxTree(searchRptNm, rptVerId){
		setting ={
				async:{
					enable:true,
					url:"${ctx}/report/frame/datashow/rptOrgAna/getRptTree.json",
					autoParam:["nodeType", "id", "indexVerId"],
					otherParam:{"busiType":busiType, "searchRptNm": searchRptNm, "rptVerId":rptVerId},
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						if (childNodes){
							for (var i = 0; i < childNodes.length; i++){
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].isParent = childNodes[i].params.isParent == "true" ? true : false;
							}
						}
						return childNodes;
					}
				},
				data:{
					key:{
						name:"text",
						title:"title"
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId",
						rootPId : null
					}
				},
				callback : {
					onClick : function(event, treeId, treeNode, clickFlag) {
						if (treeNode.params.nodeType == "rptMgr") {
							onClickTreeNode = treeNode;
						}
					}
				}
		};
		leftTreeObj = $.fn.zTree.init($("#rptIdxTree"), setting,[]);
	}
	
	//根据业务类型获取对应的版本
	function getSystemVer() {
		if (busiType) {
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frame/design/cfg/getSystemList",
				dataType : 'json',
				data : {
					busiType : busiType
				},
				type : "post",
				success : function(result) {
					if (result != null && result.length > 0) {
						$.ligerui.get("rptVerId").setData(result);
						$.ligerui.get("rptVerId").selectValue(result[result.length-1].id);	
					}
				},
				error : function() {
					parent.BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}
	
	//初始化查询框
	function initform() {
		var field = [
		 			{
		 				display : '数据日期',
		 				name : "queryDate",
		 				newline : false,
		 				type : "date",
		 				options : {
		 					format : "yyyyMMdd"
		 				},
		 				cssClass : "field"
		 			},
		 			{
		 				display : "机构",
		 				name : "queryOrg",
		 				newline : false,
						type : "popup",
		 				options : {
		 					cancelable : false,
		 					onButtonClick : function() {
		 						BIONE.commonOpenDialog("维度过滤", "chooseOrg", 400, 450, 
		 								"${ctx}/report/frame/datashow/rptOrgAna/orgFilter");
		 					}
		 				}
		 			}]
		var mainform = $("#dimFilter").ligerForm({
			fields : field
		});
	}
	
	//初始化指标名称和编号
	function initGridRowHead(treeNode){
		var rptId = treeNode.data.rptId;
		tmpId = treeNode.data.cfgId;
		var verId = $.ligerui.get("rptVerId").getValue();	
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
				parent.BIONE.tip("数据加载异常，请联系系统管理员");
			}
		});
	}
	
	//打开grid页面
	function openGrid(){
		if(onClickTreeNode){
			var height = $(window.top).height() - 20;
			var width = $(window.top).width();
			var rptId = onClickTreeNode.data.rptId;
			tmpId = onClickTreeNode.data.cfgId;
			var verId = $.ligerui.get("rptVerId").getValue();	
			var orgNo = $.ligerui.get("queryOrg").getValue();
			var queryDate = $("#dimFilter [name='queryDate']").val();
			window.top.BIONE.commonOpenDialog("机构分析", "orgAnaGrid", width, height, "${ctx}/report/frame/datashow/rptOrgAna/orgAnaGrid?rptId="+rptId+"&tmpId="+tmpId+"&verId="+verId+"&orgNo="+orgNo+"&queryDate="+queryDate+"&busiType="+busiType, null);
		}else{
			BIONE.tip("请选择报表");
		}
	}
</script>
</head>

<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">指标树</span>
	</div>
	<div id="template.tabitem">
		<div tabid="rptIdx" title="报表指标" style="height: 100%; overflow: auto;">
			<div id="rptIdxTreeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="rptIdxTree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
		<div id="template.right">
			<div id="showConfig" style="height: 100%;width:100%;">
				<div id="content" style="height: 100%;width:100%;">
					<div class="l-form-tabs">
						<ul original-title="" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
							<li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-hover" data-index="0">
								<a href="javascript:void(0)">
									<a d='title3' style = "color: #4bbdfb;padding-left: 20px;padding-right: 0px;">维度选择</a>
								</a>
							</li>
						</ul>
					</div>
					<div id="dimFilter" style="width:99%; margin:2px; border: 1px solid transparent; border-radius: 4px; border-color: #ddd; padding-top: 4px; padding-left: 100px;"></div>
					<div id="panel" style="width:99%; height:100px; margin:2px; display: none;"></div>
					<div id="table" class="frame" style="width: 100%;">
						<div class="win" style="margin-top: 2px;">    
							<div id="maingrid" class="maingrid" oncopy="return false" oncut="return false" style="margin-left: 2px;"></div>
						</div>			
					</div>
				</div>
			</div>
			<div id="storeConfig" style = "height:100%;widht:100%">
				<div id='cover' class='l-tab-loading'
				style='display:block; background:url(${ctx}/images/classics/index/bg_center.jpg) no-repeat center center #ffffff;'></div>
				<div id="store_tab" style="width: 100%; overflow: hidden;"></div>
			</div> 
		</div>
</body>
</html>