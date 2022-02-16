package com.yusys.bione.plugin.rptfav.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptfav.entity.RptFavFolderInsRel;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author 
 */
@MyBatisRepository
public interface FavFolderInsRelMybatisDao {
	public List<RptFavFolderInsRel> list(Map<String, Object> map);
	public void save(RptFavFolderInsRel rel);
	public void update(RptFavFolderInsRel rel);
	public void delete(Map<String, Object> map);
	public void updateRel(Map<String, Object> map);
}
