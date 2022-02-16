<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid;
	var objTimer;
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : '数据日期',
				name : "dataDate",
				id : "dataDate",
				newline : true,
				labelWidth : 100,
				width : 220,
				space : 30,
				type : "date",
				cssClass : "field",
				options : {
					format : "yyyyMMdd"
				},
				attr : {
					field : 't.dataDate',
					op : "="
				}
			}, {
				display : '报表名称',
				name : "rptNm",
				newline : false,
				labelWidth : 100,
				width : 220,
				space : 30,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'taskNm',
					op : "like"
				}
			},{
				display : '任务状态',
				name : 'sts',
				newline : true,
				labelWidth : 100,
				width : 220,
				space : 30,
				type : "select",
				comboboxName:"exe_sts_box",
				cssClass : "field",
				attr : {
					field : 'sts',
					op : "="
				},
				options : {
					url : "${ctx}/report/frame/enginelog/rpt/exeStsList.json"
				}
			} ]
		});
	};
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			columns : [{
				display : '报表名称',
				name : 'taskNm',
				width : "25%",
				align : 'center',
				render : function(row,index,val){
					return "<a style='color:blue' onclick='f_open_view(\"" + row.id.taskNo + "\",\"" 
							+ row.id.dataDate + "\",\"" + row.id.instanceId + "\")'>" + val + "</a>"
				}
			},{
				display : '数据日期',
				name : 'id.dataDate',
				width : "15%",
				align : 'center'
			}, {
				display : '任务状态',
				name : 'sts',
				width : "10%",
				align : 'center',
				render:function(row){
					switch(row.sts){
						case  "01":return "等待执行";
						case  "02":return "正在执行";
						case  "03":return "执行成功";
						case  "04":return "执行失败";
						case  "07":return "手动停止";
						case  "08":return "超时停止";
						default  :return "未知";
			        }
				}
			},  {
				display : '开始时间',
				name : 'startTime',
				width : "13%",
				align : 'center',
				type : "date",
				format : "yyyy-MM-dd hh:mm:ss"
			}, {
				display : '结束时间',
				name : 'endTime',
				width : "14%",
				align : 'center',
				type : "date",
				format : "yyyy-MM-dd hh:mm:ss"
			}, {
				display : '操作',
				name : 'oper',
				width : "10%",
				align : 'center',
				render : function(row,index,val){
					if(row.sts == "01" || row.sts == "02"){
						return "<a style='color:blue' onclick='f_stop(\""+row.id.taskNo+"\")'>停止</a>";
					}
					if(row.sts == "03" || row.sts == "04" || row.sts == "07" || row.sts == "08"){
						return "<a style='color:blue' onclick='f_redo(\""+row.id.instanceId+"\",\""+row.id.dataDate+"\",\""+row.taskType+"\")'>重跑</a>";
					}
				}
			}],
			dataAction : 'server', 
			usePager : true, 
			alternatingRow : true,
			colDraggable : true,
			url : "${ctx}/report/frame/enginelog/rpt/getEngineRptStsList.json",
			sortName : 'startTime',
			sortOrder : 'desc', 
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			checkbox : true,
			rownumbers : true,
			width : '100%',
			height : '99%'
		});
	};
	function initToolBar() {
		var toolBars = [ {
			text : '增加',
			click : f_open_add,
			icon : 'fa-plus'
		},{
			text : '删除',
			click : f_open_delete,
			icon : 'fa-trash-o'
		}];
		BIONE.loadToolbar(grid, toolBars, function() {
		});
	}
	$(function() {	
		initSearchForm();
		initGrid();
		initToolBar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		$(".l-panel-bbar-inner").find(".l-clear").remove();
		var btmInnerHtml = '<div  style="float:left;margin-right:1px;position: relative;top:-2px;"><span>刷新间隔：</span></div><div class="l-bar-group"><div><a><input id="refresh"></a></div></div><div class="l-bar-separator"></div><div class="l-clear"></div>';
		$(".l-panel-bbar-inner").append(btmInnerHtml);
		//加载底部样式
		$("#refresh").ligerComboBox({
			cancelable : false,
			width : 70,
			data:[{
				text : "10秒",
				id : "10"
			},{
				text : "20秒",
				id : "20"
			},{
				text : "30秒",
				id : "30"
			},{
				text : "60秒",
				id : "60"
			},{
				text : "不刷新",
				id : "off"
			}],
			onSelected : function(id,value){
			    if(id!="off") {
                    grid.reload();
                    refreshTimer(id);
                }
			}
		});
		  initBtmSts();
	});
	
	function initBtmSts(){
		var sts = queryEngineSts();
		if(sts == "exsi"){
			if($.ligerui.get("refresh").getValue() == "off" ||
				$.ligerui.get("refresh").getValue() == ""){
				$.ligerui.get("refresh").selectValue("10");
			}
		}
		else{
			$.ligerui.get("refresh").selectValue("off");
		}
	}
	
	//查询引擎状态
	function queryEngineSts(){
		var sts;
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/enginelog/rpt/getEngineRptPendingSts?d="+new Date().getTime(),
			dataType : 'json',
			type : "GET",
			success : function(result) {
				sts = result.msg;
			}
		});
		return sts;
	}
	
	//定时器
	function refreshTimer(sec){
		if(sec == 'off' && objTimer){
			window.clearInterval(objTimer);
		}else{
			if(sec != ""){
				objTimer = window.setInterval("excuteRefresh()",sec*1000);
			}
		}
	}
	//执行刷新
 	function excuteRefresh(){
 		grid.reload();
 		/*if(queryEngineSts() == "unExsi"){
 			window.clearInterval(objTimer);
 			$.ligerui.get("refresh").selectValue("off");
 		}*/
	}
	//改刷新状态,点击运行或重跑时调用
	function changeRefreshSts(){
		window.clearInterval(objTimer);
		$.ligerui.get("refresh").selectValue("10");
	}
	//发送指令后立马执行刷新,点击运行或重跑时调用,默认10秒刷新
	function quickRefresh(){
		window.clearInterval(objTimer);
		objTimer = window.setInterval("excuteRefresh()",10000);
	}
	
	function f_open_add() {
		dialog = BIONE.commonOpenDialog("新建报表引擎信息",
				"rptEngineBatchBox",800,500,
				"${ctx}/report/frame/enginelog/rpt/newRptEngineLog", null);
	}
	
	function f_stop(taskNo){
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/enginelog/rpt/stopTask",
			dataType : 'json',
			data:{
				taskNo : taskNo
			},
			type : "post",
			success : function(result) {
				if(result.msg){
					initBtmSts();
					BIONE.tip(reuslt.msg);
				}
				else{
					grid.loadData();
					BIONE.tip("任务已停止");
					
				}
			}
		});
	}
	
	function f_redo(instanceId,dataDate,taskType){
		//查询报表状态是否是提交过
		 $.ajax({
			 async : false,//同步
			 type : "POST",
			 url : "${ctx}/report/frame/enginelog/rpt/getRptSts",
			 dataType : 'json',
			 data : {
				 checkedRptIds: instanceId,
				 dataDate: dataDate
			 },
			 success: function (result) {
			 	if(result.status){
					$.ligerDialog.confirm(result.msg, function (yes){
						if(yes){
							redoTask(instanceId,dataDate,taskType);
						}
					});
				}else{
					redoTask(instanceId,dataDate,taskType);
				}
			 }
		 });
	}
	
	function redoTask(instanceId,dataDate,taskType){
		changeRefreshSts();//当点击重跑，立马显示10秒自动刷新
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/enginelog/rpt/redoTask",
			dataType : 'json',
			data:{
				instanceId : instanceId,
				dataDate : dataDate,
				taskType : taskType
			},
			type : "post",
			success : function(result) {
				if(result.msg){
					quickRefresh();//指令发送完成后立即执行自动刷新
					BIONE.tip(reuslt.msg);
				}
				else{
					grid.loadData();
					BIONE.tip("任务已重跑");
					
				}
			}
		});
	}

	function  f_open_view(taskNo, dataDate, instanceId) {
		dialog = BIONE.commonOpenDialog("报表任务状态",
				"rptEngineDetail",800,500,
				"${ctx}/report/frame/enginelog/rpt/detailLog?taskNo=" 
						+ taskNo + "&dataDate=" + dataDate + "&instanceId=" + instanceId, null);
	}
	
	//跑数任务删除
	function f_open_delete(){
	    var rows = grid.getSelectedRows();
	    var params = [];
		if(rows.length > 0){
			for(var i=0; i<rows.length; i++){
				params.push(rows[i].id.taskNo);
			}
			$.ajax({
				cache : false, 
				async : false,
				url : "${ctx}/report/frame/enginelog/rpt/deleteInstance",
				dataType : 'json',
				data : { 
					taskNos : JSON.stringify(params)
				},
				type : "post",
				success : function(result){
					if(result){
						grid.loadData();
						BIONE.tip("任务已删除");
					}else{
						BIONE.tip("任务删除失败！");
					}
				},
				error:function(){
					BIONE.tip("任务删除异常，请联系系统管理员");
				}
    		});
			 
		}else{
		    BIONE.tip("至少选择一条记录！");
		}
	}
</script>
</head>
<body>
</body>
</html>