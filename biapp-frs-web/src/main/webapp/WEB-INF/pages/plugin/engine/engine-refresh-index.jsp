<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<meta name="decorator" content="/template/template1_BS.jsp">
	<!-- 基础的JS和CSS文件 -->
	<script type="text/javascript">
		var grid;
		var objTimer;
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
					if(id != 'off'){
						grid.reload();
						refreshTimer(id);
					}
				}
			});
			initBtmSts();
		});
		
		function initSearchForm() {
			$("#search").ligerForm({
				fields : [{
					display : '刷新类型',
					name : 'taskType',
					type : "select",
					comboboxName:"taskTypeBox",
					cssClass : "field",
					attr : {
						field : 'taskType',
						op : "="
					},
					options : {
						cancelable:true,
						url : "${ctx}/frs/frame/engineRefresh/taskTypeList.json"
					}
				} ,{
					display : '任务状态',
					name : 'sts',
					newline : false,
					type : "select",
					comboboxName:"stsBox",
					cssClass : "field",
					attr : {
						field : 'sts',
						op : "="
					},
					options : {
						cancelable: true,
						url : "${ctx}/report/frame/enginelog/rpt/exeStsList.json"
					}
				}]
			});
		};
		function initGrid() {
			grid = $("#maingrid").ligerGrid({
				toolbar : {},//加载ToolBar
				columns : [{
					display: "任务id",
					id:"taskNo",
					name:"taskNo",
					type: 'hidden',
					frozen:true
				},{
					display : '任务名称',
					name : 'taskNm',
					width : "15%",
					align : 'center',
					render:function (row,index,val){
						return "<a style='color:blue' onclick='f_open_view(\"" + row.taskNo + "\")'>" + val+ "</a>"
					}
				},{
					display: '刷新类型',
					name:'taskType',
					width: "15%",
					align: 'center',
					render:function (row){
						switch (row.taskType){
							case "RefreshNodeInfo":	return"节点刷新";
							case "RefreshDSCache":	return "数据源刷新";
							case "RefreshConfCache": return "配置刷新";
						}
					}
				},{
					display : '任务状态',
					name : 'sts',
					width : "15%",
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
				}, {
					display : '开始时间',
					name : 'startTime',
					width : "15%",
					align : 'center',
					type : "date",
					format : "yyyy-MM-dd hh:mm:ss"
				}, {
					display : '结束时间',
					name : 'endTime',
					width : "15%",
					align : 'center',
					type : "date",
					format : "yyyy-MM-dd hh:mm:ss"
				},{
					display: "父任务id",
					name:"parentTaskId",
					type: "hidden",
					frozen: true
				},{
					display: '运行日志',
					name:'runLog',
					width: "18%",
					align: 'center',
					type:"text"
				}],
				dataAction : 'server',
				usePager : true,
				alternatingRow : true,
				colDraggable : true,
				url : "${ctx}/frs/frame/engineRefresh/getEngineRefrList.json",
				sortName : 'startTime',
				sortOrder : 'desc',
				pageParmName : 'page',
				pagesizeParmName : 'pagesize',
				checkbox : true,
				rownumbers : true,
				width : '100%',
				height : '100%'
			});
		};
		function initToolBar() {
			var toolBars = [ {
				text : '节点刷新',
				icon : 'fa-refresh',
				click: function(){
					refreshFun('noderef');
				}
			},{
				text : '数据源刷新',
				icon : 'fa-refresh',
				click: function(){
					refreshFun('dsref');
				}
			},{
				text: '配置刷新',
				icon:'fa-refresh',
				click: function(){
					refreshFun('confref');
				}
			},{
				text: '删除',
				icon: 'fa-trash',
				click: f_delete
			}];
			BIONE.loadToolbar(grid, toolBars, function() {
			});
		}

		function initBtmSts(){
			var sts = queryEngineSts();
			if(sts == "exsi"){
				if($.ligerui.get("refresh").getValue() == "off" ||
						$.ligerui.get("refresh").getValue() == ""){
					$.ligerui.get("refresh").selectValue("10");
				}
			} else {
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
			/* if(queryEngineSts() == "unExsi"){
				window.clearInterval(objTimer);
				$.ligerui.get("refresh").selectValue("off");
			} */
		}

		//任务名称蓝字，可点开查看详情
		function f_open_view(taskNo){
			dialog = BIONE.commonOpenDialog("引擎刷新任务状态",
					"EngineRefreshDetail",800,500,
					"${ctx}/frs/frame/engineRefresh/detailLog?taskNo="
					+ taskNo, null);

		}

		//数据源、节点、配置刷新
		function refreshFun(refrType){
			//查询是否有正在刷新的任务
			$.ajax({
				dataType:"json",
				type:"post",
				async:false,
				url:"${ctx}/frs/frame/engineRefresh/getRefreshSts?refrType="+refrType,
				success : function(result) {
					if(result.flag) {
						$.ajax({
							dataType:"json",
							type:"post",
							async:false,
							url:"${ctx}/frs/frame/engineRefresh/refresh?refrType="+refrType,
							success : function(result) {
								if(refrType=="noderef") {
									grid.loadData();
								}else if(refrType=="dsref") {
									grid.loadData();
								}else {
									grid.loadData();
									///quickRefresh();//执行自动刷新
								}
							},
							beforeSend : function() {
								BIONE.loading = true;
								BIONE.showLoading("正在刷新...");
							},
							complete : function() {
								BIONE.loading = false;
								BIONE.hideLoading();
							},
							error : function(result, b) {
								BIONE.tip('刷新错误 <BR>错误码：' + result.status);
							}
						});
					}else {
						BIONE.tip(result.msg);
					}
				},
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在刷新...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				error : function(result, b) {
					BIONE.tip('刷新错误 <BR>错误码：' + result.status);
				}
			});
		}

		//跑数任务删除
		function f_delete() {
			var rows = grid.getSelectedRows();
			var params = [];
			if (rows.length > 0) {
				$.ligerDialog.confirm("确定删除这些任务吗？",
						function (flag) {
							if (flag) {
								for (var i = 0; i < rows.length; i++) {
									params.push(rows[i].taskNo);
									// if(rows[i].sts=="02"){
									// 	BIONE.tip("正在执行的任务不可以删除！");
									// 	return;
									// }
								}
								$.ajax({
									cache: false,
									async: false,
									url: "${ctx}/frs/frame/engineRefresh/deleteEngineRefresh",
									dataType: 'json',
									data: {
										taskNos: JSON.stringify(params)
									},
									type: "post",
									success: function (result) {
										if (result.status == 'success') {
											BIONE.tip("任务已删除");
										} else {
											BIONE.tip(result.msg);
										}
										grid.loadData();
									},
									error: function () {
										BIONE.tip("任务删除异常，请联系系统管理员");
									}
								});
							}
						});

			} else {
				BIONE.tip("请先选择需要删除的任务！");
			}
		}

		//发送指令后立马执行刷新,点击运行或重跑时调用,默认10秒刷新
		function quickRefresh(){
			window.clearInterval(objTimer);
			objTimer = window.setInterval("excuteRefresh()",10000);
		}


	</script>
</head>
<body>
</body>
</html>