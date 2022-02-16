<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<title>新增任务</title>
<script type="text/javascript">
	//指标信息缓存对象
	var templateInfo = '${templateInfo}';
	var canEdit = '${canEdit}';
	var templateVO = {
		template : null,
		detailList : []
	}
	//基本信息
	var taskManage = {
		templateManage : null,
		detailManager : null
	};
	//初始化
	$(function() {
		initData();
		initTab();
		initBtn();
	});

	function initTab() {
		var height = $(document).height() - 33;
		$("#tab").append('<div tabid="tab1" title="指标补录模板信息" />');
		$("#tab").append('<div tabid="tab2" title="指标补录配置信息" />');
		tabObj = $("#tab")
				.ligerTab(
						{
							contextmenu : false,
							onBeforeSelectTabItem : function() {
								return true;
							},
							onAfterSelectTabItem : function(tabid) {
								var src = "";
								if (tabid == "tab2") {
									src = "${ctx}/rpt/input/idxdatainput/dataInputConfig?d="
											+ new Date().getTime();
								}
								loadFrame(tabid, src, tabid + "frame");
							}
						});
		loadFrame("tab1", "${ctx}/rpt/input/idxdatainput/dataInputBase?d="
				+ new Date().getTime(), "tab1frame");
	}
	function loadFrame(tabId, src, id) {
		var height = $(document).height() - 75;
		if ($('#' + id).attr('src')) {
			return;
		}
		var frame = $('<iframe/>');
		frame.attr({
			id : id,
			frameborder : 0,
			src : src
		}).css("height", height);
		$('div[tabId=' + tabId + ']').append(frame);
	}

	function initData() {
		if (templateInfo == null || templateInfo == ""
				|| typeof templateInfo == "undefined")
			return;
		var data = JSON2.parse(templateInfo);
		if (data.template != null) {
			templateVO.template = {
				templateId : data.template.templateId,
				templateType : data.template.templateType,
				templateNm : data.template.templateNm,
				remark:data.template.remark,
				catalogId : data.template.catalogId
			};

		}
		if (data.detailList != null) {
			for ( var i = 0; i < data.detailList.length; i++) {
				templateVO.detailList[i] = {
					cfgId : data.detailList[i].cfgId,
					indexNo : data.detailList[i].idxNo,
					indexNm: data.detailList[i].indexNm,
					dimFilterInfos : [],
					orderNum : data.detailList[i].orderNum,
					orgMode : data.detailList[i].orgMode,
					templateId : data.detailList[i].templateId,
					cfgNm : data.detailList[i].cfgNm,
					orgNo:data.detailList[i].orgNo,
					orgNm:data.detailList[i].orgNm,
					ruleId : data.detailList[i].ruleId,
					ruleNm : data.detailList[i].ruleNm
				};
				var dimFilterInfoList = data.detailList[i].dimFilterInfos;
				if(dimFilterInfoList!=null && typeof dimFilterInfoList !="undefined")
				{
					var filters = [];
					for(var j=0;j<dimFilterInfoList.length;j++){
						filters.push({
							filterVal:dimFilterInfoList[j].filterVal,
							filterText:dimFilterInfoList[j].filterText,
							filterMode:dimFilterInfoList[j].filterMode,
							dimNo:dimFilterInfoList[j].dimNo,
							dimNm:dimFilterInfoList[j].dimNm,
							eVal:dimFilterInfoList[j].eVal,
							indexNo:dimFilterInfoList[j].idxNo
						});
					}
					templateVO.detailList[i].dimFilterInfos =  filters;
				}
			}
		}
	}

	function initBtn() {
		if(canEdit=="true"){
			var buttons = [];

			var main = parent || window;
			var dig =  main.jQuery.ligerui.get("rptDataInputBox");
			if(dig){
				buttons.push({
					text : '取消',
					onclick : f_close
				});
			}
			buttons.push({
				text : '保存',
				onclick : f_save
			});
			BIONE.addFormButtons(buttons);
			jQuery.metadata.setType("attr", "validate");
			BIONE.validate("#mainform");
		}

	}

	function f_save() {
		operTask();
	}

	var isNeedCheck = false;
	function operDetail(){
		tabObj.selectTabItem("tab2");
		if (taskManage.templateManage != null)
			taskManage.templateManage.gatherData();
	}
	
	function doTask(){
		//执行保存操作
		$
			.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/input/idxdatainput/saveTemplate?d="
						+ new Date().getTime(),
				dataType : 'json',
				type : "post",
				data : {
					"templateVO" : JSON2.stringify(templateVO)
				},
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result) {
					window.parent.refreshTree();
					BIONE.tip('保存成功!');
					f_close();
				},
				error : function(result, b) {
					BIONE.tip('保存失败,发现系统错误 <BR>错误码：' + result.status);
				}
			});
	}
	
	function operTask() {
		isNeedCheck = true;
		tabObj.selectTabItem("tab1");
		taskManage.detailManager.gatherData();
	}
	function f_close() {
		BIONE.closeDialog("rptDataInputBox");
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>