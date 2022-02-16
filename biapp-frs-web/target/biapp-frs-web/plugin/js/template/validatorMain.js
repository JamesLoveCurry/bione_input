require.config({
    baseUrl: '../../../../js',
    paths: { 
        jquery: 'jquery/jquery-1.7.1.min',
        JSON2: 'bione/json2.min',
        Underscore: 'backbone/underscore',
        Backbone: 'backbone/backbone',
        Ligerui: 'ligerUI/ligerui.all',
        JqueryTemplate: 'backbone/jquery.tmpl',
        Bione: 'bione/BIONE',
        Validate: 'jquery/jquery.validate.min',
        ValidateMessage: 'jquery/jquery.validate.messages_cn.min',
        ValidateExpand: 'jquery/jquery.validate.expand',
        Metadata: 'jquery/jquery.metadata'
    },
    shim: {
    	JSON2: {
    		exports: 'JSON2'
    	},
        Underscore: {
            exports: '_'
        },
        Backbone: {
            deps: ['jquery', 'Underscore'],
            exports: 'Backbone'
        },
        JqueryTemplate: {
            deps: ['jquery']
        },
        Ligerui: {
            deps: ['jquery', 'JSON2']
        },
        Bione: {
            deps: ['jquery', 'JSON2', 'Ligerui']
        },
        Validate: {
            deps: ['jquery'],
            exports: 'Validate'
        },
        ValidateMessage: {
            deps: ['jquery', 'Validate']
        },
        ValidateExpand: {
            deps: ['Validate']
        },
        Metadata : {
        	deps: ['jquery']
        }
    }
});
require(['jquery', 'JSON2', 'Underscore', 'Backbone', 'Ligerui', 'JqueryTemplate', 'Bione', 'Validate', 'ValidateMessage', 'ValidateExpand', 'Metadata'], function($, JSON2, _, Backbone){
	
    $.metadata.setType("attr", "validate");

    $.extend($.validator.messages, {
        greaterThan : "最大值必须大于最小值"
    });

	var Validator = Backbone.Model.extend({
        defaults: {
            "required":    "false",
            "type":        "none"
        }
	});

	var ValidatorView = Backbone.View.extend({

        el : $('#validatorDesigner'),

        types : [{id: "none", text: "无"}, {id: "number", text: "数字"}, {id: "digits", text: "正整数"}, {id: "email", text: "电子邮件"}, {id: "url", text: "网址"}],

        initialize: function () {
            _.bindAll(this, 'render', 'loadModelValue', 'saveValidator', 'closeValidator', 'setValueIfNotEmpty', 'isNotEmpty', 'closeValidator');
            var v = window.parent.$('#validate').val();
            if (v) {
                this.validator = new Validator();
                var config = JSON2.parse(v);
                if (config) {
                    this.validator.set(config);
                }
            } else {
                this.validator = new Validator();
            }  
        },

        render: function () {
            var tmpl = $( "#validatorForm-template" ).tmpl(), g = this;
            $(this.el).html(tmpl);
            var $form = this.$("#validatorForm");
            $form.ligerForm({
                inputWidth: 200, labelWidth: 90, space: 20,
                fields: [
                    { display: "必填验证", name: "required", newline: true, type: "select" , comboboxName: "requiredName", options : { data: [{id: "true", text: "是"}, {id: "false", text: "否"}]}, group: "基础验证", groupicon: "../../../../images/classics/template/application_view_tile.png"}, 
                    { display: "类型", name: "type", newline: false, type: "select", comboboxName: "typeName", options: { data: this.types} },
                    { display: "最小长度", name: "minlength", newline: true, type: "text", group: "字符串长度", groupicon: "../../../../images/classics/template/communication.gif", validate: {digits: true, messages : {digits: "只能输入正整数"}}},
                    { display: "最大长度", name: "maxlength", newline: false, type: "text", validate: {digits: true, greaterThan : "minlength", messages : {digits: "只能输入正整数"}}},
                    { display: "最小值", name: "min", newline: true, type: "text", group: "数值范围", groupicon: "../../../../images/classics/template/pencil.png", validate: {number: true} },
                    { display: "最大值", name: "max", newline: false, type: "text", validate: {number: true, greaterThan : "min"} }
                ]
            });
            this.loadModelValue();
            this.$("#saveBtn").click(this.saveValidator);
            this.$("#cancelBtn").click(this.closeValidator);

            BIONE.validate($form);
            var buttons = [];
            buttons.push({
                text : '取消',
                onclick : this.closeValidator
            });
            buttons.push({
                text: '保存',
                onclick: function() {
                    if ($form.valid()) {
                        g.saveValidator()
                    } else {
                        BIONE.showInvalid();
                    }
                }
            });
            BIONE.addFormButtons(buttons);
        },

        loadModelValue: function () {
            $.ligerui.get("requiredName").selectValue(this.validator.get("required"));
            $.ligerui.get("typeName").selectValue(this.validator.get("type"));
            for (var i in this.types) {
                if(_.contains(_.keys(this.validator.attributes), this.types[i]['id'])){
                    $.ligerui.get("typeName").selectValue(this.types[i]['id']);
                }
            }
            $.ligerui.get("minlength").setValue(this.validator.get("minlength"));
            $.ligerui.get("maxlength").setValue(this.validator.get("maxlength"));
            $.ligerui.get("min").setValue(this.validator.get("min"));
            $.ligerui.get("max").setValue(this.validator.get("max"));
            var rangelength = this.validator.get("rangelength");
            if (typeof(rangelength) === 'string') {
                r = rangelength.substring(1, rangelength.length - 1).split(",");
                $.ligerui.get("minlength").setValue(r[0]);
                $.ligerui.get("maxlength").setValue(r[1]);
            } else if (typeof(rangelength) === 'object'){
            	$.ligerui.get("minlength").setValue(rangelength[0]);
                $.ligerui.get("maxlength").setValue(rangelength[1]);
            }
            var range = this.validator.get("range");
            if (typeof(range) === 'string') {
                r = range.substring(1, range.length - 1).split(",");
                $.ligerui.get("min").setValue(r[0]);
                $.ligerui.get("max").setValue(r[1]);
            } else if (typeof(range) === 'object') {
            	$.ligerui.get("min").setValue(range[0]);
                $.ligerui.get("max").setValue(range[1]);
            }
        },

        setValueIfNotEmpty: function (obj, name, value) {
            if(this.isNotEmpty(value)) {
                obj[name] = value;
            }
        },

        isNotEmpty: function (value) {
            return value !== undefined && value !== null && value !== "";
        },

        saveValidator: function () {
            var validate = {};
            var required = $.ligerui.get("requiredName").getValue();
            if (required != "false") {
                this.setValueIfNotEmpty(validate, "required", true);
            }else{//edit by fangjuan 20150915
            	this.setValueIfNotEmpty(validate, "required", false);
            }

            var type = $.ligerui.get("typeName").getValue();
            if (this.isNotEmpty(type) && type !== "none") {
                validate[type] = true;
            }

            var minlength = $.ligerui.get("minlength").getValue();
            var maxlength = $.ligerui.get("maxlength").getValue();
            if (this.isNotEmpty(minlength) && this.isNotEmpty(maxlength)) {
                validate['rangelength'] = [minlength, maxlength];
            } else {
                this.setValueIfNotEmpty(validate, "minlength", minlength);
                this.setValueIfNotEmpty(validate, "maxlength", maxlength);
            }

            var min = $.ligerui.get("min").getValue();
            var max = $.ligerui.get("max").getValue();
            if (this.isNotEmpty(min) && this.isNotEmpty(max)) {
                validate['range'] = [ min, max];
            } else {
                this.setValueIfNotEmpty(validate, "min", min);
                this.setValueIfNotEmpty(validate, "max", max);
            }
            if (!_.isEmpty(validate)) {
                window.parent.$('#validate').val(JSON2.stringify(validate));
                window.parent.$('#validate').trigger("change");
            }
            
            this.closeValidator();
        },

        closeValidator: function () {
            var dialog = window.parent.validatorTip.validatorDialog;
            dialog.close();
        }

	});

    $(function () {
        var vv = new ValidatorView();
        vv.render();
    });
    
});