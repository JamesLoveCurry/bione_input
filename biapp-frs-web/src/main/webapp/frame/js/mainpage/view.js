/*!
 * 参数模版中的运行时展示模块
 * 
 */
define(['jquery', 'JSON2', 'Underscore', 'Backbone', '../template/model', '../template/convertor', 'Ligerui', 'LigeruiExpand', 'Bione', 'Validate', 'ValidateMessage', 'ValidateExpand', 'Metadata', 'moment/moment.min'], function ($, JSON2, _, Backbone, Model, Convertor) {

    //设置form.validate依赖的$.metadata属性attr
	$.metadata.setType("attr", "validate");
    
	var View = Backbone.View.extend({
		
        initialize: function ($target, options) {
        	_.bindAll(this, 'render');
            this.el = $target;
            this.template = new Model.Template();
            this.componentes = this.template._getComponentes();
            //把options传递过来的初始化数据传入对象列表
            this.componentes.add(options['data']);
            this.render(options);
            
        },
        
        render: function (options) {
            this.el.ligerForm(Convertor.convert(this.componentes.toFormPorperties(), 'real',options));
            //使用BIONE的验证方法对ligerForm添加验证
            BIONE.validate(this.el);
            
            
            return this;
        }
    });

    return {View: View};
});