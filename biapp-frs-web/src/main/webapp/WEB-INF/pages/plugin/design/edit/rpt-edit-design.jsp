<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp" />
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<%@ include file="/common/spreadjs_load.jsp"%>
<%@ include file="template-content.jsp"%>
<style type="text/css">
.l-table-edit {
	
}

.l-table-edit-td {
	padding: 4px;
}

.l-button-submit,.l-button-reset {
	width: 80px;
	float: left;
	margin-left: 10px;
	padding-bottom: 2px;
}

.l-verify-tip {
	left: 230px;
	top: 120px;
}

.own-formitem-style {
	width: 108px;
}

.queryContent {
	overflow: auto;
	border: 1px solid #C1C1C1;
	margin: 0.5px;
}

.query-dim-item {
	height: 20px;
	float: left;
	line-height: 20px;
	border: 1px solid #D4D4D4;
	color: #4C4C4C;
	margin: 2px;
	padding: 0 2px;
	background-color: #F0F0F0;
	border-radius: 2px;
	cursor: default;
}

.dim-drag-place {
	height: 20px;
	width: 55px;
	float: left;
	border: 1px dotted #D4D4D4;
	margin: 2px;
	padding: 0 2px;
	border-radius: 2px;
	cursor: default;
}

.query-dim-item-close {
	cursor: pointer;
}

.query-module-item {
	height: 35px;
	float: left;
	line-height: 35px;
	border: 1px solid #D4D4D4;
	color: #4C4C4C;
	margin: 2px;
	padding: 0 2px;
	background-color: #F0F0F0;
	border-radius: 2px;
	cursor: default;
}

.module-drag-place {
	height: 35px;
	width: 80px;
	float: left;
	border: 1px dotted #D4D4D4;
	margin: 2px;
	padding: 0 2px;
	border-radius: 2px;
	cursor: default;
}

.query-module-item-close {
	cursor: pointer;
}

.query-hightlight {
	background-color: #6BEDDA;
}
</style>
<script type="text/javascript">

	var idxlabelFlag = false;
	var querys;
	var type = "";
	var glofilters = {};
	var storeIdxNos = [];
	var idxTarget = null;
	//edit by fangjuan
	var selectionQuery = function(){
		var self = this;
		self.allItems = ko.observableArray([]);
		self.selectPos = ko.observable(-1);
		self.checkbox =  ko.observable(false);
		self.queryType = ko.observable("");
		self.required = ko.observable("false");
		self.daterange = ko.observable("");
		self.showType = ko.observable("date");
		self.defValue = ko.observable("");
		self.addItem = function (model) {
			self.allItems().push(model);
	??????	};
		self.queryType.subscribe(function(newValue){
			if(self.allItems() && self.allItems().length > 0 && self.selectPos() >= 0){
				self.allItems()[self.selectPos()].type = newValue;
				self.required(self.allItems()[self.selectPos()].required);
				self.daterange(self.allItems()[self.selectPos()].daterange);
				self.checkbox(self.allItems()[self.selectPos()].checkbox);
				self.defValue(self.allItems()[self.selectPos()].defValue);
				self.showType(self.allItems()[self.selectPos()].showType);
			}
		});
		
		self.required.subscribe(function(newValue){
			if(self.allItems() && self.allItems().length > 0 && self.selectPos() >= 0){
				self.allItems()[self.selectPos()].required = newValue;
			}
		});
		
		self.daterange.subscribe(function(newValue){
			if(self.allItems() && self.allItems().length > 0 && self.selectPos() >= 0){
				self.allItems()[self.selectPos()].daterange = newValue;
			}
		});
		
		self.checkbox.subscribe(function(newValue){
			if(self.allItems() && self.allItems().length > 0 && self.selectPos() >= 0){
				self.allItems()[self.selectPos()].checkbox = newValue;
			}
		});
		
		self.daterange.subscribe(function(newValue){
			if(self.allItems() && self.allItems().length > 0 && self.selectPos() >= 0){
				self.allItems()[self.selectPos()].daterange = newValue;
			}
		});
		
		self.defValue.subscribe(function(newValue){
			if(self.allItems() && self.allItems().length > 0 && self.selectPos() >= 0){
				self.allItems()[self.selectPos()].defValue = newValue;
			}
		});
		
		self.showType.subscribe(function(newValue){
			if(self.allItems() && self.allItems().length > 0 && self.selectPos() >= 0){
				self.allItems()[self.selectPos()].showType = newValue;
			}
		});
		
		self.getSelectItem = function(paramId){
			for(var i=0;i<self.allItems().length;i++){
				if(self.allItems()[i].paramId == paramId){
					self.selectPos(i);
					 if(self.allItems()[i].type == ""){
						 if(self.allItems()[i].dimTypeNo == "tempUnit"){
							 self.allItems()[i].type = "10";
						 }
						 else if(self.allItems()[i].dbType == "" && self.allItems()[i].dimTypeNo == "DATE"){
							 self.allItems()[i].type = "00";
						 }
						 else if(self.allItems()[i].dbType == ""){
							 self.allItems()[i].type = "09";
						 }
						 else if(self.allItems()[i].dbType == "03" || self.allItems()[i].dbType == "04" || self.allItems()[i].dimTypeNo == "DATE"){
							self.allItems()[i].type = "05";
						 }else if(self.allItems()[i].dimTypeNo == "ORG"){
							 self.allItems()[i].type = "04";
						}else if(self.allItems()[i].dimTypeNo && self.allItems()[i].dimTypeNo != ""){
							self.allItems()[i].type = "03";
						}else if(self.allItems()[i].dbType == "02"){
							self.allItems()[i].type = "02";
						}else {
							self.allItems()[i].type = "01";
						}
					}
					self.queryType(self.allItems()[i].type); 
					self.required(self.allItems()[i].required);
					self.daterange(self.allItems()[i].daterange);
					self.checkbox(""+self.allItems()[i].checkbox);
					self.defValue(""+self.allItems()[i].defValue);
					self.showType(self.allItems()[i].showType);
					return i;
				}
			}
			self.selectPos(-1);
			return -1;
		}
	};
	
	var objTmp = null;

	var cellBaseInfo = null; //???????????????????????????
	
	var lineId = "${lineId}";

	// ????????????????????? - begin
	var Design,
		   spread,
		   RptIdxInfo,
		   Utils,
		   Uuid,jsonTmp;
	// ????????????????????? - end
	var templateType = "${templateType}",    // 01 - ????????????02 - ???????????? ; 03 - ????????? ??? 04 - ????????????????????? ??? 05 -  04 - ?????????????????????
		  rptId = "${rptId}",
		  verId = "${verId}",  // ???????????????
		  maxVerId = "${maxVerId}", // ???????????????
		  canEdit = "${canEdit}", // ???????????????
		  verStartTime = "", // ??????????????????
		  defSrc = "${defSrc}" , // ????????????
		  catalogId = "${catalogId}"; // ??????ID
		  
	var baseInfo4Upt = null,
		  designInfo4Upt = null,
		  paramTemplateId = "", // ????????????id
		  rptNm = "";
	
	var templateId = "";
	var contentFlag = true; //??????????????????  ????????????
	var templateDsId = "";  // ????????????????????????ID
	var titles = [];
	var moduleTreeObj;
	var indexTreeObj;
	var storeTreeObj;
	var dimSortable;
	var dimTreeObj;
	var dimTreeRootIcon = "${ctx}/images/classics/icons/Catalog.gif";
	var unitTreeIcon = "${ctx}/images/classics/icons/rmb.png";
	var dimTreeNodeIcon = "${ctx}/images/classics/icons/list-items.gif";
	var treeUrls = [];
	treeUrls["01"] = "${ctx}/report/frame/design/cfg/getModuleTree";
	treeUrls["02"] = "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json";
	treeUrls["03"] = "${ctx}/report/frame/idx/getAsyncLabelTree.json";
	titles["01"] = "????????????";
	titles["02"] = "??????";
	var layout;
	
	var catalogType = "01";
	var moduleType = "02";
	var colCatalogType = "03";
	var colType = "04";
	
	// ?????????????????????
	var notAllowedIcon = "/images/classics/icons/cancel.gif";
	var allowedIcon = "/images/classics/icons/accept.png";
	
	var treeInitFlag = true;
	
	// ???????????????????????????
	var draggingNode;
	var draggingTreeId;
	
	// ?????????????????????????????????
	var selectionIdx = {};
	// ??????????????????????????????
	
	// ?????????????????????????????????(????????????)
	var selectionToolBar = {
		font : ko.observable('??????'),
		fontSize : ko.observable(10),
		bold : ko.observable('normal'),
		italic : ko.observable('normal'),
		textDecoration : ko.observable('normal')
	};
	
	var allQueryParams = [];
	// ????????????????????????
	var allDimArray = ""; // ????????????????????????
	var allDims = "";   // ??????????????????
	var queryDims = "";   // ????????????
	var queryDimsObj = {};
	// ??????????????????
	var defaultQueryDims = "${defaultQueryDims}";
	// ??????
	var defaultParamJson = '[{"type":"date","id":"de9406d0-ff7f-11e4-ae9d-1503db5aaea1","name":"DATE","display":"????????????","newline":"false","disabled":"null","showTime":"false","required":"true","format":"yyyyMMdd"}]';
	// ???????????????
// 	var defaultParamJson2 = '[{"type":"date","id":"de9406d0-ff7f-11e4-ae9d-1503db5aaea1","name":"DATE","display":"????????????","newline":"false","disabled":"null","showTime":"false","required":"true","format":"yyyyMMdd"},{"type":"popup","id":"de99d330-ff7f-11e4-ae9d-1503db5aaea1","name":"ORG","display":"??????","newline":"false","disabled":"null","value":"null","checkbox":"true","required":"false","dialogWidth":"400","dialogHeight":"500","datasource":"{\\"options\\":{\\"url\\":\\"/report/frame/datashow/idx/orgTree\\",\\"ajaxType\\":\\"post\\"}}"}]';
	// ????????????????????????
