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
	.l-treetoolbar {
    	background: none repeat scroll 0 0 #ffffff;
    	height: auto;
	}
	
</style>
<script type="text/javascript" src="${ctx}/plugin/js/idx/cursorPosition.js"></script>
<script type="text/javascript">

	var lPosition = {start:0, end:0};
	var rPosition = {start:0, end:0};
	var flag = true;
	var spread;
	var isInnerCheck = window.parent.validObj.isInnerCheck;//是否表内校验
	var select;
	var designInfo4Upt; //报表设计器信息
	var currentNode;
	var clickTmpId = "${templateId}";
	var templateIds = [];
	var orgType = "${orgType}";//机构类型
	
	function savePos(textBox){
		lPosition = cursorPosition.get(textBox);
	}
	function saveRPos(textBox){
		rPosition = cursorPosition.get(textBox);
	}
	
	$(function(){
		templateIds[clickTmpId] = clickTmpId;
		templateIds.push(clickTmpId);
		$("#lexpression").height($("#top_middle_middle").height());
		$("#rexpression").height($("#top_middle_middle").height());
		select = $("#oper").ligerComboBox({
			width:"80"
		});
		select.selectValue("==");
		select.setText("==");
		$("#middle_bottom").css("margin-left", $("#middle").css("margin-left"));
		initTree();
		initDatas(null, "${templateId}");
		//加载运算符
		addFunc();
		//加载公式
		addSymbol();
		addEvent();
		$("#lexpression").focus();
		$("#report").css("margin-left", $("#middle").css("margin-left"));
		$("#report").height($("#center").height() - $("#top_middle").height() - $("#middle").height() - $("#middle_bottom").height() - 6);
		$("#rexpression").attr("readonly", "true").addClass("l-text-readonly");
		$("#lexpression").removeAttr("readonly").removeClass("l-text-readonly");
		//提示
		addTipPic();
		addFormButton();
		initForm();
	});
	function addText(text){
		if(flag){
			document.getElementById("lexpression").value = document.getElementById("lexpression").value + text;
			lPosition.start += text.length;
			lPosition.end += text.length;
		}else{
			document.getElementById("rexpression").value = document.getElementById("rexpression").value + text;
			rPosition.start += text.length;
			rPosition.end += text.length;
		}
	}
	function initDatas(rptId, cfgId){
		if(cfgId != null
				&& cfgId != ""
				&& typeof cfgId != "undefined"){
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
					showType:"cellnm",
					readOnly : true ,
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
		})
	}
	//单元格双击事件
	function spreadDbclkCell(sender , args , rptIdxTmp){
		if(rptIdxTmp.realIndexNo && rptIdxTmp.realIndexNo != ""){
			rptIdxTmp.realIndexNm = (rptIdxTmp.cellNm)?rptIdxTmp.cellNm:rptIdxTmp.cellNo;
			if(flag){
				savePos(document.getElementById("lexpression"));
				if("${lineNm}" != null && "${lineNm}" != ""){
					cursorPosition.add(document.getElementById("lexpression"), lPosition, "I('"+ "${rptNm}" + "." + "${lineNm}" + "." + rptIdxTmp.realIndexNm + "')");
					lPosition.start += ("I('"+ "${rptNm}" + "." + "${lineNm}" + "." + rptIdxTmp.realIndexNm + "')").length;
					lPosition.end += ("I('"+ "${rptNm}" + "." + "${lineNm}" + "." + rptIdxTmp.realIndexNm + "')").length;
				}else{
					cursorPosition.add(document.getElementById("lexpression"), lPosition, "I('"+ "${rptNm}" + "." + rptIdxTmp.realIndexNm + "')");
					lPosition.start += ("I('"+ "${rptNm}" + "." + rptIdxTmp.realIndexNm + "')").length;
					lPosition.end += ("I('"+"${rptNm}" + "." + rptIdxTmp.realIndexNm + "')").length;
				}
				
			}else{
				saveRPos(document.getElementById("rexpression"));
				if(currentNode){
					if(currentNode.params.nodeType == "template"){
						var parentNode = currentNode.getParentNode();
						cursorPosition.add(document.getElementById("rexpression"), rPosition, "I('"+ parentNode.text + "." + currentNode.text + "." + rptIdxTmp.realIndexNm + "')");
						rPosition.start += ("I('"+ parentNode.text + "." + currentNode.text + "." + rptIdxTmp.realIndexNm + "')").length;
						rPosition.end += ("I('"+ parentNode.text + "." + currentNode.text + "." + rptIdxTmp.realIndexNm + "')").length;
					}else{
						cursorPosition.add(document.getElementById("rexpression"), rPosition, "I('"+ currentNode.text + "." + rptIdxTmp.realIndexNm + "')");
						rPosition.start += ("I('"+ currentNode.text + "." + rptIdxTmp.realIndexNm + "')").length;
						rPosition.end += ("I('"+ currentNode.text + "." + rptIdxTmp.realIndexNm + "')").length;
					}
				}else{//未选择，为当前的模板
					if("${lineNm}" != null && "${lineNm}" != ""){
						cursorPosition.add(document.getElementById("rexpression"), rPosition, "I('"+ "${rptNm}" + "." + "${lineNm}" + "." + rptIdxTmp.realIndexNm + "')");
						rPosition.start += ("I('"+ "${rptNm}" + "." + "${lineNm}" + "." + rptIdxTmp.realIndexNm + "')").length;
						rPosition.end += ("I('"+ "${rptNm}" + "." + "${lineNm}" + "." + rptIdxTmp.realIndexNm + "')").length;
					}else{
						cursorPosition.add(document.getElementById("rexpression"), rPosition, "I('"+ "${rptNm}" + "." + rptIdxTmp.realIndexNm + "')");
						rPosition.start += ("I('"+ "${rptNm}" + "." + rptIdxTmp.realIndexNm + "')").length;
						rPosition.end += ("I('"+"${rptNm}" + "." + rptIdxTmp.realIndexNm + "')").length;
					}
				}
				if(!templateIds[clickTmpId]){
					templateIds[clickTmpId] = clickTmpId;
					templateIds.push(clickTmpId);
				}
			}
		}else{
			BIONE.tip("请选择报表指标！");
		}
	}
	function addFormButton(){
		var btns = [  {
			text : "取消",
			onclick : function(){
				window.parent.parent.BIONE.closeDialog("logicAdd");
			} 
		},{
			text : "下一步",
			onclick : function() {
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
			if(val && val === "Sql()"){
				BIONE.commonOpenLargeDialog('选择数据模型', 'chooseModel', '${ctx}/report/frame/valid/logic/chooseModel');
				return;
			}
			val = val.replace("&gt;", ">");
			val = val.replace("&lt;", "<");
			var reg = new RegExp("amp;", "g");
			val = val.replace(reg, "");
			if(flag){
				cursorPosition.add(document.getElementById("lexpression"), lPosition, val);
				lPosition.start += val.length;
				lPosition.end += val.length;
			}else{
				cursorPosition.add(document.getElementById("rexpression"), rPosition, val);
				rPosition.start += val.length;
				rPosition.end += val.length;
			}
		});
		$("#lexpression").live("mouseover", function(){
			$("#tip").html("左表达式");
			addTipPic();
		});
		$("#rexpression").live("mouseover", function(){
			$("#tip").html("右表达式");
			addTipPic();
		});
		$("#oper").live("mouseover", function(){
			$("#tip").html("左表达式和右表达式的比较逻辑");
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
		$("#rexpression").bind("click", function(){
			flag = false;
			$("#lexpression").attr("readonly", "true").addClass("l-text-readonly");
			$("#rexpression").removeAttr("readonly").removeClass("l-text-readonly");
		}); 
		 $("#lexpression").bind("click", function(){
			flag = true;
			$("#rexpression").attr("readonly", "true").addClass("l-text-readonly");
			$("#lexpression").removeAttr("readonly").removeClass("l-text-readonly");
			initDatas(null, "${templateId}");
		}); 
	}
	function initForm(){
		//取模板开始时间
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/valid/warn/validStartDate?cfgId=${templateId}&d="
					+ new Date().getTime(),
			dataType : 'text',
			type : "get",
			success : function(result) {
				window.parent.verStartDate = result;
			},
			error : function(result, b) {
				if(result.status != 200){
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			}
		});
		//修改时初始化表单
		if ("${checkId}" != "" && "${checkId}" != null) {
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/valid/logic/${checkId}/${orgType}",
				dataType : 'json',
				type : "GET",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading();
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(data){
					select.selectValue(data.logicOperType);
					select.setText(data.logicOperType);
					var flag = 1;var i=1;
					if (data.expressionDesc) {
						for (i = 1; i < data.expressionDesc.length; i++) {
							if (data.expressionDesc.charAt(i) == "(") {
								flag++;
							} else if (data.expressionDesc.charAt(i) == ")") {
								flag--;
							}
							if (flag == 0) {
								break;
							}
						}
					}
					$("#lexpression").val(
							data.expressionDesc.substring(1, i));
					lPosition.start += data.expressionDesc.substring(1, i).length;
					lPosition.end += data.expressionDesc.substring(1, i).length;
					$("#lexpression").removeAttr("readonly")
							.removeClass("l-text-readonly");
					$("#rexpression").val(
							data.expressionDesc.substring(i + 2 + data.logicOperType.length,
									data.expressionDesc.length - 1));
					rPosition.start += data.expressionDesc.substring(i + 3, data.expressionDesc.length - 1).length;
					rPosition.end += data.expressionDesc.substring(i + 3, data.expressionDesc.length - 1).length;
					$("#rexpression").removeAttr("readonly")
							.removeClass("l-text-readonly");
					$("#rexpression").attr("readonly", "true")
							.addClass("l-text-readonly");
					window.parent.validObj.isPre = data.isPre;
					window.parent.validObj.isSelfDef = data.isSelfDef;
					window.parent.validObj.floatVal = data.floatVal;
					window.parent.validObj.startDate = data.startDate;
					window.parent.validObj.endDate = data.endDate;
					window.parent.validObj.busiExplain = data.busiExplain;
					window.parent.validObj.isOrgFilter=data.isOrgFilter;
					window.parent.validObj.orgLevel=data.orgLevel;
					window.parent.validObj.checkSrc=data.checkSrc;
					window.parent.validObj.checkType=data.checkType;
					window.parent.validObj.serialNumber=data.serialNumber;
					window.parent.validObj.dataUnit=data.dataUnit;
					window.parent.validObj.dataPrecision=data.dataPrecision;
					window.parent.validObj.orgNo=data.orgNo;
					window.parent.validObj.orgNm=data.orgNm;
					window.parent.validObj.logicCheckCycle=data.logicCheckCycle;
					if("02" == data.checkType){
						isInnerCheck = false;
					}
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	}
	function f_save() {
		if ($("#lexpression").val() == "") {
			BIONE.tip("左表达式不能为空，请修改!");
			return;
		}
		if ($("#rexpression").val() == "") {
			BIONE.tip("右表达式不能为空，请修改!");
			return;
		}
		if ("${checkId}" == "" || "${checkId}" == null) {
			if(templateIds.length > 1){
				isInnerCheck = false;
			}
		}
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/valid/logic/replaceExpression",
			dataType : 'json',
			type : "POST",
			data : {
				"isInnerCheck" : isInnerCheck,
				"leftExpression" : $("#lexpression").val(),
				"rightExpression" : $("#rexpression").val()
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading();
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(data) {
				if (!data.message) {
					window.parent.validObj.leftExpression = data.leftExpression;
					window.parent.validObj.rightExpression = data.rightExpression;
					window.parent.validObj.expressionShortDesc = data.leftExpressionDesc+$(
					"input[data-ligerid=oper]").val()+data.rightExpressionDesc;
					window.parent.validObj.expressionDesc = "(" + $("#lexpression").val() + ")" + $(
					"input[data-ligerid=oper]").val() + "(" + $("#rexpression").val() + ")";
					window.parent.validObj.logicOperType = $(
							"input[data-ligerid=oper]").val();
					window.parent.validObj.leftFormulaIndex = data.leftFormulaIndex;
					window.parent.validObj.rightFormulaIndex = data.rightFormulaIndex;
					window.parent.validObj.leftFormulaDs = data.leftFormulaDs;
					window.parent.validObj.rightFormulaDs = data.rightFormulaDs;
					window.parent.validObj.isInnerCheck = isInnerCheck; 
					window.parent.next();
				} else {
					BIONE.tip(data.message);
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}


	function addTipPic() {
		var tipIcon = "${ctx}/images/classics/icons/lightbulb.png";
		$("#tip").prepend(
				"<div style='width:24px;float:left;height:16px;background:url("
						+ tipIcon + ") no-repeat' />");
	}

	function initTree() {
		var setting = {
			data : {
				key : {
					name : 'text'
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			callback : {
				onClick : zTreeOnClick
			}
		};
		leftTreeObj = $.fn.zTree.init($("#rptTree"), setting);
		//加载数据
		loadTree("${ctx}/report/frame/valid/logic/getRptAndTemplateTree", leftTreeObj);
	}
	function loadTree(url, component, data) {
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			data : data,
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
				for ( var i = 0; i < num; i++) {
					component.removeNode(nodes[0], false);
				}
				if (result.length > 0) {
					component.addNodes(null, result, false);
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	function zTreeOnClick(event, treeId, treeNode, clickFlag) {
		if (treeNode.params.templateId) {
			currentNode = treeNode;
			if(flag == true && "${templateId}" != treeNode.params.templateId){
				BIONE.tip("左表达式只能选择当前模板");
			}else{
				$("#bottom_right").html("");
				clickTmpId = treeNode.params.templateId;
				initDatas(treeNode.params.rptId, treeNode.params.templateId);
			}
		}
	}
	function addFunc() {
		BIONE.loading = true;
		BIONE.showLoading("正在加载数据中...");
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/valid/logic/getFuncAll.json",
			dataType : 'json',
			type : "post",
			success : function(result) {
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
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
		BIONE.loading = false;
		BIONE.hideLoading();
	}
	function addSymbol() {
		BIONE.loading = true;
		BIONE.showLoading("正在加载数据中...");
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/valid/logic/getSymbolAll.json",
			dataType : 'json',
			type : "post",
			success : function(result) {
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
			style="width: 90%; height: 90px; margin-left: auto; margin: auto;">
			<div id="top_middle_left">
				<div id="top_middle_left"
					style="width: 44%; height: 100%; float: left;">
					<div style="width: 100%;">
						<span>左表达式：</span>
					</div>
					<textarea id="lexpression"
						style="width: 100%; height: 85%; margin-top: 0%; float: left; resize: none"
						onclick="savePos(this)" onchange="savePos(this)"></textarea>
				</div>
				<div id="top_middle_middle"
					style="width: 9%; height: 99%; float: left; margin-left: 1%; margin-right: 1%;">
					<div style="margin-top: 50%">
						<select id="oper" name="oper" style="width: 100%;">
							<option value="<"><</option>
							<option value="<="><=</option>
							<option value="==">==</option>
							<option value=">=">>=</option>
							<option value=">">></option>
							<option value="!=">!=</option>
						</select>
					</div>
				</div>
				<div id="top_middle_right"
					style="width: 44%; height: 99%; float: left;">
					<div style="width: 100%;">
						<span>右表达式：</span>
					</div>
					<textarea id="rexpression"
						style="width: 100%; height: 80%; margin-top: 0%; float: left; resize: none"
						onclick="saveRPos(this)" onchange="saveRPos(this)"></textarea>
				</div>
			</div>
		</div>

		<div id="middle"
			style="width: 90%; height: 18px; margin-left: auto; margin-right: auto;">
			<div id="tip"
				style="border: 1px solid #FFF; width: 99%; height: 99%; padding: 0px 2px; background: #fffee6; color: #8f5700;">tip
			</div>
		</div>
		<div id="middle_bottom"
			style="margin-right: auto; margin-top: 2px; float: left;">
			<div style="width: 99%; float: left;">
				<div style="float: left; width: 10%">
					<img src="${ctx }/images/classics/icons/communication.gif"><span>运算符：</span>
				</div>
				<div id="opersign" style="width: 89%; float: left;"></div>
			</div>
			<div style="float: left; width: 99%; margin-top: 4px;">
				<div style="float: left; width: 10%">
					<img src="${ctx }/images/classics/icons/communication.gif"><span>公
						式：</span>
				</div>
				<div id="function" style="float: left; width: 89%;"></div>
			</div>
		</div>

		<div id="report"
			style="width: 90%; margin-left: auto; margin-right: auto; margin-top: 4px;float: left;">
			<div id="bottom_left"
				style="width: 30%; height: 99%; float: left; border: 1px solid #999; overflow: auto; clear: both;">
				<ul id="rptTree" style="font-size: 12; width: 92%" class="ztree"></ul>
			</div>
			<div id="bottom_right"
				style="width: 69%; height: 99%; float: left; border: 1px solid #999; margin-left: 0%;">
			</div>
		</div>
	</div>
</body>
</html>