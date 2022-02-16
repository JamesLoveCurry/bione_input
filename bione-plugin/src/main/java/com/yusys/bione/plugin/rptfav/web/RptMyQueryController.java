package com.yusys.bione.plugin.rptfav.web;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.rptfav.entity.RptFavFolder;
import com.yusys.bione.plugin.rptfav.service.RptMyQueryBS;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
/**
 * <pre>
 * Title:报表平台--我的收藏
 * Description: 
 * </pre>
 * 
 * @author 
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/rpt/frame/rptfav/query")
public class RptMyQueryController extends BaseController{
	
	@Autowired
	private RptMyQueryBS rptMyQueryBS;
	
	@RequestMapping
	public ModelAndView index(){
		return new ModelAndView("/plugin/rptfav/query-my-index");
	}
	
	@RequestMapping(value="/getTreeNode")
	@ResponseBody
	public List<CommonTreeNode> getTreeNode(String folderNm){
		List<CommonTreeNode> childeNodeList = this.rptMyQueryBS.getTreeNode(getContextPath(),folderNm);
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setIcon(this.getContextPath()+"/"+GlobalConstants4frame.ICON_URL+"/house.png");
			treeNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
			treeNode.setText("全部");
			List<CommonTreeNode> list = new ArrayList<CommonTreeNode>();
			list.add(treeNode);
			list.addAll(childeNodeList);
			return list;
	}
	
	@RequestMapping(value = "/addCatalog")
	public ModelAndView addCatalog(String upFolderId,String folderId,String folderNm){
		Map<String ,Object> map = new HashMap<String, Object>();
		map.put("upFolderId", StringUtils2.javaScriptEncode(upFolderId));
		map.put("folderId", StringUtils2.javaScriptEncode(folderId));
		if(folderNm != null && !folderNm.equals("")){
			map.put("folderNm", StringUtils2.javaScriptEncode(folderNm));
		}
		return new ModelAndView("/plugin/rptfav/edit-my-query",map);
	}
	
	@RequestMapping(value="/findFolderNm")
	@ResponseBody
	public boolean findFolderNm(String folderNm,String upFolderId){
		List<RptFavFolder> list = this.rptMyQueryBS.findRptFavFolder(folderNm,upFolderId);
		if(list == null || list.size() == 0){
			return true;
		}else{
			return false;
		}
	}
	
	@RequestMapping(value="/edit")
	public void edit(RptFavFolder rptFavFolder){
		if(rptFavFolder.getFolderId() == null || rptFavFolder.getFolderId().equals("")){
			this.rptMyQueryBS.edit(rptFavFolder);
		}else{
			this.rptMyQueryBS.update(rptFavFolder);
		}
	}
	
	@RequestMapping(value="delete")
	@ResponseBody
	public String delete(String folderId){
		return this.rptMyQueryBS.delete(folderId);
	}
	
	@RequestMapping("cascade")
	@ResponseBody
	public void cascade(String folderId) {
		this.rptMyQueryBS.cascadeDelete(folderId);
	}
	
	@RequestMapping("/rptType")
	@ResponseBody
	public String getRptType(String rptId) {
		return rptMyQueryBS.getRptType(rptId);
	}
	@RequestMapping("/getRptInfo")
	@ResponseBody
	public Map<String,Object> getRptInfo(String rptId) throws Exception {
		RptMgrReportInfo info = rptMyQueryBS.getRptInfo(rptId);
		
		return objectToMap(info);
	}
	public static Map<String, Object> objectToMap(Object obj) throws Exception {    
        if(obj == null)  
            return null;      
  
        Map<String, Object> map = new HashMap<String, Object>();   
  
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());    
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();    
        for (PropertyDescriptor property : propertyDescriptors) {    
            String key = property.getName();    
            if (key.compareToIgnoreCase("class") == 0) {   
                continue;  
            }  
            Method getter = property.getReadMethod();  
            Object value = getter!=null ? getter.invoke(obj) : null;  
            map.put(key, value);  
        }    
  
        return map;  
    }    

}
