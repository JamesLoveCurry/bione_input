<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<style>
.checklabel {
	color: blue;
	height : 10px;
}
tr {
	vertical-align: middle;
	line-height: 20px;
}
</style>
<script type="text/javascript">
	var mainform = null;
	var maintab = null;
	var dimTypeData = [];
	var dimInfos = [];
	var innerdimType = [];
	var dimTypeInfos = [];
	
	var checkedTypeVals = [];  // key:dimTypeNo , value:filterInfo
	var idxNos = window.parent.idxNos;
	
	$(function() {
		initDimInfo();
		initTab();
		initForm();
		initData();
	});
	
	function initDimInfo() {
		$.ajax({
					async : false,
					cache : false,
					type : "POST",
					url : '${ctx}/report/frame/design/cfg/batch/getDimInfos',
					data : {
						"idxNos" : window.parent.idxNos
					},
					success : function(data) {
						for ( var i in data) {
							var flag = false;
							if(data[i].dimType == "00"||(data[i].dimTypeNo=="CURRENCY")){
								if (data[i].dimType != "00") {
									innerdimType.push({
										id : data[i].dimTypeNo,
										text : data[i].dimTypeNm
									});
									flag = true;
								} else {
									dimTypeInfos.push({
										id : data[i].dimTypeNo,
										text : data[i].dimTypeNm
									});
								}
								dimInfos.push({
									dimNo : data[i].dimTypeNo,
									dimNm : data[i].dimTypeNm,
									dimType : data[i].dimTypeStruct,
									dimDsId : data[i].dsId||null,
									flag : flag
								});
							}
						}
						dimTypeData = innerdimType.concat(dimTypeInfos);
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
	}
	function getDimInfo(dimTypeNo) {
		for ( var i in dimInfos) {
			if (dimTypeNo == dimInfos[i].dimNo)
				return dimInfos[i];
		}
		return null;
	}
	function initForm() {
		for(var i=0;i<dimTypeData.length;i++){
			dimTypeData[i].realText = dimTypeData[i].text;
			if(dimTypeData[i].text.length > 10){
				dimTypeData[i].text = dimTypeData[i].text.substring(0, 10) + "...";
			}
		}
		mainform = $("#mainform").ligerCheckBoxList({
			rowSize : 4,
			css : 'checklabel',
			data : dimTypeData,
			textField : "text"
		});
		var checkboxHeight = $(".l-checkboxlist-inner").height() + 5; 
		$("#top").height(checkboxHeight && checkboxHeight <= 120 ? checkboxHeight : 120);
		$("td").width(($(document).width() / 4));
		$.each($("label"), function(i, n) {
			for(var j=0;j<dimTypeData.length;j++){
				if(dimTypeData[j].id == $("#"+n.htmlFor)[0].value){
					$.extend(n, {
						realText : dimTypeData[j].realText
					});
					break;
					}
				}
			$(n).ligerTip({
				auto:true,
				content: this.realText 
			});
			$(n).removeAttr('for');
		});
		$("input[type=checkbox]").live("click", function() {
			var selectdimNo = $.ligerui.get("mainform").getValue();
			var addflag = false;
			var unflag = true;
			if (selectdimNo != null) {
				var selectdimNos = selectdimNo.split(";");
				for ( var i in selectdimNos) {
					if (selectdimNos[i] == $(this).val()) {
						addflag = true;
					}
					if(getDimInfo(selectdimNos[i]).flag!=true){
						unflag=false;
					}
				}
			}
			if (addflag) {
				var dimInfo = getDimInfo($(this).val());
				if (dimInfo != null) {
					addTabInfo($(this).val(), dimInfo.dimNm);
					maintab.selectTabItem($(this).val());
				}
				disabledCheck($(this).val());
			} else {
				if(unflag){
					$("input").attr("disabled",  false);
					$("label").css("color","blue").css("cursor","pointer");
				}
				maintab.removeTabItem($(this).val());
			}
		});

		$('label').live("click", function(e) {
			maintab.selectTabItem($(this).prev().val());
		});
	}
	function disabledCheck(dimNo) {
		var dimInfo = getDimInfo(dimNo);
		if (!dimInfo.flag) {
			var cdsIds = dimInfo.dimDsId==null?[]:dimInfo.dimDsId.split(",");
			var disDimNo = [];
			if(cdsIds.length>0){
				for ( var i in dimInfos) {
					var flag=false;
					var dsIds=dimInfos[i].dimDsId==null?[]:dimInfos[i].dimDsId.split(",");
					if(dimInfos[i].flag){
						flag=true;
					}
					else{
						for ( var j in dsIds) {
							for(var k in cdsIds){
								if (cdsIds[k] == dsIds[j] ) {
									flag=true;
									break;
									break;
								}
							}
						}
					}
					if(!flag){
						disDimNo.push(dimInfos[i].dimNo);
					}
				}
				for ( var i in disDimNo) {
					$("input[value=" + disDimNo[i] + "]")[0].disabled = true;
					$("input[value=" + disDimNo[i] + "]").next("label").css("color","#333").css("cursor","default");
				}
			}
		}
	}

	function initTab() {
		maintab = $("#tab").ligerTab(	
				{
					contextmenu : false,
					moveToNew: false,
					onAfterAddTabItem: function() {
						$('#cover').hide();
						$("#middle").css("display" , "block");
					},
					onAfterRemoveTabItem: function() {
						if (maintab.getTabItemCount() == 0) {
							$('#cover').show();
							$("#middle").css("display" , "none");
						}
					},
					onAfterSelectTabItem : function(tabId) {
						if ($("#" + tabId + "frame").attr('src') == "") {
							$("#" + tabId + "frame").attr(
									'src',
									'${ctx}/report/frame/design/cfg/batch/filteritem?dimTypeNo='
											+ tabId + '&dimTypeStruct='+ getDimInfo(tabId).dimType
											+ '&idxNos='+idxNos);
						}
					}
				});
	}

	function addTabInfo(id, name) {
		var frame = $("#center").height() - $("#top").height() - 30;
		maintab.addTabItem({
			tabid : id,
			text : name,
			showClose : false,
			content : "<div id='" + id + "' style='height:" + frame
					+ "px;width:100%;'></div>"
		});
		content = "<iframe frameborder='0' id='" + id + "frame' name='" + id
				+ "frame' style='height:100%;width:100%;' src=''></iframe>";
		$("#" + id).html(content);
	}
	
	//修改时初始化数据
	function initData(){
		if(window.parent.filterInfos
				&& window.parent.filterInfos.length
				&& window.parent.filterInfos.length > 0){
			var dimTypeArr = [];
			var checkedTabId = "";
			for(var i = 0 , l = window.parent.filterInfos.length ; i < l ; i++){
				if(!getDimInfo(window.parent.filterInfos[i].dimTypeNo)){
					// 额，好吧，被你发现了，这个地方处理其实不是很好  = 。 = 
					// 原来配置的过滤维度被减维了，就不显示了
					continue;
				}
				dimTypeArr.push(window.parent.filterInfos[i].dimTypeNo);
				checkedTypeVals[window.parent.filterInfos[i].dimTypeNo] = window.parent.filterInfos[i];
				addTabInfo(window.parent.filterInfos[i].dimTypeNo , window.parent.filterInfos[i].dimTypeNm);
				if(i == 0){
					checkedTabId = window.parent.filterInfos[i].dimTypeNo;
				}
			}
			if(maintab
					&& checkedTabId){
				maintab.selectTabItem(checkedTabId);
			}
			mainform.setValue(dimTypeArr.join(";"));
		}
	}

</script>
</head>
<body>
	<div id="template.center">
		<div id="groupDiv" class="l-form l-group l-group-hasicon">
			<img src="${ctx}/images/classics/icons/communication.gif" style="width:16px;height:16px;position:absolute;left:6px;top:6px;" />
			<span style="width:5em;margin-left : 22px;"><b>维度类型</b></span>
		</div>
		<div id="top" style="width: 99%;  overflow: auto; border-bottom:1px solid #DDDDDD">
			<div id="mainform" style="width:100%">
			</div>
		</div>
		<div id='cover' class='l-tab-loading' style='display:block; position: relative; top: 0px; height : 360px; background:url(${ctx}/images/classics/index/dim.jpg) no-repeat top left #ffffff;'></div>
		<div id="middle"  style="display:none">
			<div id="tab" style="width: 99%; overflow: hidden;">
			</div>
		</div>
	</div>
</body>
</html>