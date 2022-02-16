package com.yusys.bione.plugin.rptdim.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.rptdim.entity.RptDimCatalog;
import com.yusys.bione.plugin.rptdim.entity.RptDimHistoryInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptdim.repository.mybatis.RptDimDao;
import com.yusys.bione.plugin.rptdim.web.vo.RptDimItemInfoVO;

/**
 * <pre>
 * Title:维度业务逻辑类
 * Description: 提供维度相关业务逻辑处理功能，提供事务控制
 * </pre>
 * 
 * @author aman  aman@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:1.01   修改人：aman  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptDimBS extends BaseBS<RptDimCatalog> {
	protected static Logger log = LoggerFactory.getLogger(RptDimBS.class);
	@Autowired
	private  RptDimDao rptDimDao;
	/**
	 * 获取维度目录树和维度类型
	 * @param params
	 * @return
	 */
	public List<CommonTreeNode> getDimTree(Map<String,Object> params) {
		String basePath = GlobalConstants4frame.APP_CONTEXT_PATH;
		List<CommonTreeNode> treeNodes = Lists.newArrayList();
		List<String> newExceptTypeIdList =Lists.newArrayList();
		Map<String,String>    checkedTypeIdMap  =  Maps.newHashMap();
		if(!"".equals(params.get("checkedTypeIds"))){
			String[]  checkedTypeIdStr = StringUtils.split(params.get("checkedTypeIds").toString(), ',');
			for(String checkedTypeId:checkedTypeIdStr ){
				checkedTypeIdMap.put(checkedTypeId, checkedTypeId);
			}
		}
		if(!"".equals(params.get("exceptTypeIds"))){
			String[]  exceptTypeIdStr = StringUtils.split(params.get("exceptTypeIds").toString(), ',');
			for(String exceptTypeId:exceptTypeIdStr ){
				newExceptTypeIdList.add(exceptTypeId);
			}
		}
		if(newExceptTypeIdList.size()==0){
			newExceptTypeIdList.add(" ");
		}
		params.put("list", newExceptTypeIdList);
		List<RptDimCatalog>   allCatalogs  =  this.rptDimDao.getAllRptDimCatalogs();
		List<RptDimTypeInfo>  allTypeInfoByParams =  this.rptDimDao.getAllTypeInfoByParams(params);
		List<RptDimTypeInfo>  typeInfoOfSys = Lists.newArrayList(); 
		Map<String, RptDimCatalog> catalogMap =  Maps.newHashMap();
		Map<String, RptDimCatalog> catalogMapCopy =  Maps.newHashMap();
		if(allCatalogs!=null&&allCatalogs.size()>0){
			for (RptDimCatalog catalog : allCatalogs) {
				catalogMap.put(catalog.getCatalogId()  , catalog);
				catalogMapCopy.put(catalog.getCatalogId()  , catalog);
			}
		}
		Map<String,CommonTreeNode> midMap = Maps.newHashMap();
			if (allTypeInfoByParams != null && allTypeInfoByParams.size() > 0) {
				for (RptDimTypeInfo typeinfo : allTypeInfoByParams) {
					  if(typeinfo.getDimType()!=null&&!typeinfo.getDimType().equals(GlobalConstants4plugin.DIM_TYPE_BUSI)){
						  typeInfoOfSys.add(typeinfo);
					  }else{
						CommonTreeNode treeChildNode = new CommonTreeNode();
				    	treeChildNode.setData(typeinfo);
				    	
				    	treeChildNode.setId(typeinfo.getDimTypeNo());
				    	treeChildNode.setText(typeinfo.getDimTypeNm());
				    	treeChildNode.setUpId(typeinfo.getCatalogId());
				    	
				    	treeChildNode.getParams().put("type", "dimTypeInfo");
				    	if(checkedTypeIdMap.containsKey(typeinfo.getDimTypeNo())){
				    		treeChildNode.setIschecked(true);
				    	}
				    	treeChildNode.setIcon(basePath +"/"+GlobalConstants4frame.ICON_URL+"/list-items.gif");
				    	midMap.put(treeChildNode.getId()+"_"+treeChildNode.getUpId(),treeChildNode);
						generateResultList(basePath,catalogMap,catalogMapCopy, midMap, typeinfo);
					}
				}
			}
	    //所有的情况下,midMap装剩下的catalog
		if(params.get("searchText").equals("%%")&&params.get("exceptTypeIds").equals("")&&StringUtils.isEmpty((String)params.get("preview"))){
	    	    Set<String>  keys_  =  catalogMapCopy.keySet();
	    	    for(String  key:keys_){
	    	    	RptDimCatalog  catalog = catalogMapCopy.get(key);
	    			if (catalog != null) {
	    				CommonTreeNode treeChildNode = new CommonTreeNode();
	    				treeChildNode.setData(catalog);
	    				treeChildNode.setId(catalog.getCatalogId());
	    				treeChildNode.setText(catalog.getCatalogNm());
	    				treeChildNode.setUpId(catalog.getUpCatalogId());
	    				treeChildNode.getParams().put("type", "dimCatalog");
	    			    treeChildNode.setIcon(basePath + GlobalConstants4frame.DATA_TREE_NODE_ICON_CATALOG);
	    				midMap.put(treeChildNode.getId()+"_"+treeChildNode.getUpId(),treeChildNode);
	    			}		
	    	    }
	    	    
	    }		
		//构造根节点
		CommonTreeNode baseNode = new CommonTreeNode();
		baseNode.setId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
		baseNode.setText("全部");
		baseNode.setUpId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
		RptDimCatalog  catalogBase = new  RptDimCatalog();
		catalogBase.setCatalogId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
		catalogBase.setUpCatalogId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
		baseNode.setData(catalogBase);
		baseNode.setIcon(basePath + GlobalConstants4plugin.DATA_TREE_NODE_ICON_ROOT);
		// 以midMap为入口，构造树型结构
	    Set<String>   keys =  midMap.keySet();
		for(String key:keys) {
			    CommonTreeNode    temp  =  midMap.get(key);
			    if(temp!=null){
					if (GlobalConstants4frame.DEFAULT_TREE_ROOT_NO.equals(temp.getUpId())) {
					    generateVoList(keys,midMap, temp);
						baseNode.addChildNode(temp);
	      			}
				}
		}
		sortTheTree(baseNode);
		List<CommonTreeNode>   childrenOfBaseNode =  baseNode.getChildren();
		baseNode.setChildren(new  ArrayList<CommonTreeNode>());
		for(int  i=0;i<typeInfoOfSys.size();i++){
			RptDimTypeInfo   type  =   typeInfoOfSys.get(i);
			CommonTreeNode treeChildNode = new CommonTreeNode();
	    	treeChildNode.setData(type);
	    	
	    	treeChildNode.setId(type.getDimTypeNo());
	    	treeChildNode.setText(type.getDimTypeNm());
	    	treeChildNode.setUpId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
	    	
	    	treeChildNode.getParams().put("type", "dimTypeInfo");
	    	treeChildNode.setIcon(basePath +"/"+GlobalConstants4frame.ICON_URL+"/list-items.gif");
	    	baseNode.addChildNode(treeChildNode);
		}
		if(childrenOfBaseNode!=null){
		   baseNode.getChildren().addAll(childrenOfBaseNode);
		}
		if(params.get("getAllTypeInfo")!=null){
			treeNodes.add(baseNode);
		}else{
            treeNodes.addAll(baseNode.getChildren());
		}
		return treeNodes;
	}
	
	
	/**
	 * 获取所有维度目录树
	 * @param params
	 * @return
	 */
	public List<CommonTreeNode> getCatalogTree() {
		String basePath = GlobalConstants4frame.APP_CONTEXT_PATH;
		List<CommonTreeNode> treeNodes = Lists.newArrayList();
		List<RptDimCatalog>   allCatalogs  =  this.rptDimDao.getAllRptDimCatalogs();
		//构造根节点
		CommonTreeNode baseNode = new CommonTreeNode();
		baseNode.setId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
		baseNode.setText("全部");
		baseNode.setUpId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
		RptDimCatalog  catalogBase = new  RptDimCatalog();
		catalogBase.setCatalogId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
		catalogBase.setUpCatalogId(GlobalConstants4frame.DEFAULT_TREE_ROOT_NO);
		baseNode.setData(catalogBase);
		baseNode.setIcon(basePath + GlobalConstants4plugin.DATA_TREE_NODE_ICON_ROOT);
		treeNodes.add(baseNode);
		for(int i=0;i<allCatalogs.size();i++){
			RptDimCatalog  catalog  =   allCatalogs.get(i);
			CommonTreeNode treeChildNode = new CommonTreeNode();
			treeChildNode.setData(catalog);
			treeChildNode.setId(catalog.getCatalogId());
			treeChildNode.setText(catalog.getCatalogNm());
			treeChildNode.setUpId(catalog.getUpCatalogId());
			treeChildNode.getParams().put("type", "dimCatalog");
		    treeChildNode.setIcon(basePath + GlobalConstants4frame.DATA_TREE_NODE_ICON_CATALOG);
		    treeNodes.add(treeChildNode);
		}
		return treeNodes;
	}
	private  void sortTheTree(CommonTreeNode  treeNode){
		List<CommonTreeNode>   treeNodes  =  treeNode.getChildren();
		if(treeNodes!=null){
			int  size  =  treeNodes.size();
			for(int i  =0;i<size;i++){
				sortTheTree(treeNodes.get(i));
        		int  minrecindex=i;
        		for(int j  =i+1;j<size;j++){
        			   if(treeNodes.get(minrecindex).getParams().get("type").equals("dimCatalog")&&treeNodes.get(j).getParams().get("type").equals("dimTypeInfo")){
        				   minrecindex  =  j;
        			   }
        		}
        		if(minrecindex!=i){
        			Collections.swap(treeNodes, minrecindex,i); 
        		}
        	}
			for(int i  =0;i<size;i++){
				sortTheTree(treeNodes.get(i));
        		int  minrecindex=i;
        		for(int j  =i+1;j<size;j++){
        			   if(treeNodes.get(minrecindex).getParams().get("type").equals(treeNodes.get(j).getParams().get("type"))&&treeNodes.get(minrecindex).getText().compareTo(treeNodes.get(j).getText())>0){
        				   minrecindex  =  j;
        			   }
        		}
        		if(minrecindex!=i){
        			Collections.swap(treeNodes, minrecindex,i); 
        		}
        	}
		}		
	}
	/**
	 * 进行倒查
	 * @param basePath
	 * @param catalogMap
	 * @param midMap
	 * @param typeInfo
	 */
	private void generateResultList(String  basePath,Map<String, RptDimCatalog> catalogMap,Map<String, RptDimCatalog> catalogMapCopy, Map<String,CommonTreeNode> midMap ,
			RptDimTypeInfo typeInfo) {
		
		RptDimCatalog  directUpCatalog = catalogMap.get(typeInfo.getCatalogId());
		if (directUpCatalog != null) {
			CommonTreeNode treeChildNode = new CommonTreeNode();
			treeChildNode.setData(directUpCatalog);
			treeChildNode.setId(directUpCatalog.getCatalogId());
			treeChildNode.setText(directUpCatalog.getCatalogNm());
			treeChildNode.setUpId(directUpCatalog.getUpCatalogId());
			
			treeChildNode.getParams().put("type", "dimCatalog");
			treeChildNode.getParams().put("hasTypes", "true");
		    treeChildNode.setIcon(basePath + GlobalConstants4frame.DATA_TREE_NODE_ICON_CATALOG);
		    if (midMap.containsKey(treeChildNode.getId()+"_"+treeChildNode.getUpId())) {
				return;
			}
		    catalogMapCopy.remove(typeInfo.getCatalogId());
			midMap.put(treeChildNode.getId()+"_"+treeChildNode.getUpId(),treeChildNode);
			generateOuterResultList(basePath,catalogMap,catalogMapCopy, midMap, directUpCatalog);
		}
	}
	private  void generateOuterResultList(String  basePath,Map<String, RptDimCatalog> catalogMap,Map<String, RptDimCatalog> catalogMapCopy, Map<String,CommonTreeNode> midMap ,
			RptDimCatalog catalog){
		
		RptDimCatalog  directUpCatalog = catalogMap.get(catalog.getUpCatalogId());
		if (directUpCatalog != null) {
			CommonTreeNode treeChildNode = new CommonTreeNode();
			treeChildNode.setData(directUpCatalog);
			treeChildNode.setId(directUpCatalog.getCatalogId());
			treeChildNode.setText(directUpCatalog.getCatalogNm());
			treeChildNode.setUpId(directUpCatalog.getUpCatalogId());
			
			treeChildNode.getParams().put("type", "dimCatalog");
			treeChildNode.getParams().put("hasTypes", "true");
		    treeChildNode.setIcon(basePath + GlobalConstants4frame.DATA_TREE_NODE_ICON_CATALOG);
			if (midMap.containsKey(treeChildNode.getId()+"_"+treeChildNode.getUpId())) {
				return;
			}
			catalogMapCopy.remove(catalog.getUpCatalogId());
			midMap.put(treeChildNode.getId()+"_"+treeChildNode.getUpId(),treeChildNode);
			generateOuterResultList(basePath,catalogMap,catalogMapCopy, midMap, directUpCatalog);
		}		
	}

	/**
	 * 形成树结构
	 * @param basePath
	 * @param nodeInfoList
	 * @param treeNode
	 */
	private void generateVoList(Set<String>   keys ,Map<String,CommonTreeNode> midMap , CommonTreeNode treeNode) {
		for (String  key : keys) {
			CommonTreeNode  temp   =   midMap.get(key);
			if(temp!=null){
				if (temp.getUpId().equals(treeNode.getId())) {
				    treeNode.addChildNode(temp);
				    if("dimCatalog".equals(temp.getParams().get("type"))){
					   generateVoList(keys,midMap, temp);
					}      
				}
			}
		}
	}
	
	
	/**
	 * 判断该维度项是否有子节点
	 * @param string
	 * @return
	 */
	@SuppressWarnings("unused")
	private Boolean isParentForDimCatalog(String nodeId) {
		Integer childrenCount = this.rptDimDao.getCatalogChildrenCountByUpId(nodeId.equals("base")?GlobalConstants4frame.DEFAULT_TREE_ROOT_NO:nodeId);
		return childrenCount.intValue()==0?false:true;
	}
	
	/**
	 * 删除维度目录
	 * @param id
	 */
	@Transactional(readOnly = false)
	public  void   cascadeDel(String  id){
		List<RptDimCatalog>   catalogs = this.rptDimDao.getCatalogsByUpId(id);
		for(int i=0;i<catalogs.size();i++){
			RptDimCatalog  cata = catalogs.get(i);
			cascadeDel(cata.getCatalogId());
		}
		
		List<RptDimTypeInfo>   types =  this.rptDimDao.getTypeInfosByCatalogId(id);
		String  typeids = "";
		for(int  t =0 ; t<types.size() ; t++){
			RptDimTypeInfo  type = types.get(t);
			typeids += type.getDimTypeNo()+",";
		}
		
		if(typeids.length()>0)   cascadeDimTypeDel(typeids);
		this.rptDimDao.rptDimCatalogDelById(id);
		
	}
	
	/**
	 * 删除维度类型
	 * @param id
	 */
	@Transactional(readOnly = false)
	public  void   cascadeDimTypeDel(String  id){
		if(id!=null&&id.length()>0){
			id = id.substring(0,id.length()-1);
		}
		List<String>  idList =  new  ArrayList<String>(); 
		if(id!=null&&id.length()>0){
			String[]  idSplit = StringUtils.split(id, ',');
			for(int  i=0 ;i<idSplit.length;i++){
			   idList.add(idSplit[i]);
			}
		}
		//先删除维度类型下面的维度值（维度值是树形，需要级联删除）
		Map<String,Object> conditions  =  Maps.newHashMap();
		for(int  i =0  ;i<idList.size();i++){
			conditions.put("dimTypeNo", idList.get(i));
			List<RptDimItemInfo>   items =  this.rptDimDao.findDimItemListByTypeNo(conditions);
			for(int j=0;j<items.size();j++){
				cascadeDimItemInnerDel(items.get(j).getId().getDimItemNo(),idList.get(i));
			}
		}
		this.rptDimDao.rptDimTypeDelByIds(idList);
		
	}
	
	
	/**
	 * 删除维度项（树形结构的grid记录级联删除）
	 * @param id
	 */
	@Transactional(readOnly = false)
	public  void   cascadeDimItemDel(List<String>  idList,String dimTypeNo){
		   for(int i=0 ;i<idList.size();i++){
			   cascadeDimItemInnerDel(idList.get(i),dimTypeNo);
		   }
		   
	}
	private void   cascadeDimItemInnerDel(String  id,String dimTypeNo){
		Map<String,String>   params = Maps.newHashMap();
		params.put("upNo", id);
		params.put("dimTypeNo", dimTypeNo);
		List<RptDimItemInfo>  itemList  =   this.rptDimDao.findDimItemListByUpNo(params);
		for(int i=0;i<itemList.size();i++){
			RptDimItemInfo  item  =  itemList.get(i);
			cascadeDimItemInnerDel(item.getId().getDimItemNo(),dimTypeNo);
		}
		params.put("dimItemNo", id);
		this.rptDimDao.rptDimItemDelByPkId(params);
	}
	
	/**
	 * 根据id获取维度目录实体对象
	 * @param catalogId
	 * @return
	 */
	public   RptDimCatalog   findDimCatalogById(String  catalogId){
		return   this.rptDimDao.findDimCatalogById(catalogId);
	}
	
	/**
	 * 根据id获取维度类型实体对象
	 * @param catalogId
	 * @return
	 */
	public   RptDimTypeInfo  findDimTypeInfoById(String  dimTypeNo){
		return   this.rptDimDao.findDimTypeInfoById(dimTypeNo);
	}
	
	
	/**
	 * 根据id获取维度项实体对象
	 * @param catalogId
	 * @return
	 */
	public   RptDimItemInfo  findDimItemInfoByPkId(Map<String,String> params){
		return   this.rptDimDao.findDimItemInfoByPkId(params);
	}
	
	/**
	 * 新增维度目录实体对象
	 * @param rptDimCatalog
	 */
	@Transactional(readOnly = false)
	public   void  createRptDimCatalog(RptDimCatalog  rptDimCatalog){
		this.rptDimDao.createRptDimCatalog(rptDimCatalog);
	}
	
	/**
	 * 新增维度类型实体对象
	 * @param rptDimTypeInfo
	 */
	@Transactional(readOnly = false)
	public   void  createRptDimTypeInfo(RptDimTypeInfo  rptDimTypeInfo){
		this.rptDimDao.createRptDimTypeInfo(rptDimTypeInfo);
	}
	
	/**
	 * 新增维度项实体对象
	 * @param rptDimItemInfo
	 */
	@Transactional(readOnly = false)
	public   void  createRptDimItemInfo(RptDimItemInfo  rptDimItemInfo){
		this.rptDimDao.createRptDimItemInfo(rptDimItemInfo);
	}
	
	/**
	 * 修改维度目录实体对象
	 * @param rptDimCatalog
	 */
	@Transactional(readOnly = false)
	public   void  updateRptDimCatalog(RptDimCatalog  rptDimCatalog){
		this.rptDimDao.updateRptDimCatalog(rptDimCatalog);
	}
	
	/**
	 * 修改维度类型实体对象
	 * @param rptDimTypeInfo
	 */
	@Transactional(readOnly = false)
	public   void  updateRptDimTypeInfo(RptDimTypeInfo  rptDimTypeInfo){
		this.rptDimDao.updateRptDimTypeInfo(rptDimTypeInfo);
	}
	
	/**
	 * 修改维度项实体对象
	 * @param rptDimItemInfo
	 */
	@Transactional(readOnly = false)
	public   void  updateRptDimItemInfo(RptDimItemInfo  rptDimItemInfo){
		this.rptDimDao.updateRptDimItemInfo(rptDimItemInfo);
	}
	
	/**
	 * 根据参数获取维度类型 mybatis分页
	 * @param pager
	 * @return
	 */
	public SearchResult<RptDimTypeInfo> getTypeInfosByParams(Pager pager) {
		PageHelper.startPage(pager);
		Map<String,Object>  params  =  Maps.newHashMap();
		List<String>  dimTypeList  =  Lists.newArrayList();  
		dimTypeList.add(GlobalConstants4plugin.DIM_TYPE_BUSI);
		dimTypeList.add(GlobalConstants4plugin.DIM_TYPE_CURRENCY);
	    params.put("dimTypeList", dimTypeList);
		PageMyBatis<RptDimTypeInfo>  page = (PageMyBatis<RptDimTypeInfo>)rptDimDao.getTypeInfosByParams(params);
		SearchResult<RptDimTypeInfo> results = new SearchResult<RptDimTypeInfo>();
		results.setTotalCount(page.getTotalCount());
		results.setResult(page.getResult());
		return results;
	}	
	
	/**
	 * 根据参数获取维度项 mybatis分页--树形
	 * @param pager
	 * @return
	 */
	public SearchResult<RptDimItemInfo> getItemInfoTreeByParams(Pager pager) {
		Integer   totalCount =  rptDimDao.getItemsCount();
		pager.setPagesize(totalCount);
		PageHelper.startPage(pager);
		PageMyBatis<RptDimItemInfo>  page = (PageMyBatis<RptDimItemInfo>)rptDimDao.getItemInfosByParams();
		SearchResult<RptDimItemInfo> results = new SearchResult<RptDimItemInfo>();
		results.setResult(page.getResult());
		return results;
	}	
	
	/**
	 * 根据参数获取维度项 mybatis分页--列表
	 * @param pager
	 * @return
	 */
	public SearchResult<RptDimItemInfo> getItemInfoListByParams(Pager pager) {
		PageHelper.startPage(pager);
		PageMyBatis<RptDimItemInfo>  page = (PageMyBatis<RptDimItemInfo>)rptDimDao.getItemInfosByParams();
		SearchResult<RptDimItemInfo> results = new SearchResult<RptDimItemInfo>();
		results.setTotalCount(page.getTotalCount());
		results.setResult(page.getResult());
		return results;
	}	
	
	/**
	 * 根据维度类型no获取维度项
	 * @param dimTypeNo
	 * @return
	 */
	public List<RptDimItemInfo>    findDimItemListByTypeNo(String dimTypeNo){
		Map<String,Object> conditions  =  Maps.newHashMap();
		conditions.put("dimTypeNo",dimTypeNo);
		List<RptDimItemInfo>    itemInfoList = rptDimDao.findDimItemListByTypeNo(conditions);
		   return  itemInfoList;
	}
	
	/**
	 * 进行倒查询
	 * @param itemInfoMap
	 * @param midList
	 * @param itemInfo
	 */
	private void generateResultList(Map<String, RptDimItemInfo> itemInfoMap, List<RptDimItemInfo> midList,
			RptDimItemInfo itemInfo) {
		RptDimItemInfo directUpItem = itemInfoMap.get(itemInfo.getUpNo() + "_" + itemInfo.getId().getDimTypeNo());
		if (directUpItem != null) {
			if (midList.contains(directUpItem)) {
				return;
			}
			midList.add(directUpItem);
			generateResultList(itemInfoMap, midList, directUpItem);
		}
	}

	/**
	 * 
	 * 形成树结构
	 * @param deptInfoList
	 * @param vo
	 */
	private void generateVoList(List<RptDimItemInfo> itemInfoList, RptDimItemInfoVO vo) {
		for (RptDimItemInfo item : itemInfoList) {
			if (item.getUpNo().equals(vo.getDimItemNo())) {
				RptDimItemInfoVO infoVO = new RptDimItemInfoVO();
				infoVO.setDimItemNm(item.getDimItemNm());
				infoVO.setDimItemNo(item.getId().getDimItemNo());
				infoVO.setDimTypeNo(item.getId().getDimTypeNo());
				infoVO.setRemark(item.getRemark());
				infoVO.setUpNo(item.getUpNo());
				infoVO.setBusiDef(item.getBusiDef());
				vo.getChildren().add(infoVO);
				generateVoList(itemInfoList, infoVO);
			}
		}
	}
	public Map<String, Object> getDimItemTree(Pager pager,  String dimTypeNo) {
		SearchResult<RptDimItemInfo> searchResult = getItemInfoTreeByParams(pager);
		List<RptDimItemInfo>  allItems= findDimItemListByTypeNo(dimTypeNo);
		List<RptDimItemInfo>  itemInfoList =searchResult.getResult();
		Map<String, RptDimItemInfo> itemInfoMap =  Maps.newHashMap();
		if(allItems!=null&&allItems.size()>0){
			for (RptDimItemInfo item : allItems) {
				itemInfoMap.put(item.getId().getDimItemNo() + "_" + item.getId().getDimTypeNo(), item);
			}
		}
		List<RptDimItemInfo> midList = Lists.newArrayList();

				if (itemInfoList != null && itemInfoList.size() > 0) {
					for (RptDimItemInfo item : itemInfoList) {
						RptDimItemInfo itemInfo = itemInfoMap.get(item.getId().getDimItemNo() + "_" + item.getId().getDimTypeNo());
						if (itemInfo != null) {
							if (!midList.contains(itemInfo)) {
								midList.add(itemInfo);
							}
							generateResultList(itemInfoMap, midList, itemInfo);
						}
					}
				}

		List<RptDimItemInfoVO> resultList = Lists.newArrayList();
		// 以midList为入口，构造树型结构
		if (midList != null && midList.size() > 0) {

			for (RptDimItemInfo item : midList) {
				RptDimItemInfoVO vo = new RptDimItemInfoVO();
				if (GlobalConstants4frame.DEFAULT_TREE_ROOT_NO.equals(item.getUpNo())) {
                    vo.setDimItemNm(item.getDimItemNm());
                    vo.setDimItemNo(item.getId().getDimItemNo());
                    vo.setDimTypeNo(item.getId().getDimTypeNo());
                    vo.setRemark(item.getRemark());
                    vo.setUpNo(item.getUpNo());
                    vo.setBusiDef(item.getBusiDef());
					generateVoList(midList, vo);
					resultList.add(vo);
				}
			}
		}
		Map<String, Object> inOrderItemMap = Maps.newHashMap();
		inOrderItemMap.put("Rows", resultList);
		return inOrderItemMap;
	}
	
	
	public Integer testSameDimTypeNo(Map<String,Object>   params){
		return  this.rptDimDao.testSameDimTypeNo(params);
	}
	
	public Integer testSameDimItemNm(Map<String,Object>   params){
		return  this.rptDimDao.testSameDimItemNm(params);
	}
	
	public Integer testSameDimTypeNm(Map<String,Object>   params){
		return  this.rptDimDao.testSameDimTypeNm(params);
	}
	
	public Integer testSameDimCatalogNm(Map<String,Object>   params){
		return  this.rptDimDao.testSameDimCatalogNm(params);
	}
	
	public List<RptDimTypeInfo> findDimTypeInfoByIds(List<String> dimNos){
		return this.rptDimDao.findDimTypeInfoByIds(dimNos);
	}
	
	
	public List<RptDimTypeInfo> getAllRptDimTypeInfos(){
    	return  this.rptDimDao.getAllRptDimTypeInfos();
    }
	
	
	public List<RptDimItemInfoVO> getAllRptDimItemInfos(){
		return  this.rptDimDao.getAllRptDimItemInfos();
	}
	
	
	public  String  getMaxDimNo(String  type){
		return this.rptDimDao.getMaxDimNo(type);
	}
	
	public String saveAsHistoryVer(String dimTypeNo){
		String result  ="1";
		
		SimpleDateFormat  sdf  =   null;
		if(sdf==null){
			sdf  =  new  SimpleDateFormat("yyyyMMdd");
		}
		String currDate  =  sdf.format(new Date());
		RptDimTypeInfo  type = rptDimDao.findDimTypeInfoById(dimTypeNo);
		if(type==null){
			return  "0";
		}
		List<RptDimCatalog> catalogs = this.rptDimDao.getAllRptDimCatalogs();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dimTypeNo", type.getDimTypeNo());
		List<RptDimItemInfo> items = rptDimDao.getItemInfosByDimType(params);
		Map<String,Object>   dataMap   =  Maps.newHashMap();
		dataMap.put("type", type);
		dataMap.put("catalogs", catalogs);
		dataMap.put("items", items);
		RptDimHistoryInfo  maxVerHistory  =  this.rptDimDao.getMaxVerHistory();
		RptDimHistoryInfo   history  =  new  RptDimHistoryInfo();
		history.setHisId(RandomUtils.uuid2());
		
		if(maxVerHistory==null){
			history.setVeId(1);
		}else{
			if(currDate.equals(maxVerHistory.getStartDate())){
				this.rptDimDao.deleteRptDimHistoryById(maxVerHistory.getHisId());
				history.setVeId(1);
			}else{
				history.setVeId(maxVerHistory.getVeId()+1);
				maxVerHistory.setEndDate(currDate);
				this.rptDimDao.updateRptDimHistory(maxVerHistory);
			}
		}
	    history.setDimTypeNo(dimTypeNo);
	    history.setStartDate(currDate);
	    history.setEndDate("29991231");
	    history.setHisInfo(JSON.toJSONString(dataMap));
	    try {
			this.rptDimDao.createRptDimHistory(history);
		} catch (Exception e) {
			 result  ="0";
			e.printStackTrace();
		}
	    return  result;
	}
	
	public List<RptDimHistoryInfo> getAllHistoryByDimTypeNo(String  dimTypeNo){
		  return  this.rptDimDao.getAllHistoryByDimTypeNo(dimTypeNo);
	}
	
	public  RptDimHistoryInfo   getRptDimHistoryByHisId(String  hisId){
		return  this.rptDimDao.getRptDimHistoryByHisId(hisId);
	}
	
	public  boolean  checkHasBeenCascaded(Map<String,Object> param){
		Integer num  =   this.rptDimDao.getRelatedCountByDimno(param);
		if(num==null||num==0){
			return   false;
		}
		return    true;
	}
}
