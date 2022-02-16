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
	var download = null;
	var dupontData = "";
	$(function() {
		parent.dim=window;
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
		var url="${ctx}/rpt/frame/idx/idxanalysis/dimList.json?id=${id}&type=${type}";
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
								if(data.params.type == "idx"){
									f_open_idx(data.id);
								}
								if(data.params.type == "rpt"){
									f_open_rpt(data.data.rptId,data.data.rptType);
								}
								if(data.params.type == "dim"){
									f_open_dim(data.id);
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
				    		  if(data.id != "${id}"  && data.params.type == "dim"){
									window.top.analysisIndex.maintab.addTabItem({
										tabid: data.id,
										text: "维度【"+data.text+"】追溯分析",
										url: "${ctx}/rpt/frame/idx/idxanalysis/tab?id="+data.id+"&type=dim&d="+new Date().getTime(),
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
		window.parent.parent.BIONE.commonOpenFullDialog("指标详情",
				"rptIdxInfoPreviewBox",
				"${ctx}/report/frame/idx/"+id+"/show?d="+new Date().getTime() , null);

	}
	
	function f_open_rpt(id,type){
		if(type=="01")
			parent.parent.BIONE.commonOpenFullDialog("报表信息详情", "rptViewWin","${ctx}/rpt/rpt/rptmgr/info/reportInfo?rptId="
					+ id + "&show=2");
		else
			parent.parent.BIONE.commonOpenFullDialog("报表信息详情", "rptViewWin","${ctx}/rpt/rpt/rptmgr/view/reportView?rptId="
					+ id + "&show=2");
		
	}
	
	function f_open_dim(id){
		parent.parent.BIONE.commonOpenFullDialog("维度类型详情", "rptDimTypeInfoUpdateWin","${ctx}/rpt/frame/dimCatalog/dimType/update?dimTypeNo=" + id+"&flag=view");
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