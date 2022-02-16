<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template3.jsp">
<head>
<style>
#right {
    overflow: hidden;
}
</style>
<script type="text/javascript">
var leftTreeObj,
form,
formObjTree,
formFlowTab,
tabItems=[],

tasks,
orgs = [],
seluporg = [],
taskObjs = {}
taskObjType = {}
;
var levelChecked = [];
var triggerType = '${triggerType}';
var taskId = '${taskId}';
var tabItemsTree={};
var selectTabReloaded = {};
var cascade = false;
$(function(){
    initLeftTree();
    initBtn();
    
    initForm();
    initFormOrgTree();
    initData();
    initHeight();
    initTab();
//     initTreeBars();

});

function initData (){

	try{
	    var val = JSON.parse(parent.getDeployTask());
	    form.setData({
	        taskDefId : val.rptTskInfo.taskDefId
	    });
	    orgs = val.orgs;
	    seluporg = val.upOrg;
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
	        if(!taskObjs[taskNode.taskNodeDefId]){
	            taskObjs[taskNode.taskNodeDefId] = taskObjsArr;
	            taskObjType[taskNode.taskNodeDefId] = taskNode.taskObjType;
	        }else{
	            taskObjs[taskNode.taskNodeDefId] = taskObjs[taskNode.taskNodeDefId].concat(taskObjsArr);
	        }
        }
	    
	}catch (e){
		
	}
}

function initHeight(){
    var thight = $("#mainformdiv").height();
    var mainformHight = $("#mainform").height();
    var panenHeight = thight - mainformHight;

    var leftTreePanel = $("#leftTreePanel").height();
    var orgtreeToolbar = $("#orgtreeToolbar").height();
    var orgtreeSearchbar = $("#orgtreeSearchbar").height();
	
    $("#leftTreePanel").height(panenHeight * 0.94);
    $("#formObjTreeDiv").height((panenHeight - leftTreePanel - orgtreeToolbar) * 0.95 );
    $("#formFlowTab").height(panenHeight * 0.96);
};

function initLeftTree() {
    var setting ={
        async:{
            enable:true,
            url:"${ctx}/rpt/input/task/getTaskTree.json",
            autoParam:["nodeType", "id=nodeId"],
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
                if (treeNode.level == "0") {
                    //若是根节点，展开下一级节点
                    leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
                }
                if(treeNode.params.nodeType != "taskinfo"){
                    treeNode.chkDisabled = true;
                }

                if (treeNode.id == taskId){
                    leftTreeObj.removeNode(treeNode);
                }

                if(treeNode.id == tasks){
                    leftTreeObj.checkNode(treeNode,true,false);
                }
                
            }
        },
        check : {
            enable: true,
            chkStyle: "radio",
            radioType: "all"
        }
    };
    leftTreeObj = $.fn.zTree.init($("#tree"), setting);
};

function zTreeOnClick(event,treeId,treeNode){
    if(treeNode.params.nodeType == "taskinfo"){
        treeNode.checked = true;
// 	    $("#mainformdiv").show()
    }else{
// 	    $("#mainformdiv").hide()
    }
};

function initForm(){
    var fields= [];
    fields.push({
        name : "nodeType",
        type : "hidden"
    });
//     if(triggerType=="1"){
//         fields.push({
//             display : "数据日期",
//             name : "dataDate",
//             newline : false,
//             type : 'date',
//             format : 'yyyyMMdd',
//             width : 120,
//             validate : {
//                 required : true
//             }
//         });
//     }
    fields.push({
        display : "流程选择",
        name : "taskDefId",
        newline : true,
        type : "select",
        comboboxName : 'taskDefIdBox',
        width : 490,
        validate : {
            required : true,
            messages : {
                required : "请选择流程"
            }
        },
        options : {
            url : "${ctx}/rpt/input/task/getTaskList.json"
        }
    });
    form = $("#mainform").ligerForm({
        inputWidth : 600,
        labelWidth : 90,
        space : 40,
        fields : fields
    });
    jQuery.metadata.setType("attr", "validate");
//     BIONE.validate($("#mainform"));
//     var taskDefId = defaultTaskDefId;
//     form.setData({
//         taskDefId:taskDefId
//     });
//     $("#mainform input[name=taskTitle]").attr("readonly", "true");
//     $("#nodeContent").html('<iframe frameborder="0" id="taskInfoFrame1" name="taskInfoFrame1" style="height:85%;width:100%;"></iframe>');
    $.ligerui.get("taskDefIdBox").set("onSelected", taskDefIdBoxOnSelected);
};
function taskDefIdBoxOnSelected(val,text){
    initTabItem(val);
}

