<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template8.jsp">
<script type="text/javascript">var ctx="${ctx}"</script>
<script type="text/javascript" src="${ctx}/js/frs/rptfill/TaskFill.js"></script>
<script type="text/javascript">
	var grid;
	var columns = ["sts", "dataDate", "exeObjId", "exeObjNm", "taskNm", "taskObjNm", "endTime", "isUpt", "sumpartRs", "logicRs", "warnRs"];
	$(function() {
		if("${type}" == "03"){
			//初始化grid
			TaskFillGrid("${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=taskChildInsList&_event=POST&_comp=main&Request-from=dhtmlx&taskIns=${taskIns}&orgNo=${orgNo}&rptId=${rptId}&dataDate=${dataDate}&type=${type}" , columns, false,null,true);
		}else{
			//初始化grid
			TaskFillGrid("${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=taskChildIns&_event=POST&_comp=main&Request-from=dhtmlx&instanceIds=${ins}", columns, false,null,true);
		}
		//初始化按钮
		initButtons();
	});
	//初始化按钮
	function initButtons() {
		var btns;
		if("${type}" == "03"){
			btns = [ { text : '强制结束填报', click : submit, icon : 'up', operNo : 'oper_submit'}];
		}else{
			btns = [ { text : '强制提交', click : submit, icon : 'up', operNo : 'oper_submit'}];
		}
		BIONE.loadToolbar(grid, btns, function() {});
	}
	//强制提交
	function submit(){
		var url;
		var submitTaskUrl;
		if("${type}" == "03"){
			url = "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=forceSubmitList&_event=POST&_comp=main&Request-from=dhtmlx&taskIns=${taskIns}&orgNo=${orgNo}&rptId=${rptId}&dataDate=${dataDate}&type=${type}&sts=2";
			submitTaskUrl = "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=submitTaskBatchList&_event=POST&_comp=main&Request-from=dhtmlx&taskIns=${taskIns}&sts=2";
		}else{
			url = "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=forceSubmit&_event=POST&_comp=main&Request-from=dhtmlx&instanceIds=${ins}";
			submitTaskUrl = "${ctx}/report/frs/rptfill/rptFillTabController.mo?_type=data_event&_field=submitTaskBatch&_event=POST&_comp=main&Request-from=dhtmlx";
		}
		$.ajax({
			async : false,
			type : "post",
			dataType : "json",
			url : url,
		    success : function() {
		    	$.ajax({
					cache : false,
					async : true,
					data :{
						taskInsIds:'${taskInsIds}'
					},
					url : submitTaskUrl,
					dataType : 'json',
					type : "get",
					success : function(){
						if("${type}" == "03"){
							BIONE.tip("强制结束填报成功");
						}else{
							BIONE.tip("强制提交成功");
						}
						if(parent.parent.child.grid != undefined){
							parent.parent.child.grid.loadData();
						}
						if(parent.parent.child.tmp.resetInfo != undefined){
							parent.parent.child.tmp.resetInfo();
						}
// 				    	BIONE.closeDialog("taskInsChildWin");
				    	parent.BIONE.closeDialog("taskFillWin");
					},
					error:function(){
						BIONE.tip("数据异常，请联系系统管理员");
					}
				});
		    },error : function(result, b) {
		    	BIONE.tip("数据异常，请联系系统管理员");
		    }
		});
	}
</script>
</head>
<body>
</body>
</html>