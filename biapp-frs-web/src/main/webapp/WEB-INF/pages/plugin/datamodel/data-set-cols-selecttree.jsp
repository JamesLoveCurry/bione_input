<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template12.jsp">
<script type="text/javascript">
// 	var comboObj=parent.selectFrame.$("#operBox");
	var grid=parent.selectFrame.grid;
	var rowdata=parent.selectRowData;
	var url, colTypeSelectTreeObj, treeNode_;
	var  type  = '${type}';
	var  currVal  = '${currVal}';
	var  selectedVal = eval('(${selected})');
	var  setType  = '${setType}';
	function filter(node) {
	    return (node && node.params.type=="dimCatalog");
	}
	function   initContext(){
		 var  baseNode   =  colTypeSelectTreeObj.getNodes()[0];
		var id  =baseNode.tId;
		$("#"+id).find(".chk#"+id+"_check").remove();
		var nodes = colTypeSelectTreeObj.getNodesByFilter(filter); // 查找节点集合
		if(nodes){
			for(var i=0;i<nodes.length;i++){
				 var node  = nodes[i];
				 var id_ =node.tId;
				 $("#"+id_).find(".chk#"+id_+"_check").remove();
			}
		}
	}
	$(function() {
 		url = "${ctx}/rpt/frame/dataset/listmeasures.json?setType="+setType;
		if(type&&type=="dimType"){
 		   url = "${ctx}/rpt/frame/dimCatalog/listcatalogs.json?getAllTypeInfo=getAllTypeInfo";
		}
		$("#treeContainer").before('<div id="treeSearchbar" style="width:99%;margin-top:2px;padding-left:2px;">'+
				'<ul>'+
					'<li style="width:98%;text-align:left;">'                   +   
						'<div class="l-text-wrapper" style="width: 100%;">'         +               
							'<div class="l-text l-text-date" style="width: 100%;">'  +                  
								'<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 95%;" />'   +
								'<div class="l-trigger">'                                    +                                  
									'<i id="treeSearchIcon" class = "icon-search search-size" ></i>'    +                                             
								'</div>'+
							'</div>'     +                                                                                             
						'</div>'+
					'</li>'+
				'</ul>     '+                                                                                                   
			'</div>   ');
		beforeTree();
		initTree();
		selectButton();
		initLayout();
	});
	function templateShow(){
		var height = $(document).height();
		$("#center").height(height - 40 );
		$(".content").height(height - 40);
		$("#treeContainer").height($("#center").height() - $("#treeSearchbar").height());
	}
	function beforeTree() {
		var setting = {
				data:{
					key:{
						name:'text'
					},
					simpleData:{
						enable:true,
						idKey: "id",
						pIdKey: "upId"
					}
				},
				callback : {
					beforeClick : beforeClick,
					onClick : onClick
				},	
				view : {
					selectedMulti : false,
					showLine : false,
					expandSpeed :"fast"
				},
				check:{
					chkStyle:"radio",
					enable:true,
					radioType:"all" 
				}
		};
		colTypeSelectTreeObj = $.fn.zTree.init($("#tree"), setting);
	}
	
	function initTree(searchText) {
		$.ajax({
			cache : false,
			async : true,//同步
			url : url,
			type : "post",
			data : {searchText:searchText},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				if(result) {
					for(var i in result) {
						if(result[i].params.open) {
							result[i].open = true;
						} else {
							result[i].open = false;
						}
					}
				}
				colTypeSelectTreeObj.addNodes(null, result, true);
				colTypeSelectTreeObj.expandAll(true);
				initContext();
				showBack();
				templateShow();
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	function   showBack(){
		if(currVal){
			if(colTypeSelectTreeObj.getNodeByParam("id", currVal, null)){
			   colTypeSelectTreeObj.checkNode(colTypeSelectTreeObj.getNodeByParam("id", currVal, null), true, true);
			}
		}
		for(var i=0;i<selectedVal.length;i++){
			colTypeSelectTreeObj.removeNode(colTypeSelectTreeObj.getNodeByParam("id", selectedVal[i], null));
		}
	}
	function selectButton() {
		var buttons = [];
		buttons.push({
			text : '选择',
			onclick : selected
		});
		BIONE.addFormButtons(buttons);
	}
	
	function beforeClick(treeId, treeNode, clickFlag) {
			return false;
	}
	
	function onClick(event, treeId, treeNode, clickFlag) {
		treeNode_ = treeNode;
	}
	     
 	function selected() {
 		var    radioId   =  $(".radio_true_full").attr("id");
 		if(radioId){
 			radioId  =  radioId.substring(0,radioId.length-6);
 		}else {
 			BIONE.tip("请选择节点");
 			return;
 		}
 		var    currentNode  =  colTypeSelectTreeObj.getNodeByParam("tId",radioId);
 		
 		rowdata.operText=currentNode.text;
 		if(type=="dimType"){
    		rowdata.dimTypeNo=currentNode.id;
    		rowdata.dimTypeName=currentNode.text;
 		}else{
 		   rowdata.measureNo=currentNode.id;
 		  rowdata.measureName=currentNode.text;
 		}
//  		comboObj.val(treeNode_.id);
		grid.endEdit();
		BIONE.closeDialog("rptDatasetColTypeSelectedForDataModule");
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
	}
	var searchHandler = function(){
		var searchName = $("#treeSearchInput").val();
		beforeTree();
		initTree(searchName);
	}
</script>
</head>
<body>
</body>
</html>