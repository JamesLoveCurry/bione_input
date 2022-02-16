<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1F.jsp">
<head>
<script type="text/javascript">

        var form;
        var defaultTaskDefId = '${defaultTaskDefId}';
        var selectedType;
        var nodeObj;
        var dataDate;
        var taskId = '${taskId}';
        var orgType = '${orgType}';
        var triggerType = '${triggerType}';
		var selorgs = [];
        var upOrg = [];
        var seltaskObjs ={};
        var seltaskAuthType ={};
        var deployTaskVO = '${deployTaskVO}';
        var operType = '${operType}';
        var insId = '${insId}';
        var taskTitle = '${taskTitle}';
        if(deployTaskVO){
        	deployTaskVO = JSON2.parse(deployTaskVO)
      	}
      	var insNodes;
      	if(operType == 'del'){
      		insNodes = JSON2.parse('${insNodes}');
       	}
        
        $(function() {
            initform();
            initBtn();
            initData();
        });

		function formatDate(date){
			if(date && date.length == 8){
				return date.substring(0,4) + '-' + date.substring(4,6) + '-' + date.substring(6,8);
			}else{
				return "";
			}
		}
		
        function initData(){
        	try{
        	    var val = deployTaskVO;
        	    form.setData({
        	        taskDefId : val.rptTskInfo.taskDefId,
        	        taskTitle : taskTitle,
    	       		loadDataMark : val.loadDataMark,
    	       		dataDate : formatDate(val.dataDate)
        	    });
        	    dataDate = formatDate(val.dataDate);
        	    selorgs = val.orgs;
        	    upOrg = val.upOrg;
        	    tasks = val.rptTskInfo.taskId;



        	    var taskUnifyNodeList = val.taskUnifyNodeList;
        	    for(var i = 0; i < taskUnifyNodeList.length; i++){
        	        var taskNode = taskUnifyNodeList[i];
        	        var taskObjMap = taskNode.taskObjIdMap;
        		    var taskObjsArr = [];
        	        for(var j = 0; j < taskObjMap.length; j++){
        	            var taskObjId = taskObjMap[j].taskObjId;
        	            taskObjsArr.push(taskObjId);
        	        }
        	        if(!seltaskObjs[taskNode.taskNodeDefId]){
        	        	seltaskObjs[taskNode.taskNodeDefId] = taskObjsArr;
	        	        seltaskAuthType[taskNode.taskNodeDefId] = taskNode.taskObjType;
        	        }else{
        	        	seltaskObjs[taskNode.taskNodeDefId] = seltaskObjs[taskNode.taskNodeDefId].concat(taskObjsArr);
        	        }

        	        
                }
        	    
        	}catch (e){
        		
        	}
        }
        
        function initBtn(){


            var buttons = [];
            buttons.push({
                text : '取消',
                onclick : f_close
            });

            if(operType != 'del'){
            	buttons.push({
                    text : '下发',
                    onclick : f_deploy
                });

            }else{
            	buttons.push({
                    text : '删除',
                    onclick : function(){
						$.ligerDialog.confirm("确定删除选中节点？",function(btn){
							if(btn){
								f_del();
							}
						});

                   	}
                });
            }
            BIONE.addFormButtons(buttons);
        }
        
        function f_close(){
            BIONE.closeDialog("selectDeployBox");
        }

        function trim(t){
            return (t||"").replace(/^\s+|\s+$/g, "");
        }
        
        function setTaskTitle(taskTitle){
//             $("#taskTitle").val(taskTitle);
        }
        function f_del(){
        	var nodeInfo =  nodeObj.getNodeInfo();
        	var defNodes = nodeInfo.taskUnifyNodeList;

        	var resdelInfo = {};
        	var dn = {};
        	for(var i = 0; i < defNodes.length; i++){
        		var defInfo = defNodes[i];
        		var selInsNodes = defInfo.taskObjIdMap;
        		var resDef = [];
        		for(var j = 0; j < selInsNodes.length; j++){
            		var resInsNode = {
           				taskObjId : selInsNodes[j].taskObjId,
         				taskObjType : selInsNodes[j].taskObjType
               		};
            		resDef.push(resInsNode);
           		}
           		if(resDef.length > 0){
           			dn[defInfo.nodeType] = resDef;
          		}
           	}
        	resdelInfo.insId = insId;
        	resdelInfo.nodes = JSON2.stringify(dn);
        	$.ajax({
                cache : false,
                url : "${ctx}/rpt/input/task/tskInsNode?d="+ new Date().getTime(),
                type : "get",
                data : resdelInfo,
                success : function(result) {
                    BIONE.tip('节点删除成功!');
                    f_close();
                },
                error : function(result, b) {
                    BIONE.tip('节点删除失败,发现系统错误 <BR>错误码：' + result.status);
                }
            });


        }
        function f_deploy(){
            var taskTitle = $("#taskTitle").val();
            if(!taskTitle||taskTitle==null||trim(taskTitle)=="")
            {
                BIONE.tip('标题不可以为空!');
                return ;
            }
            var dataDate = $("#dataDate").val();
            if(triggerType=="1"&&(!dataDate||dataDate==null||trim(dataDate)==""))
            {
                BIONE.tip('数据日期不可以为空!');
                return ;
            }
            var taskDefId = $("#taskDefId").val();
            if(taskDefId == "1"){
                BIONE.tip('流程选择不能为空!');
                return;
            }

            var deployTask = gatherData();
            if(!deployTask||deployTask==null)
                return ;
            //判断下发的用户/角色是否存在于下发过的任务中（当前数据日期）
            if(triggerType == "1"){
	           var deployMsg = getDeployAuthObjInfo(deployTask);
	           if(deployMsg != null && deployMsg != ""){
	        	   BIONE.showError(deployMsg);
	        	   return;
	           }
           }
           // return;
            $.ligerDialog.confirm('确实要下发吗?', function(yes) {
                if(yes) {
                    $.ajax({
                        cache : false,
                        async : true,
                        url : "${ctx}/rpt/input/task/deployTask?d="
                                + new Date().getTime(),
                        dataType : 'json',
                        contentType : "application/json",
                        type : "post",
                        data : JSON2.stringify(deployTask),
                        success : function(result) {
                            BIONE.tip('任务下发成功!');
                            window.parent.refreshIt();
                            f_close();
                        },
                        error : function(result, b) {
                            BIONE.tip('任务下发失败,发现系统错误 <BR>错误码：' + result.status);
                        }
                    });
                }
            });
        }
        
        function getDeployAuthObjInfo(deployTask){
        	var msg = null;
        	$.ajax({
                cache : false,
                async : false,
                url : "${ctx}/rpt/input/task/getDeployAuthObjInfo",
                dataType : 'json',
                contentType : "application/json",
                type : "post",
                data : JSON2.stringify(deployTask),
                success : function(result) {
                	msg = result.msg;
                }
            });
        	return msg;
        }

        function enableBtn(){
        }
        
        function enableBtn1(){
        }
        function initform(){
            var fields= [];
            fields.push({
                name : "nodeType",
                type : "hidden"
            });
            fields.push({
                display : "标题",
                name : "taskTitle",
                newline : false,
                type : 'text',
                width : 120,
                labelWidth : 40,
                validate : {
                    required : true
                }
            });
            if(triggerType=="1"){
                fields.push({
                    display : "数据日期",
                    name : "dataDate",
                    newline : false,
                    type : 'date',
                    format : 'yyyy-MM-dd',
                    width : 120,
                    labelWidth : 60,
                    options:{
                        onChangeDate: function(value){
                            $("#dataDate").val(value);
                            dataDate = value.replace("-","").replace("-","");
                        }
                    },
                    validate : {
                        required : true
                    }
                });
            }
            fields.push({
                display : "初始化数据",
                name : "loadDataMark",
                newline : false,
                type : 'checkbox',
                width : 20,
                labelWidth : 80,
            });
            fields.push({
                    display : "流程选择",
                    name : "taskDefId",
                    newline : false,
                    type : "select",
                    comboboxName : 'taskDefIdBox',
                    labelWidth : 60,
                    validate : {
                        required : true,
                        messages : {
                            required : "请选择流程"
                        }
                    },
                    options : {
                        initValue  : '156',
                        url : "${ctx}/rpt/input/task/getTaskList.json",
						onBeforeOpen : function(){
							if(operType == 'del'){return false;}
						}
                    }
            });
            form = $("#mainform").ligerForm(
                    {
                        inputWidth : 120,
                        space : 20,
                        fields : fields
                    });

            jQuery.metadata.setType("attr", "validate");
            BIONE.validate($("#mainform"));
            var taskDefId = defaultTaskDefId;
            form.setData({
                taskDefId:taskDefId,
                taskTitle: taskTitle
            });
			if(operType == 'del'){
	            $("#mainform input[name=taskTitle]").attr("readonly", "true");
	            $("#mainform input[name=loadDataMark]").attr("readonly", "true");
	            $("#mainform input[name=taskDefId]").attr("readonly", "true");
	            if(triggerType=="1"){
	            	form.getEditor('dataDate').setDisabled(true);
	            }
			}

            
            $("#nodeContent").html('<iframe frameborder="0" id="taskInfoFrame1" name="taskInfoFrame1" style="height:99%;width:100%;"></iframe>');
            $.ligerui.get("taskDefIdBox").set("onSelected", onSelected);
        }
        
        function onSelected(val,text){
            $("#taskInfoFrame1").attr("src","${ctx}/rpt/input/task/toUnifyNode?taskDefId="+val+"&orgType=" + orgType + "&d="+new Date().getTime());
        }
        
        function checkIsSelected(taskUnifyNodeList,nodeId){
            for(var i = 0 ;i <taskUnifyNodeList.length;i++){
                if(nodeId==taskUnifyNodeList[i].taskNodeDefId&&taskUnifyNodeList[i].taskObjIdMap!=null && taskUnifyNodeList[i].taskObjIdMap.length!=0)
                    return true;
            }
            return false;
        }

        function checkTaskUnifyNode(taskUnifyNodeList ){
            if(!taskUnifyNodeList||taskUnifyNodeList.length==0)
            {
                BIONE.tip('请选择任务节点');
                return false;
            }
            var nodeInfo = nodeObj.getTabNodeInfo();
            var errorNodeNm=null;
            if(!taskUnifyNodeList||taskUnifyNodeList.length==0)
            {
                BIONE.tip('请选择处理人');
                return false;
            }
            for(var i = 0 ;i <nodeInfo.length;i++){
                var nodeId = nodeInfo[i].nodeId;
                var nodeNm = nodeInfo[i].nodeNm;
                var isSelected = checkIsSelected(taskUnifyNodeList,nodeId);
                if(!isSelected){
                        BIONE.tip("节点["+nodeNm+"]没有选择处理人,请选择");
                        return false;
                }
            }
            if(taskUnifyNodeList.length==1)
                return true;
            var ids={};
            var taskObjIdMap = taskUnifyNodeList[0].taskObjIdMap;
            for(var j=0;j<taskObjIdMap.length;j++){
                ids[taskObjIdMap[j].taskObjId]=taskObjIdMap[j].taskObjNm;
            }
            var targetIdMap = taskUnifyNodeList[1].taskObjIdMap;
            for(var j=0;j<targetIdMap.length;j++){
                if(ids[targetIdMap[j].taskObjId]&&ids[targetIdMap[j].taskObjId]!=null){
                    return true;
                }
            }
            return true;
        }
        function gatherData(){
            var  deployTask = {
                rptTskInfo : {
                    nodeType : "01",
                    taskId : taskId,
                    taskDefId : $("#taskDefId").val()
                },
                taskNodeOrgList : [],
                tskExeobjRelVO : null,
                loadDataMark : $("#loadDataMark").ligerCheckBox().getValue(),
                orgs : [],
                taskTitle : $("#taskTitle").val(),
                taskUnifyNodeList : []
            };
            var nodeInfo =  nodeObj.getNodeInfo();
            if(!nodeInfo||nodeInfo == "-1")
            {
                return ;
            }
            if(!nodeInfo.taskUnifyNodeList){
                return;
            }
            if(!checkTaskUnifyNode(nodeInfo.taskUnifyNodeList))
            {
                return;
            }
            deployTask.taskUnifyNodeList = nodeInfo.taskUnifyNodeList;
            deployTask.orgs = nodeInfo.orgs;
            deployTask.dataDate = dataDate;
            return deployTask;
        }
        
</script>

<title>指标目录管理</title>
</head>
<body>
</body>
</html>