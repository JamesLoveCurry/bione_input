package com.yusys.bione.plugin.rptsys.repository;

import java.util.List;
import java.util.Map;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.plugin.rptsys.entity.RptSysVarInfo;
import com.yusys.bione.plugin.rptsys.web.vo.RptSysVarInfoVO;

/**
 * 
 * <pre>
 * Title: 系统变量Dao层
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
public interface RptSysVarMybatisDao {
	
	public List<RptSysVarInfo> getSysVarByParams(Map<String,Object> params);

	public void saveSysVarInfo(RptSysVarInfo sysInfo);
	
	public void updateSysVarInfo(RptSysVarInfo sysInfo);
	
	public void deleteSysVarInfo(List<String> varIds);
	
	public List<RptSysVarInfoVO> getSysVarVoByParams(Map<String,Object> params);
}
