package com.yusys.bione.plugin.rptidx.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDsDimFilter;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDsRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureRel;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxDsRelVO;
/**
 * 
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述 
 * </pre>
 * @author weijiaxiang  weijx@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
@MyBatisRepository
public interface RptIdxDsRelMybatisDao {
	
	public List<RptIdxDsRelVO> IdxDsRellist(String rptId);
	
	public List<RptIdxDsDimFilter> getDimFilterByParams(Map<String,Object> params);
	
	public void saveRptIdxRel(RptIdxDsRel rel);
	
	public void saveRptIdxDsFilter(RptIdxDsDimFilter filter);
	
	public void deleteRptIdxRel(String rptId);
	
	public void deleteRptIdxDsFilter(String rptId);
	
	public int validateSet(Map<String,Object> params);
	
	public List<RptDimTypeInfo> dimTypeRelList(String setId);
	
	public List<RptIdxMeasureRel> getIdxMeasure(Map<String,Object> params);
}
