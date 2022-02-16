<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
var navtab1;
var logDetail;
var ROOT_NO = '0';
	$(function(){
		navtab1 = window['tab'] = $("#tab").ligerTab();
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/bione/syslog/auth/detailList.json?d="
					+ new Date().getTime(),
			dataType : 'json',
			data : {logId : "${logId}"},
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
				logDetail = result;
				initTree();
			}
		});


	});
	
	function initTree(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/bione/admin/auth/getAuthResDefTabs.json?d="
					+ new Date().getTime(),
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
				var data = null;
				if (result) {
					data = result.Data;
				}
				if (data && data.length > 0) {
					for ( var i = 0; i < data.length; i++) {
						var appendHtml = "<div style='overflow: auto;'><div id='"
								+ data[i].resDefNo
								+ "_Container' style='width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;'><div id='authRes_"
								+ data[i].resDefNo
								+ "' class='ztree' style='font-size: 12; background-color: #FFFFFF; width: 95%''></div></div></div>";
						navtab1.addTabItem({
							tabid : data[i].resDefNo,
							content : appendHtml,
							text : data[i].resName,
							showClose : false
						});
						var subTreeId = "#authRes_" + data[i].resDefNo;

						if (eval($(subTreeId))) {
							//构造资源树	
							var resDefNoTmp = data[i].resDefNo;
							window['resTree_' + data[i].resDefNo] = $.fn.zTree
									.init(
											eval($(subTreeId)),
											{
												check : {
													chkStyle : 'checkbox',
													enable : true,
													chkboxType : {
														"Y" : "ps",
														"N" : "s"
													}
												},
												data : {
													key : {
														name : 'text'
													},
													simpleData : {
														enable : true,
														idKey : "id",
														pIdKey : "upId",
														rootPId : null
													}
												},
												view : {
													selectedMulti : false,
													showLine : true
												},
												callback : {
													beforeCheck : function(treeId, treeNode){
														return false;
													},
													onExpand : function(){
														$(".checkbox_true_disable").removeClass("checkbox_true_disable").addClass("checkbox_true_part");
														$(".checkbox_false_disable").removeClass("checkbox_false_disable").addClass("checkbox_false_part");
													}
												}
											});

							//构造高度
							var treeContainer = data[i].resDefNo
									+ "_Container";
							if ($("#" + treeContainer) && $("#center")) {
								$("#" + treeContainer).height(
										$("#center").height() - 28);
							}

							window['resTreeInit_' + data[i].resDefNo] = function(
									resObjNo) {
								$.ajax({
											cache : false,
											async : true,
											url : "${ctx}/bione/admin/auth/getAuthResDefTree.json?d="
													+ new Date()
															.getTime(),
											dataType : 'json',
											type : "post",
											data : {"resDefNo" : resObjNo},
											beforeSend : function() {
												BIONE.loading = true;
												BIONE.showLoading("正在加载数据中...");
											},
											complete : function() {
												BIONE.loading = false;
												BIONE.hideLoading();
											},
											success : function(result) {
												if (!result)
													return;
												
												checkAndDisableNode(result, resObjNo);
												if (eval('resTree_'
														+ resObjNo)) {
													result = initIcon(result);
													eval(
															'resTree_'
																	+ resObjNo)
															.addNodes(
																	null,
																	result,
																	false);
													var rootNodeTmp = eval('resTree_'+resObjNo).getNodeByParam("id", ROOT_NO , null);
													eval('resTree_'+resObjNo).expandNode(rootNodeTmp , true , false, true, true);
													//eval('resTree_'+resObjNo).checkAllNodes(true);

													//勾选节点
													/* if(logDetail && logDetail.length > 0 ){
														for(var j=0;j<logDetail.length;j++){
															if(logDetail[j].id.resDefNo == resObjNo){
																var checkNode = eval('resTree_'+resObjNo).getNodesByParam("id", logDetail[j].id.resId, null );
																if(checkNode != null && checkNode.length > 0)
																	eval('resTree_'+resObjNo).checkNode(checkNode[0], true, false, false);
																
																checkNode = eval('resTree_'+resObjNo).getNodesByParam("id", "r" + logDetail[j].id.resId, null );
																if(checkNode != null && checkNode.length > 0)
																	eval('resTree_'+resObjNo).checkNode(checkNode[0], true, false, false);
																		
															}
														}
													} */
												}
											},
											error : function(result, b) {
												BIONE.tip('发现系统错误 <BR>错误码：'
																+ result.status);
											}
										});
							};
							//追加节点
							if (eval('resTreeInit_' + data[i].resDefNo)) {
								eval('resTreeInit_' + data[i].resDefNo
										+ "('" + data[i].resDefNo
										+ "');");
							}
							
						}
						
					}
					//设置默认选中tab为第一个tab
					navtab1.selectTabItem(data[0].resDefNo);
				}
			},
			error : function(result, b) {
				////BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	// 加工图标
	function checkAndDisableNode(node, resObjNo){
		if(node && node.length > 0){
			for(var i=0;i<node.length;i++){
				node[i].chkDisabled = true;
				if(logDetail && logDetail.length > 0 ){
					for(var j=0;j<logDetail.length;j++){
						if((node[i].id == logDetail[j].id.resId || node[i].params.realId == logDetail[j].id.resId)&&(logDetail[j].id.resDefNo == resObjNo)){
							node[i].checked = true;
						}
					}
				}
				checkAndDisableNode(node[i].children, resObjNo)
			}
		}
	}
	function initIcon(nodes){
		if(nodes
				&& nodes instanceof Array === true){
			for ( var i = 0; i < nodes.length; i++) {
				var r = nodes[i];
				r.icon = "${ctx}"
						+ (r.icon.indexOf("/") != 0 ? "/" : "")
						+ r.icon;
				if(r.children
						&& r.children != null){
					r.children = initIcon(r.children);
				}
			}
		}
		return nodes;
	}
</script>
</head>
<body>
</body>
</html>