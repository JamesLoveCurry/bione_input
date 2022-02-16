<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<link rel="stylesheet" href="${ctx}/js/bootstrap3/css/bootstrap.css" />
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

#tree {
	background-color: #F1F1F1;
}
</style>
<script type="text/javascript">
	var treeObj = null; // 树对象
	var panel = null; // 标签对象
	var draggingNode = null//拖拽节点
	var labelIds,labelNames;
	// 拖拽标签头图标
	var notAllowedIcon = "/images/classics/icons/cancel.gif";
	var allowedIcon = "/images/classics/icons/accept.png";
	function templateshow() {
		var $content = $(document);
		var height = $content.height() - 40;
		$("#left").height(height);
		$("#right").height(height);
		$("#panel").css("min-height",height-15);
		var leftHeight = $("#left").height();
		var $treeContainer = $("#treeContainer");
		$treeContainer.height(leftHeight - 58);
		rightWidth=$content.width()-$("#left").width()-5;  
		$("#right").width(rightWidth);
	}
	
	$(function() {
		initTree();
		initPanel();
		initData();
		//initBtn();目前标签变为业务标签（1104、大集中等等），所以不需要手动配置
	});
	
	//初始化标签函数
	function initPanel(){
		panel = $('#panel').exlabel({
			text: 'text',
			value: 'id',
			isEdit : false,
			isInput : false,
			callback: {
		        beforeShowMenu: function (a,b) {
		        },
		        onClick: function (data) {
		        },
		        beforeAdd: function (label) {
		          
		        }
		      }
		});
	}
	
	//初始化树函数
	function initTree() {
		var setting = {
	  		async : {
	  			enable : true,
	  			type : "post",
	  			dataType:"json",
	  			url : "${ctx}/bione/label/labelConfig/treeNode.json?labelObjNo=${labelObjNo}&d="+new Date().getTime(),
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
					//setDragDrop("#"+treeNode.tId+"_a" , "#panel");目前标签变为业务标签（1104、大集中等等），所以不需要手动配置
				}
			}
		};
		treeObj = $.fn.zTree.init($("#tree"), setting);
	}
	
	function initBtn(){
		//初始化按钮
		var btns = [{
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("labelEdit");
			}
		},{
			text : '保存',
			onclick : saveRels
		}];
		BIONE.addFormButtons(btns);
	}
	
	function saveRels(){
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/bione/label/labelConfig/saveLabelRel',
			dataType : 'json',
			type : "post",
			data : {
				id : "${id}",
				labelObjNo : "${labelObjNo}",
				labelIds : panel.val().join(",")
			},
			success : function(result){
				BIONE.closeDialog("labelEdit","配置成功");
			}
		});	
	}
	
	function initData(){
		$.ajax({
			async : true,
			cache : false,
			url : '${ctx}/bione/label/labelConfig/getLabelRel',
			dataType : 'json',
			type : 'post',
			data :{
				id : "${id}",
				labelObjNo : "${labelObjNo}"
			},
			success : function(result){
				labelIds = result.infoMaps.id;
				labelNames = result.infoMaps.name;
				panel.adds(result.nodes);
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
						style="float: left; position: relative; height: 20p; margin-top: 7px">
						<i class = "icon-guide search-size"></i>
					</div>
					<div width="90%">
						<span
							style="font-size: 12px; float: left; position: relative; line-height: 30px; padding-left: 2px">
							全部业务标签
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
			<div id="panel" style="width:98%;margin:5px;">
			</div>
		</div>
	</div>
</body>
</html>