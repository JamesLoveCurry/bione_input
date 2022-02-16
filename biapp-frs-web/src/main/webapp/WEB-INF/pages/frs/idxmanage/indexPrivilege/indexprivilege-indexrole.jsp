<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template6S.jsp">
<head>
<style type="text/css">
#left,#right{
	width: 39%;
	border: 1px solid  #D6D6D6;
}
#left{
	float: right;
	margin-right: 50px;
}
#right {
	float: left;
	margin-left: 50px;
}
</style>
<style type="text/css">
.lsearch {
	height: 26px;
	position: absolute;
	left: 0px;
	top: 31px;
	width: 100%;
	background-color: #f0f0f0;
	z-index: 1000;
}
.l-input-wrap {
	position: relative;
	height: 20px;
	width: 60%;
	margin: 2px 2px 0 2px;
	border: 1px solid #CCC;
	float: left;
}
.l-input-wrap input {
	height: 20px;
	width: 100%;
}
.l-search-btn {
	height: 20px;
	width: 50px;
	border: 1px solid #CCC;
	float: left;
	margin: 2px 5px 0 0;
	line-height: 20px;
	text-align: center;
	cursor: pointer;
	background-color: white;
	
	border-radius: 2px;
}

.close {
	width: 10px;
	height: 10px;
	background: url(${ctx}/images/classics/icons/icons_label.png) no-repeat -288px -30px;
	
	position: absolute;
	top: 2px;
	right:5px;
	cursor: pointer;
}
#right {
    margin-right: 50px;
}
</style>
<script type="text/javascript">
	var ROOT_NO = '0';
	var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png", groupicon = "${ctx}/images/classics/icons/find.png";
	var OBJ_DEF_ROLE = "AUTH_OBJ_ROLE";
    var checkIndexIds = [];
    var roleIds=[];
    var flag = "${flag}";
    var IndexAsycSetting;
    $(function(){
    	setRigthHeight();
    	initRoleAsyc();
  		initIndexAsyc();
    	BIONE.createButton({
		    text : '保存',
		    width : '80px',
		    appendTo : '#bottom',
		    operNo : 'saveButton',
		    icon : 'save',
		    click:function(){
		    	var objIds="";
		    	var rightSelNode;
		    	var resdefNos="";
		    	var rightSelNodes=rightTree.getSelectedNodes();//指标ID
			/* 	if (!rightSelNodes.length || rightSelNodes.length <= 0) {
					//若没有选择授权对象
					$.ligerDialog.warn('请选择授权对象');
					return;
		    	} */
				if(leftTree){
		    		var checkedNodes = leftTree.getCheckedNodes(true);//角色ID
		    		var checkedNodesArray = leftTree.transformToArray(checkedNodes);
					for ( var j = 0; j < checkedNodesArray.length; j++) {
					    objIds += checkedNodesArray[j].id;
					    if (objIds != "") {
							objIds += ",";
					    }
					}	
				}
				rightSelNode= rightSelNodes[0];//指标ID
		    	var objRels={
		    			objId    :rightSelNode.id,//指标ID
		    			resId    :objIds,	//角色ID
		    			 flag	:flag
		    	  };
				$.ajax({
				    async : true,
				    cache : false,
				    url : "${ctx}/frs/idxmanage/indexPrivilege/saveIndexRoleRel.json",
// 				    url : "${ctx}/report/peculiar/auth/indexPrivilegeController.mo?_type=data_event&_field=saveIndexRoleRel&_event=POST&_comp=main&Request-from=dhtmlx&flag="+flag,
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
    	//初始化授权对象树查询按钮事件
		$("#treeSearchIcon").bind("click",function(e){
			searchHandler("1");
		});
		$("#treeSearchInput").bind("keydown",function(e){
			// 按下回车键进行查询
			var code = e.keyCode;
			if(code == null
					|| typeof code == "undefined"
					|| code != "13"){
				return ;
			}
			searchHandler("1");
		});
		//初始化指标树查询按钮事件
		$("#rightTreeSearchIcon").bind("click",function(e){
			searchHandler("2");
		});
		$("#rightTreeSearchInput").bind("keydown",function(e){
			// 按下回车键进行查询
			var code = e.keyCode;
			if(code == null
					|| typeof code == "undefined"
					|| code != "13"){
				return ;
			}
			searchHandler("2");
		});
    	showNm(flag);	
    });
    
    //初始化角色树
    function initRoleAsyc(searchText){
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
		window["leftTree"] = $.fn.zTree.init($("#leftTree"),setting);
   	$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/frs/idxmanage/indexPrivilege/getAuthObjTree.json",
// 			url : "${ctx}/report/peculiar/auth/indexPrivilegeController.mo?_type=data_event&_field=getAuthObjTree&_event=POST&_comp=main&Request-from=dhtmlx&d=" + new Date().getTime(),
			dataType : 'json',
			type : "post",
			data : {
				searchText : searchText
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
				
				leftTree.addNodes(null,result, false);
					}
			});
	}
    
    
    //点击左边的树节点
	function left_clickNode(event,treeId,treeNode) {
	    //获取指定指标关系勾选
	   var indexid=treeNode.id;
	    $.ajax({
			cache : false,
			async : false,
			url : "${ctx}/frs/idxmanage/indexPrivilege/getIdexRoleRel.json",
// 			url : "${ctx}/report/peculiar/auth/indexPrivilegeController.mo?_type=data_event&_field=getIdexRoleRel&_event=POST&_comp=main&Request-from=dhtmlx&flag="+flag,
			dataType : 'json',
			data : {
				indexId : indexid,
				flag : flag
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
				if(leftTree){
					leftTree.checkAllNodes(false);
				}
			    // 清空缓存
				if (!result){  
					return;
				}
				if(leftTree){
					for ( var i = 0; i < result.length; i++) {
					//勾选相应节点
					var node = leftTree.getNodeByParam("id",result[i].id.objId,null);
						 leftTree.checkNode(node,true,true,false); 
				   	}
				 
				}
			},
			error : function(result, b) {/* 
			     BIONE.tip('发现系统错误 <BR>错误码：'
				    + result.status);  */
				}
		    }); 
		}
    //初始化指标树
    function initIndexAsyc(searchText){
      	IndexAsycSetting={
      			async:{
					enable:true,
					type:"post",
					url:"${ctx}/frs/idxmanage/indexPrivilege/getAsyncIndexTree.json",
// 					url : '${ctx}/report/frame/rptidx/idxInfoController.mo?_type=data_event&_field=getAsyncTree&_event=POST&_comp=main&Request-from=dhtmlx',
					autoParam:["nodeType", "upId", "indexVerId"],
					otherParam:{'searchNm': $("#rightTreeSearchInput").val(),'isShowIdx':'1','isShowMeasure':0},
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].indexVerId = childNodes[i].params.indexVerId;
							}
						}
						return childNodes;
					}
				},
				data:{
					key:{
						checked: "isChecked",
						name : "text"
					},
					keep : {
						parent : true
					} /*  ,
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId",
						rootPId : null
					}   */
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
					},
					onClick : left_clickNode
				}
		}; 
		/* var setting={
				async : {
					//zTree的默认参数，且规定以nodeId变量名为变量传递id
					autoParam : [ "id" ],
					enable : true,
					//url : "${ctx}/rpt/peculiar/indexPrivilege/getIndexTree.json?d=" + new Date().getTime(),
					url : "${ctx}/report/peculiar/auth/indexPrivilegeController.mo?_type=data_event&_field=getIndexTree&_event=POST&_comp=main&Request-from=dhtmlx&d=" + new Date().getTime(),
					dataType : "json",
					type : "post",
					data : {
						searchText : searchText
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
					selectedMulti: false,
					showLine: true,
				},
				callback : {
					beforeClick : function(treeId, treeNode,clickFlag) {
					    if (treeNode.isParent == true) {
							//若根节点
							return false;
					    }
					},
					onClick : left_clickNode
				}
    		} */
		window["rightTree"]  = $.fn.zTree.init($("#rightTree"), IndexAsycSetting);
    }

    
	//右侧高度样式设置
	function setRigthHeight(){
		var rightHeight = $("#right").height();
		var $rightTreeContainer = $("#rightTreeContainer");
		//$rightTreeContainer.height(rightHeight - 32 - 26);
		$rightTreeContainer.height(rightHeight - 56 - $("#rightTreeSearchbar").height() );
	} 
	
    //wanghaisong 20170208 用于区分指标管理权限中的  查看权限  和  下钻权限
    function showNm(flag){
    	if(flag=="view"){
    		var indexNm = document.getElementById("indexNm").innerHTML="可查看指标树";
    	}else if(flag=="XMind"){
    		var indexNm = document.getElementById("indexNm").innerHTML="可下钻指标树";
    	}
    }
	// 搜索动作:treeFlag=1,授权对象树；treeFlag=2,指标树；
	var searchHandler = function(treeFlag){
		if(treeFlag && treeFlag=="1"){
			var searchName = $("#treeSearchInput").val();
			initRoleAsyc(searchName);
		}
		if(treeFlag && treeFlag=="2"){
			var searchName = $("#rightTreeSearchInput").val();
			initIndexAsyc(searchName);
		}
	}
	// 刷新指标树节点
	function initNodes(){
		IndexAsycSetting.async.otherParam.searchNm=$("#rightTreeSearchInput").val();
		window["rightTree"] = $.fn.zTree.init($("#idxtree"), IndexAsycSetting);
	}	
 </script>
