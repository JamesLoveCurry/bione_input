<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template22.jsp">
<script type="text/javascript">
	var currentNode;
	var tabObj;
	var leftTreeObj;
	var newFlag =false;
	var dsId  ='${dsId}';
	var storeCol  ='${storeCol}';
	var  setNm = '${setNm}';
	var  setType =  '${setType}';
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
		if(leftTreeObj == null 
				|| typeof leftTreeObj == "undefined"){
			return ;
		}
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frame/dataset/getDataModuleTree.json?d=" + new Date().getTime(),
			dataType : 'json',
			type : "post",
			data : {
				searchNm:searchText,
				setType:setType
			},
			success : function(result){
				if(result != null
						&& typeof result != "undefined"){
					// 移除旧节点
					leftTreeObj.removeChildNodes(leftTreeObj.getNodeByParam("id", '0', null));
					leftTreeObj.removeNode(leftTreeObj.getNodeByParam("id", '0', null),false);
					// 渲染新节点
					leftTreeObj.addNodes(leftTreeObj.getNodeByParam("id", '0', null) , result , true);
					leftTreeObj.expandAll(true);
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
		var  type = currentNode.params.type;
// 		if(currentNode.id=='0'){
// 			initCurrentTab('','','');
// 			return;
// 		}
		if(type!="setInfo"){
			return ;
		}else{
			initCurrentTab(currentNode.data.setId,currentNode.data.setNm,storeCol,dsId);
		}
		
	} 	
	function  initCurrentTab(setId,setNm,storeCol,oldSetId){
		tabObj.removeTabItem("rptSysModuleInfoMeasuresTab");
		curTabUri = "${ctx}/report/frame/idx/gotoMeasureList?setId="+setId+"&storeCol="+storeCol+"&oldSetId="+oldSetId;
		var   titleName  ='' ;
		if(setNm==''){
			titleName ='度量';
		}else{
			titleName ="数据集["+setNm+"]"+":度量"
		}
		tabObj
		.addTabItem({
			tabid : "rptSysModuleInfoMeasuresTab",
			text:titleName,
			showClose : false,
			content : '<iframe frameborder="0" id="rptSysModuleInfoMeasuresTabFrame" style="height:'+height+'px;" src='+curTabUri+' ></iframe>'
		});
	    tabObj.selectTabItem("rptSysModuleInfoMeasuresTab");
	}
	// 搜索动作
	var searchHandler = function(){
		var searchName = $("#treeSearchInput").val();
		initNodes(searchName);
	}
	$(function() {
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
		var $centerDom = $(document);
		height = $centerDom.height() - 26;
		leftTreeObj = $.fn.zTree.init($("#tree"),async,[]);
			initNodes("");
			if(dsId&&dsId!=""&&storeCol!=""){
						initCurrentTab(dsId,setNm,storeCol,dsId);
			}else{
						initCurrentTab('','','','');
			}
	});
</script>
<title></title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">数据集选择</span>
	</div>
	<div id="template.right">
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>