function initFormOrgTree() {
    var setting = {
        async : {
            enable : true,
            url : "${ctx}/rpt/input/task/getPubOrgTree?t=" + new Date().getTime(),
            autoParam : [ "id" ],
            dataType : "json",
            type : "post",
            dataFilter : function(treeId, parentNode, childNodes) {
                if (childNodes) {
                    for ( var i = 0; i < childNodes.length; i++) {
                        childNodes[i].isParent = true;
                    }
                }
                return childNodes;
            }
        },
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
            selectedMulti : true
        },
        check : {
            chkboxType : {"Y":"", "N":""},
            chkStyle : 'checkbox',
            enable : true
        },
        callback : {
            onCheck : function(event, treeId, treeNode){
            	levelChecked = [];
            	if(cascade){
                   	levelChecked.push(treeNode.id);
                    if(treeNode.getCheckStatus().checked==true){
                    	formObjTree.reAsyncChildNodes(treeNode, "refresh");
                    }
                    formObjTree.expandNode(treeNode, true, true, true);
                }
                loadAuthTree();
            },
            onNodeCreated : function(event,treeId,treeNode){
            	if (treeNode.level == "0") {
                    //若是根节点，展开下一级节点
                    formObjTree.reAsyncChildNodes(treeNode, "refresh");
                }
                
	            if(orgs.indexOf(treeNode.id) != -1){
	                formObjTree.checkNode(treeNode,true,false);
	                orgs.splice(orgs.indexOf(treeNode.id),1)
	            }
	
                var sel = seluporg;
                if(sel && sel.indexOf(treeNode.id) != -1){
                	formObjTree.expandNode(treeNode,true,false);
                	sel.splice(sel.indexOf(treeNode.id),1)
	            }
                if(cascade){
		            if(levelChecked.indexOf(treeNode.upId) != -1){
		            	levelChecked.push(treeNode.id);
		                formObjTree.checkNode(treeNode,true,false);
		                formObjTree.reAsyncChildNodes(treeNode, "refresh");
		            }
                }
	            
            }
        }
    };
    formObjTree = $.fn.zTree.init($("#formObjTree"), setting);
};

function getSelectFormObjTreeNode(){
    var nodes = formObjTree.getCheckedNodes(true)
    var nodeIds = "";
    for(var i = 0; i < nodes.length; i++){
        nodeIds += "," + nodes[i].id;
    }
    return nodeIds.substring(1,nodeIds.length);
}

function getSelectFormObjTreeNodeArr(){
    return formObjTree.getCheckedNodes(true);
}

function getTaskTreeObjNode(){
    var nodes = leftTreeObj.getCheckedNodes(true)
    var nodeIds = "";
    for(var i = 0; i < nodes.length; i++){
        nodeIds += "," + nodes[i].id;
    }
    return nodeIds.substring(1,nodeIds.length);
}

function getTaskTreeObjNodeNm(){
    var nodes = leftTreeObj.getCheckedNodes(true)
    var nodeNm = "";
    for(var i = 0; i < nodes.length; i++){
        nodeNm += "," + nodes[i].text;
    }
    return nodeNm.substring(1,nodeNm.length);
}

function loadAuthTree(treeId){
	if(treeId){
		return function(){
		    tabItemsTree[treeId].reAsyncChildNodes(tabItemsTree[treeId].getNodeByParam("id", 0, null), "refresh");
		}
	}else{
		for(var i in tabItemsTree){
		    tabItemsTree[i].reAsyncChildNodes(tabItemsTree[i].getNodeByParam("id", 0, null), "refresh");
		}
	}
	
}
function reloadAuthTree(treeId){
    tabItemsTree[treeId].reAsyncChildNodes(tabItemsTree[treeId].getNodeByParam("id", 0, null), "refresh");
}


