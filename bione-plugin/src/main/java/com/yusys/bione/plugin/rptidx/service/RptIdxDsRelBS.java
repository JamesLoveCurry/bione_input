package com.yusys.bione.plugin.rptidx.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.repository.mybatis.RptDataSetDao;
import com.yusys.bione.plugin.datamodel.vo.RptSysColVO;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptdim.repository.mybatis.RptDimDao;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDsDimFilter;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDsRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureRel;
import com.yusys.bione.plugin.rptidx.repository.IdxDimRelMybatisDao;
import com.yusys.bione.plugin.rptidx.repository.IdxInfoMybatisDao;
import com.yusys.bione.plugin.rptidx.repository.RptIdxDsRelMybatisDao;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxDimRelVO;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxDsRelVO;

@Service
@Transactional(readOnly = true)
public class RptIdxDsRelBS {
	@Autowired
	private IdxInfoMybatisDao idxInfoDao;
	@Autowired
	private RptIdxDsRelMybatisDao rptidrDao;
	@Autowired
	private RptDataSetDao rptDataDao;
	@Autowired
	private IdxDimRelMybatisDao idxDimDao;
	@Autowired
	private RptDimDao rptDimDao;

	public List<RptIdxDsRelVO> idxDsRelList(String rptId) {
		List<RptIdxDsRelVO> vos = this.rptidrDao.IdxDsRellist(rptId);
		if (vos != null && vos.size() > 0) {
			for (RptIdxDsRelVO vo : vos) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("rptId", rptId);
				params.put("indexNo", vo.getIndexNo());
				params.put("indexVerId", vo.getIndexVerId());
				vo.setDimfilterInfo(this.rptidrDao.getDimFilterByParams(params));
			}
		}
		return vos;
	}

	public String getIdxDsId(String indexNo,long indexVerId){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("indexNo", indexNo);
		params.put("indexVerId", indexVerId);
		RptIdxDsRel rel=idxInfoDao.getRptIdxDsRel(params);
		if(rel!=null){
			return rel.getId().getSetId();
		}
		return "";
	}
	public Map<String, Object> dimTypeRelList(Pager pager, String setId) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<RptDimTypeInfo> lists = this.rptidrDao
				.dimTypeRelList(setId);
		map.put("Rows", lists);
		return map;
	}

	public List<RptSysModuleCol> getColInfo(String setId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("setId", setId);
		params.put("colType", GlobalConstants4plugin.COL_TYPE_MEASURE);
		return this.rptDataDao.findModuleColsByColTypeAndSetId(params);
	}
	
	public List<CommonComboBoxNode> getColComboInfo(String setId) {
		List<CommonComboBoxNode> nodes=new ArrayList<CommonComboBoxNode>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("setId", setId);
		params.put("colType", GlobalConstants4plugin.COL_TYPE_MEASURE);
		List<RptSysModuleCol> cols=this.rptDataDao.findModuleColsByColTypeAndSetId(params);
		if(cols!=null&&cols.size()>0){
			for(RptSysModuleCol col: cols){
				CommonComboBoxNode node=new CommonComboBoxNode();
				node.setId(col.getColId());
				node.setText(col.getEnNm());
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	public List<RptSysColVO> getColDataInfo(String setIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("setIds", ArrayUtils.asSet(setIds, ","));
		params.put("colType", GlobalConstants4plugin.COL_TYPE_MEASURE);
		List<RptSysColVO> cols=this.rptDataDao.findModuleColsByColTypeAndSetIds(params);
		return cols;
	}
	
	public List<CommonComboBoxNode> getDimTypeInfo(String indexNo,
			String indexVerId) {
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("indexNo", indexNo);
		if (indexVerId != null && !indexVerId.equals("")) {
			params.put("indexVerId", indexVerId);
		}
		List<RptIdxDimRelVO> infos = this.idxDimDao.getIdxDimRelInfo(params);
		List<RptIdxDimRelVO> innerinfos = this.idxDimDao
				.getInnerDimInfo(GlobalConstants4plugin.DIM_TYPE_SRC_INNER);
		if (innerinfos != null && innerinfos.size() > 0) {
			for (RptDimTypeInfo info : innerinfos) {
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(info.getDimTypeNo());
				node.setText(info.getDimTypeNm());
				nodes.add(node);
			}
		}
		if (infos != null && infos.size() > 0) {
			for (RptDimTypeInfo info : infos) {
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(info.getDimTypeNo());
				node.setText(info.getDimTypeNm());
				nodes.add(node);
			}
		}
		return nodes;
	}

	public List<RptIdxDimRelVO> getRptDimTypeInfo(String indexNo) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("indexNo", indexNo);
		params.put("dimType", GlobalConstants4plugin.DIM_TYPE_BUSI);
		List<RptIdxDimRelVO> innerinfos = this.idxDimDao
				.getInnerDimInfo(GlobalConstants4plugin.DIM_TYPE_BUSI);
		List<RptIdxDimRelVO> diminfos = this.idxDimDao.getIdxDimRelInfo(params);
		List<RptIdxDimRelVO> dimTypes = new ArrayList<RptIdxDimRelVO>();
		if (diminfos != null && diminfos.size() > 0) {
			for (RptIdxDimRelVO diminfo : diminfos) {
				if (!dimTypes.contains(diminfo)) {
					dimTypes.add(diminfo);
				} else {
					RptIdxDimRelVO dimtype = dimTypes.get(dimTypes
							.indexOf(diminfo));
					if (!dimtype.getDsId().equals(""))
						dimtype.setDsId(dimtype.getDsId() + ","
								+ diminfo.getDsId());
					else
						dimtype.setDsId(diminfo.getDsId());
				}
			}
		}
		innerinfos.addAll(dimTypes);
		return innerinfos;

	}

	public List<CommonTreeNode> getDimInfoTree(String dimTypeNo, String basePath) {
		Map<String,Object>   conditions =  Maps.newHashMap();
        conditions.put("dimTypeNo", dimTypeNo);
		List<RptDimItemInfo> infos = this.rptDimDao
				.findDimItemListByTypeNo(conditions);
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
//		CommonTreeNode defaultnode = new CommonTreeNode();
//		defaultnode.setId("0");
//		defaultnode.setText("维度项信息");
//		defaultnode.setIsParent(true);
//		defaultnode.setIcon(basePath + GlobalConstants.TREE_ICON_ROOT);
//		nodes.add(defaultnode);
		if (infos != null && infos.size() > 0) {
			for (RptDimItemInfo info : infos) {
				CommonTreeNode node = new CommonTreeNode();
				node.setId(info.getId().getDimItemNo());
				node.setText(info.getDimItemNm());
				node.setUpId(info.getUpNo());
				node.setIcon(basePath + GlobalConstants4plugin.REPORT_LABELOBJ_ICON);
				nodes.add(node);
			}
		}
		return nodes;
	}

	@Transactional(readOnly = false)
	public void saveIdxDsRelInfo(List<RptIdxDsRel> rels,
			List<RptIdxDsDimFilter> filters, String rptId) {
		this.rptidrDao.deleteRptIdxRel(rptId);
		this.rptidrDao.deleteRptIdxDsFilter(rptId);
		for (RptIdxDsRel rel : rels) {
			this.rptidrDao.saveRptIdxRel(rel);
		}
		for (RptIdxDsDimFilter filter : filters) {
			this.rptidrDao.saveRptIdxDsFilter(filter);
		}
	}

	public Map<String,String> validateSet(String indexNos, String setId) {
		List<String> indexLists = ArrayUtils.asList(indexNos, ",");
		Map<String,String> map=new HashMap<String, String>();
		String eIndex="";
		String dIndex="";
		if(indexLists!=null&&indexLists.size()>0){
			int i=0;
			for(String indexNo:indexLists){
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("indexNo", indexNo);
				params.put("setId", setId);
				int count = this.rptidrDao.validateSet(params);
				if( count > 0 ){
					dIndex+=String.valueOf(i)+",";
				}
				else{
					eIndex+=String.valueOf(i)+",";
				}
				
				i++;
			}
			if(eIndex.length()>0){
				eIndex=eIndex.substring(0, eIndex.length()-1);
			}
			if(dIndex.length()>0){
				dIndex=dIndex.substring(0, dIndex.length()-1);
			}
		}
		map.put("eIndex", eIndex);
		map.put("dIndex", dIndex);
		return map;
	}

	public String getRptDimItemNm(String dimTypeNo, String dimItemNo) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("dimTypeNo", dimTypeNo);
		params.put("dimItemNo", dimItemNo);
		RptDimItemInfo info = this.rptDimDao.findDimItemInfoByPkId(params);
		if (info != null) {
			return info.getDimItemNm();
		} else {
			return "";
		}
	}
	
	public String getIdxMeasure(String indexNo,long indexVerId){
		Map<String,Object> params=new HashMap<String, Object>();
		params.put("indexNo", indexNo);
		params.put("indexVerId", indexVerId);
		List<RptIdxMeasureRel> rel=this.rptidrDao.getIdxMeasure(params);
		if(rel!=null&&rel.size()>0){
			return rel.get(0).getId().getDsId();
		}
		return "";
	}
}
