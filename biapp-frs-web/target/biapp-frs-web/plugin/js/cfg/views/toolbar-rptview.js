/*********************************************************/
/****            报表设计器 - 牛鼻工具栏 = 。 =     ****/
/** **************************************************** */
require.config({
    paths: { 
    	bootstrap : "js/bootstrap/js/bootstrap"
    },
    shim: {
    	exports : 'bootstrap',
    	deps : 'jquery'
    }
});
define(['bootstrap'] , function() {
	
	var Toolbar = {};
	
	Toolbar._spread = {};
	
	Toolbar._Constants = {
			search : "search", //查询
			window : "window", //窗口查询
			replay : "replay", //重置
			excel : "excel",  // 导出Excel
			pdf : "pdf",  // 导出Pdf
			printf : "printf", // 打印
			store : "store", // 收藏
			pagination10 : "pagination10", // 分页
			pagination20 : "pagination20", // 分页
			pagination30 : "pagination30", // 分页
			pagination50 : "pagination50", // 分页
	};
	
	
	/**
	 * 初始化
	 */
	Toolbar.initToolbar = function(toolbarDom,  ctx ) {
		Toolbar._ctx = ctx;
		if(toolbarDom != null
				&& typeof toolbarDom != "undefined"){
			var html = [
			    '<div class="toolbar" style="width:100%;margin-bottom:2px;margin-left:10px;">',
				    '<div class="btn-group">',
						'<button type="button" class="btn btn-default usetooltip clickable" toolbaritem="'+Toolbar._Constants.search+'" data-toggle="tooltip" data-placement="left"  title="查询">',
							'<i class="iconfont icon-chaxun iconfont-lgm" ></i>',
						'</button>'];
			if(state==""){
				html1 = ['<button type="button" class="btn btn-default usetooltip clickable" toolbaritem="'+Toolbar._Constants.window+'" data-toggle="tooltip" data-placement="bottom" title="窗口查询">',
							'<i class="iconfont icon-iconfonticon3 iconfont-lgm"></i>',
							'</button>'];
				html=$.merge(html,html1);
			}
			var html2=['<button type="button" class="btn btn-default usetooltip clickable" toolbaritem="'+Toolbar._Constants.replay+'" data-toggle="tooltip" data-placement="right"  title="重置">',
						'<i class="iconfont icon-zhongzhi iconfont-lgm" ></i>',
						'</button>',
					'</div>',
					'<div class="btn-group">',
						'<button type="button" class="btn btn-default usetooltip clickable" toolbaritem="'+Toolbar._Constants.excel+'" data-toggle="tooltip" data-placement="left"  title="导出Excel">',
							'<i class="iconfont icon-excel iconfont-lgm iconfont-disabled" ></i>',
						'</button>',
						'<button type="button" class="btn btn-default usetooltip clickable" toolbaritem="'+Toolbar._Constants.pdf+'" data-toggle="tooltip" data-placement="bottom" title="导出Pdf">',
							'<i class="iconfont icon-pdf iconfont-lgm iconfont-disabled"></i>',
						'</button>',
						'<button type="button" class="btn btn-default usetooltip clickable" toolbaritem="'+Toolbar._Constants.printf+'" data-toggle="tooltip" data-placement="right"  title="打印">',
						'<i class="iconfont icon-dayin iconfont-lgm iconfont-disabled" ></i>',
						'</button>',
					'</div>',
					'<div class="btn-group">',
					    '<button type="button" class="btn btn-default usetooltip clickable" toolbaritem="'+Toolbar._Constants.store+'" data-toggle="tooltip" data-placement="bottom"  title="'+(isStored==false? '加入收藏':'取消收藏')+'">',
							'<i class="iconfont '+(isStored==false? 'icon-shoucangweishoucang':'icon-shoucangyishoucang')+' iconfont-lgm" ></i>',
						'</button>',
					'</div>'
			];
			html=$.merge(html,html2);
		    if(rptType="01"){
		    	html.push('<div class="btn-group">');
		    	html.push('  <button type="button" class="btn btn-default dropdown-toggle hoverdrop usetooltip"');
		    	html.push('    data-toggle="dropdown" data-hover="dropdown" data-placement="left" title="每页显示数">');
		    	html.push('	   <i class="iconfont icon-fenyefu iconfont-lgm"></i>');
		    	html.push('    <span class="caret"></span>');
		    	html.push('  </button>');
		    	html.push('  <ul class="dropdown-menu text-left" role="menu" style="min-width:110px;">');
		    	html.push('  <li class="clickable" toolbaritem="'+Toolbar._Constants.pagination10+'"><a id="pagination10" style="color:#0007bb;">&nbsp;10</a></li>');
		    	html.push('  <li class="clickable" toolbaritem="'+Toolbar._Constants.pagination20+'"><a id="pagination20">&nbsp;20</a></li>');
		    	html.push('  <li class="clickable" toolbaritem="'+Toolbar._Constants.pagination30+'"><a id="pagination30">&nbsp;30</a></li>');
		    	html.push('  <li class="clickable" toolbaritem="'+Toolbar._Constants.pagination50+'"><a id="pagination50">&nbsp;50</a></li>');
		    	html.push('  </ul>');
		    }
					
			toolbarDom.append(html.join(""));
			// 初始化提示
			$(".usetooltip").tooltip();
			/*// 初始化hover下拉菜单
			$('.hoverdrop').dropdownHover().dropdown();*/
			// 初始化点击
			toolbarDom.delegate(".clickable" , "click" , function(){
				Toolbar._initClickEvents($(this).attr("toolbaritem"));
			});
		}
	}
	
	Toolbar._initClickEvents = function(cmd , e){
	    switch (cmd) {
	    	case Toolbar._Constants.search : 
	    		// 查询
	    		f_run();
                break;
	    	case Toolbar._Constants.window : 
	    		// 窗口查询
	    		f_open();
	    		break;
	    	case Toolbar._Constants.replay :
	    		// 重置
	    		replay();
	    		break;
	    	case Toolbar._Constants.excel :
	    		// 导出Excel
	    		f_export("excel");
	    		break;
	    	case Toolbar._Constants.pdf :
	    		// 导出Pdf
	    		f_export("pdf");
	    		break;
	    	case Toolbar._Constants.printf :
	    		// 打印
	    		printPdf();
	    		break;
	    	case Toolbar._Constants.store :
	    		store();
	    		break;
	    	case Toolbar._Constants.pagination10 :
	    		pagination(10);
	    		break;
	    	case Toolbar._Constants.pagination20 :
	    		pagination(20);
	    		break;
	    	case Toolbar._Constants.pagination30 :
	    		pagination(30);
	    		break;
	    	case Toolbar._Constants.pagination50 :
	    		pagination(50);
	    		break;
	    		
	    	
	    }
	}


	return Toolbar;
	
})