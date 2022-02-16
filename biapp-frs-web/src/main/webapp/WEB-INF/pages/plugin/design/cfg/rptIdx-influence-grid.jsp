<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14_BS.jsp">
<head>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid;
	var influenceType = "${influenceType}";
	var srcIdxNo = parent.indexNo;
	var idxStartDate = parent.idxStartDate;
	var idxEndDate = parent.idxEndDate;
	var isRpt = parent.isRpt;
	var busiTypeMap = new Map();
	
	$(function() {
		initBusiType();
		initGrid();
		initButtons();
	});
	
	function initBusiType(){
		$.ajax({
			async :false,
			type : "POST",
			url : "${ctx}/report/frame/datashow/idx/busiTypeList.json",
			dataType : 'json',
			success: function(data) {
				if(data){
					for(var i = 0; i < data.length ; i++){
						busiTypeMap.set(data[i].id, data[i].text);
					}
				}
			},
			error: function() {
				top.BIONE.tip('保存失败');
			}
		});
	}
	
	
	function initGrid() {
		if("rpt" == influenceType){
			initRptGrid();
		}
		if("logic" == influenceType){
			initLogicGrid();
		}
		if("warn" == influenceType){
			initWarnGrid();
		}
		grid.setHeight($("#center").height() - 40);
	};
	
	//加载影响报表列表
	function initRptGrid(){
		var  columnArr  =[{
			display : '报表编号',
			name : 'rptNum',
			width : "20%",
			align : 'center',
		},{
			display : '业务类型',
			name : 'busiType',
			width : "10%",
			align : 'center',
			render :function(a,b,c){
				return busiTypeMap.get(c);
			}
		},{
			display : '报表名称',
			name : 'rptNm',
			width : "40%",
			align : 'left'
		},{
			display : '单元格名称',
			name : 'indexNm',
			width : "25%",
			align : 'left'
		}];
		grid = $("#maingrid").ligerGrid({
			checkbox : false,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url :"${ctx}/report/frame/design/cfg/loadRptGrid?srcIdxNo="  + srcIdxNo + "&idxStartDate=" + idxStartDate + "&idxEndDate=" + idxEndDate,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			width : '100%'
		});
	}
	
	//加载影响逻辑校验公式列表
	function initLogicGrid(){
		var columnArr  =[{
			display : '校验公式',
			name : 'expressionDesc',
			width : "65%",
			align : 'left',
		}, {
			display : '开始时间',
			name : 'startDate',
			width : '15%',
			align : 'center'
		}, {
			display : '结束时间',
			name : 'endDate',
			width : '15%',
			align : 'center'
		}];
		grid = $("#maingrid").ligerGrid({
			checkbox : false,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url :"${ctx}/report/frame/valid/logic/list.json?srcIdxNo=" + srcIdxNo,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			width : '100%'
		});
	}
	
	//加载影响警戒值校验公式列表
	function initWarnGrid(){
		var columnArr  =[{
			display : '单元格名称',
			name : 'indexNm',
			width : "25%",
			align : 'left'
		}, {
			display : '比较值类型',
			name : 'compareValType',
			width : '10%',
			align : 'center',
			render : function(data) {
				if (data.compareValType == "00")
					return "常量";
				if (data.compareValType == "01")
					return "上日";
				if (data.compareValType == "02")
					return "月初";
				if (data.compareValType == "03")
					return "上月末";
				if (data.compareValType == "04")
					return "上月同期";
				if (data.compareValType == "05")
					return "季初";
				if (data.compareValType == "06")
					return "上季末";
				if (data.compareValType == "07")
					return "年初";
				if (data.compareValType == "08")
					return "上年末";
				if (data.compareValType == "09")
					return "上年同期";
			}
		}, {
			display : '幅度类型',
			name : 'rangeType',
			width : '20%',
			align : 'center',
			render : function(data) {
				if (data.rangeType == "01")
					return "数字";
				if (data.rangeType == "02")
					return "百分比";
			}
		}, {
			display : '开始时间',
			name : 'startDate',
			width : '15%',
			align : 'center'
		}, {
			display : '结束时间',
			name : 'endDate',
			width : '15%',
			align : 'center'
		}];
		grid = $("#maingrid").ligerGrid({
			checkbox : false,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url :"${ctx}/report/frame/valid/warn/list.json?srcIdxNo=" + srcIdxNo,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			width : '100%'
		});
	}
	
	function initButtons(){
		$("#divTips").append("指标不要轻易删除，不然会影响跨期分析，如果只是更换指标，请直接覆盖！！！")
		var buttons = [{
			text: '取消',
			onclick: function() {
				window.parent.parent.Design._recoveryCellVal(window.parent.currCellValue);
				parent.BIONE.closeDialog('idxInfluence');
			}
		},{
			text: '确认删除',
			onclick: function() {
				$.ligerDialog.confirm('该指标有校验公式会一起删除，是否确认删除？', function(yes) {
					if (yes) {
						$.ajax({
							cache : false,
							async : true,
							url : "${ctx}/report/frame/design/cfg/deleteValid",
							data : {
								idxNo : srcIdxNo
							},
							dataType : 'json',
							type : "post",
							beforeSend : function() {
								BIONE.loading = true;
								BIONE.showLoading("正在删除，请稍候..");
							},
							success : function(result) {
								BIONE.loading = false;
								if(result){
									if("YES" == result.isSuccess){
										window.parent.parent.Design._deleteIdx();
										parent.BIONE.closeDialog('idxInfluence');
									}
								}
							},
							error : function() {
								BIONE.tip("删除失败，请联系系统管理员");
							}
						});
					}
				});
			}
		}];
		if("yes" == isRpt){
			buttons = [{
				text: '取消',
				onclick: function() {
					window.parent.parent.Design._recoveryCellVal(window.parent.currCellValue);
					parent.BIONE.closeDialog('idxInfluence');
				}
			}];
			$("#divTips").append("该指标被其他报表所引用，所以不能直接删除，需要先删除引用再删除！")
		}
		BIONE.addFormButtons(buttons);
	}
</script>
</head>
<body>
	<div id="template.center">
		<div>
			<div id="divTips" style="color: red;"></div>
			<div id="maingrid"></div>
		</div>
	<div id="bottom">
		<div class="form-bar">
			<div class="form-bar-inner" style="padding-top: 5px"></div>
		</div>
		<sitemesh:div property='template.button' />
	</div>
	</div>
</body>
</html>
