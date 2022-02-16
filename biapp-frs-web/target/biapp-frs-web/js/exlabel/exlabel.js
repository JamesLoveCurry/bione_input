// version: 0.0.1 
'use strict';

(function($) {
  function apply(callback, args) {
    if (callback && $.type(callback) == 'function') {
      return callback.apply(this, args);
    } else {
      return;
    }
  }

  var textWidth = function(text) {
    var sensor = $('<pre>' + text + '</pre>').css({
      display: 'none'
    });
    $('body').append(sensor);
    var width = sensor.width();
    sensor.remove();
    return width > 30 ? width : 30;
  };

  function doGetCaretPosition(oField) {
    var iCaretPos = 0;
    if (document.selection) {
      oField.focus();
      var oSel = document.selection.createRange();
      oSel.moveStart('character', -oField.value.length);
      iCaretPos = oSel.text.length;
    } else if (oField.selectionStart || oField.selectionStart == '0') {
      iCaretPos = oField.selectionStart;
    }
    return (iCaretPos);
  }

  function Exlabel(content, config) {
    var initConfig = {
      text: 'text',
      type: 'close', // close || menu || '' ; default close 
      value: 'text',
      isOnly: true,
      isInput: true,
      isEdit: true,
      source: null,
      menuHeight: null,
      callback: {
        onClick: null,
        beforeShowMenu: null,
        beforeAdd: null,
        remove: null
      }
    };
    this.config = $.extend({}, initConfig, config || {});
    this.init(content);
    this.addEvent(content);
  }
  var _id = 1000;

  Exlabel.prototype = {
    init: function(target) {
      target.addClass('panel panel-default exlabel');
      target.css("margin-bottom","0px");
      this._content = $('<div class="panel-body" style="padding:5px;-moz-user-select:none;" onselectstart="return false"></div>').appendTo(target);
      this._menu = $('<div class="hidden exmenu"></div>').appendTo(target);
      if(!this.config.isEdit){
    	  this._menu.css("display","none");
      }
      
      this._input = $('<input type="text" class="exlabel-input" style="width: 30px;"/>');
      this._oldData = [];
      this._data = [];
      this._menuData = [];
      
      if (this.config.menuHeight) {
        this._menu.css('maxHeight', this.config.menuHeight);
      }
      
      if (this.config.isInput) {
        this._content.append(this._input);
        this._input.focus();
      }
    },
    addEvent: function(target) {
      var _this = this;
      var config = this.config;
      var menu = this._menu;
      // input
      // menu
      this._menu.on('click.exlabel', '.exmenu-item', function () {
    	  _this.add($(this).data('exlabel'));
      }).on('focus.exlabel', '.exmenu-item', function () {
      });
      // tag
     
      this._content.on('click.exlabel', '.label i.glyphicon-remove', function(e) {
        var item = $(this).parent('.label').data('exlabel');
        _this.remove(item);
	    if(typeof  _this.config.callback.remove == "function"){
	    	_this.config.callback.remove(item);
	    }
	    e.stopPropagation();
        
      }).on('click.exlabel', 'span.label.label-info', function(e) {
    	  if("btn" == _this.config.type){
    	    	var flag = $(this).hasClass("selected")? false:true;
    	        if (apply(config.callback.onClick, [$(this).data('exlabel'),flag]) == false) {
    	          return;
    	        }
    	        $(this).data('exlabel').flag = flag;
    	        $(this).toggleClass("selected");
    	  }
    	  else{
    		  if (apply(config.callback.onClick, [$(this).data('exlabel')]) == false) {
    	          return;
    	        } 
    	  }
    	
      }).on('click.exlabel', function(e) {
        _this._input.focus();
      });

      this._input.on('input.exlabel,', function() {
        // $(this).attr('size', this.value.length > 3 ? this.value.length : 3);
        $(this).width(textWidth($(this).val()));
        if (this.value) {
        		_this.showMenu();
        } else {
        		_this.hideMenu();
        }
      }).on('focusout.exlabel', function(e,b,c) {
    	  if(menu.hasClass("hidden")){
    		  if ($(this).data('exlabel')) {
                  _this.add($(this).data('exlabel'));
                  this.value = '';
                  return;
                }
                if (this.value && _this.config.isEdit) {
                  _this.add(this.value);
                  this.value = '';
                }
                _this.hideMenu();
    	  }
      })
      .on('keydown.exlabel', function(e) {
        var keyCode = e.keyCode;
        if (keyCode == 8 && doGetCaretPosition(this) == 0) {
          _this._data.pop();
          _this.render();
          return false;
        } else if (keyCode == 13) {
          
          if ($(this).data('exlabel')) {
            _this.add($(this).data('exlabel'));
            this.value = '';
            return;
          }
          if (this.value && _this.config.isEdit) {
            _this.add(this.value);
            this.value = '';
          }
          _this.hideMenu();
        } else if (keyCode == 38) {
          if (_this._menu.hasClass('hidden')) {
            return;
          }
          if (menu.children().is('.active')) {
            var s = menu.children('.active');
            var t = s.prev('.exmenu-item');
            s.removeClass('active');
            if (t.length > 0) {
              _this.activeMenuItem(t);
            } else {
              this.value = $(this).data('input');
              $(this).removeData('exlabel');
              $(this).removeData('input');
            }

          } else {
            $(this).data('input', this.value);
            var t = menu.children(':last');
            _this.activeMenuItem(t);
          }
          return false;
        } else if (keyCode == 40) {
          if (_this._menu.hasClass('hidden')) {
            return;
          } 
          if (menu.children().is('.active')) {
            var s = menu.children('.active');
            var t = s.next('.exmenu-item');
            s.removeClass('active');
            if (t.length > 0) {
              _this.activeMenuItem(t);
            } else {
              this.value = $(this).data('input');
              $(this).removeData('exlabel');
              $(this).removeData('input');
            }

          } else {
            $(this).data('input', this.value);
            var t = menu.children(':first');
            _this.activeMenuItem(t);
          }
          return false;
        }

        if (document.activeElement != this) {
          this.focus();
        }
      });
    },
    selectNodes: function(ids,flag,callflag){
    	if("btn" == this.config.type){
    		if(ids){
    			var idsArr = ids.split(",");
            	for(var i =0 ; i < idsArr.length;i++){
            		if(this._content.find("#"+idsArr[i]).length!=0){
            			if(callflag){
                			if (apply(this.config.callback.onClick, [ this._content.find("#"+idsArr[i]).parent().data('exlabel'),flag]) == false) {
                				continue;
                  	        }
                		}
                		if(flag)
                			this._content.find("#"+idsArr[i]).parent().addClass("selected");
                		else
                			this._content.find("#"+idsArr[i]).parent().removeClass("selected");
                		this._content.find("#"+idsArr[i]).parent().data('exlabel').flag = flag;
            		}
            	}
    		}
    		
    	}
    },
    getText: function(n) {
      return typeof n == 'string' ? n : n[this.config.text];
    },
    activeMenuItem: function(item) {
      item.addClass('active');
      var input = this._input;
      var data = item.data('exlabel');
      input.data('exlabel', data);
      input.val(this.getText(data));
      input.width(textWidth(input.val()));
    },
    hideMenu: function() {
      this._menu.addClass('hidden');
      this._input.removeData('exlabel');
    },
    showMenu: function() {
      var menu = this._menu;
      var _this = this;
      var input = this._input[0];

      var menudata = apply(_this.config.callback.beforeShowMenu, [this.value]);
      if (menudata == false) {
        return;
      }
      menudata = menudata || [];
      if (_this.config.source) {
        var source = _this.config.source;
        var vals = _this.val();
        if (input.value) {
          var value = input.value;
          menudata = menudata.concat($.grep(source, function(n) {
            var text = typeof n == 'string' ? n : n[_this.config.text];
            return text.indexOf(value) == 0;
          }));
        } else {
          menudata = menudata.concat(source);
        }
        // isOnly
        if (_this.config.isOnly) {
          menudata = $.grep(menudata, function(n) {
            var text = typeof n == 'string' ? n : n[_this.config.value];
            return $.inArray(text, vals) == -1;
          });
        }
      }
      menu.empty();

      if (menudata.length > 0) {
        $.each(menudata, function(i, n) {
          var item = $('<a class="exmenu-item">' + ($.type(n) == 'string' ? n : n[_this.config.text] || n['_text']) + '</a>');
          item.data('exlabel', n);
          menu.append(item);
        });
      } else {
        _this.hideMenu();
        return;
      }
      menu.removeClass('hidden');
    },
    adds: function(items,fflag) {
    	for(var i in items){
    		this.add(items[i],fflag);
    	}
    },
    add: function(item,fflag) {
      if (apply(this.config.callback.beforeAdd, [item]) == false) {
        return;
      }
      var data = this._data;
      if ($.type(item) == "string") {
        item = {
          text: item
        }
      }
      var itemData = $.extend({
        type: this.config.type
      }, item, {
        _id: 'label' + ++_id,
      });
      // isOnly
      var text = itemData[this.config.text];
      var value = itemData[this.config.value];
      if (!text) {
        return;
      }
      if (this.config.isOnly && this.find(value).length > 0) {
        return;
      }
      data.push(itemData);
      this.render(fflag);
      this._input.val('');
      this.hideMenu();
    },
    val: function() {
      var val = [];
      var config = this.config;
      $.each(this._data, function(i, n) {
        val.push(n[config.value]);
      });
      return val;
    },
    find: function(value) {
      var _this = this;
      return $.grep(this._data, function(n) {
        return value === n[_this.config.value]
      });
    },
    findsByPro: function(pro,value) {
        var _this = this;
        return $.grep(this._data, function(n) {
          return value === n[pro]
        });
    },
    findByPro: function(pro,value) {
        var _this = this;
        var grep = $.grep(this._data, function(n) {
          return value === n[pro]
        });
        if(grep.length > 0)
        	return grep[0];
        else
        	return null;
    },
    remove: function(item) {
      var _data = this._data;
      var _this = this;
      $.each(this._data, function(i, n) {
        if (n === item || n[_this.config.value] === item) {
          _data.splice(i, 1);
          _this.render();
          return false;
        }
      });
    },
    getData : function() {
    	return this._data;
    },
    rename: function(id,name) {
    	for(var i in this._data){
    		if(this._data[i][this.config.value] == id){
    			this._data[i][this.config.text] = name;
    			this._content.find("#"+id).html(name);
    		}
    	}
    },
    removeAll: function() {
      this._data = [];
      this.render();
    },
    render: function(fflag) {
      var _this = this;
      var config = this.config;
      var data = _this._data;
      if (this._oldData !== this._data) {
        this._content.find('.label').remove();
        this._oldDAta = this._data;
        $.each(data, function(i, n) {
          var label = [
            '<span class="label label-info">',
            '<span id="',
            n[config.value],
            '" class="label-text" style="font-size:12px;">',
            n[config.text],
            '</span>',
            '</span>'
          ].join('');
          var $label = $(label);
          if(config.callback.onClick != null && typeof config.callback.onClick == "function"){
        	  $label.css("cursor","pointer");
        	}
          $label.data('exlabel', n);
          if ('menu' === n.type) {
            $label.addClass('label-menu');
            $label.append('<i class="glyphicon glyphicon-remove"></i>');
          } else if ('close' === n.type) {
            $label.addClass('label-close');
            $label.append('<i class="glyphicon glyphicon-remove"></i>');
          }
          else if ('btn' === n.type) {
              $label.addClass('label-btn');
            }
          if (_this.config.isInput) {
           var $input =  _this._input.before($label);
            if(fflag == null || fflag == true){
            	$input.focus();
            }
          } else {
            _this._content.append($label);
          }
        });
      }
    }
  };

  $.fn.extend({
    exlabel: function(arg0, arg1) {
      var exlabel = this.data('exlabel');
      if (!exlabel) {
        exlabel = new Exlabel(this, arg0);
        this.data('exlabel', exlabel);
      } else if (exlabel[arg0] !== undefined) {
        return exlabel[arg0](arg1);
      }
      return exlabel;
    }
  });
})(jQuery);
