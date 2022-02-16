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
	   	};
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

	var cellBaseInfo = null; //单元格明细模板参数
	
	var lineId = "${lineId}";

	// 设计器相关对象 - begin
	var Design,
		   spread,
		   RptIdxInfo,
		   Utils,
		   Uuid,jsonTmp;
	// 设计器相关对象 - end
	var templateType = "${templateType}",    // 01 - 明细类；02 - 单元格类 ; 03 - 综合类 ； 04 - 指标列表（纵） ； 05 -  04 - 指标列表（横）
		  rptId = "${rptId}",
		  verId = "${verId}",  // 当前版本号
		  maxVerId = "${maxVerId}", // 最新版本号
		  canEdit = "${canEdit}", // 是否可编辑
		  verStartTime = "", // 版本开始时间
		  defSrc = "${defSrc}" , // 定义来源
		  catalogId = "${catalogId}"; // 目录ID
		  
	var baseInfo4Upt = null,
		  designInfo4Upt = null,
		  paramTemplateId = "", // 查询模板id
		  rptNm = "";
	
	var templateId = "";
	var contentFlag = true; //单元格属性、  查询属性
	var templateDsId = "";  // 明细报表，数据集ID
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
	titles["01"] = "数据模型";
	titles["02"] = "指标";
	var layout;
	
	var catalogType = "01";
	var moduleType = "02";
	var colCatalogType = "03";
	var colType = "04";
	
	// 拖拽标签头图标
	var notAllowedIcon = "/images/classics/icons/cancel.gif";
	var allowedIcon = "/images/classics/icons/accept.png";
	
	var treeInitFlag = true;
	
	// 正在拖拽的节点对象
	var draggingNode;
	var draggingTreeId;
	
	// 已选中的报表指标单元格
	var selectionIdx = {};
	// 已选中的报表查询条件
	
	// 已选中的报表指标单元格(工具栏用)
	var selectionToolBar = {
		font : ko.observable('宋体'),
		fontSize : ko.observable(10),
		bold : ko.observable('normal'),
		italic : ko.observable('normal'),
		textDecoration : ko.observable('normal')
	};
	
	var allQueryParams = [];
	// 报表查询维度相关
	var allDimArray = ""; // 公共维度对象集合
	var allDims = "";   // 报表维度编码
	var queryDims = "";   // 查询维度
	var queryDimsObj = {};
	// 缺省查询维度
	var defaultQueryDims = "${defaultQueryDims}";
	// 日期
	var defaultParamJson = '[{"type":"date","id":"de9406d0-ff7f-11e4-ae9d-1503db5aaea1","name":"DATE","display":"数据日期","newline":"false","disabled":"null","showTime":"false","required":"true","format":"yyyyMMdd"}]';
	// 机构、日期
// 	var defaultParamJson2 = '[{"type":"date","id":"de9406d0-ff7f-11e4-ae9d-1503db5aaea1","name":"DATE","display":"数据日期","newline":"false","disabled":"null","showTime":"false","required":"true","format":"yyyyMMdd"},{"type":"popup","id":"de99d330-ff7f-11e4-ae9d-1503db5aaea1","name":"ORG","display":"机构","newline":"false","disabled":"null","value":"null","checkbox":"true","required":"false","dialogWidth":"400","dialogHeight":"500","datasource":"{\\"options\\":{\\"url\\":\\"/report/frame/datashow/idx/orgTree\\",\\"ajaxType\\":\\"post\\"}}"}]';
	// 机构、日期、币种
