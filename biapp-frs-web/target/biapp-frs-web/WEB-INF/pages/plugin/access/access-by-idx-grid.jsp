<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
jQuery.validator.addMethod("greaterThan", function(value, element, params) {
	if (value == '' || value == null) {
		return true;
	}
	var ele = $("[name=" + params + "]");
	return value >= ele.val() ? true : false;
	}, "结束时间应当大于开始时间");
	var grid;
	
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : '用户编号',
				name : "userNo",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : 'user_no'
				}
			},{
				display : '用户名称',
				name : "userName",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : 'user_name'
				}
			},{
				display : '开始时间',
				name : "startDate",
				newline : true,
				type : "date",
				options : {
					format : "yyyyMMdd"
				},
				cssClass : "field",
				attr : {
					op : ">=",
					field : 'access_date'
				}
			},{
				display : '结束时间',
				name : "endDate",
				newline : false,
				type : "date",
				options : {
					format : "yyyyMMdd"
				},
				cssClass : "field",
				attr : {
					op : "<=",
					field : 'access_date'
				},
				validate : {
					greaterThan : "startDate"
				}
			}]
		});
		$("#startDate").val(window.parent.startAccess);
		$("#endDate").val(window.parent.endAccess);
	};
	
	$(function() {
		grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '100%',
			columns:[{
				display:'用户编号',
				name:'userNo',
				width:"23%"
			},{
				display:'用户名称',
				name:'userName',
				width:"23%"
			},{
				display:'访问时间',
				name:'accessTime',
				width:"23%"
			},{
				display:'访问Ip',
				name:'accessIp',
				width:"23%"
			}],
			rownumbers:true,
			checkbox:false,
			usePager:true,
			isScroll : false,
			alternatingRow:true,
			delayLoad : true,
			dataAction:'server',
			url:"${ctx}/rpt/frame/access/idx/search.json?indexNo=${indexNo}&indexVerId=${indexVerId}",
			type:"post",
			sortName:'accessTime',
			sortOrder:'asc'
		});
		initSearchForm();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#formsearch");
		loadGrid();
	//	grid.setHeight($("#grid").height());
		
	});
	
	function loadGrid(){
		var rule = BIONE.bulidFilterGroup("#search");
		if (rule.rules.length) {
			grid.setParm("condition",JSON2.stringify(rule));
			grid.setParm("newPage",1);
			grid.options.newPage=1
		} else {
			grid.setParm("condition","");
			grid.setParm('newPage', 1);
			grid.options.newPage=1
		}
		grid.loadData();
	}
</script>
<html>
</head>
<body>

</body>
</html>