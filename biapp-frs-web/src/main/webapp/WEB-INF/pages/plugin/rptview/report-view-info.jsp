<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
	var show="${show}";
	var mainform=null;
	var templateFlag=true;
	var paramFlag=true;
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	
	function intiForm() {
		mainform = $("#basicform").ligerForm({
			labelWidth : "90px",
			fields : [{
				name: 'rptId',
				type: 'hidden'
			},{
				name: 'cfgId',
				type: 'hidden'
			},{
				display : "报表编号",
				name : "rptNum",
				newline : true,
				type : "text",
				group : "基础信息",
				groupicon : groupicon,
				validate : {
					maxlength : 32,
					numReg : true
				}
			}, {
				display : "报表名称",
				name : "rptNm",
				newline : false,
				type : "text",
				validate : {
					maxlength : 100
				},
				options: {
					readonly: true
				}
			} , {
				display: '报表目录',
				name :'catalogNm',
				comboboxName:'catalogNameBox',
				type: 'select',
				newline: true,
				options : {
					readonly: true
				}
			} ,{
				display : "启用日期",
				name : "verStartDate",
				newline : false,
				type : "date",
				options : {
					readonly: true,
					format : "yyyyMMdd"
				}
			} , {
				display : "报表类型",
				name : "templateType",
				newline : true,
				type : "select",
				comboboxName : "templateTypeCombo",
				options : {
					readonly: true,
					initValue:'02',
					data : [ {
						text : '明细类',
						id : '01'
					}, {
						text : '单元格类',
						id : '02' 
					},{
						text : '综合类',
						id : '03'
					},{
						text : '指标列表（纵）',
						id : '04'
					},{
						text : '指标列表（横）',
						id : '05'
					}],
					onBeforeSelect : function(templateType){
						window.parent.templateType = templateType;
					}
				}
			},{
				display : "计算周期",
				name : "rptCycle",
				newline : false,
				type : "select",
				comboboxName : "rptCycleCombo",
				options : {
					readonly: true,
					initValue:'02',
					data : [ {
						text : '日',
						id : '01'
					}, {
						text : '月',
						id : '02' 
					}, {
						text : '季',
						id : '03' 
					}, {
						text : '年',
						id : '04' 
					}, {
						text : '周',
						id : '10' 
					}, {
						text : '旬',
						id : '11' 
					}, {
						text : '半年',
						id : '12' 
					} ]
				}
			},{
				display : "默认单位",
				name : "templateUnit",
				newline : true,
				type : "select",
				comboboxName : "templateUnitCombo",
				options : {
					readonly: true,
					initValue:'04',
					data : [ {
						text : '元',
						id : '01'
					}, {
						text : '百',
						id : '02' 
					}, {
						text : '千',
						id : '03' 
					}, {
						text : '万',
						id : '04' 
					}, {
						text : '亿',
						id : '05' 
					}]
				}
			},{
				display : "自动调整列宽",
				name : "isAutoAdj",
				newline : false,
				type : "select",
				comboboxName : "isAutoAdjCombo",
				options : {
					readonly: true,
					data : [ {
						text : '是',
						id : 'Y'
					}, {
						text : '否',
						id : 'N' 
					}]
				}
			},{
				display : "是否管驾报表",
				name : "true",
				newline : true,
				type : "select",
				comboboxName : "isCabinCombo",
				options : {
					readonly: true,
					data : [ {
						text : '是',
						id : '1'
					}, {
						text : '否',
						id : '0' 
					}]
				}
			} , {
				display : "报表状态",
				name : "rptSts",
				newline : false,
				type : "select",
				comboboxName : "rptStsBox",
				options : {
					readonly: true,
					initValue:'Y',
					data : [ {
						text : '启用',
						id : 'Y'
					}, {
						text : '停用',
						id : 'N'
					} ]
				}
			},{
				display : "联系人",
				name : "userName",
				newline : true,
				type : "text",
				options : {
					readonly: true
				}
			},{
				display : "主管部门",
				name : "deptName",
				newline : false,
				type : "text",
				options : {
					readonly: true
				}
			},{
				display : "联系人电话",
				name : "tel",
				newline : true,
				type : "text",
				attr : {
					readOnly :true
				}
			},{
				display : "联系人邮箱",
				name : "email",
				newline : false,
				type : "text",
				attr : {
					readOnly :true
				}
			},{
				display : "报表描述",
				name : "rptDesc",
				newline : true,
				type : "textarea",
				width : 493,
				validate : {
					maxlength : 500
				}
			} ]
		});
		if("${show}"!=""){
			jQuery.metadata.setType("attr", "validate");
			BIONE.validate($("#basicform"));
		}
		
		if("${show}"!=""){
			var isCabin;
			if($.ligerui.get("isCabinCombo").getValue() != "1"){
				isCabin = "0";
				$.ligerui.get("isCabinCombo").setValue(isCabin);
			}
		}
		$("#rptDesc").css("width","410");
		$("#rptDesc").css("resize","none");
		$("#rptDesc").attr("readonly",true);
		$("#rptNum").focus().blur();
		
		
		if (show != "") {
			$("input").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
			$.ligerui.get("catalogNameBox").setDisabled();
			$.ligerui.get("verStartDate").setDisabled();
			$.ligerui.get("templateTypeCombo").setDisabled();
			$.ligerui.get("rptCycleCombo").setDisabled();
			$.ligerui.get("templateUnitCombo").setDisabled();
			/* $.ligerui.get("serviceTypeCombo").setDisabled();
			$.ligerui.get("isReleaseSubmitCombo").setDisabled();
			$.ligerui.get("isUptCombo").setDisabled(); */
			$.ligerui.get("rptStsBox").setDisabled();
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
	
	function hideOrShowFields(busiType){
		if(busiType == "03"){
			$("#reportCode").parents("li").show();
			$("#reportCode").rules("add" , {required : true});
			$("#reportCode").rules("add" , {maxlength : 32});
		}else{
			$("#reportCode").rules("remove");
			$("#reportCode").val("");
			$("#reportCode").parents("li").hide();
		}
	}
	
	function intiTab(){
		$("#tab").ligerTab({
			changeHeightOnResize : true,
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
				if(tabId == 'param'){
					if(!paramFlag){
						content = "<iframe frameborder='0' id='paramFrame' name='paramFrame' style='height:100%;width:100%;' src='${ctx}/rpt/rpt/rptmgr/view/param?rptId=${rptId}'></iframe>";
						$("#paramFrame").html(content);
						paramFlag = true;
					}
				}
				if(tabId == 'template'){
					if(!templateFlag){
						content = "<iframe frameborder='0' id='templateFrame' name='templateFrame' style='height:100%;width:100%;' src='${ctx}/report/frame/tmp/view/preview?rptId=${rptId}'></iframe>";
						$("#templateFrame").html(content);
						templateFlag = true;
					}
				}
			}
		});
		tabObj = $("#tab").ligerGetTabManager();
		if("${show}"=="1"){
			addTabItem("param","查询信息","paramFrame",'paramFlag');
			addTabItem("template","模板信息","templateFrame",'templateFlag');
		}
		
	}
	
	function addTabItem(tabId,tabText,frameId,flag){
		var $centerDom = $(document);
		framCenter = $centerDom.height() - 18;
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
				async : false,
				cache : false,
				type : "POST",
				dataType : "JSON",
				contentType : "application/json",
				url :"${ctx}/rpt/frame/rptmgr/info/getReportFrsInfo?rptId=${rptId}",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				success : function(data) {
					window.rptvo=data.rptvo;
					injectFormData(data.rptvo);
					$("#catalogId").val(data.rptvo.catalogId);
					$("#catalogNameBox").val(data.rptvo.catalogNm);
					if(data.rptvo.catalogId == "backRptCtlNodeId"){
						$("#catalogId").val("backRptCtlNodeId");
						$("#catalogNameBox").val("撤回目录");
					}
					
					//设计器信息
					$.ajax({
						cache : false,
						async : false,
						url : "${ctx}/report/frame/design/cfg/getDesignInfo",
						dataType : 'json',
						data : {
							templateId : rptvo.templateId,
							getChildTmps : true
						},
						type : "post",
						complete : function() {
							BIONE.loading = false;
							BIONE.hideLoading();
						},
						success : function(result){
							if(result){
								window.tmpInfo = result;
							}
						},
						error:function(){
							BIONE.tip("数据加载异常，请联系系统管理员");
						}
					});
				},
				error : function(){
					BIONE.loading = false;
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
	
	
	$(function(){
		intiTab();
		//initButton();
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
				<form id="basicform" action="${ctx}/report/mgr/reportInfo/saveReportInfo" method="post"></form>
			</div>
		</div>
	</div>
</body>
</html>