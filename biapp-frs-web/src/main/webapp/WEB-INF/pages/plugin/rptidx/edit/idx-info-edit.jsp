<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<c:choose>
	<c:when test="${isDict==true}">
		<meta name="decorator" content="/template/template14.jsp">
	</c:when>
	<c:otherwise>
		<meta name="decorator" content="/template/template5.jsp">
	</c:otherwise>
</c:choose>
<head>
<style type="text/css">
#navtab iframe {
	border: none;
    width: 100%;
    height: 100%;
}
.l-dialog-btn.disable, .l-dialog-btn.disable:hover {
	color: #CCC;
	background-color: #FFF;
	background-image: none;
	cursor: default;
}
</style>
<script type="text/javascript">
var tab;
var isDict = '${isDict}' == 'true';
var isRpt = '${isRpt}' == 'true';
var isIdxInfluence = false;
var isNewVerIdx = false;
function IdxInfo(params) {
	this._init(params);
}
$.extend(IdxInfo.prototype, {
	_init: function(params) {
		$.extend(this, {
			indexNo : '',
			indexCatalogNo : '',
			indexCatalogNm : '',
			indexVerId : '',
			indexNm : '',
			indexUsualNmTemp1 : '',
			indexUsualNmTemp2 : '',
			indexType : '',
			indexSts : '',
			dataType : '',
			busiDef : '',
			dataLen : '',
			busiModel:'',
			dataPrecision : '',
			dataUnit : '',
			valRange : '',
			calcCycle : '',
			startDate : '',
			defDept : '',
			useDept : '',
			isSum : '',
			infoRights : '',
			busiRule : '',
			remark : '',
			oldStartDate:'',
			isPublish:'',
			defSrc: '',
			isExpire: ''
		}, params);
	}
});
indxInfoObj = new IdxInfo({
	firstEdit : true,
	catalogName : parent.curCatalogName,
	indexCatalogNo: '${indexCatalogNo}',
	defSrc: '${defSrc}',
	indexVerId: '${indexVerId}'
});
$(function() {
	toggleBtn();
	$('#base').attr('src', '${ctx}/report/frame/idx/${id}/busi?indexVerId=${indexVerId}&cata=${indexCatalogNo}&readOnly=${isDict}');
	tab = $("#navtab").ligerTab({
		onBeforeSelectTabItem: function(id) {
			try {
				var w = frames.base.contentWindow || frames.base;
				if (!w.$("#mainform").valid()) {
					return false;
				}
				if ("tech" === id && toggleBtn.flag == false || "base" === id && toggleBtn.flag == true) {
					toggleBtn();
				}
				if (!'${id}') {
					indxInfoObj.indexNo = w.$('#indexNo').val();
				}
				if ("tech" === id && this.getSelectedTabItemID() != id) {
					var idxType = w.liger.get('indexType').getValue();
					if (this.idxType != idxType) {
						this.idxType = idxType;
						reload(idxType);
					}
				}
				if ("tech" === id){
					$("#bottom .l-dialog-btn:eq(1)").removeClass('disable').on('click.btn', idxInfluence);
					$("#bottom .l-dialog-btn:eq(2)").removeClass('disable').on('click.btn', newVer);
					//$("#bottom .l-dialog-btn:eq(3)").removeClass('disable').on('click.btn', idxInfluence);
				}
				if ("filter" === id) {
					$('#filter').attr('src', '${ctx}/report/frame/tmp/view/index');
				}
				
			} catch(e) {
				// alert(e.message);
				return false;
			}
		}
	});
	tab.idxType = '';
	$("#navtab iframe").height($("#center").height() - $(".l-tab-links").height() - 1);
});
function newVer() {
	isNewVerIdx = true;
	idxInfluence();
}
function submit() {
	var isNewVer = isNewVerIdx;
	if(!isIdxInfluence){
		BIONE.tip("请先确认指标影响，再进行保存");
		return  ;
	}
	try {
		var base = frames.base.contentWindow || frames.base;
		var tech = frames.tech.contentWindow || frames.tech;
		var data = $.extend({}, {indexCatalogNo: '${indexCatalogNo}',defSrc: '${defSrc}',indexVerId: '${indexVerId}'}, base.getData(), tech.getData());
		if(data.indexType == '01'){
			if(data.dataJsonStr == '{fields:[]}'){
				BIONE.tip("此指标为根指标，请至少选择一个度量字段");
				return  ;
			}
		}
		if (true == isNewVer) {
			var ver = parseInt(data.indexVerId || '1');
			$.extend(data, {
				indexVerId: ++ver,
				isNewVer: 'Y'
			});
			if(data.startDate <= indxInfoObj.oldStartDate){
				BIONE.tip("新版启用日期必须大于旧版启用日期");
				return  ;
			}
		} else {
			data.isNewVer = 'N';
		}
		$.each(data, function(i, n) {
			if (n == null) {
				data[i] = '';
			}
		});
		$.ajax({
			type : "POST",
			url : "${ctx}/report/frame/idx/createRptIdxInfo",
			dataType : 'json',
			data: data,
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在保存中，请稍等……");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success: function() {
				top.BIONE.tip('保存成功');
				//刷新grid
				parent.frames["rptIdxCenterTabFrame"].grid.loadData();
				var treeObj = parent.leftTreeObj;
				var ifChangeDirectionaryCatalogNode = treeObj.getNodeByParam("id", indxInfoObj.indexCatalogNo, null);
				if(ifChangeDirectionaryCatalogNode)
				              treeObj.reAsyncChildNodes(ifChangeDirectionaryCatalogNode, "refresh");	
				treeObj.reAsyncChildNodes(parent.currentNode, "refresh");
				
				BIONE.closeDialog('idxEdit');
			},
			error: function() {
				top.BIONE.tip('保存失败');
			}
		});
	} catch(e) {
		BIONE.tip(e.message);
	}
}

