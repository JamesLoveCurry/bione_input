<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<%@ include file="/common/spreadjs_load.jsp"%>
<style >
	.noSelectText{
		-moz-user-select:none;
	}
	.haveBorder{
		border: 1px solid #999;
	}
</style>
<script type="text/javascript" src="${ctx}/plugin/js/idx/cursorPosition.js"></script>
<script type="text/javascript">
	//表间计算单元格类型
	var cellType = "05";
	// 当前选中的cell
	var currSheet = window.parent.Design.spread.getActiveSheet();
	var selRow = currSheet.getActiveRowIndex();
	var selCol = currSheet.getActiveColumnIndex();
	var spans = window.parent.Design.getSelectionSpans(currSheet);
	if(spans 
			&& typeof spans.length != "undefined"
			&& spans.length > 0){
		selRow = spans[0].row;
		selCol = spans[0].col;
	}
	var leftTreeObj;
	var dimMap = [];
	var flag = true;
	var currentNode;
	var designInfo4Upt;
	var position = {start : 0, end : 0};
	$(function(){
		initTree();
		addFunc();
		addSymbol();
		addEvent();
		addTipPic();
		addFormButton();
		initForm();
	});
	function savePos(textBox){
		position = cursorPosition.get(textBox);
	}
	function addFormButton(){
		var btns = [ {
			text : "返回",
			onclick : function() {
				BIONE.closeDialog("moduleClkDialog");
			}
		}, {
			text : "保存",
			onclick : function(){
				f_save();
			}
		}];
		BIONE.addFormButtons(btns);
	}
	function addEvent(){
		$(".l-treetoolbar-item").live("mouseover", function(){
			$("#tip").html(this.attributes[1].nodeValue);
			addTipPic();
		});
		$(".l-treetoolbar-item").live("click", function(){
			var val =  this.getElementsByTagName("span")[0].innerHTML;
			val = val.replace("&gt;", ">");
			val = val.replace("&lt;", "<");
			var reg = new RegExp("amp;", "g");
			val = val.replace(reg, "");
			
			cursorPosition.add(document.getElementById("expression"), position, val);
			position.start += val.length;
			position.end += val.length;
		});
		
		$("#expression").live("mouseover", function(){
			$("#tip").html("表达式");
			addTipPic();
		});
		$("#rptTree").live("mouseover", function(){
			$("#tip").html("报表树");
			addTipPic();
		});
		$("#bottom_right").live("mouseover", function(){
			$("#tip").html("报表模板");
			addTipPic();
		});
		
	}
	function initForm(){
		//修改时初始化表单
		if(currSheet){
			var currVal = "";
			var seq = window.parent.spread.getActiveSheet().getTag(selRow, selCol, window.parent.GC.Spread.Sheets.SheetArea.viewport);
			if(window.parent.Design.rptIdxs
					&& seq != null
					&& typeof seq != "undefined"){
				var rptIdxTmp = window.parent.Design.rptIdxs[seq];
				if(rptIdxTmp
						&& rptIdxTmp.cellType == "05"){
					currVal = rptIdxTmp.formulaDesc;
				}
			}
			if(currVal != null
					&& currVal != ""){
				$("#expression").val(currVal);
				position.start = currVal.length;
				position.end = currVal.length;
			}
		}
	}
	function f_save(){
		if ($("#expression").val() == "") {
			BIONE.tip("表达式不能为空，请修改!");
			return;
		}
		var expression = "";
		var formulaDims = "";
		var idxNos = "";
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/valid/logic/replaceExpression",
			dataType : 'json',
			type : "POST",
			data : {
				"expression" : $("#expression").val()
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("合法性校验中，请稍候..");
			},
			success : function(data) {
				if (typeof BIONE != 'undefined') {
					BIONE.loading = false;
					BIONE.hideLoading();
				}
				if (!data.message) {
					expression = data.expression;
					var newDimMap = [];
					for(var i=0;i< data.formulaDims.length;i++){
						if(formulaDims != ""){
							formulaDims += ","
						}
						formulaDims += data.formulaDims[i].dimTypeNo;
						newDimMap[data.formulaDims[i].dimTypeNo] = data.formulaDims[i].dimTypeNo;
					}
					dimMap = newDimMap;
					idxNos = data.formulaIndex;
				} else {
					BIONE.tip(data.message);
					expression = "";
					return ;
				}
				if(expression != null
						&& expression != ""
						&& formulaDims != null
						&& formulaDims != ""
						&& idxNos != null
						&& idxNos != ""){			
					var returnObj = {
						formulaContent : expression,
						formulaDesc : $("#expression").val(),
						formulaDims : formulaDims,
						indexNo : idxNos
					};
					if(typeof window.parent.selCellSettingHandler == "function"){						
						window.parent.selCellSettingHandler(returnObj , cellType);
					}
					BIONE.closeDialog("moduleClkDialog");
				}else{
					BIONE.tip("请至少选择一个指标！");
				}
			},
			error : function(result, b) {
				if (typeof BIONE != 'undefined') {
					BIONE.loading = false;
					BIONE.hideLoading();
				}
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	
	function addTipPic(){
		var tipIcon = "${ctx}/images/classics/icons/lightbulb.png";
		$("#tip").prepend(
				"<div style='width:24px;float:left;height:16px;background:url("
						+ tipIcon + ") no-repeat' />");
	}
	
	function initTree(){
		var setting = {
				data:{
					key:{
						name:'text'
					},
					simpleData:{
						enable:true,
						idKey: "id",
						pIdKey: "upId"
					}
				},
				callback: {
					onClick: zTreeOnClick
				}
			};
		leftTreeObj =$.fn.zTree.init($("#rptTree"), setting);
		//加载数据
		loadTree("${ctx}/report/frame/valid/logic/getRptAndTemplateTree",leftTreeObj);
	}
		function loadTree(url,component,data){
			$.ajax({
				cache : false,
				async : true,
				url : url,
				dataType : 'json',
				data:data,
				type : "post",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result) {
					var nodes = component.getNodes();
					var num = nodes.length;
					for(var i=0;i<num;i++){
						component.removeNode(nodes[0],false);
					}
					if(result.length>0){
						component.addNodes(null,result,false);
						//component.expandAll(true);	
					}
				},
				error : function(result, b) {
					//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	function zTreeOnClick(event, treeId, treeNode, clickFlag){
		if(treeNode.params.templateId){
			BIONE.loading = true;
			BIONE.showLoading("正在加载数据中...");
			currentNode = treeNode;
			$("#bottom_right").html("");
			initDatas(treeNode.params.rptId, treeNode.params.templateId);
		}
	}
	function initDatas(rptId, cfgId){
		if(rptId != null
				&& rptId != ""
				&& typeof rptId != "undefined" && cfgId){
			// 2.设计器信息
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/design/cfg/getDesignInfo",
				dataType : 'json',
				data : {
					templateId : cfgId,
					useParentJson : true
				},
				type : "post",
				success : function(result){
					if(result != null
							&& typeof result.tmpInfo != "undefined"){
						var jsonStr = result.tmpInfo.templateContentjson;
						var rptRemark = result.tmpInfo.remark;
						designInfo4Upt = result;
						initDesign();
						BIONE.loading = false;
						BIONE.hideLoading();
					}
				},
				error:function(){
					BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}
	function initDesign(){
		require.config({
			baseUrl : "${ctx}/plugin/js/",
			paths:{
				"design" : "cfg/views/rptdesign"
			}
		});
		require(["design"] , function(ds){
			var objTmp = designInfo4Upt;
			var jsonStr = "";
			// 修改时，初始化数据
			if(objTmp != null){
				jsonStr = objTmp.tmpInfo.templateContentjson;
				designInfo4Upt = null;
			}
			var settings = {
					ctx : "${ctx}" ,
					readOnly : true ,
					showType:"cellnm",
					onCellDoubleClick : spreadDbclkCell,
					cellDetail:true,
					toolbar:false,
					// 报表指标数据初始化
					moduleCells : objTmp==null ? null : objTmp.moduleCells,
					formulaCells : objTmp==null ? null : objTmp.formulaCells,
					idxCells : objTmp==null ? null : objTmp.idxCells,
					staticCells : objTmp==null ? null : objTmp.staticCells,
					idxCalcCells : objTmp==null ? null : objTmp.idxCalcCells,
					initJson : jsonStr
			};
			spread = ds.init($("#bottom_right") , settings);
		});
	}
	
	function spreadDbclkCell(sender , args , rptIdxTmp){
		if("03" == rptIdxTmp.cellType || "05" == rptIdxTmp.cellType || "04" == rptIdxTmp.cellType){
			if("04" == rptIdxTmp.cellType){
				if("Y" != rptIdxTmp.isRptIndex){
					BIONE.tip("该excel公式单元格没有设置为报表指标，请先设置！");
					return;
				}
			}

			var dims = rptIdxTmp.allDims;
			if (dims && dims != "") {
				var dimArry = dims.split(",");
				for ( var tmp in dimArry) {
					dimMap[dimArry[tmp]] = dimArry[tmp];
				}
			}
			rptIdxTmp.realIndexNm = (rptIdxTmp.cellNm)?rptIdxTmp.cellNm:rptIdxTmp.cellNo; 
			if(currentNode.params.nodeType == "template"){
				var parentNode = currentNode.getParentNode();
				cursorPosition.add(document.getElementById("expression"), position,
						"I('" + parentNode.text + "." +currentNode.text + "." + rptIdxTmp.realIndexNm + "')");
				position.start += ("I('" + parentNode.text + "." +currentNode.text + "." + rptIdxTmp.realIndexNm + "')").length;
				position.end += ("I('" + parentNode.text + "." +currentNode.text + "." + rptIdxTmp.realIndexNm + "')").length;
			}else if(currentNode.params.nodeType == "report" || currentNode.params.nodeType == "detail_report"){
				cursorPosition.add(document.getElementById("expression"), position,
						"I('" + currentNode.text + "." + rptIdxTmp.realIndexNm + "')");
				position.start += ("I('" + currentNode.text + "." + rptIdxTmp.realIndexNm + "')").length;
				position.end += ("I('" + currentNode.text + "." + rptIdxTmp.realIndexNm + "')").length;
			}
		}else{
			BIONE.tip("表间取数只能选择指标单元格或表间取数单元格！");
		}
	}
	
	function initDims() {
		var dim = [];
		var tmp = null;
		for (tmp in dimMap) {
			if (typeof (dimMap[tmp]) != "function") {
				dim.push(tmp);
			}
		}
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/idx/getDimByDimNos",
			dataType : 'json',
			data : {
				dimNos : dim.join(",")
			},
			type : "GET",
			success : function(result) {
				if(result && result.length){					
					$("#dimDiv").ligerCheckBoxList({
						rowSize : result.length,
						data : result,
						textField : 'text'
					});
					for ( var i = 0; i < result.length; i++) {
						liger.get("dimDiv")
								.setValue(
										liger.get("dimDiv").getValue() + ";"
												+ result[i].id);
						$($("#dimDiv input[type=checkbox]")[i]).attr("disabled",
								"disabled");
					}
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function addFunc(){
		BIONE.loading = true;
		BIONE.showLoading("正在加载数据中...");
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/valid/logic/getFuncAll.json",
			dataType : 'json',
			type : "post",
			success : function(result) {
				if(result && result.length){					
					var item = [];
					for ( var i = 0; i < result.length; i++) {
						item.push({
							text : result[i].formulaNm,
							id : result[i].formulaDisplay
						});
					}
					$("#function").ligerTreeToolBar({
						items : item
					});
					$(".l-treetoolbar-item").addClass("noSelectText");
					$(".l-treetoolbar-item").addClass("haveBorder");
					$(".l-panel-btn").css("padding", "auto");
					$(".l-treetoolbar-item span").css("padding-left", "0px");
					$(".l-treetoolbar-item span").css("text-align", "center");
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
		BIONE.loading = false;
		BIONE.hideLoading();
	}
	function addSymbol(){
		BIONE.loading = true;
		BIONE.showLoading("正在加载数据中...");
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/valid/logic/getSymbolAll.json",
			dataType : 'json',
			type : "post",
			success : function(result) {
				if(result && result.length){
					var item = [];
					for ( var i = 0; i < result.length; i++) {
						item.push({
							text : result[i].symbolNm,
							id : result[i].symbolDisplay
						});
					}
					$("#opersign").ligerTreeToolBar({
						items : item
					});
					$(".l-treetoolbar-item").addClass("noSelectText");
					$(".l-treetoolbar-item").addClass("haveBorder");
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
		BIONE.loading = false;
		BIONE.hideLoading();
	}
</script>
</head>
<body>
<div id="template.center">
	<div id="top_middle"
		style="width: 90%; height: 8%; margin-left: auto; margin: auto; margin-top: 2%;">
		<div id="top_middle_left">

			<div id="top_middle_left"
				style="width: 100%; height: 100%; ">
				<textarea id="expression"
					style="width: 99%; height: 80%; margin-top: 0%;float:left;resize:none" onclick="savePos(this)" onchange="savePos(this)"></textarea>
			</div>
		</div>
	</div>
	
	<div id="middle"
		style="width: 90%; height: 18px; margin-left: auto; margin-right: auto; margin-top: 1%;">
		<div id="tip"
			style="border: 1px solid #FFF; width: 99%; height: 99%;padding: 0px 2px; background: #fffee6; color: #8f5700;">tip
		</div>
	</div>
	
	<div id="middle_bottom"
		style="width: 90%;  margin-left: auto; margin-right: auto; margin-top: 1%;">
		
		<div style="width: 99%; ">
		<div style="float: left;width:10%">
			<img src="${ctx }/images/classics/icons/communication.gif"><span>运算符:</span>
		</div>
		<div id="opersign"
			style=" width: 89%; float: left; overflow: auto;height:27px" >
		</div>
		</div>
		<div style="float: left;width: 99%; margin-top: 1%;">
			<div style="float: left;width:10%">
				<img src="${ctx }/images/classics/icons/communication.gif"><span>公  式:</span>
			</div>
			<div id="function"
				style="float:left; width: 89%; overflow: auto;height:55px">
			</div>
		</div>
	</div>
	<div id="report"
		style="width: 90%; height: 60%; margin-left: auto; margin-right: auto; margin-top: 1%;">
		<div id="bottom_left"
			style="width: 30%; height: 99%; float: left; border: 1px solid #999;overflow: auto; clear: both;">
			<ul id="rptTree"
				style="font-size: 12;width: 92%"
				class="ztree"></ul>
			</div>
		<div id="bottom_right"
			style="width: 69%; height: 99%; float: left; border: 1px solid #999; margin-left: 0%;">
			</div>
	</div>
	</div>
			
</body>
</html>