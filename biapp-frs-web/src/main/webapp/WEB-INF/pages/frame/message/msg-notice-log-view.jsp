<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template19R.jsp">
<head>
<script type="text/javascript">
    $(function() {
		init();
    });   
</script>
<script type="text/javascript">
	var msgLog = new Object();
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
		$("#baseinfo_div").height("60%");
		//baseinfoResize(hasAttachs);
	}
	
	function baseinfoResize(hasAttachs) {
		if (hasAttachs) {
			$("#attach_div").show();
			$("#attach_div").height("40%");
		} else {
			$("#attach_div").hide();
			$("#attach_div").height(0);
		}
	}
	
	function initMessage() {
		var msgState = "${msgState}";
		var msgNum = "${msgNum}";
		if (msgState == "1") {
			window.top.$("#msg span").attr('class', 'new');
			window.top.$("#msg span").html('有新消息(<a style="color:red">'+msgNum+'</a>)');
		} else {
			window.top.$("#msg span").attr('class', 'old');
			window.top.$("#msg span").html("消息");
		}
		window.parent.grid.loadData();
		$.ajax({
			cache : false,
			async : true,
			type : "GET",
			url : "${ctx}/bione/msgNoticeLog/getInfo.json?id=${id}",
			success : function(data) {
				msgLog = data;
				infoShow();
			},
			error : function(result, b) {
				BIONE.showError("获取信息时发生错误，错误代码：" + result.status);
			}
		});
	}
	
	function infoShow() {
		if (msgLog && msgLog.msgTitle) {
			$.ajax({
				url : "${ctx}/bione/msgNoticeLog/getMsgTypeName?id=${id}",
				type : 'get',
				success : function(data){
					if(msgLog.msgDetail == null)
						msgLog.msgDetail = "";
					if(msgLog.sendUser == null)
						msgLog.sendUser = "";
					var htmls = [];
					htmls.push('<div style="width:94%; text-align:center; margin-left:auto; margin-right:auto; padding:5px;font-family:楷体; " >'); // border:1px solid #ccc; 
					htmls.push('  <div style="width:100%; text-align:center; " ><h3>' + msgLog.msgTitle + '</h3></div>');
					htmls.push('  <div class="msgbar" style="width:100%; text-align:center; ">发布时间：' + BIONE.getFormatDate(msgLog.sendTime, "yyyy-MM-dd hh:mm:ss") + '&nbsp; 发送人：' + msgLog.sendUser + '&nbsp;</div>');
					htmls.push('  <div name="info_content" style="width:99%; height:100%; text-align:left; padding-top:5px;" >' + msgLog.msgDetail + '</div>');
					htmls.push('</div>');
					
					$("#baseinfo_div").append(htmls.join(''));
					//baseinfoResize(hasAttachs);
				}
			});
			
		} 
		else {
			BIONE.showError("获取信息失败！");
		}
	}
	/** 初始化附件列表 */
	function initAttachGrid() {
		$.ajax({
			cache : false,
			async : false,
			type : "POST",
			url : "${ctx}/bione/message/attach/${id}/logAttach.json",
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
	function achieveIds(attachId) {
		var path;
		$.ajax({
			cache : false,
			async : false,
			type : "GET",
			url : "${ctx}/bione/message/attach/getRelPath.json",
			dataType : "json",
			data : {attachId : attachId},
			success : function(data) {
				path = data.path;
			}
		});
		return path;
	}
	//下载且删除附件及附件关系
	function attach_download() {
		var ids = [];
		ids = getSelectedIds();
		var relPath = achieveIds(ids[0]);
		if(relPath == ""){
			BIONE.tip('附件不存在');
			return;
		}
		if (ids && ids.length == 1) {
			$.ligerDialog.confirm("附件只能下载一次，下载完即被清空，是否继续？",
					function(yes) {
				if (yes) {
					$('body').append($('<iframe id="download"/>'));
					$("#download").attr('src', '${ctx}/report/frame/datashow/detail/download?path=' + relPath+'&attachId='+ids[0]);
				}
			});
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			BIONE.tip('请选择记录');
		}
	}
</script>

</head>
<body>
	<div id="template.baseinfo_div">
		
	</div>
</body>
</html>