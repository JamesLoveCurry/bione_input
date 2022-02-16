<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template10.jsp">
<head>
<script type="text/javascript">
	var doFlag = "";
	var currentNode=null; 
	$(function() {
		tmp.init();
		$("#left").css("width","45%");
		$("#right").css("width","45%");
// 		$("#leftTreeContainer").height($("#left").height() - 32);
		$("#leftTreeContainer").height($("#left").height() - 58);
		$("#rightTreeContainer").height($("#right").height() - 31);
		
		$("#leftTree").height($("#left").height() - 68);
		$("#rightTree").height($("#right").height() - 41);
	});
	var tmp = {};
	parent.parent.window.child = window;
	tmp.init =  function() {
			$("#treeToolbar").ligerTreeToolBar({
				items : [{
					icon:'refresh',
					text : '刷新全部',
					click : function(){
						$("#rightTree").empty();
						tmp.leftTreeObj.reAsyncChildNodes(null, "refresh");
					}
				},{
					line : true
				}]
			});
		
			var setting = {
				async : {
					enable : true,
					url : this.getAsyncUrl,
					autoParam : [ "rid","tskType" ],
					dataType : "json",
					type : "get",
					dataFilter : function(treeId, parentNode, childNodes) {
						if (childNodes) {
							for ( var i = 0; i < childNodes.length; i++) {
								childNodes[i].rid = childNodes[i].params.rid;
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].tskType = childNodes[i].params.tskType;
							}
						}
						return childNodes;
					}
				},
				data : {
					key : {
						name : "text"
					}
				},
				view : {
					selectedMulti : false,
					addDiyDom : this.addLeftDiyDom
				},
				callback : {
					onClick : this.zTreeOnClick,
					onNodeCreated : function(event, treeId, treeNode) {
						if (treeNode.nodeType == "ROOT") {
							//若是根节点，展开下一级节点
							tmp.leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
						}
					}
				}
			};
			this.leftTreeObj = $.fn.zTree.init($("#leftTree"), setting);
		};
	tmp.getAsyncUrl = function (treeId, treeNode) {
		if (!treeNode || treeNode.nodeType == "ROOT" ) {    
			return "${ctx}/rpt/frs/rptfill/getTaskChildOrgTree?"
					+ new Date().getTime();
		} else if(treeNode.nodeType == "ORG") {
			return "${ctx}/rpt/frs/rptfill/getTaskchildRptTree?type="+treeNode.params.type+"&t="
					+ new Date().getTime();
		} else if(treeNode.nodeType == "TASK"){
			return "${ctx}/rpt/frs/rptfill/getTaskchildTree?type="+treeNode.params.type+"&dataDate=" + treeNode.params.dataDate + "&t="+ new Date().getTime();
		}
	};	

	tmp.resetInfo=function(){
		var treeNode=currentNode;
		tmp.loadRightTree(treeNode.params.taskid, treeNode.rid, treeNode.params.upinstanceid, treeNode.params.lineId, treeNode.params.dataDate);
		this.leftTreeObj.reAsyncChildNodes(currentNode.getParentNode(), "refresh");
	};
	
	
	tmp.initRightTree = function (taskid, taskObjId, nodes) {
		$("#rightTree").empty();
		var setting = {
			data : {
				key : {
					name : 'text'
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			view : {
				selectedMulti : false,
				addDiyDom : this.addRightDiyDom,
				fontCss : this.setFontCss
			},
			callback : {
				onClick : this.zTreeOnClick
			}
		};
		this.rightTreeObj = $.fn.zTree.init($("#rightTree"), setting, nodes );
		this.rightTreeObj.expandAll(true);
	};
	tmp.loadRightTree = function (taskid, taskObjId, upinsid, lineId, dataDate){
		$.ajax({
			cache : false,     
			async : false,
			url : "${ctx}/rpt/frs/rptfill/getTaskRptchildExeObjTree?taskid="+taskid+"&taskObjId="+taskObjId+"&upinsid="+upinsid+"&lineId="+lineId+"&dataDate=" + dataDate + "&t="
				+ new Date().getTime(),
			type : "get",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				if(result) {
					tmp.initRightTree(taskid,taskObjId,result);
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	};
	tmp.addLeftDiyDom = function (treeId, treeNode) {
		if (!treeNode || treeNode.nodeType == "ORG") {
			return;
		}
		var aObj = $("#" + treeNode.tId + "_a");
		if (!aObj)
			return;
		if (treeNode.nodeType == "TASK") {
			var editStr = "<span id='diyBtn_" +treeNode.rid+ "' style='margin-left:30px'>"
				+ treeNode.params.dataDate + "</span>";
			aObj.after(editStr);
		} else if (treeNode.nodeType == "RPT"){
			var editStr = "<span id='diyBtn1_"
				+ treeNode.rid
				+ "' onclick='return false;' style='margin-left:10px'>已提交("
				+ treeNode.params.complete
				+ ")</span>";
			if(treeNode.params.uncomplete == 0){
				editStr += "<span id='diyBtn2_"
					+ treeNode.rid
					+ "' onclick='return false;' style='margin-left:0px;'>/未提交("
					+ treeNode.params.uncomplete + ")</span>";
			}else{
				editStr += "<span id='diyBtn2_"
					+ treeNode.rid
					+ "' onclick='return false;' style='color:red;margin-left:0px;'>/未提交("
					+ treeNode.params.uncomplete + ")</span>";
			}
// 			var editStr = "<span id='diyBtn1_"
// 				+ treeNode.rid
// 				+ "' onclick='return false;' style='margin-left:10px'>已提交("
// 				+ treeNode.params.complete
// 				+ ")</span>"
// 				+ "<span id='diyBtn2_"
// 				+ treeNode.rid
// 				+ "' onclick='return false;' style='color:red;margin-left:0px;'>/未提交("
// 				+ treeNode.params.uncomplete + ")</span>";
/* 			var editStr = "<a id='diyBtn1_"
					+ treeNode.rid
					+ "' onclick='alert(1);return false;' style='margin-left:10px'>已提交("
					+ treeNode.params.complete
					+ ")</a>"
					+ "<a id='diyBtn2_"
					+ treeNode.rid
					+ "' onclick='alert(2);return false;' style='color:red;margin-left:0px;'>/未提交("
					+ treeNode.params.uncomplete + ")</a>"; */
			aObj.after(editStr);
		}
	};
	tmp.addRightDiyDom = function (treeId, treeNode) {
		if (!treeNode || !treeNode.params || treeNode.params.nodeType != "RPTEXE") {
			return;
		}
		var aObj = $("#" + treeNode.tId + "_a");
		if (!aObj) return;
		var editStr = "<a id='diyBtn2_"
			+ treeNode.rid
			+ "' onclick=tmp.operFill('" + treeNode.params.sts + "','" + treeNode.params.rptId + "','" + treeNode.params.rptNm + "','" + treeNode.params.dataDate + "','" + treeNode.params.orgNo + "','" + $.trim(treeNode.params.orgNm) + "','" + treeNode.params.exeObjId + "','" + treeNode.params.lineId + "','" + treeNode.params.logicRs + "','" + treeNode.params.warnRs + "','" + treeNode.params.taskInsId + "','" + treeNode.params.templateId + "','" + treeNode.params.tskType + "') style='margin-left:10px;'>查看填报" + "</a>";
/* 			var editStr = "<a id='diyBtn1_"
					+ treeNode.rid
					+ "' onclick='alert(1);return false;' style='margin-left:10px'>查看历史记录"
					+ "</a>"
					+ "<a id='diyBtn2_"
					+ treeNode.rid
					+ "' onclick=tmp.operFill('" + treeNode.params.sts + "','" + treeNode.params.rptId + "','" + treeNode.params.rptNm + "','" + treeNode.params.dataDate + "','" + treeNode.params.exeObjId + "','" + treeNode.params.lineId + "','" + treeNode.params.taskInsId + "') style='margin-left:10px;'>查看填报" + "</a>"; */
			aObj.after(editStr);
	};
	tmp.zTreeOnClick = function (event, treeId, treeNode) {
		currentNode=treeNode;
		if (treeNode.nodeType == "RPT") {
			tmp.loadRightTree(treeNode.params.taskid, treeNode.rid, treeNode.params.upinstanceid, treeNode.params.lineId, treeNode.params.dataDate);
		}
	};
	tmp.setFontCss = function (treeId, treeNode) {
		return treeNode.params.sts == 0 ? {
			color : "red"
		} : {};
	};
	tmp.operFill = function (sts, rptId, rptNm, dataDate, orgNo, orgName, exeObjId, lineId, logicRs, warnRs, taskInsId, templateId, taskType) {
		if("1" == sts){ 
// 			BIONE.tip("任务已锁定不能填报");
			BIONE.ajax({
					async : false,
					url : "${ctx}/rpt/frs/rptfill/createColor",
					dataType : 'text',
					type : 'POST',
					data : {
						rptId : rptId,
						orgNo : orgNo, 
						dataDate : dataDate,
						tmpId : templateId,
						isBatchCheck : true
					}
				}, function(result) {
					var title = "当前报表:" + rptNm;
					var height = $(parent.parent.window).height() - 10;
					var width = $(parent.parent.window).width();                                
					window.parent.parent.curtab=window;
					window.top.color=result;
					window.parent.parent.BIONE.commonOpenDialog(title, "taskViewChildWin", width, height, "${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-view-child&dataDate=" + dataDate + "&orgNo=" + orgNo + "&exeObjId=" + exeObjId + "&rptId=" + rptId + "&logicRs=" + logicRs + "&warnRs=" + warnRs + "&taskInsId=" + taskInsId + "&lineId=" + lineId  + "&rptNm=" + encodeURI(encodeURI(rptNm)) + "&orgNm=" + encodeURI(encodeURI(orgName)), null);
				});
		}else{ 
			BIONE.ajax({
				async : false,
				url : "${ctx}/rpt/frs/rptfill/createColor",
				dataType : 'text',
				type : 'POST',
				data : {
					rptId : rptId,
					orgNo : orgNo,
					dataDate : dataDate,
					tmpId : templateId,
					isBatchCheck : true
				}
			}, function(result) {
				var title = "当前报表:" + rptNm;
				var height = $(parent.parent.window).height() - 10;
				var width = $(parent.parent.window).width();
				window.parent.parent.curtab=window;
				window.top.color=result;                                                                         
				window.parent.parent.BIONE.commonOpenDialog(title, "taskFillChildWin", width, height, "${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-oper-child&dataDate=" + dataDate + "&orgNo=" + orgNo + "&exeObjId=" + exeObjId + "&rptId=" + rptId + "&logicRs=" + logicRs + "&warnRs=" + warnRs + "&taskInsId=" + taskInsId + "&lineId=" + lineId  + "&rptNm=" + encodeURI(encodeURI(rptNm)) + "&type=" + taskType + "&templateId=" + templateId + "&orgNm=" + encodeURI(encodeURI(orgName)), null);
			});
		}
	};

</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.right.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">任务列表</div>
	<div id="template.right.up">任务报表详情</div>

</body>
</html>