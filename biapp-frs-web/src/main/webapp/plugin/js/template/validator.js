/*!
 * 设计器中文本框验证弹出框的验证模块
 * 
 */
define(['jquery', 'Ligerui', 'Validate', 'ValidateMessage', 'Metadata'], function () {
	function validate ($form) {
        $form.validate({

            // 离开焦点进行验证
            onfocusout: function (element) {
                this.element(element);
            },
            
            // 按下键盘进行验证
        	onkeyup: function (element) {
                this.element(element);
            },
        	
            // 不忽略隐藏域
        	ignore: "",
        	
            // 发生验证错误回调的方法
            errorPlacement: function (lable, element) {
                var name = element.parentsUntil("table").parent().find("#name").val();
                lable.html() !== "" && (window.AppTip[name] = lable.html());
            }
        });
	};

	return {
		validate : validate
	};
});