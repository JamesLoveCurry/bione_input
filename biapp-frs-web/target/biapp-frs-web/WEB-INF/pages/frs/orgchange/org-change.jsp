<%@ page language="java" contentType="text/html; charset=UTF-8"
 	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html> 
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid, btns, ids = [];
	
	$(init);

	/* 全局初始化 */
	function init() {
		url = "${ctx}/bione/frs/orgchange/initGrid";
		initSearchForm();
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	}

	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : "机构名称",
				name : "orgNm",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "orgNm"
				}
			}]
		});
	}
	
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '100%',
			columns : [ {
				display : '机构编号',
				name : 'orgNo',
				align : 'center',
				width : '15%',
				render: function(row){
					if(row.orgNo!=null&&row.orgNo!=""&&row.orgNo!="undefined"){
					return "<a href='javascript:void(0)' onclick = 'OrgShow(\""+ row.orgNo +"\" )'>"+ row.orgNo+ "</a>"; 
					}
				} 
			}, {
				display : '机构类型',
				name : 'orgType',
				align : 'center',
				width : '20%',
				render: function(row){
					var orgType = row.orgType;
					if(orgType!=null&&orgType!=""&&orgType!="undefined"){
						if(orgType=="1"){
							return "利率报备";
						}else if(orgType=="2"){
							return "1104监管";
						}else if(orgType=="3"){
							return "人行大集中";
						}else if(orgType=="4"){
							return "east明细";
						}else{
							return "未知";
						}
					} 
				}
			}, {
				display : '变更类型',
				name : 'changeType',
				align : 'center',
				width : '20%',
				render: function(row){
					var changeType = row.changeType;
					if(changeType!=null&&changeType!=""&&changeType!="undefined"){
						if(changeType=="01"){
							return "新增";
						}else if(changeType=="02"){
							return "修改";
						}else{
							return "删除";
						}
					} 
				} 
			}, {
				display : '机构名称',
				name : 'orgNm',
				align : 'center',
				width : '38%'
			}],
			checkbox : false,
			usePager : true,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : url
		});
	}
	
	//跳转至机构管理，并传递机构ID
	function OrgShow(orgNo){
		window.parent.parent.addTabInfo("9914761a4a3c457c93ba580e6039386c","机构管理","/bione/admin/org");
	}
	
</script>

</head>
<body>
</body>
</html>