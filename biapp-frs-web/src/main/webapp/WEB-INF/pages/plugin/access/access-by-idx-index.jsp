<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">

<style type="text/css">
.link {
	color: #065FB9;
	text-decoration: underline;
}
</style>

<script type="text/javascript">
	var grid;
	// 某个值大于另一个值的验证, 例如结束日期大于开始日期
	jQuery.validator.addMethod("greaterThan", function(value, element, params) {
		if (value == '' || value == null) {
			// edit by caiqy,have no endtime , return
			return true;
		}
		var ele = $("[name=" + params + "]");
		if(ele.val()=="")
			return true;
		return value >= ele.val() ? true : false;
	}, "开始日期不能晚于结束日期.");
	function initExport() {
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);

	}
	$(function() {
		$("#formsearch").ligerForm({
			fields : [ {
				display : '指标名称',
				name : 'indexNm',
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'indexNm',
					op : "like"
				}
			},{
				display : '开始时间',
				name : 'startAccess',
				newline : false,
				type : "date",
				cssClass : "field",
				options : {
					format : "yyyyMMdd"
				}
			}, {
				display : '结束时间',
				name : 'endAccess',
				newline : false,
				type : "date",
				cssClass : "field",
				options : {
					format : "yyyyMMdd"
				},
				validate:{
					greaterThan : "startAccess"
				}
			} ]
		});
		grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '100%',
			columns : [  {
				display : '指标名称',
				name : 'indexNm',
				width : "45%"
			},{
				display : '点击次数',
				name : 'pv',
				width : "15%"
			}, {
				display : '点击人数',
				name : 'uv',
				width : "15%"
			},{
				display:'操作',
				name:'detail',
				width:'15%',
				render:function(a){
					return "<a href='javascript:void(0)' class='link' onclick='detail(\""+a.indexNm + "\",\"" + a.indexNo+"\", \"" + a.indexVerId +"\")'>"+'详情'+"</a>";
				},
				isSort  : false
			}],
			rownumbers : true,
			checkbox : false,
			usePager : true,
			isScroll : false,
			alternatingRow : true,
			dataAction : 'server',
			url : "${ctx}/rpt/frame/access/idx/info.json",
			type : "post",
			sortName : 'indexNm',
			sortOrder : 'desc',
			toolbar : {}
		});

		var btn = [  {
			text : "导出Excel",
			click : exportExcel,
			icon : "export"
		} ];

		BIONE.loadToolbar(grid, btn, null);
		addSearchButtons("#formsearch", grid, "#searchbtn");
		initExport();
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#formsearch");
	});

	function detail(indexNm, indexNo, indexVerId) {
		window.startAccess = $("#startAccess").val();
		window.endAccess = $("#endAccess").val();
		BIONE.commonOpenLargeDialog("\""+indexNm+"\"访问明细","editDetail","${ctx}/rpt/frame/access/idx/detail?indexNo="+indexNo+"&indexVerId=" + indexVerId);
	}

	//导出Execl
	function exportExcel() {
		var src = '';
		src = "${ctx}/rpt/frame/access/idx/exportFile?indexNm="+encodeURI(encodeURI($("#indexNm").val()))
				+"&startAccess="+$("#startAccess").val()+"&endAccess="+$("#endAccess").val()+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	}
	
	//选择选中行
	function selectIds(){
		var ids = [];
		var rows = grid.getSelectedRows();
		for(var i in rows){
			ids.push(rows[i].indexNo);
		}
		return ids;
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
					if($("#formsearch").valid()){
						grid.setParm("indexNm", $("#indexNm").val());
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
	}
</script>
<html>
</head>
<body>

</body>
</html>