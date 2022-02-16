package com.yusys.bione.plugin.rptbank.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.rptbank.entity.RptIdxBankInfo;
import com.yusys.bione.plugin.rptbank.entity.RptIdxThemeInfo;
import com.yusys.bione.plugin.rptbank.web.vo.RptIdxBankVO;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureInfo;


/**
 * <pre>
 * Title:指标库配置事务类
 * Description: 提供标库配置的事务控制
 * </pre>
 * 
 * @author donglt
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptIdxBankBS extends BaseBS<Object> {

	/**
	 * 获取指标主题树
	 * @param path 工程路径
	 * @param isAuth 是否权限控制
	 * @return 指标主题树节点
	 */
	public List<CommonTreeNode> getThemeTree(String path, boolean isAuth) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		nodes.add(generateRootNode(path));
		if (!isAuth || BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			List<RptIdxThemeInfo> tinfos = this.getAllEntityList(
					RptIdxThemeInfo.class, "isDefault", true);
			if (tinfos != null && tinfos.size() > 0) {
				for (RptIdxThemeInfo tinfo : tinfos) {
					nodes.add(generateThemeNode(path, tinfo));
				}
			}
		} else {
			List<String> themeIds = BioneSecurityUtils
					.getResIdListOfUser(GlobalConstants4plugin.IDXTHEME_RES_NO);
			String jql = "select theme from RptIdxThemeInfo theme where theme.themeId in ?0";
			if(themeIds != null && themeIds.size() > 0){
				List<RptIdxThemeInfo> tinfos = this.baseDAO.findWithIndexParam(jql, themeIds);
				if (tinfos != null && tinfos.size() > 0) {
					for (RptIdxThemeInfo tinfo : tinfos) {
						nodes.add(generateThemeNode(path, tinfo));
					}
				}
			}
			else{
				return nodes;
			}
			
		}
		return nodes;
	}
	
	/**
	 * 生成根节点
	 * @param path 工程路径
	 * @return 根节点
	 */
	private CommonTreeNode generateRootNode(String path) {
		Map<String, String> params = new HashMap<String, String>();
		CommonTreeNode node = new CommonTreeNode();
		node.setId(GlobalConstants4frame.TREE_ROOT_NO);
		node.setText("全部主题");
		node.setIcon(path + "/" + GlobalConstants4frame.ICON_URL + "/house.png");
		node.setParams(params);
		return node;
	}
	/**
	 * 生成主题节点
	 * @param path 工程路径
	 * @param tinfo 主题信息
	 * @return 主题节点
	 */
	private CommonTreeNode generateThemeNode(String path,
			RptIdxThemeInfo tinfo) {
		Map<String, String> params = new HashMap<String, String>();
		CommonTreeNode node = new CommonTreeNode();
		node.setId(tinfo.getThemeId());
		node.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
		node.setText(tinfo.getThemeNm());
		node.setIcon(path + "/" + GlobalConstants4frame.ICON_URL + "/note.gif");
		node.setData(tinfo);
		params.put("id", tinfo.getThemeId());
		params.put(
				"resDefNo",
				GlobalConstants4plugin.IDXTHEME_RES_NO);
		node.setParams(params);
		return node;
	}
	
	/**
	 * 验证主题编号唯一性
	 * @param themeId 主题ID
	 * @param themeNo 主题编号
	 * @return 是否唯一
	 */
	public Boolean validateThemeNo(String themeId,String themeNo) {
		Map<String,Object> values = new HashMap<String, Object>();
		String jql = "select info from RptIdxThemeInfo info where info.themeNo = :themeNo ";
		values.put("themeNo", themeNo);
		if(StringUtils.isNotBlank(themeId)){
			jql += " and themeId != :themeId";
			values.put("themeId", themeId);
		}
		List<RptIdxThemeInfo> infos = this.baseDAO.findWithNameParm(jql, values);
		return infos.size()>0 ? false:true;
	}
	
	/**
	 * 验证主题名称唯一性
	 * @param themeId 主题ID
	 * @param themeNm 主题名称
	 * @return 是否唯一
	 */
	public Boolean validateThemeNm(String themeId,String themeNm) {
		Map<String,Object> values = new HashMap<String, Object>();
		String jql = "select info from RptIdxThemeInfo info where info.themeNm = :themeNm ";
		values.put("themeNm", themeNm);
		if(StringUtils.isNotBlank(themeId)){
			jql += " and themeId != :themeId";
			values.put("themeId", themeId);
		}
		List<RptIdxThemeInfo> infos = this.baseDAO.findWithNameParm(jql, values);
		return infos.size()>0 ? false:true;
	}
	
	/**
	 * 验证指标名称唯一性
	 * @param themeId 主题ID
	 * @param indexId 指标ID
	 * @param indexNm 指标名称
	 * @return 是否唯一
	 */
	public Boolean validateIndexNm(String themeId,String indexId,String indexNm) {
		Map<String,Object> values = new HashMap<String, Object>();
		String jql = "select info from RptIdxBankInfo info where info.indexNm = :indexNm and info.id.themeId = :themeId";
		values.put("indexNm", indexNm);
		values.put("themeId", themeId);
		if(StringUtils.isNotBlank(themeId)){
			jql += " and info.id.indexId != :indexId";
			values.put("indexId", indexId);
		}
		List<RptIdxBankInfo> infos = this.baseDAO.findWithNameParm(jql, values);
		return infos.size()>0 ? false:true;
	}
	
	/**
	 * 删除主题信息
	 * @param themeId 主题Id
	 * @return 删除情况
	 */
	@Transactional(readOnly = false)
	public Map<String,Object> deleteTheme(String themeId) {
		Map<String,Object> res = new HashMap<String, Object>();
		String jql = "delete from RptIdxThemeInfo info where info.themeId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, themeId);
		jql = "delete from RptIdxBankInfo info where info.id.themeId = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, themeId);
		res.put("msg", "ok");
		return res;
	}
	/**
	 * 删除指标信息
	 * @param themeId 主题ID
	 * @param indexId 指标ID
	 * @return 删除情况
	 */
	@Transactional(readOnly = false)
	public Map<String,Object> deleteIdx(String themeId,String indexId) {
		Map<String,Object> res = new HashMap<String, Object>();
		List<String> indexIds = new ArrayList<String>();
		List<String> upIds = new ArrayList<String>();
		indexIds.add(indexId);
		upIds.add(indexId);
		getAllChildIndex(upIds,indexIds);
		String jql = "delete from RptIdxBankInfo info where info.id.themeId = ?0 and info.id.indexId in ?1";
		this.baseDAO.batchExecuteWithIndexParam(jql, themeId,indexIds);
		res.put("msg", "ok");
		return res;
	}
	/**
	 * 获取全部下级指标
	 * @param upIds 上级指标编号
	 * @param indexIds 删除指标
	 */
	private void getAllChildIndex(List<String> upIds,List<String> indexIds){
		String jql = "select info.id.indexId from RptIdxBankInfo info where info.upNo in ?0";
		upIds = this.baseDAO.findWithIndexParam(jql, upIds);
		if(upIds != null && upIds.size() > 0){
			indexIds.addAll(upIds);
			getAllChildIndex(upIds,indexIds);
		}
		
	}
	/**
	 * 获取主题下的指标信息
	 * @param orderBy 排序字段
	 * @param orderType 排序方式
	 * @param conditionMap 过滤信息
	 * @param themeId 主题ID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> getBankInfoByTheme( String orderBy, String orderType,
			Map<String, Object> conditionMap,String themeId) {
		String jql = "select new com.yusys.bione.plugin.rptbank.web.vo.RptIdxBankVO(info) from RptIdxBankInfo info where info.id.themeId=:themeId";
		
		if (!conditionMap.get("jql").equals("")) {
			jql += " and " + conditionMap.get("jql") + " ";
		}

		if (!StringUtils.isEmpty(orderBy)) {
			jql += " order by " + orderBy + " " + orderType;
		}

		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		values.put("themeId", themeId);
		List<RptIdxBankVO> vos = this.baseDAO.findWithNameParm(jql, values);
		List<RptIdxBankVO> resvos = new ArrayList<RptIdxBankVO>();
		Map<String,String> itemMap = Maps.newHashMap();//币种map
		Map<String,String> idxMap = Maps.newHashMap();//指标Map
		List<RptDimItemInfo> itemList = this.getEntityListByProperty(RptDimItemInfo.class, "id.dimTypeNo", "CURRENCY");
		String idxJql = "select idx from RptIdxInfo idx where idx.isRptIndex = ?0 and idx.endDate = ?1";
		List<RptIdxInfo> idxList = this.baseDAO.findWithIndexParam(idxJql, "N","29991231");
		if(null != itemList && itemList.size() > 0){//查币种map
			for(RptDimItemInfo item : itemList){
				itemMap.put(item.getId().getDimItemNo(), item.getDimItemNm());
			}
		}
		if(null != idxList && idxList.size() > 0){//查指标Map
			for(RptIdxInfo idx : idxList){
				idxMap.put(idx.getId().getIndexNo(), idx.getIndexNm());
			}
		}
		if(vos != null && vos.size()>0){
			for(RptIdxBankVO vo :vos){
				if(vo.getUpNo().equals("0")){
					RptIdxBankVO resvo = new RptIdxBankVO();
					BeanUtils.copy(vo, resvo);
					generateVoList(vos, resvo ,itemMap,idxMap);
					resvo.setCurrency(itemMap.get(resvo.getCurrency()));
					resvo.setMainNm(this.getIdxShowNm(vo.getMainNo()));
					resvo.setPartNm(this.getIdxShowNm(vo.getPartNo()));
					resvos.add(resvo);
				}
			}
		}
		Map<String,Object> res = new HashMap<String, Object>();
		res.put("Rows", resvos);
		res.put("Total", resvos.size());
		return res;
	}
	/**
	 * 生成父子关系的指标信息列表
	 * @param vos 指标信息列表
	 * @param info 上级指标信息
	 */
	private void generateVoList(List<RptIdxBankVO> vos, RptIdxBankVO info, Map<String,String> itemMap,Map<String,String> idxMap) {
		for (RptIdxBankVO vo : vos) {
			if (vo.getUpNo().equals(info.getId().getIndexId())) {
				RptIdxBankVO resvo = new RptIdxBankVO();
				BeanUtils.copy(vo, resvo);
				generateVoList(vos, resvo, itemMap,idxMap);
				resvo.setCurrency(itemMap.get(resvo.getCurrency()));
				resvo.setMainNm(this.getIdxShowNm(vo.getMainNo()));
				resvo.setPartNm(this.getIdxShowNm(vo.getPartNo()));
				info.getChildren().add(resvo);
			}
		}
	}
	private String getIdxShowNm(String indexNo) {
		String [] idxNo = StringUtils.split(indexNo, ".");
		if(null != idxNo && idxNo.length > 0){
			String idxJql = "select idx from RptIdxInfo idx where idx.id.indexNo = ?0 and idx.isRptIndex = ?1 and idx.endDate = ?2";
			RptIdxInfo idx = this.baseDAO.findUniqueWithIndexParam(idxJql, idxNo[0], "N", "29991231");
			RptIdxMeasureInfo mes = null;
			if(idxNo.length > 1){
				String mesJql = "select mes from RptIdxMeasureInfo mes where mes.measureNo = ?0";
				mes = this.baseDAO.findUniqueWithIndexParam(mesJql, idxNo[1]);
			}
			return null == mes ? idx.getIndexNm() : idx.getIndexNm() + "." +mes.getMeasureNm();
		}else{
			return null;
		}
	}
	
	/**
	 * 获取指标信息
	 * @param themeId 主题ID
	 * @param indexId 指标ID
	 * @return 指标信息
	 */
	@RequestMapping(value ="getBankInfo")
	@ResponseBody
	public RptIdxBankVO getBankInfo(String themeId,String indexId) {
		String jql = "select new com.yusys.bione.plugin.rptbank.web.vo.RptIdxBankVO(info) from RptIdxBankInfo info where info.id.themeId=:themeId and info.id.indexId = :indexId ";
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("themeId", themeId);
		values.put("indexId", indexId);
		RptIdxBankVO vo = this.baseDAO.findUniqueWithNameParam(jql, values);
		vo.setMainNm(this.getIdxShowNm(vo.getMainNo()));
		vo.setPartNm(this.getIdxShowNm(vo.getPartNo()));
		return vo;
	}
	
	public List<CommonComboBoxNode> getCurrType() {
		List<RptDimItemInfo> currency = this.getEntityListByProperty(RptDimItemInfo.class, "id.dimTypeNo", "CURRENCY");
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		CommonComboBoxNode node = new CommonComboBoxNode();
		node.setId("");
		node.setText("全部");
		nodes.add(node);
		if(null != currency && currency.size() > 0){
			for(RptDimItemInfo cur : currency){
				CommonComboBoxNode curnode = new CommonComboBoxNode();
				curnode.setId(cur.getId().getDimItemNo());
				curnode.setText(cur.getDimItemNm());
				nodes.add(curnode);
			}
		}
		return nodes;
	}
	
	/**
	 * 设置默认模版
	 * @param tempId
	 * @return
	 */
	@Transactional(readOnly = false)
	public RptIdxThemeInfo defaultTemp(String themeId){
		String jql = "Update RptIdxThemeInfo set isDefault = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql,"N");
		jql = "select theme from RptIdxThemeInfo theme where theme.themeId = ?0";
		RptIdxThemeInfo rptIdxThemeInfo = this.baseDAO.findUniqueWithIndexParam(jql,themeId);
		if(rptIdxThemeInfo != null){
			rptIdxThemeInfo.setIsDefault("Y");
			rptIdxThemeInfo = (RptIdxThemeInfo) this.saveOrUpdateEntity(rptIdxThemeInfo);
		}
		return rptIdxThemeInfo;
	 }
	
	/**
	 * 
	 */
	public Map<String, String> getExiPecMap(String dsId) {
		Map<String, String> rstMap = new HashMap<String, String>();
		List<RptIdxBankInfo> pecs = getEntityListByProperty(RptIdxBankInfo.class, "id.themeId", dsId);
		if(null != pecs && pecs.size() > 0) {
			for(RptIdxBankInfo pec : pecs) {
				rstMap.put(pec.getId().getThemeId() + pec.getMainNo() + pec.getPartNo() + pec.getCurrency(), pec.getId().getIndexId());
			}
		}
		return rstMap;
	}
	
}
