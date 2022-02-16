<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1C.jsp">
<head>
<script type="text/javascript">

		var form;
		var rptTskInfo = window.parent.deployTask.rptTskInfo!=null&&window.parent.deployTask.rptTskInfo;
		var taskNodeOrgList = window.parent.deployTask.taskNodeOrgList;
		var isNeedCheck =  window.parent.isNeedCheck;
		var taskUnifyNodeList = window.parent.deployTask.taskUnifyNodeList;
		var orgs =   window.parent.deployTask.orgs;
		var openOrgs =   window.parent.deployTask.openOrgs;
		var defaultTaskDefId = '${defaultTaskDefId}';
		var selectedType;
		var nodeObj;
		$(function() {
			window.parent.taskManage.taskNodeManage=window;
			initBaseInfo(); 
			initform();
			//if(isNeedCheck)
			//	gatherData();
		});
		function enableBtn(){
			window.parent.isNeedCheck = false;
			isNeedCheck = false;
			window.parent.enableBtn();
		}
		
		function enableBtn1(){
			window.parent.enableBtn();
		}

		function initBaseInfo(){
			if(rptTskInfo&&rptTskInfo.nodeType){
				nodeType = rptTskInfo.nodeType
			}else
				nodeType = "01";
			
		}
		
		function initform(){

			//创建表单结构 
			form = $("#mainform")
					.ligerForm(
							{
								inputWidth : 600,
								labelWidth : 90,
								space : 40,
								validate : true,
								fields : [ {
									name : "nodeType",
									type : "hidden"
								}, {				
									display : "流程选择",
									name : "taskDefId",
									newline : true,
									type : "select",
									comboboxName : 'taskDefIdBox',
									validate : {
										required : true,
										messages : {
											required : "请选择流程"
										}
									},
									options : {
										initValue  : '156',
										url : "${ctx}/rpt/input/task/getTaskList.json"
									}}]
							});
			var taskDefId = defaultTaskDefId;
			if(rptTskInfo&&rptTskInfo.taskDefId)
				taskDefId  =  rptTskInfo.taskDefId;
			
			form.setData({
				taskDefId:taskDefId
			});
			
			$.ligerui.get("taskDefIdBox").set("onSelected", onSelected);
			
			var src = "${ctx}/rpt/input/task/toUnifyNode?taskDefId="+taskDefId;
			/*
			if(rptTskInfo&&rptTskInfo.nodeType){
				if(rptTskInfo.nodeType=="01")
					src = "${ctx}/rpt/input/task/toUnifyNode";
				else
					src = "${ctx}/rpt/input/task/toSeparateNode";
			}else{
				src = "${ctx}/rpt/input/task/toUnifyNode";
			}*/
			
			$("#nodeContent").html('<iframe frameborder="0" id="taskInfoFrame1" name="taskInfoFrame1" style="height:100%;width:100%;" src="'+src+'"></iframe>');
		}
		
		function onSelected(val,text){
			$("#taskInfoFrame1").attr("src","${ctx}/rpt/input/task/toUnifyNode?taskDefId="+val+"&d="+new Date().getTime());
		}

		function checkTaskUnifyNode(taskUnifyNodeList ){
			if(!taskUnifyNodeList||taskUnifyNodeList.length==0)
			{
				BIONE.tip('请选择任务节点');
				return false;
			}
			for(var i = 0 ;i <taskUnifyNodeList.length;i++){
				var taskUnifyNode =  taskUnifyNodeList[i];
				for(var x =0 ;x<taskUnifyNode.length;x++){
					if(taskUnifyNode[x].taskObjIdMap==null || taskUnifyNode[x].taskObjIdMap.length==0)
					{
						BIONE.tip("请选择节点["+taskUnifyNode[x].taskNodeNm+"]的执行对象信息");
						return false;
					}
					if(!taskUnifyNode[x].taskObjType||taskUnifyNode[x].taskObjType==null)
					{
						BIONE.tip("请选择节点["+taskUnifyNode[x].taskNodeNm+"]的执行对象类型");
						return false;
					}
				}
			}
			return true;
		}
		function gatherData(){
			//var nodeType = $("#nodeType").val();
			var taskDefId = $("#taskDefId").val();
			var  deployTask = window.parent.deployTask;
			deployTask.rptTskInfo.nodeType="01";
			deployTask.rptTskInfo.taskDefId = taskDefId;
			if(nodeType == "01"){
				var nodeInfo =  nodeObj.getNodeInfo();
				if(nodeInfo == "-1")
				{
					window.parent.isNeedCheck = false;
					isNeedCheck = false;
					return ;
				}
				if(!nodeInfo.taskUnifyNodeList){
					window.parent.isNeedCheck = false;
					isNeedCheck = false;
					return;
				}
				deployTask.taskUnifyNodeList = nodeInfo.taskUnifyNodeList;
				if(!checkTaskUnifyNode(deployTask.taskUnifyNodeList))
				{
					window.parent.isNeedCheck = false;
					isNeedCheck = false;
					return;
				}
				deployTask.orgs = nodeInfo.orgs;
			}else{
				deployTask.taskNodeOrgList = nodeObj.getTaskNodeOrgList();
			}
			window.parent.doOper();
		}
		
</script>

<title>指标目录管理</title>
</head>
<body>
</body>
</html>