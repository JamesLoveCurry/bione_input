<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">
<script type="text/javascript">
jQuery.validator.addMethod("greaterThan", function(value, element, params) {
	if (value == '' || value == null) {
		return true;
	}
	var ele = $("[name=" + params + "]");
	return value >= ele.val() ? true : false;
	}, "结束时间应当大于开始时间");
	var grid;
	var url;
	
	function initExport() {
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	}
	
	$(function() {
		$("#formsearch").ligerForm({
			fields : [ {
				display : '用户编号',
				name : 'userNo',
				newline : false,
				type : "text",
				cssClass : "field"
			} ,{
				display : '用户名称',
				name : 'userName',
				newline : false,
				type : "text",
				cssClass : "field"
			} ,{
				display : '机构',
				name : 'orgName',
				newline : false,
				type : "text",
				cssClass : "field"
			},
			{
				display : '开始时间',
				name : 'startAccess',
				newline :true,
				type : "date",
				cssClass : "field",
				options:{
					format:"yyyyMMdd"
				}
			},{
				display : '结束时间',
				name : 'endAccess',
				newline :false,
				type : "date",
				cssClass : "field",
				options:{
					format:"yyyyMMdd"
					},
				validate:{
					greaterThan : 'startAccess'
				}
				
			}]
		});
		
		grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '100%',
			columns:[{
				display:'用户编号',
				name:'userNo',
				width:"15%",
				sortname: "userNo"
			},{
				display:'用户名称',
				name:'userName',
				width:"15%"
			},{
				display:'机构',
				name:'orgName',
				width:"20%"
			},{
				display:'访问次数',
				name:'pv',
				width:"15%"
			},{
				display:'访问报表数',
				name:'uv',
				width:"15%"
			},{
				display:'操作',
				name:'detail',
				width:'15%',
				render:function(a){
					return "<a href='javascript:void(0)' style='color:blue' class='link' onclick='detail(\""+a.userId+"\",\""+ a.userName +"\")'>"+'详情'+"</a>";
				},
				isSort  : false
			}],
			rownumbers:true,
			checkbox:false,
			usePager:true,
			isScroll : false,
			alternatingRow:true,
			dataAction:'server',
			url:"${ctx}/rpt/frame/accuser/info.json",
			type:"post",
			sortName:'userNo',
			sortOrder:'desc',
			toolbar:{}
		});
		
		var btn = [
		{
			text:"导出Excel",
			click:exportExcel,
			icon:"export"
		}];
		initExport();
		BIONE.loadToolbar(grid,btn,null);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#formsearch");
		addSearchButtons("#formsearch", grid, "#searchbtn");
	});
	
	function detail(userId, userName){
		window.startAccess = $("#startAccess").val();
		window.endAccess = $("#endAccess").val();
		BIONE.commonOpenLargeDialog("\""+userName+"\"访问详情","editDetail","${ctx}/rpt/frame/accuser/detail?userId="+userId);
	}
	
	//导出Execl
	function exportExcel() {
		var src = '';
		src = "${ctx}/rpt/frame/accuser/exportFile?userNo="+$("#userNo").val()
				+"&userName="+encodeURI(encodeURI($("#userName").val()))
				+"&orgName="+$("#orgName").val()+"&startAccess="+$("#startAccess").val()
				+"&endAccess="+$("#endAccess").val()+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	}
	
	addSearchButtons = function(form, grid, btnContainer) {
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
						grid.setParm("userNo", $("#userNo").val());
						grid.setParm("userName", $("#userName").val());
						grid.setParm("orgName", $("#orgName").val());
						grid.setParm("startAccess", $("#startAccess").val());
						grid.setParm("endAccess", $("#endAccess").val());
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
	}
</script>
<html>
</head>
<body>
</body>
</html>