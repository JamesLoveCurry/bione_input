<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template8.jsp">

<style type="text/css">
.link {
	color: #065FB9;
	text-decoration: underline;
	
}
</style>

<script type="text/javascript">
	var grid;
	var taskId = "${taskId}";
	var dataDate = "${dataDate}";
	var orgTypes = "${orgTypes}";
	$(function() {
		//初始化grid
		initGrid();
	});
	//
	function f_open_view(a){
		BIONE.commonOpenLargeDialog("报表历史信息","rptList","${ctx}/rpt/frame/mainpage/showMoreRptList?rptId="+a);
	}
	
	function openRptWin(objectId, objectName) {
		var width = window.screen.width * 0.9;
		var height = window.screen.height * 0.8;
		$.ajax({
		   type: "POST",
		   url: "${ctx}/rpt/frame/rptfav/query/rptType",
		   data: {
				rptId: objectId
			},
		   success: function(data){
			   if ('01' == data) {
					window.top.BIONE.commonOpenDialog("外部报表", "alertRptIndexs", width, height,'${ctx}/report/frame/datashow/rpt/show/' + objectId);
				} else if ('02' == data) {
					window.top.BIONE.commonOpenDialog("平台报表", "alertRptIndexs", width, height,'${ctx}/rpt/rpt/rptplatshow/show?state=1&rptId=' + objectId+"&rptNm="+encodeURI(objectName));
				}
		   }
		});
	}

	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '报表名称',
				name : 'rptNm',
				width : '30%',
				align : 'center',
				render:function (rowdata, index, value){
					return "<a href=\"javascript:openRptWin('"+rowdata.rptId+"','"+rowdata.rptNm+"')\">"+value+"</a>";
				}
			},{
				display : '最近访问时间',
				name : 'accessTime',
				width : '40%',
				align : 'center',
				type:'date',
				format:'yyyy-MM-dd hh:mm:ss'
			},{
				display : '历史详情查询',
				name:"veiw",
				width : '25%',
				align : 'center',
				render:function(date){
					return "<a href='javascript:void(0)' class='link' onclick='f_open_view(\""+date.rptId+"\")'>查看</a>";
				}
			} ],
			checkbox : false,
			usePager : true,
			rownumbers : true,
			isScroll : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false, 
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/rpt/frame/mainpage/getReportHistory?flag=more&d="+new Date().getTime(),
			sortName : 'accessTime',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			width : '99.8%',
			height:$("#center").height()-4
		});
		
	//rpt浏览
		
	}
	
</script>
<title>Insert title here</title>
</head>
<body>
</body>
</html>