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
<link rel="stylesheet" href="${ctx}/js/exlabel/exlabel.css">
<script type="text/javascript" src="${ctx}/js/exlabel/exlabel.js"></script>
<script type="text/javascript" src="${ctx}/js/numeral/numeral.js"></script>
<script type="text/javascript" src="${ctx}/js/underscore/underscore-min.js"></script>
<script type="text/javascript"
	src="${ctx}/js/bignumber/bignumber.min.js"></script>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<script>
	var busiType = "00";//默认显示行内机构
	var result;
	var treeTab = null;
	var storeTab = null;
	var downdload = null;
	var dimNos = [];//多个指标的维度交集
	var columns = [];//表格列
	var measureFilterName = [];//度量单位中文名称
	var type = "";
	var leftTreeObj = null;
	var storeTreeObj = null;
	var grid = null;
	var notAllowedIcon = "/images/classics/icons/cancel.gif";
	var allowedIcon = "/images/classics/icons/accept.png";
	var labelFlag = false;
	var searchObj = {exeFunction : "initTree",searchType : "idx"};//默认执行initTree方法
	var searchId = '${searchId}';
	var orgs = JSON2.parse('${orgs}');
	var judge = false;
	var draggingNode = null;
	var chooseIdx = [];
	var dimFilters = [];
	var panel = null;
	var srcCode ='';
	var initFlag = true;
	var queryResult = [];
	var drawDate = "";
	var queryRules = 'everyDay';//查询规则,默认是每天
	var queryDate = [];//查询日期，当不按照日力度查询的时候生效
	var unit ={
		"01" : 1,
		"02" : 100,
		"03" : 1000,
		"04" : 10000,
		"05" : 100000000
	};
	$(function(){
		initBusiType();
		initExport();
		initRadio();
		initFilter();
		initTreeTab();
		initTreeSearch();
		if(searchId){
			judge = true; 
			initTree(searchId);
		}else{
			initTree();
		}
		initStoreTree();
		initPanel();
		initGrid();
		initOrgFilter();
		initDateFilter();
		$("#storeConfig").hide();
		$("#showConfig").show();
	});
	
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
		var selectedNodes = [{id : clock, text : clock}, {id :clock, text : clock}];
		dimFilters["DATE"] = {"selectedNodes" : selectedNodes};
	}
	
	function initOrgFilter(){
		var checkIds =[];
		var selectedNodes =[];
		if(orgs && orgs.length>0){
			for(var i=0 ;i<orgs.length;i++){
				if(busiType == orgs[i].id.orgType){
					checkIds.push(orgs[i].id.orgNo);	
					selectedNodes.push({
						id : orgs[i].id.orgNo,
						text : orgs[i].orgNm
					});
				}
			}
		}
		dimFilters["ORG"]={
			checkIds : checkIds,
			selectedNodes : selectedNodes
		}
	}
	
	function initTreeTab(){
		treeTab = $('#navtab').ligerTab({
			contextmenu: false,
			height: $('#left').height() - 2,
			onAfterSelectTabItem : function(tabId) {
				if (tabId == 'store') {
					initStoreTab();
				}
				if (tabId == 'idx') {
					$("#showConfig").show();
					$("#storeConfig").hide();
					resTree();
				}
			}
		}); 
	}
	
	function initStoreTab(){
		$("#storeConfig").show();
		$("#showConfig").hide();
		if(storeTab == null){
			storeTab = $('#store_tab').ligerTab({
				contextmenu: true,
				height: $('#center').height() - 2,
				onAfterAddTabItem: function() {
					$('#cover').hide();
				},
				onAfterRemoveTabItem: function() {
					if (tmpTab.getTabItemCount() == 0) {
						$('#cover').show();
					}
				}
			});
		}
	}
	
	//初始化标签函数
	function initPanel(){
		panel = $('#panel').exlabel({
			type: 'btn',
			text: 'text',
			value: 'id',
			isEdit : false,
			isInput : false,
			callback: {
				onClick : function(data,flag){
					if(data.id == "DATE" || data.id == "ORG" ){
						return false;
					}
					labelClick(data,flag);
				}
		    }
		});
		panel.add({
			id : "DATE",
			text : "日期"
		});
		panel.add({
			id : "ORG",
			text : "机构编号"
		});
		panel.selectNodes("DATE,ORG",true,false);
	}
	
	function labelClick(e,flag){
		if(flag == true){
			var i=0;
			for(i=0;i<columns.length;i++){
				if(columns[i].businessType == "INDEX" || columns[i].businessType == "MEASURE"){
					break;
				}
			}
			columns.splice(i,0,{
				isSort: true,
				display: e.text,
				name: e.id,
				text : e.text,
				businessType : 'DIM',
				sortname : "$"+e.id,
				width: ((e.text.length + 5)* 12 > 120)? (e.text.length + 5) * 12  : 120
			});
		}else{
			var i=0;
			for(i=0;i<columns.length;i++){
				if(columns[i].name == e.id){
					break;
				}
			}
			columns.splice(i,1);
			//删除所选维度过滤
			delete dimFilters[e.id];	
		}
		setGridColumns();
	}
	
	function initGrid() {
		columns = [];
		columns.push({
			isSort: true,
			display: "日期",
			name: 'DATE',
			text : '日期',
			type : 'date',
			sortname : "$DATE",
			businessType : 'DIM',
			width: 120
		},{
			isSort: true,
			display: "机构",
			name: 'ORG',
			text : '机构',
			type : 'org',
			sortname : "$ORG",
			businessType : 'DIM',
			width: 120
		});
		grid = $('#maingrid').ligerGrid(
				{
					toolbar : {},
					width : '100%',
					columns : columns,
					usePager : true,
					checkbox : false,
					data :{
						Rows :[]
					},
					dataAction : 'local',
					allowHideColumn : false,
					delayLoad : false,
					onBeforeShowData : function() {
		
					},
					onAfterShowData : function() {
					},
					onAfterChangeColumnWidth : function() {
						$(".delBtn").each(
								function() {
									$(this)
											.css(
													"left",
													$(this).parent().parent()
															.width() - 10);
								})
					},
					mouseoverRowCssClass : null
				});
		var buttons = [{
			text : "查询",
			icon : "search",
			operNo : "idx-show-search",
			click : function() {
				indexQuery();
			}
		},{
			text : "收藏",
			icon : "store",
			operNo : "idx-show-store",
			click : function() {
				fstore();
			}
		},{
			text : "导出",
			icon : "export",
			operNo : "idx-show-export",
			click : function() {
				fexport();
			}
		},{
			text : "折线图",
			icon : "add",
			operNo : "idx-show-linechart",
			click : function() {
				if(result && result.length > 0){
					BIONE.commonOpenDialog("图表", "chartDialog", 1100, 500,
							"${ctx}/report/frame/datashow/idx/chart?chartType=01" );
				}
			}
		},{
			text : "柱状图",
			icon : "add",
			operNo : "idx-show-barchart",
			click : function() {
				if(result && result.length > 0){
					BIONE.commonOpenDialog("图表", "chartDialog", 1100, 500,
							"${ctx}/report/frame/datashow/idx/chart?chartType=02" );
				}
			}
		}];
		grid.setHeight($(document).height() - 160);
		BIONE.loadToolbar(grid, buttons);
		addDivToColumn();
	}
	
	function fstore(){
		if(chooseIdx.length <= 0){
			BIONE.tip("未配置指标，无法收藏！");
			return; 
		}
		var req = window.favour = {
			'dimNo': [], 
			'colums': chooseIdx,
			'searchArg' : [],
			'busiType' : busiType
		};
			
		$.each(grid.getColumns(), function (i, n) {
			 if ("DIM" === n.businessType) {
				if ("DATE" == n.name) {
				} else {
					req.searchArg.push({'dimNo': n.name,
						'op' : '01',
						'value' : dimFilters[n.name]?dimFilters[n.name].checkIds:[]
					});
				}
			} 
		});
		BIONE.commonOpenSmallDialog('收藏', 'store'
					, '${ctx}/report/frame/datashow/idx/store');
	}
	
	function fexport(){
		if(window.queryResult.length <= 0){
			BIONE.tip("无可导出数据！");
			return;
		}
		var cols = {};
		var formates = {};
		var unit = {};
		for(var i in  columns){
			cols[columns[i].name] = columns[i].display; 
			if(columns[i].businessType == "DIM"){
				formates[columns[i].name] = "@";
			}
		}
		for(var j in chooseIdx){
			var format = "";
			var val = "";
			if(chooseIdx[j].dataType == "01"){
				format += "#,##";
			}
			if(chooseIdx[j].dataPrecision > 0){
				format += "0.";
				for(var h=0;h<chooseIdx[j].dataPrecision;h++){
					format += "0";
				}
			}
			if(chooseIdx[j].dataType == "02"){
				format += "%";
			}
			if(chooseIdx[j].dataType == "03"){
				format = "0";
			}
			formates[chooseIdx[j].detailId] = format;
			unit[chooseIdx[j].detailId] = chooseIdx[j].dataUnit;
		}
		$.ajax({
			url: '${ctx}/report/frame/datashow/idx/resultExport',
			type: 'post',
			data: {
				colums: JSON2.stringify(cols),
				result : JSON2.stringify(window.queryResult),
				formate: JSON2.stringify(formates),
				unit : JSON2.stringify(unit)
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
					src = "${ctx}/report/frame/datashow/idx/download?fileName="+json.fileName;
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
	
	function clone(obj){
		var o;
		switch(typeof obj){
		case 'undefined': break;
		case 'string'   : o = obj + '';break;
		case 'number'   : o = obj - 0;break;
		case 'boolean'  : o = obj;break;
		case 'object'   :
			if(obj === null){
				o = null;
			}else{
				if(obj instanceof Array){
					o = [];
					for(var i = 0, len = obj.length; i < len; i++){
						o.push(clone(obj[i]));
					}
				}else{
					o = {};
					for(var k in obj){
						o[k] = clone(obj[k]);
					}
				}
			}
			break;
		default:		
			o = obj;break;
		}
		return o;	
	}
	
	function addGridData(queryResult){ // 将搜索结果转换格式添加到grid中
		window.queryResult = queryResult;
		result = clone(queryResult);
		if(queryResult && queryResult.length > 0){
			for(var i=0;i<queryResult.length;i++){
				for(var j=0;j<chooseIdx.length;j++){
					var format = "";
					var val = "";
					if(chooseIdx[j].dataType == "01"){
						format += "0,";
					}
					if(chooseIdx[j].dataPrecision > 0){
						format += "0.";
						for(var h=0;h<chooseIdx[j].dataPrecision;h++){
							format += "0";
						}
					}
					if(chooseIdx[j].dataType == "02"){
						try{
							val = new BigNumber(queryResult[i][chooseIdx[j].detailId]).times(new BigNumber(100)).toNumber();
						}
						catch(e){
							val = 0;
						}
						
					}
					else{
						try{
							val = new BigNumber(queryResult[i][chooseIdx[j].detailId]).dividedBy(new BigNumber(unit[chooseIdx[j].dataUnit])).toNumber();
						}
						catch(e){
							val = 0; 
						}
					}
					var number = numeral(val);
					val = number.format(format);
					if(chooseIdx[j].dataType == "02"){
						val += "%";
					}
					result[i][chooseIdx[j].detailId] = val;
				}
			}
		}
		grid.sortedData = null;//added by fangjuan 20160601'
		grid.set('data',{Rows : result, Total : queryResult.length});	
	}
		
	function indexQuery(){// 指标查询
		var condition = {};
		var cols = {};
		var indexHis = [];
		condition.QueryType = "index";
		if(dimFilters["DATE"] && chooseIdx.length>0 ){
			var searchArg = [];
			var dimNo = [];
			for(var i=0;i<columns.length;i++){
				if(columns[i].businessType == "DIM"){
					var tmp = columns[i].name;
					dimNo.push(tmp);
					if(dimFilters[tmp]){
						if(tmp != "DATE"){
							if(dimFilters[tmp].checkIds.length == 0){
								BIONE.tip(columns[i].display + "维度未选择，请选择！");
								return;
							}
							searchArg.push({DimNo : tmp, Op : '=', Value : dimFilters[tmp].checkIds});
						}else{
							if(('everyDay' != queryRules) && queryDate){//判断是传日期区间还是日期时点值
								searchArg.push({DimNo : tmp, Op : '=', Value : queryDate});
							}else{
								searchArg.push({DimNo : tmp, Op : '>=', Value : [dimFilters["DATE"].selectedNodes[0].id.split("-").join("")]});
								searchArg.push({DimNo : tmp, Op : '<=', Value : [dimFilters["DATE"].selectedNodes[1].id.split("-").join("")]});
							}
						}
					}else{
						BIONE.tip(columns[i].display + "维度未选择，请选择！");
						return;
					}
				}
			}
			condition.DimNo = dimNo;
			
			if(chooseIdx.length > 0){
				var column = [];
				for(var tmp =0 ;tmp< chooseIdx.length; tmp++){
					var obj = {
							ColumNo : chooseIdx[tmp].detailId, 
							IndexNo : chooseIdx[tmp].indexNo,
							SearchArg : searchArg
					};
					var indexNo = "";
					if(chooseIdx[tmp].measureNo){
						obj.MeasureNo = chooseIdx[tmp].measureNo; 
					}
					column.push(obj);
					indexHis.push({indexNo: chooseIdx[tmp].indexNo, indexVerId: chooseIdx[tmp].indexVerId});
				}
				condition.Colums = column;
			}
			for(var tmp in columns){
				cols[columns[tmp].name] = columns[tmp].text;
			}
			searchJson = JSON2.stringify(condition);
			syncSysCode();
			$.ajax({
				url: '${ctx}/report/frame/datashow/idx/search/frsResult',
				type: 'post',
				data: {
					parameter: JSON2.stringify(condition),
					columns : JSON2.stringify(cols),
					indexNos: JSON2.stringify(indexHis),
					srcSysCode : window.srcCode,
					idxCols : JSON2.stringify(chooseIdx)
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
					if (json && json.Code && json.Code == "0000") {
						result = json.Msg;
						addGridData(result);
					}else{
						BIONE.tip(json.Msg);
					}
				},
				error : function(result, b) {
					grid.set('data',{Rows : [], Total : 0});	
					BIONE.tip("查询异常");
				}
			});
		}else{
			BIONE.tip("查询日期未选择或未配置查询指标！");
		}
	}
	
	function initExport(){
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	}
	
	//初始化选择框
	function initRadio(){
		//暂时去掉标签相关内容
		//var $radio = $('<div style="background-image: url(/biapp-frs-web/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border:1px solid #D6D6D6;padding-left:10px;padding-top:3px;height:20px;"><span>目录</span><input type="radio" id="catalog"  name="showtype" value="catalog" style="width:20px;" onclick="showtype()" checked="true"/> <span>标签</span> <input type="radio" id="label" name="showtype" value="label" style="width:20px;" onclick="showtype()"  /></div>');
		var $radio = $('<div style="background-image: url(/biapp-frs-web/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border:1px solid #D6D6D6;padding-left:10px;padding-top:3px;height:20px;"><span>目录</span><input type="radio" id="catalog"  name="showtype" value="catalog" style="width:20px;" onclick="showtype()" checked="true"/></div>');
		$("#treeContainer").before($radio);
	}
	
	function initFilter(){
		//添加高级搜索按钮
		$(".l-trigger").css("right","26px");
		var innerHtml = '<i class="l-trigger" style="right:20px;width:1px;height:12px;border-left:1px dotted gray;margin-top:2px;"></i>'+
			'<div title="高级筛选" class="l-trigger" style="right:0px;"><a id="highSearchIcon" class = "icon-light_off font-size"></a></div>';
		$(".l-trigger").after(innerHtml);
		//添加高级搜索弹框
		$("#highSearchIcon").click(function(){
			if(labelFlag){
				BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/rpt/frame/rptSearch/labelhighSearch?type=idx");
			}
			else{
				BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/rpt/frame/rptSearch/highSearch?searchObj="+JSON2.stringify(searchObj));
			}
		});
	}
	
	function showtype(){
		$("#treeSearchInput").val("");
		if($("#catalog")[0].checked == true){
			initTree();
			labelFlag = false;
		}
		else{
			initLabelTree();
			labelFlag = true;
		}
	}
	
	function initTreeSearch(){
		$("#treeSearchIcon").live('click',function(){// 树搜索
			liger.get('navtab').selectTabItem('idx');
			if($("#catalog")[0].checked == true){
				initTree($('#treeSearchInput').val());
			}
			else{
				initLabelTree($('#treeSearchInput').val());
			}
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			liger.get('navtab').selectTabItem('idx');
			if (event.keyCode == 13) {
				if($("#catalog")[0].checked == true){
					initTree($('#treeSearchInput').val());
				}
				else{
					initLabelTree($('#treeSearchInput').val());
				}
	 		}
		});
	}
	
	function zTreeOnClickIdx(event, treeId, treeNode, clickFlag) {
		
	}
	
	function initLabelIdx(ids,stype){
		type = "labelS";
		var _url = "${ctx}/report/frame/idx/getSyncLabelFilter.json";
		var data = {'ids':ids ,'type':stype,'isShowDim':'1', 'isAuth': '1','isShowMeasure':'1'};
		setting ={
					data : {
							keep : {
								parent : true
							},
							key : {
								name : "text",
								title:"title"
							},
							simpleData : {
								enable : true,
								idKey : "id",
								pIdKey : "upId",
								rootPId : null
							}
					},
					view:{
						selectedMulti:false
					},
					callback:{
						onClick : zTreeOnClickIdx,
						onNodeCreated : function(event, treeId, treeNode) {
							if((treeNode.nodeType == "idxInfo" &&  treeNode.data.indexType != "05") || treeNode.nodeType == "measureInfo"){
								labelInfo = treeNode.text;
								if(treeNode.nodeType == "measureInfo"){
									labelInfo = treeNode.data.indexNm + "." + treeNode.text; 
								}
								setDragDrop("#" + treeNode.tId + "_span", "#maingrid",labelInfo);
							}
							if(treeNode.nodeType == "idxInfo"){
								$("#"+ treeNode.tId + "_ico").bind("click",function(){
									curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
									dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
								});
							}
						}
					}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"),setting,[]);
		BIONE.loadTree(_url,leftTreeObj,data,function(childNodes){
			 if(childNodes){
				 for(var i = 0;i<childNodes.length;i++){
					childNodes[i].nodeType = childNodes[i].params.nodeType;
					childNodes[i].indexVerId = childNodes[i].params.indexVerId;
				}
			}
			return childNodes;
		},false);
	}
	
	function initTree(searchNm,searchObj) {
        var $height = $(window.parent.document).height;
		// $("#treeContainer").height($("#navtab").height()-110);
        $("#treeContainer").height($height-10);
		if(searchNm == null || searchNm == ""){
			if(type != "async"){
				type = "async";
				setting ={
						async:{
							enable:true,
							url:"${ctx}/report/frame/idx/getAsyncTreeIdxShow.json",
							autoParam:["nodeType", "id", "indexVerId"],
							otherParam:{'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "1", "showEmptyFolder":"", "busiType":busiType},
							dataType:"json",
							dataFilter:function(treeId,parentNode,childNodes){
								if(childNodes){
									for(var i = 0;i<childNodes.length;i++){
										childNodes[i].nodeType = childNodes[i].params.nodeType;
										childNodes[i].indexVerId = childNodes[i].params.indexVerId;
									}
								}
								return childNodes;
							}
						},
						data:{
							key:{
								name:"text",
								title:"title"
							}
						},
						view:{
							selectedMulti:false
						},
						callback:{
							onClick : zTreeOnClickIdx,
							onNodeCreated : function(event, treeId, treeNode) {
								if((treeNode.nodeType == "idxInfo" &&  treeNode.data.indexType != "05") || treeNode.nodeType == "measureInfo"){
									labelInfo = treeNode.text;
									if(treeNode.nodeType == "measureInfo"){
										labelInfo = treeNode.data.indexNm + "." + treeNode.text; 
									}
									setDragDrop("#" + treeNode.tId + "_span", "#maingrid",labelInfo);
								}
								if(treeNode.nodeType == "idxInfo"){
									$("#"+ treeNode.tId + "_ico").bind("click",function(){
										curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
										dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
									});
								}
							}
						}
				};
				leftTreeObj = $.fn.zTree.init($("#tree"), setting,[]);
			}
		}
		else{
			var _url = "${ctx}/report/frame/idx/getSyncTree?d="+new Date();
			var data = {'searchNm':searchNm ,'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "1", "showEmptyFolder":"", "busiType":busiType};
			if(searchObj != null && searchObj != ""){
				_url = "${ctx}/report/frame/idx/getSyncTreePro";
				data = {'searchObj':JSON2.stringify(searchObj) ,'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "1", "showEmptyFolder":"", "busiType":busiType};
			}
			if(judge){
				data = {'indexNo':searchNm ,'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "1", "showEmptyFolder":"", "busiType":busiType};
				searchId = null;
				judge = false;
			}
			if(type !="sync"){
				type = "sync";
				setting ={
						data : {
								keep : {
									parent : true
								},
								key : {
									name : "text",
									title:"title"
								},
								simpleData : {
									enable : true,
									idKey : "id",
									pIdKey : "upId",
									rootPId : null
								}
						},
						view:{
							selectedMulti:false
						},
						callback:{
							onClick : zTreeOnClickIdx,
							onNodeCreated : function(event, treeId, treeNode) {
								if((treeNode.nodeType == "idxInfo" &&  treeNode.data.indexType != "05") || treeNode.nodeType == "measureInfo"){
									labelInfo = treeNode.text;
									if(treeNode.nodeType == "measureInfo"){
										labelInfo = treeNode.data.indexNm + "." + treeNode.text; 
									}
									setDragDrop("#" + treeNode.tId + "_span", "#maingrid",labelInfo);
								}
								if(treeNode.nodeType == "idxInfo"){
									$("#"+ treeNode.tId + "_ico").bind("click",function(){
										curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
										dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
									});
								}
							}
						}
				};
				leftTreeObj = $.fn.zTree.init($("#tree"),setting,[]);
			}
			BIONE.loadTree(_url,leftTreeObj,data,function(childNodes){
				 if(childNodes){
					 for(var i = 0;i<childNodes.length;i++){
						childNodes[i].nodeType = childNodes[i].params.nodeType;
						childNodes[i].indexVerId = childNodes[i].params.indexVerId;
						if(childNodes[i].children && childNodes[i].children.length >0){
							for(var j =0 ;j < childNodes[i].children.length; j++){
								childNodes[i].children[j].nodeType = childNodes[i].children[j].params.nodeType;
								childNodes[i].children[j].indexVerId = childNodes[i].children[j].params.indexVerId;
								if(childNodes[i].children && childNodes[i].children.length >0){
									for(var j =0 ;j < childNodes[i].children.length; j++){
										childNodes[i].children[j].nodeType = childNodes[i].children[j].params.nodeType;
										childNodes[i].children[j].indexVerId = childNodes[i].children[j].params.indexVerId;
									}
								}
							}
						}
					}
				}
				return childNodes;
			},false);
		}
	}

	function initLabelTree(searchNm,searchObj) {
		if(searchNm == null || searchNm == ""){
			if(type != "label"){
				type = "label";
			}
			var setting ={
					async:{
						enable:true,
						url:"${ctx}/report/frame/idx/getAsyncLabelTree.json",
						autoParam:["nodeType", "id", "indexVerId"],
						otherParam:{'isShowDim':1, 'isShowMeasure':1, 'isPublish':'1', "busiType":busiType},
						dataType:"json",
						dataFilter:function(treeId,parentNode,childNodes){
							if(childNodes){
								for(var i = 0;i<childNodes.length;i++){
									childNodes[i].nodeType = childNodes[i].params.nodeType;
									childNodes[i].indexVerId = childNodes[i].params.indexVerId;
								}
							}
							return childNodes;
						}
					},
					data:{
						key:{
							name:"text"
						}
					},
					view:{
						selectedMulti:false
					},
					callback:{
						onClick : zTreeOnClickIdx,
						onNodeCreated : function(event, treeId, treeNode) {
							if((treeNode.nodeType == "idxInfo" &&  treeNode.data.indexType != "05") || treeNode.nodeType == "measureInfo"){
								labelInfo = treeNode.text;
								if(treeNode.nodeType == "measureInfo"){
									labelInfo = treeNode.data.indexNm + "." + treeNode.text; 
								}
								setDragDrop("#" + treeNode.tId + "_span", "#maingrid",labelInfo);
							}
							if(treeNode.nodeType == "idxInfo"){
								$("#"+ treeNode.tId + "_ico").bind("click",function(){
									curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
									dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
								});
							}
						}
					}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting,[]);
		}
		else{
			var _url = "${ctx}/report/frame/idx/getSyncLabelTree.json";
			var data = {'searchNm':searchNm, 'isShowDim':1, 'isShowMeasure':1, "busiType":busiType};
			if(type != "labelS"){
				type = "labelS";
				setting ={
						data : {
								keep : {
									parent : true
								},
								key : {
									name : "text",
									title:"title"
								},
								simpleData : {
									enable : true,
									idKey : "id",
									pIdKey : "upId",
									rootPId : null
								}
						},
						view:{
							selectedMulti:false
						},
						callback:{
							onClick : zTreeOnClickIdx,
							onNodeCreated : function(event, treeId, treeNode) {
								if((treeNode.nodeType == "idxInfo" &&  treeNode.data.indexType != "05") || treeNode.nodeType == "measureInfo"){
									labelInfo = treeNode.text;
									if(treeNode.nodeType == "measureInfo"){
										labelInfo = treeNode.data.indexNm + "." + treeNode.text; 
									}
									setDragDrop("#" + treeNode.tId + "_span", "#maingrid",labelInfo);
								}
								if(treeNode.nodeType == "idxInfo"){
									$("#"+ treeNode.tId + "_ico").bind("click",function(){
										curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
										dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
									});
								}
							}
						}
				};
				leftTreeObj = $.fn.zTree.init($("#tree"),setting,[]);
			}
			BIONE.loadTree(_url,leftTreeObj,data,function(childNodes){
				 if(childNodes){
					 for(var i = 0;i<childNodes.length;i++){
						childNodes[i].nodeType = childNodes[i].params.nodeType;
						childNodes[i].indexVerId = childNodes[i].params.indexVerId;
						if(childNodes[i].children && childNodes[i].children.length >0){
							for(var j =0 ;j < childNodes[i].children.length; j++){
								childNodes[i].children[j].nodeType = childNodes[i].children[j].params.nodeType;
								childNodes[i].children[j].indexVerId = childNodes[i].children[j].params.indexVerId;
							}
						}
					}
				}
				return childNodes;
			},false);
		}
	}
	
	function initStoreTree(param) {
		$("#storetreeContainer").height($("#navtab").height()-90);
		storeTreeObj = $.fn.zTree.init($('#store-tree'), {
			data: {
	 			key: {
	 				name: 'text'
	 			},
	 			simpleData: {
	 				enable: true,
	 				idKey: 'id',
	 				pIdKey: 'upId'
	 			}
	 		},
 			callback: {
 				onClick: function(event, treeId, treeNode) {
 					if ('catalog' == treeNode.params.type) {
 						return;
 					}
 					storeTab.addTabItem({
						tabid: treeNode.id,
						text: treeNode.text,
						url: '${ctx}/report/frame/datashow/idx/show?instanceId=' + treeNode.id,
						showClose: true
					});
 				}
 			}
		});
		BIONE.loadTree('${ctx}/report/frame/datashow/idx/store/tree',storeTreeObj,{param: param});
	}
	
	//添加拖拽控制
	function setDragDrop(dom, receiveDom,labelInfo) {
		if (typeof dom == "undefined" || dom == null) {
			return;
		}
		$(dom).ligerDrag({
			proxy : function(target, g, e) {
				var defaultName = "指标";
				var proxyLabel = "${ctx}" + notAllowedIcon;
				var targetTitle = labelInfo;
				var proxyHtml = $('<div style="width：150;position: absolute;padding-left: 10px;color: #183152;font-weight: bold;height: 20px;line-height: 19px;overflow: hidden;background: #E5EFFE ;border: 1px solid #DDDDDD;border-top:none; z-index:9999;"><span class="dragimage_span" style="position:absolute;width:16px;height:16px;left:5px;top:2px;background:url('
								+ proxyLabel
								+ ')" ></span><span style="padding-left: 14px;">'
								+ targetTitle + '</span></div>');
				var div = $(proxyHtml);
				div.appendTo('body');
				return div;
			},
			revert : false,
			receive : receiveDom,
			onStartDrag : function(current, e) {
				// 获取拖拽树节点信息
				var treeAId = current.target.attr("id");
				var treeId = treeAId;
				if (treeAId) {
					var strsTmp = treeAId.split("_");
					var treeId = treeAId;
					if (strsTmp.length > 1) {
							var newStrsTmp = [];
							for (var i = 0; i < strsTmp.length - 1; i++) {
								newStrsTmp.push(strsTmp[i]);
							}
							treeId = newStrsTmp.join("_");
					}
					draggingNode = leftTreeObj.getNodeByTId(treeId);
				}
			},
			onDragEnter : function(receive, source, e) {
				var allowLabel = "${ctx}" + allowedIcon;
				source.children(".dragimage_span").css("background","url('" + allowLabel + "')");
			},
			onDragOver : function(receive, source, e) {
				var allowLabel = "${ctx}" + allowedIcon;
				source.children(".dragimage_span").css("background","url('" + allowLabel + "')");
			},
			onDragLeave : function(rceive, source, e) {
				var notAllowLabel = "${ctx}" + notAllowedIcon;
				source.children(".dragimage_span").css("background","url('" + notAllowLabel + "')");
			},
			onDrop : function(obj, target, e) {
				var treeNode = draggingNode;
				if(treeNode.params.nodeType == "measureInfo"){
					var measureNode = treeNode;
					treeNode = treeNode.getParentNode();
					measureNode.params.dimNos = treeNode.params.dimNos;
					addIdx(measureNode, true);
				}else{
					addIdx(treeNode, true);
				} 
			}
		});
	}

	function renderLabelPad(){//重绘维度标签区
		panel.removeAll();
		panel.add({
			id :　"DATE",
			text : "日期"
		});
		panel.add({
			id :　"ORG",
			text : "机构编号"
		});
		var params = "";
		for(var i=0;i<dimNos.length;i++){
			params += dimNos[i] + ",";
		}
		$.ajax({
			url: '${ctx}/report/frame/datashow/idx/getDimNm',
			type: 'post',
			data: {
				dimNos: params
			},
			dataType: 'json',
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success: function(json) {
				for(var i=0;i<dimNos.length;i++){
					if(dimNos[i] != "INDEXNO" && dimNos[i] != "DATE"){
						panel.add({
							id :　dimNos[i],
							text : json[dimNos[i]]
						});
					}
				}
				var ids = [];
				for(var x=0;x<columns.length;x++){
					for(var y=0;y<dimNos.length;y++){
						if(columns[x].name == dimNos[y]){
							ids.push(columns[x].name);
							break;
						}
					}
				}
				panel.selectNodes("DATE,ORG",true,false);
				panel.selectNodes(ids.join(","),true);
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function syncSysCode(){
		srcCode = "01";
		for(var i = 0 ;i < chooseIdx.length;i++ ){
			if(chooseIdx[i].data.idxBelongType == "01"){
				srcCode = "01";
				break;
			}
			else{
				if(srcCode == "01"){
					srcCode = chooseIdx[i].data.idxBelongType;
				}
				else if(srcCode != chooseIdx[i].data.idxBelongType){
					srcCode = "01";
					break;
				}
			}
		}
	}
	
	function initDrawDate(indexNo){
		$.ajax({
			async : true,
			url: '${ctx}//report/frame/datashow/idx/getDrawDate',
			type: 'post',
			data: {
				indexNo : indexNo
			},
			dataType: 'json',
			success: function(json) {
				var date = json.drawDate;
				if(!window.drawDate){
					window.drawDate = date;
					var selectedNodes=[];
					selectedNodes.push({
						id : date,
						text : date
					});
					selectedNodes.push({
						id : date,
						text : date
					});
					dimFilters["DATE"]={
						selectedNodes : selectedNodes
					};
				}
				else{
					if(window.drRawDate > date){
						window.drawDate = date;
						var selectedNodes =[];
						selectedNodes.push({
							id : date,
							text : date
						});
						selectedNodes.push({
							id : date,
							text : date
						});
						dimFilters["DATE"]={
							selectedNodes : selectedNodes
						};
						
					}
				}
				
			}
		});
	}
	
	function addIdx(treeNode, isRenderLabel) {
		var colId = uuid.v1().split("-").join("");
		var text = treeNode.data.indexNm;
		var indexNo = treeNode.data.id.indexNo;
		initDrawDate(indexNo);
		var measureNo = "";
		if(treeNode.nodeType == "measureInfo"){
			text += "."+treeNode.text;
			measureNo = treeNode.id;
		}
		if(!treeNode.data.dataUnit)
			treeNode.data.dataUnit = "01";
		if(!treeNode.data.dataType)
			treeNode.data.dataType = "01";
		var idxCol = {
			detailId : colId,
			indexAlias : text,
			data : treeNode.data,
			type : treeNode.nodeType,
			indexNo : indexNo,
			measureNo : measureNo,
			indexVerId : treeNode.data.id.indexVerId,
			dimNos : treeNode.params.dimNos,
			dataUnit : treeNode.data.dataUnit,
			dataType : treeNode.data.dataType,
			dataPrecision : 2,
			ruleId : '1',
			timeMeasureId : '1',
			modeId : '1',
			isPassyear :""
		}
		chooseIdx.push(idxCol);
		//构造维度
		if(isRenderLabel){
			if(dimNos.length == 0){
				dimNos = idxCol.dimNos.split(",");
			}
			else{
				dimNos = _.intersection(dimNos, idxCol.dimNos.split(","));
			}
			renderLabelPad();
		}
		//移除掉非公共的维度列
		syncDimCol();
		
		var businessType = "";
		if(measureNo){//有度量
			businessType = "MEASURE";
		}else{
			businessType = "INDEX";
		}
		columns.push({
			isSort: true,
			display: idxCol.indexAlias,
			name: idxCol.detailId,
			indexNo : idxCol.indexNo,
			indexVerId : idxCol.indexVerId,
			businessType : 'MEASURE',
			width: ((idxCol.indexAlias.length * 12 + 80) > 120)? (idxCol.indexAlias.length * 12+80) : 120
		});
		syncSysCode();
		setGridColumns();
	}
	
	function setGridColumns(){
		window.queryResult = [];
		grid.set("data", {Rows:[]});
		grid.set("columns", columns);
		grid.reRender();
		addDivToColumn();
	}
	
	function addDivToColumn() {
		for (var i = 0; i < columns.length; i++) {
			var columnName = columns[i].name;
			var businessType = columns[i].businessType;
			var indexNo = "";
			var indexVerId = "";
			if(columns[i].indexNo){
				indexNo = columns[i].indexNo;
				indexVerId = columns[i].indexVerId;
			}
			addColBtn(columnName, businessType, indexNo, indexVerId);
		}
	}

	function addColBtn(columnName, businessType, indexNo, indexVerId) {
		var img = $("td[columnname=" + columnName
				+ "] .l-grid-hd-cell-inner > #delBtn");
		if (img.length == 0) {
			var $selectedLi = $("td[columnname=" + columnName
					+ "] .l-grid-hd-cell-inner ");
			var top = 0 - $(".l-panel-topbar").height()
			- $selectedLi.position().top + 13;
			if(businessType != "DIM"){
				var delImg = $(
						"<img src='${ctx}/images/classics/icons/icons_label_cross.png' colId = '"+columnName+"' style='width:7px;height:7px' class='l-grid-dim-filter' />")
						.attr('title', '删除');
				delImg.click(function() {
					deleteCol(this, $(this).attr("colId"));
				});
				var deldiv = $("<div id='delBtn' class='delBtn' style='display:block;position: relative;cursor: pointer;z-index:5;'/>");
				deldiv.css("left", ($selectedLi.width() - 10)).css(
						"top",
						0 - $(".l-panel-topbar").height()
								- $selectedLi.position().top + 10).css("width",
						"7px");
				deldiv.append(delImg);
				$selectedLi.append(deldiv);
				top = 0 - $(".l-panel-topbar").height()
				- $selectedLi.position().top + 5;
			}
			if(businessType == "DIM"){
				var cofImg = $(
						"<img src='${ctx}/images/classics/icons/icons_filter.png' colId = '"+columnName+"' style='width:16px;height:16px' class='l-grid-dim-filter' />")
						.attr('title', '筛选');
				cofImg.click(function() {
					f_open_filter(columnName);
				});
				var cofdiv = $("<div id='cofdiv' style='display:block;position: relative;cursor: pointer;z-index:5;'/>");
				cofdiv.css("left", "10px").css(
						"top",
						top).css("width",
						"7px");
				cofdiv.append(cofImg);
				$selectedLi.append(cofdiv);
			}
			else{
				var cofImg = $(
						"<img src='${ctx}/images/classics/icons/cog.png' colId = '"+columnName+"' style='width:16px;height:16px' class='l-grid-dim-filter' />")
						.attr('title', '配置');
				cofImg.click(function() {
					BIONE.commonOpenDialog("指标配置", "attrEdit", 700, 500, 
							"${ctx}/report/frame/datashow/idx/config?id="+ columnName + "&indexNo=" + indexNo + "&indexVerId=" + indexVerId);
				});
				var cofdiv = $("<div id='cofdiv' style='display:block;position: relative;cursor: pointer;z-index:5;'/>");
				cofdiv.css("left", "10px").css(
						"top",
						0 - $(".l-panel-topbar").height()
								- $selectedLi.position().top + 5).css("width",
						"7px");
				cofdiv.append(cofImg);
				$selectedLi.append(cofdiv);
			}
		}
		
		function f_open_filter(dimNo){
			if(dimNo != "DATE"){
				BIONE.commonOpenDialog("维度过滤", "chooseOrg", 400, 500, 
						"${ctx}/report/frame/datashow/idx/dimFilter?dimNo=" + dimNo ,null, function(){
				});
			}else{
				BIONE.commonOpenDialog("维度过滤", "chooseOrg", 650, 400, 
						"${ctx}/report/frame/datashow/idx/dimFilter?dimNo=" + dimNo ,null, function(){
				});
			}
		}
		
		function deleteCol(g, colId) {
			for(var i in chooseIdx){
				if(chooseIdx[i].detailId == colId){
					break;
				}
			}
			chooseIdx.splice(i,1);
			for (i = 0; i < columns.length; i++) {
				if (columns[i].name == colId) {
					break;
				}
			}
			if(chooseIdx.length <= 0){
				window.drawDate = "";
			}
			columns.splice(i, 1);
			syncLabel();
			syncDimCol();
			setGridColumns();
		}
	}
	
	function deleteColByCid(colId) {
		for(var i in chooseIdx){
			if(chooseIdx[i].detailId == colId){
				break;
			}
		}
		chooseIdx.splice(i,1);
		for (i = 0; i < columns.length; i++) {
			if (columns[i].name == colId) {
				break;
			}
		}
		if(chooseIdx.length <= 0){
			window.drawDate = "";
		}
		columns.splice(i, 1);
		syncLabel();
		syncDimCol();
		setGridColumns();
	}
	
	function getIdx(id){
		for(var i in chooseIdx){
			if(chooseIdx[i].detailId == id){
				return chooseIdx[i];
			}
		}
	}
	
	function setIdx(id,idxCol){
		for(var i in chooseIdx){
			if(chooseIdx[i].detailId == id){
				chooseIdx[i] = idxCol;
			}
		}
	}
	
	function syncLabel(){
		dimNos = [];
		for (var i = 0; i < chooseIdx.length; i ++){
			if(dimNos.length == 0){
				dimNos = chooseIdx[i].dimNos.split(",");
			}else{
				dimNos = _.intersection(dimNos, chooseIdx[i].dimNos.split(","));
			}
		}
		renderLabelPad();
	}
	
	function syncDimCol(){
		//移除掉非公共的维度列
		for(var x =0;x<columns.length;x++){
			if(columns[x].businessType == 'DIM' && columns[x].name != "DATE" && columns[x].name != "ORG"){
				var y = 0;
				for(y=0;y<dimNos.length;y++){
					if(columns[x].name == dimNos[y]){
						break;
					}
				}
				if(y >= dimNos.length){
					delete dimFilters[columns[x].name];
					columns.splice(x,1);
					x--;
				}
			}
		}
	}
	
	function initBusiType(){
		$("#treeContainer").before('<div class="l-form" style="margin: 2px;"><ul><li style="width: 40%;">业务类型选择：</li><li style="width: 59%;"><input id="busiType" /></li></ul></div>');
		$("#busiType").ligerComboBox(
				{
					url : "${ctx}/report/frame/datashow/idx/busiTypeList.json?isPublic=Y",
					onSelected : function(id, text) {
						if (id && id != busiType) {
							busiType = id;
							$('#treeSearchInput').val("");
							searchObj = {exeFunction : "initTree",searchType : "idx"};//默认执行initTree方法
							removeAll();
							resTree();
						}
					}
				});
		$("#busiType").parent().width("99%");
		$.ligerui.get("busiType").selectValue(busiType);
	}
	
	function removeAll(){
		dimNos = [];//多个指标的维度交集
		columns = [];//表格列
		measureFilterName = [];//度量单位中文名称
		chooseIdx = [];
		dimFilters = [];
		panel.removeAll();
		initPanel();
		initGrid();
	}
	
	function resTree(){
		$("#catalog")[0].checked = true;
		type = "async";
		setting ={
				async:{
					enable:true,
					url:"${ctx}/report/frame/idx/getAsyncTreeIdxShow.json",
					autoParam:["nodeType", "id", "indexVerId"],
					otherParam:{'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "1", "showEmptyFolder":"","busiType":busiType},
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].indexVerId = childNodes[i].params.indexVerId;
							}
						}
						return childNodes;
					}
				},
				data:{
					key:{
						name:"text",
						title:"title"
					}
				},
				view:{
					selectedMulti:false
				},
				callback:{
					onClick : zTreeOnClickIdx,
					onNodeCreated : function(event, treeId, treeNode) {
						if((treeNode.nodeType == "idxInfo" &&  treeNode.data.indexType != "05") || treeNode.nodeType == "measureInfo"){
							labelInfo = treeNode.text;
							if(treeNode.nodeType == "measureInfo"){
								labelInfo = treeNode.data.indexNm + "." + treeNode.text; 
							}
							setDragDrop("#" + treeNode.tId + "_span", "#maingrid",labelInfo);
						}
						if(treeNode.nodeType == "idxInfo"){
							$("#"+ treeNode.tId + "_ico").bind("click",function(){
								curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
								dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
							});
						}
					}
				}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting,[]);
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
		<div tabid="idx" title="指标" style="height: 100%; overflow: auto;">
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
		
		<div tabid="store" title="收藏" style="height: 100%; overflow: auto;">
			<div id="storetreeContainer"
					style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="store-tree"
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
					<div id="panel" style="width:99%;height:100px;margin:2px;">
					</div>
					<div id="table" class="frame" style="width: 100%;">
						<div class="win" style="margin-top: 2px;">
							<div id="maingrid" class="maingrid" oncopy="return false" oncut="return false"></div>
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