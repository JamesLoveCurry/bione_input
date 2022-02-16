<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template22A_BS.jsp">
<head>
<script>
jQuery.validator.addMethod("greaterThan", function(value, element, params) {
	if (value == '' || value == null) {
		return true;
	}
	var ele = $("[name=" + params + "]");
	return value >= ele.val() ? true : false;
	}, "结束时间应当大于开始时间");
	var type = "";
	var navtab1;
	var tree_icon = "${ctx}/images/classics/icons/house.png";
	var leftTreeObj = null;
	var grid = null;
	var tabChangeFlag;
	//var labelFlag = false;
	$(function(){
		$(".l-text.l-text-date").toggle("hide");
		templateshow();
		templateinit();
		initValidTypeTab();
		initSearchForm();
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	//搜索表单,应用ligerui样式
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : "机构名称",
				name : "orgNm",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : 'ORG_NM'
					}
			},{
				display : '开始时间',
				name : "startDate",
				newline : false,
				type : "date",
				options : {
					format : "yyyyMMdd"
				},
				cssClass : "field",
				attr : {
					op : ">=",
					field : 'data_date'
				}
			},{
				display : '结束时间',
				name : "endDate",
				newline : false,
				type : "date",
				options : {
					format : "yyyyMMdd"
				},
				cssClass : "field",
				attr : {
					op : "<=",
					field : 'data_date'
				},
				validate : {
					greaterThan : "startDate"
				}
			}]
		});
		//$("#startDate").val(window.parent.startDate);
		//$("#endDate").val(window.parent.endDate);
	};
	
	function initGrid(_tabId,_type,_id,_nm) {
		var columnArr =[{
			display : "校验对象",
			name : "indexNm",
			width : "20%",
			align : "center"
		}];
		columnArr.push({
		    display : "数据日期",
		    name : "dataDate",
		    width : "16%",
		    align : "center"
		}, {
			display : "机构名称",
			name : "orgNm",
			width : "20%",
			align : "center"
		}, {
			display : "校验结果",
			name : "checkSts",
			width : "15%",
			align : "center",
			render : function(data) {
				if (data.checkSts == "01")
					return "等待运行";
				if (data.checkSts == "02")
					return "运行中";
				if (data.checkSts == "03")
					return "成功";
				if (data.checkSts == "04")
					return "失败";
				if (data.checkSts == "05")
					return "通过";
				if (data.checkSts == "06")
					return "未通过";
			}
		},{
			display : '操作',
			width : "15%",
			align : 'center',
			render : function(row){
				if(row.checkSts == '05'){
					return "";
				}else{
					return "<a href='javascript:void(0)' style='color :blue' class='link' onclick='detail(\""+row.dataDate+"\",\"" + row.orgNo + "\",\"" + row.orgNm + "\",\"" + row.indexNo + "\",\"" + row.indexNm + "\",\"" + row.checkType + "\")'>"+'详情'+"</a>";
				}
			}
		});
		
		var  url_ = "${ctx}/report/frame/datashow/valid/getCheckInfoList.json?tabId="
					+ _tabId+"&indexNo=" + _id + "&indexNm=" + _nm;
		
		grid = $("#maingrid").ligerGrid({
			checkbox : false,
			columns : columnArr,
			dataAction : 'server',  //从后台获取数据
			usePager : true,        //服务器分页
			alternatingRow : true,  //附加奇偶行效果行
			colDraggable : true,    //是否允许表头拖拽
			url :url_ ,
			sortName : 'dataDate',   //第一次默认排序的字段
			sortOrder : 'desc',      //排序的方式
            //delayLoad : true,     //延迟加载，若为true，则初始化grid后再加上grid.loadData();
			//pageParmName : 'page',
			//pagesizeParmName : 'pagesize',
			rownumbers : true,      //是否显示行序号
			width : '100%'
		});
		grid.setHeight($("#center").height() - 110);
	}
		
	function initValidTypeTab(){
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/datashow/valid/getValidTypeTabs.json?d="
					+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			success : function(result) {
				var data = null;
				if (result) {
					data = result.data;
				}
				if (data && data.length > 0) {
					for ( var i = 0; i < data.length; i++) {
						tabChangeFlag = "00"; // tab 是否可切换的标识
						navtab1 = $("#navtab").ligerTab({
							onAfterSelectTabItem : function(tabId) {
								if (tabId == '01') {
									tabChangeFlag = "01";
									initGrid();
								}
								if (tabId == '02') {
									tabChangeFlag = "02";
									initGrid();
								}
								if (tabId == '05') {
									tabChangeFlag = "05";
									initGrid();
								}
							}
						});
						var subTreeId = "#vt-" + data[i].objDefNo;
						var appendHtml = "<div style='overflow: auto;height:100%;'><div id='"+ data[i].objDefNo
							+ "_Container' style='width: 100%; height:95%;overflow: auto; clear: both; background-color: #FFFFFF;'><div id='vt-"+ data[i].objDefNo
							+ "' class='ztree' style='font-size: 12; background-color: #FFFFFF; width: 95%''></div></div></div>";
					
						navtab1.addTabItem({
							tabid : data[i].objDefNo,
							content : appendHtml,
							text : data[i].objDefName,
							showClose : false
						});
						initTree(data[i].objDefNo, "#vt-" + data[i].objDefNo, data[i].beanName, true);
						var $content = $('.l-tab-content-item');
						// $content.height($content.parent().height()+$("#treeSearchInput").height()-2);
                        var $height = $(window.parent.document).height();
                        $content.height($height-10);
						//加搜索栏
						var searchInput = "searchInput" + data[i].objDefNo;
						var searchIcon = "searchIcon" + data[i].objDefNo; //placeholder="请输入指标编号或名称"
						$(subTreeId).parent().prepend('<div style="position: relative;"><input id="'+searchInput+'" type="text"  style="width: 98.5%;" /><img id="'+searchIcon+'" style="position: absolute;right: 0;top: 0;cursor: pointer;" src="${ctx}/images/classics/icons/find.png"></div>');
						$("#" + searchIcon).data("validType",data[i].objDefNo);
						$("#" + searchIcon).data("beanName",data[i].beanName);
						$("#" + searchIcon).data("subTreeId",subTreeId);
						$("#" + searchIcon).click(function() {
							var searchNm = $("#searchInput"+$(this).data("validType")).val();
							if(searchNm){
								initSyncTree($(this).data("validType"), $(this).data("beanName"), $(this).data("subTreeId"), searchNm);
							}else{
								initTree($(this).data("validType"), "#vt-" + $(this).data("validType"), $(this).data("beanName"));
							}
						});
						$("#" + searchInput).data("validType",data[i].objDefNo);
						$("#" + searchInput).data("beanName",data[i].beanName);
						$("#" + searchInput).data("subTreeId",subTreeId);
						$("#" + searchInput).bind('keydown', function(event) {
							if (event.keyCode == 13) {
								var searchNm = $("#searchInput"+$(this).data("validType")).val();
								if(searchNm){
									initSyncTree($(this).data("validType"), $(this).data("beanName"), $(this).data("subTreeId"), searchNm);
								}else{
									initTree($(this).data("validType"), "#vt-" + $(this).data("validType"), $(this).data("beanName"));
								}
							}
						});
					}
					//设置默认选中tab为第一个tab
					navtab1.selectTabItem(data[0].objDefNo);
					initTree(data[0].objDefNo, "#vt-" + data[0].objDefNo, data[0].beanName, true);
					tabChangeFlag = "01";
				}
			},
			error : function(result, b) {
				////BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function initTree(index, subTreeId, beanName, loadAllNode){
		if ($(subTreeId)) {//构造资源树
			var setting = {
					async:{
						enable : true,
						url : "${ctx}/report/frame/datashow/valid/getIdxAsyncTree.json",
						autoParam:["id", "indexVerId","params"],
						otherParam:{"beanName" : beanName,validType : index},
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
					check : {
						enable : false,
						chkStyle : "checkbox",
						chkboxType : {
							"Y" : "s",
							"N" : "s"
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
						onClick:zTreeOnClick,
						beforeClick : function(treeId, treeNode, clickFlag){
							return true;
						}
					}
				};
				leftTreeObj = $.fn.zTree.init($(subTreeId), setting,[{
					id : "0",
					text : "授权校验树",
					title : "授权校验树",
					icon : tree_icon,
					nocheck : true,
					isParent: true
			      } 
			    ]);
		}
	}
	function initSyncTree(validType, beanName, subTreeId, searchNm) {
		var _url = "${ctx}/report/frame/validgroup/loadSyncTree.json";
		var data = {validType : validType, beanName : beanName, searchNm : searchNm};
		setting ={
			check : {
				enable : false,
				chkStyle : "checkbox",
				chkboxType : {
					"Y" : "s",
					"N" : "s"
				}
			},
			data : {
				keep : {
					parent : true
				},
				key : {
					name : "text"
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
				onClick:zTreeOnClick,
				beforeClick : function(treeId, treeNode, clickFlag){
					return true;
				}
			}
		};
		window[validType + "_tree"] = $.fn.zTree.init($(subTreeId),setting,[]);
		BIONE.loadTree(_url, window[validType + "_tree"], data, function(childNodes){
			if(childNodes){
				for(var i = 0;i<childNodes.length;i++){
					childNodes[i].open = true;
				}
			}
			return childNodes;
		},false);
	}
	
	function initTreeSearch(){
		$("#treeSearchIcon").live('click',function(){// 树搜索
			if($('#treeSearchInput').val()==""){
				liger.get('navtab').selectTabItem(tabChangeFlag);
			}else{
				if(tabChangeFlag = "01"){
					initSyncTree("01", "logicValidImpl", "#vt-01", $('#treeSearchInput').val());
				}
				if(tabChangeFlag = "02"){
					initSyncTree("02", "warnValidImpl", "#vt-02", $('#treeSearchInput').val());
				}
				if(tabChangeFlag = "05"){
					initSyncTree("05", "planValidImpl", "#vt-05", $('#treeSearchInput').val());
				}
			}
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			if (event.keyCode == 13) {
				if($('#treeSearchInput').val()==""){
					liger.get('navtab').selectTabItem(tabChangeFlag);
				}else{
					if(tabChangeFlag = "01"){
						initSyncTree("01", "logicValidImpl", "#vt-01", $('#treeSearchInput').val());
					}
					if(tabChangeFlag = "02"){
						initSyncTree("02", "warnValidImpl", "#vt-02", $('#treeSearchInput').val());
					}
					if(tabChangeFlag = "05"){
						initSyncTree("05", "planValidImpl", "#vt-05", $('#treeSearchInput').val());
					}
				}
	 		}
		});
	}
	
	function zTreeOnClick(event, treeId, treeNode, clickFlag) {
		if(treeNode.id != '0' && treeNode.id != tabChangeFlag+'@0'){
			if(treeNode.params.idxType == "05"){
				BIONE.tip("该指标为总账指标，请选择度量！");
			}else{
				if(treeNode.id.indexOf("@")== -1){
					if(treeNode.nodeType == "idxInfo" ){
						initGrid(treeId,treeNode.nodeType,treeNode.id,treeNode.text);
					}else if(treeNode.nodeType == "measureInfo"){
						var measureText = treeNode.getParentNode().text+"."+treeNode.text;
						initGrid(treeId,treeNode.nodeType,treeNode.id,measureText);
					}else if(treeNode.nodeType == "check"){
						initCheckTab(treeId,treeNode.nodeType,treeNode.id,treeNode.text);
					}
				}
				if(treeNode.id.indexOf("@")!= -1){
					if(treeNode.params.nodeType == "idxInfo" ){
						initGrid(treeId,treeNode.params.nodeType,treeNode.id.substring(3,treeNode.id.length),treeNode.text);
					}else if(treeNode.params.nodeType == "measureInfo"){
						var measureText = treeNode.getParentNode().text+"."+treeNode.text;
						initGrid(treeId,treeNode.params.nodeType,treeNode.id.substring(3,treeNode.id.length),measureText);
					}else if(treeNode.params.nodeType == "check"){
						initCheckTab(treeId,treeNode.params.nodeType,treeNode.id.substring(3,treeNode.id.length),treeNode.text);
					}
				}
			}
		}
	}

	function initCheckTab(_tabId,_type,_id,_nm) {
		//window.startDate = $("#startDate").val();
		//window.endDate = $("#endDate").val();
		BIONE.commonOpenDialog( "\"" + _nm+ "\"" + "校验结果明细","editTreeDetail",1000,500,
				"${ctx}/report/frame/datashow/valid/logicTree?tabId=" + _tabId+
						"&nodeType="+_type+"&checkId="+_id+"&checkNm="+_nm);
	}
	function detail(dataDate,orgNo,orgNm,indexNo,indexNm, checkType) {
		//window.startDate = $("#startDate").val();
		//window.endDate = $("#endDate").val();
		if(checkType == '01' || checkType == '05' ){
			BIONE.commonOpenDialog( "\"" + indexNm+"-"+dataDate+"-"+ orgNm+ "\"" + "校验结果明细","editDetail",1000,500,
					"${ctx}/report/frame/datashow/valid/logicDetail?indexNo=" + indexNo+
					"&indexNm="+indexNm+"&dataDate="+dataDate+"&orgNo="+orgNo+"&checkType="+checkType);
		}
		if(checkType == '02'){
			BIONE.commonOpenDialog( "\"" + indexNm+"-"+dataDate+"-"+ orgNm+ "\"" + "校验结果明细","editDetail",1000,500,
					"${ctx}/report/frame/datashow/valid/warnDetail?indexNo=" + indexNo+
							"&dataDate="+dataDate+"&orgNo="+orgNo+"&checkType="+checkType);
		}
		
	}
	function templateinit() {
		$(".l-dialog-btn").live('mouseover', function() {
		    $(this).addClass("l-dialog-btn-over");
		}).live('mouseout', function() {
		    $(this).removeClass("l-dialog-btn-over");
		});
		$(".l-dialog-tc .l-dialog-close").live('mouseover', function() {
		    $(this).addClass("l-dialog-close-over");
		}).live('mouseout', function() {
		    $(this).removeClass("l-dialog-close-over");
		});
		$(".searchtitle .togglebtn").live(
			'click',
			function() {
			    var searchbox = $(this).parent().nextAll(
				    "div.searchbox:first");
			    var centerHeight = $("#center").height();
			    if ($(this).hasClass("togglebtn-down")) {
				$(this).removeClass("togglebtn-down");
				searchbox.slideToggle('fast', function() {

				    if (grid) {
					grid.setHeight(centerHeight
						- $("#mainsearch").height() - 8);
				    }
				});
			    } else {
				$(this).addClass("togglebtn-down");
				searchbox.slideToggle('fast', function() {

				    if (grid) {
					grid.setHeight(centerHeight
						- $("#mainsearch").height() - 3);
				    }
				});
			    }
			});
	    }
	    function templateshow() {
            var $height = $(window.parent.document).height();
		    $("#center").height($height-8);
			if (grid) {
			    var centerHeight = $("#center").height();
			    grid.setHeight(centerHeight - $("#mainsearch").height() - 8);
			}
	    }
	
</script>
</head>

<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">指标校验目录</span>
	</div>
	<div id="navtab" style="width: 100%; overflow: hidden;">
		</div>
	
	<div id="template.right">
	     <div id="mainsearch">
			<div class="searchtitle">
				<img src="${ctx}/images/classics/icons/find.png" /> <span>搜索</span>
				<div class="togglebtn">&nbsp;</div>
			</div>
			<!-- <div class="navline" style="margin-bottom: 4px;"></div> -->
			<div id="searchbox" class="searchbox">
				<form id="formsearch">
					<div id="search"></div>
					<div class="l-clear"></div>
				</form>
				<div id="searchbtn" class="searchbtn"></div>
			</div>

		</div>
		<div class="content">
			<div id="maingrid" class="maingrid"></div>
		</div>
		</div>
</body>
</html>