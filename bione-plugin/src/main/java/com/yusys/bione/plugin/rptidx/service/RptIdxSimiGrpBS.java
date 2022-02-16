package com.yusys.bione.plugin.rptidx.service;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.yusys.bione.plugin.rptidx.entity.RptIdxDimRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxSimilarGrp;
import com.yusys.bione.plugin.rptidx.entity.RptIdxSimilarGrpPK;
import com.yusys.bione.plugin.rptidx.repository.IdxDimRelMybatisDao;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxCompGrpVO;

/**
 * <pre>
 * Title:同类组配置BS
 * Description: 提供查看同类组指标/配置同类组指标等功能
 * </pre>
 * 
 * @author hubing hubing1@yusys.com.cn
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptIdxSimiGrpBS extends BaseBS<Object>{
	
	@Autowired
	private IdxDimRelMybatisDao relDao;
	
	/**
	 * 同类组配置grid面板数据查询
	 * @param indexNo 指标编号
	 * @return	返回同类组实体查询结果
	 */
	public Map<String, Object> getSimiGrpIndex(String indexNo) {
		String jql = "select new com.yusys.bione.plugin.rptidx.web.vo.RptIdxSimiGrpVO(grp2,info.indexNm) from RptIdxSimilarGrp grp1, RptIdxSimilarGrp grp2,RptIdxInfo info "
				+ " where grp1.id.indexNo = ?0 and grp2.id.indexNo != ?0 and info.endDate = ?1 and grp2.id.simigrpId = grp1.id.simigrpId and grp2.id.indexNo = info.id.indexNo ";
		List<RptIdxCompGrpVO> vos = this.baseDAO.findWithIndexParam(jql, indexNo,"29991231");
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("Rows", vos);
		result.put("Total", vos.size());
		return result;
	}
	/**
	 * 保存同类组指标
	 * @param ids 选择的指标群
	 * @param indexNo 被配置的指标编号
	 * @return 保存状态
	 */
	@Transactional(readOnly = false)
	public Map<String,String> saveSimiGrp(String ids,String indexNo,String simiGrpId){
		Map<String,String> res = new HashMap<String, String>();
		String jql = "delete from RptIdxSimilarGrp grp where grp.id.indexNo in (?0) ";
		String grpJql = "delete from RptIdxSimilarGrp grp where grp.id.simigrpId = ?0 ";
		String id[] = StringUtils.split(ids, ",");
		if(null != id && id.length > 0){//先把已存在于其他同类组中指标从其同类组中剔除
			List<String> idsList = Arrays.asList(id);
			this.baseDAO.batchExecuteWithIndexParam(jql, idsList);
		}
		if(!"".equals(simiGrpId)){//如果当前选中指标有同类组编号，就把该组全部删除，重新编组
			this.baseDAO.batchExecuteWithIndexParam(grpJql, simiGrpId);
		}else{
			simiGrpId = RandomUtils.uuid2();
		}
		//保存当前选中指标到新同类组中，组id不变
		RptIdxSimilarGrp curGrp = new RptIdxSimilarGrp();
		RptIdxSimilarGrpPK curPk = new RptIdxSimilarGrpPK();
		curPk.setIndexNo(indexNo);
		curPk.setSimigrpId(simiGrpId);
		curGrp.setId(curPk);
		this.saveOrUpdateEntity(curGrp);
		
		for(int i=0; i<id.length; i++){//保存其他添加的指标到新同类组中
			RptIdxSimilarGrp grp = new RptIdxSimilarGrp();
			RptIdxSimilarGrpPK pk = new RptIdxSimilarGrpPK();
			pk.setIndexNo(id[i]);
			pk.setSimigrpId(simiGrpId);
			grp.setId(pk);
			this.saveOrUpdateEntity(grp);
		}
		res.put("msg", "ok");
		return res;
	}
	/**
	 * 获取指标数
	 * @param indexNo 指标编号
	 * @return 指标list
	 */
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
	public List<RptIdxSimilarGrp> getExpUserList() {
		return null;
	}
	/**
	 * 检验选择的指标是否已存在于其他同类组中
	 * @param indexNo 指标编号
	 * @return log
	 */
	public Map<String,String> checkIndexNo(String indexNo) {
		Map<String,String> log = new HashMap<String, String>();
		List<RptIdxSimilarGrp> simi = this.getEntityListByProperty(RptIdxSimilarGrp.class, "id.indexNo", indexNo);
		if(null != simi && simi.size() > 0){
			log.put("msg", "该指标已存在于其他同类组中，如果要继续添加，会将该指标从其所在同类组中剔除!");
			return log;
		}else{
			log.put("msg","ok");
			return log;
		}
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
	 * 导入grp表时，先全表删除所有同类组数据
	 */
	@Transactional(readOnly = false)
	public void delAllGrps() {
		String jql = "delete from RptIdxSimilarGrp ";
		this.baseDAO.batchExecuteWithIndexParam(jql);
	}
	/**
	 * 导出转换数据格式
	 * @return 数据实体
	 */
	public List<RptIdxSimilarGrp> getAllSimiGrpList() {
		return this.getAllEntityList(RptIdxSimilarGrp.class, "id.simigrpId", false);
	}
}
