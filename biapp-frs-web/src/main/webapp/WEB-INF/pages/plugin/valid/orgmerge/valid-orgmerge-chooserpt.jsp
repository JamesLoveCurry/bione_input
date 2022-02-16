<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">

<script type="text/javascript">
	var leftTreeObj;
	var busiType = '${busiType}';
	var searchNm;//报表搜索
	$(function(){
		$("#treeContainer").height($("#center").height()-2);
		initTree();
		refreshTree();		
		//按钮
		var btn = [{
			text:'取消',
			onclick:function(){
				BIONE.closeDialog("rptIdxTreeWin");
			}
		},{
			text:'确定',
			onclick:f_save
			}
		];
		
		BIONE.addFormButtons(btn);
		jQuery.metadata.setType("attr","validate");
		BIONE.validate($("#mainform"));
		
		function loadTree(){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/design/cfg/getRptTree",
				dataType : 'json',
				type : "post",
				data : {
					busiType : busiType,
					searchNm: searchNm
				},
				success : function(result){
					if(result != null && typeof result != "undefined"){
						var branchTemplateIds = parent.$("#branchTemplateId").val();
						if(branchTemplateIds){
							var templateIdArray = [];
							var templateIdMap  = {};
							templateIdArray = branchTemplateIds.split(',');
							for(var templateId in templateIdArray){
								templateIdMap[templateIdArray[templateId]] = templateId;
							}
							for(var rptNode in result){
								if(templateIdMap[result[rptNode].params.cfgId]){
									result[rptNode].checked = true;
								}
							}
						}
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
		
		//树
		function initTree(){
			var setting = {
				data:{
					key:{
						name:"text"
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
				check : {
					enable : false
				}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting);
			//搜索框事件，点击图片可进行搜索
			$("#treeSearchIcon").bind("click",function (){
				searchNm = $("#treeSearchInput").val();
				loadTree();
			});
			//键盘按回车键,也可进行搜索
			$("#treeSearchInput").bind("keydown",function (e){
				if(e.keyCode == 13){
					searchNm = $("#treeSearchInput").val();
					loadTree();
				}
			});
		}
		
		function refreshTree() {  //刷新树
			if (leftTreeObj) {
				loadTree();
			}
		}
		
		function f_save(){
			//debugger;
			var node = leftTreeObj.getSelectedNodes()[0];
			if(node == null || node == "" || node.params.nodeType != '03'){
				BIONE.tip("请选择报表！");
			}else{
				parent.$("#branchTemplateId").val(node.params.cfgId);
				parent.$("#branchTemplateIdCombo").val(node.text);
				parent.branchTemplateId = node.params.cfgId;
				BIONE.closeDialog("rptIdxTreeWin");
			}
		}
	});

</script>
</head>
<body>
<div id="template.center">
	<div id="treeSearchbar"
		 style="width: 98%;height: 20px;margin-top: 2px;padding-left: 2px;">
		<ul>
			<li style="width: 100%;text-align: left;">
				<div class="l-text-wrapper" style="width: 100%;">
					<div class="l-text l-text-date" style="width: 100%;">
						<input id="treeSearchInput" type="text" class="l-text-field" style="width: 98%;"/>
						<div class="l-trigger">
							<i id="treeSearchIcon" class="icon-search search-size"></i>
						</div>
					</div>
				</div>
			</li>
		</ul>
	</div>
	<div id="treeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="tree"
				style="font-size:12px; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
</div>
</body>
</html>