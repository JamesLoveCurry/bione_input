require.config({
    // baseUrl: '../../../../js',
    paths: { 
        Ligerui: 'ligerUI/ligerui.all',
        LigeruiExpand: 'ligerUI/ligerui.expand',
        Underscore: 'backbone/underscore',
        Backbone: 'backbone/backbone',
        Bione: 'bione/BIONE',
        Validate: 'jquery/jquery.validate.min',
        ValidateExpand: 'jquery/jquery.validate.expand.min',
        ValidateMessage: 'jquery/jquery.validate.messages_cn.min',
        ValidateExpandMessage: 'jquery/jquery.validate.expand.messages_cn.min',
        Metadata: 'jquery/jquery.metadata',
        UUID: 'uuid/uuid'
    },
    shim: {
        JqueryUI: {
            deps: ['jquery']
        },
        Ligerui: {
            deps: ['jquery', 'JSON2']
        },
        LigeruiExpand: {
            deps: ['Ligerui']
        },
        Underscore: {
            exports: '_'
        },
        Backbone: {
            deps: ['jquery', 'Underscore'],
            exports: 'Backbone'
        },
        UUID: {
            exports: 'UUID'
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
        Metadata: {
            deps: ['jquery']
        }
    }
});
define(['jquery', '../template/view'], function($, TemplateView){
    $.fn.extend({
        templateView : function(options){
            new TemplateView.View(this, options);
        }
    })
    return {};
});