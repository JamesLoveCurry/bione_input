<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid, btns, url, ids = [];
	$(init);

	/* 全局初始化 */
	function init() {
		url = "${ctx}/bione/frs/itemchange/initGrid";
		initSearchForm();
		initGrid();
		//initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	}

	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : "科目名称",
				name : "subjectName",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "subjectName"
				}
			}]
		});
	}
	
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '100%',
			columns : [ {
				display : '科目名称',
				name : 'subjectName',
				align : 'center',
				width : '35%'
			}, {
				display : '科目号',
				name : 'subjectNum',
				align : 'center',
				width : '20%'
			}, {
				display : '科目类型代码',
				name : 'subjectTypeCd',
				align : 'center',
				width : '20%'
			}, {
				display : '变更类型',
				name : 'changeType',
				align : 'center',
				width : '18%',
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
			}],
			checkbox : false,
			usePager : true,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : url,
			//sortName : 'lastUpdateTime ', //第一次默认排序的字段
			sortOrder : 'desc'/* ,
			toolbar : {} */
		});
	}

</script>

</head>
<body>
</body>
</html>