<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template22B.jsp">
<head>
	<script type="text/javascript">
		var leftTreeObj;
		var tabObj;
		var idxInfos =[];
		var currentNodeId = null;
		var dsId = '${dsId}'; //注意：【补录表维护】的dsId和【从外表导入】【数据回写】的dsId可能不同
		var operType = '${operType}'; //table用于【从外表导入】；template用于【数据回写】
		var templateId = '${templateId}';
		var tableNm = '${tableNm}';
		var fieldMgr = null;
		$(function() {
			if(operType&&operType =="template"){
				//【设置回写】页面路径
				$(".l-form").hide();
			}
			initTab();
			initTree();
			initBtn();
			initDsImport();
		});

		function initDsImport() {
			var readonly = false;
			$("#dsImport").ligerComboBox({
				url : "${ctx}/bione/mtool/datasource/dsImportList",
				onSelected : function(id, text) {
					if(id){
						dsId=id;
					}
					initTab();
					initTree();
				},
				readonly:readonly
			});
			$.ligerui.get("dsImport").selectValue(dsId);
		}


		function initBtn(){
			var buttons = [];
			buttons.push({
				text : '取消',
				onclick : f_close
			});
			buttons.push({
				text : '确定',
				onclick : f_ok
			});
			BIONE.addFormButtons(buttons,true);
		}

		function f_close(){
			if(operType == "template"){
				parent.BIONE.closeDialog("selectSchemaTableBox");
			}else
				BIONE.closeDialog("selectSchemaTableBox");
		}
		function f_ok(){
			var treeNodes = leftTreeObj.getSelectedNodes();
			if(treeNodes == null || treeNodes.length == 0){
				BIONE.tip("请选择数据库表");
				return
			}
			if(!fieldMgr){
				BIONE.tip("请选择字段");
				return
			}
			var fields = fieldMgr.getColumnInfo();
			if(!fields)
				return ;
			if(operType=="template"){
				parent.gatherData(fields,treeNodes[0].id);
			}else
				BIONE.closeDialog("selectSchemaTableBox", null, true,
						fields);
		}

		function initTree() {
			//树
			var setting = {
				data : {
					key : {
						name : "text"
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId"
					}
				},
				view : {
					selectedMulti : false
				},

				callback : {
					onClick : zTreeOnClick,
					onNodeCreated : function(event, treeId, treeNode) {
						if (treeNode.id == "ROOT") {
							//若是根节点，展开下一级节点
							leftTreeOb.expand(treeNode, true, true, true);
						}
					}
				}
			};
			leftTreeObj = $.fn.zTree.init($("#leftTree"), setting);
			$("#template.left.center").hide();
			window['maintab'] = $("#tab").ligerTab({
				contextmenu : false
			});
			loadTree("${ctx}/rpt/input/table/getTableTree.json?dsId="+dsId, leftTreeObj,"1");

			$("#treeSearchIcon").live(
					'click',
					function() {
						var searchNm = $("#treeSearchInput").val();
						loadTree("${ctx}/rpt/input/table/getTableTree.json?dsId="+dsId+"&searchNm="
								+ searchNm, leftTreeObj);
					});

			$("#treeSearchInput").bind("keydown",function(e){
				if(e.keyCode == 13){
					var searchNm = $("#treeSearchInput").val();
					loadTree("${ctx}/rpt/input/table/getTableTree.json?dsId="+dsId+"&searchNm="
							+ searchNm, leftTreeObj);
				}
			});
		}
		//渲染树
		/*
        function initTree() {
            var  url_ =  "${ctx}/rpt/input/table/getTableTree.json?dsId="+dsId;
		var setting ={
				async:{
					enable:true,
					url:url_,
					dataType:"json"
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
					onClick : zTreeOnClick,
					onNodeCreated : function(event,treeId,treeNode){
						if (treeNode.id == "ROOT") {
							//若是根节点，展开下一级节点
							leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
						}
					}
				}

		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		$("#template.left.center").hide();
	}
	*/

		function loadTree(url, component, data) {
			$.ajax({
				cache : false,
				async : true,
				url : url,
// 			dataType : 'json',
				type : "POST",
				beforeSend : function() {
				},
				success : function(result) {
					var nodes = component.getNodes();
					var num = nodes.length;
					for ( var i = 0; i < num; i++) {
						component.removeNode(nodes[0], false);
					}
					if(!result)return ;
					if (result.length > 0) {
						component.addNodes(null, result, false);
						component.expandAll(true);
					}
					if(data&&data=="1"&&tableNm&&tableNm!=null)
					{
						currentNodeId = tableNm;
						var treeNode = component.getNodeByParam("id", tableNm , null);
						component.selectNode(treeNode,true);
						initCurrentTab(treeNode);
					}
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}

			});
		}


		function zTreeOnClick(event, treeId, treeNode){
			if(currentNodeId ==  treeNode.tId){
				return ;
			}
			currentNodeId = treeNode.tId;
			initCurrentTab(treeNode);
		}

		//渲染选项卡
		function initTab() {
			tabObj = $("#tab").ligerTab({
				contextmenu : true
			});
		}

		function  initCurrentTab(treeNode){
			if(!treeNode || treeNode==null)
				return ;
			var	curTabUri = "${ctx}/rpt/input/table/showcolumn?dsId="+dsId+"&tableNm="+treeNode.text;
			var	tabTitleName = "查看表["+treeNode.text+"]的列信息";
			if(operType&&operType =="template"){
				curTabUri = "${ctx}/rpt/input/temple/editRewritecolumn?dsId="+dsId+"&tableNm="+treeNode.text+"&templateId="+templateId;
				tabTitleName = "编辑表["+treeNode.text+"]";
			}
			var height = $("#tab").height() - 10;
			tabObj.removeTabItem("tableInfoTab");
			tabObj
					.addTabItem({
						tabid : "tableInfoTab",
						text:tabTitleName,
						showClose : false,
						content : '<iframe frameborder="0" id="tableInfoTabFrame" name="tableInfoTabFrame" style="height:'+height+'px;" src='+curTabUri+' ></iframe>'
					});
			tabObj.selectTabItem("tableInfoTab");
		}

	</script>
</head>
<body>
<div id="template.left.up.icon">
	<img src="${ctx}/images/classics/icons/application_side_tree.png" />
</div>
<div id="template.left.up">
	<span style="font-size: 12" id="left_up_span">数据库表</span>
</div>
</body>
</html>