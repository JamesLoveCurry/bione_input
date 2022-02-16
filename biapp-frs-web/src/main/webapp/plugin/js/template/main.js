require.config({
    baseUrl: '../../../../js',
    paths: { 
        jquery: 'jquery/jquery-1.7.1.min',
        JSON2: 'bione/json2.min',
        JqueryUI: 'jqueryUI/jquery-ui-1.10.3.custom',
        Ligerui: 'ligerUI/ligerui.all',
        LigeruiExpand: 'ligerUI/ligerui.expand',
        Underscore: 'backbone/underscore',
        Backbone: 'backbone/backbone',
        BackboneUndo: 'backbone/Backbone.Undo',
        JqueryTemplate: 'backbone/jquery.tmpl',
        Bione: 'bione/BIONE',
        Validate: 'jquery/jquery.validate.min',
        ValidateExpand: 'jquery/jquery.validate.expand.min',
        ValidateMessage: 'jquery/jquery.validate.messages_cn.min',
        ValidateExpandMessage: 'jquery/jquery.validate.expand.messages_cn.min',
        Metadata: 'jquery/jquery.metadata',
        UUID: 'uuid/uuid'
    },
    shim: {
    	JSON2: {
    		exports: 'JSON2'
    	},
        JqueryUI: {
            deps: ['jquery']
        },
        JqueryTemplate: {
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
        BackboneUndo: {
            deps: ['jquery', 'Underscore', 'Backbone'],
            exports: 'BackboneUndo'
        },
        Bione: {
            deps: ['jquery', 'JSON2', 'Ligerui', 'LigeruiExpand']
        },
        Validate: {
        	deps: ['jquery'],
            exports: 'Validate'
        },
        ValidateMessage: {
        	deps: ['jquery', 'Validate']
        },
        UUID: {
            exports: 'UUID'
        }
    }
});
require(['../plugin/js/template/app'], function(App){
    new App.AppView();
});