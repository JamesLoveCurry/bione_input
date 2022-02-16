<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<style type="text/css">
</style>
<script type="text/javascript">
var authCombobox,authTree;
var taskObjType = "${taskObjType}";
var taskNodeDefId = "${taskNodeDefId}";
var taskOrderno = "${taskOrderno}";
var taskNodeNm = decodeURI("${taskNodeNm}");
var nodeType = "${nodeType}";
var taskObjs = parent.taskObjs[taskNodeDefId];
var taskObjType = parent.taskObjType[taskNodeDefId];
var orgss = [];
$(function(){
	try{
		var val = JSON.parse(parent.parent.getDeployTask());
	    orgss = val.orgs;
	}catch(e){
	}
    initComboBox();
//     initTreeToolBar();
    
    initStyle();

});

function initStyle(){
    var center = $("#center").height();
    var treeToolbar = $("#treeToolbar").height();
    $("#tree").height((center - treeToolbar ) * 0.9 );
    $("#center").attr("style","overflow : auto");;
//     style="overflow: auto;
    
    
};

function initComboBox(){
    authCombobox = $("#template_left_combobox").ligerComboBox({
        url : "${ctx}/rpt/input/task/getAuthObjCombo.json?d=" + new Date().getTime(),
        ajaxType : "post",
        labelAligh : "center",
        slide : false,
        width : 150,
        onSuccess : function(data) {
            if (data && data.length > 0) {
//                 初始化后设置combobox默认值
//                 if(!taskObjType){
//                     taskObjType = authCombobox.getValue();
//                 }
                var type = taskObjType ||  data[0].id;
                authCombobox.selectValue(type);
                initAuthTree();
                //加载相应授权对象树
//                 if (leftTree && leftTree.setting) {
//                     getTypeTree(type);
//                 }
            }
         }
    });

}

function initTreeToolBar() {
    $("#treeToolbar").ligerTreeToolBar({
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
//                 openAllNodes(leftTree);
            }
        }, {
            line : true
        }, {
            icon : 'lock',
            text : '折叠',
            click : function() {
//                 closeAllNodes(leftTree);
            }
        } ],
        treemenu : false
    });
}

function initAuthTree(){
    authTree = $.fn.zTree.init($("#tree"),{
        data : {
            keep : {
                parent : true
            },
            key : {
                name : "text"
            },
            simpleData : {
                enable : true,
                idKey : "id",
                pIdKey : "upId",
                rootPId : null
            }
        },
        check : {
            chkStyle : 'checkbox',
            enable : true,
            chkboxType: { "Y": "sp", "N": "sp" }
        },
        view : {
            selectedMulti : false,
            showLine : false
        },
        async : {
            enable : true,
            url : "${ctx}/rpt/input/task/getTypeTree.json?d="+new Date().getTime(),
            autoParam : [ 'id'],
            otherParam : {
                objDefNo : function(){
                    return authCombobox.getValue();
                },
                handleType : function(){
                    return "00";
                },
                orgNo : function(){
                    return parent.getSelectFormObjTreeNode();
                },
//                 searchNm : function(){
//                    return $("#treeSearchInput").val();
//                 },
                nodeType : function(){
                    return nodeType;
                },
            },
            dataFilter : function() {
                var cNodes = arguments[2];
                $.each(cNodes,function(i, n) {
                    n.objDefNo = $('input[name=template_left_combobox_val]').val();
                });
                return cNodes;
            }
        },
        callback : {
            onClick : function(event, treeId, treeNode){
//                 if(treeNode.data.orgId !=null || typeof treeNode.data.orgId !="undefined") 
//                     return ;
            },
            onNodeCreated : function(event, treeId, treeNode) {
                if(treeNode.id == 0){
                    $.fn.zTree.getZTreeObj(treeId).expandNode(treeNode,true,false);
                }
                var seltaskObj = []; 
                seltaskObj = seltaskObj.concat(taskObjs)
                if(seltaskObj && seltaskObj.indexOf(treeNode.id) != -1){
                    $.fn.zTree.getZTreeObj(treeId).checkNode(treeNode,true,false);
                    seltaskObj.splice(seltaskObj.indexOf(treeNode.id),1);
                }
                
                if(orgss && orgss.indexOf(treeNode.id) != -1){
                    $.fn.zTree.getZTreeObj(treeId).expandNode(treeNode,true,false);
                    orgss.splice(orgss.indexOf(treeNode.id),1);

                }

                
            },
            onCheck:function(event, treeId, treeNode){
//                 initTitle();
            }
        }
    }, [ {
        id : "0",
        text : "执行对象树",
        isParent : true
    } ]);
    authTree.getAuthData = getAuthData;
    parent.tabItemsTree[taskNodeDefId] = authTree;
    $.ligerui.get("template_left_combobox").set("onSelected", parent.loadAuthTree(taskNodeDefId));
}

