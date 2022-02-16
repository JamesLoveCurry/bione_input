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
    
    /**
     * 心跳包，保持会话不超时
     */
    UDIP.heart = function(url){
    	$.ajax({
    		async : true,
    		url : url,
    		dataType : 'text',
    		type : "post"
    	});
    	window.setTimeout('UDIP.heart(\''+url+'\')',25*60*1000);//会话超时时间为30分钟,每隔25分钟心跳一次.
    };
    
})(jQuery);