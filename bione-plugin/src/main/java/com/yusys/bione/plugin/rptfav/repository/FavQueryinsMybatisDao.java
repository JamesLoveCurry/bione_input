package com.yusys.bione.plugin.rptfav.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.plugin.rptfav.entity.RptFavQueryins;
import com.yusys.bione.plugin.rptfav.web.vo.NameIdVo;
import com.yusys.bione.plugin.rptfav.web.vo.RptFavQueryinsVo;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author 
 */
@MyBatisRepository
public interface FavQueryinsMybatisDao {
	public List<RptFavQueryins> list(Map<String, Object> map);
	public void save(RptFavQueryins rel);
	public void update(RptFavQueryins rel);
	public void delete(Map<String, Object> map);
	
	public List<RptFavQueryins> getAll(Map<String, Object> map);
	public PageMyBatis<RptFavQueryinsVo> listInfo(Map<String, Object> map);
	public List<NameIdVo> findFolderNm(Map<String, Object> parames);
	
	
}