// 	var defaultParamJson = '[{"type":"date","id":"de9406d0-ff7f-11e4-ae9d-1503db5aaea1","name":"DATE","display":"????????????","newline":"false","disabled":"null","showTime":"false","required":"true"},{"type":"popup","id":"de99d330-ff7f-11e4-ae9d-1503db5aaea1","name":"ORG","display":"??????","newline":"false","disabled":"null","value":"null","checkbox":"true","required":"true","dialogWidth":"400","dialogHeight":"500","datasource":"{\\"options\\":{\\"url\\":\\"/report/frame/datashow/idx/orgTree\\",\\"ajaxType\\":\\"post\\"}}"},{"type":"popup","id":"de9d7cb0-ff7f-11e4-ae9d-1503db5aaea1","name":"CURRENCY","display":"??????","newline":"false","disabled":"null","value":"null","checkbox":"true","required":"true","dialogWidth":"400","dialogHeight":"500","datasource":"{\\"options\\":{\\"url\\":\\"/report/frame/design/paramtmp/getTreeDimItems?dimTypeNo=CURRENCY\\",\\"ajaxType\\":\\"post\\"}}"}]';
	//????????????????????????
	var autoColumnWidth=false;
	
	var searchObj = {exeFunction : "loadIndexTree",searchType : "idx"};//????????????initNodes??????
	var targetParam;//??????????????????
	$(function() {
		// ????????????????????????
		initUptDatas();
		// ??????
		initLayout();
		// ????????????
		initTree();
		// ?????????
		initDesign();
		
		//????????????????????????
		$(".l-trigger").css("right","26px");
		var innerHtml = '<i class="l-trigger" style="right:20px;width:1px;height:12px;border-left:1px dotted gray;margin-top:4px;"></i>'+
			'<div title="????????????" class="l-trigger" style="right:0px;"><div id="highSearchIcon" onclick="javascript:initSeniorSearch();" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/application_form.png) no-repeat 50% 50% transparent;"></div></div>';
		$(".l-trigger").after(innerHtml);

	});
	
	function initIdxTab(){
		$("#idxtab").ligerTab({
			changeHeightOnResize : true,
			contextmenu : false
		});
	}
	
	function initColIdxTab(){
		$("#idxColtab").ligerTab({
			changeHeightOnResize : true,
			contextmenu : false
		});
	}
	
	function initSeniorSearch(){
		//????????????????????????
		if(idxlabelFlag){
			BIONE.commonOpenDialog("????????????","highSearch","600","250","${ctx}/rpt/frame/rptSearch/labelhighSearch?type=idx");
		}
		else{
			BIONE.commonOpenDialog("????????????","highSearch","600","250","${ctx}/rpt/frame/rptSearch/highSearch?searchObj="+JSON2.stringify(searchObj));
		}
	}
	//????????????
	function loadIndexTree(searchNm,searchObj){
		initIndexTree(searchNm,targetParam,searchObj);
	}
	// ????????????????????????
	function initUptDatas(){
		if(rptId != null
				&& rptId != ""
				&& typeof rptId != "undefined"){
			// 1.????????????
			baseInfo4Upt =  '${rptInfo}' ? JSON2.parse('${rptInfo}') : null;
			if(baseInfo4Upt != null){
				verStartTime = baseInfo4Upt.verStartDate;
				verId = baseInfo4Upt.verId;
			}
			templateType = "${templateType}";
			// 2.???????????????
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frame/design/cfg/getDesignInfo",
				dataType : 'json',
				data : {
					templateId : baseInfo4Upt.templateId,
					verId : verId,
					getChildTmps : false
				},
				type : "post",
				success : function(result){
					if(result){
						designInfo4Upt = result;
					}
				},
				error:function(){
					BIONE.tip("?????????????????????????????????????????????");
				}
			});
		}
	}
	
	// ???????????????
	function initLayout(){
		// ???????????????title??????
		window.parent.$(".l-dialog-title").css("text-align" , "center");
		// ?????????layout title
		var leftTitle =         
			'<div width="8%"                                                                                                             '+
			'	style="float: left; position: relative; height: 20p; margin-top: 5px">    '+
			'		<img src="${ctx }/images/classics/icons/application_side_tree.png" />                       '+
			'</div>                                                                                                                             '+
			'<div width="90%">                                                                                                         '+
			'	<span                                                                                                                             '+
			'		style="font-size: 12; float: left; position: relative; line-height: 25px; padding-left: 2px"> '+
			'		<span style="font-size: 12">?????????</span>                                     '+
			'	</span>                                                                                                                         '+
			'</div>                                                                                                                             ';
		$("#leftDiv").attr("title",leftTitle);
		// ?????????ligerlayout
		var centerWidth = $("#center").width();
		layout = $("#designLayout").ligerLayout({
			height : $("#center").height(),
			leftWidth : (centerWidth - 220) * 0.21,
			centerWidth : (centerWidth - 220) * 0.79,
			rightWidth : 220,
			allowLeftResize : true ,
			allowRightResize : true ,
			onEndResize : function(){
				if(Design){
					Design.resize(Design.spread);
				}
				if(templateType == "04" || templateType == "05" || templateType == "06" ){
					// ??????????????????
					var leftWidth = $("#designLayout").children(".l-layout-left").width();
					$("#idxColLayout").children(".l-layout-center").width(leftWidth);
					$("#idxColLayout").children(".l-layout-centerbottom").width(leftWidth);
					$("#colTreeContainer1").width(leftWidth);
					$("#colTreeContainer2").width(leftWidth);
					$("#storeTreeContainer").width(leftWidth);
				} else if(templateType == "02" ){
					// ???????????????????????????
					var leftWidth = $("#designLayout").children(".l-layout-left").width();
					$("#idxCellLayout").children(".l-layout-center").width(leftWidth);
					$("#idxCellLayout").children(".l-layout-centerbottom").width(leftWidth);
					$("#cellTreeContainer1").width(leftWidth);
					$("#cellTreeContainer2").width(leftWidth);
					$("#storeTreeContainer").width(leftWidth);
				}
			}
		});
		//????????????????????????
		var treeContainerHeight = $("#centerDiv").height() - 25 - 25 - 2;
		if(templateType == "02"){
			// ????????????????????????????????????????????????
			$("#treeAccordion").hide();
			$("#idxCellLayout").show();
			var idxLayoutHeight = ($("#center").height() - 25 - 2) * 0.65;
			var dimLayoutHeight = ($("#center").height() - 25  - 2)  - idxLayoutHeight - 2;
			// ??????????????????????????????layout
			$("#idxCellLayout").ligerLayout({
				height : $("#center").height(),
				centerWidth : (centerWidth - 220) * 0.21,
				centerBottomWidth : (centerWidth - 220) * 0.21,
				centerHeight : dimLayoutHeight,
				centerBottomHeight : idxLayoutHeight,
				onEndResize : function(){
					$("#cellTreeContainer2").height($("#idxCellLayout").children(".l-layout-center").height() - 25);
					$("#cellTreeContainer1").height($("#idxCellLayout").children(".l-layout-centerbottom").height() - 25 - 25 -75);
					$("#storeTreeContainer1").height($("#idxCellLayout").children(".l-layout-centerbottom").height() - 25 - 25 -28);
				}
			});
			$("#cellTreeContainer1").height(idxLayoutHeight - 25 - 25 -75);
			$("#storeTreeContainer1").height(idxLayoutHeight - 25 - 25 -28);
			$("#cellTreeContainer2").height(dimLayoutHeight);
			
		}
		if(templateType == "03"){
			// ?????????????????????(?????????????????????)
			// ???????????????accordion item??????
			treeContainerHeight -= 27;
			// ?????????????????????????????????
			var appendHtml = $("#treeAccordion").html();
			$("#treeAccordion").append(appendHtml);
			$($(".treeContainer")[1]).children("ul").attr("id" , "tree2");
			$($(".treeContainer")[0]).attr("title" , titles["01"]);
			$($(".treeContainer")[1]).attr("title" , titles["02"]);
			
			$("#treeAccordion").ligerAccordion({
				height:$("#centerDiv").height() - 25 - 2
			});
			$(".treeContainer").height(treeContainerHeight);
		}else if(templateType == "04" || templateType == "05" || templateType == "06" ){
			// ???????????????????????????????????????
			$("#treeAccordion").hide();
			$("#idxColLayout").show();
			var idxLayoutHeight = ($("#center").height() - 25 - 2) * 0.65;
			var dimLayoutHeight = ($("#center").height() - 25  - 2)  - idxLayoutHeight - 2;
			// ??????????????????????????????layout
			$("#idxColLayout").ligerLayout({
				height : $("#center").height(),
				centerWidth : (centerWidth - 220) * 0.21,
				centerBottomWidth : (centerWidth - 220) * 0.21,
				centerHeight : dimLayoutHeight,
				centerBottomHeight : idxLayoutHeight,
				onEndResize : function(){
					$("#colTreeContainer2").height($("#idxColLayout").children(".l-layout-center").height() - 25);
					$("#colTreeContainer1").height($("#idxColLayout").children(".l-layout-centerbottom").height() - 25 - 25 -75);
					$("#storeTreeContainer2").height($("#idxCellLayout").children(".l-layout-centerbottom").height() - 25 - 25 -28);

				}
			});
			$("#colTreeContainer1").height(idxLayoutHeight - 25 - 25 -75);
			$("#colTreeContainer2").height(dimLayoutHeight);
			$("#storeTreeContainer2").height(idxLayoutHeight - 25 - 25 -28);
			
		}else{
			$(".treeContainer").attr("title" , titles[templateType]);
			
			$("#treeAccordion").ligerAccordion({
				height:$("#centerDiv").height() - 25 - 2
			});
			$(".treeContainer").height(treeContainerHeight);
		}
		$("#queryDimsDiv").children(".queryContent").height(135);
		
		// ???????????????????????????
		initQueryDimsCss();
		
		// ???resize???????????????
		resizeHandler();
	}
	
 	function changeRightForm(flag){
		// ??????????????????????????????????????????
		
		if(flag){
			$("#queryDimsDiv .query-hightlight").removeClass("query-hightlight");
			$(".l-layout-right").find(".l-layout-header").find(".l-layout-header-inner").html("????????????");
			$("#rightCellForm").html($("#template-content").tpl(cellBaseInfo));
			ko.cleanNode($("#rightCellForm")[0]);  
			ko.applyBindings(selectionIdx , $("#rightCellForm")[0]);
			$("#rightQueryForm").css("display", "none");
			$("#rightCellForm").css("display","");
		}
		else{
			Design.clearCellTarget();
			$(".l-layout-right").find(".l-layout-header").find(".l-layout-header-inner").html("????????????");
			$("#rightQueryForm").css("display", "");
			$("#rightCellForm").css("display","none");
			
			//ko.cleanNode($("#rightQueryForm")[0]);  
			//ko.applyBindings(selectionQuery , $("#rightQueryForm")[0]);
		}
	}
	function initDetailForm(){
		// ??????????????????????????????
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/design/cfg/getFormDetailDatas",
			dataType : 'json',
			type : "post",
			success : function(result){
				if(result != null
						&& typeof result != "undefined"){
					var objTmp = {template : result};
					cellBaseInfo = objTmp;
					$("#rightCellForm").html($("#template-content").tpl(objTmp));
					// ????????????????????????????????????
					initCellForm();
				}
			}
		});
	}
	
	// ?????????????????????????????????
	function initQueryDimsCss(){
		// ?????????title??????
		
		$("#rightQueryForm").html($("#template-query").tpl());
		
		querys = new selectionQuery();
		
		ko.applyBindings(querys,  $("#rightQueryForm")[0]);
		
		$("#rightQueryForm").css("display", "none");
		
		
		$(".queryDimImg").attr("src" , "${ctx}/images/classics/icons/chart_pie_edit.png");
		
		// ?????????????????? ??????/??????????????????/??????
		$("#queryDimsToggle").css("background","url('${ctx}/css/classics/ligerUI/Gray/images/layout/togglebar.gif')");
		// ??????????????????????????????
		$("#queryDimsToggle").css("background-position","0px 0px");
		$("#queryDimsDiv").children(".queryContent").hide();
		$("#queryDimsToggle").bind("mouseenter" , function(){
			// ????????????
			var type = $("#queryDimsToggle").attr("currType");
			if(type == "show"){
				// ?????????
				$("#queryDimsToggle").css("background-position","0px -60px");
			} else {
				// ?????????
				$("#queryDimsToggle").css("background-position","0px -20px");
			}
		})
		$("#queryDimsToggle").bind("mouseleave" , function(){
			// ????????????
			var type = $("#queryDimsToggle").attr("currType");
			if(type == "show"){
				// ?????????
				$("#queryDimsToggle").css("background-position","0px -40px");
			} else {
				// ?????????
				$("#queryDimsToggle").css("background-position","0px 0px");
			}
		})
		$(document).on('click','.query-module-item',function(){
			$("#queryDimsDiv .query-hightlight").removeClass("query-hightlight");
			$(this).addClass("query-hightlight");
			var paramId = $(this).attr("paramId");
			querys.getSelectItem(paramId);
			if(contentFlag){
				contentFlag=false;
				
				changeRightForm(contentFlag);
			} 
		});
		$(document).on('click','.query-dim-item',function(){
				$("#queryDimsDiv .query-hightlight").removeClass("query-hightlight");
				$(this).addClass("query-hightlight");
				var paramId = $(this).attr("paramId");
				querys.getSelectItem(paramId);
				if(contentFlag){
					contentFlag=false;
					changeRightForm(contentFlag);
				} 
			}); 
		// close?????????????????????
		$(".queryContent").delegate(".query-dim-item-close , .query-module-item-close" , "click" , function(){
			var paramId = $(this).parent().parent(".query-dim-item , .query-module-item").attr("paramId");
			for(var i in querys.allItems()){
				if(paramId === querys.allItems()[i].paramId){
					querys.allItems().splice(i,1);
					break;
				}
			}
			$(this).parent().parent(".query-dim-item , .query-module-item").remove();
			if(queryDimsObj){
				queryDimsObj.querySize(queryDimsObj.querySize()-1);
			}
		});
		// module click??????
		$(".queryContent").delegate(".query-module-clickable" , "click" , function(){
			var source = $(this);
			var target = $(this).parent().prev().children("i");
			var eleTypeDom = $(this).parent().parent().next(".query-module-eletype");
			target.removeClass(source.attr("classRemove")).addClass(source.attr("classAdd"));
			eleTypeDom.val(source.attr("eleType"));
		});
		
		// ??????????????????????????????
		var lengthTmp = (!queryDims || queryDims == "") ? 0 : queryDims.split(",").length;
		queryDimsObj.querySize = ko.observable(lengthTmp);
		ko.applyBindings(queryDimsObj , $(".queryDimTitle")[0]);
		
		initDropQueryDims();
		
		// ?????????sortable
		dimSortable = $("#queryDimsDiv").children(".queryContent").sortable({
			containment : $("#queryDimsDiv").children(".queryContent"),
			placeholder : "dim-drag-place",
			tolerance : 'pointer'
		});
		
		// ?????????????????????resizable
		$("#queryDimsDiv").resizable({
			handles:"s", // south??????????????????
			autoHide:true ,  // ???????????????handler
			maxWidth : $("#queryDimsDiv").width(),
			minWidth : $("#queryDimsDiv").width(),
			minHeight : 57, // 25px+25px+2
			maxHeight : ($("#center").height()) * 0.4,
			resize : function(event , ui){
				// ??????????????????
				$(".queryContent").height($(this).height() - 25 - 3);
				if(Design){
					Design.spreadDom.height($("#centerDiv").height() - $("#queryDimsDiv").height() - $("#_spreadToolbar").height() - $("#_spreadCellDetail").height() - 2);
					Design.resize(spread);
				};
			}
		});
		// ????????????jquery ui resizable??????????????????????????????????????????handler??????????????????handler(????????????handlers???authHide??????????????????)
		//$("#queryDimsDiv").children(".ui-resizable-e , .ui-resizable-se").remove();
	}
	
	// ?????????????????????????????????????????????????????????????????????????????????sorable?????????????????????????????????
	function queryDimMoreHandler(){
		// ???????????????????????????????????????????????????
		var commonDimsArray = Design.getCommonDimsByAjax();
		var commonDimNos = [];
		if(commonDimsArray
				&& commonDimsArray.length > 0){
			$.each(commonDimsArray , function(i , d){
				commonDimNos.push(d.dimTypeNo);
			});
			allDims = commonDimNos.join(",");
		}else{
			allDims = defaultQueryDims;
		}
		BIONE.commonOpenDialog('??????????????????',
				'queryDimDialog',
				Utils.transWidthOrHeight("80%" , $(window).width()),
				Utils.transWidthOrHeight("80%" , $(window).height()),
				'${ctx}/report/frame/design/paramtmp');
	}
	
	// resize??????????????????
	function resizeHandler(){
		$("#queryDimsToggle").bind("click" , function(){
			// ????????????
			var type = $("#queryDimsToggle").attr("currType");
			if(type == "show"){
				// ?????????
				$("#queryDimsDiv").children(".queryContent").hide();
				$("#spread").height($("#spread").height() + $("#queryDimsDiv").children(".queryContent").height() + 3);
				$("#queryDimsToggle").css("background-position","0px 0px");
				$("#queryDimsToggle").attr("currType","hide");
				$("#queryDimsDiv").height($("#queryDimsDiv").children(".l-layout-header").height());
				if(Design){
					var heightTmp = Design.spreadDom.height();
					Design.spreadDom.height(heightTmp + $("#queryDimsDiv").children(".queryContent").height() + 3);
					Design.resize(spread);
				};
			} else {
				// ?????????
				$("#queryDimsDiv").children(".queryContent").show();
				$("#spread").height($("#spread").height() - $("#queryDimsDiv").children(".queryContent").height() - 3);
				$("#queryDimsToggle").css("background-position","0px -40px");
				$("#queryDimsToggle").attr("currType","show");
				$("#queryDimsDiv").height($("#queryDimsDiv").children(".l-layout-header").height() + $("#queryDimsDiv").children(".queryContent").height() + 3);
				if(Design){
					var heightTmp = Design.spreadDom.height();
					Design.spreadDom.height(heightTmp - $("#queryDimsDiv").children(".queryContent").height() - 3);
					Design.resize(spread);	
				};
			}
		});
		if(layout){
			var leftToggleDoom = $(".l-layout-left").children(".l-layout-header").children(".l-layout-header-toggle");
			leftToggleDoom.unbind("click");
			leftToggleDoom.bind("click" , function(){
				layout.setLeftCollapse(true);
				if(Design){
					Design.resize(spread);	
				};
			});
			layout.leftCollapse.toggle.unbind("click");
			layout.leftCollapse.toggle.bind("click" , function(){
				layout.setLeftCollapse(false);
				if(Design){
					Design.resize(spread);	
				};
			});
			var rightToggleDoom = $(".l-layout-right").children(".l-layout-header").children(".l-layout-header-toggle");
			rightToggleDoom.unbind("click");
			rightToggleDoom.bind("click" , function(){
				layout.setRightCollapse(true);
				if(Design){
					Design.resize(spread);	
				};
			});
			layout.rightCollapse.toggle.unbind("click");
			layout.rightCollapse.toggle.bind("click" , function(){
				layout.setRightCollapse(false);
				if(Design){
					Design.resize(spread);	
				};
			});
		}
	}
	
	// ??????????????????
	function initTree(){
		if(templateType == "01"){
			moduleTreeHandler();
			// ???????????????
			initNodes();
		}else if(templateType == "02"){
			initIdxTab();
			// ??????????????????
			storeTreeHandler($("#storeTreeContainer1").children("ul"));
			indexTreeHandler($("#cellTreeContainer1").children("ul"));
			// ?????????????????????
			initNodes("02");
			// ??????????????????
			dimTreeHandler($("#cellTreeContainer2").children("ul"));
			initDimTreeNodes();
		}else if(templateType == "03"){
			initIdxTab();
			// ????????????????????????
			moduleTreeHandler($($(".treeContainer")[0]).children("ul"));
			// ???????????????
			initNodes("01");
			// ??????????????????
			storeTreeHandler($($(".treeContainer")[1]).children("ul"));
			indexTreeHandler($($(".treeContainer")[1]).children("ul"));
		}else if(templateType == "04" || templateType == "05" || templateType == "06" ){
			initColIdxTab();
			// ??????????????????
			storeTreeHandler($("#storeTreeContainer2").children("ul"));
			indexTreeHandler($("#colTreeContainer1").children("ul"));
			// ?????????????????????
			initNodes("02");
			// ??????????????????
			dimTreeHandler($("#colTreeContainer2").children("ul"));
			initDimTreeNodes();
		}
	}
	
	function moduleTreeHandler(target){
		target = target ? target : $(".treeContainer").children("ul");
		var async = {
				data : {
					keep : {
						parent : true
					},
					key : {
						name : "text"
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId",
						rootPId : null
					}
				},
				view : {
					selectedMulti : false,
					showLine : false
				},
				callback:{
				
				}
			};
			moduleTreeObj = $.fn.zTree.init(target,async,[]);
			// ?????????????????????????????????????????????????????????
			moduleTreeObj.setting.callback.beforeExpand = function(treeId , treeNode){
				if(treeInitFlag === false){
					// ??????????????????????????????
					return true;
				}
				if(moduleType == treeNode.params.type){
					return false;
				}
				return true;
			};
			moduleTreeObj.setting.callback.onNodeCreated = function(event , treeId , treeNode){						
				if(colType == treeNode.params.type){
					setDragDrop("#"+treeNode.tId+"_span" , "#spread , .queryContent");
				}
				moduleTreeObj.expandNode(moduleTreeObj.getNodeByParam("id" , treeNode.id , null) ,true , false , false , true);
			}
			target.parent().find("#searchIcon").bind("click",function(){
				initNodes(null,target.parent().find("#searchInput").val());
			});
			target.parent().find("#searchInput").bind("keydown",function(e){
				if(e.keyCode == 13){
					initNodes(null,target.parent().find("#searchInput").val());
				}
			}); 
	}
	
	function addHoverDom(treeId, treeNode) {
		if (!(treeNode.params && 'idxInfo' == treeNode.params.nodeType)) {
			return;
		}
		var sObj = $("#" + treeNode.tId + "_span");
		if (treeNode.editNameFlag || $("#addBtn_"+treeNode.id).length>0) return;
		var addStr = "<span class='button store' id='addBtn_" + treeNode.id
			+ "'title='??????' onfocus='this.blur();'></span>";
		sObj.after(addStr);
		var btn = $("#addBtn_"+treeNode.id);
		if (btn) {
			btn.bind("click", function(){
				var node = storeTreeObj.getNodeByParam("id",treeNode.id);
				if(!node){
					storeIdxNos.push(treeNode.id);
					if(treeNode.data.indexType == "05" && !treeNode.children){
						$.ajax({
							cache : false,
							async : false,
							url : "${ctx}/report/frame/idx/getMeasureInfo",
							dataType : 'json',
							data:{
								indexNo : treeNode.id,
								indexVerId : treeNode.data.id.indexVerId,
							},
							type : "post",
							success : function(result) {
								treeNode.children = result;
							},
							error : function(result, b) {
							}
						});
					}
					storeTreeObj.addNodes(null,treeNode,false);
					BIONE.tip("????????????");
				}
				else{
					BIONE.tip("???????????????");
				}
			});
		}
	};
	
	function removeHoverDom(treeId, treeNode) {
		$("#addBtn_"+treeNode.id).unbind().remove();
	};
	
	function addStoreHoverDom(treeId, treeNode) {
		if (!(treeNode.params && 'idxInfo' == treeNode.params.nodeType)) {
			return;
		}
		var sObj = $("#" + treeNode.tId + "_span");
		if (treeNode.editNameFlag || $("#delBtn_"+treeNode.id).length>0) return;
		var addStr = "<span class='button remove' id='delBtn_" + treeNode.id
			+ "'title='??????' onfocus='this.blur();'></span>";
		sObj.after(addStr);
		var btn = $("#delBtn_"+treeNode.id);
		if (btn) {
			btn.bind("click", function(){
				for(var i in storeIdxNos){
					if(storeIdxNos[i] == treeNode.id){
						storeIdxNos.splice(i,1);
						break;
					}
				}
				storeTreeObj.removeNode(treeNode);
			});
		}
	};
	
	function removeStoreHoverDom(treeId, treeNode) {
		$("#delBtn_"+treeNode.id).unbind().remove();
	}
	
	function storeTreeHandler(target){
		targetParam = target ? target : $(".treeContainer").children("ul");
		target = target ? target : $(".treeContainer").children("ul");
		var setting ={
				data : {
						keep : {
							parent : true
						},
						key : {
							name : "text"
						},
						simpleData : {
							enable : true,
							idKey : "id",
							pIdKey : "upId",
							rootPId : null
						}
				},
				view:{
					selectedMulti:false,
					addHoverDom: addStoreHoverDom,
					removeHoverDom: removeStoreHoverDom
				},
				callback:{
				}
		};
		storeTreeObj = $.fn.zTree.init(target,setting,[]);
		storeTreeObj.setting.callback.onNodeCreated = function(event , treeId , treeNode){
			if(("idxInfo" == treeNode.params.nodeType && (!treeNode.params.haveMeasure) )|| treeNode.params.nodeType == "measureInfo"){
				setDragDrop("#"+treeNode.tId+"_span" , "#spread");
			}
			if(treeNode.params.nodeType == "idxInfo"){
				$("#"+ treeNode.tId + "_ico").bind("click",function(){
					curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
					dialog =  BIONE.commonOpenFullDialog("????????????","rptIdxInfoPreviewBox",curTabUri, null);
				});
			}
		}
		var data = {
			  tmpId : baseInfo4Upt? baseInfo4Upt.templateId:""
		};
		BIONE.loadTree("${ctx}/report/frame/design/cfg/getStoreTree",storeTreeObj,data,function(childNodes){
			if(childNodes){
				for(var i = 0;i<childNodes.length;i++){
					storeIdxNos.push(childNodes[i].id);
					childNodes[i].nodeType = childNodes[i].params.nodeType;
					childNodes[i].indexVerId = childNodes[i].params.indexVerId;
				}
			}
			return childNodes;
		},false);
	}
	
	function initLabelIdx(ids,stype){
		type = "lsync";
		var _url = "${ctx}/report/frame/idx/getSyncLabelFilter.json";
		var data = {'ids':ids ,'type':stype,'isShowDim':'1', 'isAuth':'0','isShowMeasure':'1',"isExpire":1};
		if(defSrc != "01"){
			data.isAuth = '1';
		}
		setting ={
				data : {
					keep : {
						parent : true
					},
					key : {
						name : "text"
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId",
						rootPId : null
					}
				},
				view:{
					selectedMulti:false,
					addHoverDom: addHoverDom,
					removeHoverDom: removeHoverDom
				},
				callback:{
				}
		};
		indexTreeObj = $.fn.zTree.init(idxTarget,setting,[]);
		indexTreeObj.setting.callback.onNodeCreated = function(event , treeId , treeNode){
			if(("idxInfo" == treeNode.params.nodeType && (!treeNode.params.haveMeasure) )|| treeNode.params.nodeType == "measureInfo"){
				setDragDrop("#"+treeNode.tId+"_span" , "#spread");
			}
			if(treeNode.params.nodeType == "idxInfo"){
				$("#"+ treeNode.tId + "_ico").bind("click",function(){
					curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
					dialog =  BIONE.commonOpenFullDialog("????????????","rptIdxInfoPreviewBox",curTabUri, null);
				});
			}
		
		}
		BIONE.loadTree(_url,indexTreeObj,data,function(childNodes){
			if(childNodes){
				for(var i = 0;i<childNodes.length;i++){
					childNodes[i].nodeType = childNodes[i].params.nodeType;
					childNodes[i].indexVerId = childNodes[i].params.indexVerId;
				}
			}
			return childNodes;
		},false);
	}
	
	function indexTreeHandler(target){
		targetParam = target ? target : $(".treeContainer").children("ul");
		target = target ? target : $(".treeContainer").children("ul");
		idxTarget = target;
		//edit by fangjuan 2014-09-04
		var otherParam = {'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "0", "showEmptyFolder":"0","isExpire":1};
		if(defSrc != "01"){
			otherParam.isAuth = "1";
		}
		var async ={
				async:{
					enable:true,
					url: treeUrls["02"],
					autoParam:["nodeType", "id", "indexVerId"],
					otherParam: otherParam,
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						var newChildNodes = [];
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].indexVerId = childNodes[i].params.indexVerId;
								childNodes[i].defSrc = childNodes[i].params.defSrc ? childNodes[i].params.defSrc : "";
								if(target.parent().find("#searchInput").val() && childNodes[i].nodeType == "idxInfo"){
									if(childNodes[i].text.indexOf(target.parent().find("#searchInput").val()) >= 0){
										newChildNodes.push(childNodes[i]);
									}										
								}else{
									newChildNodes.push(childNodes[i]);
								}
							}
						}
						return newChildNodes;
					}
				},
				data:{
					key:{
						name:"text",
						title:"title"
					}
				},
				view:{
					selectedMulti:false,
					addHoverDom: addHoverDom,
					removeHoverDom: removeHoverDom
				},
				callback:{
				}
		};
		indexTreeObj = $.fn.zTree.init(target,async);
		indexTreeObj.setting.callback.beforeExpand = function(treeId , treeNode){
			if(treeInitFlag === false){
				// ??????????????????????????????
				return true;
			}
			if(moduleType == treeNode.params.type){
				return false;
			}
			return true;
		};
		indexTreeObj.setting.callback.onNodeCreated = function(event , treeId , treeNode){
			if(("idxInfo" == treeNode.params.nodeType && (!treeNode.params.haveMeasure) )|| treeNode.params.nodeType == "measureInfo"){
				setDragDrop("#"+treeNode.tId+"_span" , "#spread");
			}
			if(treeNode.params.nodeType == "idxInfo"){
				$("#"+ treeNode.tId + "_ico").bind("click",function(){
					curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
					dialog =  BIONE.commonOpenFullDialog("????????????","rptIdxInfoPreviewBox",curTabUri, null);
				});
			}
		}
		target.parent().parent().find("#searchIcon").unbind("click");
		target.parent().parent().find("#searchInput").unbind("keydown");
		target.parent().parent().find("#searchIcon").bind("click",function(){
			initIndexTree(target.parent().parent().find("#searchInput").val(),target)
		});
		target.parent().parent().find("#searchInput").bind("keydown",function(e){
			if (event.keyCode == 13) {
				initIndexTree(target.parent().parent().find("#searchInput").val(),target)
	 		}
		}); 
	}
	
	function indexLabelTreeHandler(target){
		targetParam = target ? target : $(".treeContainer").children("ul");
		target = target ? target : $(".treeContainer").children("ul");
		var otherParam = {'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "0", "showEmptyFolder":"0","isExpire":1};
		if(defSrc != "01"){
			otherParam.isAuth = "1";
		}
		var async ={
				async:{
					enable:true,
					url: treeUrls["03"],
					autoParam:["nodeType", "id", "indexVerId"],
					otherParam: otherParam,
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						var newChildNodes = [];
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].indexVerId = childNodes[i].params.indexVerId;
								childNodes[i].defSrc = childNodes[i].params.defSrc ? childNodes[i].params.defSrc : "";
								if(target.parent().find("#searchInput").val() && childNodes[i].nodeType == "idxInfo"){
									if(childNodes[i].text.indexOf(target.parent().find("#searchInput").val()) >= 0){
										newChildNodes.push(childNodes[i]);
									}										
								}else{
									newChildNodes.push(childNodes[i]);
								}
							}
						}
						return newChildNodes;
					}
				},
				data:{
					key:{
						name:"text",
						title:"title"
					}
				},
				view:{
					selectedMulti:false,
					addHoverDom: addHoverDom,
					removeHoverDom: removeHoverDom
				},
				callback:{
				}
		};
		indexTreeObj = $.fn.zTree.init(target,async);
		indexTreeObj.setting.callback.beforeExpand = function(treeId , treeNode){
			if(treeInitFlag === false){
				// ??????????????????????????????
				return true;
			}
			if(moduleType == treeNode.params.type){
				return false;
			}
			return true;
		};
		indexTreeObj.setting.callback.onNodeCreated = function(event , treeId , treeNode){
			if(("idxInfo" == treeNode.params.nodeType && (!treeNode.params.haveMeasure) )|| treeNode.params.nodeType == "measureInfo"){
				setDragDrop("#"+treeNode.tId+"_span" , "#spread");
			}
			if(treeNode.params.nodeType == "idxInfo"){
				$("#"+ treeNode.tId + "_ico").bind("click",function(){
					curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
					dialog =  BIONE.commonOpenFullDialog("????????????","rptIdxInfoPreviewBox",curTabUri, null);
				});
			}
		}
		target.parent().parent().find("#searchIcon").unbind("click");
		target.parent().parent().find("#searchInput").unbind("keydown");
		target.parent().parent().find("#searchIcon").bind("click",function(){
			initIndexLabelTree(target.parent().parent().find("#searchInput").val(),target)
		});
		target.parent().parent().find("#searchInput").bind("keydown",function(e){
			if (event.keyCode == 13) {
				initIndexLabelTree(target.parent().parent().find("#searchInput").val(),target)
	 		}
		}); 
	}
	
	function initIndexLabelTree(searchNm,target,searchObj) {
		if(searchNm == null || searchNm == ""){
			if(type != "lasync"){
				type = "lasync";
				var otherParam = {'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "0", "showEmptyFolder":"","isExpire":1};
				if(defSrc != "01"){
					otherParam.isAuth = "1";
				}
				setting ={
						async:{
							enable:true,
							url:"${ctx}/report/frame/idx/getAsyncLabelTree.json",
							autoParam:["nodeType", "id", "indexVerId"],
							otherParam: otherParam,
							dataType:"json",
							dataFilter:function(treeId,parentNode,childNodes){
								if(childNodes){
									for(var i = 0;i<childNodes.length;i++){
										childNodes[i].nodeType = childNodes[i].params.nodeType;
										childNodes[i].indexVerId = childNodes[i].params.indexVerId;
									}
								}
								return childNodes;
							}
						},
						data:{
							key:{
								name:"text"
							}
						},
						view:{
							selectedMulti:false,
							addHoverDom: addHoverDom,
							removeHoverDom: removeHoverDom
						},
						callback:{
						}
				};
				indexTreeObj = $.fn.zTree.init(target, setting,[]);
				indexTreeObj.setting.callback.onNodeCreated = function(event , treeId , treeNode){
					if(("idxInfo" == treeNode.params.nodeType && (!treeNode.params.haveMeasure) )|| treeNode.params.nodeType == "measureInfo"){
						setDragDrop("#"+treeNode.tId+"_span" , "#spread");
					}
					if(treeNode.params.nodeType == "idxInfo"){
						$("#"+ treeNode.tId + "_ico").bind("click",function(){
							curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
							dialog =  BIONE.commonOpenFullDialog("????????????","rptIdxInfoPreviewBox",curTabUri, null);
						});
					}
				}
			}
		}
		else{
			var _url = "${ctx}/report/frame/idx/getSyncLabelTree.json";
			var data = {'searchNm':searchNm ,'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "0", "showEmptyFolder":"","isExpire":1};
			if(defSrc != "01"){
				data.isAuth = "1";
			}
			if(type !="lsync"){
				type = "lsync";
				setting ={
						data : {
								keep : {
									parent : true
								},
								key : {
									name : "text"
								},
								simpleData : {
									enable : true,
									idKey : "id",
									pIdKey : "upId",
									rootPId : null
								}
						},
						view:{
							selectedMulti:false,
							addHoverDom: addHoverDom,
							removeHoverDom: removeHoverDom
						},
						callback:{
						}
				};
				indexTreeObj = $.fn.zTree.init(target,setting,[]);
				indexTreeObj.setting.callback.onNodeCreated = function(event , treeId , treeNode){
					if(("idxInfo" == treeNode.params.nodeType && (!treeNode.params.haveMeasure) )|| treeNode.params.nodeType == "measureInfo"){
						setDragDrop("#"+treeNode.tId+"_span" , "#spread");
					}
					if(treeNode.params.nodeType == "idxInfo"){
						$("#"+ treeNode.tId + "_ico").bind("click",function(){
							curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
							dialog =  BIONE.commonOpenFullDialog("????????????","rptIdxInfoPreviewBox",curTabUri, null);
						});
					}
				}
			}
			BIONE.loadTree(_url,indexTreeObj,data,function(childNodes){
				if(childNodes){
					for(var i = 0;i<childNodes.length;i++){
						childNodes[i].nodeType = childNodes[i].params.nodeType;
						childNodes[i].indexVerId = childNodes[i].params.indexVerId;
					}
				}
				return childNodes;
			},false);
		}
	}
	
	function initIndexTree(searchNm,target,searchObj) {
		if(searchNm == null || searchNm == ""){
			if(type != "async"){
				type = "async";
				var otherParam = {'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "0", "showEmptyFolder":"","isExpire":1};
				if(defSrc != "01"){
					otherParam.isAuth = "1";
				}
				setting ={
						async:{
							enable:true,
							url:"${ctx}/report/frame/idx/getAsyncTreeIdxShow.json",
							autoParam:["nodeType", "id", "indexVerId"],
							otherParam: otherParam,
							dataType:"json",
							dataFilter:function(treeId,parentNode,childNodes){
								if(childNodes){
									for(var i = 0;i<childNodes.length;i++){
										childNodes[i].nodeType = childNodes[i].params.nodeType;
										childNodes[i].indexVerId = childNodes[i].params.indexVerId;
									}
								}
								return childNodes;
							}
						},
						data:{
							key:{
								name:"text"
							}
						},
						view:{
							selectedMulti:false,
							addHoverDom: addHoverDom,
							removeHoverDom: removeHoverDom
						},
						callback:{
						}
				};
				indexTreeObj = $.fn.zTree.init(target, setting,[]);
				indexTreeObj.setting.callback.onNodeCreated = function(event , treeId , treeNode){
					if(("idxInfo" == treeNode.params.nodeType && (!treeNode.params.haveMeasure) )|| treeNode.params.nodeType == "measureInfo"){
						setDragDrop("#"+treeNode.tId+"_span" , "#spread");
					}
					if(treeNode.params.nodeType == "idxInfo"){
						$("#"+ treeNode.tId + "_ico").bind("click",function(){
							curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
							dialog =  BIONE.commonOpenFullDialog("????????????","rptIdxInfoPreviewBox",curTabUri, null);
						});
					}
				}
			}
		}
		else{
			var _url = "${ctx}/report/frame/idx/getSyncTree";
			var data = {'searchNm':searchNm ,'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "0", "showEmptyFolder":"","isExpire":1};
			if(searchObj != null && searchObj != ""){
				_url = "${ctx}/report/frame/idx/getSyncTreePro";
				data = {'searchObj':JSON2.stringify(searchObj) ,'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "0", "showEmptyFolder":"","isExpire":1};
			}
			if(defSrc != "01"){
				data.isAuth = "1";
			}
			if(type !="sync"){
				type = "sync";
				setting ={
						data : {
								keep : {
									parent : true
								},
								key : {
									name : "text"
								},
								simpleData : {
									enable : true,
									idKey : "id",
									pIdKey : "upId",
									rootPId : null
								}
						},
						view:{
							selectedMulti:false,
							addHoverDom: addHoverDom,
							removeHoverDom: removeHoverDom
						},
						callback:{
						}
				};
				indexTreeObj = $.fn.zTree.init(target,setting,[]);
				indexTreeObj.setting.callback.onNodeCreated = function(event , treeId , treeNode){
					if(("idxInfo" == treeNode.params.nodeType && (!treeNode.params.haveMeasure) )|| treeNode.params.nodeType == "measureInfo"){
						setDragDrop("#"+treeNode.tId+"_span" , "#spread");
					}
					if(treeNode.params.nodeType == "idxInfo"){
						$("#"+ treeNode.tId + "_ico").bind("click",function(){
							curTabUri  = "${ctx}/report/frame/idx/"+treeNode.id+"/show?d="+new Date().getTime();
							dialog =  BIONE.commonOpenFullDialog("????????????","rptIdxInfoPreviewBox",curTabUri, null);
						});
					}
				}
			}
			BIONE.loadTree(_url,indexTreeObj,data,function(childNodes){
				if(childNodes){
					for(var i = 0;i<childNodes.length;i++){
						childNodes[i].nodeType = childNodes[i].params.nodeType;
						childNodes[i].indexVerId = childNodes[i].params.indexVerId;
					}
				}
				return childNodes;
			},false);
		}
	}
	
	function dimTreeHandler(target , canDrag){
		target = target ? target : $(".treeContainer").children("ul");
		var async = {
				data : {
					keep : {
						parent : true
					},
					key : {
						name : "text"
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId",
						rootPId : null
					}
				},
				view : {
					selectedMulti : false,
					showLine : false
				},
				callback:{
				
				}
			};
			dimTreeObj = $.fn.zTree.init(target,async,[{
				text : "????????????",
				id : "0",
				icon : dimTreeRootIcon
			}]);
			if(typeof canDrag != "undefined"
					&& canDrag === false){
				// ?????????????????????
				return ;
			}
			dimTreeObj.setting.callback.onNodeCreated = function(event , treeId , treeNode){
				// ??????????????????????????????
				var dragarea = "";
				if(templateType == "02")
					dragarea=".queryContent";
				if(templateType == "04" || templateType == "05" || templateType == "06" )
					dragarea="#spread , .queryContent";
				setDragDrop("#"+treeNode.tId+"_span" , dragarea);
			}
	}
	
	// ?????????????????????
	// @param flag ?????????????????????????????? true/false
	function initDimTreeNodes(flag){
		if(!dimTreeObj){
			return ;
		}
		if(flag === true){
			// ????????????????????????
			allDimArray = Design.getCommonDimsByAjax();
		}else if(designInfo4Upt != null
				&& allDimArray == ""){
			allDimArray = designInfo4Upt.allDimArray == null ? "" : designInfo4Upt.allDimArray;
		}
		var nodes = [];
		var allDimNoArray = [];
		// ?????????????????????
		if(allDimArray.length>0){
			$.each(allDimArray , function(i , d){
				var nodeTmp = {};
				nodeTmp.id = d.dimTypeNo;
				nodeTmp.text = d.dimTypeNm;
				nodeTmp.upId = '0';
				nodeTmp.icon = dimTreeNodeIcon;
				nodeTmp.params = {};
				nodeTmp.params.dimType = d.dimType;
				nodeTmp.params.dimTypeStruct = d.dimTypeStruct;
				nodes.push(nodeTmp);
				allDimNoArray.push(d.dimTypeNo);
			})
			
			
		}
		allDims = allDimNoArray.join(",");
		var unit = {
				text : "????????????",
				id : "tempUnit",
				icon : unitTreeIcon,
				upId : '0',
				params : {
					dimTypeStruct : "01",
					dimType : "tempUnit"
				}
		};
		// ???????????????
		dimTreeObj.removeChildNodes(dimTreeObj.getNodeByParam("id" , '0' , null));
		dimTreeObj.removeNode(dimTreeObj.getNodeByParam("id" , 'tempUnit' , null));
		// ???????????????
		dimTreeObj.addNodes(dimTreeObj.getNodeByParam("id", '0', null) , nodes , true);
		dimTreeObj.addNodes(null , unit , true);
		dimTreeObj.expandAll(true);
	}
	
	// ??????ajax?????????
	function initNodes(type,searchNm){
		var data = [];
		type = type ? type : templateType;
		if(moduleTreeObj == null 
				|| typeof moduleTreeObj == "undefined"){
			return ;
		}
		var url = treeUrls[type]
		if(searchNm != null && searchNm != ""){
			data ={
				searchNm :searchNm
			};
		}
		else{
			treeInitFlag = true;
		}
		$.ajax({
			cache : false,
			async : true,
			url : url,
			data : data,
			dataType : 'json',
			type : "post",
			success : function(result){
				if(result != null
						&& typeof result != "undefined"){
					// ???????????????
					moduleTreeObj.removeChildNodes(moduleTreeObj.getNodeByParam("id" , '0' , null));
					moduleTreeObj.removeNode(moduleTreeObj.getNodeByParam("id", '0', null) , false);
					// ???????????????
					moduleTreeObj.addNodes(moduleTreeObj.getNodeByParam("id", '0', null) , result , true);
				}
				treeInitFlag = false;
			},
			error:function(){
				treeInitFlag = false;
				BIONE.tip("???????????????????????????????????????");
			}
		});
	}
	
	// ??????????????????
	function initDesign(){
		require.config({
			baseUrl : "${ctx}/plugin/js/",
			paths:{
				"design" : "cfg/views/rptdesign",
				"utils" : "cfg/utils/designutil"
			}
		});
		require(["design" , "utils"] , function(ds , utils){
			Utils = utils;
			var jsonStr = "";
			// ?????????
			if(designInfo4Upt != null){
				objTmp = designInfo4Upt;
				if( objTmp.filterContent){
					glofilters = objTmp.filterContent;
				}
				templateId = objTmp.tmpInfo.id.templateId;
				jsonStr = objTmp.tmpInfo.templateContentjson;
			}
			// ??????????????????
			var settings = {
					targetHeight : ($("#centerDiv").height() - $("#queryDimsDiv").height() - 2) ,
					templateType : templateType,
					ctx : "${ctx}" ,
					readOnly : false ,
					//onEnterCell : cellEnterCell , 
					onLeaveCell : spreadLeaveCell,
					onCellDoubleClick : spreadDbclkCell,
					onSelectionChanged : spreadSelectionChanged,
					onIdxsChanged : idxsChanged,
					onSave : rpt_save,
					onCssImport : css_import ,  
					cellDetail:true,
					toolbar:true,
					isBusiLine : false, // ????????????????????????????????? 
					// ???????????????????????????
					moduleCells : objTmp==null ? null : objTmp.moduleCells,
					formulaCells : objTmp==null ? null : objTmp.formulaCells,
					idxCells : objTmp==null ? null : objTmp.idxCells,
					staticCells : objTmp==null ? null : objTmp.staticCells,
					idxCalcCells : objTmp==null ? null : objTmp.idxCalcCells,
					colIdxCells : objTmp == null ? null : objTmp.colIdxCells,
					colDimCells : objTmp == null ? null : objTmp.colDimCells,
					comCells : objTmp == null ? null : objTmp.comCells,
					rowCols : objTmp==null? null : objTmp.batchCfgs,
					initJson : jsonStr
			};
			Design = ds;
			spread = ds.init($("#spread") , settings);
			Uuid = ds.Uuid;
			RptIdxInfo = ds.RptIdxInfo;
			// ???????????????
			initDetailForm();
			initToolBar();
		})
	}
	
	//??????????????????
 	function setDragDrop(dom , receiveDom){
 		if(typeof dom == "undefined"
 			|| dom == null){
 			return ;
 		}
 		$(dom).ligerDrag({
 			proxy : function(target , g , e){
 				var treeAId = target.current.target.attr("id");
 				var strsTmp = treeAId.split("_");
 				var defaultName = "??????  ";
 				if(strsTmp[0] == "cellTree2"){
 					defaultName = "??????";
 				}
 				else{
 	 				if(templateType == "02"){
 	 					defaultName = "??????  ";
 	 				}else if(templateType == "04"){
 	 					defaultName = "??? ";
 	 				}else if(templateType == "05"){
 	 					defaultName = "??? ";
 	 				}
 				}
 				
 				var proxyLabel = "${ctx}"+notAllowedIcon;
 				var targetTitle = $(dom).html() == null ? defaultName : defaultName+":"+$(dom).html();
 				var proxyHtml = $("#drag-proxy-content").tpl({
 					proxyLabel : proxyLabel , 
 					targetTitle : targetTitle
 				});
                var div = $(proxyHtml);
                div.appendTo('#center');
                return div;
 			},
 			revert : false , 
 			receive : receiveDom ? receiveDom : "#spread" , 
 			onStartDrag : function(current , e){
				// ???????????????????????????
 				var treeAId = current.target.attr("id");
				if(treeAId){
					var strsTmp = treeAId.split("_");
					var treeId = treeAId;
					if(strsTmp.length > 1){
						var newStrsTmp = [];
						for(var i = 0 ; i < strsTmp.length - 1 ; i++){
							newStrsTmp.push(strsTmp[i]);
							if(i == 0){
								draggingTreeId = strsTmp[i];
							}
						}
						treeId = newStrsTmp.join("_");
					}
					var treeObj = moduleTreeObj;
					if(templateType == "02" || templateType == "03" 
						|| templateType == "04" || templateType == "05" || templateType == "06" ){// ????????????????????????????????????????????????????????????????????????????????????
						treeObj = $.fn.zTree.getZTreeObj(draggingTreeId);
					} 
					draggingNode = treeObj.getNodeByTId(treeId);
				}
 			},
 			onDragEnter : function(receive , source , e){
 				var allowLabel = "${ctx}"+allowedIcon;
 				source.children(".dragimage_span").css("background" , "url('"+allowLabel+"')");
 			},
 			onDragLeave : function(receive , source , e){
 				var notAllowLabel = "${ctx}"+notAllowedIcon;
 				source.children(".dragimage_span").css("background" , "url('"+notAllowLabel+"')");
 			},
 			onDrop : function(obj , target , e){
 				if(typeof spread != "undefined"
					&& spread != null
					&& e != null){
					var sheet = spread.getActiveSheet();
					var canvasOffset = sheet._eventHandler._getCanvasOffset();
					var absolutePosX = e.clientX - canvasOffset.left;
					var absolutePosY = e.clientY - canvasOffset.top;
					var currRow = null;
					var currCol = null;
					/* if($.browser.msie && parseInt($.browser.version, 10) < 9){
						// ??????silverlight????????????????????????hitTest???????????????????????????????????????????????????
						var posStr = spread.canvas.firstChild.Content.SpreadsheetObject.hitTest(absolutePosX,absolutePosY);
						if(posStr != null && typeof posStr == "object"){
							currRow = posStr.Row;
							currCol = posStr.Col;
						}
					}else{	 */				
						var targetCell = sheet.hitTest(absolutePosX,absolutePosY);
						if(targetCell != null){								
							var targetRow = targetCell.row;
							var targetCol = targetCell.col;
							if((typeof targetRow == "undefined"
									|| targetRow == null)
									&& (typeof targetCol == "undefined"
									&& targetCol == null)){
								currRow = sheet.getActiveRowIndex();
								currCol = sheet.getActiveColumnIndex();
							}else{
								currRow = targetRow;
								currCol = targetCol;
							}
						}
					/* } */
					var spans = Design.getSelectionSpans(sheet , currRow , currCol);
					if(spans 
							&& typeof spans.length != "undefined"
							&& spans.length > 0){
						currRow = spans[0].row;
						currCol = spans[0].col;
					}
					switch(templateType){
						case "01":
							// ????????????
							moduleNodeDrop(currRow , currCol , draggingNode , sheet);
							
							//initDropQueryDims(draggingNode , "01");
							// ????????????????????????
							if($(obj).hasClass("queryContent")){
								// ?????????????????????????????????
								templateDsId = Design.getCurrDsId();
								if(templateDsId
										&& templateDsId != ""
										&& templateDsId != draggingNode.data.setId){
									BIONE.tip("????????????????????????????????????");
									return ;
								}
								var id = uuid.v1();
								var html = $("#query-module-template").tpl({
									paramId : id,
									fieldEnNm : draggingNode.data.enNm,
									fieldCnNm : draggingNode.data.cnNm && draggingNode.data.cnNm != "" ? draggingNode.data.cnNm : draggingNode.data.enNm,
									fieldId : draggingNode.data.colId,
									dimTypeNo : draggingNode.data.dimTypeNo,
									elementType : "",
									dbType : draggingNode.data.dbType,
									dimTypeStruct : draggingNode.data.dimTypeStruct,
									showClose : true // ????????????????????????????????????
								});
								$("#queryDimsDiv").children(".queryContent").append(html);
								$('.query-module-dropdown').dropdown();
								if(queryDimsObj){
									queryDimsObj.querySize(queryDimsObj.querySize()+1);
								}
								//edit by fangjuan 
								querys.addItem({
									required : "false",
									type : "",
									daterange : "",
									paramId : id,
									dimTypeNo : draggingNode.data.dimTypeNo,
									dbType : draggingNode.data.dbType,
									defValue : "",
									setId : draggingNode.data.setId
								});
								querys.getSelectItem(id);
							}
							break;
						case "02":
							// ???????????????
							if(draggingTreeId == "cellTree1" || draggingTreeId == "storeTree" ){
								indexNodeDrop(currRow , currCol , draggingNode , sheet);
								//Design.autoSetColumnWidth(sheet,currCol);
								// ??????????????????
								initDropQueryDims(draggingNode , "02");
								// ??????????????????
								initDimTreeNodes(true);
								
							}
							else {
								// ????????????????????????
								if($(obj).hasClass("queryContent")){
									// ?????????????????????????????????
									var currDimDom = $(".query-dim-item[dimno='"+draggingNode.id+"']");
									if(currDimDom.length <= 0){
										var id = uuid.v1();
										// ??????????????????????????????????????????
										var html = $("#query-dim-template").tpl({
											paramId : id,
											dimTypeNo : draggingNode.id,
											dimTypeNm : draggingNode.text,
											dimTypeStruct : draggingNode.params.dimTypeStruct,
											showClose : jQuery.inArray(draggingNode.id , defaultQueryDims.split(",")) == -1 ? true : false
										});
										$("#queryDimsDiv").children(".queryContent").append(html);
										$('.hoverdrop').dropdown();
										if(queryDimsObj){
											queryDimsObj.querySize(queryDimsObj.querySize()+1);
										}
										querys.addItem({
											required : "false",
											type : "",
											daterange : "",
											paramId : id,
											dimTypeNo : draggingNode.id,
											dbType : "",
											checkbox : false,
											defValue : ""
										});
									}
									
								}
							}
							break;
						case "03":
							// ????????????
							var typeTmp = "01";
							if(draggingTreeId == "cellTree1" || draggingTreeId == "storeTree" ){
								moduleNodeDrop(currRow , currCol , draggingNode , sheet);
								Design.autoSetColumnWidth(sheet,currCol);
							}else{
								indexNodeDrop(currRow , currCol , draggingNode , sheet);
								Design.autoSetColumnWidth(sheet,currCol);
								typeTmp = "02";
							}
							initDropQueryDims(draggingNode , typeTmp);
							break;
						case "04":
						case "05":
						case "06":
							// ????????????
							if(draggingTreeId == "colTree1" || draggingTreeId == "storeTree" ){
								var returnFlag = colIdxNodeDrop(currRow , currCol , draggingNode , sheet);
								if(!(returnFlag === false)){
									Design.autoSetColumnWidth(sheet,currCol);
									// ??????????????????
									initDropQueryDims(draggingNode , "02");
									// ??????????????????
									initDimTreeNodes(true);
									// ????????????????????????????????????????????????????????????????????????
									Design.clearDimCells(allDims ? allDims.split(",") : [] , selectionIdx , $("#rightCellForm [name='cellNo']").val());
								}
								
							} else {
								// ????????????????????????
								if($(obj).hasClass("queryContent")){
									// ?????????????????????????????????
									var currDimDom = $(".query-dim-item[dimno='"+draggingNode.id+"']");
									if(currDimDom.length <= 0){
										// ??????????????????????????????????????????
										var id = uuid.v1();
										var html = $("#query-dim-template").tpl({
											paramId : id,
											dimTypeNo : draggingNode.id,
											dimTypeNm : draggingNode.text,
											dimTypeStruct : draggingNode.params.dimTypeStruct,
											showClose : jQuery.inArray(draggingNode.id , defaultQueryDims.split(",")) == -1 ? true : false
										});
										$("#queryDimsDiv").children(".queryContent").append(html);
										$('.hoverdrop').dropdown();
										if(queryDimsObj){
											queryDimsObj.querySize(queryDimsObj.querySize()+1);
										}
										
										querys.addItem({
											required : "false",
											type : "",
											daterange : "",
											paramId : id,
											dimTypeNo : draggingNode.id,
											dbType : "",
											checkbox : false,
											defValue : ""
										});
									}
								}else{
									// ??????????????????????????????
									var hasOverrideIdx = colDimNodeDrop(currRow , currCol , draggingNode , sheet);
									if(hasOverrideIdx !="")
										Design.autoSetColumnWidth(sheet,currCol);
									if(hasOverrideIdx === true){
										// ??????????????????
										initDimTreeNodes(true);
										// ????????????????????????????????????????????????????????????????????????
										Design.clearDimCells(allDims ? allDims.split(",") : []);
									}
								}
							}
							break;
					}						
 				}
 			}
 		});
	}
	
	
	// ???????????????????????????drop??????
	function moduleNodeDrop(row , col , treeNode , sheet){
		if(row != null
				&& col != null
				&& treeNode != null
				&& typeof treeNode != "undefined"){
			var dsNode = treeNode.getParentNode();
			if(dsNode == null){
				return ;
			}
			// ??????drop?????????????????????????????????
			var currDataRow = Design.getCurrDataRow();
			if(currDataRow != null
					&& currDataRow != row){
				// ??????drop??????????????????????????????
				BIONE.tip("????????????????????????????????????");
				return ;
			}
			var currSelDsId
				  ,currSelDsNm;
			for(var seq in Design.rptIdxs){
				if(Design.rptIdxs[seq].cellType == "02"){
					currSelDsId = Design.rptIdxs[seq].dsId;
					currSelDsNm = Design.rptIdxs[seq].dsName;
				}
			}
			var templateDsId = Design.getCurrDsId();
			if(templateDsId
					&& templateDsId != ""){
				currSelDsId = templateDsId;
			}
			if(currSelDsId != null
					&& typeof currSelDsId != "undefined"){
				if(currSelDsId != dsNode.params.realId){
					// ????????????????????????????????????????????????
					if(currSelDsNm
							&& currSelDsNm != ""){
						BIONE.tip("????????????????????????[<font color='red'>"+currSelDsNm+"</font>]??????");
					}else{
						BIONE.tip("????????????????????????????????????");
					}
					return ;
				}
			}
			if(RptIdxInfo
					&& typeof RptIdxInfo == "object"){
				var rptIdx = RptIdxInfo.newInstance("02"); // ????????????????????????
				if(rptIdx != null){				
					rptIdx.dsId = dsNode.params.realId;
					rptIdx.dsName = dsNode.text;
					rptIdx.columnId = treeNode.params.realId;
					rptIdx.columnName = treeNode.text;
// 					rptIdx.cellNm = Utils.initAreaPosiLabel(row , col);
					templateDsId = dsNode.params.realId;
				}
				// ???????????????target????????????????????????????????????????????????Design.rptIdxs[cell.style.seq]???????????????
				// ??????????????????????????????????????????????????????????????? ??? else  ??????????????????????????????
				// [warning]: ????????????????????????Design.rptIdxs????????????key?????????????????????????????????????????????????????????
				//    cell???????????????????????????????????????????????????(seq)????????????cell???styleproperties???????????????seq????????????
				//    ?????????????????????????????????????????????????????????????????????????????????????????????????????????
				var currCell = sheet.getCell(row , col);
				var seq = Design._getStyleProperty(row, col, "seq");
				if(seq != null
						&& Design.rptIdxs[seq] != null
						&& typeof Design.rptIdxs[seq] != "undefined"){
					delete Design.rptIdxs[seq];
				}
				seq = Uuid.v1();
				Design.rptIdxs[seq] = rptIdx;
				currCell.formula("");
				currCell.value(rptIdx.cellLabel(Design.getShowType()));
				Design._setStyleProperty(row, col, "seq", seq)
				Design.autoSetColumnWidth(sheet,col);
			}
			
		}
	}
	
	// ???????????????????????????drop??????
	function indexNodeDrop(row , col , treeNode , sheet){
		if(row != null
				&& col != null
				&& treeNode != null
				&& typeof treeNode != "undefined"){
			var dsNode = treeNode;
			var parentNode = null;
			if(dsNode.params.nodeType == "measureInfo"){	
				parentNode = dsNode.getParentNode();
			} 
			if(dsNode == null){
				return ;
			}
			if(RptIdxInfo
					&& typeof RptIdxInfo == "object"){
				var rptIdx = RptIdxInfo.newInstance("03" , treeNode.data); // ????????????????????????
				if(rptIdx != null){
					rptIdx.indexNo = dsNode.params.indexNo;
					rptIdx.indexVerId = dsNode.params.indexVerId;
					rptIdx.isSum = "Y";
					rptIdx.isSave = "Y";
					rptIdx.statType = dsNode.data.statType;
					if(parentNode){
						rptIdx.indexNm = parentNode.data.indexNm + "." +dsNode.text;
						rptIdx.measureNo = dsNode.id;
						rptIdx.allDims = parentNode.params.dimNos;
						rptIdx.factDims = parentNode.params.dimNos;
					}else{
						rptIdx.indexNm = dsNode.data.indexNm;
						rptIdx.allDims = dsNode.params.dimNos;
						rptIdx.factDims = dsNode.params.dimNos;
					}
					
				}
				// ???????????????target????????????????????????????????????????????????Design.rptIdxs[cell.style.seq]???????????????
				// ??????????????????????????????????????????????????????????????? ??? else  ??????????????????????????????
				// [warning]: ????????????????????????Design.rptIdxs????????????key?????????????????????????????????????????????????????????
				//    cell???????????????????????????????????????????????????(seq)????????????cell???styleproperties???????????????seq????????????
				//    ?????????????????????????????????????????????????????????????????????????????????????????????????????????
				var currCell = sheet.getCell(row , col);
				var realIndexNo = Uuid.v1().replace(/-/g, '');
				var seq = Design._getStyleProperty(row, col, "seq");
				if(seq != null
						&& Design.rptIdxs[seq] != null
						&& typeof Design.rptIdxs[seq] != "undefined"){
					var oldIndexNo = Design.rptIdxs[seq].realIndexNo;
					if(oldIndexNo != ""
							&& typeof oldIndexNo != "undefined"){
						realIndexNo = oldIndexNo;
					}
					delete Design.rptIdxs[seq];
				}
				rptIdx.realIndexNo = realIndexNo;
// 				rptIdx.cellNm = Utils.initAreaPosiLabel(row , col);
				seq = Uuid.v1();
				Design.rptIdxs[seq] = rptIdx;
				currCell.formula("");
				currCell.value(rptIdx.cellLabel(Design.getShowType()));
				Design._setStyleProperty(row, col, "seq", seq)
			}
		}
	}
	
	// ?????????????????????????????????drop??????
	function colIdxNodeDrop(row , col , treeNode , sheet){
		if(row != null
				&& col != null
				&& treeNode != null
				&& typeof treeNode != "undefined"){
			var dsNode = treeNode;
			var parentNode = null;
			if(dsNode.params.nodeType == "measureInfo"){	
				parentNode = dsNode.getParentNode();
			} 
			if(dsNode == null){
				return ;
			}
			// ??????drop?????????????????????????????????
			var currDataRow = Design.getCurrDataRow();
			var currDataCol = Design.getCurrDataCol();
			var currIdxCol = Design.getCurrIdxCol();
			if(templateType == "04" && currDataRow != null
					&& currDataRow != row){
				// ??????drop??????????????????????????????
				BIONE.tip("??????????????????????????????????????????");
				return false;
			}
			if(templateType == "05" && currDataCol != null
					&& currDataCol != col){
				// ??????drop?????????????????????????????????
				BIONE.tip("??????????????????????????????????????????");
				return false;
			}
			if(templateType == "06" && currIdxCol != null
					&& !(currIdxCol.c == col && currIdxCol.r == row)){
				// ??????drop?????????????????????????????????
				BIONE.tip("?????????????????????????????????????????????");
				return false;
			}
			if(RptIdxInfo
					&& typeof RptIdxInfo == "object"){
				var rptIdx = RptIdxInfo.newInstance("07" , treeNode.data); // ????????????????????????
				if(rptIdx != null){
					rptIdx.indexNo = dsNode.params.indexNo;
					rptIdx.indexVerId = dsNode.params.indexVerId;
					rptIdx.isSum = "Y";
					rptIdx.isSave = "Y";
					rptIdx.statType = dsNode.data.statType;
					if(parentNode){
						rptIdx.indexNm = parentNode.text + "." +dsNode.text;
						rptIdx.measureNo = dsNode.id;
						rptIdx.allDims = parentNode.params.dimNos;
						rptIdx.factDims = parentNode.params.dimNos;
					}else{
						rptIdx.indexNm = dsNode.text;
						rptIdx.allDims = dsNode.params.dimNos;
						rptIdx.factDims = dsNode.params.dimNos;
					}
				}
				// ???????????????target????????????????????????????????????????????????Design.rptIdxs[cell.style.seq]???????????????
				// ??????????????????????????????????????????????????????????????? ??? else  ??????????????????????????????
				// [warning]: ????????????????????????Design.rptIdxs????????????key?????????????????????????????????????????????????????????
				//    cell???????????????????????????????????????????????????(seq)????????????cell???styleproperties???????????????seq????????????
				//    ?????????????????????????????????????????????????????????????????????????????????????????????????????????
				var currCell = sheet.getCell(row , col);
				var realIndexNo = Uuid.v1().replace(/-/g, '');
				var seq = Design._getStyleProperty(row, col, "seq");
				if(seq != null
						&& Design.rptIdxs[seq] != null
						&& typeof Design.rptIdxs[seq] != "undefined"){
					var oldIndexNo = Design.rptIdxs[seq].realIndexNo;
					if(oldIndexNo != ""
							&& typeof oldIndexNo != "undefined"){
						realIndexNo = oldIndexNo;
					}
					delete Design.rptIdxs[seq];
				}
				rptIdx.realIndexNo = realIndexNo;
				seq = Uuid.v1();
				Design.rptIdxs[seq] = rptIdx;
				currCell.formula("");
				currCell.value(rptIdx.cellLabel(Design.getShowType()));
				Design._setStyleProperty(row, col, "seq", seq)
			}
		}
	}
	
	// ?????????????????????????????????drop?????????
	function colDimNodeDrop(row , col , treeNode , sheet){
		var hasOverrideIdx = false;
		if(row != null
				&& col != null
				&& treeNode != null
				&& typeof treeNode != "undefined"){
			var dsNode = treeNode;
			var parentNode = null;
			if(dsNode == null){
				return "";
			}
			// ??????drop?????????????????????????????????
			var currDataRow = Design.getCurrDataRow();
			var currDataCol = Design.getCurrDataCol();
			if(templateType == "04" && currDataRow != null
					&& currDataRow != row){
				// ??????drop??????????????????????????????
				BIONE.tip("??????????????????????????????????????????");
				return "";
			}
			// ??????drop?????????????????????????????????
			if(templateType == "05" && currDataCol != null
					&& currDataCol != col){
				// ??????drop??????????????????????????????
				BIONE.tip("??????????????????????????????????????????");
				return "";
			}
			if(templateType == "06" && currDataCol != null
					&& currDataCol != col && currDataRow != null
					&& currDataRow != row){
				// ??????drop??????????????????????????????
				BIONE.tip("?????????????????????????????????????????????????????????");
				return "";
			}
			if(templateType == "06" && currDataCol != null
					&& currDataCol == col && currDataRow != null
					&& currDataRow == row){
				BIONE.tip("????????????????????????????????????");
				return "";
			}
			if(RptIdxInfo
					&& typeof RptIdxInfo == "object"){
				var rptIdx = RptIdxInfo.newInstance("08"); // ????????????????????????
				if(rptIdx != null){
					rptIdx.dimTypeNo = dsNode.id;
					rptIdx.dimTypeNm = dsNode.text;
					rptIdx.dimType = dsNode.params.dimType;
					if(rptIdx.dimTypeNo != Design.Constants.DIM_TYPE_DATE){
						delete rptIdx.dateFormat;
					}
					if(templateType == "04")
						rptIdx.extDirection = "01";
					if(templateType == "05")
						rptIdx.extDirection = "02";
					if(templateType == "06"){
						if(currDataRow == row)
							rptIdx.extDirection = "01";
						if(currDataCol == col)
							rptIdx.extDirection = "02";
					}
					
				}
				// ???????????????target????????????????????????????????????????????????Design.rptIdxs[cell.style.seq]???????????????
				// ??????????????????????????????????????????????????????????????? ??? else  ??????????????????????????????
				// [warning]: ????????????????????????Design.rptIdxs????????????key?????????????????????????????????????????????????????????
				//    cell???????????????????????????????????????????????????(seq)????????????cell???styleproperties???????????????seq????????????
				//    ?????????????????????????????????????????????????????????????????????????????????????????????????????????
				var currCell = sheet.getCell(row , col);
				var seq = Design._getStyleProperty(row, col, "seq");
				if(seq != null
						&& Design.rptIdxs[seq] != null
						&& typeof Design.rptIdxs[seq] != "undefined"){
					if(Design.rptIdxs[seq].cellType == Design.Constants.CELL_TYPE_IDXCOL){
						hasOverrideIdx = true
					}
					delete Design.rptIdxs[seq];
				}
				seq = Uuid.v1();
				Design.rptIdxs[seq] = rptIdx;
				currCell.formula("");
				currCell.value(rptIdx.cellLabel(Design.getShowType()));
				Design._setStyleProperty(row, col, "seq", seq)
				currCell.textDecoration("");
			}
		}
		return hasOverrideIdx;
	}
	
	// ??????????????????????????????form
	function initCellForm(){
		// ?????????ko????????????
		if(ko  != null
				&& typeof ko == "object"
				&& RptIdxInfo != null
				&& typeof RptIdxInfo != "undefined"){
			var sheet = spread.getActiveSheet();
			var seq = Design._getStyleProperty(sheet.getActiveRowIndex(), 
    				sheet.getActiveColumnIndex(), "seq");
			var rptIdxTmp = Design.rptIdxs[seq];
			var commonCellTmp = Design.commonCells[seq];
			selectionIdx=RptIdxInfo.newInstanceKO();
			if(rptIdxTmp){
				$("#rightCellForm [name='cellNo']").val(Utils.initAreaPosiLabel(sheet.getActiveRowIndex() , sheet.getActiveColumnIndex()));
				RptIdxInfo.initIdxKO(selectionIdx,rptIdxTmp);
			}
			else if(commonCellTmp){
				$("#rightCellForm [name='cellNo']").val(Utils.initAreaPosiLabel(sheet.getActiveRowIndex() , sheet.getActiveColumnIndex()));
				RptIdxInfo.initIdxKO(selectionIdx,commonCellTmp);
			}
			else{
				var uuid = Design.Uuid.v1();
				var commonCellTmp = RptIdxInfo.newInstance("01"); 
				Design.commonCells[uuid] = commonCellTmp;
				RptIdxInfo.initIdxKO(selectionIdx,commonCellTmp);
				selectionIdx.seq(uuid);
			}
			
			ko.applyBindings(selectionIdx , $("#rightCellForm")[0]);
		}
	}
	
	function initToolBar(){
		// ?????????ko????????????
		if(ko  != null
				&& typeof ko == "object"){
			ko.applyBindings(selectionToolBar , $(".toolbar-awesome")[0]);
		}
	}
	
	//spread???????????????????????????
	function spreadEnterCell(row , col){
		var cellNo="";
		var cellTmp = spread.getActiveSheet().getCell(row , col);
		var isFormal = (cellTmp.formula() != null && cellTmp.formula() != "") ? true : false;
		var font=Utils.getFont(cellTmp.font());
		selectionToolBar.font(font.font);
		selectionToolBar.fontSize(font.fontsize.substring(0,font.fontsize.length-2));
		selectionToolBar.bold(font.bold);
		selectionToolBar.italic(font.italic);
		selectionToolBar.textDecoration(cellTmp.textDecoration()?cellTmp.textDecoration():"");
		if(RptIdxInfo.initIdxKO
				&& typeof RptIdxInfo.initIdxKO == "function"){
			var seq = Design._getStyleProperty(row, col, "seq");
			if(seq != null
					&& Design.rptIdxs[seq] != null
					&& typeof Design.rptIdxs[seq] != "undefined"){
				// ??????????????????
				var rptIdxTmp = Design.rptIdxs[seq];
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxTmp);
				selectionIdx.seq(seq);
				if(selectionIdx.cellType() == "04"){
					// excel???????????????
					selectionIdx.excelFormula(cellTmp.formula());
				}
				// ?????????????????????
				if(Utils){					
					cellNo=Utils.initAreaPosiLabel(row , col);
// 					if(selectionIdx.cellNm() == ""){
// 						selectionIdx.cellNm(Utils.initAreaPosiLabel(row , col));
// 					}
				}
			}else if(isFormal === true){
				// ?????????????????????????????????????????????
				var uuid = Design.Uuid.v1();
				Design._setStyleProperty(row, col, "seq", uuid)
				var rptIdxInfoTmp = Design.RptIdxInfo.newInstance("04");
				rptIdxInfoTmp.excelFormula = cellTmp.formula();
				rptIdxInfoTmp.realIndexNo = Uuid.v1().replace(/-/g, '');
				Design.rptIdxs[uuid] = rptIdxInfoTmp;
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxInfoTmp);
				selectionIdx.seq(seq);
				selectionIdx.excelFormula(cellTmp.formula());
				// ?????????????????????
				if(Utils){		
					var cellNo=Utils.initAreaPosiLabel(row , col);
// 					if(selectionIdx.cellNm() == ""){
// 						selectionIdx.cellNm(Utils.initAreaPosiLabel(row , col));
// 					}
				}
			}else if(seq != null
					&& Design.commonCells[seq] != null
					&& typeof Design.commonCells[seq] != "undefined"){
				// ??????????????????
				var commonCellTmp = Design.commonCells[seq];
				if(commonCellTmp.content == ""){
					commonCellTmp.content = cellTmp.value();
				}
				RptIdxInfo.initIdxKO(selectionIdx , commonCellTmp);
				selectionIdx.seq(seq);
			}
			else{
				var uuid = Design.Uuid.v1();
				Design._setStyleProperty(row, col, "seq", uuid)
				var commonCellTmp = Design.RptIdxInfo.newInstance("01");
				commonCellTmp.content = cellTmp.value();
				Design.commonCells[uuid] = commonCellTmp;
				RptIdxInfo.initIdxKO(selectionIdx , commonCellTmp);
				selectionIdx.seq(uuid);
			}
		}
		if(!contentFlag){
			contentFlag=true;
			changeRightForm(true);
		}
		$("#rightCellForm [name='cellNo']").val(cellNo);
	}
	
	// spread????????????????????????????????????
	function spreadSelectionChanged(sender , args){
		var currSheet = Design.spread.getActiveSheet();
		var selections = currSheet.getSelections();
		if(selections.length == 1){
			var currSelection = selections[0];
			if(currSelection.colCount == 1
					&& currSelection.rowCount == 1){
				// ??????????????????????????????????????????????????????????????????enterCell?????????
				spreadEnterCell(currSelection.row , currSelection.col);
				return ;
			} else {
				var spans = Design.getSelectionSpans(currSheet);
				if(spans
						&& typeof spans.length != "undefined"
						&& spans.length == 1
						&& spans[0].rowCount == currSelection.rowCount
						&& spans[0].colCount == currSelection.colCount){
					// ??????????????????????????????????????????????????????
					spreadEnterCell(currSelection.row , currSelection.col);
					return ;
				}
			}
		}
		var font=Utils.getFont( currSheet.getCell(currSheet.getActiveRowIndex(), 
				currSheet.getActiveColumnIndex()).font());
		var fontsize=font.fontsize;
		var fontinfo=font.font;
		for(var c in selections){
			var currSelection = selections[c];
			for(var i=0;i<currSelection.rowCount;i++){
				for(var j=0;j<currSelection.colCount;j++){
					if(fontsize!=Utils.getFont(currSheet.getCell(currSelection.row+i,currSelection.col+j).font()).fontsize){
						fontsize="  pt";
						break;
					}
					if(fontinfo!=Utils.getFont(currSheet.getCell(currSelection.row+i,currSelection.col+j).font()).font){
						fontinfo="";
						break;
					}
				}
			}
		}
		selectionToolBar.font(fontinfo);
		selectionToolBar.fontSize(fontsize.substring(0,fontsize.length-2));
		selectionToolBar.bold(font.bold);
		selectionToolBar.italic(font.italic);
		selectionToolBar.textDecoration( currSheet.getCell(currSheet.getActiveRowIndex(), 
				currSheet.getActiveColumnIndex()).textDecoration()?currSheet.getCell(currSheet.getActiveRowIndex(), 
						currSheet.getActiveColumnIndex()).textDecoration():"");
		var batchObj = Design.generateBatchSelObj();
		if(batchObj != null){
			for(var i in selectionIdx){
				var reg = /^_.*Batch$/;
				if(reg.test(i)){
					selectionIdx[i](false);
				}
			}
			RptIdxInfo.initIdxKO(selectionIdx , batchObj);
		}
	}
	
	// spread???????????????????????????
	function spreadLeaveCell(sender , args){
		syncSelIdxs();
	}
	
	// ?????????????????????
	function spreadDbclkCell(sender , args){
		if(args){
			if(args.sheetArea
					&& args.sheetArea != 3){
				// ????????????????????????????????????????????????
				return ;
			}
			var currSheet = spread.getActiveSheet();
			var selRow = currSheet.getActiveRowIndex();
			var selCol = currSheet.getActiveColumnIndex();
			var currCell = spread.getActiveSheet().getCell(selRow , selCol);
			var seq = Design._getStyleProperty(selRow, selCol, "seq");
			var isFormal = currCell.formula() == null ? false : true;
			if(isFormal === true){
				return ;
			}
			if(selectionIdx.dbClkUrl() == null
					|| selectionIdx.dbClkUrl() == ""){
				return ;
			}
			if(selectionIdx.cellType() == "03"
					&& (selectionIdx.indexNo() == null
						|| selectionIdx.indexNo() == "")){
				// ???????????????,??????????????????(?????????)
				return ;
			}
			if(seq != null
					&& typeof seq != "undefined"
					&& Design.rptIdxs[seq] != null
					&& typeof Design.rptIdxs[seq] != "undefined"
					&& selectionIdx != null
					&& typeof selectionIdx == "object"){
				syncSelIdxs();
				var rptIdxTmp = Design.rptIdxs[seq];
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxTmp);
				selectionIdx.seq(seq);
				if(selectionIdx.cellType() == "04"){
					// excel???????????????
					selectionIdx.excelFormula(currCell.formula());
					return ;
				}
				// ??????????????????????????????????????????excel??????????????????
				var labelTmp = Utils.initAreaPosiLabel(selRow , selCol);
				if(Utils){		
					$("#rightCellForm [name='cellNo']").val(labelTmp);
// 					if(selectionIdx.cellNm() == ""){
// 						selectionIdx.cellNm(labelTmp);
// 					}
				}
				selectionIdx.rowId(selRow);
				selectionIdx.colId(selCol);
				var width = selectionIdx.dialogWidth();
				var height = selectionIdx.dialogHeight();
				BIONE.commonOpenDialog(selectionIdx.cellTypeNm()+':['+labelTmp+"]",
						'moduleClkDialog',
						width ? Utils.transWidthOrHeight(width , $(window).width()) : 700,
						height ? Utils.transWidthOrHeight(height , $(window).height()) : 400,
						Design._settings.ctx+selectionIdx.dbClkUrl()+'?d='+new Date().getTime());
			}
		}		
	}
	
	// ?????????????????????????????????????????????????????????handler
	function idxsChanged(){
		initDimTreeNodes(true);
	}
	
	// ???????????????????????????idxs??????
	function syncSelIdxs(){
		if(selectionIdx == null
				|| typeof selectionIdx == "undefined"
				|| typeof selectionIdx.seq == "undefined"){
			return ;
		}
		var cellType = selectionIdx.cellType();
		var seq = selectionIdx.seq();
		if(seq != null
				&& typeof seq != "undefined"
				&& cellType != null
				&& typeof cellType != "undefined"
				&& cellType != "01"){
			if(cellType == "-1"){
				// ????????????????????????
				var batchObjTmp = {};
				attrLoop : for(var attr in selectionIdx){
					var reg = /^_.*Batch$/;
					if(!reg.test(attr)
							|| selectionIdx[attr]() === false){
						continue attrLoop;
					}
					var _attr = attr.substring(1 , attr.length - 5);
					if(typeof selectionIdx[_attr] == "function"){
						// ??????????????????????????????????????????
						if(jQuery.inArray(_attr , Design.RptIdxInfo._batchSpeVals) != -1){
							continue attrLoop;
						}
						// ?????????????????????default??????????????????
						// *default????????????1.==RptIdxInfo._defaultVals????????????2.else??????""???????????????
						if(typeof Design.RptIdxInfo._defaultVals[_attr] != 'undefined'){
							// 1
							if(selectionIdx[_attr]() === Design.RptIdxInfo._defaultVals[_attr]){								
								continue attrLoop;
							}
						} else if(selectionIdx[_attr]() == ""){
							// 2
							continue attrLoop;
						}
						batchObjTmp[_attr] = selectionIdx[_attr]();
					}
				}
				var seqs = seq.split(",");
				for(var i = 0 , l = seqs.length ; i < l ; i++){
					var rptIdxTmp = Design.rptIdxs[seqs[i]];
					if(rptIdxTmp){
						jQuery.extend(rptIdxTmp , batchObjTmp);
					}
				}
			}else{				
				//??????????????????????????????????????????
				var rptIdx = Design.rptIdxs[seq];
				if(rptIdx){
					for(var i in rptIdx){
						if(typeof rptIdx[i] == "function"){
							continue;
						}
						if(i in selectionIdx){
							rptIdx[i] = selectionIdx[i]();
						}
					}
				}
			}
		}
		if(seq != null
				&& typeof seq != "undefined"
				&& cellType != null
				&& typeof cellType != "undefined"
				){
			if(cellType == "-1"){
				// ????????????????????????
				var batchObjTmp = {};
				attrLoop : for(var attr in selectionIdx){
					var reg = /^_.*Batch$/;
					if(!reg.test(attr)
							|| selectionIdx[attr]() === false){
						continue attrLoop;
					}
					var _attr = attr.substring(1 , attr.length - 5);
					
					if(typeof selectionIdx[_attr] == "function"){
						// ??????????????????????????????????????????
						if(jQuery.inArray(_attr , Design.RptIdxInfo._batchSpeVals) != -1){
							continue attrLoop;
						}
						// ?????????????????????default??????????????????
						// *default????????????1.==RptIdxInfo._defaultVals????????????2.else??????""???????????????
						if(typeof Design.RptIdxInfo._defaultVals[_attr] != 'undefined'){
							// 1
							if(selectionIdx[_attr]() === Design.RptIdxInfo._defaultVals[_attr]){								
								continue attrLoop;
							}
						} else if(selectionIdx[_attr]() == ""){
							// 2
							continue attrLoop;
						}
						batchObjTmp[_attr] = selectionIdx[_attr]();
					}
				}
				var seqs = seq.split(",");
				for(var i = 0 , l = seqs.length ; i < l ; i++){
					var comCellTmp = Design.commonCells[seqs[i]];
					if(comCellTmp){
						jQuery.extend(comCellTmp , batchObjTmp);
					}
				}
			}
			else if(cellType == "01"){
				var commonCell = Design.commonCells[seq];
				if(commonCell){
					for(var i in commonCell){
						if(typeof commonCell[i] == "function"){
							continue;
						}
						if(i in selectionIdx){
							commonCell[i] = selectionIdx[i]();
						}
					}
				}
			}
		}
	}
	
	// ????????????????????????
	function cssLoadHandler(jsonObj){
		if(jsonObj != null
				&& typeof jsonObj != "undefined"){
			var objResult = JSON2.parse(jsonObj);
			RptIdxInfo.initIdxKO(selectionIdx);
			Design.fromJSON(objResult.json);
			if(objResult.formula){
				for(var posi in objResult.formula){
					var formulaTmp = objResult.formula[posi];
					if(formulaTmp == null
							|| formulaTmp == ""){
						continue;
					}
					var posis = posi.split(",");
					if(posis.length == 2){
						var rowTmp = posis[0];
						var colTmp = posis[1];
						if(rowTmp == null
								|| rowTmp == ""
								|| colTmp == null
								|| colTmp == ""){
							continue;
						}
						var cellTmp = Design.spread.getActiveSheet().getCell(rowTmp , colTmp);
						var seqTmp = Uuid.v1();
						var currLabel = Utils.initAreaPosiLabel(rowTmp , colTmp);
						Design._setStyleProperty(rowTmp , colTmp, "seq", seqTmp)
						var rptIdxTmp = RptIdxInfo.newInstance("04");
						rptIdxTmp.seq = seqTmp;
// 						rptIdxTmp.cellNm = currLabel;
						rptIdxTmp.realIndexNo = Uuid.v1().replace(/-/g, '');
						rptIdxTmp.excelFormula = formulaTmp;
						Design.rptIdxs[seqTmp] = rptIdxTmp; 
					}
				}
			}
		}
	}
	
	// ???????????????????????????????????????
	function selCellSettingHandler(val , type){
		var currSheet = Design.spread.getActiveSheet();
		var selCell = currSheet.getCell(currSheet.getActiveRowIndex() , currSheet.getActiveColumnIndex());
		var currLabel = Utils.initAreaPosiLabel(selCell.row , selCell.col);
		var seqTmp = Design._getStyleProperty(currSheet.getActiveRowIndex(),
				currSheet.getActiveColumnIndex(), "seq");
		switch (type){
		case "02" : 
			syncSelIdxs();
			break;
		case "03" :
			syncSelIdxs();
			break;
		case "05" :
			if(!val
					|| typeof val.formulaContent == "undefined"
					|| typeof val.formulaDesc == "undefined"){
				return ;
			}
			var rptIdxTmp = {};
			var realIndexNo = Uuid.v1().replace(/-/g, '');
			if(seqTmp != null
					&& seqTmp != ""
					&& typeof seqTmp != "undefined"
					&& Design.rptIdxs[seqTmp] != null
					&& typeof Design.rptIdxs[seqTmp] == "object"){
				var oldIndexNo = Design.rptIdxs[seqTmp].realIndexNo;
				if(oldIndexNo != ""
						&& typeof oldIndexNo != "undefined"){
					realIndexNo = oldIndexNo;
				}
				// ????????????????????????????????????
				rptIdxTmp = RptIdxInfo.newInstance(type);
				jQuery.extend(rptIdxTmp , val);
// 				rptIdxTmp.cellNm = currLabel;
				rptIdxTmp.seq = seqTmp;
				if(rptIdxTmp.realIndexNo == null
						|| rptIdxTmp.realIndexNo == ""){
					rptIdxTmp.realIndexNo = realIndexNo;
				}
				Design.rptIdxs[seqTmp] = rptIdxTmp; 
			}else{
				seqTmp = Uuid.v1();
				rptIdxTmp = RptIdxInfo.newInstance(type);
				jQuery.extend(rptIdxTmp , val);
				rptIdxTmp.seq = seqTmp;
// 				rptIdxTmp.cellNm = currLabel;
				if(rptIdxTmp.realIndexNo == null
						|| rptIdxTmp.realIndexNo == ""){
					rptIdxTmp.realIndexNo = Uuid.v1().replace(/-/g, '');
				}
				Design.rptIdxs[seqTmp] = rptIdxTmp;
				Design._setStyleProperty(currSheet.getActiveRowIndex(),
						currSheet.getActiveColumnIndex(), "seq", seqTmp)
			}
			selCell.formula("");
			selCell.value(rptIdxTmp.cellLabel(Design.getShowType()));
			// ???????????????????????????selCell?????????????????????
			if($("#rightCellForm [name='cellNo']").val() == currLabel){				
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxTmp);
				$("#rightCellForm [name='cellNo']").val(currLabel);
				syncSelIdxs();
			}
			// ????????????????????????????????????
			unionQueryDims(val.formulaDims);
			break;
		case "06":
			var rptIdxTmp = {};
			var currLabel = Utils.initAreaPosiLabel(selCell.row , selCell.col);
			if(seqTmp != null
					&& seqTmp != ""
					&& typeof seqTmp != "undefined"
					&& Design.rptIdxs[seqTmp] != null
					&& typeof Design.rptIdxs[seqTmp] == "object"){
				if(Design.rptIdxs[seqTmp].cellType == "06"){
					// ????????????????????????????????????
					Design.rptIdxs[seqTmp].expression = val;
					jQuery.extend(rptIdxTmp , Design.rptIdxs[seqTmp]);
				} else {
					// ????????????????????????????????????
					rptIdxTmp = RptIdxInfo.newInstance(type);
// 					rptIdxTmp.cellNm = currLabel;
					rptIdxTmp.seq = seqTmp;
					rptIdxTmp.expression = val;
					Design.rptIdxs[seqTmp] = rptIdxTmp; 
				}
			}else{
				seqTmp = Uuid.v1();
				rptIdxTmp = RptIdxInfo.newInstance(type);
				rptIdxTmp.seq = seqTmp;
// 				rptIdxTmp.cellNm = currLabel;
				rptIdxTmp.expression = val;
				Design.rptIdxs[seqTmp] = rptIdxTmp;
				Design._setStyleProperty(currSheet.getActiveRowIndex(),
						currSheet.getActiveColumnIndex(), "seq", seqTmp)
			}
			selCell.formula("");
			selCell.value(rptIdxTmp.cellLabel(Design.getShowType()));
			// ???????????????????????????selCell?????????????????????
			if($("#rightCellForm [name='cellNo']").val() == currLabel){				
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxTmp);
				$("#rightCellForm [name='cellNo']").val(currLabel);
			}
			break;
		}
	}
	
	// ??????????????????
	// ????????????????????????????????????????????????
	function unionQueryDims(dims){
		if(!dims){
			return queryDims;
		}
		var dimsArr = dims.split(",");
		var queryDimsArr = queryDims.split(",");
		f1 : for(var i = 0 , l = queryDimsArr.length ; i < l ; i++){
			if(jQuery.inArray(queryDimsArr[i] , dimsArr) == -1){
				// ??????????????????
				setQueryDimState(defaultQueryDims , true);
				break f1;
			}
		}
	}
	
	// ?????????????????????
	function prepareDatas4Save(){
		var saveObj = null;
		if(Design
				&& spread){
			var tabId = "_mainTab";
			var jsonStr = "";
			saveObj = {
					lineId : lineId,
					templateId : templateId,
					tmpJson : "", 
					rptIdxs : [],
					cellsArray :[],
					idxsArray : [],
					rowCols : [],
					tmpRemark : "",
					publicDim : "",   // ????????????
					queryDim : null,  // ????????????
					detailArr : null, // ???????????????(????????????)
					paramJson : null,   // ??????????????????
					storeIdxNos : storeIdxNos,//??????????????????
					paramId : paramTemplateId ? paramTemplateId : ""
			};
			// ???????????????????????????
			// 1.???????????????????????????leaveCell??????
			// 2.????????????????????????
			// 3.???Design.rptIdxs????????????????????????????????????
			// 4.???Design.rptIdxs????????????????????????seq????????????????????????cell
			// 		?????????????????????rptIdx???????????????
			//		????????????????????????rptIdx????????????
			syncSelIdxs();
			var commonDimArray = Design.getCommonDimsByAjax();
			var commonDimNos = [];
			if(commonDimArray
					&& typeof commonDimArray.length != "undefined"){
				$.each(commonDimArray , function(i , d){
					commonDimNos.push(d.dimTypeNo);
				});
			}
			saveObj.publicDim = commonDimNos.join(",");
			var currSheet = spread.getActiveSheet();
			var firstCell = currSheet.getCell(0,0);
			if((firstCell.value() == null
					|| firstCell.value() == "")
					&& firstCell.formula() == null){
				// ???????????????????????????????????????????????????????????????????????????json??????dataTable??????????????????????????????
				// ??????????????????????????????????????????????????????????????????????????????????????????????????????dataTable??????????????????????????? = ???=
				firstCell.value("");
			}
			if(Design.rptIdxsSize() > 0
					|| Design.rowColsSize() > 0 || Design.commonCells.length > 0){
				// ????????????cell????????????????????????seq??????
				var rowCount = currSheet.getRowCount();
				var colCount = currSheet.getColumnCount();
				var cellOldVals = [];
				for(var r = 0 ; r <= rowCount ; r++){
					for(var c = 0 ; c <= colCount ; c++){
						if(r == 0
								&& c != 0){
							// ?????????
							var colSeqTmp = Design._getStyleProperty(-1, c - 1, "seq");
							var objTmp = Design.rowCols[colSeqTmp];
							if(colSeqTmp != null
									&& colSeqTmp != ""
									&& typeof colSeqTmp != "undefined"
									&&  objTmp){
								objTmp.rowId = r;
								objTmp.colId = c;
								saveObj.rowCols.push(objTmp);
							}
						}else if(r != 0
								&& c == 0){
							// ?????????
							var rowSeqTmp = Design._getStyleProperty(r - 1, -1, "seq");
							var objTmp = Design.rowCols[rowSeqTmp];
							if(rowSeqTmp != null
									&& rowSeqTmp != ""
									&& typeof rowSeqTmp != "undefined"
									&&  objTmp){
								objTmp.rowId = r;
								objTmp.colId = c;
								saveObj.rowCols.push(objTmp);
							}
						}
						// ?????????????????????
						var cellTmp = currSheet.getCell( r , c);
						var seqTmp = Design._getStyleProperty(r, c, "seq");
						if(seqTmp != null
								&& seqTmp != ""
								&& typeof seqTmp != "undefined"
								&& Design.rptIdxs[seqTmp] != null
								&& typeof Design.rptIdxs[seqTmp] == "object"){
							if(Design.rptIdxs[seqTmp].cellType == Design.Constants.CELL_TYPE_COMMON){
								// ???????????????????????????
								cellTmp.value("");
								cellTmp.formula("");
								Design._setStyleProperty(r, c, "seq", null)
								continue;
							}
							Design.rptIdxs[seqTmp].rowId = r;
							Design.rptIdxs[seqTmp].colId = c;
							Design.rptIdxs[seqTmp].cellNo = Utils.initAreaPosiLabel(r , c);
							if(typeof Design.rptIdxs[seqTmp].realIndexNo != "undefined"
									&& (Design.rptIdxs[seqTmp].realIndexNo == null
											|| Design.rptIdxs[seqTmp].realIndexNo == "")){
								Design.rptIdxs[seqTmp].realIndexNo = Uuid.v1().replace(/-/g, '');
							}
							if(cellTmp.formula() != null
									&& cellTmp.formula() != ""){
								Design.rptIdxs[seqTmp].excelFormula = cellTmp.formula();
							}
							saveObj.idxsArray.push(Design.rptIdxs[seqTmp]);							
							if(tabId == "_mainTab"){								
								if(Design.rptIdxs[seqTmp].cellType == Design.Constants.CELL_TYPE_FORMULA){
									continue;
								}
								var keyTmp = cellTmp.row + "," + cellTmp.col;
								cellOldVals.push(keyTmp);
							}
						} 
						else if(seqTmp != null
								&& seqTmp != ""
								&& typeof seqTmp != "undefined"
								&& Design.commonCells[seqTmp] != null
								&& typeof Design.commonCells[seqTmp] == "object"){
							if(Design.commonCells[seqTmp].cellType == Design.Constants.CELL_TYPE_COMMON){
								if(Design.commonCells[seqTmp].typeId != "00"){
									// ????????????????????????
									Design.commonCells[seqTmp].rowId = r;
									Design.commonCells[seqTmp].colId = c;
									Design.commonCells[seqTmp].cellNo = Utils.initAreaPosiLabel(r , c);
									saveObj.cellsArray.push(Design.commonCells[seqTmp]);		
								}
								else{
									Design._setStyleProperty(r, c, "seq", null)
								}
							}
							else{
								Design._setStyleProperty(r, c, "seq", null)
							}
							
						} 
						else if (cellTmp.formula() != null
								&& cellTmp.formula() != ""){
							// ??????excel???????????????????????????????????????????????????????????????????????????????????????????????????
							var rptIdxTmp = RptIdxInfo.newInstance(Design.Constants.CELL_TYPE_FORMULA);
							var cellNmTmp = Utils.initAreaPosiLabel(r , c);
							rptIdxTmp.rowId = r;
							rptIdxTmp.colId = c;
							rptIdxTmp.cellNo = cellNmTmp;
// 							rptIdxTmp.cellNm = cellNmTmp;
							rptIdxTmp.realIndexNo = Uuid.v1().replace(/-/g, '');
							rptIdxTmp.excelFormula = cellTmp.formula();
							saveObj.idxsArray.push(rptIdxTmp);
						}
					}
				}
				if(tabId == "_mainTab"){
					var jsonTmp = spread.toJSON();
					var jsonMin;
					if(Utils
							&& typeof Utils.jsonMin == "function"){
						jsonMin = Utils.jsonMin(jsonTmp,cellOldVals);
					}else{
						jsonMin = jsonTmp;
					}
					jsonStr = JSON2.stringify(jsonMin);
					saveObj.tmpJson = jsonStr;
				}
			}
		}
		var queryDim4Save = this.prepareQueryDims(saveObj.idxsArray);
		saveObj.filterContent = JSON2.stringify(glofilters);	
		saveObj.queryDim = queryDim4Save.dimNoArr ? queryDim4Save.dimNoArr.join(",") : defaultQueryDims,  // ????????????
		saveObj.detailArr =	queryDim4Save.detailObjArr ? queryDim4Save.detailObjArr : [], // ???????????????(????????????)
		saveObj.paramJson = queryDim4Save.queryTmpArr ? JSON2.stringify(queryDim4Save.queryTmpArr) : (templateType=="01" ? "" : defaultParamJson),   // ??????????????????
		saveObj.paramId = paramTemplateId ? paramTemplateId : ""
		return saveObj;
	}
	
	// ???????????????
	function initEmptyIdxCell(sheet , cellInfo){
		if(!sheet || !cellInfo){
			return ;
		}
		var seq = Design._getStyleProperty(cellInfo.row, cellInfo.col, "seq");
		var rptInfoTmp = Design.rptIdxs[seq];
		var realIndexNo = Uuid.v1().replace(/-/g, '');
		if(seq != null
				&& Design.rptIdxs[seq] != null
				&& typeof Design.rptIdxs[seq] != "undefined"){
			var oldIndexNo = Design.rptIdxs[seq].realIndexNo;
			if(oldIndexNo != ""
					&& typeof oldIndexNo != "undefined"){
				realIndexNo = oldIndexNo;
			}
			delete Design.rptIdxs[seq];
		}
		seq = Design.Uuid.v1();
		var emptyInfo = Design.RptIdxInfo.newInstance("03");
		emptyInfo.realIndexNo = realIndexNo;
		emptyInfo.indexNo = "";
		emptyInfo.indexNm = "";
		emptyInfo.dbClkUrl = ""; // ????????????????????????????????????????????????????????????
		var currLabel = Utils.initAreaPosiLabel(cellInfo.row , cellInfo.col);
// 		emptyInfo.cellNm = currLabel;
		Design._setStyleProperty(cellInfo.row, cellInfo.col, "seq", seq)
		Design.rptIdxs[seq] = emptyInfo;
		Design._setCellFormula(cellInfo.row , cellInfo.col , "" , sheet);
		Design._setCellValue(cellInfo.row , cellInfo.col , emptyInfo.cellLabel(Design.getShowType()) , sheet);
		if(Utils
				&& currLabel == $("#rightCellForm [name='cellNo']").val()){					
			RptIdxInfo.initIdxKO(selectionIdx , emptyInfo);
			$("#rightCellForm [name='cellNo']").val(currLabel);
		}
	}
	
	function getSelectionLabel(){
		var selObj = {};
		var currSheet = Design.spread.getActiveSheet();
		selObj.row = currSheet.getActiveRowIndex();
		selObj.col = currSheet.getActiveColumnIndex();
		selObj.label = Utils.initAreaPosiLabel(currSheet.getActiveRowIndex() , currSheet.getActiveColumnIndex());
		return selObj;
	}
	
	// ?????????????????????????????????
	function cellNmChangeHandler(){
		if(Design.Constants.SHOW_TYPE_CELLNM == Design.getShowType()){
			// ??????????????????????????????????????????label
			var newVal = $("input[name=cellNm]").val();
			var currCellNo = $("input[name=cellNo]").val(); 
			var rowCol = Design.Utils.posiToRowCol(currCellNo);
			var row = rowCol.row;
			var col = rowCol.col;
			var currSheet = Design.spread.getActiveSheet();
			currSheet.getCell(row , col).value("{"+newVal+"}");
		}
	}
	
	// ???????????????????????????
	// @params dropType   01-???????????? ??? 02-??????
	function initDropQueryDims(node , dropType){
		if(!node
				&& designInfo4Upt != null){
			// ???????????????????????????????????????
			var objTmp = designInfo4Upt;
			paramTemplateId = objTmp.paramTemplateId;
			allDims = objTmp.publicDim;
			queryDims = objTmp.queryDim;
			detailObjs = objTmp.detailObjs;
			if(templateType == "01"){
				// ?????????
				setQueryModuleState(detailObjs);
			}else{
				setQueryDimState(queryDims);
			}
			return ;
		}
		if(!queryDims){
			// ??????????????????????????????????????????????????????????????????????????????
			setQueryDimState(defaultQueryDims);
			return ;
		}
		// ????????????????????????
		// -- ??????????????????????????????????????????
		// 			??????????????????????????????????????????????????????????????????????????????
		//			??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
		var itemId = "";
		switch(dropType){
		case "01" :
			itemId = node.params.setId;
			break;
		case "02" :
			itemId = node.params.indexNo;
			break;
		}
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/design/cfg/custom/dropItemContainDimsOrNot",
			data : {
				dropType : dropType,
				itemId : itemId,
				queryDimStr : queryDims
			},
			dataType : 'json',
			type : "post",
			success : function(result){
				if(result != null
						&& typeof result != "undefined"){
					var flag = result.flag;
					if(typeof flag == "undefined"
							|| flag === true){
						return;
					}
					// ??????????????????????????????????????????????????????????????????????????????????????????
					setQueryDimState(defaultQueryDims , true);
				}
			}
		})
	}
	
	// ???????????????????????????dom
	function initQueryDimsDom(dimArrayStr){
		var dimArray = dimArrayStr == null ? [] : dimArrayStr.split(",");
		// ????????????????????????
		$("#queryDimsDiv").children(".queryContent").empty();
		// ????????????
		if(dimArray
				&& typeof dimArray.length != "undefined"){
			// ???????????????????????????
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/design/cfg/custom/getDimsInfo",
				data : {
					dimArrayStr : dimArrayStr
				},
				dataType : 'json',
				type : "post",
				success : function(result){
					if(result != null
							&& typeof result != "undefined"){
						if(designInfo4Upt){
							var paramJson = JSON2.parse(designInfo4Upt.paramJson);
						}
						$.each(dimArray , function(i , dimTmp){
							var dimTypeTmp = result[dimTmp] ? result[dimTmp] : null;
							var dimTypeNmTmp = dimTypeTmp ? dimTypeTmp.dimTypeNm : dimTmp;
							var dimTypeStruct = dimTypeTmp ? dimTypeTmp.dimTypeStruct : "";
							var paramId ="";
							if(dimTmp == "DATE"){
								var defValue = "";
								var showType = "date";
								for(var k in paramJson){
									if(paramJson[k].key == "DATE"){
										if(paramJson[k].value)
											defValue = paramJson[k].value;
										if(paramJson[k].showType)
											showType = paramJson[k].showType;
									}
								}
								querys.addItem({
									required : "true",
									type : "",
									daterange : "",
									paramId : "DATE",
									dimTypeNo : dimTmp,
									dbType : "",
									defValue : defValue,
									showType : showType
								});
								paramId = "DATE";
							}
							else{
								for(var k in paramJson){
									if(paramJson[k].key == dimTmp){
										querys.addItem({
											required : paramJson[k].required,
											type : "",
											daterange : "",
											paramId :paramJson[k].id,
											dimTypeNo : dimTmp,
											dbType : "",
											checkbox : ""+paramJson[k].type=="select" ?  paramJson[k].isMultiSelect : paramJson[k].checkbox,
											defValue : paramJson[k].value ? paramJson[k].value:""
										});
										paramId = paramJson[k].id;
										break;
									}
								}
							}
							if(dimTmp == "tempUnit"){
								dimTypeNmTmp = "????????????";
							}
							var html = $("#query-dim-template").tpl({
								paramId : paramId,
								dimTypeNo : dimTmp,
								dimTypeNm : dimTypeNmTmp,
								dimTypeStruct : dimTypeStruct,
								showClose : jQuery.inArray(dimTmp , defaultQueryDims.split(",")) == -1 ? true : false
							});
							$("#queryDimsDiv").children(".queryContent").append(html);
						});
					}
				}
			})
		}
	}
	
	// ???????????????????????????dom
	function initQueryModulesDom(detailObjs){
		// ????????????????????????
		$("#queryDimsDiv").children(".queryContent").empty();
		// ????????????
		if(detailObjs
				&& typeof detailObjs.length != "undefined"
				&& detailObjs.length > 0 && designInfo4Upt.paramJson){
			templateDsId = detailObjs[0].dsId;
			//???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????designInfo4Upt.paramJson
			//?????????????????????orderNum?????????
			//???????????????????????????rpt_paramtmp_attrs???rpt_design_query_detail ???????????????
			//designInfo4Upt.paramJson????????????????????????orderNum????????????
			//edit by fangjuan 
			var paramJson = JSON2.parse(designInfo4Upt.paramJson);
			$.each(detailObjs , function(i , detailTmp){
				if(detailTmp.elementType == '01' || detailTmp.elementType == '02' || detailTmp.elementType == '08' ){
					querys.addItem({
						type : detailTmp.elementType,
						paramId : paramJson[i].id,
						dimTypeNo : detailTmp.dimTypeNo,
						dbType : detailTmp.dbType,
						required : JSON2.parse(paramJson[i].validate).required,
						daterange : paramJson[i].dayRangeMax,
						defValue : paramJson[i].value ? paramJson[i].value:""
					});
				}else{
					querys.addItem({
						type : detailTmp.elementType,
						paramId : paramJson[i].id,
						dimTypeNo : detailTmp.dimTypeNo,
						dbType : detailTmp.dbType,
						required : paramJson[i].required,
						daterange : paramJson[i].dayRangeMax,
						defValue : paramJson[i].value ? paramJson[i].value:""
					});
				}
				var html = $("#query-module-template").tpl({
					paramId : paramJson[i].id,
					fieldEnNm : detailTmp.enNm,
					fieldCnNm : detailTmp.cnNm && detailTmp.cnNm != "" ? detailTmp.cnNm : detailTmp.enNm,
					fieldId : detailTmp.columnId,
					dimTypeNo : detailTmp.dimTypeNo,
					dimTypeStruct : detailTmp.dimTypeStruct,
					elementType : detailTmp.elementType,
					dbType : detailTmp.dbType,
					showClose : true // ????????????????????????????????????
				});
				$("#queryDimsDiv").children(".queryContent").append(html);
				$('.query-module-dropdown').dropdown();
			});
		}
	}
	
	// ??????????????????????????????
	// @params dimArgs : ????????????????????????????????????????????????
	// @params clearParamFlag : ????????????????????????????????????
	function setQueryDimState(dimArgs , clearParamFlag){
		if(!dimArgs
				|| dimArgs == ""){
			dimArgs = defaultQueryDims;
		}
		if(!dimArgs
				|| dimArgs == ""){
			// ????????????
			if(typeof queryDimsObj.querySize == "function"){
				queryDimsObj.querySize(0);
			}
			return ;
		}
		initQueryDimsDom(dimArgs);
		queryDims = dimArgs;
		if(typeof queryDimsObj.querySize == "function"){
			queryDimsObj.querySize(dimArgs.split(",").length);
		}
	}
	
	// ??????????????????????????????????????????
	// @params detailObjs : ????????????,??????java???RptDesignQueryDetailVO.java
	function setQueryModuleState(detailObjs){
		initQueryModulesDom(detailObjs);
		if(typeof queryDimsObj.querySize == "function"
				&& detailObjs
				&& typeof detailObjs.length != "undefined"){
			queryDimsObj.querySize(detailObjs.length);
		}
	}
	
	// ??????????????????????????????
	function getQueryDimsInfo(){
		var returnObj = [];
		if(Design
				&& Design.ParamComp
				&& Design.Constants){
			if(templateType == Design.Constants.TEMPLATE_TYPE_MODULE){
				// ???????????????????????????
				var queryModuleDoms = $(".query-module-item");
				for(var i = 0 , l = queryModuleDoms.length ; i < l ; i++){
					returnObj.push({
						paramid : $(queryModuleDoms[i]).attr("paramid"),
						enNm : $(queryModuleDoms[i]).attr("fieldEnNm"),
						cnNm : $(queryModuleDoms[i]).attr("fieldCnNm"),
						orderNo : i
					});
				}
			}else{
				var queryDimDoms = $(".query-dim-item");
				for(var i = 0 , l = queryDimDoms.length ; i < l ; i++){
					// ?????????????????????
					returnObj.push({
						enNm : $(queryDimDoms[i]).attr("dimno"),
						cnNm : $(queryDimDoms[i]).attr("dimnm"),
						orderNo : i
					});
				}
			}
		}
		return returnObj;
	}
	
	// ????????????????????????
	// @return {
    //	dimNoArr : Array ????????????????????????		
	//	detailObjArr : Array  ??????????????????
	//  queryTmpArr : Array ?????????????????????  
	// }
	function prepareQueryDims(rptIdxs){
		var returnObj = {
			dimNoArr : [],
			detailObjArr : [],
			queryTmpArr : []
		};
		if(Design
				&& Design.ParamComp
				&& Design.Constants){
			if(templateType == Design.Constants.TEMPLATE_TYPE_MODULE){
				// ???????????????????????????
				var queryModuleDoms = $(".query-module-item");
				for(var i = 0 , l = queryModuleDoms.length ; i < l ; i++){
					var paramid = $(queryModuleDoms[i]).attr("paramid");
					var enNm = $(queryModuleDoms[i]).attr("fieldEnNm");
					var cnNm = $(queryModuleDoms[i]).attr("fieldCnNm");
					var colId = $(queryModuleDoms[i]).attr("fieldId");
					var dimTypeNo = $(queryModuleDoms[i]).attr("dimTypeNo");
					var dimTypeStruct = $(queryModuleDoms[i]).attr("dimTypeStruct");
					var elementType ;
					var moduleObj;
					//edit by fangjuan
					for(var j=0;j<querys.allItems().length;j++){
						if(paramid == querys.allItems()[j].paramId){
							moduleObj = {
									daterange : querys.allItems()[j].daterange,
									required : querys.allItems()[j].required,
									paramId : paramid,
									enNm : enNm,
									cnNm : cnNm,
									colId : colId,
									dimTypeNo : dimTypeNo,
									dimTypeStruct : dimTypeStruct,
									elementType : querys.allItems()[j].type,
									orderNo : i,
									defValue : querys.allItems()[j].defValue
								};
							elementType = querys.allItems()[j].type;
						}
					}
					/* var enNm = $(queryModuleDoms[i]).attr("fieldEnNm");
					var cnNm = $(queryModuleDoms[i]).attr("fieldCnNm");
					var colId = $(queryModuleDoms[i]).attr("fieldId");
					var dimTypeNo = $(queryModuleDoms[i]).attr("dimTypeNo");
					var dimTypeStruct = $(queryModuleDoms[i]).attr("dimTypeStruct");
					var elementType = $(queryModuleDoms[i]).children(".query-module-eletype").val(); */
					
					// ????????????
					returnObj.detailObjArr.push({
						orderNum : i,
						dsId : templateDsId,
						columnId : colId,
						elementType : elementType,
						dimTypeNo : dimTypeNo
					});
					var objTmp = Design.ParamComp.generateModuleTmp(moduleObj);
					if(objTmp != null){
						// ????????????
						returnObj.queryTmpArr.push(objTmp);
					}
				}
			}else{
				var queryDimDoms = $(".query-dim-item");
				for(var i = 0 , l = queryDimDoms.length ; i < l ; i++){
					// ?????????????????????
					var paramid = $(queryDimDoms[i]).attr("paramid");
					var dimTypeNo = $(queryDimDoms[i]).attr("dimno");
					var dimTypeNm = $(queryDimDoms[i]).attr("dimnm");
					var dimTypeStruct = $(queryDimDoms[i]).attr("dimstruct");
					returnObj.dimNoArr.push(dimTypeNo);
					var required = "true";
					var checkbox = "false";
					if(dimTypeNo=="DATE"){
						for(var k in rptIdxs){
							if(rptIdxs[k].cellType=="08"&&rptIdxs[k].dimTypeNo=="DATE"){
								dimTypeNo="DATERANGE";
								break;
							}
						}
						required = "true";
					}
					for(var j=0;j<querys.allItems().length;j++){
						if(paramid == querys.allItems()[j].paramId){
							required = querys.allItems()[j].required;
							checkbox =  querys.allItems()[j].checkbox;
							defValue =  querys.allItems()[j].defValue;
							showType =  querys.allItems()[j].showType;
							break;
						}
					}
					var objTmp = Design.ParamComp.generateDimTmp({
						dimTypeNo : dimTypeNo,
						dimTypeNm : dimTypeNm,
						dimTypeStruct : dimTypeStruct,
						orderNo : i,
						required : required,
						checkbox : checkbox,
						defValue : defValue,
						showType : showType
					});
					if(objTmp != null){
						returnObj.queryTmpArr.push(objTmp);
					}
				}
			}
		}
		return returnObj;
	}
	
	//@param exit : true -> ???????????????false -> ??????????????? ; undefined or else : ??????confirm?????????????????????
	function saveHandler(tabBaseObj , tabDesignObj , exit){
		var returnObj = {
			rptId : "",
			verId : "",
			templateId : ""
		};
		var data = {};
		if(tabBaseObj != null){
			rptNm = tabBaseObj.rptNm;
			var objTmp = {};
			jQuery.extend(objTmp , tabBaseObj);
			data.baseObj = JSON2.stringify(objTmp);
		}
		if(tabDesignObj != null
				&& typeof tabDesignObj != "undefined"){
			data.mainTmp = JSON2.stringify(tabDesignObj);
		}
		var baseWindow = $.ligerui.get("baseEdit") ? $.ligerui.get("baseEdit").frame : this ;
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/design/cfg/saveFrsRpt",
			dataType : 'json',
			data : data,
			type : "post",
			beforeSend : function() {
				baseWindow.BIONE.loading = true;
				baseWindow.BIONE.showLoading("????????????????????????..");
			},
			success : function(result){
				BIONE.tip("????????????");
				if (typeof BIONE != 'undefined') {
					baseWindow.BIONE.loading = false;
					baseWindow.BIONE.hideLoading();
				}
				beforeCloseFunc(result);
				var exitHandler = function(){
					BIONE.closeDialog("rptEdit");
				};
				var noExitHandler = function(result){
					rptId = result.rptId;
					returnObj.rptId = result.rptId;
					baseInfo4Upt.rptId = rptId;
					verId = result.verId;
					returnObj.verId = result.verId;
					baseInfo4Upt.verId = verId;
					templateId = result.lineTmpIds._mainTab;
					returnObj.templateId = result.lineTmpIds._mainTab;
					baseInfo4Upt.templateId = templateId;
					paramTemplateId = result.paramTemplateId;
					returnObj.paramTemplateId = result.paramTemplateId;
					baseInfo4Upt.paramTemplateId = paramTemplateId;
					catalogId = result.catalogId;
					returnObj.catalogId = result.catalogId;
					if($.ligerui.get("baseEdit")){
						$.ligerui.get("baseEdit").close();
					}
				};
				if(exit === true){
					exitHandler();
				}else if(exit === false){
					// ????????????????????????????????????????????????
					noExitHandler(result);
				}else {
					$.ligerDialog.confirm('????????????????????????????????????', function(yes) {
						if(yes){
							exitHandler();
						}else{
							noExitHandler(result);
						}
					}).bind("close" , function(){
					});
						noExitHandler(result);
				}
			},
			error:function(){
				if (typeof BIONE != 'undefined') {
					baseWindow.BIONE.loading = false;
					baseWindow.BIONE.hideLoading();
				}
				BIONE.tip("???????????????????????????????????????");
			}
		});
		return returnObj; 
	}
	
	function beforeCloseFunc(data) {
		if(data){
			// ?????????
			var topWindow = window.top.$contentWindow;
			topWindow.searchHandler();
			if(topWindow.clickNodeType == topWindow.rptNodeType){
				// ?????????????????????tab???label
				if(data.rptNm){
					topWindow.mainTab.setHeader("topTab","[??????]"+data.rptNm);
				}
			}
			// ????????????
			topWindow.document.getElementById("gridFrame").contentWindow.grid.reload();
		}
	}
	
	// ???????????????
	// @return obj{ validateFlag : ?????????????????? ; msg : ????????????}
	function rpt_validate(){
		if(Design
				&& Design.rptIdxsSize() > 0
				&& (templateType == Design.Constants.TEMPLATE_TYPE_IDXCOL_V 
						|| templateType == Design.Constants.TEMPLATE_TYPE_IDXCOL_H )){
			// ????????????,????????????????????????,???????????????????????? ????????????,??????(1???)??????(n???) ?????????
			var currSheet = Design.spread.getActiveSheet();
			var rowCount = currSheet.getRowCount();
			var colCount = currSheet.getColumnCount();
			var dimCellMap = {}; // ?????????????????? - ????????????
			var dimDownMap = {}; // ?????????????????? - ????????????
			var dimColFlag = false;
			for(var r = 0 ; r <= rowCount ; r++){
				for(var c = 0 ; c <= colCount ; c++){
					var cellTmp = currSheet.getCell(r , c);
					var seqTmp = Design._getStyleProperty(r, c, "seq");
					if(seqTmp != null
							&& seqTmp != ""
							&& typeof seqTmp != "undefined"
							&& Design.rptIdxs[seqTmp] != null
							&& typeof Design.rptIdxs[seqTmp] == "object"){
						var cellTmp = Design.rptIdxs[seqTmp];
						if(cellTmp.cellType == Design.Constants.CELL_TYPE_DIMCOL){
							dimColFlag = true;
							if(cellTmp.displayLevel != null && cellTmp.displayLevel.toString().indexOf("N")<0){
								if(typeof dimCellMap[cellTmp.dimTypeNo] != 'undefined'){
									var preLvl = dimCellMap[cellTmp.dimTypeNo];
									var currLvl = cellTmp.displayLevel;
									try{										
										if(preLvl != ""
												&& (currLvl == ""
													|| Number(preLvl) > Number(currLvl))){
											currSheet.setSelection(r , c , 1 , 1);
											return {
												validateFlag : false,
												msg : "???????????????[<font color='red'>"+Utils.initAreaPosiLabel(r , c)+"</font>]    <font color='red'>????????????</font>    ???????????????????????????????????????"
											};
										}
									}catch(e){
										currSheet.setSelection(r , c , 1 , 1);
										return {
											validateFlag : false,
											msg : "???????????????[<font color='red'>"+Utils.initAreaPosiLabel(r , c)+"</font>]    <font color='red'>????????????</font>    ???????????????"
										};
									}
								}
								dimCellMap[cellTmp.dimTypeNo] = cellTmp.displayLevel;
							}
							else{
								if(dimDownMap[cellTmp.dimTypeNo] != null){
									currSheet.setSelection(r , c , 1 , 1);
									return {
										validateFlag : false,
										msg : "???????????????[<font color='red'>"+Utils.initAreaPosiLabel(r , c)+"</font>]    <font color='red'>????????????</font>    ?????????????????????????????????????????????"
									};
								}
								dimDownMap[cellTmp.dimTypeNo] = cellTmp.displayLevel;
							}
							
						}
					}
				}
			}
			if(!dimColFlag){
				return {
					validateFlag : false,
					msg : "?????????????????????????????????????????????"
				};
			}
		} 
		else if(templateType === Design.Constants.TEMPLATE_TYPE_MODULE){
			// ????????????,?????????????????????????????????
			var queryModuleDoms = $(".query-module-item");
			if(!queryModuleDoms
					|| typeof queryModuleDoms.length == "undefined"
					|| queryModuleDoms.length <= 0){
				if($("#queryDimsToggle").attr("currType") == "hide"){
					// ?????????????????????????????????????????????
					$("#queryDimsToggle").trigger("click");
				}
				return {
					validateFlag : false,
					msg : "?????????????????????????????????"
				};
			}
		}
		if(Design.rptIdxsSize() <= 0){
			return {
				validateFlag : false,
				msg : "???????????????????????????"
			};
			
		}
		return {
			validateFlag : true,
			msg : "????????????"
		};
	}
	
	// ????????????????????????dialog
	function openSaveDialog(){
		$.ligerDialog.open({
			id : 'baseEdit',
			title : '????????????',
			url: '${ctx}/report/frame/design/cfg/baseEdit?defSrc='+defSrc+'&templateType='+templateType+"&catalogId="+catalogId+"&rptId="+rptId+"&verId="+verId+"&templateId="+templateId,
			width: 600, 
			height: 450, 
			modal: true, 
			isResize: false,
			allowClose : true,
			showMin : false
		});
	}
	
	// ??????
	function rpt_save() {
		Design.leaveCurrCell();
		var validateObj = rpt_validate();
		if(validateObj.validateFlag === false){
			// ???????????????
			if(typeof validateObj.msg != "undefined"
					&& validateObj.msg != ""){
				BIONE.tip(validateObj.msg);
			}
			return ;
		}
		if(rptId
				&& rptId != ""){
			// ???????????????
			var design4Save = prepareDatas4Save();
			saveHandler(baseInfo4Upt , design4Save);
		}else{
			// ???????????????
			openSaveDialog();
		}
	}
	
	// ????????????
	function css_import(){
		BIONE.commonOpenDialog('????????????','requireuploadWin',600,330,'${ctx}/report/frame/design/cfg/cssupload?d='+new Date().getTime());
	}
	
	// ????????????????????????
	function displayLvlHandler(target){
		var dimDom ;
		if(target.nodeName == "INPUT"){
			dimDom = $(target).prev();
		}else if(target.nodeName == "DIV"){
			dimDom = $(target).parent().prevAll(".displayLvlDimNo");
		}
		if(dimDom
				&& dimDom.val()){
			var currSheet = spread.getActiveSheet();
			var selRow = currSheet.getActiveRowIndex();
			var selCol = currSheet.getActiveColumnIndex();
			var labelTmp = Utils.initAreaPosiLabel(selRow , selCol);
			var width = selectionIdx.dialogWidth();
			var height = selectionIdx.dialogHeight();
			BIONE.commonOpenDialog(selectionIdx.cellTypeNm()+':['+labelTmp+"]",
					'moduleClkDialog',
					width ? Utils.transWidthOrHeight(width , $(window).width()) : 700,
					height ? Utils.transWidthOrHeight(height , $(window).height()) : 400,
					Design._settings.ctx+selectionIdx.dbClkUrl()+'?d='+new Date().getTime());
		}
	};
	
	function defValueHandler(target){
		
	}
	function filterConten(target){
		var dimDom ;
		if(target.nodeName == "INPUT"){
			dimDom = $(target).prev();
		}else if(target.nodeName == "DIV"){
			dimDom = $(target).parent().prevAll(".displayLvlDimNo");
		}
		if(dimDom
				&& dimDom.val()){
			var currSheet = spread.getActiveSheet();
			var selRow = currSheet.getActiveRowIndex();
			var selCol = currSheet.getActiveColumnIndex();
			var labelTmp = Utils.initAreaPosiLabel(selRow , selCol);
			var width = selectionIdx.dialogWidth();
			var height = selectionIdx.dialogHeight();
			BIONE.commonOpenDialog(selectionIdx.cellTypeNm()+':['+labelTmp+"]",
					'moduleClkDialog',
					width ? Utils.transWidthOrHeight(width , $(window).width()) : 700,
					height ? Utils.transWidthOrHeight(height , $(window).height()) : 400,
					Design._settings.ctx+selectionIdx.dbClkUrl()+'?d='+new Date().getTime());
		}
	};
	
	function idxshowtype() {
		idxTarget.parent().parent().find("#searchInput").val("");
		if($("#idxcatalog")[0].checked == true){
			indexTreeHandler($("#cellTreeContainer1").children("ul"));
			idxlabelFlag = false;
		}
		else{
			indexLabelTreeHandler($("#cellTreeContainer1").children("ul"));
			idxlabelFlag = true;
		}
	}
	
	function colshowtype() {
		idxTarget.parent().parent().find("#searchInput").val("");
		if($("#colcatalog")[0].checked == true){
			indexTreeHandler($("#colTreeContainer1").children("ul"));
			idxlabelFlag = false;
		}
		else{
			indexLabelTreeHandler($("#colTreeContainer1").children("ul"));
			idxlabelFlag = true;
		}
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="designLayout" style="width: 99.8%;">
			<div id="leftDiv" position="left">
				<div id="treeAccordion">
					<div class="treeContainer"
						style="width: 99%; overflow: auto; clear: both; background-color: #FFFFFF;">
						<div class="l-text-wrapper" style="width: 99%;">                         
							<div class="l-text l-text-date" style="width: 100%;">                    
								<input id="searchInput" type="text" class="l-text-field"  style="width: 100%;" />    
								<div class="l-trigger">                                                                      
									<div id="searchIcon" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>                                                 
								</div>
							</div>                                                                                                   
						</div> 
						<ul id="tree1"
							style="font-size: 12; background-color: #FFFFFF; width: 92%"
							class="ztree">
						</ul>
					</div>
				</div>
				<div id="idxCellLayout" style="display: none;">
					<div position="center" title="????????????">
						<div class="cellTreeContainer" id="cellTreeContainer2"
							style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
							<ul id="cellTree2"
								style="font-size: 12; background-color: #FFFFFF; width: 92%"
								class="ztree">
							</ul>
						</div>
					</div>
					<div position="centerbottom" title="??????">
						<div id="idxtab" style="width: 100%; overflow: hidden;">
							<div tabid="common" title="????????????" lselected="true">
								<div class="l-text-wrapper" style="width: 99%;">                         
										<div class="l-text l-text-date" style="width: 100%;">                    
											<input id="searchInput" type="text" class="l-text-field"  style="width: 100%;" />    
											<div class="l-trigger">                                                                      
												<div id="searchIcon" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>                                                 
											</div>
										</div>                                                                                                   
									</div> 
									<div
										style="background-image: url(/biapp-frs-web/css/classics/ligerUI/Gray/images/ui/gridbar.jpg); border: 1px solid #D6D6D6; padding-left: 10px; padding-top: 3px; height: 20px;">
										<span>??????</span><input type="radio" id="idxcatalog" name="idxshowtype"
											value="idxcatalog" style="width: 20px;" onclick="idxshowtype()"
											checked="true" /> <span>??????</span> <input type="radio" id="idxlabel"
											name="idxshowtype" value="idxlabel" style="width: 20px;"
											onclick="idxshowtype()" />
									</div>
								<div class="cellTreeContainer" id="cellTreeContainer1"
									style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
									<ul id="cellTree1"
										style="font-size: 12; background-color: #FFFFFF; width: 92%"
										class="ztree">
									</ul>
								</div>
							</div>
							<div tabid="store" title="????????????" lselected="true">
								<div class="storeTreeContainer" id="storeTreeContainer1"
									style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
									<ul id="storeTree"
										style="font-size: 12; background-color: #FFFFFF; width: 92%"
										class="ztree">
									</ul>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div id="idxColLayout" style="display: none;">
					<div position="center" title="????????????">
						<div class="colTreeContainer" id="colTreeContainer2"
							style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
							<ul id="colTree2"
								style="font-size: 12; background-color: #FFFFFF; width: 92%"
								class="ztree">
							</ul>
						</div>
					</div>
					<div position="centerbottom" title="??????">
						<div id="idxColtab" style="width: 100%; overflow: hidden;">
							<div tabid="common" title="????????????" lselected="true">
								<div class="l-text-wrapper" style="width: 99%;">                         
										<div class="l-text l-text-date" style="width: 100%;">                    
											<input id="searchInput" type="text" class="l-text-field"  style="width: 100%;" />    
											<div class="l-trigger">                                                                      
												<div id="searchIcon" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>                                                 
											</div>
										</div>                                                                                                   
									</div>
								<div
										style="background-image: url(/biapp-frs-web/css/classics/ligerUI/Gray/images/ui/gridbar.jpg); border: 1px solid #D6D6D6; padding-left: 10px; padding-top: 3px; height: 20px;">
										<span>??????</span><input type="radio" id="colcatalog" name="colshowtype"
											value="colcatalog" style="width: 20px;" onclick="colshowtype()"
											checked="true" /> <span>??????</span> <input type="radio" id="collabel"
											name="colshowtype" value="collabel" style="width: 20px;"
											onclick="colshowtype()" />
								</div>
								<div class="colTreeContainer" id="colTreeContainer1"
									style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
									<ul id="colTree1"
										style="font-size: 12; background-color: #FFFFFF; width: 92%"
										class="ztree">
									</ul>
								</div>
							</div>
							<div tabid="store" title="????????????" lselected="true">
								<div class="storeTreeContainer" id="storeTreeContainer2"
									style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
									<ul id="storeTree"
										style="font-size: 12; background-color: #FFFFFF; width: 92%"
										class="ztree">
									</ul>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div id="centerDiv" position="center">
				<div id="queryDimsDiv" style="width: 99.8%; padding-top: 2px;">
					<div class="l-layout-header">
						<div id="queryDimsToggle" currType="hide"
							style="position: absolute; top: 3px; right: 3px; height: 20px; width: 20px; overflow: hidden; cursor: pointer;">
						</div>
						<div class="l-layout-header-inner">
							<div width="8%"
								style="float: left; position: relative; height: 20p; margin-top: 5px">
								<img class="queryDimImg" src="" />
							</div>
							<div width="90%">
								<span id="searchSpan"
									style="font-size: 12; float: left; position: relative; line-height: 25px; padding-left: 2px">
									<span style="font-size: 12" class="queryDimTitle">???????????????[&nbsp;<font
										style="color: red" " data-bind="text:querySize"></font>&nbsp;]&nbsp;&nbsp;
									</span>
								</span>
								<span
									style="font-size: 12; float: left; position: relative; line-height: 25px;">
									<span style="font-size: 12"></span>
								</span>
							</div>
						</div>
					</div>
					<div class="queryContent"></div>
				</div>
				<div id="spread"
					style="width: 99.8%; border-bottom: 1px solid #D0D0D0;"></div>
			</div>
			<div id="rightDiv" position="right" title="???????????????">
				<form id="rightCellForm" role="form" class="l-form">
					<!-- from template-content.jsp -->
				</form>
				<form id="rightQueryForm" role="form" class="l-form">
					<!-- from template-content.jsp -->
				</form>
			</div>
		</div>
	</div>
</body>
</html>