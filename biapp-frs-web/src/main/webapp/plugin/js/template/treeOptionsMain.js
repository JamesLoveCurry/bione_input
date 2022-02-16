require.config({
    baseUrl: '../../../../js',
    paths: { 
        jquery: 'jquery/jquery-1.7.1.min',
        JSON2: 'bione/json2.min',
        Underscore: 'backbone/underscore',
        Backbone: 'backbone/backbone',
        BackboneUndo: 'backbone/Backbone.Undo',
        Ligerui: 'ligerUI/ligerui.all',
        LigeruiExpand: 'ligerUI/ligerui.expand',
        JqueryTemplate: 'backbone/jquery.tmpl',
        Codemirror: 'codemirror/codemirror',
        CodemirrorSql: 'codemirror/sql',
        SqlFormat: 'jquery/jquery.format'
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
        BackboneUndo: {
            deps: ['jquery', 'Underscore', 'Backbone'],
            exports: 'BackboneUndo'
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
        Codemirror: {
            exports: 'CM'
        },
        CodemirrorSql: {
            deps: ['Codemirror']
        },
        SqlFormat: {
            deps: ['jquery']
        }
    }
});
require(['../plugin/js/template/treeOptions'], function(TreeOptions){
    new TreeOptions.OptionsView();
});