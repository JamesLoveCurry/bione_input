/*******************************************************/
/****             BIONE(基础应用平台)公用JS文件           ****/
/****         维护具有公用价值的JS代码，比如校验、Ajax调用等    ****/
/******************************************************/

/**
 * 根据URL请求内容，根据返回的内容更新页面指定DIV
 * 
 * url:请求的资源路径 divId：需要被更新的DIV的id showTip:操作成功后是否在右下角提示 默认false
 */

(function($) {
	// 全局系统对象
	window['UDIP'] = {};
	/**
	 * 打开一个固定高宽的弹出窗口(PAGE)
	 * 
	 * @param title
	 *            窗口标题
	 * @param name
	 *            窗口名称/Id
	 * @param url
	 *            url
	 * @param beforeClose
	 *            关闭dialog调用函数
	 * @param buttons
	 *            按钮集合
	 * @returns
	 */
	UDIP.commonOpenLargeDialog = function commonOpenLargeDialog(title, name,
			url, beforeClose) {
		if(url.indexOf('=')!=-1&&url.indexOf('?')!=-1){
			url = url + "&d="+new Date().getTime();
		}else{
			url = url + "?d="+new Date().getTime();
		}
		var width = 800;
		var height = 510;
		width = (document.body.scrollWidth);
		height = (document.body.scrollHeight);
		var _dialog = $.ligerui.get(name);
		if (_dialog) {
			$.ligerui.remove(name);
		}
		_dialog = $.ligerDialog.open({
			height : height,
			width : width,
			url : url,
			name : name,
			id : name,
			title : title,
			isResize : true,
			isDrag : true,
			isHidden : false
		});
		if(beforeClose!=null &&
				typeof beforeClose == "function"){
			_dialog._beforeClose = beforeClose;
		}
		return _dialog;
	};
	
	
	UDIP.commonOpenDialog = function commonOpenDialog(cogfig) {
		if(cogfig.url.indexOf('=')!=-1&&cogfig.url.indexOf('?')!=-1){
			cogfig.url = cogfig.url + "&d="+new Date().getTime();
		}else{
			cogfig.url = cogfig.url + "?d="+new Date().getTime();
		}
		var _dialog = $.ligerui.get(cogfig.name);
		if (_dialog) {
			$.ligerui.remove(cogfig.name);
		}
		cogfig.width = cogfig.width||(document.body.scrollWidth);
		cogfig.height = cogfig.height||(document.body.scrollHeight);
		_dialog = $.ligerDialog.open({
			height : cogfig.height,
			width : cogfig.width,
			url : cogfig.url||"",
			name : cogfig.name||"dialogModel",
			id : cogfig.name||"dialogModel",
			title : cogfig.title||"标题",
			modal : cogfig.modal,
			// xugy modified begin
			comboxName : cogfig.comboxName||null,
			// modified finished
			isResize : cogfig.isResize||true,
			isDrag : cogfig.isDrag||true,
			isHidden : cogfig.isHidden||false
		});
		if(cogfig.beforeClose!=null && 
				typeof cogfig.beforeClose == "function"){
			_dialog.beforeClose = cogfig.beforeClose;
		}
		return _dialog;

	};
	
	// 创建表单搜索按钮：搜索、高级搜索
	UDIP.addSearchButtons = function(form, btnContainer) {
		if (!form)
			return;
		form = $(form);
		BIONE.addFormButtons(btnContainer);
	};

	// 增加表单底部按钮,比如：保存、取消
	BIONE.addFormButtons = function(buttons) {
		if (!buttons)
			return;
		var formbar = $("div.form-bar");
		if (formbar.length == 0)
			formbar = $(
					'<div class="form-bar"><div class="form-bar-inner"></div></div>')
					.appendTo('body');
		if (!(buttons instanceof Array)) {
			buttons = [ buttons ];
		}
		$(buttons)
				.each(
						function(i, o) {
							var btn = $('<div class="l-dialog-btn"><div class="l-dialog-btn-l"></div><div class="l-dialog-btn-r"></div><div class="l-dialog-btn-inner"></div></div> ');
							$("div.l-dialog-btn-inner:first", btn).html(
									o.text || "BUTTON");
							if (o.onclick) {
								btn.bind('click', function() {
									o.onclick(o);
								});
							}
							if (o.width) {
								btn.width(o.width);
							}
							$("> div:first", formbar).append(btn);
						});
	};
	
	/*
	 * modify by xugy 2012-08-22
	 */
	// 弹出窗口公共函数
	UDIP.commonOpenFullDialog = function commonOpenFullDialog(title, name, url, comboxName, beforeClose) {

		if(url.indexOf('=')!=-1&&url.indexOf('?')!=-1){
			url = url + "&d="+new Date().getTime();
		}else{
			url = url + "?d="+new Date().getTime();
		}
		
		//获取当前窗口的长、宽
		var height=document.body.scrollHeight;
    	var width=document.body.scrollWidth;
    	
		var _dialog = $.ligerui.get(name);
		if (_dialog) {
			$.ligerui.remove(name);
		}
		_dialog = $.ligerDialog.open({
			height : height,
			width : width,
			url : url,
			name : name,
			id : name,
			title : title,
			// xugy modified begin
			comboxName : comboxName,
			// modified finished
			isResize : false,
			isDrag : false,
			isHidden : false
		});
		if(beforeClose!=null && 
				typeof beforeClose == "function"){
			_dialog.beforeClose = beforeClose;
		}
		return _dialog;

	};
	
	/**
	**@param treeObj 树对象
	**@param treeId  树对象所在容器
	**@param nodeId  要显示的节点
	**/
	UDIP.showTreeById = function(treeObj,treeId,nodeId){
		var root = treeObj.getNodeByParam("id", nodeId, null);
		var setting = treeObj.setting ;
		
		treeObj = $.fn.zTree.init($("#"+treeId),setting);
		treeObj.addNodes(null, root, false);
		return treeObj;
	};
	
	/**
	**@param treeObj 树对象
	**@param treeId  树对象所在容器
	**@param nodeId  要显示的节点
	**/
	UDIP.findTreeNodeById = function(all,nodeId){
		for(var j=0;j<all.length;j++){
			var n = all[j];
			if(n.id==nodeId){
				return n;
			}
		}
	};
	
	
	UDIP.onCheckRow = function(g,checked, rowindex){
		if(checked){
			for (var rowid in g.records){
				if(rowindex==rowid)
					continue ;
				g.unselect(g.records[rowid]);
	        }
		}
	};
	UDIP.onCheckAllRow = function(g){
		for (var rowid in g.records){
			g.unselect(g.records[rowid]);
        }
	};
	
	UDIP.selectText = function(ele,start, end){
        var v = ele.value;
        var doFocus = false;
        if(v.length > 0){
            start = start === undefined ? 0 : start;
            end = end === undefined ? v.length : end;
            var d = ele;
            if(d.setSelectionRange){
                d.setSelectionRange(start, end);
            }else if(d.createTextRange){
                var range = d.createTextRange();
                range.moveStart('character', start);
                range.moveEnd('character', end-v.length);
                range.select();
            }
            doFocus = false;
        }else{
            doFocus = true;
        }
        if(doFocus){
            ele.focus();
        }
    };
    
    UDIP.heart = function(url){//心跳包，保持会话不超时
    	$.ajax({
    		async : true,
    		url : url,
    		dataType : 'text',
    		type : "post"
    	});
    	window.setTimeout('UDIP.heart(\''+url+'\')',5*1000);//会话超时时间为30分钟,每隔25分钟心跳一次.
    };
    
})(jQuery);