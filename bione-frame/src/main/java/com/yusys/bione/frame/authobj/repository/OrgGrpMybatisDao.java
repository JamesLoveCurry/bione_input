package com.yusys.bione.frame.authobj.repository;

import com.yusys.bione.comp.repository.mybatis.MyBatisRepository;
import com.yusys.bione.frame.authobj.entity.BioneGrpOrgRel;
import com.yusys.bione.frame.authobj.entity.BioneOrgGrp;
import com.yusys.bione.frame.authobj.web.vo.BioneOrgGrpVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface OrgGrpMybatisDao {

    List<BioneOrgGrpVO> list(Map<String, Object> params);

    List<BioneOrgGrp> checkName(Map<String, String> params);

    BioneOrgGrp getRptById(String groupId);

    void editGroup(BioneOrgGrp orgGrp);

    void saveGroup(BioneOrgGrp orgGrp);

    void delOrg(HashMap<String, Object> params);

    void delObjs(HashMap<String, Object> params);

    void delObj(String grpId);

    void saveObj(BioneGrpOrgRel bioneGrpOrgRel);

    List<String> searchbyId(String grpId);
}
