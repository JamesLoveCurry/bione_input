/**
 *
 * <pre>
 * Title:
 * Description:
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2020/10/30 14:58
 */

function AfterInit(){

    JSPFree.createBillCard("d1","/biapp-imas/freexml/tabConfig/imas_cr_tab_sql.xml",["保存/onSave/icon-p21","取消/onCancel/icon-undo"],null);
    //赋值
    JSPFree.queryBillCardData(d1_BillCard, "rid = '"+jso_OpenPars.rid+"'");
}
/**
 * 保存
 * @return {[type]} [description]
 */
function onSave(){
    var flag = JSPFree.doBillCardUpdate(d1_BillCard,null);
    if(flag){
        JSPFree.closeDialog("保存成功");
    }
}

/**
 * 取消
 * @return {[type]} [description]
 */
function onCancel(){
    JSPFree.closeDialog();
}