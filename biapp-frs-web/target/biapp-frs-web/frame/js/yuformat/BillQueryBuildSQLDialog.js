var jso_queryTempletVO = null;  //实际表的模板VO
var ary_newQueryItems = [];  //当前选中的

function AfterInit(){
  JSPFree.createSplitByBtn("d1","左右",450,["查询/onConfirm/icon-ok","重置/onReset/icon-p39","取消/onCancel/icon-no"]);
  JSPFree.createSplit("d1_B","上下",325);

  //取得模板编码
  var str_templetCode = jso_OpenPars.templetcode;
  jso_queryTempletVO = JSPFree.getTempletVO(str_templetCode);
  //console.log(jso_queryTempletVO);

  //创建列表--所有列
  JSPFree.createBillList("d1_B_A",FreeUtil.CommXMLPath + "/TempletItems.xml");

  //创建列表-排序列
  JSPFree.createBillList("d1_B_B",FreeUtil.CommXMLPath + "/TempletOrderBy.xml");
    
  //从前端模板解析出所有字段,并加入右边表格中
  var jsy_itemVOs = jso_queryTempletVO.templet_option_b;
  var array_AllItems = [];
  for(var i=0;i<jsy_itemVOs.length;i++){
    var str_isQuery = "";
    if("Y"==jsy_itemVOs[i].query_isshow || "Y"==jsy_itemVOs[i].query2_isshow || "Y"==jsy_itemVOs[i].query3_isshow){
      str_isQuery="Y";
    }else{
      str_isQuery="N";
    }

    if("Y"==jsy_itemVOs[i].list_isshow){  //有人提出必须是列表显示的才参与
      if("按钮"==jsy_itemVOs[i].itemtype || "N"==jsy_itemVOs[i].issave){  //按钮不参与查询,不参与保存的也不查询,因为可能是加载公式带来的
       }else{
        array_AllItems.push({itemkey:jsy_itemVOs[i].itemkey,itemname:jsy_itemVOs[i].itemname,isquery:str_isQuery});
       }
    }
  }

  //把数据塞入到表格中
  JSPFree.setBillListDatas(d1_B_A_BillList,array_AllItems);

  //把排序字段塞入到表格中
  var str_orderBys = jso_queryTempletVO.templet_option.orderbys;
  if(typeof str_orderBys!="undefined" && str_orderBys!=null && str_orderBys!=""){
     var ary_orders = str_orderBys.split(",");
     var ary_orderDatas = [];  //
     for(var i=0;i<ary_orders.length;i++){
       var templetItemVO = findTempletItemVOByKey(jso_queryTempletVO.templet_option_b,ary_orders[i]);
       if(templetItemVO!=null){
          ary_orderDatas.push({itemkey:templetItemVO.itemkey,itemname:templetItemVO.itemname,isdesc:"N"});
       }
     }
     JSPFree.setBillListDatas(d1_B_B_BillList,ary_orderDatas);
  }
  
  //
  //再取出所有字段,然后渲染左边的查询条件
  doRefreshQueryItems(true);
}

//从模板子表VO[]中找到某一项
function findTempletItemVOByKey(_itemVOs,_itemkey){
  for(var i=0;i<_itemVOs.length;i++){
    if(_itemVOs[i].itemkey==_itemkey){
      return _itemVOs[i];
    }
  }
  return null;
}

//当选择了一个新的列时,立即刷新左边的查询条件
function onChooseOneColumn(_billList,_index,_field,_newValue){
  //console.log(_index + "/" + _field + "/" + _newValue);
  doRefreshQueryItems(false);
}

//刷新
function doRefreshQueryItems(_isFirst){
  var jsy_datas = JSPFree.getBillListAllDatas(d1_B_A_BillList);
  ary_newQueryItems=[];
  for(var i=0;i<jsy_datas.length;i++){
    if("Y"==jsy_datas[i].isquery){
      ary_newQueryItems.push(jsy_datas[i].itemkey);
    }
  }

  var str_templetcode = jso_OpenPars.templetcode; //模板编码
  
  var jso_par = {webcontext:v_context, divid:"d1_A", templetCode:str_templetcode, checkitems:ary_newQueryItems, isFirst:_isFirst};
  var jso_data = FreeUtil.doClassMethodCall(FreeUtil.JSPBuilderClassName,"getBillQueryFreeBuildHtml",jso_par);
  var str_html = jso_data.html;  //只生成html
  var str_jstext = jso_data.jstext;  //只生成html
 
  //再加入实际内容
  var dom_div = document.getElementById("d1_A");
  dom_div.innerHTML=str_html;  //重新输入Html

  //再搞JavaScript代码
  if(str_jstext!=null && str_jstext!=""){
    var newJSObj= document.createElement("script");  //一定要用这个创建,否则浏览器不解析
    newJSObj.id = "d1_A";
    newJSObj.type = "text/javascript";  //一定设置类型
    newJSObj.text = str_jstext;  //直接设置文本
    document.body.appendChild(newJSObj);  //在</body>前加上
  }

  $.parser.parse('#d1_A');
}

