<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template24a.jsp">
<head>
<script type="text/javascript">
    var leftTreeObj;
    var tabObj;
    
    var idxInfos =[];
    var currentNodeId = null;
    
    var dimFilterManager;
    var dimFilterInfo;
    
    var treeTIdIndex =0;
    var taskDefId = '${taskDefId}';
    var orgType = '${orgType}';
    var cascade = false;
    var checkedOrgs;
    var openOrgs;
    var init= true;
	var operType = window.parent.operType;
	var taskUnifyNodeList;
	var insNodes;
    if(operType != 'del'){
	    taskUnifyNodeList = window.parent.parent.deployTask.taskUnifyNodeList;
    }else{
    	insNodes = window.parent.insNodes;
    }
    

    $(function() {
        initBaseInfo();
        initTab();
        initTree();
        init = false;
    });
    
    function initBaseInfo(){
        window.parent.nodeObj = window;
        checkedOrgs = window.parent.orgs;
        openOrgs = window.parent.openOrgs;
        if(window.parent.isNeedCheck&&checkedOrgs.length==0){
            BIONE.tip("请设置需要下发的机构");
            window.parent.enableBtn();
        }
    }

    //渲染树
    function initTree() {
        var setting = {
                <%--async : {--%>
                <%--    enable : true,--%>
                <%--    url : "${ctx}/rpt/input/task/getPubOrgTree?orgType=" +orgType+ "&t="--%>
                <%--            + new Date().getTime(),--%>
                <%--    autoParam : [ "id" ],--%>
                <%--    dataType : "json",--%>
                <%--    type : "post",--%>
                <%--    dataFilter : function(treeId, parentNode, childNodes) {--%>
                <%--        if (childNodes) {--%>
                <%--            for ( var i = 0; i < childNodes.length; i++) {--%>
                <%--                childNodes[i].isParent = true;--%>
                <%--            }--%>
                <%--        }--%>
                <%--        return childNodes;--%>
                <%--    }--%>
                <%--},--%>
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
//                     onNodeCreated : function(event, treeId, treeNode) {
//                         if (treeNode.upId == null
//                                 /*||(treeNode.getParentNode()!=null&&treeNode.getParentNode().upId==null)
//                                 ||(treeNode.getParentNode().getParentNode()!=null&&treeNode.getParentNode().getParentNode().upId==null)*/
//                             ){
//                             //若是根节点，展开下一级节点
//                             leftTreeObj.expandNode(treeNode , true , false);
//                             if(isCheckNode(treeNode.params.realId)){
//                                 leftTreeObj.checkNode(treeNode, true, false);
//                             }
//                             //leftTreeObj.expand(treeNode, true, true, true);
//                         }else{
//                             //if(init&&isCheckNode(treeNode.params.realId))
//                             if(isCheckNode(treeNode.params.realId)){
//                                 leftTreeObj.checkNode(treeNode, true, false);
//                             }
//                             if(isOpenNode(treeNode.params.realId))
//                                 leftTreeObj.expandNode(treeNode , true , false);
//                         }
//
//                         var upOrg = parent.upOrg.concat();
//                         if(upOrg.indexOf(treeNode.id) != -1){
//                         	leftTreeObj.expandNode(treeNode,true,false);
//                         	upOrg.splice(upOrg.indexOf(treeNode.id),1)
//         	            }
//
//                         if(operType != 'del'){
// 	                        var orgs = parent.selorgs;
// 	                        if(orgs.indexOf(treeNode.id) != -1){
// 	                        	leftTreeObj.checkNode(treeNode,true,false);
// 	        	                orgs.splice(orgs.indexOf(treeNode.id),1)
// 	        	            }
//                         }else{
//                         	var insUpOrg = parent.upOrg.concat();
//                         	var insOrg = insNodes.insOrg;
//                         	if(insOrg.indexOf(treeNode.id) != -1){
// 	                        	leftTreeObj.checkNode(treeNode,true,false);
// 	                        	insOrg.splice(insOrg.indexOf(treeNode.id),1)
// 	        	            }else{
// 		        	            if(treeNode.upId != null && insUpOrg.indexOf(treeNode.id) == -1){
// // 		        	            	leftTreeObj.removeNode(treeNode);
// 	        	            	}
//         	            	}
//                         }
//                     },
                    beforeCheck : function(){
						if(operType == 'del'){
                            return false;
                        }
                    },
                    onCheck:function(event, treeId, treeNode){
						if(operType == 'del'){
							return false;
						}
                        var childOrgs = [];
                        if(cascade){
                            if(treeNode.getCheckStatus().checked==true)
                                leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
                            //rightTreeObj.expandNode(treeNode, true, true, true);
                            childOrgs.push(treeNode.id);
                            for ( var i in treeNode.children) {
                                childOrgs.push(treeNode.children[i].id);
                            }
                            if(!treeNode.checked)
                                for(var j = 0 ;j <tabObj.getTabItemCount();j++){
                                    window.frames[j].onCancelOrg(childOrgs);
                                }
                        }else{
                            childOrgs.push(treeNode.id);
                            for(var j = 0 ;j <tabObj.getTabItemCount();j++){
                                if(treeNode.checked)
                                    window.frames[j].onCheckOrg(childOrgs);
                                else
                                    window.frames[j].onCancelOrg(childOrgs);
                            }
                        }
                    },
                    onAsyncSuccess : function(event, treeId, treeNode, msg){
                        if(treeNode!=null){
                            if(cascade){
                                var childOrgs = [];
                                childOrgs.push(treeNode.id);
                                for ( var i in treeNode.children) {
                                    leftTreeObj.checkNode(treeNode.children[i], true, false);
                                    childOrgs.push(treeNode.children[i].id);
                                }
                                if(childOrgs.length!=0)
                                    for(var j = 0 ;j <tabObj.getTabItemCount();j++){
                                    	window.frames[j].onCheckOrg(childOrgs);
                                    }
                            }
                        }
                    }
                }
            };

        $.ajax({
            async : true,
            url : "${ctx}/rpt/input/task/getSyncPubOrgTree",
            dataType : 'json',
            type : "post",
            beforeSend : function() {
                BIONE.loading = true;
                BIONE.showLoading("正在加载数据中...");
            },
            complete : function() {
                BIONE.loading = false;
                BIONE.hideLoading();
            },
            success : function(result) {
                leftTreeObj = $.fn.zTree.init($("#leftTree"),setting, result);
                $("#template.left.center").hide();
            },
            error : function(result, b) {
                ////BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
            }
        });
    }
    
    function isOpenNode(org){
        if(openOrgs){
            for(var i = 0 ;i <openOrgs.length;i++){
                if(org == openOrgs[i])
                    return true;
            }
        }
        return false;
    }
    
    function isCheckNode (org){
        if(checkedOrgs){
            for(var i = 0 ;i <checkedOrgs.length;i++){
                if(org == checkedOrgs[i])
                    return true;
            }
        }
        return false;
    }

    function check() {
        cascade = $("#level1")[0].checked;
        if ($("#level1")[0].checked == true)
            leftTreeObj.setting.check.chkboxType = {
                "Y" : "s",
                "N" : "ps"
            };
        else
            leftTreeObj.setting.check.chkboxType = {
                "Y" : "",
                "N" : ""
            };
    }
    function addOrg(){

            var options = {
                url : "${ctx}/rpt/input/idxdatainput/selectIdx",
                dialogname : 'selectIdx',
                title : '选择指标',
                comboxName : 'selectIdxBox'
            };
            BIONE.commonOpenIconDialog(options.title, options.dialogname,
                    options.url, options.comboxName);
            return false;
    }
    
    function deleteOrg(){
        var selectedNodes = leftTreeObj.getSelectedNodes();
        if(selectedNodes==null ||typeof selectedNodes=="undefined"||selectedNodes.length!=1)
        {
            BIONE.tip("请选择指标进行删除");
            return ;
        }

        var selectedNode = selectedNodes[0];
        var preNode = selectedNode.getPreNode();
        if(preNode!=null&&typeof preNode!="undefined" ){

            initCurrentTab(preNode);
            leftTreeObj.selectNode(preNode);
        }else{
            var nextNode = selectedNode.getNextNode();
            if(nextNode!=null&&typeof nextNode!="undefined" ){
                initCurrentTab(nextNode);
                leftTreeObj.selectNode(nextNode);
            }else
                initCurrentTab(null);
        }
        leftTreeObj.removeNode(selectedNode);
    }
    var tabContents = [];
    //渲染选项卡
    function initTab() {
        if(taskDefId==null) 
            return ;
        $.ajax({
            cache : false,
            async : true,
            url : "${ctx}/rpt/input/task/getTaskNodeList.json?d="+new Date().getTime(),
            dataType : 'json',
            type : "post",
            data : {
                taskDefId : taskDefId
            },
            success : function(result) {
                var tabHeight = $("#right").height() - 27;
                var tabWidth = $(document).width();
                tabObj = $("#tab").ligerTab({
                    contextmenu : true,
                    height:tabHeight,
                    width :tabWidth
                });

                var tabs = result.Rows;
                if(tabs.length==0){
                    window.parent.enableBtn();
                }
                var tab0="";
                for(var i = 0 ;i <tabs.length;i++){
                    var nodeInfo = {
                        nodeId : tabs[i].taskNodeDefId,
                        nodeType : tabs[i].nodeType,
                        nodeNm : tabs[i].taskNodeNm,
                        taskOrderno : tabs[i].taskOrderno
                    };
                    var curTabUri ="${ctx}/rpt/input/task/toUnifyNodeFrame?nodeType="+tabs[i].nodeType+"&taskNodeDefId="+nodeInfo.nodeId+"&taskOrderno="+nodeInfo.taskOrderno+"&taskNodeNm="+encodeURI(encodeURI( nodeInfo.nodeNm));
                    var frameWidth = tabWidth - 360;
                    var tabNm = "tab"+i+"_"+tabs[i].nodeType;
                    tabObj
                    .addTabItem({
                        tabid : (tabNm),
                        text: nodeInfo.nodeNm,
                        height : tabHeight,
                        showClose : false,
                        content : '<iframe frameborder="0" id="frame'+i+'" name="idxInputTabFrame'+i+'" style="height:'+tabHeight+'px;width:'+frameWidth+';" src='+curTabUri+' ></iframe>'
                    });
                    tabContents.push({nodeId:nodeInfo.nodeId,nodeNm:nodeInfo.nodeNm});
                    frameInfo.push({id:"idxInputTabFrame"+i,value:false});
                    if(i==0)
                    tab0 = tabNm;
                }
                tabObj.selectTabItem(tab0);
                
            },
            error : function(result, b) {
                BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
            }
        });
    }
    function getTabNodeInfo(){
        return tabContents;
    }
    var frameInfo=[];
    
    function checkIsAllComplete(){
        
        for(var i = 0 ;i<frameInfo.length;i++){
            if(frameInfo[i].value == false)
                return false;
        }
        return true;
    }
    
    function doAfterFrameDone(frameId){
        for(var i = 0 ;i<frameInfo.length;i++){
            if(frameInfo[i].id == frameId)
            {
                frameInfo[i].value = true;
                break;
            }
        }
        
        if(checkIsAllComplete())
        {
            window.parent.enableBtn1();
            if(window.parent.isNeedCheck)
                window.parent.gatherData();
        }   
    }

    function getNodeInfo(){
        var orgs = [];
        var checkedNodes = leftTreeObj.getCheckedNodes();
        if(checkedNodes.length==0)
        {
            BIONE.tip("请选择下发机构");
            return;
        }
        for(var i = 0 ;i <checkedNodes.length;i++){
            orgs.push(checkedNodes[i].params.realId);
        }
        var taskUnifyNodeList = [];

        for(var i=0;i<  taskUnifyNodeWindowList.length;i++){
            var unifyNodes = taskUnifyNodeWindowList[i].getTaskUnifyNode();
            if(unifyNodes=="-1")
                return "-1";
            for(var x = 0 ;x<unifyNodes.length;x++ )
                taskUnifyNodeList.push(unifyNodes[x]);
        }
        return {orgs:orgs, taskUnifyNodeList:taskUnifyNodeList};
    }
    var taskUnifyNodeWindowList = [];
    function appendTaskUnifyNodeObjs(node){
        taskUnifyNodeWindowList.push(node);
    }

    
    function getTaskUnifyNode(taskNodeDefId){
        if(taskUnifyNodeList){
            for(var i = 0 ;i <taskUnifyNodeList.length;i++){
                var taskUnifyNode = taskUnifyNodeList[i];
                if(taskUnifyNode.taskNodeDefId==taskNodeDefId)
                    return taskUnifyNode;
            }
        }
        return null;
    }
    
</script>
</head>
<body>
    <div id="template.left.up.icon">
        <img src="${ctx}/images/classics/icons/application_side_tree.png" />
    </div>
    <div id="template.left.up">
        <span style="font-size: 12" id="left_up_span">机构选择</span>
    </div>
    <div id="template.left.down">
        <div id="level" style="line-height: 24px; height: 24px; margin-left: 5px;">
            <span style="display: block; float: left;">是否级联： 是 </span>
            <input type="radio" id="level1" name="level" value="level1" style="display: block; float: left; margin: 5px;" onclick="check()"/>
            <span style="display: block; float: left;">否    </span>
            <input type="radio" id="level2" name="level" value="level2" style="display: block; float: left; margin: 5px;"   onclick="check()"  checked="true"/>
        </div>
    </div>
</body>
</html>