function initTab(){
    formFlowTab = $("#formFlowTab").ligerTab({
        contextmenu : true,
        changeHeightOnResize : true,
        onAfterSelectTabItem : function(tabId){
            if(!selectTabReloaded[tabId]){
                selectTabReloaded[tabId] = true;
	            formFlowTab.reload(tabId);
            }
        }
    });
}

function initTabItem(taskDefId) {
    selectTabReloaded = {};
    if(taskDefId==null) 
        return ;
    $.ajax({
        cache : false,
        async : true,
        url : "${ctx}/rpt/input/task/getTaskNodeList.json?d="+new Date(),
//         url : "${ctx}/rpt/input/task/node?d="+new Date(),
        dataType : 'json',
        type : "post",
        data : {
            taskDefId : taskDefId
        },
        success : function(result) {
            for(var i = 0; i < tabItems.length; i++){
	            formFlowTab.removeTabItem(tabItems[i]);            
            }
            tabItems = [];
            
            var tbsHeight = $("#formFlowTab").height() - $(".l-tab-links").height();
            var tabs = result.Rows;
            
            for(var i = 0 ;i < tabs.length; i++){
                var nodeInfo = {
                        nodeId : tabs[i].taskNodeDefId,
                        nodeType : tabs[i].nodeType,
                        nodeNm : tabs[i].taskNodeNm,
                        taskOrderno : tabs[i].taskOrderno
                    };
                var tabId = "tab"+i+"_"+tabs[i].nodeType;
	            formFlowTab.addTabItem({
	                url : "${ctx}/rpt/input/task/afterTaskOptionTabItems?tabId="+tabId+"&nodeType="+tabs[i].nodeType
	                    +"&taskNodeDefId="+nodeInfo.nodeId+"&taskOrderno="+nodeInfo.taskOrderno
	                        +"&taskNodeNm="+encodeURI(encodeURI(nodeInfo.nodeNm)),
	                showClose : false,
	                height : tbsHeight,
	                tabid : tabId,
	                text: tabs[i].taskNodeNm
	            });
	            selectTabReloaded[tabId] = false;
	            tabItems.push(tabId);
            }
            formFlowTab.selectTabItem(tabItems[0]);
        },
        error : function(result, b) {
            BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
        }
    });
}















function initBtn(){
    var buttons = [];
    buttons.push({
        text : '取消',
        onclick : f_close
    });
    buttons.push({
        text : '确定',
        onclick : f_deploy
    });
    BIONE.addFormButtons(buttons);
};
function f_close(){
//     afterTaskBox
    BIONE.closeDialog("afterTaskBox");
}
function f_deploy(){
    var formData = form.getData();
    var  deployTask = {
        rptTskInfo : {
            nodeType : "01",
            taskId : getTaskTreeObjNode(),
            taskDefId : formData.taskDefId,
            triggerType : "3"
        },
        taskNodeOrgList : [],
        tskExeobjRelVO : null,
        orgs : [],
        taskTitle : "",
        taskUnifyNodeList : []
    };
    deployTask.orgs = getSelectFormObjTreeNode() ==  "" ? "" : getSelectFormObjTreeNode().split(",");
    for(var i in tabItemsTree){
        deployTask.taskUnifyNodeList = deployTask.taskUnifyNodeList.concat(tabItemsTree[i].getAuthData())
    }

    if(!validData(deployTask)){
        return;
     }
    parent.setDeployTask(getTaskTreeObjNodeNm(),JSON2.stringify(deployTask));	
    BIONE.closeDialog("afterTaskBox");
}

