<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<style>
#left {
	float: left;
	width: 30%;
	border-width: 1px;
	border-style: solid;
	border-color: #D6D6D6;
}
#right {

	width: 69%;
	float: left;

}
#tree {
	background-color: #F1F1F1;
}
</style>
<script type="text/javascript">
	var leftTreeObj = null;
	var grid = null;
	function templateshow() {
		$("#right").height($(document).height() - 49);
		$("#left").height($(document).height() - 53);
		$("#treeContainer").height($("#left").height()-58);
    }
    
    $(function() {
    	templateshow();
    	initTree();
    	initGrid();
    	initData();
    	initBtn();
    });
    
    
    function initTree() {
		var  url_ =  "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=0&t="
			+ new Date().getTime();
		var setting ={
				async:{
					enable:true,
					url:url_,
					autoParam:["nodeType", "id", "indexVerId"],
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						var newChildNodes = [];
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].indexVerId = childNodes[i].params.indexVerId;
								if($("#treeSearchInput").val() && childNodes[i].nodeType == "idxInfo"){
									if(childNodes[i].text.indexOf($("#treeSearchInput").val()) >= 0){
										newChildNodes.push(childNodes[i]);
									}										
								}else{
									if(childNodes[i].id != "${indexNo}")
										newChildNodes.push(childNodes[i]);
								}
							}
							
						}
						return newChildNodes;
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
					onNodeCreated : function(event , treeId , treeNode){
						if(treeNode.nodeType == "idxInfo"){
							
						}
					},
					onClick : zTreeOnClick
				}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		$("#treeSearchIcon").live('click',function(){// 树搜索
			leftTreeObj.reAsyncChildNodes(null, "refresh");
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			if (event.keyCode == 13) {
				leftTreeObj.reAsyncChildNodes(null, "refresh");
	 		}
		});
	}
    
    function zTreeOnClick(event, treeId, treeNode){
    	if(treeNode.nodeType == "idxInfo"){
    		if(!getRow(treeNode.id)){
        		var row	={
        			id:{
        				indexNo : treeNode.id	
        			},
        	    	indexNm : treeNode.text
        	    };
        		grid.addRow(row);
        	}
    	}
    	
    }
    
    function getRow(indexNo){
    	var data = grid.getData();
    	for(var i in  data){
    		if(data[i].id.indexNo == indexNo){
    			return true;
    		}
    	}
    	return false;
    }
    function initGrid(){
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : false,
			columns : [ {
				display : '指标编号',
				name : 'id.indexNo',
				width : "30%",
				align : 'center',
				isSort : false
			}, {
				display : '指标名称',
				name : 'indexNm',
				width : "30%",
				align : 'center',
				isSort : false
			},{
				display : '操作',
				name : 'method',
				width : "30%",
				isSort : false,
				render : function(row,b,c){
					return "<a style='color:blue' onclick='f_delete(\"" + row.__id + "\")'>删除</a>"
		
				}
			} ],
			dataAction : 'local', //从后台获取数据
			usePager : false, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			data : {},
			rownumbers : true,
			width:'99%'
		});
		var rightHeight = $("#right").height();
		grid.setHeight(rightHeight - 8);
	}
    
    function initData(){
    	$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frame/idx/compgrp/getCompGrpIndex.json?d="
					+ new Date().getTime(),
			dataType : 'json',
			data : {
				indexNo : "${indexNo}"
			},
			type : "post",
			success : function(result) {
				grid.addRows(result.Rows);
			}
		});
    }
    
    function initBtn(){
    	var buttons = [];
    	buttons.push({
    	    text : '取消',
    	    onclick : function() {
    		BIONE.closeDialog("compgrpEdit");
    	    }
    	});
    	buttons.push({
    	    text : '保存',
    	    onclick : f_save
    	});

    	BIONE.addFormButtons(buttons);
    }
    
    function f_save(){
    	var data = grid.getData();
    	var ids = [];
    	for(var i in data){
    		ids.push(data[i].id.indexNo);
    	}
    	$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frame/idx/compgrp/saveCompGrp",
			dataType : 'json',
			data : {
				ids : ids.join(","),
				indexNo : "${indexNo}"
			},
			type : "post",
			success : function(result) {
				BIONE.closeDialog("compgrpEdit","保存成功");
			}
		});
    }
    
    function f_delete(rowId){
    	grid.deleteRow(rowId);
    }
</script>
</head>
<body>
	<div id="template.center">
		<div id="left" style="background-color: #FFFFFF">
			<div id="lefttable" width="100%" border="0">
				<div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%"
						style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
						<img src="${ctx }/images/classics/icons/application_side_tree.png" />
					</div>
					<div width="90%">
						<span
							style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
							指标信息
						</span>
					</div>
				</div>
			</div>
			<div id="treeSearchbar" style="width:99%;margin-top:2px;padding-left:2px;">
				<ul>
					<li style="width:98%;text-align:left;">                      
						<div class="l-text-wrapper" style="width: 100%;">                         
							<div class="l-text l-text-date" style="width: 100%;">                    
								<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 100%;" />    
								<div class="l-trigger">                                                                      
									<div id="treeSearchIcon" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>                                                 
								</div>
							</div>                                                                                                   
						</div>
					</li>
				</ul>                                                                                                         
			</div>          
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
		<div id="right">
			<div class="content">
				<div id="maingrid" class="maingrid"></div>
			</div>
		</div>
	</div>
</body>
</html>