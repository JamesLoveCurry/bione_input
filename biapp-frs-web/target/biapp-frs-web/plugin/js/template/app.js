/*!
 * 参数模版中设计器的视图对象
 * 
 */
define(['jquery', 'JSON2', 'Underscore', 'Backbone', 'BackboneUndo', '../template/model', '../template/layout', '../template/convertor', '../template/effects', '../template/util', '../template/validator'], function ($, JSON2, _, Backbone, BackboneUndo, Model, Layout, Convertor, Effects, Util, Validator) {
    
	// 设置jquery.format验证
	$.metadata.setType("attr", "validate");
	
	window.AppTip = {};
	
    // 属性栏的视图对象
	var PropertyView = Backbone.View.extend({

        el: $('#prop'),

        // 是否允许修改属性同步刷新设计区
        isRefresh: true,
        
        tip: {},

        initialize: function () {
            _.bindAll(this, 'render');
            this.listenTo(this.model, 'remove', this.remove);
            this.listenTo(this.model, 'change', this.rerenderApp);
        },

        render: function () {
            var tmpl = $( "#designer-property-table" ).tmpl();
            tmpl.find("tr").after($( "#designer-property-tr" ).tmpl(this.model.getAttrs()));
            $(this.el).html(tmpl);
            // 添加form验证
            Validator.validate($("#propForm"));
            $("#propForm").ligerForm({inputWidth: 110});
            this.bindEvent(this.model, $("#propForm"));
            this.$(".l-text").css("border", "0px");
        },

        // 绑定属性区的控件事件
        bindEvent: function (model) {
            var g = this;
            this.$('input').bind("change", function () {
                var $this = $(this);
                g.isRefresh = model.isRefresh($this.attr("id"));
                // 修改模型中的属性，并使用模型进行验证
                model.set($this.attr("id"), $this.val(), {validate: true});
                // 显示验证信息
                g.showError($this, model.validationError);
            });
            this.$('select').bind("change", function () {
                var $this = $(this);
                g.isRefresh = model.isRefresh($this.attr("id"));
                // 修改模型中的属性，并使用模型进行验证
                model.set($this.attr("id"), $this.val(), {validate: true});
                // 显示验证信息
                g.showError($this, model.validationError);
            });
            this.$('input[ltype=popup]').each(function () {
                var $popup = $(this);
                // 点击弹出控件的取消按钮，同时取消模型中的属性
                $popup.parent().find('div.l-trigger-cancel').bind("click", function () {
                    model.unset($popup.attr('id'));
                });    
            });
            this.$('#datasource').parent().find('div.l-trigger:not(.l-trigger-cancel)').bind("click", function () {
                // 全局临时变量纪录提示信息和对话框对象
            	window.OptionTip = window.OptionTip || {};
                // 根据类型判断下拉框还是数据库，弹出不同的设置对话框
                var relativePath = g.getUrl(model.get('type'));
                window.OptionTip.datasourceDialog = $.ligerDialog.open({
                    id: 'optionsDialog',
                    title: '数据源配置',
                    url: Util.getContextPath() + relativePath,
                    width: 700,
                    height: 365,
                    isResize: true,
                    allowClose : false,
                    onClosed: function () {//关闭时同步清空提示信息和弹出框对象
                        var id;
                        for (id in window.OptionTip) {
                            if (window.OptionTip.hasOwnProperty(id) && window.OptionTip[id]) {
                                window.OptionTip[id].destroy();
                                delete window.OptionTip[id];
                            }
                        }
                    }
                });
            });
            this.$('#validate').parent().find('div.l-trigger:not(.l-trigger-cancel)').bind("click", function () {
                // 全局临时变量纪录提示信息和对话框对象
                window.validatorTip = {};
                window.validatorTip.validatorDialog = $.ligerDialog.open({
                    id: 'validatorTipDialog',
                    title: '验证配置',
                    url: Util.getContextPath() + '/report/frame/param/templates/validator',
                    width: 700,
                    height: 310,
                    isResize: true,
                    allowClose : false,
                    onClosed: function () {//关闭时同步清空提示信息和弹出框对象
                        var id;
                        for (id in window.validatorTip) {
                            if (window.validatorTip.hasOwnProperty(id) && window.validatorTip[id]) {
                                window.validatorTip[id].destroy();
                                delete window.validatorTip[id];
                            }
                        }
                        window.validatorTip = {};
                    }
                });
            });
        },
        
        getUrl: function (type) {
        	switch(type) {
            case 'select':
            	return '/report/frame/param/templates/selectOptions';
            case 'combobox': 
            	return '/report/frame/param/templates/treeOptions';
            case 'popup':
            	return '/report/frame/param/templates/popupOptions';
            case 'dblin':
            	return '/report/frame/param/templates/selectOptions';
            default:
            	return '/report/frame/param/templates/error';
            }
        },

        // 显示验证信息，如果之前有同样id的验证信息，先进行清除，然后在判断时候应该再次显示验证信息
        showError: function ($this, error) {
            var id = this.model.cid;
            if (this.tip[id]) {
                this.tip[id].destroy();
                delete this.tip[id];
            }
            if (error != null) {
                this.tip[id] = $.ligerDialog.tip({ id: id, title: '提示信息', content: '<B>' + this.model.get('display') + '[' + this.model.get('name') + ']' + "：</B>" + error });
                this.render();
            }
        },

        // 删除属性栏的控件，并停止视图监听模型变化
        remove: function () {
            this.$el.children().remove();
            this.stopListening();
        },

        // 属性改变时重新渲染设计区
        rerenderApp: function () {
            if(this.isRefresh && window.app){
                window.app.render();
                if ( $("#propForm").length > 0 ) {
                	this.render();
                }
            }
        }
        
    });

    // 设计区的视图对象
    var AppView = Backbone.View.extend({

        el: $('#designerCanvas'),

        initialize: function () {
            _.bindAll(this, 'render');
            this.template = new Model.Template();
            this.componentes = this.template._getComponentes();
            // 创建undo管理对象，并开启纪录
            this.undoManager = new Backbone.UndoManager();
            this.undoManager.register(this.componentes);
            this.undoManager.startTracking();
            this.listenTo(this.componentes, 'add', this.render);
            this.listenTo(this.componentes, 'remove', this.render);
            this.listenTo(this.componentes, 'reset', this.render);
            _.bindAll(this, 'render', 'initData');
            // 设置全局对象，方便其他模块调用
            window.app = this;
            this.initData();
            this.render();
        },

        // 本系统修改参数模版的时候，根据当前弹出框上级查找paramJson中的值，初始化数据。        
        initData: function () {
        	var $paramJson;
        	if (($paramJson = window.parent.frames["paramtmpBox"].$("#paramJson")).length > 0 && $paramJson.val() !== "") {
        		this.componentes.add(JSON2.parse($paramJson.val()));
        	}
        },

        render: function () {
            var g = this;
            // 先删除设计区的所有控件
            this.$el.children().remove();
            // 重新设置设计区的高度
            this.$el.height($('#center').height());
            // 判断如果没有添加任何控件，则需要特殊处理
            if (this.componentes.length == 0) {
                var emp = $('#designer-template-empty').tmpl();
                $(emp).height($('#center').height() - 2);
                this.$el.append(emp);
                // 设置空白的区域可以拖入控件
                Effects.createEmptyFormDroppable(function (event, ui) {
                	if (ui.draggable.attr('type')) {
                		g.template.addComponent(ui.draggable.attr('type'));
                	} else if (ui.draggable.attr('outlinelevel') && ui.draggable.attr('id')) {
                		BIONE.showLoading();
                		$.ajax({
                            type: "get",
                            url: Util.getContextPath() + "/report/frame/param/templates/" + ui.draggable.attr("id") + "?type=edit",
                            cache: false,
                            dataType: 'json',
                            success: function (data){
                            	if (data['paramJson'] && data['paramJson'].length > 0) {
                            		var cs = JSON2.parse(data['paramJson']), i = 0;
                            		for (i = 0; i < cs.length; i++) {
                            			cs[i]["id"] && (cs[i]["id"] = "");
                            		}
                            		g.componentes.add(cs);
                            	}
                            	BIONE.hideLoading();
                            },
                            error: function (XMLHttpRequest, textStatus){
                            	BIONE.hideLoading();
                            }
                        });
                	}
                });
            } else {
                // 在设计区添加转换完ligerForm的UI对象
                $('<div id="wraper"></div>').appendTo(this.$el).ligerForm(Convertor.convert(this.componentes.toFormPorperties(), 'design'));
                // 设置ligerForm中的控件可以通过拖拽修改位置
                Effects.createLigerFormSortable( function (event, ui) {
                    var model = g.template.getComponent(ui.item.find("[cid]").attr("cid"));
                    if(model === undefined) return;
                    var index = $(".l-fieldcontainer").index(ui.item);//必须要在删除前计算索引位置，否则会返回-1
                    var cloneModel = g.template.moveComponent(model);
                    g.template.removeComponent(model);
                    g.template.insertComponent(cloneModel, index);
                });
                // 设置ligerForm可以拖入控件
                Effects.createLigerFormDroppable(function (event, ui, index) {
                	//edit by fangjuan 20140725 载入模板可以在任何时候载入，而不是只有为空的时候
                	if (ui.draggable.attr('type')) {
                		 g.template.insertComponent(g.template.createComponent(ui.draggable.attr('type')), index);
                	} else if (ui.draggable.attr('outlinelevel') && ui.draggable.attr('id')) {
                		BIONE.showLoading();
                		$.ajax({
                            type: "get",
                            url: Util.getContextPath() + "/report/frame/param/templates/" + ui.draggable.attr("id") + "?type=edit",
                            cache: false,
                            dataType: 'json',
                            success: function (data){
                            	if (data['paramJson'] && data['paramJson'].length > 0) {
                            		var cs = JSON2.parse(data['paramJson']), i = 0;
                            		for (i = 0; i < cs.length; i++) {
                            			cs[i]["id"] && (cs[i]["id"] = "");
                            			var name = cs[i]["name"];
                                    	while(_.contains(_.values(Model.Component.ids), name))
                                    	{
                                    		var li = $('#placeHolder');
                                            if (li.hasClass('l-fieldcontainer-first')) {
                                                li.next('li').addClass('l-fieldcontainer-first')
                                            }
                                            li.remove();
                                    		BIONE.hideLoading();
                                    		BIONE.tip("载入的模板和原来的模板中有唯一标识重复，无法拖入");
                                    		return ;
                                    	}
                            		}
                            		g.componentes.add(cs);
                            	}
                            	BIONE.hideLoading();
                            },
                            error: function (XMLHttpRequest, textStatus){
                            	BIONE.hideLoading();
                            }
                        });
                	}
                   
                });
                // 设置每个参数的UI事件
                Effects.createLiEvent(this.$el, this.template, function(component){
                    var propertyView = new PropertyView({
                        model: component
                    });
                    return propertyView;
                });
                // 重新选择刷新界面前选择的参数
                Effects.selectHistoryLi(this.$el);
            }
            return this;
        },

        undo: function () {
            this.undoManager.undo();
            this.render();
        },

        redo: function () {
            this.undoManager.redo();
            this.render();
        }
    });

    return {
        AppView : AppView
    };
});