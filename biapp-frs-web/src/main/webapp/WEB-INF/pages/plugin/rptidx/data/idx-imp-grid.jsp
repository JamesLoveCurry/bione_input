
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var grid=null;
	 function templateshow() {
		 if (grid) {
			var centerHeight = $(window).height();
			grid.setHeight(centerHeight-$("#bottom").height()-20);
		}
		 
	}
   	function initGrid(){
   		grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '99%',
			align : 'center',
			columns : [  {
				display : '指标名称',
				name : 'indexNm',
				width : '40%'
			},{
				display : '是否存在',
				name : 'isExist',
				width : '40%',
				render: function(a,b,c){
					if(c){
						return "是";
					}
					else{
						return "否";
					}
				}
			}
			],
			checkbox : false,
			usePager : false,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'local',//从本地获取数据
			data: window.parent.rows,
			sortOrder : 'asc'
		});
   	}
   	
   	function initButton(){
   		var btns=[];
   		btns.push( {
			text : '取消',
			onclick : function(){
				parent.BIONE.closeDialog("importIdx", null, true);
			}
		},{
			text : '保存',
			onclick : save
		});
		BIONE.addFormButtons(btns);
   	}
   	
	function save(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/idx/saveImport",
			dataType : 'json',
			type : "post",
			beforeSend : function(a, b, c) {
				BIONE.showLoading('正在导入数据中...');
			},
			data : {
				pathname:pathname,
				dsId: window.parent.dsId
			},
			success : function(result){
				BIONE.hideLoading();
				BIONE.tip("导入成功");
				window.parent.parent.searchHandler();//刷新树
				parent.BIONE.closeDialog("importIdx", null, true);
				
			},
			error:function(){
				BIONE.hideLoading();
				BIONE.tip("导入失败，请联系系统管理员");
			}
		});
	}
	
	$(function() {
		if(window.parent.rows!=null&&window.parent.rows.Rows!=null&&window.parent.rows.Rows.length>0){
			window.pathname=window.parent.rows.Rows[0].fileNm;
			window.parent.parent.pathname=window.pathname;
		}
		initGrid();
		initButton();
		templateshow();
	});
	
	
	</script>
</head>
<body>
<div id="template.center">
<div id="maingrid">

</div>
</div>
</body>
</html>