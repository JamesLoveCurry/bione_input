<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<style type="text/css">
html, body {
	height: 100%;
	background-color: white;
}
html {
	font-size: 62.5%;
}
body {
	font-size: 1.2m;
}
html, body, div, table, td, tr, ul, li, iframe {
	margin: 0;
	padding: 0;
	border: 0;
	outline: 0;
}
#banner {
	height: 30px;
	padding: 0 2px;
}
#banner .line {
	height: 29px;
	border-bottom: 1px solid #CCC;
	overflow: hidden;
}
#container {
	position: absolute;
	width: 100%;
	top: 30px;
	bottom: 30px;
}
#container iframe {
	position: absolute;
	width: 100%;
	height: 100%;
}
#footer {
	position: absolute;
	width: 100%;
	height: 29px;
	bottom: 0px;
	border-top: 1px solid #CCC;
}
.label {
	color: #BBB;
	border-bottom: 2px solid #FFF;
	line-height: 20px;
	height: 20px;
	margin: 3px 5px 4px 5px;
	padding: 0 5px;
	float: left;
	cursor: default;
}
.wrap {
	position: absolute;
	width: 100%;
	top: 0;
	bottom: 0;
}
.label.curr {
	border-bottom: 2px solid #49E;
	color: #49E;
}
.label.fini {
	border-bottom: 2px solid #FFF;
	color: #8EC2F5;
}
.btn {
	margin: 2px 5px 3px 5px;
	border: 1px solid #CCC;
	float: right;
	height: 20px;
	padding: 1px 5px;
	cursor: pointer;
	border-radius: 2px;
	line-height: 20px;
}
.btn:hover {
	box-shadow: 0 1px 1px rgba(0, 0, 0, 0.15);
}
.wrap.hidden {
	display: none;
}
</style>
<script type="text/javascript">
var wizard=null;
var validateData=null;
var makeWay="0";//默认保留
(function($){
	var Base = function(options) {
		this.options = options;
		this.events = {};
		this.build();
	}
	$.extend(Base.prototype, {
		// 初始化
		build: function() {
			var options = this.options,
			target = this;
			if (options) {
				var pn = "",
				name = "";
				$.each(options, function(k, v) {
					if (k.indexOf('on') == 0) {
						name = k.substr(2);
						pn = name.substr(0, 1).toLowerCase() + name.substr(1);
						target.bind(pn, v);
					}
				});
			}
		},
		// 绑定触发器
		bind: function(arg, handler) {
			if (typeof arg == "string") {
				var cal = this.events[arg];
				if (!cal) {
					cal = $.Callbacks();
					this.events[arg] = cal;
				}
				cal.add(handler);
			}
		},
		// 触发
		trigger: function(arg, data) {
			var cal = this.events[arg];
			if (cal) {
				cal.fire(data);
			}
		},
		// 解除绑定
		unbind: function(arg, handler) {
			var cal = this.events[arg];
			if (cal) {
				if (handler) {
					cal.remove(handler);
				} else {
					cal.empty();
				}
			}
		}
	});
	var Wizard = function(params) {
		Base.call(this, params);
		this.items = [];
		this.step = -1;
		this.idMap = {};
		this.init();
	};
	Wizard.prototype = new Base();
	$.extend(Wizard.prototype, {
		init: function() {
			var p = this.options;
			var items = this.items,
			idMap = this.idMap;
			try {
				$.each(p.steps, function(i, n) {
					if (n.id) {
						idMap[n.id] = n;
					}
					var item = {};
					item.setting = n;
					item.title = $('<div class="label"></div>').attr({
						id: 'title_' + n.id
					});
					item.title.addClass('label');
					item.title.text(n.title);
					$('#banner .line').append(item.title);
					item.content = $('<div class="wrap hidden"><iframe frameborder="0"></iframe></div>');
					$('#container').append(item.content);
					items.push(item);
				});
				this.next();
			} catch(e) {
				return;
			}
		},
		buildBtn: function(btns) {
			$('#footer').empty();
			var btn = null;
			var target = this;
			$.each(btns, function(i, n) {
				btn = $('<div class="btn"></div>');
				if ('next' == n.type) {
					btn.text(n.text || '下一步');
					btn.bind('click', n, function(){
						target.next(n.click);
					});
				} else
				if ('pre' == n.type) {
					btn.text(n.text || '上一步');
					btn.bind('click', n, function(){
						target.pre(n.click);
					});
				} else {
					btn.text(n.text || '完成');
					btn.bind('click', n, n.click);
				}
				btn.prependTo('#footer');
			});
		},
		next: function(callback) {
			if ($.type(callback) == 'function' && callback() == false) {
				return;
			}
			var next = this.step + 1;
			if (next < this.items.length) {
				if (this.step > -1) {
					var curr = this.items[this.step];
					curr.title.removeClass('curr');
					curr.title.addClass('fini');
				}
				curr = this.items[next];
				curr.title.removeClass('fini');
				curr.title.addClass('curr');
				$('#container .wrap').addClass('hidden');
				curr.content.removeClass('hidden');
				var fra = curr.content.find('iframe');
				if (!fra.attr('src')) {
					fra.attr('src',curr.setting.src);
				}
				this.buildBtn(curr.setting.buttons);
				this.step++;
			}
		},
		pre: function(callback) {
			if ($.type(callback) == 'function' && callback() == false) {
				return;
			}
			var pre = this.step - 1;
			if (pre > -1) {
				var curr = this.items[this.step];
				curr.title.removeClass('curr');
				curr.title.addClass('fini');
				curr = this.items[pre];
				curr.title.removeClass('fini');
				curr.title.addClass('curr');
				$('#container .wrap').addClass('hidden');
				curr.content.removeClass('hidden');
				this.buildBtn(curr.setting.buttons);
				this.step--;
			}
		},
		getWindow: function(s) {
			if(s==null||s=="")
				s=this.step;
			try{
				return this.items[s].content.find('iframe')[0].contentWindow;
			}
			catch(e){
				return null;
			}
			
		}
	});
	$.extend({
		exWizard: function(params) {
			return new Wizard(params);
		}
	});
})(jQuery);
/**
 * 点击导入获得的弹框
 */
