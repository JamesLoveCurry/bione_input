<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<style type="text/css">
	.l-dialog-buttons {
		margin-top: 0;
		position: relative;
	}
</style>
<script type="text/javascript">
	var grid;
	var  moduleType= '${moduleType}';
	$(function() {
		//01 行内补录02 银监会1104报表 03 人行报表
		$("#search").ligerForm({
			fields : [ {
				display : "任务名称",
				name : "taskNm",
				comboboxName : 'taskNmBox',
				newline : false,
				type : "select",
				width : '140',
				cssClass : "field",
				options : {
					onBeforeOpen : function() {
							var ty = "${moduleType}";
							$.ajax({
								cache : false,
								async : false,
								url : '${ctx}/frs/rpttsk/publish/getTaskComBoxDate',
								dataType : 'json',
								type : "get",
								data : {
									taskType:ty
								},
								success : function(result) {
									$.ligerui.get('taskNmBox').setData (result);
							    }
							});
					}
				},  
				attr : {
					op : "=",
					field : "taskNm"
				}
			} , {
				display : "数据日期",
				name : "dataDate", 
				comboboxName : 'dataDateBox',
				newline : false,
				type : "date",
				width : '15%',
				cssClass : "field",
				labelWidth : '90',
			    attr : { op : "=", field : "i.data_date"},
			    options : { format : "yyyyMMdd"}
			}  ,  {
				display : "任务状态",
				name : "taskMgrSts",
				comboboxName : 'taskMgrStsBox',
				newline : false,
				type : "select",
				width : "140",
				cssClass : "field",
				options : {
				    initValue : '1',
				    data : [{
								text : '进行中',
								id : "1"
						    },  {
								text : '已完成',
								id : '0'
					    }]
				},
				attr : {
					op : "=",
					field : "taskMgrSts"
				}
			} ]
		});

		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '任务名称',
				name : 'taskNm',
				width : '30%',
				align : 'left'
			},{
				display : '数据日期',
				name : 'dataDate',
				width : '10%',
				align : 'left'
			},{
				display : '任务类型',
				name : 'taskType',
				width : '15%',
				align : 'center',
				render :function(value){
					var taskType = value.taskType;
					if(taskType=="02"){
						return "1104监管";
					}else if(taskType=="01"){
						return "行内报送";
					}else if(taskType=="03"){
						return "人行大集中";
					}else if(taskType=="05"){
						return "利率报备";
					}else if(taskType=="06"){
						return "支付报送";
					}else{
						return "其他类型"
					}
				}
			},{
				display : '任务状态',
				name : 'taskSts',
				width : '15%',
				align : 'center',
				render :function(value){
					var sts = value.taskMgrSts;
					if(sts=="1"){
						return "进行中";
					}else if(sts=="0"){
						return "已完成";
					}else{
						return "未知类型"
					} 
				}
			},{
				display : '任务处理信息',
				name : 'detail',
				width : '25%',
				align : 'center',
				render : function(value){
					return "<a href='javascript:void(0)' class= 'link' onclick = 'taskDetail(\""+value.dataDate+"\",\""+value.taskId+"\")'>"+ "任务处理信息" + "</a>"; 
				}
			}],
			checkbox : true,
			rownumbers : true,
			isScroll : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
