<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template22_BS.jsp">
<head>
<style type="text/css">
	div#rMenu {position:absolute; display:none; top:0; text-align: left;overflow: hidden;}
 	div#rMenu div{
		padding: 3px 15px 3px 15px;
     	background-color:#ddd;
    	vertical-align:middle;
    	color: #666;
    	
	}
</style>
<script type="text/javascript">
	var currentNode;
	var tabObj;
	var leftTreeObj;
	var newFlag =false;
	var tempGrid = null;
	var state = '1';
	var dialog = "";
	
	function refreshTree() {
		if (leftTreeObj) {
				leftTreeObj.reAsyncChildNodes(null, "refresh");
				$("#mainformdiv").html("");
		}
	}
	var settingasync=null;
	var settingsync=null;
	//树点击事件
	function zTreeOnClick(event, treeId, treeNode){
		if(!treeNode.params.upOrgNo){
			currentNode = treeNode;
			return;
			$("#mainformdiv").html("");
		}
		
		if(treeNode.params.orgNo == " "){
			currentNode = treeNode;
			return;
			$("#mainformdiv").html("");
		}
		if (currentNode != treeNode) {
			currentNode = treeNode;
			if (!tempGrid) {
				var content = "<iframe frameborder='0' id='gridFrame' style='height:100%;width:100%;' src='${ctx}/frs/systemmanage/orgmanage/orgFrame?orgNo="
					+ treeNode.orgNo
					+ "&orgType="
					+ treeNode.orgType
					+ "&upOrgNo="
					+ treeNode.upOrgNo
					+ "&mgrOrgNo="
					+ treeNode.mgrOrgNo
					+ "&orgLevel="
					+ treeNode.data.orgLevel
					+ "&orgClass="
					+ encodeURI(encodeURI(treeNode.data.orgClass))
					+ "'></iframe>";
				addTabItem('org','机构信息','gridFrame',content);
			} else {
				tempGrid.set('parms', {
					orgNo : treeNode.orgNo,
					orgType : treeNode.orgType,
					d : new Date().getTime()
				});
				if (catalogBox) {
					catalogBox.val(treeNode.orgNo);
				}
				tempGrid.loadData();
			}

		}
	}
	function initTree(){
		settingasync ={
				async:{
					enable:true,
					url:"${ctx}/frs/systemmanage/orgmanage/getTree?state="+state+"&t="+ new Date().getTime(),
					autoParam:["upOrgNo","orgNo","orgNm","orgType","mgrOrgNo"],
					dataType:"json",
					type:"get",
					dataFilter:function(treeId,parentNode,childNodes){
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								childNodes[i].orgType = childNodes[i].params.orgType;
								childNodes[i].upOrgNo = childNodes[i].params.upOrgNo;
								childNodes[i].orgNm = childNodes[i].params.orgNm;
								childNodes[i].orgNo = childNodes[i].params.orgNo;
								childNodes[i].mgrOrgNo = childNodes[i].params.mgrOrgNo;
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
					selectedMulti:false,
					fontCss : setFontCss
				},
				callback:{
					onRightClick: zTreeOnRightClick,
					onClick:zTreeOnClick,
					onNodeCreated:function(event, treeId, treeNode){
						if(treeNode.id == "ROOT"){
							leftTreeObj.reAsyncChildNodes(treeNode,"refresh");
						}
					}
				}
		};
		settingsync = {
				data:{
					key:{
						name:"text"
					},
/* 					simpleData:{
						enable:true,
						idKey: "id"
					} */
					simpleData:{
						enable : true,
						idKey : "id",
						pIdKey : "upId",
						rootPId : null
					}
				},
				view:{
					selectedMulti : false
				},callback:{
					onRightClick: zTreeOnRightClick,
					onClick:zTreeOnClick,
					onNodeCreated : function(event, treeId, treeNode) {
						if (treeNode.id == "ROOT") {
							//若是根节点，展开下一级节点
							leftTreeOb.expand(treeNode,true,true,true);
						}
					}
				}
		};
		$("#template.left.center").hide();
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'add',
				text : '新建',
				click : addOrg
			}, {
				icon : 'delete',
				text : '删除',
				click : deleteOrg
			},{
				icon:'fontSize',
				text:'数据处理',
				menu:{
					items:[{
						icon : 'export',
						text : '监管机构导出',//导出的是所属机构类型的全部机构信息，特别的是大集中特有的字段不导出
						click : exportOrg
						},{
						icon: 'import',
						text: '监管机构导入',
						click:importOrg
						}]
					}
			}]
		});
		$("#treeSearchIcon").bind("click",function(e){
			setTree($("#treeSearchInput").val() == "");
		});
		$("#treeSearchInput").bind("keydown",function(e){
			if(e.keyCode == 13){
				setTree($("#treeSearchInput").val() == "");
			}
		});

	}
	function setTree(flag){
		if(flag){
			leftTreeObj = $.fn.zTree.init($("#tree"), settingasync);
		}
		else{
			leftTreeObj = $.fn.zTree.init($("#tree"), settingsync);
			loadTree("${ctx}/frs/systemmanage/orgmanage/searchOrgTree",leftTreeObj,{orgNm:$("#treeSearchInput").val()});
		}
	}
	
	// 树的右键事件
	function zTreeOnRightClick(event, treeId, treeNode){
		if(treeNode.id != null && treeNode.id != "0"){
			showRMenu("node", event.clientX, event.clientY,treeNode);
		}
	}
	function showRMenu(type, x, y,treeNode) {
		y = y-20;
		x = x+14;
		currentNode = treeNode;
	    $("#rMenu").css({"top":y+"px", "left":x+"px", "display":"block"}); //设置右键菜单的位置、可见
	    $("body").bind("mousedown", onBodyMouseDown);
	}
	
	//隐藏右键菜单
	function hideRMenu() {
		$("#rMenu").css({"display": "none"});
	}

	function onBodyMouseDown(event) {
	    if (!(event.target.id == "rMenu" || $(event.target).parents("#rMenu").length > 0)) {
	    	hideRMenu();
	    }
	}
	// 配置机构汇总关系
	function editTreeOrgSumRel(){
		hideRMenu();
		dialog = BIONE.commonOpenDialog("配置机构汇总关系", "orgSumRelWin", $(window).height()-60, $(window).width()-100,
				"${ctx}/rpt/frame/rptorgsum/getNewTree?orgNo=" + currentNode.orgNo + "&orgType=" + currentNode.orgType);
	}
	
	//初始化
	$(function(){
		//设置高度
		// var $centerDom = $(document);
        var $centerDom = $(window.parent.document);
		gridCenter = $centerDom.height() - 0;
		initTree();
		intiTab();
		setTree(true);
		
	});
	function intiTab(){
		$("#tab").ligerTab({
			changeHeightOnResize : true,
			contextmenu : false
		});
		tabObj = $("#tab").ligerGetTabManager();
	}
	//加载树中数据
	function loadTree(url, component, data) {
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			data : data,
			type : "post",
			beforeSend : function() {
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				var nodes = component.getNodes();
				var num = nodes.length;
				for ( var i = 0; i < num; i++) {
					component.removeNode(nodes[0], false);
				}
				if (result.length > 0) {
					for(var i = 0;i<result.length;i++){
						result[i].orgType = result[i].params.orgType;
						result[i].upOrgNo = result[i].params.upOrgNo;
						result[i].orgNm = result[i].params.orgNm;
						result[i].orgNo = result[i].params.orgNo;
					//	childNodes[i].searchNm=$("#treeSearchInput").val();
					}
					component.addNodes(null, result, false);
					component.expandAll(true);
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	function addOrg(){
		var url = "";
		if(currentNode){
			//if(currentNode.level>0){
				var content = "<iframe frameborder='0' id='gridFrame' style='height:100%;width:100%;' src='${ctx}/frs/systemmanage/orgmanage/orgFrame?upOrgNo="
					+ currentNode.params.orgNo
					+ "&orgType="
					+ currentNode.params.orgType
					+ "'></iframe>";
				addTabItem('org','机构信息','gridFrame',content);
			/*}
			else{
				BIONE.tip("不能选择根机构");
				return;
			} */
		}
		else
		{
			BIONE.tip("请选择机构或机构源");
			return;
		}
	}

	
	function addTabItem(tabId,tabText,frameId,content){
		var $centerDom = $(document);
		framCenter = $centerDom.height() - 40;
		tabObj.addTabItem({//添加标签tab页
			tabid : tabId,
			text : tabText,
			showClose : true,
			content : "<div id='"+frameId+"' style='height:" + framCenter
					+ "px;width:100%;'></div>"
		}); 
		$("#"+frameId).html(content);
	}
	//删除机构
	function deleteOrg() {
		if (!currentNode || currentNode.id == 'ROOT') {
			BIONE.tip("未选择任何机构。");
			return;
		}
		if(!currentNode.upOrgNo){
			BIONE.tip("根目录不能删除");
			return;
		}
		$.ligerDialog.confirm("确定删除 [ " + currentNode.text + " ] ？",function(sun){
			if(sun){
				$.ajax({
					type : "POST",
					url : "${ctx}/frs/systemmanage/orgmanage/deleteOrg?d="+new Date(),
					data : {
						orgType : currentNode.orgType,
						orgNo : currentNode.orgNo,
						upOrgNo : currentNode.upOrgNo
					},
					success:function(ming){
						if(ming == null || ming.length == 0){
							BIONE.tip("删除成功!");
							leftTreeObj
							.reAsyncChildNodes(
									currentNode
											.getParentNode(),
									"refresh");
					//initTab();
					$("#gridFrame").html("");
					currentNode = null;
						}else if(ming=="false"){
							BIONE.tip("该机构下存在其他机构,无法删除!");
						}else
							BIONE.tip("该机构已经绑定任务:"+ming+" , 无法删除!");
					},
					error : function(XMLHttpRequest,
							textStatus, errorThrown) {
						BIONE.tip('删除失败,错误信息:'
								+ textStatus);
					}
				});
			}
		});
	}
	
	function modifyOrg(){
		if (!currentNode || currentNode.id == 'ROOT') {
			BIONE.tip("未选择任何机构。");
			return;
		}
		if(!currentNode.upOrgNo){
			BIONE.tip("根目录不能修改");
			return;
		}else{
			commonOpenSmallDialog("修改机构","addCurrentNode","${ctx}/frs/systemmanage/orgmanage/addOrg?orgType="+currentNode.orgType+"&orgNo="+currentNode.orgNo+"&upOrgNo="+currentNode.upOrgNo);
			
		}
	}
	//机构导出
	function exportOrg(){
		var type = 'org';
		var orgType = "";
		if(currentNode != null && currentNode != undefined){
			orgType = currentNode.params.orgType;
			var childNodes = currentNode.children;
			$.ajax({
				async:true,
				type:"POST",
				dataType:"json",
				url:"${ctx}/frs/systemmanage/orgmanage/download",
				data:{
					type:type,
					orgType:orgType
				},
				beforeSend : function(a, b, c) {
					BIONE.showLoading('正在导出数据中...');
				},
				success:function(data){
					BIONE.hideLoading();
					if(data.fileName==""){
						BIONE.tip('导出异常');
						return;
					}
					var src='${ctx}/frs/systemmanage/orgmanage/export?type='+type+'&fileName='+encodeURI(data.fileName);
					window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
					$('body').append(downdload);
					downdload.attr('src', src);
				},
				error : function(result) {
					BIONE.hideLoading();
					//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}else{
			BIONE.tip("请先点击机构树选中机构！");
		}
		
	}
	//机构导入
	function importOrg(){
		BIONE.commonOpenDialog('监管机构导入','importWin',600,480,"${ctx}/report/frame/wizard?type=Org");
	}


	//BIONE的commonOpenSmallDialog方法自己重新改的
	function commonOpenSmallDialog(title, name,
			url, beforeClose) {
		var width = 600;
		var height = 340;
		var _dialog = $.ligerui.get(name);
		if (_dialog) {
			$.ligerui.remove(name);
		}
		_dialog = $.ligerDialog.open({
			height : height,
			width : width,
			url : url,
			name : name,
			id : name,
			title : title,
			isResize : false,
			isDrag : false,
			isHidden : false
		});
		if (beforeClose != null && typeof beforeClose == "function") {
			_dialog.beforeClose = beforeClose;
		}
		return _dialog;
	};

	function setFontCss(treeId, treeNode) {
		return (treeNode.params.isVirtualOrg == 'Y') ? {
			color : "red"
		} : {};
	};

	function reload(){
		leftTreeObj.reAsyncChildNodes(null,	"refresh");
	}


	</script>
<title>机构管理</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12px">机构信息</span>
	</div>
	<div id="template.right">
		<div id="tab" style="overflow: hidden; height: 100%;">
		</div>
	</div>
</body>
</html>