//取得所有VOs
function getAllItemVOsByKey(_itemArray){
  var jsy_itemVOs = jso_queryTempletVO.templet_option_b;
  var ary_rt = [];
  for(var i=0;i<_itemArray.length;i++){
    var itemVO = null;  //
    for(var j=0;j<jsy_itemVOs.length;j++){
      if(jsy_itemVOs[j].itemkey==_itemArray[i]){
        itemVO = jsy_itemVOs[j];  //
        break;
      }
    }

    if(itemVO!=null){
      ary_rt.push(itemVO);
    }
  }
  return ary_rt;
}


//添加列
function onAddOrderCol(){
 var jso_data = JSPFree.getBillListSelectData(d1_B_A_BillList);
 if(jso_data==null){
  $.messager.alert('提示','必须选择一个列添加!','info');
  return;
 }
 
 var str_itemkey  = jso_data.itemkey;
 var str_itemname  = jso_data.itemname;

  //判断是否重复加入
 var isAlready = false;
 var jsy_orderDatas = JSPFree.getBillListAllDatas(d1_B_B_BillList);
 for(var i=0;i<jsy_orderDatas.length;i++){
  if(jsy_orderDatas[i].itemkey==str_itemkey){
    isAlready = true;
    break;
  }
 }
 
 if(isAlready){
  $.messager.alert('提示','【' + str_itemname + '】已经参与排序,不能重复添加!','info');
  return;
 }

 var jsy_data = [];
 jsy_data.push({itemkey:str_itemkey,itemname:str_itemname,isdesc:"N"});

 //加入数据
 JSPFree.billListAppendDatas(d1_B_B_BillList,jsy_data);
}

//删除列
function onDelOrderCol(){
 var li_row = JSPFree.getBillListSelectRow(d1_B_B_BillList);
 if(li_row<0){
  $.messager.alert('提示','必须选择一个排序列进行删除!','info');
  return;
 }
 
JSPFree.billListDeleteRow(d1_B_B_BillList,li_row);
}

//确定
function onConfirm(){
 //拼SQL的where部分
 var jsy_itemVOs = getAllItemVOsByKey(ary_newQueryItems);

 var dom_form = document.getElementById("d1_A_QueryForm");
 var str_sql = JSPFree.getQueryFormSQLConsByItems(dom_form,jsy_itemVOs);
 if(str_sql==null){
    return;
 }

 //xml模板中定义的查询条件,如果有,则与传入的条件再合并!
var str_xmlCons = jso_queryTempletVO.templet_option.querycontion;   
if(typeof str_xmlCons!="undefined" && str_xmlCons!=null && str_xmlCons.trim()!=""){
  if(str_sql==""){
    str_sql = str_sql + str_xmlCons;  //
  }else{
    str_sql = str_sql + " and " + str_xmlCons + " "  
  }
}
  
 //拼SQL的order by
 var str_OrderBySQL = "";
 var jsy_orderDatas = JSPFree.getBillListAllDatas(d1_B_B_BillList);
 for(var i=0;i<jsy_orderDatas.length;i++){
   if("Y"==jsy_orderDatas[i].isdesc){
     str_OrderBySQL = str_OrderBySQL + jsy_orderDatas[i].itemkey + " desc";
   } else {
     str_OrderBySQL = str_OrderBySQL + jsy_orderDatas[i].itemkey + "";
   }
   if(jsy_orderDatas.length>1 && i!=jsy_orderDatas.length-1){
    str_OrderBySQL = str_OrderBySQL + ",";  //加上逗号
   }
 }
 
 FreeUtil.hiddenDialog({result:"Confirm",buildWhereSQL:str_sql,buildOrderBy:str_OrderBySQL,popDialogId:self.v_dialogid});
}

//重置
function onReset(){
  $('#d1_A_QueryForm').form('clear');
}

//取消
function onCancel(){
 FreeUtil.hiddenDialog({popDialogId:self.v_dialogid});
}
