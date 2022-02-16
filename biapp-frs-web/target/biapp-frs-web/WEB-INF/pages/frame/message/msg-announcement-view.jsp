<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template19R.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/bione/encode.js"></script>
<script type="text/javascript">
    $(function() {
		init();
    });   
</script>
<script type="text/javascript">
	var msgObj = new Object();
	var hasAttachs = false;
	
	var attachDivLineCnt = 4; // 附件grid每行显示的附件个数
	var attachDivLineHeight = 22; // 附件grid的行高
	var attachDivBasicHeight = 111; // 附件grid固定的高度部分，不能改！！
	var attachDivTotalHeight = 177; // 附件grid的实际总高度！
	
	function init() {
		tmpltResize();
		initAttachGrid();
		initMessage();
	}
	function tmpltResize() {
		$("#editor_div").height(0);
		$("#editor_div").hide();
		$("#bottom").height(0);
		$("#bottom").hide();
		$("#attach_div").width("95%");
		$("#attach_div").hide();
		$("#center").height($(document).height());
		$("#center").css({ "background-color" : "#FFFFFF" }); //scroll
		$("#baseinfo_div").width("100%");
		$("#baseinfo_div").css({ "background-color" : "#FFFFFF", "overflow-y" : "auto", "text-align" : "center" }); //scroll , "border" : "1px solid" 
		//$("#baseinfo_div").height("80%");
		//$("#baseinfo_div").height($("#center").height() - $("#attach_div").height() - 8);
		baseinfoResize(hasAttachs);
	}
	function baseinfoResize(hasAttachs) {
		if (hasAttachs) {
			$("#attach_div").show();
			$("#attach_div").height(attachDivTotalHeight);
		} else {
			$("#attach_div").hide();
			$("#attach_div").height(0);
		}
		$("#baseinfo_div").height($("#center").height() - $("#attach_div").height() - 8);
	}
	function initMessage() {
		$.ajax({
			cache : false,
			async : true,
			type : "GET",
			url : "${ctx}/bione/msg/announcement/${id}",
			success : function(data) {
				msgObj = data;
				infoShow();
			},
			error : function(result, b) {
				BIONE.showError("获取信息时发生错误，错误代码：" + result.status);
			}
		});
	}
	function infoShow() {
		if (msgObj && msgObj.announcementTitle) {
			/* $("#baseinfo_div").append('<div style="width:90%; text-align:center; border:1px solid;" ><h1>' + msgObj.msgTitle + '</h1></div>');
			$("#baseinfo_div").append('<div class="msgbar" style="width:90%; text-align:center; border:1px solid; ">发布时间：' + BIONE.getFormatDate(msgObj.lastUpdateTime, "yyyy-MM-dd hh:mm:ss") + 
					'&nbsp; 来源：' + msgObj.msgSrc + '&nbsp;');
			$("#baseinfo_div").append('<div name="info_content" style="width:90%; height:100%; text-align:center; border:1px solid; " >' + msgObj.msgDetail + '</div>'); */
			//===========================================================
			var htmls = [];
			htmls.push('<div style="width:94%; text-align:center; margin-left:auto; margin-right:auto; padding:5px; " >'); // border:1px solid #ccc; 
			htmls.push('  <div style="width:100%; text-align:center; " ><h1>' + htmlEncode(msgObj.announcementTitle) + '</h1></div>');
			htmls.push('  <div class="msgbar" style="width:100%; text-align:center; ">发布时间：' + BIONE.getFormatDate(msgObj.lastUpdateTime, "yyyy-MM-dd hh:mm:ss") + '&nbsp; 来源：' + typeRender(msgObj.announcementType) + '&nbsp;</div>');
			htmls.push('  <div name="info_content" style="width:99%; height:100%; text-align:center; " >' + msgObj.announcementDetail + '</div>');
			htmls.push('</div>');
			//
			$("#baseinfo_div").append(htmls.join(''));
			//6
			baseinfoResize(hasAttachs);
		} 
		else {
			BIONE.showError("获取信息失败！");
		}
	}
	/** 初始化附件列表 */
	function initAttachGrid() {
		$.ajax({
			cache : false,
			async : true,
			type : "POST",
			url : "${ctx}/bione/message/attach/${id}/listAttach.json",
			dataType : "json",
			success : function(data) {
				if (data && data.Rows && data.Rows.length>0) {
					hasAttachs = true;
					// compute attach-div height
					var mlc = Math.floor(data.Rows.length / attachDivLineCnt);
					if (data.Rows.length / attachDivLineCnt - mlc > 0) { mlc += 1; }
					attachDivTotalHeight = attachDivBasicHeight + attachDivLineHeight * mlc; 
					// resize
					baseinfoResize(hasAttachs);
					// set buttons and grid
					initAttachButtons();
					loadAttachGrid(data.Rows, attachDivLineCnt);
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	function initAttachButtons() {
		var btns = [
		{
			text : '下载',
			click : attach_download,
			icon : 'download',
			operNo : 'attach_download'
		} ];
		loadAttachToolbar(btns);
	}
	/** attach utility */
	function achieveIds() {
		ids = [];
		ids = getSelectedIds();
	}
	function attach_download() {
		achieveIds();
		if (ids && ids.length > 0) {
			var attachId = ids.join(",");
			$('body').append($('<iframe id="download"/>'));
			$("#download").attr('src', '${ctx}/bione/message/attach/startDownload?attachId=' + attachId);
		} else {
			BIONE.tip('请选择记录');
		}
	}
	//公告类型显示转化
    function typeRender(data) {
		if (data == '01') {
			return "系统信息";
		}else if(data == '02'){
			return "业务信息";
		}else {
			return data;
		}
	}
</script>

</head>
<body>
	<div id="template.baseinfo_div">
		
	</div>
</body>
</html>