<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template10a_BS.jsp">
<head>
<script type="text/javascript">
	var doFlag = "";
	var onlyWatch = "false";
	var moduleType = "${moduleType}";
	var taskId = '${taskId}';
	var fromMainPage = "${fromMainPage}";
	var currentNode=null;
	var leftCurrentNode=null;
	var rightCurrentNode=null;
	var reportDialog=null;
	$(function() {
		tmp.init();
		$("#left").css("width","45%");
		$("#right").css("width","45%");
		$("#leftTreeContainer").height($("#left").height() - 58);
		$("#rightTreeContainer").height($("#right").height() - 31);
		$("#leftTree").height($("#left").height() - 68);
		$("#rightTree").height($("#right").height() - 41);
		$(window).resize(function(){
			if(reportDialog!=null){
				var height = $(parent.parent.parent.parent.window).height() - 10;
				var width = $(parent.parent.parent.window).width();
				reportDialog._setHeight(height);
				reportDialog._setWidth(width);
			}
		});
	});
	function onResetWatch(){
		var title = "设置收藏";
		var height = $("#left").height() - 80;
		var width = "315";
		BIONE.commonOpenDialog(title, "setWatchWin", width, height, "${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-watch&moduleType=${moduleType}", null,function(data) {
			if(data==null) return ;
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/saveWatch?moduleType=${moduleType}",
				dataType : 'text',
				data : {"data":JSON2.stringify(data)},
				type : "get",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result){
					BIONE.tip("保存成功!");
					if(onlyWatch=="true")
						onChangeWatch();
				},
				error:function(){
					//BIONE.tip("数据异常，请联系系统管理员");
				}
			});
		});
	}
	
	function onLoadWatch(onlyWatch){
		tmp.leftTreeObj.setting.async.otherParam = {'moduleType': moduleType,'onlyWatch':onlyWatch};
		$("#rightTree").empty();
		tmp.leftTreeObj.reAsyncChildNodes(null, 'refresh');
	}
	
	function onChangeWatch(type){
		if(type =="1"){
			if(onlyWatch=="true")
			{
				onlyWatch = "false";
                $("#onlyWatch").find("i").attr("class","fa fa-star-o");
			}
			else
			{	
				onlyWatch = "true";
                $("#onlyWatch").find("i").attr("class","fa fa-star");
			}
		}
		onLoadWatch(onlyWatch);
	}
	parent.parent.window.child = window;
	var tmp ={};
	tmp.init = function () {
		$("#treeToolbar").ligerTreeToolBar({
			items : [{
				icon: 'fa-refresh',
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
					autoParam : [ "rid","tskType","dataDate" ],
					otherParam:{'moduleType': moduleType,'onlyWatch':onlyWatch},
					dataType : "json",
					type : "get",
					dataFilter : function(treeId, parentNode, childNodes) {
						if (childNodes) {
							for ( var i = 0; i < childNodes.length; i++) {
								childNodes[i].rid = childNodes[i].params.rid;
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].tskType = childNodes[i].params.tskType;
								childNodes[i].dataDate = childNodes[i].params.dataDate;
								childNodes[i].isFill = childNodes[i].params.isFill;
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
					addDiyDom :tmp.addLeftDiyDom
				},
				callback : {
					onClick : tmp.zleftTreeOnClick,
					onNodeCreated : function(event, treeId, treeNode) {
						if (treeNode.nodeType == "ROOT") {
							//若是根节点，展开下一级节点
							tmp.leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
						}
						if(fromMainPage != null && fromMainPage == "1"){
							if(treeNode.id == "${taskId}" && treeNode.dataDate == "${dataDate}" && treeNode.nodeType == "TASK"){
								tmp.leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
							} 
							if(treeNode.params.taskid == "${taskId}" && treeNode.rid == "${rptId}" && treeNode.nodeType == "TSKRPT"){
								tmp.leftTreeObj.selectNode(treeNode);
								leftCurrentNode = treeNode;
								currentNode = treeNode;
								tmp.loadRightTree(treeNode.params.taskid, treeNode.rid,treeNode.params.tskType,treeNode.params.dataDate,treeNode.params.isFill);
							}
						}
					}
				}
			};
			this.leftTreeObj = $.fn.zTree.init($("#leftTree"), setting);
		};
		tmp.getAsyncUrl = function (treeId, treeNode) {
			if (!treeNode || treeNode.params.nodeType == "ROOT") {
				return "${ctx}/rpt/frs/rptfill/getTaskTree?t="+ new Date().getTime();
			} else {
				return "${ctx}/rpt/frs/rptfill/getTaskRptTree?isFill="+encodeURI(encodeURI(treeNode.params.isFill))+"&t="+ new Date().getTime();
			}
		};
		var taskIdForFill = "";
		tmp.initRightTree = function (taskid, taskObjId, nodes) {
			taskIdForFill = taskid;
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
					addDiyDom : tmp.addRightDiyDom,
					fontCss : tmp.setFontCss
				},
				callback : {
					//onClick : tmp.zTreeOnClick
					onClick : tmp.zrigthTreeOnClick 
				}
			};
			this.rightTreeObj = $.fn.zTree.init($("#rightTree"), setting, nodes );
			//展开前三级树节点
			if(fromMainPage != null && fromMainPage == "1"){
				this.rightTreeObj.expandAll(true);
				for(var i=0; i<nodes.length; i++){
					if(nodes[i].id == '${orgNo}'){
						this.rightTreeObj.expandNode(nodes[i-1],true, false, false)
						var id = "diyBtn2_"	+ nodes[i].id;
			 			$("#"+id).click();
			 			fromMainPage = 0;
					}
				}
			}else{
				var sNodes = this.rightTreeObj.getNodes();
				for(var s in sNodes){
					this.rightTreeObj.expandNode(sNodes[s], true, false, false);
					for(var ss in sNodes[s].children){
						this.rightTreeObj.expandNode(sNodes[s].children[ss], true, false, false);
					}
				}
			}
 			//this.rightTreeObj.expandAll(true);
		};
		//lxp
		tmp.loadRightTree = function (taskid, taskObjId,tskType,dataDate, isFill){
			$.ajax({
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/getTaskRptExeObjTree?taskid="+taskid+"&taskObjId="+taskObjId+"&tskType="+tskType+"&dataDate=" + dataDate+"&isFill=" + isFill+"&onlyWatch="+onlyWatch+"&moduleType=${moduleType}&t="
					+ new Date().getTime(),
				type : "get",
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
		if (!treeNode || treeNode.nodeType == "ROOT") {
			return;
		}
		var aObj = $("#" + treeNode.tId + "_a");
		if (!aObj)
			return;
		if (treeNode.nodeType == "TASK") {
			var editStr = "<span id='diyBtn_" +treeNode.rid+ "' style='margin-left:30px'>"
					+ treeNode.params.dataDate + "</span>";
			aObj.after(editStr);
		} else {
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
					+ "' onclick='return false;' style='color:red; margin-left:0px;'>/未提交("
					+ treeNode.params.uncomplete + ")</span>";
			}
			aObj.after(editStr);
		}
	};
	tmp.addRightDiyDom =  function (treeId, treeNode) {
		if (!treeNode || !treeNode.params || treeNode.params.nodeType != "TSKRPTINS") {
			return;
		}
		var aObj = $("#" + treeNode.tId + "_a");
		if (!aObj) return;
		if(treeNode.params.sts == "0" || treeNode.params.sts == "10"){
			var editStr = "<a id='diyBtn2_"
				+ treeNode.id
				+ "' onclick=tmp.operFill('" + treeNode.params.sts + "','" + treeNode.params.rptId + "','" + treeNode.params.rptNm + "','" + treeNode.params.dataDate + "','" + treeNode.params.orgNo + "','" + $.trim(treeNode.params.orgNm) + "','" + treeNode.params.lineId + "','" + treeNode.params.logicRs + "','" + treeNode.params.sumpartRs + "','" + treeNode.params.warnRs + "','" + treeNode.params.zeroRs + "','" + treeNode.params.taskInsId + "','" + treeNode.params.tskType + "','"+treeNode.id+"','"+treeNode.params.templateType+"','"+treeNode.params.fixedLength+"','"+treeNode.params.isPaging+"') style='margin-left:10px;'>查看填报" + "</a>";
		}else{
			var editStr = "<a id='diyBtn2_"	+ treeNode.id
			+ "' onclick=tmp.operFill('" + treeNode.params.sts + "','" + treeNode.params.rptId + "','" + treeNode.params.rptNm + "','" + treeNode.params.dataDate + "','" + treeNode.params.orgNo + "','" + $.trim(treeNode.params.orgNm) + "','" + treeNode.params.lineId + "','" + treeNode.params.logicRs + "','" + treeNode.params.sumpartRs + "','" + treeNode.params.warnRs + "','" + treeNode.params.zeroRs + "','" + treeNode.params.taskInsId + "','" + treeNode.params.tskType + "','"+treeNode.id+"','"+treeNode.params.templateType+"','"+treeNode.params.fixedLength+"','"+treeNode.params.isPaging+"') style='margin-left:10px;'>查看填报" + "</a>"
		+"<a  onclick=tmp.processInfo('" + treeNode.params.orgNo + "','" + treeNode.params.taskInsId + "') style='margin-left:10px;color:#d088ffb'> 查看流程信息   </a>";
			
		}
		
		aObj.after(editStr);
	};
	tmp.zTreeOnClick = function (event, treeId, treeNode) {
		currentNode=treeNode;
		if (treeNode.nodeType == "TSKRPT") {
			tmp.loadRightTree(treeNode.params.taskid, treeNode.rid,treeNode.params.tskType,treeNode.params.dataDate,treeNode.params.isFill);
		}
	};
	tmp.zleftTreeOnClick = function (event, treeId, treeNode) {
		leftCurrentNode=treeNode;
		currentNode=treeNode;
		if (treeNode.nodeType == "TSKRPT") {
			tmp.loadRightTree(treeNode.params.taskid, treeNode.rid,treeNode.params.tskType,treeNode.params.dataDate,treeNode.params.isFill);
		}else if(treeNode.nodeType == "TASK"){
			tmp.leftTreeObj.expandNode(treeNode);
		}
	};
	tmp.zrigthTreeOnClick = function (event, treeId, treeNode) {
		rightCurrentNode=treeNode;
		if (treeNode.isParent == true) {
			tmp.rightTreeObj.expandNode(treeNode);
		}
	};
	tmp.setFontCss = function (treeId, treeNode) {
		var tmpSts = treeNode.params.sts;
		if(tmpSts !=null || tmpSts!=undefined){
			return (tmpSts == "0" || tmpSts == "10") ? {
				color : "red"
			}: (tmpSts == 9 ) ? {  //冻结, 暂未用
				color : "blue"
			}: {};
		}
	};
	tmp.resetInfo=function(){
		var treeNode=currentNode;
		tmp.loadRightTree(treeNode.params.taskid, treeNode.rid,treeNode.params.tskType,treeNode.params.dataDate,treeNode.params.isFill);
		this.leftTreeObj.reAsyncChildNodes(currentNode.getParentNode(), "refresh");
	};
	tmp.reAsyncChildNodes=function(type){
		if(type=="left"){
			if(leftCurrentNode!=null&&leftCurrentNode.getParentNode()){
				this.leftTreeObj.reAsyncChildNodes(leftCurrentNode.getParentNode(), "refresh");
			}
		}
		if(type=="right"){
			if(currentNode!=null){
				tmp.loadRightTree(currentNode.params.taskid, currentNode.rid,currentNode.params.tskType,currentNode.params.dataDate,currentNode.params.isFill);
			}
		}
	};
	tmp.processInfo = function (orgNo,taskInsId){
		var urlp = "${ctx}/rpt/frs/rptfill/getProcessId";
		var height = $(parent.parent.parent.window).height();
		var width = $(parent.parent.parent.window).width() + 50;
		
		var data = {
				orgNo : orgNo,
				taskInsId : taskInsId
		} 
		$.ajax({
			cache : false,
			async : true,
			url : urlp,
			dataType : "json",
			type : "post",
			data : data,
			beforeSend : function(){
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			success : function(result){
				if(result){
					processDefinitionId = result.processDefinitionId;
					processInstanceId = result.processInstanceId;
					//window.parent.parent.parent.BIONE.commonOpenDialog("查看流程", "processInfo", width/1.5, height/1.5, "${ctx}/rpt/frs/rptfill/processInfo?&processDefinitionId="+processDefinitionId +"&processInstanceId="+processInstanceId,null,tmp.closeTab);
					window.parent.parent.parent.BIONE.commonOpenDialog("查看流程", "processInfo", width/1.5, height/1.5, "${ctx}/activiti/processInfo?processDefinitionId="+processDefinitionId +"&processInstanceId="+processInstanceId,null,tmp.closeTab);
				}
			}
		});
	}
	
	tmp.closeTab = function(){
	    BIONE.hideLoading();
	}
	tmp.operFill = function (sts, rptId, rptNm, dataDate, orgNo, orgName, lineId, logicRs, sumpartRs, warnRs, zeroRs, taskInsId, taskType,treeNodeId, templateType, fixedLength, isPaging) {
		rightCurrentNode = this.rightTreeObj.getNodeByParam("id",treeNodeId);
		if("0"!= sts && "10"!= sts){ 
			//已归档报表
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/getTmpId",
				dataType : 'text',
				data : {
					rptId : rptId, dataDate : dataDate, lineId : lineId
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
				success : function(result){
					var tmpId = result;
					if(result){
 						BIONE.ajax({
 							async : false,
 							url : "${ctx}/rpt/frs/rptfill/createColor",
 							dataType : 'text',
 							type : 'POST',
 							data : {
 								rptId : rptId,
 								orgNo : orgNo,
 								dataDate : dataDate,
 								tmpId : result,
 								isBatchCheck : true
 							}
 						}, function(result) { 
 							var title = "报表信息";
 							var height = $(parent.parent.parent.window).height();
 							var width = $(parent.parent.parent.window).width() + 10;
 							var rptOperType = "03";
 							window.top.color=result;
 							window.parent.parent.parent.BIONE.commonOpenDialog(title, "taskViewWin", width, height, "${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-view&templateId=" + tmpId + "&dataDate=" + dataDate + "&orgNo=" + orgNo + "&rptId=" + rptId + "&logicRs=" + logicRs + "&sumpartRs=" + sumpartRs + "&warnRs=" + warnRs + "&taskInsId=" + taskInsId + "&lineId=" + lineId + "&rptNm=" + encodeURI(encodeURI(rptNm)) +  "&orgNm=" + encodeURI(encodeURI(orgName)) + "&taskId=" + taskIdForFill+"&taskType="+taskType+"&operType="+sts+"&rptOperType="+rptOperType+"&templateType="+templateType+"&fixedLength="+fixedLength+"&isPaging="+isPaging+"&d="+new Date().getTime(), null);
 						});
					}else{
						BIONE.tip("数据异常，请联系系统管理员");
					}
				},
				error:function(){
				}
			});
		}else{ 
			//未归档
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frs/rptfill/getTmpId",
				dataType : 'text',
				data : {
					rptId : rptId, 
					dataDate : dataDate, 
					lineId : lineId
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
				success : function(result){
					var tmpId = result;
					if(result){
 						BIONE.ajax({
 							async : false,
 							url : "${ctx}/rpt/frs/rptfill/createColor",
 							dataType : 'text',
 							type : 'POST',
 							data : {
 								rptId : rptId,
 								orgNo : orgNo,
 								dataDate : dataDate,
 								tmpId : tmpId,
 								isBatchCheck : true
 							}
 						}, function(result) {
 							var title = "报表信息";
 							var height = $(parent.parent.parent.window).height() - 25;
 							var width = $(parent.parent.parent.window).width() + 10;
 							window.top.color=result;
  							<%--reportDialog = window.parent.parent.parent.BIONE.commonOpenDialog(title, "taskFillWin", width, height, "${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-oper&templateId=" + tmpId + "&dataDate=" + dataDate + "&orgNo=" + orgNo + "&rptId=" + rptId + "&logicRs=" + logicRs + "&sumpartRs=" + sumpartRs + "&warnRs=" + warnRs + "&zeroRs=" + zeroRs + "&taskInsId=" + taskInsId + "&lineId=" + lineId + "&rptNm=" + encodeURI(encodeURI(rptNm))  + "&type=" + taskType + "&orgNm=" + encodeURI(encodeURI(orgName)) + "&taskId=" + taskIdForFill, null,function(){--%>
							reportDialog = window.parent.parent.parent.BIONE.commonOpenDialog(title, "taskFillWin", width, height, "${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-oper&templateId=" + tmpId + "&dataDate=" + dataDate + "&orgNo=" + orgNo + "&rptId=" + rptId + "&logicRs=" + logicRs + "&sumpartRs=" + sumpartRs + "&warnRs=" + warnRs + "&zeroRs=" + zeroRs + "&taskInsId=" + taskInsId + "&lineId=" + lineId + "&rptNm=" + encodeURI(encodeURI(rptNm))  + "&type=" + taskType + "&orgNm=" + encodeURI(encodeURI(orgName)) + "&taskId=" + taskIdForFill + "&templateType=" + templateType+"&fixedLength="+fixedLength+"&isPaging="+isPaging, null,function(){
                                    if(this.frame.tmp.isUpdateData()){
 									var cf = this.frame.tmp.isColse(this);
 									return cf;
 								}
 							});

						});
					}else{
						BIONE.tip("数据异常，请联系系统管理员");
					}
				},
				error:function(){
					BIONE.tip("数据异常，请联系系统管理员");
				}
			});
		}
	};
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<i class="fa fa-list-ul"></i>
	</div>
	<div id="template.right.up.icon">
		<i class = "fa fa-list-alt"></i>
	</div>
	<div id="template.left.up">任务列表</div>
	<div id="template.right.oper" style="padding-right: 10px; float: left; position: relative; height: 20px; margin-top: 8px">
		<%--<input id="onlyWatch" type="button"  value="只看关注"    onclick="onChangeWatch(1)"   />&nbsp;&nbsp;--%>
		<%--<input type="button" value="设置收藏" onclick="onResetWatch()" />--%>
			<!-- <div class="btn-group">
				<button id="onlyWatch" type="button" class="btn btn-default" onclick="onChangeWatch(1)"><i class="fa fa-star-o">只看关注</i></button>
				<button type="button" class="btn btn-default" onclick="onResetWatch()"><i class="fa fa-heart-o">设置收藏</i></button>
			</div> -->
	</div>
	<div id="template.right.up">任务报表详情</div>

</body>
</html>