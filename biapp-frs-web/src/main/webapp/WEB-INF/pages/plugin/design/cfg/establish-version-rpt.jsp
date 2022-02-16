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
					busiType : busiType
				},
				success : function(result){
					if(result != null
							&& typeof result != "undefined"){
						var rptIds = parent.$("#rptIdx").val();
						if(rptIds){
							var rptIdArray = [];
							var rptIdMap  = {};
							rptIdArray = rptIds.split(',');
							for(var rptId in rptIdArray){
								rptIdMap[rptIdArray[rptId]] = rptId;
							}
							for(var rptNode in result){
								if(rptIdMap[result[rptNode].params.realId]){
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
					enable : true,
					chkStyle : "checkbox",
					chkboxType : {
						"Y" : "ps",
						"N" : "ps"
					}
				}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		}
		
		function refreshTree() {  //刷新树
			if (leftTreeObj) {
				loadTree();
			}
		}
		
		function f_save(){
			var nodes = leftTreeObj.getCheckedNodes(true);
			if(nodes == null || nodes == ""){
				BIONE.tip("请选择报表！");
			}else{
				var rptIds="";
				var rptNms="";
				for(var i in nodes){
					if(nodes[i].params.nodeType == "03"){
						rptIds += nodes[i].params.realId + ",";
						rptNms += nodes[i].text + ",";
					}
				}
				rptIds = rptIds.substring(0,rptIds.length-1);
				parent.$("#rptIdx").val(rptIds);
				parent.$("#rptIdxBox").val(rptNms);
				BIONE.closeDialog("rptIdxTreeWin");
			}
		}
	});

</script>
</head>
<body>
<div id="template.center">
			<div id="treeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="tree"
				style="font-size: 12; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
	</div>
</body>
</html>