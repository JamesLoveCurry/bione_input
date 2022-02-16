/*!
 * 参数模版中设计器的布局模块
 * 
 */
define(['jquery', '../template/effects', '../template/util', 'JSON2', 'Ligerui', 'LigeruiExpand', 'Bione', 'JqueryTemplate'], function($, Effects, Util, JSON2){
    $(function () {
        // BIONE.showLoading();
        // 添加整个设计器模版
        $('#designer-template').tmpl().appendTo("body");
        // $('#designerCanvas').append($('#designer-template-empty').tmpl());
        // 添加左侧面板
        $('#left').ligerAccordion({});
        
        $('#temp #tree').ligerTree({
        	checkbox: false,
        	url: Util.getContextPath() + '/report/frame/param/templates/getAyncTree.json',
        	onSuccess : function () {
        		Effects.createTemplatesDraggable();
        	}
        });
        // 添加整体布局
        $('#designer_layout').ligerLayout({
            topHeight: 28,
            leftWidth: 178,
            minLeftWidth: 80,
            allowLeftCollapse: false,
            allowLeftResize: false
        });
        
        $('#temp').width($('.l-layout-left').width()).height($('.l-layout-left').height()- 28 * 3 + 8);
        
        // 修正一些css效果
        $('.l-layout-left .l-layout-header').removeClass('l-layout-header');
        $('.l-layout-header').css('height', '28').css('text-align', 'center');
        $('.l-layout-header').off('hover');
        // 添加工具栏
        $('#topToolbar').ligerToolBar({ items: [
            {
                id:'saveBtn', text: '保存', click: function (){
                	//edit by fangjuan 2014-07-22  解决liger.get("paramJson") 取不到值的问题
                    window.parent.frames["paramtmpBox"].$("#mainform input[name=paramJson]").val(JSON2.stringify(window.app.template._getComponentes().toJSON()));
                    window.parent.frames["paramtmpBox"].$("#mainform input[name=paramJsonText]").val("已定义参数模版");
                    //edit end
                    window.parent.frames["paramtmpBox"].paramDialog.close();
                    //修复ie问题
                    if (window.parent.frames["paramtmpBox"]) {
                    	window.parent.frames["paramtmpBox"].paramDialog.destroy();
                    }
                }, icon:'save'},
                { line:true },
                { id:'closeBtn', text: '关闭', click: function(){
                	window.parent.frames["paramtmpBox"].paramDialog.close();
                	//修复ie下的bug
                	if (window.parent.frames["paramtmpBox"].paramDialog) {
                		window.parent.frames["paramtmpBox"].paramDialog.destroy();
                	}
                }, icon:'close' },
                { line:true },
                { id: 'clearBtn', text: '清空', click: function(){
                	var models = window.app.template._getComponentes().models, i;
                	for (i = models.length - 1 ; i >= 0 ; i--) {
                		window.app.template.removeComponent(models[i]);
                	}
                	$("#comp").prev().find(".l-accordion-toggle-close").trigger("click");
                }, icon:'delete' },
                { line:true },
                { id:'undoBtn', text: '撤销', click: function(){
                    window.app.undo();
                }, icon:'failed' },
                { line:true },
                { id: 'redoBtn', text: '还原', click: function(){
                    window.app.redo();
                }, icon:'resize' },
                { line:true },
                { id: 'previewBtn', text: '预览', click: function(){
                    $.ligerDialog.open({ title: '预览', url: Util.getContextPath() + '/report/frame/param/templates/view', width: ($(document).width() < 800 ? $(document).width() - 4 : 800), height: ($(document).height() < 600 ? $(document).height() - 4 : 600), allowClose : true, isResize: true });
                }, icon:'image' }
                ]
            });
        // 背景图片被样式覆盖，可以删除该样式
        $('#topToolbar').removeClass('l-layout-content');
        // 重新设置设计区的高度
        $('#designerCanvas').height($('#center').height() - 2);
        // 使控件可拖拽
        Effects.createComponentesDraggable();

        // BIONE.hideLoading();
    });
    return {};
});