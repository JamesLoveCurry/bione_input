<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var show="${show}";
	var mainform=null;
	var templateFlag=true;
	var attachForm ;
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	
	function intiForm() {
		mainform = $("#basicform").ligerForm({
			fields : [{
				name: 'rptId' ,
				type: 'hidden' 
			},{
				name: 'cfgId' ,
				type: 'hidden' 
			},{
				display : "填报说明",
				name : "fillDesc",
				newline : true,
				type : "textarea",
				width : 772,
				height : 1000,
				attr : {
					//readOnly : true,
					style : "resize: none;"
				}
			} ]
		});
		$("#basicform textarea[name=fillDesc]").attr("readOnly", "true");
		if("${show}"!=""){
			jQuery.metadata.setType("attr", "validate");
			BIONE.validate($("#basicform"));
		}
		if (show != "") {
			$("input").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
		}
	};
	
	function initButton(){
		var btns = [ {
			text : "返回", onclick : function() {
				if("${show}"=="1"){
					//2016-9
					BIONE.closeDialog("rptViewWin");
					return;
				}
				if("${show}"=="2"||"${show}"=="3"){
					BIONE.closeDialog("rptViewWin");
					return;
				}
				else{
					BIONE.closeDialog("rptViewWin");
				}
			}
		}]
		BIONE.addFormButtons(btns);
	}
	function intiTab(){
		$("#tab").ligerTab({
			changeHeightOnResize : true,
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
				if(tabId == 'template'){
					if(!templateFlag){///rpt/frame/rptmgr/info/template?
						content = "<iframe frameborder='0' id='templateFrame' name='templateFrame' style='height:100%;width:100%;' src='${ctx}/report/frame/rptmgr/RptInfoController.mo?doFlag=template&rptId=${rptId}'></iframe>";
						$("#templateFrame").html(content);
						templateFlag = true;
					}
				}
			}
		});
		tabObj = $("#tab").ligerGetTabManager();
	}
	function addTabItem(tabId,tabText,frameId,flag){
		var $centerDom = $(document);
		framCenter = $centerDom.height() - 75;
		tabObj.addTabItem({//添加标签tab页
			tabid : tabId,
			text : tabText,
			showClose : false,
			content : "<div id='"+frameId+"' style='height:" + framCenter
					+ "px;width:100%;'></div>"
		}); 
		this[flag]=false;
	}
	function initData(){
		if ("${rptId}"!="") {
			$.ajax({
				async : true,
				cache : false,
				type : "POST",
				dataType : "JSON",
				contentType : "application/json",
				url : '${ctx}/rpt/frame/rptmgr/info/getReportFrsInfo?rptId=${rptId}',
				success : function(data) {
					window.rptvo=data.rptvo;
					injectFormData(data.rptvo);
				},
				error : function(){
					BIONE.hideLoading();
				}
			});
		} 

	}
	/*注入form 数据*/
	function injectFormData(data) {
		for ( var p in data) {
			var ele = $("[name=" + p + "]");
			// 针对复选框和单选框 处理
			ele.val(data[p]);
		}
	};
	$(function(){
		//intiTab();
		initButton();
		//tabObj.selectTabItem("basic");
		//intiForm();
		//initData();
		//initAttachGrid();
		initAttachForm()
	});
	function initAttachForm(){
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : false,
			columns : [{
				display : "附件名称",
				name : "attachName",
				width : "60%",
			},{
				display : "操作",
				width : "30%",
				render : function(row){
					return "<a href='javascript:void(0)'  onclick='attach_download(\""+row.attachId+"\")' >下载</a>";
				}
			} ],
			dataAction : 'server', //从后台获取数据
			usePager : false, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			pageSize : 10,
			colDraggable : true,
			url : "${ctx}/rpt/frame/rptmgr/info/listAttach?rptId=${rptId}&d="+new Date().getTime(),
			sortName : 'lastUpdateTime',//第一次默认排序的字段
			sortOrder : 'desc', //排序的方式
			rownumbers : true,
			width:'776'
		});
	}
	function attach_download(attachId) {
		$('body').append($('<iframe id="download"/>'));
		$("#download").attr('src', '${ctx}/bione/message/attach/startDownload?attachId=' + attachId);

	}
</script>
</head>
<body>
	<div id='template.center'>
		<div id="tab" style="width: 100%; overflow: hidden;">
			<div tabid="basic" title="基本信息" lselected="true">
				<form id="basicform" action="${ctx}/report/mgr/reportInfo/saveReportInfo" method="post"></form>
			</div>
			<div id="maingrid" style="width: 900px; overflow: hidden;"></div>
		</div>
	</div>
</body>
</html>