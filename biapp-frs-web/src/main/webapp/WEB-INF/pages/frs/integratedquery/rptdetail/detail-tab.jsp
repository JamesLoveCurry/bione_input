<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<link rel="stylesheet" href="${ctx}/css/classics/jqueryui/smoothness/jquery-ui.min.css">
<script src="${ctx}/js/jqueryUI/jquery.ui.core.js"></script>
<script src="${ctx}/js/jqueryUI/jquery.ui.datepicker.js"></script>
<script type="text/javascript">
	
	var rptId = "${rptId}";
	var templateId = "${templateId}";
	var type = "${type}";
	var templateType = "02";
	var verStartTime = "";  // 版本开始时间
	var pageFlag = window.parent[0].pageFlag;
	var rptNm = "";
	var tableNameEn = "";
	// houzh 增加操作标识
	var operFlag = "${operFlag}";
	var tempRptId = "${tempRptId}";
	var tableNameEn = "${tableNameEn}";
	var catalogId = "${catalogId}";
	var catalogNm = "${catalogNm}";
	var checkedSearch = [];
	var uncheckedSearch = [];
	var checkedShow = [];
	var uncheckedShow = [];
	var checkedSearchCN = [];
	var uncheckedSearchCN = [];
	var checkedShowCN = [];
	var uncheckedShowCN = [];
	var frameHeight;
	
	var tabObj;
	var baseInfo4Upt = null;

	
	var frameSrcs = [];
	//基本信息
	frameSrcs["tabBase"] = "${ctx}/frs/submitConfig/detailrpt/showPage?flag=detail-tab-base&catalogId="+catalogId+"&catalogNm="+encodeURI(encodeURI(catalogNm));
	//查询项配置
	frameSrcs["tabSearch"] = "${ctx}/frs/submitConfig/detailrpt/showPage?flag=detail-tab-search&rptId="+"${rptId}";
	//显示列配置
	frameSrcs["tabShow"] = "${ctx}/frs/submitConfig/detailrpt/showPage?flag=detail-tab-show";
	
	$(function() {
		frameHeight = $(document).height() - 75;
		// 初始化页签
		initTab();
		// 初始化底部按钮
		initBtns();
	});
	// 初始化底部按钮
	function initBtns() {
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("rptEdit");
			}
		});
			buttons.push({
				text : '保存并退出',
				onclick : function(){
					rpt_save(true)
				}
			});
		BIONE.addFormButtons(buttons);
	}
	// 初始化页签
	function initTab() {
		$("#tabBase").height(frameHeight);
		$("#tabSearch").height(frameHeight);
		$("#tabShow").height(frameHeight);
		tabObj = $('#tab').ligerTab({
			onBeforeSelectTabItem : function(tabId){
				
				var frameSrc = $("#"+tabId).attr("src");
				if( frameSrc == ""
						|| frameSrc == null
						|| typeof frameSrc == "undefined"){
					$("#"+tabId).attr("src" , frameSrcs[tabId]);
				} else if(tabId == "tabSearch"
						&& document.getElementById("tabSearch").contentWindow.templateType != templateType){
					tabObj.reload("tabSearch");
				}else if(tabId == "tabShow"
					&& document.getElementById("tabShow").contentWindow.templateType != templateType){
					tabObj.reload("tabShow");
				}
			}
		});
		// 选中指定页签
		if(type != null
				&& typeof type != "undefined"
				&& "2" == type){
			tabObj.selectTabItem("tabSearch");
		} else if(type != null
				&& typeof type != "undefined"
					&& "3" == type){
			tabObj.selectTabItem("tabShow");
		}else {
			tabObj.selectTabItem("tabBase");
		}
	}
	
	
	// 保存
	function rpt_save(exit) {
		if(tabObj == null || typeof tabObj == "undefined"){
			return ;
		}
		if(rptId){
			// 修改
			var tabBaseObj = generateBaseContext();
			if($("#tabBase").attr("src") == null
					|| $("#tabBase").attr("src") == ""){
				tabObj.selectTabItem("tabBase");
				return ;
			}  else if($("#tabSearch").attr("src") == null
					|| $("#tabSearch").attr("src") == ""){
				tabObj.selectTabItem("tabSearch");
				return ;
			}else if($("#tabShow").attr("src") == null || $("#tabShow").attr("src") == ""){
				tabObj.selectTabItem("tabShow");
				return ;
			}
			// 报表设计器信息整合
			var searchObj = generateSearchObjContext();
			// 获取编辑的 editCol
			var showObj = generateShowObjContext();
			
			saveHandler(tabBaseObj , searchObj , exit, showObj);
		}else{		
			// 基本信息校验整合
			var tabBaseObj = generateBaseContext();
			if(tabBaseObj){
				// 新增
				if($("#tabBase").attr("src") == null
						|| $("#tabBase").attr("src") == ""){
					tabObj.selectTabItem("tabBase");
					return ;
				}  else if($("#tabSearch").attr("src") == null
						|| $("#tabSearch").attr("src") == ""){
					tabObj.selectTabItem("tabSearch");
					return ;
				}else if($("#tabShow").attr("src") == null || $("#tabShow").attr("src") == ""){
					tabObj.selectTabItem("tabShow");
					return ;
				}
				// 报表设计器信息整合
				var searchObj = generateSearchObjContext();
				// 获取编辑的 editCol
				var showObj = generateShowObjContext();
				 if(tabBaseObj == null
						|| searchObj == null){
					return ;
				} 
				// 进行保存
				 saveHandler(tabBaseObj , searchObj , exit, showObj);
			}
		}
	}
	
	function generateBaseContext(){
		var baseContext = document.getElementById("tabBase").contentWindow;
		var tabBaseObj = baseContext.prepareDatas4Save();   //调用tab子页面方法用于保存数据
		if(tabBaseObj == null){
			tabObj.selectTabItem("tabBase");
			return null;
		}
		return tabBaseObj;
	}
	
	 function generateSearchObjContext(){
		var designContext = document.getElementById("tabSearch").contentWindow;
		var tabSearchObj = designContext.prepareDatas4Save();  //调用tab子页面方法用于保存数据
		if(tabSearchObj == null){
			tabObj.selectTabItem("tabSearch");
			return null;
		}
		return tabSearchObj;
	}  
	 
	 function generateShowObjContext(){
			var showContext = document.getElementById("tabShow").contentWindow;
			var showObj = showContext.prepareDatas4Save();  //调用tab子页面方法用于保存数据
			if(showObj == null){
				tabObj.selectTabItem("tabShow");
				return null;
			}
			return showObj;
		} 
	 
	
