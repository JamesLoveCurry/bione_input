<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var show="${show}";
	var requireFlag=true;
	var authorityFlag=true;
	var dataFlag=true;
	var mainform=null;
	var reportInfo={
		reportBaseInfo:null,
		reportShowInfo:null,
		reportRequireInfo: null,
		reportDimTypes: null,
		reportDataItems:  null,
		reportModuleInfo: null
	};
	//基本信息
	var reportManage={
		reportAuthority: null,
		reportData: null,
		reportRequire: null
	};
	
	
	
	function intiForm() {
		mainform = $("#basicform").ligerForm({
			fields : [{
				name: 'rptId',
				type: 'hidden'
			},{
				name: 'cfgId',
				type: 'hidden'
			},{
				display: '报表编号',
				name: 'rptNum',
				type: 'text',
				newline: true,
				group : "报表基本信息",
				groupicon : "${ctx}/images/classics/icons/communication.gif",
				validate:{
					required: true,
					remote : "${ctx}/frs/integratedquery/rptmesquery/info/validateRptNum?rptId=${rptId}&d="+new Date().getTime(),
					messages : {
						remote : "该报表编号已经存在"
					}
				}
			},{
				display: '报表目录',
				name :'catalogId',
				comboboxName:'catalogNameBox',
				type: 'select',
				newline: false,
				options : {
					onBeforeOpen : function() {
						window.parent.selectedTab=window;
						window.parent.BIONE
								.commonOpenDialog(
										"报表目录选择",
										"catalogTreeWin",
										400,
										380,
										'${ctx}/frs/integratedquery/rptmesquery/info/showRptTree',
										null);
						return false;
					}
				},
				validate:{
					required: true
				}
			},{
				display: '排列顺序',
				name: 'rankOrder',
				type: 'text',
				newline:true,
				validate:{
					digits: true
				}
			},{
				display: '展示优先级',
				name :'showPriority',
				type: 'select',
				comboboxName:'showPriorityBox',
				newline: false,
				options:{
					initValue: '0',
					data: [{ 
						id: '0',
						text: '最高' 
					},{
						id: '1',
						text: '1' 
					},{
						id: '2',
						text: '2' 
					},{
						id: '3',
						text: '3' 
					},{
						id: '4',
						text: '4' 
					},{
						id: '5',
						text: '5' 
					},{
						id: '6',
						text: '6' 
					},{
						id: '7',
						text: '7' 
					},{
						id: '8',
						text: '8' 
					},{
						id: '9',
						text: '最低' 
					}]
				}
			},{
				display: '报表名称',
				name :'rptNm',
				type: 'text',
				newline: true,
				validate:{
					required: true,
					remote : {
						url: "${ctx}/frs/integratedquery/rptmesquery/info/validateRptNm?rptId=${rptId}&d="+new Date().getTime(),
						type: 'get',
						data : {
							rptNm : $("#rptNm").val()
						}
					},
					messages : {
						remote : "该报表名称已经存在"
					}
				}
			},{
				display: '报表用途',
				name :'rptUse',
				comboboxName:'rptUseBox',
				type: 'select',
				newline: false,
				options:{
					isMultiSelect:true,
					data: [{ 
						id: '01',
						text: '外部监督' 
					},{
						id: '02',
						text: '内部审计' 
					},{
						id: '03',
						text: '绩效考核' 
					},{
						id: '04',
						text: '风险管理' 
					},{
						id: '05',
						text: '运营支持' 
					},{
						id: '06',
						text: '营销扩展' 
					}]
				}
			},{
				display : '责任部门',
				name : 'dutyDept',
				newline : true,
				type : "select",
				comboboxName : "dutyDeptBox",
				options : {
						url : "${ctx}/report/frame/idx/defDeptList"
				},
				validate:{
					required: true
				}
			}, {
				display: '币种',
				name :'rptCurrtype',
				comboboxName:'rptCurrtypeBox',
				type: 'select',
				newline: false,
				options:{
					isMultiSelect:true,
					data: [{ 
						id: '01',
						text: '原币' 
					},{
						id: '02',
						text: '人民币' 
					},{
						id: '03',
						text: '外币折美元' 
					},{
						id: '04',
						text: '外币折人民币' 
					},{
						id: '05',
						text: '本外币合计' 
					}]
				}
			},{
				display: '金额单位',
				name :'dataUnit',
				comboboxName:'dataUnitBox',
				type: 'select',
				newline: true,
				options:{
					data: [{ 
						id: '01',
						text: '元' 
					},{
						id: '02',
						text: '千' 
					},{
						id: '03',
						text: '万' 
					},{
						id: '04',
						text: '十万' 
					},{
						id: '05',
						text: '百万' 
					},{
						id: '06',
						text: '亿' 
					},{
						id: '07',
						text: '十亿' 
					}]
				}
			}, {
				display: '生成频率',
				name :'rptCycle',
				comboboxName:'rptCycleBox',
				type: 'select',
				newline: false,
				options:{
					isMultiSelect:true,
					data: [{ 
						id: '01',
						text: '日' 
					},{
						id: '02',
						text: '月' 
					},{
						id: '03',
						text: '季' 
					},{
						id: '04',
						text: '年' 
					},{
						id: '10',
						text: '周' 
					},{
						id: '11',
						text: '旬' 
					},{
						id: '12',
						text: '半年' 
					}]
				}
			},{
				display: '折算汇率表',
				name :'parties',
				comboboxName:'partiesBox',
				type: 'select',
				newline: true,
				options:{
					data: [{ 
						id: '01',
						text: '内部汇率表' 
					},{
						id: '02',
						text: '年终汇率表' 
					}]
				}
			},{
				display: '启用时间',
				name :'startDate',
				type: 'text',
				newline: false,
				attr:{
					readOnly: true
				}
			},{
				display: '精度',
				name :'dataPrecision',
				comboboxName:'dataPrecisionBox',
				type: 'select',
				newline: true,
				options:{
					data: [{ 
						id: '0',
						text: '0' 
					},{
						id: '1',
						text: '1' 
					},{
						id: '2',
						text: '2' 
					},{
						id: '3',
						text: '3' 
					},{
						id: '4',
						text: '4' 
					},{
						id: '5',
						text: '5' 
					}]
				}
			},{
				display: '报表状态',
				name :'rptSts',
				comboboxName:'rptStsBox',
				type: 'select',
				newline: false,
				options:{
					initValue: 'Y',
					data: [{ 
						id: 'Y',
						text: '启用 ' 
					},{
						id: 'N',
						text: '停用' 
					}]
				}
			},{
				display: '信息权限',
				name :'infoRights',
				type: 'select',
				comboboxName:'infoRightsBox',
				newline: true,
				options:{
					initValue: 'N',
					data: [{ 
						id: 'Y',
						text: '有' 
					},{
						id: 'N',
						text: '无' 
					}]
				}
			},{
				display : "报表描述",
				name : "rptDesc",
				newline : true,
				type : "textarea",
				width : '300',
				validate:{
					maxlength : 500
				}
			}]
		});

		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#basicform"));
		$("#rptDesc").css("width","410");
		$("#rptDesc").css("resize","none");
		$("#rptNum").focus().blur();
		
		
		if (show != "") {
			$("#rptNum").parent().parent().parent("ul").hide();
			$("#catalogId").parent().parent().parent("ul").hide();
			$("#rankOrder").parent().parent().parent("ul").hide();
			$("#showPriority").parent().parent().parent("ul").hide();
			$("#rptNum").parent().parent().parent("ul").hide();
			$("#dataPrecision").parent().parent().parent("ul").hide();
			$("#rptSts").parent().parent().parent("ul").hide();
			$("#infoRights").parent().parent().parent("ul").hide();
			$("#startDate").parent().parent().parent("ul").show();
			
			$("#rptNum").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
			$.ligerui.get("catalogNameBox").setDisabled();
			$("#catalogNameBox").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
			$("#rptNm").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
			$("#rankOrder").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
			
			$.ligerui.get("rptUseBox").setDisabled();
			$("#rptUseBox").css("color", "#333").attr("readOnly", "true")
					.removeAttr("validate");
			$.ligerui.get("dutyDeptBox").setDisabled();
			$("#dutyDeptBox").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
			$.ligerui.get("partiesBox").setDisabled();
			$("#partiesBox").css("color", "#333").attr("readOnly", "true")
					.removeAttr("validate");
			$.ligerui.get("rptCurrtypeBox").setDisabled();
			$("#rptCurrtypeBox").css("color", "#333").attr("readOnly", "true")
					.removeAttr("validate");
			$.ligerui.get("dataUnitBox").setDisabled();
			$("#dataUnitBox").css("color", "#333").attr("readOnly", "true")
					.removeAttr("validate");
			$.ligerui.get("dataPrecisionBox").setDisabled();
			$("#dataPrecisionBox").css("color", "#333").attr("readOnly", "true")
					.removeAttr("validate");
			$.ligerui.get("rptCycleBox").setDisabled();
			$("#rptCycleBox").css("color", "#333").attr("readOnly", "true")
					.removeAttr("validate");
			$("#startDate").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
			$.ligerui.get("rptStsBox").setDisabled();
			$("#rptStsBox").css("color", "#333").attr("readOnly", "true")
					.removeAttr("validate");
			
			$.ligerui.get("showPriorityBox").setDisabled();
			$("#showPriorityBox").css("color", "#333").attr("readOnly", "true")
					.removeAttr("validate");
			$.ligerui.get("infoRightsBox").setDisabled();
			$("#infoRightsBox").css("color", "#333").attr("readOnly", "true")
					.removeAttr("validate");
			$("#rptDesc").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
		}
		else{
			$("#startDate").parent().parent().parent("ul").hide();
		}
	};
	
	function initButton(){
		var btns = [ {
			text : "取消", onclick : function() {
				if("${show}"=="1"){
					parent.currentNode=null;
					parent.$("#tab").ligerGetTabManager().removeSelectedTabItem();
					return;
				}
				if("${show}"=="2"||"${show}"=="3"){
					BIONE.closeDialog("rptViewWin");
					return;
				}
				else{
					parent.currentNode=null;
					parent.removeInfo();
				}
			}
		} ];
		if("${show}"==""){
			btns.push( {
				text : "保存", onclick : save
			});
		}
		BIONE.addFormButtons(btns);
	}
	
	function intiTab(){
		
		$("#tab").ligerTab({
			changeHeightOnResize : true,
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
				if(tabId == 'require'){
					if(!requireFlag){
						content = "<iframe frameborder='0' id='editRequire' name='editRequire' style='height:100%;width:100%;' src='${ctx}/frs/integratedquery/rptmesquery/info/reportRequire?rptId=${rptId}'></iframe>";
						$("#requireFrame").html(content);
						requireFlag = true;
					}
				}
				
				if(tabId == 'authority'){
					if(!authorityFlag){
						content = "<iframe frameborder='0' id='editAuthority' name='editAuthority' style='height:100%;width:100%;' src='${ctx}/frs/integratedquery/rptmesquery/info/reportAuthority?rptId=${rptId}'></iframe>";
						$("#authorityFrame").html(content);
						authorityFlag = true;
					}
				}
				if(tabId == 'data'){
					if(!dataFlag){
						content = "<iframe frameborder='0' id='editData' name='editData' style='height:100%;width:100%;' src='${ctx}/frs/integratedquery/rptmesquery/info/reportData?rptId=${rptId}'></iframe>";
						$("#dataFrame").html(content);
						dataFlag = true;
					}
				}
				
			}
		});
		tabObj = $("#tab").ligerGetTabManager();
		addTabItem("require","需求信息","requireFrame",'requireFlag');
		if("${show}"!="3"){
			if("${show}"!=""){
				//addTabItem("authority","权限信息","authorityFrame",'authorityFlag');
			}
			if("${show}"==""){
				addTabItem("data","技术信息","dataFrame",'dataFlag');
			}
		}
		
		
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
				type : "GET",
				dataType : "JSON",
				contentType : "application/json",
				url :"${ctx}/frs/integratedquery/rptmesquery/info/getReportInfo?rptId=${rptId}",
				success : function(data) {
					if(data.rptvo!=null){
						reportInfo={
								reportBaseInfo: {
									rptId: data.rptvo.rptId,
									catalogId: data.rptvo.catalogId,
									catalogNm: data.rptvo.catalogNm,
									rptNum: data.rptvo.rptNum,
									rptNm: data.rptvo.rptNm,
									rankOrder:data.rptvo.rankOrder,
									rptUse: data.rptvo.rptUse,
									dutyDept: data.rptvo.dutyDept,
									parties: data.rptvo.parties,
									rptCurrtype: data.rptvo.rptCurrtype,
									dataUnit: data.rptvo.dataUnit,
									dataPrecision: data.rptvo.dataPrecision,
									rptCycle: data.rptvo.rptCycle,
									rptGenerateType: data.rptvo.rptGenerateType,
									startDate: data.rptvo.startDate,
									rptSts: data.rptvo.rptSts,
									rptDesc: data.rptvo.rptDesc,
									cfgId: data.rptvo.cfgId,
									showPriority: data.rptvo.showPriority,
									infoRights: data.rptvo.infoRights
								},
								reportShowInfo:{
									serverId:data.rptvo.serverId,
									serverNm:data.rptvo.serverNm,
									paramtmpId: data.rptvo.paramtmpId,
									paramtmpNm: data.rptvo.paramtmpNm,
									searchPath: data.rptvo.searchPath,
									rptSrcPath: data.rptvo.rptSrcPath
								},
								reportRequireInfo: data.rptvo.requireExplain,
								reportDimTypes: data.dimTypes,
								reportDataItems:  data.dataItems,
								reportModuleInfos: data.modules
							};
							injectFormData(reportInfo.reportBaseInfo);
							$.ligerui.get("catalogNameBox").selectValue(reportInfo.reportBaseInfo.catalogId);
							$("#catalogNameBox").val(reportInfo.reportBaseInfo.catalogNm);
					}
					
					
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
		// 下面是更新表单的样式
		var managers = $.ligerui.find($.ligerui.controls.Input);
		for ( var i = 0, l = managers.length; i < l; i++) {
			// 改变了表单的值，需要调用这个方法来更新ligerui样式
			var o = managers[i];
			o.updateStyle();
			if (managers[i] instanceof $.ligerui.controls.TextBox)
				o.checkValue();
		}
	};
	function getData(){
		var reportBaseInfo= {
			rptId:	$("#rptId").val(),
			cfgId: $("#cfgId").val(),
			catalogId: $("#catalogId").val(),
			rptNum: $("#rptNum").val(),
			rptNm: $("#rptNm").val(),
			rankOrder:$("#rankOrder").val(),
			rptUse: $("#rptUse").val(),
			dutyDept: $("#dutyDept").val(),
			parties: $("#parties").val(),
			rptCurrtype: $("#rptCurrtype").val(),
			dataUnit: $("#dataUnit").val(),
			dataPrecision: $("#dataPrecision").val(),
			rptCycle: $("#rptCycle").val(),
			rptGenerateType: $("#rptGenerateType").val(),
			rptSts: $("#rptSts").val(),
			rptDesc: $("#rptDesc").val(),
			showPriority: $("#showPriority").val(),
			infoRights: $("#infoRights").val()
		};
		return reportBaseInfo;
	}
	function save(){
		if(!$("#basicform").valid()){
			tabObj.selectTabItem("basic");
			return;
		}
		var reportSaveInfo={};
		reportSaveInfo.reportBaseInfo=getData();
		if(reportManage.reportData==null){
			if(reportInfo.reportShowInfo!=null){
				reportSaveInfo.reportShowInfo={
						serverId: reportInfo.reportShowInfo.serverId,
						paramtmpId: reportInfo.reportShowInfo.paramtmpId,
						searchPath: reportInfo.reportShowInfo.searchPath,
						rptSrcPath: reportInfo.reportShowInfo.rptSrcPath
					};
			}
		}
		else{
			if(!reportManage.reportData.reportShow.validate()){
				tabObj.selectTabItem("data");
				//reportManage.reportData.tabObj.selectTabItem("show");
				return;
			}
			reportSaveInfo.reportShowInfo=reportManage.reportData.getShowData();
		}
		if(reportManage.reportRequire==null){
			reportSaveInfo.reportRequireInfo=reportInfo.reportRequireInfo;
		}
		else{
			reportSaveInfo.reportRequireInfo=reportManage.reportRequire.getData();
		}
		if(reportManage.reportData==null){
			reportSaveInfo.reportDimTypes=reportInfo.reportDimTypes;
			reportSaveInfo.reportDataItems=reportInfo.reportDataItems;
			reportSaveInfo.reportModules=reportInfo.reportModuleInfos;
		}
		else{
			reportSaveInfo.reportDimTypes=reportManage.reportData.getDimData();
			reportSaveInfo.reportDataItems=reportManage.reportData.getDataitemData();
			if(reportManage.reportData.reportModule!=null){
				if(!reportManage.reportData.reportModule.validate()){
					tabObj.selectTabItem("data");
					reportManage.reportData.tabObj.selectTabItem("module");
					return;
				}
				reportSaveInfo.reportModules=reportManage.reportData.getModuleData();
			}
			else{
				reportSaveInfo.reportModules=reportInfo.reportModuleInfos;
			}
			
		}
		/*执行保存*/
		$.ajax({
			async : true,
			cache : false,
			type : "POST",
			dataType : "JSON",
			contentType : "application/json",
			data : JSON2.stringify(reportSaveInfo),
			url : "${ctx}/frs/integratedquery/rptmesquery/info/create",
			beforeSend : function(a, b, c) {
				BIONE.showLoading('正在保存数据中...');
			},
			complete : function() {
				 BIONE.hideLoading();
			},
			success : function() {
				parent.refreshAllTree();
				parent.BIONE.tip("保存成功");
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	$(function(){
		intiTab();
		initButton();
		tabObj.selectTabItem("basic");
		intiForm();
		initData();
	});
	
</script>
</head>
<body>
	<div id='template.center'>
		<div id="tab" style="width: 100%; overflow: hidden;">
			<div tabid="basic" title="基本信息" lselected="true">
				<form id="basicform" action="${ctx}/frs/integratedquery/rptmesquery/info/saveReportInfo" method="post"></form>
			</div>
		</div>
	</div>
</body>
</html>