package com.yusys.bione.plugin.access.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.access.web.vo.IdxUserAccessVO;
import com.yusys.bione.plugin.access.web.vo.IdxUserInfoVO;
import com.yusys.bione.plugin.rptidx.entity.RptIdxVisitHis;

@MyBatisRepository
public interface RptAccessIdxDao {
	public List<IdxUserAccessVO> search(Map<String, Object> map) ;
	public List<IdxUserInfoVO> findInfo(Map<String, Object> map);
	public void saveHis(RptIdxVisitHis his);
	
	public List<IdxUserAccessVO> userSearch(Map<String, Object> map);
	public List<IdxUserInfoVO> findUserDetail(Map<String, Object> map);
	
}
