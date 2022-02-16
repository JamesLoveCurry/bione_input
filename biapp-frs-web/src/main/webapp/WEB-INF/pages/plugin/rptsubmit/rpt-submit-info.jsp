<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<html>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template20B.jsp">
<head>
<link rel="stylesheet" type="text/css" href="${ctx}/plugin/js/show/views/img/iconfont.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/css/classics/daterangepicker/daterangepicker.css"/>
<style type="text/css">
html {
	background-color: #f1f1f1;
}

#report {
	position: absolute;
	height: 100%;
	width: 100%;
}

#temps-loading {
	background-image:
		url("${ctx}/css/classics/ligerUI/Gray/images/ui/loading2.gif");
}

#togglebutton {
	border-radius: 2px;
	position: absolute;
	width: 68px;
	height: 19px;
	border: 1px solid #CCC;
	line-height: 20px;
	right: 0px;
	top: 1px;
	text-align: center;
	cursor: pointer;
	z-index: 99999;
}

#togglebutton:hover {
	border: 1px solid #C0C0C0;
	box-shadow: 0 1px 1px rgba(0, 0, 0, 0.15);
}
</style>
<script type="text/javascript" src="${ctx}/js/require/require.js"></script>
<script type="text/javascript" src="${ctx}/js/bione/encode.js"></script>
<script type="text/javascript">
var checkBoxId = [];
var isStored=${isStored};
var isChecked="${isChecked}";
var isSuper="${isSuper}";
var rptType="${rptType}";
var state="${state}";
var view=null;
var pageSize=20;
var searchFlag=false;
var searchArgs=[];
require.config({
	paths:{
		"toolbar" : "report/cfg/views/toolbar-rptview"
	}
});
var onToggle = null;
function initFrame() {
	
	var $main = $('#main');
	
	$main.css('top', $('#searchbox').height() +30);
	onToggle = $.Callbacks();
	$('#togglebtn').unbind('click');
	$('#togglebtn').bind('click', {
		flag: true
	}, function(e) {
		if (e.data.flag == true) {
			e.data.flag = false;
			$(this).addClass("togglebtn-down");
			$main.animate({
				top: '22px'
			}, 'fast', function() {
				onToggle.fire();
				if(window.frames["report"])
					window.frames["report"].changeSpread();
			});
		} else {
			e.data.flag = true;
			$(this).removeClass("togglebtn-down");
			$main.animate({
				top:  $('#searchbox').height() + 30
			}, 'fast', function() {
				onToggle.fire();
				if(window.frames["report"])
					window.frames["report"].changeSpread();
			});
		}
	});
}

function greaterThanNow(value){
	var tdate=new Date(value.substring(0,4)+"-"+value.substring(4,6)+"-"+value.substring(6,8)).valueOf()/ (60 * 60 * 24 * 1000);
	var fdate=new Date().valueOf()/ (60 * 60 * 24 * 1000);
	if(tdate-fdate>=0){
		return false;
	}
	return true;
}

function replay(){
	$("#temps").resetForm();
	$("#temps").valid();
}

function createPage(divName, paramtmpId) {
	require.config({
		baseUrl : '${ctx}/js/',
		paths : {
			jquery : 'jquery/jquery-1.7.1.min',
			JSON2 : 'bione/json2.min'
		},
		shim : {
			JSON2 : {
				exports : 'JSON2'
			}
		}
	});
	require([ 'jquery', 'JSON2', '../plugin/js/template/viewMain' ], function() {
		$(function() {
			if (paramtmpId != "") {
				var is
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/report/frame/param/templates/" + paramtmpId
							+ "?type=view",
					dataType : 'json',
					success : function(data) {
						try {
							$('#' + divName).templateView({
								data : JSON2.parse(data.paramJson)
							});
							var params = JSON2.parse(data.paramJson);
							for(var i in params){
								if(params[i].checkbox == "true" || params[i].isMultiSelect == "true"){
									checkBoxId.push(params[i].name);
								}
							}
							
						} catch(e) {
						} finally {
							initFrame();
							$('#temps-loading').css('display', 'none');
						}
					}
				});
			}
		});
	});
}


// 外部集成报表
function f_run(){
	$.metadata.setType("attr", "validate");
	BIONE.validate($("#temps"));
	if($("#temps").valid()){
		var infos=[];
		var dataDate=getDate();
		var unit = "00";
		var searchArgs=$('#temps').getJson();
		for(var i in  searchArgs){
			if(searchArgs[i].name=="tempUnit"){
				unit=searchArgs[i].value;
				searchArgs.splice(i,1);
			}
		}
		var url = '${ctx}/rpt/frame/rptsubmit/dataFillView';
		var html = '<form action="' + url + '" method="post" target="_self" id="postData_form">' +
					'<input id="params" name="params" type="hidden" value="' + htmlAttributeEncode(JSON2.stringify(searchArgs)) + '"/>' +
					'<input id="rptId" name="rptId" type="hidden" value="' + htmlAttributeEncode('${rptId}') + '"/>' +
					'<input id="rptNm" name="rptNm" type="hidden" value="' + htmlAttributeEncode('${rptNm}') + '"/>' +
					'<input id="rptType" name="rptType" type="hidden" value="' + htmlAttributeEncode('${rptType}') + '"/>' +
					'<input id="dataDate" name="dataDate" type="hidden" value="' + htmlAttributeEncode(dataDate) + '"/>' +
					'<input id="unit" name="unit" type="hidden" value="' + htmlAttributeEncode(unit) + '"/>' +
					'</form>';
	    $("#report").remove();
	    $("#frame-wrap").append('<iframe id="report" name="report" src="about:blank" frameborder="0" style="width:99%;height:99%"/> ');
	    document.getElementById('report').contentDocument.write(html);  
		document.getElementById('report').contentDocument.getElementById('postData_form').submit();  
	}
}

$(function() {
	initFrame();
	var w = parent || window;
	//$("#searchbtn").hide();
	/* require(["report/cfg/views/toolbar-rptview" ] , function(Toolbar){
		Toolbar.initToolbar($("#_spreadToolbar") ,"${ctx}");
	}); */
	$('body').prepend('<div id="body-loading" class="l-tab-loading" style="display: none;"></div>');
	createPage("temps", '${paramtmpId}');
	$("input").live("change",function(){$("#temps").valid()});
	$('body').append('<iframe id="download" style="visibility: hidden;" src=""></iframe>');
	initBtn();
});

function initBtn(){
	var btns = [{
		appendTo : "#searchbtn",
		text : '查询',
		width : '50px',
		click: function() {
			f_run();
		}
	},{
		appendTo : "#searchbtn",
		text : '重置',
		width : '50px',
		click: function() {
			replay();
		}
	} ];
	
	for(var i = 0; i < btns.length; i++) {
		BIONE.createButton(btns[i]);
	}
	$('#togglebutton').hide();
}


function getDate(){
	var dataDate="";
	var searchArgs=[];
	var paramValue=$('#temps').getJson();
	for(var i in paramValue){
		if(paramValue[i].name=="DATE" && !paramValue[i].begin && !paramValue[i].end){
			dataDate=paramValue[i].value;
		}
	}
	return dataDate;
}


</script>
</head>
<body>
	<div id="template.form">
		<form id="temps"></form>
	</div>
	<!-- <div id="template.tool">
		<div id='_spreadToolbar' style='width:100%;'></div>
	</div> -->
	<div id="template.frame">
		<div id="frame-wrap" style="height: 100%;">
		</div>
	</div>
</body>
</html>