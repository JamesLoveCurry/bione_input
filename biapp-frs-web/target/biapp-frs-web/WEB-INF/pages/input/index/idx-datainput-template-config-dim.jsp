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
	var type = parent.type;
	var innerdimType = [];
	var dimTypeInfos = [];
	$(function() {
		initDimInfo();
		initTab();
		initForm();
		//ininBtn();
		$('#middle').hide();
		initData();
		
	});
	function getDimItemName(dimTypeNo, dimItemNo) {
		var dimItemNm = "";
		$.ajax({
			async : false,
			cache : false,
			type : "POST",
			dataType : "JSON",
			contentType : "application/json",
			url : '${ctx}/rpt/frame/mgr/idxdsrel/getRptDimItemNm?dimTypeNo='
					+ dimTypeNo + '&dimItemNo=' + dimItemNo,
			success : function(data) {
				dimItemNm = data.dimItemNm;
			},
			error : function() {
				dimItemNm = "";
			}
		});
		return dimItemNm;
	}
	function initDimInfo() {
		$
				.ajax({
					async : false,
					cache : false,
					type : "POST",
					dataType : "JSON",
					contentType : "application/json",
					url : '${ctx}/rpt/frame/mgr/idxdsrel/getRptDimTypeInfo?indexNo=${indexNo}',
					success : function(data) {
						
						for ( var i in data) {
							var flag = false;
							if(data[i].dimType == "00"||(type==1&&data[i].dimTypeNo=="CURRENCY")){
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
		//$("#cover").height($("#center").height()-$("#top").height()-50);
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
				content: this.realText });
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
				//如果是添加指标
				updateDimType($(this).val());
			} else {
				if(unflag){
					$("input").attr("disabled",  false);
					$("label").css("color","blue").css("cursor","pointer");
				}
				if(type==2){
					for ( var i in innerdimType) {
						$("input[value=" + innerdimType[i].id + "]")[0].disabled = true;
						$("input[value=" + innerdimType[i].id + "]").next("label").css("color","#333").css("cursor","default");
					}
				}
				maintab.removeTabItem($(this).val());
				var i = getFilterNo($(this).val());
				parent.dimFilterInfo.splice(i, 1);
				//如果是删除指标
				deleteDimType($(this).val());
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
	function ininBtn() {
		//初始化按钮
		var btns = [{
			text : '取消',
			onclick : function() {
				parent.BIONE.closeDialog("dimFilterWin");
			}
		},{
			text : '确定',
			onclick : function() {
				window.parent.save();
			}
		}];
		BIONE.addFormButtons(btns);
	}

	function initTab() {
		maintab = $("#tab").ligerTab(	
				{
					contextmenu : false,
					moveToNew: false,
					onAfterAddTabItem: function() {
						$('#cover').hide();
						$('#middle').show();
					},
					onAfterRemoveTabItem: function() {
						if (maintab.getTabItemCount() == 0) {
							$('#cover').show();
							$('#middle').hide();
						}
					},
					onAfterSelectTabItem : function(tabId) {
						if ($("#" + tabId + "frame").attr('src') == "") {
							$("#" + tabId + "frame").attr(
									'src',
									'${ctx}/rpt/frame/mgr/idxdsrel/dimInfo?dimTypeNo='
											+ tabId + '&dimTypeStruct='
											+ getDimInfo(tabId).dimType);
						}
					}
				});
	}

	function initData() {
		if (parent.flag) {
			if (parent.dimFilterInfo.length > 0) {
				$('#middle').show();
				var dimfNos = [];
				var dimNum = [];
				for ( var i in parent.dimFilterInfo) {
					var dimInfo = getDimInfo(parent.dimFilterInfo[i].dimNo);
					if (dimInfo != null) {
						addTabInfo(parent.dimFilterInfo[i].dimNo, dimInfo.dimNm);
						dimfNos.push(parent.dimFilterInfo[i].dimNo);
						//edit by fangjuan 20150227
						disabledCheck(parent.dimFilterInfo[i].dimNo);
						//edit end
						if (type == "1")
							parent.dimFilterInfo[i].viewformula = parent
									.getfilterFormula(parent.dimFilterInfo[i].dimNo);
						else
							parent.dimFilterInfo[i].filterformula = parent
									.getfilterFormula(parent.dimFilterInfo[i].dimNo);
						parent.dimFilterInfo[i].indexNo="${indexNo}";
						if(parent.dimFilterInfo[i].filterVal){
							var vals = parent.dimFilterInfo[i].filterVal.split(",");
							var filterTexts = [];
							for ( var j in vals) {
								filterTexts.push(getDimItemName(
										parent.dimFilterInfo[i].dimNo, vals[j]));
							}
							parent.dimFilterInfo[i].filterText = filterTexts
									.join(",");
						}else{

							updateDimType(parent.dimFilterInfo[i].dimNo);
						}
					} else {
						dimNum.push(i);
					} 
				}
				for ( var i = dimNum.length - 1; i >= 0; i--) {
					parent.dimFilterInfo.splice(dimNum[i], 1);
				}
				if (parent.dimFilterInfo.length > 0) {
					maintab.selectTabItem(parent.dimFilterInfo[0].dimNo);
					$.ligerui.get("mainform").setValue(dimfNos.join(";"));
				}
				if(type==2){
					for ( var i in innerdimType) {
						$("input[value=" + innerdimType[i].id + "]")[0].disabled = true;
						$("input[value=" + innerdimType[i].id + "]").next("label").css("color","#333").css("cursor","default");
					}
				}

			} else {
				if (dimInfos==null||dimInfos.length <= 0) {
					parent.BIONE.tip("该指标不存在关联维度，无法配置过滤");
				}
				/* var dimfNos = [];
				
				if(type==2){
					for ( var i in innerdimType) {
						$("input[value=" + innerdimType[i].id + "]")[0].disabled = true;
						$("input[value=" + innerdimType[i].id + "]").next("label").css("color","#333").css("cursor","default");
					}
				}
				for ( var i in innerdimType) {
					dimfNos.push(innerdimType[i].id);
					addTabInfo(innerdimType[i].id, innerdimType[i].text);
				}
				for ( var i in dimTypeInfos) {
					dimfNos.push(dimTypeInfos[i].id);
					addTabInfo(dimTypeInfos[i].id, dimTypeInfos[i].text);
				}
				if(innerdimType.length>0){
					maintab.selectTabItem(innerdimType[0].id);
				}
				if(dimTypeInfos.length>0){
					maintab.selectTabItem(dimTypeInfos[0].id);
				}
				$.ligerui.get("mainform").setValue(dimfNos.join(";")); */
			} 
		} else {
			//var dimfNos = [];
			if (dimInfos==null||dimInfos.length <= 0) {
				parent.BIONE.tip("该指标不存在关联维度，无法配置过滤");
			}
			/* if(type==2){
				for ( var i in innerdimType) {
					$("input[value=" + innerdimType[i].id + "]")[0].disabled = true;
					$("input[value=" + innerdimType[i].id + "]").next("label").css("color","#333").css("cursor","default");
				}
			}
			for ( var i in innerdimType) {
				dimfNos.push(innerdimType[i].id);
				addTabInfo(innerdimType[i].id, innerdimType[i].text);
			}
			for ( var i in dimTypeInfos) {
				dimfNos.push(dimTypeInfos[i].id);
				addTabInfo(dimTypeInfos[i].id, dimTypeInfos[i].text);
			}
			if(innerdimType.length>0){
				maintab.selectTabItem(innerdimType[0].id);
			}
			if(dimTypeInfos.length>0){
				maintab.selectTabItem(dimTypeInfos[0].id);
			}
			$.ligerui.get("mainform").setValue(dimfNos.join(";")); */
		}

	}
	function addTabInfo(id, name) {

		var frame = $("#center").height() - $("#top").height() - 50;
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
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			width : '99%',
			height : '99%',
			columns : [ {
				display : '维度类型',
				name : 'dimTypeNm',
				width : "20%"
			}, {
				display : '维度过滤信息',
				name : 'dimFilterInfo',
				width : "70%"
			} ],
			checkbox : false,
			data : null,
			usePager : true,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			dataAction : 'server',//从后台获取数据
			usePager : false
		});
	}
	function updateDimType(dimTypeNo){
		var i = getFilterNo(dimTypeNo);
		if (i ==-1) {
			parent.dimFilterInfo.push({
				dimNo : dimTypeNo,
				dimNm : getDimInfo(dimTypeNo).dimNm,
				filterMode : "01",
				indexNo: "${indexNo}"
			});
		}
	}
	
	function deleteDimType(dimTypeNo){

		var i = getFilterNo(dimTypeNo);
		parent.dimFilterInfo.splice(i, 1);
	}
	function updateRow(dimTypeNo, filterMode, filterVal, filterText,
			viewformula, filterformula,eVal,lVal) {
		var i = getFilterNo(dimTypeNo);
		if (i >= 0) {
			if (filterVal == "") {
				parent.dimFilterInfo.splice(i, 1);
			} else {
				parent.dimFilterInfo[i].filterMode = filterMode;
				parent.dimFilterInfo[i].filterVal = filterVal;
				parent.dimFilterInfo[i].filterText = filterText;
				parent.dimFilterInfo[i].viewformula = viewformula;
				parent.dimFilterInfo[i].filterformula = filterformula;
				parent.dimFilterInfo[i].indexNo = "${indexNo}";
				parent.dimFilterInfo[i].eVal = eVal;
				parent.dimFilterInfo[i].lVal = lVal;
			}

		} else {
			parent.dimFilterInfo.push({
				dimNo : dimTypeNo,
				dimNm : getDimInfo(dimTypeNo).dimNm,
				filterMode : filterMode,
				filterVal : filterVal,
				filterText : filterText,
				viewformula : viewformula,
				filterformula : filterformula,
				indexNo: "${indexNo}",
				eVal: eVal,
				lVal: lVal
			});
		}
	}

	function getFilterNo(dimTypeNo) {
		for ( var i in parent.dimFilterInfo) {
			if (parent.dimFilterInfo[i].dimNo == dimTypeNo) {
				return i;
			}
		}
		return -1;
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="top" style="height: 150px;width: 99%;  overflow: auto">
			<!-- <div id="maingrid">
				<table cellspacing="0" cellpadding="0" border="0" class="l-listbox-table l-table-checkbox l-listbox-grid">
					<tbody>
						<tr index="0" value="1" class="l-selected">
							<td class="l-checkboxrow" style="width:50px;" width="100"><input type="checkbox" index="0" value="1" id="1"/><label for="1">dim2</label></td>
						</tr>
					</tbody>
				</table>
			</div> -->
			<div id="mainform"></div>
		</div>
		<div id='cover' class='l-tab-loading' style='display:block; position: relative; top: 0px; height : 340px; background:url(${ctx}/images/classics/index/dim.jpg) no-repeat top left #ffffff;'></div>
		<div id="middle"  >
			<div id="tab" style="width: 99%; overflow: hidden;">
			</div>
			
		</div>

	</div>
</body>
</html>