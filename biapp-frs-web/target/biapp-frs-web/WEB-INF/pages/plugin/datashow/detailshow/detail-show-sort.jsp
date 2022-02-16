<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
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
	float: right;

}
#tree {
	background-color: #F1F1F1;
}
</style>
<script type="text/javascript">
	var colInfos = window.parent.colInfos;
	var colSorts = window.parent.colSorts;
	var leftTreeObj = null;
	var grid = null;
	var notAllowedIcon = "/images/classics/icons/cancel.gif";
	var allowedIcon = "/images/classics/icons/accept.png";
	
	function templateShow(){
		var $content = $(document);
		var height = $content.height() - 50;
		$("#left").height(height);
		$("#right").height(height);
		var rightHeight = $("#right").height();
		var leftHeight = $("#left").height();
		var $treeContainer = $("#treeContainer");
		$treeContainer.height(leftHeight - 26);
	}
	$(function() {
		templateShow();
		initTree();
		initGrid();
		initButtons();
	});

	function initTree() {
		var setting = {
			data : {
				key : {
					name : 'text'
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			callback : {
				onNodeCreated : function(event, treeId, treeNode) {
					setDragDrop("#" + treeNode.tId + "_a", "#maingrid");
				}
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		var nodes = [];
		for ( var i in colInfos) {
			var node = {
				id : i,
				text : colInfos[i].displayNm,
				icon : "${ctx}/images/classics/icons/column.png",
				data : colInfos[i]
			}
			nodes.push(node);
		}
		setTreeNode(nodes, leftTreeObj);
	}
	
	function initGrid(){
		var data ={};
		var rows = [];
		if(colSorts != null && colSorts != undefined){
			for(var i in colSorts){
				if(colInfos[colSorts[i].id.cfgId]!=null)
					rows.push(colSorts[i]);
			}
			data = {
				Rows : rows,
				Total : colSorts.length
			}
		}
		grid = $('#maingrid').ligerGrid({
			width : '99%',
			columns : [{
				display : "名称",
				name :  "displayNm",
				width: "30%",
				align: "left",
				render: function(a,b,c){
					return colInfos[a.id.cfgId].displayNm;
				}
			},{
				display : "排序方式",
				name :  "sortMode",
				width: "25%",
				align: "center",
				render : function(a,b,c){
					if(c == "asc")
						return "正序";
					if(c == "desc")
						return "倒序";
				},
				editor : {
					type : 'select',
					data : [ {
						'id' : 'asc',
						'text' : '正序'
					}, {
						'id' : 'desc',
						'text' : '倒序'
					}]
				}
			},{
				display : "操作",
				name :  "oper",
				width: "15%",
				render : function(row,b,c){
					return "<a href='javascript:deleteInfo(\""+row.__id+"\");'>删除</a>";
				}
			}],
			enabledEdit : true,
			usePager: false,
			checkbox: false,
			dataAction : 'local',
			allowHideColumn: false,
			rowDraggable : true,
			onBeforeSubmitEdit : function(e) {
				if(e.column.columnname=="sortOrderno"){
					if(e.value<=0){
						e.value = 1;
					}
					if(e.value>=grid.getData().length){
						e.value = grid.getData().length;
					}
					var s = e.record.sortOrderno;
					var data = grid.getData();
					var curdata = null;
					for(var i in data){
						if(data[i].sortOrderno ==s){
							curdata = data[i];
						}
						if(data[i].sortOrderno>s){
							data[i].sortOrderno = parseInt(data[i].sortOrderno)-1;
						}
					}
					for(var i in data){
						if(data[i].sortOrderno>=e.value && data[i].sortOrderno != s){
							data[i].sortOrderno = parseInt(data[i].sortOrderno)+1;
						}
					}
					curdata.sortOrderno = e.value;
					data.sort(function(a,b){
						return a.sortOrderno - b.sortOrderno;
					});
					grid.set("data",{
						Rows: data,
						Total: data.length
					})
					return false;
					
				}
			},
			sortName : "",
			data : data
		});
		grid.setHeight($("#right").height());
	}
	
	function setTreeNode(result, component) {
		var nodes = component.getNodes();
		var num = nodes.length;
		for (var i = 0; i < num; i++) {
			component.removeNode(nodes[0], false);
		}
		component.addNodes(null, result, false);
		component.expandAll(true);
	}
	
	function deleteInfo(rowId){
		grid.deleteRow(rowId);
		var rows = grid.getData();
		var i = 1;
	}
	//添加拖拽控制
 	function setDragDrop(dom , receiveDom){
 		if(typeof dom == "undefined"
 			|| dom == null){
 			return ;
 		}
 		$(dom).ligerDrag({
 			proxy : function(target , g , e){
 				var defaultName = "字段";
 				var proxyLabel = "${ctx}"+notAllowedIcon;
 				var targetTitle = $(dom).attr("title") == null ? defaultName : defaultName+":"+$(dom).attr("title");
 				var proxyHtml = $('<div style="width：150;position: absolute;padding-left: 10px;color: #183152;font-weight: bold;height: 20px;line-height: 19px;overflow: hidden;background: #E5EFFE ;border: 1px solid #DDDDDD;border-top:none; z-index:9999;"><span class="dragimage_span" style="position:absolute;width:16px;height:16px;left:5px;top:2px;background:url('+proxyLabel+')" ></span><span style="padding-left: 14px;">'+targetTitle+'</span></div>');
                var div = $(proxyHtml);
                div.appendTo('body');
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
					draggingNode = leftTreeObj.getNodeByTId(treeId);
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
 				var rows = grid.getData();
 				for(var i in rows){
 					if(rows[i].cfgId == draggingNode.data.id.cfgId){
 						BIONE.tip("已配置此列排序",2500);
 						return;
 					}
 				}
 				var row = {
 					id :{
 						cfgId :  draggingNode.data.id.cfgId,
 					},
 					sortMode : "asc",
 					sortOrderno :rows.length+1
 				};
 				grid.addRow(row);
 			}
 		});
	}
	
 	function initButtons(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function(){
				BIONE.closeDialog("sortEdit");
			}
		});
		buttons.push({
			text : '确认',
			onclick : function(){
				f_save();
			}
		});
		BIONE.addFormButtons(buttons);
	}
 	
 	function f_save(){
 		grid.endEdit();
 		var sortInfo = grid.getData();
 		for(var i in sortInfo){
 			sortInfo[i].sortOrderno = i;
 		}
 		window.parent.colSorts = sortInfo;
 		BIONE.closeDialog("sortEdit");
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
						<img src="${ctx}/images/classics/icons/application_side_tree.png" />
					</div>
					<div width="90%">
						<span
							style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
							数据列
						</span>
					</div>
				</div>
			</div>
			<div id="treeToolbar"
				style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
		<div id="right">
			<div id="maingrid" style="width：99%;"></div>
		</div>
	</div>
</body>
</html>