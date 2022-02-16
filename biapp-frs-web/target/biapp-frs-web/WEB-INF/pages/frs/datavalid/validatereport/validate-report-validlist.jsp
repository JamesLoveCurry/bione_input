<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
	var mainform;
	var grid;
	var taskId = '${taskId}';
	var orgNo = '${orgNo}';
	var dataDate = '${dataDate}';
	$(function() {
		initForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	function initForm() {
		mainform = $("#search").ligerForm({
			fields : [ {
				display : "报表名称",
				name : "rptNm",
				newline : false,
				type : "text",
				width : '140',
				cssClass : "field",
				labelWidth : '90',
				attr : {
					op : "like",
					field : "t2.rpt_nm"
				}
			}, {
				display : "校验状态", 
				name : "checkSts", 
				newline : false, 
				type : "select",
				width : '140', 
				cssClass : "field", 
				labelWidth : '90',
				comboboxName : "checkSts_sel", 
				attr : { 
					op : "=", 
					field : "check_sts"
				},
				options : { 
					data : [{id : "00", text : "未校验"},{id : "05", text : "通过"},{id : "06", text : "未通过"}]
				}
			}]
		});
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			columns : [{
				display : '报表名称',
				name : 'RPT_NM',
				width : "25%",
				align : "left"
			}, {
				display : '校验状态',
				name : 'CHECK_STS',
				width : "12%",
				render : function(data){
  					var sts = "";
  					var warnSts = "";
					if(data.CHECK_STS == "00"){
						sts = "未校验";
					}else if(data.CHECK_STS == "05"){
						sts = "通过";
					}else if(data.CHECK_STS == "06"){
						sts = "未通过";
					}
					var warnSts = "";
					if(data.WARN_CHECK_STS == "00"){
						warnSts = "未校验";
					}else if(data.WARN_CHECK_STS == "05"){
						warnSts = "通过";
					}else if(data.WARN_CHECK_STS == "06"){
						warnSts = "未通过";
					}
					return sts+"/"+warnSts;
				}
			}, {
				display : '逻辑校验总数',
				name : 'VALIDCOUNT',
				width : "10%"
			}, {
				display : '表内校验不通过数',
				name : 'INDEFAULTCOUNT',
				width : "12%"
			}, {
				display : '表间校验不通过数',
				name : 'OUTDEFAULTCOUNT',
				width : "12%"
			}, {
				display : '预警校验总数',
				name : 'WARNCOUNT',
				width : "10%"
			}, {
				display : '预警校验不通过数',
				name : 'WARNDEFAULTCOUNT',
				width : "12%"
			}],
			height : '99%',
			width : '100%',
			checkbox : false,
			rownumbers : true,
			usePager : true,
            isScroll : true,
            alternatingRow : true,//附加奇偶行效果行
            colDraggable : false,
			dataAction : 'server',//从后台获取数据
            method : 'post',
			url : "${ctx}/frs/validatereport/getRptValidList?taskId="+taskId+"&orgNo="+orgNo+"&dataDate="+dataDate,
			toolbar : {}
		});
	}
	
	//初始化按钮
    function initButtons() {
        var btns = [ {
            text : '统计图',
            click : createValidChart,
            icon : 'fa-book'
        }, {
            text : '校验结果',
            click : getRptValidDetail,
            icon : 'icon-view'
        }];
        BIONE.loadToolbar(grid, btns, function() {});
    }
	
	//校验结果
	function getRptValidDetail(){
		var row = grid.getSelectedRow();
		if(row != null){
			var url = "${ctx}/rpt/frs/rptfill/showTab?path=rptfill-todowork-check-result"
					+"&rptId="+row.RPT_ID+"&templateId="+row.CFG_ID+"&dataDate="+dataDate+"&orgNo="+orgNo+"&type=02"
					+"&taskId="+taskId+"&taskInsId="+ row.TASK_INSTANCE_ID;
			var height = $("#center").height() - 20;
			BIONE.commonOpenDialog("校验结果详情", "reportWin", 860, height, 
					url);
		}else{
			BIONE.tip("请选择一条数据！");
		}
	}
	
	//生成统计图
	function createValidChart(){
		var row = grid.getSelectedRow();
		if(row != null){
			var height = $("#center").height() - 10;
			BIONE.commonOpenDialog("近一年未通过校验数统计", "reportWin", 860, height, 
					"${ctx}/frs/validatereport/createValidChart?taskId="+taskId+"&rptId="+row.RPT_ID+"&dataDate="+dataDate+"&orgNo="+orgNo);
		}else{
			BIONE.tip("请选择一条数据！");
		}
	}
	
	//报表名称链接
	function taskObjNmRender(rowdata) {
		var aa = rowdata.taskObjNm;
		return "<a href='javascript:void(0)' class='link' onclick='onShowRptValid(\""
				+ rowdata.TASK_ID
				+ "\",\""
				+ rowdata.DATA_DATE
				+ "\",\""
				+ rowdata.EXE_OBJ_ID
				+ "\")' title='"
				+ rowdata.TASK_NM
				+ "'>" + rowdata.TASK_NM + "</a>";
	};
</script>
<title>校验简报</title>
</head>
<body>
</body>
</html>