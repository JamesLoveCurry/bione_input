<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	jQuery.validator.addMethod("greaterThan", function(value, element, params) {
		if (value == '' || value == null) {
			return true;
		}
		var ele = $("[name=" + params + "]");
		return value >= ele.val() ? true : false;
		}, "结束时间应当大于开始时间");
	
	var grid;
	function initExport() {
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);

	}
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : '功能模块',
				name : "funcName",
				newline : false,
				type : "text",
				cssClass : "field"
			},{
				display : '开始时间',
				name : "startDate",
				newline : false,
				type : "date",
				options : {
					format : "yyyyMMdd"
				},
				cssClass : "field"
			},{
				display : '结束时间',
				name : "endDate",
				newline : false,
				type : "date",
				options : {
					format : "yyyyMMdd"
				},
				cssClass : "field",
				validate : {
					greaterThan : "startDate"
				}
			}]
		});
	};
	
	function initGrid() {	
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			columns : [ {
				display : '功能模块',
				name : 'funcName',
				width : "40%",
				align : 'center'
			},{
				display : '访问用户数',
				name : 'uv',
				width : "15%",
				align : 'center'
			},{
				display : '访问次数',
				name : 'pv',
				width : "15%",
				align : 'center'
			},{
				display : '操作',
				width : "20%",
				align : 'center',
				render : function(row){
					return "<a href='javascript:void(0)' style='color :blue' class='link' onclick='detail(\""+row.menuId+"\",\"" + row.funcName + "\")'>"+'明细'+"</a>";
				}
			}],
			width : '100%',
			height : '99%',
			isScroll : true,
			checkbox: false,
			dataAction : 'server', 
			usePager : true, 
			alternatingRow : true,
			colDraggable : true,
			url : "${ctx}/bione/syslog/func/list.json",
 			sortName : 'info.func_name',//第一次默认排序的字段
 			sortOrder : 'asc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			checkbox : false,
			rownumbers : true
		});
		var btn = [{
			text : "导出Excel",
			click : exportExcel,
			icon : "fa-download"
		}];
		initExport();
		BIONE.loadToolbar(grid,btn,null);
	};
	function detail(menuId, funcName) {
		window.startDate = $("#startDate").val();
		window.endDate = $("#endDate").val();
		BIONE.commonOpenLargeDialog( "\"" + funcName + "\"" + "访问明细","editDetail","${ctx}/bione/syslog/func/detail?menuId=" + menuId);
	}
	$(function() {	
		initSearchForm();
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#formsearch");
	});
	//导出Execl
	function exportExcel() {
		var funcName =  encodeURI(encodeURI($.trim($("#search input[name='funcName']").val())));
		var startDate = $("#startDate").val();
		var endDate = $("#endDate").val();
		var src = '';
		src = "${ctx}/bione/syslog/func/exportFile?startDate="+startDate+"&endDate="+endDate+"&funcName="+funcName+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	}
	// 创建表单搜索按钮：搜索、高级搜索
	BIONE.addSearchButtons = function(form, grid, btnContainer) {
		if (!form)
			return;
		form = $(form);
		if (btnContainer) {
			BIONE.createButton({
				appendTo : btnContainer,
				text : '查询',
				icon : 'fa-search',
				// width : '50px',
				click : function() {
					BIONE.validate($("#formsearch"));
					if($("#formsearch").valid()){
						grid.setParm("funcName", $("#funcName").val());
						grid.setParm("startDate", $("#startDate").val());
						grid.setParm("endDate", $("#endDate").val());
						grid.setParm("newPage",1);
						grid.options.newPage=1
						grid.loadData();
					}
				}
			});
			BIONE
					.createButton({
						appendTo : btnContainer,
						text : '重置',
						icon : 'fa-query',
						// width : '50px',
						click : function() {
							$(":input", form)
									.not(
											":submit, :reset,:hidden,:image,:button, [disabled]")
									.each(function() {
										$(this).val("");
									});
						}
					});
		}
	};
	
</script>
</head>
<body>
</body>
</html>