<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content="/template/template4.jsp">
<title>角色到报表</title>
<style type="text/css">
 .lsearch {
	height: 26px;
	position: absolute;
	left: 0px;
	top: 31px;
	width: 30%;
	background-color: #f0f0f0;
	z-index: 1000;
} 
</style>

<script type="text/javascript">
	var checkRptIds=[];
	var OBJ_DEF_ROLE = "AUTH_OBJ_ROLE";
	$(function(){
		initRptAsyc();
		initOperaAsyc();
		initRoleAsyc();
		
		BIONE.createButton({
		    text : '保 存',
		    width : '80px',
		    appendTo : '#bottom',
		    operNo : 'saveButton',
		    icon : 'save',
		    click:function(){
		    	var leftIds="";
		    	var centerIds="";
		    	var uncheckObjIds="";
		    	var rightIds="";
		    	var leftSelNode;
		    	var centerSelNode;
		    	var rightSelNode;
		    	var leftSelNodes=leftTree.getSelectedNodes();//角色ID
		    	var centerSelNodes=centerTree.getCheckedNodes(true);//报表ID
		    	var uncheckedNodes=centerTree.getCheckedNodes(false);//wanghaisong 20170214 获取未选中节点ID
		    	var rightSelNodes=rightTree.getCheckedNodes(true);//操作类型
		    	
 				if (!leftSelNodes.length || leftSelNodes.length <= 0) {
					//请选择报表资源
					$.ligerDialog.warn('请选择角色资源');
					return;
		    	} 
				//获取左边的数据
				leftIds=leftSelNodes[0].id;
				//获取中间的数据
				if(centerTree){
		    		var centerNodesArray = centerTree.transformToArray(centerSelNodes);
					for ( var j = 0; j < centerNodesArray.length; j++) {
						//centerIds += centerNodesArray[j].id;
						centerIds += centerNodesArray[j].params.rptId;
					    if (centerIds != "") {
					    	centerIds += ",";
					    }
					}
		    		var uncheckedNodesArray = centerTree.transformToArray(uncheckedNodes);
					for ( var j = 0; j < uncheckedNodesArray.length; j++) {
						if(uncheckedNodesArray[j].params.catalogId!=undefined){
							uncheckObjIds += uncheckedNodesArray[j].params.catalogId;
						}else{
							uncheckObjIds += uncheckedNodesArray[j].params.rptId;
						}
					    if (uncheckObjIds != "") {
					    	uncheckObjIds += ",";
					    }
					}
				}
				//获取右边的数据
				if(rightTree){
		    		var rightNodesArray = rightTree.transformToArray(rightSelNodes);
					for ( var j = 0; j < rightNodesArray.length; j++) {
						rightIds += rightNodesArray[j].id;
					    if (rightIds != "") {
					    	rightIds += ",";
					    }
					}	
				}
		    	var objRels={
				    	leftIds    		:leftIds,//角色ID
				    	centerIds  		:rightIds,//资源ID
				    	uncheckObjIds	:uncheckObjIds,//为选中节点ID
				    	rightIds   		:centerIds//报表ID
		    	  };
				$.ajax({
				    async : true,
				    cache : false,
				    url : "${ctx}/frs/submitConfig/frsAuthority/saveRoleRpt?d=" + new Date().getTime(),
				    dataType : 'json',
				    type : "post",
				    data : {
				    	objRels : JSON2.stringify(objRels)
				    },
				    beforeSend : function() {
						BIONE.loading = true;
						BIONE.showLoading("正在加载数据中...");
				    },
				    complete : function() {
						BIONE.loading = false;
						BIONE.hideLoading();
				    },
				    success : function(result) {
						BIONE.tip('保存成功!');
				    },
				    error : function(result, b) {
						BIONE.tip('保存失败,发现系统错误 <BR>错误码：' + result.status);
				    }
				});
		    }
		});
	});
    //初始化报表树
    function initRptAsyc(){
      	var setting={
				async : {
					//zTree的默认参数，且规定以nodeId变量名为变量传递id
					autoParam : [ "id" ],
					enable : true,
					url : "${ctx}/frs/submitConfig/frsAuthority/getRptTree?d=" + new Date().getTime(),
					dataType : "json",
					type : "post",
					dataFilter: function(treeId, parentNode, childNodes) {
						var cNodes = childNodes;
						$.each(cNodes, function(i,n){
							for(var i = 0 ,l = checkRptIds.length; i < l ; i++){
									if(n.id == checkRptIds[i]){
										n.isChecked = true;
										break;
									}
								}
							n.rptId = n.id;
							n.objDefNo = OBJ_DEF_ROLE;
							});
							return cNodes; 
						}
				},
				data : {
					key : {
					 	checked: "isChecked",
						name : "text"					
					},
					keep : {
						parent : true
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId",
						rootPId : null
					}
				},
				view : {
					selectedMulti: true,
					showLine: true,
				},
				check : {
    			    chkStyle : 'checkbox',
    			    enable : true,
    			    chkboxType : {
    				"Y" : "s",
    				"N" : "s"
    			    }
    			},
				callback : {
					onClick : left_clickNode
				}
    		}
		//window["rightTree"]  = $.fn.zTree.init($("#rightTree"),setting);
      	window["centerTree"] = $.fn.zTree.init($("#centerTree"),setting);
    }
    
  //初始化操作类型
    function initOperaAsyc(){
       	var setting={
    	 	data : {
    				keep:{
    					leaf:true,
    					parent:true
    					}, 
    				key : {
    					//checked: "isChecked",
    					name : "text"
    				},
    				simpleData : {
    					enable : true,
    					idKey : "id",
    					pIdKey : "upId",
    					rootPId : null
    				}
    			},
    			view : {
    				selectedMulti: true,
    				showLine: true
    			},
    			check : {
    			    chkStyle : 'checkbox',
    			    enable : true,
    			    chkboxType : {
    				"Y" : "",
    				"N" : ""
    			    }
    			}
        	}
    		//window["centerTree"] = $.fn.zTree.init($("#centerTree"),setting);
       		window["rightTree"]  = $.fn.zTree.init($("#rightTree"),setting);
       	$.ajax({
    			cache : false,
    			async : true,
    			url : "${ctx}/frs/submitConfig/frsAuthority/getOper?d=" + new Date().getTime(),
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
    				//centerTree.addNodes(null,result, false);
    				rightTree.addNodes(null,result, false);
    					}
    			});
  	}
    
    //初始化角色树
    function initRoleAsyc(){
   	var setting={
	 	data : {
				keep:{
					leaf:true,
					parent:true
					}, 
				key : {
					//checked: "isChecked",
					name : "text"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId",
					rootPId : null
				}
			},
			view : {
				selectedMulti: false,
				showLine: true
			},
			callback : {
				beforeClick : function(treeId, treeNode,clickFlag) {
			    if (treeNode.isParent == true) {
					//若根节点
					return false;
			    }
			} 
			,onClick : left_clickNode
		}
    	}
		window["leftTree"] = $.fn.zTree.init($("#leftTree"),setting);
   	$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/frs/submitConfig/frsAuthority/getAuthObjTree?d=" + new Date().getTime(),
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
				leftTree.addNodes(null,result, false);
					}
			});
	}
    
    //点击左边的树节点
	function left_clickNode(event,treeId,treeNode) {
	    //获取指定指标关系勾选
	   var roleid=treeNode.id;
	    $.ajax({
			cache : false,
			async : false,
			url : "${ctx}/frs/submitConfig/frsAuthority/getRoleRptRel",
			dataType : 'json',
			data : {
				roleid : roleid
			},
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
				//清除所有树之前选中
				if(centerTree){
					centerTree.checkAllNodes(false);
					}
				if(rightTree){
					rightTree.checkAllNodes(false);
					}
				checkRptIds=[];
			    // 清空缓存
				if (!result){  
					return;
				}
				if(centerTree && rightTree){
					for ( var i = 0; i < result.length; i++) {
	                     //勾选相应节点
						var node1 = rightTree.getNodeByParam("id",
								result[i].id.resDefNo, null);//操作类型
						var node2 = centerTree.getNodeByParam("id",
								result[i].id.resId, null);//角色
						if (node1 != null) {
							rightTree.checkNode(node1, true, true, false);
						}
						if (node2 != null) {
							centerTree.checkNode(node2, true, true, false);
						}
						checkRptIds.push(result[i].id.resId);
					}

				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">角色名称</div>
	<!-- 中间操作类型 -->
		<div id="template.center" style="background-color: #FFFFFF">
			<div id="centertable" width="100%" border="0">
				<div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%"
						style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
					</div>
					<div width="30%">
						<span
							style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
							报表名称
						</span>
					</div>
				</div>
			</div>
			<div id="centenTreeToolbar"
				style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;">
				
			</div>
			<div id="centerTreeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="centerTree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
					
			</div>
		</div>
	
	<!-- 右边角色树 -->
	<div id="template.right" style="background-color: #FFFFFF">
			<div id="righttable" width="100%" border="0">
				<div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%"
						style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
					</div>
					<div width="30%">
						<span
							style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
							操作类型
						</span>
					</div>
				</div>
			</div>
			<div id="rightTreeToolbar"
				style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
			<div id="rightTreeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="rightTree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
</body>
</html>