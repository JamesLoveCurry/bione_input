<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/mainpage/example.css" />
<script type="text/javascript">
	
	// 快速图标导航
	var searchReportButClick = function (){
		window.top.doMenuClick("messageBac","报文备份","/rpt/frs/rptexport/pbc");
	}
	var idxAnalysisButClick = function(){
		window.top.doMenuClick("detailsel","明细数据查询","frs/integratedquery/detailsel");
	}
	var indexAnalysisButClick = function(){
		window.top.doMenuClick("idxAnalysis","指标追溯分析","/rpt/frame/idx/idxanalysis");
	}
	var searchDetailButClick = function(){
		window.top.doMenuClick("searchDt","明细灵活查询","/report/frame/datashow/detail");
	}
	var searchIndexButClick = function(){
		window.top.doMenuClick("searchIdx","指标灵活查询","/report/frame/datashow/idx");
	}
	
	var info = {
			rpt : {
				hover : "${ctx}/images/classics/imgsearch/rptcolor.png",
				unhover :  "${ctx}/images/classics/imgsearch/rpt.png",
				click : searchReportButClick
			},
			idxAna : {
				hover : "${ctx}/images/classics/imgsearch/idxanalysiscolor.png",
				unhover :  "${ctx}/images/classics/imgsearch/idxanalysis.png",
				click : idxAnalysisButClick
			},
			idxDeA :{
				hover : "${ctx}/images/classics/imgsearch/idxcolor.png",
				unhover :  "${ctx}/images/classics/imgsearch/idx.png",
				click : indexAnalysisButClick
			},
			idxSe :{
				hover : "${ctx}/images/classics/imgsearch/idxsearchcolor.png",
				unhover :  "${ctx}/images/classics/imgsearch/idxsearch.png",
				click : searchIndexButClick
			},
			detailSe :{
				hover : "${ctx}/images/classics/imgsearch/detailcolor.png",
				unhover :  "${ctx}/images/classics/imgsearch/detail.png",
				click : searchDetailButClick
			}
		}
	function menuInit(){
		$.ajax({
			type : "POST",
			url : "${ctx}/rpt/frame/mainpage/isHasInfoRights.json?d="+new Date().getTime(),
			success : function(result) {
				for(var id in result){
					if(result[id] == "1"){
						 $("#"+id).css("cursor","pointer");
						 $("#"+id).bind("click",function(){
							 info[$(this).attr("id")].click();
						 });
						 $("#"+id).bind("mouseover",function(){
							  $(this).css("width","90%");
							  $(this).css("height","90%");
							  $(this).attr("src",info[$(this).attr("id")].hover);
						  });
						 $("#"+id).bind("mouseout",function(){
							  $(this).css("width","100%");
							  $(this).css("height","100%");
							  $(this).attr("src",info[$(this).attr("id")].unhover);
						  });
					}
					else{
						var title = $("#"+id).attr("title",$("#"+id).attr("title")+"(无权限)");
					}
				}
			}
		});
	}
	$(function() {
		menuInit();
		var contentHeight = $(document).height();
		$("#hisrpt_inbox").height(contentHeight - 2);
		$("#info").height(contentHeight - $(".in_box_titbg").height());
		$(".quickNav").width($("#info").width()/7);
		$(".quickNav").height($("#info").height()*0.6);
		$(".quickNav").css("margin-left",$(".quickNav").width()*0.3);
		$(".icon").css("line-height",$(".quickNav").height() + "px");
	});
</script>
<style>
/*快速导航*/
html{background-color:#f5f5f5;}
.bg-color{background-color:#fff;}
 .color1{background-color:#4bbdfb}
 .color2{background-color:#fc8f4a}
 .color3{background-color:#fcca54}
 .color4{background-color:#65d8cb}
 .color5{background-color:#d08ffb}
 .quickNav{border:1px solid #fff;float:left;text-align:center;cursor:pointer;}
 .bg-color{width:100%;height:100%;}
 .icon{width:100%;height:100%;border-radius:50%;font-size:24px;color:#fff;margin:0 auto;}
</style>
</head>
<body>
	<div id="hisrpt_inbox" class="in_box">
		<!-- <div class="in_box_titbg">
			  <div class="in_box_tit"><span class="icon">快速导航</span></div>
		</div> -->
		<div id="info" class="in_box_con" style="width: 100%; overflow: hidden;padding:10px 0;">
			<div id="searchInfo" style="width:100%;">
			    <%-- <img id="idxSe" src="${ctx}/images/classics/imgsearch/idxsearch.png" title="指标灵活查询"  style="width:10%;max-width:120px;float:left;" />
			    <img id="detailSe" src="${ctx}/images/classics/imgsearch/detail.png" title="明细灵活查询"  style="width:10%;margin-left:8%;max-width:120px;float:left;"/>
			    <img id="rpt" src="${ctx}/images/classics/imgsearch/rpt.png" title="公共报表查询" style="width:10%;margin-left:8%;max-width:120px;float:left;"/>
			    <img id="idxAna" src="${ctx}/images/classics/imgsearch/idxanalysis.png"  title="指标分析" style="width:10%;margin-left:8%;max-width:120px;float:left;"/>
			    <img id="idxDeA" src="${ctx}/images/classics/imgsearch/idx.png"  title="指标追溯分析" style="width:10%;margin-left:8%;max-width:120px;float:left;"/> --%>
				<div class="quickNav" title="指标灵活查询">
					<div class="bg-color"><div class="icon-analyze-02 color1 icon" id="idxSe"></div></div>
				</div>
				<div class="quickNav" title="明细灵活查询">
					<div class="bg-color"><div class="icon-search-03 color2 icon" id="detailSe"></div></div>
				</div>
				<div class="quickNav" title="报文备份">
					<div class="bg-color"><div class="icon-search-04 color3 icon" id="rpt"></div></div>
				</div>
				<div class="quickNav" title="明细数据查询">
					<div class="bg-color"><div class="icon-analyze color4 icon" id="idxAna"></div></div>
				</div>
				<div class="quickNav" title="指标追溯分析">
					<div class="bg-color"><div class="icon-analyze-01 color5 icon" id="idxDeA"></div></div>
				</div>
			</div> 
		</div>
	</div>
</body>
</html>