function reload(idxType) {
	$('#tech').parent().find('.l-tab-loading').show();
	$("#bottom .l-dialog-btn:gt(0)").addClass('disable').off('click.btn');
	$('#tech').attr('src', '${ctx}/report/frame/idx/${id}/tech?isDict=${isDict}&indexVerId=${indexVerId}&type=' + idxType);
}

function toggleBtn() {
	var btn1 = [{
		text: '取消',
		onclick: function() {
			if(isDict){
				BIONE.closeDialog('rptIdxInfoPreviewBox');
			}
			BIONE.closeDialog('idxEdit');
		}
	}];
	var btn2 = [btn1[0], {
		text: '保存'
	}
	//基础指标不设置版本概念
/* 	, {
		text: '发布新版本'
	} */];
	$('.form-bar .form-bar-inner').empty();
	if(isDict){
		BIONE.addFormButtons(btn1);
	}else{
		if (toggleBtn.flag == true) {
			BIONE.addFormButtons(btn1);
		} else {
			BIONE.addFormButtons(btn2);
		}
	}
	toggleBtn.flag = !toggleBtn.flag;
}
toggleBtn.flag = true;

function idxInfluence(){
	if('${id}'){//指标是修改的，才去查询影响分析
		var base = frames.base.contentWindow || frames.base;
		var tech = frames.tech.contentWindow || frames.tech;
		var data = $.extend({}, {indexCatalogNo: '${indexCatalogNo}',defSrc: '${defSrc}',indexVerId: '${indexVerId}'}, base.getData(), tech.getData());
		BIONE.commonOpenDialog("指标影响查看",
				"idxInfluence",600, $(document).height(),
				"${ctx}/report/frame/idx/idxInfluence?indexNo=" + data.indexNo + "&indexVerId=" + data.indexVerId, null);
	}else{
		isIdxInfluence = true;
		submit();
	}
}
</script>
</head>
<body>
<div id="template.center">
	<div id="navtab" class="liger-tab">
		<div tabid="base" title="业务属性">
			<iframe id="base" frameborder="0"></iframe>
		</div>
		<div tabid="tech" title="技术属性">
			<iframe id="tech" frameborder="0"></iframe>
		</div>
		<c:if test="${isRpt==true}">
			<div tabid="filter" title="过滤信息" >
				<iframe id="filter" frameborder="0"></iframe>
			</div>
		</c:if>
	</div>
</div>
</body>
</html>