<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	var dataDate = '${dataDate}';
	var orgNo = '${orgNo}';
	var rptIndexNo = '${rptIndexNo}';
	var checkType = '${checkType}';
	var cellNo = '${cellNo}';
	var grid;
</script>
<script type="text/javascript">
	$(function() {
		initLogicGrid();
	});

	//初始化逻辑校验grid
	function initLogicGrid() {
		grid = $("#griddiv").ligerGrid({
			height : '100%',
			width : '100%',
			columns : [{
				display : '校验公式',
				name : 'expressionDesc',
				width : "28%",
				align : 'left',
				render : function(a, b, c) {
					if (c != null) {
						c.replace(new RegExp("<", "gm"), "&lt;");
						var str = "";
						if(c.length > 40){
							for(var i=0; i<c.length;i++){
								str+=c[i];
								if(i != 0 && i%40 == 0){
									str+="\n";
								}
							}
						}else{
							str = c;
						}
						return str;
					} else {
						return c;
					}
				},
				isSort : true
			},{
				display : '校验数值',
				name : 'replaceExpression',
				width : "15%",
				render : function(a, b, c) {
					if (c != null) {
						c.replace(new RegExp("<", "gm"), "&lt;");
						var str = "";
						if(c.length > 40){
							for(var i=0; i<c.length;i++){
								str+=c[i];
								if(i != 0 && i%40 == 0){
									str+="\n";
								}
							}
						}else{
							str = c;
						}
						return str;
					} else {
						return c;
					}
				},
				isSort : true
			} ,{
				display : '校验状态',
				name : 'verifytSts',
				width : "5%",
				render : VerifyStsRender,
				isSort : true
			} ,{
				display : '容差值',
				name : 'floatVal',
				width : "5%",
				align : 'right',
				isSort : true,
				render: function (a, b, c) {
					if (a.dataUnit != null) {
						if (a.dataUnit == "01") {
							return c + "";
						} else if (a.dataUnit == "02") {
							return c / 100 + "百";
						} else if (a.dataUnit == "03") {
							return c / 1000 + "千";
						} else if (a.dataUnit == "04") {
							return c / 10000 + "万";
						} else if (a.dataUnit == "05") {
							return c / 100000000 + "亿";
						} else if (a.dataUnit == "06") {
							return c * 100 + "%";
						} else {
							return c + "";
						}
					} else {
						return c + "";
					}
				}
			},{
				display : '差值',
				name : 'differVal',
				width : "5%",
				align : 'right',
				isSort : true,
				render: function (a, b, c) {
					return c + "";
				}
			},{
				display : '公式来源',
				name : 'checkSrc',
				width : "5%",
				render : function(a, b, c) {
					if (c) {
						if(c == '01'){
							return '监管制度';
						}else if(c == '02'){
							return '自定义';
						}
					} else {
						return '其他';
					}
				},
				isSort : true
			},{
				display : '公式说明',
				name : 'busiExplain',
				width : "25%",
				align : 'left',
				isSort : true
			},{
				display : '操作',
				name : 'oper',
				width : '10%',
				align : 'center',
				render : function(a, b, c){
					if(a.checkSrc == "02" && a.verifytSts == "N"){
						if(a.validDesc != "" && a.validDesc != null){
							return "<a href='javascript:void(0);' onClick=\"addValidDesc('"+a.checkId +"','"+ a.validDesc+"')\">已添加</a>";
						}else{
							return "<a href='javascript:void(0);' onClick=\"addValidDesc('"+a.checkId +"','"+ a.validDesc+"')\">添加说明</a>";
						}
					}else{
						return "";
					}
				}
			},{
				display : '说明',
				name : 'validDesc',
				hide : true
			},{
				display : '校验id',
				name : 'checkId',
				hide : true
			}],
			dataAction : 'server',
			url : "${ctx}/frs/verificationLogic/logicResultByIdxNo.json",
			parms : {
				dataDate : dataDate,
				orgNo : orgNo,
				indexNo : rptIndexNo,
				checkType : checkType,
				verId : window.parent.base_verId,
				rptTemplateId : window.parent.base_TmpId,
				cellNo : cellNo
				//verifytSts : 'N'//只取未通过的校验
			},
			type : "post",
			checkbox : false,
			rownumbers : true,
			usePager : false,
			isScroll : false,
			alternatingRow : true,
			onSelectRow : function(rowdata, rowid, rowobj){
				//先恢复上一个校验公式涉及到的单元格的背景色
				for (var cellNo in window.parent.cellColor){
					var backColor = window.parent.cellColor[cellNo];
					if(backColor){
						var rowCol = window.parent.View.Utils.posiToRowCol(cellNo);
						var myCell = window.parent.View.spread.getActiveSheet().getCell(Number(rowCol.row), Number(rowCol.col)).backColor(backColor);
					}else{//默认背景颜色就是白色
						var rowCol = window.parent.View.Utils.posiToRowCol(cellNo);
						var myCell = window.parent.View.spread.getActiveSheet().getCell(Number(rowCol.row), Number(rowCol.col)).backColor("#ffffff");
					}
				}
				window.parent.cellColor = [];
				if(!rowdata.idxCellNos){
					return;
				}
				var cellNos = rowdata.idxCellNos;
				//涉及到当前点击检验公式的显示黄色
				for(var num in cellNos){
					var cellNo = cellNos[num];
					var rowCol = window.parent.View.Utils.posiToRowCol(cellNo);
					var myCell = window.parent.View.spread.getActiveSheet().getCell(Number(rowCol.row), Number(rowCol.col));
					window.parent.cellColor[cellNo] = myCell.backColor();
					myCell.backColor("yellow");
				}
			}
		});
		grid.setHeight($("#center").height());
	}
	
	//渲染校验状态
	VerifyStsRender = function(rowdata) {
		var verifytStsNm = "无数据";
		if (rowdata.verifytSts == 'Y'){
			verifytStsNm = "通过";
		}else if (rowdata.verifytSts == 'N'){
			verifytStsNm = "未通过";
			return "<div style='color: red;'>"+ verifytStsNm + "</div>";
		}
		return verifytStsNm;
	}
	
	//添加校验未通过说明
	function addValidDesc(checkId, validDesc){
		parent.BIONE.commonOpenDialog("编辑校验未通过说明", "validDescWin", "550", "350", "${ctx}/frs/verificationLogic/addValidDesc?checkId=" + checkId +"&dataDate=${dataDate}&orgNo=${orgNo}&validDesc=" + validDesc + "&checkType=" + checkType, null);
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="griddiv"></div>
	</div>
</body>
</html>