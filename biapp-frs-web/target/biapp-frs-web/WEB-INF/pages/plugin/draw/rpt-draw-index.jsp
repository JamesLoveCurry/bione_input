<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">
<script type="text/javascript">

    var grid;
    function initSearchForm() {
    	$("#search").ligerForm({
    		fields : [{
    			display : '报表编号',
    			name : "rptNum",
    			id : "rptNum",
    			newline : true,
    			labelWidth : 100,
    			width : 220,
    			space : 30,
    			type : "text",
    			cssClass : "field",
    			attr : {
    				field : 'rpt.RPT_NUM',
    				op : "="
    			}
    		}, {
    			display : '报表名称',
    			name : "rptNm",
    			id : "rptNm",
    			newline : false,
    			labelWidth : 100,
    			width : 220,
    			space : 30,
    			type : "text",
    			cssClass : "field",
    			attr : {
    				field : 'rpt.RPT_NM',
    				op : "like"
    				}
    			}]
    	});
    };
    function initGrid() {
    	grid = $("#maingrid").ligerGrid({
    		toolbar : {},
    		columns : [{
    			display : '报表编号',
    			name : 'rptNum',
    			sortName : 'rpt.RPT_NUM',
    			width : "25%",
    			align : 'center',
    			render : function(row,index,val){
    				return "<a style='color:blue' onclick='f_view(\""+row.rptId+"\",\""+row.rptNm+"\",\""+row.rptNum+"\")'>"+val+"</a>"
    			}
    		}, {
    			display : '报表名称',
    			name : 'rptNm',
    			sortName : 'rpt.RPT_NM',
    			width : "25%",
    			align : 'center'
    		},  {
    			display : '翻牌日期',
    			name : 'drawDate',
    			sortName : 'prd.DRAW_DATE',
    			width : "20%",
    			align : 'center',
    			type : "date"
    		}],
    		dataAction : 'server', 
    		usePager : true, 
    		url : "${ctx}/rpt/rpt/draw/list.json",
    		sortName : 'rpt.RPT_NUM',
    		sortOrder : 'asc', 
    		pageParmName : 'page',
    		pagesizeParmName : 'pagesize',
    		checkbox : true,
    		rownumbers : true
    	});
    };
    function initToolBar() {
    	var toolBars = [ {
    		text : '手动翻牌',
    		click : f_draw,
    		icon : 'modify'
    	}];
    	BIONE.loadToolbar(grid, toolBars, function() {
    	});
    }
    $(function() {	
    	initSearchForm();
    	initGrid();
    	initToolBar();
    	BIONE.addSearchButtons("#search", grid, "#searchbtn");
    });
    
	//手工翻牌
   function f_draw(){
		var rows = grid.getSelectedRows();
		var rptIds = '';//用于处理批量选择的记录
		if(rows.length == 0){
			BIONE.tip("请至少选择一条记录");
			return false;
		}
		for(var i in rows){
			rptIds += rows[i].rptId+",";
		}
		window.rptIds = rptIds;
		BIONE.commonOpenSmallDialog('手工翻牌', 'editWin','${ctx}/rpt/rpt/draw/edit');
	}
	
  /*  function f_view(nodeId){
    	BIONE.commonOpenDialog("节点信息",
    			"engineNodeDialog",700,440, 
    			"${ctx}/report/frame/engine/log/node/view?nodeId="+nodeId);
    } */
   //点击指标查看指标翻牌详情
   function f_view(rptId,rptNm,rptNum){
		BIONE.commonOpenDialog(rptNum+":"+rptNm+"--翻牌日志",
				"rptDrawLogDialog",700,300, 
				"${ctx}/rpt/rpt/draw/view?rptId="+rptId);
	}
   /*  function  f_delete(taskNo){
    	var rows = grid.getCheckedRows();
    		if(rows && rows.length == 0) {
    			BIONE.tip("请选择一条数据！");
    			return;
    		}	
    		var id ="";
    		for(var i in rows){
    			id += rows[i].nodeId+",";
    		}
    		$.ligerDialog.confirm('您确定删除这条记录么？', function(yes) {
    		if (yes) {
    			$.ajax({
    				cache : false,
    				async : true,
    				url : '${ctx}/report/frame/engine/log/node/' + id,
    				type : "DELETE",
    				beforeSend : function() {
    					BIONE.loading = true;
    					BIONE.showLoading("正在删除数据...");
    				},
    				complete : function() {
    					BIONE.loading = false;
    					BIONE.hideLoading();
    				},
    				success : function(data) {
    					BIONE.tip('删除成功');
    					grid.loadData();	
    					
    				},
    				error : function(result, b) {
    					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
    				}
    			});
    		}
    	});
    } */
	
</script>
</head>
<body>
</body>
</html>