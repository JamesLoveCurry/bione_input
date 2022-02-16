<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var authTypeNo ="${authTypeNo}";
	
	$(function() {
		$("#maingrid").ligerGrid(
				{
					height : '90%',
					width : '99%',
					columns : [ {
						display : '授权资源标识',
						name : 'defNo',
						align : 'center',
						width : '30%'
					}, {
						display : '授权资源名称',
						name : 'defName',
						align : 'center',
						width : '20%'
					},{
						display : '实现类名称',
						name : 'beanName',
						align : 'center',
						width : '40%',
						editor : {
							type : 'text'
						}
					} ],
					usePager:false,
					checkbox : false,
					enabledEdit : true,
					isScroll : true,
					enabledEdit : true,
					rownumbers : true,
					alternatingRow : true,//附加奇偶行效果行
					dataAction : 'server',//从后台获取数据
					method : 'get',
					url : "${ctx}/bione/admin/authConfig/getDefRel?authTypeNo="+authTypeNo+"&t="
							+ new Date().getTime(),
					sortOrder : 'asc'
				});

		buttons = [];
		buttons.push({
			text : '取消',
			onclick : f_close
		},{
			text : '保存',
			onclick : f_save
		});
		BIONE.addFormButtons(buttons);

	});
	
	function f_close() {
		BIONE.closeDialog("authTypeWin");
	}
	
	function f_save(){
		var grid = $("#maingrid").liger();
		grid.endEdit();
		var data = $("#maingrid").liger().getData();
		BIONE.ajax({
			url : "${ctx}/bione/admin/authConfig/saveRes",
			type : "post",
			dataType : 'json',
			data : {
				model : JSON2.stringify(data),
				authTypeNo:authTypeNo
			}
		},function(){
			BIONE.tip("保存成功");
			f_close();
		});
		
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div id="maingrid"></div>
	</div>

</body>
</html>