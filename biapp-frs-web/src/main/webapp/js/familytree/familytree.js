// version: 0.0.1 
'use strict';

(function($) {
	var TimeFn = null; 
	function clone(obj) {
		var o;
		if (typeof obj == "object") {
			if (obj === null) {
				o = null;
			} else {
				if (obj instanceof Array) {
					o = [];
					for (var i = 0, len = obj.length; i < len; i++) {
						o.push(clone(obj[i]));
					}
				} else {
					o = {};
					for (var j in obj) {
						o[j] = clone(obj[j]);
					}
				}
			}
		} else {
			o = obj;
		}
		return o;
	}
  function FamilyTree(content, config) {
    var initConfig = {
      async : false,
      pAdd : false,
      data :[],
      callback: {
    	onAsync: null,
        onClick: null,
        onDbClick: null
      }
    };
    this.config = $.extend({}, initConfig, config || {});
    this.init(content);
    this.addEvent(content);
    this.render(this._content,this.config.data,true);
    this.resetWidth();
  }

  FamilyTree.prototype = {
    init: function(target) {
      target.addClass('familytree');
      this._content = $('<div class="content"></div>').appendTo(target);
      this.treeDatas = {};
    },
    addEvent: function(target) {
    	var ft = this;
    	this._content.on("mouseover.familyTree","li .data",function(e){
    		var id =$(this).parent().attr("id");
    		if(ft.config.pAdd && !ft.getTreeData(id).isPsync){
    			$(this).children(".parent").css("display","block");
    		}
    		if((!ft.config.async && $(this).parent().children("ul").length > 0) ||
    				(ft.config.async && ($(this).parent().children("ul").length > 0 || !ft.getTreeData(id).isAsync)))
    			$(this).children(".children").css("display","block");
    		if(ft.getTreeData(id).isextend){
    			$(this).children(".children").removeClass("unexpand").addClass("expand");
    		}
    		else{
    			$(this).children(".children").removeClass("expand").addClass("unexpand");
    		}
		}); 
    	this._content.on("mouseout.familyTree","li .data",function(){
			$(this).find(".parent").css("display","none");
			$(this).find(".children").css("display","none");
		});
    	this._content.on("click.familyTree",".children",function(){
    		var id =$(this).parent().parent().attr("id");
    		if(ft.config.async && !ft.getTreeData(id).isAsync){
    			ft.getTreeData(id).isAsync = true;
    			ft.getTreeData(id).isextend = true;
    			$(this).parent().children(".children").removeClass("unexpand").addClass("expand");
    			if(typeof ft.config.callback.onAsync == "function"){
    				ft.config.callback.onAsync(id,$(this).parent(),"children");
    			}
    		}
    		else{
    			if($(this).parent().parent().children("ul").css("display")=="block"){
    				$(this).parent().parent().children("ul").css("display","none");
    				ft.getTreeData(id).isextend = false;
    				$(this).parent().children(".children").removeClass("expand").addClass("unexpand");
    			}
    			else{
    				$(this).parent().parent().children("ul").css("display","block");
    				ft.getTreeData(id).isextend = true;
    				$(this).parent().children(".children").removeClass("unexpand").addClass("expand");
    				ft.resetWidth();
    			}
    			
    		}
    		
		}); 
    	this._content.on("click.familyTree",".parent",function(){
    		var id =$(this).parent().parent().attr("id");
    		if(ft.config.async && !ft.getTreeData(id).isPsync){
    			ft.getTreeData(id).isPsync = true;
    			if(typeof ft.config.callback.onAsync == "function"){
    				ft.config.callback.onAsync(id,$(this).parent(),"parent");
    			}
    		}
		}); 
    	this._content.on("click.familyTree","li a",function(){
    		var id =$(this).parent().parent().attr("id");
    		if(typeof ft.config.callback.onClick == "function"){
    			clearTimeout(TimeFn); 
    			//执行延时 
    			TimeFn = setTimeout(function(){ 
    				ft.config.callback.onClick(id,$(this).parent().parent(),ft.getTreeData(id));
    			},300); 
				
			}
		}); 
    	this._content.on("dblclick.familyTree","li a",function(){
    		var id =$(this).parent().parent().attr("id");
    		if(typeof ft.config.callback.onDblClick  == "function"){
    			clearTimeout(TimeFn); 
				ft.config.callback.onDblClick(id,$(this).parent().parent(),ft.getTreeData(id));
			}
		}); 
    },
    render: function(target,data,extend){
    	var $ul = $('<ul ></ul>').appendTo(target);
    	if(extend){
    		$ul.css("display","block");
    	}
    	else{
    		$ul.css("display","none");
    	}
    	for(var i in data){
    		var cdata = data[i];
    		var $li = $('<li id='+cdata.id+'><div class="data"><div class="parent" style="width:16px;height:16px;margin:auto;display:none;" ></div><a href="#">'+cdata.text+'</a><div class="children" style="width:16px;height:16px;margin:auto;display:none;"></div></div></li>').appendTo($ul);
    		$li.data("node",cdata);
    		this.treeDatas[cdata.id] = clone(cdata);
    		if(cdata.children!=null && cdata.children.length>0){
    			this.render($li,cdata.children,cdata.isextend);
    		}
    		
    	}
    },
    resetWidth: function(){
		var width = 0;
		var inf = $.grep(this._content.find("li"),function(li){
			return ($(li).find("ul").length==0 || $(li).find("ul").css("display")=="none") ;
		});
		var oinf = $.grep(this._content.find("li"),function(li){
			return !($(li).find("ul").length==0 || $(li).find("ul").css("display")=="none");
		})
		for(var i in inf){
			if($(inf[i]).parent().children("li").length>1){
				width+=$(inf[i]).width()+10;
			}
			else{
				width+=$(inf[i]).width();
			}
		}
		for(var i in oinf){
			$(oinf[i]).css("padding-left","0px");
		}
		this._content.width(width+20);
		for(var i in inf){
			if($(inf[i]).parent().children("li").length>1){
				$(inf[i]).css("padding-left","10px");
			}
			else{
				$(inf[i]).css("padding-left","0px");
			}
		}
		
	},
	getTreeData: function(id){
		return this.treeDatas[id];
	},
	addChild: function(id,data){
		var $dom = this._content.find("#"+id);
		var $ul = $dom.children("ul");
		if(this.getTreeData(data.id) == null){
			if($ul.length<=0){
				$ul = $('<ul style="display:block"></ul>').appendTo($dom);
			}
			var $li = $('<li id='+data.id+'><div class="data"><div class="parent" style="width:16px;height:16px;margin:auto;display:none;" ></div><a href="#">'+data.text+'</a><div class="children" style="width:16px;height:16px;margin:auto;display:none;"></div></div></li>').appendTo($ul);
    		$li.data("node",data);
			this.treeDatas[data.id] = clone(data);
			this.resetWidth();
		}
	},
	addChilds: function(id,datas){
		for(var i in datas){
			this.addChild(datas[i]);
		}
	},
	addParent: function(id,data){
		var $dom = this._content.find("#"+id);
		var $a = $('<a href="#">'+data.text+'</a>');
		var $cul =$("<ul></ul>").append($("<li id='"+data.id+"'></li>").append($dom.html()));
		$dom.html($a).append($cul);
		this.resetWidth();
	}
  };

  $.fn.extend({
    familytree: function(arg0, arg1) {
      var familytree = this.data('familytree');
      if (!familytree) {
    	  familytree = new FamilyTree(this, arg0);
        this.data('familytree', familytree);
      } else if (familytree[arg0] !== undefined) {
        return FamilyTree[arg0](arg1);
      }
      return familytree;
    }
  });
})(jQuery);
