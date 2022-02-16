package com.yusys.bione.plugin.rptidx.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
/**
 * 
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述 
 * </pre>
 * @author fangjuan  fangjuan@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
@MyBatisRepository
public interface IdxCatalogMybatisDao {
	public List<RptIdxCatalog> listIdxCatalog(Map<String, Object> map);
	public void saveIdxCatalog(RptIdxCatalog info);
	public void updateIdxCatalog(RptIdxCatalog info);
	public void deleteIdxCatalog(Map<String, Object> map);
	public Integer testSameIndexCatalogNm(Map<String, Object> map);
}
