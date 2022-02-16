<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var dialog;
	var grid;
	//var taskComBoxDate = '${taskComBoxDate}'?JSON2.parse('${taskComBoxDate}'):[];
	$(function() {
		//搜索框初始化
		$("#search").ligerForm({
			fields : [ {
				display : "任务类型",
				name : "taskType",
				newline : true,
				type : "select",
				cssClass : "field",
				comboboxName : "taskTypeBox",
				options : {
					url : "${ctx}/frs/rpttsk/monitor/listModuleType?d="+new Date(),
					onSelected : function(value, text) {
						if (value) {
							liger.get('taskNmBox').clear();
							$('#taskTypeBox').blur();
							BIONE.validator.element('#taskTypeBox');
						}
					}, cancelable  : true,dataFilter : true
				},
				attr : {
					op : "=",
					field : "taskType"
				}
			}, {
				display : "任务名称",
				name : "taskNm",
				comboboxName : 'taskNmBox',
				newline : false,
				type : "select",
				cssClass : "field",
				options : {
					onBeforeOpen : function() {
						var flg = BIONE.validator.element($('#taskTypeBox'));
						if(flg){
							var ty = $("#search input[name='taskType']").val()
							$.ajax({
								cache : false,
								async : false,
								url : "${ctx}/frs/rpttsk/monitor/getTaskComBoxDate",
								dataType : 'json',
								type : "get",
								data : {
									taskType:ty
								},
								success : function(result) {
									$.ligerui.get('taskNmBox').setData (result);
							    }
							});
							return true;
						}else{
							BIONE.tip("请选择任务类型!");
						}
						return false;
					}, cancelable  : true,dataFilter : true
				},
				attr : {
					op : "=",
					field : "taskNm"
				}
			},{
				display : "预校验状态",
				name : "preSts",
				newline : false,
				type : "select",
				cssClass : "field",
				labelWidth : '100',
				options : {
				    data : [{
						text : '校验未通过',
						id : "01"
				    },{
						text : '校验通过',
						id : '02'
				    }, {
						text : '无校验',
						id : '03'
					}]
				},
				attr : {
					op : "=",
					field : "preSts"
				}
			}]
		});
		// 监控列表
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '数据日期',
				name : 'dataDate',
				width : '10%',
				align : 'center'
			},{
				display : '任务类型',
				name : 'taskType',
				width : '15%',
				align : 'center',
				render :function(rowdata, index, value){
					if(value=="02"){
						return "1104监管";
					}else if(value=="01"){
						return "行内报送";
					}else if(value=="03"){
						return "人行大集中";
					}else if(value=="04"){
						return "east报送";
					}else if(value=="05"){
						return "利率报备";
					}else if(value=="06"){
						return "个人账户";
					}else if(value=="07"){
						return "存贷款利率";
					}else{
						return "未知类型";
					}
				}
			},{
				display:'任务名称',
				name:'taskNm',
				width:'25%',
				align:'left'
			}, {
				display : '预校验状态',
				name : 'preSts',
				width : '10%',
				align : 'center',
				render :function(rowdata, index, value){
					if(value=="01"){
						return "校验未通过";
					}else if(value=="02"){
						return "校验通过";
					}else if(value=="03"){
						return "无校验";
					}else{
						return "未知";
					}
				}
			}, {
				display : '生成状态',
				name : 'generateSts',
				width : '10%',
				align : 'center',
				render :function(rowdata, index, value){
					if(value=="01"){
						return "已生成";
					}else if(value=="02"){
						return "未生成";
					}else{
						return "未知";
					}
				}
			},{
				display : '生成时间',
				name : 'generateTime',
				width : '15%',
				align : 'center',
				type:"date",
				format:"yyyy-MM-dd hh:mm:ss"
			}, {
				display :  '查看明细',
				name : 'showDefail',
				width : '10%',
				align : 'center',
				render : function(rowdata, index, value){
					if(rowdata.preSts=="01"&&rowdata.generateSts=="02"){
						var inerHtml = '<a style="color: blue;" onClick="showDetail(\''+rowdata.taskId+'\',\''+rowdata.dataDate+'\',\''+rowdata.taskNm+'\')">查看明细</a>';
						return inerHtml;
					}else{
						return "----";
					}
					
				}
			} ],
			checkbox : true,
			rownumbers : true,
			isScroll : false,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/frs/rpttsk/monitor/getMonitorTskList",
			sortName : 'generateTime',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			width : '100%',
			height : '100%',
			toolbar : {}
		});
		grid.setHeight($("#center").height() - 115);
		var btns = [ {
			text : '任务回收',
			click : cellbackTask,
			icon : 'fa-repeat'
		}];

		BIONE.loadToolbar(grid, btns, function() {
		});

		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate('#formsearch');
	});
	

	//任务回收
	function cellbackTask() {
		var rows = grid.getSelectedRows();
		if (rows.length < 1) {
			BIONE.tip('请选择记录');
		} else if (rows.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			var taskId = rows[0].taskId;
			var taskType = rows[0].taskType;
			var dataDate = rows[0].dataDate
			dialog = BIONE.commonOpenDialog("任务回收", "callbackTskWin", 1000,$(window).height()-10,
					'${ctx}/frs/rpttsk/monitor/showCallbackTsk?taskId='+taskId+'&taskType='+taskType+'&dataDate='+dataDate+'&d='+new Date().getTime());
		}
	}
	
	function showDetail(taskId,dataDate,taskNm){
		dialog = BIONE.commonOpenDialog(taskNm, "tskTskDetailWin", 900,
				520,'${ctx}/frs/rpttsk/monitor/showTskDetail?taskId='+taskId+'&dataDate='+dataDate+'&d='+new Date().getTime());
	}
</script>
</head>
<body>
</body>
</html>