function validData(deployTask){
    if(deployTask.rptTskInfo.taskId.length == 0){
        BIONE.tip("请选择补录任务.");
        return false;
    }
//     if(!deployTask.taskTitle){
//         BIONE.tip("请填写标题.");
//         return false;
//     }
    if(!deployTask.rptTskInfo.taskDefId){
        BIONE.tip("请选择流程.");
        return false;
    }
    if(deployTask.orgs.length == 0){
        BIONE.tip("请选择机构.");
        return false;
    }
    var role = [];
    var taskUnifyNodeList = deployTask.taskUnifyNodeList;
    for(var i = 0; i < taskUnifyNodeList.length; i++){
        var tunl = taskUnifyNodeList[i];
        
        if(tunl.taskObjIdMap.length == 0){
	        BIONE.tip("流程节点[" + tunl.taskNodeNm + "]未选择执行对象.");
            return false;
        }else{
        	if(tunl.taskObjType == "AUTH_OBJ_ROLE"){
        		role
				for(var roleIdx = 0; i < tunl.taskObjIdMap.length; i++){
					var roleID = tunl.taskObjIdMap[roleIdx].taskObjId;
					if(role.indexOf(roleID) != -1){
// 				        BIONE.tip("角色[" + tunl.taskObjIdMap[roleIdx].taskObjNm + "]在流程节点[" + tunl.taskNodeNm + "]已勾选.");
// 			            return false;
					}
					role.push(roleID);
				}
           	}
        }
    	
    }
    
    return true;
}

function initTreeBars(){
    $("#orgtreeToolbar").ligerTreeToolBar({
        items : [ {
            icon : 'refresh',
            text : '刷新',
            click : function() {
            }
        }, {
            line : true
        }, {
            icon : 'plus',
            text : '展开',
            click : function() {
            }
        }, {
            line : true
        }, {
            icon : 'lock',
            text : '折叠',
            click : function() {
            }
        } ],
        treemenu : false
    });
}

function check() {
    cascade = $("#level1")[0].checked;
    if ($("#level1")[0].checked == true)
    	formObjTree.setting.check.chkboxType = {
            "Y" : "s",
            "N" : "ps"
        };
    else
    	formObjTree.setting.check.chkboxType = {
            "Y" : "",
            "N" : ""
        };
}

</script>
</head>
<body>
	<div id="template.left.up">任务目录</div>
	<div id="template.right">
		<div id="mainform" style="overflow: hidden;"></div>
		<div>
		    <div id="leftTreePanel" style="width:45%;float:left;margin-right:1em;border-right:inset;"> 
				<div id="orgtreeToolbar" style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
		        <div id="orgtreeSearchbar" style="width:100%;margin-top:2px;padding-left:2px;display:none;">
		            <ul>
		                <li style="width:98%;text-align:left;">                      
		                    <div class="l-text-wrapper" style="width: 100%;">                         
		                        <div class="l-text l-text-date" style="width: 100%;">                    
		                            <input id="orgtreeSearchInput" type="text" class="l-text-field" style="width: 99%;" />    
		                            <div class="l-trigger">                                                                      
		                                <i id="orgtreeSearchIcon" class = "icon-search search-size" ></i>                                                 
		                            </div>
		                        </div>                                                                                                   
		                    </div>
		                </li>
		            </ul> 
		        </div> 
		        
		        
		        
		        <div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%"
						style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
						<img  src="${ctx}/images/classics/icons/application_side_tree.png" />
					</div>
					
					<div width="30%">
		        		<span style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
		        			<span style="font-size: 12" id="left_up_span">&nbsp;机构选择</span>
						</span>
					</div>
					<div width="60%"> 
						<div style="float: right; position: relative; padding-right: 3px; padding-top:4px;">
				        	<div id="level" style="line-height: 24px; height: 24px; margin-left: 5px;">
				            	<span style="display: block; float: left;">是否级联： 是 </span>
				            	<input type="radio" id="level1" name="level" value="level1" style="display: block; float: left; margin: 5px;" onclick="check()"/>
				            	<span style="display: block; float: left;">否    </span>
				            	<input type="radio" id="level2" name="level" value="level2" style="display: block; float: left; margin: 5px;"   onclick="check()"  checked="true"/>
				        	</div>
						</div>
					</div>
				</div>
		        
				<div style="clear:both;"></div>
		        
		        <div id='formObjTreeDiv' style="width:100%;float:left;overflow: auto;" >
					<ul id="formObjTree" class="ztree"  ></u>
		        </div>
				
			</div>
			<div id="formFlowTab" style="width:45%;float:left;"></div>
			<div style="clear:both;"></div>
		</div>
	</div>
</body>
</html>