<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/jqueryui/smoothness/jquery-ui.min.css" />
<script type="text/javascript"
	src="${ctx}/js/jqueryUI/jquery-ui-1.10.3.custom.min.js"></script>
<script type="text/javascript">
	var dimTypeNo = window.parent.selectionIdx.dimTypeNo();
	var displayLvl = window.parent.selectionIdx.displayLevel();
	var lvlArr = [];
	var baseFlag = false;
	$(function() {
		initSlider();
		initLayout();
		initButtons();
		initDimMapping();
	});
	
	function baseCheck(){
		baseFlag =$("#base")[0].checked;
	}
	
	function initDimMapping(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/design/cfg/platform/getDimLvlMapping",
			dataType : 'json',
			type : "post",
			data : {
				dimTypeNo : dimTypeNo
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在获取层级信息...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result){
				if(result){
					// 维度类型信息
					dimInfo = result.dimInfo;
					// 维度级别映射map
					lvlMap = result.lvlMap;
					if(lvlMap){
						$.each(lvlMap , function(i , n){
							lvlArr.push(n);
						})
						$( "#slider").slider({
							max : lvlArr.length
						});
						var lvlTmp = 1
						if(typeof displayLvl == "string" && displayLvl.indexOf("N")< 0){
							lvlTmp = typeof displayLvl == "undefined" || displayLvl == "" ? "1" : displayLvl;
						}
						else{
							lvlTmp = displayLvl;
						}
						var textTmp = "按级别显示 : <span class='lvl_descspan'><font color='red'><b>"+lvlTmp+"-"+lvlArr[Number(lvlTmp) - 1]+"</b></font></span>";
						$($("input[name='radio']")[1]).next("label").html(textTmp);
						if(typeof displayLvl == "undefined" || displayLvl == ""){
							turnOnOffLvlChoose(false);
							turnOnOffDownLvlChoose(false);
						}
						else{
							if(displayLvl.toString().indexOf("N")>=0){
								turnOnOffLvlChoose(false);
								turnOnOffDownLvlChoose(true);
								if(displayLvl.toString().indexOf("B")>=0){
									$("#base").attr("checked",true);
									baseFlag = true;
								}
								var val = displayLvl.substring(displayLvl.toString().indexOf("N")+1,displayLvl.length);
								$("#level").val(val);
							}
							else{
								turnOnOffLvlChoose(true);
								turnOnOffDownLvlChoose(false);
							}
						}
						
					}
				}
			}
		});
	}
	
	function initLayout(){
		// 初始化form tab高度
		$("#contentDiv").height($("#center").height() - 30 - 30);
		// 初始化radio
		var value = "0";
		if(typeof displayLvl == "undefined" || displayLvl == ""){
			value = "0";
		}
		else{
			if(displayLvl.toString().indexOf("N")>=0){
				value = "2";
			}
			else{
				value = "1";
			}
		}
		
		var rdata=  [{"id":"0","text":"显示全部："},
				    {"id":"1", "text":"按级别显示"}];
		if(dimTypeNo != "DATE")
			rdata.push({"id":"2", "text":"按下级显示："});
		 radio = $("#radio").ligerRadioList(
		 {
			 data:rdata,
			 textField: 'text',
			 rowSize: 1,
			 value : value
		});
		radio.radioList.on("click" , function(){
			var value = radio.getValue();
			if(value == "0"){
				turnOnOffLvlChoose(false);
				turnOnOffDownLvlChoose(false);
			}
			else if(value == "1"){
				turnOnOffLvlChoose(true);
				turnOnOffDownLvlChoose(false);
			} else {
				turnOnOffLvlChoose(false);
				turnOnOffDownLvlChoose(true);
			}
		});
		if(typeof displayLvl == "undefined" || displayLvl == ""){
			turnOnOffLvlChoose(false);
		}
		// 初始化滑块，滑块部分样式
		var divWidth = $("#contentDiv").width();
		$(".sliderContent").css("margin-left" , divWidth*0.1); // (divWidth:100% - 滑块width:80%) / 2:两端 
	}
	
	function initButtons(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : closeDialog
		});
		buttons.push({
			text : '保存',
			onclick : saveLvl
		});
		BIONE.addFormButtons(buttons);
	}
	
	function initSlider(){
		var dLvl =1
		if(typeof displayLvl == "string" && displayLvl.indexOf("N")<0){
			dLvl = typeof displayLvl == "undefined" || displayLvl == "" ? 1 : displayLvl;
		}
		else{
			dLvl = displayLvl;
		}
		slider = $( "#slider").slider({
			range: "min",
			value: dLvl,
			min: 1,
			max: 1,
			step: 1,
			slide: function( event, ui ) {
				var textTmp = "按级别显示 : <font color='red'><b>"+ui.value+"-"+lvlArr[ui.value - 1]+"</b></font>";
				$($("input[name='radio']")[1]).next("label").html(textTmp);
			}
		});
		$(".ui-slider-handle").focus();
	}
	
	// 显示/隐藏级别显示   true - 显示 ； false - 隐藏
	function turnOnOffLvlChoose(flag){
		if(flag === true){
			$(".lvl_descspan").show();
			if(slider){				
				$( "#slider").show();
				$(".ui-slider-handle").focus();
				$($("input[name='radio']")[1]).next("label").find("b").show();
			}
		}else{
			$(".lvl_descspan").hide();
			$($("input[name='radio']")[1]).next("label").find("b").hide();
			if(slider){
				$( "#slider").hide();
			}
		}
	}
	
	// 显示/隐藏级别显示   true - 显示 ； false - 隐藏
	function turnOnOffDownLvlChoose(flag){
		if(flag === true){
			$("#downLvl").show();
		}else{
			$("#downLvl").hide();
		}
	}
	
	// 关闭对话框
	function closeDialog(){
		BIONE.closeDialog("moduleClkDialog");
	}
	
	// 保存
	function saveLvl(){
		// 获取选中的显示级别
		if(radio){
			var typeVal = radio.getValue();
			if(typeVal == "0"){
				// 显示全部
				window.parent.selectionIdx.displayLevel("");
			}else if(typeVal == "1"){
				// 显示具体级别
				window.parent.selectionIdx.displayLevel($( "#slider").slider("value"));
			}else if(typeVal == "2"){
				// 显示具体级别
				var level = baseFlag ? "BN" : "N";
				level += $("#level").val();
				window.parent.selectionIdx.displayLevel(level);
			}
		}
		closeDialog();
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div class="l-form-tabs">
			<ul original-title="" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
				<li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-hover" data-index="0">
					<a href="javascript:void(0)">显示设置</a>
				</li>
			</ul>
			<div class="ui-tabs-panel ui-widget-content ui-corner-bottom" id="contentDiv" data-index="0">
				<div id="radio"></div>
<!-- 				<p class="sliderContent"> -->
<!-- 					<label for="sliderTip">显示级别:</label> -->
<!-- 					<input type="text" id="sliderTip" style="border:0; color:#f6931f; font-weight:bold;" /> -->
<!-- 				</p> -->
				<div id="slider" class="sliderContent" style="width:80%;margin-top:15px;"></div>
				<div id="downLvl" style="width:80%;margin-top:15px;margin-left:20px;">是否显示本级：<input type="checkbox" id="base" name="check" value="base" onclick=baseCheck() /><span style="margin-left:50px">显示下级层数:</span><input id="level" type="text"/></div>
			</div>
		</div>
	</div>
</body>
</html>