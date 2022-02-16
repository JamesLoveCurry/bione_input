<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid;
	$(function() {
		initSearchForm();
		initGrid();
		BIONE.loadToolbar(grid, [{
			text : '新增',
			icon : 'fa-plus',
			menu : {
				items : [{
					text : '指标组',
					click : function(){
						addValid("02");
					},
					icon : 'fa-plus'
				},{
					text : '报表组',
					click : function(){
						addValid("05");
					},
					icon : 'fa-plus'
				}]
			}
		},{
			text : '修改',
			click : modify,
			icon : 'fa-pencil-square-o'
		},{
			text : '删除',
			click : remove,
			icon : 'fa-trash-o'
		},{
			icon:'fa-wrench',
			click : config,
			text:'配置'
		},{
			icon:'fa-cogs',
			click : check,
			text:'校验'
		}]);
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	function check(){
		var rows = grid.getSelectedRows();
		if(rows.length == 0){
			BIONE.tip("请选择一条记录");
			return;
		}
		if(rows.length > 1){
			BIONE.tip("只能选择一条记录");
			return;
		}
		
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/validgroup/getValidTypeTabs.json?d="
				+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			success : function(result) {
				var data = null;
				if (result) {
					data = result.data;
				}
				if (data && data.length > 0) {
					BIONE.commonOpenDialog("校验","checkDialog",580,350,
							"${ctx}/report/frame/validgroup/check?grpType="+rows[0].groupType+"&groupId="+rows[0].groupId);
				}else{
					BIONE.tip("无授权校验类型，无法校验！");
				}
			}
		})
	}
	
	function addValid(type){
		BIONE.commonOpenDialog("新增校验组","validDialog",580,300,
				"${ctx}/report/frame/validgroup/new?grpType="+type);
	}
	
	function modify(){
		var rows = grid.getSelectedRows();
		if(rows.length == 0){
			BIONE.tip("请选择一条记录");
			return;
		}
		if(rows.length > 1){
			BIONE.tip("只能选择一条记录");
			return;
		}
		BIONE.commonOpenDialog("修改校验组","validDialog",580,300,
		"${ctx}/report/frame/validgroup/new?grpType="+rows[0].groupType+"&groupId="+rows[0].groupId);
	}
	
	function remove(){
		var rows = grid.getSelectedRows();
		if(rows.length == 0){
			BIONE.tip("请至少选择一条记录");
			return;
		}
		var ids = [];
		for(var i in rows){
			ids.push(rows[i].groupId);
		}
		$.ligerDialog.confirm('您确定删除选中记录么？', function(yes) {
			if (yes) {
				$.ajax({
				    url:"${ctx}/report/frame/validgroup/delValidGrp?ids="+ids.join(","),  //请求的url地址
				    dataType:"json",   //返回格式为json
				    async:false,
				    type:"post",   //请求方式
				    success:function(data){
				    	if(data.msg == "ok"){
				    		grid.reload();
				    		BIONE.tip('删除成功');
				    	}else{
				    		BIONE.tip('删除失败');
				    	}
				    },
				    error:function(){
						BIONE.tip('请求出错');
				    }
				});
			}
		});
	}
	
	function config(){
		var rows = grid.getSelectedRows();
		if(rows.length == 0){
			BIONE.tip("请选择一条记录！");
			return;
		}
		if(rows.length > 1){
			BIONE.tip("只能选择一条记录！");
			return;
		}
		
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/validgroup/getValidTypeTabs.json?d="
				+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			success : function(result) {
				var data = null;
				if (result) {
					data = result.data;
				}
				if (data && data.length > 0) {
					BIONE.commonOpenDialog("配置校验组","configDialog",800,450,
							"${ctx}/report/frame/validgroup/config?groupId="+rows[0].groupId+"&grpType="+rows[0].groupType);
				}else{
					BIONE.tip("无授权校验类型，无法配置校验组！");
				}
			}
		})
	}
	
	function initSearchForm(){
		$("#search").ligerForm({
			fields : [{
				display : '校验组编号',
				name : "groupNo",
				labelWidth : 80,
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'grp.groupNo',
					op : "like"
				}
			},{
				display : '校验组名称',
				name : "groupNm",
				labelWidth : 80,
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'grp.groupNm',
					op : "like"
				}
			},{
				display : '校验组类型',
				name : "groupType",
				labelWidth : 80,
				newline : false,
				type : "select",
				options :{
					data :[{
						id : "02",
						text : "指标组"
					},{
						id : "05",
						text : "报表组"
					}]
				},
				attr : {
					field : 'grp.groupType',
					op : "="
				}
			}]
		});
	};
	
	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			columns : [{
				display : '校验组编号',
				name : 'groupNo',
				width : "19%",
				align : 'left'
			},{
				display : '校验组名称',
				name : 'groupNm',
				width : "19%",
				align : 'left'
			},{
				display : '校验组类型',
				name : 'groupType',
				width : "19%",
				align : 'left',
				render:function(row){
					switch(row.groupType){
						case "02" :
							return "指标组";
						case "05" :
							return "报表组";
						default :
							return row.groupType;
					}
				}
			},{
				display : '备注',
				name : 'remark',
				width : "19%",
				align : 'left'
			}],
			checkbox : true,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/report/frame/validgroup/list.json",
			sortName : 'grp.groupName',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			rownumbers : true,
			width:'100%',
			toolbar : {}
		});
	}
	
</script>
</head>
<body>
</body>
</html>