<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5_BS.jsp">
<link rel="stylesheet" href="${ctx}/css/classics/jqueryui/smoothness/jquery-ui.min.css">
<script src="${ctx}/js/jqueryUI/jquery.ui.core.js"></script>
<script src="${ctx}/js/jqueryUI/jquery.ui.datepicker.js"></script>
<script type="text/javascript">
	
	var rptId = "${rptId}";
	var templateId = "${templateId}";
	var verId = "${verId}";  // 当前版本号
	var maxVerId = "${maxVerId}"; // 最新版本号
	var canEdit = "${canEdit}"; // 是否可编辑
	var type = "${type}";
	var rptNum = "${rptNum}";
	var selCatalogId ="${selCatalogId}";
	var selCatalogNm ="${selCatalogNm}";
	var templateType = "02";
	var verStartTime = "";  // 版本开始时间
	var isRptIdxCfg = "${isRptIdxCfg}";//是否报表指标配置
	var isRptTmpCfg = "${isRptTmpCfg}";//是否报表表样配置
	var baseInfo4Upt = null;   //修改后的报表基本信息
	var designInfo4Upt = null;
	var isChoDesign = false;
	
	var frameHeight;
	
	var tabObj;
	
	var frameSrcs = [];
	frameSrcs["tabBase"] = "${ctx}/report/frame/design/cfg/frsindex/edit/base";
	frameSrcs["tabDesign"] = "${ctx}/report/frame/design/cfg/frsindex/edit/design/frame?rptNum =" + rptNum;
	
	$(function() {
		// 初始化控件环境
		initModule();
		frameHeight = $(document).height() - 75;
		// 修改时初始化数据
		initUptDatas();
		// 初始化页签
		initTab();
		// 初始化底部按钮
		initBtns();
		if(("Y" == isRptIdxCfg) || ("Y" == isRptTmpCfg)){//报表指标配置时隐藏报表基本信息tab便签页
			$("li[tabid='tabBase']").hide();
			$("#tabBase").hide();
			tabObj.selectTabItem("tabDesign");
		}
	});
	
	// 修改时初始化数据
	function initUptDatas(){
		if(rptId != null
				&& rptId != ""
				&& typeof rptId != "undefined"){
			// 1.基本信息
			baseInfo4Upt =  '${rptInfo}' ? JSON2.parse('${rptInfo}') : null;
			if(baseInfo4Upt != null){
				templateId = baseInfo4Upt.templateId,
				verStartTime = baseInfo4Upt.verStartDate;
				verId = baseInfo4Upt.verId;
			}
			templateType = "${templateType}";
			// 2.设计器信息
/* 			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frame/design/cfg/getDesignInfo",
				dataType : 'json',
				data : {
					templateId : baseInfo4Upt.templateId,
					verId : verId,
					getChildTmps : true
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
			}); */
		}
	}
	
	// 初始化底部按钮
	function initBtns() {
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("rptEdit");
			}
		});
		if(canEdit != "N"){
			// 是否可编辑为是的就可以进行维护，不再要求必须是最新版本
			buttons.push({
				text : '保存并退出',
				onclick : function(){
					rpt_save(true)
				}
			});
			if(templateId){
				buttons.push({
					text : '保存',
					onclick : function(){
						rpt_save(false)
					}
				});
			}
		}
		BIONE.addFormButtons(buttons);
	}
	
	// 初始化页签
	function initTab() {
		$("#tabBase").height(frameHeight);
		$("#tabDesign").height(frameHeight);
		tabObj = $('#tab').ligerTab({
			onBeforeSelectTabItem : function(tabId){
				if(tabId == "tabDesign"){
					isChoDesign = true;
				}
				var frameSrc = $("#"+tabId).attr("src");
				if( frameSrc == ""
						|| frameSrc == null
						|| typeof frameSrc == "undefined"){
					$("#"+tabId).attr("src" , frameSrcs[tabId]);
				} else if(tabId == "tabDesign"
						&& document.getElementById("tabDesign").contentWindow.templateType != templateType){
					tabObj.reload("tabDesign");
				}
			}
		});
		// 选中指定页签
		if(type != null
				&& typeof type != "undefined"
				&& "2" == type){
			tabObj.selectTabItem("tabDesign");
		} else {
			tabObj.selectTabItem("tabBase");
		}
	}
	
	function needToSaveDesign(){
		var flag = false;
		// 启用时间或业务类型发生变化的话，需要同步设计模板和指标
		var baseContext = document.getElementById("tabBase").contentWindow;
		if(baseContext
				&& typeof baseContext.needToSaveDesign == "function"){			
			flag = baseContext.needToSaveDesign();
		}
		return flag;
	}
	
	// 保存
	function rpt_save(exit) {
		if(tabObj == null
				|| typeof tabObj == "undefined"){
			return ;
		}
		if(rptId){
			// 修改
			//  1、任何情况下，基础信息都要重新update
			//  2、设计器信息，在只修改了基本信息的情况下，只保存基础信息
			var baseObjTmp = baseInfo4Upt != null ? baseInfo4Upt :  generateBaseContext();
			
			if(baseObjTmp == null){//baseObjTmp 报表基本信息
				return ;
			}
			// 若并未选择设计tab，判断是否需要重新保存设计模板信息
			var designTmp = null;
			if(isChoDesign){//如果选择过了报表设计tab页
				designTmp = generateDesignContext();
			}
			saveHandler(baseObjTmp , designTmp , exit);
		}else{		
			// 新增
			if($("#tabBase").attr("src") == null
					|| $("#tabBase").attr("src") == ""){
				tabObj.selectTabItem("tabBase");
				return ;
			} else if($("#tabDesign").attr("src") == null
					|| $("#tabDesign").attr("src") == ""){
				tabObj.selectTabItem("tabDesign");
				return ;
			}
			// 基本信息校验整合
			var tabBaseObj = generateBaseContext();
			// 报表设计器信息整合
			var tabDesignObj = generateDesignContext();
			if(tabBaseObj == null
					|| tabDesignObj == null){
				return ;
			}
			// 进行保存
			saveHandler(tabBaseObj , tabDesignObj , exit);
		}
	}
	
	function generateDesign(obj){
		var rptIdxs4Save = [];
		if(obj.formulaCells){
			for(var i = 0 , l = obj.formulaCells.length ; i < l ; i++){
				obj.formulaCells[i].cellType = "04";
			}
			$.merge(rptIdxs4Save , obj.formulaCells);
		}
		if(obj.idxCalcCells){
			for(var i = 0 , l = obj.idxCalcCells.length ; i < l ; i++){
				obj.idxCalcCells[i].cellType = "05";
			}
			$.merge(rptIdxs4Save , obj.idxCalcCells);
		}
		if(obj.idxCells){
			for(var i = 0 , l = obj.idxCells.length ; i < l ; i++){
				obj.idxCells[i].cellType = "03";
			}
			$.merge(rptIdxs4Save , obj.idxCells);
		}
		if(obj.moduleCells){
			for(var i = 0 , l = obj.moduleCells.length ; i < l ; i++){
				obj.moduleCells[i].cellType = "02";
			}
			$.merge(rptIdxs4Save , obj.moduleCells);
		}
		if(obj.staticCells){
			for(var i = 0 , l = obj.staticCells.length ; i < l ; i++){
				obj.staticCells[i].cellType = "06";
			}
			$.merge(rptIdxs4Save , obj.staticCells);
		}
		return {
				lineId : obj.tmpInfo.lineId == null ? "" : obj.tmpInfo.lineId,
				templateId : obj.tmpInfo.id.templateId,
				tmpJson : obj.tmpInfo.templateContentjson , 
				tmpRemark : obj.tmpInfo.remark ,
				idxsArray : rptIdxs4Save,
				rowCols : obj.rowCols ? obj.rowCols : []
		};
	}
	
	function generateBaseContext(){
		var baseContext = document.getElementById("tabBase").contentWindow;
		var tabBaseObj = baseContext.prepareDatas4Save();
		if(tabBaseObj == null){
			tabObj.selectTabItem("tabBase");
			return null;
		}
		return tabBaseObj;
	}
	
	function generateDesignContext(){
		var designContext = document.getElementById("tabDesign").contentWindow;
		var tabDesignObj = designContext.prepareDatas4Save();
		if(tabDesignObj == null){
			tabObj.selectTabItem("tabDesign");
			return null;
		}
		return tabDesignObj;
	}
	
	function save4PublishHandler(tabBaseObj , tabDesignObj){
		var data = {};
		if(tabBaseObj != null){
			var objTmp = {};
			jQuery.extend(objTmp , tabBaseObj);
			objTmp.templateNm = $("#publishName").val();
			data.baseObj = JSON2.stringify(objTmp);
			data.templateId = objTmp.templateId;
			data.versionId = objTmp.verId;
		}
		if(tabDesignObj != null
				&& typeof tabDesignObj != "undefined"){
			data.tmpArr = JSON2.stringify(tabDesignObj.saveObjArr);
			data.mainTmp = JSON2.stringify(tabDesignObj.mainTmp);
		}
		data.newVerStartDate = $("#publishDate").val();
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/design/cfg/frsindex/tmpPublish",
			dataType : 'json',
			data : data,
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在发布，请稍后..");
			},
			success : function(result){
				BIONE.tip("发布成功");
				if (typeof BIONE != 'undefined') {
					BIONE.loading = false;
					BIONE.hideLoading();
				}
				// 将版本开始日期同步回当前页面
				var baseContent = document.getElementById("tabBase").contentWindow;
				var currStartDate = $("#publishDate").val();
				if(baseContent.uptOldObj){			
					baseContent.$("#verStartDate").val(currStartDate);
				}else if(baseInfo4Upt){
					baseInfo4Upt.verStartDate = currStartDate;
				}
				// 关闭窗口
				var publishDialog = jQuery.ligerui.get("publishDialog");
				if(publishDialog
						&& typeof publishDialog.show == "function"){
					publishDialog.hide();
				}
				beforeCloseFunc(result);
				BIONE.closeDialog("rptEdit");
			},
			error:function(){
				if (typeof BIONE != 'undefined') {
					BIONE.loading = false;
					BIONE.hideLoading();
				}
				BIONE.tip("发布失败，请联系系统管理员");
			}
		});
	}
	
	function saveHandler(tabBaseObj , tabDesignObj , exit){
		var data = {};
		if(tabBaseObj != null){
			var objTmp = {};
			jQuery.extend(objTmp , tabBaseObj);
			data.baseObj = JSON2.stringify(objTmp);
		}
		if(tabDesignObj != null
				&& typeof tabDesignObj != "undefined"){
			if(tabDesignObj.mainTmp != null || typeof tabDesignObj.mainTmp != "undefined"){
				var tmpIdxs = tabDesignObj.mainTmp.idxsArray;
				var num = tmpIdxs.length;
				var dsPkJsons = [];
				for(var i=0; i<num; i++){
					var idxCellNm = tmpIdxs[i].cellNm;
					var realIndexNo = tmpIdxs[i].realIndexNo;
					if(idxCellNm == "" || idxCellNm == null || typeof idxCellNm == "undefined"){
						BIONE.tip("单元格"+ tmpIdxs[i].cellNo +"名称不能为空!");
						return;
					}else{
						for(var j=i+1; j<num; j++){
							if(tmpIdxs[j].cellNm == idxCellNm){
								BIONE.tip("单元格"+ tmpIdxs[i].cellNo +"与"+ tmpIdxs[j].cellNo +"名称不能相同!");
								return;
							}
						}
					}
					if(realIndexNo != "" && realIndexNo != null && typeof realIndexNo != "undefined"){
						for(var j=i+1; j<num; j++){
							if(tmpIdxs[j].realIndexNo == realIndexNo){
								BIONE.tip("单元格"+ tmpIdxs[i].cellNo +"与"+ tmpIdxs[j].cellNo +"的报表指标号不能相同!");
								return;
							}
						}
					}
					var dsPkJson = {};
					if(tmpIdxs[i].dsId != null){
						if(tmpIdxs[i].isPk == "Y"){
							dsPkJson.dsId = tmpIdxs[i].dsId;
							dsPkJson.columnName = tmpIdxs[i].columnId;
							dsPkJsons.push(dsPkJson);
						}
					}
				}
				//判断保存的模板信息是否包含模型的所有主键
				if(dsPkJsons != null && dsPkJsons.length > 0){
					var result = getDsPksResult(dsPkJsons);
					if(result != null && result.msg != ""){
						BIONE.showError(result.msg);
						return;
					}
				}
				
				//若未 return
				data.tmpArr = JSON2.stringify(tabDesignObj.saveObjArr);
				data.mainTmp = encodeURIComponent(JSON2.stringify(tabDesignObj.mainTmp));
				data.changeRptIdxs = JSON2.stringify(tabDesignObj.mainTmp.changeArray);
				data.isRptTmpCfg = isRptTmpCfg;
			}
		}
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/design/cfg/saveFrsRpt",
			dataType : 'json',
			data : data,
			type : "post",
			beforeSend : function() {
 				BIONE.loading = true;
				BIONE.showLoading("正在保存，请稍后..");
			},
			success : function(result){
				BIONE.tip("维护成功");
				if (typeof BIONE != 'undefined') {
					BIONE.loading = false;
					BIONE.hideLoading();
				}
				if(exit === true){
					beforeCloseFunc(result);
					BIONE.closeDialog("rptEdit");
				}else{
					// 保存但不退出时，缓存已保存的信息
					var rptIdTmp = result.rptId;
					var lineTmpIds = result.lineTmpIds;
					var verId = result.verId;
					var baseTab = document.getElementById("tabBase").contentWindow;
					var designTab = document.getElementById("tabDesign").contentWindow;
					if(baseTab
							&& baseTab.mainform){
						baseTab.$("#baseform input[name=rptId]").val(rptIdTmp);
						baseTab.$("#baseform input[name=verId]").val(verId);
						baseTab.uptOldObj.busiType = baseTab.$.ligerui.get("serviceTypeCombo").getValue();
						baseTab.uptOldObj.verStartDate = baseTab.$("#verStartDate").val();
					}
					if(designTab
							&& designTab.rptTab){
						designTab.lineTmpIds = lineTmpIds;
					}
					//清空口径变动提醒，同步最新数据
					var mainTmpTab = designTab.document.getElementById('_mainTab').contentWindow;
					mainTmpTab.initRptIdxs = mainTmpTab.strRptIdxs(mainTmpTab.Design.rptIdxs);
					mainTmpTab.rptIdxchange = {};
				}
			},
			error:function(){
				if (typeof BIONE != 'undefined') {
					BIONE.loading = false;
					BIONE.hideLoading();
				}
				BIONE.tip("维护失败，请联系系统管理员");
			}
		});
	}
	
	function getDsPksResult(dsPkJsons){
		var resultMsg = null;
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/design/cfg/getDsPksResult",
			dataType : 'json',
			data : {
				dsPkJsons : JSON2.stringify(dsPkJsons)
			},
			type : "post",
			success : function(result){
				resultMsg = result;
			}
		});
		return resultMsg;
	}
	
	//判断是否包含rptNum
	function isContains(str, rptNumstr){
		return new RegExp(rptNumstr).test(str);  //返回true或false
	}
	
	function beforeCloseFunc(data) {
		if(data){
			// 刷新页面
			var $iframe = window.parent.findIframeByTitle("通用报表定制");
			if($iframe){
				$iframe[0].contentWindow.mainTab.reload('topTab');
			}
		}
	}
	
	// 初始化控件环境
	function initModule(){
		$.datepicker.regional['zh-CN'] = {  
                closeText: '关闭',  
                prevText: '<上月',  
                nextText: '下月>',  
                currentText: '今天',  
                monthNames: ['一月','二月','三月','四月','五月','六月',  
                '七月','八月','九月','十月','十一月','十二月'],  
                monthNamesShort: ['一','二','三','四','五','六',  
                '七','八','九','十','十一','十二'],  
                dayNames: ['星期日','星期一','星期二','星期三','星期四','星期五','星期六'],  
                dayNamesShort: ['周日','周一','周二','周三','周四','周五','周六'],  
                dayNamesMin: ['日','一','二','三','四','五','六'],  
                weekHeader: '周',  
                dateFormat: 'yy-mm-dd',  
                firstDay: 1,  
                isRTL: false,  
                showMonthAfterYear: true,  
                yearSuffix: '年'};  
            $.datepicker.setDefaults($.datepicker.regional['zh-CN']); 
	}
	
	// 从日期字符串生成日期对象
	function generateDateFromStr(dateStr){
		var date = new Date();
		if(dateStr){
			if (typeof dateStr == 'string') {
				if(dateStr.indexOf('-') != -1
						&& dateStr.length >= 10){					
					// if format as 'yyyy-MM-dd' or 'yyyy-mm-dd
					// hh:mm:ss'
					tdate = new Date(new Number(dateStr
							.substr(0, 4)), new Number(dateStr
							.substr(5, 2)) - 1, new Number(dateStr
							.substr(8, 2)));
				}else if(dateStr.length == 8){
					tdate = new Date(new Number(dateStr
							.substr(0, 4)), new Number(dateStr
							.substr(4, 2)) - 1, new Number(dateStr
							.substr(6, 2)));
				}
			} else {
				tdate = new Date(dateStr);
			}
			date = new Date(tdate);
		}
		return date;
	}
	
	// 从日期转换成字符串(8位)
	function generateStrFromDate(date){
		var dateStr = "";
		if(date 
				&& typeof date.getFullYear == "function"){
			var yearStr = date.getFullYear();
			var monthStr = (date.getMonth()+1) + "";
			if(monthStr.length == 1){
				monthStr = "0" + monthStr;
			}
			var dateStr = date.getDate() + "";
			if(dateStr.length == 1){
				dateStr = "0" + dateStr;
			}
			dateStr = yearStr + "" + monthStr + "" + dateStr;
		}
		return dateStr;
	}

</script>
</head>
<body>
	<div id="template.center">
		<div id="tab" style="width:100%;overflow: hidden;">
			<div tabid="tabBase" title="报表信息"  >
				<iframe frameborder="0" id="tabBase" src=""></iframe> 
			</div>
			<div tabid="tabDesign" title="模板设计"  >
				<iframe frameborder="0" id="tabDesign" src=""></iframe> 
			</div>
		</div>
	<div id="publishDiv" style="width:370px; margin:3px; display:none;">
    	<h3>请确定新版本的启用时间</h3><br/>
    	<div>
   			<label style="float: left;" for="publishDate">启用时间：</label>
       		<input style="width: 100px; float: left; margin-right: 40px;" type="text" id="publishDate" />
	        <label for="publishDate">版本名称：</label>
       		<input style="width: 100px; float: right; margin-right: 10px;" type="text" id="publishName" />
    	</div>
 	</div>
</div>
</body>
</html>