</head>
<body>
	<div id="template.left.up">授权对象树</div>
	
	<div id="template.right">
		<div id="righttable" width="100%" border="0">
				<div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%" style="padding-left: 10px;float: left;position: relative;height:20p;margin-top: 8px">
						<%-- <img src="${ctx}/images/classics/icons/application_side_tree.png" /> --%>
					</div>
					<div width="90%">
						<span id="indexNm" style="font-size: 12;float: left;position: relative;line-height: 30px;padding-left:2px ">
							指标树
						</span>
					</div>
				</div>
			</div>
		<div id="rightToolbar"
			style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
		<div id="rightTreeSearchbar" style="width:99%;margin-top:2px;padding-left:2px;">
			<ul>
				<li style="width:99%;text-align:left;">                      
					<div class="l-text-wrapper" style="width: 100%;">                         
						<div class="l-text l-text-date" style="width: 100%;">                    
							<input id="rightTreeSearchInput" type="text" class="l-text-field"  style="width: 100%;right: 0px;padding-left: 0px;" />    
							<div class="l-trigger">                                                                      
								<a id="rightTreeSearchIcon" class = "icon-search search-size" ></a>                                                   
							</div>
						</div>                                                                                                   
					</div>
				</li>
			</ul>                                                                                                         
		</div> 
		<div id="rightTreeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="rightTree"
				style="font-size: 12; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
	</div>
</body>
</html>