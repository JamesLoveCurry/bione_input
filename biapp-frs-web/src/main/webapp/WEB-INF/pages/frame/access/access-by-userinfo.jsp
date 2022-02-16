<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid;
	function initExport() {
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);

	}
	jQuery.validator.addMethod("greaterThan", function(value, element, params) {
		if (value == '' || value == null) {
			// edit by caiqy,have no endtime , return
			return true;
		}
		var ele = $("[name=" + params + "]");
		return value >= ele.val() ? true : false;
	}, "开始时间不能晚于结束时间.");
	$(function() {
		$("#search").ligerForm({
			fields : [ {
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
		
		grid = $("#maingrid").ligerGrid({
			width : '100%',
			columns:[{
				display:'用户账号',
				name:'userNo',
				width:"15%"
			},{
				display:'用户名称',
				name:'userName',
				width:"15%"
			},{
				display:'机构',
				name:'orgName',
				width:"15%"
			}/* ,{
				display:'部门',
				name:'deptName',
				width:"10%"
			} */,{
				display:'登录次数',
				name:'loginNum',
				width:"15%"
			},{
				display:'在线时间',
				name:'loginTime',
				width:"20%",
				render : function(row){
					return calTime(row.loginTime);
				}
			},{
				display : '操作',
				width : "15%",
				align : 'center',
				render : function(row){
					return "<a href='javascript:void(0)' style='color :blue' class='link' onclick='detail(\""+row.userId+"\",\"" + row.userNo + "\")'>"+'明细'+"</a>";
				}
			}],
			width : '100%',
			height : '99%',
			rownumbers:true,
			checkbox:false,
			usePager:true,
			isScroll : true,
			alternatingRow:true,
			dataAction:'server',
			url:"${ctx}/bione/syslog/login/list.json",
			type:"post",
			sortName:'userNo',
			sortOrder:'asc',
			toolbar:{}
		});
		grid.setHeight($("#center").height() - 140);
		var btn = [{
			text : "导出Excel",
			click : exportExcel,
			// color : "#00EE00",
			icon : "fa-download"
		}];
		initExport();
		BIONE.loadToolbar(grid,btn,null);
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#formsearch");
	});
	
	function detail(userId, userName) {
		window.startDate = $("#startDate").val();
		window.endDate = $("#endDate").val();
		BIONE.commonOpenLargeDialog( "\"" + userName + "\"" + "登录明细","editDetail","${ctx}/bione/syslog/login/detail?userId=" + userId);
	}
	
	//导出Execl
	function exportExcel() {
		var src = '';
		src = "${ctx}/bione/syslog/login/exportFile?userName="+encodeURI(encodeURI($("#userName").val()))+"&orgName="+encodeURI(encodeURI($("#orgName").val()))+"&startDate="+$("#startDate").val()+"&endDate="+$("#endDate").val()+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	}
	//选择选中行
	function selectIds(){
		var ids = [];
		var rows = grid.getSelectedRows();
		for(var i in rows){
			ids.push(rows[i].rptId);
		}
		return ids;
	}
	
	function calTime(time){
		time=parseInt(time);
		time = parseInt(time/1000);
		var second=time%60;
		time=parseInt(time/60);
		var minute=time%60;
		time=parseInt(time/60);
		var hour=time%24;
		var day=parseInt(time/24);
		return day+"天"+hour+"时"+minute+"分"+second+"秒";
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
						grid.setParm("userName", $("#userName").val());
						grid.setParm("orgName", $("#orgName").val());
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
						icon : 'fa-repeat',
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
<html>
</head>
<body>
</body>
</html>