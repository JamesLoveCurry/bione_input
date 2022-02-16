<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<style type="text/css">
.content {
	height: 100%;
}
#navtab1 {
	height: 100%;
}
</style>
<script type="text/javascript">
	var app = {
		ctx : '${ctx}',
		parms : null,
		nodeType : '2'		// nodeType: 1.目录; 2.报表; 3.条线
	};
</script>

<link rel="stylesheet" type="text/css" href="${ctx}/js/myPagination/js/msgbox/msgbox.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/js/myPagination/js/myPagination/page.css" />
<script src="${ctx}/js/myPagination/js/myPagination/jquery.myPagination6.0.js" type="text/javascript"></script>
<script language="javascript" type="text/javascript" src="${ctx}/js/myPagination/js/msgbox/msgbox.js"></script>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/dateCal.js"></script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/rptquery.js"></script>
<%@ include file="/common/spreadjs_load.jsp"%>
<script type="text/javascript">
	var pageSize=20;//明细类报表分页展示条数
	var Pagination=null;
	$.extend(app, {
	});
	$(function() {
		//明细类报表分页 20200212
		var height = $(window).height();
		$('#content').height(height - 30);
		$('#navtab1').height(height - 30);
		$(document).on("focusout","#pagination input",jumpPage);
		$(document).on("change","#pageSize",function(val,text){
			changeSize($(this).val());
		});
		
		
		app.rptQuery_initTab();
		var parms = $.parseJSON(${parms});
		app.rptQuery_execQuery(parms.argsArr, parms.rptInfo, parms.lineInfo);
		$('body').append('<iframe id="download" style="visibility: hidden;" src=""></iframe>');
	});
	
	$(window).resize(function(){
		var $window = $(window);
		var height = $window.height();
		$('#content').height(height - 30);
		$('#navtab1').height(height - 30);
		$('div[tabid^=tab_]').height(height - 62);
   });
	
	//分页-输入页码跳转 20200212
	function jumpPage() {
		var size = pageSize;
		var page = $("#pagination").find("input").val();
		p = parseInt(page)
		if (isNaN(p) || p != page) {
			p = oldVal;
			$("#pagination").find("input").val(oldVal);
		}
		if (total < p) {
			p = total;
			Pagination.jumpPage(p);
		}
		if (p != oldVal) {
			var sumPage = parseInt(window.total / size)
			if (window.total % size != 0) {
				sumPage++;
			}
			var isInit = false;
			if (page == sumPage) {
				isInit = true;
				window.islast = true;
			} else {
				if (window.islast) {
					isInit = true;
					window.islast = false;
				}
			}
			oldVal = p;
			Pagination.jumpPage(p);
			View.ajaxJson(null, null, isInit, "/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(), (p - 1) * size + 1, size, false);
		}
	}
		
	//分页-初始化分页栏 20200212
	function initPagination(total){
		window.total = total;
		this.total=total;
		var size=pageSize;
		if(total != null && total != 0){
			if(size == "全部"){
				total=1;
			}else{
				total=parseInt((total-1)/size)+1;
			}
		}else{
			total=1;
		}
		Pagination=$("#pagination").myPagination({
			pageCount: total,
			pageNumber: 10,
			cssStyle: 'liger',
	        panel: {
	            tipInfo_on: true,
	            tipInfo: '<span class="tip">{input}/{sumPage}页,每页显示<select id="pageSize"><option value="20">20</option><option value="50">50</option><option value="100">100</option><option value="全部">全部</option></select>条</span>',
	            tipInfo_css: {
	              width: '25px',
	              height: "20px",
	              border: "1px solid #777",
	              padding: "0 0 0 5px",
	              margin: "0 5px 0 5px",
	              color: "#333"
	            }
	        },
	        ajax: {
	            on: false,
	            onClick: function(page) {
	            	var sumPage =parseInt(window.total/size)
	            	if(window.total%size !=0){
	            		sumPage ++;
	            	}
	            	var isInit = false;
	            	if(page == sumPage){
	            		isInit = true;
	            		window.islast = true;
	            	}
	            	else{
	            		if(window.islast){
	            			isInit = true;
		            		window.islast = false;
	            		}
	            	}
	            	oldVal = page;
	        		View.ajaxJson(null,null,isInit,"/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(),(page-1)*size+1,size,false);
	        		setTimeout(function(){
	        			if($("#pageSize").val()!= size){
	        				$("#pageSize").val(size);
	        				setTimeout(function(){
	    	        			if($("#pageSize").val()!= size){
	    	        				$("#pageSize").val(size);
	    	        			}
	    	        		})
	        			}
	        		});
	            }
	        }
	    });
		$("#pagination").css("overflow","hidden").css("margin-top","0px").css("padding-top","0px");
		$("#pageSize").val(size);
	}
	
	//分页-切换显示条数 20200212
	function changeSize(size){
		pageSize=size;
		View.ajaxJson(null,null,true,"/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(),1,size);
		
	}
</script>
</head>
<body>
	<div id="content" class="content">
		<div id="navtab1" style="width: 100%;overflow:hidden; border:1px solid #D6D6D6; ">
		</div>
		<div id="pagination"></div>
	</div>
</body>
</html>