function validTree(){
    
    
}

function getAuthData(){
    var array = [];
    var orgNode = authTree.getNodeByParam('id',0,null).children;
    var TabAuthList = function(){return{
        nodeType : nodeType,
        taskNodeDefId : taskNodeDefId,
        taskNodeNm : taskNodeNm,
        taskObjIdMap : [],
        taskObjType : $('input[name=template_left_combobox_val]').val(),
        taskOrderno : taskOrderno
    }};
    var nodes = authTree.getCheckedNodes(true);

    if($('input[name=template_left_combobox_val]').val() == "AUTH_OBJ_ROLE"){
		
		var roleOrg = parent.getSelectFormObjTreeNodeArr();
// 		for(var roi = 0; roi < roleOrg.length; roi++){
// 			var roleOrgNode = roleOrg[roi];
			var authList = TabAuthList();
			authList.orgNo = "," + parent.getSelectFormObjTreeNode();
// 		    authList.orgNm = roleOrgNode.text;

		    for(var i = nodes.length - 1; i >= 0; i--){
    	        var n = nodes[i];
    	        if(!n.isParent){
    	            authList.taskObjIdMap.push({
                        taskObjId : n.id,
                        taskObjNm : n.text,
                        taskObjType : n.objDefNo
                    });
    	            nodes.splice(i,1);
    	        }
    	    }
    	    array.push(authList);

// 		}
		
    }else{
    	for(var oi = 0; oi < orgNode.length; oi++){
    	    var authList = TabAuthList();
    	    
   	    	var orgNo = orgNode[oi].id;
   		    authList.orgNo = orgNo;
   		    authList.orgNm = orgNode[oi].text;
   		    
    	    for(var i = nodes.length - 1; i >= 0; i--){
    	        var n = nodes[i];
    	        if(orgNo == n.upId && !n.isParent){
    	            authList.taskObjIdMap.push({
                        taskObjId : n.id,
                        taskObjNm : n.text,
                        taskObjType : n.objDefNo
                    });
    	            nodes.splice(i,1);
    	        }
    	    }
    	    array.push(authList);
        }
    }
    
    
    return array;
}

// function initTitle(){
//     if(window.parent.nodeType=="02")
//         return ;
//     var checkedNodes = leftTree.getCheckedNodes();
//     var allShowNodes=[];
//     var showText = "";
//     for(var i =0;i<checkedNodes.length;i++){
//         if(!checkedNodes[i].isParent&&!checkContains(allShowNodes,checkedNodes[i].upId))
//             allShowNodes.push(checkedNodes[i].upId);
//     }
//     var isFirst = true;
//     for(var i = 0 ;i <allShowNodes.length;i++){
//         if(isFirst)
//             isFirst=false;
//         else
//             showText +=",";
//         showText += allShowNodes[i];
//     }
//     window.parent.parent.parent.setTaskTitle(showText);
// }

// function checkContains(allIds,id){
//     for(var i = 0 ;i<allIds.length;i++ )
//         if(allIds[i]==id)
//             return true;
//     return false;
// }


</script>
</head>
<body>
    <div id="template.center">
        <div id="title" width="100%" 
        style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
            <div width="8%"
                style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px; margin-right:3px;">
                    <img src="${ctx}/images/classics/icons/application_side_tree.png" />
            </div>
            <div width="30%">
                    <span
                        style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px; margin-right:20px;">
                        <span>执行对象类型</span>
                    </span>
                </div>
                <div width="60%"> 
                    <div
                        style="float: left; position: relative; padding-right: 3px; padding-top:4px;">
                        <input type="text" id="template_left_combobox"></input>
                    </div>
                </div>
        </div>
        <div id="treeToolbar" style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
        <div id="tree" class="ztree" ></div>
        
    </div>
</body>
</html>