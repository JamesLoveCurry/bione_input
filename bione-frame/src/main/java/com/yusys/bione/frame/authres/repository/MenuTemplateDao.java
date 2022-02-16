package com.yusys.bione.frame.authres.repository;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.authres.entity.BioneFuncInfo;
import com.yusys.bione.frame.authres.entity.BioneMenuTemplateInfo;
import com.yusys.bione.frame.authres.entity.BioneModuleInfo;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface MenuTemplateDao {

    List<BioneMenuTemplateInfo> getMenuTemplateList(@Param("funcType") String funcType, @Param("upId") String upId);

    List<BioneParamInfo> getOrgType(Map<String, Object> map);

    void batchInsertFunc(List<BioneFuncInfo> list);

    void insertModule(BioneModuleInfo bioneModuleInfo);
}
