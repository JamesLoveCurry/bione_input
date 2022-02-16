<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1D.jsp">
<head>
<style type="text/css">
#mainform {
	display: none;
}
</style>
<script type="text/javascript">
	var form;
	var nodeType = '${nodeType}';
	var taskNodeDefId = '${taskNodeDefId}';
	var taskOrderno = '${taskOrderno}';
	var taskNodeNm = '${taskNodeNm}';
	var handleType = "00";
	var taskUnifyNode ;
	var selectedType ;
	var frameObj;
	$(function() {
		initBaseInfo();
		initData();
		initFrame();
	});
	
	function onCheckOrg(orgs){
		if(frameObj)
			frameObj.onCheckOrg(orgs,nodeType);
	}
	
	function onCancelOrg(orgs){
		if(frameObj)
			frameObj.onCancelOrg(orgs);
	}
	
	function initFrame(){
		if(form&&taskUnifyNode!=null){
			form.setData({
				handleType : taskUnifyNode.handleType
			});
		}
		var src = "${ctx}/rpt/input/task/toCommon";
		selectedType = "01";
		if(taskUnifyNode){
			selectedType =taskUnifyNode.handleType;
			if(taskUnifyNode.handleType=="03")
			{
				src = "${ctx}/rpt/input/task/toCustom";
			}
		}
		$("#nodeContent")
				.html(
						'<iframe frameborder="0" id="taskInfoFrame1_'+taskNodeDefId+'" name="taskInfoFrame1_'+taskNodeDefId+'" style="height:100%;width:100%" src="'+src+'"></iframe>');
	}
	
	function initData(){
		taskUnifyNode =  parent.getTaskUnifyNode(taskNodeDefId);
	}
	function initBaseInfo() {
		window.parent.appendTaskUnifyNodeObjs(window);
	}
	
	function getHandleType(){
		return "00";
	}

	function getTaskObjByOrg(org,taskObjs){
		for(var i = 0 ;i <taskObjs.taskObjs.length;i++){
			if(taskObjs.taskObjs[i].org == org )
				return taskObjs.taskObjs[i];
		}
		return null;
	}
	function getTaskUnifyNode() {
		var nodeTaskObj = window["taskInfoFrame1_"+taskNodeDefId].getTaskObj();
		if(nodeTaskObj==-1)
		{
			return "-1";
		}
		if(taskObj == "-1"){
			return "-1";
		}
		var taskObjType = nodeTaskObj.taskObjType;
		var checkedNodes = window.parent.leftTreeObj.getCheckedNodes();
		var objs = [];
		if(taskObjType=="AUTH_OBJ_USER"){
			//用户节点,每个节点有自己的机构信息
			for(var  i = 0 ;i <checkedNodes.length;i++){
				var taskObj = getTaskObjByOrg(checkedNodes[i].id,nodeTaskObj);
				if(taskObj&&taskObj.taskInfo){
					objs.push({
						taskNodeDefId : taskNodeDefId,
						taskObjType : nodeTaskObj.taskObjType,
						taskObjIdMap :taskObj.taskInfo,
						taskOrderno : taskOrderno,
						taskNodeNm : taskNodeNm,
						nodeType : nodeType,
						orgNo : taskObj.org
					});
				}
			}
		}else{
			//角色节点,每个节点需要从上级机构节点取出节点信息
			var orgNos = "";
			for(var  i = 0 ;i <checkedNodes.length;i++){
				orgNos = orgNos +","+checkedNodes[i].id;
			}
			
			objs.push({
				taskNodeDefId : taskNodeDefId,
				handleType : handleType,
				taskObjType : nodeTaskObj.taskObjType,
				taskObjIdMap : nodeTaskObj.taskObjs[0]? nodeTaskObj.taskObjs[0].taskInfo : [],
				taskOrderno : taskOrderno,
				taskNodeNm : taskNodeNm,
				nodeType : nodeType,
				orgNo : orgNos
			});
		}
		return objs;
	}
</script>

<title>指标目录管理</title>
</head>
<body>
</body>
</html>