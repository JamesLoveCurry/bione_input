<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<script type="text/javascript">

	var type = '${type}';
	var notUptCells = type == "batch" ? parent.notUptCells : parent.View.notUptCells;
	var columns = [];
	$(function(){
		if(type == "batch"){
			columns = [{
				display : '报表编号',
				name : 'rptNum',
				align : 'center',
				width : '10%'
			},{
				display : '数据日期',
				name : 'dataDate',
				align : 'center',
				width : '8%'
			},{
				display : '机构编号',
				name : 'orgNo',
				align : 'center',
				width : '8%'
			},{
				display : '单元格编号',
				name : 'cellNo',
				align : 'center',
				width : '8%'
			},{
				display : '原值',
				name : 'oldValue',
				align : 'center',
				width : '8%'
			},{
				display : '导入值',
				name : 'value',
				align : 'center',
				width : '8%'
			},{
				display : '导入结果',
				name : 'result',
				align : 'center',
				width : '15%'
			},{
				display : '说明',
				name : 'desc',
				align : 'center',
				width : '32%'
			}];
		}else{
			columns = [{
				display : '单元格编号',
				name : 'cellNo',
				align : 'center',
				width : '6%'
			},{
				display : '原值',
				name : 'oldValue',
				align : 'center',
				width : '6%'
			},{
				display : '导入值',
				name : 'value',
				align : 'center',
				width : '6%'
			},{
				display : '导入结果',
				name : 'result',
				align : 'center',
				width : '8%'
			},{
				display : '说明',
				name : 'desc',
				align : 'center',
				width : '12%'
			},{
				display : '表间计算公式',
				name : 'sourceFormulaDesc',
				align : 'left',
				width : '30%'
			},{
				display : '来源报表',
				name : 'sourceRptNm',
				align : 'left',
				width : '15%'
			},{
				display : '来源单元格编号',
				name : 'sourceCellNo',
				align : 'left',
				width : '13%'
			}];
			initSourceCells();
		}
		initGrid();
	});

	function initGrid() {	
		grid = $("#maingrid").ligerGrid({
			columns : columns,
			width: "99%",
			height: $("#center").height(),
			checkbox: false,
			isScroll: true,
			alternatingRow : true,
			colDraggable : true,
			rownumbers : true,
			data : {Rows: notUptCells, Total: notUptCells.length},
			usePager:false
		});
	}
	
	function initSourceCells(){
		var cellNos = [];
		var templateId = notUptCells[0].templateId;
		var dataDate = notUptCells[0].dataDate;
		var verId = notUptCells[0].verId;
		for(var i=0; i<notUptCells.length; i++){
			cellNos.push(notUptCells[i].cellNo);
		}
		var sourceCells = [];
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/getCellSourceInfo",
			type : 'POST',
			data : {
				cellNos : cellNos.join(","),
				dataDate : dataDate,
				templateId : templateId,
				verId : verId
			},
			success : function(result) {
				sourceCells = result.sourceCells;
			}
		});
		for(var i=0; i<notUptCells.length; i++){
			var sourceTmpIds = [];
			var sourceCellNos = [];
			var sourceRptNms = [];
			var sourceFormulaDescs = [];
			for(var j=0; j<sourceCells.length; j++){
				if(notUptCells[i].cellNo == sourceCells[j].cellNo){
					if(sourceTmpIds.indexOf(sourceCells[j].sourceTmpId) < 0){
						sourceTmpIds.push(sourceCells[j].sourceTmpId);
					}
					if(sourceCellNos.indexOf(sourceCells[j].sourceCellNo) < 0){
						sourceCellNos.push(sourceCells[j].sourceCellNo);
					}
					if(sourceRptNms.indexOf(sourceCells[j].sourceRptNm) < 0){
						sourceRptNms.push(sourceCells[j].sourceRptNm);
					}
					if(sourceFormulaDescs.indexOf(sourceCells[j].sourceFormulaDesc) < 0){
						sourceFormulaDescs.push(sourceCells[j].sourceFormulaDesc);
					}
				}
			}
			notUptCells[i].sourceTmpId = sourceTmpIds.join(",");
			notUptCells[i].sourceCellNo = sourceCellNos.join(",");
			notUptCells[i].sourceRptNm = sourceRptNms.join(",");
			notUptCells[i].sourceFormulaDesc = sourceFormulaDescs.join(",");
		}
	}
</script>
</head>
<body>

</body>
</html>