<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var dialog;
	var grid;

    $(function() {
    	initForm();
        initGrid();
        //initButtons();
    });
    
  	//初始化form
    function initForm() {
		//搜索框初始化
		$("#search").ligerForm({
			fields : [ {
				display : "日期(帐期)",
				name : "dataDt",
				newline : false,
				type : 'date',
				options : { format : "yyyyMMdd"},
                width : 120,
				attr : {
					field : "dataDt",
					op : "="
				}
			}]
		});
	};

	//初始化Grid
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
		columns : [ {
			display : '主键',
			name : 'ruleId',
			type : 'hidden',
			frozen: true
		},{
			display : '日期(帐期)',
			name : 'dataDt',
			width : '10%',  
			align : 'center'
		},{
			display : '规则名称',
			name : 'ruleName',
			width : '19%',
			align : 'center'
		},{
			display : '表名',
			name : 'tabName',
			width : '10%',
			align : 'center'
		},{
			display:'规则类型',
			name:'typeCd',
			width : '16%',
			align : 'center'
		},{
			display:'权重',
			name:'a',
			width : '14%',
			align : 'center'
		},{
			display:'通过数量',
			name:'passCount',
			width : '8%',
			align : 'center'
		},{
			display:'失败数量',
			name:'failCount',
			width : '8%',
			align : 'center'
		},{
			display:'创建时间',
			name:'createTm',
			width : '13%',
			align : 'center'
		}],
		checkbox : false,
		rownumbers : true,
		isScroll : false,
		alternatingRow : true, // 附加奇偶行效果行
		colDraggable : false,
		dataAction : 'local', // 从后台获取数据
		method : 'post',
		url : "${ctx}/east/checkresult/query/queryList",
		parms :{
			dataDt : dataDt
		},
		sortName : 'dataDt', // 第一次默认排序的字段
		sortOrder : 'asc', // 排序的方式
		toolbar : {}
	})
	};

	//初始化按钮
    function initButtons() {
		var btns = [{
           	text : '查询结果明细',
            click : queryDetail,
            icon : 'fa-search'
        }];

		BIONE.loadToolbar(grid, btns, function() {});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate('#formsearch');
	};
	
	//查询结果明细
    function queryDetail() {
    	var rows = grid.getSelectedRows();
        if(rows.length == 1){
        	BIONE.commonOpenDialog("", "editTab", 960, $("#center").height()-30, 
			"${ctx}/east/business/blood/add");
        } else {
            BIONE.tip('请选择一条记录');
        }
    }
    
</script>
</head>
<body>
</body>
</html>