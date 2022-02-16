<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<link rel="stylesheet" href="${ctx}/js/bootstrap3/css/bootstrap.css" />
<meta name="decorator" content="/template/template5.jsp">
<link rel="stylesheet" href="${ctx}/js/exlabel/exlabel.css">
<script type="text/javascript" src="${ctx}/js/exlabel/exlabel.js"></script>
<style>
#left {
	float: left;
	width: 200px ;
	border-width: 1px;
	border-style: solid;
	border-color: #D6D6D6;
}

#right {
	width: 100%;
	float: right;
	border-width: 1px;
	border-style: solid;
	border-color: #D6D6D6;
	overflow: auto;
}

div{
	font-size :12px;
}
#tree {
	background-color: #F1F1F1;
}
.searchtitle .togglebtn{
	top:90px;
}
.searchbox{
	width:99%;
	margin:0;
}
#fengediv{
	height:20px;
}
</style>
<script type="text/javascript">
	var treeObj = null; // 树对象
	var panel = null; // 标签对象
	var draggingNode = null//拖拽节点
	var labelIds,labelNames,labelPids;
	var type ="${type}";
	// 拖拽标签头图标
	var notAllowedIcon = "/images/classics/icons/cancel.gif";
	var allowedIcon = "/images/classics/icons/accept.png";
	function templateinit() {
		var height = $("#center").height() - 10;
		$("#left").height(height);
		$("#right").height(height);
		$("#panel").css("height",height-30);
		var leftHeight = $("#left").height();
		var $treeContainer = $("#treeContainer");
		$treeContainer.height(leftHeight - 26);
		rightWidth=$("#center").width()-$("#left").width()-25;  
		$("#right").width(rightWidth);
	}
	
	$(function() {
		templateinit();
		initPanel();
		initData();
		initInfo(type);
		initBtn();
	});
	
	
	function initBtn(){
		var btns = [{
			text : "重置",
			onclick : function(){
				panel.removeAll();
			}
		},{
			text : "筛选",
			onclick : highFilter
		},{
			text : "搜索",
			onclick : highSearch
		}];
		BIONE.addFormButtons(btns);
	}
	
	function highFilter(){
		var data = panel.getData();
		if(data.length > 0){
			var ids = [];
			for(var i in data){
				ids.push(data[i].id);
			}
			var idsStr = ids.join(",");
			if(type == "idx"){
				window.parent.initLabelIdx(idsStr,"filter");
			}
			if(type == "rpt"){
				window.parent.initLabelRpt(idsStr,"filter");
			}
			BIONE.closeDialog("highSearch");
		}
		else{
			BIONE.tip("请选择一个标签");
		}
	}
	
	function highSearch(){
		var data = panel.getData();
		if(data.length > 0){
			var ids = [];
			for(var i in data){
				ids.push(data[i].id);
			}
			var idsStr = ids.join(",");
			if(type == "idx"){
				window.parent.initLabelIdx(idsStr,"search");
			}
			if(type == "rpt"){
				window.parent.initLabelRpt(idsStr,"search");
			}
			BIONE.closeDialog("highSearch");
		}
		else{
			BIONE.tip("请选择一个标签");
		}
	}
	function initInfo(type){
		panel.removeAll();
		initTree(type);
	}
	//初始化标签函数
	function initPanel(){
		panel = $('#panel').exlabel({
			text: 'text',
			value: 'id',
			isEdit : false,
			isInput : false,
			callback: {
		     }
		});
	}
	
	//初始化树函数
	function initTree(labelObjNo) {
		var setting = {
	  		async : {
	  			enable : true,
	  			type : "post",
	  			dataType:"json",
	  			url : "${ctx}/bione/label/labelConfig/treeNode.json?labelObjNo="+labelObjNo+"&d="+new Date(),
	  			autoParam : ["realId", "type"],
				dataFilter : function(treeId, parentNode, childNodes) {
				    if(childNodes) {
				    	$.each(childNodes, function(i, n) {
				    		childNodes[i].type = n.params.type;
				    		childNodes[i].realId = n.params.realId;
				    	});
				    }
				    return childNodes;
				}
			},
			data:{
				keep:{
					parent : true
				},
				key : {
					name : "text"
				}
			},
			callback: {
				onNodeCreated : function(event , treeId , treeNode){
					setDragDrop("#"+treeNode.tId+"_a" , "#panel");
				}
			}
		};
		treeObj = $.fn.zTree.init($("#tree"), setting);
	}
	
	function initData(){
		$.ajax({
			async : true,
			cache : false,
			url : '${ctx}/bione/label/labelConfig/getLabelMap',
			dataType : 'json',
			type : 'post',
			data :{
				id : "${id}",
				labelObjNo : "${labelObjNo}"
			},
			success : function(result){
				labelIds = result.id;
				labelNames = result.name;
				labelPids = result.pid;
			}
			
		})
	}
	
	//添加拖拽控制
 	function setDragDrop(dom , receiveDom){
 		if(typeof dom == "undefined"
 			|| dom == null){
 			return ;
 		}
 		$(dom).ligerDrag({
 			proxy : function(target , g , e){
 				var defaultName = "标签";
 				var proxyLabel = "${ctx}"+notAllowedIcon;
 				var targetTitle = $(dom).attr("title") == null ? defaultName : defaultName+":"+$(dom).attr("title");
 				var proxyHtml = $('<div style="width：150;position: absolute;padding-left: 10px;color: #183152;font-weight: bold;height: 20px;line-height: 19px;overflow: hidden;background: #E5EFFE ;border: 1px solid #DDDDDD;border-top:none; z-index:9999;"><span class="dragimage_span" style="position:absolute;width:16px;height:16px;left:5px;top:2px;background:url('+proxyLabel+')" ></span><span style="padding-left: 14px;">'+targetTitle+'</span></div>');
                var div = $(proxyHtml);
                div.appendTo('#center');
                return div;
 			},
 			revert : false , 
 			receive : receiveDom, 
 			onStartDrag : function(current , e){
				// 获取拖拽树节点信息
 				var treeAId = current.target.attr("id");
 				var treeId = treeAId;
				if(treeAId){
					var strsTmp = treeAId.split("_");
					var treeId = treeAId;
					if(strsTmp.length > 1){
						var newStrsTmp = [];
						for(var i = 0 ; i < strsTmp.length - 1 ; i++){
							newStrsTmp.push(strsTmp[i]);
						}
						treeId = newStrsTmp.join("_");
					}
					draggingNode = treeObj.getNodeByTId(treeId);
				}
 			},
 			onDragEnter : function(receive , source , e){
 				var allowLabel = "${ctx}"+allowedIcon;
 				source.children(".dragimage_span").css("background" , "url('"+allowLabel+"')");
 			},
 			onDragLeave : function(receive , source , e){
 				var notAllowLabel = "${ctx}"+notAllowedIcon;
 				source.children(".dragimage_span").css("background" , "url('"+notAllowLabel+"')");
 			},
 			onDrop : function(obj , target , e){
 				var data=panel.getData();
 				var delData = [];
 				for(var i in data){
 					var pid = labelIds[draggingNode.realId].split(",")
 					if($.inArray(data[i].id, pid)>=0){
 						delData.push(data[i]);
 					}
 					var ids =labelIds[data[i].id];
 					if(ids.indexOf(draggingNode.realId)>=0){
 						delData.push(data[i]);
 					}
 				}
 				for(var i in delData){
 					panel.remove(delData[i]);
 				}
 				panel.add({
 	 				id : draggingNode.realId,
 	 				text : labelNames[draggingNode.realId]
 	 			});
 			}
 		});
	}
	
	
	
	
	
	
	
</script>
	
</head>
<body>
	<div id="template.center">
		<div id="left"  style="background-color: #FFFFFF">
			<div id="lefttable" width="100%" border="0">
				<div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%"
						style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
						<img src="${ctx}/images/classics/icons/application_side_tree.png" />
					</div>
					<div width="90%">
						<span
							style="font-size: 12px; float: left; position: relative; line-height: 30px; padding-left: 2px">
							标签选择
						</span>
					</div>
				</div>
			</div>
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
		<div id="right">
			<div style="margin: 5px;">
				<div style="width:100%; padding-left:10px;font-size: 12px;font-weight: 700;color: #959595;text-transform: uppercase;letter-spacing: 1px;">
					检索标签：
				</div>
				<div id="panel" style="width:99%;">
				</div>
			</div>
		</div>
	</div>
</body>
</html>