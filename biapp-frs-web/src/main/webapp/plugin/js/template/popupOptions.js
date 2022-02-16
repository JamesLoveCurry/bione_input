/*!
 * 数据树的数据源配置视图模块
 * 
 */
define(['jquery', 'JSON2', 'Underscore', 'Backbone', 'BackboneUndo', '../template/util','JqueryTemplate', 'Ligerui', 'LigeruiExpand', 'Codemirror', 'CodemirrorSql', 'SqlFormat'], function ($, JSON2, _, Backbone, BackboneUndo, Util) {

    // 远程数据源配置视图
	var RemoteOptionsView = Backbone.View.extend({
		
		type : "url",
		
		initialize: function () {
            _.bindAll(this, 'render', 'saveData', 'closeDialog', 'destroy');
		},

        render: function (){
            var g = this;
            this.destroy();
            this.el = $('#remoteOptions-template').tmpl();
            $(this.el).ligerForm({inputWidth: 544});
            // 添加帮助提示信息
            $(this.el).find(".template-help").ligerTip();
            var ajaxType = $.ligerui.get('ajaxType');
            ajaxType.set('data', [{id: 'post', text: 'POST'}, {id: 'get', text: 'GET'}]);
            ajaxType.setValue('post');
            // 初始化工具栏
            $(this.el).find("#gridToolbar").ligerToolBar({
                items: [
                    { text: '保存', click: g.saveData, icon:'save'},
                    { line:true },
                    { text: '关闭', click: g.closeDialog, icon:'right'}
                ]
            });
            return this;
        },

        // 销毁的ligerui对象
        destroy: function () {
            var ajaxType = null;
            if(ajaxType = $.ligerui.get('ajaxType')){
            	ajaxType.destroy();
            }
        },
        
        // 验证填写的url
        validate: function (url) {
        	var errorMessage = null, id = 'options_url';
			if (window.parent.OptionTip[id]) {
				window.parent.OptionTip[id].destroy();
				delete window.parent.OptionTip[id];
			}
			if (url === undefined || url === null || url === "") {
				errorMessage = '请求地址不能为空！';
			}
			if (errorMessage) {
    			window.parent.OptionTip[id] = window.parent.$.ligerDialog.tip({ id: id, title: '提示信息', content: errorMessage});
    			return false;
			} else {
				return true;
			}
        },
        
        // 设置上级下拉框的唯一标识uuid
        setParentId: function (parentId) {
            this.parentId = parentId;
        },

        // 设置数据并改变视图
        setData: function (data) {
            var url = data['url'], ajaxType = data['ajaxType'];
            if (url) {
                $('#url').val(url);
            }
            if (ajaxType) {
                $.ligerui.get('ajaxType').setValue(ajaxType);
            }
        },

        // 保存数据
        saveData: function () {
        	var url = $('#url').val();
            if (this.validate(url)) {
            	var data = {}, puuid = undefined;
            	data["options"] = {};
            	if ((puuid = this.parentId) && puuid !=='null' ) {
                    data["puuid"] = puuid;
                }
                data["options"]["url"] = url;
                data["options"]["ajaxType"] = $.ligerui.get('ajaxType').getValue();
                window.parent.$('#datasource').val(JSON2.stringify(data));
                window.parent.$('#datasource').trigger("change");
                this.closeDialog();
            }
        },

        closeDialog: function () {
            var dialog = window.parent.OptionTip.datasourceDialog;
            dialog.close();
        }
	
	});


    var DatabaseOptionsView = Backbone.View.extend({
        
        config : null,
        
        type : "db",
        
        fmt : false,
        
        initialize: function () {
            _.bindAll(this, 'render', 'setData', 'saveData', 'destroy', 'formatSql', 'format', 'min');
        },

        render: function (){
            var g = this;
            this.destroy();
            this.el = $('#databaseOptions-template').tmpl();
            $(this.el).ligerForm({inputWidth: 330});
            $(this.el).find(".template-help").ligerTip();
            this.db = $.ligerui.get('db');
            this.db.set('url', Util.getContextPath() + '/report/frame/param/commonComboBox/dataSources.json');
            // 初始化系统参数列表框
            this.systemVariables = $(this.el).find("#systemVariables").ligerListBox({
                isShowCheckBox: false, isMultiSelect: false, width: 188, height:254,
                url: Util.getContextPath() + "/report/frame/param/commonComboBox/systemVariables.json",
                onSuccess : function () {
                	if (g.parentId && g.parentId !== 'null') {
                		var ltype = window.parent.$('input[uuid=' + g.parentId + ']').attr('ltype');
                		if (ltype === 'select' || ltype === 'combobox') {
                			this.addItems({id: "selectedValue", text: "上级实际值"});
                			this.addItems({id: "selectedText", text: "上级显示值"});
                		} else {
                			this.addItems({id: "parentValue", text: "上级实际值"});
                		}
                	}
                	$("#systemVariables tr").dblclick(function () {
                		if (typeof g.editor.getSelection() === "string" && g.editor.getSelection().length > 0) {
                			g.editor.replaceSelection("#{" + $(this).attr("value") + "}");
                		} else {
                			g.editor.replaceRange("#{" + $(this).attr("value") + "}", g.editor.getCursor(), g.editor.getCursor());
                		}
                    	return false;
                    });
                }
            });
            // 初始化工具栏
            $(this.el).find("#gridToolbar").ligerToolBar({
                items: [
                    { text: '保存', click: g.saveData, icon:'save'},
                    { line:true },
                    { text: '关闭', click: g.closeDialog, icon:'right'},
                    { line:true },
                    { text: '格式化', click: g.formatSql, icon: 'attibutes'}
                ]
            });
            $('tbody:first').children().remove();
            $('tbody:first').append(this.el);
            // 初始化代码着色器
            this.editor = CodeMirror.fromTextArea($('#sql').get(0), {
                mode: "text/x-sql",
                indentWithTabs: true,
                smartIndent: true,
                lineNumbers: true,
                matchBrackets : true,
                autofocus: true,
                theme: 'eclipse'
            });
        },

        // 销毁的ligerui对象
        destroy: function () {
        	var db = null;
            if(db = $.ligerui.get('db')){
                db.destroy();
            }
        },
        
        // 设置上级下拉框的唯一标识uuid
        setParentId: function (parentId) {
            this.parentId = parentId;
        },

        // 设置数据并改变视图
        setData: function (data) {
            var db = data['db'], sql = data['sql'], format = data['format'];
            if (db) {
                $.ligerui.get('db').setValue(db);
            }
            if (sql) {
                if (format === true) {
                    this.fmt = true;
                    this.editor.setValue(this.format(sql));
                } else {
                    this.editor.setValue(sql);
                }
            }
        },

        // 验证sql语句
        validate: function (sql) {
        	var errorMessage = null, id = 'options_sql';
			if (window.parent.OptionTip[id]) {
				window.parent.OptionTip[id].destroy();
				delete window.parent.OptionTip[id];
			}
			if (sql === undefined || sql === null || sql === "") {
				errorMessage = 'SQL语句不能为空！';
			} else if (!new RegExp(/\sid/).test(sql.toLowerCase())) {
                errorMessage = 'SQL语句中缺少id字段！';
            } else if (!new RegExp(/\stext/).test(sql.toLowerCase())) {
                errorMessage = 'SQL语句中缺少text字段！';
            }
			if (errorMessage) {
    			window.parent.OptionTip[id] = window.parent.$.ligerDialog.tip({ id: id, title: '提示信息', content: errorMessage});
    			return false;
			} else {
				return true;
			}
        },

        // 保存数据
        saveData: function () {
        	var sql = this.fmt ? this.min(this.editor.getValue()) : this.editor.getValue();
        	if (this.validate(sql)) {
        		var data = {}, puuid = undefined;
        		data["options"] = {};
        		if ((puuid = this.parentId) && puuid !=='null' ) {
        			data["puuid"] = puuid;
        		}
        		data["options"]["db"] = $.ligerui.get('db').getValue();
        		data["options"]["sql"] = sql;
        		data["options"]["format"] = this.fmt;
        		window.parent.$('#datasource').val(JSON2.stringify(data));
        		window.parent.$('#datasource').trigger("change");
        		this.closeDialog();
        	}
        },
        
        // 设置格式化后的sql语句
        formatSql: function () {
            this.editor.setValue(this.format(this.editor.getValue()));
        },

        // 调用接口返回格式化后的sql语句
        format: function (sql) {
            if (sql === undefined || sql === null || sql === "") {
                return "";
            } else {
                this.fmt = true;
                return $.format(sql, {method: 'sql'});
            }
        },

        // 最小化sql语句
        min: function (sql) {
            return $.format(sql, {method: 'sqlmin'});
        },

        closeDialog: function () {
            var dialog = window.parent.OptionTip.datasourceDialog;
            dialog.close();
        }
    }); 

    // 数据树数据源设置视图
	var OptionsView = Backbone.View.extend({

		el : $('#treeOptionsDesigner'),

        _defaultOptionType : 'url',
        
        _deafaultCorrelated : 'null',

		initialize: function () {
            _.bindAll(this, 'render', 'renderTbody', 'createTypeOptions');
            var tmpl = $( "#treeOptionsForm-template" ).tmpl();
            $(this.el).html(tmpl);
            this.$('#treeOptionsForm').ligerForm({inputWidth: 190});
            this.optionsTypeSelect = $.ligerui.get('options_type');
            this.correlatedSelect = $.ligerui.get('correlated');
            this.render();
        },

        render: function () {
            var g = this, options = window.parent.$('#datasource').val(), config = null, op = null;
            // 初始化类型下拉框数据
            this.optionsTypeSelect.set('data', this.createTypeOptions());
            // 初始化上级下拉框数据
            this.correlatedSelect.set('data', this.createParentOptions());
            // 如果上级窗体中已有数据源设置
        	if (options) {
        		config = JSON2.parse(options);
                op = config['options'];
                if (op['url']) {
                    this.optionsTypeSelect.selectValue('url');
                } else if (op['sql']) {
                    this.optionsTypeSelect.selectValue('sql');
                }
                if (config['puuid']) {
                    this.correlatedSelect.selectValue(config['puuid']);
                } else {
                    this.correlatedSelect.selectValue('null');
                }
                this.renderTbody(op);
            } else {
                this.optionsTypeSelect.selectValue(this._defaultOptionType);
                this.correlatedSelect.selectValue(this._deafaultCorrelated);
                this.renderTbody();
            }
        	this.optionsTypeSelect.set('onSelected', function () {
        		g.correlatedSelect.selectValue('null');
            });
        	this.correlatedSelect.set('onSelected', function () {
                g.renderTbody();
            });
        },

        createTypeOptions: function () {
            return [{id: 'url', text: '超链接'}, {id: 'sql', text: '数据库'}];
        },
        
        // 创建上级下拉框的唯一标志对象
        createParentOptions: function () {
        	var selectComponentes = _.union(window.parent.app.template._getComponentesWhere({type : 'date'}), window.parent.app.template._getComponentesWhere({type : 'select'}));
        	var selectedCid = window.parent.$('.li-selected :input[cid]').attr('cid');
        	var result = [];
        	result.push({id : "null", text : "无"});
        	_.each(selectComponentes, function (comp){
        		if (selectedCid !== comp.cid) {
        			result.push({id : comp.id, text : comp.get('name')});
        		}
        	});
        	return result;
        },

        renderTbody: function (data) {
        	var optionsType = this.optionsTypeSelect.getValue(), appendHtml = '';
        	if (optionsType === 'url') {
                var rov = new RemoteOptionsView();
                rov.setParentId(this.correlatedSelect.getValue());
                appendHtml = rov.render().el;
                this.$('tbody').children().remove();
                this.$('tbody').append(appendHtml);
                if (data) {
                    rov.setData(data);
                }
        	} else if (optionsType === 'sql') {
                var dov = new DatabaseOptionsView();
                dov.setParentId(this.correlatedSelect.getValue());
                dov.render();
                if (data) {
                    dov.setData(data);
                }
        	}
        }
	});

	return {
		OptionsView : OptionsView
	};
});