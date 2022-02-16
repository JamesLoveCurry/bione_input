<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/mainpage/example.css" />
	<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/font-awesome/css/font-awesome.css" />
<script type="text/javascript">

	var mesuNmstr = "${mesuNmstr}";
	var mesuIdstr = "${mesuIdstr}";
	var taskType = "${taskType}";
	$(function() {
		initGrid();
	});

	function openTask(taskType,sts,taskId,dataDate,rptId,orgNo,taskInstanceId){
		if(taskType){
			if(sts=='0'){
				var url="/rpt/frs/rptfill/fillRpt?orgType=" + taskType + "&taskId=" + taskId + "&dataDate=" + dataDate
						+ "&rptId=" + rptId + "&orgNo=" + orgNo + "&fromMainPage=1";
				window.parent.parent.doMenuClick("1b0db592465043659d12bc3647475b86","报表填报",url);
			}else if(sts=='1'){
				var url="/frs/rptsubmit/submit/busiIndex?orgType=" + taskType + "&taskInstanceId=" + taskInstanceId + "&fromMainPage=1";
				window.parent.parent.doMenuClick("5ce622c111cb47c2b904017b4edbdead","报表复核",url);
			}else if(sts=='2'){
				var url="/frs/rptsubmit/submit/busiApprIndex?orgType=" + taskType + "&taskInstanceId=" + taskInstanceId + "&fromMainPage=1";
				window.parent.parent.doMenuClick("d243e8d226ce4e29a9943536e5d304ef","报表审核",url);
			}else if(sts=='6'){
				var url="/frs/rptfill/reject/approve?orgType=" + taskType + "&taskInstanceId=" + taskInstanceId + "&fromMainPage=1";
				window.parent.parent.doMenuClick("0f00fd7ed1834825a8169c9fe4e6aa4d","审批解锁",url);
			}else{
				alert("填报已结束！");
			}
		}
	}
	function initGrid() {
		var url = "${ctx}/frs/mainpage/blacklog/initGrid?&mesuNmstr="+encodeURI(mesuNmstr) + "&taskType=" + taskType;
		 grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '100%',
			columns : [ {
				display : '任务名称',
				name : 'taskNm',
				align : 'left',
				width : '25%',
				render: function(a,b,c){
					var aa = a.taskNm+"("+a.taskobjnm+")";
					var bb =aa;
					return  "<a href='javascript:void(0)' onclick='openTask(\""+a.taskType+"\",\""+a.sts+"\",\""+a.taskId+"\",\""+a.dataDate+"\",\""+a.rptId+"\",\""+a.orgNo+"\",\""+a.taskInstanceId+"\")' title='"+aa+"'>"+bb+"</a>";
				} 
			},{
				display : '任务类型',
				name : 'sts',
				align : 'center',
				width : '10%',
				render: function(a,b,c){
					if(c=='0'){
						return "填报";
					}else if(c=='1'){
						return "复核";
					}else if(c=='2'){
						return "审核";
					}else if(c=='6'){
						return "审批解锁";
					}
				} 
			},{
				display : '数据日期',
				name : 'dataDate',
				align : 'center',
				width : '13%'
			},
            {
                display : '截止日期',
                name : 'endTime',
                align : 'center',
                width : '25%',
                type : 'date',
                format : "yyyy-MM-dd hh:mm:ss"
            },
            {
				display : '机构名称',
				name : 'exeobjnm',
				align : 'center',
				width : '25%'
			}, {
				display : '机构',
				name : 'orgNo',
				hide : true,
                width : '1%'
			}],
			checkbox : false,
			usePager : true,
			pageSize : 10,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : url
		}); 
	}
</script>
</head>
<body>
	<div id="all_div" class="in_box w300">
        <div class="in_box_titbg">
          <div class="in_box_tit" style = "background-color:#4b9efb;"><span class="icon">待办任务</span><span class="more"><a href="javascript:openMore()"></a></span></div>
        </div>
        <div id="maingrid" class="maingrid"></div>
      </div>
</body>
</html>