<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template22.jsp">
<head>
<%@ include file="/common/spreadjs_load.jsp"%>
<style type="text/css">
.searchtitle img {
	display: block;
	float: left;
}
.searchtitle span {
	display: block;
	float: left;
	margin-top: 1px;
	margin-left: 2px;
	line-height: 19px;
}
.l-tab-links {
/* 	position: absolute; */
}
</style>
<script type="text/javascript">
	var tab = null;
	var rptTreeObj = null;
	var searchObj = {exeFunction : "seniorSearch",searchType : "rpt"};//默认执行seniorSearch方法
	$(function() {
		var $centerDom = $(document);
		gridCenter = $centerDom.height() - 26;
		// 初始化页面布局
		initLayout();
		// 构建报表树
		initTree();
		initTab();
		
		//添加高级搜索按钮
		$(".l-trigger").css("right","26px");
		var innerHtml = '<i class="l-trigger" style="right:20px;width:1px;height:12px;border-left:1px dotted gray;margin-top:4px;"></i>'+
			'<div title="高级筛选" class="l-trigger" style="right:0px;"><div id="highSearchIcon" onclick="javascript:initSeniorSearch();" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/application_form.png) no-repeat 50% 50% transparent;"></div></div>';
		$(".l-trigger").after(innerHtml);
	});
	
	function initSeniorSearch(){
		//添加高级搜索弹框
		BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/rpt/frame/rptSearch/highSearch?searchObj="+JSON2.stringify(searchObj));
	}
	
	function seniorSearch(){//高级搜索
		if(typeof searchObj.rptNum == "undefined"){
			initNodes("" , rptTreeObj);   //公共
		}else{
			initNodes(JSON2.stringify(searchObj) , rptTreeObj , "seniorSearch");
		}
	}
	
	function initTab(){
		tab = $('#right_tab').ligerTab({
			contextmenu: true,
			height: $('#right').height() - 2,
			onAfterAddTabItem: function() {
				$('#cover').hide();
			},
			onAfterRemoveTabItem: function() {
				if (tab.getTabItemCount() == 0) {
					$('#cover').show();
				}
			}
		});
	}
	
	// 初始化页面布局
	var initLayout = function(){
		//初始化树查询按钮事件
		$("#treeSearchIcon").bind("click",function(e){
			searchHandler();
		});
		$("#treeSearchInput").bind("keydown",function(e){
			// 按下回车键进行查询
			var code = e.keyCode;
			if(code == null
					|| typeof code == "undefined"
					|| code != "13"){
				return ;
			}
			searchHandler();
		});
		$("#treeContainer").height($("#treeContainer").height()+30);
		$("#treeSearchbar").find("ul").css("margin-left","0px").css("margin-bottom","0px");
	}
	
/* 	function showtype() {
		$("#treeSearchInput").val("")
		if($("#catalog")[0].checked == true){
			initNodes("" , rptTreeObj , RPT_DEF_SRC_LIB,SHOW_TYPE_CATALOG); 
		}
		else{
			initNodes("" , rptTreeObj , RPT_DEF_SRC_LIB,SHOW_TYPE_LABEL); 
		}
	} */
	
	// 初始化报表树
	var initTree = function(){
		var async = {
			data : {
				keep : {
					parent : true
				},
				key : {
					name : "text",
					title : "title"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId",
					rootPId : null
				}
			},
			view : {
				selectedMulti : false,
				showLine : false
			},
			callback:{
				onClick : treeOnClick
			}
		};
		rptTreeObj = $.fn.zTree.init($("#tree") , async , []);
		initNodes(null , rptTreeObj); 
		searchHandler();
	};
	
	//搜索动作
	function searchHandler(){
		var searchName = $("#treeSearchInput").val();
		initNodes(searchName , rptTreeObj);   //公共
	}
	
	// 加载树节点
	var initNodes = function(searchNm , tree , seniorSearch){
		var url =  "${ctx}/rpt/frame/rptsubmit/getRptTreeSync";
		
		var data = {searchNm : searchNm == null ? "" : searchNm,defSrc : "01"};
		if(typeof seniorSearch != "undefined"){//高级搜索
			url =  "${ctx}/rpt/frame/rptsubmit/getRptTreeSyncPro";
			data = {searchObj : searchNm == null ? "" : searchNm,defSrc : "01"};
		}
		$.ajax({
			cache : false,
			async : true,
			url :url,
			dataType : 'json',
			type : "post",
			data : data,
			success : function(result){
				if(result != null
						&& typeof result != "undefined"){
					// 移除旧节点
					var nodes = tree.getNodes();
					var num = nodes.length;
					for(var i=0;i<num;i++){
						tree.removeNode(nodes[0],false);
					}
					// 渲染新节点
					tree.addNodes(tree.getNodeByParam("id", '0', null) , result , true);
					tree.expandNode(tree.getNodeByParam("id", "0", null),true,false,false);
				}
			},
			error:function(){
				//BIONE.tip("加载失败，请联系系统管理员");
			}
		});
	}
	
	function treeOnClick(event, treeId, treeNode){
		if(treeNode.params.nodeType=="03"){
			tab.addTabItem({
				tabid: treeNode.id,
				text: treeNode.text,
				url: '${ctx}/rpt/frame/rptsubmit/show?rptId=' + treeNode.params.realId+"&rptNm="+encodeURI(encodeURI(treeNode.text)),
				showClose: true
			});
		}
		else{
			treeObj.expandNode(treeNode, true, false, true);
		}
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">报表信息</span>
	</div>
	<div id="template.right">
		<div style="position: relative;">
		<div id='tab_loading' class='l-tab-loading' style='display:none;'></div>
		<div id='cover' class='l-tab-loading' style='display:block; background:url(${ctx}/images/classics/index/report.jpg) no-repeat center center #ffffff;'></div>
		<div id="right_tab"></div>
	</div>
	</div>
</body>
</html>