<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<style>
.indexStsA,.indexNmA{
     width:55%;
     cursor:pointer;
}
.stop{
    color:red;
}
</style>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid;
	var ids;
    var  taskName_  ='${taskName}';
	var taskType_='${taskType}';
	var taskSts_ = '${taskSts}';
	var canOper = '${canOper}';
	var catalogId_ ;
	var catalogName_ ;
	var isStopTrigger = false;
	var setDeployTask;
	var getDeployTask;

	$(function() {	
		initBaseInfo();
		initSearchForm();
		initGrid();
		initToolBar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		$("input[name='taskName']").val(taskName_);
		$("input[name='taskType']").val(taskType_);
		$("input[name='sts']").val(taskSts_);
		
		loadData();
	});
	
	function loadData(){
		
		grid.loadData();
		
	}
	
	function initBaseInfo(){

		var catalog = parent.currentNode;
		if(catalog==null || typeof catalog=="undefined")
		{
			catalogId_ = "ROOT";
			catalogName_ = "ROOT";
		}
		else{
			catalogId_ = catalog.id;
			catalogName_ = catalog.text;
		}
	}
	function initSearchForm() {
		//搜索表单应用ligerui样式
		$("#search").ligerForm({
			fields : [{
				display : '任务名称',
				name : "taskName",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'taskNm',
					op : "like"
				}
			}]
		});
	};
	function initGrid() {
		var  columnArr  =[{
			display : '任务名称',
			name : 'taskNm',
			width : "30%",
			align : 'left',
			render:function(row){
				return  "<a href='javascript:void(0)'   onclick='showTaskInfoSingle(\""+row.taskId+"\")'>"+row.taskNm+"</a>";
			}			
		},  {
			display : '任务类型',
			name : 'taskTypeNm',
			align : 'center',
			hide : "true"
		},  {
			display : '生效日期',
			name : 'effectDate',
			width : "10%",
			align : 'center'
		},  {
			display : '失效日期',
			name : 'invalidDate',
			width : "10%",
			align : 'center'
		},  {
			display : '是否启用',
			name : 'taskSts',
			width : "10%",
			align : 'center',
			render : function (rowData){
				if(rowData.taskSts=="0"){
					return "停用";
				}
				if(rowData.taskSts=="1"){
					return "启用";
				}
			}
		},  {
			display : '触发器状态',
			name : 'triggerType',
			width : "10%",
			align : 'center'
		},  {
			display : '最后修改时间',
			name : 'updateDate',
			width : "20%",
			align : 'center',
			type : 'date',
			format : 'yyyyMMdd hh:mm:ss'
		}];
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : true,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/rpt/input/task/getList.json?catalogId="+catalogId_ + "&type=grid",
			sortName : 'updateDate',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
            delayLoad:true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			width : '100%',	
			height : '99%',
			onBeforeCheckRow : function(){
				var checkedRows = grid.getCheckedRows();
				$.each(checkedRows, function(i, n) {
					grid.unselect(grid.getRow(n));
				});
			},
			onCheckRow : function(checked, rowdata, rowindex,rowDomElement){
				if(checked){
					checkObjs.push({taskId:rowdata.taskId,taskSts:rowdata.taskSts});
				}else if(!checked){
					var delIndex = -1;
					for(var i =0;i<checkObjs.length;i++){
						if(checkObjs[i]&&checkObjs[i].taskId == rowdata.taskId)
						{
							delIndex = i;
							break;
						}
					}
					delete checkObjs[delIndex];
				}
			}
		});
	};

	function datetimeFormat_1(longTypeDate){  
	    var datetimeType = "";  
	    var date = new Date();  
	    date.setTime(longTypeDate);  
	    datetimeType+= date.getFullYear();   //年  
	    datetimeType+= "-" + date.getMonth(date); //月   
	    datetimeType+= "-" + date.getDay(date);   //日  
	    datetimeType+= "&nbsp;&nbsp;" + date.getHours(date);   //时  
	    datetimeType+= ":" + date.getMinutes(date);      //分
	    datetimeType+= ":" + date.getSeconds(date);      //分
	    return datetimeType;
	} 
	
	function canDeployBtn(){
		for(var i = 0 ;i <checkObjs.length;i++){
			if(checkObjs[i]&&checkObjs[i].taskSts=="0"){
				return false;
			}
		}
		return true;
	}
	function initDeployBtn(){
		if(!canStopTrigger())
		{
			grid.toolbarManager.setDisabled("stopTrigger");

			if(!canDeployBtn())
				grid.toolbarManager.setDisabled("deployBtn");
			else grid.toolbarManager.setEnabled("deployBtn");
			isStopTrigger = false;
		}
		else {
			grid.toolbarManager.setEnabled("stopTrigger");
			grid.toolbarManager.setDisabled("deployBtn");
			isStopTrigger = true;
		}
		
	}
	

	function canStopTrigger(){
		var checkedTask = grid.getSelectedRows();
		if (checkedTask.length == 0) {
			return false;
		}else if(checkedTask.length ==1) {
			var taskObj = checkedTask[0];
			if(taskObj.triggerType=="1")
				return false;

			BIONE.ajax({
				type : "post",
				url : "${ctx}/rpt/input/task/canStopTrigger?d=" + new Date().getTime(),
				data : {
					taskId : taskObj.taskId,
					triggerId :　taskObj.triggerId
				}
			}, function(result) {
				if(result) {
					isStopTrigger = true;
				} else {
					isStopTrigger = false;
				}
			});
		}
	}
	
	var checkObjs = [];
	function initToolBar() {
		var toolBars = [];
// 		if(canOper=="1"){
			toolBars.push({
				text : '增加',
				click : f_open_add,
				operNo : 'taskAdd',
				icon : 'fa-plus'
			});
			toolBars.push({
				text : '修改',
				click : f_open_modify,
				operNo : 'taskAddMod',
				icon : 'icon-modify'
			});
			toolBars.push({
				text : '删除',
				click : f_delete,
				operNo : 'taskDel',
				icon : 'icon-delete'
			});
// 		}
		toolBars.push({
			text : '发布',
			operNo: "deployBtn",
			click : f_begindeploy,
			icon : 'icon-discuss'
		});
// 		if(canOper=="1"){
			toolBars.push({
				text : '停用/启用',
				click : isStop,
				operNo: "isStopBtn",
				icon : 'fa-power-off'
			
			});
			toolBars.push({
				text : '停止触发器下发',
				operNo: "stopTrigger",
				click : function(){
					
					var checkedTask = grid.getSelectedRows();
					if(checkedTask.length != 1) {
						BIONE.tip('请选择一条数据行进行操作');
						return;
					}
					if(checkedTask[0].triggerType != '启用中'){
						BIONE.tip('该任务不能停止触发器');
						return;
					}
					
					$.ligerDialog.confirm("确定停止触发器下发？",function(btn){
						if(btn){
							f_stopTrigger();
						}
					});
				},
				icon : 'icon-delete1'
			});
			toolBars.push({
				text : '流程管理',
				operNo: "flowOption",
				click : flowManager,
				icon : 'icon-shop'
			});
			toolBars.push({
				text : '历史发布',
				operNo: "showDeptInfo",
				click : showDeptInfo,
				icon : 'icon-time1'
			});
		grid.toolbarManager.setDisabled("stopTrigger");
		BIONE.loadToolbar(grid, toolBars, function() {
		});
	}
	
	function flowManager(){
		var options = {
				url : "${ctx}/rpt/input/task/flowManager",
				dialogname : "flowManager",
				title : "流程管理"
			};
		BIONE.commonOpenDialog(options.title, options.dialogname, 600, 450,
				options.url);
	}
	
	function f_stopTrigger(){
		var checkedTask = grid.getSelectedRows();
		if(checkedTask.length != 1) {
			BIONE.tip('请选择一条数据行进行操作');
			return;
		}
		var stopTask = checkedTask[0].taskId;
		var triggerId = checkedTask[0].triggerId;
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/task/stopTrigger?d=" + new Date().getTime(),
			dataType : 'json',
			type : "post",
			data : {
				taskId : stopTask,
				triggerId :　triggerId
			},
			success : function(result) {
				BIONE.tip('操作成功！');
				window.parent.refreshTree();
			},
			error : function(result, b) {
				BIONE.tip('触发器停止失败,发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function isStop(){
		var rows = grid.getSelectedRows();

		if (rows.length>0){
			var tskIds = [];
			for(var i =0;i<rows.length;i++){
					var rowData = rows[i];
					tskIds.push(rowData.taskId);
				}
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/input/task/changeTaskSts?d=" + new Date().getTime(),
				dataType : 'json',
				type : "post",
				data : {
					tskIds : tskIds.join(',')
				},
				success : function(result) {
					if(rowData.taskSts=="0"){
						BIONE.tip('任务启用成功!');
					}
					if(rowData.taskSts=="1"){
						BIONE.tip('任务停止成功!');
					}
					window.parent.refreshTree();
				},
				error : function(result, b) {
					BIONE.tip('任务下发失败,发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	}
	
	var deployTask ;
	function f_begindeploy(){
		if(!grid.toolbarManager.isEnable("deployBtn"))
			return ;
		var checkedTask = grid.getSelectedRows();
		if (checkedTask.length == 0) {
			BIONE.tip('请选择要发布的任务');
			return;
		}else if(checkedTask.length >1) {
			BIONE.tip('只能选择1个任务进行发布');
			return;
		}
		var rows = grid.getSelectedRows();
		var taskSts = rows[0].taskSts;
		if(taskSts==0){
			BIONE.tip('停用状态的任务不允许发布');
			return;
		}
		deployTask = checkedTask[0];
		//修改为如果是触发器类型则不需要选择数据日期直接下发
		showDeployDialog(deployTask.taskId, deployTask.exeObjNm);
	}
	function f_deploy(orgNos,dataDate){
		var data = {"taskId" : deployTask.taskId} ;
		if(orgNos)
			data.orgNos = JSON2.stringify(orgNos);
		if(dataDate)
			data.dataDate = dataDate;
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/task/deployTaskWithoutCheck?d=" + new Date().getTime(),
			dataType : 'json',
			type : "post",
			data : data,
			success : function(result) {
				BIONE.tip('发布成功');
				window.parent.refreshTree();
			},
			error : function(result, b) {
				BIONE.tip('任务下发失败,发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	//弹出窗口中选择触发器
	function showDeployDialog(taskId, exeObjNm) {
		var width = $(document).width()-50;
		var height = $(document).height()-30;
		var options = {
			url : "${ctx}/rpt/input/task/selectDeployDept?taskId="+taskId+"&exeObjNm=" + exeObjNm,
			dialogname : "selectDeployBox",
			title : "选择下发机构"
		};
		BIONE.commonOpenDialog(options.title, options.dialogname, width, height, options.url);
	}
	function f_open_add() {
		//校验根目录不能创建任务
		if(catalogId_ == "ROOT" || catalogId_ == '0'){
			BIONE.tip('根目录不能创建任务!');
			return;
		}
		var width = $(parent.document).width()*0.75;
		var height = $(parent.document).height()*0.95;
			dialog = window.parent.BIONE.commonOpenDialog("新建任务",
					"rptTaskBox",width, height,
					"${ctx}/rpt/input/task/taskInfoFrame?catalogId="
							+  encodeURI(encodeURI(catalogId_))+"&catalogName="+encodeURI(encodeURI(catalogName_))+"&canOper=1&d="+new Date().getTime() , null);
	}
	function showTaskInfoSingle(taskId){
		var width = $(parent.document).width()*0.75;
		var height = $(parent.document).height()*0.95;
		dialog = window.parent.BIONE.commonOpenDialog("修改任务",
				"rptTaskBox",width, height,
				"${ctx}/rpt/input/task/taskInfoFrame?taskId="+encodeURI(encodeURI(taskId))+"&canOper="+canOper+"&d="+new Date().getTime(), null);
	}
	
	function f_open_modify(){
		var checkedTask = grid.getSelectedRows();
		if (checkedTask.length == 0) {
			BIONE.tip('请选择要修改的行');
			return;
		}
		if (checkedTask.length > 1) {
			BIONE.tip('请选择一行进行修改');
			return;
		}
		showTaskInfoSingle(checkedTask[0].taskId);
	}
	
	function f_delete() {
		var checkedTask = grid.getSelectedRows();
		if (checkedTask.length == 0) {
			BIONE.tip('请选择要删除的行');
			return;
		}
		window.parent.$.ligerDialog.confirm('将进行删除操作，确实要删除这' + checkedTask.length + '条记录吗!',
				function(yes) {
					if (yes) {
						var ids = "";
						var length = checkedTask.length;
						for ( var i = 0; i < length; i++) {
							ids = ids + checkedTask[i].taskId +"@"+checkedTask[i].exeObjType+ ",";
						}
						$.ajax({
							type : "POST",
							url : '${ctx}/rpt/input/task/delTask/' + ids,
							dataType : 'json',
							success : function(result) {
								if (result == '1') {
									BIONE.tip("删除成功");
									refreshIt();
								} else if(result == '0'){
									BIONE.tip("所选任务中已有任务发布");
								}else {
									BIONE.tip("删除失败");
								}
							}
						});
					}
				});
	}
	function  refreshIt(){
		window.parent.refreshTree();
	}

	function showDeptInfo(){
		var selectedRows = grid.getSelectedRows();
		if(selectedRows.length != 1) {
			BIONE.tip('请选择一条数据行进行操作');
			return;
		}
		var taskId = selectedRows[0].taskId;
		var options = {
			url : "${ctx}/rpt/input/task/deptView?taskId="+taskId,
			dialogname : "flowManager",
			title : "流程管理"
		};
		BIONE.commonOpenFullDialog(options.title, options.dialogname, options.url);
	}
</script>
</head>
<body>

	<div id="template.right.down">
		<div id="aaa">
			<div id="maingrid" style="margin-top: 60px;"></div>
		</div>
	</div>
</body>
</html>