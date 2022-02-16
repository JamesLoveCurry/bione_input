<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<style>
  .oper{
     text-align:center;
  }
</style>
<script type="text/javascript">
	//全局变量
	var grid;
	var dimTypeNo  =  '${dimTypeNo}';
	var dimTypeNm  =  '${dimTypeNm}';
	var urlPrefix  =  '${urlPrefix}';
	function initExport() {
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);

	}
	function showVersionInfo(hisId){
		src = "${ctx}/rpt/frame/dimCatalog/exportFile?hisId="+hisId+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	}
	$(function() {
		var indxInfoObj = parent.indxInfoObj;
		//渲染grid
		ligerGrid();
		//渲染GRID
		function ligerGrid() {
			                   grid = $("#maingrid").ligerGrid({
			                    	toolbar : {},
			            			checkbox : false,
			            			columns : [{
			            				display : '有效日期',
			            				name : 'startDate',
			            				width : "50%",
			            				align : 'center',
			            				render:function(row){
			            					return row.startDate+" -- "+row.endDate;
			            				}
			            			}, {
			            				display : '操作',
			            				name : 'hisId',
			            				width : "46%",
			            				align : 'center',
			            				render:function(row){
			            					return "<span class='oper'><a href='javascript:void(0)'  onclick='showVersionInfo(\""+row.hisId+"\")'>版本查看</a></span>";
			            				}
			            			}],
			            			dataAction : 'server', //从后台获取数据
			            			usePager : false, //服务器分页
			            			alternatingRow : true, //附加奇偶行效果行
			            			colDraggable : true,
			            			url : "${ctx}/rpt/frame/dimCatalog/getDimInfoVerList.json?dimTypeNo="+dimTypeNo,
			            			rownumbers : true,
			            			width : "99.8%"
			            			,
									height : "88%"
							});
			//初始化按钮
			var btns = [ {
				text : "刷新",
				icon : "refresh",
				click : function() {
					grid.loadData();
				},
				operNo : "col_refresh"
			}];
			BIONE.loadToolbar(grid, btns, function() {}
			);
		}

		//添加表单按钮
		var btns = [ {
			text : "关闭",
			onclick : function() {
				setTimeout('BIONE.closeDialog("dimInfoVerPreBox");',0);
			}
		}];
		BIONE.addFormButtons(btns);
		initExport();
	});
</script>
<title></title>
</head>
<body>
	<div id="template.center">
		<div id="maingrid"></div>
	</div>
</body>
</html>