var type="${type}";//获取控制器传入的导入类型参数
var tab=[];
var wizard = null;
$(function(){
	initData();//加载弹框的内容
	wizard=$.exWizard({
		steps: tab
	});
});
function initData(){
	switch(type){
		case 'PbcIJFile':
			tab=[{
				id: 'fileup',
				title: '文件上传',
				src: '${ctx}/report/frame/wizard/fileup?type=${type}&d='+new Date().getTime(),
				buttons: [{
					type: 'next',
					click: function(){
						var win=wizard.getWindow();
						if(!win.validate()){
							return false;
						}else{
							win=wizard.getWindow(1);
							if(win!=null){
								try{
									win.loadData();
								}catch(e){
									
								}
							}
						}
					}
				}]
			}, {
				id: 'validate',
				title: '校验结果',
				src: '${ctx}/report/frame/wizard/validate?type=${type}&d='+new Date().getTime(),
				buttons: [{
					type: 'pre',
					click: function() {
					}
				}, {
	// 				text: '',
					type: 'finish',// next/pre/finish
					click: function(){
						var win=wizard.getWindow();
						if(!win.validate()){
							return false;
						}
						$.ligerDialog.confirm("请定时备份数据，此操作将覆盖原报表数据！<br/>是否保存数据？", function(yes) {
							if (yes) {
								saveInfo();
							}
						});	
					}
				}]
			}]
			break;
		case 'PisaIdx':
		case 'PisaInOut':
		case 'PbcCfg':
			tab=[{
				id: 'fileup',
				title: 'Excel文件上传',
				src: '${ctx}/report/frame/wizard/fileup?type=${type}&d='+new Date().getTime(),
				buttons: [{
	// 				text: '',
					type: 'next',// next/pre/finish
					click: function(){
						var win=wizard.getWindow();
						if(!win.validate()){
							return false;
						}
						else{
							win=wizard.getWindow(1);
							if(win!=null){
								try{
									win.loadData();
								}
								catch(e){
									
								}
							}
						}
					}
				}]
			}, {
				id: 'validate',
				title: 'Excel校验结果',
				src: '${ctx}/report/frame/wizard/validate?type=${type}&d='+new Date().getTime(),
				buttons: [{
					type: 'pre',
					click: function() {
					}
				}, {
	// 				text: '',
					type: 'finish',// next/pre/finish
					click: function(){
						var win=wizard.getWindow();
						if(!win.validate()){
							return false;
						}
						$.ligerDialog.confirm("请定时备份数据，此操作将覆盖原报表配置信息，是否继续？", function(yes) {
							if (yes) {
								saveInfo();
							}
						});	
					}
				}]
			}]
			break;
		case 'Report':
		case 'Dim':
		case 'Division':
		case 'Error':
		case 'Rule':
		case 'Org':
		case 'BioneOrg':
		case 'User':
		case 'Compgrp':
		case 'Similar':
		case 'Idxplanval':
		case 'Idxcheck':
		case 'Label':
		case 'Trans':
			tab=[{
				id: 'fileup',
				title: '模板文件上传',
				src: '${ctx}/report/frame/wizard/fileup?type=${type}&d='+new Date().getTime(),
				buttons: [{
//	 				text: '',
					type: 'next',// next/pre/finish
					click: function(){
						var win=wizard.getWindow();
						if(!win.validate()){
							return false;
						}
						else{
							win=wizard.getWindow(1);
							if(win!=null){
								try{
									win.loadData();
								}
								catch(e){
									
								}
							}
						}
					}
				}]
			}, {
				id: 'validate',
				title: '校验结果查询',
				src: '${ctx}/report/frame/wizard/validate?type=${type}&d='+new Date().getTime(),
				buttons: [{
					type: 'pre',
					click: function() {
					}
				}, {
//	 				text: '',
					type: 'finish',// next/pre/finish
					click: function(){
						var win=wizard.getWindow();
						var errorLevel = win.validate();
						if(errorLevel == "02"){
							return false;
						}else if(errorLevel == "01"){
				            $.ligerDialog.confirm("存在校验警告，是否保存？", function(yes) {
				            	if(yes){
				            		saveInfo();
				            	}
				            });
						}else{
							saveInfo();
						}
					}
				}]
			}]
			break;
		case 'Rptrel':
		case 'Model':
		case 'Index':
			tab=[{
				id: 'source',
				title: '数据源配置',
				src: '${ctx}/report/frame/wizard/source',
				buttons: [{
//	 				text: '',
					type: 'next',// next/pre/finish
					click: function(){
						window.dsId=wizard.getWindow().getDsId();
						if(dsId==null||dsId==""){
							BIONE.tip("请选择数据源");
							return false;
						}
						
					}
				}]
			}, {
				id: 'fileup',
				title: '模板文件上传',
				src: '${ctx}/report/frame/wizard/fileup?type=${type}&d='+new Date().getTime(),
				buttons: [{
					type: 'pre',
					click: function() {
					}
				}, {
//	 				text: '',
					type: 'next',// next/pre/finish
					click: function(){
						var win=wizard.getWindow();
						if(!win.validate()){
							return false;
						}
						else{
							win=wizard.getWindow(2);
							if(win!=null){
								try{
									win.loadData();
								}
								catch(e){
									
								}
							}
						}
					}
				}]
			}, {
				id: 'validate',
				title: '校验结果查询',
				src: '${ctx}/report/frame/wizard/validate?type=${type}&d='+new Date().getTime(),
				buttons: [{
					type: 'pre',
					click: function() {
					}
				}, {
//	 				text: '',
					type: 'finish',// next/pre/finish
					click: function(){
						var win=wizard.getWindow();
						var errorLevel = win.validate();
						if(errorLevel == "02"){
							return false;
						}else if(errorLevel == "01"){
				            $.ligerDialog.confirm("存在校验警告，是否保存？", function(yes) {
				            	if(yes){
				            		saveInfo();
				            	}
				            });
						}else{
							saveInfo();
						}
					}
				}]
			}]
			break;
		case 'RptBank':
		case 'InterBank':
			window.dsId=parent.themeId;
			tab=[{
				id: 'fileup',
				title: '模板文件上传',
				src: '${ctx}/report/frame/wizard/fileup?type=${type}&d='+new Date().getTime(),
				buttons: [{
					type: 'next',
					click: function(){
						var win=wizard.getWindow();
						if(!win.validate()){
							return false;
						}
						else{
							win=wizard.getWindow(1);
							if(win!=null){
								try{
									win.loadData();
								}
								catch(e){
									
								}
							}
						}
					}
				}]
			}, {
				id: 'validate',
				title: '校验结果查询',
				src: '${ctx}/report/frame/wizard/validate?type=${type}&d='+new Date().getTime(),
				buttons: [{
					type: 'pre',
					click: function() {
					}
				}, {
					type: 'finish',
					click: function(){
						var win=wizard.getWindow();
						var errorLevel = win.validate();
						if(errorLevel == "02"){
							return false;
						}else if(errorLevel == "01"){
				            $.ligerDialog.confirm("存在校验警告，是否保存？", function(yes) {
				            	if(yes){
				            		saveInfo();
				            	}
				            });
						}else{
							saveInfo();
						}
					}
				}]
			}]
			break;
		case 'Generalize':
			tab=[{
				id: 'fileup',
				title: '指标目录选择',
				src: '${ctx}/report/frame/generalize/selectCatalog?d='+new Date().getTime(),
				buttons: [{
					type: 'next',
					click: function(){
						window.selectCatalogNo = wizard.getWindow().selectCatalogNo;
						if(!window.selectCatalogNo){
							BIONE.tip("未选择指标存放目录！");
							return false;
						}
					}
				}]
			},{
				id: 'fileup',
				title: '模板文件上传',
				src: '${ctx}/report/frame/wizard/fileup?type=${type}&d='+new Date().getTime(),
				buttons: [{
					type: 'pre',
					click: function() {
					}
				},{
					type: 'next',
					click: function(){
						var win=wizard.getWindow();
						if(!win.validate()){
							return false;
						}
						else{
							win=wizard.getWindow(1);
							if(win!=null){
								try{
									win.loadData();
								}
								catch(e){
									
								}
							}
						}
					}
				}]
			}, {
				id: 'validate',
				title: '校验结果查询',
				src: '${ctx}/report/frame/wizard/validate?type=${type}&d='+new Date().getTime(),
				buttons: [{
					type: 'pre',
					click: function() {
					}
				}, {
					type: 'finish',
					click: function(){
						window.dsId = window.selectCatalogNo + "@" + makeWay + "@" + parent.modelEnNm;
						var win=wizard.getWindow();
						var errorLevel = win.validate();
						if(errorLevel == "02"){
							return false;
						}else if(errorLevel == "01"){
				            $.ligerDialog.confirm("存在校验警告，是否保存？", function(yes) {
				            	if(yes){
				            		saveInfo();
				            	}
				            });
						}else{
							saveInfo();
						}
					}
				}]
			}]
			break;
	}
}

function saveInfo(){
	if(window.ehcacheId!=null&&window.ehcacheId!=""){
		if(window.dsId==null)
			window.dsId="";
		$.ajax({
			async : true,
			cache : false,
			type : "POST",
			dataType : "JSON",
			contentType : "application/json",
			url :"${ctx}/report/frame/wizard/save?type=${type}&ehcacheId="+ehcacheId+"&dsId="+window.dsId,
			beforeSend : function(a, b, c) {
				BIONE.showLoading('正在导入数据中...');
			},
			success : function(data) {
				BIONE.hideLoading();
				if(!data.isSuccess){
					$.ligerDialog.confirm("导入失败! <br/>是否关闭窗口？", function(yes) {
		            	if(yes){
		            		BIONE.closeDialog("importWin");
		            	}
		            });
				}else{
					parent.BIONE.tip("导入成功");
					window.parent.reload();
					BIONE.closeDialog("importWin");
				}
			},
			error: function(){
				BIONE.hideLoading();
			}
		});
	}
	else{
		BIONE.tip("未选择上传文件无法导入");
	}
	
}
</script>
</head>
<body>
	<div id="banner">
		<div class="line"></div>
	</div>
	<div id="container"></div>
	<div id="footer"></div>
</body>
</html>