// version: 0.0.1 
// author: donglt
'use strict';

(function($) {
  var TimeFn = null;
  function DupontChart(content, config) {
    var defaultConfig = {
      lineWidth : 50, //线宽
      lineColor : "#495463",
      sharpWidth : 100, //形状宽度
      sharpHeight : 20,//形状高度
      sharpColor : "#1360B2",
      sharpFColor : "#56A2F5",
      textHeight : 20,//文字高度
      slice : 10,//间隔距离
      url : "", //获取路径
      params : {}, //传递参数
      data : null, //杜邦数据
      dataunit : null,//数据单位,
      showModel: "percent",
      callback :{
    	  onClick : null,
    	  onDblClick : null,
    	  onBeforeExpand : null
      }
    };
    this.config = $.extend({}, defaultConfig, config || {});
    this.content = content;
    this._render(content);
    this._attachEvent(content);
  }


  DupontChart.prototype = {
	_render: function(target) {
    	var $content = $('<div class="dupont-content" style="position:relative;width:100%;top:10px;"></div>').css("overflow","auto");
    	$content.css("height",target.height()-5);
    	this.content = $content;
    	target.append($content);	   
		this.loadData();
    },
    _attachEvent: function(target) {
    	var _this = this;
    	$(document).on("click",".dupont-sharp-chart",function(){
    		var dataNode = $(this).data("data");
    		var config = _this.config;
    		if(typeof config.callback.onClick == "function"){
    			clearTimeout(TimeFn); 
    			//执行延时 
    			TimeFn = setTimeout(function(){ 
    				config.callback.onClick(dataNode);
    			},300); 
    		}
    	});
    	$(document).on("dblclick",".dupont-sharp-chart",function(){
    		var dataNode = $(this).data("data");
    		var config = _this.config;
    		if(typeof config.callback.onDblClick  == "function"){
    			clearTimeout(TimeFn); 
				config.callback.onDblClick(dataNode);
			}
    	});
    },
    _init : function(){
    	var content = this.content;
    	var config = this.config;
    	var params = {
				top : 0,
				level : 0,
				dom : content
		};
    	content.children().remove();
		this.initChart(config.data,config.dataunit,params,config);
    },
    initChart : function(dataNode,dataunit,params,config){
		var info = {};
		var bottom = params.top;
		var ltop = params.top;
		var top = params.top;
		var fvTop = 0;
		var evTop = 0;
		var vHeight = 0;
		var cflag = false; // 是否有子节点标志
		var level = params.level;
		var $dom = params.dom;
		var btnWidth = 18;
		if (dataNode.children && dataNode.children.length > 0
				&& dataNode.isExpand) {
			var $divChild = $("<div id='" + dataNode.id + "child'></div>"); // 子节点dom
			$dom.append($divChild);
			cflag = true;
			var clevel = params.level + 1;
			var ltop = 0;
			for ( var i in dataNode.children) {
				var params = {
					top : bottom,
					level : clevel,
					dom : $divChild
				};
				var a = this.initChart(dataNode.children[i],dataunit, params, config);
				bottom = a.bottom;
				if (i == 0) {
					fvTop = a.ltop
				}
				if (i == dataNode.children.length - 1) {
					evTop = a.ltop
				}
				var ltop = (evTop + fvTop) / 2;
				vHeight = evTop - fvTop;
			}
			info = {
				ltop : ltop,
				bottom : bottom + config.slice
			}
		} else {
			bottom = top + config.sharpHeight + config.textHeight * 2
					+ config.slice;
			ltop = (bottom + top) / 2;
			info = {
				ltop : ltop,
				bottom : bottom
			}
		}
		var left = config.lineWidth;
		if (level != 0) {
			left = level * (config.lineWidth * 2 + config.sharpWidth+btnWidth);
		}
		var sleft = left;
		if (level != 0) {
			var $frontLine = $('<div class= "dupont-horizontal-line"></div>');
			$frontLine.css("left", sleft + "px").css("top", ltop + "px").css(
					"width", config.lineWidth + "px").css("background-color",
					config.lineColor);
			$dom.append($frontLine);
			sleft += config.lineWidth;
		}
		var stop = ltop - (config.textHeight + config.sharpHeight / 2);
		
		var sharpFColor = config.sharpFColor;
		if(dataNode.color){
			sharpFColor = dataNode.color;
		}
		if(config.showModel == "percent"){
			var $topText = $('<div class = "dupont-top-text" "></div>');
			$topText.css("height", config.textHeight + "px").css("line-height",
					config.textHeight + "px").css("position", "absolute").css(
					"left", (sleft - config.lineWidth) + "px").css("top",
					stop + "px").css("width",
					(config.lineWidth * 2 + config.sharpWidth) + "px");
			$topText.html(dataNode.text);
			$dom.append($topText);
			var $sharp = $('<div class="dupont-sharp"></div>');
			$sharp.attr("id", dataNode.id);
			$sharp.css("left", sleft + "px").css("width", config.sharpWidth + "px")
					.css("top", (stop + config.textHeight) + "px").css("height",
							(config.textHeight + config.sharpHeight) + "px").css(
							"position", "absolute");
			var $sharpChart = $('<div class = "dupont-sharp-chart" ></div>')
			$sharpChart.data("data",dataNode);
			$sharpChart.css("height", config.sharpHeight + "px").css("background-color", config.sharpFColor);
			var $sharpPercentDiv = $('<div class="dupont-sharp-div"></div>')
			$sharpPercentDiv.css("background-color", config.sharpColor).css(
					"width", dataNode.percent + "%").css("height", "100%");
			var $sharpPercentSpan = $('<div class="dupont-sharp-span"></div>');
			$sharpPercentSpan.css("position","relative").css("top",-(config.sharpHeight-2)+"px");
			$sharpPercentSpan.html(dataNode.percent + "%");
			$sharpChart.append($sharpPercentDiv);
			$sharpChart.append($sharpPercentSpan);
			$sharp.append($sharpChart);
			$dom.append($sharp);
			var $bottomText = $('<div class = "dupont-bottom-text" ></div>')
			$bottomText.css("height", config.textHeight + "px").css("line-height",
					config.textHeight + "px").css(
							"left", (sleft - config.lineWidth) + "px").css("top",
									(stop+config.textHeight+config.sharpHeight) + "px").css("width",
									(config.lineWidth * 2 + config.sharpWidth) + "px").css("position", "absolute");
			if(dataunit){
				$bottomText.html(dataNode.value + dataunit);
			}else{
				$bottomText.html(dataNode.value);
			}
			$dom.append($bottomText);
		}
		
		if(config.showModel == "text"){
			var $topText = $('<div class = "dupont-top-text dupont-sharp-chart"></div>');
			$topText.data("data",dataNode);
			$topText.css("height", (config.textHeight*2+config.sharpHeight) + "px").css("line-height",
					(config.textHeight*2+config.sharpHeight) + "px").css("position", "absolute").css(
					"left", sleft + "px").css("top",
					stop + "px").css("width",config.sharpWidth + "px").css("color","#fff").css("background-color",sharpFColor).css("border-radius","8px").css("cursor","pointer").attr("title",dataNode.text);
			$topText.html(dataNode.text);
			$dom.append($topText);
		}
		
		if(config.showModel == "data"){
			var $topText = $('<div class = "dupont-top-text dupont-sharp-chart"></div>');
			$topText.data("data",dataNode);
			$topText.css("height", (config.textHeight*2+config.sharpHeight) + "px").css("line-height",
					(config.textHeight*2+config.sharpHeight) + "px").css("position", "absolute").css(
					"left", sleft + "px").css("top",
					stop + "px").css("width",config.sharpWidth + "px").css("color","#fff").css("background-color",sharpFColor).css("border-radius","8px").css("cursor","pointer").attr("title",dataNode.text);
			$topText.html(dataNode.text);
			$topText.attr("id", dataNode.id);
			$dom.append($topText);
			var $bottomText = $('<div class = "dupont-bottom-text" ></div>')
			$bottomText.css("height", config.textHeight + "px").css("line-height",
					config.textHeight*3 + "px").css(
							"left", (sleft - config.lineWidth) + "px").css("top",
									(stop+config.textHeight+config.sharpHeight) + "px").css("width",
									(config.lineWidth * 2 + config.sharpWidth) + "px").css("position", "absolute");
			if(dataNode.unit){
				$bottomText.html(dataNode.value + dataNode.unit);
			}else if(dataunit){
				$bottomText.html(dataNode.value + dataunit);
			}else{
				$bottomText.html(dataNode.value);
			}
			$dom.append($bottomText);
		}
		
		if(dataNode.children && dataNode.children.length > 0){
			var eleft = left + config.sharpWidth;
			if (level != 0) {
				eleft = eleft + config.lineWidth;
			}
			var $expandBtn = $('<div><a class="expand-flag-btn navbar-minimalize minimalize-styl-3" ><i class="fa"></i> </a></div>');
			$expandBtn.find("a").data("data",dataNode);
			var _this = this;
			$expandBtn.on("click",function(){
	    		var dataNode = $(this).find("a").data("data");
	    		var expandFlag = true;
	    		if(typeof config.callback.onBeforeExpand == "function"){
	    			expandFlag = config.callback.onBeforeExpand(dataNode);
	    		}
	    		if(!(expandFlag == false)){
	    			dataNode.isExpand = !dataNode.isExpand ;
	    			_this._init();
	    		}
	    	});
			$expandBtn.find("a").css("background-color",sharpFColor);
			$expandBtn.css("left", (eleft +1)+ "px").css("top", (ltop-9.5) + "px").css("position","absolute").css("line-height","16px");
			$expandBtn.find("i").addClass(dataNode.isExpand?"fa-minus":"fa-plus").css("margin-top","2px");
			$dom.append($expandBtn);
		}
		if (cflag) {
			var eleft = left + config.sharpWidth;
			if (level != 0) {
				eleft = eleft + config.lineWidth;
			}
			var $afterLine = $('<div class= "dupont-horizontal-line behind-line"></div>');
			$afterLine.css("left", (eleft+btnWidth) + "px").css("top", ltop + "px").css(
					"width", config.lineWidth + "px").css("background-color",
					config.lineColor);
			$divChild.append($afterLine);
			var vleft = eleft + btnWidth + config.lineWidth;
			var $verticalLine = $('<div class= "dupont-vertical-line"></div>');
			$verticalLine.css("left", vleft + "px").css("top", fvTop + "px")
					.css("height", vHeight + "px").css("background-color",config.lineColor);
			$divChild.append($verticalLine);
		}
		return info;
    },
    setData : function(data,dataunit){
    	var config =  this.config;
    	config.data = data;
    	config.dataunit = dataunit;
    },
    expandAll : function(flag){
    	var config =  this.config;
    	var data = config.data;
    	data.isExpand = flag;
    	if(data.children && data.children.length > 0){
    		this._expandAll(flag,data.children);
    	}
    	this._init();
    },
    _expandAll : function(flag,datas){
    	for(var i in datas){
    		datas[i].isExpand = flag;
    		if(datas[i].children && datas[i].children.length > 0){
    			this._expandAll(flag,datas[i].children);
    		}
    	}
    },
    loadData: function(){
    	var config =  this.config;
    	var content = this.content;
    	if(config.url != null && config.url != ""){
    		$.ajax({
    			cache : false,
    			async : true,
    			url : config.url,
    			dataType : 'json',
    			data: config.params,
    			type : "post",
    			beforeSend : function() {
    				
    			},
    			complete : function(result) {
    				
    			},
    			success : function(result) {
    				config.data = result;
    				var params = {
    					top : 0,
    					level : 0,
    					dom : content
    				};
    				this.initChart(config.data,config.dataunit,params,config);
    			},
    			error : function(result, b) {
    			}
    		});
    	}
    	else{
    		if(config.data != null){
    			var params = {
    					top : 0,
    					level : 0,
    					dom : content
    				};
    			this.initChart(config.data,config.dataunit,params,config);
    		}
    	}
    }
  };

  $.fn.extend({
    dupontChart: function(arg0, arg1) {
      var dupontChart = this.data('dupontChart');
      if (!dupontChart) {
    	  dupontChart = new DupontChart($(this), arg0);
        this.data('dupontChart', dupontChart);
      } else if (exlabel[arg0] !== undefined) {
        return exlabel[arg0](arg1);
      }
      return dupontChart;
    }
  });
})(jQuery);
