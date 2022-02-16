<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid,id,url;
	function initSearchForm() {
        $("#search").ligerForm({
            fields : [{
                display : '对象类型',
                name : "objDefNo",
                newline : true,
                comboboxName : 'typeBox',
                type : "select",
                cssClass : "field",
                options : {
                    initValue : 'AUTH_OBJ_ORG',
                    url : "${ctx}/bione/admin/authUsrRel/getObjUserRelationType.json"
                },
                attr : {
                    field : "objDefNo",
                    op : "="
                },
                validate : {
                	required : true
                }
            }]
        });
	};

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '授权对象类型',
				name : 'objDefNo',
				width : "45%",
				align : 'center'
			},{
				display : '授权对象名称',
				name : 'objId',
				width : "45%",
				align : 'center'
			}],
			width : '100%',
			height : '99%',
			isScroll : true,
			checkbox: false,
			dataAction : 'server',
			usePager : false,
			alternatingRow : true,
			colDraggable : true,
			delayLoad : true,
			url : "${ctx}/bione/admin/authUsrRel/userRelation.json?id=${id}",
 			sortName : 'occurTime',//第一次默认排序的字段
 			sortOrder : 'desc', //排序的方式
			checkbox : false,
			rownumbers : true
		});
		loadGrid();
	};
	$(function() {
		initSearchForm();
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
        jQuery.metadata.setType("attr", "validate");
        BIONE.validate("#formsearch");
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
</head>
<body>
</body>
</html>