/*!
 * 参数模版中设计器的特效工具模块
 * 
 */
define(['jquery', 'JqueryUI','../template/layout'], function() {

    var that = this;

    var placeHolderWidth = 120;

    // 移除新参数占位效果
    function _removePlaceHolder () {
        // if (that.flash) {
        //     clearInterval(that.flash);
        //     that.flash = null;
        // }
        var li = $('#placeHolder');
        if (li.hasClass('l-fieldcontainer-first')) {
            li.next('li').addClass('l-fieldcontainer-first')
        }
        li.remove();
    };

    // 创建属性区域的可拖拽效果
    function createComponentesDraggable () {
        $("#componentes li").draggable({
            revert: true,
            appendTo: "#designerCanvas",
            helper: function (event) {
                return $("<span style='border-radius:4px;border: 2px solid #E9E5ED;height:28px;width:120px;text-align:center; display:block;z-index:10;'></span>").append($(this).clone());
            }
        });
       
    };
    
    function createTemplatesDraggable () {
    	 $("#temp li img[src$='report.png']").each(function (i, item){
    		 var li = $(item).parents("li[outlinelevel]").get(0);
    		 $(li).draggable({
    			 revert: true,
    			 appendTo: "#designerCanvas",
    			 helper: function (event) {
    				 var c = $(this).clone();
    				 c.find(".l-line, .l-note").remove();
    				 return $("<span style='border-radius:4px;border: 2px solid #E9E5ED;height:28px;width:120px;text-align:center; display:block;z-index:10;'></span>").append(c);
    			 }
    		 });
    	 });
    };

    // 设置空白的区域可以拖入控件
    function createEmptyFormDroppable (dropFunc) {
        $(".ul-empty").droppable({
            accept: "#componentes li ,#temp .ui-draggable",
            drop: dropFunc,
            over: function(event, ui) {
                $this = $(this);
                _removePlaceHolder();
                var $placeHolder = $("<li id='placeHolder' class='ui-sortable-placeholder ui-corner-all' style='width:" + placeHolderWidth + "px;'></li>");
                $this.append($placeHolder);
            },
            out: function(event, ui) {
                _removePlaceHolder();
            }
        });
    };

    // 设置ligerForm可以拖入控件
    function createLigerFormDroppable (dropFunc) {
        $(".l-fieldcontainer").droppable({
            accept: "#componentes li,#temp .ui-draggable",//edit by fangjuan 20140725
            activeClass: "ui-state-hover ui-corner-all",
            // hoverClass: "template-hover",
            drop:  function (event, ui) {
                this.isLeft = false;
                var index = $(".l-fieldcontainer").index(this);
                if(!_isLeft(ui, $(this))) index += 1;
                dropFunc.call(this, event, ui, index);
            },
            over: function (event, ui) {
                _removePlaceHolder();
                _addPlaceHolder($(this), ui, dropFunc);
            },
            out: function (event, ui) {
                _removePlaceHolder();
                var $this = $(this);
                // 根据isLeft标识位判断是否需要改变位置
                if ($this.hasClass("li-selected") && isLeft) {
                    _resetBtnPosition($this.parent().find("#delBtn"), -placeHolderWidth);
                    _resetBtnPosition($this.parent().find("#addBtn"), -placeHolderWidth);
                }
            }
        });
    };

    // 拖入或移除新参数时，重置参数功能按钮的位置
    function _resetBtnPosition ($btnDiv, width) {
        $btnDiv.css("left", ($btnDiv.position().left + width));
    };

    // 添加新参数占位效果
    function _addPlaceHolder ($target, ui, dropFunc) {
        var $placeHolder = $("<li id='placeHolder' class='ui-sortable-placeholder ui-corner-all' style='width:" + placeHolderWidth + "px;'></li>");
        var index = $(".l-fieldcontainer").index($target);
        if (_isLeft(ui, $target)) {
            // 修改当前li的样式，否则会不恰当的换行
            if ($target.hasClass('l-fieldcontainer-first')) {
                $target.removeClass('l-fieldcontainer-first');
                $placeHolder.addClass('l-fieldcontainer-first');
            }
            $target.before($placeHolder);
            if ($target.hasClass("li-selected")) {
                _resetBtnPosition($target.parent().find("#delBtn"), placeHolderWidth);
                _resetBtnPosition($target.parent().find("#addBtn"), placeHolderWidth);
            }
            this.isLeft = true;
            // that.flash = setInterval(_flashBorder($target, 'left'), 500);
        } else {
            // that.flash = setInterval(_flashBorder($target, 'right'), 500);
            $target.after($placeHolder);
            // 当前对象位置序号加一
            index += 1;
            this.isLeft = false;
        }
        $placeHolder.droppable({
            accept: "#componentes li",
            drop: function (event, ui) {
                this.isLeft = false;
                _removePlaceHolder();
                dropFunc.call(this, event, ui, index);
                // dropFunc();
            }
        });
    };

    // 判断是否当前拖拽参数在目标参数的左面
    function _isLeft (ui, $target) {
        return ui.offset.left < $target.offset().left + $target.width() / 2 - 20;
    }

    // 闪烁边框来标识拖入控件
    function _flashBorder ($target, position) {
        return function () {
            var originalColor = "rgb(153, 153, 153)", lightColor = "rgb(248, 137, 63)";
            if ($target.css("border-" + position + "-color") == lightColor) {
                $target.css("border-" + position + "-color", originalColor);
            } else {
                $target.css("border-" + position + "-color", lightColor);
            }
        };
    };

    // 设置ligerForm中的控件可以通过拖拽修改位置
    function createLigerFormSortable (updateFunc) {
        $(".l-fieldcontainer-first").each(function() {
            $(this).css("display", "inline-block");
            $(this).css("float", "");
            var parent = $(this).parent();
            if (!parent.hasClass("connectedSortable")) {
                parent.addClass("connectedSortable");
            }
        });
        $(".connectedSortable").sortable({
            placeholder: "ui-sortable-placeholder",
            connectWith: ".connectedSortable",
            start: function( event, ui ) {
            	_removeBtn(ui.item.parent());
            },
            update : updateFunc
        }).disableSelection();

    };
    
    // 单击指定id面板
    function clickAccordion (divId) {
        $("#" + divId).prev().find(".l-accordion-toggle-close").trigger("click");
    };

    // 设置每个参数的UI事件
    function createLiEvent ($location, template, createPropertyViewFunc) {
        var g = this;
        //添加hover事件
        $location.find(".l-fieldcontainer").hover(function () {
            $(this).addClass('li-hover');
        }, function () {
            $(this).removeClass('li-hover');
        });
        // 添加参数li上的click事件
        $location.find(".l-fieldcontainer").click(function() {
            var $this = $(this);
            // 如果当前是选中状态
            if ($this.hasClass('li-selected')) {
            	g.selectedCid = null;
            	//删除功能按钮
            	_removeBtn($this);
                //删除选择框
                $this.removeClass('li-selected');
            	//触发还未离开焦点的input属性框change事件
            	g.propertyView.$("input:focus").trigger('change');
                //删除属性区
                g.propertyView.remove();
                // 单击组件面板
                clickAccordion("comp");
                // $("#propForm").children().remove();
            } else {
            	var $that = null, selectedCid = g.selectedCid;
                // 查找其他已经被选中的参数
                if ($location.find(".li-selected").length > 0) {
                	$that = $location.find(".li-selected");
                }

                // 对已经选中的参数做处理
                if ($that) {
                	//删除功能按钮
                    _removeBtn($that);
                    //删除选择框
                    $that.removeClass('li-selected');
                    //触发还未离开焦点的input属性框change事件
                    g.propertyView.$("input:focus").trigger('change');
                    //删除属性区
                    g.propertyView.remove();
                }
                
                $this.addClass('li-selected');
                _addBtn(template, $this);
                
                clickAccordion("prop");
                
                var cid = $this.find("[cid]").attr("cid");
                g.selectedCid = cid;
                
                // 重新渲染属性区
                if (selectedCid != cid) {
                    g.propertyView = createPropertyViewFunc.call(this, template.getComponent(cid));
                    g.propertyView.render();
                }
            }
        });
    };

    //删除参数的功能按钮
    function _removeBtn ($this) {
        $this.parent().find("#delBtn").remove();
        $this.parent().find("#addBtn").remove();
    };

    // 添加参数的功能按钮
    function _addBtn (template, $this) {
        _addDelBtn(template, $this);
        _addCloneBtn(template, $this);
    };

    // 添加参数删除功能按钮
    function _addDelBtn (template, $selectedLi) {
        var img = $("<img src='../../../../images/classics/template/delete.png'/>").attr('title', '删除');
        var div = $("<div id='delBtn' style='display:block;position: absolute;clear: both;cursor: pointer;z-index:5;'/>");
        img.click(function () {
            var model = template.getComponent($selectedLi.find("[cid]").attr("cid"));
            template.removeComponent(model);
            clickAccordion("comp");
        });
        div.css("left", ($selectedLi.position().left + $selectedLi.width() + 2)).css("top", $selectedLi.position().top + 16);
        div.append(img);
        $selectedLi.after(div);
    };

    // 添加参数拷贝功能按钮
    function _addCloneBtn (template, $selectedLi) {
        var img = $("<img src='../../../../images/classics/template/add.png'/>").attr('title', '复制');
        var div = $("<div id='addBtn' style='display:block;position: absolute;clear: both;cursor: pointer;z-index:5;'/>");
        img.click(function () {
            var model = template.getComponent($selectedLi.find("[cid]").attr("cid"));
            var cloneModel = template.cloneComponent(model);
            template.insertComponent(cloneModel, template.indexOfComponent(model) + 1);
        });
        div.css("left", ($selectedLi.position().left + $selectedLi.width() + 2)).css("top", $selectedLi.position().top - 1);
        div.append(img);
        $selectedLi.after(div);
    };

    // 重新选择刷新界面前选择的参数
    function selectHistoryLi ($location) {
        if (this.selectedCid) {
            var parents = $location.find("[cid=" + this.selectedCid + "]").parentsUntil("li.l-fieldcontainer");
            var target = $(parents[parents.length-1]).parent();
            target.trigger("click");
        }
    };

    return {
        createComponentesDraggable : createComponentesDraggable,
        createTemplatesDraggable : createTemplatesDraggable,
        createEmptyFormDroppable : createEmptyFormDroppable,
        createLigerFormDroppable : createLigerFormDroppable,
        createLigerFormSortable : createLigerFormSortable,
        createLiEvent : createLiEvent,
        selectHistoryLi : selectHistoryLi
    };
});