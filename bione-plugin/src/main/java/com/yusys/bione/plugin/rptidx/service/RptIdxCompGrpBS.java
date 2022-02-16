package com.yusys.bione.plugin.rptidx.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCompGrp;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCompGrpPK;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDimRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.repository.IdxDimRelMybatisDao;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxCompGrpVO;

@Service
@Transactional(readOnly = true)
public class RptIdxCompGrpBS extends BaseBS<Object>{
	
	@Autowired
	private IdxDimRelMybatisDao relDao;
	
	public Map<String, Object> getCompGrpIndex(String indexNo) {
		String jql = "select new com.yusys.bione.plugin.rptidx.web.vo.RptIdxCompGrpVO(grp,info.indexNm) from RptIdxCompGrp grp,RptIdxInfo info where grp.id.mainIndexNo = ?0 and info.endDate = ?1 and grp.id.indexNo = info.id.indexNo";
		List<RptIdxCompGrpVO> vos = this.baseDAO.findWithIndexParam(jql, indexNo , "29991231");
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("Rows", vos);
		result.put("Total", vos.size());
		return result;
	}
	
	@Transactional(readOnly = false)
	public Map<String,String> saveCompGrp(String ids,String indexNo){
		Map<String,String> res = new HashMap<String, String>();
		String jql = "delete from RptIdxCompGrp grp where grp.id.mainIndexNo = ?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, indexNo);
		String id[] = StringUtils.split(ids, ",");
		for(int i=0; i<id.length; i++){
			RptIdxCompGrp grp = new RptIdxCompGrp();
			RptIdxCompGrpPK pk = new RptIdxCompGrpPK();
			pk.setIndexNo(id[i]);
			pk.setMainIndexNo(indexNo);
			pk.setCompgrpId(RandomUtils.uuid2());
			grp.setId(pk);
			this.saveOrUpdateEntity(grp);
		}
		res.put("msg", "ok");
		return res;
	}
	
	public List<CommonTreeNode> getGrpIndex(String indexNo){
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		Map<String,Object> condition = new HashMap<String, Object>();
		String jql = "select idx from RptIdxInfo idx,RptIdxCompGrp grp where grp.id.mainIndexNo = ?0 and idx.endDate=?1 and grp.id.indexNo = idx.id.indexNo";
		
		List<RptIdxInfo> idxList = this.baseDAO.findWithIndexParam(jql, indexNo,"29991231");
		List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
		if(idxList != null && idxList.size() > 0){
			condition.put("idxList", idxList);
			dimRelList = this
					.getDimNosOfIndex(condition);
		}
		for (RptIdxInfo tmp : idxList) {
			CommonTreeNode idxNode = new CommonTreeNode();
			idxNode.setId(tmp.getId().getIndexNo());
			idxNode.setText(tmp.getIndexNm());
			idxNode.setData(tmp);
			idxNode.setUpId(tmp.getId().getIndexNo());
			idxNode.setIcon("");
			idxNode.getParams().put("nodeType", "idxInfo");
			idxNode.getParams().put("indexNo",
					tmp.getId().getIndexNo());
			idxNode.getParams().put("indexVerId",
					String.valueOf((tmp.getId().getIndexVerId())));
			idxNode.getParams().put("isSum", tmp.getIsSum());
			idxNode.getParams().put("dataType", tmp.getDataType());
			idxNode.getParams().put("dataUnit", tmp.getDataUnit());
			for (RptIdxDimRel rel : dimRelList) {
				if (rel.getId().getIndexNo()
						.equals(tmp.getId().getIndexNo())
						&& rel.getId().getIndexVerId() == tmp
								.getId().getIndexVerId()) {
					idxNode.getParams().put("dimNos",
							rel.getId().getDimNo());
				}
			}
			idxNode.setIsParent(false);
			resultList.add(idxNode);
		}
		return resultList;
	}
	
	private List<RptIdxDimRel> getDimNosOfIndex(Map<String, Object> map) {

		List<RptIdxDimRel> list = this.relDao.getAllDimRel(map);

		List<RptIdxDimRel> resultList = new ArrayList<RptIdxDimRel>();
		for (RptIdxDimRel x : list) {
			boolean flag = false;
			for (RptIdxDimRel y : resultList) {
				if (x.getId().getIndexNo().equals(y.getId().getIndexNo())
						&& x.getId().getIndexVerId() == y.getId()
								.getIndexVerId()) {
					//edit by fangjuan 20150721 对维度进行去重
					if(!y.getId().getDimNo().contains(x.getId().getDimNo())){
						String newDimNos = y.getId().getDimNo() + ","
								+ x.getId().getDimNo();
						y.getId().setDimNo(newDimNos);
					}
					flag = true;// 存在
				}
			}
			if (!flag) {
				resultList.add(x);
			}
		}
		return resultList;
	}
	/**
	 * 查询指标信息表所有一存在的指标，用于指标存在性检验
	 * @return 指标号实体
	 */
	public List<Object[]> getIndexNoList() {
		String sql = "select info.INDEX_NO,info.INDEX_NM from RPT_IDX_INFO info where info.END_DATE = ?0 and info.IS_RPT_INDEX = ?1 ";
		return this.baseDAO.findByNativeSQLWithIndexParam(sql, "29991231","N");
	}
	/**
	 * 删除对比组数据表中与导入数据中主指标重复的数据
	 */
	@Transactional(readOnly = false)
	public void deleteData(String idx) {
		String jql = "delete from RptIdxCompGrp t where t.id.mainIndexNo= ?0 ";
		this.baseDAO.batchExecuteWithIndexParam(jql,idx);
	}
	/**
	 * 查询所选择指标对应的对比组数据
	 *  @return 指标号实体 
	 */
	public List<RptIdxCompGrp> getAllCompGrpList(String idx) {
		String sql = "select t from RptIdxCompGrp t where t.id.mainIndexNo=?0";
		return this.baseDAO.findWithIndexParam(sql,idx);
	}
}
