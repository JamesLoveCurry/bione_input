<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<link rel="stylesheet" href="${ctx}/js/dupontChart/dupontChart.css">
<link rel="stylesheet" href="${ctx}/css/classics/font-awesome/css/font-awesome.css">
<script type="text/javascript" src="${ctx}/js/dupontChart/dupontChart.all.js"></script>
<script type="text/javascript">
	var _id = 0;
	var dupontTree = null;
	var dupontData = "";
	var download = null;
	var dataDate = '${dataDate}';
	$(function() {
		parent.idx=window;
		initData();
		initExport();
		$("#center").css("overflow","auto");
	});
	
	function f_export(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frame/idx/idxanalysis/export",
			data :{
				dupontNode : JSON2.stringify(dupontData)
			},
			dataType : 'json',
			type : "POST",
			beforeSend : function() {
				BIONE.showLoading("生成数据文件中，请稍等");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				var src = '';
				src = "${ctx}/rpt/frame/idx/idxanalysis/download?fileName="+result.fileName+"&name="+encodeURI(encodeURI(result.name));//导出成功后的excell文件地址
				downdload.attr('src', src);//给下载文件显示框加上文件地址链接
			}
		});
	}
	
	function initExport() {
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append (downdload);
	}
	
	function initData(){
		var url="${ctx}/rpt/frame/idx/idxanalysis/idxViewList.json?id=${id}&type=${type}&flag=${flag}&dataDate="+dataDate;
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			type : "POST",
			beforeSend : function() {
				BIONE.showLoading("分析中，请稍等");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				dupontTree = $("#dupontTree").dupontChart({
					showModel : "text",
					lineColor : "#56A2F5",
					sharpWidth : 150, //形状宽度
					 callback :{
				    	  onClick : function(data){
								if(data.data.isRptIndex != "Y" && data.params.type == "idx"){
									f_open_idx(data.id);
								}
								if(data.params.type == "rpt"){
									f_open_rpt(data.data.rptId,data.data.rptType);
								}
								if(data.params.type == "sys"){
									f_open_module(data.data.setId);
								}
							},
				    	  onDblClick : function(data){
				    		  if(data.id != "${id}"  && data.params.type == "idx"){
									window.top.analysisIndex.maintab.addTabItem({
										tabid: data.id,
										text: "指标【"+data.text+"】追溯分析",
										url: "${ctx}/rpt/frame/idx/idxanalysis/tab?id="+data.id+"&type=idx&d="+new Date().getTime(),
										showClose: true
									});
							  }
				    		  if(data.data.setId != "${id}" && data.params.type == "sys"){
									window.top.analysisIndex.maintab.addTabItem({
										tabid: data.data.setId,
										text: "模型【"+data.text+"】追溯分析",
										url: "${ctx}/rpt/frame/idx/idxanalysis/tab?id="+data.data.setId+"&type=model&d="+new Date().getTime(),
										showClose: true
									});
							  }
				    	  }
					 }
				});
				dupontData = result.Rows;
				dupontTree.setData(result.Rows);
				dupontTree.loadData();
			}
		});
	}
	
	function f_open_idx(id){
		window.parent.BIONE.commonOpenDialog("指标详情",
				"rptIdxInfoPreviewBox", 800, 480,
				"${ctx}/report/frame/idx/"+id+"/show?d="+new Date().getTime() , null);

	}
	
	function f_open_rpt(id,type){
		if(type=="01")
			BIONE.commonOpenDialog("报表信息详情", "rptViewWin",800, 480,"${ctx}/rpt/rpt/rptmgr/info/reportInfo?rptId="
					+ id + "&show=2");
		else
			BIONE.commonOpenDialog("报表信息详情", "rptViewWin",800, 480,"${ctx}/rpt/rpt/rptmgr/view/reportView?rptId="
					+ id + "&show=2");
		
	}
	
	function f_open_module(id){
		BIONE.commonOpenDialog("模型信息详情", "rptViewWin",800, 480,'${ctx}/rpt/frame/dataset/infoFrame?datasetId=' + id);
	}
	
	
	
</script>
</head>
<body>
	<div id="template.center">
		<div  id="dupontTree"  style="height:100%;width:100%;overflow:auto;"> 
		</div>
	</div>
</body>
</html>