// 			url : '${ctx}/report/frs/rpttsk/getTskInfoList?moduleType=${moduleType}',
			url : '${ctx}/frs/rpttsk/publish/getTskInfoList',
			parms : {moduleType : '${moduleType}',isManager:'1'},
			sortName : 'taskNm',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			width : '100%',
			height : '99%',
			toolbar : {}
		});

		var btns = [{
			text : '结束任务',
			click : finish,
			icon : 'fa-hourglass-end'
		},{
			text : '全部列表导出',
			click : expor,
			icon : 'fa-download'
		},{
			text : '重新打开',
			click : reEdit,
			icon : 'fa-hourglass-start'
		},{
			text : '删除任务',
			click : deleteTsk,
			icon : 'fa-hourglass-end'
		}];

		BIONE.loadToolbar(grid, btns, function() {
		});

		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate('#formsearch');
	});
	
	//结束任务
	function finish(){
	    var rows = $("#maingrid").ligerGetGridManager().getSelectedRows();
		if(rows.length==1){
			var dataDate = rows[0].dataDate;
			var taskId = rows[0].taskId;
			var mgrSts = '0';
			var taskMgrSts = rows[0].taskMgrSts;
			if(taskMgrSts==1){
 			 $.ajax({
		     	 cache : false, async : true,
		      	 url : "${ctx}/frs/rpttsk/publish/checkTaskSts",
		     	 dataType : 'json',
		     	 data : { dataDate : dataDate, taskId : taskId , moduleType : moduleType},//异步
		     	 type : "post",
		     	 success : function(result){ 
		     		 if(result){
		     			 $.ajax({
		    		     	 cache : false, async : true,
		    		      	 url : "${ctx}/frs/rpttsk/publish/updateTaskMgrSts",
		    		     	 dataType : 'json',
		    		     	 data : { dataDate : dataDate, taskId : taskId , moduleType : moduleType , mgrSts : mgrSts},//异步
		    		     	 type : "post",
		    		     	 success : function(result){
		    		            grid.loadData();
		    		     		BIONE.tip("操作已完成");
		    		     	 },
		    		    	 error:function(){
		    		    	  	BIONE.tip("结束任务失败，请联系管理员");
		    		    	  }
		    		     });
		     		 }else if(!result){
		     			$.ligerDialog.confirm('当前仍有机构未完成填报流程，是否确认结束任务?', function(yes) {
		    				if(yes) {
				     			 $.ajax({
				    		     	 cache : false, async : true,
				    		      	 url : "${ctx}/frs/rpttsk/publish/updateTaskMgrSts",
				    		     	 dataType : 'json',
				    		     	 data : { dataDate : dataDate, taskId : taskId , moduleType : moduleType , mgrSts : mgrSts},//异步
				    		     	 type : "post",
				    		     	 success : function(result){ 
				    		     		grid.loadData();
				    		     		BIONE.tip("操作已完成");
				    		     	 },
				    		    	 error:function(){
				    		    	  	BIONE.tip("结束任务失败，请联系管理员");
				    		    	  	}
				    		     });
		    				}
		    			});
		     		 }
		     	 },
		    	 error:function(){
		    	  	BIONE.tip("任务状态信息异常，请联系系统管理员");
		    	  	}
		    	 });
			}else{
		 		 BIONE.tip("当前任务已是结束状态");
		 	}
		 }else{
		     BIONE.tip("请选择一条记录");
		 }
	}
	
	//全部列表导出
	function expor(){
		var download=null;
		download = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(download);
		var data = queryList();
		var condition = data.condition;
		var src = "${ctx}/frs/rpttsk/publish/flTaskList?moduleType="+moduleType+"&condition="+encodeURI(encodeURI(condition));
		download.attr('src', src);
	}
	
	function queryList(){
		var form = $('#formsearch');
		var data = {};
	  	if("" != $.ligerui.get("taskNmBox").getValue()){
			data.taskNmBox=$.ligerui.get("taskNmBox").getValue();
		}
		if("" != $.ligerui.get("dataDate").getValue()){
			data.dataDate=$.ligerui.get("dataDate").getValue();
	  	}
	  	
		var rule = BIONE.bulidFilterGroup(form);
		if (rule.rules.length) {
			data.condition=JSON2.stringify(rule);
		}else{
			data.condition="";
		}
   		return data; 
	}
	//重新打开
	function reEdit(){
	    var rows = $("#maingrid").ligerGetGridManager().getSelectedRows();
		if(rows.length==1){
			var dataDate = rows[0].dataDate;
			var taskId = rows[0].taskId;
			var mgrSts = '1';
			var taskMgrSts = rows[0].taskMgrSts;
			if(taskMgrSts==0){
 				 $.ajax({
		     	 	cache : false, async : true,
   		      		url : "${ctx}/frs/rpttsk/publish/updateTaskMgrSts",
		     	 	dataType : 'json',
		     	 	data : { dataDate : dataDate, taskId : taskId , moduleType : moduleType, mgrSts : mgrSts},
		     	 	type : "post",
		     	 	success : function(result){
    		     		grid.loadData();
    		     		BIONE.tip("任务已重新打开");
		     		 },
		    		 error:function(){
		    	 	 	BIONE.tip("任务打开异常，请联系系统管理员");
		    	 	 }
		    		});
		 	}else{
		 		 BIONE.tip("当前任务已是打开状态");
		 	}
		}else{
		    BIONE.tip("请选择一条记录");
		}
	}
	
	function taskDetail(dataDate,taskId){
		var src = "${ctx}/frs/rpttsk/publish/taskDealDetail?taskId="+taskId+"&dataDate="+dataDate+"&moduleType="+moduleType;
		BIONE.commonOpenDialog("任务处理信息", "taskDetail", $("#center").width(),$("#center").height(), src);
	}
	
	//删除任务-批量删除
	function deleteTsk(){
	    var rows = $("#maingrid").ligerGetGridManager().getSelectedRows();
	    var params = [];
		if(rows.length > 0){
			$.ligerDialog.confirm('您确认要删除任务吗?', function(yes) {
				if(yes) {
					for(var i=0; i<rows.length; i++){
						var param = {
							"dataDate" : rows[i].dataDate,
							"taskId" : rows[i].taskId,
							"moduleType" : moduleType
						};
						params.push(param);
					}
					$.ajax({
						cache : false, async : true,
						url : "${ctx}/frs/rpttsk/publish/deleteTask",
						dataType : 'json',
						data : { 
							params : JSON.stringify(params)
						},
						type : "post",
						success : function(result){
							if(result){
								grid.loadData();
								BIONE.tip("任务已删除");
							}else{
								BIONE.tip("任务信息缺失，删除失败！");
							}
						},
						error:function(){
							BIONE.tip("任务删除异常，请联系系统管理员");
						}
		    		});
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