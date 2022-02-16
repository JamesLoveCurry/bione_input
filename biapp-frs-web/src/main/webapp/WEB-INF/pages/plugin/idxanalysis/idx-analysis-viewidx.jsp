<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template8.jsp">
<script type="text/javascript">
	var grid=null;
	var columns= [ {
		display : '指标编号',
		name : 'id.indexNo',
		id:'indexNo',
		width : "25%"
	},{
		display : '指标名称',
		name : 'indexNm',
		width : "25%"
	}, {
		display : '指标类型',
		name : 'indexType',
		width : "20%",
		render: function(a,b,c){
			switch(c){
			case "01":
				return "根指标";
				break;
			case "02":
				return "组合指标";
				break;
			case "03":
				return "派生指标";
				break;
			case "04":
				return "泛化指标";
				break;
			case "05":
				return "总账指标";
				break;
			}
		}
	},{
		display : '值',
		name : 'value',
		width : "20%"
	}];
	
	$(function() {
		parent.idx=window;
		initGrid();
	});
	function reloadGrid(id){
		grid.setParm('newPage', 1);
		grid.set("url","${ctx}/rpt/frame/idx/idxanalysis/idxList.json?id="+id+"&type=${type}&flag=${flag}");
	}
	function initGrid(){
		var url="${ctx}/rpt/frame/idx/idxanalysis/idxViewList.json?id=${id}&type=${type}&flag=${flag}";
		
		grid = $("#maingrid").ligerGrid({
				toolbar : {},
				columns :columns,
				checkbox: false,
				dataAction : 'server', //从后台获取数据
				usePager : false, //服务器分页
				alternatingRow : true, //附加奇偶行效果行
				colDraggable : true,
				sortName: "indexNo",
				url : url,
				rownumbers : true,
				width : '100%',
				tree:{ columnId: 'indexNo' },
				parms :{
					json : window.parent.searchJson,
					searchArgs : JSON2.stringify(window.parent.cursearchArgs)
				}
		});
		var toolBars = [ {
			text : '查看指标信息',
			click : f_open_view,
			icon : 'view'
		},{
			text : '查看指标明细',
			click : f_open_detail,
			icon : 'view'
		}];
		BIONE.loadToolbar(grid, toolBars, function() {
		});
	}
	function f_open_view(){
		var rows=grid.getSelectedRows();
		if(rows.length==0){
			BIONE.tip("请选择一条指标记录");
			return;
		}
		if(rows.length>1){
			BIONE.tip("请选择一条指标记录");
			return;
		}
		window.parent.parent.BIONE.commonOpenLargeDialog("指标查看",
				"rptIdxInfoPreviewBox",
				"${ctx}/report/frame/idx/idxInfoPreview?indexNo="+rows[0].id.indexNo , null);

	}
	
	function f_open_detail(){
		var rows=grid.getSelectedRows();
		if(rows.length==0){
			BIONE.tip("请选择一条指标记录");
			return;
		}
		if(rows.length>1){
			BIONE.tip("请选择一条指标记录");
			return;
		}
		if(rows[0].children && rows[0].children.length >0){
			
			BIONE.tip("请选择一条根指标记录");
			return;
		}
		window.parent.parent.cursearchArgs=window.parent.cursearchArgs;
		window.parent.parent.BIONE.commonOpenLargeDialog("指标查看",
				"rptIdxInfoPreviewBox",
				"${ctx}/rpt/frame/idx/idxanalysis/idxDetail?indexNo="+rows[0].id.indexNo , null);

	}
	
</script>
</head>
<body>

</body>
</html>