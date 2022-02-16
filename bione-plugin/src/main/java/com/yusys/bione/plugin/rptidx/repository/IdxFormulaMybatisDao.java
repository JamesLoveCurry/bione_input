package com.yusys.bione.plugin.rptidx.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaInfo;
import com.yusys.bione.plugin.rptidx.web.vo.IdxFormulaAndSrcIdxVO;
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
public interface IdxFormulaMybatisDao {
	public List<IdxFormulaAndSrcIdxVO> list(Map<String, Object> map);
	public void getAll(Map<String, Object> map);
	public void saveFormula(RptIdxFormulaInfo info);
	public void deleteFormula(Map<String, Object> map);
}
