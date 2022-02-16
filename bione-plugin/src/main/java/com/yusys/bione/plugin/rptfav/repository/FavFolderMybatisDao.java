package com.yusys.bione.plugin.rptfav.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.plugin.rptfav.entity.RptFavFolder;
import com.yusys.bione.plugin.rptfav.web.vo.RptFavFolderRelInsVo;
import com.yusys.bione.plugin.rptfav.web.vo.RptFavQueryinsVo;

/**
 * 通过@MapperScannerConfigurer扫描目录中的所有接口, 动态在Spring Context中生成实现.
 * 方法名称必须与Mapper.xml中保持一致.
 * 
 * @author 
 */
@MyBatisRepository
public interface FavFolderMybatisDao {
	
	public List<RptFavFolder> list(Map<String, Object> map);
	
	public void save(RptFavFolder rel);
	public void update(RptFavFolder rel);
	public void delete(Map<String, Object> map);
	//报表查询 -- 孙玉明
	public PageMyBatis<RptFavQueryinsVo> reportInfo(Map<String, Object> map);
	public PageMyBatis<RptFavQueryinsVo> reportInfo1(Map<String, Object> map);
	public void del(Map<String, Object> map);
	public void delindex(Map<String, Object> map);

	public List<RptFavFolderRelInsVo> listInstance(Map<String, Object> map);

	//收索
	public List<RptFavFolder> findList(Map<String,Object> map);
	
	//级联查询RptFavFolder
	public List<RptFavFolder> listFolder(Map<String,Object> map);
	
	public List<CommonTreeNode> findStoreItems(Map<String, Object> params);
	
	public List<CommonTreeNode> findFolder(Map<String, Object> params);
}
