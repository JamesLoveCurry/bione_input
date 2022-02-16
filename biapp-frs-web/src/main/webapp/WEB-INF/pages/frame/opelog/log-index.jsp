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

	$(function() {
		initSearchForm();
		initGrid();
		initButtons();
		loadData();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	function loadData(){
		grid.loadData();
	}
	
	function initSearchForm() {
		//搜索表单应用ligerui样式
		$("#search").ligerForm({
			fields : [{
				display : '功能名称',
				name : "moduleNm",
				newline : true,
				type : "text",
				width : '140',
				cssClass : "field",
				attr : {
					field : 'moduleNm',
					op : "="
				}
			},{
				display : '资源类型',
				name : 'resNo',
				newline : false,
				type : "select",
				width : '140',
				comboboxName:"resNoBox",
				cssClass : "field",
				attr : {
					field : 'resNo',
					op : "="
				},
				options : {
					url : "${ctx}/bione/syslog/opelog/getRes"
				}
			},{
				display : '操作类型',
				name : 'operType',
				newline : false,
				type : "select",
				width : '140',
				comboboxName:"operTypeBox",
				cssClass : "field",
				attr : {
					field : 'operType',
					op : "="
				},
				options : {
				    data : [{
							text : "新增",
							id : "01"
					    }, {
							text : "删除",
							id : "02"
					    }, {
							text : "修改",
							id : "03"
					    },{
							text : "查询",
							id : "04"
					    }]
				}
			},{
				display : '操作人',
				name : "operUserNm",
				newline : false,
				type : "text",
				width : '140',
				cssClass : "field",
				attr : {
					field : 'operUserNm',
					op : "="
				}
			},{
				display : '资源名称',
				name : "resNm",
				newline : true,
				type : "text",
				width : '140',
				cssClass : "field",
				attr : {
					field : 'resNm',
					op : "="
				}
			},{
				display : '内容',
				name : "logContent",
				newline : false,
				type : "text",
				width : '140',
				cssClass : "field",
				attr : {
					field : 'logContent',
					op : "="
				}
			},{
				display : '操作时间',
				name : "operTime",
				newline : false,
				type : "date",
				width : '140',
				cssClass : "field",
				attr : {
					field : 'operTime',
					op : "="
				}
			}]
		});
	}
	function initGrid() {
		var url = "${ctx}/bione/syslog/opelog/getList?d="+new Date();
		var  columnArr  =[{
			display : '功能名称',
			name : 'moduleNm',
			width : "10%",
			align : 'center'
		},  {
			display : '资源类型',
			name : 'resNo',
			width : "10%",
			align : 'center',
			render : function(data,dt_num,value){
				var showText = "";
				showText = getResNm(value);
				return showText;
			}
		},  {
			display : '操作类型',
			name : 'operType',
			width : "10%",
			align : 'center',
			render : function(data,dt_num,value){
				var showText = "其他";
				showText = getOperNm(value);
				return showText;
			}
		},  {
			display : '操作人',
			name : 'operUserNm',
			width : "10%",
			align : 'center'
		},  {
			display : '资源名称',
			name : 'resNm',
			width : "15%",
			align : 'center'
		},  {
			display : '操作时间',
			name : 'operTime',
			width : "15%",
			align : 'center',
			render : function(data){
				if(data.operTime&&data.operTime!=""){
					return BIONE.getFormatDate(data.operTime, "yyyy-MM-dd hh:mm:ss");
				}else{
					return "";
				}					
			},
		},  {
			display : '内容',
			name : 'logContent',
			width : "35%",
			align : 'center'
		}];
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : true,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : url,
			sortName : 'oper_time',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
            delayLoad:true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			width : '100%',
			height : '99%',
			isScroll : true
		});
	}
	
	//初始化按钮
	function initButtons() {
		var btns = [{
			text : '删除',
			click : deleteLog,
			icon : 'fa-trash-o'
		},{
			text : '全部删除',
			click : deleteAll,
			icon : 'fa-trash'
		}];
		BIONE.loadToolbar(grid, btns);
	}
	
	//删除日志
	function deleteLog(){
		var rows = grid.getSelectedRows();
		if(rows.length>0){
			 $.ligerDialog.confirm('您确定删除这' + rows.length + "条日志吗？", function(yes) {
					var logIds = [];
					for(var i =0;i<rows.length;i++){
						var rowData = rows[i];
						logIds.push(rowData.logId);
					}
					if (yes) {
						$.ajax({
							async : false,
							type : "get",
							dataType : "json",
							url : '${ctx}/bione/syslog/opelog/deleteLog?logIds=' + logIds.join(';'),
						    success : function(result) {
						    	if(result && result.deleteNo == "ok"){
						        	BIONE.tip('删除成功');
									grid.loadData();
						    	}
						    },
							    error : function(result, b) {
								BIONE.tip('删除错误 <BR>错误码：' + result.status);
						    }
						});
						
					}
				});	
		}else{
			BIONE.tip('请至少选择一条记录');
		}
	}
	
	//全部删除
	function deleteAll(){
		 $.ligerDialog.confirm('您确定删除全部日志吗？', function(yes) {
				if (yes) {
					$.ajax({
						async : false,
						type : "get",
						dataType : "json",
						url : '${ctx}/bione/syslog/opelog/deleteAll',
					    success : function(result) {
					    	if(result && result.deleteNo == "ok"){
					        	BIONE.tip('删除成功');
								grid.loadData();
					    	}
					    },
						    error : function(result, b) {
							BIONE.tip('删除错误 <BR>错误码：' + result.status);
					    }
					});
					
				}
			});	
	}
	
	//根据资源no获取资源名称
	function getResNm(resNo){
		var resNm = "其他资源";
		if(resNo){
			if(resNo=="OPER_RES_RPT")
				resNm="报表资源";
			else if(resNo=="OPER_RES_IDX")
				resNm="指标资源";
			else if(resNo=="OPER_RES_DIM")
				resNm="维度资源";
			else if(resNo=="OPER_RES_TAKE")
				resNm="任务资源";
			else if(resNo=="OPER_RES_MODEL")
				resNm="模型资源";
		}
		return resNm;
	}
	
	//根据操作类型获取操作名称
	function getOperNm(operType){
		var operNm = "其他";
		if(operType){
			if(operType=="01")
				operNm="新增";
			else if(operType=="02")
				operNm="删除";
			else if(operType=="03")
				operNm="修改";
			else if(operType=="04")
				operNm="查询";
		}
		return operNm;
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