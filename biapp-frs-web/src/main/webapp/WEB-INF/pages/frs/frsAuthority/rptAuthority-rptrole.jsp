<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content="/template/template4.jsp">
<title>报表到角色</title>
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
		    	var rightIds="";
		    	
		    	var leftSelNode;
		    	var centerSelNode;
		    	var rightSelNode;
		    	var leftSelNodes=leftTree.getSelectedNodes();//报表ID
		    	var centerSelNodes=centerTree.getCheckedNodes(true);//操作类型
		    	var rightSelNodes=rightTree.getCheckedNodes(true);//角色ID
		    	
 				if (!leftSelNodes.length || leftSelNodes.length <= 0) {
					//请选择报表资源
					$.ligerDialog.warn('请选择报表资源');
					return;
		    	} 
 		/* 		if (!centerSelNodes.length || centerSelNodes.length <= 0) {
					//请选择报表资源
					$.ligerDialog.warn('请选操作类型');
					//请选择报表资源
					$.ligerDialog.warn('请选择角色对象');
					return;
		    	}
 				if (!rightSelNodes.length || rightSelNodes.length <= 0) {
					//请选择报表资源
					$.ligerDialog.warn('请选择角色对象');
					//请选择报表资源
					$.ligerDialog.warn('请选操作类型');
					return;
		    	}  */
				//获取左边的数据
				leftIds=leftSelNodes[0].id;
				//获取中间的数据
				if(centerTree){
		    		var centerNodesArray = centerTree.transformToArray(centerSelNodes);
					for ( var j = 0; j < centerNodesArray.length; j++) {
						centerIds += centerNodesArray[j].id;
					    if (centerIds != "") {
					    	centerIds += ",";
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
				    	leftIds    :leftIds,//报表ID
				    	//centerIds  :centerIds,//资源ID
				    	//rightIds   :rightIds//角色ID
				    	centerIds  :rightIds,//资源ID
				    	rightIds   :centerIds//角色ID
		    	  };
		    	
				$.ajax({
				    async : true,
				    cache : false,
				    url : "${ctx}/frs/submitConfig/frsAuthority/saveRpt?d=" + new Date().getTime(),
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
					type : "post"
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
					selectedMulti: false,
					showLine: true,
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
		window["leftTree"]  = $.fn.zTree.init($("#leftTree"),setting);
   /*  	var leftUp=$("#leftTree");
    	leftUp.append('<div id="lsearch" class="lsearch" style="display: none;"><div id="l-text-wrap" class="l-input-wrap"><input id="l-text-field" class="l-text-field" /></div><div id="l-search-btn" class="l-search-btn">搜索</div><div class="close" style=""></div></div>');
		$('#lsearch').show();
		$('#l-text-wrap').width($('#lsearch').width() - 80); */

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
       		window["rightTree"] = $.fn.zTree.init($("#rightTree"),setting);
       	$.ajax({
    			cache : false,
    			async : false,
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
		//window["rightTree"] = $.fn.zTree.init($("#rightTree"),setting);
   		window["centerTree"] = $.fn.zTree.init($("#centerTree"),setting);
   	$.ajax({
			cache : false,
			async : false,
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
				//rightTree.addNodes(null,result, false);
				centerTree.addNodes(null,result, false);
					}
			});
	}
    
    //点击左边的树节点
	function left_clickNode(event,treeId,treeNode) {
    	
	    //获取指定指标关系勾选
	   var rptid=treeNode.id;
	    $.ajax({
			cache : false,
			async : false,
			url : "${ctx}/frs/submitConfig/frsAuthority/getRptRoleRel",
			dataType : 'json',
			data : {
				rptid : rptid
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
			    // 清空缓存
				if (!result){  
					return;
				}
				if(centerTree && rightTree){
					for ( var i = 0; i < result.length; i++) {
					//勾选相应节点
					//var node1 = centerTree.getNodeByParam("id",result[i].id.resDefNo,null);//操作类型
					//var node2 = rightTree.getNodeByParam("id",result[i].id.objId,null);//角色
					var node1 = rightTree.getNodeByParam("id",result[i].id.resDefNo,null);//操作类型
					var node2 = centerTree.getNodeByParam("id",result[i].id.objId,null);//角色
					if(node1!=null){
						//centerTree.checkNode(node1,true,true,false);
						rightTree.checkNode(node1,true,true,false);
					}
					if(node2!=null){
						//rightTree.checkNode(node2,true,true,false); 
						centerTree.checkNode(node2,true,true,false); 
					}	
				   }
				 
				}
			},
			error : function(result, b) { 
			     BIONE.tip('发现系统错误 <BR>错误码：'
				    + result.status);  
				}
		    }); 
		}
</script>
</head>
<body>
	<div id="template.left.up.icon">报表名称</div>
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
							角色名称
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