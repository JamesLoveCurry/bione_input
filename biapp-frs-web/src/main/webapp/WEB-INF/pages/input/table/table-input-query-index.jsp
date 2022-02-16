<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template22_BS.jsp">
<script type="text/javascript">
	var currentNode;
	var tabObj;
	var leftTreeObj;
	var newFlag =false;
	var async = {
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
			view : {
				selectedMulti : false,
				showLine : false
			},
			callback:{
				onClick : f_clickNode
			}
		};

	// 加载树节点
	var initNodes = function(searchText){
		if(leftTreeObj)  leftTreeObj.destroy();
		leftTreeObj = $.fn.zTree.init($("#tree"),async,[]);
		if(leftTreeObj == null 
				|| typeof leftTreeObj == "undefined"){
			return ;
		}
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/table/listtables.json",
			dataType : 'json',
			type : "post",
			data : {
				tableName:searchText
			},
			success : function(result){
				if(result != null && typeof result != "undefined"){
					// 渲染新节点
					leftTreeObj.addNodes(null, result, true);
				}
			},
			error:function(){
				BIONE.tip("加载失败，请联系系统管理员");
			}
		});
	}

	 //树点击事件
	function f_clickNode(event, treeId, treeNode, clickFlag) {
		newFlag =false;
		currentNode = treeNode;
		initCurrentTab("iteminfo",currentNode.id,currentNode.text);
	}
	function initCurrentTab(type,id,name){
		tabObj.removeTabItem("inputTableData");
		curTabUri = "${ctx}/rpt/input/table/queryDataList?templateId=" + id;
		tabTitleName = "补录数据";
		tabObj.addTabItem({
			tabid : "inputTableData",
			text:tabTitleName,
			showClose : false,
			content : '<iframe frameborder="0" id="inputTableDataFrame" name="inputTableDataFrame"  style="height:'+height+'px;" src='+curTabUri+' ></iframe>'
		});

	    tabObj.selectTabItem("inputTableData");
	}
	// 搜索动作
	var searchHandler = function(){
		var searchName = $("#treeSearchInput").val();
		initNodes(searchName);
	}
	$(function() {
		$("#rMenu").hide();
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
		tabObj = $("#tab").ligerTab({
			contextmenu : true
		});
		var $centerDom = $(window.document);
		height = $centerDom.height() - 26;
		initCurrentTab("","","");
		initNodes("");

		function commonOpenSmallDialog(title, name,
				url, beforeClose) {
			var width = 450;
			var height = 600;
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
	});

	function reload(){
		searchHandler();
	}
</script>
<title></title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12px">补录数据</span>
	</div>
	<div id="template.right">
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>