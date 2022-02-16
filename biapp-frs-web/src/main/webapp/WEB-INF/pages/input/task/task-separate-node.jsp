<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5A.jsp">
<head>
<script type="text/javascript">

		var grid;
		var taskList;
		var taskNodeObjList = window.parent.deployTask.taskNodeObjList;
		var taskDefId = window.parent.deployTask.rptTskInfo!=null&&window.parent.deployTask.rptTskInfo.taskDefId;
		var selectedObjs;
		var isNeedCheck =  window.parent.isNeedCheck;
		$(function() {
			window.parent.taskManage.taskNodeManage=window;
			initBaseInfo(); 
			if(isNeedCheck)
				gatherData();
		});
		
		function initBaseInfo(){
			initNodeInfo();
			initTaskNodeGrid();
			initTaskList();
			if(taskNodeObjList==null)
				initTaskNodeList(taskDefId);
			else
				initData();
		}
		
		function initData(){
			currentNodeValue = taskDefId;
			liger.get("taskList").setValue(taskDefId);
			
			if(taskNodeObjList!=null){
				for(var i = 0 ;i <taskNodeObjList.length;i++){
					
					var taskObjVOList = taskNodeObjList[i].taskObjVOList;
					var volist = [];
					if(taskObjVOList!=null){
						for(var j =0;j <taskObjVOList.length;j++){
							volist[j]={
									taskObjId:taskObjVOList[j].taskObjId,
									taskObjNm:taskObjVOList[j].taskObjNm,
									taskObjType:taskObjVOList[j].taskObjType
							};
						}
					}
					grid.addRow({
						isCanInterrupt : taskNodeObjList[i].isCanInterrupt,
						taskNodeDefId : taskNodeObjList[i].taskNodeDefId,
						taskNodeNm : taskNodeObjList[i].taskNodeNm,
						taskOrderno : taskNodeObjList[i].taskOrderno,
						nodeType : taskNodeObjList[i].nodeType,
						taskNodeTskobjRel : volist
					});
				}
			}
			
		}
		
		function initNodeInfo(){
			if(taskNodeObjList == null ) return ;
			
			for (var i=0 ; i<taskNodeObjList.length;i++){
				
				var nodeObj = taskNodeObjList[i];
				var taskNodeDefId = nodeObj.taskNodeDefId;
			}
		}
		
		function initTaskNodeGrid(){
			

			var $taskNodeList = $("#right");
			
			var height = $taskNodeList.height()-10;
			var width =  $taskNodeList.width();
			grid = $("#taskNodeList").ligerGrid({
				height : height,
				width :width,
				options : {
					data : [ {
						taskDefId : taskDefId
					}]
				},
				columns : [ {
					display : '节点序号',
					name : 'taskOrderno',
					width : "10%"
				}, {
					display : '节点名称',
					name : 'taskNodeNm',
					width : "20%"
				}, {
					display : '节点类型',
					name : 'nodeType',
					width : "20%",
					render : function(row, index, value, column) {
						if(value=="01"){
							return "填报";
						}else if(value=="02")
							return "审批"
					}
				},{
					display : '执行对象',
					name : 'taskObjNm',
					width : "30%",
					render : function(row, index, value, column) {
						var taskNodeDefId = row.taskNodeDefId;
						var showText = "";
						var taskNodeTskobjRel = row.taskNodeTskobjRel;
						if(taskNodeTskobjRel != null &&typeof taskNodeTskobjRel!="undefined")
						{
							for(var i = 0 ;i <taskNodeTskobjRel.length;i++){
									if(i!=0)
										showText=showText+",";
								var taskObjType =  taskNodeTskobjRel[i].taskObjType;
								var tmpText = "";
								if(taskObjType=="01")
									tmpText =  "用户[";
								if(taskObjType=="02")
									tmpText =  "角色[";
								if(taskObjType=="03")
									tmpText = "岗位[";
								if(taskObjType=="04")
									tmpText = "部门[";
								showText =showText +tmpText+taskNodeTskobjRel[i].taskObjNm+"]";
							}
						}
						/*
						if(keymap.length !=0)
							{
								var nodeObj = keymap[taskNodeDefId];
								if(nodeObj.taskObjVOList!=null)
									for(var i=0;i<nodeObj.taskObjVOList.size;i++){
										if(i!=0)
											showText = showText +",";
										showText = showText+ taskObjVOList[i].taskObjNm;
									}
							}
						*/
						//showText = showText+'<a name="' + column.name + '" class="oper"  onclick="onchoseExeObjs('+index+')">选择</a>';
						return showText;
					},
					editor:{
							type : "select",
							ext : onchoseExeObjs
						}
				}],
				checkbox : false,
				usePager : true,
				isScroll : false,
				rownumbers : true,
				alternatingRow : true,//附加奇偶行效果行
				dataAction : 'server',//从后台获取数据
	            delayLoad:true,
				url :  "${ctx}/rpt/input/task/getTaskNodeList.json",
				usePager : false, //服务器分页
				sortName : 'taskOrderno',//第一次默认排序的字段
				sortOrder : 'asc', //排序的方式
				toolbar : {},
				enabledEdit : true,
				clickToEdit : true
			});
		}
		
		var dataMap =[];
		var currentNodeValue="" ;
		//渲染GRID
		function initTaskList() {
			
			var $taskList = $("#left");
			
			var height = $taskList.height();
			var width =  $taskList.width();
			taskList = $("#taskList").ligerListBox({
                isShowCheckBox: false,
                isMultiSelect: false,
                url :  "${ctx}/rpt/input/task/getTaskList.json",
                onSelected: function(value){
                	if(value == currentNodeValue) return ;
                	if(!currentNodeValue)
                		currentNodeValue = "1";
            		dataMap[currentNodeValue] = getCurrentTaskInfo();
                	
                	if(dataMap[value]){
                		var nodeInfo = dataMap[value];
                		initNodeData(nodeInfo);
                	}else{
                    	initNodeList(value);
                	}
                	currentNodeValue = value;
                },
                width: width,
                height:height
            });
		}
		
		function initNodeData(taskNodeObjList){
			var rows = grid.rows;
			$.each(rows, function(i, n) {
				grid.deleteRow(n);
			});
			for(var i = 0 ;i<taskNodeObjList.length;i++){
				
				grid.addRow({
					isCanInterrupt : taskNodeObjList[i].isCanInterrupt,
					taskNodeDefId : taskNodeObjList[i].taskNodeDefId,
					taskNodeNm : taskNodeObjList[i].taskNodeNm,
					taskOrderno : taskNodeObjList[i].taskOrderno,
					nodeType : taskNodeObjList[i].nodeType,
					taskNodeTskobjRel : taskNodeObjList[i].taskObjVOList
				});
			}
		}
		
		function getCurrentTaskInfo(){

			var rows = grid.rows;
			if(rows==null ||rows.length<=0){
				return ;
			}
			var taskNodeObjList = [];
			for(var i=0;i<rows.length;i++){
				taskNodeObjList[i]={taskNodeDefId:null,taskNodeNm:null,taskOrderno:null,isCanInterrupt:null,taskObjVOList:[]};
				taskNodeObjList[i].taskNodeDefId=rows[i].taskNodeDefId;
				taskNodeObjList[i].taskNodeNm=rows[i].taskNodeNm;
				taskNodeObjList[i].taskOrderno=rows[i].taskOrderno;
				var isCanInterrupt= rows[i].isCanInterrupt==null||typeof  rows[i].isCanInterrupt=="undefined"?"":rows[i].isCanInterrupt;
				taskNodeObjList[i].isCanInterrupt=isCanInterrupt;
				taskNodeObjList[i].nodeType=rows[i].nodeType;
				
				 if(rows[i].taskNodeTskobjRel!=null && typeof rows[i].taskNodeTskobjRel!="undefined"){
					 var taskNodeTskobjRel = rows[i].taskNodeTskobjRel;
					 for(var j=0;j<taskNodeTskobjRel.length;j++){
						 var taskInfo = {taskObjId:null,taskObjNm:null,taskObjType:null};
						 taskInfo.taskObjId=taskNodeTskobjRel[j].taskObjId;
						 taskInfo.taskObjNm=taskNodeTskobjRel[j].taskObjNm;
						 taskInfo.taskObjType=taskNodeTskobjRel[j].taskObjType;
						 
						 taskNodeObjList[i].taskObjVOList[j] = taskInfo;
					 }
				 }
			}
			return taskNodeObjList;
		}
		
		function initNodeList(taskDefId){
			initTaskNodeList(taskDefId);
		}
		
		function initTaskNodeList(taskDefId){
			grid.setParm('taskDefId', taskDefId);
			grid.loadData();
		}
		
		function onchoseExeObjs(){
			var index = arguments[1];
			var selectedObjs="";
			var row=grid.getRow(index);
			var tskObjRel = row.taskNodeTskobjRel==null|| typeof row.taskNodeTskobjRel=="undefined"?"":row.taskNodeTskobjRel;
			BIONE.commonOpenLargeDialog("选择部门", "onchoseExeObjs", "${ctx}/rpt/input/task/chooseExeObjs?selectedObjs="+encodeURI(encodeURI(tskObjRel))+"&parentRowIndex="+index);
		}
		//index,ids,nms,types
		function refreshRowContent(index,ids,nms,types){
			var row = grid.getRow(index);
			var taskObjNm = "";
			if(ids!=null && ids.length>0)
			{
				var updRowData={
						taskObjNm:null,
						taskNodeTskobjRel:[]
				};
				for(var i = 0 ;i <ids.length;i++){
					updRowData.taskNodeTskobjRel[i]={
							taskObjId:ids[i],
							taskObjNm:nms[i],
							taskObjType:types[i]
					};
					if(i!=0)
					{
						taskObjNm = taskObjNm+",";
					}
					taskObjNm = taskObjNm +nms[i];
				}
				updRowData.taskObjNm=taskObjNm;
			}
			grid.updateRow(row,updRowData);
			grid.endEdit();
		}
		
		function gatherData(){
			var rows = grid.rows;
			if(rows==null ||rows.length<=0){
				BIONE.tip('请选择流程' );
				return ;
			}
			//window.parent.deployTask.taskNodeObjList;
			var taskNodeObjList = [];
			for(var i=0;i<rows.length;i++){
				taskNodeObjList[i]={taskNodeDefId:null,taskNodeNm:null,taskOrderno:null,isCanInterrupt:null,taskObjVOList:[]};
				taskNodeObjList[i].taskNodeDefId=rows[i].taskNodeDefId;
				taskNodeObjList[i].taskNodeNm=rows[i].taskNodeNm;
				taskNodeObjList[i].taskOrderno=rows[i].taskOrderno;
				var isCanInterrupt= rows[i].isCanInterrupt==null||typeof  rows[i].isCanInterrupt=="undefined"?"":rows[i].isCanInterrupt;
				taskNodeObjList[i].isCanInterrupt=isCanInterrupt;
				taskNodeObjList[i].nodeType=rows[i].nodeType;
				
				 if(rows[i].taskNodeTskobjRel!=null && typeof rows[i].taskNodeTskobjRel!="undefined"){
					 var taskNodeTskobjRel = rows[i].taskNodeTskobjRel;
					 for(var j=0;j<taskNodeTskobjRel.length;j++){
						 var taskInfo = {taskObjId:null,taskObjNm:null,taskObjType:null};
						 taskInfo.taskObjId=taskNodeTskobjRel[j].taskObjId;
						 taskInfo.taskObjNm=taskNodeTskobjRel[j].taskObjNm;
						 taskInfo.taskObjType=taskNodeTskobjRel[j].taskObjType;
						 
						 taskNodeObjList[i].taskObjVOList[j] = taskInfo;
					 }
					 
				 }else{
						BIONE.tip('流程节点['+taskNodeObjList[i].taskNodeNm+"]还未选择" );
						return ;
				 }
			}
			window.parent.deployTask.taskNodeObjList = taskNodeObjList;
			var selectedItems = taskList.getSelectedItems();
			if(selectedItems)
				window.parent.deployTask.rptTskInfo.taskDefId = selectedItems[0].id;

			window.parent.isNeedCheck = false;
			window.parent.doOper();
			

		}
		
		
</script>

<title>指标目录管理</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">授权用户</div>
	<div id="template.right">
		<div id="navtab" style="overflow: hidden;"></div>
	</div>
</body>
</html>