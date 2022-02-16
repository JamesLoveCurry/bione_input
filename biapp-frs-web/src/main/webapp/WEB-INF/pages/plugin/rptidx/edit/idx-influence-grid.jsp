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
		var  columnArr  =[{
			display : '报表编号',
			name : 'rptNum',
			width : "35%",
			align : 'left',
		},{
			display : '报表名称',
			name : 'rptNm',
			width : "40%",
			align : 'left'
		},{
			display : '业务类型',
			name : 'busiType',
			width : "20%",
			align : 'center',
			render :function(a,b,c){
				return busiTypeMap.get(c);
			}
		}];
		if("idx" == influenceType){
			columnArr  =[{
				display : '指标编号',
				name : 'id.indexNo',
				width : "35%",
				align : 'left',
			},{
				display : '指标名称',
				name : 'indexNm',
				width : "40%",
				align : 'left'
			},{
				display : '指标类型',
				name : 'indexType',
				width : "20%",
				align : 'center',
				render :function(a,b,c){
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
					case "07":
						return "补录指标";
						break;
					}
				}
			}];
		}
		grid = $("#maingrid").ligerGrid({
			checkbox : false,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url :"${ctx}/report/frame/idx/loadInfluenceGrid?influenceType=" + influenceType + "&srcIdxNo=" + srcIdxNo,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true,
			width : '100%'
		});
		grid.setHeight($("#center").height() - 40);
	};
	
	function initButtons(){
		var buttons = [{
			text: '取消',
			onclick: function() {
				parent.parent.isNewVerIdx = false;
				parent.BIONE.closeDialog('idxInfluence');
			}
		},{
			text: '确认保存',
			onclick: function() {
				parent.parent.isIdxInfluence = true;
				parent.parent.submit();
				parent.BIONE.closeDialog('idxInfluence');
			}
		}];
		BIONE.addFormButtons(buttons);
	}
</script>
</head>
<body>
	<div id="template.center">
		<div>
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
