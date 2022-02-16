<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">

<style type="text/css">
.link {
	color: #065FB9;
	text-decoration: underline;
}
</style>

<script type="text/javascript">
jQuery.validator.addMethod("greaterThan", function(value, element, params) {
	if (value == '' || value == null) {
		return true;
	}
	var ele = $("[name=" + params + "]");
	return value >= ele.val() ? true : false;
	}, "结束时间应当大于开始时间");
	var grid;
	var startAccess = "";
	var endAccess = "";
	function initExport() {
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append (downdload);
	}
	$(function() {
		$("#formsearch").ligerForm({
			fields : [ {
				display : '报表名称',
				name : 'rptNm',
				newline : false,
				type : "text",
				cssClass : "field",
				attr:{
					field:'rptNm',
					op:"like"
				}
			},
			{
				display : '开始时间',
				name : 'startAccess',
				newline : false,
				type : "date",
				cssClass : "field",
				options:{
					format:"yyyyMMdd"
					}
			},
			{
				display : '结束时间',
				name : 'endAccess',
				newline : false,
				type : "date",
				cssClass : "field",
				options:{
					format:"yyyyMMdd"
					},
				validate:{
					greaterThan : "startAccess"
				}
			}
			]
		});
		
		grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '100%',
			 columns:[{
				display:'报表名称',
				name:'rptNm',
				width:"45%"
			}
			,{
				display:'点击次数',
				name:'pv',
				width:"15%"
				
			},{
				display:'点击人数',
				name:'uv',
				width:"15%"
				
			},{
				display:'操作',
				name:'detail',
				width:'15%',
				render:function(a){
					return "<a href='javascript:void(0)' class='link' onclick='detail(\""+a.rptId+"\",\""+ a.rptNm +"\")'>"+'详情'+"</a>";
				},
				isSort  : false
			}],
			rownumbers:true,
			checkbox:false,
			usePager:true,
			isScroll : false,
			alternatingRow:true,
			dataAction:'server',
			url:"${ctx}/rpt/frame/access/info.json",
			type:"post",
			sortName:'rptNm',
			sortOrder:'asc',
			toolbar:{}
		});
		
		var btn = [{
			text:"导出Excel",
			click:exportExcel,
			icon:"export"
		},{
			text:"饼状图",
			icon:"piechart",
			operNo : "idx-show-piechartchart",
			click : function() {
					BIONE.commonOpenDialog("图表", "chartDialog", 900, 500,
							"${ctx}/rpt/frame/access/chart" );
			}
		}];
		initExport();
		BIONE.loadToolbar(grid,btn,null);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#formsearch");
		BIONE.addSearchButtons("#formsearch", grid, "#searchbtn");
	});
	
	function detail(rptId, rptNm){
		window.startAccess = $("#startAccess").val();
		window.endAccess = $("#endAccess").val();
		BIONE.commonOpenLargeDialog("\""+rptNm+"\"访问明细","editDetail","${ctx}/rpt/frame/access/detail?rptId="+rptId);
	}
	
	//导出Execl
	function exportExcel() {
		var src = '';
		src = "${ctx}/rpt/frame/access/exportFile?rptNm="+encodeURI(encodeURI($("#rptNm").val()))+"&startAccess="+$("#startAccess").val()+"&endAccess="+$("#endAccess").val()+"&d="+ new Date().getTime();
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
				width : '50px',
				click : function() {
					BIONE.validate($("#formsearch"));
					if($("#formsearch").valid()){
						grid.setParm("rptNm", $("#rptNm").val());
						grid.setParm("startAccess", $("#startAccess").val());
						grid.setParm("endAccess", $("#endAccess").val());
						grid.setParm("newPage",1);
						grid.options.newPage=1;
						grid.loadData();
					}
				}
			});
			BIONE
					.createButton({
						appendTo : btnContainer,
						text : '重置',
						icon : 'refresh2',
						width : '50px',
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
<html>
</head>
<body>

</body>
</html>