package com.yusys.bione.plugin.rptsys.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.frame.variable.entity.BioneParamTypeInfo;
/**
 * 
 * <pre>
 * Title: 系统参数Dao层
 * Description:
 * </pre>
 * 
 * @author weijiaxiang weijx@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@MyBatisRepository
public interface RptParamMybatisDao {
	public List<BioneParamInfo> paramInfolist(Map<String,Object> param);
	
	public List<BioneParamInfo> paramInfoOrderlist(Map<String,Object> param);
	
	public List<BioneParamTypeInfo> paramTypelist(Map<String,Object> param);
	
	public void paramInfosave(BioneParamInfo param);
	
	public void paramTypesave(BioneParamTypeInfo param);
	
	public void paramInfoupdate(BioneParamInfo param);
	
	public void paramTypeupdate(BioneParamTypeInfo param);
	
	public void paramInfodelete(Map<String,Object> param);
	
	public void paramTypedelete(Map<String,Object> param);
}
