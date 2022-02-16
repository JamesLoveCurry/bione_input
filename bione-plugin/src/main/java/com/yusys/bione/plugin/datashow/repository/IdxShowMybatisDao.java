package com.yusys.bione.plugin.datashow.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.datashow.web.vo.CerrWorkDateVO;
import com.yusys.bione.plugin.datashow.web.vo.FavIdxDetailInfoVO;
import com.yusys.bione.plugin.datashow.web.vo.IdxBaseInfoVO;
import com.yusys.bione.plugin.datashow.web.vo.IdxDimInfoVO;
import com.yusys.bione.plugin.datashow.web.vo.RptOrgInfoVO;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
/**
 * <pre>
 * Title: IdxShowMybatisDao
 * Description: 
 * </pre>
 * 
 * @author kanglg kanglg@yuchengtech.com
 * @version 1.00.00
 */
@MyBatisRepository
public interface IdxShowMybatisDao {
	public IdxBaseInfoVO getIdxBaseInfo(Map<String, Object> params);
	//add by zhongqh 
	public List<CerrWorkDateVO> getCurrentDate(Map<String, Object> paramMap);
	public List<IdxDimInfoVO> findIdxDim(Map<String, Object> params);
	public List<RptDimTypeInfo> findDimTypeByIdx(Map<String, Object> params);
	public List<RptDimItemInfo> findDimItemByDimTypeNo(String dimTypeNo);
	public List<FavIdxDetailInfoVO> findStoreIdxInfo(String instanceId);
	public List<RptOrgInfoVO> getOrgSimpleInfo(Map<String, Object> params);
	public List<String> getUserIdxDefSrc(Map<String, Object> params);
	public List<RptIdxInfo> findIdxInfoBySrc(Map<String, Object> params);
	public List<RptIdxCatalog> findIdxCataInfoBySrc(Map<String, Object> params);
	/**
	 * @param params defSrc为必填项
	 * @return
	 */
	public List<RptIdxCatalog> findLimitIdxCataInfoBySrc(Map<String, Object> params);
}