// 	 //保存具体方法
	function saveHandler(tabBaseObj , tabSearchObj , exit, showObj){
		var data = {};
		if(tabBaseObj != null){
			var objTmp = {};
			jQuery.extend(objTmp , tabBaseObj);
			data.baseObj = encodeURI(encodeURI(JSON2.stringify(objTmp)));
		}
		 if(tabSearchObj != null && typeof tabSearchObj != "undefined"){
			 data.searchObj = encodeURI(encodeURI(JSON2.stringify(tabSearchObj)));
		}
		if(showObj!=null && typeof showObj != "undefined"){
 			data.showObj = encodeURI(encodeURI(JSON2.stringify(showObj)));
		}
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/frs/submitConfig/detailrpt/saveEdit?rptId="+rptId+"&operFlag="+operFlag,
			dataType : 'json',
			data : data,
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在保存，请稍后..");
			},
			success : function(result){
				BIONE.tip("维护成功");
				if (typeof BIONE != 'undefined') {
					BIONE.loading = false;
					BIONE.hideLoading();
				}
				if(exit === true){
					parent.document.findIframeByTitle("明细数据查询配置")[0].contentWindow.manager.loadData();
					parent.document.findIframeByTitle("明细数据查询配置")[0].contentWindow.taskTree.refresh();
					BIONE.closeDialog("rptEdit");
				}
			},
			error:function(){
				if (typeof BIONE != 'undefined') {
					BIONE.loading = false;
					BIONE.hideLoading();
				}
				BIONE.tip("维护失败，请联系系统管理员");
			}
		});
	} 
</script>
</head>
<body>
	<div id="template.center">
		<div id="tab" style="width:100%;overflow: hidden;">
			<div tabid="tabBase" title="报表信息"  >
			<iframe frameborder="0" id="tabBase" src=""></iframe> 
			</div>
			<div tabid="tabSearch" title="查询项配置">
				<iframe frameborder="0" id="tabSearch" src=""></iframe> 
			</div>
			<div tabid="tabShow" title="显示列配置">
				<iframe frameborder="0" id="tabShow" src=""></iframe> 
			</div>
		</div>
</div>
</body>
</html>