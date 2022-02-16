<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	//版本号
	var verId = "${verId}";
	var submitRangeCode = "${submitRangeCode}";
	var isView = "${isView}";
	var gridUrl = '${ctx}/frs/pbmessage/idxOrgList?verId='+verId+'&submitRangeCode='+submitRangeCode;
	var grid;
	//初始化函数
	$(function() {
		searchForm();
		if(isView == '1'){
			//查看模式
			initViewGrid();
		}else{
			initGrid();
			initButtons();
		}
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [{
				display : "地区编码",
				name : "reportOrgCode",
				newline : false,
				type : "text",
				width: '200',
				cssClass : "field",
				attr : {
					op : "like",
					field : "pro.reportOrgCode"
				}
			},{
				display : "人行机构编码",
				name : "financeCode",
				newline : false,
				type : "text",
				width: '200',
				cssClass : "field",
				attr : {
					op : "like",
					field : "pro.financeCode"
				}
			} ]
		});

	}
	//查看模型 初始化Grid
	function initViewGrid() {
		grid = $("#maingrid").ligerGrid({
			InWindow : false,
			height : '99%',
			width : '100%',
			columns : [ {
				display : '地区名称',
				name : 'addrNm',
				width : '25%'
			},{
				display : '地区编码',
				name : 'reportOrgCode',
				width : '15%'
			},{
				display : '人行机构编码',
				name : 'financeCode',
				width : '20%'
			}],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			pageSize: 50,                           //每页默认的结果数
	        pageSizeOptions: [50, 200, 500, 800, 1000],  //可选择设定的每页结果数
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : gridUrl,
			sortName : 'reportOrgCode', //第一次默认排序的字段
			sortOrder : 'asc'
		});
	}
	//初始化Grid
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			InWindow : false,
			height : '99%',
			width : '100%',
			columns : [ {
				display : '地区名称',
				name : 'addrNm',
				width : '25%'
			},{
				display : '地区编码',
				name : 'reportOrgCode',
				width : '15%'
			},{
				display : '人行机构编码',
				name : 'financeCode',
				width : '20%'
			}],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			pageSize: 50,                           //每页默认的结果数
	        pageSizeOptions: [50, 200, 500, 800, 1000],  //可选择设定的每页结果数
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : gridUrl,
			sortName : 'reportOrgCode', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}
	
	//初始化Button
	function initButtons() {
		var btns;
		btns = [ {
			text : '新增',
			click : org_add,
			icon : 'add',
			operNo : 'org_add'
		}, {
			text : '删除',
			click : org_delete,
			icon : 'delete',
			operNo : 'org_delete'
		}];
		BIONE.loadToolbar(grid, btns, function() { });
	}
	
	//批量新增
	function org_add() {
		//若没有选择版本号 不能新增
		if(verId==null||verId==""){
			BIONE.tip("请选择版本！");
			return;
		}
		var height = $(window).height() - 40;
		BIONE.commonOpenDialog('批量新增上报地区', 'addOrg', '350', height,'${ctx}/frs/pbmessage/orgAdd');
	}
	
	//批量删除
	function org_delete(){
		//若没有选择版本号 不能删除
		if(verId==null||verId==""){
			BIONE.tip("请选择版本！");
			return;
		}
		var ids = achieveIds();
		var newIds=ids.join(',');
		if(ids.length > 0){
			$.ligerDialog.confirm('您确定删除这' + ids.length + "条记录吗？", function(yes) {
				if (yes) {
					$.ajax({
						async : false,
						type : "POST",
						url : '${ctx}/frs/pbmessage/deleteRangeOrg',
						data : {
							ids : newIds,
							verId : verId
						},
						success : function(){
							BIONE.tip('删除成功');
							grid.loadData();
							//更新上一个页面对报送范围
						},
						error : function(){
							BIONE.tip('删除失败');
						}
					});
					
				}
			});	
		} else {
			BIONE.tip('请选择记录');
		}
	}
	//获取选中行的主键
	function achieveIds() {
		var orgCfgNos = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			orgCfgNos.push(rows[i].reportOrgCfgNo)
		}
		return orgCfgNos;
	}
</script>
</head>
<body>
</body>
</html>