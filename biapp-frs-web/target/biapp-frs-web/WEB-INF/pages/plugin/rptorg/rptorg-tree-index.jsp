<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template22.jsp">
<head>
<script type="text/javascript">
	var currentNode;
	var tabObj;
	var leftTreeObj;
	var newFlag =false;
	var tempGrid = null;
	var state = '1';
	
	function refreshTree(node) {
		if (leftTreeObj) {
				leftTreeObj.reAsyncChildNodes(node, "refresh");
				$("#mainformdiv").html("");
		}
	}
	
	function reload() {
		if (leftTreeObj) {
				leftTreeObj.reAsyncChildNodes(null, "refresh");
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
				var content = "<iframe frameborder='0' id='gridFrame' style='height:100%;width:100%;' src='${ctx}/rpt/frame/rptorg/grid?orgNo="
					+ treeNode.orgNo
					+ "&orgType="
					+ treeNode.orgType
					+ "&upOrgNo="
					+ treeNode.upOrgNo
					+ "&mgrOrgNo="
					+ treeNode.mgrOrgNo
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
					url:"${ctx}/rpt/frame/rptorg/getTree.json?state="+state+"&t="+ new Date().getTime(),
					autoParam:["${ctx}/rpt/frame/rptorg/","upOrgNo","orgNo","orgNm","orgType","mgrOrgNo"],
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								childNodes[i].orgType = childNodes[i].params.orgType;
								childNodes[i].upOrgNo = childNodes[i].params.upOrgNo;
								childNodes[i].orgNm = childNodes[i].params.orgNm;
								childNodes[i].orgNo = childNodes[i].params.orgNo;
								childNodes[i].mgrOrgNo = childNodes[i].params.mgrOrgNo;
 							//	childNodes[i].searchNm=$("#treeSearchInput").val();
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
					simpleData:{
						enable:true,
						idKey: "id"
					}
				},
				view:{
					selectedMulti : false
				},callback:{
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
		var datadeal = {
				width :150,
				items : [{
					icon : 'import',
					text : '模板导入',
					click : importOrg
				},{
					icon : 'export',
					text : '模板导出',
					click : exportOrg
				}]
			};
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
				icon : 'bookpen',
				text : '数据处理',
				menu : datadeal
			}
			]
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
			loadTree("${ctx}/rpt/frame/rptorg/searchTree.json?",leftTreeObj,{orgNm:$("#treeSearchInput").val()});
		}
	}
	
	//初始化
	$(function(){
		//设置高度
		var $centerDom = $(document);
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
			type : "POST",
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
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	function addOrg(){
		var url = "";
		if(currentNode){
			var content = "<iframe frameborder='0' id='gridFrame' style='height:100%;width:100%;' src='${ctx}/rpt/frame/rptorg/grid?upOrgNo="
				+ currentNode.params.orgNo
				+ "&orgType="
				+ currentNode.params.orgType
				+ "'></iframe>";
			addTabItem('org','机构信息','gridFrame',content);
		}
		else
		{
			BIONE.tip("请选择机构或机构源");
			return;
		}
	}
	
	function saveOrg(){
		
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
					type : "GET",
					url : "${ctx}/rpt/frame/rptorg/deleteOrg",
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
						}else{
							BIONE.tip("该机构下存在其他机构,无法删除!");
						}
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
			commonOpenSmallDialog("修改机构","addCurrentNode","${ctx}/rpt/frame/rptorg/addOrg?orgType="+currentNode.orgType+"&orgNo="+currentNode.orgNo+"&upOrgNo="+currentNode.upOrgNo);
			
		}
	}
	//机构导入
	function importOrg(){
		BIONE.commonOpenDialog("报表机构导入", "importWin", 600, 480,
		"${ctx}/report/frame/wizard?type=Org");
	}
	//机构导出
	function exportOrg(){
		var type = 'org';
		$.ajax({
			async:true,
			type:"POST",
			dataType:"json",
			url:"${ctx}/rpt/frame/rptorg/download",
			data:{type:type,
				orgType:"01"},
			beforeSend : function(a, b, c) {
				BIONE.showLoading('正在导出数据中...');
			},
			success:function(data){
				BIONE.hideLoading();
				if(data.fileName==""){
					BIONE.tip('导出异常');
					return;
				}
				var src='${ctx}/report/frame/wizard/export?type='+type+'&fileName='+data.fileName;
				window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
				$('body').append(downdload);
				downdload.attr('src', src);
			},
			error : function(result) {
				BIONE.hideLoading();
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
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
	</script>
<title>机构管理</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">机构信息</span>
	</div>
	<div id="template.right">
		<div id="tab" style="overflow: hidden; height: 100%;">
		</div>
	</div>
</body>
</html>