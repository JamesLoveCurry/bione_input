(function ($)
{
     $.fn.ligerTreeToolBar = function(options) {
          return $.ligerui.run.call(this, "ligerTreeToolBar", arguments);
     };

     $.fn.ligerGetTreeToolBarManager = function() {
          return $.ligerui.run.call(this, "ligerGetTreeToolBarManager", arguments);
     };

     $.ligerDefaults.TreeToolBar = {};

     $.ligerMethos.TreeToolBar = {};

     $.ligerui.controls.TreeToolBar = function(element, options) {
          $.ligerui.controls.TreeToolBar.base.constructor
                    .call(this, element, options);
     };
     $.ligerui.controls.TreeToolBar
               .ligerExtend(
                         $.ligerui.core.UIComponent,
                         {
                              __getType : function() {
                                   return 'TreeToolBar';
                              },
                              __idPrev : function() {
                                   return 'TreeToolBar';
                              },
                              _extendMethods : function() {
                                   return $.ligerMethos.TreeToolBar;
                              },
                              _render : function() {
                                   var g = this, p = this.options;
                                   g.toolBar = $(this.element);
                                   g.toolBar.addClass("l-treetoolbar");
                                   g.set(p);
                              },
                              _setItems : function(items) {
                                   var g = this, p = this.options;
                                   $(items).each(function(i, item) {
                                        g.addItem(item);
                                   });
                                   if(p.treemenu){
                                        var treemenu = {items:
                                         [
                                             { text: '保存', click: itemclick ,icon:'add'}
                                         ]
                                          };
                                      var item={
                                           icon:'settings',
                                           menu:treemenu,
                                           align:right
                                      };
                                        g.addItem(item);
                                   }
                              },
                              addItem : function(item) {
                                   var g = this, p = this.options;
                                   if (item.line) {
                                        g.toolBar
                                                  .append('<div class="l-toolbar-separator"></div>');
                                        return;
                                   }
                                   if (item.menu) {
                                        var menucontainer=$('<div class="l-treemenubar-item"></div>');
                                        var ditem = $('<div class="l-treetoolbar-item l-panel-btn"><span></span><div class="l-panel-btn-l"></div><div class="l-panel-btn-r"></div><div class="l-menubar-item-down"></div></div>');
                                        if(item.align&&item.align==right){
                                             menucontainer=$('<div class="l-treemenubar-item-right"></div>');
                                             ditem = $('<div class="l-treetoolbar-item-right l-panel-btn"><span></span><div class="l-panel-btn-l"></div><div class="l-panel-btn-r"></div><div class="l-menubar-item-down"></div></div>');
                                        }
                                        menucontainer.append(ditem);
                                        g.toolBar.append(menucontainer);
                                        item.id && ditem.attr("menubarid", item.id);
                                        item.text
                                                  && $("span:first", ditem).html(
                                                            item.text);
                                        item.disable
                                                  && ditem
                                                            .addClass("l-menubar-item-disable");
                                        //item.click && ditem.click(function() {
                                        //     item.click(item);
                                        //});
                                        if (item.img) {
                                             ditem.append("<img src='" + item.img
                                                       + "' />");
                                             ditem.addClass("l-treetoolbar-item-hasicon");
                                        } else if (item.icon) {
                                             ditem.append("<div class='l-icon l-icon-"
                                                       + item.icon + "'></div>");
                                             ditem.addClass("l-treetoolbar-item-hasicon");
                                        }
                                        item.menu.parent=menucontainer;
                                        var menu = $.ligerMenu(item.menu);
                                        ditem.hover(
                                                  function() {
                                                       if(g.premenu!=undefined){
                                                            g.premenu.hide();
                                                       }
                                                       g.premenu=menu;
                                                       $(".l-panel-btn-over").removeClass('l-panel-btn-over');
                                                       $(this).addClass("l-panel-btn-over");
                                                  },function(){
                                   //                    menu.hide();
                                   //                    $(this).removeClass("l-panel-btn-over");
                                                  });
                                        ditem.click(
                                                  function(){
                                                       g.actionMenu&& g.actionMenu.hide();
                                                       var left = 0;
                                                       var top = g.toolBar.height();
                                                       menu.show({
                                                            top : top,
                                                            left : left
                                                       });
                                                       g.actionMenu = menu;
                                        });
                                        g.toolBar.mouseleave(function(){
                                                       menu.hide();
                                                       $(".l-panel-btn-over").removeClass('l-panel-btn-over');

                                       });
                                   } else {
                                        var ditem = $('<div class="l-treetoolbar-item l-panel-btn"><span></span><div class="l-panel-btn-l"></div><div class="l-panel-btn-r"></div></div>');
                                        g.toolBar.append(ditem);
                                        item.id && ditem.attr("toolbarid", item.id);
                                        if (item.img) {
                                             ditem.append("<img src='" + item.img
                                                       + "' />");
                                             ditem.addClass("l-treetoolbar-item-hasicon");
                                        } else if (item.icon) {
                                             ditem.append("<div class='l-icon l-icon-"
                                                       + item.icon + "'></div>");
                                             if(item.text){
                                                  ditem.addClass("l-treetoolbar-item-hasicon");
                                             }else{
                                                  ditem.addClass("l-treetoolbar-item-onlyicon");
                                             }
                                        }
                                        item.text
                                                  && $("span:first", ditem).html(
                                                            item.text);
                                        item.disable
                                                  && ditem
                                                            .addClass("l-toolbar-item-disable");
                                        item.click && ditem.click(function() {
                                             item.click(item);
                                        });

                                        ditem.hover(function() {
                                             $(".l-panel-btn-over").removeClass('l-panel-btn-over');
                                             $(this).addClass("l-panel-btn-over");
                                        }, function() {
                                             $(this).removeClass('l-panel-btn-over');
                                        });
                                   }
                              }
                         });
})(jQuery);