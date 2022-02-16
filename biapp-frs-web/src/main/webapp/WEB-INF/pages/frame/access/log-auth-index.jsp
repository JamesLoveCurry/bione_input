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

	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : '操作人',
				name : "userName",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : 'like',
					field : 'user.userName'
				}
			},{
				display : '开始时间',
				name : "startDate",
				newline : false,
				type : "date",
				options : {
					format : "yyyy-MM-dd"
				},
				cssClass : "field",
				attr : {
					op : '>=',
					field : 'operTime'
				}
			},{
				display : '结束时间',
				name : "endDate",
				newline : false,
				type : "date",
				options : {
					format : "yyyy-MM-dd"
				},
				cssClass : "field",
				validate : {
					greaterThan : "startDate"
				},
				attr : {
					op : '<=',
					field : 'operTime'
				}
			}]
		});
	};
	
	function initGrid() {	
		grid = $("#maingrid").ligerGrid({
			//toolbar : {},
			columns : [ {
				display : '操作人账号',
				name : 'userNo',
				width : "15%",
				align : 'center',
				sortname : 'user.userNo'
			},{
				display : '操作人',
				name : 'userName',
				width : "20%",
				align : 'center',
				sortname : 'user.userName'
			},{
				display : '授权对象',
				name : 'objDefName',
				width : "15%",
				align : 'center'
			},{
				display : '授权对象名称',
				name : 'authObjName',
				width : "15%",
				align : 'center',
				isSort : false
			},{
				display : '操作时间',
				name : 'operTime',
				width : "15%",
				type : 'date',
				format : 'yyyy-MM-dd hh:mm:ss',
				align : 'center',
				sortname : 'info.operTime'
			},{
				display : '操作',
				width : "15%",
				align : 'center',
				render : function(row){
					return "<a href='javascript:void(0)' class='link' onclick='detail(\""+row.logId+"\",\"" + row.authObjNo + "\")'>详情</a>";
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
			url : "${ctx}/bione/syslog/auth/list.json",
 			sortName : 'info.operTime',//第一次默认排序的字段
 			sortOrder : 'desc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			checkbox : false,
			rownumbers : true
		});
		var btn = [{
			text:"导出Excel",
			click:exportExcel,
			icon:"export"
		}];
		BIONE.loadToolbar(grid,btn,null);
	};
	function detail(logId, authObjNo) {
		BIONE.commonOpenLargeDialog( "操作明细","editDetail","${ctx}/bione/syslog/auth/detail?logId=" + logId + "&authObjNo=" + authObjNo);
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
				text : '搜索',
				icon : 'search3',
				// width : '50px',
				click : function() {
					BIONE.validate($("#formsearch"));
					if($("#formsearch").valid()){
						grid.setParm("userName", $("#userName").val());
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
						icon : 'refresh2',
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