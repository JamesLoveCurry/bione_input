//autor:xch
//不外抛给普通开发的的一些工具(私有)方法
(function ($) {
    window['FreeUtil'] = {};
    FreeUtil.ControlURL = "/frs/yufreejs";
    FreeUtil.RemoteCallPath = "/frs/yuformat";
    FreeUtil.RemoteCallMethod = "/doClassMethodCall";
    FreeUtil.CommDMOClassName = "com.yusys.bione.plugin.yuformat.service.CommDMO";
    FreeUtil.JSPBuilderClassName = "com.yusys.bione.plugin.yuformat.web.FreeJSPBuilder";
    FreeUtil.CommServicePath = "com.yusys.bione.plugin.yuformat.service."; //通用Service的路径
    FreeUtil.CommXMLPath = "/bione-plugin/xml";  //xml路径
    FreeUtil.DownloadFileUrl = "/frs/yuformat/downloadFile";  //下载文件url



    //远程调用工具方法
    FreeUtil.remoteCall = function (_actname, _pars, _callBack, _isAsync) {
        var isAsync = false;
        if (_isAsync) {
            isAsync = true;
        }
        $.ajax({
            cache: false,
            async: isAsync,
            url: v_context + FreeUtil.RemoteCallPath + _actname,
            dataType: "json",
            type: "post",
            data: _pars,
            success: _callBack,
            error: function (result, b) {
                console.log('发现系统错误 <BR>错误码：' + result.status);  //
            }
        });
    };

    //通用万通的调用!反射!
    //比如var v_rt = doClassMethodCall("com.yuchengtech.base.utils.YuFORMATBSUtil2","testMethod",v_sql);
    //这个方法的好处是,不要再折腾Control中的映射了,对应参数名，如果改参数名或加参数,则要改一堆,还要去文本搜索寻找对应的路径,现在从入口可以直接看到是哪个类与方法!
    FreeUtil.doClassMethodCall = function (_className, _methodName, _parJSO) {
        var arrayEncrypt = FreeUtil.encrypt(_parJSO);
        var v_parstr = JSON.stringify(arrayEncrypt);  //将对象转换成字符串
        console.log("远程调用" + _className + "●" + _methodName + "【" + v_parstr + "】");

        var v_par = {_class: _className, _method: _methodName, _parstr: v_parstr};  //
        var v_returnData;  //返回值
        FreeUtil.remoteCall(FreeUtil.RemoteCallMethod, v_par, function (_rtData) {
            v_returnData = _rtData;  //
        }, false);  //同步,必须做完才返回

        if (typeof v_returnData.Exception != "undefined" || v_returnData.Exception != null) {
            console.error("发生服务器端异常:" + v_returnData.Exception);
            throw("发生服务器端异常:<br>\n" + v_returnData.Exception);  //重抛服务器端异常
        } else {
            return v_returnData;  //
        }
    };

    //异步调用!
    FreeUtil.doClassMethodCall2 = function (_className, _methodName, _parJSO, callBackFunction) {
        var arrayEncrypt = FreeUtil.encrypt(_parJSO);
        var v_parstr = JSON.stringify(arrayEncrypt);  //将对象转换成字符串
        console.log("远程调用" + _className + "●" + _methodName + "【" + v_parstr + "】");
        var v_par = {_class: _className, _method: _methodName, _parstr: v_parstr};  //
        FreeUtil.remoteCall(FreeUtil.RemoteCallMethod, v_par, function (_rtData) {
            if (typeof _rtData.Exception != "undefined" || _rtData.Exception != null) {
                console.error("发生服务器端异常:" + _rtData.Exception);
                throw("发生服务器端异常:<br>\n" + _rtData.Exception);  //重抛服务器端异常
            } else {
                callBackFunction(_rtData);  //把返回值传给回调函数!
            }
        }, true);  //异步!!
    };


    //直接查询数据,返回的是一个数组
    FreeUtil.getHashVOs = function (_sql) {
        var jso_par = {SQL: _sql};
        var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.CommDMOClassName, "getHashVOsBySQL", jso_par);
        return jso_rt["DATA"];  //这直接是一个数组
    };

    //直接查询数据,返回的是一个数组(根据数据源)
    FreeUtil.getHashVOsByDS = function (_sql, _ds) {
        var jso_par = {SQL: _sql, DS: _ds};
        var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.CommDMOClassName, "getHashVOsBySQLAndDs", jso_par);
        return jso_rt["DATA"];  //这直接是一个数组
    };
    
    //创建多页签
    FreeUtil.createTabb = function (_divid, _titles, _width) {
        var dom_root = document.getElementById(_divid);  //
        var str_html = "";
        var tabWidth = 120;  //默认页签宽度是120
        var isWidthArray = false;  //宽度是否是数组

        if (typeof _width != "undefined" && _width != null) {  //如果指定了宽度!
            if (Array.isArray(_width)) {  //如果是数组
                isWidthArray = true;  //
            } else {  //如果不是数组,是个常量
                tabWidth = "" + _width;
            }
        }

        var str_tabOption = "";  //整个页签的配置
        str_tabOption = str_tabOption + "tabWidth:" + tabWidth;  //

        //外面的整个tab定义
        str_html = str_html + "<div id=\"" + _divid + "_tabb\" class=\"easyui-tabs\" data-options=\"" + str_tabOption + "\" style=\"width:100%;height:100%\">\r\n";

        //各个页签子项定义
        for (var i = 1; i <= _titles.length; i++) {
            var str_itemTitle = _titles[i - 1];  //标题
            var str_icon = "";  //图标
            var li_pos = _titles[i - 1].indexOf("/");  //看是否有斜杠
            if (li_pos > 0) { //如果有斜杠
                str_itemTitle = _titles[i - 1].substring(0, li_pos);
                str_icon = _titles[i - 1].substring(li_pos + 1, _titles[i - 1].length);
            }

            //各个页签的配置
            var str_tabItemOption = "";
            str_tabItemOption = str_tabItemOption + "title:'" + str_itemTitle + "',";

            //如果有图标
            if (str_icon != "") {
                str_tabItemOption = str_tabItemOption + "iconCls:'" + str_icon + "',";
            }

            //如果是数组
            if (isWidthArray) {
                str_tabItemOption = str_tabItemOption + "tabWidth:" + _width[i - 1] + ",";
            }

            if (str_tabItemOption != "") {
                str_tabItemOption = str_tabItemOption.substring(0, str_tabItemOption.length - 1);
            }

            //输出html
            str_html = str_html + "<div id=\"" + _divid + "_" + i + "\"  data-options=\"" + str_tabItemOption + "\" style=\"padding-top:5px\"></div>\r\n";  //
        }

        str_html = str_html + "</div>\r\n";
        dom_root.innerHTML = str_html;  //

        //创建JS代码
        var str_jsCode = "\r\n";  //
        str_jsCode = str_jsCode + "var " + _divid + "_tabb = $(\"#" + _divid + "_tabb\");\r\n";

        var newJSObj = document.createElement("script");  //一定要用这个创建,否则浏览器不解析
        newJSObj.id = _divid;
        newJSObj.type = "text/javascript";  //一定设置类型
        newJSObj.text = str_jsCode;  //直接设置文本
        document.body.appendChild(newJSObj);  //在</body>前加上
    };

    //创建多页签(带按钮)
    FreeUtil.createTabbByBtn = function (_divid, _titles, _btns, _isNorth, _width) {
        var dom_root = document.getElementById(_divid);  //
        var str_html = "";
        str_html = str_html + "<div class=\"easyui-layout\" data-options=\"border:false\" style=\"width:100%;height:100%;overflow:hidden\">\r\n";

        var str_region = "south";  //默认按钮在下面!
        var str_align = "center";  //排序
        if (typeof _isNorth != "undefined" && _isNorth) {
            str_region = "north";  //在上面
            str_align = "left";  //居左
        }
        str_html = str_html + "<div data-options=\"region:'" + str_region + "',border:false\" style=\"height:45px;text-align:" + str_align + ";padding-top:5px;overflow:hidden;\">\r\n";

        //拼出各个按钮
        if (typeof _btns != "undefined") {
            for (var i = 0; i < _btns.length; i++) {
                var btn_defs = _btns[i].split("/");
                var str_btnText = btn_defs[0];
                var str_btnAction = "";
                var str_btnImg = "";
                if (btn_defs.length >= 2) {
                    str_btnAction = btn_defs[1];
                }
                if (btn_defs.length >= 3) {
                    str_btnImg = btn_defs[2];
                }
                str_html = str_html + "<a id=\"" + _divid + "_TabbBtn" + (i + 1) + "\"   href=\"JavaScript:" + str_btnAction + "();\" class=\"easyui-linkbutton\" ";
                if (str_btnImg != "") {
                    str_html = str_html + "data-options=\"iconCls:'" + str_btnImg + "'\" ";
                }
                str_html = str_html + "style=\"width:100px\">" + str_btnText + "</a>\r\n";
            }
        }

        str_html = str_html + "</div>\r\n";  //按钮栏结束

        //原来的分割器,放在中间
        str_html = str_html + "<div data-options=\"region:'center',border:false\">\r\n"  //center开始

        var tabWidth = 120;  //默认页签宽度是120
        var isWidthArray = false; //
        if (typeof _width != "undefined" && _width != null) {  //如果指定了宽度!
            if (Array.isArray(_width)) {  //如果是数组
                isWidthArray = true;  //
            } else {  //如果不是数组,是个常量
                tabWidth = "" + _width;
            }
        }

        //整个页签的配置
        var str_tabOption = "";
        str_tabOption = str_tabOption + "tabWidth:" + tabWidth;  //

        //外面的整个tab定义
        str_html = str_html + "<div id=\"" + _divid + "_tabb\" class=\"easyui-tabs\" data-options=\"" + str_tabOption + "\" style=\"width:100%;height:100%\">\r\n";

        //各个页签子项定义
        for (var i = 1; i <= _titles.length; i++) {
            var str_itemTitle = _titles[i - 1];  //标题
            var str_icon = "";  //图标
            var li_pos = _titles[i - 1].indexOf("/");  //看是否有斜杠
            if (li_pos > 0) { //如果有斜杠
                str_itemTitle = _titles[i - 1].substring(0, li_pos);
                str_icon = _titles[i - 1].substring(li_pos + 1, _titles[i - 1].length);
            }

            //各个页签的配置
            var str_tabItemOption = "";
            str_tabItemOption = str_tabItemOption + "title:'" + str_itemTitle + "',";

            //如果有图标
            if (str_icon != "") {
                str_tabItemOption = str_tabItemOption + "iconCls:'" + str_icon + "',";
            }

            //如果是数组
            if (isWidthArray) {
                str_tabItemOption = str_tabItemOption + "tabWidth:" + _width[i - 1] + ",";
            }

            if (str_tabItemOption != "") {
                str_tabItemOption = str_tabItemOption.substring(0, str_tabItemOption.length - 1);
            }

            //输出html
            str_html = str_html + "<div id=\"" + _divid + "_" + i + "\"  data-options=\"" + str_tabItemOption + "\" style=\"padding-top:5px\"></div>\r\n";  //
        }

        str_html = str_html + "</div>\r\n";  //多页签本身的结尾

        str_html = str_html + "</div>\r\n";  //center结束
        str_html = str_html + "</div>\r\n";  //最外结尾

        dom_root.innerHTML = str_html;  //

        //创建JS代码
        var str_jsCode = "\r\n";  //
        str_jsCode = str_jsCode + "var " + _divid + "_tabb = $(\"#" + _divid + "_tabb\");\r\n";

        var newJSObj = document.createElement("script");  //一定要用这个创建,否则浏览器不解析
        newJSObj.id = _divid;
        newJSObj.type = "text/javascript";  //一定设置类型
        newJSObj.text = str_jsCode;  //直接设置文本
        document.body.appendChild(newJSObj);  //在</body>前加上
    };

    //创建分割器,左右分割
    FreeUtil.createSplit_X = function (_divid, _location, _isCanDrag) {
        var dom_root = document.getElementById(_divid);  //
        var str_html = "";
        str_html = str_html + "<div class=\"easyui-layout\" style=\"width:100%;height:100%;\">\r\n";
        str_html = str_html + "<div id=\"" + _divid + "_A\" data-options=\"region:'west',split:" + _isCanDrag + ",border:false\" title=\"\" style=\"width:" + _location + "px;\"></div>\r\n";
        str_html = str_html + "<div id=\"" + _divid + "_B\" data-options=\"region:'center',border:false\"></div>\r\n";
        str_html = str_html + "</div>\r\n";
        dom_root.innerHTML = str_html;
    };

    //创建分割器,左右分割
    FreeUtil.createSplit_X_ByBtn = function (_divid, _location, _btns, _isCanDrag) {
        var dom_root = document.getElementById(_divid);  //
        var str_html = "";
        str_html = str_html + "<div class=\"easyui-layout\" data-options=\"border:false\" style=\"width:100%;height:100%;overflow:hidden\">\r\n";
        str_html = str_html + "<div data-options=\"region:'south',border:false\" style=\"height:45px;text-align:center;padding-top:5px;overflow:hidden;\">\r\n";

        //拼出各个按钮
        if (typeof _btns != "undefined") {
            for (var i = 0; i < _btns.length; i++) {
                var btn_defs = _btns[i].split("/");
                var str_btnText = btn_defs[0];
                var str_btnAction = "";
                var str_btnImg = "";
                if (btn_defs.length >= 2) {
                    str_btnAction = btn_defs[1];
                }
                if (btn_defs.length >= 3) {
                    str_btnImg = btn_defs[2];
                }
                str_html = str_html + "<a id=\"" + _divid + "_SplitBtn" + (i + 1) + "\"   href=\"JavaScript:" + str_btnAction + "();\" class=\"easyui-linkbutton\" ";
                if (str_btnImg != "") {
                    str_html = str_html + "data-options=\"iconCls:'" + str_btnImg + "'\" ";
                }
                str_html = str_html + "style=\"width:80px\">" + str_btnText + "</a>\r\n";
            }
        }
        str_html = str_html + "</div>\r\n";

        //原来的分割器,放在中间
        str_html = str_html + "<div data-options=\"region:'center',border:false\">\r\n"
        str_html = str_html + "  <div class=\"easyui-layout\" style=\"width:100%;height:100%;\">\r\n";
        str_html = str_html + "    <div id=\"" + _divid + "_A\" data-options=\"region:'west',split:" + _isCanDrag + ",border:false\" title=\"\" style=\"width:" + _location + "px;\"></div>\r\n";
        str_html = str_html + "    <div id=\"" + _divid + "_B\" data-options=\"region:'center',border:false\"></div>\r\n";
        str_html = str_html + "  </div>\r\n";
        str_html = str_html + "</div>\r\n";
        str_html = str_html + "</div>\r\n";
        dom_root.innerHTML = str_html;
    };

    //创建分割器
    FreeUtil.createSplit_Y = function (_divid, _location, _isCanDrag) {
        var dom_root = document.getElementById(_divid);  //
        var str_html = "";
        str_html = str_html + "<div class=\"easyui-layout\" style=\"width:100%;height:100%;\">\r\n";
        str_html = str_html + "<div id=\"" + _divid + "_A\" data-options=\"region:'north',split:" + _isCanDrag + ",border:false\" style=\"height:" + _location + "px\"></div>\r\n";
        str_html = str_html + "<div id=\"" + _divid + "_B\" data-options=\"region:'center',border:false\"></div>\r\n";
        str_html = str_html + "</div>\r\n";
        dom_root.innerHTML = str_html;
    };

    //创建分割器
    FreeUtil.createSplit_Y_ByBtn = function (_divid, _location, _btns, _isCanDrag) {
        var dom_root = document.getElementById(_divid);  //
        var str_html = "";
        str_html = str_html + "<div class=\"easyui-layout\" data-options=\"border:false\" style=\"width:100%;height:100%;overflow:hidden\">\r\n";
        str_html = str_html + "<div data-options=\"region:'south',border:false\" style=\"height:45px;text-align:center;padding-top:5px;overflow:hidden;\">\r\n";

        //拼出各个按钮
        if (typeof _btns != "undefined") {
            for (var i = 0; i < _btns.length; i++) {
                var btn_defs = _btns[i].split("/");
                var str_btnText = btn_defs[0];
                var str_btnAction = "";
                var str_btnImg = "";
                if (btn_defs.length >= 2) {
                    str_btnAction = btn_defs[1];
                }
                if (btn_defs.length >= 3) {
                    str_btnImg = btn_defs[2];
                }
                str_html = str_html + "<a id=\"" + _divid + "_SplitBtn" + (i + 1) + "\"   href=\"JavaScript:" + str_btnAction + "();\" class=\"easyui-linkbutton\" ";
                if (str_btnImg != "") {
                    str_html = str_html + "data-options=\"iconCls:'" + str_btnImg + "'\" ";
                }
                str_html = str_html + "style=\"width:80px\">" + str_btnText + "</a>\r\n";
            }
        }
        str_html = str_html + "</div>\r\n";

        //原来的分割器,放在中间
        str_html = str_html + "<div data-options=\"region:'center',border:false\">\r\n"
        str_html = str_html + "  <div class=\"easyui-layout\" style=\"width:100%;height:100%;\">\r\n";
        str_html = str_html + "    <div id=\"" + _divid + "_A\" data-options=\"region:'north',split:" + _isCanDrag + ",border:false\" style=\"height:" + _location + "px\"></div>\r\n";
        str_html = str_html + "    <div id=\"" + _divid + "_B\" data-options=\"region:'center',border:false\"></div>\r\n";
        str_html = str_html + "  </div>\r\n";
        str_html = str_html + "</div>\r\n";
        str_html = str_html + "</div>\r\n";
        dom_root.innerHTML = str_html;
    };


    //创建分割器
    FreeUtil.createSpanByBtn = function (_divid, _btns, _isNorth, _isBtnAlignLeft) {
        var dom_root = document.getElementById(_divid);  //
        var str_html = "";
        str_html = str_html + "<div class=\"easyui-layout\" data-options=\"border:false\" style=\"width:100%;height:100%;overflow:hidden\">\r\n";

        var str_region = "south";
        if (typeof _isNorth != "undefined" && _isNorth) {
            str_region = "north";
        }

        var str_btnAlign = "center";
        if (typeof _isBtnAlignLeft != "undefined" && _isBtnAlignLeft) {
            str_btnAlign = "left";
        }
        str_html = str_html + "<div data-options=\"region:'" + str_region + "',border:false\" style=\"height:45px;text-align:" + str_btnAlign + ";padding-top:5px;overflow:hidden;\">\r\n";

        //拼出各个按钮
        if (typeof _btns != "undefined") {
            for (var i = 0; i < _btns.length; i++) {
                var btn_defs = _btns[i].split("/");
                var str_btnText = btn_defs[0];  //按钮文字
                var str_btnAction = "";
                var str_btnImg = "";
                if (btn_defs.length >= 2) {
                    str_btnAction = btn_defs[1];
                }
                if (btn_defs.length >= 3) {
                    str_btnImg = btn_defs[2];
                }
                var str_btnWidth = "85";
                var li_pos = str_btnText.indexOf("(");  //是否有宽度定义
                if (li_pos > 0) {
                    str_btnWidth = str_btnText.substring(li_pos + 1, str_btnText.length - 1);
                    str_btnText = str_btnText.substring(0, li_pos); //
                }
                str_html = str_html + "<a id=\"" + _divid + "_" + str_btnAction + "\" onclick=\"" + str_btnAction + "();\"  href=\"#\" class=\"easyui-linkbutton\" ";
                if (str_btnImg != "") {
                    str_html = str_html + "data-options=\"iconCls:'" + str_btnImg + "'\" ";
                }
                str_html = str_html + "style=\"width:" + str_btnWidth + "px\">" + str_btnText + "</a>\r\n";
            }
        }
        str_html = str_html + "</div>\r\n";

        //原来的分割器,放在中间
        str_html = str_html + "<div data-options=\"region:'center',border:false\">\r\n"
        str_html = str_html + "  <div id=\"" + _divid + "_A\" style=\"width:100%;height:100%;overflow:auto\"></div>\r\n";
        str_html = str_html + "</div>\r\n";
        str_html = str_html + "</div>\r\n";
        dom_root.innerHTML = str_html;
    };


    //从服务器端取得UUID
    FreeUtil.getUUIDFromServer = function () {
        var jso_data = FreeUtil.doClassMethodCall(FreeUtil.CommDMOClassName, "getUUID", {});
        return jso_data.UUID;  //
    };

    //列表数据比较特殊,有一个唯一标识行的隐藏字段叫_rownum,数据库中没有,需要在前台补上
    FreeUtil.appendBillListDataRowNum = function (_data) {
        var arr_rows = _data.rows;
        for (var i = 0; i < arr_rows.length; i++) {
            var jso_item = arr_rows[i];
            jso_item["_rownum"] = ("" + i);  //加上
        }
    };

    //根据类名反射取数,这个方法入参必须是JSObject,返回是HashVO[]
    FreeUtil.queryDataByClass = function (_billList, _className, _methodName, _jso_par, _isYiBu) {
        var str_templetcode = _billList.templetVO.templet_option.templetcode;  //取得模板编码
        var v_par = {templetcode: str_templetcode, className: _className, methodName: _methodName, pars: _jso_par};  //拼接条件

        var isYiBu = false;  //是否异步,默认是同步的
        if (typeof _isYiBu != "undefined") {
            isYiBu = _isYiBu;
        }

        if (isYiBu) {
            FreeUtil.doClassMethodCall2(FreeUtil.CommDMOClassName, "getBillListDataByClass", v_par, function (_rtData) {
                FreeUtil.appendBillListDataRowNum(_rtData);  //补上【_rownum】,以前在后台处理的,后来发现还是在前台处理更好
                _billList.datagrid('loadData', _rtData);
                _billList.datagrid('clearSelections');
            });
        } else {
            var jso_data = FreeUtil.doClassMethodCall(FreeUtil.CommDMOClassName, "getBillListDataByClass", v_par);
            FreeUtil.appendBillListDataRowNum(jso_data);  //补上【_rownum】,以前在后台处理的,后来发现还是在前台处理更好
            _billList.datagrid('loadData', jso_data);
            _billList.datagrid('clearSelections');
        }
    };


    //实际查询,拼SQL
    FreeUtil.queryDataByConditonReal = function (_grid, _cons, _isYiBu, _currPage, _orderBy, _isSpanXmlCons) {
        var str_templetcode = _grid.templetVO.templet_option.templetcode;  //取得模板编码
        var str_table = _grid.templetVO.templet_option.fromtable;  //取得模板编码
        var isPager = true;  //默认是分页
        if ("N" == _grid.templetVO.templet_option.list_ispagebar) {
            isPager = false;  //不分页
        }

        //先把表名拼出来
        var str_sql = "select * from " + str_table + " where 1=1 ";

        //如果列表在前端设置了强制条件则加上,即有时需要在前端再加上强制条件
        if (typeof _grid.forceSQLWhere != "undefined" && _grid.forceSQLWhere != null && _grid.forceSQLWhere != "") {
            str_sql = str_sql + " and " + _grid.forceSQLWhere;  //
        }

        //拼接条件
        if (_cons != undefined && _cons != null && _cons != "") {
            str_sql = str_sql + " and " + _cons;  //
        }

        //拼接模板中配置的条件
        //xml模板中定义的查询条件,如果有,则与传入的条件再合并!
        if (typeof _isSpanXmlCons != "undefined" && _isSpanXmlCons) {
            var str_xmlCons = _grid.templetVO.templet_option.querycontion;
            if (typeof str_xmlCons != "undefined" && str_xmlCons != null && str_xmlCons.trim() != "") {
                str_sql = str_sql + " and " + str_xmlCons;  //
            }
        }

        //处理排序,如果入参指定了排序,则使用入参的,否则使用模板中定义的
        if (typeof _orderBy != "undefined" && _orderBy != null && _orderBy != "") {
            str_sql = str_sql + " order by " + _orderBy;
        } else {
            var str_orderby = _grid.templetVO.templet_option.orderbys;
            if (typeof str_orderby != "undefined" && str_orderby != null && str_orderby != "") {
                str_sql = str_sql + " order by " + str_orderby;
            }
        }

        FreeUtil.queryDataByConditonRealBySQL(_grid, str_sql, _isYiBu, isPager, _currPage);
    };


    //实际执行的!
    FreeUtil.queryDataByConditonRealBySQL = function (_grid, _SQL, _isYiBu, _isPager, _currPage) {
        var str_templetcode = _grid.templetVO.templet_option.templetcode;  //取得模板编码
        //取得分页
        var li_onePageSize = 20;  //默认每页20条
        if ((typeof _grid.OnePageSize) != "undefined") {
            li_onePageSize = _grid.OnePageSize;  //采用实际值
        }

        //数据源名称,有可能为空
        var str_dsName = _grid.templetVO.templet_option.ds; //数据源
        var str_afterloadclass = _grid.templetVO.templet_option.afterloadclass;  //后续处理类,即可能后续处理.
        var v_par = {
            "templetcode": str_templetcode, //模板编码
            "isPager": _isPager,  //是否分页
            "currPage": _currPage,  //当前是第几页
            "onePageSize": li_onePageSize,  //当前是第几页
            "pagerType": _grid.pagerType,  //分页类型
            "SQL": _SQL,  //实际SQL
            "DataSourceName": str_dsName,  //数据源
            "AfterLoadClass": str_afterloadclass  //后续处理类
        };

        //如果是异步,则,则在回调函数中执行!
        if (_isYiBu) {
            FreeUtil.doClassMethodCall2(FreeUtil.CommDMOClassName, "getBillListData", v_par, function (jso_data) {
                FreeUtil.appendBillListDataRowNum(jso_data);  //补上【_rownum】,以前在后台处理的,后来发现还是在前台处理更好
                _grid.datagrid('loadData', jso_data);
                _grid.datagrid('clearSelections');
                _grid.CurrSQL = _SQL;
                _grid.CurrSQL2 = jso_data.realSQL;  //设置表格的当前SQL为此SQL,这样分页跳转时要这个!
                _grid.CurrSQL3 = jso_data.realSQL3;  //分页前的SQL,王小宇导出Excel需要这个
            });
        } else {  //如果是同步,则等待返回值后再设置界面上
            MaskUtil.mask();
            setTimeout(function () {
                var jso_data = FreeUtil.doClassMethodCall(FreeUtil.CommDMOClassName, "getBillListData", v_par);
                FreeUtil.appendBillListDataRowNum(jso_data);  //补上【_rownum】,以前在后台处理的,后来发现还是在前台处理更好
                _grid.datagrid('loadData', jso_data);
                _grid.datagrid('clearSelections');
                _grid.CurrSQL = _SQL;
                _grid.CurrSQL2 = jso_data.realSQL;  //设置表格的当前SQL为此SQL,这样分页跳转时要这个!
                _grid.CurrSQL3 = jso_data.realSQL3; //分页前的SQL,王小宇导出Excel需要这个
                MaskUtil.unmask();
            }, 100);
        }
    };

    /**
     * 获取遮盖css
     * @returns {{unmask: MaskUtil.unmask, mask: MaskUtil.mask}}
     */
    FreeUtil.getMaskUtil = function () {
        return MaskUtil;
    }

    /**
     * 遮盖css
     * @type {{unmask: MaskUtil.unmask, mask: MaskUtil.mask}}
     */
    var MaskUtil = (function(){

        var $mask,$maskMsg;

        var defMsg = '正在处理，请稍待。。。';

        function init(){

            if(!$mask){

                $mask = $("<div class=\"datagrid-mask mymask\"></div>").appendTo("body");

            }

            if(!$maskMsg){

                $maskMsg = $("<div class=\"datagrid-mask-msg mymask\"style='padding: 10px 7px 20px 30px;'>"+defMsg+"</div>")

                    .appendTo("body").css({'font-size':'15px'});

            }

            $mask.css({width:"100%",height:$(document).height()});

            var scrollTop = $(document.body).scrollTop();

            $maskMsg.css({

                left:( $(document.body).outerWidth(true) - 190 ) / 2

                ,top:( ($(window).height() - 45) / 2 ) + scrollTop

            });

        }

        return {

            mask:function(msg){

                init();

                $mask.show();

                $maskMsg.html(msg||defMsg).show();

            }

            ,unmask:function(){

                $mask.hide();

                $maskMsg.hide();

            }

        }

    }());

    //查询列表中一行记录
    FreeUtil.getBillListOneRowData = function (_grid, _sqlwhere) {
        var str_ds = _grid.templetVO.templet_option.ds;  //数据源
        var str_templetcode = _grid.templetVO.templet_option.templetcode;  //取得模板编码
        var str_afterloadclass = _grid.templetVO.templet_option.afterloadclass;  //后续加载类

        var v_par = {ds: str_ds, templetcode: str_templetcode, SQLWhere: _sqlwhere, AfterLoadClass: str_afterloadclass};  //入参
        var jso_data = FreeUtil.doClassMethodCall(FreeUtil.CommDMOClassName, "getBillListOneRowData", v_par);
        return jso_data;
    };

    //设置编辑状态下纺可编辑
    FreeUtil.setBillCardEditableForUpdate = function (_billCard) {
        var jso_templet_b = _billCard.templetVO.templet_option_b;  //模板子表
        for (var i = 0; i < jso_templet_b.length; i++) {
            var str_itemkey = jso_templet_b[i].itemkey;
            var str_itemtype = jso_templet_b[i].itemtype;
            var str_card_iseditable = jso_templet_b[i].card_iseditable;  //卡片是否可编辑
            if ("N2" == str_card_iseditable) {  //如果是N2，则表示只在编辑时禁用
                FreeUtil.setBillCardItemEditableByItemType(_billCard, str_itemkey, str_itemtype, false);
            }
        }
    };

    //绑定所有表单项编辑事项,必须在AfterBodyLoad()方法中调用
    FreeUtil.addBillCardItemEditListener = function (_billCard, _function) {
        var jsy_itemVOs = _billCard.templetVO.templet_option_b;  //模板子表
        for (var i = 0; i < jsy_itemVOs.length; i++) {
            var str_itemkey = jsy_itemVOs[i].itemkey;   //控件itemkey
            var str_itemtype = jsy_itemVOs[i].itemtype;  //控件类型
            var str_card_isshow = jsy_itemVOs[i].card_isshow;  //卡片是否显示
            if ("N" == str_card_isshow) {
                continue;  //如果卡片不显示,则跳过
            }

            //文本框与下拉框调用EasyUI的监听方法,参照是自己控制(即在弹出窗口返回时手工触发)
            if (str_itemtype == "文本框" || str_itemtype == "数字框") {
                (function (_billCard, str_itemkey) {  //必须是闭包,否则总是找最后一个字段
                    $('#' + str_itemkey).textbox({
                        onChange: function (_newValue, _oldValue) {
                            if (typeof event != "undefined" && event instanceof UIEvent) {
                                //console.log("文本框触发onChange,是UIUIEvent！！！");  //
                                var jso_parValue = {oldValue: _oldValue, newValue: _newValue};  //参数,即把旧值与新值都传入
                                _function(_billCard, str_itemkey, jso_parValue);  //调用入参函数
                            } else {
                                //console.log("文本框触发onChange不是UIUIEvent..");  //
                            }
                        }
                    });
                })(_billCard, str_itemkey);
            } else if (str_itemtype == "下拉框") {
                (function (_billCard, str_itemkey) {  //必须是闭包,否则总是找最后一个字段
                    $('#' + str_itemkey).combobox({
                        onSelect: function (_record) {
                            if (typeof event != "undefined" && event instanceof UIEvent) {
                                _function(_billCard, str_itemkey, _record);
                            } else {
                                //console.log("下拉环框触发onSelect事件,但不是UIEvent..");  //
                            }
                        }
                    });
                })(_billCard, str_itemkey);
            } else if (str_itemtype == "日历") {  //日历是日历的监听方式
                (function (_billCard, str_itemkey) {  //必须是闭包,否则总是找最后一个字段
                    $('#' + str_itemkey).datebox({
                        onSelect: function (_date) {
                            if (typeof event != "undefined" && event instanceof UIEvent) {
                                //console.log("日历触发onSelect事件,是UIEvent！！！！");  //
                                var str_date = FreeUtil.formatDate(_date);  //把日期转换成字符串
                                var jso_parValue = {newDate: _date, newDateStr: str_date};  //参数,即把日期即转换成字符串都转入
                                _function(_billCard, str_itemkey, jso_parValue);
                            } else {
                                //console.log("日历触发onSelect事件,但不是UIEvent..");  //
                            }
                        }
                    });
                })(_billCard, str_itemkey);
            } else if (str_itemtype == "时间") {  //时间
                (function (_billCard, str_itemkey) {  //必须是闭包,否则总是找最后一个字段
                    $('#' + str_itemkey).datetimebox({
                        onSelect: function (_date) {
                            if (typeof event != "undefined" && event instanceof UIEvent) {
                                //console.log("时间触发onSelect事件,是UIEvent！！！");  //
                                var str_date = FreeUtil.formatTime(_date);  //把日期转换成字符串
                                var jso_parValue = {newDate: _date, newDateStr: str_date};  //参数,即把日期即转换成字符串都转入
                                _function(_billCard, str_itemkey, jso_parValue);
                            } else {
                                //console.log("时间触发onSelect事件,但不是UIEvent..");  //
                            }
                        }
                    });
                })(_billCard, str_itemkey);
            } else if (str_itemtype == "勾选框") {  //勾选框
                var dom_checkBox = document.getElementById(str_itemkey);  //找到这个勾选框
                (function (_billCard, str_itemkey, dom_checkBox) {  //必须是闭包,否则总是找最后一个字段
                    dom_checkBox.addEventListener('click', function (event) {  //直接绑定点击事件
                        var str_checked = dom_checkBox.checked ? "Y" : "N";  //
                        _function(_billCard, str_itemkey, str_checked);  //转调归口函数
                    }, true);
                })(_billCard, str_itemkey, dom_checkBox);
            } else {
                //其他控件暂时不做,参照是点击弹出窗口后返回时才触发,所以只能把入参函数绑定在BillCard上,然后在参照返回的地方调这个函数!
            }
        }  //遍历所有控件结束

        //直接把这个函数与BillCard绑定,这样参照点击时才能找到这个函数
        _billCard.BillCardItemEditListener = _function;  //绑定,用于参照点击时触发用
    };

    //为一个卡片表单所有项设置label的帮助提示,因为EasyUI本身没有这个配置,所以只能在解析后在前端再自己重新处理Dom
    FreeUtil.setBillCardLabelHelptip = function (_billCard) {
        try {
            var str_divid = _billCard.divid;
            var jsy_itemVOs = _billCard.templetVO.templet_option_b;  //模板子表
            for (var i = 0; i < jsy_itemVOs.length; i++) {
                var str_card_isshow = jsy_itemVOs[i].card_isshow;  //卡片是否显示
                if ("Y" != str_card_isshow) {  //如果卡片不显示
                    continue;
                }

                var str_itemkey = jsy_itemVOs[i].itemkey;  //
                var str_itemname = jsy_itemVOs[i].itemname;  //
                var str_helptip = jsy_itemVOs[i].helptip;  //帮助提示
                var str_title = "";
                if (typeof str_helptip != "undefined" && str_helptip != null && str_helptip != "") {
                    str_title = str_helptip;
                } else {
                    if (str_itemname.length >= 6) {
                        str_title = str_itemname;
                    }
                }

                if (str_title != "") {
                    //找到label,并设置title属性,因为easyUI没找到配置可以操作label的title,所以只能dom操作
                    var dom_span = document.getElementById(str_divid + "_CardItem_" + str_itemkey); //先找到外面的包装器
                    var dom_label = dom_span.firstChild;  //第一个儿子就是label
                    dom_label.title = str_title
                }
            }
        } catch (_ex) {
            console.log(_ex);
        }
    };

    //设置所有警告框显示或隐藏
    FreeUtil.setBillCardItemWarnMsgVisible = function (_billCard, _isVisible) {
        var str_divid = _billCard.divid;  //卡片绑定的divid
        var templetItemVOs = _billCard.templetVO.templet_option_b;  //模板子表
        for (var i = 0; i < templetItemVOs.length; i++) {
            var str_itemkey = templetItemVOs[i].itemkey;  //
            var str_spanid = str_divid + "_CardItem_" + str_itemkey + "▲error";  //警告框的<span>的id
            var dom_span = document.getElementById(str_spanid);  //
            if (typeof dom_span != "undefined" && dom_span != null) {  //如果不为空,因为隐藏域是没有的!
                if (_isVisible) {  //如果设置为显示
                    dom_span.style.display = "block";
                } else {  //隐藏
                    dom_span.style.display = "none";
                }
            }
        }
    };

    //设置某一项的警告,即在控件的下面冒出一个警告提示!
    FreeUtil.setBillCardItemWarnMsg = function (_billCard, _itemkey, _warnMsg) {
        var str_divid = _billCard.divid;  //卡片绑定的divid
        var templetItemVOs = _billCard.templetVO.templet_option_b;  //模板子表
        var itemVO = FreeUtil.findItemVOByItemkey(templetItemVOs, _itemkey);  //
        var str_allWidth = itemVO["card_width_realallwidth"];  //整个控件宽度
        var str_labelWidth = itemVO["card_width_reallabelwidth"];  //label的宽度

        var str_spanid = str_divid + "_CardItem_" + _itemkey + "▲error";  //找到这个span
        var dom_span = document.getElementById(str_spanid); //找到这个span
        if (typeof dom_span != "undefined" && dom_span != null) {
            dom_span.style.display = "block";  //先显示

            //设置警告提示内容!
            var str_msgSpan = "<span style=\"display:block;width:" + str_allWidth + "px;margin-top:5px;margin-bottom:5px;padding-left:" + str_labelWidth + "px;color:#FF4444\">" + _warnMsg + "</span>";
            dom_span.innerHTML = str_msgSpan;  //
        }
    };

    //设置某一个字段的提示!
    FreeUtil.setBillCardItemHelptip = function (_billCard, _itemkey, _helpMsg) {
        var str_helpMsg = _helpMsg;  //设置提示框
        str_helpMsg = str_helpMsg.replace(/<br>/g, "\r\n");  //替换所的<br>为换行符
        var jsy_itemVOs = _billCard.templetVO.templet_option_b;  //模板子表
        var jso_itemVO = FreeUtil.findItemVOByItemkey(jsy_itemVOs, _itemkey);
        var str_itemtype = jso_itemVO.itemtype;  //控件类型
        var str_isCardShow = jso_itemVO.card_isshow;  //卡片是否显示
        if ("Y" == str_isCardShow) {  //必须参与卡片才做
            if (str_itemtype == "文本框" || str_itemtype == "数字框") {
                $('#' + _itemkey).textbox('textbox')[0].title = str_helpMsg;
            } else if (str_itemtype == "列表参照" || str_itemtype == "列表购物车参照" || str_itemtype == "树型参照" || str_itemtype == "自定义参照") {
                $('#' + _itemkey).textbox('textbox')[0].title = str_helpMsg;
            } else if (str_itemtype == "日历" || str_itemtype == "时间") {
                $('#' + _itemkey).textbox('textbox')[0].title = str_helpMsg;
            } else if (str_itemtype == "勾选框") { //勾选框单独取
                var dom_check = document.getElementById(_itemkey);
                dom_check.title = str_helpMsg;
            } else {
                $('#' + _itemkey).textbox('textbox')[0].title = str_helpMsg;
            }
        }
    };

    //设置卡片中某一项是否显示
    FreeUtil.setBillCardItemColor = function (_billCard, _itemkey, _color) {
        var str_divid = _billCard.divid;  //卡片绑定的divid
        var jsy_itemVOs = _billCard.templetVO.templet_option_b;  //模板子表
        var str_allKeys = [];
        if (_itemkey == "*") {
            for (var i = 0; i < jsy_itemVOs.length; i++) {
                str_allKeys.push(jsy_itemVOs[i].itemkey);
            }
        } else {
            str_allKeys = _itemkey.split(",");  //以逗号分割,多个数组
        }

        //循环处理各个字段
        for (var i = 0; i < str_allKeys.length; i++) {
            var jso_itemVO = FreeUtil.findItemVOByItemkey(jsy_itemVOs, str_allKeys[i]);
            if (jso_itemVO != null) {
                var str_itemtype = jso_itemVO.itemtype;  //控件类型
                var str_isCardShow = jso_itemVO.card_isshow;  //卡片是否显示
                if ("Y" == str_isCardShow) {  //必须参与卡片才做
                    if (str_itemtype == "文本框" || str_itemtype == "数字框") {
                        $('#' + str_allKeys[i]).textbox('textbox')[0].style.background = _color;
                    } else if (str_itemtype == "列表参照" || str_itemtype == "列表购物车参照" || str_itemtype == "树型参照" || str_itemtype == "自定义参照") {
                        $('#' + str_allKeys[i]).textbox('textbox')[0].style.background = _color;
                    } else if (str_itemtype == "日历" || str_itemtype == "时间") {
                        $('#' + str_allKeys[i]).textbox('textbox')[0].style.background = _color;
                    } else if (str_itemtype == "勾选框") {
                        //勾选框比较特殊,不处理
                    } else {
                        $('#' + str_allKeys[i]).textbox('textbox')[0].style.background = _color;
                    }
                }
            }
        }
    };

    //设置卡片中某一项是否显示
    FreeUtil.setBillCardItemVisible = function (_billCard, _itemkey, _isVisible) {
        var str_divid = _billCard.divid;  //卡片绑定的divid
        var jsy_itemVOs = _billCard.templetVO.templet_option_b;  //模板子表
        var str_allKeys = [];
        if (_itemkey == "*") {
            for (var i = 0; i < jsy_itemVOs.length; i++) {
                str_allKeys.push(jsy_itemVOs[i].itemkey);
            }
        } else {
            str_allKeys = _itemkey.split(",");  //以逗号分割,多个数组
        }

        //循环处理各个字段
        for (var i = 0; i < str_allKeys.length; i++) {
            var jso_itemVO = FreeUtil.findItemVOByItemkey(jsy_itemVOs, str_allKeys[i]);
            if (jso_itemVO != null) {
                var str_isCardShow = jso_itemVO.card_isshow;  //卡片是否显示
                if ("Y" == str_isCardShow) {  //必须参与卡片才做
                    var str_itemSpanId = str_divid + "_CardItem_" + str_allKeys[i];  //包装的span的id
                    var dom_span = document.getElementById(str_itemSpanId);  //
                    if (_isVisible) {
                        dom_span.style.display = "inline-block";  //
                    } else {
                        dom_span.style.display = "none";  //
                    }
                }
            }
        }
    };

    //设置表单某一项是否可编辑
    FreeUtil.setBillCardItemEditable = function (_billCard, _itemkey, _isEditable) {
        var jsy_itemVOs = _billCard.templetVO.templet_option_b;  //模板子表
        var str_allKeys = [];
        if (_itemkey == "*") {
            for (var i = 0; i < jsy_itemVOs.length; i++) {
                str_allKeys.push(jsy_itemVOs[i].itemkey);
            }
        } else {
            str_allKeys = _itemkey.split(",");  //以逗号分割,多个数组
        }

        //循环处理各个字段
        for (var i = 0; i < str_allKeys.length; i++) {
            var jso_itemVO = FreeUtil.findItemVOByItemkey(jsy_itemVOs, str_allKeys[i]);
            if (jso_itemVO != null) {
                var str_itemtype = jso_itemVO.itemtype;  //控件类型
                var str_isCardShow = jso_itemVO.card_isshow;  //卡片是否显示
                if ("Y" == str_isCardShow) {  //必须参与卡片才做
                    FreeUtil.setBillCardItemEditableByItemType(_billCard, str_allKeys[i], str_itemtype, _isEditable);
                }
            }
        }
    };

    //设置卡片中某一项是否禁用
    FreeUtil.setBillCardItemEditableByItemType = function (_billCard, _itemkey, _itemtype, _isEditable) {
        //var dom_form = _billCard.form;  //找到表单
        //var dom_text = dom_form.querySelector("#" + _itemkey);  //找到这个控件
        if (_itemtype == "文本框" || _itemtype == "数字框" || _itemtype == "多行文本框" || _itemtype == "日历") {
            if (_isEditable) {
                $('#' + _itemkey).textbox('enable');
            } else {
                $('#' + _itemkey).textbox('disable');
            }
        } else if ("勾选框" == _itemtype) {
        	var dom_checkBox = document.getElementById(_itemkey);  // 找到这个勾选框
        	var label = document.querySelector('label[for="'+dom_checkBox.id+'"]');
        	console.log(label);
        	if (_isEditable) {
        		dom_checkBox.disabled  = false;
        	} else {
        		dom_checkBox.disabled  = true;
        		document.querySelector('label[for="'+dom_checkBox.id+'"]').className="textbox-label textbox-label-before textbox-label-disabled"
        	}
        } else if ("下拉框" == _itemtype) {
            if (_isEditable) {
                $('#' + _itemkey).combobox('enable');
            } else {
                $('#' + _itemkey).combobox('disable');
            }
        } else if (_itemtype.indexOf("参照") > 0) {  //所有参照
            if (_isEditable) {
                $('#' + _itemkey).textbox('enable');
            } else {
                $('#' + _itemkey).textbox('disable');
            }
        } else {
            if (_isEditable) {
                //$('#' + _itemkey).textbox('enable');
            } else {
                //$('#' + _itemkey).textbox('disable');
            }
        }
    };

    //设置查询面板中某一项是否禁用
    FreeUtil.setBillQueryItemEditable = function (_itemkey, _itemtype, _isEditable) {

        if (_itemtype == "文本框" || _itemtype == "数字框" || _itemtype == "多行文本框" || _itemtype == "日历") {
            if (_isEditable) {
                $('#' + _itemkey).textbox('enable');
            } else {
                $('#' + _itemkey).textbox('disable');
            }
        } else if ("下拉框" == _itemtype) {
            if (_isEditable) {
                $('#' + _itemkey).combobox('enable');
            } else {
                $('#' + _itemkey).combobox('disable');
            }
        } else if (_itemtype.indexOf("参照") > 0) {  //所有参照 
            if (_isEditable) {
                $('#' + _itemkey).textbox('enable');
            } else {
                $('#' + _itemkey).textbox('disable');
            }
        } else {
            if (_isEditable) {
                //$('#' + _itemkey).textbox('enable');
            } else {
                //$('#' + _itemkey).textbox('disable');
            }
        }
    };

    //清空表单某一的值
    FreeUtil.setBillCardItemClearValue = function (_billCard, _itemkey) {
        var jsy_itemVOs = _billCard.templetVO.templet_option_b;  //模板子表
        var str_allKeys = [];
        if (_itemkey == "*") {
            for (var i = 0; i < jsy_itemVOs.length; i++) {
                str_allKeys.push(jsy_itemVOs[i].itemkey);
            }
        } else {
            str_allKeys = _itemkey.split(",");  //以逗号分割,多个数组
        }

        //循环处理所有值
        for (var i = 0; i < str_allKeys.length; i++) {
            var jso_itemVO = FreeUtil.findItemVOByItemkey(jsy_itemVOs, str_allKeys[i]);
            if (jso_itemVO != null) {
                var str_itemtype = jso_itemVO.itemtype;  //控件类型
                var str_isCardShow = jso_itemVO.card_isshow;  //卡片是否显示
                if ("Y" == str_isCardShow) {  //必须参与卡片才做
                    if (str_itemtype == "文本框" || str_itemtype == "数字框") {
                        $('#' + str_allKeys[i]).textbox('clear');
                    } else if (str_itemtype == "下拉框") {
                        $('#' + str_allKeys[i]).combobox('clear');  //
                    } else if (str_itemtype == "日历" || str_itemtype == "时间") {
                        $('#' + str_allKeys[i]).textbox('clear');
                    } else if (str_itemtype == "列表参照" || str_itemtype == "树型参照" || str_itemtype == "自定义参照") {
                        $('#' + str_allKeys[i]).textbox('clear');
                    } else {
                        //$('#' + str_allKeys[i]).textbox('clear');
                    }
                }
            }
        }
    };

    //设置某一项是否必输项,除了模板中默认设置的,还要自己加入
    FreeUtil.setBillCardItemIsMust = function (_billCard, _itemkey, _isMust) {
    	var ary_isMust = _billCard.isMustItemFromUI;  //
        if (typeof ary_isMust == "undefined") {
            ary_isMust = [];  //
        }
        //如果是加入
        if (_isMust) {
            ary_isMust.push(_itemkey.toLowerCase());
        } else {
            var li_pos = -1;
            for (var i = 0; i < ary_isMust.length; i++) {
                if (ary_isMust[i].toLowerCase() == _itemkey.toLowerCase()) {
                    li_pos = i;
                    break;
                }
            }
            if (li_pos >= 0) {
                ary_isMust.splice(li_pos, 1); //删除指定的项
            }
        }

        _billCard.isMustItemFromUI = ary_isMust;  //重新设置
    };

    //取得BillQuery中的所有值
    FreeUtil.getBillQueryFormValue = function (_billQuery) {
        var dom_form = _billQuery.form;  //
        var jso_templet = _billQuery.templetVO.templet_option;  //模板主表
        var jso_templet_b = _billQuery.templetVO.templet_option_b;  //模板子表

        var jso_formdata = {};  //表单数据对象
        //永远是先从模板VO循环,然后去表单中反向寻找,即模板VO是数据对象的标准与依据!因为表单中可能因为有加载公式导致临时数据
        for (var i = 0; i < jso_templet_b.length; i++) {
            var str_itemkey = jso_templet_b[i].itemkey;
            var str_value = FreeUtil.getFormItemValue(dom_form, jso_templet_b[i]);  //
            jso_formdata[str_itemkey] = str_value;  //设置数据
        }
        return jso_formdata;
    };

    //取得表单中某一项的实际控件,比如是下拉框或文本框
    FreeUtil.getBillCardItemCompent = function (_billCard, _itemkey) {
        var dom_item = document.getElementById(_billCard.divid + "_CardItem_" + _itemkey);  //先取得整个span
        var dom_label = dom_item.firstChild;  //找到第一个子结点，就是Label
        var str_forid = dom_label.getAttribute("for");  //找到Label的for属性，就是对应的实际控件的id
        return document.getElementById(str_forid);  //再根据这个id，取得实际控件
    };

    //取得表单中开支一项的值
    FreeUtil.getBillCardItemValue = function (_billCard, _itemkey) {
        var jso_cardData = FreeUtil.getBillCardFormValue(_billCard);  //先取得整个表单的值
        return jso_cardData[_itemkey.toLocaleLowerCase()];  //取得表
    };

    //卡片中的表单的值
    FreeUtil.getBillCardFormValue = function (_billCard) {
        var dom_form = _billCard.form;  //
        if (!dom_form) {
            dom_form = document.getElementById(_billCard.divid + '_QueryForm');//如果没有form属性，自己现去获取一下
        }
        var jso_templet = _billCard.templetVO.templet_option;  //模板主表
        var jso_templet_b = _billCard.templetVO.templet_option_b;  //模板子表

        var jso_formdata = {};  //表单数据对象
        //永远是先从模板VO循环,然后去表单中反向寻找,即模板VO是数据对象的标准与依据!因为表单中可能因为有加载公式导致临时数据
        for (var i = 0; i < jso_templet_b.length; i++) {
            var str_itemkey = jso_templet_b[i].itemkey;
            var str_value = FreeUtil.getFormItemValue(dom_form, jso_templet_b[i]);  //
            jso_formdata[str_itemkey] = str_value;  //设置数据
        }
        return jso_formdata;
    };

    //设置卡片中某一项参照的值与显示内容!
    //这个完全是根据EasyUI的渲染机制来计算的，即itemkey其实绑定的是一个隐藏域,而隐藏域前面的一个控件才是文本框
    FreeUtil.setBillCardRefIdName = function (_form, _itemkey, _id, _text) {
        var dom_inputText = null;
        var dom_inputHidden = null;

        var jsy_items = _form.elements;
        for (var i = 0; i < jsy_items.length; i++) {
            if (jsy_items[i].name == _itemkey) {
                dom_inputHidden = jsy_items[i];
                dom_inputText = jsy_items[i - 1];
                break;  //
            }
        }

        //设置文本框中为显示值,而隐藏域是实际值,从而达到参照效果!
        dom_inputText.value = _text;
        dom_inputHidden.value = _id;
    };

    //加载卡片数据
    //参照的思路:实际数据是useid,有另一个字段是显示名称username,使用加载公式把人员姓名在后台先赋值到username上
    //在表单中,userid是自己的隐藏域,username是参照,只有这样卡片加载数据时,才正好参照文本框中是姓名!!
    //然后保存时username不参与保存,这样直接保存没有问题!
    //但在弹出参照按钮时，弹出窗口，返回值时，是一个Json对象，如果是自定义参照,则返回时根据key,自动把value打到对应的表单项上!
    //如果是列表模板参照，则在参照定义时，设置弹出窗口中的字段与本卡片中的字段的,userid=>userid,name=>username,根据这个公式就能自动把弹出窗口中的数据返回到卡片中来!!
    //
    FreeUtil.loadBillCardData = function (_billCard, _jso_data) {
        var str_divid = _billCard.divid;  //id
        var dom_form = _billCard.form;  //表单
        var jsa_itemVOs = _billCard.templetVO.templet_option_b;

        //勾选框比较特殊,我们数据库中存的是Y/N，但在表单中要转换成true/false
        for (var i = 0; i < jsa_itemVOs.length; i++) {
            if (jsa_itemVOs[i].itemtype == "勾选框") {
                var str_itemkey = jsa_itemVOs[i].itemkey; // 根据itemkey找到表单项,即
                var str_itemValue = _jso_data[str_itemkey]; // 实际值
                if (typeof str_itemValue != "undefined") {
                    if ("Y" == str_itemValue) {  // checkbox比较特殊,如果是Y,则要改成true,使用form('load')才会选中!
                        _jso_data[str_itemkey] = true;
                    } else {
                        _jso_data[str_itemkey] = false;
                    }
                }
            } else if (jsa_itemVOs[i].itemtype == "文本框" && typeof jsa_itemVOs[i].formatvalidate != "undefined" && (jsa_itemVOs[i].formatvalidate).indexOf("数字文本") != -1) { // 对，进行补位操作
                var str_itemkey = jsa_itemVOs[i].itemkey; // 根据itemkey找到表单项,即
                var str_itemValue = _jso_data[str_itemkey]; // 实际值
                
                // 如果不是undefined，则进行补位（因：例如当页面选择机构号，弹出一个窗口接收返回值时，需要重新load页面，此时实际值是空的，如果是空的不需要转，容易导致页面原始值被置空）
                if (typeof str_itemValue != "undefined" && str_itemValue != "" && str_itemValue != null) {
                	var stringResult = jsa_itemVOs[i].formatvalidate.split('/');
                	var p_str = "";
                    if (stringResult[1].indexOf("精度") != -1) {
                        p_str = stringResult[1].slice(3);
                    }
                    // 调取校验
                    var newV = FreeUtil.validateStrNum(str_itemValue, p_str, "");
                    _jso_data[str_itemkey] = newV;
                }
            }
        }

        //先直接使用EasyUI自带的load方法加载
        $('#' + str_divid + '_form').form('load', _jso_data);  //加载数据

        //以后还有参照等其他控件需要特殊处理
    };

    //加载查询框数据
    FreeUtil.loadBillQueryData = function (_billQuery, _jso_data) {
        var str_divid = _billQuery.divid;  //id
        var dom_form = _billQuery.form;  //表单
        var jsa_itemVOs = _billQuery.templetVO.templet_option_b;

        //先直接使用EasyUI自带的load方法加载
        $('#' + str_divid + '_QueryForm').form('load', _jso_data);  //加载数据
    };

    //校验是否有空值
    FreeUtil.validateNullBilldData = function (_billCard, jso_templetVO, jso_formData) {
    	var str_errmsg = "";
        var isSucess = true;
        var jsa_items = jso_templetVO.templet_option_b;  //模板子表VO数组
        for (var i = 0; i < jsa_items.length; i++) {
            var str_itemkey = jsa_items[i].itemkey;
            var str_itemname = jsa_items[i].itemname;
            var str_ismust = jsa_items[i].ismust;
            if ("Y" == str_ismust) {  //如果是必输项!
                var str_itemvalue = jso_formData[str_itemkey];  //
                if (typeof str_itemvalue == "undefined" || str_itemvalue == null || str_itemvalue == "") {
                    str_errmsg = str_errmsg + "【" + str_itemname + "】不能为空!<br>";
                    isSucess = false;
                }
            }
        }

        //前端设置的额外必输项!
        var ary_isMust = _billCard.isMustItemFromUI;  //
        if (typeof ary_isMust != "undefined") {
            for (var i = 0; i < ary_isMust.length; i++) {
                var str_itemkey = ary_isMust[i];
                var str_itemname = ary_isMust[i];
                var str_itemvalue = jso_formData[str_itemkey];
                if (typeof str_itemvalue == "undefined" || str_itemvalue == null || str_itemvalue == "") {
                    var jso_itemVO = FreeUtil.findItemVOByItemkey(jsa_items, str_itemkey);  //
                    if (typeof jso_itemVO != "undefined") {
                        str_itemname = jso_itemVO.itemname;  //
                    }
                    str_errmsg = str_errmsg + "【" + str_itemname + "】不能为空!<br>";
                    isSucess = false;
                }
            }
        }

        if (!isSucess) {
            $.messager.alert('提示', str_errmsg);
        }

        return isSucess;
    };

    //校验是否有空值
    FreeUtil.validateLengthBilldData = function (_billCard, jso_templetVO, jso_formData) {
        var str_errmsg = "";
        var isSucess = true;
        var jsa_items = jso_templetVO.templet_option_b;  //模板子表VO数组

        // 在循环之前，先获取该表在数据库中各个字段的长度
        var jso_par = {tab_name_en: jso_templetVO.templet_option.fromtable};

        for (var i = 0; i < jsa_items.length; i++) {
            var str_itemkey = jsa_items[i].itemkey;
            var str_itemname = jsa_items[i].itemname;
            var str_itemtype = jsa_items[i].itemtype;
            var str_formatvalidate = jsa_items[i].formatvalidate; // 格式校验
            var str_maxlen = jsa_items[i].maxlen; // 最大宽度

            //如果定义了最大宽度限制
            var str_itemvalue = jso_formData[str_itemkey];  //取得值
            if (typeof str_maxlen != "undefined" && str_maxlen != null && str_maxlen != "") {
                if (str_itemtype == "数字框") {
                    if (str_itemvalue.indexOf("e") > 0) {
                        str_errmsg = str_errmsg + "【" + str_itemname + "】宽度不能超过[" + str_maxlen + "]位,现在超长!<br>";
                        isSucess = false;
                    }

                    if (str_itemvalue.indexOf(".") > 0) {
                        str_itemvalue = str_itemvalue.substring(0, str_itemvalue.indexOf("."));
                    }
                } else if (str_itemtype == "文本框") {
                	if (typeof str_formatvalidate != "undefined" && str_formatvalidate != null && str_formatvalidate != "") {
                		var formatItems = str_formatvalidate.split(';');
                		if (formatItems[0].indexOf("数字文本") != -1) {
                			if (str_itemvalue.indexOf(".") > 0) {
                                str_itemvalue = str_itemvalue.split('.').join('');
                            }
                			if (str_itemvalue.startsWith("-")) {
                                str_itemvalue = str_itemvalue.split('-').join('');
                            }
                		}
                	}
                }

                var li_itemlen = str_itemvalue.length;
                var li_maxlen = parseInt(str_maxlen);
                if (li_itemlen > li_maxlen) {  //如果超过宽度
                    str_errmsg = str_errmsg + "【" + str_itemname + "】宽度不能超过[" + str_maxlen + "]位,现在是[" + li_itemlen + "]位!<br>";
                    isSucess = false;
                }
            } else {

            }
        }

        if (!isSucess) {
            $.messager.alert('提示', str_errmsg);
        }

        return isSucess;
    };

    //唯一性校验
    FreeUtil.validateOnlyOneBilldData = function (jso_templetVO, jso_formData, _isUpdate) {
        var str_errmsg = "";
        var isSucessZ = true;
        var jsa_items = jso_templetVO.templet_option_b;
        for (var i = 0; i < jsa_items.length; i++) {
            var str_itemkey = jsa_items[i].itemkey;
            var str_itemname = jsa_items[i].itemname;
            var str_issave = jsa_items[i].issave;   //是否保存
            var str_isonlyone = jsa_items[i].isonlyone;  //是否是唯一性
            var str_validatefields = jsa_items[i].validatefields;  //校验唯一性关联字段
            if ("Y" == str_issave && "Y" == str_isonlyone) {  //如果参与保存,而且做唯一性校验
                var str_itemvalue = jso_formData[str_itemkey];  //取得数据

                if (typeof str_itemvalue != "undefined" && str_itemvalue != null && str_itemvalue != "") {
                    var params = {};
                    var validatefields = [];
                    if (str_validatefields != "undefined" && str_validatefields != null) {
                        validatefields = str_validatefields.split(",");
                        for (var j = 0; j < validatefields.length; j++) {
                            $(params).attr(validatefields[j], jso_formData[validatefields[j]]);
                        }
                    }
                    var str_table = jso_templetVO.templet_option.fromtable;  //表名

                    var isSucess = true;
                    if (_isUpdate) {  //update时的重复校验
                        var str_where = FreeUtil.getSQLWhereByPK(jso_templetVO, jso_formData);  //update要计算出主键值
                        var v_par = {
                            table: str_table,
                            colname: str_itemkey,
                            colvalue: str_itemvalue,
                            fieldValues: params,
                            validatefields: validatefields,
                            whereSQL: str_where
                        };
                        var jso_validate = JSPFree.doClassMethodCall(FreeUtil.CommDMOClassName, "validateOnlyOneByUpdate", v_par);  //
                        isSucess = jso_validate.result;
                    } else {  //insert时的校验
                        var v_par = {
                            table: str_table,
                            colname: str_itemkey,
                            colvalue: str_itemvalue,
                            fieldValues: params,
                            validatefields: validatefields
                        };
                        var jso_validate = JSPFree.doClassMethodCall(FreeUtil.CommDMOClassName, "validateOnlyOneByInsert", v_par);  //
                        isSucess = jso_validate.result;
                    }

                    if (!isSucess) {
                        str_errmsg = str_errmsg + str_itemname + "【" + str_itemvalue + "】已有重名的!<br>";
                        isSucessZ = false;
                    }
                }
            }
        }

        if (!isSucessZ) {
            $.messager.alert('提示', str_errmsg);
        }

        return isSucessZ;
    };


    //校验字符串格式
    FreeUtil.validateFormatBilldData = function (jso_templetVO, jso_formData) {
        var str_errmsg = "";
        var isSucess = true;
        var jsa_items = jso_templetVO.templet_option_b;
        for (var i = 0; i < jsa_items.length; i++) {
            var str_itemkey = jsa_items[i].itemkey;
            var str_itemname = jsa_items[i].itemname;
            var str_itemvalue = jso_formData[str_itemkey];
            var str_formatvalidate = jsa_items[i].formatvalidate; // 格式校验
            var str_maxlen = jsa_items[i].maxlen; // 长度
            
            if (typeof str_formatvalidate != "undefined" && str_formatvalidate != null && str_formatvalidate != "" && typeof str_itemvalue != "undefined" && str_itemvalue != null && str_itemvalue != "") {
                var formatItems = str_formatvalidate.split(';');
                for (var j = 0; j < formatItems.length; j++) {
                    if (formatItems[j] == "全英文") {
                        var isOK = FreeUtil.validateStrAllEnglish(str_itemvalue);
                        if (!isOK) {
                            str_errmsg = str_errmsg + "【" + str_itemname + "】【" + str_itemvalue + "】必须全部是英文!<br>";
                            isSucess = false;
                        }
                    } else if (formatItems[j] == "全中文") {
                        var isOK = FreeUtil.validateStrAllChinese(str_itemvalue);
                        if (!isOK) {
                            str_errmsg = str_errmsg + "【" + str_itemname + "】【" + str_itemvalue + "】必须全部是中文!<br>";
                            isSucess = false;
                        }
                    } else if (formatItems[j] == "非特殊符号") {
                        var isOK = FreeUtil.validateStrNotHaveSpecial(str_itemvalue);
                        if (!isOK) {
                            str_errmsg = str_errmsg + "【" + str_itemname + "】不能有特殊符号!<br>";
                            isSucess = false;
                        }
                    } else if (formatItems[j] == "邮件地址") {
                        var isOK = FreeUtil.validateStrMailAddr(str_itemvalue);
                        if (!isOK) {
                            str_errmsg = str_errmsg + "【" + str_itemname + "】不能合法的邮箱地址!<br>";
                            isSucess = false;
                        }
                    } else if (formatItems[j] == "全数字") {
                        var isOK = FreeUtil.validateStrAllNum(str_itemvalue);
                        if (!isOK) {
                            str_errmsg = str_errmsg + "【" + str_itemname + "】必须全部是数字!<br>";
                            isSucess = false;
                        }
                    } else if (formatItems[j].indexOf("数字文本") != -1) { // 校验格式（/精度/提示信息/是否有千分位）
                        var stringResult = formatItems[j].split('/');
                    	var isOK = false; 
                    	var p_str = null;

                		if (stringResult[1].indexOf("精度") != -1) {
                			p_str = stringResult[1].slice(3);
                		}
                		
                    	if (stringResult[2].indexOf("提示信息") != -1) {
                			var p_ts = stringResult[2].slice(5);
                			if (p_ts != "") {
                				str_errmsg = "【" + p_ts + "】!<br>";
                			} else {
                				str_errmsg = "【" + str_itemname + "】必须是数字!<br>";
                			}
                		}
                    	
                    	// 判断整数位是否超长
                    	var maxlen_msg =  FreeUtil.validateStrNumLength(str_itemname, str_itemvalue, p_str, str_maxlen);
                    	if (maxlen_msg != "") { // 如果没有返回错误信息，则录入长度正确
                    		isSucess = false;
                    		str_errmsg = maxlen_msg;
                    	} else {
                    		var val = FreeUtil.validateStrNum1(str_itemvalue, p_str, str_errmsg);
                			if (val != "") {
                				isOK = true;
                			} else {
                				isOK = false;
                			}
                        	
                            if (!isOK) {
                                isSucess = false;
                            }
                    	}
                    }
                }
            }
        }

        if (!isSucess) {
            $.messager.alert('提示', str_errmsg);
        }

        return isSucess;
    };

    //校验一个字符串全是英文..
    FreeUtil.validateStrAllEnglish = function (_str) {
        var pattern = new RegExp("^[0-9a-zA-Z_]+$");  //全英文的正则表达式
        return pattern.test(_str);
    };

    //校验一个字符串全是中文
    FreeUtil.validateStrAllChinese = function (_str) {
        for (var i = 0; i < _str.length; i++) {
            var str_item = _str.substring(i, i + 1);
            var isChinese = FreeUtil.validateOneStrIsChinese(str_item);
            if (!isChinese) {
                return false;
            }
        }
        return true;
    };

    //校验一个字符串全是中文
    FreeUtil.validateOneStrIsChinese = function (_str) {
        var pattern = /[\u4E00-\u9FA5]/;
        return pattern.test(_str);
    };

    //校验一个字符串没有特殊符号
    FreeUtil.validateStrNotHaveSpecial = function (_str) {
        var str_specials = ["~", "!", "@", "#", "$", "%", "^", "&", "*", "……", "￥", "(", ")", "[", "]", "+", "-", "_", "/", "?", ">", "<", ".", ",", ";", ":", "'"];
        var li_len = _str.length;
        for (var i = 0; i < li_len; i++) {
            var str_item = _str.substring(i, i + 1);
            for (var j = 0; j < str_specials.length; j++) {
                if (str_specials[j] == str_item) {
                    return false; //只要有一个在里面,则直接校验失败
                }
            }
        }
        return true;
    };

    //校验一个字符串全是中文
    FreeUtil.validateStrMailAddr = function (_str) {
        return false;
    };
    //校验一个字符串全是数字..
    FreeUtil.validateStrAllNum = function (_str) {
        var pattern = new RegExp("^[0-9]*$");  //正整数的正则表达式
        return pattern.test(_str);
    };
    //校验一个字符串是数字..
    FreeUtil.validateStrNum = function (_str, _index, _mes) {
        var pattern = /^(-?\d+)(\.\d+)?$/;
        var flag = pattern.test(_str);
        var v = "";

        if (!flag) {
        	JSPFree.alert(_mes);
        } else {
        	v = FreeUtil.keepNDecimalFull(_str, _index);
        }
        
        return v;
    };
    //校验一个字符串是数字..
    FreeUtil.validateStrNum1 = function (_str, _index, _mes) {
        var pattern = /^(-?\d+)(\.\d+)?$/;
        var flag = pattern.test(_str);
        var v = "";

        if (!flag) {
        } else {
            v = FreeUtil.keepNDecimalFull(_str, _index);
        }

        return v;
    };
    //校验一个数字长度..
    FreeUtil.validateStrNumLength = function (_itemname, _str, _po, _le) {
    	var str_errmsg = "";
    	// 当精度=0，则录入的是整数
    	if (_po == 0) {
    		// 如果是负数
    		if (_str.startsWith("-")) {
    			if (_str.indexOf(".") != -1) {
        			str_errmsg = "【" + _itemname + "】不允许输入小数!<br>";
        		} else {
        			var _newl = _str.length;
                    if (_newl > (_le - _po) + 1) {
                    	str_errmsg = "【" + _itemname + "】整数位超长!<br>";
                    }
        		}
    		} else {
    			if (_str.indexOf(".") != -1) {
        			str_errmsg = "【" + _itemname + "】不允许输入小数!<br>";
        		} else {
        			var _newl = _str.length;
                    if (_newl > (_le - _po)) {
                    	str_errmsg = "【" + _itemname + "】整数位超长!<br>";
                    }
        		}
    		}
    	} else {
    		var _newl = _str.substr(0, _str.indexOf(".")).length;
    		// 如果是负数
    		if (_str.startsWith("-")) {
    			// 判断整数位是否超长
        		if (_newl == 0) {
        			var _newZ = _str.length;
        			if (_newZ > _le - _po + 1) {
        				str_errmsg = "【" + _itemname + "】整数位超长!<br>";
        			}
        		} else if (_newl > (_le - _po + 1)) {
                	str_errmsg = "【" + _itemname + "】整数位超长!<br>";
                } else {
                	// 判断小数位是否超长
                	var _new2 = _str.substr(_str.indexOf(".")+1, _str.length).length;
                	if (_new2 > _po) {
                		str_errmsg = "【" + _itemname + "】小数位超长!<br>";
                	}
                }
    		} else {
    			// 判断整数位是否超长
        		if (_newl == 0) {
        			var _newZ = _str.length;
        			if (_newZ > _le - _po) {
        				str_errmsg = "【" + _itemname + "】整数位超长!<br>";
        			}
        		} else if (_newl > (_le - _po)) {
                	str_errmsg = "【" + _itemname + "】整数位超长!<br>";
                } else {
                	// 判断小数位是否超长
                	var _new2 = _str.substr(_str.indexOf(".")+1, _str.length).length;
                	if (_new2 > _po) {
                		str_errmsg = "【" + _itemname + "】小数位超长!<br>";
                	}
                }
    		}
    	}
    	
        return str_errmsg;
    };
    FreeUtil.keepNDecimalFull = function (_value, _index) {
    	var v_par = {
                value: _value,
                index: _index
    	};

    	var jso_validate = JSPFree.doClassMethodCall(FreeUtil.CommDMOClassName, "validateBigNumber", v_par);  //
        var value = jso_validate.value;
        
        return value;
	};

    //执行保存卡片数据
    FreeUtil.execInsertBillCardData = function (_ds, _templetcode, _formdata) {
        var jso_par = {ds: _ds, templetcode: _templetcode, formdata: _formdata};  //只有模板编码与表单数据
        var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.CommDMOClassName, "insertBillCardData", jso_par);  //保存数据
    };

    //执行保存卡片数据
    FreeUtil.execUpdateBillCardData = function (_ds, _templetcode, _formdata, _sqlwhere) {
        var jso_par = {ds: _ds, templetcode: _templetcode, formdata: _formdata, SQLWhere: _sqlwhere};  //
        var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.CommDMOClassName, "updateBillCardData", jso_par);  //保存数据
    };

    //执行保存删除数据,新增与修改与card,删除直接是list
    FreeUtil.execDeleteBillListdData = function (_ds, _templetcode, _table, _sqlwhere) {
        var jso_par = {ds: _ds, templetcode: _templetcode, savetable: _table, SQLWhere: _sqlwhere};  //
        var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.CommDMOClassName, "deleteBillListData", jso_par);  //保存数据
    };

    //刷新当前行
    FreeUtil.refreshBillListCurrRow = function (_billList) {
        var jso_rowdata = _billList.datagrid('getSelected');  //取提当前行数据
        if (jso_rowdata == null) {
            return;  //
        }

        //根据数据计算出行号
        var str_rownumValue = jso_rowdata['_rownum'];  //取得行号数据
        var li_row = _billList.datagrid('getRowIndex', str_rownumValue);  //

        //根据模板定义计算出主键的where条件
        var jso_templetVO = _billList.templetVO;  //模板VO
        var str_sqlwhere = FreeUtil.getSQLWhereByPK(jso_templetVO, jso_rowdata);  //SQL条件

        //重新查询这一行数据
        var jso_newdata = FreeUtil.getBillListOneRowData(_billList, str_sqlwhere);

        //更改页面数据,经过debug发现,_rownum没有被冲掉,这说明easyUI做了交叉处理,即不是整行更新,而是找到对应字段才更新!
        _billList.datagrid('updateRow', {index: li_row, row: jso_newdata});

        //刷新数据,一定要手动刷新,因为有可能批量插入数据,然后一次刷新!如果每次都刷新,则会性能较低
        _billList.datagrid('refreshRow', li_row);
    };

    //刷新当前所有选中行
    FreeUtil.refreshBillListCurrRows = function (_billList) {
        var jsy_rowdata = _billList.datagrid('getSelections');  //取提当前行数据
        if (jsy_rowdata == null || jsy_rowdata.length <= 0) {
            return;  //
        }

        //循环处理各行
        for (var i = 0; i < jsy_rowdata.length; i++) {
            var jso_rowdata = jsy_rowdata[i];  //这一行的数据

            //根据数据计算出行号
            var str_rownumValue = jso_rowdata['_rownum'];  //取得行号数据
            var li_row = _billList.datagrid('getRowIndex', str_rownumValue);  //

            //根据模板定义计算出主键的where条件
            var jso_templetVO = _billList.templetVO;  //模板VO
            var str_sqlwhere = FreeUtil.getSQLWhereByPK(jso_templetVO, jso_rowdata);  //SQL条件

            //重新查询这一行数据
            var jso_newdata = FreeUtil.getBillListOneRowData(_billList, str_sqlwhere);

            //更改页面数据,经过debug发现,_rownum没有被冲掉,这说明easyUI做了交叉处理,即不是整行更新,而是找到对应字段才更新!
            _billList.datagrid('updateRow', {index: li_row, row: jso_newdata});

            //刷新数据,一定要手动刷新,因为有可能批量插入数据,然后一次刷新!如果每次都刷新,则会性能较低
            _billList.datagrid('refreshRow', li_row);
        }
    };

    //刷新当前页面,许多时候需要直接刷新当前页面
    //先取得页面上所有数据,然后分别查询,这样其实性能有点慢,以后优化,即先拼出主键,然后查询一次!
    FreeUtil.refreshBillListCurrPage = function (_billList) {
        var jso_AllData = _billList.datagrid('getData');
        if (jso_AllData == null) {
            return;
        }
        var jsy_rowdata = jso_AllData.rows;  //取提当前行数据
        if (jsy_rowdata == null || jsy_rowdata.length <= 0) {
            return;  //
        }

        //循环处理各行
        for (var i = 0; i < jsy_rowdata.length; i++) {
            var jso_rowdata = jsy_rowdata[i];  //这一行的数据

            //根据数据计算出行号
            var str_rownumValue = jso_rowdata['_rownum'];  //取得行号数据
            var li_row = _billList.datagrid('getRowIndex', str_rownumValue);  //

            //根据模板定义计算出主键的where条件
            var jso_templetVO = _billList.templetVO;  //模板VO
            var str_sqlwhere = FreeUtil.getSQLWhereByPK(jso_templetVO, jso_rowdata);  //SQL条件

            //重新查询这一行数据
            var jso_newdata = FreeUtil.getBillListOneRowData(_billList, str_sqlwhere);

            //更改页面数据,经过debug发现,_rownum没有被冲掉,这说明easyUI做了交叉处理,即不是整行更新,而是找到对应字段才更新!
            _billList.datagrid('updateRow', {index: li_row, row: jso_newdata});

            //刷新数据,一定要手动刷新,因为有可能批量插入数据,然后一次刷新!如果每次都刷新,则会性能较低
            _billList.datagrid('refreshRow', li_row);
        }
    };

    //刷新列表某一条行
    FreeUtil.refreshBillListOneRowData = function (_grid, _row, _sqlwhere) {
        //重新查询数据库,而且只查一条数据
        var jso_newdata = FreeUtil.getBillListOneRowData(_grid, _sqlwhere);  //

        //更改页面数据,经过debug发现,_rownum没有被冲掉,这说明easyUI做了交叉处理,即不是整行更新,而是找到对应字段才更新!
        _grid.datagrid('updateRow', {index: _row, row: jso_newdata});

        //刷新数据,一定要手动刷新,因为有可能批量插入数据,然后一次刷新!如果每次都刷新,则会性能较低
        _grid.datagrid('refreshRow', _row);
    };

    //新增后前端刷新
    FreeUtil.refreshBillListOneRowDataByInsert = function (_grid, _sqlwhere) {
        //重新查询数据库,而且只查一条数据
        var jso_newdata = FreeUtil.getBillListOneRowData(_grid, _sqlwhere);  //

        //补上_rownum,新增数据很特别要补上_rownum，可以直接使用当前时间,因为只要每一行不一样就可以! 之前查询的是1-20,新增的就是超长时间!这不影响逻辑.
        var li_now = Date.now();
        jso_newdata["_rownum"] = "" + li_now;

        //直接在前端新增一行,新增行是直接生效
        _grid.datagrid('insertRow', {index: 0, row: jso_newdata});

        //立即选中这一行,效果更好!
        _grid.datagrid('selectRow', 0);
    };

    //修改后刷新某一行
    FreeUtil.refreshBillListOneRowDataByUpdate = function (_grid, _row, _sqlwhere) {
        //重新查询数据库,而且只查一条数据
        var jso_newdata = FreeUtil.getBillListOneRowData(_grid, _sqlwhere);  //

        //更改页面数据,经过debug发现,_rownum没有被冲掉,这说明easyUI做了交叉处理,即不是整行更新,而是找到对应字段才更新!
        _grid.datagrid('updateRow', {index: _row, row: jso_newdata});

        //刷新数据,一定要手动刷新,因为有可能批量插入数据,然后一次刷新!如果每次都刷新,则会性能较低
        _grid.datagrid('refreshRow', parseInt(_row));
    };

    //取得按钮对应的表格
    FreeUtil.getBtnBindBillList = function (_btn) {
        var str_btnid = _btn.id;
        var str_billListId = str_btnid.substring(0, str_btnid.lastIndexOf("_"));  //
        return window[str_billListId];
    };

    //将某一个ele删除
    FreeUtil.dirCloseOneElement = function (_id) {
        var dom_item = document.getElementById(_id);
        dom_item.parentNode.removeChild(dom_item);  //删除
    };

    //创建窗口
    FreeUtil.openHtmlMsgBox = function (_title, _width, _height, _html) {
        var li_now = Date.now();
        //计算left与top
        var li_left = 50;
        var li_top = 50;
        var li_win_Width = 600;
        var li_win_Height = 400;

        li_win_Width = self.innerWidth;  //窗口高度
        li_win_Height = self.innerHeight;  //窗口高度
        li_left = (li_win_Width - _width) / 2;  //窗口居中时的X位置
        li_top = (li_win_Height - _height) / 2;  //窗口居中的Y位置

        //创建窗口
        var dom_div_root = document.createElement("div");  //

        dom_div_root.setAttribute("id", "htmlmsgbox_" + li_now);  //当前时间毫秒
        dom_div_root.setAttribute("style", "position:absolute;left:" + li_left + "px;top:" + li_top + "px;width:" + _width + "px;height:" + _height + "px;z-index:" + li_now + ";background:#FFFFFF;");

        //创建div上面的标题
        var dom_div_title = document.createElement("div");  //
        dom_div_title.setAttribute("style", "width:100%;height:25px;background:#3C8DBC");

        //标题说明
        var dom_titletext = document.createElement("span");  //
        dom_titletext.textContent = _title;  //
        dom_titletext.setAttribute("style", "float:left;color:white;font-size:12px;margin-top:5px;margin-left:5px");
        dom_div_title.appendChild(dom_titletext);

        //关闭按钮
        var dom_close = document.createElement("span");  //
        dom_close.textContent = "×";
        dom_close.setAttribute("style", "float:right;display:block;width:25px;height:25px;color:white;font-size:20px;text-align:center;cursor:pointer;");
        dom_close.setAttribute("onclick", "FreeUtil.dirCloseOneElement('htmlmsgbox_" + li_now + "');");
        dom_div_title.appendChild(dom_close);

        dom_div_root.appendChild(dom_div_title);  //加入标题

        var li_height_center = _height - 25 - 2;  //标题上下占一个像素
        var dom_div_center = document.createElement("div");  //
        dom_div_center.setAttribute("style", "width:100%;height:" + li_height_center + "px;background:#FFFFFF;border:1px solid #DDDDDD;overflow:auto");
        dom_div_center.innerHTML = _html;  //

        dom_div_root.appendChild(dom_div_center);  //加入内容
        document.body.appendChild(dom_div_root);
    };

    //第一个方法是没有远程调用，但有个限制是无法比父亲窗口大,所以提供第二个方法
    FreeUtil.openHtmlMsgBox2 = function (_title, _width, _height, _html, _isText) {
        var jso_par = {_BillCardData: {htmltext: _html}, isText: _isText};
        FreeUtil.openDialog(_title, "/frame/js/yuformat/HtmlTextDialog.js", _width, _height, jso_par);
    };

    //从模板VO中找出所有参与查询的数据!
    FreeUtil.getQueryItemFromTempletVO = function (_templetVO, _queryLevel) {
        var itemArray = _templetVO.templet_option_b;
        var v_rtArray = new Array();

        for (var i = 0; i < itemArray.length; i++) {
            if (_queryLevel == 1) { //简单条件
                if ("Y" == itemArray[i].query_isshow) {  //只有定义参与查询的才加入计算!
                    v_rtArray.push(itemArray[i]);  //直接送入一条对象
                }
            } else if (_queryLevel == 2) { //更多条件
                if ("Y" == itemArray[i].query2_isshow) {  //只有定义参与查询的才加入计算!
                    v_rtArray.push(itemArray[i]);  //直接送入一条对象
                }
            } else if (_queryLevel == 3) { //更多条件
                if ("Y" == itemArray[i].query3_isshow) {  //只有定义参与查询的才加入计算!
                    v_rtArray.push(itemArray[i]);  //直接送入一条对象
                }
            }
        }
        return v_rtArray;
    };


    //从整个form表单中找出指定列的查询数据,要根据文本框、下拉框、日历、参照。。。进行不同的处理
    FreeUtil.getQueryItemSQLCons = function (_form, _templetItemVO) {
        var str_itemType = _templetItemVO.itemtype;
        var str_queryItemType = _templetItemVO.query_itemtype;
        if (typeof str_queryItemType != "undefined" && str_queryItemType != "") {
            str_itemType = str_queryItemType;  //如果指定了查询时的控件类型
        }
        //计算比较类型,即有时强制必须是某种类型
        var str_query_comparetype = _templetItemVO.query_comparetype;
        if (typeof str_query_comparetype != "undefined" && str_query_comparetype != null) {
            if (str_query_comparetype == "=") {
                return FreeUtil.getQueryItemSQLCons_Equals(_form, _templetItemVO, str_itemType);  //等于计算
            } else if (str_query_comparetype == "like") {
                return FreeUtil.getQueryItemSQLCons_Like(_form, _templetItemVO, str_itemType);  //like计算
            } else if (str_query_comparetype == "in") {
                return FreeUtil.getQueryItemSQLCons_In(_form, _templetItemVO, str_itemType);  //like计算
            } else if (str_query_comparetype == "isnull") {
                return FreeUtil.getQueryItemSQLCons_IsNull(_form, _templetItemVO, str_itemType);  //like计算
            } else if (str_query_comparetype == "isnotnull") {
                return FreeUtil.getQueryItemSQLCons_IsNotNull(_form, _templetItemVO, str_itemType);  //like计算
            } else {
                return "query_comparetype[格式]不对";
            }
        } else { //如果没有显式指定"计算比较类型".
            if (str_itemType == "文本框") {
                return FreeUtil.getQueryItemSQLCons_Like(_form, _templetItemVO, str_itemType);  //
            } else if (str_itemType == "下拉框") {
                //以前下拉框是=操作,后来要改成like,然后后来又要改成=，这样改来改去何时是个头呢?xch
                return FreeUtil.getQueryItemSQLCons_Equals(_form, _templetItemVO, str_itemType);
                //return FreeUtil.getQueryItemSQLCons_Like(_form,_templetItemVO,str_itemType);
            } else if (str_itemType == "日历") {
                return FreeUtil.getQueryItemSQLCons_Equals(_form, _templetItemVO, str_itemType);  //
            } else if (str_itemType == "日历区间") {
                return FreeUtil.getQueryItemSQLCons_DirAdd(_form, _templetItemVO);  //
            } else if (str_itemType == "勾选框") {
                return FreeUtil.getQueryItemSQLCons_CheckBox(_form, _templetItemVO);  //
            } else if (str_itemType == "列表参照" || str_itemType == "树型参照" || str_itemType == "自定义参照") {
                return FreeUtil.getQueryItemSQLCons_Like(_form, _templetItemVO, str_itemType);  //
            } else {
                return FreeUtil.getQueryItemSQLCons_Like(_form, _templetItemVO, str_itemType);  //
            }
        }
    };

    //='XX'的SQL查询
    FreeUtil.getQueryItemSQLCons_Equals = function (_form, _templetItemVO, _itemtype) {
        var str_itemkey = _templetItemVO.itemkey;
        var str_itemname = _templetItemVO.itemname;

        var str_itemValue = FreeUtil.getFormItemValue(_form, _templetItemVO);  //
        //查询过滤类型,可能是=，也可能是like,默认先是Like
        if (str_itemValue == null || str_itemValue == "") {
            if ("Y" == _templetItemVO.query_ismust) {
                return "◆【" + str_itemname + "】查询条件不能为空!";
            } else {
                return "";
            }
        } else {
            if (_itemtype.indexOf("参照") > 0) {  //如果是各种参照,并且有/,则取/前面的编码!因为经常返回的编码与名称,但过滤时使用编码
                var li_pos = str_itemValue.indexOf("/");
                if (li_pos > 0) {
                    str_itemValue = str_itemValue.substring(0, li_pos);  //取前面的值
                }
            }
            return str_itemkey + " = '" + str_itemValue + "'";
        }
    };

    //Like %XX%的SQL查询
    FreeUtil.getQueryItemSQLCons_Like = function (_form, _templetItemVO, _itemtype) {
    	var str_itemkey = _templetItemVO.itemkey;
        var str_itemname = _templetItemVO.itemname;
        var str_query_ismust = _templetItemVO.query_ismust;

        //这个取值好象有问题
        var str_itemValue = FreeUtil.getFormItemValue(_form, _templetItemVO);  //

        //查询过滤类型,可能是=，也可能是like,默认先是Like
        if (str_itemValue == null || str_itemValue == "") {
            if ("Y" == str_query_ismust) {
                return "◆【" + str_itemname + "】查询条件不能为空!";
            } else {
                return "";
            }
        } else {
            if (_itemtype.indexOf("参照") > 0) {  //如果是各种参照,并且有/,则取/前面的编码!因为经常返回的编码与名称,但过滤时使用编码
                var li_pos = str_itemValue.indexOf("/");
                if (li_pos > 0) {
                    str_itemValue = str_itemValue.substring(0, li_pos);  //取前面的值
                }
            }
            return str_itemkey + " like '%" + str_itemValue + "%'";
        }
    };

    //in ('aa','bb','cc')的操作
    FreeUtil.getQueryItemSQLCons_In = function (_form, _templetItemVO, _itemtype) {
    	var str_itemkey = _templetItemVO.itemkey;
        var str_itemname = _templetItemVO.itemname;
        var str_query_ismust = _templetItemVO.query_ismust;

        //这个取值好象有问题
        var str_itemValue = FreeUtil.getFormItemValue(_form, _templetItemVO); //

        //查询过滤类型,可能是=，也可能是like,默认先是Like
        if (str_itemValue == null || str_itemValue == "") {
            if ("Y" == str_query_ismust) {
                return "◆【" + str_itemname + "】查询条件不能为空!";
            } else {
                return "";
            }
        } else {
            var ary_itemvalue = str_itemValue.split(";");  //分号相隔
            var str_sql = [];  //
            for (var i = 0; i < ary_itemvalue.length; i++) {
                var str_inItemValue = ary_itemvalue[i];  //
                if (str_inItemValue != null && str_inItemValue != "") {
                    if (_itemtype.indexOf("参照") > 0) {  //如果是各种参照,并且有/,则取/前面的编码!因为经常返回的编码与名称,但过滤时使用编码
                        var li_pos = str_inItemValue.indexOf("/");
                        if (li_pos > 0) {
                            str_inItemValue = str_inItemValue.substring(0, li_pos);  //取前面的值
                        }
                    }
                    str_sql.push("'" + str_inItemValue + "'")
                }
            }

            var str_sql_new = "";
            if (str_sql != null && str_sql.length > 0) {
                var size = str_sql.length;
                if (size == 1) {
                    str_sql_new = "(" + str_itemkey + " in (" + str_sql[0] + "))";
                } else {
                    for (var k = 0; k < size; k++) {
                        if (k == 0) { //开头
                            str_sql_new = "(" + str_itemkey + " in (" + str_sql[k];
                        } else if (k == size - 1) { //结尾
                            str_sql_new = str_sql_new + "," + str_sql[k] + "))";
                        } else if ((k % 999) == 0 && k != size - 1) {
                            str_sql_new = str_sql_new + ") or " + str_itemkey + " in (" + str_sql[k];
                        } else { //其余情况都走这里
                            str_sql_new = str_sql_new + "," + str_sql[k];
                        }
                    }
                }
            }

            return str_sql_new;
        }
    };

    // 'aa' is null 的操作
    FreeUtil.getQueryItemSQLCons_IsNull = function (_form, _templetItemVO, _itemtype) {
    	var str_itemkey = _templetItemVO.itemkey;
        var str_itemname = _templetItemVO.itemname;
        var str_query_ismust = _templetItemVO.query_ismust;

        //这个取值好象有问题
        var str_itemValue = FreeUtil.getFormItemValue(_form, _templetItemVO);

        //查询过滤类型,可能是=，也可能是like,默认先是Like
        if (str_itemValue == null || str_itemValue == "") {
            if ("Y" == str_query_ismust) {
                return "◆【" + str_itemname + "】查询条件不能为空!";
            } else {
                return "";
            }
        } else {
            var ary_itemvalue = str_itemValue.split(";");  //分号相隔
            var str_sql = [];  //
            for (var i = 0; i < ary_itemvalue.length; i++) {
                var str_inItemValue = ary_itemvalue[i];  //
                if (str_inItemValue != null && str_inItemValue != "") {
                    if (_itemtype.indexOf("参照") > 0) {  //如果是各种参照,并且有/,则取/前面的编码!因为经常返回的编码与名称,但过滤时使用编码
                        var li_pos = str_inItemValue.indexOf("/");
                        if (li_pos > 0) {
                            str_inItemValue = str_inItemValue.substring(0, li_pos);  //取前面的值
                        }
                    }
                    str_sql.push("" + str_inItemValue + "")
                }
            }

            var str_sql_new = "";
            if (str_sql != null && str_sql.length > 0) {
                var size = str_sql.length;
                if (size == 1) {
                    str_sql_new = "(" + str_sql[0] + " is null)";
                } else {
                    for (var k = 0; k < size; k++) {
                        if (k == 0) { //开头
                            str_sql_new = "(" + str_sql[k] + " is null";
                        } else if (k == size - 1) { //结尾
                            str_sql_new = str_sql_new + " and " + str_sql[k] + " is null)";
                        } else { //其余情况都走这里
                            str_sql_new = str_sql_new + " and  " + str_sql[k] + " is null";
                        }
                    }
                }
            }

            return str_sql_new;
        }
    };

    // 'aa' is not null 的操作
    FreeUtil.getQueryItemSQLCons_IsNotNull = function (_form, _templetItemVO, _itemtype) {
    	var str_itemkey = _templetItemVO.itemkey;
        var str_itemname = _templetItemVO.itemname;
        var str_query_ismust = _templetItemVO.query_ismust;

        //这个取值好象有问题
        var str_itemValue = FreeUtil.getFormItemValue(_form, _templetItemVO);

        //查询过滤类型,可能是=，也可能是like,默认先是Like
        if (str_itemValue == null || str_itemValue == "") {
            if ("Y" == str_query_ismust) {
                return "◆【" + str_itemname + "】查询条件不能为空!";
            } else {
                return "";
            }
        } else {
            var ary_itemvalue = str_itemValue.split(";");  //分号相隔
            var str_sql = [];  //
            for (var i = 0; i < ary_itemvalue.length; i++) {
                var str_inItemValue = ary_itemvalue[i];  //
                if (str_inItemValue != null && str_inItemValue != "") {
                    if (_itemtype.indexOf("参照") > 0) {  //如果是各种参照,并且有/,则取/前面的编码!因为经常返回的编码与名称,但过滤时使用编码
                        var li_pos = str_inItemValue.indexOf("/");
                        if (li_pos > 0) {
                            str_inItemValue = str_inItemValue.substring(0, li_pos);  //取前面的值
                        }
                    }
                    str_sql.push("" + str_inItemValue + "")
                }
            }

            var str_sql_new = "";
            if (str_sql != null && str_sql.length > 0) {
                var size = str_sql.length;
                if (size == 1) {
                    str_sql_new = "(" + str_sql[0] + " is not null)";
                } else {
                    for (var k = 0; k < size; k++) {
                        if (k == 0) { //开头
                            str_sql_new = "(" + str_sql[k] + " is not null";
                        } else if (k == size - 1) { //结尾
                            str_sql_new = str_sql_new + " and " + str_sql[k] + " is not null)";
                        } else { //其余情况都走这里
                            str_sql_new = str_sql_new + " and  " + str_sql[k] + " is not null";
                        }
                    }
                }
            }

            return str_sql_new;
        }
    };
    
    //直接拼接
    FreeUtil.getQueryItemSQLCons_DirAdd = function (_form, _templetItemVO) {
        var str_itemkey = _templetItemVO.itemkey;
        var str_itemname = _templetItemVO.itemname;

        var str_itemValue = FreeUtil.getFormItemValue(_form, _templetItemVO);  //
        //查询过滤类型,可能是=，也可能是like,默认先是Like
        if (str_itemValue == null || str_itemValue == "") {
            if ("Y" == _templetItemVO.query_ismust) {
                return "◆【" + str_itemname + "】查询条件不能为空!";
            } else {
                return "";
            }
        } else {
            return "(" + str_itemValue + ")";
        }
    };


    //勾选框
    FreeUtil.getQueryItemSQLCons_CheckBox = function (_form, _templetItemVO) {
        var str_itemkey = _templetItemVO.itemkey;
        var str_itemname = _templetItemVO.itemname;

        var str_itemValue = FreeUtil.getFormItemValue(_form, _templetItemVO);  //
        //查询过滤类型,可能是=，也可能是like,默认先是Like
        if (str_itemValue == null || str_itemValue == "") {
            if ("Y" == _templetItemVO.query_ismust) {
                return "◆【" + str_itemname + "】查询条件不能为空!";
            } else {
                return "";
            }
        } else {
            return str_itemkey + " = '" + str_itemValue + "'";
        }
    };


    //查询面板切换第二种查询条件,就是远程调用,然后重绘
    FreeUtil.billQuerySwitch2 = function (_billQuery, _type) {
        var str_divid = _billQuery.divid;  //取得id
        var str_templetcode = _billQuery.templetVO.templet_option.templetcode; //模板编码

        var jso_par = {webcontext: v_context, divid: str_divid, templetCode: str_templetcode, buildType: _type};
        var jso_data = FreeUtil.doClassMethodCall(FreeUtil.JSPBuilderClassName, "getBillQuerySwitch2Html", jso_par);
        var str_html = jso_data.html;  //只生成html
        var li_newHeight = jso_data.domheight;  //html的高度,肯定高度不一样

        var str_rootDivId = str_divid + '_BillQueryRoot';  //查询面板所在的父亲容器的id
        var str_queryDivId = str_divid + '_BillQueryNorth';  //查询面板自己的id

        //先删除原来的
        $('#' + str_rootDivId).layout('remove', "north");

        //再重新添加
        $('#' + str_rootDivId).layout('add', {
            id: str_queryDivId,
            region: 'north',
            height: li_newHeight
        });

        //再加入实际内容
        var dom_div = document.getElementById(str_queryDivId);
        dom_div.innerHTML = str_html;  //重新输入Html

        $.parser.parse('#' + str_queryDivId);

        //设置查询框的查询级别,这样在拼接SQL才会拼对
        _billQuery["queryLevel"] = _type;
    };

    //查询面板中【自由组装SQL】的动作,还支持自定义排序
    FreeUtil.billQueryOpenFreeBuild = function (_billQuery) {
        if (typeof _billQuery.popDialogId != "undefined") {
            FreeUtil.showDialogById(_billQuery.popDialogId);  //直接显示!
        } else {
            var str_divid = _billQuery.divid;
            var billList = self[str_divid + "_BillList"];  //肯定还有一个对应有表格,前辍一样!
            var str_templetCode = _billQuery.templetVO.templet_option.templetcode;
            var jso_par = {templetcode: str_templetCode};
            FreeUtil.openDialog("灵活查询【勾选右边数据列会自动创建查询条件】", "/frame/js/yuformat/BillQueryBuildSQLDialog.js", 800, 560, jso_par, function (_rtdata) {
                if ("Confirm" == _rtdata.result) {
                    _billQuery["popDialogId"] = _rtdata.popDialogId; //
                    FreeUtil.queryDataByConditonReal(billList, _rtdata.buildWhereSQL, false, 1, _rtdata.buildOrderBy);
                } else {
                    _billQuery["popDialogId"] = _rtdata.popDialogId; //
                }
            }, false);  //不要右上角的关闭按钮,因为那个关闭是清除div,而这里是隐藏
        }
    };

    //点击列表中某个格子的值
    FreeUtil.onBillListClickCell = function (_billList, _index, _field, _value) {
        var jsy_items = _billList.templetVO.templet_option_b;  //取得模板子表VO[]
        var jso_item = null;  //
        for (var i = 0; i < jsy_items.length; i++) {
            if (jsy_items[i].itemkey == _field) {
                jso_item = jsy_items[i];
                break;  //
            }
        }

        //如果是勾选框,则直接把值修改,后面要加一个参数,并不是每个都可以修改的
        if (jso_item != null && jso_item.itemtype == "勾选框") {
            var str_action = jso_item.list_checkaction;  //处理动作
            if (typeof str_action == "undefined" || str_action == null || str_action == "") {
                return;  //如果没有定义勾选动作,则不做
            }

            var str_newValue = "";
            if (_value == null || _value == "" || _value == "N") {
                str_newValue = "Y";
            } else if (_value == "Y") {
                str_newValue = "N";
            }
            if (str_newValue != "") {
                var jso_newData = {};
                jso_newData[_field] = str_newValue;
                _billList.datagrid('updateRow', {index: _index, row: jso_newData});
            }

            var fn_action = self[str_action];
            if (typeof fn_action == "function") {
                fn_action(_billList, _index, _field, str_newValue);
            } else {
                console.log(str_action);
            }
        }
    };

    //取得一个表单中的某一项的值
    FreeUtil.getFormItemValue = function (_form, _templetItemVO) {
        var str_itemkey = _templetItemVO.itemkey;  //列名!
        var str_itemtype = _templetItemVO.itemtype;  //类型

        //找到表单中的对应的表单项
        var dom_findElement = FreeUtil.findFormItemByName(_form, str_itemkey);

        //如果没有找到匹配的,则返回空字符串
        if (dom_findElement == null) {
            return "";
        }

        var str_value = dom_findElement.value;
        //不同的控件类型返回值的原理不一样!
        if (str_itemtype == "勾选框") {
            if (dom_findElement.checked) {
                return "Y";
            } else {
                return "N";
            }
        } else {
            return str_value;
        }

        return str_value;
    };

    //搜索表单项,根据名称
    FreeUtil.findFormItemByName = function (_form, _itemkey) {
        var formItemlist = _form.elements;  //先把form表单中所有列的值取出来
        var dom_findElement = null;
        //去所有表单项中寻找名字等于这一列的,因为EasyUI其实搞了三个输入框，但实际值存储在一个hidden域中,而这个隐藏域的name就等于该列名
        for (var i = 0; i < formItemlist.length; i++) {
            var dom_item = formItemlist.item(i);
            var str_display = dom_item.style.display;
            if ("none" == str_display) {
                continue;
            }
            //console.log(dom_item);
            if (dom_item.getAttribute("name") == _itemkey) {  //表单是根据name来取值的
                dom_findElement = dom_item
                break;
            }
        }
        return dom_findElement;  //
    };

    //根据itemkey从模板子表数组中找出VO
    FreeUtil.findItemVOByItemkey = function (_templetItemVOs, _itemkey) {
        for (var i = 0; i < _templetItemVOs.length; i++) {
            if (_templetItemVOs[i].itemkey == _itemkey) {
                return _templetItemVOs[i];
            }
        }
        return null;
    };


    //拼接SQLwhere条件根据主键字段与主键值
    FreeUtil.getSQLWhereByPK = function (_templetVO, _data) {
        var str_pk = _templetVO.templet_option.pkname.toLocaleLowerCase();  //主键字段名
        var pkItems = str_pk.split(",");  //有可能有多个联合主键
        if (pkItems.length == 1) {
            var str_pkvalue = _data[pkItems[0]];
            var str_sql = " " + pkItems[0] + "='" + str_pkvalue + "' ";
            return str_sql;  //
        } else {
            var str_sql = "";  //
            for (var i = 0; i < pkItems.length; i++) {
                var str_pkvalue = _data[pkItems[i]];
                if (i == 0) {
                    str_sql = str_sql + " " + pkItems[i] + "='" + str_pkvalue + "' ";
                } else {
                    str_sql = str_sql + " and " + pkItems[i] + "='" + str_pkvalue + "' ";
                }
            }
            return str_sql;  //
        }
    };

    //跳转到某一页!
    FreeUtil.resetOnePageSize = function (_grid, _newOnePageSize) {
        _grid.OnePageSize = _newOnePageSize;  //先设置好!
        if ((typeof _grid.CurrSQL) == "undefined") {
            return;
        }

        FreeUtil.queryDataByConditonRealBySQL(_grid, _grid.CurrSQL, false, true, 1);
    };

    //跳转到某一页!
    FreeUtil.skipToOnePage = function (_grid, _newPage) {
        if ((typeof _grid.CurrSQL) == "undefined") {
            //alert("还没有查询数据呢,请先查询!");  //
            return;
        }
        //直接拿当前页面的SQL去重新查询,而且当前页
        if ("N" == _grid["NotTriggerPageSelectedEvent"]) {
            //以前发生查询后,然后跳转到第一页,然后又触发跳转页事件,然后再次查询,即查询两次!所以需要一个开关，如果不触发则不做!
        } else {
            FreeUtil.queryDataByConditonRealBySQL(_grid, _grid.CurrSQL, false, true, _newPage);
        }
    };

    //重置到第一页
    FreeUtil.resetToFirstPage = function (_billList) {
        var isPager = true;  //默认是分页
        if ("N" == _billList.templetVO.templet_option.list_ispagebar) {
            isPager = false;  //不分页
        }
        if (isPager) {
            var js_pager = _billList.datagrid('getPager'); //
            _billList["NotTriggerPageSelectedEvent"] = "N"; //以前发生查询后,然后跳转到第一页,然后又触发跳转页事件,然后再次查询,即查询两次!所以需要一个开关
            js_pager.pagination('select', 1); //查询时重新设置当前页是第1页,否则如果先跳至第2页,再查询时,会停在第2页,xch
            _billList["NotTriggerPageSelectedEvent"] = "Y";
        }
    };


    //设置卡片中下拉框的数据
    FreeUtil.setBillCardComboBoxData = function (_billCard, _itemkey, _jsyData) {
        //因为EasyUI下拉框赋值必须是对象中要的value与tetx这两个字段,而查询过来的数据可能没有这两个字段,所以需要处理一下
        for (var i = 0; i < _jsyData.length; i++) {
            var jso_rowData = _jsyData[i];
            var ary_keys = [];  //存储所有字段
            for (var _key in jso_rowData) {  //遍历所有key
                ary_keys.push(_key);  //加入数组
            }
            var str_value = jso_rowData[ary_keys[0]];  //第1列的值
            var str_text = jso_rowData[ary_keys[1]];  //第2列的值
            jso_rowData["value"] = str_value;  //重新设置value的值,如果原来有此字段,则覆盖之,如果原来没有，则添加之
            jso_rowData["text"] = str_text; //重新设置text的值,如果原来有此字段,则覆盖之,如果原来没有，则添加之
        }

        $('#' + _itemkey).combobox('loadData', _jsyData);  //重新加载下拉框的数据
    };


    //设置卡片中下拉框的数据
    FreeUtil.setBillCardComboBoxData2 = function (_billCard, _itemkey, _jsyData) {
        //因为EasyUI下拉框赋值必须是对象中要的value与tetx这两个字段,而查询过来的数据可能没有这两个字段,所以需要处理一下
        for (var i = 0; i < _jsyData.length; i++) {
            var jso_rowData = _jsyData[i];
            /*var ary_keys = [];  //存储所有字段
     for(var _key in jso_rowData){  //遍历所有key
      ary_keys.push(_key);  //加入数组
     }*/
            var str_value = jso_rowData.id;  //第1列的值
            var str_text = jso_rowData.name;  //第2列的值
            jso_rowData["value"] = str_value;  //重新设置value的值,如果原来有此字段,则覆盖之,如果原来没有，则添加之
            jso_rowData["text"] = str_text; //重新设置text的值,如果原来有此字段,则覆盖之,如果原来没有，则添加之
        }

        $('#' + _itemkey).combobox('loadData', _jsyData);  //重新加载下拉框的数据
    };


    //加载动态下拉框数据.
    FreeUtil.loadDynamicComboBoxData = function (_billCard, _itemkey) {
        var jsy_itemVOs = _billCard.templetVO.templet_option_b;  //模板子表VO数组
        var jso_itemVO = FreeUtil.findItemVOByItemkey(jsy_itemVOs, _itemkey);  //模板子表VO
        var str_define = jso_itemVO.itemdefine;  //定义
        var defineMap = FreeUtil.parseDefineToMap(str_define);  //解析成Map,以后都这样
        var str_sql = defineMap["SQL"];  //模板编码
        var jso_cardData = FreeUtil.getBillCardFormValue(_billCard);  //卡片表单数据
        str_sql = FreeUtil.replaceStrByMacroKeys(str_sql, jso_cardData);  //替换宏代码后的数据
        var jsy_data = JSPFree.getHashVOs(str_sql);  //查询数据库
        FreeUtil.setBillCardComboBoxData(_billCard, _itemkey, jsy_data);  //重新加载下拉框数据
    }

    //列表参照
    FreeUtil.openBillListRefDialog = function (_billPanel, _itemkey, _event) {
        var str_billtype = _billPanel.billtype;  //单据类型,有可能是"BillCard"与"BillQuery"
        var jso_templetOption_b = _billPanel.templetVO.templet_option_b;  //模板主表配置
        var jso_item = null;  //
        for (var i = 0; i < jso_templetOption_b.length; i++) {
            if (jso_templetOption_b[i].itemkey == _itemkey) {
                jso_item = jso_templetOption_b[i];
                break;  //
            }
        }
        if (jso_item == null) {
            return;
        }
        var str_itemname = jso_item.itemname;  //
        var str_itemrefdialogtitle = jso_item.itemrefdialogtitle;  //
        if (typeof str_itemrefdialogtitle != "undefined") {
            str_itemname = str_itemname + "【" + str_itemrefdialogtitle + "】";
        }

        var str_define = jso_item.itemdefine;  //
        var str_querydefine = jso_item.query_itemdefine;  //查询框的定义

        //如果是查询框,并且定义了查询条件,则参照定义使用查询定义!
        if (str_billtype == "BillQuery" && (typeof str_querydefine != "undefined" && str_querydefine != null && str_querydefine != "")) {
            str_define = str_querydefine;
        }

        if (typeof str_define == "undefined" || str_define == null || str_define == "") {
            alert("该参照没有定义,必须要有定义!");
            return;
        }

        var map = FreeUtil.parseDefineToMap(str_define);  //解析成Map,以后都这样
        var str_templetcode = map["templetcode"];  //模板编码
        var str_cols = map["cols"];

        if (typeof str_templetcode == "undefined") {
            alert("列表参照必须定义参数【templetcode】");
            return; //
        }

        if (typeof str_cols == "undefined") {
            alert("列表参照必须定义参数【cols】");
            return; //
        }

        var str_winsize = map["winsize"];
        var li_width = 700;
        var li_height = 400;
        if (typeof str_winsize != "undefined") {
            var li_pos = str_winsize.indexOf("*");
            li_width = str_winsize.substring(0, li_pos);
            li_height = str_winsize.substring(li_pos + 1, str_winsize.length);
        }

        //是否多选,如果没有定义,则返回的是undefined
        var str_ismulti = map["ismulti"];

        //where条件.
        var str_whereSQL = map["whereSQL"];
        if (typeof str_whereSQL != "undefined" && str_whereSQL != null && str_whereSQL != "") {
            //如果是以双引号开头的,则截掉双引号
            if (str_whereSQL.substring(0, 1) == "\"") {
                str_whereSQL = str_whereSQL.substring(1, str_whereSQL.length - 1);
            }

            //卡片与查询框中点击弹出参照时不一样的!
            if (str_billtype == "BillCard") {
                var jso_CardData = FreeUtil.getBillCardFormValue(_billPanel);
                str_whereSQL = FreeUtil.replaceStrByMacroKeys(str_whereSQL, jso_CardData);
                str_whereSQL = FreeUtil.replaceStrByMacroKeys(str_whereSQL, self.jso_InsertOrUpdateRefSQLWhere);
            } else if (str_billtype == "BillQuery") {  //如果是查询框,则可能需要根据列表中的变量进行再次过滤!
                str_whereSQL = FreeUtil.replaceStrByMacroKeys(str_whereSQL, _billPanel.bindBillList.InsertOrUpdateRefSQLWhere);   //根据设置的变量进行替换
                str_whereSQL = FreeUtil.replaceStrByMacroKeys(str_whereSQL, _billPanel.bindBillList.DefaultValues);   //根据默认值替换
            }
        }

        //构造入参弹出窗口!
        var jso_par = {
            _templetcode: str_templetcode,
            _BillCardData: jso_CardData,
            ismulti: str_ismulti,
            whereSQL: str_whereSQL
        };
        FreeUtil.openDialog(str_itemname, "/frame/js/yuformat/BillListRefDialog.js", li_width, li_height, jso_par, function (_returnData) {
            if (typeof _returnData == "undefined") {
                console.log("没有返回值!!");
                return;
            }

            if (_returnData.type == "dirclose") {
                console.log("直接点右上角叉关闭的,没有返回值!!");
                return;
            }

            //如果没有定义转换列,则直接强行加载
            var colMap = FreeUtil.parseStrToMap(str_cols, ";", "->");
            var jso_newData = {};
            for (var _key in colMap) {
                var str_col2 = colMap[_key];  //列名
                var str_colValue = FreeUtil.getJsonDataSpanText(_returnData, _key);  //_returnData[_key];  //取得返回的对象中的数据!
                jso_newData[str_col2] = str_colValue;  //赋值
            }

            if (str_billtype == "BillQuery") {
                FreeUtil.loadBillQueryData(_billPanel, jso_newData); //如果是查询框,则调用查询框加载数据的方法
            } else {
                //重置表单数据
                FreeUtil.loadBillCardData(_billPanel, jso_newData);

                //触发编辑事件,如果卡片绑定了编辑变化监听器
                if (typeof _billPanel.BillCardItemEditListener != "undefined") {
                    _billPanel.BillCardItemEditListener(_billPanel, _itemkey, jso_newData);
                }
            }
        });
    };

    //列表购物车参照,即左右可以选择加入!
    FreeUtil.openBillListShopCartDialog = function (_billPanel, _itemkey,_isQuery, _event, iscomma) {
        var jso_templetOption_b = _billPanel.templetVO.templet_option_b;  //模板主表配置
        var jso_item = null;  //
        for (var i = 0; i < jso_templetOption_b.length; i++) {
            if (jso_templetOption_b[i].itemkey == _itemkey) {
                jso_item = jso_templetOption_b[i];
                break;  //
            }
        }
        if (jso_item == null) {
            return;
        }
        var str_itemname = jso_item.itemname;  //
        var str_define = jso_item.itemdefine;  //
        if (typeof str_define == "undefined" || str_define == null || str_define == "") {
            alert("该参照没有定义,必须要有定义!");
        }

        var map = FreeUtil.parseDefineToMap(str_define);  //解析成Map,以后都这样
        var str_templetcode = map["templetcode"];  //模板编码
        var str_cols = map["cols"];

        if (typeof str_templetcode == "undefined") {
            alert("列表参照必须定义参数【templetcode】");
            return; //
        }

        if (typeof str_cols == "undefined") {
            alert("列表参照必须定义参数【cols】");
            return; //
        }

        //如果没有定义转换列,则直接强行加载
        var colMap = FreeUtil.parseStrToMap(str_cols, ";", "->");
        var ary_cartkeys = [];  //购物车各列
        var art_cartwidths = [];  //购物车各列宽度
        for (var _key in colMap) {
            ary_cartkeys.push(_key);
            art_cartwidths.push("100");  //默认宽度100
        }

        //有可能会指定宽度
        var str_colswidth = map["colswidth"];
        if (typeof str_colswidth != "undefined") {
            art_cartwidths = str_colswidth.split(";");
        }

        //处理窗口大小
        var str_winsize = map["winsize"];
        var li_width = 700;
        var li_height = 400;
        if (typeof str_winsize != "undefined") {
            var li_pos = str_winsize.indexOf("*");
            li_width = str_winsize.substring(0, li_pos);
            li_height = str_winsize.substring(li_pos + 1, str_winsize.length);
        }


        //取得整个表单中的值
        var jso_CardData = FreeUtil.getBillCardFormValue(_billPanel);
        var jso_par = {
            _templetcode: str_templetcode,
            cartkeys: ary_cartkeys,
            cartwidths: art_cartwidths,
            _BillCardData: jso_CardData,
            iscomma: iscomma
        };

        FreeUtil.openDialog(str_itemname, "/frame/js/yuformat/BillListShopCartDialog.js", li_width, li_height, jso_par, function (_returnData) {
        	if (typeof _returnData == "undefined") {
                console.log("没有返回值!!");
                return;
            }

            if (_returnData.type == "dirclose") {
                console.log("直接点右上角叉关闭的,没有返回值!!");
                return;
            }

            //把数据转换
            var jso_newData = {};
            for (var _key in colMap) {
                var str_col2 = colMap[_key];
                var str_colValue = _returnData[_key];  //取得返回的对象中的数据!
                jso_newData[str_col2] = str_colValue;  //赋值
            }

            //重新加载数据
            FreeUtil.loadBillCardData(_billPanel, jso_newData);

            //触发编辑事件,如果卡片绑定了编辑变化监听器
            if (typeof _billPanel.BillCardItemEditListener != "undefined") {
                _billPanel.BillCardItemEditListener(_billPanel, _itemkey, jso_newData);
            }
        });
    };


    //树型参照
    FreeUtil.openBillTreeRefDialog = function (_billPanel, _itemkey, _event) {
        var str_billtype = _billPanel.billtype;  //单据类型,有可能是"BillCard"与"BillQuery"
        var jso_templetOption_b = _billPanel.templetVO.templet_option_b;  //模板主表配置
        var jso_item = null;  //
        for (var i = 0; i < jso_templetOption_b.length; i++) {
            if (jso_templetOption_b[i].itemkey == _itemkey) {
                jso_item = jso_templetOption_b[i];
                break;  //
            }
        }
        if (jso_item == null) {
            return;
        }
        var str_itemname = jso_item.itemname;  //
        var str_itemrefdialogtitle = jso_item.itemrefdialogtitle;  //
        if (typeof str_itemrefdialogtitle != "undefined") {
            str_itemname = str_itemname + "【" + str_itemrefdialogtitle + "】";
        }

        var str_define = jso_item.itemdefine;  //
        var str_querydefine = jso_item.query_itemdefine;  //查询框的定义

        //如果是查询框,并且定义了查询条件,则参照定义使用查询定义!
        if (str_billtype == "BillQuery" && (typeof str_querydefine != "undefined" && str_querydefine != null && str_querydefine != "")) {
            str_define = str_querydefine;
        }

        if (typeof str_define == "undefined" || str_define == null || str_define == "") {
            alert("该参照没有定义,必须要有定义!");
            return;
        }

        var map = FreeUtil.parseDefineToMap(str_define);  //解析成Map,以后都这样
        var str_templetcode = map["templetcode"];  //模板编码
        var str_cols = map["cols"];

        if (typeof str_templetcode == "undefined") {
            alert("树型参照必须定义参数【templetcode】");
            return; //
        }

        if (typeof str_cols == "undefined") {
            alert("树型参照必须定义参数【cols】");
            return; //
        }

        var str_winsize = map["winsize"];
        var li_width = 700;
        var li_height = 400;
        if (typeof str_winsize != "undefined") {
            var li_pos = str_winsize.indexOf("*");
            li_width = str_winsize.substring(0, li_pos);
            li_height = str_winsize.substring(li_pos + 1, str_winsize.length);
        }

        //取得整个表单中的值
        var jso_CardData = FreeUtil.getBillCardFormValue(_billPanel);
        var jso_par = {_templetcode: str_templetcode, _BillCardData: jso_CardData};
        FreeUtil.openDialog(str_itemname, "/frame/js/yuformat/BillTreeRefDialog.js", li_width, li_height, jso_par, function (_returnData) {
            if (typeof _returnData == "undefined") {
                console.log("没有返回值!!");
                return;
            }

            if (_returnData.type == "dirclose") {
                console.log("直接点右上角叉关闭的,没有返回值!!");
                return;
            }

            //如果没有定义转换列,则直接强行加载
            var colMap = FreeUtil.parseStrToMap(str_cols, ";", "->");  //转换列
            //遍历map
            var jso_newData = {};
            for (var _key in colMap) {
                var str_col2 = colMap[_key];
                var str_colValue = FreeUtil.getJsonDataSpanText(_returnData, _key);  //_returnData[_key];  //取得返回的对象中的数据!
                jso_newData[str_col2] = str_colValue;  //赋值
            }

            if (str_billtype == "BillQuery") {
                FreeUtil.loadBillQueryData(_billPanel, jso_newData); //如果是查询框,则调用查询框加载数据的方法
            } else {
                FreeUtil.loadBillCardData(_billPanel, jso_newData); //加载数据

                //触发编辑事件,如果卡片绑定了编辑变化监听器
                if (typeof _billPanel.BillCardItemEditListener != "undefined") {
                    _billPanel.BillCardItemEditListener(_billPanel, _itemkey, jso_newData);
                }
            }
        });
    };
    
    //公式参照
    FreeUtil.openFormulaRefDialog = function (_billPanel, _itemkey, _event) {
        var str_billtype = _billPanel.billtype;  //单据类型,有可能是"BillCard"与"BillQuery"
        var jso_templetOption_b = _billPanel.templetVO.templet_option_b;  //模板主表配置
        var jso_item = null;  //
        for (var i = 0; i < jso_templetOption_b.length; i++) {
            if (jso_templetOption_b[i].itemkey == _itemkey) {
                jso_item = jso_templetOption_b[i];
                break;  //
            }
        }
        if (jso_item == null) {
            return;
        }
        var str_itemname = jso_item.itemname;  //
        var str_define = jso_item.itemdefine;  //

        if (typeof str_define == "undefined" || str_define == null || str_define == "") {
            alert("该参照没有定义,必须要有定义!");
            return;
        }

        var map = FreeUtil.parseDefineToMap(str_define);  //解析成Map,以后都这样
        var str_dataxml = map["dataxml"];  //模板编码

        if (typeof str_dataxml == "undefined") {
            alert("公式参照必须定义参数【dataxml】");
            return; //
        }

        var str_colSQL = map["colSQL"];  //列SQL，有可能不定义
        var str_winsize = map["winsize"];  //窗口大小
        var li_width = 800;
        var li_height = 650;
        if (typeof str_winsize != "undefined") {
            var li_pos = str_winsize.indexOf("*");
            li_width = str_winsize.substring(0, li_pos);
            li_height = str_winsize.substring(li_pos + 1, str_winsize.length);
        }

        //取得整个表单中的值
        var jso_CardData = FreeUtil.getBillCardFormValue(_billPanel);
        var jso_par = {itemkey: _itemkey, dataxml: str_dataxml, colSQL: str_colSQL, _BillCardData: jso_CardData};
        FreeUtil.openDialog(str_itemname, "/frame/js/yuformat/FormulaRefDialog.js", li_width, li_height, jso_par, function (_returnData) {
            if (typeof _returnData == "undefined") {
                console.log("没有返回值!!");
                return;
            }

            if (_returnData.type == "dirclose") {
                console.log("直接点右上角叉关闭的,没有返回值!!");
                return;
            }
            var jso_newData = {};
            jso_newData[_itemkey] = _returnData["rtdata"];
            FreeUtil.loadBillCardData(_billPanel, jso_newData); //加载数据
        });
    };

    //自定义参照
    FreeUtil.openCustRefDialog = function (_billPanel, _itemkey, _event) {
    	var str_billtype = _billPanel.billtype;  //单据类型,有可能是"BillCard"与"BillQuery"
        var jso_templetOption_b = _billPanel.templetVO.templet_option_b;  //模板主表配置
        var jso_item = null;  //
        for (var i = 0; i < jso_templetOption_b.length; i++) {
            if (jso_templetOption_b[i].itemkey == _itemkey) {
                jso_item = jso_templetOption_b[i];
                break;  //
            }
        }
        if (jso_item == null) {
            return;
        }
        var str_itemname = jso_item.itemname;  //
        var str_itemrefdialogtitle = jso_item.itemrefdialogtitle;  //
        if (typeof str_itemrefdialogtitle != "undefined") {
            str_itemname = str_itemname + "【" + str_itemrefdialogtitle + "】";
        }
        var str_define = jso_item.itemdefine;  //
        var str_querydefine = jso_item.query_itemdefine;  //查询框的定义

        //如果是查询框,并且定义了查询条件,则参照定义使用查询定义!
        if (str_billtype == "BillQuery" && (typeof str_querydefine != "undefined" && str_querydefine != null && str_querydefine != "")) {
            str_define = str_querydefine;
        }

        if (typeof str_define == "undefined" || str_define == null || str_define == "") {
            alert("该参照没有定义,必须要有定义!");
        }
        var map = FreeUtil.parseDefineToMap(str_define);  //解析成Map,以后都这样
        var str_jsfile = map["jsfile"];
        var str_cols = map["cols"];

        if (typeof str_jsfile == "undefined") {
            alert("自定义参照必须定义参数【jsfile】");
            return; //
        }

        //自定义参照的参数
        var str_custpars = map["custpars"]; //客户端变量参数
        var str_templet = map["templetcode"]; //模板参数

        var str_winsize = map["winsize"];
        var li_width = 700;
        var li_height = 400;
        var tabName = map["tabName"]; // 传参数（表名）
        
        if (typeof str_winsize != "undefined") {
            var li_pos = str_winsize.indexOf("*");
            li_width = str_winsize.substring(0, li_pos);
            li_height = str_winsize.substring(li_pos + 1, str_winsize.length);
        }

        //取得整个表单中的值
        var jso_CardData = FreeUtil.getBillCardFormValue(_billPanel);
        var jso_par = {custpars: str_custpars,_templetcode:str_templet, _BillCardData: jso_CardData, tabName: tabName};
        FreeUtil.openDialog(str_itemname, str_jsfile, li_width, li_height, jso_par, function (_returnData) {
            if (typeof _returnData == "undefined") {
                console.log("没有返回值!!");
                return;
            }

            if (_returnData.type == "dirclose") {
                console.log("直接点右上角叉关闭的,没有返回值!!");
                return;
            }

            //如果没有定义转换列,则直接强行加载
            if (typeof str_cols == "undefined" || str_cols == null) {
                FreeUtil.loadBillCardData(_billPanel, _returnData);  //直接加载,只要对上就行
            } else {  //如果定义了转换列!
                var colMap = FreeUtil.parseStrToMap(str_cols, ";", "->");  //转换列
                //遍历map
                var jso_newData = {};
                for (var _key in colMap) {
                    var str_col2 = colMap[_key];
                    var str_colValue = FreeUtil.getJsonDataSpanText(_returnData, _key);  //_returnData[_key];  //取得返回的对象中的数据!
                    jso_newData[str_col2] = str_colValue;  //赋值
                }

                if (str_billtype == "BillQuery") {
                    FreeUtil.loadBillQueryData(_billPanel, jso_newData); //如果是查询框,则调用查询框加载数据的方法
                } else {
                    console.log("参数返回重新设置值!!!");
                    console.log(jso_newData);
                    FreeUtil.loadBillCardData(_billPanel, jso_newData); //加载数据

                    //触发编辑事件,如果卡片绑定了编辑变化监听器
                    if (typeof _billPanel.BillCardItemEditListener != "undefined") {
                        _billPanel.BillCardItemEditListener(_billPanel, _itemkey, jso_newData);
                    }
                }
            }
        });
    };

    //两个日历比较的参照,就是查询时返回日期范围,还可以控制只能在7天之内
    FreeUtil.openTwoDateRefDialog = function (_billPanel, _itemkey, _event, _isQuery) {
        var jso_templetOption_b = _billPanel.templetVO.templet_option_b;  //模板主表配置
        var jso_item = null;  //
        for (var i = 0; i < jso_templetOption_b.length; i++) {
            if (jso_templetOption_b[i].itemkey == _itemkey) {
                jso_item = jso_templetOption_b[i];
                break;  //
            }
        }
        if (jso_item == null) {
            return;
        }
        var str_itemname = jso_item.itemname;  //
        var str_range = jso_item.query_range;
        var str_define = null;
        if (_isQuery) {
            str_define = jso_item.query_itemdefine;  //如果是查询框,则取查询定义
        } else {
            str_define = jso_item.itemdefine;  //
        }

        var map = FreeUtil.parseDefineToMap(str_define);  //解析成Map,以后都这样
        var str_is8 = map["is8"];  //是否是8位格式,有可能是unfefined

        //弹出窗口!
        var jso_par = {itemkey: _itemkey, is8: str_is8, range: str_range};  //参数
        FreeUtil.openDialog(str_itemname, "/frame/js/yuformat/TwoDateRefDialog.js", 550, 380, jso_par, function (_returnData) {
            if (typeof _returnData == "undefined") {
                console.log("没有返回值!!");
                return;
            }

            if (_returnData.type == "dirclose") {
                console.log("直接点右上角叉关闭的,没有返回值!!");
                return;
            }

            //设置值!
            var str_sqlValue = _returnData.sqlValue;
            var str_sqlText = _returnData.sqlText;

            var str_divId = _billPanel.divid;

            var dom_form = null;  //
            if (_billPanel.billtype == "BillQuery") {
                dom_form = document.getElementById(str_divId + "_QueryForm");
            } else if (_billPanel.billtype == "BillCard") {
                dom_form = document.getElementById(str_divId + "_form");
            }
            if (dom_form == null) {
                return;
            }

            //设置参照的实际值与显示值
            FreeUtil.setBillCardRefIdName(dom_form, _itemkey, str_sqlValue, str_sqlText);
        });

    };

    //取得Json中几个字段的拼起来的字符串,_key中key1/key2/key3的格式
    FreeUtil.getJsonDataSpanText = function (_returnData, _key) {
        var ary_keys = _key.split("/");
        var str_spanText = "";
        for (var i = 0; i < ary_keys.length; i++) {
            str_spanText = str_spanText + _returnData[ary_keys[i]];
            if (ary_keys.length > 1 && i != ary_keys.length - 1) {
                str_spanText = str_spanText + "/";  //如果不是最后一个,则加/
            }
        }
        return str_spanText;
    };


    //把一个字符串转换成Map,以后到处需要
    FreeUtil.parseDefineToMap = function (_str) {
        var map = {};
        if (typeof _str == "undefined" || _str == null || _str == "") {
            return map;
        }
        var str_remain = _str; //
        while (true) {
            var li_pos_1 = str_remain.indexOf("【");
            var li_pos_2 = str_remain.indexOf("】");
            if (li_pos_1 < 0 || li_pos_2 < 0) {
                break;
            }
            var str_item = str_remain.substring(li_pos_1 + 1, li_pos_2);
            var li_pos3 = str_item.indexOf("=");
            if (li_pos3 > 0) {
                var str_key = str_item.substring(0, li_pos3);
                var str_value = str_item.substring(li_pos3 + 1, str_item.length);
                map[str_key] = str_value;
            }
            str_remain = str_remain.substring(li_pos_2 + 1, str_remain.length);
        }

        return map;
    };

    //解析字符串为Map,key1=value1;kes2=value2;key3=value3;返回Map
    FreeUtil.parseStrToMap = function (_str, _split1, _split2) {
        var map = {};
        var li_len = _split2.length;
        var str_items = _str.split(_split1);
        for (var i = 0; i < str_items.length; i++) {
            var li_pos = str_items[i].indexOf(_split2);
            var str_key = str_items[i].substring(0, li_pos);
            var str_value = str_items[i].substring(li_pos + li_len, str_items[i].length);
            map[str_key] = str_value;
        }
        return map;
    };

    //解析,把一个字符串中以_beginStr开始,_endStr结束的中间的字符串取出来!!
    FreeUtil.parseStrMacroKeys = function (_str, _beginStr, _endStr) {
        var ary_rt = [];  //返回的数组
        var li_len1 = _beginStr.length;  //开始字符串的长度
        var li_len2 = _endStr.length;  //结束字符串的位置

        var str_remain = _str;  //剩余的
        var li_pos1 = str_remain.indexOf(_beginStr);  //开始位置

        //如果有
        while (li_pos1 > 0) {
            var li_pos2 = str_remain.indexOf(_endStr);  //结束位置
            var str_item = str_remain.substring(li_pos1 + li_len1, li_pos2);  //
            ary_rt.push(str_item);  //加入
            str_remain = str_remain.substring(li_pos2 + li_len2, str_remain.length);  //剩余
            li_pos1 = str_remain.indexOf(_beginStr); //再计算剩下的字符串的开始
        }

        return ary_rt;  //返回
    };

    //把一个SQL中的${key1}替换成实际值,jso_CardData是一个json数据对象
    FreeUtil.replaceStrByMacroKeys = function (_sql, jso_CardData) {
        var str_whereSQL = _sql;
        if (typeof jso_CardData == "undefined" || jso_CardData == null) {
            return str_whereSQL;
        }

        if (typeof str_whereSQL != "undefined" && str_whereSQL != null) {
            var ary_items = FreeUtil.parseStrMacroKeys(str_whereSQL, "${", "}");
            if (ary_items != null && ary_items.length > 0) {
                for (var i = 0; i < ary_items.length; i++) {
                    var isHaveCol = jso_CardData.hasOwnProperty(ary_items[i].toLocaleLowerCase());  //是否有该字段
                    var str_cardItemValue = jso_CardData[ary_items[i].toLocaleLowerCase()];  //卡片中的值
                    if (isHaveCol) {  //如果有该字段才进行实际替换
                        str_whereSQL = str_whereSQL.replace("${" + ary_items[i] + "}", str_cardItemValue);
                    }
                }
            }
        }
        return str_whereSQL;  //
    };

    //打开窗口
    FreeUtil.openDialog = function (_title, _js, _width, _height, _pars, _callBack, _isHaveCloseBtn) {
        FreeUtil.openDialogAndCloseMe(_title, _js, _width, _height, _pars, _callBack, false, _isHaveCloseBtn);
    };

    //打开窗口同时关闭自己
    FreeUtil.openDialogAndCloseMe = function (_title, _js, _width, _height, _pars, _callBack, _isCloseMe, _isHaveCloseBtn) {
        var li_now = Date.now();
        li_now = li_now - 1557442530000;  //减去一个常量,搞得小一点

        //计算left与top
        var li_left = 50;
        var li_top = 50;
        var li_win_Width = 600;
        var li_win_Height = 400;

        //如果是本窗口就是根窗口
        if (self.v_isopen == "N") {
            li_win_Width = self.innerWidth;  //窗口高度
            li_win_Height = self.innerHeight;  //窗口高度
        } else {
            li_win_Width = self.parent.innerWidth;  //窗口高度
            li_win_Height = self.parent.innerHeight;  //窗口高度
        }
        li_left = (li_win_Width - _width) / 2;  //窗口居中时的X位置
        li_top = (li_win_Height - _height) / 2;  //窗口居中的Y位置

        //如果有人传入的窗口高度,大于可容纳的高度,则强制控制在窗口范围中!
        //解决分辨造成的窗口无法看完或关闭问题!
        if (_height > (li_win_Height - 5)) {
            li_top = 0;
            _height = li_win_Height - 5;
        }

        //处理请求参数,使用encodeURI包两层!!
        //后来发现表单中传过来的值太长!需要拆成两个变量!一个是传到后台的,一个是从前台传入!
        var jso_parUI = {};  //从前端传入的参数
        if ((typeof _pars != "undefined") && _pars != null && (typeof _pars["_BillCardData"] != "undefined")) {
            jso_parUI = _pars["_BillCardData"];  //赋给前端变量
            _pars["_BillCardData"] = "jso_OpenPars2";
        }
        var str_pars = encodeURIComponent(encodeURIComponent(JSON.stringify(_pars)));

        //创建窗口
        var dom_div_root = document.createElement("div");  //
        dom_div_root.setAttribute("id", "dialog_" + li_now);  //当前时间毫秒
        //dom_div_root.setAttribute("class","easyui-draggable");
        //dom_div_root.setAttribute("data-options","handle:'#dialogtitle_" + li_now + "'");  //
        dom_div_root.setAttribute("style", "position:absolute;left:" + li_left + "px;top:" + li_top + "px;width:" + _width + "px;height:" + _height + "px;z-index:" + li_now + ";background:#FFFFFF;");

        //创建div上面的标题
        var dom_div_title = document.createElement("div");  //
        dom_div_title.setAttribute("id", "dialogtitle_" + li_now);
        dom_div_title.setAttribute("draggable", "true");
        dom_div_title.setAttribute("ondragstart", "FreeUtil.ondragstartDialog(this,event);");
        dom_div_title.setAttribute("ondrag", "FreeUtil.ondragDialog(this,event);");
        dom_div_title.setAttribute("ondragend", "FreeUtil.ondragendDialog(this,event);");

        dom_div_title.setAttribute("style", "width:100%;height:25px;background:#3C8DBC");

        //标题说明
        var dom_titletext = document.createElement("span");  //
        dom_titletext.textContent = _title;  //
        dom_titletext.setAttribute("style", "float:left;color:white;font-size:12px;margin-top:5px;margin-left:5px");
        dom_div_title.appendChild(dom_titletext);

        //关闭按钮,有的时候不要这个关闭按钮!
        if (typeof _isHaveCloseBtn == "undefined" || _isHaveCloseBtn) {
            var dom_close = document.createElement("span");  //
            dom_close.textContent = "×";
            dom_close.setAttribute("style", "float:right;display:block;width:25px;height:25px;color:white;font-size:20px;text-align:center;cursor:pointer;");
            dom_close.setAttribute("onclick", "FreeUtil.dirCloseSelf2('" + li_now + "');");  //关闭自己窗口,并回调
            dom_div_title.appendChild(dom_close);
        }

        dom_div_root.appendChild(dom_div_title);  //加入标题

        //创建内容
        var li_height_center = _height - 25 - 2;  //标题上下占一个像素
        var dom_div_center = document.createElement("div");  //
        dom_div_center.setAttribute("style", "width:100%;height:" + li_height_center + "px;background:#FFFFFF");

        //创建iframe对象，并加入到center的DIV中去
        var str_url = v_context + FreeUtil.ControlURL + "?js=";  //路径
        str_url = str_url + _js + "&isopen=Y&dialogid=" + li_now + "&OpenPars=" + str_pars;
        if ((typeof self.v_dialogid) != "undefined") {  //如果有dialogid,则说明这本身就是一个弹出窗口
            str_url = str_url + "&fromdialogid=" + self.v_dialogid;  //
        }

        //创建iframe,极其关键!
        var dom_iframe = document.createElement("iframe");  //
        dom_iframe.id = "iframe_" + li_now;
        dom_iframe.src = str_url; //
        dom_iframe.width = "100%";
        dom_iframe.height = "100%";
        dom_div_center.appendChild(dom_iframe);
        dom_div_root.appendChild(dom_div_center);  //加入内容

        //创建遮罩,实现模态窗口效果!mask
        var dom_div_mask = document.createElement("div");  //
        dom_div_mask.setAttribute("id", "dialogmask_" + li_now);  //当前时间毫秒
        dom_div_mask.setAttribute("style", "position:fixed;left:0px;top:0px;width:100%;height:100%;z-index:" + (li_now - 1) + ";background:#646464;opacity:0.2");

        //实际加入页面
        var dom_iframe2 = null;
        if (self.v_isopen == "N") {  //如果本窗口不是弹出的，即是根窗口,则直接在本窗口添加!
            document.body.appendChild(dom_div_mask);
            document.body.appendChild(dom_div_root);
            dom_iframe2 = document.getElementById("iframe_" + li_now);  //必须再次获取,
            self.AllOpenDialogIds = ["" + li_now];  //搞一个数组记录所有的弹出窗口
        } else {  //如果本窗口就是弹出的,则在父亲窗口中添加
            self.parent.document.body.appendChild(dom_div_mask);
            self.parent.document.body.appendChild(dom_div_root);
            dom_iframe2 = self.parent.document.getElementById("iframe_" + li_now);  //必须再次获取
            self.parent.AllOpenDialogIds.push("" + li_now);  //往数组中记录新的窗口id
        }

        //从前端传入参数,因为之前表单中的值太大太长传到后台url过长而报错!
        dom_iframe2.contentWindow["jso_OpenPars2"] = jso_parUI;

        //把回调函数赋给本窗口的一个变量中!
        if (_isCloseMe) {  //如果打开新窗口时立即关闭自己,则不设置回调函数,但立即关闭本窗口!
            if (typeof self.v_dialogid != "undefined") { //防止有人在根窗口不小心误调了这个函数!!
                FreeUtil.dirCloseSelf(self.v_dialogid);  //直接关闭自己
            }
        } else {  //如果不关闭自己,则设置回调函数
            self.dialogcallback = _callBack;  //设置回调函数!
        }
    };

    //明细数据打开修改，新增按钮时，要全屏铺开
    FreeUtil.openDialogAndCloseMe1 = function (_title, _js, _width, _height, _pars, _callBack, _isCloseMe, _isHaveCloseBtn) {
        var li_now = Date.now();
        li_now = li_now - 1557442530000;  //减去一个常量,搞得小一点

        //计算left与top
        var li_left = 50;
        var li_top = 50;
        var li_win_Width = 600;
        var li_win_Height = 400;

        li_win_Width = self.parent.innerWidth;  //窗口高度
        li_win_Height = self.parent.innerHeight;  //窗口高度
        _width = li_win_Width - 60;
        _height = li_win_Width - 4;

        li_left = 30;  //窗口居中时的X位置
        li_top = 2;  //窗口居中的Y位置

        //如果有人传入的窗口高度,大于可容纳的高度,则强制控制在窗口范围中!
        //解决分辨造成的窗口无法看完或关闭问题!
        if (_height > (li_win_Height - 5)) {
            li_top = 0;
            _height = li_win_Height - 5;
        }

        //处理请求参数,使用encodeURI包两层!!
        //后来发现表单中传过来的值太长!需要拆成两个变量!一个是传到后台的,一个是从前台传入!
        var jso_parUI = {};  //从前端传入的参数
        if ((typeof _pars != "undefined") && _pars != null && (typeof _pars["_BillCardData"] != "undefined")) {
            jso_parUI = _pars["_BillCardData"];  //赋给前端变量
            _pars["_BillCardData"] = "jso_OpenPars2";
        }

        var str_pars = encodeURI(encodeURI(JSON.stringify(_pars)));

        //创建窗口
        var dom_div_root = document.createElement("div");  //
        dom_div_root.setAttribute("id", "dialog_" + li_now);  //当前时间毫秒
        dom_div_root.setAttribute("style", "position:absolute;left:" + li_left + "px;top:" + li_top + "px;width:" + _width + "px;height:" + _height + "px;z-index:" + li_now + ";background:#FFFFFF;");

        //创建div上面的标题
        var dom_div_title = document.createElement("div");  //
        dom_div_title.setAttribute("id", "dialogtitle_" + li_now);
        dom_div_title.setAttribute("draggable", "true");
        dom_div_title.setAttribute("ondragstart", "FreeUtil.ondragstartDialog(this,event);");
        dom_div_title.setAttribute("ondrag", "FreeUtil.ondragDialog(this,event);");
        dom_div_title.setAttribute("ondragend", "FreeUtil.ondragendDialog(this,event);");

        dom_div_title.setAttribute("style", "width:100%;height:25px;background:#3C8DBC");

        //标题说明
        var dom_titletext = document.createElement("span");  //
        dom_titletext.textContent = _title;  //
        dom_titletext.setAttribute("style", "float:left;color:white;font-size:12px;margin-top:5px;margin-left:5px");
        dom_div_title.appendChild(dom_titletext);

        //关闭按钮,有的时候不要这个关闭按钮!
        if (typeof _isHaveCloseBtn == "undefined" || _isHaveCloseBtn) {
            var dom_close = document.createElement("span");  //
            dom_close.textContent = "×";
            dom_close.setAttribute("style", "float:right;display:block;width:25px;height:25px;color:white;font-size:20px;text-align:center;cursor:pointer;");
            dom_close.setAttribute("onclick", "FreeUtil.dirCloseSelf2('" + li_now + "');");  //关闭自己窗口,并回调
            dom_div_title.appendChild(dom_close);
        }

        dom_div_root.appendChild(dom_div_title);  //加入标题

        //创建内容
        var li_height_center = _height - 25 - 2;  //标题上下占一个像素
        var dom_div_center = document.createElement("div");  //
        dom_div_center.setAttribute("style", "width:100%;height:" + li_height_center + "px;background:#FFFFFF");

        //创建iframe对象，并加入到center的DIV中去
        var str_url = v_context + FreeUtil.ControlURL + "?js=";  //路径
        str_url = str_url + _js + "&isopen=Y&dialogid=" + li_now + "&OpenPars=" + str_pars;
        if ((typeof self.v_dialogid) != "undefined") {  //如果有dialogid,则说明这本身就是一个弹出窗口
            str_url = str_url + "&fromdialogid=" + self.v_dialogid;  //
        }

        //创建iframe,极其关键!
        var dom_iframe = document.createElement("iframe");  //
        dom_iframe.id = "iframe_" + li_now;
        dom_iframe.src = str_url; //
        dom_iframe.width = "100%";
        dom_iframe.height = "100%";
        dom_div_center.appendChild(dom_iframe);
        dom_div_root.appendChild(dom_div_center);  //加入内容

        //创建遮罩,实现模态窗口效果!mask
        var dom_div_mask = document.createElement("div");  //
        dom_div_mask.setAttribute("id", "dialogmask_" + li_now);  //当前时间毫秒
        dom_div_mask.setAttribute("style", "position:fixed;left:0px;top:0px;width:100%;height:100%;z-index:" + (li_now - 1) + ";background:#646464;opacity:0.2");

        //实际加入页面
        var dom_iframe2 = null;
        self.parent.document.body.appendChild(dom_div_mask);
        self.parent.document.body.appendChild(dom_div_root);
        dom_iframe2 = self.parent.document.getElementById("iframe_" + li_now);  //必须再次获取,
        self.parent.AllOpenDialogIds = ["" + li_now];  //搞一个数组记录所有的弹出窗口

        //从前端传入参数,因为之前表单中的值太大太长传到后台url过长而报错!
        dom_iframe2.contentWindow["jso_OpenPars2"] = jso_parUI;

        //把回调函数赋给本窗口的一个变量中!
        self.parent.dialogcallback = _callBack;  //设置回调函数!
    };

    //删除
    FreeUtil.removeOpenDialogId = function (_window, _dialogId) {
        var li_pos = _window.AllOpenDialogIds.indexOf(_dialogId);
        _window.AllOpenDialogIds.splice(li_pos, 1);  //删除这条记录
        //console.log(_window.AllOpenDialogIds);
    };

    //删除所有窗口
    FreeUtil.closeDialogAll = function (_returnData) {
        var rootWindow = self.parent;
        var array_ids = rootWindow.AllOpenDialogIds;  //所有id数组
        if (typeof array_ids == "undefined") {
            return;
        }

        //先调回调函数
        var fn_callback = rootWindow.dialogcallback;
        if ((typeof fn_callback) != "undefined") {  //有可能没有回调函数
            rootWindow.dialogcallback(_returnData); //
        } else {
            //console.log("没有定义回调函数..");
        }


        //可以考虑先删前面的后删后面的,
        for (var i = 0; i < array_ids.length; i++) {
            var str_did = array_ids[i];  //取得id

            //删除两个div
            var div_dialogmask = rootWindow.document.getElementById('dialogmask_' + str_did);
            div_dialogmask.parentNode.removeChild(div_dialogmask);  //删除

            //因为总是从父亲窗口弹出的,所以直接拿父亲窗口来删除
            var div_dialog = rootWindow.document.getElementById("dialog_" + str_did);
            div_dialog.parentNode.removeChild(div_dialog);  //删除
        }

        rootWindow.AllOpenDialogIds = [];  //直接清空数据
    };

    //直接关闭自己,并直接回调根窗口的回调事件
    FreeUtil.closeDialogToRoot = function (_returnData) {
        var str_did = self.v_dialogid;  //从本页面取得dialogid
        var fn_callback = self.parent.dialogcallback;
        if ((typeof fn_callback) != "undefined") {  //有可能没有回调函数
            self.parent.dialogcallback(_returnData); //
        } else {
            //console.log("没有定义回调函数..");
        }

        //删除缓存中的dialogId
        FreeUtil.removeOpenDialogId(self.parent, str_did);  //删除id

        //删除遮罩,必须先删除这个,如果先删除窗口,则这个代码就跑不到了
        var div_dialogmask = self.parent.document.getElementById('dialogmask_' + str_did);
        div_dialogmask.parentNode.removeChild(div_dialogmask);  //删除

        //因为总是从父亲窗口弹出的,所以直接拿父亲窗口来删除
        var div_dialog = self.parent.document.getElementById("dialog_" + str_did);
        div_dialog.parentNode.removeChild(div_dialog);  //删除
    };

    //显示某窗口
    FreeUtil.showDialogById = function (_dialogId) {
        var str_thisDialogId = self.v_dialogid;  //从本页面取得dialogid

        var dealDocument = null;
        if (typeof str_thisDialogId == "undefined") {  //如果没有,则说明本窗口是根窗口
            dealDocument = self.document;
        } else {  //本人是弹出窗口
            dealDocument = self.parent.document;
        }

        var div_dialogmask = dealDocument.getElementById('dialogmask_' + _dialogId);
        var div_dialog = dealDocument.getElementById("dialog_" + _dialogId);

        div_dialogmask.style.display = "block";
        div_dialog.style.display = "block";
    };

    //隐藏本窗口
    FreeUtil.hiddenDialog = function (_returnData) {
        var str_did = self.v_dialogid;  //从本页面取得dialogid
        var str_fromdid = self.v_fromdialogid;  //是从哪个父亲窗口过来的!

        //取得对应的iframe,即关闭的这个窗口是父亲窗口是的
        if ((typeof str_fromdid) == "undefined") {  //如果没有来源弹出窗口,则说明是从根据窗口弹过来的
            var fn_callback = self.parent.dialogcallback;
            if ((typeof fn_callback) != "undefined") {  //有可能没有回调函数
                self.parent.dialogcallback(_returnData); //
            } else {
                //console.log("没有定义回调函数..");
            }
        } else {
            var dom_iframe = self.parent.document.getElementById("iframe_" + str_fromdid);  //取得来源窗口
            if (dom_iframe != null && dom_iframe.contentWindow != null) {  //防止有人误操作,打开时立即关闭了窗口
                var fn_callback = dom_iframe.contentWindow.dialogcallback;
                if ((typeof fn_callback) != "undefined") {
                    fn_callback(_returnData); //先执行回调函数!!
                } else {
                    //console.log("没有定义回调函数..");
                }
            } else {  //如果没有来源窗口,则直接调根窗口的,这样使用CloseDialog也能起效果!
                var fn_callback = self.parent.dialogcallback;
                if ((typeof fn_callback) != "undefined") {  //有可能没有回调函数
                    self.parent.dialogcallback(_returnData); //
                } else {
                    //console.log("没有定义回调函数..");
                }
            }
        }

        //删除遮罩,必须先删除这个,如果先删除窗口,则这个代码就跑不到了
        var div_dialogmask = self.parent.document.getElementById('dialogmask_' + str_did);
        div_dialogmask.style.display = "none";

        //因为总是从父亲窗口弹出的,所以直接拿父亲窗口来删除
        var div_dialog = self.parent.document.getElementById("dialog_" + str_did);
        div_dialog.style.display = "none";
    };

    //关闭弹出窗口,并有返回值..
    FreeUtil.closeDialog = function (_returnData) {
        var str_did = self.v_dialogid;  //从本页面取得dialogid
        var str_fromdid = self.v_fromdialogid;  //是从哪个父亲窗口过来的!
        //console.log("准备关闭本窗口,本窗口的id是["  + str_did + "],是从【"+str_fromdid+"】窗口弹出的!!!");

        //取得对应的iframe,即关闭的这个窗口是父亲窗口是的
        if ((typeof str_fromdid) == "undefined") {  //如果没有来源弹出窗口,则说明是从根窗口弹过来的
            var fn_callback = self.parent.dialogcallback;
            if ((typeof fn_callback) != "undefined") {  //有可能没有回调函数
                self.parent.dialogcallback(_returnData); //
            } else {
                //console.log("没有定义回调函数..");
            }
        } else {
            var dom_iframe = self.parent.document.getElementById("iframe_" + str_fromdid);  //取得来源窗口
            if (dom_iframe != null && dom_iframe.contentWindow != null) {  //防止有人误操作,打开时立即关闭了窗口
                var fn_callback = dom_iframe.contentWindow.dialogcallback;
                if ((typeof fn_callback) != "undefined") {
                    fn_callback(_returnData); //先执行回调函数!!
                } else {
                    //console.log("没有定义回调函数..");
                }
            } else {  //如果没有来源窗口,则直接调根窗口的,这样使用CloseDialog也能起效果!
                var fn_callback = self.parent.dialogcallback;
                if ((typeof fn_callback) != "undefined") {  //有可能没有回调函数
                    self.parent.dialogcallback(_returnData); //
                } else {
                    //console.log("没有定义回调函数..");
                }
            }
        }

        //一定要先删除缓存中的id,否则窗口已关闭了,就取不到self.parent了
        FreeUtil.removeOpenDialogId(self.parent, str_did);  //删除id

        //删除遮罩,必须先删除这个,如果先删除窗口,则这个代码就跑不到了
        var div_dialogmask = self.parent.document.getElementById('dialogmask_' + str_did);
        div_dialogmask.parentNode.removeChild(div_dialogmask);  //删除

        //因为总是从父亲窗口弹出的,所以直接拿父亲窗口来删除
        var div_dialog = self.parent.document.getElementById("dialog_" + str_did);
        div_dialog.parentNode.removeChild(div_dialog);  //删除
    };

    //直接关闭自己
    FreeUtil.dirCloseSelf = function (_id) {
        var dealDocument = null;
        if (self.v_isopen == "N") {
            dealDocument = self.document;
            FreeUtil.removeOpenDialogId(self, _id);  //删除id
        } else {
            dealDocument = self.parent.document;
            FreeUtil.removeOpenDialogId(self.parent, _id);  //删除id
        }

        //删除弹出窗口
        var div_dialog = dealDocument.getElementById('dialog_' + _id);
        div_dialog.parentNode.removeChild(div_dialog);  //删除
        //删除遮罩
        var div_dialogmask = dealDocument.getElementById('dialogmask_' + _id);
        div_dialogmask.parentNode.removeChild(div_dialogmask);  //删除
    };

    //直接关闭自己,同时回调函数!
    FreeUtil.dirCloseSelf2 = function (_id) {
        //console.log("self.v_isopen=" + self.v_isopen);    //永远是N,因为右上角这个按钮永远在根窗口

        //调用回调函数!
        var dom_iframe = self.document.getElementById('iframe_' + _id);  //先根据id取得iframe
        var str_fromDialogId = dom_iframe.contentWindow.v_fromdialogid;  //取得该窗口是从哪个窗口来的,因为回调函数在来源窗口中
        //console.log("From DialogId[" + str_fromDialogId + "]");  //

        //如果没有定义来源id,则说明是从根窗口打开的
        try {
            if (typeof str_fromDialogId == "undefined") {
                var fn_callback = self.dialogcallback;
                if (typeof fn_callback == "function") {  //有可能没有回调函数
                    fn_callback({type: "dirclose"}); //返回数据是固定的,即dirclose=type
                }
            } else {  //如果有来源窗口,则调来源窗口中的回调函数
                var dom_fromIframe = self.document.getElementById('iframe_' + str_fromDialogId);  //iframe
                if (typeof dom_fromIframe == "undefined" || dom_fromIframe == null) {  //如果前面的窗口已关闭,即是openAndcloseMe(),则直接调父窗口试试
                    var fn_callback = self.dialogcallback;
                    if (typeof fn_callback == "function") {  //有可能没有回调函数
                        fn_callback({type: "dirclose"}); //返回数据是固定的,即dirclose=type
                    }
                } else {
                    var window_fromIframe = dom_fromIframe.contentWindow;  //
                    var fn_callback = window_fromIframe.dialogcallback;  //看这个窗口中是否定义了回调函数,即打开时是否设置了!
                    if (typeof fn_callback == "function") {  //有可能没有回调函数
                        fn_callback({type: "dirclose"}); //返回数据是固定的,即dirclose=type
                    }
                }
            }
        } catch (_ex) {
            console.log(_ex);  //曾经有人在回调函数中发生异常，导致窗口关闭不掉,所以要把异常吃掉.
        }

        //删除窗口!
        FreeUtil.removeOpenDialogId(self, _id);  //删除id

        //删除弹出窗口
        var div_dialog = self.document.getElementById('dialog_' + _id);  //div窗口
        div_dialog.parentNode.removeChild(div_dialog);  //删除

        //删除遮罩
        var div_dialogmask = self.document.getElementById('dialogmask_' + _id);
        div_dialogmask.parentNode.removeChild(div_dialogmask);  //删除
    };

    //表格中如果有错误提示,则直接把提示设成鼠标移上去也提示
    FreeUtil.getWarnMsg = function (_value, _warnMsg) {
        if (_warnMsg == "undefined" || _warnMsg == null || _warnMsg == "") {
            return _value;  //
        } else {
            var str_warnMsg = _warnMsg.replace(/<br>/g, "\r\n").replace(/'/g, "‘");  //替换所的<br>为换行符
            return "<span style='display:block;width:100%;height:100%' title='" + str_warnMsg + "'>" + _value + "</span>";
        }
    };

    //右键点击卡片
    FreeUtil.onRightClickBillCard = function (_billCard, _divid, _itemkey, _event, _target) {
        _event.preventDefault();  //防止事件穿透!

        var str_menuid = _divid + "_BillCardRightMenu"; //
        var dom_itemmenu = document.getElementById(str_menuid);  //取得菜单
        dom_itemmenu.dataset.clickDivId = _divid;  //设置绑定点击的列名!
        dom_itemmenu.dataset.clickColName = _itemkey;  //设置绑定点击的列名!
        $('#' + str_menuid).menu('show', {left: _event.pageX, top: _event.pageY});  //显示弹出菜单
    };

    //点击弹出菜单--清空当前字段数据
    FreeUtil.onRightClickBillCard_0 = function (_billCard, _target) {
        var str_divid = _target.parentNode.dataset.clickDivId;
        var str_colName = _target.parentNode.dataset.clickColName;  //点击的字段名
        FreeUtil.setBillCardItemClearValue(_billCard, str_colName);  //清空该字段值
    };


    //点击弹出菜单--查看SQL
    FreeUtil.onRightClickBillCard_1 = function (_billCard, _target) {
        var str_sql = _billCard.CurrSQL;  //
        if (typeof str_sql == "undefined" || str_sql == null) {
            str_sql = "当前SQL为空,可能没有查询数据,比如是直接设置数据的,或是新增状态!";
        }

        FreeUtil.openHtmlMsgBox2("查看当前SQL", 500, 250, str_sql, true);
    };

    //点击弹出菜单--查看UI数据
    FreeUtil.onRightClickBillCard_2 = function (_billCard, _target) {
        var jso_data = FreeUtil.getBillCardFormValue(_billCard);  //
        var str_fromtable = _billCard.templetVO.templet_option.fromtable;  //表名
        var itemVOs = _billCard.templetVO.templet_option_b;  //模板子表列表
        FreeUtil.showUIDataDialog("查看UI数据-[" + str_fromtable + "]", null, jso_data, itemVOs);  //把一个数据与模板VO弹出一个表格显示
    };

    //点击弹出菜单--查看DB数据
    FreeUtil.onRightClickBillCard_3 = function (_billCard, _target) {
        var str_divid = _target.parentNode.dataset.clickDivId;
        var str_colName = _target.parentNode.dataset.clickColName;

        var jso_CardData = FreeUtil.getBillCardFormValue(_billCard);  //
        var str_fromtable = _billCard.templetVO.templet_option.fromtable;  //查询表名
        var itemVOs = _billCard.templetVO.templet_option_b;  //模板子表列表
        var str_sqlWhere = FreeUtil.getSQLWhereByPK(_billCard.templetVO, jso_CardData);  //拼出SQL的where
        var str_sql = "select * from " + str_fromtable + " where " + str_sqlWhere;

        //拼列名
        var itemVOs = _billCard.templetVO.templet_option_b;
        var jso_itemKeyAndName = {};  //把模板itemkey与itemname也传到后台,生成表格时显示列名
        for (var i = 0; i < itemVOs.length; i++) {
            var str_itemkey = itemVOs[i].itemkey;
            var str_itemname = itemVOs[i].itemname;
            jso_itemKeyAndName[str_itemkey] = str_itemname;  //
        }

        var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.CommDMOClassName, "getDBDataByHtmlTable", {
            "SQL": str_sql,
            "ItemKeyNames": jso_itemKeyAndName
        });
        var str_html = jso_rt.html;  //

        FreeUtil.openHtmlMsgBox2("查看DB数据", 800, 600, str_html);  //
    };

    //点击弹出菜单--查看模板配置
    FreeUtil.onRightClickBillCard_4 = function (_billCard, _target) {
        var jso_templetVO = _billCard.templetVO;  //模板对象
        var str_templetcode = jso_templetVO.templet_option.templetcode;
        var jso_par = {"templetcode": str_templetcode};
        var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.JSPBuilderClassName, "getTempletXmlText", jso_par);
        var str_text = jso_rt.text;  //
        FreeUtil.openHtmlMsgBox2("模板配置[" + str_templetcode + "]", 900, 600, str_text, true);
    };

    //点击弹出菜单--查看模板子项
    FreeUtil.onRightClickBillCard_5 = function (_billCard, _target) {
        var str_divid = _target.parentNode.dataset.clickDivId;
        var str_colName = _target.parentNode.dataset.clickColName;

        var jso_templetVO = _billCard.templetVO;  //模板对象
        var str_templetcode = jso_templetVO.templet_option.templetcode;
        var jso_par = {"templetcode": str_templetcode, "itemkey": str_colName};
        var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.JSPBuilderClassName, "getTempletItemXmlText", jso_par);
        var str_text = jso_rt.text;  //
        FreeUtil.openHtmlMsgBox2("模板配置[" + str_templetcode + "]", 900, 600, str_text, true);
    };


    //右键表头弹出
    FreeUtil.onRightClickBillListHead = function (_billList, _event, _field) {
        var str_divid = _billList.divid;  //唯一id
        var str_menuid = str_divid + "_BillListHeadRightMenu";

        _event.preventDefault();  //防止事件穿透!

        var dom_itemmenu = document.getElementById(str_menuid);  //
        dom_itemmenu.dataset.clickColName = _field;  //设置绑定点击的列名!
        $('#' + str_menuid).menu('show', {left: _event.pageX, top: _event.pageY});
    };

    //右键-查看SQL
    FreeUtil.onRightClickBillListHead_1 = function (_billList, _target) {
        //var str_colName = _target.parentNode.dataset.clickColName;
        var str_sql = _billList.CurrSQL;  //
        if (typeof str_sql == "undefined") {
            str_sql = "当前SQL为空,可能还没有查询数据!";
        }

        var str_sql2 = _billList.CurrSQL2;  //
        var str_sql3 = _billList.CurrSQL3;  //
        if (typeof str_sql2 == "undefined") {
            str_sql2 = "当前实际SQL为空,可能还没有查询数据!";
        }

        var str_text = "请求执行的SQL:\r\n" + str_sql + "\r\n\r\n\r\n";
        str_text = str_text + "实际执行(分页后)的SQL:\r\n" + str_sql2 + "\r\n\r\n";
        str_text = str_text + "分页前的SQL[即CurrSQL3]:\r\n" + str_sql3 + "\r\n";

        FreeUtil.openHtmlMsgBox2("当前表格查询数据的SQL", 900, 300, str_text, true);
    };

    //右键查看模板
    FreeUtil.onRightClickBillListHead_2 = function (_billList, _target) {
        //var str_colName = _target.parentNode.dataset.clickColName;
        var jso_templetVO = _billList.templetVO;  //模板对象
        var str_templetcode = jso_templetVO.templet_option.templetcode;
        var jso_par = {"templetcode": str_templetcode};
        var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.JSPBuilderClassName, "getTempletXmlText", jso_par);
        var str_text = jso_rt.text;  //
        FreeUtil.openHtmlMsgBox2("模板配置[" + str_templetcode + "]", 900, 600, str_text, true);
    };

    //右键查看模板
    FreeUtil.onRightClickBillListHead_3 = function (_billList, _target) {
        var str_colName = _target.parentNode.dataset.clickColName;
        var jso_templetVO = _billList.templetVO;  //模板对象
        var str_templetcode = jso_templetVO.templet_option.templetcode;
        var jso_par = {"templetcode": str_templetcode, "itemkey": str_colName};
        var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.JSPBuilderClassName, "getTempletItemXmlText", jso_par);
        var str_text = jso_rt.text;  //
        FreeUtil.openHtmlMsgBox2("模板配置[" + str_templetcode + "]", 900, 600, str_text, true);
    };

    //右键--穿透查询
    FreeUtil.onRightClickBillListHead_4 = function (_billList, _target) {
        var jso_templetVO = _billList.templetVO;  //模板对象
        var str_templetcode = jso_templetVO.templet_option.templetcode;
        FreeUtil.queryDataByConditonReal(_billList, null, false, 1, null, false);
    };

    //右键--设置isPOC
    FreeUtil.onRightClickBillListHead_5 = function (_billList, _target) {
        var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.JSPBuilderClassName, "getIsPOC", null);
        var str_isPOC = jso_rt.isPOC;  //
        var isPOCBtnId = _billList[0].id + "_Btn假设校验失败";
        if ("Y" == str_isPOC) {
            $.messager.confirm("提示", "当前isPOC状态是Y,你是否要禁用之?", function (_isOK) {
                if (_isOK) {
                    FreeUtil.doClassMethodCall(FreeUtil.JSPBuilderClassName, "setIsPOC", {"isPOC": "N"});
                    FreeUtil.alert("禁用成功!");
                    $("#" + isPOCBtnId).parent().hide();
                }

            });
        } else {
            $.messager.confirm("提示", "当前isPOC状态是N,你是否要启用之?", function (_isOK) {
                if (_isOK) {
                    FreeUtil.doClassMethodCall(FreeUtil.JSPBuilderClassName, "setIsPOC", {"isPOC": "Y"});
                    FreeUtil.alert("启用成功!");

                    $("#" + isPOCBtnId).parent().show();
                }
            });
        }
    };


    //右键点击行
    FreeUtil.onRightClickBillListRow = function (_billList, _event, _rowIndex, _rowData) {
        var str_divid = _billList.divid;  //唯一id
        var str_menuid = str_divid + "_BillListRowRightMenu";

        _event.preventDefault();  //防止事件穿透!
        $('#' + str_menuid).menu('show', {left: _event.pageX, top: _event.pageY});
    };


    //右键行--查看UI数据
    FreeUtil.onRightClickBillListRow_1 = function (_billList, _target) {
        var json_rowdata = _billList.datagrid('getSelected');  //
        var jso_templetVO = _billList.templetVO;  //模板对象
        var str_fromtable = jso_templetVO.templet_option.fromtable;  //表名
        var itemVOs = jso_templetVO.templet_option_b;
        FreeUtil.showUIDataDialog("查看UI数据-[" + str_fromtable + "]", null, json_rowdata, itemVOs);
    };

    //右键行--查看DB数据
    FreeUtil.onRightClickBillListRow_2 = function (_billList, _target) {
        var json_rowdata = _billList.datagrid('getSelected');  //
        if (json_rowdata == null) {
            JSPFree.alert("取得选中行数据为空!");
            return;
        }

        var jso_templetVO = _billList.templetVO;  //模板对象
        var itemVOs = jso_templetVO.templet_option_b;
        var jso_itemKeyAndName = {};  //把模板itemkey与itemname也传到后台,生成表格时显示列名
        for (var i = 0; i < itemVOs.length; i++) {
            var str_itemkey = itemVOs[i].itemkey;
            var str_itemname = itemVOs[i].itemname;
            jso_itemKeyAndName[str_itemkey] = str_itemname;  //
        }

        var str_sqlWhere = FreeUtil.getSQLWhereByPK(jso_templetVO, json_rowdata);
        var str_table = jso_templetVO.templet_option.fromtable;
        var str_sql = "select * from " + str_table + " where " + str_sqlWhere;

        var jso_rt = FreeUtil.doClassMethodCall(FreeUtil.CommDMOClassName, "getDBDataByHtmlTable", {
            "SQL": str_sql,
            "ItemKeyNames": jso_itemKeyAndName
        });
        var str_html = jso_rt.html;  //

        FreeUtil.openHtmlMsgBox2("查看DB数据", 800, 600, str_html);  //
    };


    //右键行数据时
    FreeUtil.onRightClickBillListRow_3 = function (_billList, _target) {
        JSPFree.alert("UI与DB比较,系统开发中...");
    };


    //把一个数据与模板VO弹出一个表格显示
    FreeUtil.showUIDataDialog = function (_title, _beginText, jso_data, itemVOs) {
        if (jso_data == null) {
            JSPFree.alert("取得选中行数据为空!");
            return;
        }
        //数据中的列..
        var ary_dataKeys = Object.keys(jso_data);

        var ary_allItemKeys = [];
        var ary_realData = [];  //
        for (var i = 0; i < itemVOs.length; i++) {
            var str_itemkey = itemVOs[i].itemkey;  //
            ary_allItemKeys.push(str_itemkey);  //
            if (ary_dataKeys.indexOf(str_itemkey) >= 0) {  //如果数据对象中有这个列
                var str_itemname = itemVOs[i].itemname;  //显示名
                var str_itemvalue = jso_data[str_itemkey];  //实际数据
                ary_realData.push({"col": str_itemkey, "colname": str_itemname, "colvalue": str_itemvalue});
            }
        }

        //反向寻找不存的数据
        for (var i = 0; i < ary_dataKeys.length; i++) {
            if (ary_allItemKeys.indexOf(ary_dataKeys[i]) < 0) {  //如果不存在!
                var str_itemname = ary_dataKeys[i];  //显示名
                var str_itemvalue = jso_data[ary_dataKeys[i]];  //实际数据
                ary_realData.push({"col": ary_dataKeys[i], "colname": str_itemname, "colvalue": str_itemvalue});
            }
        }

        var str_table = "";
        if (_beginText != null && _beginText != "") {
            str_table = str_table + _beginText + "<br>\r\n";
        }
        str_table = str_table + "<table border=1 style=\"width:95%;\">\r\n";
        var str_style = "text-align:center;background:#80FFFF";
        str_table = str_table + "<tr><td style='width:15%;" + str_style + "'>字段</td><td style='width:30%;" + str_style + "'>字段名</td><td style='width:55%;" + str_style + "'>字段值</td></tr>\r\n";
        for (var i = 0; i < ary_realData.length; i++) {
            str_table = str_table + "<tr>";
            str_table = str_table + "<td style='width:15%;'>" + ary_realData[i].col + "</td>";
            str_table = str_table + "<td style='width:30%;'>" + ary_realData[i].colname + "</td>";
            str_table = str_table + "<td style='width:55%;'>" + ary_realData[i].colvalue + "</td>";
            str_table = str_table + "</tr>\r\n";
        }
        str_table = str_table + "</table>\r\n";
        FreeUtil.openHtmlMsgBox2(_title, 800, 600, str_table);
    };

    //设置客户端变量
    FreeUtil.setClientEnv = function (_key, _value) {
        var rootWindow = FreeUtil.getRootWindow(self);
        rootWindow[_key] = _value;  //
    };

    //设置客户端变量
    FreeUtil.getClientEnv = function (_key) {
        var rootWindow = FreeUtil.getRootWindow(self);
        return rootWindow[_key];  //
    };

    //取得根窗口
    FreeUtil.getRootWindow = function (_window) {
        var str_isopen = _window["v_isopen"];  //isopen这个变量可以判断是否是根窗口
        if ("N" == str_isopen) {
            return _window;
        } else {
            var parentWindow = _window.parent;  //父亲窗口
            return FreeUtil.getRootWindow(parentWindow);  //继续取
        }
    };


    //拖动事件
    FreeUtil.ondragstartDialog = function (_target, _event) {
        var str_id = _target.id;  //
        var li_pos = str_id.indexOf("_");
        var str_now = str_id.substring(li_pos + 1, str_id.length);

        var dom_dialog = document.getElementById('dialog_' + str_now);  //
        var rect = dom_dialog.getBoundingClientRect();  //取得分割条
        var li_juli_wid = _event.clientX - rect.left;
        var li_juli_hei = _event.clientY - rect.top;

        self.thisDragTarget = dom_dialog;  //
        self.thisDragTarget_wid = li_juli_wid;  //
        self.thisDragTarget_hei = li_juli_hei;  //
    };

    //拖动窗口
    FreeUtil.ondragDialog = function (_target, _event) {
        var li_x = _event.clientX;  //
        var li_y = _event.clientY;  //
        if (li_x <= 0 || li_y <= 0) {
            return;  //
        }

        var li_newX = li_x - self.thisDragTarget_wid;
        var li_newY = li_y - self.thisDragTarget_hei;
        if (li_newX <= 0 || li_newY <= 0) {
            return;  //
        }

        if ((typeof lastX) != "undefined" && (typeof lastY) != "undefined") {
            if (Math.abs(lastX - li_newX) <= 3 && Math.abs(lastY - li_newY) <= 3) {
                return;
            }
        }

        self.thisDragTarget.style.left = li_newX + "px";
        self.thisDragTarget.style.top = li_newY + "px";

        self.lastX = li_newX;
        self.lastY = li_newY;
    };

    //拖动结束
    FreeUtil.ondragendDialog = function (_target, _event) {
        var li_x = _event.clientX;  //
        var li_y = _event.clientY;  //
        if (li_x <= 0 || li_y <= 0) {
            return;  //
        }

        var li_newX = li_x - self.thisDragTarget_wid;
        var li_newY = li_y - self.thisDragTarget_hei;
        if (li_newX <= 0 || li_newY <= 0) {
            return;  //
        }

        self.thisDragTarget.style.left = li_newX + "px";
        self.thisDragTarget.style.top = li_newY + "px";
    };

    //日历选择返回的数据
    FreeUtil.formatDate = function (_date) {
        if (_date == null) {
            return "";
        }
        return FreeUtil.dateFormat("yyyy-MM-dd", _date);
    };

    //日历选择返回的数据，6位日期，年+月
    FreeUtil.formatDate6 = function (_date) {
        if (_date == null) {
            return "";
        }
        return FreeUtil.dateFormat("yyyyMMdd", _date).substring(0, 6);
    };

    //日历选择返回的数据
    FreeUtil.formatDate8 = function (_date) {
        if (_date == null) {
            return "";
        }
        return FreeUtil.dateFormat("yyyyMMdd", _date);
    };

    //解析日期
    FreeUtil.parserDate = function (_str) {
        if (typeof _str == "undefined" || _str == null || _str == "") {
            return new Date();
        }

        //如果是8位数,则解析日期会失败,必须转换成10位数
        if (_str.length == 8) {
            _str = _str.substring(0, 4) + "-" + _str.substring(4, 6) + "-" + _str.substring(6, 8);
            //如果是6位 解析也会失败，必须转换成10位
        } else if (_str.length == 6) {
            _str = _str.substring(0, 4) + "-" + _str.substring(4, 6) + "-01";
        }

        var t = Date.parse(_str);
        if (!isNaN(t)) {
            return new Date(t);
        } else {
            return null;
        }
    };

    //FreeUtil.dateFormat("yyyy-MM-dd hh:mm:ss",_date);
    FreeUtil.dateFormat = function (fmt, date) {
        var o = {
            "M+": date.getMonth() + 1,
            "d+": date.getDate(),
            "h+": date.getHours(),
            "m+": date.getMinutes(),
            "s+": date.getSeconds(),
            "q+": Math.floor((date.getMonth() + 3) / 3),
            "S": date.getMilliseconds()
        };
        if (/(y+)/.test(fmt)) {
            fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
        }
        for (var k in o) {
            if (new RegExp("(" + k + ")").test(fmt)) {
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            }
        }
        return fmt;
    };


    //日历选择返回的数据
    FreeUtil.formatTime = function (_date) {
        if (_date == null) {
            return "";
        }
        return FreeUtil.dateFormat("yyyy-MM-dd hh:mm:ss", _date);
    };


    //提示,包一下,以后可以更换其他开源框架!
    FreeUtil.alert = function (_str, _callBackFunc) {
        $.messager.alert('提示', _str, 'info', _callBackFunc);
    };

    //修改按钮，中文显示
    $.extend($.messager.defaults, {
        ok: "确定",
        cancel: "取消"
    });

    //修改分页，中文显示
    $.extend($.fn.pagination.defaults, {
        beforePageText: "第",
        afterPageText: "共{pages}页",
        displayMsg: "显示{from}到{to},共{total}记录"
    });

    //修改分页,分页数列表
    $.extend($.fn.datagrid.defaults, {
        pageList: [20, 50, 100, 200, 500]
    });

    // 根据父表查询所有字表，返回字表按钮 -- create by wm 2021-4-9 10:29:11
    FreeUtil.getChildTabBtn = function (tab_name_en, report_type) {
        var childTabBtn = '';
        $.ajax({
            cache: false,
            async: false,
            url: v_context + "/frs/safe/rptDataMgr/getChildTabBtn",
            type: "get",
            data: {
                tabNameEn: tab_name_en,
                reportType: report_type
            },
            success: function (result) {
                childTabBtn = result.butStr;
            }
        });
        return childTabBtn;
    };
    /**
     *
     * 获取公钥
     */
    FreeUtil.getPublicKey = function () {
        var publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCLFVCZEkcznSlb4ZSI6ra2uXk8HHMXG3e2WZ/6i8Q51rq7uJ5PwbuHVhK4KS3G/dhrzNdFzsuejA30eRNExzuabX0ErJeqVVocLbHb5YpAwR7QT/j/4atE/8Z5ScTxP9ky3I+xwjjC8uI7I31EUeWTfY8W1qo1JlY/8tZupvOaowIDAQAB";
        return publicKey;
    }
    
    /**
     *
     * 参数加密
     */
    FreeUtil.encrypt = function (_parJSO) {
        var encrypt = new JSEncrypt();
        encrypt.setPublicKey(FreeUtil.getPublicKey());
        var v_parstr = encrypt.encryptLong(encodeURI(JSON.stringify(_parJSO)));
        return v_parstr;
    }

})(jQuery);