// 	var defaultParamJson = '[{"type":"date","id":"de9406d0-ff7f-11e4-ae9d-1503db5aaea1","name":"DATE","display":"数据日期","newline":"false","disabled":"null","showTime":"false","required":"true"},{"type":"popup","id":"de99d330-ff7f-11e4-ae9d-1503db5aaea1","name":"ORG","display":"机构","newline":"false","disabled":"null","value":"null","checkbox":"true","required":"true","dialogWidth":"400","dialogHeight":"500","datasource":"{\\"options\\":{\\"url\\":\\"/report/frame/datashow/idx/orgTree\\",\\"ajaxType\\":\\"post\\"}}"},{"type":"popup","id":"de9d7cb0-ff7f-11e4-ae9d-1503db5aaea1","name":"CURRENCY","display":"币种","newline":"false","disabled":"null","value":"null","checkbox":"true","required":"true","dialogWidth":"400","dialogHeight":"500","datasource":"{\\"options\\":{\\"url\\":\\"/report/frame/design/paramtmp/getTreeDimItems?dimTypeNo=CURRENCY\\",\\"ajaxType\\":\\"post\\"}}"}]';
	//是否自动调整列宽
	var autoColumnWidth=false;
	
	var searchObj = {exeFunction : "loadIndexTree",searchType : "idx"};//默认执行initNodes方法
	var targetParam;//保留原有参数
	$(function() {
		// 修改时初始化数据
		initUptDatas();
		// 布局
		initLayout();
		// 左侧的树
		initTree();
		// 设计器
		initDesign();
		
		//添加高级搜索按钮
		$(".l-trigger").css("right","26px");
		var innerHtml = '<i class="l-trigger" style="right:20px;width:1px;height:12px;border-left:1px dotted gray;margin-top:4px;"></i>'+
			'<div title="高级筛选" class="l-trigger" style="right:0px;"><div id="highSearchIcon" onclick="javascript:initSeniorSearch();" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/application_form.png) no-repeat 50% 50% transparent;"></div></div>';
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
		//添加高级搜索弹框
		if(idxlabelFlag){
			BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/rpt/frame/rptSearch/labelhighSearch?type=idx");
		}
		else{
			BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/rpt/frame/rptSearch/highSearch?searchObj="+JSON2.stringify(searchObj));
		}
	}
	//高级搜索
	function loadIndexTree(searchNm,searchObj){
		initIndexTree(searchNm,targetParam,searchObj);
	}
	// 修改时初始化数据
	function initUptDatas(){
		if(rptId != null
				&& rptId != ""
				&& typeof rptId != "undefined"){
			// 1.基本信息
			baseInfo4Upt =  '${rptInfo}' ? JSON2.parse('${rptInfo}') : null;
			if(baseInfo4Upt != null){
				verStartTime = baseInfo4Upt.verStartDate;
				verId = baseInfo4Upt.verId;
			}
			templateType = "${templateType}";
			// 2.设计器信息
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
					BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}
	
	// 初始化布局
	function initLayout(){
		// 初始化窗口title位置
		window.parent.$(".l-dialog-title").css("text-align" , "center");
		// 初始化layout title
		var leftTitle =         
			'<div width="8%"                                                                                                             '+
			'	style="float: left; position: relative; height: 20p; margin-top: 5px">    '+
			'		<img src="${ctx }/images/classics/icons/application_side_tree.png" />                       '+
			'</div>                                                                                                                             '+
			'<div width="90%">                                                                                                         '+
			'	<span                                                                                                                             '+
			'		style="font-size: 12; float: left; position: relative; line-height: 25px; padding-left: 2px"> '+
			'		<span style="font-size: 12">工具栏</span>                                     '+
			'	</span>                                                                                                                         '+
			'</div>                                                                                                                             ';
		$("#leftDiv").attr("title",leftTitle);
		// 初始化ligerlayout
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
					// 若是指标列表
					var leftWidth = $("#designLayout").children(".l-layout-left").width();
					$("#idxColLayout").children(".l-layout-center").width(leftWidth);
					$("#idxColLayout").children(".l-layout-centerbottom").width(leftWidth);
					$("#colTreeContainer1").width(leftWidth);
					$("#colTreeContainer2").width(leftWidth);
					$("#storeTreeContainer").width(leftWidth);
				} else if(templateType == "02" ){
					// 若是指标单元格报表
					var leftWidth = $("#designLayout").children(".l-layout-left").width();
					$("#idxCellLayout").children(".l-layout-center").width(leftWidth);
					$("#idxCellLayout").children(".l-layout-centerbottom").width(leftWidth);
					$("#cellTreeContainer1").width(leftWidth);
					$("#cellTreeContainer2").width(leftWidth);
					$("#storeTreeContainer").width(leftWidth);
				}
			}
		});
		//初始化左侧树布局
		var treeContainerHeight = $("#centerDiv").height() - 25 - 25 - 2;
		if(templateType == "02"){
			// 若是指标单元格报表（指标，维度）
			$("#treeAccordion").hide();
			$("#idxCellLayout").show();
			var idxLayoutHeight = ($("#center").height() - 25 - 2) * 0.65;
			var dimLayoutHeight = ($("#center").height() - 25  - 2)  - idxLayoutHeight - 2;
			// 初始化指标列表工具栏layout
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
			// 若是综合类报表(指标，数据模型)
			// 多减去一个accordion item高度
			treeContainerHeight -= 27;
			// 即有数据模型，也有指标
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
			// 若是指标列表（指标，维度）
			$("#treeAccordion").hide();
			$("#idxColLayout").show();
			var idxLayoutHeight = ($("#center").height() - 25 - 2) * 0.65;
			var dimLayoutHeight = ($("#center").height() - 25  - 2)  - idxLayoutHeight - 2;
			// 初始化指标列表工具栏layout
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
		
		// 初始化查询条件区域
		initQueryDimsCss();
		
		// 对resize的相关处理
		resizeHandler();
	}
	
 	function changeRightForm(flag){
		// 切换单元格明细及查询明细模板
		
		if(flag){
			$("#queryDimsDiv .query-hightlight").removeClass("query-hightlight");
			$(".l-layout-right").find(".l-layout-header").find(".l-layout-header-inner").html("查询信息");
			$("#rightCellForm").html($("#template-content").tpl(cellBaseInfo));
			ko.cleanNode($("#rightCellForm")[0]);  
			ko.applyBindings(selectionIdx , $("#rightCellForm")[0]);
			$("#rightQueryForm").css("display", "none");
			$("#rightCellForm").css("display","");
		}
		else{
			Design.clearCellTarget();
			$(".l-layout-right").find(".l-layout-header").find(".l-layout-header-inner").html("查询信息");
			$("#rightQueryForm").css("display", "");
			$("#rightCellForm").css("display","none");
			
			//ko.cleanNode($("#rightQueryForm")[0]);  
			//ko.applyBindings(selectionQuery , $("#rightQueryForm")[0]);
		}
	}
	function initDetailForm(){
		// 初始化单元格明细模板
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
					// 初始化属性部分的数据绑定
					initCellForm();
				}
			}
		});
	}
	
	// 初始化查询条件区域样式
	function initQueryDimsCss(){
		// 初始化title图标
		
		$("#rightQueryForm").html($("#template-query").tpl());
		
		querys = new selectionQuery();
		
		ko.applyBindings(querys,  $("#rightQueryForm")[0]);
		
		$("#rightQueryForm").css("display", "none");
		
		
		$(".queryDimImg").attr("src" , "${ctx}/images/classics/icons/chart_pie_edit.png");
		
		// 报表查询条件 隐藏/展现按钮事件/样式
		$("#queryDimsToggle").css("background","url('${ctx}/css/classics/ligerUI/Gray/images/layout/togglebar.gif')");
		// 初始化是查询条件隐藏
		$("#queryDimsToggle").css("background-position","0px 0px");
		$("#queryDimsDiv").children(".queryContent").hide();
		$("#queryDimsToggle").bind("mouseenter" , function(){
			// 鼠标移入
			var type = $("#queryDimsToggle").attr("currType");
			if(type == "show"){
				// 展现时
				$("#queryDimsToggle").css("background-position","0px -60px");
			} else {
				// 隐藏时
				$("#queryDimsToggle").css("background-position","0px -20px");
			}
		})
		$("#queryDimsToggle").bind("mouseleave" , function(){
			// 鼠标移出
			var type = $("#queryDimsToggle").attr("currType");
			if(type == "show"){
				// 展现时
				$("#queryDimsToggle").css("background-position","0px -40px");
			} else {
				// 隐藏时
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
		// close按钮对应的事件
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
		// module click事件
		$(".queryContent").delegate(".query-module-clickable" , "click" , function(){
			var source = $(this);
			var target = $(this).parent().prev().children("i");
			var eleTypeDom = $(this).parent().parent().next(".query-module-eletype");
			target.removeClass(source.attr("classRemove")).addClass(source.attr("classAdd"));
			eleTypeDom.val(source.attr("eleType"));
		});
		
		// 查询条件相关数据绑定
		var lengthTmp = (!queryDims || queryDims == "") ? 0 : queryDims.split(",").length;
		queryDimsObj.querySize = ko.observable(lengthTmp);
		ko.applyBindings(queryDimsObj , $(".queryDimTitle")[0]);
		
		initDropQueryDims();
		
		// 初始化sortable
		dimSortable = $("#queryDimsDiv").children(".queryContent").sortable({
			containment : $("#queryDimsDiv").children(".queryContent"),
			placeholder : "dim-drag-place",
			tolerance : 'pointer'
		});
		
		// 初始化内容部分resizable
		$("#queryDimsDiv").resizable({
			handles:"s", // south方向可以拖拽
			autoHide:true ,  // 隐藏右下角handler
			maxWidth : $("#queryDimsDiv").width(),
			minWidth : $("#queryDimsDiv").width(),
			minHeight : 57, // 25px+25px+2
			maxHeight : ($("#center").height()) * 0.4,
			resize : function(event , ui){
				// 只能调节高度
				$(".queryContent").height($(this).height() - 25 - 3);
				if(Design){
					Design.spreadDom.height($("#centerDiv").height() - $("#queryDimsDiv").height() - $("#_spreadToolbar").height() - $("#_spreadCellDetail").height() - 2);
					Design.resize(spread);
				};
			}
		});
		// 去掉由于jquery ui resizable出现的但此处不需要的宽度调节handler和右下角调节handler(可以使用handlers和authHide配置项来隐藏)
		//$("#queryDimsDiv").children(".ui-resizable-e , .ui-resizable-se").remove();
	}
	
	// 跳转到查询条件的明细页面（目前被废弃，直接使用外面基于sorable的标签拖拽来实现配置）
	function queryDimMoreHandler(){
		// 获取当前报表指标对应的公共维度集合
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
		BIONE.commonOpenDialog('设置查询条件',
				'queryDimDialog',
				Utils.transWidthOrHeight("80%" , $(window).width()),
				Utils.transWidthOrHeight("80%" , $(window).height()),
				'${ctx}/report/frame/design/paramtmp');
	}
	
	// resize时的相关处理
	function resizeHandler(){
		$("#queryDimsToggle").bind("click" , function(){
			// 鼠标点击
			var type = $("#queryDimsToggle").attr("currType");
			if(type == "show"){
				// 显示时
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
				// 隐藏时
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
	
	// 初始化左侧树
	function initTree(){
		if(templateType == "01"){
			moduleTreeHandler();
			// 初始化节点
			initNodes();
		}else if(templateType == "02"){
			initIdxTab();
			// 初始化指标树
			storeTreeHandler($("#storeTreeContainer1").children("ul"));
			indexTreeHandler($("#cellTreeContainer1").children("ul"));
			// 初始化指标节点
			initNodes("02");
			// 初始化维度树
			dimTreeHandler($("#cellTreeContainer2").children("ul"));
			initDimTreeNodes();
		}else if(templateType == "03"){
			initIdxTab();
			// 初始化数据模型树
			moduleTreeHandler($($(".treeContainer")[0]).children("ul"));
			// 初始化节点
			initNodes("01");
			// 初始化指标树
			storeTreeHandler($($(".treeContainer")[1]).children("ul"));
			indexTreeHandler($($(".treeContainer")[1]).children("ul"));
		}else if(templateType == "04" || templateType == "05" || templateType == "06" ){
			initColIdxTab();
			// 初始化指标树
			storeTreeHandler($("#storeTreeContainer2").children("ul"));
			indexTreeHandler($("#colTreeContainer1").children("ul"));
			// 初始化指标节点
			initNodes("02");
			// 初始化维度树
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
			// 明细报表时，控制只展开到【数据模型】级
			moduleTreeObj.setting.callback.beforeExpand = function(treeId , treeNode){
				if(treeInitFlag === false){
					// 非初始化时，直接展开
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
			+ "'title='收藏' onfocus='this.blur();'></span>";
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
					BIONE.tip("收藏成功");
				}
				else{
					BIONE.tip("指标已收藏");
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
			+ "'title='删除' onfocus='this.blur();'></span>";
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
					dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
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
					dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
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
				// 非初始化时，直接展开
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
					dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
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
				// 非初始化时，直接展开
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
					dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
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
							dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
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
							dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
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
							dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
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
							dialog =  BIONE.commonOpenFullDialog("指标查看","rptIdxInfoPreviewBox",curTabUri, null);
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
				text : "共有维度",
				id : "0",
				icon : dimTreeRootIcon
			}]);
			if(typeof canDrag != "undefined"
					&& canDrag === false){
				// 不允许拖拽维度
				return ;
			}
			dimTreeObj.setting.callback.onNodeCreated = function(event , treeId , treeNode){
				// 所有的维度均可以拖拽
				var dragarea = "";
				if(templateType == "02")
					dragarea=".queryContent";
				if(templateType == "04" || templateType == "05" || templateType == "06" )
					dragarea="#spread , .queryContent";
				setDragDrop("#"+treeNode.tId+"_span" , dragarea);
			}
	}
	
	// 渲染维度树节点
	// @param flag 是否重新分析公共维度 true/false
	function initDimTreeNodes(flag){
		if(!dimTreeObj){
			return ;
		}
		if(flag === true){
			// 重新分析公共维度
			allDimArray = Design.getCommonDimsByAjax();
		}else if(designInfo4Upt != null
				&& allDimArray == ""){
			allDimArray = designInfo4Upt.allDimArray == null ? "" : designInfo4Upt.allDimArray;
		}
		var nodes = [];
		var allDimNoArray = [];
		// 分析树节点信息
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
				text : "数据单位",
				id : "tempUnit",
				icon : unitTreeIcon,
				upId : '0',
				params : {
					dimTypeStruct : "01",
					dimType : "tempUnit"
				}
		};
		// 移除旧节点
		dimTreeObj.removeChildNodes(dimTreeObj.getNodeByParam("id" , '0' , null));
		dimTreeObj.removeNode(dimTreeObj.getNodeByParam("id" , 'tempUnit' , null));
		// 渲染新节点
		dimTreeObj.addNodes(dimTreeObj.getNodeByParam("id", '0', null) , nodes , true);
		dimTreeObj.addNodes(null , unit , true);
		dimTreeObj.expandAll(true);
	}
	
	// 渲染ajax树节点
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
					// 移除旧节点
					moduleTreeObj.removeChildNodes(moduleTreeObj.getNodeByParam("id" , '0' , null));
					moduleTreeObj.removeNode(moduleTreeObj.getNodeByParam("id", '0', null) , false);
					// 渲染新节点
					moduleTreeObj.addNodes(moduleTreeObj.getNodeByParam("id", '0', null) , result , true);
				}
				treeInitFlag = false;
			},
			error:function(){
				treeInitFlag = false;
				BIONE.tip("加载失败，请联系系统管理员");
			}
		});
	}
	
	// 初始化设计器
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
			// 修改时
			if(designInfo4Upt != null){
				objTmp = designInfo4Upt;
				if( objTmp.filterContent){
					glofilters = objTmp.filterContent;
				}
				templateId = objTmp.tmpInfo.id.templateId;
				jsonStr = objTmp.tmpInfo.templateContentjson;
			}
			// 初始化设计器
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
					isBusiLine : false, // 平台报表没有子业务条线 
					// 报表指标数据初始化
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
			// 单元格明细
			initDetailForm();
			initToolBar();
		})
	}
	
	//添加拖拽控制
 	function setDragDrop(dom , receiveDom){
 		if(typeof dom == "undefined"
 			|| dom == null){
 			return ;
 		}
 		$(dom).ligerDrag({
 			proxy : function(target , g , e){
 				var treeAId = target.current.target.attr("id");
 				var strsTmp = treeAId.split("_");
 				var defaultName = "字段  ";
 				if(strsTmp[0] == "cellTree2"){
 					defaultName = "维度";
 				}
 				else{
 	 				if(templateType == "02"){
 	 					defaultName = "指标  ";
 	 				}else if(templateType == "04"){
 	 					defaultName = "列 ";
 	 				}else if(templateType == "05"){
 	 					defaultName = "行 ";
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
				// 获取拖拽树节点信息
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
						|| templateType == "04" || templateType == "05" || templateType == "06" ){// 单元格报表、明细报表和指标列表，拖拽树需要视具体情况判断
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
						// 由于silverlight未提供相应接口，hitTest是反编译扩展的方法，调用方式很恶心
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
							// 明细报表
							moduleNodeDrop(currRow , currCol , draggingNode , sheet);
							
							//initDropQueryDims(draggingNode , "01");
							// 拖拽来源于维度树
							if($(obj).hasClass("queryContent")){
								// 拖拽到【查询维度】区域
								templateDsId = Design.getCurrDsId();
								if(templateDsId
										&& templateDsId != ""
										&& templateDsId != draggingNode.data.setId){
									BIONE.tip("请选择相同的数据模型字段");
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
									showClose : true // 明细类查询条件均可以删除
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
							// 单元格报表
							if(draggingTreeId == "cellTree1" || draggingTreeId == "storeTree" ){
								indexNodeDrop(currRow , currCol , draggingNode , sheet);
								//Design.autoSetColumnWidth(sheet,currCol);
								// 刷新查询条件
								initDropQueryDims(draggingNode , "02");
								// 刷新公共维度
								initDimTreeNodes(true);
								
							}
							else {
								// 拖拽来源于维度树
								if($(obj).hasClass("queryContent")){
									// 拖拽到【查询维度】区域
									var currDimDom = $(".query-dim-item[dimno='"+draggingNode.id+"']");
									if(currDimDom.length <= 0){
										var id = uuid.v1();
										// 该维度暂时未被设置为查询维度
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
							// 综合报表
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
							// 指标列表
							if(draggingTreeId == "colTree1" || draggingTreeId == "storeTree" ){
								var returnFlag = colIdxNodeDrop(currRow , currCol , draggingNode , sheet);
								if(!(returnFlag === false)){
									Design.autoSetColumnWidth(sheet,currCol);
									// 刷新查询条件
									initDropQueryDims(draggingNode , "02");
									// 刷新公共维度
									initDimTreeNodes(true);
									// 清除由于公共维度发生变化导致的旧列表维度无效数据
									Design.clearDimCells(allDims ? allDims.split(",") : [] , selectionIdx , $("#rightCellForm [name='cellNo']").val());
								}
								
							} else {
								// 拖拽来源于维度树
								if($(obj).hasClass("queryContent")){
									// 拖拽到【查询维度】区域
									var currDimDom = $(".query-dim-item[dimno='"+draggingNode.id+"']");
									if(currDimDom.length <= 0){
										// 该维度暂时未被设置为查询维度
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
									// 拖拽到【单元格】区域
									var hasOverrideIdx = colDimNodeDrop(currRow , currCol , draggingNode , sheet);
									if(hasOverrideIdx !="")
										Design.autoSetColumnWidth(sheet,currCol);
									if(hasOverrideIdx === true){
										// 刷新公共维度
										initDimTreeNodes(true);
										// 清除由于公共维度发生变化导致的旧列表维度无效数据
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
	
	
	// 数据集明细类，拖拽drop处理
	function moduleNodeDrop(row , col , treeNode , sheet){
		if(row != null
				&& col != null
				&& treeNode != null
				&& typeof treeNode != "undefined"){
			var dsNode = treeNode.getParentNode();
			if(dsNode == null){
				return ;
			}
			// 判断drop行是否是当前列表数据行
			var currDataRow = Design.getCurrDataRow();
			if(currDataRow != null
					&& currDataRow != row){
				// 当前drop行和列表数据行不一致
				BIONE.tip("请保持数据在同一行上。。");
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
					// 目前数据集明细报表只支持单数据集
					if(currSelDsNm
							&& currSelDsNm != ""){
						BIONE.tip("只能选择数据模型[<font color='red'>"+currSelDsNm+"</font>]字段");
					}else{
						BIONE.tip("请选择相同的数据模型字段");
					}
					return ;
				}
			}
			if(RptIdxInfo
					&& typeof RptIdxInfo == "object"){
				var rptIdx = RptIdxInfo.newInstance("02"); // 数据模型报表指标
				if(rptIdx != null){				
					rptIdx.dsId = dsNode.params.realId;
					rptIdx.dsName = dsNode.text;
					rptIdx.columnId = treeNode.params.realId;
					rptIdx.columnName = treeNode.text;
// 					rptIdx.cellNm = Utils.initAreaPosiLabel(row , col);
					templateDsId = dsNode.params.realId;
				}
				// 先判断当前target是否已经是报表指标，判断逻辑是，Design.rptIdxs[cell.style.seq]是否有记录
				// 若已是报表指标，删除旧指标，维护新指标信息 ； else  直接维护报表指标信息
				// [warning]: 报表指标被记录在Design.rptIdxs数组中，key是客户端内存中的一个随机生成唯一标识；
				//    cell与报表指标关系，全部通过唯一序列值(seq)被定义在cell的styleproperties中的自定义seq属性上。
				//    这么一来，可以最大限度的把报表指标和行、列松耦合，页面操作会简单很多。
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
	
	// 指标单元格类，拖拽drop处理
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
				var rptIdx = RptIdxInfo.newInstance("03" , treeNode.data); // 指标类型报表指标
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
				// 先判断当前target是否已经是报表指标，判断逻辑是，Design.rptIdxs[cell.style.seq]是否有记录
				// 若已是报表指标，删除旧指标，维护新指标信息 ； else  直接维护报表指标信息
				// [warning]: 报表指标被记录在Design.rptIdxs数组中，key是客户端内存中的一个随机生成唯一标识；
				//    cell与报表指标关系，全部通过唯一序列值(seq)被定义在cell的styleproperties中的自定义seq属性上。
				//    这么一来，可以最大限度的把报表指标和行、列松耦合，页面操作会简单很多。
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
	
	// 列表指标单元格类，拖拽drop处理
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
			// 判断drop行是否是当前列表数据行
			var currDataRow = Design.getCurrDataRow();
			var currDataCol = Design.getCurrDataCol();
			var currIdxCol = Design.getCurrIdxCol();
			if(templateType == "04" && currDataRow != null
					&& currDataRow != row){
				// 当前drop行和列表数据行不一致
				BIONE.tip("请保持列表数据在同一行上。。");
				return false;
			}
			if(templateType == "05" && currDataCol != null
					&& currDataCol != col){
				// 判断drop列是否是当前列表数据列
				BIONE.tip("请保持列表数据在同一列上。。");
				return false;
			}
			if(templateType == "06" && currIdxCol != null
					&& !(currIdxCol.c == col && currIdxCol.r == row)){
				// 判断drop列是否是当前列表数据列
				BIONE.tip("交叉列表只允许存在一个指标。。");
				return false;
			}
			if(RptIdxInfo
					&& typeof RptIdxInfo == "object"){
				var rptIdx = RptIdxInfo.newInstance("07" , treeNode.data); // 指标类型报表指标
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
				// 先判断当前target是否已经是报表指标，判断逻辑是，Design.rptIdxs[cell.style.seq]是否有记录
				// 若已是报表指标，删除旧指标，维护新指标信息 ； else  直接维护报表指标信息
				// [warning]: 报表指标被记录在Design.rptIdxs数组中，key是客户端内存中的一个随机生成唯一标识；
				//    cell与报表指标关系，全部通过唯一序列值(seq)被定义在cell的styleproperties中的自定义seq属性上。
				//    这么一来，可以最大限度的把报表指标和行、列松耦合，页面操作会简单很多。
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
	
	// 列表维度单元格类，拖拽drop处理类
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
			// 判断drop行是否是当前列表数据行
			var currDataRow = Design.getCurrDataRow();
			var currDataCol = Design.getCurrDataCol();
			if(templateType == "04" && currDataRow != null
					&& currDataRow != row){
				// 当前drop行和列表数据行不一致
				BIONE.tip("请保持列表数据在同一行上。。");
				return "";
			}
			// 判断drop列是否是当前列表数据列
			if(templateType == "05" && currDataCol != null
					&& currDataCol != col){
				// 当前drop行和列表数据行不一致
				BIONE.tip("请保持列表数据在同一列上。。");
				return "";
			}
			if(templateType == "06" && currDataCol != null
					&& currDataCol != col && currDataRow != null
					&& currDataRow != row){
				// 当前drop行和列表数据行不一致
				BIONE.tip("请保持列表数据与指标在在同一行列上。。");
				return "";
			}
			if(templateType == "06" && currDataCol != null
					&& currDataCol == col && currDataRow != null
					&& currDataRow == row){
				BIONE.tip("请不要覆盖指标单元格。。");
				return "";
			}
			if(RptIdxInfo
					&& typeof RptIdxInfo == "object"){
				var rptIdx = RptIdxInfo.newInstance("08"); // 维度类型报表指标
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
				// 先判断当前target是否已经是报表指标，判断逻辑是，Design.rptIdxs[cell.style.seq]是否有记录
				// 若已是报表指标，删除旧指标，维护新指标信息 ； else  直接维护报表指标信息
				// [warning]: 报表指标被记录在Design.rptIdxs数组中，key是客户端内存中的一个随机生成唯一标识；
				//    cell与报表指标关系，全部通过唯一序列值(seq)被定义在cell的styleproperties中的自定义seq属性上。
				//    这么一来，可以最大限度的把报表指标和行、列松耦合，页面操作会简单很多。
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
	
	// 初始化右侧单元格信息form
	function initCellForm(){
		// 初始化ko数据绑定
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
		// 初始化ko数据绑定
		if(ko  != null
				&& typeof ko == "object"){
			ko.applyBindings(selectionToolBar , $(".toolbar-awesome")[0]);
		}
	}
	
	//spread移入单元格事件扩展
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
				// 若是报表指标
				var rptIdxTmp = Design.rptIdxs[seq];
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxTmp);
				selectionIdx.seq(seq);
				if(selectionIdx.cellType() == "04"){
					// excel公式单元格
					selectionIdx.excelFormula(cellTmp.formula());
				}
				// 维护单元格编号
				if(Utils){					
					cellNo=Utils.initAreaPosiLabel(row , col);
// 					if(selectionIdx.cellNm() == ""){
// 						selectionIdx.cellNm(Utils.initAreaPosiLabel(row , col));
// 					}
				}
			}else if(isFormal === true){
				// 将该单元格定义成为公式报表指标
				var uuid = Design.Uuid.v1();
				Design._setStyleProperty(row, col, "seq", uuid)
				var rptIdxInfoTmp = Design.RptIdxInfo.newInstance("04");
				rptIdxInfoTmp.excelFormula = cellTmp.formula();
				rptIdxInfoTmp.realIndexNo = Uuid.v1().replace(/-/g, '');
				Design.rptIdxs[uuid] = rptIdxInfoTmp;
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxInfoTmp);
				selectionIdx.seq(seq);
				selectionIdx.excelFormula(cellTmp.formula());
				// 维护单元格编号
				if(Utils){		
					var cellNo=Utils.initAreaPosiLabel(row , col);
// 					if(selectionIdx.cellNm() == ""){
// 						selectionIdx.cellNm(Utils.initAreaPosiLabel(row , col));
// 					}
				}
			}else if(seq != null
					&& Design.commonCells[seq] != null
					&& typeof Design.commonCells[seq] != "undefined"){
				// 若是报表指标
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
	
	// spread选择区域发生变化事件扩展
	function spreadSelectionChanged(sender , args){
		var currSheet = Design.spread.getActiveSheet();
		var selections = currSheet.getSelections();
		if(selections.length == 1){
			var currSelection = selections[0];
			if(currSelection.colCount == 1
					&& currSelection.rowCount == 1){
				// 只有一个单元格，不处理（一个单元格的处理交由enterCell事件）
				spreadEnterCell(currSelection.row , currSelection.col);
				return ;
			} else {
				var spans = Design.getSelectionSpans(currSheet);
				if(spans
						&& typeof spans.length != "undefined"
						&& spans.length == 1
						&& spans[0].rowCount == currSelection.rowCount
						&& spans[0].colCount == currSelection.colCount){
					// 选中了多行多列，但是一个合并的单元格
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
	
	// spread移出单元格事件扩展
	function spreadLeaveCell(sender , args){
		syncSelIdxs();
	}
	
	// 双击单元格事件
	function spreadDbclkCell(sender , args){
		if(args){
			if(args.sheetArea
					&& args.sheetArea != 3){
				// 只有选中单元格区域才进行后续动作
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
				// 指标单元格,来源指标为空(空指标)
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
					// excel公式单元格
					selectionIdx.excelFormula(currCell.formula());
					return ;
				}
				// 处理报表指标的双击事件，若是excel公式，不处理
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
	
	// 指标列表时，指标类报表指标发生变化事件handler
	function idxsChanged(){
		initDimTreeNodes(true);
	}
	
	// 同步选中节点信息到idxs缓存
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
				// 若之前是批量操作
				var batchObjTmp = {};
				attrLoop : for(var attr in selectionIdx){
					var reg = /^_.*Batch$/;
					if(!reg.test(attr)
							|| selectionIdx[attr]() === false){
						continue attrLoop;
					}
					var _attr = attr.substring(1 , attr.length - 5);
					if(typeof selectionIdx[_attr] == "function"){
						// 若是批量设置特殊属性，不处理
						if(jQuery.inArray(_attr , Design.RptIdxInfo._batchSpeVals) != -1){
							continue attrLoop;
						}
						// 若批量设置中是default值，不予处理
						// *default值判断：1.==RptIdxInfo._defaultVals中的值；2.else，为""，空字符串
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
				//针对报表指标格，进行数据修改
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
				// 若之前是批量操作
				var batchObjTmp = {};
				attrLoop : for(var attr in selectionIdx){
					var reg = /^_.*Batch$/;
					if(!reg.test(attr)
							|| selectionIdx[attr]() === false){
						continue attrLoop;
					}
					var _attr = attr.substring(1 , attr.length - 5);
					
					if(typeof selectionIdx[_attr] == "function"){
						// 若是批量设置特殊属性，不处理
						if(jQuery.inArray(_attr , Design.RptIdxInfo._batchSpeVals) != -1){
							continue attrLoop;
						}
						// 若批量设置中是default值，不予处理
						// *default值判断：1.==RptIdxInfo._defaultVals中的值；2.else，为""，空字符串
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
	
	// 上传表样回调函数
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
	
	// 当前选中单元格设置回调函数
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
				// 原先是其他类型的报表指标
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
			// 若当前选中单元格是selCell，同步两者数据
			if($("#rightCellForm [name='cellNo']").val() == currLabel){				
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxTmp);
				$("#rightCellForm [name='cellNo']").val(currLabel);
				syncSelIdxs();
			}
			// 判断是否需要重置查询条件
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
					// 若该单元格是表达式单元格
					Design.rptIdxs[seqTmp].expression = val;
					jQuery.extend(rptIdxTmp , Design.rptIdxs[seqTmp]);
				} else {
					// 原先是其他类型的报表指标
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
			// 若当前选中单元格是selCell，同步两者数据
			if($("#rightCellForm [name='cellNo']").val() == currLabel){				
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxTmp);
				$("#rightCellForm [name='cellNo']").val(currLabel);
			}
			break;
		}
	}
	
	// 获取维度交集
	// 若报表维度发生变化，重置查询条件
	function unionQueryDims(dims){
		if(!dims){
			return queryDims;
		}
		var dimsArr = dims.split(",");
		var queryDimsArr = queryDims.split(",");
		f1 : for(var i = 0 , l = queryDimsArr.length ; i < l ; i++){
			if(jQuery.inArray(queryDimsArr[i] , dimsArr) == -1){
				// 重置查询条件
				setQueryDimState(defaultQueryDims , true);
				break f1;
			}
		}
	}
	
	// 准备待保存数据
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
					publicDim : "",   // 报表维度
					queryDim : null,  // 查询维度
					detailArr : null, // 查询项明细(明细报表)
					paramJson : null,   // 参数模板内容
					storeIdxNos : storeIdxNos,//收藏指标列表
					paramId : paramTemplateId ? paramTemplateId : ""
			};
			// 分析已定义报表指标
			// 1.触发一次当前单元格leaveCell事件
			// 2.执行一次同步方法
			// 3.若Design.rptIdxs为空，表示没定义报表指标
			// 4.若Design.rptIdxs不为空，判断这些seq是否对应有具体的cell
			// 		若有，维护相应rptIdx中的行，列
			//		若无，不算合法的rptIdx，不解析
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
				// 此处其实是一个为了防止报表无任何数据导致自动生成的json缺失dataTable信息的一个解决方法。
				// 默认把第一个无数据无公式的表置入一个空值，这样就算表单没数，也会生成dataTable属性。此处应有掌声 = 。=
				firstCell.value("");
			}
			if(Design.rptIdxsSize() > 0
					|| Design.rowColsSize() > 0 || Design.commonCells.length > 0){
				// 获取全部cell，挨个分析是否有seq属性
				var rowCount = currSheet.getRowCount();
				var colCount = currSheet.getColumnCount();
				var cellOldVals = [];
				for(var r = 0 ; r <= rowCount ; r++){
					for(var c = 0 ; c <= colCount ; c++){
						if(r == 0
								&& c != 0){
							// 列对象
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
							// 行对象
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
						// 一般单元格对象
						var cellTmp = currSheet.getCell( r , c);
						var seqTmp = Design._getStyleProperty(r, c, "seq");
						if(seqTmp != null
								&& seqTmp != ""
								&& typeof seqTmp != "undefined"
								&& Design.rptIdxs[seqTmp] != null
								&& typeof Design.rptIdxs[seqTmp] == "object"){
							if(Design.rptIdxs[seqTmp].cellType == Design.Constants.CELL_TYPE_COMMON){
								// 一般单元格，不保存
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
									// 一般单元格则保存
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
							// 若是excel公式，但由于某些原因保存时并没有被解析成报表指标，自动生成报表指标
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
		saveObj.queryDim = queryDim4Save.dimNoArr ? queryDim4Save.dimNoArr.join(",") : defaultQueryDims,  // 查询维度
		saveObj.detailArr =	queryDim4Save.detailObjArr ? queryDim4Save.detailObjArr : [], // 查询项明细(明细报表)
		saveObj.paramJson = queryDim4Save.queryTmpArr ? JSON2.stringify(queryDim4Save.queryTmpArr) : (templateType=="01" ? "" : defaultParamJson),   // 参数模板内容
		saveObj.paramId = paramTemplateId ? paramTemplateId : ""
		return saveObj;
	}
	
	// 设置空指标
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
		emptyInfo.dbClkUrl = ""; // 空指标，不允许和普通指标一样进行双击设置
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
	
	// 单元格名称变化事件处理
	function cellNmChangeHandler(){
		if(Design.Constants.SHOW_TYPE_CELLNM == Design.getShowType()){
			// 若是单元格名称展示，需要同步label
			var newVal = $("input[name=cellNm]").val();
			var currCellNo = $("input[name=cellNo]").val(); 
			var rowCol = Design.Utils.posiToRowCol(currCellNo);
			var row = rowCol.row;
			var col = rowCol.col;
			var currSheet = Design.spread.getActiveSheet();
			currSheet.getCell(row , col).value("{"+newVal+"}");
		}
	}
	
	// 初始化报表查询条件
	// @params dropType   01-数据模型 ， 02-指标
	function initDropQueryDims(node , dropType){
		if(!node
				&& designInfo4Upt != null){
			// 修改时的参数模板信息初始化
			var objTmp = designInfo4Upt;
			paramTemplateId = objTmp.paramTemplateId;
			allDims = objTmp.publicDim;
			queryDims = objTmp.queryDim;
			detailObjs = objTmp.detailObjs;
			if(templateType == "01"){
				// 明细类
				setQueryModuleState(detailObjs);
			}else{
				setQueryDimState(queryDims);
			}
			return ;
		}
		if(!queryDims){
			// 若暂不存在查询维度，使用缺省维度（机构、日期、币种）
			setQueryDimState(defaultQueryDims);
			return ;
		}
		// 若已存在查询维度
		// -- 分析当前拖拽元素对应的维度，
		// 			若当前查询维度包含在拖拽元素维度内，使用当前查询维度
		//			若当前查询维度不是完全包含在拖拽元素维度内，使用缺省维度（机构、日期、币种）
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
					// 重置查询条件到缺省状态（重置状态需要清空当前的参数模板配置）
					setQueryDimState(defaultQueryDims , true);
				}
			}
		})
	}
	
	// 初始化查询维度部分dom
	function initQueryDimsDom(dimArrayStr){
		var dimArray = dimArrayStr == null ? [] : dimArrayStr.split(",");
		// 先清空之前的显示
		$("#queryDimsDiv").children(".queryContent").empty();
		// 追加显示
		if(dimArray
				&& typeof dimArray.length != "undefined"){
			// 获取维度对应的信息
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
								dimTypeNmTmp = "数据单位";
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
	
	// 初始化查询维度部分dom
	function initQueryModulesDom(detailObjs){
		// 先清空之前的显示
		$("#queryDimsDiv").children(".queryContent").empty();
		// 追加显示
		if(detailObjs
				&& typeof detailObjs.length != "undefined"
				&& detailObjs.length > 0 && designInfo4Upt.paramJson){
			templateDsId = detailObjs[0].dsId;
			//由于查询参数所需要的类型，是否必填，日期范围等参数需要从参数模板中获取，查询模板的信息放在designInfo4Upt.paramJson
			//相关联的信息是orderNum的顺序
			//相同的模板中，在表rpt_paramtmp_attrs和rpt_design_query_detail 的顺序一致
			//designInfo4Upt.paramJson的顺序必须要根据orderNum进行排序
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
					showClose : true // 明细类查询条件均可以删除
				});
				$("#queryDimsDiv").children(".queryContent").append(html);
				$('.query-module-dropdown').dropdown();
			});
		}
	}
	
	// 统一设置查询条件相关
	// @params dimArgs : 维度类型标识集合字符串，逗号分隔
	// @params clearParamFlag : 是否清空参数模板对象信息
	function setQueryDimState(dimArgs , clearParamFlag){
		if(!dimArgs
				|| dimArgs == ""){
			dimArgs = defaultQueryDims;
		}
		if(!dimArgs
				|| dimArgs == ""){
			// 任然为空
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
	
	// 统一设置明细报表查询条件相关
	// @params detailObjs : 明细对象,对应java端RptDesignQueryDetailVO.java
	function setQueryModuleState(detailObjs){
		initQueryModulesDom(detailObjs);
		if(typeof queryDimsObj.querySize == "function"
				&& detailObjs
				&& typeof detailObjs.length != "undefined"){
			queryDimsObj.querySize(detailObjs.length);
		}
	}
	
	// 获取查询条件基本信息
	function getQueryDimsInfo(){
		var returnObj = [];
		if(Design
				&& Design.ParamComp
				&& Design.Constants){
			if(templateType == Design.Constants.TEMPLATE_TYPE_MODULE){
				// 按明细报表字段解析
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
					// 按查询维度解析
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
	
	// 准备查询维度信息
	// @return {
    //	dimNoArr : Array 维度标识查询数组		
	//	detailObjArr : Array  列名查询数组
	//  queryTmpArr : Array 待保存模板数组  
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
				// 按明细报表字段解析
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
					
					// 明细对象
					returnObj.detailObjArr.push({
						orderNum : i,
						dsId : templateDsId,
						columnId : colId,
						elementType : elementType,
						dimTypeNo : dimTypeNo
					});
					var objTmp = Design.ParamComp.generateModuleTmp(moduleObj);
					if(objTmp != null){
						// 参数模板
						returnObj.queryTmpArr.push(objTmp);
					}
				}
			}else{
				var queryDimDoms = $(".query-dim-item");
				for(var i = 0 , l = queryDimDoms.length ; i < l ; i++){
					// 按查询维度解析
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
	
	//@param exit : true -> 关闭窗口；false -> 不关闭窗口 ; undefined or else : 弹出confirm窗口按提示操作
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
				baseWindow.BIONE.showLoading("正在保存，请稍后..");
			},
			success : function(result){
				BIONE.tip("维护成功");
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
					// 保存但不退出时，缓存已保存的信息
					noExitHandler(result);
				}else {
					$.ligerDialog.confirm('保存成功，退出维护窗口？', function(yes) {
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
				BIONE.tip("维护失败，请联系系统管理员");
			}
		});
		return returnObj; 
	}
	
	function beforeCloseFunc(data) {
		if(data){
			// 刷新树
			var topWindow = window.top.$contentWindow;
			topWindow.searchHandler();
			if(topWindow.clickNodeType == topWindow.rptNodeType){
				// 若是报表，刷新tab的label
				if(data.rptNm){
					topWindow.mainTab.setHeader("topTab","[报表]"+data.rptNm);
				}
			}
			// 刷新列表
			topWindow.document.getElementById("gridFrame").contentWindow.grid.reload();
		}
	}
	
	// 保存前校验
	// @return obj{ validateFlag : 校验成功标识 ; msg : 校验信息}
	function rpt_validate(){
		if(Design
				&& Design.rptIdxsSize() > 0
				&& (templateType == Design.Constants.TEMPLATE_TYPE_IDXCOL_V 
						|| templateType == Design.Constants.TEMPLATE_TYPE_IDXCOL_H )){
			// 指标列表,若有相同的维度列,显示级别必须遵从 从左至右,从高(1级)到低(n级) 的规则
			var currSheet = Design.spread.getActiveSheet();
			var rowCount = currSheet.getRowCount();
			var colCount = currSheet.getColumnCount();
			var dimCellMap = {}; // 维度类型标识 - 显示级别
			var dimDownMap = {}; // 维度类型标识 - 显示下级
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
												msg : "维度单元格[<font color='red'>"+Utils.initAreaPosiLabel(r , c)+"</font>]    <font color='red'>显示级别</font>    不能低于之前的同类维度级别"
											};
										}
									}catch(e){
										currSheet.setSelection(r , c , 1 , 1);
										return {
											validateFlag : false,
											msg : "维度单元格[<font color='red'>"+Utils.initAreaPosiLabel(r , c)+"</font>]    <font color='red'>显示级别</font>    定义不合法"
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
										msg : "维度单元格[<font color='red'>"+Utils.initAreaPosiLabel(r , c)+"</font>]    <font color='red'>显示级别</font>    无法定义多个下级级别显示的维度"
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
					msg : "指标列表必须至少配置一个维度列"
				};
			}
		} 
		else if(templateType === Design.Constants.TEMPLATE_TYPE_MODULE){
			// 明细报表,校验是否设置了查询条件
			var queryModuleDoms = $(".query-module-item");
			if(!queryModuleDoms
					|| typeof queryModuleDoms.length == "undefined"
					|| queryModuleDoms.length <= 0){
				if($("#queryDimsToggle").attr("currType") == "hide"){
					// 若当前查询条件是隐藏的，显示之
					$("#queryDimsToggle").trigger("click");
				}
				return {
					validateFlag : false,
					msg : "保存前，请设置查询条件"
				};
			}
		}
		if(Design.rptIdxsSize() <= 0){
			return {
				validateFlag : false,
				msg : "请至少配置一个指标"
			};
			
		}
		return {
			validateFlag : true,
			msg : "校验通过"
		};
	}
	
	// 展开基本信息维护dialog
	function openSaveDialog(){
		$.ligerDialog.open({
			id : 'baseEdit',
			title : '报表保存',
			url: '${ctx}/report/frame/design/cfg/baseEdit?defSrc='+defSrc+'&templateType='+templateType+"&catalogId="+catalogId+"&rptId="+rptId+"&verId="+verId+"&templateId="+templateId,
			width: 600, 
			height: 450, 
			modal: true, 
			isResize: false,
			allowClose : true,
			showMin : false
		});
	}
	
	// 保存
	function rpt_save() {
		Design.leaveCurrCell();
		var validateObj = rpt_validate();
		if(validateObj.validateFlag === false){
			// 未通过校验
			if(typeof validateObj.msg != "undefined"
					&& validateObj.msg != ""){
				BIONE.tip(validateObj.msg);
			}
			return ;
		}
		if(rptId
				&& rptId != ""){
			// 修改时保存
			var design4Save = prepareDatas4Save();
			saveHandler(baseInfo4Upt , design4Save);
		}else{
			// 新增时保存
			openSaveDialog();
		}
	}
	
	// 导入样式
	function css_import(){
		BIONE.commonOpenDialog('样式上传','requireuploadWin',600,330,'${ctx}/report/frame/design/cfg/cssupload?d='+new Date().getTime());
	}
	
	// 显示级别点击方法
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
					<div position="center" title="公共维度">
						<div class="cellTreeContainer" id="cellTreeContainer2"
							style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
							<ul id="cellTree2"
								style="font-size: 12; background-color: #FFFFFF; width: 92%"
								class="ztree">
							</ul>
						</div>
					</div>
					<div position="centerbottom" title="指标">
						<div id="idxtab" style="width: 100%; overflow: hidden;">
							<div tabid="common" title="通用指标" lselected="true">
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
										<span>目录</span><input type="radio" id="idxcatalog" name="idxshowtype"
											value="idxcatalog" style="width: 20px;" onclick="idxshowtype()"
											checked="true" /> <span>标签</span> <input type="radio" id="idxlabel"
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
							<div tabid="store" title="收藏指标" lselected="true">
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
					<div position="center" title="公共维度">
						<div class="colTreeContainer" id="colTreeContainer2"
							style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
							<ul id="colTree2"
								style="font-size: 12; background-color: #FFFFFF; width: 92%"
								class="ztree">
							</ul>
						</div>
					</div>
					<div position="centerbottom" title="指标">
						<div id="idxColtab" style="width: 100%; overflow: hidden;">
							<div tabid="common" title="通用指标" lselected="true">
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
										<span>目录</span><input type="radio" id="colcatalog" name="colshowtype"
											value="colcatalog" style="width: 20px;" onclick="colshowtype()"
											checked="true" /> <span>标签</span> <input type="radio" id="collabel"
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
							<div tabid="store" title="收藏指标" lselected="true">
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
									<span style="font-size: 12" class="queryDimTitle">查询条件：[&nbsp;<font
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
			<div id="rightDiv" position="right" title="单元格信息">
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